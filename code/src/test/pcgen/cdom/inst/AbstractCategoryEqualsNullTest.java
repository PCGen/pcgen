/*
 * Copyright 2026 (C) Vest <Vest@users.noreply.github.com>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.inst;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

/**
 * Pins the equals(null) contract for AbstractCategory subclasses
 * after the SpotBugs NP_EQUALS fix (PR #7628).
 */
class AbstractCategoryEqualsNullTest
{

	/** equals(null) returns false instead of throwing NPE. */
	@Test
	void equalsNullReturnsFalseNotNPE()
	{
		DynamicCategory category = new DynamicCategory();
		category.setName("Movement");
		assertFalse(category.equals(null));
	}
}
