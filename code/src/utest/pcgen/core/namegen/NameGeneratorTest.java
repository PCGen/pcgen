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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Behavioural tests for {@link NameGenerator} driven by the bundled
 * {@code plugins/Random Names} dataset.
 */
public class NameGeneratorTest
{
	private static final File DATA_DIR =
			new File(System.getProperty("user.dir"), "plugins/Random Names");

	private static NameGenerator generator;

	@BeforeAll
	public static void loadOnce() throws IOException
	{
		generator = new NameGenerator(DATA_DIR);
	}

	@Test
	public void getCategoriesHidesInternalBuckets()
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
	public void getTitlesForUnknownCategoryReturnsEmpty()
	{
		assertTrue(generator.getTitlesFor("definitely-not-a-category").isEmpty());
	}

	@Test
	public void getTitlesForFantasyElvenIsNonEmptyAndSorted()
	{
		List<String> titles = generator.getTitlesFor("Fantasy: Elven");
		assertFalse(titles.isEmpty(), "Fantasy: Elven should have titles");
		assertEquals(titles.stream().sorted().toList(), titles, "titles must be sorted");
	}

	@Test
	public void getGendersForCanonicalOrder()
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
	public void getCatalogResolvesKnownTriple()
	{
		RuleSet rs = generator.getCatalog("Fantasy: Elven", "Middle Earth Elf", "Male");
		assertNotNull(rs, "Fantasy: Elven / Middle Earth Elf / Male should resolve");
		assertEquals("final", rs.getUsage());
		assertEquals("Middle Earth Elf", rs.getTitle());
	}

	@Test
	public void getCatalogReturnsNullForUnknownTriple()
	{
		assertNull(generator.getCatalog("Fantasy: Elven", "Middle Earth Elf", "Nonexistent"));
		assertNull(generator.getCatalog("Fantasy: Elven", "no-such-title", "Male"));
		assertNull(generator.getCatalog("no-such-category", "Middle Earth Elf", "Male"));
	}

	@Test
	public void generateProducesNonEmptyNameRepeatedly() throws Exception
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
	public void generateWithRuleAlwaysUsesGivenRule() throws Exception
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

}
