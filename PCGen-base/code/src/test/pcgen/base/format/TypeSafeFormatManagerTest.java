/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net> This program is free
 * software; you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.base.format;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import pcgen.base.util.FormatManager;
import pcgen.base.util.SimpleValueStore;
import pcgen.testsupport.TestSafe;

/**
 * Test the TypeSafeFormatManager class
 */
public class TypeSafeFormatManagerTest
{

	@SuppressWarnings({"rawtypes", "unchecked", "cast"})
	@Test
	public void testFailBadSubFormat()
	{
		assertThrows(NullPointerException.class, () -> new TypeSafeFormatManager<>(TestSafe.class, null));
		assertThrows(IllegalArgumentException.class, () -> new TypeSafeFormatManager<>(TestSafe.class, ""));
		assertThrows(IllegalArgumentException.class, () -> new TypeSafeFormatManager((Class) Object.class, "TS"));
		assertThrows(NullPointerException.class, () -> new TypeSafeFormatManager<>(null, "TS"));
	}

	@Test
	public void testConvert()
	{
		FormatManager<TestSafe> manager = new TypeSafeFormatManager<>(TestSafe.class, "TESTSAFE");
		assertThrows(NullPointerException.class, () -> manager.convert(null));
		assertThrows(IllegalArgumentException.class, () -> manager.convert(""));
		assertThrows(IllegalArgumentException.class, () -> manager.convert("TS"));
		assertEquals(TestSafe.TS1, manager.convert("TS1"));
	}

	@Test
	public void testGetIdentifier()
	{
		FormatManager<TestSafe> manager = new TypeSafeFormatManager<>(TestSafe.class, "TESTSAFE");
		assertEquals("TESTSAFE", manager.getIdentifierType());
	}

	@Test
	public void testHashCodeEquals()
	{
		FormatManager<TestSafe> manager = new TypeSafeFormatManager<>(TestSafe.class, "TESTSAFE");
		assertEquals(new TypeSafeFormatManager<>(TestSafe.class, "TESTSAFE").hashCode(), manager.hashCode());
		assertFalse(manager.equals(new Object()));
		assertFalse(manager.equals(new StringManager()));
		assertTrue(manager.equals(new TypeSafeFormatManager<>(TestSafe.class, "TESTSAFE")));
	}

	@Test
	public void testGetComponent()
	{
		FormatManager<TestSafe> manager = new TypeSafeFormatManager<>(TestSafe.class, "TESTSAFE");
		assertTrue(manager.getComponentManager().isEmpty());
	}

	@Test
	public void testIsDirect()
	{
		FormatManager<TestSafe> manager = new TypeSafeFormatManager<>(TestSafe.class, "TESTSAFE");
		assertTrue(manager.isDirect());
	}

	@Test
	public void testInitializeFrom()
	{
		FormatManager<TestSafe> manager = new TypeSafeFormatManager<>(TestSafe.class, "TESTSAFE");
		SimpleValueStore valueStore = new SimpleValueStore();
		valueStore.addValueFor(manager.getIdentifierType(), TestSafe.TS1);
		Object value = manager.initializeFrom(valueStore);
		assertEquals(TestSafe.TS1, value);
		valueStore.addValueFor(manager.getIdentifierType(), TestSafe.TS2);
		value = manager.initializeFrom(valueStore);
		assertEquals(TestSafe.TS2, value);
	}

}
