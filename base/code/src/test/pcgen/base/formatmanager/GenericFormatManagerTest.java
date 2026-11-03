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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.base.format.StringManager;
import pcgen.testsupport.MockObjectDatabase;

/**
 * Test the GenericFormatManager class
 */
class GenericFormatManagerTest
{
	private GenericFormatManager<Object> manager;
	private MockObjectDatabase database;

	@BeforeEach
	void setUp()
	{
		database = new MockObjectDatabase();
		manager = new GenericFormatManager<>(database, Object.class, "KEYED");
	}

	@AfterEach
	void tearDown()
	{
		database = null;
		manager = null;
	}

	@Test
	void testConvertFailNull()
	{
		assertThrows(NullPointerException.class, () -> manager.convert(null));
	}

	@Test
	void testUnconvertFailNull()
	{
		assertThrows(NullPointerException.class, () -> manager.unconvert(null));
	}

	@Test
	void testConvertIndirectFailNull()
	{
		assertThrows(NullPointerException.class, () -> manager.convertIndirect(null));
	}

	@Test
	void testConvertIndirectFailNotNumeric()
	{
		assertThrows(IllegalArgumentException.class, () -> manager.convertIndirect("SomeString"));
	}

	@Test
	void testGetManagedClass()
	{
		assertSame(Object.class, manager.getManagedClass());
	}

	@Test
	void testConvert()
	{
		database.map.put(Object.class, "1", 1);
		database.map.put(Object.class, "-3", -3);
		database.map.put(Object.class, "1.4", 1.4);
		assertEquals(Integer.valueOf(1), manager.convert("1"));
		assertEquals(Integer.valueOf(-3), manager.convert("-3"));
		assertEquals(Double.valueOf(1.4), manager.convert("1.4"));
	}

	@Test
	void testUnconvert()
	{
		assertEquals("1", manager.unconvert(Integer.valueOf(1)));
		assertEquals("-3", manager.unconvert(Integer.valueOf(-3)));
		assertEquals("1.4", manager.unconvert(Double.valueOf(1.4)));
	}

	@Test
	void testConvertIndirect()
	{
		database.map.put(Object.class, "1", 1);
		database.map.put(Object.class, "-3", -3);
		database.map.put(Object.class, "1.4", 1.4);
		assertEquals(Integer.valueOf(1), manager.convertIndirect("1").get());
		assertEquals(Integer.valueOf(-3), manager.convertIndirect("-3").get());
		assertEquals(Double.valueOf(1.4), manager.convertIndirect("1.4").get());
	}

	@Test
	void testGetIdentifier()
	{
		assertEquals("KEYED", manager.getIdentifierType());
	}

	@Test
	void testHashCodeEquals()
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

	@Test
	void testGetComponent()
	{
		assertTrue(manager.getComponentManager().isEmpty());
	}


	@Test
	void testIsDirect()
	{
		assertEquals(database.isDirect(), manager.isDirect());
	}
}
