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

import java.util.Optional;

import junit.framework.TestCase;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.base.util.SimpleValueStore;

/**
 * Test the OptionalFormatManager class
 */
public class OptionalFormatManagerTest extends TestCase
{
	private OptionalFormatManager<Number> manager =
			new OptionalFormatManager<>(new NumberManager());

	@SuppressWarnings("unused")
	public void testConstructor()
	{
		try
		{
			new OptionalFormatManager<>(null);
			fail("null value should fail");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//expected
		}
	}

	public void testConvertFailNotNumeric()
	{
		try
		{
			manager.convert("SomeString");
			fail("null value should fail");
		}
		catch (IllegalArgumentException e)
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

	@SuppressWarnings({"rawtypes", "unchecked"})
	public void testUnconvertFailObject()
	{
		try
		{
			//Yes generics are being violated in order to do this test
			FormatManager formatManager = manager;
			formatManager.unconvert(new Object());
			fail("Object should fail");
		}
		catch (ClassCastException | IllegalArgumentException e)
		{
			//expected
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public void testUnconvertFailUnderlying()
	{
		try
		{
			//Yes generics are being violated in order to do this test
			FormatManager formatManager = manager;
			formatManager.unconvert(1);
			fail("Integer should fail");
		}
		catch (ClassCastException | IllegalArgumentException e)
		{
			//expected
		}
	}

	public void testConvertIndirectFailNotNumeric()
	{
		try
		{
			manager.convertIndirect("SomeString");
			fail("null value should fail");
		}
		catch (IllegalArgumentException e)
		{
			//expected
		}
	}

	public void testConvertEmpty()
	{
		assertEquals(Optional.empty(), manager.convert(""));
		assertEquals(Optional.empty(), manager.convert(null));
	}

	public void testConvertIndirectEmpty()
	{
		assertEquals(Optional.empty(), manager.convertIndirect("").get());
		assertEquals(Optional.empty(), manager.convertIndirect(null).get());
	}

	public void testConvert()
	{
		assertEquals(Optional.of(1), manager.convert("1"));
		assertEquals(Optional.of(-3), manager.convert("-3"));
		assertEquals(Optional.of(1.4), manager.convert("1.4"));
	}

	public void testUnconvert()
	{
		assertEquals("", manager.unconvert(Optional.empty()));
		assertEquals("1", manager.unconvert(Optional.of(1)));
		assertEquals("-3", manager.unconvert(Optional.of(-3)));
		assertEquals("1.4", manager.unconvert(Optional.of(1.4)));
	}

	public void testConvertIndirect()
	{
		assertEquals(Optional.of(1), manager.convertIndirect("1").get());
		assertEquals(Optional.of(-3), manager.convertIndirect("-3").get());
		assertEquals(Optional.of(1.4), manager.convertIndirect("1.4").get());

		assertEquals("1", manager.convertIndirect("1").getUnconverted());
		assertEquals("-3", manager.convertIndirect("-3").getUnconverted());
		assertEquals("1.4", manager.convertIndirect("1.4").getUnconverted());
	}

	public void testGetIdentifier()
	{
		assertEquals("OPTIONAL[NUMBER]", manager.getIdentifierType());
	}

	public void testManagedClass()
	{
		assertSame(Optional.class, manager.getManagedClass());
	}

	public void testHashCodeEquals()
	{
		assertEquals(
			new OptionalFormatManager<>(new NumberManager()).hashCode(),
			manager.hashCode());
		//different underlying
		assertFalse(
			manager.equals(new OptionalFormatManager<>(new BooleanManager())));
	}

	public void testGetComponent()
	{
		assertEquals(new NumberManager(), manager.getComponentManager().get());
	}

	public void testIsDirect()
	{
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

	public void testInitializeFrom()
	{
		assertEquals(Optional.empty(),
			manager.initializeFrom(new SimpleValueStore()));
	}

}
