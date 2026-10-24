/*
 * Copyright 2026 (C) Vest <Vest@users.noreply.github.com>
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
package pcgen.gui3.sources;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for the Advanced tab's search predicate
 * ({@link SourceSelectionModel#matches}): case-insensitive match
 * over a campaign's display name, book type, and short source abbreviation,
 * with a blank query matching everything (Swing SearchFilterPanel parity).
 */
class AdvancedSourceSearchTest
{
	private static Campaign campaign(String name, String bookType, String sourceShort)
	{
		var campaign = new Campaign();
		campaign.setName(name);
		if (bookType != null)
		{
			campaign.addToListFor(ListKey.BOOK_TYPE, bookType);
		}
		if (sourceShort != null)
		{
			campaign.put(StringKey.SOURCE_SHORT, sourceShort);
		}
		return campaign;
	}

	@ParameterizedTest(name = "matches on \"{0}\"")
	@ValueSource(strings = {
			"judg",   // display name
			"JUDG",   // case-insensitive
			"supp",   // book type
			"ij"      // short source abbreviation
	})
	void matches_should_returnTrue_when_queryHitsAnyField(String query)
	{
		var c = campaign("Inquisitors' Judgments", "Supplement", "IJ");
		assertTrue(SourceSelectionModel.matches(c, query));
	}

	@Test
	void matches_should_returnFalse_when_queryMatchesNoField()
	{
		var c = campaign("Inquisitors' Judgments", "Supplement", "IJ");
		assertFalse(SourceSelectionModel.matches(c, "dragon"));
	}

	@Test
	void matches_should_notThrow_when_fieldsAbsent()
	{
		var c = campaign("Core Rules", null, null);
		assertTrue(SourceSelectionModel.matches(c, "core"));
		assertFalse(SourceSelectionModel.matches(c, "supplement"));
	}
}
