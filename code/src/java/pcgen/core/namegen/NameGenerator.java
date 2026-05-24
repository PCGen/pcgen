/*
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * UI-toolkit-independent facade for the random-name engine. Loads XML data
 * once at construction and exposes operations a UI (Swing, JavaFX, CLI)
 * needs to drive a category/title/gender picker and produce names.
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

	private final NameGenData data;

	public NameGenerator(File dataDir) throws IOException
	{
		this.data = NameGenDataLoader.load(dataDir);
	}

	/**
	 * Sorted display-only categories (no {@code Sex:} entries, no
	 * {@code All}). Suitable for direct use as a combo-box model.
	 */
	public List<String> getCategories()
	{
		return data.categories().keySet().stream()
				.filter(key -> !key.startsWith(SEX_PREFIX))
				.filter(key -> !ALL_CATEGORY.equals(key))
				.sorted()
				.toList();
	}

	/**
	 * Sorted titles available within {@code category} (final-usage rulesets
	 * only). A title appearing under multiple categories will be returned
	 * for each of them.
	 */
	public List<String> getTitlesFor(String category)
	{
		return data.categories().getOrDefault(category, List.of()).stream()
				.filter(rs -> FINAL_USAGE.equals(rs.usage()))
				.map(RuleSet::title)
				.distinct()
				.sorted()
				.toList();
	}

	/**
	 * Genders available for a given (category, title). Returned in the
	 * {@code [Female, Male, Other]} canonical order; entries missing from
	 * the data are simply absent.
	 */
	public List<String> getGendersFor(String category, String title)
	{
		Set<String> raw = data.categories().getOrDefault(category, List.of()).stream()
				.filter(rs -> FINAL_USAGE.equals(rs.usage()))
				.filter(rs -> rs.title().equals(title))
				.flatMap(rs -> data.categories().entrySet().stream()
						.filter(e -> e.getKey().startsWith(SEX_PREFIX) && e.getValue().contains(rs))
						.map(e -> e.getKey().substring(SEX_PREFIX.length()).trim()))
				.collect(java.util.stream.Collectors.toSet());

		// canonical order first, then any non-canonical genders (defensive)
		Set<String> ordered = new LinkedHashSet<>();
		Stream.of("Female", "Male", "Other").filter(raw::contains).forEach(ordered::add);
		ordered.addAll(raw);
		return List.copyOf(ordered);
	}

	/**
	 * Resolve a (category, title, gender) triple to a final-usage ruleset.
	 *
	 * @return matching {@link RuleSet}, or {@code null} if no entry matches
	 */
	public RuleSet getCatalog(String category, String title, String gender)
	{
		List<RuleSet> genderRules = data.categories().getOrDefault(SEX_PREFIX + " " + gender, List.of());
		return data.categories().getOrDefault(category, List.of()).stream()
				.filter(rs -> FINAL_USAGE.equals(rs.usage()))
				.filter(rs -> rs.title().equals(title))
				.filter(genderRules::contains)
				.findFirst()
				.orElse(null);
	}

	/**
	 * Pick a {@link Rule} from {@code catalog} by weighted random and
	 * generate a name from it.
	 */
	public GeneratedName generate(RuleSet catalog)
	{
		Rule rule = catalog.pick();
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
	 * Exposes the loaded data, primarily for tests.
	 */
	public NameGenData getData()
	{
		return data;
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
