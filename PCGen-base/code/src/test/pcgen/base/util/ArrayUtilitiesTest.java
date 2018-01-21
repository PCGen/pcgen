/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.util;

import java.util.Arrays;

import org.junit.Test;

import junit.framework.TestCase;
import pcgen.testsupport.TestSupport;

public class ArrayUtilitiesTest extends TestCase
{

	@Test
	public void testConstructor()
	{
		TestSupport.invokePrivateConstructor(ArrayUtilities.class);
	}

	@Test
	public void testMerge()
	{
		Class<Integer> arrayClass = Integer.class;
		Integer[] first = null;
		Integer[] second = null;
		assertNull(ArrayUtilities.mergeArray(arrayClass, first, second));
		first = new Integer[]{};
		assertTrue(ArrayUtilities.mergeArray(arrayClass, first, second).length == 0);
		first = new Integer[]{3, 4};
		assertTrue(ArrayUtilities.mergeArray(arrayClass, first, second) == first);
		second = new Integer[]{};
		assertTrue(ArrayUtilities.mergeArray(arrayClass, first, second) == first);
		second = new Integer[]{5, 6};
		assertTrue(Arrays.deepEquals(ArrayUtilities.mergeArray(arrayClass, first, second),
			new Integer[]{3, 4, 5, 6}));
		first = new Integer[]{};
		assertTrue(ArrayUtilities.mergeArray(arrayClass, first, second) == second);
		first = null;
		assertTrue(ArrayUtilities.mergeArray(arrayClass, first, second) == second);
	}

}
