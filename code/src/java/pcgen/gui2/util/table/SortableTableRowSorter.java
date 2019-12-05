/*
 * Copyright 2016 Connor Petty <cpmeister@users.sourceforge.net>
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
 */
package pcgen.gui2.util.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.RowSorter;
import javax.swing.SortOrder;

import pcgen.util.Comparators;

public class SortableTableRowSorter extends RowSorter<SortableTableModel>
{

    private List<? extends RowSorter.SortKey> sortKeys = Collections.emptyList();

    private SortableTableModel model;

    protected SortableTableRowSorter()
    {
        this(null);
    }

    public SortableTableRowSorter(SortableTableModel model)
    {
        setModel(model);
    }

    @Override
    public SortableTableModel getModel()
    {
        return model;
    }

    public void setModel(SortableTableModel model)
    {
        this.model = model;
    }

    /**
     * Reverses the sort order from ascending to descending (or descending
     * to ascending) if the specified column is already the primary sorted
     * column; otherwise, makes the specified column the primary sorted
     * column, with an ascending sort order. If the specified column is not
     * sortable, this method has no effect.
     *
     * @param column index of the column to make the primary sorted column,
     *               in terms of the underlying model
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public void toggleSortOrder(int column)
    {
        List<RowSorter.SortKey> keys = new ArrayList<>(getSortKeys());
        RowSorter.SortKey sortKey;
        int sortIndex;
        for (sortIndex = keys.size() - 1;sortIndex >= 0;sortIndex--)
        {
            if (keys.get(sortIndex).getColumn() == column)
            {
                break;
            }
        }
        if (sortIndex == -1)
        {
            // Key doesn't exist
            sortKey = new RowSorter.SortKey(column, SortOrder.ASCENDING);
            keys.add(0, sortKey);
        } else if (sortIndex == 0)
        {
            // It's the primary sorting key, toggle it
            keys.set(0, toggle(keys.get(0)));
        } else
        {
            // It's not the first, but was sorted on, remove old
            // entry, insert as first with ascending.
            keys.remove(sortIndex);
            keys.add(0, new RowSorter.SortKey(column, SortOrder.ASCENDING));
        }
        if (keys.size() > 2)
        {
            keys = keys.subList(0, 2);
        }
        setSortKeys(keys);
    }

    private RowSorter.SortKey toggle(RowSorter.SortKey key)
    {
        if (key.getSortOrder() == SortOrder.ASCENDING)
        {
            return new RowSorter.SortKey(key.getColumn(), SortOrder.DESCENDING);
        }
        return new RowSorter.SortKey(key.getColumn(), SortOrder.ASCENDING);
    }

    @Override
    public int convertRowIndexToModel(int index)
    {
        return index;
    }

    @Override
    public int convertRowIndexToView(int index)
    {
        return index;
    }

    @Override
    public void setSortKeys(List<? extends RowSorter.SortKey> keys)
    {
        sortKeys = keys;
        sort();
    }

    private void sort()
    {
        SortableTableModel m = getModel();
        if (m == null)
        {
            return;
        }
        int columnCount = m.getColumnCount();
        Comparator<?>[] comparators = new Comparator[columnCount];
        for (int i = 0;i < columnCount;i++)
        {
            comparators[i] = Comparators.getComparatorFor(m.getColumnClass(i));
        }
        RowSorter.SortKey[] keys = sortKeys.toArray(new RowSorter.SortKey[0]);

        m.sortModel(new RowComparator(keys, comparators));
    }

    @Override
    public List<? extends RowSorter.SortKey> getSortKeys()
    {
        return sortKeys;
    }

    @Override
    public int getViewRowCount()
    {
        if (getModel() == null)
        {
            return 0;
        }
        return getModel().getRowCount();
    }

    @Override
    public int getModelRowCount()
    {
        if (getModel() == null)
        {
            return 0;
        }
        return getModel().getRowCount();
    }

    @Override
    public void modelStructureChanged()
    {
    }

    @Override
    public void allRowsChanged()
    {
    }

    @Override
    public void rowsInserted(int firstRow, int endRow)
    {
    }

    @Override
    public void rowsDeleted(int firstRow, int endRow)
    {
    }

    @Override
    public void rowsUpdated(int firstRow, int endRow)
    {
    }

    @Override
    public void rowsUpdated(int firstRow, int endRow, int column)
    {
    }

}

class RowComparator implements Comparator<Row>
{

    private final RowSorter.SortKey[] keys;
    private final Comparator<?>[] comparators;

    RowComparator(RowSorter.SortKey[] keys, Comparator<?>[] comparators)
    {
        this.keys = keys;
        this.comparators = comparators;
    }

    @Override
    public int compare(Row o1, Row o2)
    {
        for (RowSorter.SortKey key : keys)
        {
            if (key.getSortOrder() == SortOrder.UNSORTED)
            {
                continue;
            }
            int column = key.getColumn();
            Comparator comparator = comparators[column];
            Object obj1 = o1.getValueAt(column);
            Object obj2 = o2.getValueAt(column);
            int ret;
            if (obj1 == null)
            {
                if (obj2 == null)
                {
                    ret = 0;
                } else
                {
                    ret = -1;
                }
            } else if (obj2 == null)
            {
                ret = 1;
            } else
            {
                ret = comparator.compare(obj1, obj2);
            }
            if (key.getSortOrder() == SortOrder.DESCENDING)
            {
                ret *= -1;
            }
            if (ret != 0)
            {
                return ret;
            }
        }

        return 0;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 19 * hash + Arrays.deepHashCode(this.keys);
        hash = 19 * hash + Arrays.deepHashCode(this.comparators);
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final RowComparator other = (RowComparator) obj;
        if (!Arrays.deepEquals(this.keys, other.keys))
        {
            return false;
        }
        return Arrays.deepEquals(this.comparators, other.comparators);
    }

}
