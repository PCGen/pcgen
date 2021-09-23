/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net> This program is
 * free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version.
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
package pcgen.base.formatmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.base.format.NumberManager;
import pcgen.base.format.StringManager;
import pcgen.base.util.FormatManager;
import pcgen.testsupport.TestSupport;

/**
 * Test the SimpleFormatManagerLibrary class
 */
public class SimpleFormatManagerLibraryTest
{
	private SimpleFormatManagerLibrary library;

	@BeforeEach
	void setUp()
	{
		library = new SimpleFormatManagerLibrary();
		FormatUtilities.loadDefaultFormats(library);
		FormatUtilities.loadDefaultFactories(library);
	}

	@AfterEach
	void tearDown()
	{
		library = null;
	}

	@Test
	public void testFailOnlyClose()
	{
		assertFalse(library.hasFormatManager("NUMBER]"));
		assertThrows(IllegalArgumentException.class, () -> library.getFormatManager(Optional.empty(), "NUMBER]"));
	}

	@Test
	public void testFailInvalidSub()
	{
		assertFalse(library.hasFormatManager("NUMBER[NUMBER]"));
		assertThrows(IllegalArgumentException.class, () -> library.getFormatManager(Optional.empty(), "NUMBER[NUMBER]"));
	}

	@Test
	public void testBadFormatFail()
	{
		assertFalse(library.hasFormatManager("NIMBLER"));
		assertThrows(IllegalArgumentException.class, () -> library.getFormatManager(Optional.empty(), "NIMBLER"));
	}

	@Test
	public void testGetBad()
	{
		assertFalse(library.hasFormatManager("ARRAY[NUMBER"));
		assertThrows(IllegalArgumentException.class, () -> library.getFormatManager(Optional.empty(), "ARRAY[NUMBER"));
	}

	@Test
	public void testOneOnly()
	{
		assertThrows(IllegalArgumentException.class, () -> library.addFormatManager(new NumberManager()
			{
				@Override
				public String getIdentifierType()
				{
					//To force equality failure
					return "STRING";
				}
			}));

		//This is okay (equality)
		library.addFormatManager(new NumberManager());
	}

	@Test
	public void testConvertFailBadSeparator()
	{
		assertFalse(library.hasFormatManager("ARRAY[NIMBLER]"));
		assertThrows(IllegalArgumentException.class, () -> library.getFormatManager(Optional.empty(), "ARRAY[NIMBLER]"));
	}

	@Test
	public void testConvert()
	{
		assertTrue(library.hasFormatManager("NUMBER"));
		assertTrue(library.hasFormatManager("ARRAY[NUMBER]"));
		@SuppressWarnings("unchecked")
		FormatManager<Number[]> manager =
				(FormatManager<Number[]>) library
					.getFormatManager(Optional.empty(), "ARRAY[NUMBER]");
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
		@SuppressWarnings("unchecked")
		FormatManager<Number[]> manager =
				(FormatManager<Number[]>) library
					.getFormatManager(Optional.empty(), "ARRAY[NUMBER]");
		assertEquals("1", manager.unconvert(TestSupport.ARR_1));
		assertEquals("-3", manager.unconvert(TestSupport.ARR_N3));
		assertEquals("-3,4,5", manager.unconvert(TestSupport.ARR_N3_4_5));
		assertEquals("1.4", manager.unconvert(TestSupport.ARR_1P4));
		assertEquals("-3,4.1,5", manager.unconvert(TestSupport.ARR_N3_4P1_5));
		//Just to show it's not picky
		assertEquals("1.4",
			manager.unconvert(new Double[]{Double.valueOf(1.4)}));
	}

	@Test
	public void testConvertIndirect()
	{
		@SuppressWarnings("unchecked")
		FormatManager<Number[]> manager =
				(FormatManager<Number[]>) library
					.getFormatManager(Optional.empty(), "ARRAY[NUMBER]");
		assertTrue(Arrays.equals(new Number[]{}, manager.convertIndirect(null)
			.get()));
		assertTrue(Arrays.equals(new Number[]{}, manager.convertIndirect("")
			.get()));
		assertTrue(Arrays.equals(TestSupport.ARR_1, manager.convertIndirect("1").get()));
		assertTrue(Arrays.equals(TestSupport.ARR_N3, manager.convertIndirect("-3").get()));
		assertTrue(Arrays.equals(TestSupport.ARR_1P4, manager.convertIndirect("1.4").get()));
		assertTrue(Arrays.equals(TestSupport.ARR_N3_4P1_5,
			manager.convertIndirect("-3,4.1,5").get()));
		assertTrue(Arrays.equals(TestSupport.ARR_N3_4_5, manager.convertIndirect("-3,4,5")
			.get()));

		assertEquals("", manager.convertIndirect(null).getUnconverted());
		assertEquals("", manager.convertIndirect("").getUnconverted());
		assertEquals("1", manager.convertIndirect("1").getUnconverted());
		assertEquals("-3", manager.convertIndirect("-3").getUnconverted());
		assertEquals("1.4", manager.convertIndirect("1.4").getUnconverted());
		assertEquals("-3,4.1,5", manager.convertIndirect("-3,4.1,5")
			.getUnconverted());
		assertEquals("-3,4,5", manager.convertIndirect("-3,4,5")
			.getUnconverted());
	}

	@Test
	public void testGetIdentifier()
	{
		FormatManager<?> manager = library.getFormatManager(Optional.empty(), "ARRAY[NUMBER]");
		assertEquals("ARRAY[NUMBER]", manager.getIdentifierType());
		manager = library.getFormatManager(Optional.empty(), "ARRAY[STRING]");
		assertEquals("ARRAY[STRING]", manager.getIdentifierType());
		manager = library.getFormatManager(Optional.empty(), "STRING");
		assertEquals("STRING", manager.getIdentifierType());
	}

	@Test
	public void testManagedClass()
	{
		FormatManager<?> manager = library.getFormatManager(Optional.empty(), "ARRAY[NUMBER]");
		assertSame(Number[].class, manager.getManagedClass());
		manager = library.getFormatManager(Optional.empty(), "ARRAY[STRING]");
		assertSame(String[].class, manager.getManagedClass());
		manager = library.getFormatManager(Optional.empty(), "STRING");
		assertSame(String.class, manager.getManagedClass());
	}

	@Test
	public void testGetComponent()
	{
		FormatManager<?> manager = library.getFormatManager(Optional.empty(), "ARRAY[NUMBER]");
		assertEquals(new NumberManager(), manager.getComponentManager().get());
		manager = library.getFormatManager(Optional.empty(), "ARRAY[STRING]");
		assertEquals(new StringManager(), manager.getComponentManager().get());
		manager = library.getFormatManager(Optional.empty(), "STRING");
		assertTrue(manager.getComponentManager().isEmpty());
	}
}
