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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class GenericMapToListTest_IdentityHash extends TestCase
{

	private static final Integer CONST_5 = Integer.valueOf(5);

	private static final Integer CONST_2 = Integer.valueOf(2);

	private static final Integer CONST_1A = new Integer(1);

	private static final Integer CONST_1B = new Integer(1);

	private static final Character CONST_E = 'E';

	private static final Character CONST_C = 'C';

	private static final Character CONST_F = 'F';

	private static final Character CONST_D = 'D';

	private static final Character CONST_B = 'B';

	private static final Character CONST_A = 'A';

	GenericMapToList<Integer, Character> dkm;

	@Override
	@Before
	public void setUp()
	{
		dkm = GenericMapToList.getMapToList(IdentityHashMap.class);
	}

	public void populate()
	{
		dkm.addToListFor(CONST_1A, CONST_A);
		dkm.addToListFor(CONST_1B, CONST_B);
		dkm.addToListFor(CONST_1B, CONST_C);
		dkm.addToListFor(CONST_2, CONST_D);
		dkm.addToListFor(CONST_2, CONST_E);
		dkm.addToListFor(CONST_2, null);
		dkm.addToListFor(null, CONST_F);
		dkm.addToListFor(CONST_5, null);
	}

	@Test
	public void testInitializeListFor()
	{
		assertNull(dkm.getListFor(CONST_1A));
		assertNull(dkm.getListFor(CONST_1B));
		dkm.initializeListFor(CONST_1A);
		List<Character> l = dkm.getListFor(CONST_1A);
		assertEquals(0, l.size());
		assertNull(dkm.getListFor(CONST_1B));
		dkm.initializeListFor(CONST_1B);
		l = dkm.getListFor(CONST_1B);
		assertEquals(0, l.size());
		try
		{
			dkm.initializeListFor(CONST_1A);
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
		assertNull(dkm.getListFor(null));
		assertNull(dkm.getListFor(CONST_1A));
		populate();
		List<Character> l = dkm.getListFor(CONST_1A);
		assertEquals(1, l.size());
		assertTrue(l.contains(CONST_A));
		dkm.addToListFor(CONST_1B, CONST_C);
		l = dkm.getListFor(CONST_1B);
		assertEquals(3, l.size());
		assertTrue(l.contains(CONST_B));
		assertTrue(l.contains(CONST_C));
		// two of them
		l.remove(Character.valueOf(CONST_C));
		assertTrue(l.contains(CONST_C));
		l = dkm.getListFor(CONST_2);
		assertEquals(3, l.size());
		assertTrue(l.contains(CONST_D));
		assertTrue(l.contains(CONST_E));
		assertTrue(l.contains(null));
		dkm.addToListFor(CONST_2, null);
		l = dkm.getListFor(CONST_2);
		assertEquals(4, l.size());
		assertTrue(l.contains(CONST_D));
		assertTrue(l.contains(CONST_E));
		assertTrue(l.contains(null));
		// Two of them.
		l.remove(null);
		assertTrue(l.contains(null));
		assertNull(dkm.getListFor(Integer.valueOf(4)));
		l = dkm.getListFor(null);
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
		assertFalse(dkm.containsListFor(CONST_1A));
		assertFalse(dkm.containsListFor(null));
		populate();
		assertTrue(dkm.containsListFor(CONST_1A));
		// Keys are .equals items, not instance
		assertFalse(dkm.containsListFor(new Integer(1)));
		assertTrue(dkm.containsListFor(CONST_2));
		assertTrue(dkm.containsListFor(CONST_5));
		assertFalse(dkm.containsListFor(Integer.valueOf(-4)));
		assertTrue(dkm.containsListFor(null));
	}

	@Test
	public void testRemoveListFor()
	{
		assertNull(dkm.removeListFor(CONST_1A));
		assertNull(dkm.removeListFor(null));
		populate();
		List<Character> l = dkm.removeListFor(CONST_1A);
		assertEquals(1, l.size());
		assertTrue(l.contains(CONST_A));
		assertFalse(dkm.containsListFor(CONST_1A));
		assertNull(dkm.getListFor(CONST_1A));
		l = dkm.removeListFor(CONST_2);
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
		assertFalse(dkm.removeFromListFor(CONST_1B, CONST_D));
		populate();
		assertTrue(dkm.removeFromListFor(CONST_1B, CONST_B));
		assertTrue(dkm.containsListFor(CONST_1B));
		// Keys are instance
		assertFalse(dkm.containsListFor(new Integer(1)));
		assertEquals(1, dkm.sizeOfListFor(CONST_1B));
		assertFalse(dkm.removeFromListFor(CONST_1B, CONST_A));
		assertTrue(dkm.containsListFor(CONST_1B));
		assertFalse(dkm.removeFromListFor(CONST_1B, CONST_B));
		assertTrue(dkm.removeFromListFor(CONST_1B, CONST_C));
		assertEquals(0, dkm.sizeOfListFor(CONST_1B));
		assertFalse(dkm.containsListFor(CONST_1B));

		// add a second :)
		dkm.addToListFor(CONST_2, CONST_D);
		assertFalse(dkm.removeFromListFor(CONST_2, CONST_A));
		assertTrue(dkm.containsListFor(CONST_2));
		assertEquals(4, dkm.sizeOfListFor(CONST_2));
		assertFalse(dkm.removeFromListFor(CONST_2, CONST_A));
		assertTrue(dkm.removeFromListFor(CONST_2, CONST_D));
		assertEquals(3, dkm.sizeOfListFor(CONST_2));
		assertTrue(dkm.containsListFor(CONST_2));
		assertTrue(dkm.removeFromListFor(CONST_2, CONST_E));
		assertEquals(2, dkm.sizeOfListFor(CONST_2));
		assertTrue(dkm.containsListFor(CONST_2));
		assertTrue(dkm.removeFromListFor(CONST_2, null));
		assertEquals(1, dkm.sizeOfListFor(CONST_2));
		assertTrue(dkm.containsListFor(CONST_2));
		assertTrue(dkm.removeFromListFor(CONST_2, CONST_D));
		assertEquals(0, dkm.sizeOfListFor(CONST_2));
		assertFalse(dkm.containsListFor(CONST_2));

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
		assertFalse(dkm.containsInList(CONST_1B, CONST_D));
		populate();
		assertTrue(dkm.containsInList(CONST_1B, CONST_B));
		// Keys are instance
		assertFalse(dkm.containsInList(new Integer(1), CONST_B));
		assertTrue(dkm.containsInList(CONST_1B, CONST_B));
		assertTrue(dkm.containsInList(CONST_1B, CONST_C));
		assertFalse(dkm.containsInList(CONST_1B, CONST_D));

		// add a second :)
		dkm.addToListFor(CONST_1B, CONST_C);
		assertTrue(dkm.containsInList(CONST_1B, CONST_C));

		// Test null stuff :)
		assertTrue(dkm.containsInList(CONST_2, null));

		assertFalse(dkm.containsInList(null, CONST_A));
		assertTrue(dkm.containsInList(null, CONST_F));
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
		assertTrue(s3.contains(CONST_1A));
		assertTrue(s3.contains(CONST_1B));
		assertTrue(s3.contains(CONST_2));
		assertTrue(s3.contains(CONST_5));
		assertTrue(s3.contains(null));
	}

	@Test
	public void testClearIsEmpty()
	{
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.size());
		populate();
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
		dkm.addToListFor(Integer.valueOf(3), 'G');
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.size());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.size());
		dkm.addToListFor(CONST_5, null);
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.size());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.size());
	}

	@Test
	public void testEmptyAddAll()
	{
		dkm.addAllToListFor(CONST_1A, null);
		assertFalse(dkm.containsListFor(CONST_1A));
		dkm.addAllToListFor(CONST_1A, new ArrayList<Character>());
		assertFalse(dkm.containsListFor(CONST_1A));
	}

	@Test
	public void testAddAll()
	{
		List<Character> l = new ArrayList<Character>();
		l.add(CONST_A);
		l.add(null);
		l.add(CONST_A);
		l.add(CONST_B);
		dkm.addAllToListFor(CONST_1A, l);
		assertTrue(dkm.containsListFor(CONST_1A));
		assertEquals(4, dkm.sizeOfListFor(CONST_1A));
		dkm.addToListFor(CONST_1A, CONST_D);
		assertEquals(4, l.size());
		// Check reference semantics!
		l.add(CONST_C);
		l.add(CONST_E);
		assertTrue(dkm.containsListFor(CONST_1A));
		assertEquals(5, dkm.sizeOfListFor(CONST_1A));
		l.clear();
		assertTrue(dkm.containsListFor(CONST_1A));
		assertEquals(5, dkm.sizeOfListFor(CONST_1A));
	}

	@Test
	public void testInstanceBehavior()
	{
		Character ca = Character.valueOf('a');
		Character cb = Character.valueOf('b');
		Character cc = Character.valueOf('c');
		Character ca1 = new Character('a');
		Integer i1 = CONST_1A;
		dkm.addToListFor(i1, ca);
		dkm.addToListFor(i1, cb);
		dkm.addToListFor(i1, cc);
		Integer i2 = CONST_2;
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
		HashMapToList<Integer, Character> dkm2 = new HashMapToList<Integer, Character>();
		populate();
		dkm2.addAllLists(dkm);
		assertTrue(dkm.removeFromListFor(CONST_1B, CONST_C));
		assertTrue(dkm2.containsInList(CONST_1B, CONST_C));

		assertTrue(dkm2.removeFromListFor(CONST_1B, CONST_B));
		assertTrue(dkm.containsInList(CONST_1B, CONST_B));

		dkm.removeListFor(CONST_1B);
		assertFalse(dkm.containsListFor(CONST_1B));
		assertTrue(dkm2.containsListFor(CONST_1B));
	}
}
