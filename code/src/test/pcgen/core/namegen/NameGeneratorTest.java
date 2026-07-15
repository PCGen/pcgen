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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Behavioural tests for {@link NameGenerator} driven by the bundled
 * {@code plugins/Random Names} dataset.
 */
class NameGeneratorTest
{
	private static final File DATA_DIR =
			new File(System.getProperty("user.dir"), "plugins/Random Names");

	private static NameGenerator generator;

	@BeforeAll
	static void loadOnce() throws IOException
	{
		generator = new NameGenerator(DATA_DIR);
	}

	@Test
	void getCategoriesHidesInternalBuckets()
	{
		List<String> categories = generator.getCategories();
		assertFalse(categories.isEmpty());
		assertTrue(categories.stream().noneMatch(c -> c.startsWith("Sex:")),
				"Sex: buckets must not leak through the facade");
		assertTrue(categories.stream().noneMatch("All"::equals),
				"the 'All' pseudo-category must not leak through");
		assertEquals(categories.stream().sorted().toList(), categories,
				"categories must be sorted");
	}

	@Test
	void getTitlesForUnknownCategoryReturnsEmpty()
	{
		assertTrue(generator.getTitlesFor("definitely-not-a-category").isEmpty());
	}

	@Test
	void getTitlesForFantasyElvenIsNonEmptyAndSorted()
	{
		List<String> titles = generator.getTitlesFor("Fantasy: Elven");
		assertFalse(titles.isEmpty(), "Fantasy: Elven should have titles");
		assertEquals(titles.stream().sorted().toList(), titles, "titles must be sorted");
	}

	@Test
	void getGendersForCanonicalOrder()
	{
		List<String> genders = generator.getGendersFor("Fantasy: Elven", "Middle Earth Elf");
		assertFalse(genders.isEmpty());
		// Whatever subset shows up must appear in canonical order.
		List<String> canonical = List.of("Female", "Male", "Other");
		List<String> filteredCanonical = canonical.stream().filter(genders::contains).toList();
		assertEquals(filteredCanonical, genders.subList(0, filteredCanonical.size()),
				"canonical genders must come first in canonical order");
	}

	@Test
	void getCatalogResolvesKnownTriple()
	{
		RuleSet rs = generator.getCatalog("Fantasy: Elven", "Middle Earth Elf", "Male");
		assertNotNull(rs, "Fantasy: Elven / Middle Earth Elf / Male should resolve");
		assertEquals("final", rs.usage());
		assertEquals("Middle Earth Elf", rs.title());
	}

	@Test
	void getCatalogReturnsNullForUnknownTriple()
	{
		assertNull(generator.getCatalog("Fantasy: Elven", "Middle Earth Elf", "Nonexistent"));
		assertNull(generator.getCatalog("Fantasy: Elven", "no-such-title", "Male"));
		assertNull(generator.getCatalog("no-such-category", "Middle Earth Elf", "Male"));
	}

	@Test
	void generateProducesNonEmptyNameRepeatedly() throws Exception
	{
		RuleSet rs = generator.getCatalog("Fantasy: Elven", "Middle Earth Elf", "Male");
		assertNotNull(rs);
		for (int i = 0; i < 100; i++)
		{
			GeneratedName generated = generator.generate(rs);
			assertNotNull(generated.rule());
			assertFalse(generated.name().isBlank(), "iteration " + i + " produced blank name");
		}
	}

	@Test
	void generateWithRuleAlwaysUsesGivenRule() throws Exception
	{
		RuleSet rs = generator.getCatalog("Fantasy: Elven", "Middle Earth Elf", "Male");
		List<Rule> rules = generator.getRulesFor(rs);
		assertFalse(rules.isEmpty());
		Rule chosen = rules.get(0);
		for (int i = 0; i < 20; i++)
		{
			GeneratedName generated = generator.generateWithRule(chosen);
			assertEquals(chosen, generated.rule());
		}
	}

	@Test
	void mixedContentValueExcludesSubvalueText() throws Exception
	{
		// In gaelic.xml, <VALUE>Donn<SUBVALUE type="meaning">brown,
		// brown-haired</SUBVALUE></VALUE>: the value text must be just
		// "Donn", not "Donnbrown, brown-haired" — the DOM's
		// getTextContent concatenates descendant text, but the data
		// model stores the value separately from its subvalues.
		NameList list = generator.getData().lists().get("gaelic-male-descriptive-byname");
		assertNotNull(list, "expected to find list 'gaelic-male-descriptive-byname'");
		Optional<WeightedDataValue> donn = list.values().stream()
				.filter(v -> "Donn".equals(v.getValue()))
				.findFirst();
		assertTrue(donn.isPresent(), "expected to find 'Donn' value in list");
		assertEquals("brown, brown-haired", donn.get().getSubValue("meaning"));
	}
}
