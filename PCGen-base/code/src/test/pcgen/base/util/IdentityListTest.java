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
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

public class IdentityListTest
{

	private Integer a1 = new Integer(1);
	private Integer a2 = new Integer(2);
	private Integer b1 = new Integer(1);
	private Integer b2 = new Integer(2);

	@Test
	public void testIdentityAdd()
	{
		IdentityList<Integer> ls = new IdentityList<>();
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
		ls.add(null);
		assertTrue(ls.contains(a1));
		assertTrue(ls.contains(null));
		assertFalse(ls.contains(b1));
		assertEquals(2, ls.size());
		assertFalse(ls.isEmpty());
		assertEquals(a1, ls.get(0));
		assertEquals(null, ls.get(1));
		assertEquals(0, ls.indexOf(a1));
		assertEquals(1, ls.indexOf(null));
		assertEquals(0, ls.lastIndexOf(a1));
		assertEquals(1, ls.lastIndexOf(null));
		assertEquals(true, ls.remove(a1));
		assertEquals(false, ls.remove(b1));
		assertEquals(1, ls.size());
		assertEquals(null, ls.get(0));
		ls.add(a1);
		assertEquals(a1, ls.remove(1));
		assertThrows(IndexOutOfBoundsException.class, () -> ls.remove(2));
		assertEquals(1, ls.size());
		assertEquals(null, ls.get(0));
	}

	@Test
	public void testContainsAll()
	{
		IdentityList<Integer> ls = new IdentityList<>();
		assertThrows(NullPointerException.class, () -> ls.containsAll(null));
	}

	@Test
	public void testIdentityAddAll()
	{
		IdentityList<Integer> ls = new IdentityList<>();
		assertFalse(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertTrue(ls.isEmpty());
		assertEquals(0, ls.size());
		ls.addAll(Arrays.asList(new Integer[] {a1, a1}));
		assertTrue(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertTrue(ls.containsAll(Arrays.asList(new Integer[] {a1})));
		assertTrue(ls.containsAll(Arrays.asList(new Integer[] {a1, a1})));
		assertTrue(ls.containsAll(Arrays.asList(new Integer[] {a1, a1, a1})));
		assertFalse(ls.containsAll(Arrays.asList(new Integer[] {a1, b1})));
		assertFalse(ls.isEmpty());
		assertEquals(2, ls.size());
		assertTrue(ls.removeAll(Arrays.asList(new Integer[] {a1})));
		ls.addAll(Arrays.asList(new Integer[] {a1, a1}));
		assertTrue(ls.removeAll(Arrays.asList(new Integer[] {a1, a1})));
		assertTrue(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertFalse(ls.removeAll(Arrays.asList(new Integer[] {a1, a1})));
		assertTrue(ls.isEmpty());
		assertEquals(0, ls.size());
		ls.addAll(Arrays.asList(new Integer[] {a1, null}));
		assertTrue(ls.contains(a1));
		assertTrue(ls.contains(null));
		assertFalse(ls.contains(b1));
		assertFalse(ls.containsAll(Arrays.asList(new Integer[] {a1, b1})));
		assertTrue(ls.containsAll(Arrays.asList(new Integer[] {null, a1})));
		assertEquals(2, ls.size());
		assertFalse(ls.isEmpty());
		assertEquals(a1, ls.get(0));
		assertEquals(null, ls.get(1));
		assertEquals(0, ls.indexOf(a1));
		assertEquals(1, ls.indexOf(null));
		assertEquals(0, ls.lastIndexOf(a1));
		assertEquals(1, ls.lastIndexOf(null));
		assertFalse(ls.removeAll(Arrays.asList(new Integer[] {a1, b1})));
		assertEquals(1, ls.size());
		assertEquals(null, ls.get(0));
	}

	@Test
	public void testIdentityAddInt()
	{
		IdentityList<Integer> ls = new IdentityList<>();
		assertFalse(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertTrue(ls.isEmpty());
		assertEquals(0, ls.size());
		ls.add(0, a1);
		assertTrue(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertFalse(ls.isEmpty());
		assertEquals(1, ls.size());
		ls.add(1, a1);
		assertTrue(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertEquals(2, ls.size());
		assertFalse(ls.isEmpty());
		ls.clear();
		assertFalse(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertTrue(ls.isEmpty());
		assertEquals(0, ls.size());
		ls.add(0, a1);
		assertTrue(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertEquals(1, ls.size());
		assertFalse(ls.isEmpty());
		assertThrows(IndexOutOfBoundsException.class, () -> ls.add(-1, a1));
		assertThrows(IndexOutOfBoundsException.class, () -> ls.add(2, a1));
	}

	@Test
	public void testEqualityAdd()
	{
		IdentityList<Integer> ls = new IdentityList<>();
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
		IdentityList<Integer> ls = new IdentityList<>();
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
		IdentityList<Integer> ls = new IdentityList<>();
		Iterator<Integer> it = ls.iterator();
		assertNotNull(it);
		assertFalse(it.hasNext());
		assertThrows(NoSuchElementException.class, () -> it.next());
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
		//Yes, this needs to be an identity test, not .equals
		assertTrue(o1 == a1);
		assertTrue(iter.hasNext());
		Object o2 = iter.next();
		//Yes, this needs to be an identity test, not .equals
		assertTrue(o2 == b2);
		assertFalse(iter.hasNext());
		assertThrows(NoSuchElementException.class, () -> iter.next());
	}

	@Test
	public void testIteratorRemove()
	{
		IdentityList<Integer> ls = new IdentityList<>();
		Iterator<Integer> it = ls.iterator();
		assertNotNull(it);
		assertFalse(it.hasNext());
		assertThrows(NoSuchElementException.class, () -> it.next());
		ls.add(a1);
		ls.add(b1);
		ls.add(b2);
		Iterator<Integer> iter = ls.iterator();
		assertNotNull(iter);
		assertTrue(iter.hasNext());
		Object o1 = iter.next();
		//Yes, this needs to be an identity test, not .equals
		assertTrue(o1 == a1);
		assertTrue(iter.hasNext());
		Object o2 = iter.next();
		//Yes, this needs to be an identity test, not .equals
		assertTrue(o2 == b1);
		iter.remove();
		assertFalse(ls.contains(o2));
		assertTrue(ls.contains(a1));
		Object o3 = iter.next();
		//Yes, this needs to be an identity test, not .equals
		assertTrue(o3 == b2);
		assertFalse(iter.hasNext());
		assertThrows(NoSuchElementException.class, () -> iter.next());
	}

	@Test
	public void testIdentityAddConstructor()
	{
		IdentityList<Integer> ls = new IdentityList<>();
		assertThrows(NullPointerException.class, () -> new IdentityList<>(null));
		ls = new IdentityList<>(Arrays.asList(new Integer[]{}));
		assertFalse(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertTrue(ls.isEmpty());
		assertEquals(0, ls.size());
		ls = new IdentityList<>(Arrays.asList(new Integer[]{a1}));
		assertTrue(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertFalse(ls.isEmpty());
		assertEquals(1, ls.size());
		ls = new IdentityList<>(Arrays.asList(new Integer[]{a1, a1}));
		assertTrue(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertEquals(2, ls.size());
		assertFalse(ls.isEmpty());
		ls.clear();
		assertFalse(ls.contains(a1));
		assertFalse(ls.contains(b1));
		assertTrue(ls.isEmpty());
		assertEquals(0, ls.size());
		List<Integer> list = new ArrayList<>();
		list.add(a1);
		list.add(b1);
		ls = new IdentityList<>(list);
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
