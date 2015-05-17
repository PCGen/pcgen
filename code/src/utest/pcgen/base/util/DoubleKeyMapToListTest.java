/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import pcgen.testsupport.NoPublicZeroArgConstructorMap;
import pcgen.testsupport.NoZeroArgConstructorMap;
import pcgen.testsupport.StrangeMap;

public class DoubleKeyMapToListTest extends TestCase
{

	private static final char CONST_E = 'E';
	private static final char CONST_C = 'C';
	private static final char CONST_G = 'G';
	private static final char CONST_F = 'F';
	private static final char CONST_D = 'D';
	private static final char CONST_B = 'B';
	private static final char CONST_A = 'A';
	DoubleKeyMapToList<Integer, Double, Character> dkm;

	@Override
	@Before
	public void setUp()
	{
		dkm = new DoubleKeyMapToList<Integer, Double, Character>();
	}

	public void populate()
	{
		dkm.addToListFor(Integer.valueOf(1), Double.valueOf(1), CONST_A);
		dkm.addToListFor(Integer.valueOf(1), Double.valueOf(1), CONST_B);
		dkm.addToListFor(Integer.valueOf(1), Double.valueOf(2), CONST_C);
		dkm.addToListFor(Integer.valueOf(2), Double.valueOf(1), CONST_D);
		dkm.addToListFor(Integer.valueOf(2), Double.valueOf(2), CONST_E);
		dkm.addToListFor(Integer.valueOf(2), Double.valueOf(2), null);
		dkm.addToListFor(null, Double.valueOf(3), CONST_F);
		dkm.addToListFor(Integer.valueOf(3), null, CONST_G);
		dkm.addToListFor(Integer.valueOf(5), Double.valueOf(6), null);
	}

	public void testNullInConstructor()
	{
		try
		{
			new DoubleKeyMapToList(null, HashMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK, expected
		}
		try
		{
			new DoubleKeyMapToList(HashMap.class, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK, expected
		}
	}

	public void testBadClassInConstructor()
	{
		try
		{
			new DoubleKeyMapToList(StrangeMap.class, HashMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//OK, expected
		}
		try
		{
			new DoubleKeyMapToList(HashMap.class, StrangeMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//OK, expected
		}
	}


	public void testBadClassInConstructor2()
	{
		try
		{
			new DoubleKeyMapToList(NoPublicZeroArgConstructorMap.class, HashMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//OK, expected
		}
		try
		{
			new DoubleKeyMapToList(HashMap.class, NoPublicZeroArgConstructorMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//OK, expected
		}
	}


	public void testBadClassInConstructor3()
	{
		try
		{
			new DoubleKeyMapToList(NoZeroArgConstructorMap.class, HashMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//OK, expected
		}
		try
		{
			new DoubleKeyMapToList(HashMap.class, NoZeroArgConstructorMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//OK, expected
		}
	}

	@Test
	public void testPutGet()
	{
		assertNull(dkm.getListFor(Integer.valueOf(1), Double.valueOf(0)));
		populate();
		List<Character> l =
				dkm.getListFor(Integer.valueOf(1), Double.valueOf(1));
		assertEquals(2, l.size());
		assertTrue(l.contains(CONST_A));
		assertTrue(l.contains(CONST_B));
		assertTrue(dkm.containsListFor(Integer.valueOf(1)));
		l = dkm.getListFor(Integer.valueOf(1), Double.valueOf(2));
		assertEquals(1, l.size());
		assertTrue(l.contains(CONST_C));
		dkm.addToListFor(Integer.valueOf(1), Double.valueOf(2), CONST_C);
		l = dkm.getListFor(Integer.valueOf(1), Double.valueOf(2));
		assertEquals(2, l.size());
		assertTrue(l.contains(CONST_C));
		// two of them
		l.remove(Character.valueOf(CONST_C));
		assertTrue(l.contains(CONST_C));
		l = dkm.getListFor(Integer.valueOf(2), Double.valueOf(2));
		assertEquals(2, l.size());
		assertTrue(l.contains(CONST_E));
		assertTrue(l.contains(null));
		l.remove(Character.valueOf(CONST_E));
		List<Character> l2 =
				dkm.getListFor(Integer.valueOf(2), Double.valueOf(2));
		assertEquals(2, l2.size());
		assertTrue(l2.contains(CONST_E));
		assertTrue(l2.contains(null));
		assertEquals(1, l.size());
		assertTrue(l.contains(null));
		dkm.addToListFor(Integer.valueOf(2), Double.valueOf(2), null);
		l = dkm.getListFor(Integer.valueOf(2), Double.valueOf(2));
		assertEquals(3, l.size());
		assertTrue(l.contains(CONST_E));
		assertTrue(l.contains(null));
		// Two of them.
		l.remove(null);
		assertTrue(l.contains(null));
		assertNull(dkm.getListFor(Integer.valueOf(1), Double.valueOf(0)));
		assertNull(dkm.getListFor(Integer.valueOf(2), Double.valueOf(3)));
		assertNull(dkm.getListFor(Integer.valueOf(4), Double.valueOf(0)));
		assertNull(dkm.getListFor(Integer.valueOf(1), null));
		assertNull(dkm.getListFor(null, Double.valueOf(1)));
		dkm.clear();
		assertEquals(2, l2.size());
		assertTrue(l2.contains(CONST_E));
		assertTrue(l2.contains(null));
	}

	@Test
	public void testContainsKey()
	{
		populate();
		assertTrue(dkm.containsListFor(Integer.valueOf(1), Double.valueOf(1)));
		assertTrue(dkm.containsListFor(Integer.valueOf(1), Double.valueOf(2)));
		assertTrue(dkm.containsListFor(Integer.valueOf(2), Double.valueOf(1)));
		assertTrue(dkm.containsListFor(Integer.valueOf(2), Double.valueOf(2)));
		assertFalse(dkm.containsListFor(Integer.valueOf(2), Double.valueOf(3)));
		assertFalse(dkm.containsListFor(Integer.valueOf(-4), Double.valueOf(0)));
		assertFalse(dkm.containsListFor(Integer.valueOf(1), null));
		assertFalse(dkm.containsListFor(null, Double.valueOf(1)));
		assertTrue(dkm.containsListFor(null, Double.valueOf(3)));
		assertTrue(dkm.containsListFor(Integer.valueOf(3), null));
	}

	@Test
	public void testRemoveListFor()
	{
		assertNull(dkm.removeListFor(Integer.valueOf(1), Double.valueOf(1)));
		populate();
		assertTrue(dkm.containsListFor(Integer.valueOf(1)));
		List<Character> l =
				dkm.removeListFor(Integer.valueOf(1), Double.valueOf(1));
		assertTrue(dkm.containsListFor(Integer.valueOf(1)));
		assertEquals(2, l.size());
		assertTrue(l.contains(CONST_A));
		assertTrue(l.contains(CONST_B));
		assertFalse(dkm.containsListFor(Integer.valueOf(1), Double.valueOf(1)));
		assertNull(dkm.getListFor(Integer.valueOf(1), Double.valueOf(1)));
		l = dkm.removeListFor(Integer.valueOf(1), Double.valueOf(2));
		assertFalse(dkm.containsListFor(Integer.valueOf(1)));
		assertEquals(1, l.size());
		assertTrue(l.contains(CONST_C));
		dkm.addToListFor(Integer.valueOf(1), Double.valueOf(2), CONST_C);
		l = dkm.removeListFor(Integer.valueOf(1), Double.valueOf(2));
		assertEquals(1, l.size());
		assertTrue(l.contains(CONST_C));
		l = dkm.removeListFor(Integer.valueOf(2), Double.valueOf(2));
		assertEquals(2, l.size());
		assertTrue(l.contains(CONST_E));
		assertTrue(l.contains(null));
		assertNull(dkm.removeListFor(Integer.valueOf(2), Double.valueOf(2)));
		assertNull(dkm.getListFor(Integer.valueOf(1), Double.valueOf(0)));
		assertNull(dkm.getListFor(Integer.valueOf(2), Double.valueOf(3)));
		assertNull(dkm.getListFor(Integer.valueOf(4), Double.valueOf(0)));
		assertNull(dkm.getListFor(Integer.valueOf(1), null));
		assertNull(dkm.getListFor(null, Double.valueOf(1)));
	}

	@Test
	public void testRemoveListsFor()
	{
		assertNull(dkm.removeListsFor(Integer.valueOf(1)));
		populate();
		assertTrue(dkm.containsListFor(Integer.valueOf(1)));
		MapToList<Double, Character> mtl =
				dkm.removeListsFor(Integer.valueOf(1));
		assertFalse(dkm.containsListFor(Integer.valueOf(1)));
		assertFalse(dkm.containsListFor(Integer.valueOf(1), Double.valueOf(1)));
		assertFalse(dkm.containsListFor(Integer.valueOf(1), Double.valueOf(2)));
		Set<Double> keys = mtl.getKeySet();
		assertEquals(2, keys.size());
		assertTrue(keys.contains(Double.valueOf(1)));
		assertTrue(keys.contains(Double.valueOf(2)));
		List<Character> list = mtl.getListFor(Double.valueOf(1));
		assertEquals(2, list.size());
		assertTrue(list.contains(CONST_A));
		assertTrue(list.contains(CONST_B));
		list = mtl.getListFor(Double.valueOf(2));
		assertEquals(1, list.size());
		assertTrue(list.contains(CONST_C));
	}

	@Test
	public void testRemoveFromListFor()
	{
		assertFalse(dkm.removeFromListFor(Integer.valueOf(1),
			Double.valueOf(1), CONST_D));
		populate();
		assertTrue(dkm.removeFromListFor(Integer.valueOf(1), Double.valueOf(1),
			CONST_A));
		assertTrue(dkm.containsListFor(Integer.valueOf(1)));
		assertTrue(dkm.containsListFor(Integer.valueOf(1), Double.valueOf(1)));
		assertEquals(1, dkm
			.sizeOfListFor(Integer.valueOf(1), Double.valueOf(1)));
		assertFalse(dkm.removeFromListFor(Integer.valueOf(1),
			Double.valueOf(1), CONST_A));
		assertTrue(dkm.removeFromListFor(Integer.valueOf(1), Double.valueOf(1),
			CONST_B));
		assertEquals(0, dkm
			.sizeOfListFor(Integer.valueOf(1), Double.valueOf(1)));
		assertFalse(dkm.containsListFor(Integer.valueOf(1), Double.valueOf(1)));
		assertTrue(dkm.containsListFor(Integer.valueOf(1)));

		// add a second :)
		dkm.addToListFor(Integer.valueOf(1), Double.valueOf(2), CONST_C);
		assertFalse(dkm.removeFromListFor(Integer.valueOf(1),
			Double.valueOf(2), CONST_A));
		assertTrue(dkm.containsListFor(Integer.valueOf(1), Double.valueOf(2)));
		assertEquals(2, dkm
			.sizeOfListFor(Integer.valueOf(1), Double.valueOf(2)));
		assertFalse(dkm.removeFromListFor(Integer.valueOf(1),
			Double.valueOf(2), CONST_A));
		assertTrue(dkm.removeFromListFor(Integer.valueOf(1), Double.valueOf(2),
			CONST_C));
		assertEquals(1, dkm
			.sizeOfListFor(Integer.valueOf(1), Double.valueOf(2)));
		assertTrue(dkm.containsListFor(Integer.valueOf(1), Double.valueOf(2)));
		assertTrue(dkm.containsListFor(Integer.valueOf(1)));
		assertTrue(dkm.removeFromListFor(Integer.valueOf(1), Double.valueOf(2),
			CONST_C));
		assertEquals(0, dkm
			.sizeOfListFor(Integer.valueOf(1), Double.valueOf(2)));
		assertFalse(dkm.containsListFor(Integer.valueOf(1), Double.valueOf(2)));
		assertFalse(dkm.containsListFor(Integer.valueOf(1)));

		// Test null stuff :)
		assertFalse(dkm.removeFromListFor(null, Double.valueOf(3), CONST_A));
		assertTrue(dkm.containsListFor(null, Double.valueOf(3)));
		assertEquals(1, dkm.sizeOfListFor(null, Double.valueOf(3)));
		assertFalse(dkm.removeFromListFor(null, Double.valueOf(3), CONST_A));
		assertTrue(dkm.removeFromListFor(null, Double.valueOf(3), CONST_F));
		assertEquals(0, dkm.sizeOfListFor(null, Double.valueOf(3)));
		assertFalse(dkm.containsListFor(null, Double.valueOf(3)));

		assertFalse(dkm.removeFromListFor(Integer.valueOf(3), null, CONST_A));
		assertTrue(dkm.containsListFor(Integer.valueOf(3), null));
		assertEquals(1, dkm.sizeOfListFor(Integer.valueOf(3), null));
		assertFalse(dkm.removeFromListFor(Integer.valueOf(3), null, CONST_A));
		assertTrue(dkm.removeFromListFor(Integer.valueOf(3), null, CONST_G));
		assertEquals(0, dkm.sizeOfListFor(Integer.valueOf(3), null));
		assertFalse(dkm.containsListFor(Integer.valueOf(3), null));
	}

	@Test
	public void testContainsInList()
	{
		assertFalse(dkm.containsInList(Integer.valueOf(1), Double.valueOf(1),
			CONST_D));
		populate();
		assertFalse(dkm.containsInList(Integer.valueOf(1), Double.valueOf(16),
			CONST_D));
		assertTrue(dkm.containsInList(Integer.valueOf(1), Double.valueOf(1),
			CONST_A));
		assertTrue(dkm.containsInList(Integer.valueOf(1), Double.valueOf(1),
			CONST_B));

		// add a second :)
		dkm.addToListFor(Integer.valueOf(1), Double.valueOf(2), CONST_C);
		assertFalse(dkm.containsInList(Integer.valueOf(1), Double.valueOf(2),
			CONST_A));
		assertTrue(dkm.containsInList(Integer.valueOf(1), Double.valueOf(2),
			CONST_C));

		// Test null stuff :)
		assertTrue(dkm.containsInList(Integer.valueOf(2), Double.valueOf(2),
			null));

		assertFalse(dkm.containsInList(null, Double.valueOf(3), CONST_A));
		assertTrue(dkm.containsInList(null, Double.valueOf(3), CONST_F));

		assertFalse(dkm.containsInList(Integer.valueOf(3), null, CONST_A));
		assertTrue(dkm.containsInList(Integer.valueOf(3), null, CONST_G));
	}

	@Test
	public void testGetKeySet()
	{
		Set<Integer> s = dkm.getKeySet();
		assertEquals(0, s.size());
		s.add(Integer.valueOf(-5));
		// Ensure not saved in DoubleKeyMap
		Set<Integer> s2 = dkm.getKeySet();
		assertEquals(0, s2.size());
		assertEquals(1, s.size());
		// And ensure references are not kept the other direction to be altered
		// by changes in the underlying DoubleKeyMap
		populate();
		assertEquals(1, s.size());
		assertEquals(0, s2.size());
		Set<Integer> s3 = dkm.getKeySet();
		assertEquals(5, s3.size());
		assertTrue(s3.contains(Integer.valueOf(1)));
		assertTrue(s3.contains(Integer.valueOf(2)));
		assertTrue(s3.contains(Integer.valueOf(3)));
		assertTrue(s3.contains(Integer.valueOf(5)));
		assertTrue(s3.contains(null));
	}

	@Test
	public void testGetSecondaryKeySet()
	{
		Set<Double> s = dkm.getSecondaryKeySet(Integer.valueOf(4));
		assertEquals(0, s.size());
		int sSize = 1;
		try
		{
			s.add(Double.valueOf(-5));
		}
		catch (UnsupportedOperationException uoe)
		{
			// This is OK, just account for it
			sSize = 0;
		}
		// Ensure not saved in DoubleKeyMap
		Set<Double> s2 = dkm.getSecondaryKeySet(Integer.valueOf(4));
		assertEquals(0, s2.size());
		assertEquals(sSize, s.size());
		// And ensure references are not kept the other direction to be altered
		// by changes in the underlying DoubleKeyMap
		populate();
		assertEquals(sSize, s.size());
		assertEquals(0, s2.size());
		Set<Double> s3 = dkm.getSecondaryKeySet(Integer.valueOf(1));
		assertEquals(2, s3.size());
		assertTrue(s3.contains(Double.valueOf(1)));
		assertTrue(s3.contains(Double.valueOf(2)));
		Set<Double> s4 = dkm.getSecondaryKeySet(Integer.valueOf(3));
		assertEquals(1, s4.size());
		assertTrue(s4.contains(null));
		Set<Double> s5 = dkm.getSecondaryKeySet(null);
		assertEquals(1, s5.size());
		assertTrue(s5.contains(Double.valueOf(3)));
	}

	@Test
	public void testClearIsEmpty()
	{
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.firstKeyCount());
		populate();
		assertFalse(dkm.isEmpty());
		assertEquals(5, dkm.firstKeyCount());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.firstKeyCount());
		dkm.addToListFor(null, Double.valueOf(3), 'F');
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.firstKeyCount());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.firstKeyCount());
		dkm.addToListFor(Integer.valueOf(3), null, 'G');
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.firstKeyCount());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.firstKeyCount());
		dkm.addToListFor(Integer.valueOf(5), Double.valueOf(6), null);
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.firstKeyCount());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.firstKeyCount());
	}

	@Test
	public void testInstanceBehavior()
	{
		Character ca = Character.valueOf('a');
		Character cb = Character.valueOf('b');
		Character cc = Character.valueOf('c');
		Character ca1 = new Character('a');
		Integer i1 = Integer.valueOf(1);
		Double d1 = Double.valueOf(1);
		dkm.addToListFor(i1, d1, ca);
		dkm.addToListFor(i1, d1, cb);
		Double d2 = Double.valueOf(2);
		dkm.addToListFor(i1, d2, cc);
		Integer i2 = Integer.valueOf(2);
		dkm.addToListFor(i2, d1, ca);
		dkm.addToListFor(i2, d1, ca);
		Integer i3 = Integer.valueOf(3);
		dkm.addToListFor(i3, d2, cb);
		dkm.addToListFor(i3, d2, cc);
		assertTrue(dkm.containsInList(i1, d1, ca));
		assertTrue(dkm.containsInList(i1, d1, ca1));
		assertTrue(dkm.removeFromListFor(i1, d1, ca1));
		assertFalse(dkm.containsInList(i1, d1, ca));

		assertTrue(dkm.containsInList(i2, d1, ca));
		assertTrue(dkm.containsInList(i2, d1, ca1));
		assertTrue(dkm.removeFromListFor(i2, d1, ca1));
		assertTrue(dkm.containsInList(i2, d1, ca));
		assertTrue(dkm.removeFromListFor(i2, d1, ca));
		// There were two
		assertFalse(dkm.containsInList(i2, d1, ca));
	}

	@Test
	public void testAddAllToListFor()
	{
		Integer i1 = Integer.valueOf(1);
		Double d1 = Double.valueOf(1);
		List<Character> l = new ArrayList<Character>();
		l.add(CONST_A);
		l.add(null);
		l.add(CONST_A);
		l.add(CONST_B);
		dkm.addAllToListFor(i1, d1, l);
		assertTrue(dkm.containsListFor(i1));
		assertTrue(dkm.containsListFor(i1, d1));
		assertEquals(4, dkm.sizeOfListFor(i1, d1));
		dkm.addToListFor(i1, d1, CONST_D);
		assertEquals(4, l.size());
		// Check reference semantics!
		l.add(CONST_C);
		l.add(CONST_E);
		assertTrue(dkm.containsListFor(i1, d1));
		assertEquals(5, dkm.sizeOfListFor(i1, d1));
		l.clear();
		assertTrue(dkm.containsListFor(i1, d1));
		assertEquals(5, dkm.sizeOfListFor(i1, d1));
	}

	@Test
	public void testAddAll()
	{
		DoubleKeyMapToList<Integer, Double, Character> copy =
			new DoubleKeyMapToList<Integer, Double, Character>();
		try
		{
			copy.addAll(null);
		}
		catch (NullPointerException e)
		{
			// OK
		}
		populate();
		copy.addAll(dkm);
		Integer i1 = Integer.valueOf(1);
		Double d1 = Double.valueOf(1);
		// test independence
		dkm.addToListFor(i1, d1, CONST_D);
		assertEquals(2, copy.sizeOfListFor(i1, d1));
		List<Character> l = copy.getListFor(i1, d1);
		assertEquals(2, l.size());
		assertTrue(l.contains(CONST_A));
		assertTrue(l.contains(CONST_B));
		copy.addToListFor(i1, d1, CONST_E);
		l = dkm.getListFor(i1, d1);
		assertEquals(3, l.size());
		assertTrue(l.contains(CONST_A));
		assertTrue(l.contains(CONST_B));
		assertTrue(l.contains(CONST_D));
	}
}
