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

import junit.framework.TestCase;

import org.junit.Test;

public class CaseInsensitiveStringTest extends TestCase
{

	@Test
	public void testNullConstructor()
	{
		try
		{
			new CaseInsensitiveString(null);
			fail("Expected CaseInsensitiveString to reject null argument in constructor");
		}
		catch (IllegalArgumentException e)
		{
			//OK
		}
	}
	
	@Test
	public void testIdentical()
	{
		CaseInsensitiveString cis1 = new CaseInsensitiveString("Foo");
		CaseInsensitiveString cis2 = new CaseInsensitiveString("Foo");
		assertTrue(cis1.equals(cis2));
		assertTrue(cis2.equals(cis1));
		assertEquals(cis1.hashCode(), cis2.hashCode());
	}

	@Test
	public void testMixedCase()
	{
		CaseInsensitiveString cis1 = new CaseInsensitiveString("FooGoo");
		CaseInsensitiveString cis2 = new CaseInsensitiveString("fOoGOO");
		assertTrue(cis1.equals(cis2));
		assertTrue(cis2.equals(cis1));
		assertEquals(cis1.hashCode(), cis2.hashCode());
	}

	@Test
	public void testMixedCaseNotFirstLetter()
	{
		CaseInsensitiveString cis1 = new CaseInsensitiveString("FoO");
		CaseInsensitiveString cis2 = new CaseInsensitiveString("Foo");
		assertTrue(cis1.equals(cis2));
		assertTrue(cis2.equals(cis1));
		assertEquals(cis1.hashCode(), cis2.hashCode());
	}

	@Test
	public void testSpace()
	{
		CaseInsensitiveString cis1 = new CaseInsensitiveString("Foo");
		CaseInsensitiveString cis2 = new CaseInsensitiveString("Foo ");
		assertFalse(cis1.equals(cis2));
		assertFalse(cis2.equals(cis1));
	}

	@Test
	public void testDifferent()
	{
		CaseInsensitiveString cis1 = new CaseInsensitiveString("Foo");
		CaseInsensitiveString cis2 = new CaseInsensitiveString("Foe");
		assertFalse(cis1.equals(cis2));
		assertFalse(cis2.equals(cis1));
	}

	@Test
	public void testString()
	{
		CaseInsensitiveString cis1 = new CaseInsensitiveString("Foo");
		assertFalse(cis1.equals("Foo"));
	}

	@Test
	public void testToString()
	{
		CaseInsensitiveString cis1 = new CaseInsensitiveString("Foo");
		assertEquals("Foo", cis1.toString());
		CaseInsensitiveString cis2 = new CaseInsensitiveString("Foo ");
		assertEquals("Foo ", cis2.toString());
	}
}
