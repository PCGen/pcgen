/*
 * Copyright 2026 Vest <Vest@users.noreply.github.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package pcgen.core.namegen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import pcgen.util.Logging;

/**
 * Lightweight pre-scan of the random-name XML directory. Reads only top-level
 * {@code <RULESET>} / {@code <LIST>} attributes and the {@code <CATEGORY>}
 * children of each ruleset — never the {@code <RULE>} or {@code <VALUE>}
 * bodies — so it is dramatically cheaper than a full DOM parse.
 *
 * <p>Drives the lazy-load path: at startup the UI only needs to know which
 * categories exist and which rulesets/lists each file declares. The bodies
 * are parsed on demand by {@link NameGenLazyData} once the user picks a
 * category.
 */
public final class NameGenIndex
{
	private final Map<String, RuleSetMeta> rulesetsById;
	private final Map<String, File> listIdToFile;
	private final Map<String, List<String>> rulesetIdsByCategory;

	private NameGenIndex(Map<String, RuleSetMeta> rulesetsById,
			Map<String, File> listIdToFile,
			Map<String, List<String>> rulesetIdsByCategory)
	{
		this.rulesetsById = Map.copyOf(rulesetsById);
		this.listIdToFile = Map.copyOf(listIdToFile);
		this.rulesetIdsByCategory = unmodifiableDeep(rulesetIdsByCategory);
	}

	/**
	 * Scan every {@code *.xml} file in {@code dataDir} for ruleset/list/
	 * category metadata. Returns an empty index if no files match.
	 *
	 * @throws IOException if {@code dataDir} is not a directory or any file
	 *                     fails to scan
	 */
	public static NameGenIndex scan(File dataDir) throws IOException
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
		Arrays.sort(dataFiles);

		XMLInputFactory factory = newSecureInputFactory();
		Map<String, RuleSetMeta> rulesetsById = new LinkedHashMap<>();
		Map<String, File> listIdToFile = new LinkedHashMap<>();
		Map<String, List<String>> rulesetIdsByCategory = new LinkedHashMap<>();

		for (File file : dataFiles)
		{
			scanOne(file, factory, rulesetsById, listIdToFile, rulesetIdsByCategory);
		}
		return new NameGenIndex(rulesetsById, listIdToFile, rulesetIdsByCategory);
	}

	/** All ruleset ids declared anywhere, mapped to their metadata. */
	public Map<String, RuleSetMeta> rulesetsById()
	{
		return rulesetsById;
	}

	/** Owning file for a {@code <LIST id="...">}, or {@code null} if unknown. */
	public File fileForList(String listId)
	{
		return listIdToFile.get(listId);
	}

	/** Owning file for a {@code <RULESET id="...">}, or {@code null} if unknown. */
	public File fileForRuleset(String rulesetId)
	{
		RuleSetMeta meta = rulesetsById.get(rulesetId);
		return meta == null ? null : meta.file();
	}

	/**
	 * Ordered ruleset ids declared under each {@code <CATEGORY title="...">}.
	 * Order matches scan order (file alphabetical, then declaration order).
	 */
	public Map<String, List<String>> rulesetIdsByCategory()
	{
		return rulesetIdsByCategory;
	}

	/**
	 * Per-ruleset metadata gathered without parsing rule bodies: enough to
	 * populate the UI's category/title/gender pickers.
	 */
	public record RuleSetMeta(File file, String id, String title, String usage,
			List<String> categoryTitles)
	{
		public RuleSetMeta
		{
			categoryTitles = List.copyOf(categoryTitles);
		}
	}

	private static void scanOne(File file, XMLInputFactory factory,
			Map<String, RuleSetMeta> rulesetsById,
			Map<String, File> listIdToFile,
			Map<String, List<String>> rulesetIdsByCategory) throws IOException
	{
		try (InputStream in = new FileInputStream(file))
		{
			// Pass a systemId so the parser can resolve the DOCTYPE's
			// relative SYSTEM "generator.dtd" against the data file's URL.
			XMLStreamReader reader = factory.createXMLStreamReader(
					file.toURI().toString(), in);
			try
			{
				scanStream(reader, file, rulesetsById, listIdToFile, rulesetIdsByCategory);
			}
			finally
			{
				reader.close();
			}
		}
		catch (XMLStreamException e)
		{
			Logging.errorPrint("Failed to pre-scan " + file.getName(), e);
			throw new IOException("XML error scanning " + file.getName(), e);
		}
	}

	private static void scanStream(XMLStreamReader reader, File file,
			Map<String, RuleSetMeta> rulesetsById,
			Map<String, File> listIdToFile,
			Map<String, List<String>> rulesetIdsByCategory) throws XMLStreamException
	{
		// State: when we enter a <RULESET>, capture its attrs and begin
		// collecting <CATEGORY> children. When we leave, emit a meta record.
		// <LIST> is recorded by id only; we skip its body entirely.
		String currentRulesetId = null;
		String currentRulesetTitle = null;
		String currentRulesetUsage = null;
		List<String> currentCategories = null;
		int listDepth = 0;

		while (reader.hasNext())
		{
			int event = reader.next();
			if (event == XMLStreamConstants.START_ELEMENT)
			{
				String localName = reader.getLocalName();
				if (listDepth > 0)
				{
					// We're inside a <LIST>; ignore everything until close.
					listDepth++;
					continue;
				}
				switch (localName)
				{
					case "RULESET" ->
					{
						currentRulesetId = reader.getAttributeValue(null, "id");
						currentRulesetTitle = nullToEmpty(reader.getAttributeValue(null, "title"));
						currentRulesetUsage = nullToEmpty(reader.getAttributeValue(null, "usage"));
						currentCategories = new ArrayList<>();
					}
					case "CATEGORY" ->
					{
						if (currentCategories != null)
						{
							String title = reader.getAttributeValue(null, "title");
							if (title != null)
							{
								currentCategories.add(title);
							}
						}
					}
					case "LIST" ->
					{
						String id = reader.getAttributeValue(null, "id");
						if (id != null)
						{
							listIdToFile.put(id, file);
						}
						listDepth = 1;
					}
					default ->
					{
						// We don't care about RULE/GETLIST/etc. for the index.
					}
				}
			}
			else if (event == XMLStreamConstants.END_ELEMENT)
			{
				String localName = reader.getLocalName();
				if (listDepth > 0)
				{
					listDepth--;
					continue;
				}
				if ("RULESET".equals(localName) && currentRulesetId != null)
				{
					RuleSetMeta meta = new RuleSetMeta(file, currentRulesetId,
							currentRulesetTitle, currentRulesetUsage,
							currentCategories);
					rulesetsById.put(currentRulesetId, meta);
					for (String cat : currentCategories)
					{
						rulesetIdsByCategory
								.computeIfAbsent(cat, k -> new ArrayList<>())
								.add(currentRulesetId);
					}
					currentRulesetId = null;
					currentRulesetTitle = null;
					currentRulesetUsage = null;
					currentCategories = null;
				}
			}
		}
	}

	private static XMLInputFactory newSecureInputFactory()
	{
		XMLInputFactory factory = XMLInputFactory.newInstance();
		// XXE hardening. The data files declare <!DOCTYPE GENERATOR SYSTEM
		// "generator.dtd"> so we must allow `file` access for the local DTD,
		// but not network access. External entity references stay disabled.
		factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
		factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "file");
		return factory;
	}

	private static String nullToEmpty(String s)
	{
		return s == null ? "" : s;
	}

	private static Map<String, List<String>> unmodifiableDeep(Map<String, List<String>> src)
	{
		Map<String, List<String>> out = new LinkedHashMap<>(src.size());
		for (Map.Entry<String, List<String>> e : src.entrySet())
		{
			out.put(e.getKey(), List.copyOf(e.getValue()));
		}
		return Map.copyOf(out);
	}
}
