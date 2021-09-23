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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.testsupport.TestSupport;

public class ListSetTest
{

	private ListSet<Integer> ls, ls2, ls3, ls4;

	private Comparator<Integer> c = (arg0, arg1) ->
	{
		if (arg0 == arg1)
		{
			return 0;
		}
		int comp = arg0.compareTo(arg1);
		if (comp != 0)
		{
			return comp;
		}
		if (System.identityHashCode(arg0) < System.identityHashCode(arg1))
		{
			return -1;
		}
		return 1;
	};

	@BeforeEach
	void setUp()
	{
		ls = new ListSet<>();
		ls2 = new ListSet<>(15);
		ls3 = new ListSet<>(c);
		ls4 = new ListSet<>(14, c);
	}

	@AfterEach
	void tearDown()
	{
		ls = null;
		ls2 = null;
		ls3 = null;
		ls4 = null;
	}


	@Test
	public void testArchitecture()
	{
		assertFalse(ls instanceof List, "ListSet must not implement List, "
			+ "as it does not always return true to add(), "
			+ "as defined by the List interface");
	}

	@Test
	public void testSize()
	{
		testSetSize(ls);
		testSetSize(ls2);
		testSetSize(ls3);
		testSetSize(ls4);
	}

	public static void testSetSize(ListSet<Integer> set)
	{
		assertTrue(set.isEmpty());
		assertEquals(0, set.size());
		set.add(TestSupport.I1);
		assertFalse(set.isEmpty());
		assertEquals(1, set.size());
		set.add(TestSupport.I2);
		assertEquals(2, set.size());
		set.ensureCapacity(16);
		assertEquals(2, set.size());
		assertFalse(set.isEmpty());
		set.clear();
		assertTrue(set.isEmpty());
		set.ensureCapacity(34);
		assertTrue(set.isEmpty());
		assertEquals(0, set.size());
		set.add(TestSupport.I1);
		assertEquals(1, set.size());
		set.trimToSize();
		assertFalse(set.isEmpty());
		assertEquals(1, set.size());
	}

	@Test
	public void testAddRemove()
	{
		testBasicSet(ls);
		testBasicSet(ls2);
		testIdentitySet(ls3);
		testIdentitySet(ls4);
	}

	public static void testBasicSet(ListSet<Integer> set)
	{
		assertTrue(set.isEmpty());
		assertFalse(set.remove(TestSupport.I1));
		assertFalse(set.contains(new Integer(1)));
		assertFalse(set.contains(TestSupport.I1));
		assertEquals(0, set.size());
		assertEquals(0, set.size());
		assertFalse(set.contains(TestSupport.I1));
		assertTrue(set.isEmpty());
		set.add(TestSupport.I1);
		assertEquals(1, set.size());
		assertTrue(set.contains(TestSupport.I1));
		assertTrue(set.contains(new Integer(1)));
		assertFalse(set.contains(TestSupport.I2));
		assertFalse(set.isEmpty());
		assertTrue(set.remove(TestSupport.I1));
		assertFalse(set.contains(TestSupport.I1));
		assertEquals(0, set.size());
		assertTrue(set.isEmpty());
		assertFalse(set.remove(TestSupport.I1));
		assertFalse(set.contains(TestSupport.I1));
		assertEquals(0, set.size());
		assertTrue(set.isEmpty());
		set.add(TestSupport.I1);
		assertEquals(1, set.size());
		assertTrue(set.contains(TestSupport.I1));
		assertTrue(set.contains(new Integer(1)));
		assertFalse(set.isEmpty());
		set.add(TestSupport.I1);
		assertEquals(1, set.size());
		set.add(new Integer(1)); // Keep NEW (instance identity part of
		// test!)
		assertEquals(1, set.size());
		set.add(TestSupport.I2);
		assertEquals(2, set.size());
		assertTrue(set.contains(TestSupport.I2));
		set.add(new Integer(1)); // Keep NEW (instance identity part of
		// test!)
		assertEquals(2, set.size());
		set.add(new Integer(2)); // Keep NEW (instance identity part of
		// test!)
		assertEquals(2, set.size());
		set.ensureCapacity(16);
		set.add(new Integer(2)); // Keep NEW (instance identity part of
		// test!)
		assertEquals(2, set.size());
		assertTrue(set.contains(TestSupport.I1));
		assertTrue(set.contains(new Integer(1)));
		assertTrue(set.remove(TestSupport.I1));
		assertEquals(1, set.size());
		set.add(TestSupport.I3);
		assertEquals(2, set.size());
		set.add(TestSupport.I4);
		assertEquals(3, set.size());
		set.add(TestSupport.I5);
		assertEquals(4, set.size());
		assertFalse(set.isEmpty());
		assertTrue(set.remove(new Integer(2))); // Keep NEW (instance identity
		// part of test!)
		assertEquals(3, set.size());
		assertTrue(set.remove(new Integer(4))); // Keep NEW (instance identity
		// part of test!)
		assertEquals(2, set.size());
		assertTrue(set.contains(TestSupport.I5));
		assertTrue(set.contains(new Integer(5)));
	}

	public static void testIdentitySet(ListSet<Integer> set)
	{
		assertTrue(set.isEmpty());
		assertFalse(set.remove(TestSupport.I1));
		assertFalse(set.contains(TestSupport.I1));
		assertFalse(set.contains(new Integer(1)));
		assertEquals(0, set.size());
		assertEquals(0, set.size());
		set.add(TestSupport.I1);
		assertEquals(1, set.size());
		assertTrue(set.contains(TestSupport.I1));
		assertFalse(set.contains(new Integer(1)));
		assertFalse(set.isEmpty());
		assertTrue(set.remove(TestSupport.I1));
		assertEquals(0, set.size());
		assertTrue(set.isEmpty());
		assertFalse(set.contains(TestSupport.I1));
		assertFalse(set.contains(new Integer(1)));
		assertFalse(set.remove(TestSupport.I1));
		assertEquals(0, set.size());
		assertTrue(set.isEmpty());
		set.add(TestSupport.I1);
		assertEquals(1, set.size());
		set.add(TestSupport.I1);
		assertEquals(1, set.size());
		set.add(new Integer(1)); // Keep NEW (instance identity part of
		// test!)
		assertTrue(set.contains(TestSupport.I1));
		assertFalse(set.contains(new Integer(1)));
		assertEquals(2, set.size());
		set.add(TestSupport.I2);
		assertEquals(3, set.size());
		set.add(new Integer(1)); // Keep NEW (instance identity part of
		// test!)
		assertEquals(4, set.size());
		set.add(new Integer(2)); // Keep NEW (instance identity part of
		// test!)
		assertEquals(5, set.size());
		set.ensureCapacity(16);
		set.add(new Integer(2)); // Keep NEW (instance identity part of
		// test!)
		assertEquals(6, set.size());
		assertTrue(set.remove(TestSupport.I1));
		assertFalse(set.contains(TestSupport.I1));
		assertFalse(set.contains(new Integer(1)));
		assertEquals(5, set.size());
		set.add(TestSupport.I3);
		assertEquals(6, set.size());
		set.add(TestSupport.I4);
		assertEquals(7, set.size());
		set.add(TestSupport.I5);
		assertEquals(8, set.size());
		assertFalse(set.remove(new Integer(2))); // Keep NEW (instance
		// identity part of
		// test!)
		assertEquals(8, set.size());
		assertFalse(set.remove(new Integer(4))); // Keep NEW (instance
		// identity part of
		// test!)
		assertEquals(8, set.size());
		assertTrue(set.contains(TestSupport.I5));
		assertFalse(set.contains(new Integer(1)));
		Integer nine = Integer.valueOf(9);
		set.add(nine);
		assertEquals(9, set.size());
		assertTrue(set.remove(nine));
		assertEquals(8, set.size());
		assertFalse(set.remove(nine));
		assertEquals(8, set.size());
		assertFalse(set.isEmpty());
	}

	@Test
	public void testIterator()
	{
		testIterator(ls);
		testIterator(ls2);
		testIterator(ls3);
		testIterator(ls4);
	}

	public static void testIterator(ListSet<Integer> set)
	{
		Iterator<Integer> it = set.iterator();
		assertNotNull(it);
		assertFalse(it.hasNext());
		assertThrows(NoSuchElementException.class, () -> it.next());
		Integer five = TestSupport.I5;
		set.add(five);
		Integer three = TestSupport.I3;
		set.add(three);
		Integer one = TestSupport.I1;
		set.add(one);
		List<Integer> total = new ArrayList<>();
		total.add(one);
		total.add(three);
		total.add(five);
		Iterator<Integer> iter = set.iterator();
		assertNotNull(iter);
		assertTrue(iter.hasNext());
		Object o1 = iter.next();
		//Yes, this needs to be an identity test, not .equals
		assertTrue(o1 == one || o1 == three || o1 == five);
		assertTrue(total.remove(o1));
		assertTrue(iter.hasNext());
		Object o2 = iter.next();
		//Yes, this needs to be an identity test, not .equals
		assertTrue(o2 == one || o2 == three || o2 == five);
		assertTrue(total.remove(o2));
		assertTrue(iter.hasNext());
		Object o3 = iter.next();
		//Yes, this needs to be an identity test, not .equals
		assertTrue(o3 == one || o3 == three || o3 == five);
		assertTrue(total.remove(o3));
		assertTrue(total.isEmpty());
		assertFalse(iter.hasNext());
		assertThrows(NoSuchElementException.class, () -> iter.next());
	}

	@Test
	public void testIteratorRemove()
	{
		testIteratorRemove(ls);
		testIteratorRemove(ls2);
		testIteratorRemove(ls3);
		testIteratorRemove(ls4);
	}

	public static void testIteratorRemove(ListSet<Integer> set)
	{
		Iterator<Integer> it = set.iterator();
		assertNotNull(it);
		assertFalse(it.hasNext());
		assertThrows(NoSuchElementException.class, () -> it.next());
		Integer five = TestSupport.I5;
		set.add(five);
		Integer three = TestSupport.I3;
		set.add(three);
		Integer one = TestSupport.I1;
		set.add(one);
		List<Integer> total = new ArrayList<>();
		total.add(one);
		total.add(three);
		total.add(five);
		Iterator<Integer> iter = set.iterator();
		assertNotNull(iter);
		assertTrue(iter.hasNext());
		Object o1 = iter.next();
		//Yes, this needs to be an identity test, not .equals
		assertTrue(o1 == one || o1 == three || o1 == five);
		assertTrue(total.remove(o1));
		assertTrue(iter.hasNext());
		Object o2 = iter.next();
		//Yes, this needs to be an identity test, not .equals
		assertTrue(o2 == one || o2 == three || o2 == five);
		assertTrue(total.remove(o2));
		iter.remove();
		assertFalse(set.contains(o2));
		assertTrue(iter.hasNext());
		Object o3 = iter.next();
		//Yes, this needs to be an identity test, not .equals
		assertTrue(o3 == one || o3 == three || o3 == five);
		assertTrue(total.remove(o3));
		assertTrue(total.isEmpty());
		assertFalse(iter.hasNext());
	}

	@Test
	public void testIdentityAddConstructor()
	{
		Collection<Integer> nc = null;
		assertThrows(NullPointerException.class, () -> new ListSet<>(nc));
		ls = new ListSet<>(Arrays.asList(new Integer[]{}));
		assertFalse(ls.contains(TestSupport.I1));
		assertFalse(ls.contains(TestSupport.I2));
		assertTrue(ls.isEmpty());
		assertEquals(0, ls.size());
		ls = new ListSet<>(Arrays.asList(new Integer[]{TestSupport.I1}));
		assertTrue(ls.contains(TestSupport.I1));
		assertFalse(ls.contains(TestSupport.I2));
		assertFalse(ls.isEmpty());
		assertEquals(1, ls.size());
		ls = new ListSet<>(Arrays.asList(new Integer[]{TestSupport.I1,
				TestSupport.I1}));
		assertTrue(ls.contains(TestSupport.I1));
		assertFalse(ls.contains(TestSupport.I2));
		assertEquals(2, ls.size());
		assertFalse(ls.isEmpty());
		ls.clear();
		assertFalse(ls.contains(TestSupport.I1));
		assertFalse(ls.contains(TestSupport.I2));
		assertTrue(ls.isEmpty());
		assertEquals(0, ls.size());
		List<Integer> list = new ArrayList<>();
		list.add(TestSupport.I1);
		list.add(TestSupport.I2);
		ls = new ListSet<>(list);
		assertTrue(ls.contains(TestSupport.I1));
		assertTrue(ls.contains(TestSupport.I2));
		assertEquals(2, ls.size());
		assertFalse(ls.isEmpty());
		//prove isolation
		list.remove(TestSupport.I1);
		assertTrue(ls.contains(TestSupport.I1));
		assertTrue(ls.contains(TestSupport.I2));
		assertEquals(2, ls.size());
		assertFalse(ls.isEmpty());
		assertEquals(1, list.size());
		ls.add(TestSupport.I3);
		assertEquals(1, list.size());
	}

}
