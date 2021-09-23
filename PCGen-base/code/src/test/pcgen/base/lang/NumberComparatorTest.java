/*
 * Copyright (c) 2018 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.base.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Test the NumberUtilities class
 */
public class NumberComparatorTest
{
	@Test
	public void testCompareFail()
	{
		NumberComparator comparator = new NumberComparator();
		assertThrows(NullPointerException.class, () -> comparator.compare(3, null));
		assertThrows(NullPointerException.class, () -> comparator.compare(null, 2.5));
	}

	@Test
	public void testCompare()
	{
		NumberComparator comparator = new NumberComparator();
		assertEquals(0, comparator.compare(3, 3));
		assertEquals(0, comparator.compare(3, 3.0));
		assertEquals(0, comparator.compare(3.0, 3.0));
		assertEquals(0, comparator.compare(3.0, 3));
		assertEquals(0, comparator.compare(3, 3.0f));
		assertEquals(0, comparator.compare(3.0f, 3.0));
		assertEquals(0, comparator.compare(3.0f, 3));
		assertEquals(1, comparator.compare(3, 2));
		assertEquals(1, comparator.compare(3, 2.5));
		assertEquals(-1, comparator.compare(1.5, 3));
		assertEquals(1, comparator.compare(3, -3));
	}

}
