/*
 * Copyright 2011 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.gui2.util;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import pcgen.gui2.util.event.DynamicTableColumnModelListener;
import pcgen.gui2.util.table.DefaultDynamicTableColumnModel;
import pcgen.gui2.util.table.DynamicTableColumnModel;

public class JDynamicTable extends JTableEx
{

	private final DynamicTableColumnModelListener listener = new DynamicTableColumnModelListener()
	{

		@Override
		public void availableColumnAdded(TableColumnModelEvent event)
		{
			int index = event.getToIndex();
			TableColumn column = dynamicColumnModel.getAvailableColumns().get(index);
			menu.insert(createMenuItem(column), index);
			cornerButton.setVisible(true);
		}

		@Override
		public void availableColumnRemove(TableColumnModelEvent event)
		{

			menu.remove(event.getFromIndex());
			if (menu.getComponentCount() == 0)
			{
				cornerButton.setVisible(false);
			}
		}

	};
	private final JTableMenuButton cornerButton;
	private DynamicTableColumnModel dynamicColumnModel = null;
	private JPopupMenu menu = new JPopupMenu();

	public JDynamicTable()
	{
		this.cornerButton = new JTableMenuButton(this, menu);
	}

	@Override
	protected void configureEnclosingScrollPane()
	{
		super.configureEnclosingScrollPane();
		Container p = getParent();
		if (p instanceof JViewport)
		{
			Container gp = p.getParent();
			if (gp instanceof JScrollPane)
			{
				JScrollPane scrollPane = (JScrollPane) gp;
				// Make certain we are the viewPort's view and not, for
				// example, the rowHeaderView of the scrollPane -
				// an implementor of fixed columns might do this.
				JViewport viewport = scrollPane.getViewport();
				if (viewport == null || viewport.getView() != this)
				{
					return;
				}
				scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
				scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, cornerButton);
			}
		}
	}

	@Override
	protected void unconfigureEnclosingScrollPane()
	{
		super.unconfigureEnclosingScrollPane();
		Container p = getParent();
		if (p instanceof JViewport)
		{
			Container gp = p.getParent();
			if (gp instanceof JScrollPane)
			{
				JScrollPane scrollPane = (JScrollPane) gp;
				// Make certain we are the viewPort's view and not, for
				// example, the rowHeaderView of the scrollPane -
				// an implementor of fixed columns might do this.
				JViewport viewport = scrollPane.getViewport();
				if (viewport == null || viewport.getView() != this)
				{
					return;
				}
				scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, null);
			}
		}
	}

	protected DynamicTableColumnModel createDefaultDynamicTableColumnModel()
	{
		return new DefaultDynamicTableColumnModel(getColumnModel(), 1);
	}

	private JCheckBoxMenuItem createMenuItem(TableColumn column)
	{
		JCheckBoxMenuItem item = new JCheckBoxMenuItem();
		boolean visible = dynamicColumnModel.isVisible(column);
		item.setSelected(visible);
		item.setAction(new MenuAction(column, visible));
		return item;
	}

	@Override
	public void setColumnModel(TableColumnModel columnModel)
	{
		if (this.dynamicColumnModel != null)
		{
			this.dynamicColumnModel.removeDynamicTableColumnModelListener(listener);
			cornerButton.setVisible(false);
		}
		super.setColumnModel(columnModel);
	}

	public void setColumnModel(DynamicTableColumnModel columnModel)
	{
		if (this.dynamicColumnModel != null)
		{
			this.dynamicColumnModel.removeDynamicTableColumnModelListener(listener);
		}
		this.dynamicColumnModel = columnModel;
		columnModel.addDynamicTableColumnModelListener(listener);
		super.setColumnModel(columnModel);
		List<TableColumn> columns = columnModel.getAvailableColumns();
		menu.removeAll();
		if (!columns.isEmpty())
		{
			for (TableColumn column : columns)
			{
				menu.add(createMenuItem(column));
			}
			cornerButton.setVisible(true);
		}
		else
		{
			cornerButton.setVisible(false);
		}
	}

	private class MenuAction extends AbstractAction
	{

		private boolean visible;
		private TableColumn column;

		public MenuAction(TableColumn column, boolean visible)
		{
			super(column.getHeaderValue().toString());
			this.visible = visible;
			this.column = column;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			dynamicColumnModel.setVisible(column, visible = !visible);
		}

	}

}
