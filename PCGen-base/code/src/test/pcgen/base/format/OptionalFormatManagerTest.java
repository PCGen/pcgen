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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.base.util.SimpleValueStore;

/**
 * Test the OptionalFormatManager class
 */
public class OptionalFormatManagerTest
{
	@Test
	public void testConstructor()
	{
		assertThrows(NullPointerException.class, () -> new OptionalFormatManager<>(null));
	}

	@Test
	public void testConvertFailNotNumeric()
	{
		OptionalFormatManager<Number> manager =
				new OptionalFormatManager<>(new NumberManager());
		assertThrows(IllegalArgumentException.class, () -> manager.convert("SomeString"));
	}

	@Test
	public void testUnconvertFailNull()
	{
		OptionalFormatManager<Number> manager =
				new OptionalFormatManager<>(new NumberManager());
		assertThrows(NullPointerException.class, () -> manager.unconvert(null));
	}

	@Test
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void testUnconvertFailObject()
	{
		//Yes generics are being violated in order to do this test
		FormatManager formatManager = new OptionalFormatManager<>(new NumberManager());
		assertThrows(ClassCastException.class, () -> formatManager.unconvert(new Object()));
	}

	@Test
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void testUnconvertFailUnderlying()
	{
		//Yes generics are being violated in order to do this test
		FormatManager formatManager = new OptionalFormatManager<>(new NumberManager());
		assertThrows(ClassCastException.class, () -> formatManager.unconvert(1));
	}

	@Test
	public void testConvertIndirectFailNotOptional()
	{
		OptionalFormatManager<Number> manager =
				new OptionalFormatManager<>(new NumberManager());
		assertThrows(IllegalArgumentException.class, () -> manager.convertIndirect("SomeString"));
	}

	@Test
	public void testConvertEmpty()
	{
		OptionalFormatManager<Number> manager =
				new OptionalFormatManager<>(new NumberManager());
		assertEquals(Optional.empty(), manager.convert(""));
		assertEquals(Optional.empty(), manager.convert(null));
	}

	@Test
	public void testConvertIndirectEmpty()
	{
		OptionalFormatManager<Number> manager =
				new OptionalFormatManager<>(new NumberManager());
		assertEquals(Optional.empty(), manager.convertIndirect("").get());
		assertEquals(Optional.empty(), manager.convertIndirect(null).get());
	}

	@Test
	public void testConvert()
	{
		OptionalFormatManager<Number> manager =
				new OptionalFormatManager<>(new NumberManager());
		assertEquals(Optional.of(1), manager.convert("1"));
		assertEquals(Optional.of(-3), manager.convert("-3"));
		assertEquals(Optional.of(1.4), manager.convert("1.4"));
	}

	@Test
	public void testUnconvert()
	{
		OptionalFormatManager<Number> manager =
				new OptionalFormatManager<>(new NumberManager());
		assertEquals("", manager.unconvert(Optional.empty()));
		assertEquals("1", manager.unconvert(Optional.of(1)));
		assertEquals("-3", manager.unconvert(Optional.of(-3)));
		assertEquals("1.4", manager.unconvert(Optional.of(1.4)));
	}

	@Test
	public void testConvertIndirect()
	{
		OptionalFormatManager<Number> manager =
				new OptionalFormatManager<>(new NumberManager());
		assertEquals(Optional.of(1), manager.convertIndirect("1").get());
		assertEquals(Optional.of(-3), manager.convertIndirect("-3").get());
		assertEquals(Optional.of(1.4), manager.convertIndirect("1.4").get());

		assertEquals("1", manager.convertIndirect("1").getUnconverted());
		assertEquals("-3", manager.convertIndirect("-3").getUnconverted());
		assertEquals("1.4", manager.convertIndirect("1.4").getUnconverted());
	}

	@Test
	public void testGetIdentifier()
	{
		OptionalFormatManager<Number> manager =
				new OptionalFormatManager<>(new NumberManager());
		assertEquals("OPTIONAL[NUMBER]", manager.getIdentifierType());
	}

	@Test
	public void testManagedClass()
	{
		OptionalFormatManager<Number> manager =
				new OptionalFormatManager<>(new NumberManager());
		assertSame(Optional.class, manager.getManagedClass());
	}

	@Test
	public void testHashCodeEquals()
	{
		OptionalFormatManager<Number> manager =
				new OptionalFormatManager<>(new NumberManager());
		assertEquals(
			new OptionalFormatManager<>(new NumberManager()).hashCode(),
			manager.hashCode());
		//different underlying
		assertFalse(
			manager.equals(new OptionalFormatManager<>(new BooleanManager())));
	}

	@Test
	public void testGetComponent()
	{
		OptionalFormatManager<Number> manager =
				new OptionalFormatManager<>(new NumberManager());
		assertEquals(new NumberManager(), manager.getComponentManager().get());
	}

	@Test
	public void testIsDirect()
	{
		OptionalFormatManager<Number> manager =
				new OptionalFormatManager<>(new NumberManager());
		assertTrue(manager.isDirect());
		assertTrue(
			new OptionalFormatManager<>(new BooleanManager()).isDirect());
		assertTrue(new OptionalFormatManager<>(new StringManager()).isDirect());
		assertFalse(new OptionalFormatManager<>(new FormatManager<Object>()
		{

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

		}).isDirect());
	}

	@Test
	public void testInitializeFrom()
	{
		OptionalFormatManager<Number> manager =
				new OptionalFormatManager<>(new NumberManager());
		assertEquals(Optional.empty(),
			manager.initializeFrom(new SimpleValueStore()));
	}


	@Test
	public void testEquals()
	{
		OptionalFormatManager<Number> manager1 =
				new OptionalFormatManager<>(new NumberManager());
		OptionalFormatManager<String> manager2 =
				new OptionalFormatManager<>(new StringManager());
		OptionalFormatManager<Number> manager3 =
				new OptionalFormatManager<>(new NumberManager());
		assertFalse(manager1.equals(manager2));
		assertFalse(manager2.equals(manager1));
		assertTrue(manager1.equals(manager3));
	}

}
