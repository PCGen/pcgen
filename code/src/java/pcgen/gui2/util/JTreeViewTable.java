/*
 * JTreeViewTable.java
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on May 13, 2010, 11:53:50 AM
 */
package pcgen.gui2.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;
import pcgen.core.facade.util.ListFacade;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.util.event.DynamicTableColumnModelListener;
import pcgen.gui2.util.table.DefaultDynamicTableColumnModel;
import pcgen.gui2.util.table.DynamicTableColumnModel;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DataViewColumn.Visibility;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewTableModel;
import pcgen.system.PropertyContext;
import pcgen.util.CollectionMaps;
import pcgen.util.ListMap;
import pcgen.util.Logging;

/**
 * This class is a reimplementation of JTreeViewPane
 * but is implemented into the JTreeTable rather than into the JScrollPane
 * This should be used instead of JTreeViewPane since it provides direct
 * access to underlying JTreeTable. Care should be taken (as with JTreeTable)
 * to not overwrite the underlying data structures through parent methods.
 * Methods whose usage would endanger the integrity of this class are the following:
 * getModel()
 * setModel(TableModel)
 * getTreeTableModel()
 * setTreeTableModel(TreeTableModel)
 * setTableHeader(JTableHeader)
 * setAutoCreateColumnsFromModel(boolean);
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
@SuppressWarnings("serial")
public class JTreeViewTable<T> extends JTreeTable implements PropertyChangeListener
{

	/** The preferences key for the selected tree view index. */
	private static final String VIEW_INDEX_PREFS_KEY = "viewIdx";
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
	protected TreeViewTableModel<T> treetableModel;
	private TreeViewModel<T> viewModel;
	private TreeViewsPopupMenu treeviewMenu = new TreeViewsPopupMenu();
	private PropertyContext baseContext;

	/**
	 * Create a new instance of JTreeViewTable
	 */
	public JTreeViewTable()
	{
		setTableHeader(new JTreeViewHeader());
		setAutoCreateColumnsFromModel(false);
		baseContext = UIPropertyContext.createContext("tablePrefs");
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
//			Logging.errorPrint("new width of " + evt.getNewValue() + " for "
//				+ this.viewModel.getDataView().getPrefsKey() + "."
//				+ normalisePrefsKey(col.getHeaderValue().toString()) + " from "
//				+ evt.getOldValue());
			PropertyContext context =
					baseContext.createChildContext(
						this.viewModel.getDataView().getPrefsKey())
						.createChildContext("width"); //$NON-NLS-1$
			context.setInt(normalisePrefsKey(col.getHeaderValue().toString()), (Integer) evt.getNewValue());
		}
		
	}

	private String normalisePrefsKey(String origKey)
	{
		return origKey.replaceAll("[^\\w\\.]", "_"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	protected <TM> TreeViewTableModel<TM> createDefaultTreeViewTableModel(DataView<TM> dataView)
	{
		return new TreeViewTableModel<TM>(dataView);
	}

	private JCheckBoxMenuItem createMenuItem(TableColumn column)
	{
		JCheckBoxMenuItem item = new JCheckBoxMenuItem();
		boolean visible = dynamicColumnModel.isVisible(column);
		item.setSelected(visible);
		item.setAction(new MenuAction(column, visible));
		return item;
	}

	private DynamicTableColumnModel createTableColumnModel(TreeView<?> startingView,
														   DataView<?> dataView)
	{
		@SuppressWarnings("unchecked")
		ListMap<Visibility, TableColumn, List<TableColumn>> listMap =
				CollectionMaps.createListMap(HashMap.class, ArrayList.class);
		PropertyContext viewPrefsContext =
				baseContext.createChildContext(this.viewModel.getDataView()
					.getPrefsKey());
		PropertyContext colWidthCtx = viewPrefsContext.createChildContext("width"); //$NON-NLS-1$
		PropertyContext colVisibleCtx = viewPrefsContext.createChildContext("visible"); //$NON-NLS-1$
		int index = 1;
		for (DataViewColumn column : dataView.getDataColumns())
		{
			TableColumn tableColumn = new TableColumn(index++);
			tableColumn.setHeaderValue(column.getName());
			String prefsKey =
					normalisePrefsKey(tableColumn.getHeaderValue().toString());
			Visibility vis = column.getVisibility();
			if (vis != Visibility.ALWAYS_VISIBLE)
			{
				boolean showCol =
						colVisibleCtx.initBoolean(prefsKey,
							vis == Visibility.INITIALLY_VISIBLE);
				vis =
						showCol ? Visibility.INITIALLY_VISIBLE
							: Visibility.INITIALLY_INVISIBLE;
			}
			listMap.add(vis, tableColumn);

			tableColumn.setPreferredWidth(colWidthCtx.initInt(prefsKey, 75));
			tableColumn.addPropertyChangeListener(this);
		}

		List<TableColumn> columns = listMap.get(Visibility.ALWAYS_VISIBLE);
		if (columns == null)
		{
			columns = Collections.emptyList();
		}
		DynamicTableColumnModel model = new DefaultDynamicTableColumnModel(columns.size() + 1);
		TableColumn viewColumn = new TableColumn();
		viewColumn.setHeaderValue(startingView.getViewName());
		model.addColumn(viewColumn);
		viewColumn.setPreferredWidth(colWidthCtx.initInt(
			normalisePrefsKey(viewColumn.getHeaderValue().toString()), 150));
		viewColumn.addPropertyChangeListener(this);

		for (TableColumn column : columns)
		{
			model.addColumn(column);
		}

		columns = listMap.get(Visibility.INITIALLY_VISIBLE);
		if (columns != null)
		{
			for (TableColumn column : columns)
			{
				model.addColumn(column);
				model.setVisible(column, true);
			}
		}

		columns = listMap.get(Visibility.INITIALLY_INVISIBLE);
		if (columns != null)
		{
			for (TableColumn column : columns)
			{
				model.addColumn(column);
			}
		}
		return model;
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

	/**
	 * This returns data that is currently highlighted by the user. This may 
	 * include branch nodes which are of type Object and not the type managed by 
	 * the table model. Hence we cannot use <T> here.  
	 * @return A list of selected leaf and branch rows.
	 */
	public List<Object> getSelectedData()
	{
		TreePath[] paths = getTree().getSelectionPaths();
		if (paths == null)
		{
			return Collections.emptyList();
		}
		List<Object> data = new ArrayList<Object>(paths.length);
		for (TreePath path : paths)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
			data.add(node.getUserObject());
		}
		return data;
	}

	/**
	 * Returns the currently selected object, if any.
	 * @return the selected object or null if none selected. 
	 */
	public Object getSelectedObject()
	{
		int selectedRow = getSelectedRow();
		if (selectedRow != -1)
		{
			return getModel().getValueAt(selectedRow, 0);
		}
		return null;
	}

	public void refreshModelData()
	{
		if (treetableModel != null)
		{
			treetableModel.refreshData();
		}
		resizeAndRepaint();
	}

	/**
	 * React to a non structural change in model data by repainting the table. Will 
	 * not collapse the tree or change which rows are displayed and will not be 
	 * sufficient if rows have been added or removed.   
	 */
	public void updateDisplay()
	{
		resizeAndRepaint();
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

	protected void setTreeView(TreeView<? super T> view)
	{
		TableColumn viewColumn = getColumn(treetableModel.getSelectedTreeView().getViewName());
		treetableModel.setSelectedTreeView(view);
		viewColumn.setHeaderValue(view.getViewName());
		sortModel();
		getTableHeader().repaint();
		PropertyContext context =
				baseContext.createChildContext(
					this.viewModel.getDataView().getPrefsKey());
		
		int index = getIndex(viewModel.getTreeViews(), view);
		if (index >= 0)
		{
			context.setInt(VIEW_INDEX_PREFS_KEY, index); //$NON-NLS-1$
		}
	}

	/**
	 * get the index of the view.
	 * @param treeViews The list of tree views.
	 * @param view The view to be found
	 * @return The index or -1 if not found.
	 */
	private int getIndex(ListFacade<? extends TreeView<T>> treeViews,
		TreeView<? super T> view)
	{
		for (int i = 0; i < treeViews.getSize(); i++)
		{
			TreeView<T> treeView = treeViews.getElementAt(i);
			if (treeView.equals(view))
			{
				return i;
			}
		}
		Logging.errorPrint("Unable to find view " + view + " in " + treeViews);
		return -1;
	}

	public TreeViewModel<?> getTreeViewModel()
	{
		return viewModel;
	}

	public void setTreeViewModel(TreeViewModel<T> viewModel)
	{
		ListFacade<? extends TreeView<T>> views = viewModel.getTreeViews();
		PropertyContext context =
				baseContext.createChildContext(
					viewModel.getDataView().getPrefsKey());
		int viewIndex = context.initInt(VIEW_INDEX_PREFS_KEY, viewModel.getDefaultTreeViewIndex());
		TreeView<? super T> startingView = views.getElementAt(viewIndex);
		DataView<T> dataView = viewModel.getDataView();
		final TreeViewTableModel<T> model = createDefaultTreeViewTableModel(dataView);
		this.treetableModel = model;
		if (this.viewModel != null)
		{
			this.viewModel.getTreeViews().removeListListener(treeviewMenu);
		}
		this.viewModel = viewModel;
		treeviewMenu.resetComponents();
		this.viewModel.getTreeViews().addListListener(treeviewMenu);

		model.setDataModel(viewModel.getDataModel());
		model.setSelectedTreeView(startingView);
		setTreeTableModel(model);
		setColumnModel(createTableColumnModel(startingView, dataView));
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				sortModel();
			}

		});
	}

	/**
     * Find the named view.
	 * @param views The list of TreeViews.
	 * @param viewName The name of the desired view.
	 * @return The matching view, or the first one if none match.
	 */
	private TreeView<? super T> findViewByName(
		ListFacade<? extends TreeView<T>> views, String viewName)
	{
		for (TreeView<T> treeView : views)
		{
			if (treeView.getViewName().equals(viewName))
			{
				return treeView;
			}
		}
		return views.getElementAt(0);
	}

	private class TreeViewsPopupMenu extends JPopupMenu implements ListListener<TreeView<T>>
	{

		private ButtonGroup group = new ButtonGroup();

		@Override
		public void elementAdded(ListEvent<TreeView<T>> e)
		{
			JMenuItem item = new JRadioButtonMenuItem(new ChangeViewAction(e.getElement()));
			group.add(item);
			add(item, e.getIndex());
		}

		@Override
		public void elementRemoved(ListEvent<TreeView<T>> e)
		{
			group.remove((AbstractButton) getComponent(e.getIndex()));
			remove(e.getIndex());
		}

		public void resetComponents()
		{
			elementsChanged(null);
		}

		@Override
		public void elementsChanged(ListEvent<TreeView<T>> e)
		{
			ListFacade<? extends TreeView<T>> views = viewModel.getTreeViews();
			PropertyContext context =
					baseContext.createChildContext(viewModel.getDataView()
						.getPrefsKey());
			int viewIndex = context.initInt(VIEW_INDEX_PREFS_KEY, viewModel.getDefaultTreeViewIndex());
			TreeView<? super T> startingView =
					views.getElementAt(viewIndex);
			group = new ButtonGroup();
			removeAll();
			for (TreeView<T> treeview : views)
			{
				JMenuItem item = new JRadioButtonMenuItem(new ChangeViewAction(treeview));
				item.setSelected(startingView == treeview);
				group.add(item);
				add(item);
			}
		}

		@Override
		public void elementModified(ListEvent<TreeView<T>> e)
		{
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
					baseContext.createChildContext(
						viewModel.getDataView().getPrefsKey())
						.createChildContext("visible"); //$NON-NLS-1$
			context.setBoolean(normalisePrefsKey(column.getHeaderValue()
				.toString()), visible);
		}

	}

	private class ChangeViewAction extends AbstractAction
	{

		private TreeView<T> view;

		public ChangeViewAction(TreeView<T> view)
		{
			super(view.getViewName());
			this.view = view;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			setTreeView(view);
		}

	}

	protected class JTreeViewHeader extends JTableSortingHeader
	{

		public JTreeViewHeader()
		{
			super(JTreeViewTable.this);
		}

		@Override
		protected TableCellRenderer createDefaultRenderer()
		{
			return new TreeViewHeaderRenderer();
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			if (!treeviewMenu.isVisible())
			{
				super.mouseClicked(e);
			}
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			super.mousePressed(e);
			maybeShowPopup(e);
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{

			super.mouseReleased(e);
			maybeShowPopup(e);
		}

		protected void maybeShowPopup(MouseEvent e)
		{
			if (e.isPopupTrigger() && getTrackedColumn().getHeaderValue()
					== treetableModel.getSelectedTreeView().getViewName())
			{
				TableColumnModel columnmodel = getColumnModel();
				Rectangle rect = getHeaderRect(columnmodel.getColumnIndexAtX(e.getX()));
				treeviewMenu.setPopupSize(rect.width, treeviewMenu.getPreferredSize().height);
				treeviewMenu.show(JTreeViewTable.this.getParent(), rect.x, rect.y);
			}
		}

		private class TreeViewHeaderRenderer extends SortingHeaderRenderer
		{

			private JLabel arrowLabel;

			public TreeViewHeaderRenderer()
			{
				arrowLabel = new JLabel(new ArrowIcon(SwingConstants.SOUTH, 5));
				arrowLabel.setPreferredSize(new Dimension(16, 16));
				arrowLabel.setMaximumSize(new Dimension(16, Integer.MAX_VALUE));
				arrowLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
			}

			@Override
			public Component getTableCellRendererComponent(JTable jTable,
														   Object value,
														   boolean isSelected,
														   boolean hasFocus,
														   int row,
														   int column)
			{
				super.getTableCellRendererComponent(jTable, value, isSelected, hasFocus, row, column);
				removeAll();
				if (treetableModel.getSelectedTreeView().getViewName() == value)
				{
					add(arrowLabel);
				}
				return this;
			}

		}

	}

}
