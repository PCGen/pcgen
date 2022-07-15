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
 * Test the ArrayFormatFactory class
 */
public class ArrayFormatFactoryTest
{
	private SimpleFormatManagerLibrary library;

	@BeforeEach
	void setUp()
	{
		library = new SimpleFormatManagerLibrary();
		FormatUtilities.loadDefaultFormats(library);
		library.addFormatManagerBuilder(TestSupport.ARRAY_FACTORY);
	}

	@AfterEach
	void tearDown()
	{
		library = null;
	}

	@Test
	public void testFailBadSubFormat()
	{
		assertThrows(IllegalArgumentException.class, () -> TestSupport.ARRAY_FACTORY.build(Optional.empty(), Optional.of("NUM"), library));
	}

	@Test
	public void testFailNullSubFormat()
	{
		assertThrows(IllegalArgumentException.class, () -> TestSupport.ARRAY_FACTORY.build(Optional.empty(), Optional.empty(), library));
	}

	@Test
	public void testConvert()
	{
		@SuppressWarnings("unchecked")
		FormatManager<Number[]> manager =
				(FormatManager<Number[]>) TestSupport.ARRAY_FACTORY.build(Optional.empty(), Optional.of("NUMBER"), library);
		assertTrue(Arrays.equals(new Number[]{}, manager.convert(null)));
		assertTrue(Arrays.equals(new Number[]{}, manager.convert("")));
		assertTrue(Arrays.equals(TestSupport.ARR_1, manager.convert("1")));
		assertTrue(Arrays.equals(TestSupport.ARR_N3, manager.convert("-3")));
		assertTrue(Arrays.equals(TestSupport.ARR_N3_4_5, manager.convert("-3,4,5")));
		assertTrue(Arrays.equals(TestSupport.ARR_1P4, manager.convert("1.4")));
		assertTrue(Arrays.equals(TestSupport.ARR_N3_4P1_5, manager.convert("-3,4.1,5")));
	}

	@Test
	public void testGetIdentifier()
	{
		FormatManager<?> manager = TestSupport.ARRAY_FACTORY.build(Optional.empty(), Optional.of("NUMBER"), library);
		assertEquals("ARRAY[NUMBER]", manager.getIdentifierType());
		manager = TestSupport.ARRAY_FACTORY.build(Optional.empty(), Optional.of("STRING"), library);
		assertEquals("ARRAY[STRING]", manager.getIdentifierType());
	}

	@Test
	public void testManagedClass()
	{
		FormatManager<?> manager = TestSupport.ARRAY_FACTORY.build(Optional.empty(), Optional.of("NUMBER"), library);
		assertSame(Number[].class, manager.getManagedClass());
		manager = TestSupport.ARRAY_FACTORY.build(Optional.empty(), Optional.of("STRING"), library);
		assertSame(String[].class, manager.getManagedClass());
	}

	@Test
	public void testGetComponent()
	{
		FormatManager<?> manager = TestSupport.ARRAY_FACTORY.build(Optional.empty(), Optional.of("NUMBER"), library);
		assertEquals(new NumberManager(), manager.getComponentManager().get());
		manager = TestSupport.ARRAY_FACTORY.build(Optional.empty(), Optional.of("STRING"), library);
		assertEquals(new StringManager(), manager.getComponentManager().get());
	}

	/*
	 * Note that this is currently failing due to an error check in ArrayFormatFactory.
	 * This is not "strict" behavior, in that if an enhancement is made that fixes this
	 * limitation, please remove this test.
	 */
	@Test
	public void testFailInvalidSub()
	{
		assertFalse(library.hasFormatManager("ARRAY[ARRAY[NUMBER]]"));
		assertThrows(IllegalArgumentException.class, () -> library.getFormatManager(Optional.empty(), "ARRAY[ARRAY[NUMBER]]"));
	}

}
