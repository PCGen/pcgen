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
 * Test the NumberManager class
 */
public class NumberManagerTest
{
	@Test
	public void testConvertFailNull()
	{
		assertThrows(NullPointerException.class, () -> FormatUtilities.NUMBER_MANAGER.convert(null));
	}

	@Test
	public void testConvertFailNotNumeric()
	{
		assertThrows(IllegalArgumentException.class, () -> FormatUtilities.NUMBER_MANAGER.convert("SomeString"));
	}

	@Test
	public void testUnconvertFailNull()
	{
		assertThrows(NullPointerException.class, () -> FormatUtilities.NUMBER_MANAGER.unconvert(null));
	}

	@Test
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void testUnconvertFailObject()
	{
		//Yes generics are being violated in order to do this test
		FormatManager formatManager = FormatUtilities.NUMBER_MANAGER;
		assertThrows(ClassCastException.class, () -> formatManager.unconvert(new Object()));
	}

	@Test
	public void testConvertIndirectFailNull()
	{
		assertThrows(NullPointerException.class, () -> FormatUtilities.NUMBER_MANAGER.convertIndirect(null));
	}

	@Test
	public void testConvertIndirectFailNotNumeric()
	{
		assertThrows(IllegalArgumentException.class, () -> FormatUtilities.NUMBER_MANAGER.convertIndirect("SomeString"));
	}

	@Test
	public void testConvert()
	{
		assertEquals(Integer.valueOf(1), FormatUtilities.NUMBER_MANAGER.convert("1"));
		assertEquals(Integer.valueOf(-3), FormatUtilities.NUMBER_MANAGER.convert("-3"));
		assertEquals(Double.valueOf(1.4), FormatUtilities.NUMBER_MANAGER.convert("1.4"));
	}

	@Test
	public void testUnconvert()
	{
		assertEquals("1", FormatUtilities.NUMBER_MANAGER.unconvert(Integer.valueOf(1)));
		assertEquals("-3", FormatUtilities.NUMBER_MANAGER.unconvert(Integer.valueOf(-3)));
		assertEquals("1.4", FormatUtilities.NUMBER_MANAGER.unconvert(Double.valueOf(1.4)));
	}

	@Test
	public void testConvertIndirect()
	{
		assertEquals(Integer.valueOf(1), FormatUtilities.NUMBER_MANAGER.convertIndirect("1").get());
		assertEquals(Integer.valueOf(-3), FormatUtilities.NUMBER_MANAGER.convertIndirect("-3").get());
		assertEquals(Double.valueOf(1.4), FormatUtilities.NUMBER_MANAGER.convertIndirect("1.4").get());
	}

	@Test
	public void testGetIdentifier()
	{
		assertEquals("NUMBER", FormatUtilities.NUMBER_MANAGER.getIdentifierType());
	}

	@Test
	public void testHashCodeEquals()
	{
		assertEquals(new NumberManager().hashCode(), FormatUtilities.NUMBER_MANAGER.hashCode());
		assertFalse(FormatUtilities.NUMBER_MANAGER.equals(new Object()));
		assertFalse(FormatUtilities.NUMBER_MANAGER.equals(new StringManager()));
		assertTrue(FormatUtilities.NUMBER_MANAGER.equals(new NumberManager()));
	}

	@Test
	public void testGetComponent()
	{
		assertTrue(FormatUtilities.NUMBER_MANAGER.getComponentManager().isEmpty());
	}

	@Test
	public void testIsDirect()
	{
		assertTrue(FormatUtilities.NUMBER_MANAGER.isDirect());
	}

	@Test
	public void testGetComparator()
	{
		Comparator<Number> comparator = new NumberManager().getComparator();
		assertEquals(0, comparator.compare(Integer.valueOf(1), Integer.valueOf(1)));
		assertEquals(1, comparator.compare(Integer.valueOf(21), Integer.valueOf(12)));
		assertEquals(-1, comparator.compare(Integer.valueOf(1), Integer.valueOf(2)));
		assertEquals(0, comparator.compare(Integer.valueOf(1), Double.valueOf(1)));
	}

	@Test
	public void testInitializeFrom()
	{
		SimpleValueStore valueStore = new SimpleValueStore();
		valueStore.addValueFor(FormatUtilities.NUMBER_MANAGER.getIdentifierType(), 1);
		Object value = FormatUtilities.NUMBER_MANAGER.initializeFrom(valueStore);
		assertEquals(1, value);
		valueStore.addValueFor(FormatUtilities.NUMBER_MANAGER.getIdentifierType(), 3);
		value = FormatUtilities.NUMBER_MANAGER.initializeFrom(valueStore);
		assertEquals(3, value);
	}
}
