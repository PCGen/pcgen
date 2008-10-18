/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.lang;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import junit.framework.TestCase;
import pcgen.testsupport.TestSupport;

public class StringUtilTest extends TestCase
{

	public void testConstructor()
	{
		TestSupport.invokePrivateConstructor(StringUtil.class);
	}

	public void testAllowNullCollection()
	{
		assertEquals("", StringUtil.join((Collection<?>) null, ", "));
	}

	public void testAllowNullArray()
	{
		assertEquals("", StringUtil.join((String[]) null, ", "));
	}

	public void testAllowEmptyCollection()
	{
		assertEquals("", StringUtil.join(Collections.emptyList(), ", "));
	}

	public void testAllowEmptyArray()
	{
		assertEquals("", StringUtil.join(new String[]{}, ", "));
	}

	public void testJoinArray()
	{
		assertEquals("Foo, Bar", StringUtil.join(new String[]{"Foo", "Bar"},
			", "));
	}

	public void testJoinDupeArray()
	{
		assertEquals("Foo, Foo", StringUtil.join(new String[]{"Foo", "Foo"},
			", "));
	}

	public void testJoinNonStringList()
	{
		assertEquals("1, 2", StringUtil.join(
			Arrays.asList(new Integer[]{1, 2}), ", "));
	}

	public void testJoinNullArrayContents()
	{
		try
		{
			assertEquals("Foo", StringUtil
				.join(new String[]{"Foo", null}, ", "));
			fail();
		}
		catch (NullPointerException e)
		{
			//Expected
		}
	}

	public void testJoinNullListContents()
	{
		try
		{
			assertEquals("1", StringUtil.join(Arrays.asList(new Integer[]{1,
				null}), ", "));
			fail();
		}
		catch (NullPointerException e)
		{
			//Expected
		}
	}
}
