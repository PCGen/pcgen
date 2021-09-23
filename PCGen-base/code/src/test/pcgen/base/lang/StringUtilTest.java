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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.junit.jupiter.api.Test;

import pcgen.testsupport.TestSupport;

/**
 * Test the StringUtil class
 */
public class StringUtilTest
{

	@Test
	public void testConstructor()
	{
		TestSupport.invokePrivateConstructor(StringUtil.class);
	}

	@Test
	public void testAllowNullCollection()
	{
		assertEquals("", StringUtil.join((Collection<?>) null, ", "));
	}

	@Test
	public void testAllowNullArray()
	{
		assertEquals("", StringUtil.join((String[]) null, ", "));
	}

	@Test
	public void testAllowEmptyCollection()
	{
		assertEquals("", StringUtil.join(Collections.emptyList(), ", "));
	}

	@Test
	public void testAllowEmptyArray()
	{
		assertEquals("", StringUtil.join(new String[]{}, ", "));
	}

	@Test
	public void testJoinArray()
	{
		assertEquals("Foo, Bar", StringUtil.join(new String[]{"Foo", "Bar"},
			", "));
	}

	@Test
	public void testJoinDupeArray()
	{
		assertEquals("Foo, Foo", StringUtil.join(new String[]{"Foo", "Foo"},
			", "));
	}

	@Test
	public void testJoinNonStringList()
	{
		assertEquals("1, 2", StringUtil.join(
			Arrays.asList(new Integer[]{1, 2}), ", "));
	}

	@Test
	public void testJoinNullArrayContents()
	{
		assertEquals("Foo, null", StringUtil
			.join(new String[]{"Foo", null}, ", "));
	}

	@Test
	public void testJoinNullListContents()
	{
		assertThrows(NullPointerException.class,
			() -> StringUtil.join(Arrays.asList(new Integer[]{1, null}), ", "));
	}

	@Test
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

	@Test
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
	
	@Test
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
		assertThrows(NullPointerException.class, () -> cso.compare(null, "Bc"));
		assertThrows(NullPointerException.class, () -> cso.compare("Bc", null));
	}
}
