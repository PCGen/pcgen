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
import java.util.List;
import java.util.function.IntFunction;

import org.junit.Test;

import junit.framework.TestCase;
import pcgen.testsupport.TestSupport;

public class ArrayUtilitiesTest extends TestCase
{

	private final Integer two = 2;
	private final Integer three = 3;
	private final Integer four = 4;
	private final Integer five = 5;
	private final Integer six = 6;

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
		first = new Integer[]{three, four};
		assertTrue(ArrayUtilities.mergeArray(arrayClass, first, second) == first);
		second = new Integer[]{};
		assertTrue(ArrayUtilities.mergeArray(arrayClass, first, second) == first);
		second = new Integer[]{five, six};
		assertTrue(Arrays.deepEquals(ArrayUtilities.mergeArray(arrayClass, first, second),
			new Integer[]{3, 4, 5, 6}));
		first = new Integer[]{};
		assertTrue(ArrayUtilities.mergeArray(arrayClass, first, second) == second);
		first = null;
		assertTrue(ArrayUtilities.mergeArray(arrayClass, first, second) == second);
	}

	@Test
	public void testDifferenceBad()
	{
		Integer[] first = null;
		Integer[] second = null;
		try
		{
			ArrayUtilities.calculateDifference(first, second);
			fail("Expected NPE for null values");
		}
		catch (NullPointerException e)
		{
			//Expected
		}
		first = new Integer[]{};
		try
		{
			ArrayUtilities.calculateDifference(first, second);
			fail("Expected NPE for null values");
		}
		catch (NullPointerException e)
		{
			//Expected
		}
	}

	@Test
	public void testDifferenceEmpty()
	{
		Integer[] first = new Integer[]{};
		Integer[] second = new Integer[]{};
		Tuple<List<Integer>, List<Integer>> tuple =
				ArrayUtilities.calculateDifference(first, second);
		assertEquals(0, tuple.getFirst().size());
		assertEquals(0, tuple.getSecond().size());
	}

	@Test
	public void testDifferenceOnlyRemove()
	{
		Integer[] first = new Integer[]{three, four};
		Integer[] second = new Integer[]{};
		Tuple<List<Integer>, List<Integer>> tuple =
				ArrayUtilities.calculateDifference(first, second);
		List<Integer> removed = tuple.getFirst();
		assertEquals(2, removed.size());
		assertTrue(removed.contains(3));
		assertTrue(removed.contains(4));
		assertEquals(0, tuple.getSecond().size());
	}

	@Test
	public void testDifferenceOnlyAdd()
	{
		Integer[] first = new Integer[]{};
		Integer[] second = new Integer[]{five, six};
		Tuple<List<Integer>, List<Integer>> tuple =
				ArrayUtilities.calculateDifference(first, second);
		List<Integer> added = tuple.getSecond();
		assertEquals(0, tuple.getFirst().size());
		assertEquals(2, added.size());
		assertTrue(added.contains(5));
		assertTrue(added.contains(6));
	}

	@Test
	public void testDifferenceFull()
	{
		/*
		 * Specifically use new Integer for tests - IGNORE ANY CODE VALIDATION TOOLS.
		 * 
		 * This is specifically testing that the two "3" objects are detected as equal
		 */
		Integer[] first = new Integer[]{three, four, six};
		Integer[] second = new Integer[]{7, new Integer(3), 5};
		Tuple<List<Integer>, List<Integer>> tuple =
				ArrayUtilities.calculateDifference(first, second);
		List<Integer> removed = tuple.getFirst();
		List<Integer> added = tuple.getSecond();
		assertEquals(2, removed.size());
		assertEquals(2, added.size());
		assertTrue(added.contains(5));
		assertTrue(added.contains(7));
		assertTrue(removed.contains(4));
		assertTrue(removed.contains(6));
	}

	@Test
	public void testIdentityBad()
	{
		Integer[] first = null;
		Integer[] second = null;
		try
		{
			ArrayUtilities.calculateIdentityDifference(first, second);
			fail("Expected NPE for null values");
		}
		catch (NullPointerException e)
		{
			//Expected
		}
		first = new Integer[]{};
		try
		{
			ArrayUtilities.calculateIdentityDifference(first, second);
			fail("Expected NPE for null values");
		}
		catch (NullPointerException e)
		{
			//Expected
		}
	}

	@Test
	public void testIdentityEmpty()
	{
		Integer[] first = new Integer[]{};
		Integer[] second = new Integer[]{};
		Tuple<List<Integer>, List<Integer>> tuple =
				ArrayUtilities.calculateIdentityDifference(first, second);
		assertEquals(0, tuple.getFirst().size());
		assertEquals(0, tuple.getSecond().size());
	}

	@Test
	public void testIdentityOnlyRemove()
	{
		Integer[] first = new Integer[]{three, four};
		Integer[] second = new Integer[]{};
		Tuple<List<Integer>, List<Integer>> tuple =
				ArrayUtilities.calculateIdentityDifference(first, second);
		List<Integer> removed = new IdentityList<>(tuple.getFirst());
		assertEquals(2, removed.size());
		assertTrue(removed.contains(three));
		assertTrue(removed.contains(four));
		assertEquals(0, tuple.getSecond().size());
	}

	@Test
	public void testIdentityOnlyAdd()
	{
		Integer[] first = new Integer[]{};
		Integer[] second = new Integer[]{five, six};
		Tuple<List<Integer>, List<Integer>> tuple =
				ArrayUtilities.calculateIdentityDifference(first, second);
		List<Integer> added = new IdentityList<>(tuple.getSecond());
		assertEquals(0, tuple.getFirst().size());
		assertEquals(2, added.size());
		assertTrue(added.contains(five));
		assertTrue(added.contains(six));
	}

	@Test
	public void testIdentityFull()
	{
		/*
		 * Specifically use new Integer for tests - IGNORE ANY CODE VALIDATION TOOLS.
		 * 
		 * This is specifically testing that the two "3" objects are detected as equal
		 */
		Integer otherthree = new Integer(3);
		Integer[] first = new Integer[]{three, four, six};
		Integer[] second = new Integer[]{two, otherthree, five};
		Tuple<List<Integer>, List<Integer>> tuple =
				ArrayUtilities.calculateIdentityDifference(first, second);
		List<Integer> removed = new IdentityList<>(tuple.getFirst());
		List<Integer> added = new IdentityList<>(tuple.getSecond());
		assertEquals(3, removed.size());
		assertEquals(3, added.size());
		assertTrue(added.contains(two));
		assertTrue(added.contains(otherthree));
		assertTrue(added.contains(five));
		assertTrue(removed.contains(six));
		assertTrue(removed.contains(four));
		assertTrue(removed.contains(three));
	}
	
	@Test
	public void testUsingArray()
	{
		Integer[] startingArray = new Integer[4];
		IntFunction<Integer[]> function = ArrayUtilities.usingArray(startingArray);
		Integer[] array = function.apply(3);
		assertTrue(array.length >= 3);
		//Identity equality is fine
		assertEquals(startingArray, array);
		array = function.apply(4);
		assertTrue(array.length >= 4);
		//Identity equality is fine
		assertEquals(startingArray, array);
		array = function.apply(5);
		assertTrue(array.length >= 5);
		//Different since length in function was greater than original array.
		assertNotSame(startingArray, array);
		
	}
}
