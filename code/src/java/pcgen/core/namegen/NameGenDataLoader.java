/*
 * Copyright 2003 (C) Devon Jones
 * Copyright 2026 Vest <Vest@users.noreply.github.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package pcgen.core.namegen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import pcgen.util.Logging;

/**
 * Loads random-name XML files from a directory into a {@link NameGenData}
 * snapshot, keeping the data model independent of any UI toolkit.
 *
 * <p>Uses the JDK-bundled {@code javax.xml.parsers} API so the project
 * doesn't need a third-party XML library.
 *
 * <p>Loading is two-pass: first parse every file and gather raw
 * {@code <LIST>}/{@code <RULESET>} elements with their ids, then resolve
 * cross-references and build the immutable model. The two passes let
 * rulesets refer to each other freely without forward-declaration issues.
 */
public final class NameGenDataLoader
{
	private static final String ATTR_TITLE = "title";
	private static final String ATTR_IDREF = "idref";
	private static final String TAG_GETLIST = "GETLIST";
	private static final String TAG_GETRULE = "GETRULE";

	private NameGenDataLoader()
	{
	}

	/**
	 * Load every {@code *.xml} file in the given directory.
	 *
	 * @param dataDir directory containing {@code generator.dtd} and the XML
	 *                files to parse
	 * @return populated {@link NameGenData}
	 * @throws IOException if {@code dataDir} is not a directory or any file
	 *                     fails to parse
	 */
	public static NameGenData load(File dataDir) throws IOException
	{
		if (!dataDir.isDirectory())
		{
			throw new IOException("Not a directory: " + dataDir);
		}
		File[] dataFiles = dataDir.listFiles((dir, name) -> name.endsWith(".xml"));
		if (dataFiles == null)
		{
			throw new IOException("Cannot list files in: " + dataDir);
		}

		DocumentBuilder builder = newDocumentBuilder();
		builder.setEntityResolver(dtdResolver(dataDir));

		Map<String, Element> rawLists = new LinkedHashMap<>();
		Map<String, RawRuleSet> rawRuleSets = new LinkedHashMap<>();
		Map<String, List<String>> rawCategories = new LinkedHashMap<>();

		// Pass 1: parse each file, harvest raw LIST and RULESET elements.
		for (File dataFile : dataFiles)
		{
			RawFile rf = parseOne(dataFile, builder);
			for (Element list : rf.lists)
			{
				rawLists.put(list.getAttribute("id"), list);
			}
			for (Element ruleSet : rf.ruleSets)
			{
				String id = ruleSet.getAttribute("id");
				rawRuleSets.put(id, new RawRuleSet(ruleSet, id));
			}
		}

		// Pass 2a: build NameList records (no cross-refs).
		Map<String, NameList> lists = rawLists.values().stream()
				.map(NameGenDataLoader::buildList)
				.collect(Collectors.toMap(
						NameList::id,
						nl -> nl,
						(a, b) -> b,
						LinkedHashMap::new));

		// Pass 2b: build RuleSet records. RuleSetRef parts share a single
		// map instance that gets populated as we go, so a ruleset can
		// reference any other ruleset regardless of file order.
		Map<String, RuleSet> rulesets = new LinkedHashMap<>();
		List<NameGenData.UnresolvedReference> unresolved = new ArrayList<>();
		for (RawRuleSet raw : rawRuleSets.values())
		{
			RuleSet rs = buildRuleSet(raw, lists, rulesets, rawRuleSets, unresolved);
			rulesets.put(raw.id, rs);
			collectCategories(raw.element, raw.id, rawCategories);
		}

		// Pass 3: resolve category ids to RuleSet records. Categories
		// declared on rulesets that ultimately failed to build are skipped.
		Map<String, List<RuleSet>> categories = rawCategories.entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						e -> e.getValue().stream()
								.map(rulesets::get)
								.filter(Objects::nonNull)
								.toList(),
						(a, b) -> b,
						LinkedHashMap::new));

		return new NameGenData(lists, rulesets, categories, unresolved);
	}

	private static RawFile parseOne(File dataFile, DocumentBuilder builder) throws IOException
	{
		try
		{
			Document nameSet = builder.parse(dataFile);
			DocumentType dt = nameSet.getDoctype();
			if (dt == null || !"GENERATOR".equals(dt.getName()))
			{
				return RawFile.EMPTY;
			}
			Element generator = nameSet.getDocumentElement();
			return new RawFile(
					childElements(generator, "LIST"),
					childElements(generator, "RULESET"));
		} catch (SAXException | NumberFormatException e)
		{
			Logging.errorPrint("Failed to parse " + dataFile.getName(), e);
			throw new IOException("XML error in file " + dataFile.getName(), e);
		}
	}

	/**
	 * Lazy-loader phase 1. Parses a single file's DOM, registers every
	 * {@code <LIST>} from this file into the live {@code lists} map, and
	 * returns the raw ruleset elements together with the idrefs the file
	 * reaches into. The lazy caller demand-parses the owners of any
	 * not-yet-loaded ids before invoking
	 * {@link #buildRuleSetsForLazy(LazyFilePrepared, Map, Map, List, UnaryOperator)}.
	 */
	static LazyFilePrepared prepareFileForLazy(File dataFile, File dataDir,
	                                           Map<String, NameList> lists) throws IOException
	{
		DocumentBuilder builder = newDocumentBuilder();
		builder.setEntityResolver(dtdResolver(dataDir));
		RawFile raw = parseOne(dataFile, builder);

		for (Element listEl : raw.lists)
		{
			NameList nl = buildList(listEl);
			lists.put(nl.id(), nl);
		}

		Map<String, RawRuleSet> localRawRuleSets = raw.ruleSets.stream()
				.collect(Collectors.toMap(
						rsEl -> rsEl.getAttribute("id"),
						rsEl -> new RawRuleSet(rsEl, rsEl.getAttribute("id")),
						(a, b) -> b,
						LinkedHashMap::new));

		Set<String> referencedListIds = new LinkedHashSet<>();
		Set<String> referencedRuleSetIds = new LinkedHashSet<>();
		for (Element rsEl : raw.ruleSets)
		{
			for (Element ruleEl : childElements(rsEl, "RULE"))
			{
				for (Element child : childElements(ruleEl))
				{
					switch (child.getTagName())
					{
						case TAG_GETLIST -> referencedListIds.add(child.getAttribute(ATTR_IDREF));
						case TAG_GETRULE -> referencedRuleSetIds.add(child.getAttribute(ATTR_IDREF));
						default ->
						{
							// nothing
						}
					}
				}
			}
		}

		return new LazyFilePrepared(localRawRuleSets, referencedListIds, referencedRuleSetIds);
	}

	/**
	 * Lazy-loader phase 2. Builds and registers {@link RuleSet} records for
	 * a file whose phase-1 result was returned by {@link
	 * #prepareFileForLazy(File, File, Map)}.
	 *
	 * <p>{@code crossFileRuleSetTitle} is consulted whenever a {@code GETRULE}
	 * points at an id not declared in the current file. Returning {@code null}
	 * from the resolver records an unresolved reference; returning a title
	 * string produces a {@link RulePart.RuleSetRef} that will resolve through
	 * the shared {@code rulesets} map at generation time.
	 */
	static void buildRuleSetsForLazy(LazyFilePrepared prepared,
	                                 Map<String, NameList> lists,
	                                 Map<String, RuleSet> rulesets,
	                                 List<NameGenData.UnresolvedReference> unresolved,
	                                 UnaryOperator<String> crossFileRuleSetTitle)
	{
		for (RawRuleSet rrs : prepared.localRawRuleSets().values())
		{
			RuleSet rs = buildRuleSetLazy(rrs, lists, rulesets,
					prepared.localRawRuleSets(), unresolved, crossFileRuleSetTitle);
			rulesets.put(rrs.id, rs);
		}
	}

	private static RuleSet buildRuleSetLazy(RawRuleSet raw,
	                                        Map<String, NameList> lists,
	                                        Map<String, RuleSet> rulesets,
	                                        Map<String, RawRuleSet> localRawRuleSets,
	                                        List<NameGenData.UnresolvedReference> unresolved,
	                                        UnaryOperator<String> crossFileRuleSetTitle)
	{
		List<Rule> rules = childElements(raw.element, "RULE").stream()
				.map(rule -> buildRuleLazy(rule, lists, rulesets,
						localRawRuleSets, unresolved, crossFileRuleSetTitle))
				.toList();
		return new RuleSet(raw.id,
				raw.element.getAttribute(ATTR_TITLE),
				raw.element.getAttribute("usage"),
				rules);
	}

	private static Rule buildRuleLazy(Element rule,
	                                  Map<String, NameList> lists,
	                                  Map<String, RuleSet> rulesets,
	                                  Map<String, RawRuleSet> localRawRuleSets,
	                                  List<NameGenData.UnresolvedReference> unresolved,
	                                  UnaryOperator<String> crossFileRuleSetTitle)
	{
		List<RulePart> parts = new ArrayList<>();
		StringBuilder label = new StringBuilder();
		for (Element child : childElements(rule))
		{
			RulePart part = switch (child.getTagName())
			{
				case TAG_GETLIST -> resolveListRef(child.getAttribute(ATTR_IDREF), lists, unresolved);
				case TAG_GETRULE -> resolveRuleSetRefLazy(child.getAttribute(ATTR_IDREF),
						rulesets, localRawRuleSets, unresolved, crossFileRuleSetTitle);
				case "SPACE" -> RulePart.Literal.SPACE;
				case "HYPHEN" -> RulePart.Literal.HYPHEN;
				case "CR" -> RulePart.Literal.CR;
				default -> null;
			};
			if (part != null)
			{
				parts.add(part);
				label.append(part.label());
			}
		}
		return new Rule(parseWeight(rule), label.toString(), parts);
	}

	private static RulePart resolveRuleSetRefLazy(String idref,
	                                              Map<String, RuleSet> rulesets,
	                                              Map<String, RawRuleSet> localRawRuleSets,
	                                              List<NameGenData.UnresolvedReference> unresolved,
	                                              UnaryOperator<String> crossFileRuleSetTitle)
	{
		// Same-file target: title from the local raw element so it works
		// even when the target hasn't been built yet.
		RawRuleSet local = localRawRuleSets.get(idref);
		if (local != null)
		{
			return new RulePart.RuleSetRef(idref, local.element.getAttribute(ATTR_TITLE), rulesets);
		}
		String title = crossFileRuleSetTitle.apply(idref);
		if (title == null)
		{
			unresolved.add(new NameGenData.UnresolvedReference(
					NameGenData.UnresolvedReference.Kind.GETRULE, idref));
			return null;
		}
		return new RulePart.RuleSetRef(idref, title, rulesets);
	}

	/**
	 * Phase-1 output for {@link #prepareFileForLazy(File, File, Map)}.
	 */
	record LazyFilePrepared(Map<String, RawRuleSet> localRawRuleSets,
	                        Set<String> referencedListIds,
	                        Set<String> referencedRuleSetIds)
	{
	}

	private static DocumentBuilder newDocumentBuilder() throws IOException
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// XXE hardening: data files are local but the parser shouldn't fetch external entities or evaluate
			// parameter entities. We keep external-DTD loading on so generator.dtd still resolves
			// through the EntityResolver.
			factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			factory.setXIncludeAware(false);
			factory.setExpandEntityReferences(false);
			return factory.newDocumentBuilder();
		} catch (ParserConfigurationException e)
		{
			throw new IOException("Cannot create XML parser", e);
		}
	}

	private static NameList buildList(Element list)
	{
		String id = list.getAttribute("id");
		String title = list.getAttribute(ATTR_TITLE);
		List<WeightedDataValue> values = new ArrayList<>();
		for (Element child : childElements(list, "VALUE"))
		{
			WeightedDataValue dv = new WeightedDataValue(directText(child), parseWeight(child));
			childElements(child, "SUBVALUE").forEach(sub ->
					dv.addSubValue(sub.getAttribute("type"), directText(sub)));
			values.add(dv);
		}
		return new NameList(id, title, values);
	}

	private static RuleSet buildRuleSet(RawRuleSet raw,
	                                    Map<String, NameList> lists,
	                                    Map<String, RuleSet> rulesets,
	                                    Map<String, RawRuleSet> rawRuleSets,
	                                    List<NameGenData.UnresolvedReference> unresolved)
	{
		List<Rule> rules = childElements(raw.element, "RULE").stream()
				.map(rule -> buildRule(rule, lists, rulesets, rawRuleSets, unresolved))
				.toList();
		return new RuleSet(raw.id,
				raw.element.getAttribute(ATTR_TITLE),
				raw.element.getAttribute("usage"),
				rules);
	}

	private static Rule buildRule(Element rule,
	                              Map<String, NameList> lists,
	                              Map<String, RuleSet> rulesets,
	                              Map<String, RawRuleSet> rawRuleSets,
	                              List<NameGenData.UnresolvedReference> unresolved)
	{
		List<RulePart> parts = new ArrayList<>();
		StringBuilder label = new StringBuilder();
		for (Element child : childElements(rule))
		{
			RulePart part = switch (child.getTagName())
			{
				case TAG_GETLIST -> resolveListRef(child.getAttribute(ATTR_IDREF), lists, unresolved);
				case TAG_GETRULE ->
						resolveRuleSetRef(child.getAttribute(ATTR_IDREF), rulesets, rawRuleSets, unresolved);
				case "SPACE" -> RulePart.Literal.SPACE;
				case "HYPHEN" -> RulePart.Literal.HYPHEN;
				case "CR" -> RulePart.Literal.CR;
				default -> null;
			};
			if (part != null)
			{
				parts.add(part);
				label.append(part.label());
			}
		}
		return new Rule(parseWeight(rule), label.toString(), parts);
	}

	private static RulePart resolveListRef(String idref,
	                                       Map<String, NameList> lists,
	                                       List<NameGenData.UnresolvedReference> unresolved)
	{
		NameList target = lists.get(idref);
		if (target == null)
		{
			unresolved.add(new NameGenData.UnresolvedReference(
					NameGenData.UnresolvedReference.Kind.GETLIST, idref));
			return null;
		}
		return new RulePart.ListRef(target);
	}

	private static RulePart resolveRuleSetRef(String idref,
	                                          Map<String, RuleSet> rulesets,
	                                          Map<String, RawRuleSet> rawRuleSets,
	                                          List<NameGenData.UnresolvedReference> unresolved)
	{
		RawRuleSet raw = rawRuleSets.get(idref);
		if (raw == null)
		{
			unresolved.add(new NameGenData.UnresolvedReference(
					NameGenData.UnresolvedReference.Kind.GETRULE, idref));
			return null;
		}
		// Title comes from the raw element so forward references work
		// before the target ruleset has been built.
		String title = raw.element.getAttribute(ATTR_TITLE);
		return new RulePart.RuleSetRef(idref, title, rulesets);
	}

	private static void collectCategories(Element ruleSet, String id,
	                                      Map<String, List<String>> rawCategories)
	{
		for (Element category : childElements(ruleSet, "CATEGORY"))
		{
			rawCategories.computeIfAbsent(category.getAttribute(ATTR_TITLE), k -> new ArrayList<>()).add(id);
		}
	}

	private static List<Element> childElements(Element parent)
	{
		NodeList nodes = parent.getChildNodes();
		return IntStream.range(0, nodes.getLength())
				.mapToObj(nodes::item)
				.filter(n -> n.getNodeType() == Node.ELEMENT_NODE)
				.map(Element.class::cast)
				.toList();
	}

	/**
	 * Reads the {@code weight} attribute and defaults to 1 when absent or
	 * blank, matching the DTD's {@code weight CDATA "1"} default. The
	 * project uses a non-validating parser, so DTD-defaulted attributes
	 * arrive here as {@code ""} rather than {@code "1"}.
	 */
	private static int parseWeight(Element element)
	{
		String raw = element.getAttribute("weight");
		if (raw.isBlank())
		{
			return 1;
		}
		return Integer.parseInt(raw.trim());
	}

	private static List<Element> childElements(Element parent, String tagName)
	{
		NodeList nodes = parent.getChildNodes();
		return IntStream.range(0, nodes.getLength())
				.mapToObj(nodes::item)
				.filter(n -> n.getNodeType() == Node.ELEMENT_NODE)
				.map(Element.class::cast)
				.filter(e -> tagName.equals(e.getTagName()))
				.toList();
	}

	/**
	 * Returns the concatenation of direct child text nodes only,
	 * excluding text inside descendant elements. Needed because the
	 * data files use mixed content like
	 * {@code <VALUE>Donn<SUBVALUE>...</SUBVALUE></VALUE>} where the
	 * value is just {@code "Donn"} — {@code Element.getTextContent()}
	 * would return {@code "Donn"} concatenated with the subvalue's text.
	 */
	private static String directText(Element parent)
	{
		NodeList nodes = parent.getChildNodes();
		StringBuilder sb = new StringBuilder();
		IntStream.range(0, nodes.getLength())
				.mapToObj(nodes::item)
				.filter(n -> n.getNodeType() == Node.TEXT_NODE
						|| n.getNodeType() == Node.CDATA_SECTION_NODE)
				.forEach(n -> sb.append(n.getNodeValue()));
		return sb.toString();
	}

	/**
	 * Parsed DOM element + id, captured in pass 1 for use in pass 2.
	 */
	record RawRuleSet(Element element, String id)
	{
	}

	/**
	 * Per-file pass-1 result: the LIST and RULESET elements harvested.
	 */
	private record RawFile(List<Element> lists, List<Element> ruleSets)
	{
		static final RawFile EMPTY = new RawFile(List.of(), List.of());
	}

	/**
	 * Returns an {@link EntityResolver} that resolves {@code generator.dtd}
	 * from the given directory rather than the network. Other system ids are
	 * delegated to the parser's default resolution.
	 */
	private static EntityResolver dtdResolver(File parent)
	{
		return (publicId, systemId) ->
		{
			if (systemId == null || !systemId.endsWith("generator.dtd"))
			{
				return null;
			}
			File dtd = new File(parent, "generator.dtd");
			try
			{
				return new InputSource(Files.newInputStream(dtd.toPath()));
			} catch (IOException e)
			{
				Logging.errorPrint("Cannot open " + dtd, e);
				return null;
			}
		};
	}
}
