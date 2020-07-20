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

import java.util.Optional;

import junit.framework.TestCase;
import pcgen.base.format.NumberManager;
import pcgen.base.formatmanager.GenericFormatManager;
import pcgen.base.formatmanager.SimpleFormatManagerLibrary;
import pcgen.base.util.FormatManager;
import pcgen.testsupport.MockObjectDatabase;

/**
 * Test the ColumnFormatFactory class
 */
public class ColumnFormatFactoryTest extends TestCase
{
	private final NumberManager numberManager = new NumberManager();
	MockObjectDatabase mod = new MockObjectDatabase();
	FormatManager<TableColumn> baseManager =
			new GenericFormatManager<>(mod, TableColumn.class, "IGNORED");
	ColumnFormatFactory factory = new ColumnFormatFactory(baseManager);

	public void testBuild()
	{
		SimpleFormatManagerLibrary library = new SimpleFormatManagerLibrary();
		library.addFormatManager(numberManager);
		assertEquals("COLUMN", factory.getBuilderBaseFormat());
		ColumnFormatManager<?> formatManager = (ColumnFormatManager<?>) factory
			.build(Optional.empty(), Optional.of("NUMBER"), library);
		assertEquals(TableColumn.class, formatManager.getManagedClass());
		assertTrue(formatManager.getComponentManager().isPresent());
		assertEquals(numberManager, formatManager.getComponentManager().get());
	}

	public void testBuildNoFormatAvailable()
	{
		SimpleFormatManagerLibrary library = new SimpleFormatManagerLibrary();
		assertEquals("COLUMN", factory.getBuilderBaseFormat());
		try
		{
			factory.build(Optional.empty(), Optional.of("NUMBER"), library);
			fail();
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//Expected!
		}
		try
		{
			factory.build(Optional.empty(), Optional.empty(), library);
			fail();
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//Expected!
		}
	}

	public void testBuildParentIllegal()
	{
		SimpleFormatManagerLibrary library = new SimpleFormatManagerLibrary();
		assertEquals("COLUMN", factory.getBuilderBaseFormat());
		try
		{
			factory.build(Optional.of("NUMBER"), Optional.of("NUMBER"),
				library);
			fail();
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//Expected!
		}
	}
}
