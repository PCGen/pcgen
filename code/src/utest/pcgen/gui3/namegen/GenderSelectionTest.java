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
package pcgen.gui3.namegen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class GenderSelectionTest
{
	@Test
	void emptyAvailableYieldsEmptyString()
	{
		assertEquals("", GenderSelection.chooseSticky(List.of(), "Female", "Male"));
	}

	@Test
	void previousWinsWhenStillValid()
	{
		assertEquals("Male",
				GenderSelection.chooseSticky(List.of("Female", "Male"), "Male", "Female"));
	}

	@Test
	void fallsBackToPreferredWhenPreviousMissing()
	{
		assertEquals("Female",
				GenderSelection.chooseSticky(List.of("Female", "Other"), "Male", "Female"));
	}

	@Test
	void fallsBackToPreferredWhenPreviousNull()
	{
		assertEquals("Other",
				GenderSelection.chooseSticky(List.of("Other"), null, "Other"));
	}

	@Test
	void fallsBackToFirstWhenNeitherValid()
	{
		assertEquals("Female",
				GenderSelection.chooseSticky(List.of("Female", "Other"), "Male", "Male"));
	}

	@Test
	void fallsBackToFirstWhenPreferredNull()
	{
		assertEquals("Female",
				GenderSelection.chooseSticky(List.of("Female", "Male"), null, null));
	}
}
