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
 * An ColumnFormatFactory builds a FormatManager supporting columns of a
 * DataTable from the name of the format of the ColumnFormat
 */
public class ColumnFormatFactory implements FormatManagerFactory
{

	/**
	 * A pattern to ensure no column subformat.
	 */
	private static final Pattern SUB_PATTERN =
			Pattern.compile(Pattern.quote("COLUMN["), Pattern.CASE_INSENSITIVE);

	/**
	 * The ObjectDatabase used by ColumnFormatManager objects built by this
	 * ColumnFormatFactory.
	 */
	private final ObjectDatabase database;

	/**
	 * Constructs a new ColumnFormatFactory with the given ObjectDatabase to be
	 * used by ColumnFormatManager objects built by this ColumnFormatFactory.
	 * 
	 * @param objDatabase
	 *            The ObjectDatabase used by ColumnFormatManager objects built
	 *            by this ColumnFormatFactory
	 */
	public ColumnFormatFactory(ObjectDatabase objDatabase)
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
				"Column Format cannot be built from no instructions");
		}
		if (SUB_PATTERN.matcher(subFormatName).find())
		{
			/*
			 * This is currently prohibited because - among other things -
			 * ColumnFormatFactory has no way to understand a subcolumn
			 */
			throw new IllegalArgumentException(
				"Column Subformat not supported: " + subFormatName
					+ " may not contain COLUMN inside COLUMN");
		}
		return new ColumnFormatManager<>(database,
			library.getFormatManager(subFormatName));
	}

	@Override
	public String getBuilderBaseFormat()
	{
		return "COLUMN";
	}

}
