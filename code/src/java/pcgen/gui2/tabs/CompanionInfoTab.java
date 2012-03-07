/*
 * CompanionInfoTab.java
 * Copyright 2012 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Mar 4, 2012, 5:01:02 PM
 */
package pcgen.gui2.tabs;

import java.awt.Component;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreePath;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.CompanionFacade;
import pcgen.core.facade.CompanionSupportFacade;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.util.JTreeViewTable;
import pcgen.gui2.util.treetable.SortableTreeTableModel;
import pcgen.gui2.util.treetable.TreeTableNode;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DefaultDataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class CompanionInfoTab extends FlippingSplitPane implements CharacterInfoTab
{

	private final JTreeViewTable companionsTable;
	private final JEditorPane infoPane;

	public CompanionInfoTab()
	{
		this.companionsTable = new JTreeViewTable();
		this.infoPane = new JEditorPane();
		initComponents();
	}

	private void initComponents()
	{
		setLeftComponent(new JScrollPane(companionsTable));
		setRightComponent(new JScrollPane(infoPane));
	}

	@Override
	public Hashtable<Object, Object> createModels(CharacterFacade character)
	{
		Hashtable<Object, Object> state = new Hashtable<Object, Object>();
		state.put(CompanionsModel.class, new CompanionsModel(character));
		return state;
	}

	@Override
	public void restoreModels(Hashtable<?, ?> state)
	{
		companionsTable.setTreeViewModel((CompanionsModel) state.get(CompanionsModel.class));
	}

	@Override
	public void storeModels(Hashtable<Object, Object> state)
	{
	}

	@Override
	public TabTitle getTabTitle()
	{
		return new TabTitle("Companions");
	}

	private class ButtonCellEditor extends AbstractCellEditor implements TableCellEditor
	{

		@Override
		public Object getCellEditorValue()
		{
			return null;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}

	private class CompanionModel implements SortableTreeTableModel
	{

		private EventListenerList listenerList = new EventListenerList();
		private ListFacade<CompanionFacade> companions;
		private CompanionSupportFacade support;

		@Override
		public boolean isCellEditable(Object node, int column)
		{
			return true;
		}

		@Override
		public Class<?> getColumnClass(int column)
		{
			if (column == 0)
			{
				return TreeTableNode.class;
			}
			return Object.class;
		}

		@Override
		public int getColumnCount()
		{
			return 2;
		}

		@Override
		public String getColumnName(int column)
		{
			return null;
		}

		@Override
		public void setValueAt(Object aValue, Object node, int column)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Object getValueAt(Object node, int column)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

		private Object root = new Object();

		@Override
		public Object getRoot()
		{
			return root;
		}

		@Override
		public Object getChild(Object parent, int index)
		{
			if (parent == root)
			{
			}
			return null;
		}

		@Override
		public int getChildCount(Object parent)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean isLeaf(Object node)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void valueForPathChanged(TreePath path, Object newValue)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public int getIndexOfChild(Object parent, Object child)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void addTreeModelListener(TreeModelListener l)
		{
			listenerList.add(TreeModelListener.class, l);
		}

		@Override
		public void removeTreeModelListener(TreeModelListener l)
		{
			listenerList.remove(TreeModelListener.class, l);
		}

		@Override
		public void sortModel(Comparator<List<?>> comparator)
		{
		}

		/**
		 * Notifies all listeners that have registered interest for
		 * notification on this event type.  The event instance
		 * is lazily created using the parameters passed into
		 * the fire method.
		 *
		 * @param source the node being changed
		 * @param path the path to the root node
		 * @param childIndices the indices of the changed elements
		 * @param children the changed elements
		 * @see EventListenerList
		 */
		protected void fireTreeNodesChanged(Object source, Object[] path,
											int[] childIndices,
											Object[] children)
		{
			// Guaranteed to return a non-null array
			Object[] listeners = listenerList.getListenerList();
			TreeModelEvent e = null;
			// Process the listeners last to first, notifying
			// those that are interested in this event
			for (int i = listeners.length - 2; i >= 0; i -= 2)
			{
				if (listeners[i] == TreeModelListener.class)
				{
					// Lazily create the event:
					if (e == null)
					{
						e = new TreeModelEvent(source, path,
											   childIndices, children);
					}
					((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
				}
			}
		}

		/**
		 * Notifies all listeners that have registered interest for
		 * notification on this event type.  The event instance
		 * is lazily created using the parameters passed into
		 * the fire method.
		 *
		 * @param source the node where new elements are being inserted
		 * @param path the path to the root node
		 * @param childIndices the indices of the new elements
		 * @param children the new elements
		 * @see EventListenerList
		 */
		protected void fireTreeNodesInserted(Object source, Object[] path,
											 int[] childIndices,
											 Object[] children)
		{
			// Guaranteed to return a non-null array
			Object[] listeners = listenerList.getListenerList();
			TreeModelEvent e = null;
			// Process the listeners last to first, notifying
			// those that are interested in this event
			for (int i = listeners.length - 2; i >= 0; i -= 2)
			{
				if (listeners[i] == TreeModelListener.class)
				{
					// Lazily create the event:
					if (e == null)
					{
						e = new TreeModelEvent(source, path,
											   childIndices, children);
					}
					((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
				}
			}
		}

		/**
		 * Notifies all listeners that have registered interest for
		 * notification on this event type.  The event instance
		 * is lazily created using the parameters passed into
		 * the fire method.
		 *
		 * @param source the node where elements are being removed
		 * @param path the path to the root node
		 * @param childIndices the indices of the removed elements
		 * @param children the removed elements
		 * @see EventListenerList
		 */
		protected void fireTreeNodesRemoved(Object source, Object[] path,
											int[] childIndices,
											Object[] children)
		{
			// Guaranteed to return a non-null array
			Object[] listeners = listenerList.getListenerList();
			TreeModelEvent e = null;
			// Process the listeners last to first, notifying
			// those that are interested in this event
			for (int i = listeners.length - 2; i >= 0; i -= 2)
			{
				if (listeners[i] == TreeModelListener.class)
				{
					// Lazily create the event:
					if (e == null)
					{
						e = new TreeModelEvent(source, path,
											   childIndices, children);
					}
					((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
				}
			}
		}

		/**
		 * Notifies all listeners that have registered interest for
		 * notification on this event type.  The event instance
		 * is lazily created using the parameters passed into
		 * the fire method.
		 *
		 * @param source the node where the tree model has changed
		 * @param path the path to the root node
		 * @param childIndices the indices of the affected elements
		 * @param children the affected elements
		 * @see EventListenerList
		 */
		protected void fireTreeStructureChanged(Object source, Object[] path,
												int[] childIndices,
												Object[] children)
		{
			// Guaranteed to return a non-null array
			Object[] listeners = listenerList.getListenerList();
			TreeModelEvent e = null;
			// Process the listeners last to first, notifying
			// those that are interested in this event
			for (int i = listeners.length - 2; i >= 0; i -= 2)
			{
				if (listeners[i] == TreeModelListener.class)
				{
					// Lazily create the event:
					if (e == null)
					{
						e = new TreeModelEvent(source, path,
											   childIndices, children);
					}
					((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
				}
			}
		}

	}

	private class CompanionsModel implements TreeViewModel<CompanionFacade>,
			DataView<CompanionFacade>, TreeView<CompanionFacade>
	{

		private final ListFacade<TreeView<CompanionFacade>> views =
				new DefaultListFacade<TreeView<CompanionFacade>>(Arrays.asList(this));
		private final List<DefaultDataViewColumn> columns = Arrays.asList(new DefaultDataViewColumn(null, Object.class));
		private CharacterFacade character;

		public CompanionsModel(CharacterFacade character)
		{
			this.character = character;
		}

		@Override
		public ListFacade<? extends TreeView<CompanionFacade>> getTreeViews()
		{
			return views;
		}

		@Override
		public int getDefaultTreeViewIndex()
		{
			return 0;
		}

		@Override
		public DataView<CompanionFacade> getDataView()
		{
			return this;
		}

		@Override
		public ListFacade<CompanionFacade> getDataModel()
		{
			return character.getCompanionSupport().getCompanions();
		}

		@Override
		public List<?> getData(CompanionFacade obj)
		{
			return Collections.singletonList(null);
		}

		@Override
		public List<? extends DataViewColumn> getDataColumns()
		{
			return columns;
		}

		@Override
		public String getViewName()
		{
			return "Companions View";//it doesn't matter what this string is
		}

		@Override
		public List<TreeViewPath<CompanionFacade>> getPaths(CompanionFacade pobj)
		{
			return Arrays.asList(new TreeViewPath<CompanionFacade>(pobj, pobj.getCompanionType()));
		}

	}

}
