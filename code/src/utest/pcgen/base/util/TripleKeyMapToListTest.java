/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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

public class TripleKeyMapToListTest extends TestCase
{

	private static final Double D0 = Double.valueOf(0);
	private static final Double D1 = Double.valueOf(1);
	private static final Double D2 = Double.valueOf(2);
	private static final Integer I1 = Integer.valueOf(1);
	private static final Integer I2 = Integer.valueOf(2);
	private static final Long L1 = Long.valueOf(1);
	private static final Long L2 = Long.valueOf(2);
	private static final char CONST_E = 'E';
	private static final char CONST_C = 'C';
	private static final char CONST_G = 'G';
	private static final char CONST_H = 'H';
	private static final char CONST_F = 'F';
	private static final char CONST_D = 'D';
	private static final char CONST_B = 'B';
	private static final char CONST_A = 'A';
	TripleKeyMapToList<Integer, Double, Long, Character> dkm;

	@Override
	@Before
	public void setUp()
	{
		dkm = new TripleKeyMapToList<Integer, Double, Long, Character>();
	}

	public void populate()
	{
		dkm.addToListFor(I1, D1, L1, CONST_A);
		dkm.addToListFor(I1, D1, L1, CONST_B);
		dkm.addToListFor(I1, D2, L2, CONST_C);
		dkm.addToListFor(I2, D1, L2, CONST_D);
		dkm.addToListFor(I2, D2, L1, CONST_E);
		dkm.addToListFor(I2, D2, L1, null);
		dkm.addToListFor(null, Double.valueOf(3), L2, CONST_F);
		dkm.addToListFor(Integer.valueOf(3), null, L1, CONST_G);
		dkm.addToListFor(Integer.valueOf(4), Double.valueOf(4), null, CONST_H);
		dkm.addToListFor(Integer.valueOf(5), Double.valueOf(6), L1, null);
	}

	@Test
	public void testPutGet()
	{
		assertNull(dkm.getListFor(I1, D0, L1));
		populate();
		List<Character> l = dkm.getListFor(I1, D1, L1);
		assertEquals(2, l.size());
		assertTrue(l.contains(CONST_A));
		assertTrue(l.contains(CONST_B));
		assertTrue(dkm.containsListFor(I1, D2, L2));
		l = dkm.getListFor(I1, D2, L2);
		assertEquals(1, l.size());
		assertTrue(l.contains(CONST_C));
		dkm.addToListFor(I1, D2, L2, CONST_C);
		l = dkm.getListFor(I1, D2, L2);
		assertEquals(2, l.size());
		assertTrue(l.contains(CONST_C));
		// two of them
		l.remove(Character.valueOf(CONST_C));
		assertTrue(l.contains(CONST_C));
		l = dkm.getListFor(I2, D2, L1);
		assertEquals(2, l.size());
		assertTrue(l.contains(CONST_E));
		assertTrue(l.contains(null));
		l.remove(Character.valueOf(CONST_E));
		List<Character> l2 = dkm.getListFor(I2, D2, L1);
		assertEquals(2, l2.size());
		assertTrue(l2.contains(CONST_E));
		assertTrue(l2.contains(null));
		assertEquals(1, l.size());
		assertTrue(l.contains(null));
		dkm.addToListFor(I2, D2, L1, null);
		l = dkm.getListFor(I2, D2, L1);
		assertEquals(3, l.size());
		assertTrue(l.contains(CONST_E));
		assertTrue(l.contains(null));
		// Two of them.
		l.remove(null);
		assertTrue(l.contains(null));
		assertNull(dkm.getListFor(I1, D0, L1));
		assertNull(dkm.getListFor(I2, Double.valueOf(3), L2));
		assertNull(dkm.getListFor(Integer.valueOf(4), D0, L1));
		assertNull(dkm.getListFor(I1, null, L2));
		assertNull(dkm.getListFor(null, D1, L1));
		assertNull(dkm.getListFor(I1, D1, null));
		dkm.clear();
		assertEquals(2, l2.size());
		assertTrue(l2.contains(CONST_E));
		assertTrue(l2.contains(null));
	}

	@Test
	public void testContainsKey()
	{
		populate();
		assertTrue(dkm.containsListFor(I1, D1, L1));
		assertTrue(dkm.containsListFor(I1, D2, L2));
		assertTrue(dkm.containsListFor(I2, D1, L2));
		assertTrue(dkm.containsListFor(I2, D2, L1));
		assertFalse(dkm.containsListFor(I2, Double.valueOf(3), L1));
		assertFalse(dkm.containsListFor(Integer.valueOf(-4), D0, L1));
		assertFalse(dkm.containsListFor(I1, null, L1));
		assertFalse(dkm.containsListFor(null, D1, L1));
		assertTrue(dkm.containsListFor(null, Double.valueOf(3), L2));
		assertTrue(dkm.containsListFor(Integer.valueOf(3), null, L1));
		assertTrue(dkm.containsListFor(Integer.valueOf(4), Double.valueOf(4), null));
	}

	@Test
	public void testRemoveListFor()
	{
		assertNull(dkm.removeListFor(I1, D1, L1));
		populate();
		assertTrue(dkm.containsListFor(I1, D1, L1));
		List<Character> l = dkm.removeListFor(I1, D1, L1);
		assertFalse(dkm.containsListFor(I1, D1, L1));
		assertEquals(2, l.size());
		assertTrue(l.contains(CONST_A));
		assertTrue(l.contains(CONST_B));
		assertFalse(dkm.containsListFor(I1, D1, L1));
		assertNull(dkm.getListFor(I1, D1, L1));
		l = dkm.removeListFor(I1, D2, L2);
		assertFalse(dkm.containsListFor(I1, D1, L1));
		assertEquals(1, l.size());
		assertTrue(l.contains(CONST_C));
		dkm.addToListFor(I1, D2, L2, CONST_C);
		l = dkm.removeListFor(I1, D2, L2);
		assertEquals(1, l.size());
		assertTrue(l.contains(CONST_C));
		l = dkm.removeListFor(I2, D2, L1);
		assertEquals(2, l.size());
		assertTrue(l.contains(CONST_E));
		assertTrue(l.contains(null));
		assertNull(dkm.removeListFor(I2, D2, L1));
		assertNull(dkm.getListFor(I1, D0, L1));
		assertNull(dkm.getListFor(I2, Double.valueOf(3), L1));
		assertNull(dkm.getListFor(Integer.valueOf(4), D0, L1));
		assertNull(dkm.getListFor(I1, null, L1));
		assertNull(dkm.getListFor(null, D1, L1));
		assertNull(dkm.getListFor(I1, D1, null));
	}

	@Test
	public void testRemoveListsFor()
	{
		assertNull(dkm.removeListsFor(I1, D1));
		populate();
		dkm.addToListFor(I1, D1, L2, CONST_C);
		assertTrue(dkm.containsListFor(I1, D1, L1));
		MapToList<Long, Character> mtl = dkm.removeListsFor(I1, D1);
		assertFalse(dkm.containsListFor(I1, D1, L1));
		Set<Long> keys = mtl.getKeySet();
		assertEquals(2, keys.size());
		assertTrue(keys.contains(L1));
		assertTrue(keys.contains(L2));
		List<Character> list = mtl.getListFor(L1);
		assertEquals(2, list.size());
		assertTrue(list.contains(CONST_A));
		assertTrue(list.contains(CONST_B));
		list = mtl.getListFor(L2);
		assertEquals(1, list.size());
		assertTrue(list.contains(CONST_C));
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
		assertEquals(6, s3.size());
		assertTrue(s3.contains(I1));
		assertTrue(s3.contains(I2));
		assertTrue(s3.contains(Integer.valueOf(3)));
		assertTrue(s3.contains(Integer.valueOf(4)));
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
		Set<Double> s3 = dkm.getSecondaryKeySet(I1);
		assertEquals(2, s3.size());
		assertTrue(s3.contains(D1));
		assertTrue(s3.contains(D2));
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
		assertEquals(6, dkm.firstKeyCount());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.firstKeyCount());
		dkm.addToListFor(null, Double.valueOf(3), L1, 'F');
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.firstKeyCount());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.firstKeyCount());
		dkm.addToListFor(Integer.valueOf(3), null, L2, 'G');
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.firstKeyCount());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.firstKeyCount());
		dkm.addToListFor(Integer.valueOf(5), Double.valueOf(6), L1, null);
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.firstKeyCount());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.firstKeyCount());
		dkm.addToListFor(Integer.valueOf(5), Double.valueOf(6), null, 'F');
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.firstKeyCount());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.firstKeyCount());
	}

	@Test
	public void testAddAllToListFor()
	{
		Integer i1 = I1;
		Double d1 = D1;
		List<Character> l = new ArrayList<Character>();
		l.add(CONST_A);
		l.add(null);
		l.add(CONST_A);
		l.add(CONST_B);
		dkm.addAllToListFor(i1, d1, L1, l);
		assertTrue(dkm.containsListFor(i1, d1, L1));
		assertEquals(4, dkm.getListFor(i1, d1, L1).size());
		dkm.addToListFor(i1, d1, L1, CONST_D);
		assertEquals(4, l.size());
		// Check reference semantics!
		l.add(CONST_C);
		l.add(CONST_E);
		assertTrue(dkm.containsListFor(i1, d1, L1));
		assertEquals(5, dkm.getListFor(i1, d1, L1).size());
		l.clear();
		assertTrue(dkm.containsListFor(i1, d1, L1));
		assertEquals(5, dkm.getListFor(i1, d1, L1).size());
	}

	public void testNullInConstructor()
	{
		try
		{
			new TripleKeyMapToList(null, HashMap.class, HashMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK, expected
		}
		try
		{
			new TripleKeyMapToList(HashMap.class, null, HashMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK, expected
		}
		try
		{
			new TripleKeyMapToList(HashMap.class, HashMap.class, null);
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
			new TripleKeyMapToList(StrangeMap.class, HashMap.class,
				HashMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK, expected
		}
		try
		{
			new TripleKeyMapToList(HashMap.class, StrangeMap.class,
				HashMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK, expected
		}
		try
		{
			new TripleKeyMapToList(HashMap.class, HashMap.class,
				StrangeMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK, expected
		}
	}

	public void testBadClassInConstructor2()
	{
		try
		{
			new TripleKeyMapToList(NoPublicZeroArgConstructorMap.class,
				HashMap.class, HashMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK, expected
		}
		try
		{
			new TripleKeyMapToList(HashMap.class,
				NoPublicZeroArgConstructorMap.class, HashMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK, expected
		}
		try
		{
			new TripleKeyMapToList(HashMap.class, HashMap.class,
				NoPublicZeroArgConstructorMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK, expected
		}
	}

	public void testBadClassInConstructor3()
	{
		try
		{
			new TripleKeyMapToList(NoZeroArgConstructorMap.class,
				HashMap.class, HashMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK, expected
		}
		try
		{
			new TripleKeyMapToList(HashMap.class,
				NoZeroArgConstructorMap.class, HashMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK, expected
		}
		try
		{
			new TripleKeyMapToList(HashMap.class, HashMap.class,
				NoZeroArgConstructorMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK, expected
		}
	}

	@Test
	public void testGetTertiaryKeySet()
	{
		Set<Long> s = dkm.getTertiaryKeySet(Integer.valueOf(4), D2);
		assertEquals(0, s.size());
		int sSize = 1;
		try
		{
			s.add(Long.valueOf(-5));
		}
		catch (UnsupportedOperationException uoe)
		{
			// This is OK, just account for it
			sSize = 0;
		}
		// Ensure not saved in DoubleKeyMap
		Set<Long> s2 = dkm.getTertiaryKeySet(Integer.valueOf(4), D2);
		assertEquals(0, s2.size());
		assertEquals(sSize, s.size());
		// And ensure references are not kept the other direction to be altered
		// by changes in the underlying DoubleKeyMap
		populate();
		assertEquals(sSize, s.size());
		assertEquals(0, s2.size());
		Set<Long> s3 = dkm.getTertiaryKeySet(I1, D1);
		assertEquals(1, s3.size());
		assertTrue(s3.contains(L1));
		Set<Long> s4 = dkm.getTertiaryKeySet(Integer.valueOf(3), null);
		assertEquals(1, s4.size());
		assertTrue(s4.contains(L1));
		Set<Long> s5 = dkm.getTertiaryKeySet(null, Double.valueOf(3));
		assertEquals(1, s5.size());
		assertTrue(s5.contains(L2));
		Set<Long> s6 = dkm.getTertiaryKeySet(Integer.valueOf(4), Double.valueOf(4));
		assertEquals(1, s6.size());
		assertTrue(s6.contains(null));
	}


}
