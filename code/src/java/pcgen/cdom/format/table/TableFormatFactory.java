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
 * An TableFormatFactory builds a FormatManager supporting a DataTable from the
 * name of the format of the lookup column of the TableFormat.
 */
public class TableFormatFactory implements FormatManagerFactory
{

    /**
     * A pattern to ensure no subtables.
     */
    private static final Pattern SUB_PATTERN = Pattern.compile(Pattern.quote("TABLE["), Pattern.CASE_INSENSITIVE);

    /**
     * The FormatManager used by ColumnFormatManager objects built by this
     * TableFormatFactory.
     */
    private final FormatManager<DataTable> tableFormat;

    /**
     * Constructs a new TableFormatFactory with the given FormatManager to be
     * used by TableFormatManager objects built by this TableFormatFactory.
     *
     * @param tableFormat The FormatManager used by TableFormatManager objects built by
     *                    this TableFormatFactory
     */
    public TableFormatFactory(FormatManager<DataTable> tableFormat)
    {
        this.tableFormat = tableFormat;
    }

    @Override
    public FormatManager<DataTable> build(String subFormatName, FormatManagerLibrary library)
    {
        Objects.requireNonNull(subFormatName, "Table Format cannot be built from no instructions");
        if (SUB_PATTERN.matcher(subFormatName).find())
        {
            /*
             * This is currently prohibited because - among other things -
             * TableFormatFactory has no way to convert a multi-dimensional
             * Table.
             */
            throw new IllegalArgumentException(
                    "Multidimensional Table format not supported: " + subFormatName + " may not contain brackets");
        }
        return new TableFormatManager(tableFormat, library.getFormatManager(subFormatName));
    }

    @Override
    public String getBuilderBaseFormat()
    {
        return "TABLE";
    }

}
