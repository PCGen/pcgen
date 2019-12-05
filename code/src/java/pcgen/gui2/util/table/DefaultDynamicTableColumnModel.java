/*
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
 */
package pcgen.gui2.util.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import pcgen.gui2.util.event.DynamicTableColumnModelListener;

/**
 *
 */
public class DefaultDynamicTableColumnModel extends DefaultTableColumnModel implements DynamicTableColumnModel
{

    private final List<TableColumn> availableColumns = new ArrayList<>(5);
    private final List<TableColumn> safeColumns = Collections.unmodifiableList(availableColumns);
    private int offset;

    /**
     * This constructs an empty table model with an intial offset of {@code offset}.
     * When adding columns to the model, the first {@code offset} number of colums added will
     * be made always visible.
     *
     * @param offset this is the number of always visible columns when this model is populated.
     */
    public DefaultDynamicTableColumnModel(int offset)
    {
        this.offset = offset;
    }

    /**
     * @param model  the columns model to copy data from
     * @param offset describes the number of always visible columns
     */
    public DefaultDynamicTableColumnModel(TableColumnModel model, int offset)
    {
        this(offset);
        ArrayList<TableColumn> allColumns = Collections.list(model.getColumns());
        if (offset < allColumns.size())
        {
            tableColumns.addAll(allColumns.subList(0, offset));
            availableColumns.addAll(allColumns.subList(offset, allColumns.size()));
        }
    }

    @Override
    public void addDynamicTableColumnModelListener(DynamicTableColumnModelListener listener)
    {
        listenerList.add(DynamicTableColumnModelListener.class, listener);
    }

    @Override
    public void removeDynamicTableColumnModelListener(DynamicTableColumnModelListener listener)
    {
        listenerList.remove(DynamicTableColumnModelListener.class, listener);
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     *
     * @param e the event received
     */
    protected void fireAvailableColumnAdded(TableColumnModelEvent e)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2;i >= 0;i -= 2)
        {
            if (listeners[i] == DynamicTableColumnModelListener.class)
            {
                ((DynamicTableColumnModelListener) listeners[i + 1]).availableColumnAdded(e);
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     *
     * @param e the event received
     */
    protected void fireAvailableColumnRemoved(TableColumnModelEvent e)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2;i >= 0;i -= 2)
        {
            if (listeners[i] == DynamicTableColumnModelListener.class)
            {
                ((DynamicTableColumnModelListener) listeners[i + 1]).availableColumnRemove(e);
            }
        }
    }

    @Override
    public void addColumn(TableColumn column)
    {
        if (getColumnCount() < offset)
        {
            super.addColumn(column);
        } else
        {
            int index = availableColumns.size();
            availableColumns.add(column);
            fireAvailableColumnAdded(new TableColumnModelEvent(this, -1, index));
        }
    }

    @Override
    public void removeColumn(TableColumn column)
    {
        super.removeColumn(column);
        if (availableColumns.contains(column))
        {
            int index = availableColumns.indexOf(column);
            availableColumns.remove(column);
            fireAvailableColumnRemoved(new TableColumnModelEvent(this, index, -1));
        }
    }

    @Override
    public List<TableColumn> getAvailableColumns()
    {
        return safeColumns;
    }

    @Override
    public boolean isVisible(TableColumn column)
    {
        return tableColumns.contains(column);
    }

    @Override
    public void setVisible(TableColumn column, boolean visible)
    {
        if (availableColumns.contains(column) && isVisible(column) != visible)
        {
            if (visible)
            {
                super.addColumn(column);
            } else
            {
                super.removeColumn(column);
            }
        }
    }

}
