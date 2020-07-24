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
package pcgen.base.formatmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.base.format.NumberManager;
import pcgen.base.format.StringManager;
import pcgen.base.util.FormatManager;

/**
 * Test the OptionalFormatFactory class
 */
public class OptionalFormatFactoryTest
{

	private SimpleFormatManagerLibrary library;
	private OptionalFormatFactory factory;

	@BeforeEach
	void setUp()
	{
		library = new SimpleFormatManagerLibrary();
		FormatUtilities.loadDefaultFormats(library);
		factory = new OptionalFormatFactory();
		library.addFormatManagerBuilder(factory);
	}

	@AfterEach
	void tearDown()
	{
		library = null;
		factory = null;
	}

	@Test
	public void testFailBadSubFormat()
	{
		assertThrows(IllegalArgumentException.class, () -> factory.build(Optional.empty(), Optional.of("NUM"), library));
	}

	@Test
	public void testFailEmptySubFormat()
	{
		assertThrows(IllegalArgumentException.class, () -> factory.build(Optional.empty(), Optional.empty(), library));
	}

	@Test
	public void testConvert()
	{
		@SuppressWarnings("unchecked")
		FormatManager<Optional<Number>> manager =
				(FormatManager<Optional<Number>>) factory
					.build(Optional.empty(), Optional.of("NUMBER"), library);
		assertEquals(Optional.empty(), manager.convert(null));
		assertEquals(Optional.empty(), manager.convert(""));
		assertEquals(1, manager.convert("1").get());
		assertEquals(-3, manager.convert("-3").get());
		assertEquals(1.4, manager.convert("1.4").get());
	}

	@Test
	public void testGetIdentifier()
	{
		FormatManager<?> manager =
				factory.build(Optional.empty(), Optional.of("NUMBER"), library);
		assertEquals("OPTIONAL[NUMBER]", manager.getIdentifierType());
		manager =
				factory.build(Optional.empty(), Optional.of("STRING"), library);
		assertEquals("OPTIONAL[STRING]", manager.getIdentifierType());
	}

	@Test
	public void testManagedClass()
	{
		FormatManager<?> manager =
				factory.build(Optional.empty(), Optional.of("NUMBER"), library);
		assertEquals(Optional.class, manager.getManagedClass());
		assertTrue(manager.getComponentManager().isPresent());
		assertEquals(new NumberManager(), manager.getComponentManager().get());
		manager =
				factory.build(Optional.empty(), Optional.of("STRING"), library);
		assertEquals(Optional.class, manager.getManagedClass());
		assertTrue(manager.getComponentManager().isPresent());
		assertEquals(new StringManager(), manager.getComponentManager().get());
	}

	@Test
	public void testGetComponent()
	{
		FormatManager<?> manager =
				factory.build(Optional.empty(), Optional.of("NUMBER"), library);
		assertEquals(new NumberManager(), manager.getComponentManager().get());
		manager =
				factory.build(Optional.empty(), Optional.of("STRING"), library);
		assertEquals(new StringManager(), manager.getComponentManager().get());
	}

}
