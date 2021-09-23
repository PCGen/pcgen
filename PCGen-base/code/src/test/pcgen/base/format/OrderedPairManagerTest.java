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

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.math.OrderedPair;
import pcgen.base.util.FormatManager;
import pcgen.base.util.SimpleValueStore;

/**
 * Test the OrderedPairManager class
 */
public class OrderedPairManagerTest
{
	@Test
	public void testConvertFailNull()
	{
		assertThrows(NullPointerException.class, () -> FormatUtilities.ORDEREDPAIR_MANAGER.convert(null));
	}

	@Test
	public void testConvertFailNotNumeric()
	{
		assertThrows(IllegalArgumentException.class, () -> FormatUtilities.ORDEREDPAIR_MANAGER.convert("SomeString"));
	}

	@Test
	public void testUnconvertFailNull()
	{
		assertThrows(NullPointerException.class, () -> FormatUtilities.ORDEREDPAIR_MANAGER.unconvert(null));
	}

	@Test
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void testUnconvertFailObject()
	{
		//Yes generics are being violated in order to do this test
		FormatManager formatManager = FormatUtilities.ORDEREDPAIR_MANAGER;
		assertThrows(ClassCastException.class, () -> formatManager.unconvert(new Object()));
	}

	@Test
	public void testConvertIndirectFailNull()
	{
		assertThrows(NullPointerException.class, () -> FormatUtilities.ORDEREDPAIR_MANAGER.convertIndirect(null));
	}

	@Test
	public void testConvertIndirectFailNotNumeric()
	{
		assertThrows(IllegalArgumentException.class, () -> FormatUtilities.ORDEREDPAIR_MANAGER.convertIndirect("SomeString"));
	}

	@Test
	public void testConvert()
	{
		assertEquals(new OrderedPair(1, 1), FormatUtilities.ORDEREDPAIR_MANAGER.convert("1,1"));
		assertEquals(new OrderedPair(-3, 4), FormatUtilities.ORDEREDPAIR_MANAGER.convert("-3,4"));
		assertEquals(new OrderedPair(new BigDecimal("1.4"), new BigDecimal("6.5")), FormatUtilities.ORDEREDPAIR_MANAGER.convert("1.4,6.5"));
	}

	@Test
	public void testUnconvert()
	{
		assertEquals("1,2", FormatUtilities.ORDEREDPAIR_MANAGER.unconvert(new OrderedPair(1, 2)));
		assertEquals("-3,4", FormatUtilities.ORDEREDPAIR_MANAGER.unconvert(new OrderedPair(-3, 4)));
		assertEquals("1.4,6.5", FormatUtilities.ORDEREDPAIR_MANAGER.unconvert(new OrderedPair(1.4, 6.5)));
	}

	@Test
	public void testConvertIndirect()
	{
		assertEquals(new OrderedPair(1, 1), FormatUtilities.ORDEREDPAIR_MANAGER.convertIndirect("1,1")
			.get());
		assertEquals(new OrderedPair(-3, 4), FormatUtilities.ORDEREDPAIR_MANAGER.convertIndirect("-3,4")
			.get());
		assertEquals(new OrderedPair(new BigDecimal("1.4"), new BigDecimal("6.5")), FormatUtilities.ORDEREDPAIR_MANAGER
			.convertIndirect("1.4,6.5").get());
	}

	@Test
	public void testGetIdentifier()
	{
		assertEquals("ORDEREDPAIR", FormatUtilities.ORDEREDPAIR_MANAGER.getIdentifierType());
	}

	@Test
	public void testHashCodeEquals()
	{
		assertEquals(new OrderedPairManager().hashCode(), FormatUtilities.ORDEREDPAIR_MANAGER.hashCode());
		assertFalse(FormatUtilities.ORDEREDPAIR_MANAGER.equals(new Object()));
		assertFalse(FormatUtilities.ORDEREDPAIR_MANAGER.equals(new StringManager()));
		assertTrue(FormatUtilities.ORDEREDPAIR_MANAGER.equals(new OrderedPairManager()));
	}

	@Test
	public void testGetComponent()
	{
		assertTrue(FormatUtilities.ORDEREDPAIR_MANAGER.getComponentManager().isEmpty());
	}

	@Test
	public void testIsDirect()
	{
		assertTrue(FormatUtilities.ORDEREDPAIR_MANAGER.isDirect());
	}

	@Test
	public void testInitializeFrom()
	{
		SimpleValueStore valueStore = new SimpleValueStore();
		valueStore.addValueFor(FormatUtilities.ORDEREDPAIR_MANAGER.getIdentifierType(), new OrderedPair(1, 3));
		Object value = FormatUtilities.ORDEREDPAIR_MANAGER.initializeFrom(valueStore);
		assertEquals(new OrderedPair(1, 3), value);
		valueStore.addValueFor(FormatUtilities.ORDEREDPAIR_MANAGER.getIdentifierType(), new OrderedPair(2, 6));
		value = FormatUtilities.ORDEREDPAIR_MANAGER.initializeFrom(valueStore);
		assertEquals(new OrderedPair(2, 6), value);
	}
}
