/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
import java.util.IdentityHashMap;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import pcgen.testsupport.NoPublicZeroArgConstructorMap;
import pcgen.testsupport.NoZeroArgConstructorMap;
import pcgen.testsupport.StrangeMap;

public class WrappedMapSetTest extends TestCase
{

	WrappedMapSet<Integer> ls;
	Integer a1 = new Integer(1);
	Integer a2 = new Integer(2);
	Integer b1 = new Integer(1);
	Integer b2 = new Integer(2);

	@Override
	@Before
	public void setUp()
	{
		ls = new WrappedMapSet<Integer>(IdentityHashMap.class);
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
		assertEquals(1, ls.size());
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
		ls.remove(b1);
		assertFalse(ls.isEmpty());
		assertEquals(1, ls.size());
		ls.add(b1);
		assertEquals(2, ls.size());
		assertFalse(ls.isEmpty());
		ls.remove(b1);
		assertEquals(1, ls.size());
		assertFalse(ls.isEmpty());
		assertTrue(a1 == ls.iterator().next());
	}

	@Test
	public void testIdentityAddConstructor()
	{
		try
		{
			ls = new WrappedMapSet<Integer>(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//:)
		}
		ls = new WrappedMapSet<Integer>(IdentityHashMap.class, Arrays.asList(new Integer[]{}));
		assertFalse(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertTrue(ls.isEmpty());
		assertEquals(0, ls.size());
		ls = new WrappedMapSet<Integer>(IdentityHashMap.class, Arrays.asList(new Integer[]{a1}));
		assertTrue(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertFalse(ls.isEmpty());
		assertEquals(1, ls.size());
		ls = new WrappedMapSet<Integer>(IdentityHashMap.class, Arrays.asList(new Integer[]{a1, a1}));
		assertTrue(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertEquals(1, ls.size());
		assertFalse(ls.isEmpty());
		ls.clear();
		assertFalse(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertTrue(ls.isEmpty());
		assertEquals(0, ls.size());
		List<Integer> list = new ArrayList<Integer>();
		list.add(a1);
		list.add(b1);
		ls = new WrappedMapSet<Integer>(IdentityHashMap.class, list);
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

	@Test
	public void testBadClass()
	{
		try
		{
			Class cl = getClass();
			ls = new WrappedMapSet<Integer>(cl);
			fail();
		}
		catch (ClassCastException e)
		{
			//:)
		}
	}

	@Test
	public void testConstructorNoZeroArg()
	{
		try
		{
			new WrappedMapSet(NoZeroArgConstructorMap.class);
			fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
	}

	@Test
	public void testConstructorPrivate()
	{
		try
		{
			new WrappedMapSet(NoPublicZeroArgConstructorMap.class);
			fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
	}

	public void testBadClassInConstructor()
	{
		try
		{
			new WrappedMapSet(StrangeMap.class);
			fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException e)
		{
			//OK, expected
		}
	}
}
