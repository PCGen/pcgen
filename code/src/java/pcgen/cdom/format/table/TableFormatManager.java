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
 * A TableFormatManager is a FormatManager that defines the format of a
 * DataTable.
 * 
 * Note that a DataTable can have more than one TableFormatManager represent
 * that DataTable. This is possible because the Result format is only indicative
 * that such a column exists in the DataTable, not that it has a specific name
 * or that it is the only column.
 */
public final class TableFormatManager implements FormatManager<DataTable>
{

	/**
	 * The ObjectDatabase used to construct or look up DataTable objects.
	 */
	private final ObjectDatabase database;

	/**
	 * The Format of any DataTable referred to by this TableFormatManager.
	 */
	private final FormatManager<?> lookupFormat;

	/**
	 * The Format of any DataTable referred to by this TableFormatManager.
	 */
	private final FormatManager<?> resultFormat;

	/**
	 * Constructs a new TableFormatManager that will use the underlying
	 * AbstractReferenceContext to construct and look up DataTable objects. The
	 * DataTable should have the lookup and result formats matching the formats
	 * of the given FormatManagers.
	 * 
	 * @param objDatabase
	 *            The ObjectDatabase used to construct or look up DataTable
	 *            objects
	 * @param lookupFormat
	 *            The FormatManager for the format of the Lookup column of the
	 *            DataTable format represented by this TableFormatManager
	 * @param resultFormat
	 *            The FormatManager for the format of the Result column of the
	 *            DataTable format represented by this TableFormatManager
	 */
	public TableFormatManager(ObjectDatabase objDatabase,
		FormatManager<?> lookupFormat, FormatManager<?> resultFormat)
	{
		this.database = Objects.requireNonNull(objDatabase);
		this.lookupFormat = Objects.requireNonNull(lookupFormat);
		this.resultFormat = Objects.requireNonNull(resultFormat);
	}

	@Override
	public DataTable convert(String inputStr)
	{
		//TODO Does this need validation that the lookup/result columns are appropriate?
		return database.get(DataTable.class, inputStr);
	}

	@Override
	public Indirect<DataTable> convertIndirect(String inputStr)
	{
		/*
		 * TODO Need validation that the lookup/result columns are appropriate?
		 * Yes, probably during the initialization of these references, it will
		 * need to be checked... but how? Does this need to be like Categorized
		 * references? ugh
		 */
		return database.getIndirect(DataTable.class, inputStr);
	}

	@Override
	public String unconvert(DataTable table)
	{
		return table.getName();
	}

	@Override
	public Class<DataTable> getManagedClass()
	{
		return DataTable.class;
	}

	@Override
	public String getIdentifierType()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("TABLE[");
		sb.append(lookupFormat.getIdentifierType());
		sb.append(",");
		sb.append(resultFormat.getIdentifierType());
		sb.append("]");
		return sb.toString();
	}

	@Override
	public FormatManager<?> getComponentManager()
	{
		return null;
	}

	@Override
	public int hashCode()
	{
		return lookupFormat.hashCode() ^ resultFormat.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof TableFormatManager)
		{
			TableFormatManager other = (TableFormatManager) o;
			return lookupFormat.equals(other.lookupFormat)
				&& resultFormat.equals(other.resultFormat);
		}
		return false;
	}

	/**
	 * Returns the FormatManager for the format of the Lookup column of the
	 * DataTable format represented by this TableFormatManager.
	 * 
	 * @return The FormatManager for the format of the Lookup column of the
	 *         DataTable format represented by this TableFormatManager
	 */
	public FormatManager<?> getLookupFormat()
	{
		return lookupFormat;
	}

	/**
	 * Returns the FormatManager for the format of the Result column of the
	 * DataTable format represented by this TableFormatManager.
	 * 
	 * @return The FormatManager for the format of the Result column of the
	 *         DataTable format represented by this TableFormatManager
	 */
	public FormatManager<?> getResultFormat()
	{
		return resultFormat;
	}
}
