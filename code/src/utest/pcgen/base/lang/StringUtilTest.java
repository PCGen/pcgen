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
import java.util.Comparator;

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
		assertEquals("Foo, null", StringUtil
			.join(new String[]{"Foo", null}, ", "));
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

	public void testReplaceAll()
	{
		assertEquals("ABC", StringUtil.replaceAll("ABD", "D", "C"));
		assertEquals("ABCC", StringUtil.replaceAll("ABDD", "D", "C"));
		assertEquals("ACBC", StringUtil.replaceAll("ADBD", "D", "C"));
		assertEquals("ACC", StringUtil.replaceAll("ACD", "D", "C"));
		assertEquals("ACaCa", StringUtil.replaceAll("ADD", "D", "Ca"));
		assertEquals("ADD", StringUtil.replaceAll("ADD", "F", "Ca"));
		assertEquals("AdCa", StringUtil.replaceAll("AdD", "D", "Ca"));
		assertEquals("ABCEFG", StringUtil.replaceAll("ABDEFG", "D", "C"));
		assertEquals("ABCCDbEFG", StringUtil.replaceAll("ABDaDaDbEFG", "Da", "C"));
	}

	public void testHasBalancedParens()
	{
		assertTrue(StringUtil.hasBalancedParens("No Parens!"));
		assertTrue(StringUtil.hasBalancedParens("I Ignore ["));
		assertTrue(StringUtil.hasBalancedParens("I ignore ] too"));
		assertTrue(StringUtil.hasBalancedParens("Oh look, I ignore {"));
		assertTrue(StringUtil.hasBalancedParens("Am I supposed to pay attention to }?  Nope!"));
		assertTrue(StringUtil.hasBalancedParens("I'm Balanced (Honest!)"));
		assertTrue(StringUtil.hasBalancedParens("I'm balanced (also can use an embedded ())"));
		assertTrue(StringUtil.hasBalancedParens("I'm Balanced (Go!)(Team!)(Go!)"));
		assertFalse(StringUtil.hasBalancedParens("(Uh oh"));
		assertFalse(StringUtil.hasBalancedParens("Uh oh)"));
		assertFalse(StringUtil.hasBalancedParens("Nope (not good))"));
		assertFalse(StringUtil.hasBalancedParens("This won't work ((either)"));
		assertFalse(StringUtil.hasBalancedParens("(Nice (Try)"));
		assertFalse(StringUtil.hasBalancedParens(") will cause a problem alone "));
		assertFalse(StringUtil.hasBalancedParens(") will also cause a problem without a ( before it"));
		assertFalse(StringUtil.hasBalancedParens("(and getting fancy) doesn't help)"));
	}
	
	public void testCaseSensitiveOrder()
	{
		Comparator<String> cso = StringUtil.CASE_SENSITIVE_ORDER;
		assertTrue(cso.compare("1", "2") < 0);
		assertTrue(cso.compare("1", "a") < 0);
		assertTrue(cso.compare("1", "A") < 0);
		assertTrue(cso.compare("aa", "Aa") > 0);
		assertTrue(cso.compare("Aa", "aa") < 0);
		assertTrue(cso.compare("cb", "Ca") > 0);
		assertTrue(cso.compare("Ca", "cb") < 0);
		assertTrue(cso.compare("bb", "Bc") > 0);
		assertTrue(cso.compare("Bc", "bb") < 0);
		try
		{
			assertTrue(cso.compare(null, "Bc") > 0);
			fail();
		}
		catch (NullPointerException e)
		{
			//OK
		}
		try
		{
			assertTrue(cso.compare("Bc", null) > 0);
			fail();
		}
		catch (NullPointerException e)
		{
			//OK
		}
	}
}
