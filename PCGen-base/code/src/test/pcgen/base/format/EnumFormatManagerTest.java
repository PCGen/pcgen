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

/**
 * Test the EnumFormatManager class
 */
public class EnumFormatManagerTest
{

	@SuppressWarnings({"rawtypes", "unchecked", "cast"})
	@Test
	public void testFailBadSubFormat()
	{
		assertThrows(NullPointerException.class, () -> new EnumFormatManager<>(TestEnum.class, null));
		assertThrows(IllegalArgumentException.class, () -> new EnumFormatManager<>(TestEnum.class, ""));
		assertThrows(IllegalArgumentException.class, () -> new EnumFormatManager((Class) Object.class, "TS"));
		assertThrows(NullPointerException.class, () -> new EnumFormatManager<>(null, "TS"));
	}

	@Test
	public void testConvert()
	{
		FormatManager<TestEnum> manager = new EnumFormatManager<>(TestEnum.class, "TestEnum");
		assertThrows(NullPointerException.class, () -> manager.convert(null));
		assertThrows(IllegalArgumentException.class, () -> manager.convert(""));
		assertThrows(IllegalArgumentException.class, () -> manager.convert("TS"));
		assertEquals(TestEnum.TS1, manager.convert("TS1"));
	}

	@Test
	public void testGetIdentifier()
	{
		FormatManager<TestEnum> manager = new EnumFormatManager<>(TestEnum.class, "TestEnum");
		assertEquals("TestEnum", manager.getIdentifierType());
	}

	@Test
	public void testHashCodeEquals()
	{
		FormatManager<TestEnum> manager = new EnumFormatManager<>(TestEnum.class, "TestEnum");
		assertEquals(new EnumFormatManager<>(TestEnum.class, "TestEnum").hashCode(), manager.hashCode());
		assertFalse(manager.equals(new Object()));
		assertFalse(manager.equals(new StringManager()));
		assertTrue(manager.equals(new EnumFormatManager<>(TestEnum.class, "TestEnum")));
	}

	@Test
	public void testGetComponent()
	{
		FormatManager<TestEnum> manager = new EnumFormatManager<>(TestEnum.class, "TestEnum");
		assertTrue(manager.getComponentManager().isEmpty());
	}

	@Test
	public void testIsDirect()
	{
		FormatManager<TestEnum> manager = new EnumFormatManager<>(TestEnum.class, "TestEnum");
		assertTrue(manager.isDirect());
	}

	@Test
	public void testInitializeFrom()
	{
		FormatManager<TestEnum> manager = new EnumFormatManager<>(TestEnum.class, "TestEnum");
		SimpleValueStore valueStore = new SimpleValueStore();
		valueStore.addValueFor(manager.getIdentifierType(), TestEnum.TS1);
		Object value = manager.initializeFrom(valueStore);
		assertEquals(TestEnum.TS1, value);
		valueStore.addValueFor(manager.getIdentifierType(), TestEnum.TS2);
		value = manager.initializeFrom(valueStore);
		assertEquals(TestEnum.TS2, value);
	}
	
	private enum TestEnum
	{
		TS1, TS2, TS3
	}

}
