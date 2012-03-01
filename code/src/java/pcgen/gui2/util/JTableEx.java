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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import pcgen.gui2.util.table.DefaultSortableTableModel;
import pcgen.gui2.util.table.SortableTableModel;
import pcgen.gui2.util.table.TableCellUtilities;
import pcgen.util.Comparators;

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
	private final RowComparator rowComparator = new RowComparator();
	private List<SortingPriority> columnkeys;

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
	public JTableEx(SortableTableModel tm)
	{
		this(tm, null, null);
	}

	/**
	 * Constructor
	 * @param tm
	 * @param tcm
	 */
	public JTableEx(SortableTableModel tm, TableColumnModel tcm)
	{
		this(tm, tcm, null);
	}

	public JTableEx(SortableTableModel tm, TableColumnModel tcm,
					ListSelectionModel lsm)
	{
		super(tm, tcm, lsm);

		setDefaultRenderer(BigDecimal.class, new TableCellUtilities.AlignRenderer(SwingConstants.RIGHT));
		setDefaultRenderer(Float.class, new TableCellUtilities.AlignRenderer(SwingConstants.RIGHT));
		setDefaultRenderer(Integer.class, new TableCellUtilities.AlignRenderer(SwingConstants.RIGHT));
		setSortingPriority(createDefaultSortingPriority());
		setTableHeader(new JTableSortingHeader(this));
		installDoubleCLickListener();
	}

	protected List<SortingPriority> createDefaultSortingPriority()
	{
		Vector<SortingPriority> list = new Vector<SortingPriority>();
		list.add(new SortingPriority(0, SortMode.ASCENDING));
		return list;
	}

	private void installDoubleCLickListener()
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
	public void setModel(TableModel model)
	{
		if (!(model instanceof SortableTableModel))
		{
			model = new DefaultSortableTableModel(model);
		}
		super.setModel(model);
		sortModel();
	}

	@Override
	public SortableTableModel getModel()
	{
		return (SortableTableModel) super.getModel();
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

	@Override
	public void tableChanged(TableModelEvent e)
	{
		super.tableChanged(e);
//		if(!isSorting)
//		{
//			isSorting = true;
//			sortModel();
//			isSorting = false;
//		}
	}

	public void toggleSort(int column)
	{
		Vector<SortingPriority> list = new Vector<SortingPriority>(getSortingPriority());
		int index;
		for (index = list.size() - 1; index >= 0; index--)
		{
			if (list.get(index).getColumn() == column)
			{
				break;
			}
		}
		switch (index)
		{
			case 0:
				if (list.get(0).getMode() == SortMode.ASCENDING)
				{
					list.set(0, new SortingPriority(column, SortMode.DESCENDING));
					break;
				}
			default:
				list.remove(index);
			case -1:
				list.add(0, new SortingPriority(column, SortMode.ASCENDING));
		}
		if (list.size() > 2)
		{
			list.setSize(2);
		}
		setSortingPriority(list);
	}

	public void setSortingPriority(List<SortingPriority> keys)
	{
		this.columnkeys = Collections.unmodifiableList(keys);
		sortModel();
	}

	public void sortModel()
	{
		if (getAutoCreateColumnsFromModel())
		{
			TableColumnModel old = getColumnModel();
			setColumnModel(new DefaultTableColumnModel());
			getModel().sortModel(rowComparator);
			setColumnModel(old);
		}
		else
		{
			getModel().sortModel(rowComparator);
		}
	}

	public List<SortingPriority> getSortingPriority()
	{
		return columnkeys;
	}

	private final class RowComparator implements Comparator<List<?>>
	{

		@Override
		@SuppressWarnings("unchecked")
		public int compare(List<?> o1,
						   List<?> o2)
		{
			SortableModel model = getModel();
			for (SortingPriority priority : columnkeys)
			{
				if (priority.getMode() == SortMode.UNORDERED)
				{
					continue;
				}
				int column = priority.getColumn();
				Comparator comparator = Comparators.getComparatorFor(model.getColumnClass(column));
				Object obj1 = null;
				Object obj2 = null;
				if (o1.size() > column)
				{
					obj1 = o1.get(column);
				}
				if (o2.size() > column)
				{
					obj2 = o2.get(column);
				}
				int ret;
				if (obj1 == null || obj2 == null)
				{
					if (obj1 == obj2)
					{
						ret = 0;
					}
					else if (obj1 == null)
					{
						ret = -1;
					}
					else
					{
						ret = 1;
					}
				}
				else
				{
					ret = comparator.compare(obj1, obj2);
				}
				if (priority.getMode() == SortMode.DESCENDING)
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

	}

}
