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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.base.util.SimpleValueStore;
import pcgen.testsupport.TestSupport;

/**
 * Test the ArrayFormatManager class
 */
public class ArrayFormatManagerTest
{
	@Test
	public void testConstructor()
	{
		assertThrows(NullPointerException.class, () -> new ArrayFormatManager<>(null, '\n', ','));
	}

	@Test
	public void testConvertFailNotNumeric()
	{
		assertThrows(IllegalArgumentException.class, () -> TestSupport.NUMBER_ARRAY_MANAGER.convert("SomeString"));
	}

	@Test
	public void testUnconvertFailNull()
	{
		assertThrows(NullPointerException.class, () -> TestSupport.NUMBER_ARRAY_MANAGER.unconvert(null));
	}

	@Test
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void testUnconvertFailObject()
	{
		//Yes, generics violated
		FormatManager formatManager = TestSupport.NUMBER_ARRAY_MANAGER;
		assertThrows(ClassCastException.class, () -> formatManager.unconvert(new Object()));
	}

	@Test
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void testUnconvertFailUnderlying()
	{
		//Yes, generics violated
		FormatManager formatManager = TestSupport.NUMBER_ARRAY_MANAGER;
		assertThrows(ClassCastException.class, () -> formatManager.unconvert(1));
	}

	@Test
	public void testConvertIndirectFailNotNumeric()
	{
		assertThrows(IllegalArgumentException.class, () -> TestSupport.NUMBER_ARRAY_MANAGER.convertIndirect("SomeString"));
	}

	@Test
	public void testConvertIndirectFailBadSeparatorLeading()
	{
		assertThrows(IllegalArgumentException.class, () -> TestSupport.NUMBER_ARRAY_MANAGER.convertIndirect(",4,6"));
	}

	@Test
	public void testConvertIndirectFailBadSeparatorTrailing()
	{
		assertThrows(IllegalArgumentException.class, () -> TestSupport.NUMBER_ARRAY_MANAGER.convertIndirect("4,5,"));
	}

	@Test
	public void testConvertIndirectFailBadSeparatorMiddle()
	{
		assertThrows(IllegalArgumentException.class, () -> TestSupport.NUMBER_ARRAY_MANAGER.convertIndirect("3,4,,5"));
	}

	@Test
	public void testConvertFailBadSeparatorLeading()
	{
		assertThrows(IllegalArgumentException.class, () -> TestSupport.NUMBER_ARRAY_MANAGER.convert(",4,6"));
	}

	@Test
	public void testConvertFailBadSeparatorTrailing()
	{
		assertThrows(IllegalArgumentException.class, () -> TestSupport.NUMBER_ARRAY_MANAGER.convert("4,5,"));
	}

	@Test
	public void testConvertFailBadSeparatorMiddle()
	{
		assertThrows(IllegalArgumentException.class, () -> TestSupport.NUMBER_ARRAY_MANAGER.convert("3,4,,5"));
	}

	@Test
	public void testConvert()
	{
		ArrayFormatManager<Number> manager = TestSupport.NUMBER_ARRAY_MANAGER;
		assertTrue(Arrays.equals(new Number[]{}, manager.convert(null)));
		assertTrue(Arrays.equals(new Number[]{}, manager.convert("")));
		assertTrue(Arrays.equals(TestSupport.ARR_1, manager.convert("1")));
		assertTrue(Arrays.equals(TestSupport.ARR_N3, manager.convert("-3")));
		assertTrue(Arrays.equals(TestSupport.ARR_N3_4_5, manager.convert("-3,4,5")));
		assertTrue(Arrays.equals(TestSupport.ARR_1P4, manager.convert("1.4")));
		assertTrue(Arrays.equals(TestSupport.ARR_N3_4P1_5, manager.convert("-3,4.1,5")));
	}

	@Test
	public void testUnconvert()
	{
		ArrayFormatManager<Number> manager = TestSupport.NUMBER_ARRAY_MANAGER;
		assertEquals("1", manager.unconvert(TestSupport.ARR_1));
		assertEquals("-3", manager.unconvert(TestSupport.ARR_N3));
		assertEquals("-3,4,5", manager.unconvert(TestSupport.ARR_N3_4_5));
		assertEquals("1.4", manager.unconvert(TestSupport.ARR_1P4));
		assertEquals("-3,4.1,5", manager.unconvert(TestSupport.ARR_N3_4P1_5));
		//Just to show it's not picky
		assertEquals("1.4", manager.unconvert(new Double[]{Double.valueOf(1.4)}));
		assertTrue(Arrays.equals(new Number[]{}, manager.convert(null)));
		assertTrue(Arrays.equals(new Number[]{}, manager.convert("")));
	}

	@Test
	public void testConvertIndirect()
	{
		ArrayFormatManager<Number> manager = TestSupport.NUMBER_ARRAY_MANAGER;
		assertTrue(Arrays.equals(new Number[]{}, manager.convertIndirect(null).get()));
		assertTrue(Arrays.equals(new Number[]{}, manager.convertIndirect("").get()));
		assertTrue(Arrays.equals(TestSupport.ARR_1, manager.convertIndirect("1").get()));
		assertTrue(Arrays.equals(TestSupport.ARR_N3, manager.convertIndirect("-3").get()));
		assertTrue(Arrays.equals(TestSupport.ARR_1P4, manager.convertIndirect("1.4").get()));
		assertTrue(Arrays.equals(TestSupport.ARR_N3_4P1_5, manager.convertIndirect("-3,4.1,5").get()));
		assertTrue(Arrays.equals(TestSupport.ARR_N3_4_5, manager.convertIndirect("-3,4,5").get()));

		assertEquals("", manager.convertIndirect(null).getUnconverted());
		assertEquals("", manager.convertIndirect("").getUnconverted());
		assertEquals("1", manager.convertIndirect("1").getUnconverted());
		assertEquals("-3", manager.convertIndirect("-3").getUnconverted());
		assertEquals("1.4", manager.convertIndirect("1.4").getUnconverted());
		assertEquals("-3,4.1,5", manager.convertIndirect("-3,4.1,5").getUnconverted());
		assertEquals("-3,4,5", manager.convertIndirect("-3,4,5").getUnconverted());
	}

	@Test
	public void testGetIdentifier()
	{
		assertEquals("ARRAY[NUMBER]", TestSupport.NUMBER_ARRAY_MANAGER.getIdentifierType());
	}

	@Test
	public void testManagedClass()
	{
		assertSame(Number[].class, TestSupport.NUMBER_ARRAY_MANAGER.getManagedClass());
	}

	@Test
	public void testHashCodeEquals()
	{
		ArrayFormatManager<Number> manager = TestSupport.NUMBER_ARRAY_MANAGER;
		assertEquals(new ArrayFormatManager<>(new NumberManager(), '\n', ',').hashCode(), manager.hashCode());
		//different list separator
		assertFalse(new ArrayFormatManager<>(new NumberManager(), '\n', '|').hashCode() == manager.hashCode());
		//different group separator
		assertFalse(new ArrayFormatManager<>(new NumberManager(), '-', '|').hashCode() == manager.hashCode());
		//different underlying
		assertFalse(new ArrayFormatManager<>(new BooleanManager(), '\n', ',').hashCode() == manager.hashCode());
		assertFalse(manager.equals(new Object()));
		assertFalse(manager.equals(new StringManager()));
		assertTrue(manager.equals(new ArrayFormatManager<>(new NumberManager(), '\n', ',')));
		//different list separator
		assertFalse(manager.equals(new ArrayFormatManager<>(new NumberManager(), '\n', '|')));
		//different group separator
		assertFalse(manager.equals(new ArrayFormatManager<>(new NumberManager(), '-', '|')));
		//different underlying
		assertFalse(manager.equals(new ArrayFormatManager<>(new BooleanManager(), '\n', ',')));
	}

	@Test
	public void testGetComponent()
	{
		assertEquals(new NumberManager(), TestSupport.NUMBER_ARRAY_MANAGER.getComponentManager().get());
	}
	
	@Test
	public void testEscapeNeeded()
	{
		ArrayFormatManager<Number> otherManager =
				new ArrayFormatManager<>(new NumberManager(), '\n', '|');
		assertTrue(Arrays.equals(TestSupport.ARR_N3_4_5, otherManager.convert("-3|4|5")));
		assertTrue(Arrays.equals(TestSupport.ARR_N3_4P1_5, otherManager.convert("-3|4.1|5")));
	}

	@Test
	public void testIsDirect()
	{
		assertTrue(TestSupport.NUMBER_ARRAY_MANAGER.isDirect());
		assertTrue(new ArrayFormatManager<>(new BooleanManager(), '\n', ',').isDirect());
		assertTrue(new ArrayFormatManager<>(new StringManager(), '\n', ',').isDirect());
		assertFalse(new ArrayFormatManager<>(new FormatManager<Object>() {

			@Override
			public Object convert(String inputStr)
			{
				return null;
			}

			@Override
			public Indirect<Object> convertIndirect(String inputStr)
			{
				return null;
			}

			@Override
			public boolean isDirect()
			{
				return false;
			}

			@Override
			public String unconvert(Object obj)
			{
				return null;
			}

			@Override
			public Class<Object> getManagedClass()
			{
				return Object.class;
			}

			@Override
			public String getIdentifierType()
			{
				return null;
			}

			@Override
			public Optional<FormatManager<?>> getComponentManager()
			{
				return Optional.empty();
			}
			
		}, '\n', ',').isDirect());
	}

	@Test
	public void testInitializeFrom()
	{
		Object value = TestSupport.NUMBER_ARRAY_MANAGER.initializeFrom(new SimpleValueStore());
		assertTrue(value.getClass().isArray());
		Object[] array = (Object[]) value;
		assertEquals(0, array.length);
	}
	
}

