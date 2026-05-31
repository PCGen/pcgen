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
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * On-demand backend that mirrors what {@link NameGenData} exposes but only
 * parses each XML file when one of its rulesets is actually used. Built on
 * top of {@link NameGenIndex} (cheap per-file pre-scan) and the per-file
 * full-parse helpers in {@link NameGenDataLoader}.
 *
 * <p>A {@link RuleSet} returned to a caller is always fully materialised
 * (its {@link RuleSet#rules() rules} list is real, not a stub). The {@code
 * GETRULE} cross-references it contains keep working through
 * {@link RulePart.RuleSetRef}'s shared map; that map grows as more files are
 * parsed, so a ruleset that today references a not-yet-loaded ruleset will
 * resolve once that target's owning file is parsed.
 *
 * <p>Not thread-safe. The UI calls into this serially on the FX thread.
 */
public final class NameGenLazyData
{
	private final File dataDir;
	private final NameGenIndex index;

	// Live, mutable accumulators populated as files are parsed.
	private final Map<String, NameList> lists = new HashMap<>();
	private final Map<String, RuleSet> rulesets = new HashMap<>();
	private final List<NameGenData.UnresolvedReference> unresolved = new ArrayList<>();
	private final Set<File> parsedFiles = new LinkedHashSet<>();
	private final Set<File> inProgressFiles = new LinkedHashSet<>();

	private NameGenLazyData(File dataDir, NameGenIndex index)
	{
		this.dataDir = dataDir;
		this.index = index;
	}

	/**
	 * Build a lazy data layer over {@code dataDir}. Performs the StAX
	 * pre-scan immediately; XML bodies are not parsed until needed.
	 */
	public static NameGenLazyData open(File dataDir) throws IOException
	{
		NameGenIndex index = NameGenIndex.scan(dataDir);
		return new NameGenLazyData(dataDir, index);
	}

	/**
	 * Sorted display category titles, exactly as a UI combo would show.
	 * Reads only index metadata — no XML bodies parsed.
	 */
	public List<String> categoryTitles()
	{
		return List.copyOf(index.rulesetIdsByCategory().keySet());
	}

	/**
	 * Ruleset metadata for every ruleset declared under {@code category}.
	 * Reads index only — no parse triggered. Returns an empty list if the
	 * category is unknown.
	 */
	public List<NameGenIndex.RuleSetMeta> rulesetMetaFor(String category)
	{
		List<String> ids = index.rulesetIdsByCategory().getOrDefault(category, List.of());
		List<NameGenIndex.RuleSetMeta> out = new ArrayList<>(ids.size());
		for (String id : ids)
		{
			NameGenIndex.RuleSetMeta meta = index.rulesetsById().get(id);
			if (meta != null)
			{
				out.add(meta);
			}
		}
		return out;
	}

	/**
	 * Returns whether the ruleset id {@code candidate} is also declared
	 * under the {@code Sex: <gender>} bucket. Reads index only — no parse
	 * triggered.
	 */
	public boolean isInGenderBucket(String candidateId, String gender)
	{
		List<String> ids = index.rulesetIdsByCategory()
				.getOrDefault("Sex: " + gender, List.of());
		return ids.contains(candidateId);
	}

	/**
	 * Genders declared for ruleset id {@code rulesetId}. Reads index only.
	 */
	public List<String> gendersForRuleset(String rulesetId)
	{
		List<String> out = new ArrayList<>(3);
		for (Map.Entry<String, List<String>> e : index.rulesetIdsByCategory().entrySet())
		{
			String key = e.getKey();
			if (key.startsWith("Sex:") && e.getValue().contains(rulesetId))
			{
				out.add(key.substring("Sex:".length()).trim());
			}
		}
		return out;
	}

	/**
	 * Fully-materialised {@link RuleSet} for {@code rulesetId}. Triggers
	 * parsing of the owning file (and any files it transitively references
	 * via {@code GETLIST}/{@code GETRULE}). Returns {@code null} if the id
	 * is unknown.
	 */
	public RuleSet ruleSet(String rulesetId)
	{
		File owner = index.fileForRuleset(rulesetId);
		if (owner == null)
		{
			return null;
		}
		ensureFileParsed(owner);
		return rulesets.get(rulesetId);
	}

	/** Live ruleset map — every entry is fully materialised. */
	Map<String, RuleSet> liveRulesets()
	{
		return rulesets;
	}

	/** Live name-list map — entries appear as their owning files are parsed. */
	Map<String, NameList> liveLists()
	{
		return lists;
	}

	/** All ruleset metadata known to the index. */
	Map<String, NameGenIndex.RuleSetMeta> rulesetMeta()
	{
		return index.rulesetsById();
	}

	/** Index's category map (raw): category title -> ruleset ids. */
	Map<String, List<String>> rulesetIdsByCategory()
	{
		return index.rulesetIdsByCategory();
	}

	/** Live unresolved-references list, mutated as files get parsed. */
	public List<NameGenData.UnresolvedReference> unresolvedReferences()
	{
		return Collections.unmodifiableList(unresolved);
	}

	/** Files parsed so far. Useful for benchmarks/tests. */
	public Set<File> parsedFiles()
	{
		return Collections.unmodifiableSet(parsedFiles);
	}

	private String crossFileRuleSetTitle(String rulesetId)
	{
		NameGenIndex.RuleSetMeta meta = index.rulesetsById().get(rulesetId);
		return meta == null ? null : meta.title();
	}

	/**
	 * Idempotently parse {@code file} and every file it transitively pulls
	 * in via {@code GETLIST}/{@code GETRULE}. The {@code parsedFiles} guard
	 * keeps us from reparsing; {@code inProgressFiles} short-circuits
	 * cycles between mutually-referencing files. {@link RulePart.RuleSetRef}
	 * resolves through a shared map at generation time, so a cycle just
	 * means one ruleset's reference temporarily points at an unbuilt
	 * target — populated correctly once the outer call's phase 2 returns.
	 */
	private void ensureFileParsed(File file)
	{
		if (parsedFiles.contains(file) || inProgressFiles.contains(file))
		{
			return;
		}
		// Two-phase parse so cross-file GETLIST/GETRULE references can be
		// satisfied before this file's rule bodies are built. Phase 1
		// registers this file's <LIST>s into the live map and returns the
		// idrefs this file points at. We then transitively parse any
		// referenced files (which goes through the same two-phase dance).
		// Phase 2 finally builds this file's rulesets, by which time every
		// referenced list/ruleset is in the live maps.
		inProgressFiles.add(file);
		NameGenDataLoader.LazyFilePrepared prepared;
		try
		{
			try
			{
				prepared = NameGenDataLoader.prepareFileForLazy(file, dataDir, lists);
			}
			catch (IOException e)
			{
				throw new UncheckedIOException(e);
			}
			// Mark as parsed BEFORE recursing, otherwise a cycle (file A's
			// rules reference file B which references file A) causes
			// infinite recursion. The in-progress guard already short-
			// circuits, but adding to parsedFiles here also lets us return
			// early on identity-cycles without redoing prepare.
			parsedFiles.add(file);

			for (String idref : prepared.referencedListIds())
			{
				if (lists.containsKey(idref))
				{
					continue;
				}
				File target = index.fileForList(idref);
				if (target != null)
				{
					ensureFileParsed(target);
				}
			}
			for (String idref : prepared.referencedRuleSetIds())
			{
				if (rulesets.containsKey(idref))
				{
					continue;
				}
				File target = index.fileForRuleset(idref);
				if (target != null)
				{
					ensureFileParsed(target);
				}
			}

			// Phase 2: build this file's rulesets now that every cross-file
			// dependency has been registered.
			NameGenDataLoader.buildRuleSetsForLazy(prepared, lists, rulesets, unresolved,
					this::crossFileRuleSetTitle);
		}
		finally
		{
			inProgressFiles.remove(file);
		}
	}
}
