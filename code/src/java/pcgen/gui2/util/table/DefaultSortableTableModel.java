/*
 * DefaultSortableTableModel.java
 * Copyright 2008 (C) Connor Petty <mistercpp2000@gmail.com>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on Feb 22, 2008, 3:08:09 PM
 */
package pcgen.gui2.util.table;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Connor Petty &lt;mistercpp2000@gmail.com&gt;
 */
public class DefaultSortableTableModel extends DefaultTableModel implements SortableTableModel
{

    private Map<Integer, Comparator<?>> comparatorMap = null;

    /**
     *  Constructs a default <code>DefaultSortableTableModel</code> 
     *  which is a table of zero columns and zero rows.
     */
    public DefaultSortableTableModel()
    {
        super();
    }

    /**
     *  Constructs a <code>DefaultSortableTableModel</code> with
     *  <code>rowCount</code> and <code>columnCount</code> of
     *  <code>null</code> object values.
     *
     * @param rowCount           the number of rows the table holds
     * @param columnCount        the number of columns the table holds
     *
     * @see #setValueAt
     */
    public DefaultSortableTableModel(int rowCount, int columnCount)
    {
        super(rowCount, columnCount);
    }

    /**
     *  Constructs a <code>DefaultSortableTableModel</code> with as many columns
     *  as there are elements in <code>columnNames</code>
     *  and <code>rowCount</code> of <code>null</code>
     *  object values.  Each column's name will be taken from
     *  the <code>columnNames</code> vector.
     *
     * @param columnNames       <code>vector</code> containing the names
     *				of the new columns; if this is 
     *                          <code>null</code> then the model has no columns
     * @param rowCount           the number of rows the table holds
     * @see #setDataVector
     * @see #setValueAt
     */
    public DefaultSortableTableModel(Vector<?> columnNames, int rowCount)
    {
        super(columnNames, rowCount);
    }

    /**
     *  Constructs a <code>DefaultSortableTableModel</code> with as many
     *  columns as there are elements in <code>columnNames</code>
     *  and <code>rowCount</code> of <code>null</code>
     *  object values.  Each column's name will be taken from
     *  the <code>columnNames</code> array.
     *
     * @param columnNames       <code>array</code> containing the names
     *				of the new columns; if this is
     *                          <code>null</code> then the model has no columns
     * @param rowCount           the number of rows the table holds
     * @see #setDataVector
     * @see #setValueAt
     */
    public DefaultSortableTableModel(Object[] columnNames, int rowCount)
    {
        super(columnNames, rowCount);
    }

    /**
     *  Constructs a <code>DefaultSortableTableModel</code> and initializes the table
     *  by passing <code>data</code> and <code>columnNames</code>
     *  to the <code>setDataVector</code> method.
     *
     * @param data              the data of the table, a <code>Vector</code>
     *                          of <code>Vector</code>s of <code>Object</code>
     *                          values
     * @param columnNames       <code>vector</code> containing the names
     *				of the new columns
     * @see #getDataVector
     * @see #setDataVector
     */
    public DefaultSortableTableModel(Vector<?> data, Vector<?> columnNames)
    {
        super(data, columnNames);
    }

    /**
     *  Constructs a <code>DefaultSortableTableModel</code> and initializes the table
     *  by passing <code>data</code> and <code>columnNames</code>
     *  to the <code>setDataVector</code>
     *  method. The first index in the <code>Object[][]</code> array is
     *  the row index and the second is the column index.
     *
     * @param data              the data of the table
     * @param columnNames       the names of the columns
     * @see #getDataVector
     * @see #setDataVector
     */
    public DefaultSortableTableModel(Object[][] data, Object[] columnNames)
    {
        super(data, columnNames);
    }

    public DefaultSortableTableModel(TableModel model)
    {
        super(model.getRowCount(), model.getColumnCount());
        for (int x = 0; x < getRowCount(); x++)
        {
            for (int y = 0; y < getColumnCount(); y++)
            {
                setValueAt(model.getValueAt(x, y), x, y);
            }
        }
        Vector<String> titles = new Vector<>();
        for(int x = 0; x < getColumnCount(); x++)
        {
            titles.add(model.getColumnName(x));
        }
        setColumnIdentifiers(titles);
    }

	@Override
    @SuppressWarnings("unchecked")
    public void sortModel(Comparator<Row> comparator)
    {
        dataVector.sort(comparator);
        fireTableDataChanged();
    }

}
