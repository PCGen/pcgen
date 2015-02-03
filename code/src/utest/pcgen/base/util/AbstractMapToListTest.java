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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

public abstract class AbstractMapToListTest extends TestCase
{

	private static final Character CONST_E = 'E';

	private static final Character CONST_C = 'C';

	private static final Character CONST_F = 'F';

	private static final Character CONST_D = 'D';

	private static final Character CONST_B = 'B';

	private static final Character CONST_A = 'A';

	public void populate(AbstractMapToList<Integer, Character> dkm)
	{
		dkm.addToListFor(Integer.valueOf(1), CONST_A);
		dkm.addToListFor(Integer.valueOf(1), CONST_B);
		dkm.addToListFor(Integer.valueOf(1), CONST_C);
		dkm.addToListFor(Integer.valueOf(2), CONST_D);
		dkm.addToListFor(Integer.valueOf(2), CONST_E);
		dkm.addToListFor(Integer.valueOf(2), null);
		dkm.addToListFor(null, CONST_F);
		dkm.addToListFor(Integer.valueOf(5), null);
	}

	protected abstract AbstractMapToList<Integer, Character> getMapToList();

	@Test
	public void testInitializeListFor()
	{
		AbstractMapToList<Integer, Character> dkm = getMapToList();
		assertNull(dkm.getListFor(Integer.valueOf(1)));
		dkm.initializeListFor(Integer.valueOf(1));
		List<Character> l = dkm.getListFor(Integer.valueOf(1));
		assertEquals(0, l.size());
		try
		{
			dkm.initializeListFor(Integer.valueOf(1));
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
	}

	@Test
	public void testPutGet()
	{
		AbstractMapToList<Integer, Character> dkm = getMapToList();
		assertNull(dkm.getListFor(null));
		assertNull(dkm.getListFor(Integer.valueOf(1)));
		populate(dkm);
		List<Character> l = dkm.getListFor(Integer.valueOf(1));
		assertEquals(3, dkm.sizeOfListFor(Integer.valueOf(1)));
		assertEquals(3, l.size());
		assertTrue(l.contains(CONST_A));
		assertTrue(l.contains(CONST_B));
		assertTrue(l.contains(CONST_C));
		dkm.addToListFor(Integer.valueOf(1), CONST_C);
		l = dkm.getListFor(Integer.valueOf(1));
		assertEquals(4, dkm.sizeOfListFor(Integer.valueOf(1)));
		assertEquals(4, l.size());
		assertTrue(l.contains(CONST_A));
		assertTrue(l.contains(CONST_B));
		assertTrue(l.contains(CONST_C));
		// two of them
		l.remove(Character.valueOf(CONST_C));
		assertTrue(l.contains(CONST_C));
		l = dkm.getListFor(Integer.valueOf(2));
		assertEquals(3, dkm.sizeOfListFor(Integer.valueOf(2)));
		assertEquals(3, l.size());
		assertTrue(l.contains(CONST_D));
		assertTrue(l.contains(CONST_E));
		assertTrue(l.contains(null));
		dkm.addToListFor(Integer.valueOf(2), null);
		l = dkm.getListFor(Integer.valueOf(2));
		assertEquals(4, dkm.sizeOfListFor(Integer.valueOf(1)));
		assertEquals(4, l.size());
		assertTrue(l.contains(CONST_D));
		assertTrue(l.contains(CONST_E));
		assertTrue(l.contains(null));
		// Two of them.
		l.remove(null);
		assertTrue(l.contains(null));
		assertNull(dkm.getListFor(Integer.valueOf(4)));
		l = dkm.getListFor(null);
		assertEquals(1, dkm.sizeOfListFor(null));
		assertEquals(1, l.size());
		assertTrue(l.contains(CONST_F));
		l.add(CONST_A);
		List<Character> l2 = dkm.getListFor(null);
		assertEquals(1, l2.size());
		assertTrue(l2.contains(CONST_F));
		assertEquals(2, l.size());
		assertTrue(l.contains(CONST_F));
		assertTrue(l.contains(CONST_A));
		dkm.clear();
		assertEquals(1, l2.size());
		assertTrue(l2.contains(CONST_F));
		assertEquals(2, l.size());
		assertTrue(l.contains(CONST_F));
		assertTrue(l.contains(CONST_A));
		l2.clear();
		assertEquals(0, l2.size());
		assertEquals(2, l.size());
		assertTrue(l.contains(CONST_F));
		assertTrue(l.contains(CONST_A));
	}

	@Test
	public void testContainsKey()
	{
		AbstractMapToList<Integer, Character> dkm = getMapToList();
		assertFalse(dkm.containsListFor(Integer.valueOf(1)));
		assertFalse(dkm.containsListFor(null));
		populate(dkm);
		assertTrue(dkm.containsListFor(Integer.valueOf(1)));
		// Keys are .equals items, not instance
		assertTrue(dkm.containsListFor(new Integer(1)));
		assertTrue(dkm.containsListFor(Integer.valueOf(2)));
		assertTrue(dkm.containsListFor(Integer.valueOf(5)));
		assertFalse(dkm.containsListFor(Integer.valueOf(-4)));
		assertTrue(dkm.containsListFor(null));
	}

	@Test
	public void testRemoveListFor()
	{
		AbstractMapToList<Integer, Character> dkm = getMapToList();
		assertNull(dkm.removeListFor(Integer.valueOf(1)));
		assertNull(dkm.removeListFor(null));
		populate(dkm);
		List<Character> l = dkm.removeListFor(Integer.valueOf(1));
		assertEquals(3, l.size());
		assertTrue(l.contains(CONST_A));
		assertTrue(l.contains(CONST_B));
		assertTrue(l.contains(CONST_C));
		assertFalse(dkm.containsListFor(Integer.valueOf(1)));
		assertNull(dkm.getListFor(Integer.valueOf(1)));
		l = dkm.removeListFor(Integer.valueOf(2));
		assertEquals(3, l.size());
		assertTrue(l.contains(CONST_D));
		assertTrue(l.contains(CONST_E));
		assertTrue(l.contains(null));
		l = dkm.removeListFor(null);
		assertEquals(1, l.size());
		assertTrue(l.contains(CONST_F));
	}

	@Test
	public void testRemoveFromListFor()
	{
		AbstractMapToList<Integer, Character> dkm = getMapToList();
		assertFalse(dkm.removeFromListFor(Integer.valueOf(1), CONST_D));
		populate(dkm);
		assertTrue(dkm.removeFromListFor(Integer.valueOf(1), CONST_A));
		assertTrue(dkm.containsListFor(Integer.valueOf(1)));
		// Keys are .equals items, not instance
		assertTrue(dkm.containsListFor(new Integer(1)));
		assertEquals(2, dkm.sizeOfListFor(Integer.valueOf(1)));
		assertFalse(dkm.removeFromListFor(Integer.valueOf(1), CONST_A));
		assertTrue(dkm.removeFromListFor(Integer.valueOf(1), CONST_B));
		assertEquals(1, dkm.sizeOfListFor(Integer.valueOf(1)));
		assertTrue(dkm.containsListFor(Integer.valueOf(1)));
		assertFalse(dkm.removeFromListFor(Integer.valueOf(1), CONST_A));
		assertTrue(dkm.removeFromListFor(Integer.valueOf(1), CONST_C));
		assertEquals(0, dkm.sizeOfListFor(Integer.valueOf(1)));
		assertFalse(dkm.containsListFor(Integer.valueOf(1)));

		// add a second :)
		dkm.addToListFor(Integer.valueOf(2), CONST_D);
		assertFalse(dkm.removeFromListFor(Integer.valueOf(2), CONST_A));
		assertTrue(dkm.containsListFor(Integer.valueOf(2)));
		assertEquals(4, dkm.sizeOfListFor(Integer.valueOf(2)));
		assertFalse(dkm.removeFromListFor(Integer.valueOf(2), CONST_A));
		assertTrue(dkm.removeFromListFor(Integer.valueOf(2), CONST_D));
		assertEquals(3, dkm.sizeOfListFor(Integer.valueOf(2)));
		assertTrue(dkm.containsListFor(Integer.valueOf(2)));
		assertTrue(dkm.removeFromListFor(Integer.valueOf(2), CONST_E));
		assertEquals(2, dkm.sizeOfListFor(Integer.valueOf(2)));
		assertTrue(dkm.containsListFor(Integer.valueOf(2)));
		assertTrue(dkm.removeFromListFor(Integer.valueOf(2), null));
		assertEquals(1, dkm.sizeOfListFor(Integer.valueOf(2)));
		assertTrue(dkm.containsListFor(Integer.valueOf(2)));
		assertTrue(dkm.removeFromListFor(Integer.valueOf(2), CONST_D));
		assertEquals(0, dkm.sizeOfListFor(Integer.valueOf(2)));
		assertFalse(dkm.containsListFor(Integer.valueOf(2)));

		// Test null stuff :)
		assertFalse(dkm.removeFromListFor(null, CONST_A));
		assertTrue(dkm.containsListFor(null));
		assertEquals(1, dkm.sizeOfListFor(null));
		assertFalse(dkm.removeFromListFor(null, CONST_A));
		assertTrue(dkm.removeFromListFor(null, CONST_F));
		assertEquals(0, dkm.sizeOfListFor(null));
		assertFalse(dkm.containsListFor(null));
	}

	@Test
	public void testContainsInList()
	{
		AbstractMapToList<Integer, Character> dkm = getMapToList();
		assertFalse(dkm.containsInList(Integer.valueOf(1), CONST_D));
		populate(dkm);
		assertTrue(dkm.containsInList(Integer.valueOf(1), CONST_A));
		// Keys are .equals items, not instance
		assertTrue(dkm.containsInList(new Integer(1), CONST_A));
		assertTrue(dkm.containsInList(Integer.valueOf(1), CONST_B));
		assertTrue(dkm.containsInList(Integer.valueOf(1), CONST_C));
		assertFalse(dkm.containsInList(Integer.valueOf(1), CONST_D));

		// add a second :)
		dkm.addToListFor(Integer.valueOf(1), CONST_C);
		assertTrue(dkm.containsInList(Integer.valueOf(1), CONST_C));

		// Test null stuff :)
		assertTrue(dkm.containsInList(Integer.valueOf(2), null));

		assertFalse(dkm.containsInList(null, CONST_A));
		assertTrue(dkm.containsInList(null, CONST_F));
	}


	@Test
	public void testContainsAnyInList()
	{
		AbstractMapToList<Integer, Character> dkm = getMapToList();
		assertFalse(dkm.containsAnyInList(Integer.valueOf(1), Collections.singletonList(CONST_D)));
		populate(dkm);
		assertTrue(dkm.containsAnyInList(Integer.valueOf(1), Collections.singletonList(CONST_A)));
		// Keys are .equals items, not instance
		assertTrue(dkm.containsAnyInList(new Integer(1), Arrays.asList(new Character[] {CONST_A, CONST_D})));
		assertTrue(dkm.containsAnyInList(Integer.valueOf(1), Arrays.asList(new Character[] {CONST_D, CONST_B})));
		assertTrue(dkm.containsAnyInList(Integer.valueOf(1), Arrays.asList(new Character[] {CONST_C, CONST_B})));
		assertFalse(dkm.containsAnyInList(Integer.valueOf(1), Arrays.asList(new Character[] {CONST_D, CONST_E, CONST_F})));

		// add a second :)
		dkm.addToListFor(Integer.valueOf(1), CONST_C);
		assertTrue(dkm.containsAnyInList(Integer.valueOf(1), Arrays.asList(new Character[] {CONST_C, CONST_F})));

		// Test null stuff :)
		assertTrue(dkm.containsAnyInList(Integer.valueOf(2), Arrays.asList(new Character[] {CONST_F, null})));
		try
		{
			assertFalse(dkm.containsAnyInList(Integer.valueOf(2), null));
			fail();
		}
		catch (NullPointerException e)
		{
			//Yep!
		}
		assertFalse(dkm.containsAnyInList(Integer.valueOf(2), Collections.EMPTY_LIST));

		assertTrue(dkm.containsAnyInList(null, Arrays.asList(new Character[] {CONST_A, CONST_F})));
		assertFalse(dkm.containsAnyInList(null, Arrays.asList(new Character[] {CONST_A, CONST_D})));
	}

	@Test
	public void testGetKeySet()
	{
		AbstractMapToList<Integer, Character> dkm = getMapToList();
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
		assertEquals(4, s3.size());
		assertTrue(s3.contains(Integer.valueOf(1)));
		assertTrue(s3.contains(Integer.valueOf(2)));
		assertTrue(s3.contains(Integer.valueOf(5)));
		assertTrue(s3.contains(null));
	}

	@Test
	public void testClearIsEmpty()
	{
		AbstractMapToList<Integer, Character> dkm = getMapToList();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.size());
		populate(dkm);
		assertFalse(dkm.isEmpty());
		assertEquals(4, dkm.size());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.size());
		dkm.addToListFor(null, 'F');
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.size());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.size());
		dkm.addToListFor(Integer.valueOf(3), 'G');
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.size());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.size());
		dkm.addToListFor(Integer.valueOf(5), null);
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.size());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.size());
	}

	@Test
	public void testEmptyAddAll()
	{
		AbstractMapToList<Integer, Character> dkm = getMapToList();
		dkm.addAllToListFor(Integer.valueOf(1), null);
		assertFalse(dkm.containsListFor(Integer.valueOf(1)));
		dkm.addAllToListFor(Integer.valueOf(1), new ArrayList<Character>());
		assertFalse(dkm.containsListFor(Integer.valueOf(1)));
	}

	@Test
	public void testAddAll()
	{
		AbstractMapToList<Integer, Character> dkm = getMapToList();
		List<Character> l = new ArrayList<Character>();
		l.add(CONST_A);
		l.add(null);
		l.add(CONST_A);
		l.add(CONST_B);
		dkm.addAllToListFor(Integer.valueOf(1), l);
		assertTrue(dkm.containsListFor(Integer.valueOf(1)));
		assertEquals(4, dkm.sizeOfListFor(Integer.valueOf(1)));
		dkm.addToListFor(Integer.valueOf(1), CONST_D);
		assertEquals(4, l.size());
		// Check reference semantics!
		l.add(CONST_C);
		l.add(CONST_E);
		assertTrue(dkm.containsListFor(Integer.valueOf(1)));
		assertEquals(5, dkm.sizeOfListFor(Integer.valueOf(1)));
		l.clear();
		assertTrue(dkm.containsListFor(Integer.valueOf(1)));
		assertEquals(5, dkm.sizeOfListFor(Integer.valueOf(1)));
	}

	@Test
	public void testInstanceBehavior()
	{
		AbstractMapToList<Integer, Character> dkm = getMapToList();
		Character ca = Character.valueOf('a');
		Character cb = Character.valueOf('b');
		Character cc = Character.valueOf('c');
		Character ca1 = new Character('a');
		Integer i1 = Integer.valueOf(1);
		dkm.addToListFor(i1, ca);
		dkm.addToListFor(i1, cb);
		dkm.addToListFor(i1, cc);
		Integer i2 = Integer.valueOf(2);
		dkm.addToListFor(i2, ca);
		dkm.addToListFor(i2, ca);
		Integer i3 = Integer.valueOf(3);
		dkm.addToListFor(i3, cb);
		dkm.addToListFor(i3, cc);
		assertTrue(dkm.containsInList(i1, ca));
		assertTrue(dkm.containsInList(i1, ca1));
		assertTrue(dkm.removeFromListFor(i1, ca1));
		assertFalse(dkm.containsInList(i1, ca));

		assertTrue(dkm.containsInList(i2, ca));
		assertTrue(dkm.containsInList(i2, ca1));
		assertTrue(dkm.removeFromListFor(i2, ca1));
		// There were two
		assertTrue(dkm.containsInList(i2, ca));
		assertTrue(dkm.removeFromListFor(i2, ca));
		// There were two
		assertFalse(dkm.containsInList(i2, ca));
	}

	@Test
	public void testAddAllLists()
	{
		AbstractMapToList<Integer, Character> dkm = getMapToList();
		AbstractMapToList<Integer, Character> dkm2 = getMapToList();
		populate(dkm);
		dkm2.addAllLists(dkm);
		assertTrue(dkm.removeFromListFor(Integer.valueOf(1), CONST_A));
		assertTrue(dkm2.containsInList(Integer.valueOf(1), CONST_A));

		assertTrue(dkm2.removeFromListFor(Integer.valueOf(1), CONST_B));
		assertTrue(dkm.containsInList(Integer.valueOf(1), CONST_B));

		dkm.removeListFor(Integer.valueOf(1));
		assertFalse(dkm.containsListFor(Integer.valueOf(1)));
		assertTrue(dkm2.containsListFor(Integer.valueOf(1)));
	}

	@Test
	public void testGetElementInList()
	{
		AbstractMapToList<Integer, Character> dkm = getMapToList();
		try
		{
			dkm.getElementInList(Integer.valueOf(1), 0);
			fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		populate(dkm);
		try
		{
			dkm.getElementInList(Integer.valueOf(1), 3);
			fail("Expected IndexOutOfBoundsException");
		}
		catch (IndexOutOfBoundsException e)
		{
			// OK
		}
		assertEquals(CONST_A, dkm.getElementInList(Integer.valueOf(1), 0));
		assertEquals(CONST_B, dkm.getElementInList(Integer.valueOf(1), 1));
		assertEquals(CONST_C, dkm.getElementInList(Integer.valueOf(1), 2));
		dkm.addToListFor(Integer.valueOf(1), CONST_C);
		assertEquals(CONST_A, dkm.getElementInList(Integer.valueOf(1), 0));
		assertEquals(CONST_B, dkm.getElementInList(Integer.valueOf(1), 1));
		assertEquals(CONST_C, dkm.getElementInList(Integer.valueOf(1), 2));
		assertEquals(CONST_C, dkm.getElementInList(Integer.valueOf(1), 3));

		assertEquals(CONST_D, dkm.getElementInList(Integer.valueOf(2), 0));
		assertEquals(CONST_E, dkm.getElementInList(Integer.valueOf(2), 1));
		assertNull(dkm.getElementInList(Integer.valueOf(2), 2));

		assertEquals(CONST_F, dkm.getElementInList(null, 0));
	}
}
