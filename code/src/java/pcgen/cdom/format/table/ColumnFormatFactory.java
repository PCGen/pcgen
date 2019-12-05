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
import java.util.regex.Pattern;

import pcgen.base.formatmanager.FormatManagerFactory;
import pcgen.base.formatmanager.FormatManagerLibrary;
import pcgen.base.util.FormatManager;

/**
 * An ColumnFormatFactory builds a FormatManager supporting columns of a
 * DataTable from the name of the format of the ColumnFormat
 */
public class ColumnFormatFactory implements FormatManagerFactory
{

    /**
     * A pattern to ensure no column subformat.
     */
    private static final Pattern SUB_PATTERN = Pattern.compile(Pattern.quote("COLUMN["), Pattern.CASE_INSENSITIVE);

    /**
     * The FormatManager used by ColumnFormatManager objects built by this
     * ColumnFormatFactory.
     */
    private final FormatManager<TableColumn> columnFormat;

    /**
     * Constructs a new ColumnFormatFactory with the given FormatManager to be
     * used by ColumnFormatManager objects built by this ColumnFormatFactory.
     *
     * @param columnFormat The FormatManager used by ColumnFormatManager objects built
     *                     by this ColumnFormatFactory
     */
    public ColumnFormatFactory(FormatManager<TableColumn> columnFormat)
    {
        this.columnFormat = columnFormat;
    }

    @Override
    public FormatManager<TableColumn> build(String subFormatName, FormatManagerLibrary library)
    {
        Objects.requireNonNull(subFormatName, "Column Format cannot be built from no instructions");
        if (SUB_PATTERN.matcher(subFormatName).find())
        {
            /*
             * This is currently prohibited because - among other things -
             * ColumnFormatFactory has no way to understand a subcolumn
             */
            throw new IllegalArgumentException(
                    "Column Subformat not supported: " + subFormatName + " may not contain COLUMN inside COLUMN");
        }
        FormatManager<?> formatManager = library.getFormatManager(subFormatName);
        return proc(formatManager);
    }

    private <T> ColumnFormatManager<T> proc(FormatManager<T> formatManager)
    {
        return new ColumnFormatManager<>(columnFormat, formatManager);
    }

    @Override
    public String getBuilderBaseFormat()
    {
        return "COLUMN";
    }

}
