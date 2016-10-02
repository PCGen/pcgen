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

import java.util.Arrays;

import junit.framework.TestCase;

public class ArrayFormatManagerTest extends TestCase
{
	private static final Number[] ARR_N3_4_5 = {Integer.valueOf(-3), Integer.valueOf(4), Integer.valueOf(5)};
	private static final Number[] ARR_N3_4P1_5 = {Integer.valueOf(-3), Double.valueOf(4.1), Integer.valueOf(5)};
	private static final Number[] ARR_1P4 = {Double.valueOf(1.4)};
	private static final Number[] ARR_N3 = {Integer.valueOf(-3)};
	private static final Number[] ARR_1 = {Integer.valueOf(1)};
	
	private ArrayFormatManager<Number> manager = new ArrayFormatManager<>(
		new NumberManager(), ',');

	public void testConstructor()
	{
		try
		{
			new ArrayFormatManager(null, ',');
			fail("null value should fail");
		}
		catch (IllegalArgumentException e)
		{
			//ok as well
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
			//ok as well
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
			//ok
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
			//ok as well
		}
	}

	public void testConvertIndirectFailBadSeparator()
	{
		try
		{
			manager.convertIndirect(",4,6");
			fail("starting comma value should fail");
		}
		catch (IllegalArgumentException e)
		{
			//ok as well
		}
		try
		{
			manager.convertIndirect("4,5,");
			fail("endign comma value should fail");
		}
		catch (IllegalArgumentException e)
		{
			//ok as well
		}
		try
		{
			manager.convertIndirect("3,4,,5");
			fail("doublecomma value should fail");
		}
		catch (IllegalArgumentException e)
		{
			//ok as well
		}
	}

	public void testConvertFailBadSeparator()
	{
		try
		{
			manager.convert(",4,6");
			fail("starting comma value should fail");
		}
		catch (IllegalArgumentException e)
		{
			//ok as well
		}
		try
		{
			manager.convert("4,5,");
			fail("endign comma value should fail");
		}
		catch (IllegalArgumentException e)
		{
			//ok as well
		}
		try
		{
			manager.convert("3,4,,5");
			fail("doublecomma value should fail");
		}
		catch (IllegalArgumentException e)
		{
			//ok as well
		}
	}

	public void testConvert()
	{
		assertTrue(Arrays.equals(new Number[]{}, manager.convert(null)));
		assertTrue(Arrays.equals(new Number[]{}, manager.convert("")));
		assertTrue(Arrays.equals(ARR_1, manager.convert("1")));
		assertTrue(Arrays.equals(ARR_N3, manager.convert("-3")));
		assertTrue(Arrays.equals(ARR_N3_4_5, manager.convert("-3,4,5")));
		assertTrue(Arrays.equals(ARR_1P4, manager.convert("1.4")));
		assertTrue(Arrays.equals(ARR_N3_4P1_5, manager.convert("-3,4.1,5")));
	}

	public void testUnconvert()
	{
		assertEquals("1", manager.unconvert(ARR_1));
		assertEquals("-3", manager.unconvert(ARR_N3));
		assertEquals("-3,4,5", manager.unconvert(ARR_N3_4_5));
		assertEquals("1.4", manager.unconvert(ARR_1P4));
		assertEquals("-3,4.1,5", manager.unconvert(ARR_N3_4P1_5));
		//Just to show it's not picky
		assertEquals("1.4", manager.unconvert(new Double[]{Double.valueOf(1.4)}));
		assertTrue(Arrays.equals(new Number[]{}, manager.convert(null)));
		assertTrue(Arrays.equals(new Number[]{}, manager.convert("")));
	}

	public void testConvertIndirect()
	{
		assertTrue(Arrays.equals(new Number[]{}, manager.convertIndirect(null).get()));
		assertTrue(Arrays.equals(new Number[]{}, manager.convertIndirect("").get()));
		assertTrue(Arrays.equals(ARR_1, manager.convertIndirect("1").get()));
		assertTrue(Arrays.equals(ARR_N3, manager.convertIndirect("-3").get()));
		assertTrue(Arrays.equals(ARR_1P4, manager.convertIndirect("1.4").get()));
		assertTrue(Arrays.equals(ARR_N3_4P1_5, manager.convertIndirect("-3,4.1,5").get()));
		assertTrue(Arrays.equals(ARR_N3_4_5, manager.convertIndirect("-3,4,5").get()));

		assertEquals("", manager.convertIndirect(null).getUnconverted());
		assertEquals("", manager.convertIndirect("").getUnconverted());
		assertEquals("1", manager.convertIndirect("1").getUnconverted());
		assertEquals("-3", manager.convertIndirect("-3").getUnconverted());
		assertEquals("1.4", manager.convertIndirect("1.4").getUnconverted());
		assertEquals("-3,4.1,5", manager.convertIndirect("-3,4.1,5").getUnconverted());
		assertEquals("-3,4,5", manager.convertIndirect("-3,4,5").getUnconverted());
	}

	public void testGetIdentifier()
	{
		assertEquals("ARRAY[NUMBER]", manager.getIdentifierType());
	}

	public void testManagedClass()
	{
		assertEquals(Number[].class, manager.getManagedClass());
	}

	public void testHashCodeEquals()
	{
		assertEquals(new ArrayFormatManager<>(new NumberManager(), ',').hashCode(), manager.hashCode());
		//different separator
		assertFalse(new ArrayFormatManager<>(new NumberManager(), '|').hashCode() == manager.hashCode());
		//different underlying
		assertFalse(new ArrayFormatManager<>(new BooleanManager(), ',').hashCode() == manager.hashCode());
		assertFalse(manager.equals(new Object()));
		assertFalse(manager.equals(new StringManager()));
		assertTrue(manager.equals(new ArrayFormatManager<>(new NumberManager(), ',')));
		//different separator
		assertFalse(manager.equals(new ArrayFormatManager<>(new NumberManager(), '|')));
		//different underlying
		assertFalse(manager.equals(new ArrayFormatManager<>(new BooleanManager(), ',')));
	}

	public void testGetComponent()
	{
		assertEquals(new NumberManager(), manager.getComponentManager());
	}
}
