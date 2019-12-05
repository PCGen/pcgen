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

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;

import pcgen.base.util.ComparableManager;
import pcgen.base.util.FormatManager;
import pcgen.cdom.base.Loadable;

/**
 * A DataTable represents tabular data (stored in columns and rows). Each column
 * has a name and a specific Format for the data.
 * <p>
 * This class makes best-effort based on an assumed good behavior of other
 * classes. This assumes that all columns are added before any rows. Also, if
 * the format of a column is altered after rows are added, then the behavior of
 * DataTable is not defined.
 */
public class DataTable implements Loadable
{
    /**
     * The source URI for this DataTable
     */
    private URI sourceURI;

    /**
     * The name of this DataTable.
     */
    private String name;

    /**
     * The columns definitions for this DataTable.
     */
    private ArrayList<TableColumn> columns = new ArrayList<>();

    /**
     * The contents of the DataTable.
     */
    private Map<Object, Object[]> dataByRow;

    /**
     * Appends a new TableColumn to the DataTable.
     * <p>
     * This may only be done BEFORE addRow is called. Once addRow is called,
     * this method will return an IllegalStateException.
     * <p>
     * The provided TableColumn is value-semantic. It is captured and stored by
     * this DataTable and changes (such as changing the TableColumn name) will
     * alter the contents of this DataTable.
     *
     * @param column The new TableColumn to be added to the DataTable
     */
    public void addColumn(TableColumn column)
    {
        if ((dataByRow != null) && !dataByRow.isEmpty())
        {
            throw new IllegalStateException("Column may not be added after rows are added");
        }
        if (columns.contains(Objects.requireNonNull(column)))
        {
            throw new IllegalArgumentException("Column may not be duplicate: " + column);
        }
        if (columns.isEmpty())
        {
            //First column
            FormatManager<?> formatManager = column.getFormatManager();
            if (formatManager instanceof ComparableManager)
            {
                dataByRow = new TreeMap<>(((ComparableManager) formatManager).getComparator());
            } else
            {
                dataByRow = new HashMap<>();
            }
        }
        columns.add(column);
    }

    /**
     * Returns the TableColumn at the given index in this DataTable.
     *
     * @param i The index for which the TableColumn in this DataTable should be returned
     * @return The TableColumn at the given index in this DataTable
     */
    public TableColumn getColumn(int i)
    {
        return columns.get(i);
    }

    /**
     * Appends a new row of data to this DataTable.
     * <p>
     * Note that the provided row must conform to the format of data as defined
     * by the columns in this DataTable.
     * <p>
     * The provided List is reference-semantic. The contents of the List are
     * stored, but the list itself is not stored. Thus any modification of the
     * list after this method completes will not alter the contents of the
     * DataTable.
     *
     * @param rowData The List of objects in a row to be added to this DataTable.
     */
    public void addRow(List<Object> rowData)
    {
        Object[] data = rowData.toArray();
        for (int i = 0;i < columns.size();i++)
        {
            TableColumn column = columns.get(i);
            Object object = data[i];
            if (!column.getFormatManager().getManagedClass().isAssignableFrom(object.getClass()))
            {
                throw new IllegalArgumentException("Item " + i + " in provided row was incorrect format, found: "
                        + object.getClass() + " but requried " + column.getFormatManager().getManagedClass());
            }
        }
        //Cast should be enforced by behavior of addColumn
        dataByRow.put(data[0], data);
    }

    /**
     * Sets the name for this DataTable.
     *
     * @param name The name to be given to this DataTable
     */
    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the name of this DataTable.
     *
     * @return The name of this DataTable
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the count of the number of rows in this DataTable.
     *
     * @return The count of the number of rows in this DataTable
     */
    public int getRowCount()
    {
        return dataByRow.size();
    }

    /**
     * Returns the count of the number of columns in this DataTable.
     *
     * @return The count of the number of columns in this DataTable
     */
    public int getColumnCount()
    {
        return columns.size();
    }

    /**
     * Returns true if the given String is the name of a column in this
     * DataTable.
     *
     * @param columnName The name to be checked to see if it is a column in this
     *                   DataTable.
     * @return true if the given String is the name of a column in this
     * DataTable
     */
    public boolean isColumn(String columnName)
    {
        return getColumnIndex(columnName) != -1;
    }

    /**
     * Returns true if the given object is appropriate as a lookup value (value in the
     * first column) to get a row in this DataTable using the given LookupType.
     *
     * @param lookupType  The LookupType used to determine how rows are analyzed to determine if
     *                    one will be matched for the given lookup value
     * @param lookupValue The value to be checked to see if it is a lookup value in a row of this
     *                    DataTable.
     * @return true if the given object is is appropriate as a lookup value (value in the
     * first column) to get a row in this DataTable using the given LookupType
     */
    public boolean hasRow(LookupType lookupType, Object lookupValue)
    {
        //Cast should be enforced by behavior of addColumn
        return getRow(lookupType, lookupValue) != null;
    }

    /**
     * Returns the FormatManager for the given column (zero indexed).
     *
     * @param i The column number for which the FormatManager should be
     *          returned
     * @return The format for the given column (zero indexed)
     */
    public FormatManager<?> getFormat(int i)
    {
        return columns.get(i).getFormatManager();
    }

    /**
     * Returns the FormatManager for the column with the given name.
     *
     * @param columnName The name of the column for which the FormatManager should be
     *                   returned
     * @return The FormatManager for the column with the given name
     */
    public FormatManager<?> getFormat(String columnName)
    {
        return columns.stream().filter(column -> column.getName().equals(columnName)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Column Name must exist in the DataTable"))
                .getFormatManager();
    }

    /**
     * Returns the value in this DataTable for the row with the lookup value valid (via
     * the given LookupType) to the given key and from the column with the given name.
     * <p>
     * Note that the returned value is value-semantic. Therefore any changes made to that
     * object will change the object stored in this DataTable.
     *
     * @param lookupType      The LookupType used to determine which row is to be chosen
     * @param lookupValue     The value used to determine (via the given lookupType) the row from
     *                        which the value will be retrieved
     * @param resultingColumn The name of the column from which the value should be retrieved
     * @return The value in this DataTable for the row with the lookup value appropriate
     * for the given LookupType, from the column with the given name
     */
    public Object lookup(LookupType lookupType, Object lookupValue, String resultingColumn)
    {
        int resultingColumnNumber = getColumnIndex(resultingColumn);
        if (resultingColumnNumber == -1)
        {
            throw new IllegalArgumentException("Cannot find column named: " + resultingColumn);
        }
        return lookup(lookupType, lookupValue, resultingColumnNumber);
    }

    private int getColumnIndex(String string)
    {
        int columnNumber = 0;
        for (TableColumn column : columns)
        {
            if (column.getName().equals(string))
            {
                return columnNumber;
            }
            columnNumber++;
        }
        return -1;
    }

    /**
     * Returns the value in this DataTable for the row with the lookup value valid (via
     * the given LookupType) to the given key and from the column with the given index.
     * <p>
     * Note that the returned value is value-semantic. Therefore any changes made to that
     * object will change the object stored in this DataTable.
     *
     * @param lookupType   The LookupType used to determine which row is to be chosen
     * @param lookupValue  The value used to determine (via the given lookupType) the row from
     *                     which the value will be retrieved
     * @param columnNumber The index of the column from which the value should be retrieved
     * @return The value in this DataTable for the row with the lookup value appropriate
     * for the given LookupType, from the column with the given index
     */
    public Object lookup(LookupType lookupType, Object lookupValue, int columnNumber)
    {
        //Cast should be enforced by behavior of addColumn
        Object[] row = getRow(lookupType, lookupValue);
        return row[columnNumber];
    }

    private Object[] getRow(LookupType lookupType, Object lookupValue)
    {
        Function<? super Map<Object, Object[]>, Object[]> p = lookupType.getRowFor(lookupValue);
        return p.apply(dataByRow);
    }

    /**
     * Trims this DataTable to reduce memory usage. Similar to trim() in
     * java.util.ArrayList.
     */
    public void trim()
    {
        columns.trimToSize();
    }

    @Override
    public String getKeyName()
    {
        return name;
    }

    @Override
    public String getDisplayName()
    {
        return name;
    }

    @Override
    public URI getSourceURI()
    {
        return sourceURI;
    }

    @Override
    public void setSourceURI(URI source)
    {
        sourceURI = source;
    }

    @Override
    public boolean isInternal()
    {
        return false;
    }

    @Override
    public boolean isType(String type)
    {
        return false;
    }

    /**
     * An Enumeration of the legal lookup types used to determine which row of a table to use.
     */
    public enum LookupType
    {
        /**
         * Lookup for an exact match in a table.
         */
        EXACT
                {
                    @Override
                    public <V> Function<? super Map<Object, V>, V> getRowFor(Object lookupValue)
                    {
                        return map -> map.get(lookupValue);
                    }

                    @Override
                    public boolean requiresSorting()
                    {
                        return false;
                    }
                },

        /**
         * Lookup for an match in a table of the the last less than or equal to value. In
         * effect this would round down in situations where all of the lookup items are
         * integers.
         */
        LASTLTEQ
                {
                    @Override
                    public <V> Function<? super Map<Object, V>, V> getRowFor(Object lookupValue)
                    {
                        return map -> ((NavigableMap<Object, V>) map).floorEntry(lookupValue).getValue();
                    }

                    @Override
                    public boolean requiresSorting()
                    {
                        return true;
                    }
                };

        /**
         * Returns a Function that will get the target row for the given lookupValue,
         * based on the behavior of the LookupType.
         *
         * @param lookupValue The value used to determine which row is to be used
         * @return A Function that will get the target row for the given lookupValue
         */
        public abstract <V> Function<? super Map<Object, V>, V> getRowFor(Object lookupValue);

        /**
         * Returns true if the LookupType requires comparison of values for sorting (not
         * just equality). If the format for the lookup column is not Comparable or does
         * not have a ComparableManager, then a LookupType that requries sorting cannot be
         * used.
         * <p>
         * This allows us to prevent errors on formats which are not comparable for
         * purposes of sorting. Used e.g. to prevent the use of LASTLTEQ in lookup
         * function when the table is not navigable...
         *
         * @return true if the LookupType requires sorting of values; false otherwise
         */
        public abstract boolean requiresSorting();
    }

}
