/*
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
 */
package pcgen.gui2.util.treeview;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.JTree;

import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ListFacades;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.gui2.util.table.Row;
import pcgen.gui2.util.treetable.AbstractTreeTableModel;
import pcgen.gui2.util.treetable.SortableTreeTableModel;
import pcgen.gui2.util.treetable.SortableTreeTableNode;
import pcgen.gui2.util.treetable.TreeTableNode;
import pcgen.util.CollectionMaps;
import pcgen.util.ListMap;
import pcgen.util.Logging;

@SuppressWarnings({"UseOfObsoleteCollectionType", "PMD.ReplaceVectorWithList", "PMD.UseArrayListInsteadOfVector"})
public class TreeViewTableModel<E> extends AbstractTreeTableModel implements SortableTreeTableModel
{

    private final ListListener<E> listListener = new ListListener<>()
    {

        @Override
        public void elementAdded(ListEvent<E> e)
        {
            addElement(e.getElement());
        }

        @Override
        public void elementRemoved(ListEvent<E> e)
        {
            removeElement(e.getElement());
        }

        @Override
        public void elementsChanged(ListEvent<E> e)
        {
            //Todo: optimize
            setElements(ListFacades.wrap(model));
        }

        @Override
        public void elementModified(ListEvent<E> e)
        {
        }

    };
    private final DataViewColumn namecolumn = new DataViewColumn()
    {

        @Override
        public String getName()
        {
            return selectedView.getViewName();
        }

        @Override
        public Class<?> getDataClass()
        {
            return TreeTableNode.class;
        }

        @Override
        public Visibility getVisibility()
        {
            return Visibility.ALWAYS_VISIBLE;
        }

        @Override
        public boolean isEditable()
        {
            return true;
        }

    };

    protected final Set<E> dataElements = new HashSet<>();
    protected List<? extends DataViewColumn> datacolumns;
    protected DataView<E> dataview;
    protected ListFacade<E> model = null;
    protected TreeView<? super E> selectedView = null;

    public TreeViewTableModel(DataView<E> dataView)
    {
        this.dataview = dataView;
        this.datacolumns = dataview.getDataColumns();
    }

    public void refreshData()
    {
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
        dataElements.clear();
        dataElements.addAll(data);
        setSelectedTreeView(selectedView);
    }

    private void setElements(Collection<E> data)
    {
        Set<E> newData = new HashSet<>(data);
        for (E newKey : newData)
        {
            if (!dataElements.contains(newKey))
            {
                addElement(newKey);
            }
        }
        Set<E> oldData = new HashSet<>(dataElements);
        for (E oldKey : oldData)
        {
            if (!newData.contains(oldKey))
            {
                removeElement(oldKey);
            }
        }
    }

    private void removeElement(E elem)
    {
        if (dataElements.contains(elem) && selectedView != null)
        {
            TreeViewNode rootNode = (TreeViewNode) getRoot();
            for (TreeViewPath<? super E> path : selectedView.getPaths(elem))
            {
                rootNode.removeTreeViewPath(path);
            }
            dataElements.remove(elem);
        }
    }

    private void addElement(E elem)
    {
        if (!dataElements.contains(elem) && selectedView != null)
        {
            dataElements.add(elem);
            TreeViewNode rootNode = (TreeViewNode) getRoot();
            for (TreeViewPath<? super E> path : selectedView.getPaths(elem))
            {
                rootNode.insertTreeViewPath(path);
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
            Vector<TreeViewPath<? super E>> paths = new Vector<>();
            for (E element : dataElements)
            {
                paths.addAll(view.getPaths(element));
            }
            setRoot(new TreeViewNode(paths));
        }
    }

    @Override
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
            return column == 0 || dataElements.contains(((TreeViewNode) node).getUserObject());
        }
        return false;
    }

    private DataViewColumn getDataColumn(int column)
    {
        if (column == 0)
        {
            return namecolumn;
        }
        return datacolumns.get(column - 1);
    }

    @Override
    public final void sortModel(Comparator<Row> comparator)
    {
        TreeViewNode rootNode = (TreeViewNode) getRoot();
        rootNode.sortChildren(comparator);
        reload();
    }

    private final class TreeViewNode extends JTree.DynamicUtilTreeNode implements SortableTreeTableNode
    {

        private final int level;

        public TreeViewNode(Vector<TreeViewPath<? super E>> paths)
        {
            this(0, null, paths);
        }

        private TreeViewNode(int level, Object name, Vector<TreeViewPath<? super E>> paths)
        {
            super(name, paths);
            this.level = level;
            setAllowsChildren(true);
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
                ListMap<Object, TreeViewPath<? super E>, Vector<TreeViewPath<? super E>>> vectorMap =
                        CollectionMaps.createListMap(HashMap.class, Vector.class);
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
                    if (vector.size() == 1 && vector.firstElement().getPathCount() == level + 1)
                    {
                        child = new TreeViewNode(level + 1, key, null);
                    } else
                    {
                        child = new TreeViewNode(level + 1, key, vector);
                    }
                    child.setComparator(mostRecentComparator);
                    if (mostRecentComparator == null || children == null)
                    {
                        add(child);
                    } else
                    {
                        Vector nonGenericChildren = children;
                        int index = Collections.binarySearch(nonGenericChildren, child, mostRecentComparator);
                        if (index < 0)
                        {
                            insert(child, -(index + 1));
                        } else
                        {
                            insert(child, index);
                        }
                    }
                }
                childValue = null;
            }
        }

        public void removeTreeViewPath(TreeViewPath<? super E> path)
        {
            if (!loadedChildren)
            {
                Vector<TreeViewPath<? super E>> vector = (Vector<TreeViewPath<? super E>>) childValue;
                vector.remove(path);
                return;
            }
            Object levelObject = path.getPathComponent(level);
            if (levelObject == null)
            {
                return;
            }

            for (int i = 0;i < getChildCount();i++)
            {
                TreeViewNode child = (TreeViewNode) getChildAt(i);
                if (child != null && levelObject.equals(child.userObject))
                {
                    if (path.getPathCount() == level + 1)
                    {//its a leaf, so remove appropriate child
                        removeNodeFromParent(child);
                    } else
                    {//its in a branch, so pass on the request to the child
                        child.removeTreeViewPath(path);
                        //make sure to remove the branch if it is no longer useful
                        if (!dataElements.contains(child.userObject) && child.getChildCount() == 0)
                        {
                            removeNodeFromParent(child);
                        }
                    }
                    return;
                }
            }
        }

        public void insertTreeViewPath(TreeViewPath<? super E> path)
        {
            //			Logging.errorPrint("adding: "+path);
            if (!loadedChildren)
            {
                Vector<TreeViewPath<? super E>> vector = (Vector<TreeViewPath<? super E>>) childValue;
                vector.add(path);
                return;
            }
            if (level >= path.getPathCount())
            {
                Logging.errorPrint(
                        "Ignoring attempt to add child at level " + level + " which is beyond end of path " + path);
                return;
            }
            Object levelObject = path.getPathComponent(level);
            if (mostRecentComparator == null)
            {
                for (int i = 0;i < getChildCount();i++)
                {
                    TreeViewNode child = (TreeViewNode) getChildAt(i);
                    if (levelObject.equals(child.userObject))
                    {
                        child.insertTreeViewPath(path);
                        return;
                    }
                }
            }
            TreeViewNode newchild;
            if (path.getPathCount() == level + 1)
            {
                newchild = new TreeViewNode(level + 1, levelObject, null);
            } else
            {
                Vector<TreeViewPath<? super E>> vector = new Vector<>();
                vector.add(path);
                newchild = new TreeViewNode(level + 1, levelObject, vector);
            }
            newchild.setComparator(mostRecentComparator);
            if (mostRecentComparator == null || children == null)
            {
                insertNodeInto(newchild, this, getChildCount());
                return;
            }
            Vector nonGenericChildren = children;
            int index = Collections.binarySearch(nonGenericChildren, newchild, mostRecentComparator);
            if (index >= 0)
            {
                TreeViewNode child = (TreeViewNode) getChildAt(index);
                if (child.getLevel() >= path.getPathCount())
                {
                    // Duplicate named entry - just add it to the tree.
                    insertNodeInto(newchild, this, (index + 1));
                } else
                {
                    child.insertTreeViewPath(path);
                }
            } else
            {
                insertNodeInto(newchild, this, -(index + 1));
            }
        }

        @Override
        public boolean isLeaf()
        {
            if (level == 0)
            {
                return false;
            }
            if (!loadedChildren)
            {
                return childValue == null;
            } else
            {
                return getChildCount() == 0;
            }
        }

        private Comparator<Row> mostRecentComparator = null;

        public void setComparator(Comparator<Row> comparator)
        {
            this.mostRecentComparator = comparator;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void sortChildren(Comparator<Row> comparator)
        {
            setComparator(comparator);
            if (!loadedChildren)
            {
                loadChildren();
            }
            if (children != null)
            {
                Vector nonGenericChildren = children;
                nonGenericChildren.sort(comparator);
                for (Object obj : children)
                {
                    TreeViewNode child = (TreeViewNode) obj;
                    child.setComparator(mostRecentComparator);
                    if (child.loadedChildren)
                    {
                        child.sortChildren(comparator);
                    }
                }
            }
        }

        @Override
        public Object getValueAt(int column)
        {
            if (column == 0)
            {
                return userObject;
            }
            if (dataElements.contains(userObject))
            {
                return dataview.getData((E) userObject, column - 1);
            }
            return null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void setValueAt(Object value, int column)
        {
            if (dataElements.contains(userObject))
            {
                dataview.setData(value, (E) userObject, column - 1);
            }
        }

        @Override
        public TreeTableNode getChildAt(int childIndex)
        {
            return (TreeTableNode) super.getChildAt(childIndex);
        }

        @Override
        public TreeTableNode getParent()
        {
            return (TreeTableNode) super.getParent();
        }

    }

}
