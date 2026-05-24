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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
		builder.setEntityResolver(new GeneratorDtdResolver(dataDir));

		// Pass 1: parse each file, harvest raw LIST and RULESET elements.
		Map<String, Element> rawLists = new LinkedHashMap<>();
		Map<String, RawRuleSet> rawRuleSets = new LinkedHashMap<>();
		Map<String, List<String>> rawCategories = new LinkedHashMap<>();

		for (File dataFile : dataFiles)
		{
			try
			{
				Document nameSet = builder.parse(dataFile);
				DocumentType dt = nameSet.getDoctype();
				if (dt == null || !"GENERATOR".equals(dt.getName()))
				{
					continue;
				}
				Element generator = nameSet.getDocumentElement();
				for (Element list : childElements(generator, "LIST"))
				{
					rawLists.put(list.getAttribute("id"), list);
				}
				for (Element ruleSet : childElements(generator, "RULESET"))
				{
					String id = ruleSet.getAttribute("id");
					rawRuleSets.put(id, new RawRuleSet(ruleSet, id));
				}
			}
			catch (SAXException | NumberFormatException e)
			{
				Logging.errorPrint("Failed to parse " + dataFile.getName(), e);
				throw new IOException("XML error in file " + dataFile.getName(), e);
			}
		}

		// Pass 2a: build NameList records (no cross-refs).
		Map<String, NameList> lists = new LinkedHashMap<>();
		for (Map.Entry<String, Element> entry : rawLists.entrySet())
		{
			lists.put(entry.getKey(), buildList(entry.getValue()));
		}

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
		Map<String, List<RuleSet>> categories = new LinkedHashMap<>();
		for (Map.Entry<String, List<String>> entry : rawCategories.entrySet())
		{
			List<RuleSet> resolved = entry.getValue().stream()
					.map(rulesets::get)
					.filter(Objects::nonNull)
					.toList();
			categories.put(entry.getKey(), resolved);
		}

		return new NameGenData(lists, rulesets, categories, unresolved);
	}

	private static DocumentBuilder newDocumentBuilder() throws IOException
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// XXE hardening: data files are local but the parser shouldn't
			// fetch external entities or evaluate parameter entities. We
			// keep external-DTD loading on so generator.dtd still resolves
			// through the EntityResolver.
			factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			factory.setXIncludeAware(false);
			factory.setExpandEntityReferences(false);
			return factory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			throw new IOException("Cannot create XML parser", e);
		}
	}

	private static NameList buildList(Element list)
	{
		String id = list.getAttribute("id");
		String title = list.getAttribute("title");
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
				raw.element.getAttribute("title"),
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
				case "GETLIST" -> resolveListRef(child.getAttribute("idref"), lists, unresolved);
				case "GETRULE" -> resolveRuleSetRef(child.getAttribute("idref"), rulesets, rawRuleSets, unresolved);
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
		String title = raw.element.getAttribute("title");
		return new RulePart.RuleSetRef(idref, title, rulesets);
	}

	private static void collectCategories(Element ruleSet, String id,
			Map<String, List<String>> rawCategories)
	{
		for (Element category : childElements(ruleSet, "CATEGORY"))
		{
			rawCategories.computeIfAbsent(category.getAttribute("title"), k -> new ArrayList<>()).add(id);
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

	/** Parsed DOM element + id, captured in pass 1 for use in pass 2. */
	private record RawRuleSet(Element element, String id) { }

	/**
	 * Resolves {@code generator.dtd} from a known directory rather than the
	 * network. Carried over verbatim from the old Swing panel.
	 */
	static final class GeneratorDtdResolver implements EntityResolver
	{
		private final File parent;

		GeneratorDtdResolver(File parent)
		{
			this.parent = parent;
		}

		@Override
		public InputSource resolveEntity(String publicId, String systemId)
		{
			if (systemId.endsWith("generator.dtd"))
			{
				InputStream dtdIn;
				try
				{
					dtdIn = new FileInputStream(new File(parent, "generator.dtd"));
				}
				catch (FileNotFoundException e)
				{
					Logging.errorPrint("GeneratorDtdResolver.resolveEntity failed", e);
					return null;
				}
				return new InputSource(dtdIn);
			}
			return null;
		}
	}
}
