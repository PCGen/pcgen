/*
 * Copyright 2020 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.format.table;

import junit.framework.TestCase;
import pcgen.base.format.NumberManager;
import pcgen.base.formatmanager.GenericFormatManager;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.base.util.SimpleValueStore;
import pcgen.testsupport.MockObjectDatabase;

/**
 * Test the ColumnFormatManager class
 */
public class ColumnFormatManagerTest extends TestCase
{
	private final NumberManager numberManager = new NumberManager();


	@SuppressWarnings({"rawtypes", "unchecked"})
	public void testUnconvertFailObject()
	{
		MockObjectDatabase mod = new MockObjectDatabase();
		FormatManager<TableColumn> baseManager =
				new GenericFormatManager<>(mod, TableColumn.class, "IGNORED");
		ColumnFormatManager<Number> manager =
				new ColumnFormatManager<>(baseManager, numberManager);
		try
		{
			//Yes generics are being violated in order to do this test
			FormatManager formatManager = manager;
			formatManager.unconvert(new Object());
			fail("Object should fail");
		}
		catch (ClassCastException e)
		{
			//expected
		}
	}

	@SuppressWarnings("unused")
	public void testConstructor()
	{
		MockObjectDatabase mod = new MockObjectDatabase();
		FormatManager<TableColumn> baseManager =
				new GenericFormatManager<>(mod, TableColumn.class, "IGNORED");
		try
		{
			ColumnFormatManager<Number> manager =
					new ColumnFormatManager<>(baseManager, null);
			fail("Should not be able to use null format");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			ColumnFormatManager<Number> manager =
					new ColumnFormatManager<>(null, numberManager);
			fail("Should not be able to use null format");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
	}

	public void testRoundRobinIdentifier()
	{
		MockObjectDatabase mod = new MockObjectDatabase();
		FormatManager<TableColumn> baseManager =
				new GenericFormatManager<>(mod, TableColumn.class, "IGNORED");
		ColumnFormatManager<Number> manager =
				new ColumnFormatManager<>(baseManager, numberManager);
		assertEquals("COLUMN[NUMBER]", manager.getIdentifierType());
	}

	public void testInvalidConvertSimpleFail()
	{
		MockObjectDatabase mod = new MockObjectDatabase();
		FormatManager<TableColumn> baseManager =
				new GenericFormatManager<>(mod, TableColumn.class, "IGNORED");
		ColumnFormatManager<Number> manager =
				new ColumnFormatManager<>(baseManager, numberManager);
		try
		{
			manager.convert(null);
			fail("Should not be able to convert null instructions");
		}
		catch (NullPointerException e)
		{
			//ok
		}
		catch (IllegalArgumentException e)
		{
			//ok too
		}
		try
		{
			manager.convert("");
			fail("Should not be able to convert null instructions");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			manager.convert("|");
			fail("Should not be able to convert null instructions");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}
	
	public void testConvert()
	{
		MockObjectDatabase mod = new MockObjectDatabase();
		FormatManager<TableColumn> baseManager =
				new GenericFormatManager<>(mod, TableColumn.class, "IGNORED");
		ColumnFormatManager<Number> manager =
				new ColumnFormatManager<>(baseManager, numberManager);
		TableColumn column = new TableColumn();
		column.setName("Age");
		column.setFormatManager(numberManager);
		mod.map.put(TableColumn.class, column.getName(), column);
		TableColumn c = manager.convert("Age");
		assertEquals("Age", c.getName());
		assertEquals(numberManager, c.getFormatManager());
		assertEquals("Age", manager.unconvert(c));
	}

	public void testConvertIndirect()
	{
		MockObjectDatabase mod = new MockObjectDatabase();
		FormatManager<TableColumn> baseManager =
				new GenericFormatManager<>(mod, TableColumn.class, "IGNORED");
		ColumnFormatManager<Number> manager =
				new ColumnFormatManager<>(baseManager, numberManager);
		try
		{
			manager.convertIndirect(null);
			fail("Should not be able to convert null instructions");
		}
		catch (NullPointerException e)
		{
			//ok
		}
		catch (IllegalArgumentException e)
		{
			//ok too
		}
		try
		{
			manager.convertIndirect("");
			fail("Should not be able to convert null instructions");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		TableColumn column = new TableColumn();
		column.setName("Age");
		column.setFormatManager(numberManager);
		mod.map.put(TableColumn.class, column.getName(), column);
		Indirect<TableColumn> idc = manager.convertIndirect("Age");
		TableColumn c = idc.get();
		assertEquals("Age", c.getName());
		assertEquals(numberManager, c.getFormatManager());
		assertEquals("Age", manager.unconvert(c));
	}

	public void testInitializeFrom()
	{
		MockObjectDatabase mod = new MockObjectDatabase();
		FormatManager<TableColumn> baseManager =
				new GenericFormatManager<>(mod, TableColumn.class, "IGNORED");
		ColumnFormatManager<Number> manager =
				new ColumnFormatManager<>(baseManager, numberManager);
		//No need to make much assertion here
		assertNotNull(manager.initializeFrom(new SimpleValueStore()));
	}
}
