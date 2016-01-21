/*
 * JTableEx.java
 * Copyright 2001 (C) Jonas Karlsson <jujutsunerd@users.sourceforge.net>
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
 * Created on June 27, 2001, 20:36 PM
 */
package pcgen.gui2.util;

import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;

import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import pcgen.gui2.util.table.SortableTableModel;
import pcgen.gui2.util.table.SortableTableRowSorter;
import pcgen.gui2.util.table.TableCellUtilities;

/**
 *  <code>JTableEx</code> extends JTable to provide auto-tooltips.
 *
 * @author     Jonas Karlsson <jujutsunerd@users.sourceforge.net>
 * @version    $Revision: 1817 $
 */
public class JTableEx extends JTable
{

	private static final long serialVersionUID = 514835142307946415L;

	/** Constant for a double click action event. */
	public static final int ACTION_DOUBLECLICK = 2042;
	private boolean sortingEnabled;

	/**
	 * Constructor
	 */
	public JTableEx()
	{
		this(null, null, null);
	}

	/**
	 * Constructor
	 * @param tm
	 */
	public JTableEx(TableModel tm)
	{
		this(tm, null, null);
	}

	/**
	 * Constructor
	 * @param tm
	 * @param tcm
	 */
	public JTableEx(TableModel tm, TableColumnModel tcm)
	{
		this(tm, tcm, null);
	}

	public JTableEx(TableModel tm, TableColumnModel tcm,
			ListSelectionModel lsm)
	{
		super(tm, tcm, lsm);

		setDefaultRenderer(BigDecimal.class, new TableCellUtilities.AlignRenderer(SwingConstants.RIGHT));
		setDefaultRenderer(Float.class, new TableCellUtilities.AlignRenderer(SwingConstants.RIGHT));
		setDefaultRenderer(Integer.class, new TableCellUtilities.AlignRenderer(SwingConstants.RIGHT));
		installDoubleClickListener();
	}

	private void installDoubleClickListener()
	{
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getComponent().isEnabled() && e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
				{
					Point p = e.getPoint();
					int row = convertRowIndexToModel(rowAtPoint(p));
					int column = convertColumnIndexToModel(columnAtPoint(p));
					Object value = getModel().getValueAt(row, column);
					fireActionEvent(JTableEx.this, ACTION_DOUBLECLICK, String.valueOf(value));
				}
			}
		});
	}

	private void fireActionEvent(Object value, int id, String command)
	{
		ActionEvent e = null;
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == ActionListener.class)
			{
				// Lazily create the event:
				if (e == null)
				{
					e = new ActionEvent(value, id, command);
				}

				((ActionListener) listeners[i + 1]).actionPerformed(e);
			}
		}
	}

	public void addActionListener(ActionListener listener)
	{
		listenerList.add(ActionListener.class, listener);
	}

	public void removeActionListener(ActionListener listener)
	{
		listenerList.remove(ActionListener.class, listener);
	}

	@Override
	public boolean getScrollableTracksViewportHeight()
	{
		// fetch the table's parent
		Container viewport = getParent();

		// if the parent is not a viewport, calling this isn't useful
		if (!(viewport instanceof JViewport))
		{
			return false;
		}

		// return true if the table's preferred height is smaller
		// than the viewport height, else false
		return getPreferredSize().height < viewport.getHeight();
	}

	@Override
	public boolean getAutoCreateRowSorter()
	{
		return sortingEnabled;
	}

	@Override
	public void setAutoCreateRowSorter(boolean autoCreateRowSorter)
	{
		boolean oldValue = this.sortingEnabled;
		this.sortingEnabled = autoCreateRowSorter;
		if (sortingEnabled)
		{
			TableModel model = getModel();
			if (model instanceof SortableTableModel)
			{
				setRowSorter(new SortableTableRowSorter((SortableTableModel) dataModel));
			}
			else
			{
				setRowSorter(new TableRowSorter(model));
			}
		}
		firePropertyChange("autoCreateRowSorter", oldValue,
				autoCreateRowSorter);
	}

	public void sortModel()
	{
		RowSorter rowSorter = getRowSorter();
		if (rowSorter != null)
		{
			rowSorter.setSortKeys(getRowSorter().getSortKeys());
		}
	}

	@Override
	public void setModel(TableModel dataModel)
	{
		if (dataModel == null)
		{
			throw new IllegalArgumentException("Cannot set a null TableModel");
		}
		if (this.dataModel != dataModel)
		{
			TableModel old = this.dataModel;
			if (old != null)
			{
				old.removeTableModelListener(this);
			}
			this.dataModel = dataModel;
			dataModel.addTableModelListener(this);

			tableChanged(new TableModelEvent(dataModel, TableModelEvent.HEADER_ROW));

			firePropertyChange("model", old, dataModel);

			if (getAutoCreateRowSorter())
			{
				if (dataModel instanceof SortableTableModel)
				{
					super.setRowSorter(new SortableTableRowSorter((SortableTableModel) dataModel));
				}
				else
				{
					super.setRowSorter(new TableRowSorter(dataModel));
				}
			}
		}
	}

	/**
	 * set horizontal alignment of column
	 * and attach a new cell renderer
	 * @param col
	 * @param alignment
	 **/
	public void setColAlign(int col, int alignment)
	{
		getColumnModel().getColumn(col).setCellRenderer(
				new TableCellUtilities.AlignRenderer(alignment));
	}

//	public class PassthroughRowSorter extends RowSorter<SortableTableModel> implements Comparator<Row>{
//
//		private List<? extends SortKey> sortKeys = Collections.emptyList();
//		
//		@Override
//		public SortableTableModel getModel()
//		{
//			return (SortableTableModel)dataModel;
//		}
//
//		/**
//		 * Reverses the sort order from ascending to descending (or descending
//		 * to ascending) if the specified column is already the primary sorted
//		 * column; otherwise, makes the specified column the primary sorted
//		 * column, with an ascending sort order. If the specified column is not
//		 * sortable, this method has no effect.
//		 *
//		 * @param column index of the column to make the primary sorted column,
//		 * in terms of the underlying model
//		 * @throws IndexOutOfBoundsException {@inheritDoc}
//		 * @see #setSortable(int,boolean)
//		 * @see #setMaxSortKeys(int)
//		 */
//		@Override
//		public void toggleSortOrder(int column)
//		{
//			List<SortKey> keys = new ArrayList<SortKey>(getSortKeys());
//			SortKey sortKey;
//			int sortIndex;
//			for (sortIndex = keys.size() - 1; sortIndex >= 0; sortIndex--)
//			{
//				if (keys.get(sortIndex).getColumn() == column)
//				{
//					break;
//				}
//			}
//			if (sortIndex == -1)
//			{
//				// Key doesn't exist
//				sortKey = new SortKey(column, SortOrder.ASCENDING);
//				keys.add(0, sortKey);
//			}
//			else if (sortIndex == 0)
//			{
//				// It's the primary sorting key, toggle it
//				keys.set(0, toggle(keys.get(0)));
//			}
//			else
//			{
//				// It's not the first, but was sorted on, remove old
//				// entry, insert as first with ascending.
//				keys.remove(sortIndex);
//				keys.add(0, new SortKey(column, SortOrder.ASCENDING));
//			}
//			if (keys.size() > 2)
//			{
//				keys = keys.subList(0, 2);
//			}
//			setSortKeys(keys);
//		}
//
//		private SortKey toggle(SortKey key)
//		{
//			if (key.getSortOrder() == SortOrder.ASCENDING)
//			{
//				return new SortKey(key.getColumn(), SortOrder.DESCENDING);
//			}
//			return new SortKey(key.getColumn(), SortOrder.ASCENDING);
//		}
//
//		@Override
//		public int convertRowIndexToModel(int index)
//		{
//			return index;
//		}
//
//		@Override
//		public int convertRowIndexToView(int index)
//		{
//			return index;
//		}
//
//		@Override
//		public void setSortKeys(List<? extends SortKey> keys)
//		{
//			sortKeys = keys;
//			getModel().sortModel(this);
//		}
//
//		@Override
//		public List<? extends SortKey> getSortKeys()
//		{
//			return sortKeys;
//		}
//
//		@Override
//		public int getViewRowCount()
//		{
//			if(dataModel == null){
//				return 0;
//			}
//			return dataModel.getRowCount();
//		}
//
//		@Override
//		public int getModelRowCount()
//		{
//			if(dataModel == null){
//				return 0;
//			}
//			return dataModel.getRowCount();
//		}
//
//		
//		@Override
//		public int compare(Row o1, Row o2)
//		{
//			for (SortKey key : sortKeys)
//			{
//				if (key.getSortOrder() == SortOrder.UNSORTED)
//				{
//					continue;
//				}
//				int column = key.getColumn();
//				Comparator comparator = Comparators.getComparatorFor(getModel().getColumnClass(column));
//				Object obj1 = o1.getValueAt(column);
//				Object obj2 = o2.getValueAt(column);
////				if (o1.size() > column)
////				{
////					obj1 = o1.get(column);
////				}
////				if (o2.size() > column)
////				{
////					obj2 = o2.get(column);
////				}
//				int ret;
//				if (obj1 == null)
//				{
//					if (obj2 == null)
//					{
//						ret = 0;
//					}
//					else
//					{
//						ret = -1;
//					}
//				}
//				else if (obj2 == null)
//				{
//					ret = 1;
//				}
//				else
//				{
//					ret = comparator.compare(obj1, obj2);
//				}
//				if (key.getSortOrder() == SortOrder.DESCENDING)
//				{
//					ret *= -1;
//				}
//				if (ret != 0)
//				{
//					return ret;
//				}
//			}
//			return 0;
//		}
//
//		@Override
//		public void modelStructureChanged()
//		{
//		}
//
//		@Override
//		public void allRowsChanged()
//		{
//		}
//
//		@Override
//		public void rowsInserted(int firstRow, int endRow)
//		{
//		}
//
//		@Override
//		public void rowsDeleted(int firstRow, int endRow)
//		{
//		}
//
//		@Override
//		public void rowsUpdated(int firstRow, int endRow)
//		{
//		}
//
//		@Override
//		public void rowsUpdated(int firstRow, int endRow, int column)
//		{
//		}
//
//	}
}
