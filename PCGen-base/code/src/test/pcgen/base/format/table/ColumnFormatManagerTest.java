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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formatmanager.GenericFormatManager;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.base.util.SimpleValueStore;
import pcgen.testsupport.MockObjectDatabase;

/**
 * Test the ColumnFormatManager class
 */
public class ColumnFormatManagerTest
{
	@Test
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void testUnconvertFailObject()
	{
		MockObjectDatabase mod = new MockObjectDatabase();
		FormatManager<TableColumn> baseManager =
				new GenericFormatManager<>(mod, TableColumn.class, "IGNORED");
		//Yes generics are being violated in order to do this test
		FormatManager formatManager = new ColumnFormatManager<>(baseManager, FormatUtilities.NUMBER_MANAGER);
		assertThrows(ClassCastException.class, () -> formatManager.unconvert(new Object()));
	}

	@Test
	public void testConstructor()
	{
		MockObjectDatabase mod = new MockObjectDatabase();
		FormatManager<TableColumn> baseManager =
				new GenericFormatManager<>(mod, TableColumn.class, "IGNORED");
		assertThrows(NullPointerException.class, () -> new ColumnFormatManager<>(baseManager, null));
		assertThrows(NullPointerException.class, () -> new ColumnFormatManager<>(null, FormatUtilities.NUMBER_MANAGER));
	}

	@Test
	public void testRoundRobinIdentifier()
	{
		MockObjectDatabase mod = new MockObjectDatabase();
		FormatManager<TableColumn> baseManager =
				new GenericFormatManager<>(mod, TableColumn.class, "IGNORED");
		ColumnFormatManager<Number> manager =
				new ColumnFormatManager<>(baseManager, FormatUtilities.NUMBER_MANAGER);
		assertEquals("COLUMN[NUMBER]", manager.getIdentifierType());
	}

	@Test
	public void testInvalidConvertSimpleFail()
	{
		MockObjectDatabase mod = new MockObjectDatabase();
		FormatManager<TableColumn> baseManager =
				new GenericFormatManager<>(mod, TableColumn.class, "IGNORED");
		ColumnFormatManager<Number> manager =
				new ColumnFormatManager<>(baseManager, FormatUtilities.NUMBER_MANAGER);
		assertThrows(NullPointerException.class, () -> manager.convert(null));
		assertThrows(IllegalArgumentException.class, () -> manager.convert(""));
		assertThrows(IllegalArgumentException.class, () -> manager.convert("|"));
	}
	
	@Test
	public void testConvert()
	{
		MockObjectDatabase mod = new MockObjectDatabase();
		FormatManager<TableColumn> baseManager =
				new GenericFormatManager<>(mod, TableColumn.class, "IGNORED");
		ColumnFormatManager<Number> manager =
				new ColumnFormatManager<>(baseManager, FormatUtilities.NUMBER_MANAGER);
		TableColumn column = new TableColumn();
		column.setName("Age");
		column.setFormatManager(FormatUtilities.NUMBER_MANAGER);
		mod.map.put(TableColumn.class, column.getName(), column);
		TableColumn c = manager.convert("Age");
		assertEquals("Age", c.getName());
		assertEquals(FormatUtilities.NUMBER_MANAGER, c.getFormatManager());
		assertEquals("Age", manager.unconvert(c));
	}

	@Test
	public void testConvertIndirect()
	{
		MockObjectDatabase mod = new MockObjectDatabase();
		FormatManager<TableColumn> baseManager =
				new GenericFormatManager<>(mod, TableColumn.class, "IGNORED");
		ColumnFormatManager<Number> manager =
				new ColumnFormatManager<>(baseManager, FormatUtilities.NUMBER_MANAGER);
		assertThrows(NullPointerException.class, () -> manager.convertIndirect(null));
		assertThrows(IllegalArgumentException.class, () -> manager.convertIndirect(""));
		TableColumn column = new TableColumn();
		column.setName("Age");
		column.setFormatManager(FormatUtilities.NUMBER_MANAGER);
		mod.map.put(TableColumn.class, column.getName(), column);
		Indirect<TableColumn> idc = manager.convertIndirect("Age");
		TableColumn c = idc.get();
		assertEquals("Age", c.getName());
		assertEquals(FormatUtilities.NUMBER_MANAGER, c.getFormatManager());
		assertEquals("Age", manager.unconvert(c));
	}

	@Test
	public void testInitializeFrom()
	{
		MockObjectDatabase mod = new MockObjectDatabase();
		FormatManager<TableColumn> baseManager =
				new GenericFormatManager<>(mod, TableColumn.class, "IGNORED");
		ColumnFormatManager<Number> manager =
				new ColumnFormatManager<>(baseManager, FormatUtilities.NUMBER_MANAGER);
		//No need to make much assertion here
		assertNotNull(manager.initializeFrom(new SimpleValueStore()));
	}
}
