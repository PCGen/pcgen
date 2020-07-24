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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import pcgen.testsupport.NoPublicZeroArgConstructorMap;
import pcgen.testsupport.NoZeroArgConstructorMap;
import pcgen.testsupport.StrangeMap;
import pcgen.testsupport.TestSupport;

public class TripleKeyMapToListTest
{

	public void populate(TripleKeyMapToList<Integer, Double, Long, Character> tkm)
	{
		tkm.addToListFor(TestSupport.I1, TestSupport.D1, TestSupport.L1, TestSupport.CONST_A);
		tkm.addToListFor(TestSupport.I1, TestSupport.D1, TestSupport.L1, TestSupport.CONST_B);
		tkm.addToListFor(TestSupport.I1, TestSupport.D2, TestSupport.L2, TestSupport.CONST_C);
		tkm.addToListFor(TestSupport.I2, TestSupport.D1, TestSupport.L2, TestSupport.CONST_D);
		tkm.addToListFor(TestSupport.I2, TestSupport.D2, TestSupport.L1, TestSupport.CONST_E);
		tkm.addToListFor(TestSupport.I2, TestSupport.D2, TestSupport.L1, null);
		tkm.addToListFor(null, TestSupport.D3, TestSupport.L2, TestSupport.CONST_F);
		tkm.addToListFor(TestSupport.I3, null, TestSupport.L1, TestSupport.CONST_G);
		tkm.addToListFor(TestSupport.I4, TestSupport.D4, null, TestSupport.CONST_H);
		tkm.addToListFor(TestSupport.I5, TestSupport.D6, TestSupport.L1, null);
	}

	@Test
	public void testPutGet()
	{
		TripleKeyMapToList<Integer, Double, Long, Character> tkm =
				new TripleKeyMapToList<>();
		assertNull(tkm.getListFor(TestSupport.I1, TestSupport.D0, TestSupport.L1));
		populate(tkm);
		List<Character> l = tkm.getListFor(TestSupport.I1, TestSupport.D1, TestSupport.L1);
		assertEquals(2, l.size());
		assertTrue(l.contains(TestSupport.CONST_A));
		assertTrue(l.contains(TestSupport.CONST_B));
		assertTrue(tkm.containsListFor(TestSupport.I1, TestSupport.D2, TestSupport.L2));
		l = tkm.getListFor(TestSupport.I1, TestSupport.D2, TestSupport.L2);
		assertEquals(1, l.size());
		assertTrue(l.contains(TestSupport.CONST_C));
		tkm.addToListFor(TestSupport.I1, TestSupport.D2, TestSupport.L2, TestSupport.CONST_C);
		l = tkm.getListFor(TestSupport.I1, TestSupport.D2, TestSupport.L2);
		assertEquals(2, l.size());
		assertTrue(l.contains(TestSupport.CONST_C));
		// two of them
		l.remove(Character.valueOf(TestSupport.CONST_C));
		assertTrue(l.contains(TestSupport.CONST_C));
		l = tkm.getListFor(TestSupport.I2, TestSupport.D2, TestSupport.L1);
		assertEquals(2, l.size());
		assertTrue(l.contains(TestSupport.CONST_E));
		assertTrue(l.contains(null));
		l.remove(Character.valueOf(TestSupport.CONST_E));
		List<Character> l2 = tkm.getListFor(TestSupport.I2, TestSupport.D2, TestSupport.L1);
		assertEquals(2, l2.size());
		assertTrue(l2.contains(TestSupport.CONST_E));
		assertTrue(l2.contains(null));
		assertEquals(1, l.size());
		assertTrue(l.contains(null));
		tkm.addToListFor(TestSupport.I2, TestSupport.D2, TestSupport.L1, null);
		l = tkm.getListFor(TestSupport.I2, TestSupport.D2, TestSupport.L1);
		assertEquals(3, l.size());
		assertTrue(l.contains(TestSupport.CONST_E));
		assertTrue(l.contains(null));
		// Two of them.
		l.remove(null);
		assertTrue(l.contains(null));
		assertNull(tkm.getListFor(TestSupport.I1, TestSupport.D0, TestSupport.L1));
		assertNull(tkm.getListFor(TestSupport.I2, TestSupport.D3, TestSupport.L2));
		assertNull(tkm.getListFor(TestSupport.I4, TestSupport.D0, TestSupport.L1));
		assertNull(tkm.getListFor(TestSupport.I1, null, TestSupport.L2));
		assertNull(tkm.getListFor(null, TestSupport.D1, TestSupport.L1));
		assertNull(tkm.getListFor(TestSupport.I1, TestSupport.D1, null));
		tkm.clear();
		assertEquals(2, l2.size());
		assertTrue(l2.contains(TestSupport.CONST_E));
		assertTrue(l2.contains(null));
	}

	@Test
	public void testContainsKey()
	{
		TripleKeyMapToList<Integer, Double, Long, Character> tkm =
				new TripleKeyMapToList<>();
		populate(tkm);
		assertTrue(tkm.containsListFor(TestSupport.I1, TestSupport.D1, TestSupport.L1));
		assertTrue(tkm.containsListFor(TestSupport.I1, TestSupport.D2, TestSupport.L2));
		assertTrue(tkm.containsListFor(TestSupport.I2, TestSupport.D1, TestSupport.L2));
		assertTrue(tkm.containsListFor(TestSupport.I2, TestSupport.D2, TestSupport.L1));
		assertFalse(tkm.containsListFor(TestSupport.I2, TestSupport.D3, TestSupport.L1));
		assertFalse(tkm.containsListFor(Integer.valueOf(-4), TestSupport.D0, TestSupport.L1));
		assertFalse(tkm.containsListFor(TestSupport.I1, null, TestSupport.L1));
		assertFalse(tkm.containsListFor(null, TestSupport.D1, TestSupport.L1));
		assertTrue(tkm.containsListFor(null, TestSupport.D3, TestSupport.L2));
		assertTrue(tkm.containsListFor(TestSupport.I3, null, TestSupport.L1));
		assertTrue(tkm.containsListFor(TestSupport.I4, TestSupport.D4, null));
	}

	@Test
	public void testRemoveListFor()
	{
		TripleKeyMapToList<Integer, Double, Long, Character> tkm =
				new TripleKeyMapToList<>();
		assertNull(tkm.removeListFor(TestSupport.I1, TestSupport.D1, TestSupport.L1));
		populate(tkm);
		assertTrue(tkm.containsListFor(TestSupport.I1, TestSupport.D1, TestSupport.L1));
		List<Character> l = tkm.removeListFor(TestSupport.I1, TestSupport.D1, TestSupport.L1);
		assertFalse(tkm.containsListFor(TestSupport.I1, TestSupport.D1, TestSupport.L1));
		assertEquals(2, l.size());
		assertTrue(l.contains(TestSupport.CONST_A));
		assertTrue(l.contains(TestSupport.CONST_B));
		assertFalse(tkm.containsListFor(TestSupport.I1, TestSupport.D1, TestSupport.L1));
		assertNull(tkm.getListFor(TestSupport.I1, TestSupport.D1, TestSupport.L1));
		l = tkm.removeListFor(TestSupport.I1, TestSupport.D2, TestSupport.L2);
		assertFalse(tkm.containsListFor(TestSupport.I1, TestSupport.D1, TestSupport.L1));
		assertEquals(1, l.size());
		assertTrue(l.contains(TestSupport.CONST_C));
		tkm.addToListFor(TestSupport.I1, TestSupport.D2, TestSupport.L2, TestSupport.CONST_C);
		l = tkm.removeListFor(TestSupport.I1, TestSupport.D2, TestSupport.L2);
		assertEquals(1, l.size());
		assertTrue(l.contains(TestSupport.CONST_C));
		l = tkm.removeListFor(TestSupport.I2, TestSupport.D2, TestSupport.L1);
		assertEquals(2, l.size());
		assertTrue(l.contains(TestSupport.CONST_E));
		assertTrue(l.contains(null));
		assertNull(tkm.removeListFor(TestSupport.I2, TestSupport.D2, TestSupport.L1));
		assertNull(tkm.getListFor(TestSupport.I1, TestSupport.D0, TestSupport.L1));
		assertNull(tkm.getListFor(TestSupport.I2, TestSupport.D3, TestSupport.L1));
		assertNull(tkm.getListFor(TestSupport.I4, TestSupport.D0, TestSupport.L1));
		assertNull(tkm.getListFor(TestSupport.I1, null, TestSupport.L1));
		assertNull(tkm.getListFor(null, TestSupport.D1, TestSupport.L1));
		assertNull(tkm.getListFor(TestSupport.I1, TestSupport.D1, null));
	}

	@Test
	public void testRemoveListsFor()
	{
		TripleKeyMapToList<Integer, Double, Long, Character> tkm =
				new TripleKeyMapToList<>();
		assertNull(tkm.removeListsFor(TestSupport.I1, TestSupport.D1));
		populate(tkm);
		tkm.addToListFor(TestSupport.I1, TestSupport.D1, TestSupport.L2, TestSupport.CONST_C);
		assertTrue(tkm.containsListFor(TestSupport.I1, TestSupport.D1, TestSupport.L1));
		MapToList<Long, Character> mtl = tkm.removeListsFor(TestSupport.I1, TestSupport.D1);
		assertFalse(tkm.containsListFor(TestSupport.I1, TestSupport.D1, TestSupport.L1));
		Set<Long> keys = mtl.getKeySet();
		assertEquals(2, keys.size());
		assertTrue(keys.contains(TestSupport.L1));
		assertTrue(keys.contains(TestSupport.L2));
		List<Character> list = mtl.getListFor(TestSupport.L1);
		assertEquals(2, list.size());
		assertTrue(list.contains(TestSupport.CONST_A));
		assertTrue(list.contains(TestSupport.CONST_B));
		list = mtl.getListFor(TestSupport.L2);
		assertEquals(1, list.size());
		assertTrue(list.contains(TestSupport.CONST_C));
	}

	@Test
	public void testGetKeySet()
	{
		TripleKeyMapToList<Integer, Double, Long, Character> tkm =
				new TripleKeyMapToList<>();
		Set<Integer> s = tkm.getKeySet();
		assertEquals(0, s.size());
		s.add(Integer.valueOf(-5));
		// Ensure not saved in DoubleKeyMap
		Set<Integer> s2 = tkm.getKeySet();
		assertEquals(0, s2.size());
		assertEquals(1, s.size());
		// And ensure references are not kept the other direction to be altered
		// by changes in the underlying DoubleKeyMap
		populate(tkm);
		assertEquals(1, s.size());
		assertEquals(0, s2.size());
		Set<Integer> s3 = tkm.getKeySet();
		assertEquals(6, s3.size());
		assertTrue(s3.contains(TestSupport.I1));
		assertTrue(s3.contains(TestSupport.I2));
		assertTrue(s3.contains(TestSupport.I3));
		assertTrue(s3.contains(TestSupport.I4));
		assertTrue(s3.contains(TestSupport.I5));
		assertTrue(s3.contains(null));
	}

	@Test
	public void testGetSecondaryKeySet()
	{
		TripleKeyMapToList<Integer, Double, Long, Character> tkm =
				new TripleKeyMapToList<>();
		Set<Double> s = tkm.getSecondaryKeySet(TestSupport.I4);
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
		Set<Double> s2 = tkm.getSecondaryKeySet(TestSupport.I4);
		assertEquals(0, s2.size());
		assertEquals(sSize, s.size());
		// And ensure references are not kept the other direction to be altered
		// by changes in the underlying DoubleKeyMap
		populate(tkm);
		assertEquals(sSize, s.size());
		assertEquals(0, s2.size());
		Set<Double> s3 = tkm.getSecondaryKeySet(TestSupport.I1);
		assertEquals(2, s3.size());
		assertTrue(s3.contains(TestSupport.D1));
		assertTrue(s3.contains(TestSupport.D2));
		Set<Double> s4 = tkm.getSecondaryKeySet(TestSupport.I3);
		assertEquals(1, s4.size());
		assertTrue(s4.contains(null));
		Set<Double> s5 = tkm.getSecondaryKeySet(null);
		assertEquals(1, s5.size());
		assertTrue(s5.contains(TestSupport.D3));
	}

	@Test
	public void testClearIsEmpty()
	{
		TripleKeyMapToList<Integer, Double, Long, Character> tkm =
				new TripleKeyMapToList<>();
		assertTrue(tkm.isEmpty());
		assertEquals(0, tkm.firstKeyCount());
		populate(tkm);
		assertFalse(tkm.isEmpty());
		assertEquals(6, tkm.firstKeyCount());
		tkm.clear();
		assertTrue(tkm.isEmpty());
		assertEquals(0, tkm.firstKeyCount());
		tkm.addToListFor(null, TestSupport.D3, TestSupport.L1, 'F');
		assertFalse(tkm.isEmpty());
		assertEquals(1, tkm.firstKeyCount());
		tkm.clear();
		assertTrue(tkm.isEmpty());
		assertEquals(0, tkm.firstKeyCount());
		tkm.addToListFor(TestSupport.I3, null, TestSupport.L2, 'G');
		assertFalse(tkm.isEmpty());
		assertEquals(1, tkm.firstKeyCount());
		tkm.clear();
		assertTrue(tkm.isEmpty());
		assertEquals(0, tkm.firstKeyCount());
		tkm.addToListFor(TestSupport.I5, TestSupport.D6, TestSupport.L1, null);
		assertFalse(tkm.isEmpty());
		assertEquals(1, tkm.firstKeyCount());
		tkm.clear();
		assertTrue(tkm.isEmpty());
		assertEquals(0, tkm.firstKeyCount());
		tkm.addToListFor(TestSupport.I5, TestSupport.D6, null, 'F');
		assertFalse(tkm.isEmpty());
		assertEquals(1, tkm.firstKeyCount());
		tkm.clear();
		assertTrue(tkm.isEmpty());
		assertEquals(0, tkm.firstKeyCount());
	}

	@Test
	public void testAddAllToListFor()
	{
		TripleKeyMapToList<Integer, Double, Long, Character> tkm =
				new TripleKeyMapToList<>();
		Integer i1 = TestSupport.I1;
		Double d1 = TestSupport.D1;
		List<Character> l = new ArrayList<>();
		l.add(TestSupport.CONST_A);
		l.add(null);
		l.add(TestSupport.CONST_A);
		l.add(TestSupport.CONST_B);
		tkm.addAllToListFor(i1, d1, TestSupport.L1, l);
		assertTrue(tkm.containsListFor(i1, d1, TestSupport.L1));
		assertEquals(4, tkm.getListFor(i1, d1, TestSupport.L1).size());
		tkm.addAllToListFor(i1, d1, TestSupport.L1, Collections.singleton(TestSupport.CONST_D));
		assertEquals(4, l.size());
		// Check reference semantics!
		l.add(TestSupport.CONST_C);
		l.add(TestSupport.CONST_E);
		assertTrue(tkm.containsListFor(i1, d1, TestSupport.L1));
		assertEquals(5, tkm.getListFor(i1, d1, TestSupport.L1).size());
		l.clear();
		assertTrue(tkm.containsListFor(i1, d1, TestSupport.L1));
		assertEquals(5, tkm.getListFor(i1, d1, TestSupport.L1).size());
	}

	@Test
	public void testNullInConstructor()
	{
		assertThrows(NullPointerException.class, () -> new TripleKeyMapToList<>(null, HashMap.class, HashMap.class));
		assertThrows(NullPointerException.class, () -> new TripleKeyMapToList<>(HashMap.class, null, HashMap.class));
		assertThrows(NullPointerException.class, () -> new TripleKeyMapToList<>(HashMap.class, HashMap.class, null));
	}

	@Test
	public void testBadClassInConstructor()
	{
		assertThrows(IllegalArgumentException.class,
			() -> new TripleKeyMapToList<>(StrangeMap.class, HashMap.class, HashMap.class));
		assertThrows(IllegalArgumentException.class,
			() -> new TripleKeyMapToList<>(HashMap.class, StrangeMap.class, HashMap.class));
		assertThrows(IllegalArgumentException.class,
			() -> new TripleKeyMapToList<>(HashMap.class, HashMap.class, StrangeMap.class));
	}

	@Test
	public void testBadClassInConstructor2()
	{
		assertThrows(IllegalArgumentException.class,
			() -> new TripleKeyMapToList<>(NoPublicZeroArgConstructorMap.class,
				HashMap.class, HashMap.class));
		assertThrows(IllegalArgumentException.class,
			() -> new TripleKeyMapToList<>(HashMap.class,
				NoPublicZeroArgConstructorMap.class, HashMap.class));
		assertThrows(IllegalArgumentException.class,
			() -> new TripleKeyMapToList<>(HashMap.class, HashMap.class,
				NoPublicZeroArgConstructorMap.class));
	}

	@Test
	@SuppressWarnings("unused")
	public void testGoodConstructor()
	{
		new TripleKeyMapToList<>(HashMap.class, HashMap.class,
			IdentityHashMap.class);
	}

	@Test
	public void testBadClassInConstructor3()
	{
		assertThrows(IllegalArgumentException.class,
			() -> new TripleKeyMapToList<>(NoZeroArgConstructorMap.class,
				HashMap.class, HashMap.class));
		assertThrows(IllegalArgumentException.class,
			() -> new TripleKeyMapToList<>(HashMap.class,
				NoZeroArgConstructorMap.class, HashMap.class));
		assertThrows(IllegalArgumentException.class,
			() -> new TripleKeyMapToList<>(HashMap.class, HashMap.class,
				NoZeroArgConstructorMap.class));
	}

	@Test
	public void testGetTertiaryKeySet()
	{
		TripleKeyMapToList<Integer, Double, Long, Character> tkm =
				new TripleKeyMapToList<>();
		Set<Long> s = tkm.getTertiaryKeySet(TestSupport.I4, TestSupport.D2);
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
		Set<Long> s2 = tkm.getTertiaryKeySet(TestSupport.I4, TestSupport.D2);
		assertEquals(0, s2.size());
		assertEquals(sSize, s.size());
		// And ensure references are not kept the other direction to be altered
		// by changes in the underlying DoubleKeyMap
		populate(tkm);
		assertEquals(sSize, s.size());
		assertEquals(0, s2.size());
		Set<Long> s3 = tkm.getTertiaryKeySet(TestSupport.I1, TestSupport.D1);
		assertEquals(1, s3.size());
		assertTrue(s3.contains(TestSupport.L1));
		Set<Long> s4 = tkm.getTertiaryKeySet(TestSupport.I3, null);
		assertEquals(1, s4.size());
		assertTrue(s4.contains(TestSupport.L1));
		Set<Long> s5 = tkm.getTertiaryKeySet(null, TestSupport.D3);
		assertEquals(1, s5.size());
		assertTrue(s5.contains(TestSupport.L2));
		Set<Long> s6 = tkm.getTertiaryKeySet(TestSupport.I4, TestSupport.D4);
		assertEquals(1, s6.size());
		assertTrue(s6.contains(null));
	}

	//TODO Need detailed testing of methods if 3 argument constructor is used...
}
