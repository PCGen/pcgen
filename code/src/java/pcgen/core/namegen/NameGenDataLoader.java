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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		VariableHashMap allVars = new VariableHashMap();
		Map<String, List<RuleSet>> categories = new HashMap<>();
		DocumentBuilder builder = newDocumentBuilder();
		builder.setEntityResolver(new GeneratorDtdResolver(dataDir));

		for (File dataFile : dataFiles)
		{
			try
			{
				Document nameSet = builder.parse(dataFile);
				DocumentType dt = nameSet.getDoctype();
				if (dt != null && "GENERATOR".equals(dt.getName()))
				{
					loadFromDocument(nameSet, allVars, categories);
				}
			}
			catch (SAXException | NumberFormatException e)
			{
				Logging.errorPrint("Failed to parse " + dataFile.getName(), e);
				throw new IOException("XML error in file " + dataFile.getName(), e);
			}
		}
		return new NameGenData(allVars, categories);
	}

	private static DocumentBuilder newDocumentBuilder() throws IOException
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			return factory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			throw new IOException("Cannot create XML parser", e);
		}
	}

	private static void loadFromDocument(Document nameSet, VariableHashMap allVars,
			Map<String, List<RuleSet>> categories)
	{
		Element generator = nameSet.getDocumentElement();
		childElements(generator, "LIST").forEach(list -> loadList(list, allVars));
		childElements(generator, "RULESET").forEach(ruleset -> {
			RuleSet rs = loadRuleSet(ruleset, allVars, categories);
			allVars.addDataElement(rs);
		});
	}

	private static String loadList(Element list, VariableHashMap allVars)
	{
		DDList dataList = new DDList(allVars,
				list.getAttribute("title"), list.getAttribute("id"));
		for (Element child : childElements(list, "VALUE"))
		{
			WeightedDataValue dv = new WeightedDataValue(directText(child),
					Integer.parseInt(child.getAttribute("weight")));
			childElements(child, "SUBVALUE").forEach(sub ->
					dv.addSubValue(sub.getAttribute("type"), directText(sub)));
			dataList.add(dv);
		}
		allVars.addDataElement(dataList);
		return dataList.getId();
	}

	private static String loadRule(Element rule, String id, VariableHashMap allVars)
	{
		Rule dataRule = new Rule(allVars, id, id, Integer.parseInt(rule.getAttribute("weight")));
		for (Element child : childElements(rule))
		{
			switch (child.getTagName())
			{
				case "GETLIST" -> dataRule.add(child.getAttribute("idref"));
				case "SPACE" -> {
					SpaceRule sp = new SpaceRule();
					allVars.addDataElement(sp);
					dataRule.add(sp.getId());
				}
				case "HYPHEN" -> {
					HyphenRule hy = new HyphenRule();
					allVars.addDataElement(hy);
					dataRule.add(hy.getId());
				}
				case "CR" -> {
					CRRule cr = new CRRule();
					allVars.addDataElement(cr);
					dataRule.add(cr.getId());
				}
				case "GETRULE" -> dataRule.add(child.getAttribute("idref"));
				default -> { /* ignore */ }
			}
		}
		allVars.addDataElement(dataRule);
		return dataRule.getId();
	}

	private static RuleSet loadRuleSet(Element ruleSet, VariableHashMap allVars,
			Map<String, List<RuleSet>> categories)
	{
		RuleSet rs = new RuleSet(allVars, ruleSet.getAttribute("title"),
				ruleSet.getAttribute("id"), ruleSet.getAttribute("usage"));
		List<Element> children = childElements(ruleSet);
		// Index counter is preserved across all child element types — it's
		// part of the generated rule id and must match the legacy numbering.
		int num = 0;
		for (Element child : children)
		{
			String elementName = child.getTagName();
			if ("CATEGORY".equals(elementName))
			{
				loadCategory(child, rs, categories);
			}
			else if ("RULE".equals(elementName))
			{
				rs.add(loadRule(child, rs.getId() + num, allVars));
			}
			num++;
		}
		return rs;
	}

	private static void loadCategory(Element category, RuleSet rs, Map<String, List<RuleSet>> categories)
	{
		String key = category.getAttribute("title");
		categories.computeIfAbsent(key, k -> new ArrayList<>()).add(rs);
	}

	private static List<Element> childElements(Element parent)
	{
		NodeList nodes = parent.getChildNodes();
		List<Element> out = new ArrayList<>(nodes.getLength());
		IntStream.range(0, nodes.getLength())
				.mapToObj(nodes::item)
				.filter(n -> n.getNodeType() == Node.ELEMENT_NODE)
				.map(Element.class::cast)
				.forEach(out::add);
		return out;
	}

	private static List<Element> childElements(Element parent, String tagName)
	{
		return childElements(parent).stream()
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
