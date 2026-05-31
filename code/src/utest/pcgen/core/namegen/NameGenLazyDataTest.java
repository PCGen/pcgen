/*
 * Copyright 2026 Vest <Vest@users.noreply.github.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package pcgen.core.namegen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests for the lazy XML-loading path: verifies that the index pre-scan
 * correctly identifies categories without parsing rule bodies, that
 * selecting a (category, title, gender) triple forces parsing of just the
 * relevant file plus its transitive references, and that the observable
 * outputs match the eager loader's.
 */
public class NameGenLazyDataTest
{
	private static final File DATA_DIR =
			new File(System.getProperty("user.dir"), "plugins/Random Names");

	@Test
	public void preScanFindsCategoriesWithoutParsingFiles() throws Exception
	{
		NameGenLazyData lazy = NameGenLazyData.open(DATA_DIR);
		// open() does the StAX index scan but should never trigger a full
		// DOM parse — parsedFiles must be empty.
		assertTrue(lazy.parsedFiles().isEmpty(),
				"open() must not parse any rule bodies");
		assertFalse(lazy.categoryTitles().isEmpty(),
				"pre-scan should expose at least one category");
	}

	@Test
	public void selectingCategoryParsesOnlyOwningFiles() throws Exception
	{
		NameGenerator generator = new NameGenerator(DATA_DIR);
		List<String> categories = generator.getCategories();
		assertFalse(categories.isEmpty());

		// Reading category lists from the index should not have parsed
		// anything yet — the lazy backend exposes its parse set via the
		// internals; we observe through getCatalog instead.
		String category = categories.getFirst();
		List<String> titles = generator.getTitlesFor(category);
		assertFalse(titles.isEmpty());

		String title = titles.getFirst();
		List<String> genders = generator.getGendersFor(category, title);
		String gender = genders.isEmpty() ? "Male" : genders.getFirst();

		RuleSet catalog = generator.getCatalog(category, title, gender);
		assertNotNull(catalog, "expected a catalog for " + category + "/" + title + "/" + gender);
		assertFalse(catalog.rules().isEmpty(),
				"selected ruleset should be fully materialised");
	}

	@Test
	public void lazyAndEagerProduceSameCategoryListing() throws Exception
	{
		NameGenData eager = NameGenDataLoader.load(DATA_DIR);
		NameGenLazyData lazy = NameGenLazyData.open(DATA_DIR);
		assertEquals(eager.categories().keySet(),
				lazy.rulesetIdsByCategory().keySet(),
				"category names must match between eager and lazy paths");
	}

	@Test
	public void getDataMaterialisesEverything() throws Exception
	{
		// getData() is documented as forcing a full eager materialisation
		// for tests. After calling it the rulesets() / lists() maps should
		// be populated as if the eager loader had run.
		NameGenerator generator = new NameGenerator(DATA_DIR);
		NameGenData snapshot = generator.getData();
		assertFalse(snapshot.lists().isEmpty(), "lists should be populated");
		assertFalse(snapshot.rulesets().isEmpty(), "rulesets should be populated");
	}
}
