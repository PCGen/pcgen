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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.jdom2.DataConversionException;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import pcgen.util.Logging;

/**
 * Loads random-name XML files from a directory into a {@link NameGenData}
 * snapshot. The legacy loading code lived inline in
 * {@code pcgen.gui2.namegen.NameGenPanel}; pulling it here decouples the
 * data model from any UI toolkit.
 *
 * <p>This class still uses JDOM2; a follow-up commit replaces it with the
 * built-in {@code javax.xml.parsers} API.
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
	 * @throws IOException if {@code dataDir} is not a directory
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
		SAXBuilder builder = new SAXBuilder();
		builder.setEntityResolver(new GeneratorDtdResolver(dataDir));

		for (File dataFile : dataFiles)
		{
			try
			{
				URL url = dataFile.toURI().toURL();
				Document nameSet = builder.build(url);
				DocType dt = nameSet.getDocType();
				if (dt != null && "GENERATOR".equals(dt.getElementName()))
				{
					loadFromDocument(nameSet, allVars, categories);
				}
			}
			catch (JDOMException | IOException e)
			{
				Logging.errorPrint("Failed to parse " + dataFile.getName(), e);
				throw new IOException("XML error in file " + dataFile.getName(), e);
			}
		}
		return new NameGenData(allVars, categories);
	}

	private static void loadFromDocument(Document nameSet, VariableHashMap allVars,
			Map<String, List<RuleSet>> categories) throws DataConversionException
	{
		Element generator = nameSet.getRootElement();
		List<?> rulesets = generator.getChildren("RULESET");
		List<?> lists = generator.getChildren("LIST");

		for (final Object o : lists)
		{
			loadList((Element) o, allVars);
		}
		for (final Object ruleset : rulesets)
		{
			RuleSet rs = loadRuleSet((Element) ruleset, allVars, categories);
			allVars.addDataElement(rs);
		}
	}

	private static String loadList(Element list, VariableHashMap allVars) throws DataConversionException
	{
		DDList dataList = new DDList(allVars,
				list.getAttributeValue("title"), list.getAttributeValue("id"));
		for (Element child : list.getChildren("VALUE"))
		{
			WeightedDataValue dv = new WeightedDataValue(child.getText(),
					child.getAttribute("weight").getIntValue());
			child.getChildren("SUBVALUE").forEach(sub ->
					dv.addSubValue(sub.getAttributeValue("type"), sub.getText()));
			dataList.add(dv);
		}
		allVars.addDataElement(dataList);
		return dataList.getId();
	}

	private static String loadRule(Element rule, String id, VariableHashMap allVars) throws DataConversionException
	{
		Rule dataRule = new Rule(allVars, id, id, rule.getAttribute("weight").getIntValue());
		List<?> elements = rule.getChildren();
		for (final Object element : elements)
		{
			Element child = (Element) element;
			switch (child.getName())
			{
				case "GETLIST" -> dataRule.add(child.getAttributeValue("idref"));
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
				case "GETRULE" -> dataRule.add(child.getAttributeValue("idref"));
				default -> { /* ignore */ }
			}
		}
		allVars.addDataElement(dataRule);
		return dataRule.getId();
	}

	private static RuleSet loadRuleSet(Element ruleSet, VariableHashMap allVars,
			Map<String, List<RuleSet>> categories) throws DataConversionException
	{
		RuleSet rs = new RuleSet(allVars, ruleSet.getAttributeValue("title"),
				ruleSet.getAttributeValue("id"), ruleSet.getAttributeValue("usage"));
		List<?> elements = ruleSet.getChildren();
		ListIterator<?> it = elements.listIterator();
		int num = 0;
		while (it.hasNext())
		{
			Element child = (Element) it.next();
			String elementName = child.getName();
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
		String key = category.getAttributeValue("title");
		categories.computeIfAbsent(key, k -> new ArrayList<>()).add(rs);
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
