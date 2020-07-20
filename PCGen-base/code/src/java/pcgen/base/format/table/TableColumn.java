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

import pcgen.base.util.FormatManager;

/**
 * A TableColumn is effectively a name/format pair that indicates a column within a
 * DataTable.
 */
@SuppressWarnings("PMD.DataClass")
public class TableColumn
{
	/**
	 * The name of this TableColumn
	 */
	private String name;

	/**
	 * The FormatManager indicating the format of the data in the DataTable column
	 * represented by this TableColumn.
	 */
	private FormatManager<?> formatManager;

	/**
	 * Sets the FormatManager indicating the format of the data in the DataTable column
	 * represented by this TableColumn.
	 * 
	 * @param formatManager
	 *            The FormatManager indicating the format of the data in the DataTable
	 *            column represented by this TableColumn
	 */
	public void setFormatManager(FormatManager<?> formatManager)
	{
		this.formatManager = formatManager;
	}

	/**
	 * Gets the FormatManager indicating the format of the data in the DataTable column
	 * represented by this TableColumn.
	 * 
	 * @return The FormatManager indicating the format of the data in the DataTable column
	 *         represented by this TableColumn
	 */
	public FormatManager<?> getFormatManager()
	{
		return formatManager;
	}

	/**
	 * Sets the name for this TableColumn.
	 * 
	 * @param name
	 *            The name for this TableColumn
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Returns the name for this TableColumn.
	 * 
	 * @return The name for this TableColumn
	 */
	public String getName()
	{
		return name;
	}
}
