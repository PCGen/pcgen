/*
 * JDynamicTable.java
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
 * Created on Jan 25, 2011, 12:39:13 PM
 */
package pcgen.gui2.util;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.util.event.DynamicTableColumnModelListener;
import pcgen.gui2.util.table.DefaultDynamicTableColumnModel;
import pcgen.gui2.util.table.DynamicTableColumnModel;
import pcgen.system.PropertyContext;

/**
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class JDynamicTable extends JTableEx implements PropertyChangeListener
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
	private final JButton cornerButton = new JButton(new CornerAction());
	private DynamicTableColumnModel dynamicColumnModel = null;
	private JPopupMenu menu = new JPopupMenu();
	private final PropertyContext baseContext;
	private final String prefsKey;

	/**
	 * Create a new instance of JDynamicTable
	 * @param prefsKey The key for storage of use preferences for the table.
	 */
	public JDynamicTable(String prefsKey)
	{
		this.prefsKey = prefsKey;
		baseContext = UIPropertyContext.createContext("tablePrefs");
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

		PropertyContext viewPrefsContext =
				baseContext.createChildContext(prefsKey);
		PropertyContext colWidthCtx = viewPrefsContext.createChildContext("width"); //$NON-NLS-1$
		PropertyContext colVisibleCtx = viewPrefsContext.createChildContext("visible"); //$NON-NLS-1$
		for (int i = 0; i < columnModel.getColumnCount(); i++)
		{
			TableColumn column = columnModel.getColumn(i);
			String colKey =
					normalisePrefsKey(column.getHeaderValue().toString());
			column.setPreferredWidth(colWidthCtx.initInt(colKey, 75));
			columnModel.setVisible(
				column,
				colVisibleCtx.initBoolean(colKey,
					columnModel.isVisible(column)));
			column.addPropertyChangeListener(this);
		}		
		
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

	private String normalisePrefsKey(String origKey)
	{
		return origKey.replaceAll("[^\\w\\.]", "_"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if (isShowing() && "width".equals(evt.getPropertyName()) //$NON-NLS-1$
			&& evt.getSource() instanceof TableColumn)
		{
			TableColumn col = (TableColumn) evt.getSource();
			PropertyContext context =
					baseContext.createChildContext(prefsKey)
						.createChildContext("width"); //$NON-NLS-1$
			String colKey = col.getHeaderValue().toString();
			context.setInt(normalisePrefsKey(colKey),
				(Integer) evt.getNewValue());
		}
	}

	private class CornerAction extends AbstractAction
	{

		public CornerAction()
		{
			super("...");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Container parent = getParent();
			//make sure that the menu has a chance to layout its components
			//so that its width can be initialized
			menu.setVisible(true);
			menu.show(parent, parent.getWidth() - menu.getWidth(), 0);
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
			PropertyContext context =
					baseContext.createChildContext(prefsKey)
						.createChildContext("visible"); //$NON-NLS-1$
			context.setBoolean(normalisePrefsKey(column.getHeaderValue()
				.toString()), visible);
		}

	}

}
