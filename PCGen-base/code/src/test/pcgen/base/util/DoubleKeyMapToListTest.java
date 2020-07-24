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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import pcgen.testsupport.NoPublicZeroArgConstructorMap;
import pcgen.testsupport.NoZeroArgConstructorMap;
import pcgen.testsupport.StrangeMap;
import pcgen.testsupport.TestSupport;

public class DoubleKeyMapToListTest
{

	public void populate(DoubleKeyMapToList<Integer, Double, Character> dkm)
	{
		dkm.addToListFor(TestSupport.I1, TestSupport.D1, TestSupport.CONST_A);
		dkm.addToListFor(TestSupport.I1, TestSupport.D1, TestSupport.CONST_B);
		dkm.addToListFor(TestSupport.I1, TestSupport.D2, TestSupport.CONST_C);
		dkm.addToListFor(TestSupport.I2, TestSupport.D1, TestSupport.CONST_D);
		dkm.addToListFor(TestSupport.I2, TestSupport.D2, TestSupport.CONST_E);
		dkm.addToListFor(TestSupport.I2, TestSupport.D2, null);
		dkm.addToListFor(null, TestSupport.D3, TestSupport.CONST_F);
		dkm.addToListFor(TestSupport.I3, null, TestSupport.CONST_G);
		dkm.addToListFor(TestSupport.I5, TestSupport.D6, null);
	}

	@Test
	@SuppressWarnings("unused")
	public void testGoodConstructor()
	{
		new DoubleKeyMapToList<>(HashMap.class, IdentityHashMap.class);
	}

	@Test
	public void testNullInConstructor()
	{
		assertThrows(NullPointerException.class, () -> new DoubleKeyMapToList<>(null, HashMap.class));
		assertThrows(NullPointerException.class, () -> new DoubleKeyMapToList<>(HashMap.class, null));
	}

	@Test
	public void testBadClassInConstructor()
	{
		assertThrows(IllegalArgumentException.class, () -> new DoubleKeyMapToList<>(StrangeMap.class, HashMap.class));
		assertThrows(IllegalArgumentException.class, () -> new DoubleKeyMapToList<>(HashMap.class, StrangeMap.class));
	}

	@Test
	public void testBadClassInConstructor2()
	{
		assertThrows(IllegalArgumentException.class, () -> new DoubleKeyMapToList<>(NoPublicZeroArgConstructorMap.class, HashMap.class));
		assertThrows(IllegalArgumentException.class, () -> new DoubleKeyMapToList<>(HashMap.class, NoPublicZeroArgConstructorMap.class));
	}

	@Test
	public void testBadClassInConstructor3()
	{
		assertThrows(IllegalArgumentException.class, () -> new DoubleKeyMapToList<>(NoZeroArgConstructorMap.class, HashMap.class));
		assertThrows(IllegalArgumentException.class, () -> new DoubleKeyMapToList<>(HashMap.class, NoZeroArgConstructorMap.class));
	}

	@Test
	public void testPutGet()
	{
		DoubleKeyMapToList<Integer, Double, Character> dkm =
				new DoubleKeyMapToList<>();
		assertNull(dkm.getListFor(TestSupport.I1, TestSupport.D0));
		populate(dkm);
		List<Character> l =
				dkm.getListFor(TestSupport.I1, TestSupport.D1);
		assertEquals(2, l.size());
		assertTrue(l.contains(TestSupport.CONST_A));
		assertTrue(l.contains(TestSupport.CONST_B));
		assertTrue(dkm.containsListFor(TestSupport.I1));
		l = dkm.getListFor(TestSupport.I1, TestSupport.D2);
		assertEquals(1, l.size());
		assertTrue(l.contains(TestSupport.CONST_C));
		dkm.addToListFor(TestSupport.I1, TestSupport.D2, TestSupport.CONST_C);
		l = dkm.getListFor(TestSupport.I1, TestSupport.D2);
		assertEquals(2, l.size());
		assertTrue(l.contains(TestSupport.CONST_C));
		// two of them
		l.remove(Character.valueOf(TestSupport.CONST_C));
		assertTrue(l.contains(TestSupport.CONST_C));
		l = dkm.getListFor(TestSupport.I2, TestSupport.D2);
		assertEquals(2, l.size());
		assertTrue(l.contains(TestSupport.CONST_E));
		assertTrue(l.contains(null));
		l.remove(Character.valueOf(TestSupport.CONST_E));
		List<Character> l2 =
				dkm.getListFor(TestSupport.I2, TestSupport.D2);
		assertEquals(2, l2.size());
		assertTrue(l2.contains(TestSupport.CONST_E));
		assertTrue(l2.contains(null));
		assertEquals(1, l.size());
		assertTrue(l.contains(null));
		dkm.addToListFor(TestSupport.I2, TestSupport.D2, null);
		l = dkm.getListFor(TestSupport.I2, TestSupport.D2);
		assertEquals(3, l.size());
		assertTrue(l.contains(TestSupport.CONST_E));
		assertTrue(l.contains(null));
		// Two of them.
		l.remove(null);
		assertTrue(l.contains(null));
		assertNull(dkm.getListFor(TestSupport.I1, TestSupport.D0));
		assertNull(dkm.getListFor(TestSupport.I2, TestSupport.D3));
		assertNull(dkm.getListFor(TestSupport.I4, TestSupport.D0));
		assertNull(dkm.getListFor(TestSupport.I1, null));
		assertNull(dkm.getListFor(null, TestSupport.D1));
		dkm.clear();
		assertEquals(2, l2.size());
		assertTrue(l2.contains(TestSupport.CONST_E));
		assertTrue(l2.contains(null));
	}

	@Test
	public void testContainsKey()
	{
		DoubleKeyMapToList<Integer, Double, Character> dkm =
				new DoubleKeyMapToList<>();
		populate(dkm);
		assertTrue(dkm.containsListFor(TestSupport.I1, TestSupport.D1));
		assertTrue(dkm.containsListFor(TestSupport.I1, TestSupport.D2));
		assertTrue(dkm.containsListFor(TestSupport.I2, TestSupport.D1));
		assertTrue(dkm.containsListFor(TestSupport.I2, TestSupport.D2));
		assertFalse(dkm.containsListFor(TestSupport.I2, TestSupport.D3));
		assertFalse(dkm.containsListFor(Integer.valueOf(-4), TestSupport.D0));
		assertFalse(dkm.containsListFor(TestSupport.I1, null));
		assertFalse(dkm.containsListFor(null, TestSupport.D1));
		assertTrue(dkm.containsListFor(null, TestSupport.D3));
		assertTrue(dkm.containsListFor(TestSupport.I3, null));
	}

	@Test
	public void testRemoveListFor()
	{
		DoubleKeyMapToList<Integer, Double, Character> dkm =
				new DoubleKeyMapToList<>();
		assertNull(dkm.removeListFor(TestSupport.I1, TestSupport.D1));
		populate(dkm);
		assertTrue(dkm.containsListFor(TestSupport.I1));
		List<Character> l =
				dkm.removeListFor(TestSupport.I1, TestSupport.D1);
		assertTrue(dkm.containsListFor(TestSupport.I1));
		assertEquals(2, l.size());
		assertTrue(l.contains(TestSupport.CONST_A));
		assertTrue(l.contains(TestSupport.CONST_B));
		assertFalse(dkm.containsListFor(TestSupport.I1, TestSupport.D1));
		assertNull(dkm.getListFor(TestSupport.I1, TestSupport.D1));
		l = dkm.removeListFor(TestSupport.I1, TestSupport.D2);
		assertFalse(dkm.containsListFor(TestSupport.I1));
		assertEquals(1, l.size());
		assertTrue(l.contains(TestSupport.CONST_C));
		dkm.addToListFor(TestSupport.I1, TestSupport.D2, TestSupport.CONST_C);
		l = dkm.removeListFor(TestSupport.I1, TestSupport.D2);
		assertEquals(1, l.size());
		assertTrue(l.contains(TestSupport.CONST_C));
		l = dkm.removeListFor(TestSupport.I2, TestSupport.D2);
		assertEquals(2, l.size());
		assertTrue(l.contains(TestSupport.CONST_E));
		assertTrue(l.contains(null));
		assertNull(dkm.removeListFor(TestSupport.I2, TestSupport.D2));
		assertNull(dkm.getListFor(TestSupport.I1, TestSupport.D0));
		assertNull(dkm.getListFor(TestSupport.I2, TestSupport.D3));
		assertNull(dkm.getListFor(TestSupport.I4, TestSupport.D0));
		assertNull(dkm.getListFor(TestSupport.I1, null));
		assertNull(dkm.getListFor(null, TestSupport.D1));
	}

	@Test
	public void testRemoveListsFor()
	{
		DoubleKeyMapToList<Integer, Double, Character> dkm =
				new DoubleKeyMapToList<>();
		assertNull(dkm.removeListsFor(TestSupport.I1));
		populate(dkm);
		assertTrue(dkm.containsListFor(TestSupport.I1));
		MapToList<Double, Character> mtl =
				dkm.removeListsFor(TestSupport.I1);
		assertFalse(dkm.containsListFor(TestSupport.I1));
		assertFalse(dkm.containsListFor(TestSupport.I1, TestSupport.D1));
		assertFalse(dkm.containsListFor(TestSupport.I1, TestSupport.D2));
		Set<Double> keys = mtl.getKeySet();
		assertEquals(2, keys.size());
		assertTrue(keys.contains(TestSupport.D1));
		assertTrue(keys.contains(TestSupport.D2));
		List<Character> list = mtl.getListFor(TestSupport.D1);
		assertEquals(2, list.size());
		assertTrue(list.contains(TestSupport.CONST_A));
		assertTrue(list.contains(TestSupport.CONST_B));
		list = mtl.getListFor(TestSupport.D2);
		assertEquals(1, list.size());
		assertTrue(list.contains(TestSupport.CONST_C));
	}

	@Test
	public void testRemoveFromListFor()
	{
		DoubleKeyMapToList<Integer, Double, Character> dkm =
				new DoubleKeyMapToList<>();
		assertFalse(dkm.removeFromListFor(TestSupport.I1,
			TestSupport.D1, TestSupport.CONST_D));
		populate(dkm);
		assertTrue(dkm.removeFromListFor(TestSupport.I1, TestSupport.D1,
			TestSupport.CONST_A));
		assertTrue(dkm.containsListFor(TestSupport.I1));
		assertTrue(dkm.containsListFor(TestSupport.I1, TestSupport.D1));
		assertEquals(1, dkm
			.sizeOfListFor(TestSupport.I1, TestSupport.D1));
		assertFalse(dkm.removeFromListFor(TestSupport.I1,
			TestSupport.D1, TestSupport.CONST_A));
		assertTrue(dkm.removeFromListFor(TestSupport.I1, TestSupport.D1,
			TestSupport.CONST_B));
		assertEquals(0, dkm
			.sizeOfListFor(TestSupport.I1, TestSupport.D1));
		assertFalse(dkm.containsListFor(TestSupport.I1, TestSupport.D1));
		assertTrue(dkm.containsListFor(TestSupport.I1));

		// add a second :)
		dkm.addToListFor(TestSupport.I1, TestSupport.D2, TestSupport.CONST_C);
		assertFalse(dkm.removeFromListFor(TestSupport.I1,
			TestSupport.D2, TestSupport.CONST_A));
		assertTrue(dkm.containsListFor(TestSupport.I1, TestSupport.D2));
		assertEquals(2, dkm
			.sizeOfListFor(TestSupport.I1, TestSupport.D2));
		assertFalse(dkm.removeFromListFor(TestSupport.I1,
			TestSupport.D2, TestSupport.CONST_A));
		assertTrue(dkm.removeFromListFor(TestSupport.I1, TestSupport.D2,
			TestSupport.CONST_C));
		assertEquals(1, dkm
			.sizeOfListFor(TestSupport.I1, TestSupport.D2));
		assertTrue(dkm.containsListFor(TestSupport.I1, TestSupport.D2));
		assertTrue(dkm.containsListFor(TestSupport.I1));
		assertTrue(dkm.removeFromListFor(TestSupport.I1, TestSupport.D2,
			TestSupport.CONST_C));
		assertEquals(0, dkm
			.sizeOfListFor(TestSupport.I1, TestSupport.D2));
		assertFalse(dkm.containsListFor(TestSupport.I1, TestSupport.D2));
		assertFalse(dkm.containsListFor(TestSupport.I1));

		// Test null stuff :)
		assertFalse(dkm.removeFromListFor(null, TestSupport.D3, TestSupport.CONST_A));
		assertTrue(dkm.containsListFor(null, TestSupport.D3));
		assertEquals(1, dkm.sizeOfListFor(null, TestSupport.D3));
		assertFalse(dkm.removeFromListFor(null, TestSupport.D3, TestSupport.CONST_A));
		assertTrue(dkm.removeFromListFor(null, TestSupport.D3, TestSupport.CONST_F));
		assertEquals(0, dkm.sizeOfListFor(null, TestSupport.D3));
		assertFalse(dkm.containsListFor(null, TestSupport.D3));

		assertFalse(dkm.removeFromListFor(TestSupport.I3, null, TestSupport.CONST_A));
		assertTrue(dkm.containsListFor(TestSupport.I3, null));
		assertEquals(1, dkm.sizeOfListFor(TestSupport.I3, null));
		assertFalse(dkm.removeFromListFor(TestSupport.I3, null, TestSupport.CONST_A));
		assertTrue(dkm.removeFromListFor(TestSupport.I3, null, TestSupport.CONST_G));
		assertEquals(0, dkm.sizeOfListFor(TestSupport.I3, null));
		assertFalse(dkm.containsListFor(TestSupport.I3, null));
	}

	@Test
	public void testContainsInList()
	{
		DoubleKeyMapToList<Integer, Double, Character> dkm =
				new DoubleKeyMapToList<>();
		assertFalse(dkm.containsInList(TestSupport.I1, TestSupport.D1,
			TestSupport.CONST_D));
		populate(dkm);
		assertFalse(dkm.containsInList(TestSupport.I1, Double.valueOf(16),
			TestSupport.CONST_D));
		assertTrue(dkm.containsInList(TestSupport.I1, TestSupport.D1,
			TestSupport.CONST_A));
		assertTrue(dkm.containsInList(TestSupport.I1, TestSupport.D1,
			TestSupport.CONST_B));

		// add a second :)
		dkm.addToListFor(TestSupport.I1, TestSupport.D2, TestSupport.CONST_C);
		assertFalse(dkm.containsInList(TestSupport.I1, TestSupport.D2,
			TestSupport.CONST_A));
		assertTrue(dkm.containsInList(TestSupport.I1, TestSupport.D2,
			TestSupport.CONST_C));

		// Test null stuff :)
		assertTrue(dkm.containsInList(TestSupport.I2, TestSupport.D2,
			null));

		assertFalse(dkm.containsInList(null, TestSupport.D3, TestSupport.CONST_A));
		assertTrue(dkm.containsInList(null, TestSupport.D3, TestSupport.CONST_F));

		assertFalse(dkm.containsInList(TestSupport.I3, null, TestSupport.CONST_A));
		assertTrue(dkm.containsInList(TestSupport.I3, null, TestSupport.CONST_G));
	}

	@Test
	public void testGetKeySet()
	{
		DoubleKeyMapToList<Integer, Double, Character> dkm =
				new DoubleKeyMapToList<>();
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
		DoubleKeyMapToList<Integer, Double, Character> dkm =
				new DoubleKeyMapToList<>();
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
		assertEquals(2, s3.size());
		assertTrue(s3.contains(TestSupport.D1));
		assertTrue(s3.contains(TestSupport.D2));
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
		DoubleKeyMapToList<Integer, Double, Character> dkm =
				new DoubleKeyMapToList<>();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.firstKeyCount());
		populate(dkm);
		assertFalse(dkm.isEmpty());
		assertEquals(5, dkm.firstKeyCount());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.firstKeyCount());
		dkm.addToListFor(null, TestSupport.D3, 'F');
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.firstKeyCount());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.firstKeyCount());
		dkm.addToListFor(TestSupport.I3, null, 'G');
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.firstKeyCount());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.firstKeyCount());
		dkm.addToListFor(TestSupport.I5, TestSupport.D6, null);
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.firstKeyCount());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.firstKeyCount());
	}

	@Test
	public void testInstanceBehavior()
	{
		DoubleKeyMapToList<Integer, Double, Character> dkm =
				new DoubleKeyMapToList<>();
		Character ca = TestSupport.CONST_A;
		Character cb = TestSupport.CONST_B;
		Character cc = TestSupport.CONST_C;
		Character ca1 = new Character(TestSupport.CONST_A.charValue());
		dkm.addToListFor(TestSupport.I1, TestSupport.D1, ca);
		dkm.addToListFor(TestSupport.I1, TestSupport.D1, cb);
		dkm.addToListFor(TestSupport.I1, TestSupport.D2, cc);
		dkm.addToListFor(TestSupport.I2, TestSupport.D1, ca);
		dkm.addToListFor(TestSupport.I2, TestSupport.D1, ca);
		dkm.addToListFor(TestSupport.I3, TestSupport.D2, cb);
		dkm.addToListFor(TestSupport.I3, TestSupport.D2, cc);
		assertTrue(dkm.containsInList(TestSupport.I1, TestSupport.D1, ca));
		assertTrue(dkm.containsInList(TestSupport.I1, TestSupport.D1, ca1));
		assertTrue(dkm.removeFromListFor(TestSupport.I1, TestSupport.D1, ca1));
		assertFalse(dkm.containsInList(TestSupport.I1, TestSupport.D1, ca));

		assertTrue(dkm.containsInList(TestSupport.I2, TestSupport.D1, ca));
		assertTrue(dkm.containsInList(TestSupport.I2, TestSupport.D1, ca1));
		assertTrue(dkm.removeFromListFor(TestSupport.I2, TestSupport.D1, ca1));
		assertTrue(dkm.containsInList(TestSupport.I2, TestSupport.D1, ca));
		assertTrue(dkm.removeFromListFor(TestSupport.I2, TestSupport.D1, ca));
		// There were two
		assertFalse(dkm.containsInList(TestSupport.I2, TestSupport.D1, ca));
	}

	@Test
	public void testAddAllToListFor()
	{
		DoubleKeyMapToList<Integer, Double, Character> dkm =
				new DoubleKeyMapToList<>();
		Integer i1 = TestSupport.I1;
		Double d1 = TestSupport.D1;
		List<Character> l = new ArrayList<>();
		l.add(TestSupport.CONST_A);
		l.add(null);
		l.add(TestSupport.CONST_A);
		l.add(TestSupport.CONST_B);
		dkm.addAllToListFor(i1, d1, l);
		assertTrue(dkm.containsListFor(i1));
		assertTrue(dkm.containsListFor(i1, d1));
		assertEquals(4, dkm.sizeOfListFor(i1, d1));
		dkm.addToListFor(i1, d1, TestSupport.CONST_D);
		assertEquals(4, l.size());
		// Check reference semantics!
		l.add(TestSupport.CONST_C);
		l.add(TestSupport.CONST_E);
		assertTrue(dkm.containsListFor(i1, d1));
		assertEquals(5, dkm.sizeOfListFor(i1, d1));
		l.clear();
		assertTrue(dkm.containsListFor(i1, d1));
		assertEquals(5, dkm.sizeOfListFor(i1, d1));
	}

	@Test
	public void testAddAll()
	{
		DoubleKeyMapToList<Integer, Double, Character> dkm =
				new DoubleKeyMapToList<>();
		DoubleKeyMapToList<Integer, Double, Character> copy =
				new DoubleKeyMapToList<>();
		assertThrows(NullPointerException.class, () -> copy.addAll(null));
		populate(dkm);
		copy.addAll(dkm);
		Integer i1 = TestSupport.I1;
		Double d1 = TestSupport.D1;
		// test independence
		dkm.addToListFor(i1, d1, TestSupport.CONST_D);
		assertEquals(2, copy.sizeOfListFor(i1, d1));
		List<Character> l = copy.getListFor(i1, d1);
		assertEquals(2, l.size());
		assertTrue(l.contains(TestSupport.CONST_A));
		assertTrue(l.contains(TestSupport.CONST_B));
		copy.addToListFor(i1, d1, TestSupport.CONST_E);
		l = dkm.getListFor(i1, d1);
		assertEquals(3, l.size());
		assertTrue(l.contains(TestSupport.CONST_A));
		assertTrue(l.contains(TestSupport.CONST_B));
		assertTrue(l.contains(TestSupport.CONST_D));
	}

	@Test
	public void testClone()
	{
		DoubleKeyMapToList<Integer, Double, Character> dkm =
				new DoubleKeyMapToList<>();
		populate(dkm);
		DoubleKeyMapToList<Integer, Double, Character> copy;
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
		Double d1 = TestSupport.D1;
		// test independence
		dkm.addToListFor(i1, d1, TestSupport.CONST_D);
		assertEquals(2, copy.sizeOfListFor(i1, d1));
		List<Character> l = copy.getListFor(i1, d1);
		assertEquals(2, l.size());
		assertTrue(l.contains(TestSupport.CONST_A));
		assertTrue(l.contains(TestSupport.CONST_B));
		copy.addToListFor(i1, d1, TestSupport.CONST_E);
		l = dkm.getListFor(i1, d1);
		assertEquals(3, l.size());
		assertTrue(l.contains(TestSupport.CONST_A));
		assertTrue(l.contains(TestSupport.CONST_B));
		assertTrue(l.contains(TestSupport.CONST_D));
	}

	//TODO Need detailed testing of methods if 2 argument constructor is used...
}
