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
import pcgen.base.util.ReferenceConverter;

/**
 * An ColumnFormatFactory builds a FormatManager supporting columns of a DataTable from
 * the name of the format of the ColumnFormat.
 */
public class ColumnFormatFactory implements FormatManagerFactory
{
	/**
	 * The ReferenceConverter used by ColumnFormatManager objects built by this
	 * ColumnFormatFactory.
	 */
	private final ReferenceConverter<TableColumn> converter;

	/**
	 * Constructs a new ColumnFormatFactory with the given Converter to be used by
	 * ColumnFormatManager objects built by this ColumnFormatFactory.
	 * 
	 * @param converter
	 *            The ReferenceConverter used to look up columns by ColumnFormatManager
	 *            objects built by this ColumnFormatFactory
	 */
	public ColumnFormatFactory(ReferenceConverter<TableColumn> converter)
	{
		this.converter = converter;
	}

	@Override
	public FormatManager<?> build(Optional<String> parentFormat,
		Optional<String> subFormatName, FormatManagerLibrary library)
	{
		if (subFormatName.isEmpty())
		{
			throw new IllegalArgumentException("Poorly formatted instructions "
				+ "(subformat not provided in ColumnFormatFactory)");
		}
		if (parentFormat.isPresent())
		{
			//ColumnFormatFactory is not sensible inside another format
			throw new IllegalArgumentException(
				"Column format not supported inside another format: "
					+ parentFormat.get() + " may not contain a column");
		}
		FormatManager<?> formatManager = library.getFormatManager(
			Optional.of(getBuilderBaseFormat()), subFormatName.get());
		return proc(formatManager);
	}

	private <T> ColumnFormatManager<T> proc(FormatManager<T> formatManager)
	{
		return new ColumnFormatManager<>(converter, formatManager);
	}

	@Override
	public String getBuilderBaseFormat()
	{
		return "COLUMN";
	}

}
