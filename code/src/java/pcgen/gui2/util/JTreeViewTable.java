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
 */
package pcgen.gui2.util;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import pcgen.facade.util.ListFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.tools.PrefTableColumnModel;
import pcgen.gui2.util.event.DynamicTableColumnModelListener;
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

/**
 * JTreeViewTable is a subclass of JTreeTable that uses a TreeViewModel instead
 * of a TreeTableModel. The TreeViewModel is a oriented towards displaying
 * arbitrary objects as a tree instead of TreeTableNodes. In addition, the
 * TreeViewModel supports multiple viewing methods and column visibility
 * controls.
 * <br>
 * <br>Node: Methods whose usage would endanger the integrity of this class are
 * the following:
 * <br>getModel()
 * <br>setModel(TableModel)
 * <br>getTreeTableModel()
 * <br>setTreeTableModel(TreeTableModel)
 * <br>setTableHeader(JTableHeader)
 * <br>setAutoCreateColumnsFromModel(boolean);
 *
 */
@SuppressWarnings("serial")
public class JTreeViewTable<T> extends JTreeTable
{

	/**
	 * Preferences key for the width of the tree view column.
	 */
	private static final String TREE_VIEW_COL_PREFS_KEY = "TreeView";
	/**
	 * The preferences key for the selected tree view index.
	 */
	private static final String VIEW_INDEX_PREFS_KEY = "viewIdx";

	private final JTableMenuButton cornerButton;
	private DynamicTableColumnModel dynamicColumnModel = null;
	protected TreeViewTableModel<T> treetableModel;
	private TreeViewModel<T> viewModel;
	protected CornerButtonPopupMenu cornerPopupMenu = new CornerButtonPopupMenu();
	private static final PropertyContext baseContext = UIPropertyContext.createContext("tablePrefs");

	/**
	 * Create a new instance of JTreeViewTable
	 */
	public JTreeViewTable()
	{
		setAutoCreateColumnsFromModel(false);
		setAutoCreateRowSorter(false);
		getTree().setLargeModel(true);
		this.cornerButton = new JTableMenuButton(this, cornerPopupMenu);
	}

	protected <TM> TreeViewTableModel<TM> createDefaultTreeViewTableModel(DataView<TM> dataView)
	{
		return new TreeViewTableModel<>(dataView);
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
		ListMap<Visibility, TableColumn, List<TableColumn>> listMap
				= CollectionMaps.createListMap(HashMap.class, ArrayList.class);
		int index = 1;
		for (DataViewColumn column : dataView.getDataColumns())
		{
			TableColumn tableColumn = new TableColumn(index++);
			tableColumn.setHeaderValue(column.getName());
			Visibility vis = column.getVisibility();
			listMap.add(vis, tableColumn);
		}

		List<TableColumn> columns = listMap.get(Visibility.ALWAYS_VISIBLE);
		if (columns == null)
		{
			columns = Collections.emptyList();
		}

		PrefTableColumnModel model = new PrefTableColumnModel(this.viewModel.getDataView().getPrefsKey(),
				columns.size() + 1);
		TableColumn viewColumn = new TableColumn();
		viewColumn.setHeaderValue(startingView.getViewName());
		viewColumn.setIdentifier(TREE_VIEW_COL_PREFS_KEY);
		model.addColumn(viewColumn, true, 150);

		for (TableColumn column : columns)
		{
			model.addColumn(column, true, 75);
		}

		columns = listMap.get(Visibility.INITIALLY_VISIBLE);
		if (columns != null)
		{
			for (TableColumn column : columns)
			{
				model.addColumn(column, true, 75);
			}
		}

		columns = listMap.get(Visibility.INITIALLY_INVISIBLE);
		if (columns != null)
		{
			for (TableColumn column : columns)
			{
				model.addColumn(column, false, 75);
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
	 * the table model. Hence we cannot use &lt;T&gt; here.
	 *
	 * @return A list of selected leaf and branch rows.
	 */
	public List<Object> getSelectedData()
	{
		TreePath[] paths = getTree().getSelectionPaths();
		if (paths == null)
		{
			return Collections.emptyList();
		}
		List<Object> data = new ArrayList<>(paths.length);
		for (TreePath path : paths)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
			data.add(node.getUserObject());
		}
		return data;
	}

	/**
	 * Returns the currently selected object, if any.
	 *
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
	 * React to a non structural change in model data by repainting the table.
	 * Will not collapse the tree or change which rows are displayed and will
	 * not be sufficient if rows have been added or removed.
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
			this.dynamicColumnModel.removeDynamicTableColumnModelListener(cornerPopupMenu);
			cornerButton.setVisible(false);
		}
		super.setColumnModel(columnModel);
	}

	public void setColumnModel(DynamicTableColumnModel columnModel)
	{
		if (this.dynamicColumnModel != null)
		{
			this.dynamicColumnModel.removeDynamicTableColumnModelListener(cornerPopupMenu);
		}
		this.dynamicColumnModel = columnModel;
		columnModel.addDynamicTableColumnModelListener(cornerPopupMenu);
		super.setColumnModel(columnModel);
		cornerPopupMenu.resetComponents();
	}

	protected void setTreeView(TreeView<? super T> view)
	{
		TableColumn viewColumn = getColumn(TREE_VIEW_COL_PREFS_KEY);
		treetableModel.setSelectedTreeView(view);
		viewColumn.setHeaderValue(view.getViewName());
		sortModel();
		getTableHeader().repaint();
		PropertyContext context = baseContext.createChildContext(
				this.viewModel.getDataView().getPrefsKey());

		int index = getIndex(viewModel.getTreeViews(), view);
		if (index >= 0)
		{
			context.setInt(VIEW_INDEX_PREFS_KEY, index); //$NON-NLS-1$
		}
	}

	/**
	 * get the index of the view.
	 *
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
		// If not found it is most likely the text search view,
		return -1;
	}

	public TreeViewModel<?> getTreeViewModel()
	{
		return viewModel;
	}

	public void setTreeViewModel(TreeViewModel<T> viewModel)
	{
		ListFacade<? extends TreeView<T>> views = viewModel.getTreeViews();
		PropertyContext context = baseContext.createChildContext(
				viewModel.getDataView().getPrefsKey());
		int viewIndex = context.initInt(VIEW_INDEX_PREFS_KEY, viewModel.getDefaultTreeViewIndex());
		TreeView<? super T> startingView = views.getElementAt(viewIndex);
		DataView<T> dataView = viewModel.getDataView();
		final TreeViewTableModel<T> model = createDefaultTreeViewTableModel(dataView);
		this.treetableModel = model;
		if (this.viewModel != null)
		{
			this.viewModel.getTreeViews().removeListListener(cornerPopupMenu);
		}
		this.viewModel = viewModel;

		model.setDataModel(viewModel.getDataModel());
		model.setSelectedTreeView(startingView);
		setTreeTableModel(model);
		setColumnModel(createTableColumnModel(startingView, dataView));
		cornerPopupMenu.resetComponents();
		this.viewModel.getTreeViews().addListListener(cornerPopupMenu);
	}

	/**
	 * @return The currently selected tree view.
	 */
	public TreeView<? super T> getSelectedTreeView()
	{
		return treetableModel.getSelectedTreeView();
	}

	/**
	 * Find the named view.
	 *
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

	/**
	 * This is the popup menu for the CornerButton which allows selection of the
	 * selected tree view as well as the visible columns for the table.
	 */
	protected class CornerButtonPopupMenu extends JPopupMenu implements
			ListListener<TreeView<T>>, DynamicTableColumnModelListener
	{

		private boolean treeViewsEnabled = true;
		private boolean tableColumnsEnabled = true;

		@Override
		public void availableColumnAdded(TableColumnModelEvent event)
		{
			resetComponents();
		}

		@Override
		public void availableColumnRemove(TableColumnModelEvent event)
		{
			resetComponents();
		}

		@Override
		public void elementAdded(ListEvent<TreeView<T>> e)
		{
			resetComponents();
		}

		@Override
		public void elementRemoved(ListEvent<TreeView<T>> e)
		{
			resetComponents();
		}

		public void setTreeViewsEnabled(boolean enabled)
		{
			this.treeViewsEnabled = enabled;
			resetComponents();
		}

		public void setTableColumnsEnabled(boolean enabled)
		{
			this.tableColumnsEnabled = enabled;
			resetComponents();
		}

		@Override
		public void elementsChanged(ListEvent<TreeView<T>> e)
		{
			resetComponents();
		}

		@Override
		public void elementModified(ListEvent<TreeView<T>> e)
		{
		}

		public void resetComponents()
		{
			ListFacade<? extends TreeView<T>> views = viewModel.getTreeViews();
			PropertyContext context
					= baseContext.createChildContext(viewModel.getDataView()
							.getPrefsKey());
			int viewIndex = context.initInt(VIEW_INDEX_PREFS_KEY, viewModel.getDefaultTreeViewIndex());

			ButtonGroup group = new ButtonGroup();
			TreeView<? super T> startingView = views.getElementAt(viewIndex);
			removeAll();
			JLabel treeLabel = new JLabel("Tree Views");
			treeLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
			add(treeLabel);
			for (TreeView<T> treeview : views)
			{
				JMenuItem item = new JRadioButtonMenuItem(new ChangeViewAction(treeview));
				item.setSelected(startingView == treeview);
				group.add(item);
				item.setEnabled(treeViewsEnabled);
				add(item);
			}
			addSeparator();
			JLabel columnLabel = new JLabel("Columns");
			columnLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
			add(columnLabel);
			List<TableColumn> columns = dynamicColumnModel.getAvailableColumns();
			for (TableColumn column : columns)
			{
				JMenuItem item = createMenuItem(column);
				item.setEnabled(tableColumnsEnabled);
				add(item);
			}
			cornerButton.setVisible(!columns.isEmpty() || !views.isEmpty());
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

}
