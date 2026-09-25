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
package pcgen.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests that {@link SkillComparator} sorts skill output names with a
 * {@link java.text.Collator}, so accented/non-English names order correctly
 * rather than after every plain ASCII letter as compareToIgnoreCase did.
 */
class SkillComparatorTest
{

	/**
	 * French skill names: "Épée" (sword) carries an accented capital E (U+00C9),
	 * whose code point is greater than 'Z'. The three names in collation order
	 * are Apple, Épée, Zap.
	 */
	private static final List<String> UNSORTED = List.of("Zap", "Épée", "Apple");
	private static final List<String> COLLATED = List.of("Apple", "Épée", "Zap");

	private static Skill skill(String name)
	{
		Skill skill = new Skill();
		skill.setName(name);
		return skill;
	}

	private static List<String> sortedOutputNames(SkillComparator comparator)
	{
		List<Skill> skills = new ArrayList<>();
		for (String name : UNSORTED)
		{
			skills.add(skill(name));
		}
		skills.sort(comparator);
		return skills.stream().map(Skill::getOutputName).toList();
	}

	/**
	 * Reproduces the original defect: plain String.compareToIgnoreCase (the old
	 * comparator body) sorts the accented "Épée" AFTER "Zap", because 'É' has a
	 * higher code point than 'Z'. This is what a French user saw.
	 */
	@Test
	void compareToIgnoreCaseMisordersAccentedNames()
	{
		List<String> names = new ArrayList<>(UNSORTED);
		names.sort(String::compareToIgnoreCase);

		assertEquals(List.of("Apple", "Zap", "Épée"), names,
				"compareToIgnoreCase pushes the accented name past 'Z' - the bug being fixed");
		assertNotEquals(COLLATED, names, "the buggy order must differ from the desired collated order");
	}

	@Test
	void accentedNameSortsByCollationNotCodePoint()
	{
		assertEquals(COLLATED,
				sortedOutputNames(new SkillComparator(null, SkillComparator.RESORT_NAME, SkillComparator.RESORT_ASCENDING)));
	}

	@Test
	void plainAsciiNamesStillSortAscending()
	{
		List<Skill> skills = new ArrayList<>(List.of(skill("Charlie"), skill("alpha"), skill("Bravo")));

		skills.sort(new SkillComparator(null, SkillComparator.RESORT_NAME, SkillComparator.RESORT_ASCENDING));

		List<String> ordered = skills.stream().map(Skill::getOutputName).toList();
		assertEquals(List.of("alpha", "Bravo", "Charlie"), ordered);
	}

	@Test
	void descendingReversesTheCollatedOrder()
	{
		assertEquals(COLLATED.reversed(),
				sortedOutputNames(new SkillComparator(null, SkillComparator.RESORT_NAME, SkillComparator.RESORT_DESCENDING)));
	}
}
