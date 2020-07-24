/*
 * Copyright 2020 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.format.table;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formatmanager.GenericFormatManager;
import pcgen.base.formatmanager.SimpleFormatManagerLibrary;
import pcgen.base.util.FormatManager;
import pcgen.testsupport.MockObjectDatabase;

/**
 * Test the TableFormatFactory class
 */
public class TableFormatFactoryTest
{
	private MockObjectDatabase mod = new MockObjectDatabase();
	private FormatManager<DataTable> baseManager =
			new GenericFormatManager<>(mod, DataTable.class, "IGNORED");
	private TableFormatFactory factory = new TableFormatFactory(baseManager);

	@AfterEach
	void tearDown()
	{
		factory = null;
		baseManager = null;
		mod = null;
	}

	@Test
	public void testBuild()
	{
		SimpleFormatManagerLibrary library = new SimpleFormatManagerLibrary();
		library.addFormatManager(FormatUtilities.NUMBER_MANAGER);
		assertEquals("TABLE", factory.getBuilderBaseFormat());
		TableFormatManager<?> formatManager = (TableFormatManager<?>) factory
			.build(Optional.empty(), Optional.of("NUMBER"), library);
		assertEquals(DataTable.class, formatManager.getManagedClass());
		assertTrue(formatManager.getComponentManager().isEmpty());
	}

	@Test
	public void testBuildNoFormatAvailable()
	{
		SimpleFormatManagerLibrary library = new SimpleFormatManagerLibrary();
		assertEquals("TABLE", factory.getBuilderBaseFormat());
		assertThrows(NullPointerException.class, () -> factory.build(Optional.empty(), null, library));
		assertThrows(NullPointerException.class, () -> factory.build(Optional.empty(), null, library));
		assertThrows(NullPointerException.class, () -> factory.build(null, Optional.of("NUMBER"), library));
		assertThrows(IllegalArgumentException.class, () -> factory.build(null, Optional.empty(), library));
		assertThrows(IllegalArgumentException.class, () -> factory.build(Optional.empty(), Optional.of("NUMBER"), library));
		assertThrows(IllegalArgumentException.class, () -> factory.build(Optional.empty(), Optional.empty(), library));
	}

	@Test
	public void testBuildNoLibraryAvailable()
	{
		assertThrows(NullPointerException.class, () -> factory.build(Optional.empty(), Optional.of("NUMBER"), null));
	}

	@Test
	public void testBuildParentIllegal()
	{
		SimpleFormatManagerLibrary library = new SimpleFormatManagerLibrary();
		assertEquals("TABLE", factory.getBuilderBaseFormat());
		assertThrows(IllegalArgumentException.class, () -> factory.build(Optional.of("NUMBER"), Optional.of("NUMBER"), library));
	}
}
