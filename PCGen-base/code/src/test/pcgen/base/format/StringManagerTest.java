/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.format;

import java.util.Comparator;

import junit.framework.TestCase;

/**
 * Test the StringManager class
 */
public class StringManagerTest extends TestCase
{
	private StringManager manager = new StringManager();

	public void testConvertFailNull()
	{
		try
		{
			manager.convert(null);
			fail("null value should fail");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//expected
		}
	}

	public void testUnconvertFailNull()
	{
		try
		{
			manager.unconvert(null);
			fail("null value should fail");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//expected
		}
	}

	public void testConvertIndirectFailNull()
	{
		try
		{
			manager.convertIndirect(null);
			fail("null value should fail");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//expected
		}
	}

	public void testConvert()
	{
		assertEquals("1", manager.convert("1"));
		assertEquals("abc", manager.convert("abc"));
	}

	public void testUnconvert()
	{
		assertEquals("1", manager.unconvert("1"));
		assertEquals("abc", manager.unconvert("abc"));
	}

	public void testConvertIndirect()
	{
		assertEquals("1", manager.convertIndirect("1").get());
		assertEquals("gfd", manager.convertIndirect("gfd").get());
	}

	public void testGetIdentifier()
	{
		assertEquals("STRING", manager.getIdentifierType());
	}

	public void testGetComparator()
	{
		Comparator<String> comparator = new StringManager().getComparator();
		assertEquals(0, comparator.compare("A", "A"));
		assertEquals(-1, comparator.compare("A", "AB"));
		assertEquals(1, comparator.compare("BA", "AB"));
		assertEquals(0, comparator.compare("A", "a"));
	}

	public void testHashCodeEquals()
	{
		assertEquals(new StringManager().hashCode(), manager.hashCode());
		assertFalse(manager.equals(new Object()));
		assertFalse(manager.equals(new BooleanManager()));
		assertTrue(manager.equals(new StringManager()));
	}

	public void testGetComponent()
	{
		assertNull(manager.getComponentManager());
	}

	public void testIsDirect()
	{
		assertTrue(manager.isDirect());
	}
}
