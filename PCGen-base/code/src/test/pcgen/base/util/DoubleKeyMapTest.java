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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import pcgen.testsupport.NoPublicZeroArgConstructorMap;
import pcgen.testsupport.NoZeroArgConstructorMap;
import pcgen.testsupport.StrangeMap;

public class DoubleKeyMapTest extends TestCase
{

	private static final char CONST_G = 'G';
	private static final char CONST_F = 'F';
	private static final char CONST_D = 'D';
	private static final char CONST_B = 'B';
	private static final char CONST_A = 'A';
	private DoubleKeyMap<Integer, Double, Character> dkm;

	@Override
	@Before
	public void setUp()
	{
		dkm = new DoubleKeyMap<>();
	}

	public void populate()
	{
		dkm.put(Integer.valueOf(1), Double.valueOf(1), CONST_A);
		dkm.put(Integer.valueOf(1), Double.valueOf(2), CONST_B);
		dkm.put(Integer.valueOf(1), Double.valueOf(3), 'C');
		dkm.put(Integer.valueOf(2), Double.valueOf(1), CONST_D);
		dkm.put(Integer.valueOf(2), Double.valueOf(2), 'E');
		dkm.put(null, Double.valueOf(3), CONST_F);
		dkm.put(Integer.valueOf(3), null, CONST_G);
		dkm.put(Integer.valueOf(5), Double.valueOf(6), null);
	}

	@SuppressWarnings("unused")
	public void testNullInConstructor()
	{
		try
		{
			new DoubleKeyMap<>(null, HashMap.class);
			fail();
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//OK, expected
		}
		try
		{
			new DoubleKeyMap<>(HashMap.class, null);
			fail();
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//OK, expected
		}
	}

	@SuppressWarnings("unused")
	public void testBadClassInConstructor()
	{
		try
		{
			new DoubleKeyMap<>(StrangeMap.class, HashMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//OK, expected
		}
		try
		{
			new DoubleKeyMap<>(HashMap.class, StrangeMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//OK, expected
		}
	}


	@SuppressWarnings("unused")
	public void testBadClassInConstructor2()
	{
		try
		{
			new DoubleKeyMap<>(NoPublicZeroArgConstructorMap.class, HashMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//OK, expected
		}
		try
		{
			new DoubleKeyMap<>(HashMap.class, NoPublicZeroArgConstructorMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//OK, expected
		}
	}


	@SuppressWarnings("unused")
	public void testBadClassInConstructor3()
	{
		try
		{
			new DoubleKeyMap<>(NoZeroArgConstructorMap.class, HashMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//OK, expected
		}
		try
		{
			new DoubleKeyMap<>(HashMap.class, NoZeroArgConstructorMap.class);
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
		assertNull(dkm.get(Integer.valueOf(1), Double.valueOf(0)));
		populate();
		assertEquals(Character.valueOf('A'), dkm.get(Integer.valueOf(1), Double
			.valueOf(1)));
		assertEquals(Character.valueOf('B'), dkm.get(Integer.valueOf(1), Double
			.valueOf(2)));
		assertEquals(Character.valueOf('C'), dkm.get(Integer.valueOf(1), Double
			.valueOf(3)));
		assertNull(dkm.get(Integer.valueOf(1), Double.valueOf(0)));
		assertEquals(Character.valueOf('D'), dkm.get(Integer.valueOf(2), Double
			.valueOf(1)));
		assertEquals(Character.valueOf('E'), dkm.get(Integer.valueOf(2), Double
			.valueOf(2)));
		assertEquals(Character.valueOf('F'), dkm.get(null, Double.valueOf(3)));
		assertEquals(Character.valueOf('G'), dkm.get(Integer.valueOf(3), null));
		assertNull(dkm.get(Integer.valueOf(2), Double.valueOf(3)));
		assertNull(dkm.get(Integer.valueOf(4), Double.valueOf(0)));
		assertNull(dkm.get(Integer.valueOf(1), null));
		assertNull(dkm.get(null, Double.valueOf(1)));
	}

	@Test
	public void testRemoveAll()
	{
		assertNull(dkm.get(Integer.valueOf(1), Double.valueOf(0)));
		populate();
		dkm.removeAll(Integer.valueOf(1));
		assertNull(dkm.get(Integer.valueOf(1), Double.valueOf(1)));
		assertNull(dkm.get(Integer.valueOf(1), Double.valueOf(2)));
		assertNull(dkm.get(Integer.valueOf(1), Double.valueOf(3)));
		assertNull(dkm.get(Integer.valueOf(1), Double.valueOf(0)));
		assertEquals(Character.valueOf('D'), dkm.get(Integer.valueOf(2), Double
			.valueOf(1)));
		assertEquals(Character.valueOf('E'), dkm.get(Integer.valueOf(2), Double
			.valueOf(2)));
		assertEquals(Character.valueOf('F'), dkm.get(null, Double.valueOf(3)));
		assertEquals(Character.valueOf('G'), dkm.get(Integer.valueOf(3), null));
		assertNull(dkm.get(Integer.valueOf(2), Double.valueOf(3)));
		assertNull(dkm.get(Integer.valueOf(4), Double.valueOf(0)));
		assertNull(dkm.get(Integer.valueOf(1), null));
		assertNull(dkm.get(null, Double.valueOf(1)));
	}

	@Test
	public void testContainsKey()
	{
		assertFalse(dkm.containsKey(Integer.valueOf(4)));
		populate();
		assertTrue(dkm.containsKey(Integer.valueOf(1)));
		assertTrue(dkm.containsKey(Integer.valueOf(2)));
		assertTrue(dkm.containsKey(Integer.valueOf(3)));
		assertFalse(dkm.containsKey(Integer.valueOf(4)));
		assertTrue(dkm.containsKey(Integer.valueOf(1), Double.valueOf(1)));
		assertTrue(dkm.containsKey(Integer.valueOf(1), Double.valueOf(2)));
		assertTrue(dkm.containsKey(Integer.valueOf(1), Double.valueOf(3)));
		assertTrue(dkm.containsKey(Integer.valueOf(2), Double.valueOf(1)));
		assertTrue(dkm.containsKey(Integer.valueOf(2), Double.valueOf(2)));
		assertFalse(dkm.containsKey(Integer.valueOf(2), Double.valueOf(3)));
		assertFalse(dkm.containsKey(Integer.valueOf(3), Double.valueOf(0)));
		assertFalse(dkm.containsKey(Integer.valueOf(1), null));
		assertFalse(dkm.containsKey(null, Double.valueOf(1)));
		assertTrue(dkm.containsKey(null, Double.valueOf(3)));
		assertTrue(dkm.containsKey(Integer.valueOf(3), null));
	}

	@Test
	public void testRemove()
	{
		assertNull(dkm.remove(Integer.valueOf(1), Double.valueOf(1)));
		populate();
		assertEquals(Character.valueOf('A'), dkm.remove(Integer.valueOf(1),
			Double.valueOf(1)));
		assertFalse(dkm.containsKey(Integer.valueOf(1), Double.valueOf(1)));
		assertNull(dkm.remove(Integer.valueOf(1), Double.valueOf(1)));
		assertEquals(Character.valueOf('F'), dkm
			.remove(null, Double.valueOf(3)));
		assertFalse(dkm.containsKey(null, Double.valueOf(3)));
		assertNull(dkm.remove(null, Double.valueOf(3)));
		assertEquals(Character.valueOf('G'), dkm.remove(Integer.valueOf(3),
			null));
		assertFalse(dkm.containsKey(Integer.valueOf(3), null));
		assertNull(dkm.remove(Integer.valueOf(3), null));
		assertEquals(Character.valueOf('B'), dkm.remove(Integer.valueOf(1),
			Double.valueOf(2)));
		assertTrue(dkm.containsKey(Integer.valueOf(1)));
		assertEquals(Character.valueOf('C'), dkm.remove(Integer.valueOf(1),
			Double.valueOf(3)));
		assertFalse(dkm.containsKey(Integer.valueOf(1)));
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
		assertEquals(3, s3.size());
		assertTrue(s3.contains(Double.valueOf(1)));
		assertTrue(s3.contains(Double.valueOf(2)));
		assertTrue(s3.contains(Double.valueOf(3)));
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
		assertEquals(0, dkm.primaryKeyCount());
		populate();
		assertFalse(dkm.isEmpty());
		assertEquals(5, dkm.primaryKeyCount());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.primaryKeyCount());
		dkm.put(null, Double.valueOf(3), 'F');
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.primaryKeyCount());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.primaryKeyCount());
		dkm.put(Integer.valueOf(3), null, 'G');
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.primaryKeyCount());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.primaryKeyCount());
		dkm.put(Integer.valueOf(5), Double.valueOf(6), null);
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.primaryKeyCount());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.primaryKeyCount());
	}

	@Test
	public void testValues()
	{
		Set<Character> s = dkm.values(Integer.valueOf(4));
		assertEquals(0, s.size());
		int sSize = 1;
		try
		{
			s.add('Q');
		}
		catch (UnsupportedOperationException uoe)
		{
			// This is OK, just account for it
			sSize = 0;
		}
		// Ensure not saved in DoubleKeyMap
		Set<Character> s2 = dkm.values(Integer.valueOf(4));
		assertEquals(0, s2.size());
		assertEquals(sSize, s.size());
		// And ensure references are not kept the other direction to be altered
		// by changes in the underlying DoubleKeyMap
		populate();
		assertEquals(sSize, s.size());
		assertEquals(0, s2.size());
		Set<Character> s3 = dkm.values(Integer.valueOf(1));
		assertEquals(3, s3.size());
		assertTrue(s3.contains('A'));
		assertTrue(s3.contains('B'));
		assertTrue(s3.contains('C'));
		Set<Character> s4 = dkm.values(Integer.valueOf(3));
		assertEquals(1, s4.size());
		assertTrue(s4.contains('G'));
		Set<Character> s5 = dkm.values(null);
		assertEquals(1, s5.size());
		assertTrue(s5.contains('F'));
		Set<Character> s6 = dkm.values(Integer.valueOf(5));
		assertEquals(1, s6.size());
		assertTrue(s6.contains(null));
	}

	@Test
	public void testRemoveValue()
	{
		assertFalse(dkm.removeValue(Integer.valueOf(1), 'A'));
		assertFalse(dkm.containsKey(Integer.valueOf(1), Double.valueOf(1)));
		populate();
		assertTrue(dkm.containsKey(Integer.valueOf(1), Double.valueOf(1)));
		assertTrue(dkm.removeValue(Integer.valueOf(1), 'A'));
		assertFalse(dkm.containsKey(Integer.valueOf(1), Double.valueOf(1)));
		assertFalse(dkm.removeValue(Integer.valueOf(1), 'A'));
		assertFalse(dkm.containsKey(Integer.valueOf(1), Double.valueOf(1)));
		assertTrue(dkm.containsKey(null, Double.valueOf(3)));
		assertTrue(dkm.removeValue(null, 'F'));
		assertFalse(dkm.containsKey(null, Double.valueOf(3)));
		assertFalse(dkm.removeValue(null, 'F'));
		assertFalse(dkm.containsKey(null, Double.valueOf(3)));
		assertTrue(dkm.containsKey(Integer.valueOf(3), null));
		assertTrue(dkm.removeValue(Integer.valueOf(3), 'G'));
		assertFalse(dkm.containsKey(Integer.valueOf(3), null));
		assertFalse(dkm.removeValue(Integer.valueOf(3), 'G'));
		assertFalse(dkm.containsKey(Integer.valueOf(3), null));
		assertTrue(dkm.containsKey(Integer.valueOf(5), Double.valueOf(6)));
		assertTrue(dkm.removeValue(Integer.valueOf(5), null));
		assertFalse(dkm.containsKey(Integer.valueOf(5), Double.valueOf(6)));
		assertFalse(dkm.removeValue(Integer.valueOf(5), null));
		assertFalse(dkm.containsKey(Integer.valueOf(5), Double.valueOf(6)));
	}

	@Test
	public void testDKMconstructorOneClear()
	{
		populate();
		DoubleKeyMap<Integer, Double, Character> dkm2;
		dkm2 = new DoubleKeyMap<>(dkm);
		// Ensure 1 clear is innocent
		dkm.clear();
		assertFalse(dkm2.isEmpty());
		assertEquals(Character.valueOf('A'), dkm2.get(Integer.valueOf(1),
			Double.valueOf(1)));
		assertEquals(Character.valueOf('B'), dkm2.get(Integer.valueOf(1),
			Double.valueOf(2)));
	}

	@Test
	public void testDKMconstructorTwoClear()
	{
		populate();
		DoubleKeyMap<Integer, Double, Character> dkm2;
		dkm2 = new DoubleKeyMap<>(dkm);
		// Ensure 2 clear is innocent
		dkm2.clear();
		assertFalse(dkm.isEmpty());
		assertEquals(Character.valueOf('A'), dkm.get(Integer.valueOf(1), Double
			.valueOf(1)));
		assertEquals(Character.valueOf('B'), dkm.get(Integer.valueOf(1), Double
			.valueOf(2)));
	}

	@Test
	public void testDKMconstructorOneChange()
	{
		populate();
		DoubleKeyMap<Integer, Double, Character> dkm2;
		dkm2 = new DoubleKeyMap<>(dkm);
		// Ensure 1 change is innocent
		dkm.put(Integer.valueOf(1), Double.valueOf(1), 'Z');
		assertEquals(Character.valueOf('Z'), dkm.get(Integer.valueOf(1), Double
			.valueOf(1)));
		assertEquals(Character.valueOf('A'), dkm2.get(Integer.valueOf(1),
			Double.valueOf(1)));
	}

	@Test
	public void testDKMconstructorTwoChange()
	{
		populate();
		DoubleKeyMap<Integer, Double, Character> dkm2;
		dkm2 = new DoubleKeyMap<>(dkm);
		// Ensure 2 change is innocent
		dkm2.put(Integer.valueOf(1), Double.valueOf(1), 'Z');
		assertEquals(Character.valueOf('A'), dkm.get(Integer.valueOf(1), Double
			.valueOf(1)));
		assertEquals(Character.valueOf('Z'), dkm2.get(Integer.valueOf(1),
			Double.valueOf(1)));
	}

	@Test
	public void testDKMconstructorOneRemoveAll()
	{
		populate();
		DoubleKeyMap<Integer, Double, Character> dkm2;
		dkm2 = new DoubleKeyMap<>(dkm);
		// Ensure 1 remove is innocent
		dkm.removeAll(Integer.valueOf(1));
		assertEquals(null, dkm.get(Integer.valueOf(1), Double.valueOf(1)));
		assertEquals(Character.valueOf('A'), dkm2.get(Integer.valueOf(1),
			Double.valueOf(1)));
	}

	@Test
	public void testDKMconstructorTwoRemoveAll()
	{
		populate();
		DoubleKeyMap<Integer, Double, Character> dkm2;
		dkm2 = new DoubleKeyMap<>(dkm);
		// Ensure 2 remove is innocent
		dkm2.removeAll(Integer.valueOf(1));
		assertEquals(null, dkm2.get(Integer.valueOf(1), Double.valueOf(1)));
		assertEquals(Character.valueOf('A'), dkm.get(Integer.valueOf(1), Double
			.valueOf(1)));
	}

	@Test
	public void testDKMputAllOneClear()
	{
		populate();
		DoubleKeyMap<Integer, Double, Character> dkm2 =
				new DoubleKeyMap<>();
		dkm2.putAll(dkm);
		// Ensure 1 clear is innocent
		dkm.clear();
		assertFalse(dkm2.isEmpty());
		assertEquals(Character.valueOf('A'), dkm2.get(Integer.valueOf(1),
			Double.valueOf(1)));
		assertEquals(Character.valueOf('B'), dkm2.get(Integer.valueOf(1),
			Double.valueOf(2)));
	}

	@Test
	public void testDKMputAllTwoClear()
	{
		populate();
		DoubleKeyMap<Integer, Double, Character> dkm2 =
				new DoubleKeyMap<>();
		dkm2.putAll(dkm);
		// Ensure 2 clear is innocent
		dkm2.clear();
		assertFalse(dkm.isEmpty());
		assertEquals(Character.valueOf('A'), dkm.get(Integer.valueOf(1), Double
			.valueOf(1)));
		assertEquals(Character.valueOf('B'), dkm.get(Integer.valueOf(1), Double
			.valueOf(2)));
	}

	@Test
	public void testDKMputAllOneChange()
	{
		populate();
		DoubleKeyMap<Integer, Double, Character> dkm2 =
				new DoubleKeyMap<>();
		dkm2.putAll(dkm);
		// Ensure 1 change is innocent
		dkm.put(Integer.valueOf(1), Double.valueOf(1), 'Z');
		assertEquals(Character.valueOf('Z'), dkm.get(Integer.valueOf(1), Double
			.valueOf(1)));
		assertEquals(Character.valueOf('A'), dkm2.get(Integer.valueOf(1),
			Double.valueOf(1)));
	}

	@Test
	public void testDKMputAllTwoChange()
	{
		populate();
		DoubleKeyMap<Integer, Double, Character> dkm2 =
				new DoubleKeyMap<>();
		dkm2.putAll(dkm);
		// Ensure 2 change is innocent
		dkm2.put(Integer.valueOf(1), Double.valueOf(1), 'Z');
		assertEquals(Character.valueOf('A'), dkm.get(Integer.valueOf(1), Double
			.valueOf(1)));
		assertEquals(Character.valueOf('Z'), dkm2.get(Integer.valueOf(1),
			Double.valueOf(1)));
	}

	@Test
	public void testDKMputAllOneRemoveAll()
	{
		populate();
		DoubleKeyMap<Integer, Double, Character> dkm2 =
				new DoubleKeyMap<>();
		dkm2.putAll(dkm);
		// Ensure 1 remove is innocent
		dkm.removeAll(Integer.valueOf(1));
		assertEquals(null, dkm.get(Integer.valueOf(1), Double.valueOf(1)));
		assertEquals(Character.valueOf('A'), dkm2.get(Integer.valueOf(1),
			Double.valueOf(1)));
	}

	@Test
	public void testDKMputAllTwoRemoveAll()
	{
		populate();
		DoubleKeyMap<Integer, Double, Character> dkm2 =
				new DoubleKeyMap<>();
		dkm2.putAll(dkm);
		// Ensure 2 remove is innocent
		dkm2.removeAll(Integer.valueOf(1));
		assertEquals(null, dkm2.get(Integer.valueOf(1), Double.valueOf(1)));
		assertEquals(Character.valueOf('A'), dkm.get(Integer.valueOf(1), Double
			.valueOf(1)));
	}

	@Test
	public void testPutAllNull()
	{
		try
		{
			dkm.putAll(null);
			fail();
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//expected
		}
	}
	
	@Test
	public void testGetMap()
	{
		Map<Double, Character> map = dkm.getMapFor(Integer.valueOf(7));
		assertNotNull(map);
		assertTrue(map.isEmpty());
		assertNull(dkm.get(Integer.valueOf(1), Double.valueOf(1)));
		populate();
		map = dkm.getMapFor(Integer.valueOf(1));
		assertNotNull(map);
		assertFalse(map.isEmpty());
		Set<Double> keys = map.keySet();
		assertEquals(3, keys.size());
		assertTrue(keys.contains(Double.valueOf(1)));
		assertTrue(keys.contains(Double.valueOf(2)));
		assertTrue(keys.contains(Double.valueOf(3)));
		assertEquals(Character.valueOf(CONST_A), map.get(Double.valueOf(1)));
		assertEquals(Character.valueOf(CONST_B), map.get(Double.valueOf(2)));
		assertEquals(Character.valueOf('C'), map.get(Double.valueOf(3)));
		dkm.remove(Integer.valueOf(1), Double.valueOf(1));
		//Shouldn't alter keys
		assertEquals(3, keys.size());
		assertTrue(keys.contains(Double.valueOf(1)));
		assertTrue(keys.contains(Double.valueOf(2)));
		assertTrue(keys.contains(Double.valueOf(3)));
		assertEquals(Character.valueOf(CONST_A), map.get(Double.valueOf(1)));
		assertEquals(Character.valueOf(CONST_B), map.get(Double.valueOf(2)));
		assertEquals(Character.valueOf('C'), map.get(Double.valueOf(3)));
		keys.remove(Double.valueOf(2));
		//Shouldn't alter dkm
		map = dkm.getMapFor(Integer.valueOf(1));
		assertNotNull(map);
		assertFalse(map.isEmpty());
		keys = map.keySet();
		//At 2 here due to dkm.remove above
		assertEquals(2, keys.size());
		assertTrue(keys.contains(Double.valueOf(2)));
		assertTrue(keys.contains(Double.valueOf(3)));
		assertEquals(Character.valueOf(CONST_B), map.get(Double.valueOf(2)));
		assertEquals(Character.valueOf('C'), map.get(Double.valueOf(3)));
	}

	@Test
	public void testClone()
	{
		dkm = new DoubleKeyMap<>();
		populate();
		DoubleKeyMap<Integer, Double, Character> copy;
		try
		{
			copy = dkm.clone();
		}
		catch (CloneNotSupportedException e)
		{
			fail(e.getMessage());
			return;
		}
		Integer i1 = Integer.valueOf(1);
		Double d4 = Double.valueOf(4);
		Double d5 = Double.valueOf(5);
		// test independence
		dkm.put(i1, d4, CONST_D);
		assertNull(copy.get(i1, d4));
		copy.put(i1, d5, CONST_B);
		assertNull(dkm.get(i1, d5));
	}

	@Test
	public void testReadOnlyMap()
	{
		dkm = new DoubleKeyMap<>();
		populate();
		Map<Double, Character> map = dkm.getReadOnlyMapFor(Integer.valueOf(1));
		assertNotNull(map);
		assertFalse(map.isEmpty());
		Set<Double> keys = map.keySet();
		assertEquals(3, keys.size());
		assertTrue(keys.contains(Double.valueOf(1)));
		assertTrue(keys.contains(Double.valueOf(2)));
		assertTrue(keys.contains(Double.valueOf(3)));
		assertEquals(Character.valueOf(CONST_A), map.get(Double.valueOf(1)));
		assertEquals(Character.valueOf(CONST_B), map.get(Double.valueOf(2)));
		assertEquals(Character.valueOf('C'), map.get(Double.valueOf(3)));
		dkm.remove(Integer.valueOf(1), Double.valueOf(1));
		assertEquals(2, keys.size());
		assertTrue(keys.contains(Double.valueOf(2)));
		assertTrue(keys.contains(Double.valueOf(3)));
		assertEquals(Character.valueOf(CONST_B), map.get(Double.valueOf(2)));
		assertEquals(Character.valueOf('C'), map.get(Double.valueOf(3)));
		try
		{
			//Shouldn't alter dkm
			keys.remove(Double.valueOf(2));
			fail();
		}
		catch (UnsupportedOperationException e)
		{
			//Expected
		}
		dkm.removeAll(Integer.valueOf(1));
		//Now map is independent, but not empty
		assertEquals(2, keys.size());
		assertTrue(keys.contains(Double.valueOf(2)));
		assertTrue(keys.contains(Double.valueOf(3)));
		assertEquals(Character.valueOf(CONST_B), map.get(Double.valueOf(2)));
		assertEquals(Character.valueOf('C'), map.get(Double.valueOf(3)));
		map = dkm.getReadOnlyMapFor(Integer.valueOf(2));
		keys = map.keySet();
		assertEquals(2, keys.size());
		assertTrue(keys.contains(Double.valueOf(1)));
		assertTrue(keys.contains(Double.valueOf(2)));
		assertEquals(Character.valueOf(CONST_D), map.get(Double.valueOf(1)));
		assertEquals(Character.valueOf('E'), map.get(Double.valueOf(2)));
		dkm.clear();
		//Again, we are now independent, but not empty
		keys = map.keySet();
		assertEquals(2, keys.size());
		assertTrue(keys.contains(Double.valueOf(1)));
		assertTrue(keys.contains(Double.valueOf(2)));
		assertEquals(Character.valueOf(CONST_D), map.get(Double.valueOf(1)));
		assertEquals(Character.valueOf('E'), map.get(Double.valueOf(2)));
	}
}
