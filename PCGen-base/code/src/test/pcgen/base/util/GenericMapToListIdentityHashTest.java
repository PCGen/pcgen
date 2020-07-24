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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import pcgen.testsupport.TestSupport;

/**
 * Test GenericMapToList using an IdentityHashMap as the underlying Map
 */
public class GenericMapToListIdentityHashTest
{

	private static final Integer CONST_1A = new Integer(1);
	private static final Integer CONST_1B = new Integer(1);

	public void populate(GenericMapToList<Integer, Character> dkm)
	{
		dkm.addToListFor(CONST_1A, TestSupport.CONST_A);
		dkm.addToListFor(CONST_1B, TestSupport.CONST_B);
		dkm.addToListFor(CONST_1B, TestSupport.CONST_C);
		dkm.addToListFor(TestSupport.I2, TestSupport.CONST_D);
		dkm.addToListFor(TestSupport.I2, TestSupport.CONST_E);
		dkm.addToListFor(TestSupport.I2, null);
		dkm.addToListFor(null, TestSupport.CONST_F);
		dkm.addToListFor(TestSupport.I5, null);
	}

	@Test
	public void testInitializeListFor()
	{
		GenericMapToList<Integer, Character> dkm =
				GenericMapToList.getMapToList(IdentityHashMap.class);
		assertNull(dkm.getListFor(CONST_1A));
		assertNull(dkm.getListFor(CONST_1B));
		dkm.initializeListFor(CONST_1A);
		List<Character> l = dkm.getListFor(CONST_1A);
		assertEquals(0, l.size());
		assertNull(dkm.getListFor(CONST_1B));
		dkm.initializeListFor(CONST_1B);
		l = dkm.getListFor(CONST_1B);
		assertEquals(0, l.size());
		assertThrows(IllegalArgumentException.class, () -> dkm.initializeListFor(CONST_1A));
	}

	@Test
	public void testPutGet()
	{
		GenericMapToList<Integer, Character> dkm =
				GenericMapToList.getMapToList(IdentityHashMap.class);
		assertNull(dkm.getListFor(null));
		assertNull(dkm.getListFor(CONST_1A));
		populate(dkm);
		List<Character> l = dkm.getListFor(CONST_1A);
		assertEquals(1, l.size());
		assertTrue(l.contains(TestSupport.CONST_A));
		dkm.addToListFor(CONST_1B, TestSupport.CONST_C);
		l = dkm.getListFor(CONST_1B);
		assertEquals(3, l.size());
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
		assertNull(dkm.getListFor(Integer.valueOf(4)));
		l = dkm.getListFor(null);
		assertEquals(1, l.size());
		assertTrue(l.contains(TestSupport.CONST_F));
		l.add(TestSupport.CONST_A);
		List<Character> l2 = dkm.getListFor(null);
		assertEquals(1, l2.size());
		assertTrue(l2.contains(TestSupport.CONST_F));
		assertEquals(2, l.size());
		assertTrue(l.contains(TestSupport.CONST_F));
		assertTrue(l.contains(TestSupport.CONST_A));
		dkm.clear();
		assertEquals(1, l2.size());
		assertTrue(l2.contains(TestSupport.CONST_F));
		assertEquals(2, l.size());
		assertTrue(l.contains(TestSupport.CONST_F));
		assertTrue(l.contains(TestSupport.CONST_A));
		l2.clear();
		assertEquals(0, l2.size());
		assertEquals(2, l.size());
		assertTrue(l.contains(TestSupport.CONST_F));
		assertTrue(l.contains(TestSupport.CONST_A));
	}

	@Test
	public void testContainsKey()
	{
		GenericMapToList<Integer, Character> dkm =
				GenericMapToList.getMapToList(IdentityHashMap.class);
		assertFalse(dkm.containsListFor(CONST_1A));
		assertFalse(dkm.containsListFor(null));
		populate(dkm);
		assertTrue(dkm.containsListFor(CONST_1A));
		// Keys are .equals items, not instance
		assertFalse(dkm.containsListFor(new Integer(1)));
		assertTrue(dkm.containsListFor(TestSupport.I2));
		assertTrue(dkm.containsListFor(TestSupport.I5));
		assertFalse(dkm.containsListFor(Integer.valueOf(-4)));
		assertTrue(dkm.containsListFor(null));
	}

	@Test
	public void testRemoveListFor()
	{
		GenericMapToList<Integer, Character> dkm =
				GenericMapToList.getMapToList(IdentityHashMap.class);
		assertNull(dkm.removeListFor(CONST_1A));
		assertNull(dkm.removeListFor(null));
		populate(dkm);
		List<Character> l = dkm.removeListFor(CONST_1A);
		assertEquals(1, l.size());
		assertTrue(l.contains(TestSupport.CONST_A));
		assertFalse(dkm.containsListFor(CONST_1A));
		assertNull(dkm.getListFor(CONST_1A));
		l = dkm.removeListFor(TestSupport.I2);
		assertEquals(3, l.size());
		assertTrue(l.contains(TestSupport.CONST_D));
		assertTrue(l.contains(TestSupport.CONST_E));
		assertTrue(l.contains(null));
		l = dkm.removeListFor(null);
		assertEquals(1, l.size());
		assertTrue(l.contains(TestSupport.CONST_F));
	}

	@Test
	public void testRemoveFromListFor()
	{
		GenericMapToList<Integer, Character> dkm =
				GenericMapToList.getMapToList(IdentityHashMap.class);
		assertFalse(dkm.removeFromListFor(CONST_1B, TestSupport.CONST_D));
		populate(dkm);
		assertTrue(dkm.removeFromListFor(CONST_1B, TestSupport.CONST_B));
		assertTrue(dkm.containsListFor(CONST_1B));
		// Keys are instance
		assertFalse(dkm.containsListFor(new Integer(1)));
		assertEquals(1, dkm.sizeOfListFor(CONST_1B));
		assertFalse(dkm.removeFromListFor(CONST_1B, TestSupport.CONST_A));
		assertTrue(dkm.containsListFor(CONST_1B));
		assertFalse(dkm.removeFromListFor(CONST_1B, TestSupport.CONST_B));
		assertTrue(dkm.removeFromListFor(CONST_1B, TestSupport.CONST_C));
		assertEquals(0, dkm.sizeOfListFor(CONST_1B));
		assertFalse(dkm.containsListFor(CONST_1B));

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

		// Test null stuff :)
		assertFalse(dkm.removeFromListFor(null, TestSupport.CONST_A));
		assertTrue(dkm.containsListFor(null));
		assertEquals(1, dkm.sizeOfListFor(null));
		assertFalse(dkm.removeFromListFor(null, TestSupport.CONST_A));
		assertTrue(dkm.removeFromListFor(null, TestSupport.CONST_F));
		assertEquals(0, dkm.sizeOfListFor(null));
		assertFalse(dkm.containsListFor(null));
	}

	@Test
	public void testContainsInList()
	{
		GenericMapToList<Integer, Character> dkm =
				GenericMapToList.getMapToList(IdentityHashMap.class);
		assertFalse(dkm.containsInList(CONST_1B, TestSupport.CONST_D));
		populate(dkm);
		assertTrue(dkm.containsInList(CONST_1B, TestSupport.CONST_B));
		// Keys are instance
		assertFalse(dkm.containsInList(new Integer(1), TestSupport.CONST_B));
		assertTrue(dkm.containsInList(CONST_1B, TestSupport.CONST_B));
		assertTrue(dkm.containsInList(CONST_1B, TestSupport.CONST_C));
		assertFalse(dkm.containsInList(CONST_1B, TestSupport.CONST_D));

		// add a second :)
		dkm.addToListFor(CONST_1B, TestSupport.CONST_C);
		assertTrue(dkm.containsInList(CONST_1B, TestSupport.CONST_C));

		// Test null stuff :)
		assertTrue(dkm.containsInList(TestSupport.I2, null));

		assertFalse(dkm.containsInList(null, TestSupport.CONST_A));
		assertTrue(dkm.containsInList(null, TestSupport.CONST_F));
	}

	@Test
	public void testGetKeySet()
	{
		GenericMapToList<Integer, Character> dkm =
				GenericMapToList.getMapToList(IdentityHashMap.class);
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
		assertTrue(s3.contains(CONST_1A));
		assertTrue(s3.contains(CONST_1B));
		assertTrue(s3.contains(TestSupport.I2));
		assertTrue(s3.contains(TestSupport.I5));
		assertTrue(s3.contains(null));
	}

	@Test
	public void testClearIsEmpty()
	{
		GenericMapToList<Integer, Character> dkm =
				GenericMapToList.getMapToList(IdentityHashMap.class);
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.size());
		populate(dkm);
		assertFalse(dkm.isEmpty());
		assertEquals(5, dkm.size());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.size());
		dkm.addToListFor(null, 'F');
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.size());
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
				GenericMapToList.getMapToList(IdentityHashMap.class);
		dkm.addAllToListFor(CONST_1A, null);
		assertFalse(dkm.containsListFor(CONST_1A));
		dkm.addAllToListFor(CONST_1A, new ArrayList<>());
		assertFalse(dkm.containsListFor(CONST_1A));
	}

	@Test
	public void testAddAll()
	{
		GenericMapToList<Integer, Character> dkm =
				GenericMapToList.getMapToList(IdentityHashMap.class);
		List<Character> l = new ArrayList<>();
		l.add(TestSupport.CONST_A);
		l.add(null);
		l.add(TestSupport.CONST_A);
		l.add(TestSupport.CONST_B);
		dkm.addAllToListFor(CONST_1A, l);
		assertTrue(dkm.containsListFor(CONST_1A));
		assertEquals(4, dkm.sizeOfListFor(CONST_1A));
		dkm.addToListFor(CONST_1A, TestSupport.CONST_D);
		assertEquals(4, l.size());
		// Check reference semantics!
		l.add(TestSupport.CONST_C);
		l.add(TestSupport.CONST_E);
		assertTrue(dkm.containsListFor(CONST_1A));
		assertEquals(5, dkm.sizeOfListFor(CONST_1A));
		l.clear();
		assertTrue(dkm.containsListFor(CONST_1A));
		assertEquals(5, dkm.sizeOfListFor(CONST_1A));
	}

	@Test
	public void testInstanceBehavior()
	{
		GenericMapToList<Integer, Character> dkm =
				GenericMapToList.getMapToList(IdentityHashMap.class);
		Character ca = TestSupport.CONST_A;
		Character cb = TestSupport.CONST_B;
		Character cc = TestSupport.CONST_C;
		Character ca1 = new Character(TestSupport.CONST_A.charValue());
		dkm.addToListFor(CONST_1A, ca);
		dkm.addToListFor(CONST_1A, cb);
		dkm.addToListFor(CONST_1A, cc);
		dkm.addToListFor(TestSupport.I2, ca);
		dkm.addToListFor(TestSupport.I2, ca);
		dkm.addToListFor(TestSupport.I3, cb);
		dkm.addToListFor(TestSupport.I3, cc);
		assertTrue(dkm.containsInList(CONST_1A, ca));
		assertTrue(dkm.containsInList(CONST_1A, ca1));
		assertTrue(dkm.removeFromListFor(CONST_1A, ca1));
		assertFalse(dkm.containsInList(CONST_1A, ca));

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
				GenericMapToList.getMapToList(IdentityHashMap.class);
		populate(dkm);
		HashMapToList<Integer, Character> dkm2 = new HashMapToList<>();
		dkm2.addAllLists(dkm);
		assertTrue(dkm.removeFromListFor(CONST_1B, TestSupport.CONST_C));
		assertTrue(dkm2.containsInList(CONST_1B, TestSupport.CONST_C));

		assertTrue(dkm2.removeFromListFor(CONST_1B, TestSupport.CONST_B));
		assertTrue(dkm.containsInList(CONST_1B, TestSupport.CONST_B));

		dkm.removeListFor(CONST_1B);
		assertFalse(dkm.containsListFor(CONST_1B));
		assertTrue(dkm2.containsListFor(CONST_1B));
	}
}
