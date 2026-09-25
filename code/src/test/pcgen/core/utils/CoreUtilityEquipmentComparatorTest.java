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
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.List;

import pcgen.core.Equipment;

import org.junit.jupiter.api.Test;

/**
 * Tests that {@link CoreUtility#EQUIPMENT_COMPARATOR} orders equipment output
 * names with a {@link java.text.Collator}, so accented / non-English gear names
 * collate correctly rather than after every plain ASCII letter as
 * compareToIgnoreCase did. This comparator drives character-sheet output order.
 */
class CoreUtilityEquipmentComparatorTest
{

	/** French gear names: "Épée" (sword) has an accented capital E (U+00C9), above 'Z' by code point. */
	private static final List<String> UNSORTED = List.of("Zarf", "Épée", "Aegis");
	private static final List<String> COLLATED = List.of("Aegis", "Épée", "Zarf");

	private static Equipment equip(String name)
	{
		Equipment e = new Equipment();
		e.setName(name);
		return e;
	}

	private static List<String> sortedNames(List<String> names)
	{
		List<Equipment> list = new ArrayList<>();
		for (String name : names)
		{
			list.add(equip(name));
		}
		list.sort(CoreUtility.EQUIPMENT_COMPARATOR);
		return list.stream().map(Equipment::getName).toList();
	}

	/**
	 * Reproduces the original defect: plain compareToIgnoreCase sorts the
	 * accented "Épée" AFTER "Zarf", because 'É' has a higher code point than 'Z'.
	 */
	@Test
	void compareToIgnoreCaseMisordersAccentedGearNames()
	{
		List<String> names = new ArrayList<>(UNSORTED);
		names.sort(String::compareToIgnoreCase);

		assertEquals(List.of("Aegis", "Zarf", "Épée"), names,
				"compareToIgnoreCase pushes the accented gear name past 'Z' - the bug being fixed");
		assertNotEquals(COLLATED, names);
	}

	@Test
	void accentedGearNameSortsByCollationNotCodePoint()
	{
		assertEquals(COLLATED, sortedNames(UNSORTED));
	}

	@Test
	void plainAsciiGearNamesStillSortAscending()
	{
		assertEquals(List.of("Axe", "Bow", "Club"), sortedNames(List.of("Club", "Axe", "Bow")));
	}
}
