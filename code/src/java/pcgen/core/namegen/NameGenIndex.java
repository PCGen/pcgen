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
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang3.StringUtils;

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
	private static final String TAG_RULESET = "RULESET";
	private static final String TAG_CATEGORY = "CATEGORY";
	private static final String TAG_LIST = "LIST";
	private static final String ATTR_ID = "id";
	private static final String ATTR_TITLE = "title";
	private static final String ATTR_USAGE = "usage";

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

	/**
	 * Drives the StAX state machine over one document. The body of every
	 * {@code <LIST>} is skipped via a depth counter on {@link ScanState}
	 * rather than parsed; only attribute headers on {@code <RULESET>},
	 * {@code <CATEGORY>}, and {@code <LIST>} matter to the index.
	 */
	private static void scanStream(XMLStreamReader reader, File file,
			Map<String, RuleSetMeta> rulesetsById,
			Map<String, File> listIdToFile,
			Map<String, List<String>> rulesetIdsByCategory) throws XMLStreamException
	{
		ScanState state = new ScanState(file, rulesetsById, listIdToFile, rulesetIdsByCategory);
		while (reader.hasNext())
		{
			int event = reader.next();
			if (event == XMLStreamConstants.START_ELEMENT)
			{
				state.handleStart(reader);
			}
			else if (event == XMLStreamConstants.END_ELEMENT)
			{
				state.handleEnd(reader.getLocalName());
			}
		}
	}

	/**
	 * Streaming-scan accumulator. Holds the in-flight {@code <RULESET>}'s
	 * attributes and a depth counter for skipping {@code <LIST>} bodies, and
	 * commits a {@link RuleSetMeta} record on each {@code </RULESET>}.
	 */
	private static final class ScanState
	{
		private final File file;
		private final Map<String, RuleSetMeta> rulesetsById;
		private final Map<String, File> listIdToFile;
		private final Map<String, List<String>> rulesetIdsByCategory;

		private String currentRulesetId;
		private String currentRulesetTitle;
		private String currentRulesetUsage;
		private List<String> currentCategories;
		private int listDepth;

		ScanState(File file,
				Map<String, RuleSetMeta> rulesetsById,
				Map<String, File> listIdToFile,
				Map<String, List<String>> rulesetIdsByCategory)
		{
			this.file = file;
			this.rulesetsById = rulesetsById;
			this.listIdToFile = listIdToFile;
			this.rulesetIdsByCategory = rulesetIdsByCategory;
		}

		void handleStart(XMLStreamReader reader)
		{
			if (listDepth > 0)
			{
				// Inside a <LIST>; ignore everything until close.
				listDepth++;
				return;
			}
			switch (reader.getLocalName())
			{
				case TAG_RULESET -> beginRuleSet(reader);
				case TAG_CATEGORY -> addCategory(reader);
				case TAG_LIST -> beginList(reader);
				default ->
				{
					// We don't care about RULE/GETLIST/etc. for the index.
				}
			}
		}

		void handleEnd(String localName)
		{
			if (listDepth > 0)
			{
				listDepth--;
				return;
			}
			if (TAG_RULESET.equals(localName) && currentRulesetId != null)
			{
				commitRuleSet();
			}
		}

		private void beginRuleSet(XMLStreamReader reader)
		{
			currentRulesetId = reader.getAttributeValue(null, ATTR_ID);
			currentRulesetTitle = StringUtils.defaultString(reader.getAttributeValue(null, ATTR_TITLE));
			currentRulesetUsage = StringUtils.defaultString(reader.getAttributeValue(null, ATTR_USAGE));
			currentCategories = new ArrayList<>();
		}

		private void addCategory(XMLStreamReader reader)
		{
			if (currentCategories == null)
			{
				return;
			}
			String title = reader.getAttributeValue(null, ATTR_TITLE);
			if (title != null)
			{
				currentCategories.add(title);
			}
		}

		private void beginList(XMLStreamReader reader)
		{
			String id = reader.getAttributeValue(null, ATTR_ID);
			if (id != null)
			{
				listIdToFile.put(id, file);
			}
			listDepth = 1;
		}

		private void commitRuleSet()
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

	private static Map<String, List<String>> unmodifiableDeep(Map<String, List<String>> src)
	{
		return src.entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						e -> List.copyOf(e.getValue()),
						(a, b) -> b,
						LinkedHashMap::new));
	}
}
