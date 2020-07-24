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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import pcgen.testsupport.NoPublicZeroArgConstructorMap;
import pcgen.testsupport.NoZeroArgConstructorMap;
import pcgen.testsupport.StrangeMap;
import pcgen.testsupport.TestSupport;

public class DoubleKeyMapTest
{

	public void populate(DoubleKeyMap<Integer, Double, Character> dkm)
	{
		dkm.put(TestSupport.I1, TestSupport.D1, TestSupport.CONST_A);
		dkm.put(TestSupport.I1, TestSupport.D2, TestSupport.CONST_B);
		dkm.put(TestSupport.I1, TestSupport.D3, 'C');
		dkm.put(TestSupport.I2, TestSupport.D1, TestSupport.CONST_D);
		dkm.put(TestSupport.I2, TestSupport.D2, 'E');
		dkm.put(null, TestSupport.D3, TestSupport.CONST_F);
		dkm.put(TestSupport.I3, null, TestSupport.CONST_G);
		dkm.put(TestSupport.I5, TestSupport.D6, null);
	}

	@Test
	public void testNullInConstructor()
	{
		assertThrows(NullPointerException.class, () -> new DoubleKeyMap<>(null, HashMap.class));
		assertThrows(NullPointerException.class, () -> new DoubleKeyMap<>(HashMap.class, null));
	}

	@Test
	public void testBadClassInConstructor()
	{
		assertThrows(IllegalArgumentException.class, () -> new DoubleKeyMap<>(StrangeMap.class, HashMap.class));
		assertThrows(IllegalArgumentException.class, () -> new DoubleKeyMap<>(HashMap.class, StrangeMap.class));
	}

	@Test
	public void testBadClassInConstructor2()
	{
		assertThrows(IllegalArgumentException.class, () -> new DoubleKeyMap<>(NoPublicZeroArgConstructorMap.class, HashMap.class));
		assertThrows(IllegalArgumentException.class, () -> new DoubleKeyMap<>(HashMap.class, NoPublicZeroArgConstructorMap.class));
	}

	@Test
	public void testBadClassInConstructor3()
	{
		assertThrows(IllegalArgumentException.class, () -> new DoubleKeyMap<>(NoZeroArgConstructorMap.class, HashMap.class));
		assertThrows(IllegalArgumentException.class, () -> new DoubleKeyMap<>(HashMap.class, NoZeroArgConstructorMap.class));
	}

	@Test
	public void testPutGet()
	{
		DoubleKeyMap<Integer, Double, Character> dkm = new DoubleKeyMap<>();
		assertNull(dkm.get(TestSupport.I1, TestSupport.D0));
		populate(dkm);
		assertEquals(Character.valueOf('A'), dkm.get(TestSupport.I1, Double
			.valueOf(1)));
		assertEquals(Character.valueOf('B'), dkm.get(TestSupport.I1, Double
			.valueOf(2)));
		assertEquals(Character.valueOf('C'), dkm.get(TestSupport.I1, Double
			.valueOf(3)));
		assertNull(dkm.get(TestSupport.I1, TestSupport.D0));
		assertEquals(Character.valueOf('D'), dkm.get(TestSupport.I2, Double
			.valueOf(1)));
		assertEquals(Character.valueOf('E'), dkm.get(TestSupport.I2, Double
			.valueOf(2)));
		assertEquals(Character.valueOf('F'), dkm.get(null, TestSupport.D3));
		assertEquals(Character.valueOf('G'), dkm.get(TestSupport.I3, null));
		assertNull(dkm.get(TestSupport.I2, TestSupport.D3));
		assertNull(dkm.get(TestSupport.I4, TestSupport.D0));
		assertNull(dkm.get(TestSupport.I1, null));
		assertNull(dkm.get(null, TestSupport.D1));
	}

	@Test
	public void testRemoveAll()
	{
		DoubleKeyMap<Integer, Double, Character> dkm = new DoubleKeyMap<>();
		assertNull(dkm.get(TestSupport.I1, TestSupport.D0));
		populate(dkm);
		dkm.removeAll(TestSupport.I1);
		assertNull(dkm.get(TestSupport.I1, TestSupport.D1));
		assertNull(dkm.get(TestSupport.I1, TestSupport.D2));
		assertNull(dkm.get(TestSupport.I1, TestSupport.D3));
		assertNull(dkm.get(TestSupport.I1, TestSupport.D0));
		assertEquals(Character.valueOf('D'), dkm.get(TestSupport.I2, Double
			.valueOf(1)));
		assertEquals(Character.valueOf('E'), dkm.get(TestSupport.I2, Double
			.valueOf(2)));
		assertEquals(Character.valueOf('F'), dkm.get(null, TestSupport.D3));
		assertEquals(Character.valueOf('G'), dkm.get(TestSupport.I3, null));
		assertNull(dkm.get(TestSupport.I2, TestSupport.D3));
		assertNull(dkm.get(TestSupport.I4, TestSupport.D0));
		assertNull(dkm.get(TestSupport.I1, null));
		assertNull(dkm.get(null, TestSupport.D1));
	}

	@Test
	public void testContainsKey()
	{
		DoubleKeyMap<Integer, Double, Character> dkm = new DoubleKeyMap<>();
		assertFalse(dkm.containsKey(TestSupport.I4));
		populate(dkm);
		assertTrue(dkm.containsKey(TestSupport.I1));
		assertTrue(dkm.containsKey(TestSupport.I2));
		assertTrue(dkm.containsKey(TestSupport.I3));
		assertFalse(dkm.containsKey(TestSupport.I4));
		assertTrue(dkm.containsKey(TestSupport.I1, TestSupport.D1));
		assertTrue(dkm.containsKey(TestSupport.I1, TestSupport.D2));
		assertTrue(dkm.containsKey(TestSupport.I1, TestSupport.D3));
		assertTrue(dkm.containsKey(TestSupport.I2, TestSupport.D1));
		assertTrue(dkm.containsKey(TestSupport.I2, TestSupport.D2));
		assertFalse(dkm.containsKey(TestSupport.I2, TestSupport.D3));
		assertFalse(dkm.containsKey(TestSupport.I3, TestSupport.D0));
		assertFalse(dkm.containsKey(TestSupport.I1, null));
		assertFalse(dkm.containsKey(null, TestSupport.D1));
		assertTrue(dkm.containsKey(null, TestSupport.D3));
		assertTrue(dkm.containsKey(TestSupport.I3, null));
	}

	@Test
	public void testRemove()
	{
		DoubleKeyMap<Integer, Double, Character> dkm = new DoubleKeyMap<>();
		assertNull(dkm.remove(TestSupport.I1, TestSupport.D1));
		populate(dkm);
		assertEquals(Character.valueOf('A'), dkm.remove(TestSupport.I1,
			TestSupport.D1));
		assertFalse(dkm.containsKey(TestSupport.I1, TestSupport.D1));
		assertNull(dkm.remove(TestSupport.I1, TestSupport.D1));
		assertEquals(Character.valueOf('F'), dkm
			.remove(null, TestSupport.D3));
		assertFalse(dkm.containsKey(null, TestSupport.D3));
		assertNull(dkm.remove(null, TestSupport.D3));
		assertEquals(Character.valueOf('G'), dkm.remove(TestSupport.I3,
			null));
		assertFalse(dkm.containsKey(TestSupport.I3, null));
		assertNull(dkm.remove(TestSupport.I3, null));
		assertEquals(Character.valueOf('B'), dkm.remove(TestSupport.I1,
			TestSupport.D2));
		assertTrue(dkm.containsKey(TestSupport.I1));
		assertEquals(Character.valueOf('C'), dkm.remove(TestSupport.I1,
			TestSupport.D3));
		assertFalse(dkm.containsKey(TestSupport.I1));
	}

	@Test
	public void testGetKeySet()
	{
		DoubleKeyMap<Integer, Double, Character> dkm = new DoubleKeyMap<>();
		Set<Integer> s = dkm.getKeySet();
		assertEquals(0, s.size());
		s.add(Integer.valueOf(-5));
		// Ensure not saved in DoubleKeyMap
		Set<Integer> s2 = dkm.getKeySet();
		assertEquals(0, s2.size());
		assertEquals(1, s.size());
		// And ensure references are not kept the other direction to be altered
		// by changes in the underlying DoubleKeyMap
		populate(dkm);
		assertEquals(1, s.size());
		assertEquals(0, s2.size());
		Set<Integer> s3 = dkm.getKeySet();
		assertEquals(5, s3.size());
		assertTrue(s3.contains(TestSupport.I1));
		assertTrue(s3.contains(TestSupport.I2));
		assertTrue(s3.contains(TestSupport.I3));
		assertTrue(s3.contains(TestSupport.I5));
		assertTrue(s3.contains(null));
	}

	@Test
	public void testGetSecondaryKeySet()
	{
		DoubleKeyMap<Integer, Double, Character> dkm = new DoubleKeyMap<>();
		Set<Double> s = dkm.getSecondaryKeySet(TestSupport.I4);
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
		Set<Double> s2 = dkm.getSecondaryKeySet(TestSupport.I4);
		assertEquals(0, s2.size());
		assertEquals(sSize, s.size());
		// And ensure references are not kept the other direction to be altered
		// by changes in the underlying DoubleKeyMap
		populate(dkm);
		assertEquals(sSize, s.size());
		assertEquals(0, s2.size());
		Set<Double> s3 = dkm.getSecondaryKeySet(TestSupport.I1);
		assertEquals(3, s3.size());
		assertTrue(s3.contains(TestSupport.D1));
		assertTrue(s3.contains(TestSupport.D2));
		assertTrue(s3.contains(TestSupport.D3));
		Set<Double> s4 = dkm.getSecondaryKeySet(TestSupport.I3);
		assertEquals(1, s4.size());
		assertTrue(s4.contains(null));
		Set<Double> s5 = dkm.getSecondaryKeySet(null);
		assertEquals(1, s5.size());
		assertTrue(s5.contains(TestSupport.D3));
	}

	@Test
	public void testClearIsEmpty()
	{
		DoubleKeyMap<Integer, Double, Character> dkm = new DoubleKeyMap<>();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.primaryKeyCount());
		populate(dkm);
		assertFalse(dkm.isEmpty());
		assertEquals(5, dkm.primaryKeyCount());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.primaryKeyCount());
		dkm.put(null, TestSupport.D3, 'F');
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.primaryKeyCount());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.primaryKeyCount());
		dkm.put(TestSupport.I3, null, 'G');
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.primaryKeyCount());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.primaryKeyCount());
		dkm.put(TestSupport.I5, TestSupport.D6, null);
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.primaryKeyCount());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.primaryKeyCount());
	}

	@Test
	public void testValues()
	{
		DoubleKeyMap<Integer, Double, Character> dkm = new DoubleKeyMap<>();
		Set<Character> s = dkm.values(TestSupport.I4);
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
		Set<Character> s2 = dkm.values(TestSupport.I4);
		assertEquals(0, s2.size());
		assertEquals(sSize, s.size());
		// And ensure references are not kept the other direction to be altered
		// by changes in the underlying DoubleKeyMap
		populate(dkm);
		assertEquals(sSize, s.size());
		assertEquals(0, s2.size());
		Set<Character> s3 = dkm.values(TestSupport.I1);
		assertEquals(3, s3.size());
		assertTrue(s3.contains('A'));
		assertTrue(s3.contains('B'));
		assertTrue(s3.contains('C'));
		Set<Character> s4 = dkm.values(TestSupport.I3);
		assertEquals(1, s4.size());
		assertTrue(s4.contains('G'));
		Set<Character> s5 = dkm.values(null);
		assertEquals(1, s5.size());
		assertTrue(s5.contains('F'));
		Set<Character> s6 = dkm.values(TestSupport.I5);
		assertEquals(1, s6.size());
		assertTrue(s6.contains(null));
	}

	@Test
	public void testRemoveValue()
	{
		DoubleKeyMap<Integer, Double, Character> dkm = new DoubleKeyMap<>();
		assertFalse(dkm.removeValue(TestSupport.I1, 'A'));
		assertFalse(dkm.containsKey(TestSupport.I1, TestSupport.D1));
		populate(dkm);
		assertTrue(dkm.containsKey(TestSupport.I1, TestSupport.D1));
		assertTrue(dkm.removeValue(TestSupport.I1, 'A'));
		assertFalse(dkm.containsKey(TestSupport.I1, TestSupport.D1));
		assertFalse(dkm.removeValue(TestSupport.I1, 'A'));
		assertFalse(dkm.containsKey(TestSupport.I1, TestSupport.D1));
		assertTrue(dkm.containsKey(null, TestSupport.D3));
		assertTrue(dkm.removeValue(null, 'F'));
		assertFalse(dkm.containsKey(null, TestSupport.D3));
		assertFalse(dkm.removeValue(null, 'F'));
		assertFalse(dkm.containsKey(null, TestSupport.D3));
		assertTrue(dkm.containsKey(TestSupport.I3, null));
		assertTrue(dkm.removeValue(TestSupport.I3, 'G'));
		assertFalse(dkm.containsKey(TestSupport.I3, null));
		assertFalse(dkm.removeValue(TestSupport.I3, 'G'));
		assertFalse(dkm.containsKey(TestSupport.I3, null));
		assertTrue(dkm.containsKey(TestSupport.I5, TestSupport.D6));
		assertTrue(dkm.removeValue(TestSupport.I5, null));
		assertFalse(dkm.containsKey(TestSupport.I5, TestSupport.D6));
		assertFalse(dkm.removeValue(TestSupport.I5, null));
		assertFalse(dkm.containsKey(TestSupport.I5, TestSupport.D6));
	}

	@Test
	public void testDKMconstructorOneClear()
	{
		DoubleKeyMap<Integer, Double, Character> dkm = new DoubleKeyMap<>();
		populate(dkm);
		DoubleKeyMap<Integer, Double, Character> dkm2;
		dkm2 = new DoubleKeyMap<>(dkm);
		// Ensure 1 clear is innocent
		dkm.clear();
		assertFalse(dkm2.isEmpty());
		assertEquals(Character.valueOf('A'), dkm2.get(TestSupport.I1,
			TestSupport.D1));
		assertEquals(Character.valueOf('B'), dkm2.get(TestSupport.I1,
			TestSupport.D2));
	}

	@Test
	public void testDKMconstructorTwoClear()
	{
		DoubleKeyMap<Integer, Double, Character> dkm = new DoubleKeyMap<>();
		populate(dkm);
		DoubleKeyMap<Integer, Double, Character> dkm2;
		dkm2 = new DoubleKeyMap<>(dkm);
		// Ensure 2 clear is innocent
		dkm2.clear();
		assertFalse(dkm.isEmpty());
		assertEquals(Character.valueOf('A'), dkm.get(TestSupport.I1, Double
			.valueOf(1)));
		assertEquals(Character.valueOf('B'), dkm.get(TestSupport.I1, Double
			.valueOf(2)));
	}

	@Test
	public void testDKMconstructorOneChange()
	{
		DoubleKeyMap<Integer, Double, Character> dkm = new DoubleKeyMap<>();
		populate(dkm);
		DoubleKeyMap<Integer, Double, Character> dkm2;
		dkm2 = new DoubleKeyMap<>(dkm);
		// Ensure 1 change is innocent
		dkm.put(TestSupport.I1, TestSupport.D1, 'Z');
		assertEquals(Character.valueOf('Z'), dkm.get(TestSupport.I1, Double
			.valueOf(1)));
		assertEquals(Character.valueOf('A'), dkm2.get(TestSupport.I1,
			TestSupport.D1));
	}

	@Test
	public void testDKMconstructorTwoChange()
	{
		DoubleKeyMap<Integer, Double, Character> dkm = new DoubleKeyMap<>();
		populate(dkm);
		DoubleKeyMap<Integer, Double, Character> dkm2;
		dkm2 = new DoubleKeyMap<>(dkm);
		// Ensure 2 change is innocent
		dkm2.put(TestSupport.I1, TestSupport.D1, 'Z');
		assertEquals(Character.valueOf('A'), dkm.get(TestSupport.I1, Double
			.valueOf(1)));
		assertEquals(Character.valueOf('Z'), dkm2.get(TestSupport.I1,
			TestSupport.D1));
	}

	@Test
	public void testDKMconstructorOneRemoveAll()
	{
		DoubleKeyMap<Integer, Double, Character> dkm = new DoubleKeyMap<>();
		populate(dkm);
		DoubleKeyMap<Integer, Double, Character> dkm2;
		dkm2 = new DoubleKeyMap<>(dkm);
		// Ensure 1 remove is innocent
		dkm.removeAll(TestSupport.I1);
		assertEquals(null, dkm.get(TestSupport.I1, TestSupport.D1));
		assertEquals(Character.valueOf('A'), dkm2.get(TestSupport.I1,
			TestSupport.D1));
	}

	@Test
	public void testDKMconstructorTwoRemoveAll()
	{
		DoubleKeyMap<Integer, Double, Character> dkm = new DoubleKeyMap<>();
		populate(dkm);
		DoubleKeyMap<Integer, Double, Character> dkm2;
		dkm2 = new DoubleKeyMap<>(dkm);
		// Ensure 2 remove is innocent
		dkm2.removeAll(TestSupport.I1);
		assertEquals(null, dkm2.get(TestSupport.I1, TestSupport.D1));
		assertEquals(Character.valueOf('A'), dkm.get(TestSupport.I1, Double
			.valueOf(1)));
	}

	@Test
	public void testDKMputAllOneClear()
	{
		DoubleKeyMap<Integer, Double, Character> dkm = new DoubleKeyMap<>();
		populate(dkm);
		DoubleKeyMap<Integer, Double, Character> dkm2 =
				new DoubleKeyMap<>();
		dkm2.putAll(dkm);
		// Ensure 1 clear is innocent
		dkm.clear();
		assertFalse(dkm2.isEmpty());
		assertEquals(Character.valueOf('A'), dkm2.get(TestSupport.I1,
			TestSupport.D1));
		assertEquals(Character.valueOf('B'), dkm2.get(TestSupport.I1,
			TestSupport.D2));
	}

	@Test
	public void testDKMputAllTwoClear()
	{
		DoubleKeyMap<Integer, Double, Character> dkm = new DoubleKeyMap<>();
		populate(dkm);
		DoubleKeyMap<Integer, Double, Character> dkm2 =
				new DoubleKeyMap<>();
		dkm2.putAll(dkm);
		// Ensure 2 clear is innocent
		dkm2.clear();
		assertFalse(dkm.isEmpty());
		assertEquals(Character.valueOf('A'), dkm.get(TestSupport.I1, Double
			.valueOf(1)));
		assertEquals(Character.valueOf('B'), dkm.get(TestSupport.I1, Double
			.valueOf(2)));
	}

	@Test
	public void testDKMputAllOneChange()
	{
		DoubleKeyMap<Integer, Double, Character> dkm = new DoubleKeyMap<>();
		populate(dkm);
		DoubleKeyMap<Integer, Double, Character> dkm2 =
				new DoubleKeyMap<>();
		dkm2.putAll(dkm);
		// Ensure 1 change is innocent
		dkm.put(TestSupport.I1, TestSupport.D1, 'Z');
		assertEquals(Character.valueOf('Z'), dkm.get(TestSupport.I1, Double
			.valueOf(1)));
		assertEquals(Character.valueOf('A'), dkm2.get(TestSupport.I1,
			TestSupport.D1));
	}

	@Test
	public void testDKMputAllTwoChange()
	{
		DoubleKeyMap<Integer, Double, Character> dkm = new DoubleKeyMap<>();
		populate(dkm);
		DoubleKeyMap<Integer, Double, Character> dkm2 =
				new DoubleKeyMap<>();
		dkm2.putAll(dkm);
		// Ensure 2 change is innocent
		dkm2.put(TestSupport.I1, TestSupport.D1, 'Z');
		assertEquals(Character.valueOf('A'), dkm.get(TestSupport.I1, Double
			.valueOf(1)));
		assertEquals(Character.valueOf('Z'), dkm2.get(TestSupport.I1,
			TestSupport.D1));
	}

	@Test
	public void testDKMputAllOneRemoveAll()
	{
		DoubleKeyMap<Integer, Double, Character> dkm = new DoubleKeyMap<>();
		populate(dkm);
		DoubleKeyMap<Integer, Double, Character> dkm2 =
				new DoubleKeyMap<>();
		dkm2.putAll(dkm);
		// Ensure 1 remove is innocent
		dkm.removeAll(TestSupport.I1);
		assertEquals(null, dkm.get(TestSupport.I1, TestSupport.D1));
		assertEquals(Character.valueOf('A'), dkm2.get(TestSupport.I1,
			TestSupport.D1));
	}

	@Test
	public void testDKMputAllTwoRemoveAll()
	{
		DoubleKeyMap<Integer, Double, Character> dkm = new DoubleKeyMap<>();
		populate(dkm);
		DoubleKeyMap<Integer, Double, Character> dkm2 =
				new DoubleKeyMap<>();
		dkm2.putAll(dkm);
		// Ensure 2 remove is innocent
		dkm2.removeAll(TestSupport.I1);
		assertEquals(null, dkm2.get(TestSupport.I1, TestSupport.D1));
		assertEquals(Character.valueOf('A'), dkm.get(TestSupport.I1, Double
			.valueOf(1)));
	}

	@Test
	public void testPutAllNull()
	{
		DoubleKeyMap<Integer, Double, Character> dkm = new DoubleKeyMap<>();
		assertThrows(NullPointerException.class, () -> dkm.putAll(null));
	}
	
	@Test
	public void testGetMap()
	{
		DoubleKeyMap<Integer, Double, Character> dkm = new DoubleKeyMap<>();
		Map<Double, Character> map = dkm.getMapFor(Integer.valueOf(7));
		assertNotNull(map);
		assertTrue(map.isEmpty());
		assertNull(dkm.get(TestSupport.I1, TestSupport.D1));
		populate(dkm);
		map = dkm.getMapFor(TestSupport.I1);
		assertNotNull(map);
		assertFalse(map.isEmpty());
		Set<Double> keys = map.keySet();
		assertEquals(3, keys.size());
		assertTrue(keys.contains(TestSupport.D1));
		assertTrue(keys.contains(TestSupport.D2));
		assertTrue(keys.contains(TestSupport.D3));
		assertEquals(Character.valueOf(TestSupport.CONST_A), map.get(TestSupport.D1));
		assertEquals(Character.valueOf(TestSupport.CONST_B), map.get(TestSupport.D2));
		assertEquals(Character.valueOf('C'), map.get(TestSupport.D3));
		dkm.remove(TestSupport.I1, TestSupport.D1);
		//Shouldn't alter keys
		assertEquals(3, keys.size());
		assertTrue(keys.contains(TestSupport.D1));
		assertTrue(keys.contains(TestSupport.D2));
		assertTrue(keys.contains(TestSupport.D3));
		assertEquals(Character.valueOf(TestSupport.CONST_A), map.get(TestSupport.D1));
		assertEquals(Character.valueOf(TestSupport.CONST_B), map.get(TestSupport.D2));
		assertEquals(Character.valueOf('C'), map.get(TestSupport.D3));
		keys.remove(TestSupport.D2);
		//Shouldn't alter dkm
		map = dkm.getMapFor(TestSupport.I1);
		assertNotNull(map);
		assertFalse(map.isEmpty());
		keys = map.keySet();
		//At 2 here due to dkm.remove above
		assertEquals(2, keys.size());
		assertTrue(keys.contains(TestSupport.D2));
		assertTrue(keys.contains(TestSupport.D3));
		assertEquals(Character.valueOf(TestSupport.CONST_B), map.get(TestSupport.D2));
		assertEquals(Character.valueOf('C'), map.get(TestSupport.D3));
	}

	@Test
	public void testClone()
	{
		DoubleKeyMap<Integer, Double, Character> dkm = new DoubleKeyMap<>();
		populate(dkm);
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
		Integer i1 = TestSupport.I1;
		Double d4 = TestSupport.D4;
		Double d5 = TestSupport.D5;
		// test independence
		dkm.put(i1, d4, TestSupport.CONST_D);
		assertNull(copy.get(i1, d4));
		copy.put(i1, d5, TestSupport.CONST_B);
		assertNull(dkm.get(i1, d5));
	}

	@Test
	public void testReadOnlyMap()
	{
		DoubleKeyMap<Integer, Double, Character> dkm = new DoubleKeyMap<>();
		populate(dkm);
		Map<Double, Character> map = dkm.getReadOnlyMapFor(TestSupport.I1);
		assertNotNull(map);
		assertFalse(map.isEmpty());
		Set<Double> keys = map.keySet();
		assertEquals(3, keys.size());
		assertTrue(keys.contains(TestSupport.D1));
		assertTrue(keys.contains(TestSupport.D2));
		assertTrue(keys.contains(TestSupport.D3));
		assertEquals(Character.valueOf(TestSupport.CONST_A), map.get(TestSupport.D1));
		assertEquals(Character.valueOf(TestSupport.CONST_B), map.get(TestSupport.D2));
		assertEquals(Character.valueOf('C'), map.get(TestSupport.D3));
		dkm.remove(TestSupport.I1, TestSupport.D1);
		assertEquals(2, keys.size());
		assertTrue(keys.contains(TestSupport.D2));
		assertTrue(keys.contains(TestSupport.D3));
		assertEquals(Character.valueOf(TestSupport.CONST_B), map.get(TestSupport.D2));
		assertEquals(Character.valueOf('C'), map.get(TestSupport.D3));
		assertThrows(UnsupportedOperationException.class, () -> keys.remove(TestSupport.D2));
		dkm.removeAll(TestSupport.I1);
		//Now map is independent, but not empty
		assertEquals(2, keys.size());
		assertTrue(keys.contains(TestSupport.D2));
		assertTrue(keys.contains(TestSupport.D3));
		assertEquals(Character.valueOf(TestSupport.CONST_B), map.get(TestSupport.D2));
		assertEquals(Character.valueOf('C'), map.get(TestSupport.D3));
		map = dkm.getReadOnlyMapFor(TestSupport.I2);
		Set<Double> newKeys = map.keySet();
		assertEquals(2, newKeys.size());
		assertTrue(newKeys.contains(TestSupport.D1));
		assertTrue(newKeys.contains(TestSupport.D2));
		assertEquals(Character.valueOf(TestSupport.CONST_D), map.get(TestSupport.D1));
		assertEquals(Character.valueOf('E'), map.get(TestSupport.D2));
		dkm.clear();
		//Again, we are now independent, but not empty
		newKeys = map.keySet();
		assertEquals(2, newKeys.size());
		assertTrue(newKeys.contains(TestSupport.D1));
		assertTrue(newKeys.contains(TestSupport.D2));
		assertEquals(Character.valueOf(TestSupport.CONST_D), map.get(TestSupport.D1));
		assertEquals(Character.valueOf('E'), map.get(TestSupport.D2));
	}
}
