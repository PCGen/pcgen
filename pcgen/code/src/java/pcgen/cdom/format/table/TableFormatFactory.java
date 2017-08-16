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

import java.util.regex.Pattern;

import pcgen.base.formatmanager.FormatManagerFactory;
import pcgen.base.formatmanager.FormatManagerLibrary;
import pcgen.base.util.FormatManager;
import pcgen.base.util.ObjectDatabase;

/**
 * An TableFormatFactory builds a FormatManager supporting a DataTable from the
 * name of the formats of the component of the TableFormat.
 */
public class TableFormatFactory implements FormatManagerFactory
{

	/**
	 * A pattern to ensure no subtables.
	 */
	private static final Pattern SUB_PATTERN =
			Pattern.compile(Pattern.quote("TABLE["), Pattern.CASE_INSENSITIVE);

	/**
	 * The ObjectDatabase used by ColumnFormatManager objects built by this
	 * TableFormatFactory.
	 */
	private final ObjectDatabase database;

	/**
	 * Constructs a new TableFormatFactory with the given ObjectDatabase to be
	 * used by TableFormatManager objects built by this TableFormatFactory.
	 * 
	 * @param objDatabase
	 *            The ObjectDatabase used by TableFormatManager objects built by
	 *            this TableFormatFactory
	 */
	public TableFormatFactory(ObjectDatabase objDatabase)
	{
		this.database = objDatabase;
	}

	@Override
	public FormatManager<?> build(String subFormatName,
		FormatManagerLibrary library)
	{
		if (subFormatName == null)
		{
			throw new IllegalArgumentException(
				"Table Format cannot be built from no instructions");
		}
		if (SUB_PATTERN.matcher(subFormatName).find())
		{
			/*
			 * This is currently prohibited because - among other things -
			 * TableFormatFactory has no way to convert a multi-dimensional
			 * Table.
			 */
			throw new IllegalArgumentException(
				"Multidimensional Table format not supported: " + subFormatName
					+ " may not contain brackets");
		}
		String[] parts = subFormatName.split(",");
		if (parts.length != 2)
		{
			throw new IllegalArgumentException(
				"Table format must have 2 sub parts (lookup and result), found: "
					+ subFormatName);
		}
		return new TableFormatManager(database,
			library.getFormatManager(parts[0]),
			library.getFormatManager(parts[1]));
	}

	@Override
	public String getBuilderBaseFormat()
	{
		return "TABLE";
	}

}
