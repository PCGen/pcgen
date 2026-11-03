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

import org.junit.jupiter.api.Test;

/**
 * Test the CaseInsensitiveString class
 */
class CaseInsensitiveStringTest
{

	@Test
	void testNullConstructor()
	{
		assertThrows(NullPointerException.class, () -> new CaseInsensitiveString(null));
	}
	
	@Test
	void testIdentical()
	{
		CaseInsensitiveString cis1 = new CaseInsensitiveString("Foo");
		CaseInsensitiveString cis2 = new CaseInsensitiveString("Foo");
		assertTrue(cis1.equals(cis2));
		assertTrue(cis2.equals(cis1));
		assertEquals(cis1.hashCode(), cis2.hashCode());
	}

	@Test
	void testMixedCase()
	{
		CaseInsensitiveString cis1 = new CaseInsensitiveString("FooGoo");
		CaseInsensitiveString cis2 = new CaseInsensitiveString("fOoGOO");
		assertTrue(cis1.equals(cis2));
		assertTrue(cis2.equals(cis1));
		assertEquals(cis1.hashCode(), cis2.hashCode());
	}

	@Test
	void testMixedCaseNotFirstLetter()
	{
		CaseInsensitiveString cis1 = new CaseInsensitiveString("FoO");
		CaseInsensitiveString cis2 = new CaseInsensitiveString("Foo");
		assertTrue(cis1.equals(cis2));
		assertTrue(cis2.equals(cis1));
		assertEquals(cis1.hashCode(), cis2.hashCode());
	}

	@Test
	void testSpace()
	{
		CaseInsensitiveString cis1 = new CaseInsensitiveString("Foo");
		CaseInsensitiveString cis2 = new CaseInsensitiveString("Foo ");
		assertFalse(cis1.equals(cis2));
		assertFalse(cis2.equals(cis1));
	}

	@Test
	void testDifferent()
	{
		CaseInsensitiveString cis1 = new CaseInsensitiveString("Foo");
		CaseInsensitiveString cis2 = new CaseInsensitiveString("Foe");
		assertFalse(cis1.equals(cis2));
		assertFalse(cis2.equals(cis1));
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	void testString()
	{
		CaseInsensitiveString cis1 = new CaseInsensitiveString("Foo");
		//Should fail both ways
		assertFalse(cis1.equals("Foo"));
		assertFalse("Foo".equals(cis1));
	}

	@Test
	void testToString()
	{
		CaseInsensitiveString cis1 = new CaseInsensitiveString("Foo");
		assertEquals("Foo", cis1.toString());
		CaseInsensitiveString cis2 = new CaseInsensitiveString("Foo ");
		assertEquals("Foo ", cis2.toString());
	}
}
