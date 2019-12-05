/*
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
package pcgen.gui2.filter;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;

import javax.swing.RowSorter;

import pcgen.gui2.util.JTreeViewTable;
import pcgen.gui2.util.table.SortableTableModel;
import pcgen.gui2.util.table.SortableTableRowSorter;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;

public class FilteredTreeViewTable<C, E> extends JTreeViewTable<E> implements FilterHandler
{

    private static final TreeView<Object> SEARCH_VIEW = new TreeView<>()
    {

        @Override
        public String getViewName()
        {
            // TODO: use localized string
            return "Search";
        }

        @Override
        public List<TreeViewPath<Object>> getPaths(Object pobj)
        {
            return Collections.singletonList(new TreeViewPath<>(pobj));
        }

    };
    private boolean searchMode = false;
    private DisplayableFilter<C, E> filter = null;
    private FilteredTreeViewModel<C, E> filteredModel = null;
    private C context = null;
    private TreeView<? super E> tempView;

    public FilteredTreeViewTable()
    {
        RowSorter<SortableTableModel> rowSorter = new SortableTableRowSorter()
        {

            @Override
            public SortableTableModel getModel()
            {
                return (SortableTableModel) FilteredTreeViewTable.this.getModel();
            }

        };
        setRowSorter(rowSorter);
        rowSorter.toggleSortOrder(0);
    }

    @Override
    public void refilter()
    {
        filteredModel.refilter();
        updateDisplay();
    }

    @Override
    public void scrollToTop()
    {
        this.scrollRectToVisible(new Rectangle(getCellRect(0, 0, true)));
    }

    public void setContext(C context)
    {
        this.context = context;
        if (filteredModel != null)
        {
            filteredModel.setContext(context);
        }
    }

    public void setDisplayableFilter(DisplayableFilter<C, E> filter)
    {
        if (this.filter != null)
        {
            this.filter.setFilterHandler(null);
        }
        this.filter = filter;
        filter.setFilterHandler(this);
        if (filteredModel != null)
        {
            filteredModel.setFilter(filter);
        }
    }

    @Override
    public void setTreeViewModel(TreeViewModel<E> viewModel)
    {
        FilteredTreeViewModel<C, E> oldModel = filteredModel;
        filteredModel = new FilteredTreeViewModel<>();
        filteredModel.setBaseModel(viewModel);
        if (filter != null)
        {
            filteredModel.setFilter(filter);
            filteredModel.setContext(context);
        }
        super.setTreeViewModel(filteredModel);
        if (oldModel != null)
        {
            oldModel.setBaseModel(null);
        }
        sortModel();
    }

    @Override
    public void setSearchEnabled(boolean searchMode)
    {
        if (this.searchMode != searchMode)
        {
            this.searchMode = searchMode;
            if (treetableModel != null)
            {
                if (searchMode)
                {
                    tempView = treetableModel.getSelectedTreeView();
                    setTreeView(SEARCH_VIEW);
                    cornerPopupMenu.setTreeViewsEnabled(false);
                } else
                {
                    setTreeView(tempView);
                    cornerPopupMenu.setTreeViewsEnabled(true);
                }
            }
        }
    }

    @Override
    public void refreshModelData()
    {
        super.refreshModelData();
        refilter();
    }

}
