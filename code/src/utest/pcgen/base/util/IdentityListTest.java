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
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class IdentityListTest extends TestCase
{

	IdentityList<Integer> ls;
	Integer a1 = new Integer(1);
	Integer a2 = new Integer(2);
	Integer b1 = new Integer(1);
	Integer b2 = new Integer(2);

	@Override
	@Before
	public void setUp()
	{
		ls = new IdentityList<Integer>();
	}

	@Test
	public void testIdentityAdd()
	{
		assertFalse(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertTrue(ls.isEmpty());
		assertEquals(0, ls.size());
		ls.add(a1);
		assertTrue(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertFalse(ls.isEmpty());
		assertEquals(1, ls.size());
		ls.add(a1);
		assertTrue(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertEquals(2, ls.size());
		assertFalse(ls.isEmpty());
		ls.clear();
		assertFalse(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertTrue(ls.isEmpty());
		assertEquals(0, ls.size());
		ls.add(a1);
		assertTrue(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertEquals(1, ls.size());
		assertFalse(ls.isEmpty());
	}

	@Test
	public void testEqualityAdd()
	{
		assertFalse(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertTrue(ls.isEmpty());
		assertEquals(0, ls.size());
		ls.add(a1);
		assertTrue(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertFalse(ls.isEmpty());
		assertEquals(1, ls.size());
		ls.add(b1);
		assertTrue(ls.contains(a1));
		assertTrue(ls.contains(b1));
		assertEquals(2, ls.size());
		assertFalse(ls.isEmpty());
		ls.clear();
		assertFalse(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertTrue(ls.isEmpty());
		assertEquals(0, ls.size());
		ls.add(a1);
		assertTrue(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertEquals(1, ls.size());
		assertFalse(ls.isEmpty());
	}

	@Test
	public void testIdentityRemove()
	{
		assertTrue(ls.isEmpty());
		assertEquals(0, ls.size());
		ls.add(a1);
		assertFalse(ls.isEmpty());
		assertEquals(1, ls.size());
		ls.add(b1);
		assertEquals(2, ls.size());
		assertFalse(ls.isEmpty());
		ls.remove(b1);
		assertEquals(1, ls.size());
		assertFalse(ls.isEmpty());
		assertTrue(a1 == ls.get(0));
	}

	@Test
	public void testIterator()
	{
		Iterator<Integer> it = ls.iterator();
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
		ls.add(a1);
		ls.add(a2);
		ls.add(b1);
		ls.add(b2);
		ls.remove(b1);
		ls.remove(a2);
		Iterator<Integer> iter = ls.iterator();
		assertNotNull(iter);
		assertTrue(iter.hasNext());
		Object o1 = iter.next();
		assertTrue(o1 == a1);
		assertTrue(iter.hasNext());
		Object o2 = iter.next();
		assertTrue(o2 == b2);
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
		Iterator<Integer> it = ls.iterator();
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
		ls.add(a1);
		ls.add(b1);
		ls.add(b2);
		Iterator<Integer> iter = ls.iterator();
		assertNotNull(iter);
		assertTrue(iter.hasNext());
		Object o1 = iter.next();
		assertTrue(o1 == a1);
		assertTrue(iter.hasNext());
		Object o2 = iter.next();
		assertTrue(o2 == b1);
		iter.remove();
		assertFalse(ls.contains(o2));
		assertTrue(ls.contains(a1));
		Object o3 = iter.next();
		assertTrue(o3 == b2);
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
	public void testIdentityAddConstructor()
	{
		try
		{
			ls = new IdentityList<Integer>(null);
			fail();
		}
		catch (NullPointerException e)
		{
			//:)
		}
		ls = new IdentityList<Integer>(Arrays.asList(new Integer[]{}));
		assertFalse(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertTrue(ls.isEmpty());
		assertEquals(0, ls.size());
		ls = new IdentityList<Integer>(Arrays.asList(new Integer[]{a1}));
		assertTrue(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertFalse(ls.isEmpty());
		assertEquals(1, ls.size());
		ls = new IdentityList<Integer>(Arrays.asList(new Integer[]{a1, a1}));
		assertTrue(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertEquals(2, ls.size());
		assertFalse(ls.isEmpty());
		ls.clear();
		assertFalse(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertTrue(ls.isEmpty());
		assertEquals(0, ls.size());
		List<Integer> list = new ArrayList<Integer>();
		list.add(a1);
		list.add(b1);
		ls = new IdentityList<Integer>(list);
		assertTrue(ls.contains(a1));
		assertTrue(ls.contains(b1));
		assertEquals(2, ls.size());
		assertFalse(ls.isEmpty());
		//prove isolaation
		list.remove(a1);
		assertTrue(ls.contains(a1));
		assertTrue(ls.contains(b1));
		assertEquals(2, ls.size());
		assertFalse(ls.isEmpty());
		assertEquals(1, list.size());
		ls.add(a2);
		assertEquals(1, list.size());
	}

	
}
