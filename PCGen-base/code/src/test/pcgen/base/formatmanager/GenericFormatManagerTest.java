/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net> This program is
 * free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version.
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
package pcgen.base.formatmanager;

import junit.framework.TestCase;
import pcgen.base.format.StringManager;
import pcgen.testsupport.MockObjectDatabase;

/**
 * Test the GenericFormatManager class
 */
public class GenericFormatManagerTest extends TestCase
{
	private GenericFormatManager<Object> manager;
	private MockObjectDatabase database;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		database = new MockObjectDatabase();
		manager = new GenericFormatManager<>(database, Object.class, "KEYED");
	}

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

	public void testConvertIndirectFailNotNumeric()
	{
		try
		{
			manager.convertIndirect("SomeString");
			fail("invalid value should fail");
		}
		catch (IllegalArgumentException e)
		{
			//expected
		}
	}

	public void testGetManagedClass()
	{
		assertSame(Object.class, manager.getManagedClass());
	}

	public void testConvert()
	{
		database.map.put(Object.class, "1", 1);
		database.map.put(Object.class, "-3", -3);
		database.map.put(Object.class, "1.4", 1.4);
		assertEquals(Integer.valueOf(1), manager.convert("1"));
		assertEquals(Integer.valueOf(-3), manager.convert("-3"));
		assertEquals(Double.valueOf(1.4), manager.convert("1.4"));
	}

	public void testUnconvert()
	{
		assertEquals("1", manager.unconvert(Integer.valueOf(1)));
		assertEquals("-3", manager.unconvert(Integer.valueOf(-3)));
		assertEquals("1.4", manager.unconvert(Double.valueOf(1.4)));
	}

	public void testConvertIndirect()
	{
		database.map.put(Object.class, "1", 1);
		database.map.put(Object.class, "-3", -3);
		database.map.put(Object.class, "1.4", 1.4);
		assertEquals(Integer.valueOf(1), manager.convertIndirect("1").get());
		assertEquals(Integer.valueOf(-3), manager.convertIndirect("-3").get());
		assertEquals(Double.valueOf(1.4), manager.convertIndirect("1.4").get());
	}

	public void testGetIdentifier()
	{
		assertEquals("KEYED", manager.getIdentifierType());
	}

	public void testHashCodeEquals()
	{
		assertFalse(manager.equals(new Object()));
		assertFalse(manager.equals(
			new GenericFormatManager<>(database, Number.class, "KEYED")));
		assertFalse(manager.equals(
			new GenericFormatManager<>(database, Object.class, "OBJECT")));
		assertFalse(manager.equals(new GenericFormatManager<>(
			new MockObjectDatabase(), Object.class, "KEYED")));
		assertFalse(manager.hashCode() == new GenericFormatManager<>(database,
			Number.class, "KEYED").hashCode());
		assertFalse(manager.hashCode() == new GenericFormatManager<>(database,
			Object.class, "OBJECT").hashCode());
		assertFalse(manager.equals(new StringManager()));
		assertEquals(manager.hashCode(),
			new GenericFormatManager<>(database, Object.class, "KEYED")
				.hashCode());
		assertTrue(manager.equals(
			new GenericFormatManager<>(database, Object.class, "KEYED")));
	}

	public void testGetComponent()
	{
		assertTrue(manager.getComponentManager().isEmpty());
	}


	public void testIsDirect()
	{
		assertEquals(database.isDirect(), manager.isDirect());
	}
}
