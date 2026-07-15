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

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.util.FormatManager;
import pcgen.base.util.SimpleValueStore;

/**
 * Test the BooleanManager class
 */
class BooleanManagerTest
{
	@Test
	void testConvertFailNull()
	{
		assertThrows(IllegalArgumentException.class, () -> FormatUtilities.BOOLEAN_MANAGER.convert(null));
	}

	@Test
	void testConvertFailNotBoolean()
	{
		assertThrows(IllegalArgumentException.class, () -> FormatUtilities.BOOLEAN_MANAGER.convert("SomeString"));
	}

	@Test
	void testUnconvertFailNull()
	{
		assertThrows(NullPointerException.class, () -> FormatUtilities.BOOLEAN_MANAGER.unconvert(null));
	}

	@Test
	@SuppressWarnings({"rawtypes", "unchecked"})
	void testUnconvertFailObject()
	{
		//Yes generics are being violated in order to do this test
		FormatManager formatManager = FormatUtilities.BOOLEAN_MANAGER;
		assertThrows(ClassCastException.class, () -> formatManager.unconvert(new Object()));
	}

	@Test
	void testConvertIndirectFailNull()
	{
		assertThrows(IllegalArgumentException.class, () -> FormatUtilities.BOOLEAN_MANAGER.convertIndirect(null));
	}

	@Test
	void testConvertIndirectFailNotBoolean()
	{
		assertThrows(IllegalArgumentException.class, () -> FormatUtilities.BOOLEAN_MANAGER.convertIndirect("SomeString"));
	}

	@Test
	void testConvert()
	{
		assertEquals(Boolean.TRUE, FormatUtilities.BOOLEAN_MANAGER.convert("true"));
		assertEquals(Boolean.FALSE, FormatUtilities.BOOLEAN_MANAGER.convert("false"));
		assertEquals(Boolean.TRUE, FormatUtilities.BOOLEAN_MANAGER.convert("True"));
	}

	@Test
	void testUnconvert()
	{
		assertEquals("true", FormatUtilities.BOOLEAN_MANAGER.unconvert(Boolean.TRUE));
		assertEquals("false", FormatUtilities.BOOLEAN_MANAGER.unconvert(Boolean.FALSE));
	}

	@Test
	void testConvertIndirect()
	{
		assertEquals(Boolean.TRUE, FormatUtilities.BOOLEAN_MANAGER.convertIndirect("true").get());
		assertEquals(Boolean.FALSE, FormatUtilities.BOOLEAN_MANAGER.convertIndirect("false").get());
	}

	@Test
	void testGetIdentifier()
	{
		assertEquals("BOOLEAN", FormatUtilities.BOOLEAN_MANAGER.getIdentifierType());
	}

	@Test
	void testHashCodeEquals()
	{
		assertEquals(new BooleanManager().hashCode(), FormatUtilities.BOOLEAN_MANAGER.hashCode());
		assertFalse(FormatUtilities.BOOLEAN_MANAGER.equals(new Object()));
		assertFalse(FormatUtilities.BOOLEAN_MANAGER.equals(new StringManager()));
		assertTrue(FormatUtilities.BOOLEAN_MANAGER.equals(new BooleanManager()));
	}

	@Test
	void testGetComponent()
	{
		assertTrue(FormatUtilities.BOOLEAN_MANAGER.getComponentManager().isEmpty());
	}

	@Test
	void testIsDirect()
	{
		assertTrue(FormatUtilities.BOOLEAN_MANAGER.isDirect());
	}

	@Test
	void testInitializeFrom()
	{
		SimpleValueStore valueStore = new SimpleValueStore();
		valueStore.addValueFor(FormatUtilities.BOOLEAN_MANAGER.getIdentifierType(), Boolean.FALSE);
		Object value = FormatUtilities.BOOLEAN_MANAGER.initializeFrom(valueStore);
		assertEquals(Boolean.FALSE, value);
		valueStore.addValueFor(FormatUtilities.BOOLEAN_MANAGER.getIdentifierType(), Boolean.TRUE);
		value = FormatUtilities.BOOLEAN_MANAGER.initializeFrom(valueStore);
		assertEquals(Boolean.TRUE, value);
	}
}
