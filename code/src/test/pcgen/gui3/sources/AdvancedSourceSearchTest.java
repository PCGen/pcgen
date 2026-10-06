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

/**
 * Unit tests for the Advanced tab's search predicate
 * ({@link AdvancedSourceSelectionController#matches}): case-insensitive match
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

	@Test
	void matches_should_matchByName_when_queryInDisplayName()
	{
		var c = campaign("Inquisitors' Judgments", "Supplement", "IJ");
		assertTrue(AdvancedSourceSelectionController.matches(c, "judg"));
	}

	@Test
	void matches_should_beCaseInsensitive_when_queryDiffersInCase()
	{
		var c = campaign("Inquisitors' Judgments", "Supplement", "IJ");
		assertTrue(AdvancedSourceSelectionController.matches(c, "JUDG"));
	}

	@Test
	void matches_should_matchByBookType_when_queryInBookType()
	{
		var c = campaign("Inquisitors' Judgments", "Supplement", "IJ");
		assertTrue(AdvancedSourceSelectionController.matches(c, "supp"));
	}

	@Test
	void matches_should_matchBySourceShort_when_queryInAbbreviation()
	{
		var c = campaign("Inquisitors' Judgments", "Supplement", "IJ");
		assertTrue(AdvancedSourceSelectionController.matches(c, "ij"));
	}

	@Test
	void matches_should_returnFalse_when_queryMatchesNoField()
	{
		var c = campaign("Inquisitors' Judgments", "Supplement", "IJ");
		assertFalse(AdvancedSourceSelectionController.matches(c, "dragon"));
	}

	@Test
	void matches_should_matchEverything_when_queryBlank()
	{
		var c = campaign("Inquisitors' Judgments", "Supplement", "IJ");
		assertTrue(AdvancedSourceSelectionController.matches(c, ""));
		assertTrue(AdvancedSourceSelectionController.matches(c, "   "));
		assertTrue(AdvancedSourceSelectionController.matches(c, null));
	}

	@Test
	void matches_should_notThrow_when_fieldsAbsent()
	{
		var c = campaign("Core Rules", null, null);
		assertTrue(AdvancedSourceSelectionController.matches(c, "core"));
		assertFalse(AdvancedSourceSelectionController.matches(c, "supplement"));
	}
}
