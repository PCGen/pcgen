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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import pcgen.testsupport.TestSupport;

/**
 * Test GenericMapToList using a TreeMap as the underlying Map
 */
public class GenericMapToListTreeTest
{

	public void populate(GenericMapToList<Integer, Character> dkm)
	{
		dkm.addToListFor(TestSupport.I1, TestSupport.CONST_A);
		dkm.addToListFor(TestSupport.I1, TestSupport.CONST_B);
		dkm.addToListFor(TestSupport.I1, TestSupport.CONST_C);
		dkm.addToListFor(TestSupport.I2, TestSupport.CONST_D);
		dkm.addToListFor(TestSupport.I2, TestSupport.CONST_E);
		dkm.addToListFor(TestSupport.I2, null);
		dkm.addToListFor(TestSupport.I5, null);
	}

	@Test
	public void testInitializeListFor()
	{
		GenericMapToList<Integer, Character> dkm =
				GenericMapToList.getMapToList(TreeMap.class);
		assertNull(dkm.getListFor(TestSupport.I1));
		dkm.initializeListFor(TestSupport.I1);
		List<Character> l = dkm.getListFor(TestSupport.I1);
		assertEquals(0, l.size());
		assertThrows(IllegalArgumentException.class, () -> dkm.initializeListFor(TestSupport.I1));
	}

	@Test
	public void testPutNull()
	{
		GenericMapToList<Integer, Character> dkm =
				GenericMapToList.getMapToList(TreeMap.class);
		assertThrows(NullPointerException.class, () -> dkm.addToListFor(null, TestSupport.CONST_F));
	}

	@Test
	public void testPutGet()
	{
		GenericMapToList<Integer, Character> dkm =
				GenericMapToList.getMapToList(TreeMap.class);
		assertNull(dkm.getListFor(TestSupport.I1));
		populate(dkm);
		List<Character> l = dkm.getListFor(TestSupport.I1);
		assertEquals(3, l.size());
		assertTrue(l.contains(TestSupport.CONST_A));
		assertTrue(l.contains(TestSupport.CONST_B));
		assertTrue(l.contains(TestSupport.CONST_C));
		dkm.addToListFor(TestSupport.I1, TestSupport.CONST_C);
		l = dkm.getListFor(TestSupport.I1);
		assertEquals(4, l.size());
		assertTrue(l.contains(TestSupport.CONST_A));
		assertTrue(l.contains(TestSupport.CONST_B));
		assertTrue(l.contains(TestSupport.CONST_C));
		// two of them
		l.remove(TestSupport.CONST_C);
		assertTrue(l.contains(TestSupport.CONST_C));
		l = dkm.getListFor(TestSupport.I2);
		assertEquals(3, l.size());
		assertTrue(l.contains(TestSupport.CONST_D));
		assertTrue(l.contains(TestSupport.CONST_E));
		assertTrue(l.contains(null));
		dkm.addToListFor(TestSupport.I2, null);
		l = dkm.getListFor(TestSupport.I2);
		assertEquals(4, l.size());
		assertTrue(l.contains(TestSupport.CONST_D));
		assertTrue(l.contains(TestSupport.CONST_E));
		assertTrue(l.contains(null));
		// Two of them.
		l.remove(null);
		assertTrue(l.contains(null));
		assertNull(dkm.getListFor(TestSupport.I4));
		l = dkm.getListFor(TestSupport.I5);
		assertEquals(1, l.size());
		assertTrue(l.contains(null));
		l.add(TestSupport.CONST_A);
		List<Character> l2 = dkm.getListFor(TestSupport.I5);
		assertEquals(1, l2.size());
		assertTrue(l2.contains(null));
		assertEquals(2, l.size());
		assertTrue(l.contains(null));
		assertTrue(l.contains(TestSupport.CONST_A));
		dkm.clear();
		assertEquals(1, l2.size());
		assertTrue(l2.contains(null));
		assertEquals(2, l.size());
		assertTrue(l.contains(null));
		assertTrue(l.contains(TestSupport.CONST_A));
		l2.clear();
		assertEquals(0, l2.size());
		assertEquals(2, l.size());
		assertTrue(l.contains(null));
		assertTrue(l.contains(TestSupport.CONST_A));
	}

	@Test
	public void testContainsKey()
	{
		GenericMapToList<Integer, Character> dkm =
				GenericMapToList.getMapToList(TreeMap.class);
		assertFalse(dkm.containsListFor(TestSupport.I1));
		populate(dkm);
		assertTrue(dkm.containsListFor(TestSupport.I1));
		// Keys are .equals items, not instance
		assertTrue(dkm.containsListFor(new Integer(1)));
		assertTrue(dkm.containsListFor(TestSupport.I2));
		assertTrue(dkm.containsListFor(TestSupport.I5));
		assertFalse(dkm.containsListFor(Integer.valueOf(-4)));
	}

	@Test
	public void testRemoveListFor()
	{
		GenericMapToList<Integer, Character> dkm =
				GenericMapToList.getMapToList(TreeMap.class);
		assertNull(dkm.removeListFor(TestSupport.I1));
		populate(dkm);
		List<Character> l = dkm.removeListFor(TestSupport.I1);
		assertEquals(3, l.size());
		assertTrue(l.contains(TestSupport.CONST_A));
		assertTrue(l.contains(TestSupport.CONST_B));
		assertTrue(l.contains(TestSupport.CONST_C));
		assertFalse(dkm.containsListFor(TestSupport.I1));
		assertNull(dkm.getListFor(TestSupport.I1));
		l = dkm.removeListFor(TestSupport.I2);
		assertEquals(3, l.size());
		assertTrue(l.contains(TestSupport.CONST_D));
		assertTrue(l.contains(TestSupport.CONST_E));
		assertTrue(l.contains(null));
	}

	@Test
	public void testRemoveFromListFor()
	{
		GenericMapToList<Integer, Character> dkm =
				GenericMapToList.getMapToList(TreeMap.class);
		assertFalse(dkm.removeFromListFor(TestSupport.I1, TestSupport.CONST_D));
		populate(dkm);
		assertTrue(dkm.removeFromListFor(TestSupport.I1, TestSupport.CONST_A));
		assertTrue(dkm.containsListFor(TestSupport.I1));
		// Keys are .equals items, not instance
		assertTrue(dkm.containsListFor(new Integer(1)));
		assertEquals(2, dkm.sizeOfListFor(TestSupport.I1));
		assertFalse(dkm.removeFromListFor(TestSupport.I1, TestSupport.CONST_A));
		assertTrue(dkm.removeFromListFor(TestSupport.I1, TestSupport.CONST_B));
		assertEquals(1, dkm.sizeOfListFor(TestSupport.I1));
		assertTrue(dkm.containsListFor(TestSupport.I1));
		assertFalse(dkm.removeFromListFor(TestSupport.I1, TestSupport.CONST_A));
		assertTrue(dkm.removeFromListFor(TestSupport.I1, TestSupport.CONST_C));
		assertEquals(0, dkm.sizeOfListFor(TestSupport.I1));
		assertFalse(dkm.containsListFor(TestSupport.I1));

		// add a second :)
		dkm.addToListFor(TestSupport.I2, TestSupport.CONST_D);
		assertFalse(dkm.removeFromListFor(TestSupport.I2, TestSupport.CONST_A));
		assertTrue(dkm.containsListFor(TestSupport.I2));
		assertEquals(4, dkm.sizeOfListFor(TestSupport.I2));
		assertFalse(dkm.removeFromListFor(TestSupport.I2, TestSupport.CONST_A));
		assertTrue(dkm.removeFromListFor(TestSupport.I2, TestSupport.CONST_D));
		assertEquals(3, dkm.sizeOfListFor(TestSupport.I2));
		assertTrue(dkm.containsListFor(TestSupport.I2));
		assertTrue(dkm.removeFromListFor(TestSupport.I2, TestSupport.CONST_E));
		assertEquals(2, dkm.sizeOfListFor(TestSupport.I2));
		assertTrue(dkm.containsListFor(TestSupport.I2));
		assertTrue(dkm.removeFromListFor(TestSupport.I2, null));
		assertEquals(1, dkm.sizeOfListFor(TestSupport.I2));
		assertTrue(dkm.containsListFor(TestSupport.I2));
		assertTrue(dkm.removeFromListFor(TestSupport.I2, TestSupport.CONST_D));
		assertEquals(0, dkm.sizeOfListFor(TestSupport.I2));
		assertFalse(dkm.containsListFor(TestSupport.I2));
	}

	@Test
	public void testContainsInList()
	{
		GenericMapToList<Integer, Character> dkm =
				GenericMapToList.getMapToList(TreeMap.class);
		assertFalse(dkm.containsInList(TestSupport.I1, TestSupport.CONST_D));
		populate(dkm);
		assertTrue(dkm.containsInList(TestSupport.I1, TestSupport.CONST_A));
		// Keys are .equals items, not instance
		assertTrue(dkm.containsInList(new Integer(1), TestSupport.CONST_A));
		assertTrue(dkm.containsInList(TestSupport.I1, TestSupport.CONST_B));
		assertTrue(dkm.containsInList(TestSupport.I1, TestSupport.CONST_C));
		assertFalse(dkm.containsInList(TestSupport.I1, TestSupport.CONST_D));

		// add a second :)
		dkm.addToListFor(TestSupport.I1, TestSupport.CONST_C);
		assertTrue(dkm.containsInList(TestSupport.I1, TestSupport.CONST_C));

		// Test null stuff :)
		assertTrue(dkm.containsInList(TestSupport.I2, null));
	}

	@Test
	public void testGetKeySet()
	{
		GenericMapToList<Integer, Character> dkm =
				GenericMapToList.getMapToList(TreeMap.class);
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
		assertEquals(3, s3.size());
		assertTrue(s3.contains(TestSupport.I1));
		assertTrue(s3.contains(TestSupport.I2));
		assertTrue(s3.contains(TestSupport.I5));
	}

	@Test
	public void testClearIsEmpty()
	{
		GenericMapToList<Integer, Character> dkm =
				GenericMapToList.getMapToList(TreeMap.class);
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.size());
		populate(dkm);
		assertFalse(dkm.isEmpty());
		assertEquals(3, dkm.size());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.size());
		dkm.addToListFor(TestSupport.I3, 'G');
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.size());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.size());
		dkm.addToListFor(TestSupport.I5, null);
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.size());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.size());
	}

	@Test
	public void testEmptyAddAll()
	{
		GenericMapToList<Integer, Character> dkm =
				GenericMapToList.getMapToList(TreeMap.class);
		dkm.addAllToListFor(TestSupport.I1, null);
		assertFalse(dkm.containsListFor(TestSupport.I1));
		dkm.addAllToListFor(TestSupport.I1, new ArrayList<>());
		assertFalse(dkm.containsListFor(TestSupport.I1));
	}

	@Test
	public void testAddAll()
	{
		GenericMapToList<Integer, Character> dkm =
				GenericMapToList.getMapToList(TreeMap.class);
		List<Character> l = new ArrayList<>();
		l.add(TestSupport.CONST_A);
		l.add(null);
		l.add(TestSupport.CONST_A);
		l.add(TestSupport.CONST_B);
		dkm.addAllToListFor(TestSupport.I1, l);
		assertTrue(dkm.containsListFor(TestSupport.I1));
		assertEquals(4, dkm.sizeOfListFor(TestSupport.I1));
		dkm.addToListFor(TestSupport.I1, TestSupport.CONST_D);
		assertEquals(4, l.size());
		// Check reference semantics!
		l.add(TestSupport.CONST_C);
		l.add(TestSupport.CONST_E);
		assertTrue(dkm.containsListFor(TestSupport.I1));
		assertEquals(5, dkm.sizeOfListFor(TestSupport.I1));
		l.clear();
		assertTrue(dkm.containsListFor(TestSupport.I1));
		assertEquals(5, dkm.sizeOfListFor(TestSupport.I1));
	}

	@Test
	public void testInstanceBehavior()
	{
		GenericMapToList<Integer, Character> dkm =
				GenericMapToList.getMapToList(TreeMap.class);
		Character ca = TestSupport.CONST_A;
		Character cb = TestSupport.CONST_B;
		Character cc = TestSupport.CONST_C;
		Character ca1 = new Character(TestSupport.CONST_A.charValue());
		dkm.addToListFor(TestSupport.I1, ca);
		dkm.addToListFor(TestSupport.I1, cb);
		dkm.addToListFor(TestSupport.I1, cc);
		dkm.addToListFor(TestSupport.I2, ca);
		dkm.addToListFor(TestSupport.I2, ca);
		dkm.addToListFor(TestSupport.I3, cb);
		dkm.addToListFor(TestSupport.I3, cc);
		assertTrue(dkm.containsInList(TestSupport.I1, ca));
		assertTrue(dkm.containsInList(TestSupport.I1, ca1));
		assertTrue(dkm.removeFromListFor(TestSupport.I1, ca1));
		assertFalse(dkm.containsInList(TestSupport.I1, ca));

		assertTrue(dkm.containsInList(TestSupport.I2, ca));
		assertTrue(dkm.containsInList(TestSupport.I2, ca1));
		assertTrue(dkm.removeFromListFor(TestSupport.I2, ca1));
		// There were two
		assertTrue(dkm.containsInList(TestSupport.I2, ca));
		assertTrue(dkm.removeFromListFor(TestSupport.I2, ca));
		// There were two
		assertFalse(dkm.containsInList(TestSupport.I2, ca));
	}

	@Test
	public void testAddAllLists()
	{
		GenericMapToList<Integer, Character> dkm =
				GenericMapToList.getMapToList(TreeMap.class);
		HashMapToList<Integer, Character> dkm2 =
				new HashMapToList<>();
		populate(dkm);
		dkm2.addAllLists(dkm);
		assertTrue(dkm.removeFromListFor(TestSupport.I1, TestSupport.CONST_A));
		assertTrue(dkm2.containsInList(TestSupport.I1, TestSupport.CONST_A));

		assertTrue(dkm2.removeFromListFor(TestSupport.I1, TestSupport.CONST_B));
		assertTrue(dkm.containsInList(TestSupport.I1, TestSupport.CONST_B));

		dkm.removeListFor(TestSupport.I1);
		assertFalse(dkm.containsListFor(TestSupport.I1));
		assertTrue(dkm2.containsListFor(TestSupport.I1));
	}

	// TODO Need to test iterator order
}
