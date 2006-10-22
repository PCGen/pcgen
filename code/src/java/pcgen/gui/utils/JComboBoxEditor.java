/*
 * JComboBoxEditor.java
 * Copyright 2003 (C) Jonas Karlsson
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
package pcgen.gui.utils;

import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import java.awt.Component;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

/**
 * Handles editing of a jcombobox in a table.
 *
 * @author  Jonas Karlsson
 * @version $Revision$
 */
public class JComboBoxEditor extends JComboBoxEx implements TableCellEditor
{
	private final transient List<CellEditorListener> d_listeners = new ArrayList<CellEditorListener>();
	private transient int d_originalValue = 0;

	/**
	 * Constructor
	 * @param objects
	 */
	public JComboBoxEditor(Object[] objects)
	{
		super(objects);
	}

	public boolean isCellEditable(EventObject eventObject)
	{
		return true;
	}

	public Object getCellEditorValue()
	{
		return Integer.valueOf(getSelectedIndex());
	}

	public Component getTableCellEditorComponent(JTable jTable, Object value, boolean isSelected, int row, int column)
	{
		int i = -1;

		if (value == null)
		{
			return null;
		}

		d_originalValue = getSelectedIndex();

		if (value instanceof String)
		{
			i = Integer.parseInt((String) value);
		}
		else if (value instanceof Integer)
		{
			i = ((Integer) value).intValue();
		}

		if ((i < 0) || (i >= this.getItemCount()))
		{
			i = 0;
		}

		setSelectedIndex(i);
		jTable.setRowSelectionInterval(row, row);
		jTable.setColumnSelectionInterval(column, column);

		return this;
	}

	public void addCellEditorListener(CellEditorListener cellEditorListener)
	{
		d_listeners.add(cellEditorListener);
	}

	public void cancelCellEditing()
	{
		fireEditingCanceled();
	}

	public void removeCellEditorListener(CellEditorListener cellEditorListener)
	{
		d_listeners.remove(cellEditorListener);
	}

	public boolean shouldSelectCell(EventObject eventObject)
	{
		return true;
	}

	public boolean stopCellEditing()
	{
		fireEditingStopped();

		return true;
	}

	private void fireEditingCanceled()
	{
		setSelectedIndex(d_originalValue);

		ChangeEvent ce = new ChangeEvent(this);

		for (int i = d_listeners.size() - 1; i >= 0; --i)
		{
			(d_listeners.get(i)).editingCanceled(ce);
		}
	}

	private void fireEditingStopped()
	{
		ChangeEvent ce = new ChangeEvent(this);

		for (int i = d_listeners.size() - 1; i >= 0; --i)
		{
			(d_listeners.get(i)).editingStopped(ce);
		}
	}
}
