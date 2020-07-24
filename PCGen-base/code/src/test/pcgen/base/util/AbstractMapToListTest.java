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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import pcgen.testsupport.TestSupport;

/**
 * Shared Tests for testing classes that extend AbstractMapToList 
 */
public abstract class AbstractMapToListTest
{

	public static void populate(AbstractMapToList<Integer, Character> mtl)
	{
		mtl.addToListFor(TestSupport.I1, TestSupport.CONST_A);
		mtl.addToListFor(TestSupport.I1, TestSupport.CONST_B);
		mtl.addToListFor(TestSupport.I1, TestSupport.CONST_C);
		mtl.addToListFor(TestSupport.I2, TestSupport.CONST_D);
		mtl.addToListFor(TestSupport.I2, TestSupport.CONST_E);
		mtl.addToListFor(TestSupport.I2, null);
		mtl.addToListFor(null, TestSupport.CONST_F);
		mtl.addToListFor(TestSupport.I5, null);
	}

	protected abstract AbstractMapToList<Integer, Character> getMapToList();

	@Test
	public void testInitializeListFor()
	{
		AbstractMapToList<Integer, Character> mtl = getMapToList();
		assertNull(mtl.getListFor(TestSupport.I1));
		mtl.initializeListFor(TestSupport.I1);
		List<Character> l = mtl.getListFor(TestSupport.I1);
		assertEquals(0, l.size());
		assertThrows(IllegalArgumentException.class, () -> mtl.initializeListFor(TestSupport.I1));
	}

	@Test
	public void testPutGet()
	{
		AbstractMapToList<Integer, Character> mtl = getMapToList();
		assertNull(mtl.getListFor(null));
		assertNull(mtl.getListFor(TestSupport.I1));
		populate(mtl);
		List<Character> l = mtl.getListFor(TestSupport.I1);
		assertEquals(3, mtl.sizeOfListFor(TestSupport.I1));
		assertEquals(3, l.size());
		assertTrue(l.contains(TestSupport.CONST_A));
		assertTrue(l.contains(TestSupport.CONST_B));
		assertTrue(l.contains(TestSupport.CONST_C));
		mtl.addToListFor(TestSupport.I1, TestSupport.CONST_C);
		l = mtl.getListFor(TestSupport.I1);
		assertEquals(4, mtl.sizeOfListFor(TestSupport.I1));
		assertEquals(4, l.size());
		assertTrue(l.contains(TestSupport.CONST_A));
		assertTrue(l.contains(TestSupport.CONST_B));
		assertTrue(l.contains(TestSupport.CONST_C));
		// two of them
		l.remove(TestSupport.CONST_C);
		assertTrue(l.contains(TestSupport.CONST_C));
		l = mtl.getListFor(TestSupport.I2);
		assertEquals(3, mtl.sizeOfListFor(TestSupport.I2));
		assertEquals(3, l.size());
		assertTrue(l.contains(TestSupport.CONST_D));
		assertTrue(l.contains(TestSupport.CONST_E));
		assertTrue(l.contains(null));
		mtl.addToListFor(TestSupport.I2, null);
		l = mtl.getListFor(TestSupport.I2);
		assertEquals(4, mtl.sizeOfListFor(TestSupport.I1));
		assertEquals(4, l.size());
		assertTrue(l.contains(TestSupport.CONST_D));
		assertTrue(l.contains(TestSupport.CONST_E));
		assertTrue(l.contains(null));
		// Two of them.
		l.remove(null);
		assertTrue(l.contains(null));
		assertNull(mtl.getListFor(TestSupport.I4));
		l = mtl.getListFor(null);
		assertEquals(1, mtl.sizeOfListFor(null));
		assertEquals(1, l.size());
		assertTrue(l.contains(TestSupport.CONST_F));
		l.add(TestSupport.CONST_A);
		List<Character> l2 = mtl.getListFor(null);
		assertEquals(1, l2.size());
		assertTrue(l2.contains(TestSupport.CONST_F));
		assertEquals(2, l.size());
		assertTrue(l.contains(TestSupport.CONST_F));
		assertTrue(l.contains(TestSupport.CONST_A));
		mtl.clear();
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
		AbstractMapToList<Integer, Character> mtl = getMapToList();
		assertFalse(mtl.containsListFor(TestSupport.I1));
		assertFalse(mtl.containsListFor(null));
		populate(mtl);
		assertTrue(mtl.containsListFor(TestSupport.I1));
		// Keys are .equals items, not instance
		assertTrue(mtl.containsListFor(new Integer(1)));
		assertTrue(mtl.containsListFor(TestSupport.I2));
		assertTrue(mtl.containsListFor(TestSupport.I5));
		assertFalse(mtl.containsListFor(Integer.valueOf(-4)));
		assertTrue(mtl.containsListFor(null));
	}

	@Test
	public void testRemoveListFor()
	{
		AbstractMapToList<Integer, Character> mtl = getMapToList();
		assertNull(mtl.removeListFor(TestSupport.I1));
		assertNull(mtl.removeListFor(null));
		populate(mtl);
		List<Character> l = mtl.removeListFor(TestSupport.I1);
		assertEquals(3, l.size());
		assertTrue(l.contains(TestSupport.CONST_A));
		assertTrue(l.contains(TestSupport.CONST_B));
		assertTrue(l.contains(TestSupport.CONST_C));
		assertFalse(mtl.containsListFor(TestSupport.I1));
		assertNull(mtl.getListFor(TestSupport.I1));
		l = mtl.removeListFor(TestSupport.I2);
		assertEquals(3, l.size());
		assertTrue(l.contains(TestSupport.CONST_D));
		assertTrue(l.contains(TestSupport.CONST_E));
		assertTrue(l.contains(null));
		l = mtl.removeListFor(null);
		assertEquals(1, l.size());
		assertTrue(l.contains(TestSupport.CONST_F));
	}

	@Test
	public void testRemoveFromListFor()
	{
		AbstractMapToList<Integer, Character> mtl = getMapToList();
		assertFalse(mtl.removeFromListFor(TestSupport.I1, TestSupport.CONST_D));
		populate(mtl);
		assertTrue(mtl.removeFromListFor(TestSupport.I1, TestSupport.CONST_A));
		assertTrue(mtl.containsListFor(TestSupport.I1));
		// Keys are .equals items, not instance
		assertTrue(mtl.containsListFor(new Integer(1)));
		assertEquals(2, mtl.sizeOfListFor(TestSupport.I1));
		assertFalse(mtl.removeFromListFor(TestSupport.I1, TestSupport.CONST_A));
		assertTrue(mtl.removeFromListFor(TestSupport.I1, TestSupport.CONST_B));
		assertEquals(1, mtl.sizeOfListFor(TestSupport.I1));
		assertTrue(mtl.containsListFor(TestSupport.I1));
		assertFalse(mtl.removeFromListFor(TestSupport.I1, TestSupport.CONST_A));
		assertTrue(mtl.removeFromListFor(TestSupport.I1, TestSupport.CONST_C));
		assertEquals(0, mtl.sizeOfListFor(TestSupport.I1));
		assertFalse(mtl.containsListFor(TestSupport.I1));

		// add a second :)
		mtl.addToListFor(TestSupport.I2, TestSupport.CONST_D);
		assertFalse(mtl.removeFromListFor(TestSupport.I2, TestSupport.CONST_A));
		assertTrue(mtl.containsListFor(TestSupport.I2));
		assertEquals(4, mtl.sizeOfListFor(TestSupport.I2));
		assertFalse(mtl.removeFromListFor(TestSupport.I2, TestSupport.CONST_A));
		assertTrue(mtl.removeFromListFor(TestSupport.I2, TestSupport.CONST_D));
		assertEquals(3, mtl.sizeOfListFor(TestSupport.I2));
		assertTrue(mtl.containsListFor(TestSupport.I2));
		assertTrue(mtl.removeFromListFor(TestSupport.I2, TestSupport.CONST_E));
		assertEquals(2, mtl.sizeOfListFor(TestSupport.I2));
		assertTrue(mtl.containsListFor(TestSupport.I2));
		assertTrue(mtl.removeFromListFor(TestSupport.I2, null));
		assertEquals(1, mtl.sizeOfListFor(TestSupport.I2));
		assertTrue(mtl.containsListFor(TestSupport.I2));
		assertTrue(mtl.removeFromListFor(TestSupport.I2, TestSupport.CONST_D));
		assertEquals(0, mtl.sizeOfListFor(TestSupport.I2));
		assertFalse(mtl.containsListFor(TestSupport.I2));

		// Test null stuff :)
		assertFalse(mtl.removeFromListFor(null, TestSupport.CONST_A));
		assertTrue(mtl.containsListFor(null));
		assertEquals(1, mtl.sizeOfListFor(null));
		assertFalse(mtl.removeFromListFor(null, TestSupport.CONST_A));
		assertTrue(mtl.removeFromListFor(null, TestSupport.CONST_F));
		assertEquals(0, mtl.sizeOfListFor(null));
		assertFalse(mtl.containsListFor(null));
	}

	@Test
	public void testContainsInList()
	{
		AbstractMapToList<Integer, Character> mtl = getMapToList();
		assertFalse(mtl.containsInList(TestSupport.I1, TestSupport.CONST_D));
		populate(mtl);
		assertTrue(mtl.containsInList(TestSupport.I1, TestSupport.CONST_A));
		// Keys are .equals items, not instance
		assertTrue(mtl.containsInList(new Integer(1), TestSupport.CONST_A));
		assertTrue(mtl.containsInList(TestSupport.I1, TestSupport.CONST_B));
		assertTrue(mtl.containsInList(TestSupport.I1, TestSupport.CONST_C));
		assertFalse(mtl.containsInList(TestSupport.I1, TestSupport.CONST_D));

		// add a second :)
		mtl.addToListFor(TestSupport.I1, TestSupport.CONST_C);
		assertTrue(mtl.containsInList(TestSupport.I1, TestSupport.CONST_C));

		// Test null stuff :)
		assertTrue(mtl.containsInList(TestSupport.I2, null));

		assertFalse(mtl.containsInList(null, TestSupport.CONST_A));
		assertTrue(mtl.containsInList(null, TestSupport.CONST_F));
	}

	@Test
	public void testContainsAnyInList()
	{
		AbstractMapToList<Integer, Character> mtl = getMapToList();
		assertFalse(mtl.containsAnyInList(TestSupport.I1,
			Collections.singletonList(TestSupport.CONST_D)));
		populate(mtl);
		assertTrue(mtl.containsAnyInList(TestSupport.I1,
			Collections.singletonList(TestSupport.CONST_A)));
		// Keys are .equals items, not instance
		assertTrue(mtl.containsAnyInList(new Integer(1),
			Arrays.asList(new Character[]{TestSupport.CONST_A, TestSupport.CONST_D})));
		assertTrue(mtl.containsAnyInList(TestSupport.I1,
			Arrays.asList(new Character[]{TestSupport.CONST_D, TestSupport.CONST_B})));
		assertTrue(mtl.containsAnyInList(TestSupport.I1,
			Arrays.asList(new Character[]{TestSupport.CONST_C, TestSupport.CONST_B})));
		assertFalse(mtl.containsAnyInList(TestSupport.I1,
			Arrays.asList(new Character[]{TestSupport.CONST_D, TestSupport.CONST_E, TestSupport.CONST_F})));

		// add a second :)
		mtl.addToListFor(TestSupport.I1, TestSupport.CONST_C);
		assertTrue(mtl.containsAnyInList(TestSupport.I1,
			Arrays.asList(new Character[]{TestSupport.CONST_C, TestSupport.CONST_F})));

		// Test null stuff :)
		assertTrue(mtl.containsAnyInList(TestSupport.I2,
			Arrays.asList(new Character[]{TestSupport.CONST_F, null})));
		assertThrows(NullPointerException.class, () -> mtl.containsAnyInList(TestSupport.I2, null));
		assertFalse(mtl.containsAnyInList(TestSupport.I2, Collections.<Character>emptyList()));

		assertTrue(mtl.containsAnyInList(null,
			Arrays.asList(new Character[]{TestSupport.CONST_A, TestSupport.CONST_F})));
		assertFalse(mtl.containsAnyInList(null,
			Arrays.asList(new Character[]{TestSupport.CONST_A, TestSupport.CONST_D})));
	}

	@Test
	public void testGetKeySet()
	{
		AbstractMapToList<Integer, Character> mtl = getMapToList();
		Set<Integer> s = mtl.getKeySet();
		assertEquals(0, s.size());
		s.add(Integer.valueOf(-5));
		// Ensure not saved in DoubleKeyMap
		Set<Integer> s2 = mtl.getKeySet();
		assertEquals(0, s2.size());
		assertEquals(1, s.size());
		// And ensure references are not kept the other direction to be altered
		// by changes in the underlying DoubleKeyMap
		populate(mtl);
		assertEquals(1, s.size());
		assertEquals(0, s2.size());
		Set<Integer> s3 = mtl.getKeySet();
		assertEquals(4, s3.size());
		assertTrue(s3.contains(TestSupport.I1));
		assertTrue(s3.contains(TestSupport.I2));
		assertTrue(s3.contains(TestSupport.I5));
		assertTrue(s3.contains(null));
	}

	@Test
	public void testClearIsEmpty()
	{
		AbstractMapToList<Integer, Character> mtl = getMapToList();
		assertTrue(mtl.isEmpty());
		assertEquals(0, mtl.size());
		populate(mtl);
		assertFalse(mtl.isEmpty());
		assertEquals(4, mtl.size());
		mtl.clear();
		assertTrue(mtl.isEmpty());
		assertEquals(0, mtl.size());
		mtl.addToListFor(null, 'F');
		assertFalse(mtl.isEmpty());
		assertEquals(1, mtl.size());
		mtl.clear();
		assertTrue(mtl.isEmpty());
		assertEquals(0, mtl.size());
		mtl.addToListFor(TestSupport.I3, 'G');
		assertFalse(mtl.isEmpty());
		assertEquals(1, mtl.size());
		mtl.clear();
		assertTrue(mtl.isEmpty());
		assertEquals(0, mtl.size());
		mtl.addToListFor(TestSupport.I5, null);
		assertFalse(mtl.isEmpty());
		assertEquals(1, mtl.size());
		mtl.clear();
		assertTrue(mtl.isEmpty());
		assertEquals(0, mtl.size());
	}

	@Test
	public void testEmptyAddAll()
	{
		AbstractMapToList<Integer, Character> mtl = getMapToList();
		mtl.addAllToListFor(TestSupport.I1, null);
		assertFalse(mtl.containsListFor(TestSupport.I1));
		mtl.addAllToListFor(TestSupport.I1, new ArrayList<>());
		assertFalse(mtl.containsListFor(TestSupport.I1));
	}

	@Test
	public void testAddAll()
	{
		AbstractMapToList<Integer, Character> mtl = getMapToList();
		List<Character> l = new ArrayList<>();
		l.add(TestSupport.CONST_A);
		l.add(null);
		l.add(TestSupport.CONST_A);
		l.add(TestSupport.CONST_B);
		mtl.addAllToListFor(TestSupport.I1, l);
		assertTrue(mtl.containsListFor(TestSupport.I1));
		assertEquals(4, mtl.sizeOfListFor(TestSupport.I1));
		mtl.addToListFor(TestSupport.I1, TestSupport.CONST_D);
		assertEquals(4, l.size());
		// Check reference semantics!
		l.add(TestSupport.CONST_C);
		l.add(TestSupport.CONST_E);
		assertTrue(mtl.containsListFor(TestSupport.I1));
		assertEquals(5, mtl.sizeOfListFor(TestSupport.I1));
		l.clear();
		assertTrue(mtl.containsListFor(TestSupport.I1));
		assertEquals(5, mtl.sizeOfListFor(TestSupport.I1));
	}

	@Test
	public void testInstanceBehavior()
	{
		AbstractMapToList<Integer, Character> mtl = getMapToList();
		Character ca = TestSupport.CONST_A;
		Character cb = TestSupport.CONST_B;
		Character cc = TestSupport.CONST_C;
		Character ca1 = new Character(TestSupport.CONST_A.charValue());
		mtl.addToListFor(TestSupport.I1, ca);
		mtl.addToListFor(TestSupport.I1, cb);
		mtl.addToListFor(TestSupport.I1, cc);
		mtl.addToListFor(TestSupport.I2, ca);
		mtl.addToListFor(TestSupport.I2, ca);
		mtl.addToListFor(TestSupport.I3, cb);
		mtl.addToListFor(TestSupport.I3, cc);
		assertTrue(mtl.containsInList(TestSupport.I1, ca));
		assertTrue(mtl.containsInList(TestSupport.I1, ca1));
		assertTrue(mtl.removeFromListFor(TestSupport.I1, ca1));
		assertFalse(mtl.containsInList(TestSupport.I1, ca));

		assertTrue(mtl.containsInList(TestSupport.I2, ca));
		assertTrue(mtl.containsInList(TestSupport.I2, ca1));
		assertTrue(mtl.removeFromListFor(TestSupport.I2, ca1));
		// There were two
		assertTrue(mtl.containsInList(TestSupport.I2, ca));
		assertTrue(mtl.removeFromListFor(TestSupport.I2, ca));
		// There were two
		assertFalse(mtl.containsInList(TestSupport.I2, ca));
	}

	@Test
	public void testAddAllLists()
	{
		AbstractMapToList<Integer, Character> mtl = getMapToList();
		AbstractMapToList<Integer, Character> mtl2 = getMapToList();
		populate(mtl);
		mtl2.addAllLists(mtl);
		assertTrue(mtl.removeFromListFor(TestSupport.I1, TestSupport.CONST_A));
		assertTrue(mtl2.containsInList(TestSupport.I1, TestSupport.CONST_A));

		assertTrue(mtl2.removeFromListFor(TestSupport.I1, TestSupport.CONST_B));
		assertTrue(mtl.containsInList(TestSupport.I1, TestSupport.CONST_B));

		mtl.removeListFor(TestSupport.I1);
		assertFalse(mtl.containsListFor(TestSupport.I1));
		assertTrue(mtl2.containsListFor(TestSupport.I1));
	}

	@Test
	public void testGetElementInList()
	{
		AbstractMapToList<Integer, Character> mtl = getMapToList();
		assertThrows(NullPointerException.class, () -> mtl.getElementInList(TestSupport.I1, 0));
		populate(mtl);
		try
		{
			mtl.getElementInList(TestSupport.I1, 3);
			fail("Expected IndexOutOfBoundsException");
		}
		catch (IndexOutOfBoundsException e)
		{
			//expected
		}
		assertEquals(TestSupport.CONST_A, mtl.getElementInList(TestSupport.I1, 0));
		assertEquals(TestSupport.CONST_B, mtl.getElementInList(TestSupport.I1, 1));
		assertEquals(TestSupport.CONST_C, mtl.getElementInList(TestSupport.I1, 2));
		mtl.addToListFor(TestSupport.I1, TestSupport.CONST_C);
		assertEquals(TestSupport.CONST_A, mtl.getElementInList(TestSupport.I1, 0));
		assertEquals(TestSupport.CONST_B, mtl.getElementInList(TestSupport.I1, 1));
		assertEquals(TestSupport.CONST_C, mtl.getElementInList(TestSupport.I1, 2));
		assertEquals(TestSupport.CONST_C, mtl.getElementInList(TestSupport.I1, 3));

		assertEquals(TestSupport.CONST_D, mtl.getElementInList(TestSupport.I2, 0));
		assertEquals(TestSupport.CONST_E, mtl.getElementInList(TestSupport.I2, 1));
		assertNull(mtl.getElementInList(TestSupport.I2, 2));

		assertEquals(TestSupport.CONST_F, mtl.getElementInList(null, 0));
	}
}
