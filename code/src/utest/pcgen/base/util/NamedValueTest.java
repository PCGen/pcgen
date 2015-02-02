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

import junit.framework.TestCase;

import org.junit.Test;

public class NamedValueTest extends TestCase
{

	@Test
	public void testNullConstructor()
	{
		try
		{
			new NamedValue(null, 1.0);
			fail("Expected NamedValue to reject null argument in constructor");
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		try
		{
			new NamedValue(null);
			fail("Expected NamedValue to reject null argument in constructor");
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
	}

	@Test
	public void testBasics()
	{
		NamedValue nf1 = new NamedValue("Foo");
		assertEquals("Foo", nf1.getName());
		assertEquals(0.0, nf1.getWeight());
		nf1.addWeight(4.3);
		assertEquals(4.3, nf1.getWeight(), 10e-8);
		nf1.addWeight(2.1);
		assertEquals(6.4, nf1.getWeight(), 10e-8);
		nf1.removeWeight(3.3);
		assertEquals(3.1, nf1.getWeight(), 10e-8);
	}

	@Test
	public void testToString()
	{
		NamedValue nf1 = new NamedValue("Foo", 2.0);
		assertEquals("Foo:2.0", nf1.toString());
	}
}
