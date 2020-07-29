/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.Objects;
import java.util.Optional;

import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.base.util.ReferenceConverter;
import pcgen.base.util.ValueStore;

/**
 * A TableFormatManager is a FormatManager that defines the format of a DataTable.
 * 
 * @param <T> The Format for the table lookup (first column in the table)
 */
public final class TableFormatManager<T> implements FormatManager<DataTable>
{

	/**
	 * The ReferenceConverter used to construct or look up DataTable objects.
	 */
	private final ReferenceConverter<DataTable> tableFormat;

	/**
	 * The Format of any DataTable referred to by this TableFormatManager.
	 */
	private final FormatManager<T> lookupFormat;

	/**
	 * Constructs a new TableFormatManager that will use the underlying ReferenceConverter
	 * to look up DataTable objects. The DataTable should have the lookup format matching
	 * the format of the given FormatManager.
	 * 
	 * @param tableFormat
	 *            The ReferenceConverter used to look up DataTable objects
	 * @param lookupFormat
	 *            The FormatManager for the format of the Lookup column of the DataTable
	 *            format represented by this TableFormatManager
	 */
	public TableFormatManager(ReferenceConverter<DataTable> tableFormat,
		FormatManager<T> lookupFormat)
	{
		this.tableFormat = Objects.requireNonNull(tableFormat);
		this.lookupFormat = Objects.requireNonNull(lookupFormat);
	}

	@Override
	public DataTable convert(String inputStr)
	{
		//TODO Does this need validation that the lookup/result columns are appropriate?
		return tableFormat.convert(inputStr);
	}

	@Override
	public Indirect<DataTable> convertIndirect(String inputStr)
	{
		/*
		 * TODO Need validation that the lookup column is appropriate? Yes, probably
		 * during the initialization of these references, it will need to be checked...
		 * but how? Does this need to be like Categorized references? ugh
		 */
		return tableFormat.convertIndirect(inputStr);
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
		return "TABLE[" + lookupFormat.getIdentifierType() + "]";
	}

	@Override
	public Optional<FormatManager<?>> getComponentManager()
	{
		return Optional.empty();
	}

	@Override
	public int hashCode()
	{
		return lookupFormat.hashCode() + 33;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof TableFormatManager)
		{
			TableFormatManager<?> other = (TableFormatManager<?>) o;
			return lookupFormat.equals(other.lookupFormat);
		}
		return false;
	}

	/**
	 * Returns the FormatManager for the format of the Lookup column of the DataTable
	 * format represented by this TableFormatManager.
	 * 
	 * @return The FormatManager for the format of the Lookup column of the DataTable
	 *         format represented by this TableFormatManager
	 */
	public FormatManager<T> getLookupFormat()
	{
		return lookupFormat;
	}

	@Override
	public boolean isDirect()
	{
		return false;
	}

	@Override
	public DataTable initializeFrom(ValueStore valueStore)
	{
		DataTable empty = new DataTable();
		empty.setName("<empty table>");
		return empty;
	}
}
