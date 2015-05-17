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
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class ListSetTest extends TestCase
{

	ListSet<Integer> ls, ls2, ls3, ls4;

	Comparator<Integer> c = new Comparator<Integer>()
	{

        @Override
		public int compare(Integer arg0, Integer arg1)
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
		}

	};

	@Override
	@Before
	public void setUp()
	{
		ls = new ListSet<Integer>();
		ls2 = new ListSet<Integer>(15);
		ls3 = new ListSet<Integer>(c);
		ls4 = new ListSet<Integer>(14, c);
	}

	@Test
	public void testArchitecture()
	{
		assertFalse(
				"ListSet must not implement List, as it does not always return true to add(), as defined by the List interface",
				ls instanceof List);
	}

	@Test
	public void testSize()
	{
		testSetSize(ls);
		testSetSize(ls2);
		testSetSize(ls3);
		testSetSize(ls4);
	}

	public void testSetSize(ListSet<Integer> set)
	{
		assertTrue(set.isEmpty());
		assertEquals(0, set.size());
		set.add(Integer.valueOf(1));
		assertFalse(set.isEmpty());
		assertEquals(1, set.size());
		set.add(Integer.valueOf(2));
		assertEquals(2, set.size());
		set.ensureCapacity(16);
		assertEquals(2, set.size());
		assertFalse(set.isEmpty());
		set.clear();
		assertTrue(set.isEmpty());
		set.ensureCapacity(34);
		assertTrue(set.isEmpty());
		assertEquals(0, set.size());
		set.add(Integer.valueOf(1));
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

	public void testBasicSet(ListSet<Integer> set)
	{
		assertTrue(set.isEmpty());
		assertFalse(set.remove(Integer.valueOf(1)));
		assertFalse(set.contains(new Integer(1)));
		assertFalse(set.contains(Integer.valueOf(1)));
		assertEquals(0, set.size());
		assertEquals(0, set.size());
		assertFalse(set.contains(Integer.valueOf(1)));
		assertTrue(set.isEmpty());
		set.add(Integer.valueOf(1));
		assertEquals(1, set.size());
		assertTrue(set.contains(Integer.valueOf(1)));
		assertTrue(set.contains(new Integer(1)));
		assertFalse(set.contains(Integer.valueOf(2)));
		assertFalse(set.isEmpty());
		assertTrue(set.remove(Integer.valueOf(1)));
		assertFalse(set.contains(Integer.valueOf(1)));
		assertEquals(0, set.size());
		assertTrue(set.isEmpty());
		assertFalse(set.remove(Integer.valueOf(1)));
		assertFalse(set.contains(Integer.valueOf(1)));
		assertEquals(0, set.size());
		assertTrue(set.isEmpty());
		set.add(Integer.valueOf(1));
		assertEquals(1, set.size());
		assertTrue(set.contains(Integer.valueOf(1)));
		assertTrue(set.contains(new Integer(1)));
		assertFalse(set.isEmpty());
		set.add(Integer.valueOf(1));
		assertEquals(1, set.size());
		set.add(new Integer(1)); // Keep NEW (instance identity part of
		// test!)
		assertEquals(1, set.size());
		set.add(Integer.valueOf(2));
		assertEquals(2, set.size());
		assertTrue(set.contains(Integer.valueOf(2)));
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
		assertTrue(set.contains(Integer.valueOf(1)));
		assertTrue(set.contains(new Integer(1)));
		assertTrue(set.remove(Integer.valueOf(1)));
		assertEquals(1, set.size());
		set.add(Integer.valueOf(3));
		assertEquals(2, set.size());
		set.add(Integer.valueOf(4));
		assertEquals(3, set.size());
		set.add(Integer.valueOf(5));
		assertEquals(4, set.size());
		assertFalse(set.isEmpty());
		assertTrue(set.remove(new Integer(2))); // Keep NEW (instance identity
		// part of test!)
		assertEquals(3, set.size());
		assertTrue(set.remove(new Integer(4))); // Keep NEW (instance identity
		// part of test!)
		assertEquals(2, set.size());
		assertTrue(set.contains(Integer.valueOf(5)));
		assertTrue(set.contains(new Integer(5)));
	}

	public void testIdentitySet(ListSet<Integer> set)
	{
		assertTrue(set.isEmpty());
		assertFalse(set.remove(Integer.valueOf(1)));
		assertFalse(set.contains(Integer.valueOf(1)));
		assertFalse(set.contains(new Integer(1)));
		assertEquals(0, set.size());
		assertEquals(0, set.size());
		set.add(Integer.valueOf(1));
		assertEquals(1, set.size());
		assertTrue(set.contains(Integer.valueOf(1)));
		assertFalse(set.contains(new Integer(1)));
		assertFalse(set.isEmpty());
		assertTrue(set.remove(Integer.valueOf(1)));
		assertEquals(0, set.size());
		assertTrue(set.isEmpty());
		assertFalse(set.contains(Integer.valueOf(1)));
		assertFalse(set.contains(new Integer(1)));
		assertFalse(set.remove(Integer.valueOf(1)));
		assertEquals(0, set.size());
		assertTrue(set.isEmpty());
		set.add(Integer.valueOf(1));
		assertEquals(1, set.size());
		set.add(Integer.valueOf(1));
		assertEquals(1, set.size());
		set.add(new Integer(1)); // Keep NEW (instance identity part of
		// test!)
		assertTrue(set.contains(Integer.valueOf(1)));
		assertFalse(set.contains(new Integer(1)));
		assertEquals(2, set.size());
		set.add(Integer.valueOf(2));
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
		assertTrue(set.remove(Integer.valueOf(1)));
		assertFalse(set.contains(Integer.valueOf(1)));
		assertFalse(set.contains(new Integer(1)));
		assertEquals(5, set.size());
		set.add(Integer.valueOf(3));
		assertEquals(6, set.size());
		set.add(Integer.valueOf(4));
		assertEquals(7, set.size());
		set.add(Integer.valueOf(5));
		assertEquals(8, set.size());
		assertFalse(set.remove(new Integer(2))); // Keep NEW (instance
		// identity part of
		// test!)
		assertEquals(8, set.size());
		assertFalse(set.remove(new Integer(4))); // Keep NEW (instance
		// identity part of
		// test!)
		assertEquals(8, set.size());
		assertTrue(set.contains(Integer.valueOf(5)));
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

	public void testIterator(ListSet<Integer> set)
	{
		Iterator<Integer> it = set.iterator();
		assertNotNull(it);
		assertFalse(it.hasNext());
		try
		{
			it.next();
			fail();
		}
		catch (NoSuchElementException ise)
		{
			// Yes!
		}
		Integer five = Integer.valueOf(5);
		set.add(five);
		Integer three = Integer.valueOf(3);
		set.add(three);
		Integer one = Integer.valueOf(1);
		set.add(one);
		List<Integer> total = new ArrayList<Integer>();
		total.add(one);
		total.add(three);
		total.add(five);
		Iterator<Integer> iter = set.iterator();
		assertNotNull(iter);
		assertTrue(iter.hasNext());
		Object o1 = iter.next();
		assertTrue(o1 == one || o1 == three || o1 == five);
		assertTrue(total.remove(o1));
		assertTrue(iter.hasNext());
		Object o2 = iter.next();
		assertTrue(o2 == one || o2 == three || o2 == five);
		assertTrue(total.remove(o2));
		assertTrue(iter.hasNext());
		Object o3 = iter.next();
		assertTrue(o3 == one || o3 == three || o3 == five);
		assertTrue(total.remove(o3));
		assertTrue(total.isEmpty());
		assertFalse(iter.hasNext());
		try
		{
			iter.next();
			fail();
		}
		catch (NoSuchElementException ise)
		{
			// Yes!
		}
	}

	@Test
	public void testIteratorRemove()
	{
		testIteratorRemove(ls);
		testIteratorRemove(ls2);
		testIteratorRemove(ls3);
		testIteratorRemove(ls4);
	}

	public void testIteratorRemove(ListSet<Integer> set)
	{
		Iterator<Integer> it = set.iterator();
		assertNotNull(it);
		assertFalse(it.hasNext());
		try
		{
			it.next();
			fail();
		}
		catch (NoSuchElementException ise)
		{
			// Yes!
		}
		Integer five = Integer.valueOf(5);
		set.add(five);
		Integer three = Integer.valueOf(3);
		set.add(three);
		Integer one = Integer.valueOf(1);
		set.add(one);
		List<Integer> total = new ArrayList<Integer>();
		total.add(one);
		total.add(three);
		total.add(five);
		Iterator<Integer> iter = set.iterator();
		assertNotNull(iter);
		assertTrue(iter.hasNext());
		Object o1 = iter.next();
		assertTrue(o1 == one || o1 == three || o1 == five);
		assertTrue(total.remove(o1));
		assertTrue(iter.hasNext());
		Object o2 = iter.next();
		assertTrue(o2 == one || o2 == three || o2 == five);
		assertTrue(total.remove(o2));
		iter.remove();
		assertFalse(set.contains(o2));
		assertTrue(iter.hasNext());
		Object o3 = iter.next();
		assertTrue(o3 == one || o3 == three || o3 == five);
		assertTrue(total.remove(o3));
		assertTrue(total.isEmpty());
		assertFalse(iter.hasNext());
	}

	@Test
	public void testIdentityAddConstructor()
	{
		try
		{
			Collection<Integer> nc = null;
			ls = new ListSet<Integer>(nc);
			fail();
		}
		catch (NullPointerException e)
		{
			//:)
		}
		ls = new ListSet<Integer>(Arrays.asList(new Integer[]{}));
		assertFalse(ls.contains(Integer.valueOf(1)));
		assertFalse(ls.contains(Integer.valueOf(2)));
		assertTrue(ls.isEmpty());
		assertEquals(0, ls.size());
		ls = new ListSet<Integer>(Arrays.asList(new Integer[]{Integer.valueOf(1)}));
		assertTrue(ls.contains(Integer.valueOf(1)));
		assertFalse(ls.contains(Integer.valueOf(2)));
		assertFalse(ls.isEmpty());
		assertEquals(1, ls.size());
		ls = new ListSet<Integer>(Arrays.asList(new Integer[]{Integer.valueOf(1), Integer.valueOf(1)}));
		assertTrue(ls.contains(Integer.valueOf(1)));
		assertFalse(ls.contains(Integer.valueOf(2)));
		assertEquals(2, ls.size());
		assertFalse(ls.isEmpty());
		ls.clear();
		assertFalse(ls.contains(Integer.valueOf(1)));
		assertFalse(ls.contains(Integer.valueOf(2)));
		assertTrue(ls.isEmpty());
		assertEquals(0, ls.size());
		List<Integer> list = new ArrayList<Integer>();
		list.add(Integer.valueOf(1));
		list.add(Integer.valueOf(2));
		ls = new ListSet<Integer>(list);
		assertTrue(ls.contains(Integer.valueOf(1)));
		assertTrue(ls.contains(Integer.valueOf(2)));
		assertEquals(2, ls.size());
		assertFalse(ls.isEmpty());
		//prove isolaation
		list.remove(Integer.valueOf(1));
		assertTrue(ls.contains(Integer.valueOf(1)));
		assertTrue(ls.contains(Integer.valueOf(2)));
		assertEquals(2, ls.size());
		assertFalse(ls.isEmpty());
		assertEquals(1, list.size());
		ls.add(Integer.valueOf(3));
		assertEquals(1, list.size());
	}

}
