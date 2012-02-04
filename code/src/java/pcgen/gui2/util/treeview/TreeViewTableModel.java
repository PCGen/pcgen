/*
 * TreeTableViewModel.java
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
 * Created on Feb 11, 2008, 9:04:19 PM
 */
package pcgen.gui2.util.treeview;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTree;

import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;
import pcgen.core.facade.util.ListFacade;
import pcgen.core.facade.util.ListFacades;
import pcgen.gui2.util.treetable.AbstractTreeTableModel;
import pcgen.gui2.util.treetable.SortableTreeTableModel;
import pcgen.gui2.util.treetable.SortableTreeTableNode;
import pcgen.gui2.util.treetable.TreeTableNode;
import pcgen.util.CollectionMaps;
import pcgen.util.ListMap;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class TreeViewTableModel<E> extends AbstractTreeTableModel
		implements SortableTreeTableModel
{

	protected final Map<TreeView<? super E>, TreeViewNode> viewMap = new HashMap<TreeView<? super E>, TreeViewNode>();
	private final ListListener<E> listListener = new ListListener<E>()
	{

		public void elementAdded(ListEvent<E> e)
		{
			E elem = e.getElement();
			addElement(elem);
		}

		public void elementRemoved(ListEvent<E> e)
		{
			//Todo: optimize
			setData(ListFacades.wrap(model));
		}

		public void elementsChanged(ListEvent<E> e)
		{
			//Todo: optimize
			setData(ListFacades.wrap(model));
		}

	};
	private final DataViewColumn namecolumn = new DataViewColumn()
	{

		public String getName()
		{
			return selectedView.getViewName();
		}

		public Class<?> getDataClass()
		{
			return TreeTableNode.class;
		}

		public Visibility getVisibility()
		{
			return Visibility.ALWAYS_VISIBLE;
		}

		public boolean isEditable()
		{
			return true;
		}

	};
	protected final Map<E, List<?>> dataMap = new HashMap<E, List<?>>();
	protected List<? extends DataViewColumn> datacolumns;
	protected DataView<E> dataview;
	protected ListFacade<E> model = null;
	protected TreeView<? super E> selectedView = null;

	protected TreeViewTableModel()
	{
	}

	public TreeViewTableModel(DataView<E> dataView)
	{
		this.dataview = dataView;
		this.datacolumns = dataview.getDataColumns();
	}

	public void refreshData()
	{
		dataMap.clear();
		populateDataMap(ListFacades.wrap(model));
	}

	public final void setDataModel(ListFacade<E> model)
	{
		if (this.model != null)
		{
			this.model.removeListListener(listListener);
		}
		this.model = model;
		model.addListListener(listListener);
		setData(ListFacades.wrap(model));
	}

	private void setData(Collection<E> data)
	{
		dataMap.keySet().retainAll(data);
		populateDataMap(data);
		viewMap.clear();
		setSelectedTreeView(selectedView);
	}

	private void addElement(E elem)
	{
		if (!dataMap.containsKey(elem))
		{
			dataMap.put(elem, dataview.getData(elem));
			//TODO If we change the root (and possibly the expanded path nodes) we lose the currently expanded path.
			// So here we need to insert the new node wihtout disturbing the exisint nodes.
			// We'll probably need to do the same for remove as well, both for efficiency and for keeping things expanded.
			
//			TreeViewNode parent = viewMap.get(selectedView);
//			for (TreeViewPath<? super E> path : selectedView.getPaths(elem))
//			{
//				Logging.errorPrint("Have path " + path);
//				Vector<TreeViewPath<? super E>> paths = new Vector<TreeViewPath<? super E>>();
//				paths.add(path);
//				for (int i = 0; i < path.getPathCount(); i++)
//				{
//					Object pathComp =path.getPathComponent(i);
//					boolean found = false;
//					for (int j = 0; j < parent.getChildCount(); j++)
//					{
//						TreeViewNode child = (TreeViewNode) parent.getChildAt(j);
//						if (child.getUserObject().equals(pathComp))
//						{
//							parent = child;
//							found = true;
//							break;
//						}
//					}
//					if (!found && i < path.getPathCount() -1)
//					{
//						TreeViewNode node = new TreeViewNode(i, pathComp, null);
//						Logging.errorPrint("Adding " + node + " to " + parent);
//						insertNodeInto(node, parent, 0);
//						parent = node;
//					}
//				}
//				TreeViewNode node = new TreeViewNode(path.getPathCount(), elem, null);
//				Logging.errorPrint("Adding " + node + " to " + parent + " numChildren " + parent.getChildCount());
//				insertNodeInto(node, parent, 0);
//				Logging.errorPrint("After numChildren " + parent.getChildCount());
//			}


			viewMap.clear();
			setSelectedTreeView(selectedView);
		}
	}

	private void populateDataMap(Collection<E> data)
	{
		for (E obj : data)
		{
			if (!dataMap.containsKey(obj))
			{
				dataMap.put(obj, dataview.getData(obj));
			}
		}
	}

	public final TreeView<? super E> getSelectedTreeView()
	{
		return selectedView;
	}

	public final void setSelectedTreeView(TreeView<? super E> view)
	{
		if (view != null)
		{
			this.selectedView = view;
			TreeViewNode node = viewMap.get(view);
			if (node == null)
			{
				Vector<TreeViewPath<? super E>> paths = new Vector<TreeViewPath<? super E>>();
				for (E element : dataMap.keySet())
				{
					for (TreeViewPath<? super E> path : view.getPaths(element))
					{
						paths.add(path);
					}
				}
				node = new TreeViewNode(paths);
				viewMap.put(view, node);
			}
			setRoot(node);
		}
	}

	public final int getColumnCount()
	{
		return datacolumns.size() + 1;
	}

	@Override
	public Class<?> getColumnClass(int column)
	{
		return getDataColumn(column).getDataClass();
	}

	@Override
	public String getColumnName(int column)
	{
		return getDataColumn(column).getName();
	}

	@Override
	public boolean isCellEditable(Object node, int column)
	{
		if (getDataColumn(column).isEditable())
		{
			return column == 0 ||
					dataMap.containsKey(((TreeViewNode) node).getUserObject());
		}
		return false;
	}

	private DataViewColumn getDataColumn(int column)
	{
		switch (column)
		{
			case 0:
				return namecolumn;
			default:
				return datacolumns.get(column - 1);
		}
	}

	public final void sortModel(Comparator<List<?>> comparator)
	{
		viewMap.get(selectedView).sortChildren(new TreeNodeComparator(comparator));
		reload();
	}

	private final class TreeViewNode extends JTree.DynamicUtilTreeNode
			implements SortableTreeTableNode
	{

		private final int level;

		public TreeViewNode(Vector<TreeViewPath<? super E>> paths)
		{
			this(0, null, paths);
		}

		private TreeViewNode(int level, Object name,
							 Vector<TreeViewPath<? super E>> paths)
		{
			super(name, paths);
			this.level = level;
		}

		@Override
		public int getLevel()
		{
			return level;
		}

		@Override
		@SuppressWarnings("unchecked")
		protected void loadChildren()
		{
			loadedChildren = true;
			if (childValue != null)
			{
				ListMap<Object, TreeViewPath<? super E>, Vector<TreeViewPath<? super E>>> vectorMap = CollectionMaps.createListMap(
						HashMap.class,
						Vector.class);
				Vector<TreeViewPath<? super E>> vector = (Vector<TreeViewPath<? super E>>) childValue;
				for (TreeViewPath<? super E> path : vector)
				{
					if (path.getPathCount() > level)
					{
						Object key = path.getPathComponent(level);
						vectorMap.add(key, path);
					}
				}
				for (Object key : vectorMap.keySet())
				{
					vector = vectorMap.get(key);
					TreeViewNode child;
					if (vector.size() == 1 &&
							vector.firstElement().getPathCount() <= level + 1)
					{
						child = new TreeViewNode(level + 1, key, null);
					}
					else
					{
						child = new TreeViewNode(level + 1, key, vector);
					}
					add(child);
				}
				childValue = null;
			}
		}

		@SuppressWarnings("unchecked")
		public void sortChildren(Comparator<TreeTableNode> comparator)
		{
			if (!loadedChildren)
			{
				loadChildren();
			}
			if (children != null)
			{
				Collections.sort(children, comparator);
				for (Object obj : children)
				{
					TreeViewNode child = (TreeViewNode) obj;
					if (child.loadedChildren)
					{
						child.sortChildren(comparator);
					}
				}
			}
		}

		public List<Object> getValues()
		{
			Vector<Object> list = new Vector<Object>(getColumnCount());
			list.add(userObject);
			List<?> data = dataMap.get(userObject);
			if (data != null)
			{
				list.addAll(data);
			}
			list.setSize(getColumnCount());
			return list;
		}

		public Object getValueAt(int column)
		{
			if (column == 0)
			{
				return userObject;
			}
			List<?> data = dataMap.get(userObject);
			if (data != null && column <= data.size())
			{
				return data.get(column - 1);
			}
			return null;
		}

		@SuppressWarnings("unchecked")
		public void setValueAt(Object value, int column)
		{
			List data = dataMap.get(userObject);
			data.set(column - 1, value);
		}

	}

}
