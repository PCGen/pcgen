/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Arrays;
import java.util.Collection;
import org.junit.jupiter.api.Test;

import pcgen.base.lang.StringUtil;

public class FixedStringListTest
{

	@SuppressWarnings("unused")
	@Test
	public void testConstructorNullValuesLegal()
	{
		new FixedStringList((String) null);
		new FixedStringList("Foo", "Bar", null);
		new FixedStringList("Foo", "Bar", null, "Baz");
	}

	@Test
	public void testConstructor()
	{
		String[] s = null;
		assertThrows(NullPointerException.class, () -> new FixedStringList(s));
		assertThrows(NullPointerException.class, () -> new FixedStringList((Collection<String>) null));
		assertThrows(NegativeArraySizeException.class, () -> new FixedStringList(-1));
	}

	@Test
	public void testAddRemove()
	{
		FixedStringList list = new FixedStringList(Arrays.asList(new String[]{"Foo", "Bar", null, "Baz"}));
		assertEquals("Foo", list.get(0));
		assertEquals("Bar", list.get(1));
		assertEquals(null, list.get(2));
		assertEquals("Baz", list.get(3));
		
		assertEquals(null, list.remove(2));
		assertEquals("Foo", list.get(0));
		assertEquals("Bar", list.get(1));
		assertEquals(null, list.get(2));
		assertEquals("Baz", list.get(3));

		assertTrue(list.add("Hi"));
		assertEquals("Foo", list.get(0));
		assertEquals("Bar", list.get(1));
		assertEquals("Hi", list.get(2));
		assertEquals("Baz", list.get(3));

		assertFalse(list.add("There"));
		assertEquals("Foo", list.get(0));
		assertEquals("Bar", list.get(1));
		assertEquals("Hi", list.get(2));
		assertEquals("Baz", list.get(3));

		assertEquals("Foo", list.remove(0));
		assertEquals(null, list.get(0));
		assertEquals("Bar", list.get(1));
		assertEquals("Hi", list.get(2));
		assertEquals("Baz", list.get(3));

		assertTrue(list.add("There"));
		assertEquals("There", list.get(0));
		assertEquals("Bar", list.get(1));
		assertEquals("Hi", list.get(2));
		assertEquals("Baz", list.get(3));

		assertEquals("Bar", list.set(1, "Me"));
		assertEquals("There", list.get(0));
		assertEquals("Me", list.get(1));
		assertEquals("Hi", list.get(2));
		assertEquals("Baz", list.get(3));

		FixedStringList empty = new FixedStringList(0);
		assertFalse(empty.add("Hi"));
	}
	
	@Test
	public void testSize()
	{
		FixedStringList list = new FixedStringList(Arrays.asList(new String[]{"Foo", "Bar", null, "Baz"}));
		//nulls don't matter
		assertEquals(4, list.size());
		assertTrue(list.add("There"));
		assertEquals(4, list.size());
	}

	@Test
	public void testArrayIndex()
	{
		FixedStringList list = new FixedStringList(Arrays.asList(new String[]{"Foo", "Bar", null, "Baz"}));
		assertThrows(ArrayIndexOutOfBoundsException.class, () -> list.remove(-1));
		FixedStringList empty = new FixedStringList(0);
		assertThrows(ArrayIndexOutOfBoundsException.class, () -> empty.remove(0));
	}

	@Test
	public void testAddAll()
	{
		FixedStringList list1 = new FixedStringList(4);
		assertTrue(list1.addAll(1, Arrays.asList(new String[]{"Hi", "There", "People!"})));
		assertFalse(list1.addAll(0, Arrays.asList(new String[]{"Too", "Much!"})));
		FixedStringList list2 = new FixedStringList(4);
		assertTrue(list2.addAll(Arrays.asList(new String[]{"Hi", "There", "Nice", "People!"})));
		assertFalse(list1.addAll(Arrays.asList(new String[]{"Too", "Much!"})));
	}

	@Test
	public void testEquals()
	{
		FixedStringList list1u = new FixedStringList(4);
		assertTrue(list1u.addAll(1, Arrays.asList(new String[]{"Hi", "There", "People!"})));
		FixedStringList list2 = new FixedStringList(3);
		assertTrue(list2.addAll(Arrays.asList(new String[]{"Hi", "There", "People!"})));
		FixedStringList list3u = new FixedStringList(new String[]{"Hi", "There", "People!"});
		FixedStringList list3l = new FixedStringList(new String[]{"Hi", "there", "People!"});
		//self
		assertTrue(list1u.equals(list1u));
		//equal content
		assertTrue(list2.equals(list3u));
		assertTrue(list3u.equals(list2));
		//case sensitive
		assertFalse(list2.equals(list3l));
		assertFalse(list3l.equals(list2));
		//length sensitive
		assertFalse(list1u.equals(list3u));
		assertFalse(list3u.equals(list1u));
	}

	@Test
	public void testEqualsIgnoreCase()
	{
		FixedStringList list1u = new FixedStringList(4);
		assertTrue(list1u.addAll(1, Arrays.asList(new String[]{"Hi", "There", "People!"})));
		FixedStringList list1l = new FixedStringList(4);
		assertTrue(list1l.addAll(1, Arrays.asList(new String[]{"hi", "there", "people!"})));
		FixedStringList list1f = new FixedStringList(4);
		assertTrue(list1f.addAll(1, Arrays.asList(new String[]{"hi", "there", "people!", "Suffix"})));
		FixedStringList list2 = new FixedStringList(3);
		assertTrue(list2.addAll(Arrays.asList(new String[]{"Hi", "There", "People!"})));
		FixedStringList list3u = new FixedStringList(new String[]{"Hi", "There", "People!"});
		FixedStringList list3l = new FixedStringList(new String[]{"Hi", "there", "People!"});
		//self
		assertTrue(list1u.equalsIgnoreCase(list1u));
		//equal content
		assertTrue(list2.equalsIgnoreCase(list3u));
		assertTrue(list3u.equalsIgnoreCase(list2));
		//case insensitive
		assertTrue(list2.equalsIgnoreCase(list3l));
		assertTrue(list3l.equalsIgnoreCase(list2));
		//case insensitive but null resistant
		assertTrue(list1u.equalsIgnoreCase(list1l));
		assertTrue(list1l.equalsIgnoreCase(list1u));
		//length sensitive
		assertFalse(list1u.equalsIgnoreCase(list3u));
		assertFalse(list3u.equalsIgnoreCase(list1u));
		//null equality sensitive
		assertFalse(list1u.equalsIgnoreCase(list1f));
		assertFalse(list1f.equalsIgnoreCase(list1u));
	}

	@Test
	public void testCompare()
	{
		FixedStringList list1u = new FixedStringList(4);
		assertTrue(list1u.addAll(1, Arrays.asList(new String[]{"Hi", "There", "People!"})));
		FixedStringList list1l = new FixedStringList(4);
		assertTrue(list1l.addAll(1, Arrays.asList(new String[]{"hi", "there", "people!"})));
		FixedStringList list1f = new FixedStringList(4);
		assertTrue(list1f.addAll(1, Arrays.asList(new String[]{"hi", "there", "people!", "Suffix"})));
		FixedStringList list2 = new FixedStringList(3);
		assertTrue(list2.addAll(Arrays.asList(new String[]{"Hi", "There", "People!"})));
		FixedStringList list3u = new FixedStringList(new String[]{"Hi", "There", "People!"});
		FixedStringList list3l = new FixedStringList(new String[]{"Hi", "there", "People!"});
		//self
		assertTrue(FixedStringList.compare(list1u, list1u, StringUtil.CASE_SENSITIVE_ORDER) == 0);
		assertTrue(FixedStringList.compare(list1u, list1u, String.CASE_INSENSITIVE_ORDER) == 0);
		//equal content
		assertTrue(FixedStringList.compare(list2, list3u, StringUtil.CASE_SENSITIVE_ORDER) == 0);
		assertTrue(FixedStringList.compare(list2, list3u, String.CASE_INSENSITIVE_ORDER) == 0);
		assertTrue(FixedStringList.compare(list3u, list2, StringUtil.CASE_SENSITIVE_ORDER) == 0);
		assertTrue(FixedStringList.compare(list3u, list2, String.CASE_INSENSITIVE_ORDER) == 0);
		//case difference
		assertTrue(FixedStringList.compare(list2, list3l, StringUtil.CASE_SENSITIVE_ORDER) < 0);
		assertTrue(FixedStringList.compare(list2, list3l, String.CASE_INSENSITIVE_ORDER) == 0);
		assertTrue(FixedStringList.compare(list3l, list2, StringUtil.CASE_SENSITIVE_ORDER) > 0);
		assertTrue(FixedStringList.compare(list3l, list2, String.CASE_INSENSITIVE_ORDER) == 0);
		//case insensitive but null resistant
		assertTrue(FixedStringList.compare(list1u, list1l, StringUtil.CASE_SENSITIVE_ORDER) < 0);
		assertTrue(FixedStringList.compare(list1u, list1l, String.CASE_INSENSITIVE_ORDER) == 0);
		assertTrue(FixedStringList.compare(list1l, list1u, StringUtil.CASE_SENSITIVE_ORDER) > 0);
		assertTrue(FixedStringList.compare(list1l, list1u, String.CASE_INSENSITIVE_ORDER) == 0);
		//length sensitive
		FixedStringList list3z = new FixedStringList(new String[]{"Zi", "There", "People!"});
		assertTrue(FixedStringList.compare(list1u, list3z, StringUtil.CASE_SENSITIVE_ORDER) > 0);
		assertTrue(FixedStringList.compare(list1u, list3z, String.CASE_INSENSITIVE_ORDER) > 0);
		assertTrue(FixedStringList.compare(list3z, list1u, StringUtil.CASE_SENSITIVE_ORDER) < 0);
		assertTrue(FixedStringList.compare(list3z, list1u, String.CASE_INSENSITIVE_ORDER) < 0);
		//null sorts first
		assertTrue(FixedStringList.compare(list1u, list1f, StringUtil.CASE_SENSITIVE_ORDER) < 0);
		assertTrue(FixedStringList.compare(list1u, list1f, String.CASE_INSENSITIVE_ORDER) < 0);
		assertTrue(FixedStringList.compare(list1f, list1u, StringUtil.CASE_SENSITIVE_ORDER) > 0);
		assertTrue(FixedStringList.compare(list1f, list1u, String.CASE_INSENSITIVE_ORDER) > 0);
	}
}
