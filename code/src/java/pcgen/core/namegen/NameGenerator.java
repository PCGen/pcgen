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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * UI-toolkit-independent facade for the random-name engine. Backed by the
 * lazy {@link NameGenLazyData} loader: at construction time only a cheap
 * StAX index pre-scan runs; XML rule bodies are parsed when the user
 * actually selects a category/title/gender combination.
 *
 * <p>Categories on disk are keyed by display name; gender categories use
 * the {@code "Sex: Female"} / {@code "Sex: Male"} / {@code "Sex: Other"}
 * convention. This facade hides that representation: callers see
 * {@link #getCategories()} (no {@code Sex:} entries, no {@code All}) and
 * a separate {@link #getGendersFor(String, String)} method.
 */
public final class NameGenerator
{
	private static final String SEX_PREFIX = "Sex:";
	private static final String ALL_CATEGORY = "All";
	private static final String FINAL_USAGE = "final";

	private final NameGenLazyData backing;

	public NameGenerator(File dataDir) throws IOException
	{
		this.backing = NameGenLazyData.open(dataDir);
	}

	/**
	 * Sorted display-only categories (no {@code Sex:} entries, no
	 * {@code All}). Suitable for direct use as a combo-box model.
	 */
	public List<String> getCategories()
	{
		return backing.categoryTitles().stream()
				.filter(key -> !key.startsWith(SEX_PREFIX))
				.filter(key -> !ALL_CATEGORY.equals(key))
				.sorted()
				.toList();
	}

	/**
	 * Sorted titles available within {@code category} (final-usage rulesets
	 * only). A title appearing under multiple categories will be returned
	 * for each of them.
	 *
	 * <p>Reads index metadata only — no XML body parsed.
	 */
	public List<String> getTitlesFor(String category)
	{
		return backing.rulesetMetaFor(category).stream()
				.filter(meta -> FINAL_USAGE.equals(meta.usage()))
				.map(NameGenIndex.RuleSetMeta::title)
				.distinct()
				.sorted()
				.toList();
	}

	/**
	 * Genders available for a given (category, title). Returned in the
	 * {@code [Female, Male, Other]} canonical order; entries missing from
	 * the data are simply absent.
	 *
	 * <p>Reads index metadata only — no XML body parsed.
	 */
	public List<String> getGendersFor(String category, String title)
	{
		Set<String> raw = backing.rulesetMetaFor(category).stream()
				.filter(meta -> FINAL_USAGE.equals(meta.usage()))
				.filter(meta -> meta.title().equals(title))
				.flatMap(meta -> backing.gendersForRuleset(meta.id()).stream())
				.collect(Collectors.toSet());

		Set<String> ordered = new LinkedHashSet<>();
		Stream.of("Female", "Male", "Other").filter(raw::contains).forEach(ordered::add);
		ordered.addAll(raw);
		return List.copyOf(ordered);
	}

	/**
	 * Resolve a (category, title, gender) triple to a final-usage ruleset.
	 * Triggers parsing of the file containing the matched ruleset (and any
	 * files it transitively references via {@code GETLIST}/{@code GETRULE}).
	 *
	 * @return matching {@link RuleSet}, or {@code null} if no entry matches
	 */
	public RuleSet getCatalog(String category, String title, String gender)
	{
		return backing.rulesetMetaFor(category).stream()
				.filter(meta -> FINAL_USAGE.equals(meta.usage()))
				.filter(meta -> meta.title().equals(title))
				.filter(meta -> backing.isInGenderBucket(meta.id(), gender))
				.findFirst()
				.map(meta -> backing.ruleSet(meta.id()))
				.orElse(null);
	}

	/**
	 * Pick a {@link Rule} from {@code catalog} by weighted random and
	 * generate a name from it.
	 */
	public GeneratedName generate(RuleSet catalog)
	{
		Rule rule = catalog.pick();
		if (rule == null)
		{
			return new GeneratedName("", "", "", null, List.of());
		}
		List<DataValue> parts = rule.generate();
		return assemble(rule, parts);
	}

	/**
	 * Generate a name forcing a specific {@link Rule} (Structure override).
	 */
	public GeneratedName generateWithRule(Rule forcedRule)
	{
		List<DataValue> parts = forcedRule.generate();
		return assemble(forcedRule, parts);
	}

	/**
	 * Returns the {@link Rule} alternatives within {@code catalog}; used by
	 * the Advanced "Structure" picker.
	 */
	public List<Rule> getRulesFor(RuleSet catalog)
	{
		return catalog.rules();
	}

	/**
	 * Exposes a snapshot of all loaded data. Forces every XML file to be
	 * parsed (equivalent to the legacy eager load). Primarily for tests
	 * that want to inspect the full corpus.
	 */
	public NameGenData getData()
	{
		// Parse every ruleset declared anywhere — that pulls in every
		// referenced list/ruleset transitively, which between them touch
		// every file.
		for (String id : backing.rulesetMeta().keySet())
		{
			backing.ruleSet(id);
		}
		Map<String, RuleSet> rulesets = new LinkedHashMap<>(backing.liveRulesets());

		Map<String, List<RuleSet>> categories = backing.rulesetIdsByCategory().entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						e -> e.getValue().stream()
								.map(rulesets::get)
								.filter(Objects::nonNull)
								.toList(),
						(a, b) -> b,
						LinkedHashMap::new));
		return new NameGenData(backing.liveLists(), rulesets, categories,
				backing.unresolvedReferences());
	}

	private static GeneratedName assemble(Rule rule, List<DataValue> parts)
	{
		StringBuilder name = new StringBuilder();
		StringBuilder meaning = new StringBuilder();
		StringBuilder pron = new StringBuilder();
		boolean anyMeaning = false;
		boolean anyPron = false;
		for (DataValue v : parts)
		{
			name.append(v.getValue());

			String m = v.getSubValue("meaning");
			meaning.append(m == null ? v.getValue() : m);
			anyMeaning |= m != null;

			String p = v.getSubValue("pronounciation");
			pron.append(p == null ? v.getValue() : p);
			anyPron |= p != null;
		}
		return new GeneratedName(name.toString(),
				anyMeaning ? meaning.toString() : "",
				anyPron ? pron.toString() : "",
				rule, List.copyOf(parts));
	}
}
