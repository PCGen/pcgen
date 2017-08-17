/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.format.table;

import java.util.Objects;

import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.base.util.ObjectDatabase;

/**
 * A ColumnFormatManager is a FormatManager that defines the format of a
 * TableColumn.
 * 
 * @param <T>
 *            The Format of the contents of a Column defined by this
 *            ColumnFormatManager in a DataTable
 */
public class ColumnFormatManager<T> implements FormatManager<TableColumn>
{

	/**
	 * The ObjectDatabase used to construct or look up TableColumn objects.
	 */
	private final ObjectDatabase database;

	/**
	 * The Format of any Column referred to by this ColumnFormatManager
	 */
	private final FormatManager<T> underlying;

	/**
	 * Constructs a new ColumnFormatManager that will use the underlying
	 * AbstractReferenceContext to construct and look up TableColumn objects of
	 * the format of the given FormatManager.
	 * 
	 * @param objDatabase
	 *            The ObjectDatabase used to construct or look up TableColumn
	 *            objects
	 * @param formatManager
	 *            The Format of TableColumns referred to by this
	 *            ColumnFormatManager
	 */
	public ColumnFormatManager(ObjectDatabase objDatabase,
		FormatManager<T> formatManager)
	{
		database = Objects.requireNonNull(objDatabase);
		underlying = Objects.requireNonNull(formatManager);
	}

	@Override
	public TableColumn convert(String inputStr)
	{
		return database.get(TableColumn.class, inputStr);
	}

	@Override
	public Indirect<TableColumn> convertIndirect(String inputStr)
	{
		return database.getIndirect(TableColumn.class, inputStr);
	}

	@Override
	public String unconvert(TableColumn table)
	{
		return table.getName();
	}

	@Override
	public Class<TableColumn> getManagedClass()
	{
		return TableColumn.class;
	}

	@Override
	public String getIdentifierType()
	{
		return "COLUMN";
	}

	@Override
	public FormatManager<?> getComponentManager()
	{
		return underlying;
	}

	@Override
	public boolean isDirect()
	{
		return false;
	}
}
