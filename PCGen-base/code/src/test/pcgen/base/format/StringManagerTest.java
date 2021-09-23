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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.util.FormatManager;
import pcgen.base.util.SimpleValueStore;

/**
 * Test the StringManager class
 */
public class StringManagerTest
{
	@Test
	public void testConvertFailNull()
	{
		assertThrows(NullPointerException.class, () -> FormatUtilities.STRING_MANAGER.convert(null));
	}

	@Test
	public void testUnconvertFailNull()
	{
		assertThrows(NullPointerException.class, () -> FormatUtilities.STRING_MANAGER.unconvert(null));
	}

	@Test
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void testUnconvertFailObject()
	{
		//Yes generics are being violated in order to do this test
		FormatManager formatManager = FormatUtilities.STRING_MANAGER;
		assertThrows(ClassCastException.class, () -> formatManager.unconvert(new Object()));
	}

	@Test
	public void testConvertIndirectFailNull()
	{
		assertThrows(NullPointerException.class, () -> FormatUtilities.STRING_MANAGER.convertIndirect(null));
	}

	@Test
	public void testConvert()
	{
		assertEquals("1", FormatUtilities.STRING_MANAGER.convert("1"));
		assertEquals("abc", FormatUtilities.STRING_MANAGER.convert("abc"));
	}

	@Test
	public void testUnconvert()
	{
		assertEquals("1", FormatUtilities.STRING_MANAGER.unconvert("1"));
		assertEquals("abc", FormatUtilities.STRING_MANAGER.unconvert("abc"));
	}

	@Test
	public void testConvertIndirect()
	{
		assertEquals("1", FormatUtilities.STRING_MANAGER.convertIndirect("1").get());
		assertEquals("gfd", FormatUtilities.STRING_MANAGER.convertIndirect("gfd").get());
	}

	@Test
	public void testGetIdentifier()
	{
		assertEquals("STRING", FormatUtilities.STRING_MANAGER.getIdentifierType());
	}

	@Test
	public void testGetComparator()
	{
		Comparator<String> comparator = new StringManager().getComparator();
		assertEquals(0, comparator.compare("A", "A"));
		assertEquals(-1, comparator.compare("A", "AB"));
		assertEquals(1, comparator.compare("BA", "AB"));
		assertEquals(0, comparator.compare("A", "a"));
	}

	@Test
	public void testHashCodeEquals()
	{
		assertEquals(new StringManager().hashCode(), FormatUtilities.STRING_MANAGER.hashCode());
		assertFalse(FormatUtilities.STRING_MANAGER.equals(new Object()));
		assertFalse(FormatUtilities.STRING_MANAGER.equals(new BooleanManager()));
		assertTrue(FormatUtilities.STRING_MANAGER.equals(new StringManager()));
	}

	@Test
	public void testGetComponent()
	{
		assertTrue(FormatUtilities.STRING_MANAGER.getComponentManager().isEmpty());
	}

	@Test
	public void testIsDirect()
	{
		assertTrue(FormatUtilities.STRING_MANAGER.isDirect());
	}

	@Test
	public void testInitializeFrom()
	{
		SimpleValueStore valueStore = new SimpleValueStore();
		valueStore.addValueFor(FormatUtilities.STRING_MANAGER.getIdentifierType(), "");
		Object value = FormatUtilities.STRING_MANAGER.initializeFrom(valueStore);
		assertEquals("", value);
		valueStore.addValueFor(FormatUtilities.STRING_MANAGER.getIdentifierType(), "Hi");
		value = FormatUtilities.STRING_MANAGER.initializeFrom(valueStore);
		assertEquals("Hi", value);
	}
}
