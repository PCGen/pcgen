/*
 * TableMap.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on April 21, 2001, 2:15 PM
 */
package pcgen.gui.utils;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 * In a chain of data manipulators some behaviour is common. TableMap
 * provides most of this behavour and can be subclassed by filters
 * that only need to override a handful of specific methods. TableMap
 * implements TableModel by routing all requests to its model, and
 * TableModelListener by routing all events to its listeners. Inserting
 * a TableMap which has not been subclassed into a chain of table filters
 * should have no effect.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
class TableMap extends AbstractTableModel implements TableModelListener
{
	TableModel model;

    @Override
	public boolean isCellEditable(int row, int column)
	{
		return model.isCellEditable(row, column);
	}

    @Override
	public Class<?> getColumnClass(int aColumn)
	{
		return model.getColumnClass(aColumn);
	}

    @Override
	public int getColumnCount()
	{
		return (model == null) ? 0 : model.getColumnCount();
	}

    @Override
	public String getColumnName(int aColumn)
	{
		return model.getColumnName(aColumn);
	}

    @Override
	public int getRowCount()
	{
		return (model == null) ? 0 : model.getRowCount();
	}

    @Override
	public void setValueAt(Object aValue, int aRow, int aColumn)
	{
		model.setValueAt(aValue, aRow, aColumn);
	}

	// By default, implement TableModel by forwarding all messages
	// to the model.
    @Override
	public Object getValueAt(int aRow, int aColumn)
	{
		return model.getValueAt(aRow, aColumn);
	}

	//
	// Implementation of the TableModelListener interface,
	//
	// By default forward all events to all the listeners.
    @Override
	public void tableChanged(TableModelEvent e)
	{
		if (e != null)
		{
			fireTableChanged(e);
		}
	}

	void setModel(TableModel model)
	{
		this.model = model;
		model.addTableModelListener(this);
	}
}
