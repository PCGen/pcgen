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

import java.util.Optional;

import pcgen.base.formatmanager.FormatManagerFactory;
import pcgen.base.formatmanager.FormatManagerLibrary;
import pcgen.base.util.FormatManager;

/**
 * An TableFormatFactory builds a FormatManager supporting a DataTable from the name of
 * the format of the lookup column of the TableFormat.
 */
public class TableFormatFactory implements FormatManagerFactory
{
	/**
	 * The FormatManager used by ColumnFormatManager objects built by this
	 * TableFormatFactory.
	 */
	private final FormatManager<DataTable> tableFormat;

	/**
	 * Constructs a new TableFormatFactory with the given FormatManager to be used by
	 * TableFormatManager objects built by this TableFormatFactory.
	 * 
	 * @param tableFormat
	 *            The FormatManager used by TableFormatManager objects built by this
	 *            TableFormatFactory
	 */
	public TableFormatFactory(FormatManager<DataTable> tableFormat)
	{
		this.tableFormat = tableFormat;
	}

	@Override
	public FormatManager<?> build(Optional<String> parentFormat,
		Optional<String> subFormatName, FormatManagerLibrary library)
	{
		if (subFormatName.isEmpty())
		{
			throw new IllegalArgumentException("Poorly formatted instructions "
				+ "(subformat not provided in TableFormatFactory)");
		}
		if (parentFormat.isPresent())
		{
			//TableFormatFactory not sensible inside another format
			throw new IllegalArgumentException(
				"Table format not supported inside another format: "
					+ parentFormat.get() + " may not contain a table");
		}
		return new TableFormatManager<>(tableFormat, library.getFormatManager(
			Optional.of(getBuilderBaseFormat()), subFormatName.get()));
	}

	@Override
	public String getBuilderBaseFormat()
	{
		return "TABLE";
	}

}
