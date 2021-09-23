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

import org.junit.jupiter.api.Test;

import pcgen.testsupport.TestSupport;

public class TreeMapToListTest
{

	public void populate(TreeMapToList<Integer, Character> tml)
	{
		tml.addToListFor(TestSupport.I1, TestSupport.CONST_A);
		tml.addToListFor(TestSupport.I1, TestSupport.CONST_B);
		tml.addToListFor(TestSupport.I1, TestSupport.CONST_C);
		tml.addToListFor(TestSupport.I2, TestSupport.CONST_D);
		tml.addToListFor(TestSupport.I2, TestSupport.CONST_E);
		tml.addToListFor(TestSupport.I2, null);
		tml.addToListFor(TestSupport.I5, null);
	}

	@Test
	public void testConstructorWithNull()
	{
		TreeMapToList<Integer, Character> tml = new TreeMapToList<>(null);
		runPutGet(tml);
	}

	@Test
	public void testDefaultConstructor()
	{
		TreeMapToList<Integer, Character> tml = new TreeMapToList<>();
		runPutGet(tml);
	}

	@Test
	public void testPutNull()
	{
		TreeMapToList<Integer, Character> tml = new TreeMapToList<>();
		assertThrows(NullPointerException.class, () -> tml.addToListFor(null, TestSupport.CONST_F));
	}

	@Test
	public void testInitializeListFor()
	{
		TreeMapToList<Integer, Character> tml = new TreeMapToList<>();
		assertNull(tml.getListFor(TestSupport.I1));
		tml.initializeListFor(TestSupport.I1);
		List<Character> l = tml.getListFor(TestSupport.I1);
		assertEquals(0, l.size());
		assertThrows(IllegalArgumentException.class, () -> tml.initializeListFor(TestSupport.I1));
	}

	public void runPutGet(TreeMapToList<Integer, Character> tml)
	{
		assertNull(tml.getListFor(TestSupport.I1));
		populate(tml);
		List<Character> l = tml.getListFor(TestSupport.I1);
		assertEquals(3, l.size());
		assertTrue(l.contains(TestSupport.CONST_A));
		assertTrue(l.contains(TestSupport.CONST_B));
		assertTrue(l.contains(TestSupport.CONST_C));
		tml.addToListFor(TestSupport.I1, TestSupport.CONST_C);
		l = tml.getListFor(TestSupport.I1);
		assertEquals(4, l.size());
		assertTrue(l.contains(TestSupport.CONST_A));
		assertTrue(l.contains(TestSupport.CONST_B));
		assertTrue(l.contains(TestSupport.CONST_C));
		// two of them
		l.remove(TestSupport.CONST_C);
		assertTrue(l.contains(TestSupport.CONST_C));
		l = tml.getListFor(TestSupport.I2);
		assertEquals(3, l.size());
		assertTrue(l.contains(TestSupport.CONST_D));
		assertTrue(l.contains(TestSupport.CONST_E));
		assertTrue(l.contains(null));
		tml.addToListFor(TestSupport.I2, null);
		l = tml.getListFor(TestSupport.I2);
		assertEquals(4, l.size());
		assertTrue(l.contains(TestSupport.CONST_D));
		assertTrue(l.contains(TestSupport.CONST_E));
		assertTrue(l.contains(null));
		// Two of them.
		l.remove(null);
		assertTrue(l.contains(null));
		assertNull(tml.getListFor(TestSupport.I4));
		l = tml.getListFor(TestSupport.I5);
		assertEquals(1, l.size());
		assertTrue(l.contains(null));
		l.add(TestSupport.CONST_A);
		List<Character> l2 = tml.getListFor(TestSupport.I5);
		assertEquals(1, l2.size());
		assertTrue(l2.contains(null));
		assertEquals(2, l.size());
		assertTrue(l.contains(null));
		assertTrue(l.contains(TestSupport.CONST_A));
		tml.clear();
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
		TreeMapToList<Integer, Character> tml = new TreeMapToList<>();
		assertFalse(tml.containsListFor(TestSupport.I1));
		populate(tml);
		assertTrue(tml.containsListFor(TestSupport.I1));
		// Keys are .equals items, not instance
		assertTrue(tml.containsListFor(new Integer(1)));
		assertTrue(tml.containsListFor(TestSupport.I2));
		assertTrue(tml.containsListFor(TestSupport.I5));
		assertFalse(tml.containsListFor(Integer.valueOf(-4)));
	}

	@Test
	public void testRemoveListFor()
	{
		TreeMapToList<Integer, Character> tml = new TreeMapToList<>();
		assertNull(tml.removeListFor(TestSupport.I1));
		populate(tml);
		List<Character> l = tml.removeListFor(TestSupport.I1);
		assertEquals(3, l.size());
		assertTrue(l.contains(TestSupport.CONST_A));
		assertTrue(l.contains(TestSupport.CONST_B));
		assertTrue(l.contains(TestSupport.CONST_C));
		assertFalse(tml.containsListFor(TestSupport.I1));
		assertNull(tml.getListFor(TestSupport.I1));
		l = tml.removeListFor(TestSupport.I2);
		assertEquals(3, l.size());
		assertTrue(l.contains(TestSupport.CONST_D));
		assertTrue(l.contains(TestSupport.CONST_E));
		assertTrue(l.contains(null));
	}

	@Test
	public void testRemoveFromListFor()
	{
		TreeMapToList<Integer, Character> tml = new TreeMapToList<>();
		assertFalse(tml.removeFromListFor(TestSupport.I1, TestSupport.CONST_D));
		populate(tml);
		assertTrue(tml.removeFromListFor(TestSupport.I1, TestSupport.CONST_A));
		assertTrue(tml.containsListFor(TestSupport.I1));
		// Keys are .equals items, not instance
		assertTrue(tml.containsListFor(new Integer(1)));
		assertEquals(2, tml.sizeOfListFor(TestSupport.I1));
		assertFalse(tml.removeFromListFor(TestSupport.I1, TestSupport.CONST_A));
		assertTrue(tml.removeFromListFor(TestSupport.I1, TestSupport.CONST_B));
		assertEquals(1, tml.sizeOfListFor(TestSupport.I1));
		assertTrue(tml.containsListFor(TestSupport.I1));
		assertFalse(tml.removeFromListFor(TestSupport.I1, TestSupport.CONST_A));
		assertTrue(tml.removeFromListFor(TestSupport.I1, TestSupport.CONST_C));
		assertEquals(0, tml.sizeOfListFor(TestSupport.I1));
		assertFalse(tml.containsListFor(TestSupport.I1));

		// add a second :)
		tml.addToListFor(TestSupport.I2, TestSupport.CONST_D);
		assertFalse(tml.removeFromListFor(TestSupport.I2, TestSupport.CONST_A));
		assertTrue(tml.containsListFor(TestSupport.I2));
		assertEquals(4, tml.sizeOfListFor(TestSupport.I2));
		assertFalse(tml.removeFromListFor(TestSupport.I2, TestSupport.CONST_A));
		assertTrue(tml.removeFromListFor(TestSupport.I2, TestSupport.CONST_D));
		assertEquals(3, tml.sizeOfListFor(TestSupport.I2));
		assertTrue(tml.containsListFor(TestSupport.I2));
		assertTrue(tml.removeFromListFor(TestSupport.I2, TestSupport.CONST_E));
		assertEquals(2, tml.sizeOfListFor(TestSupport.I2));
		assertTrue(tml.containsListFor(TestSupport.I2));
		assertTrue(tml.removeFromListFor(TestSupport.I2, null));
		assertEquals(1, tml.sizeOfListFor(TestSupport.I2));
		assertTrue(tml.containsListFor(TestSupport.I2));
		assertTrue(tml.removeFromListFor(TestSupport.I2, TestSupport.CONST_D));
		assertEquals(0, tml.sizeOfListFor(TestSupport.I2));
		assertFalse(tml.containsListFor(TestSupport.I2));
	}

	@Test
	public void testContainsInList()
	{
		TreeMapToList<Integer, Character> tml = new TreeMapToList<>();
		assertFalse(tml.containsInList(TestSupport.I1, TestSupport.CONST_D));
		populate(tml);
		assertTrue(tml.containsInList(TestSupport.I1, TestSupport.CONST_A));
		// Keys are .equals items, not instance
		assertTrue(tml.containsInList(new Integer(1), TestSupport.CONST_A));
		assertTrue(tml.containsInList(TestSupport.I1, TestSupport.CONST_B));
		assertTrue(tml.containsInList(TestSupport.I1, TestSupport.CONST_C));
		assertFalse(tml.containsInList(TestSupport.I1, TestSupport.CONST_D));

		// add a second :)
		tml.addToListFor(TestSupport.I1, TestSupport.CONST_C);
		assertTrue(tml.containsInList(TestSupport.I1, TestSupport.CONST_C));

		// Test null stuff :)
		assertTrue(tml.containsInList(TestSupport.I2, null));
	}

	@Test
	public void testGetKeySet()
	{
		TreeMapToList<Integer, Character> tml = new TreeMapToList<>();
		Set<Integer> s = tml.getKeySet();
		assertEquals(0, s.size());
		s.add(Integer.valueOf(-5));
		// Ensure not saved in DoubleKeyMap
		Set<Integer> s2 = tml.getKeySet();
		assertEquals(0, s2.size());
		assertEquals(1, s.size());
		// And ensure references are not kept the other direction to be altered
		// by changes in the underlying DoubleKeyMap
		populate(tml);
		assertEquals(1, s.size());
		assertEquals(0, s2.size());
		Set<Integer> s3 = tml.getKeySet();
		assertEquals(3, s3.size());
		assertTrue(s3.contains(TestSupport.I1));
		assertTrue(s3.contains(TestSupport.I2));
		assertTrue(s3.contains(TestSupport.I5));
	}

	@Test
	public void testClearIsEmpty()
	{
		TreeMapToList<Integer, Character> tml = new TreeMapToList<>();
		assertTrue(tml.isEmpty());
		assertEquals(0, tml.size());
		populate(tml);
		assertFalse(tml.isEmpty());
		assertEquals(3, tml.size());
		tml.clear();
		assertTrue(tml.isEmpty());
		assertEquals(0, tml.size());
		tml.addToListFor(TestSupport.I3, 'G');
		assertFalse(tml.isEmpty());
		assertEquals(1, tml.size());
		tml.clear();
		assertTrue(tml.isEmpty());
		assertEquals(0, tml.size());
		tml.addToListFor(TestSupport.I5, null);
		assertFalse(tml.isEmpty());
		assertEquals(1, tml.size());
		tml.clear();
		assertTrue(tml.isEmpty());
		assertEquals(0, tml.size());
	}

	@Test
	public void testEmptyAddAll()
	{
		TreeMapToList<Integer, Character> tml = new TreeMapToList<>();
		tml.addAllToListFor(TestSupport.I1, null);
		assertFalse(tml.containsListFor(TestSupport.I1));
		tml.addAllToListFor(TestSupport.I1, new ArrayList<>());
		assertFalse(tml.containsListFor(TestSupport.I1));
	}

	@Test
	public void testAddAll()
	{
		TreeMapToList<Integer, Character> tml = new TreeMapToList<>();
		List<Character> l = new ArrayList<>();
		l.add(TestSupport.CONST_A);
		l.add(null);
		l.add(TestSupport.CONST_A);
		l.add(TestSupport.CONST_B);
		tml.addAllToListFor(TestSupport.I1, l);
		assertTrue(tml.containsListFor(TestSupport.I1));
		assertEquals(4, tml.sizeOfListFor(TestSupport.I1));
		tml.addToListFor(TestSupport.I1, TestSupport.CONST_D);
		assertEquals(4, l.size());
		// Check reference semantics!
		l.add(TestSupport.CONST_C);
		l.add(TestSupport.CONST_E);
		assertTrue(tml.containsListFor(TestSupport.I1));
		assertEquals(5, tml.sizeOfListFor(TestSupport.I1));
		l.clear();
		assertTrue(tml.containsListFor(TestSupport.I1));
		assertEquals(5, tml.sizeOfListFor(TestSupport.I1));
	}

	@Test
	public void testInstanceBehavior()
	{
		TreeMapToList<Integer, Character> tml = new TreeMapToList<>();
		Character ca = TestSupport.CONST_A;
		Character cb = TestSupport.CONST_B;
		Character cc = TestSupport.CONST_C;
		Character ca1 = new Character(TestSupport.CONST_A.charValue());
		tml.addToListFor(TestSupport.I1, ca);
		tml.addToListFor(TestSupport.I1, cb);
		tml.addToListFor(TestSupport.I1, cc);
		tml.addToListFor(TestSupport.I2, ca);
		tml.addToListFor(TestSupport.I2, ca);
		tml.addToListFor(TestSupport.I3, cb);
		tml.addToListFor(TestSupport.I3, cc);
		assertTrue(tml.containsInList(TestSupport.I1, ca));
		assertTrue(tml.containsInList(TestSupport.I1, ca1));
		assertTrue(tml.removeFromListFor(TestSupport.I1, ca1));
		assertFalse(tml.containsInList(TestSupport.I1, ca));

		assertTrue(tml.containsInList(TestSupport.I2, ca));
		assertTrue(tml.containsInList(TestSupport.I2, ca1));
		assertTrue(tml.removeFromListFor(TestSupport.I2, ca1));
		// There were two
		assertTrue(tml.containsInList(TestSupport.I2, ca));
		assertTrue(tml.removeFromListFor(TestSupport.I2, ca));
		// There were two
		assertFalse(tml.containsInList(TestSupport.I2, ca));
	}

	@Test
	public void testAddAllLists()
	{
		TreeMapToList<Integer, Character> tml = new TreeMapToList<>();
		HashMapToList<Integer, Character> tml2 = new HashMapToList<>();
		populate(tml);
		tml2.addAllLists(tml);
		assertTrue(tml.removeFromListFor(TestSupport.I1, TestSupport.CONST_A));
		assertTrue(tml2.containsInList(TestSupport.I1, TestSupport.CONST_A));

		assertTrue(tml2.removeFromListFor(TestSupport.I1, TestSupport.CONST_B));
		assertTrue(tml.containsInList(TestSupport.I1, TestSupport.CONST_B));

		tml.removeListFor(TestSupport.I1);
		assertFalse(tml.containsListFor(TestSupport.I1));
		assertTrue(tml2.containsListFor(TestSupport.I1));
	}
	
	// TODO Need to test iterator order
}
