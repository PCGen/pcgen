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

import junit.framework.TestCase;
import pcgen.base.format.NumberManager;
import pcgen.base.formatmanager.GenericFormatManager;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.base.util.SimpleValueStore;
import pcgen.testsupport.MockObjectDatabase;

/**
 * Test the TableFormatManager class
 */
public class TableFormatManagerTest extends TestCase
{
	private final NumberManager numberManager = new NumberManager();

	@SuppressWarnings({"rawtypes", "unchecked"})
	public void testUnconvertFailObject()
	{
		MockObjectDatabase mod = new MockObjectDatabase();
		FormatManager<DataTable> baseManager =
				new GenericFormatManager<>(mod, DataTable.class, "IGNORED");
		TableFormatManager<Number> manager =
				new TableFormatManager<>(baseManager, numberManager);
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
		FormatManager<DataTable> baseManager =
				new GenericFormatManager<>(mod, DataTable.class, "IGNORED");
		try
		{
			TableFormatManager<Number> manager =
					new TableFormatManager<>(baseManager, null);
			fail("Should not be able to use null format");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			TableFormatManager<Number> manager =
					new TableFormatManager<>(null, numberManager);
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
		FormatManager<DataTable> baseManager =
				new GenericFormatManager<>(mod, DataTable.class, "IGNORED");
		TableFormatManager<Number> manager =
				new TableFormatManager<>(baseManager, numberManager);
		assertEquals("TABLE[NUMBER]", manager.getIdentifierType());
	}

	public void testInvalidConvertSimpleFail()
	{
		MockObjectDatabase mod = new MockObjectDatabase();
		FormatManager<DataTable> baseManager =
				new GenericFormatManager<>(mod, DataTable.class, "IGNORED");
		TableFormatManager<Number> manager =
				new TableFormatManager<>(baseManager, numberManager);
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
		FormatManager<DataTable> baseManager =
				new GenericFormatManager<>(mod, DataTable.class, "IGNORED");
		TableFormatManager<Number> manager =
				new TableFormatManager<>(baseManager, numberManager);
		TableColumn column = new TableColumn();
		column.setName("Age");
		column.setFormatManager(numberManager);
		DataTable table = new DataTable();
		table.setName("Penalties");
		table.addColumn(column);
		mod.map.put(DataTable.class, table.getName(), table);
		DataTable c = manager.convert("Penalties");
		assertEquals("Penalties", c.getName());
		assertEquals(column, c.getColumn(0));
		assertEquals("Penalties", manager.unconvert(c));
	}

	public void testConvertIndirect()
	{
		MockObjectDatabase mod = new MockObjectDatabase();
		FormatManager<DataTable> baseManager =
				new GenericFormatManager<>(mod, DataTable.class, "IGNORED");
		TableFormatManager<Number> manager =
				new TableFormatManager<>(baseManager, numberManager);
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
		DataTable table = new DataTable();
		table.setName("Age");
		table.addColumn(column);
		mod.map.put(DataTable.class, table.getName(), table);
		Indirect<DataTable> idc = manager.convertIndirect("Age");
		DataTable c = idc.get();
		assertEquals("Age", c.getName());
		assertEquals(column, c.getColumn(0));
		assertEquals("Age", manager.unconvert(c));
	}

	public void testInitializeFrom()
	{
		MockObjectDatabase mod = new MockObjectDatabase();
		FormatManager<DataTable> baseManager =
				new GenericFormatManager<>(mod, DataTable.class, "IGNORED");
		TableFormatManager<Number> manager =
				new TableFormatManager<>(baseManager, numberManager);
		//No need to make much assertion here
		assertNotNull(manager.initializeFrom(new SimpleValueStore()));
	}
}
