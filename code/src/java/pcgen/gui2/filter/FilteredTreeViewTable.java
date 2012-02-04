/*
 * FilteredTreeViewTable.java
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
 * Created on May 15, 2010, 7:42:03 PM
 */
package pcgen.gui2.filter;

import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import pcgen.gui2.util.JTreeViewTable;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class FilteredTreeViewTable<C, E> extends JTreeViewTable<E> implements FilterHandler
{

	private static final TreeView<Object> searchView = new TreeView<Object>()
	{

		public String getViewName()
		{
			// TODO: use localized string
			return "Search";
		}

		public List<TreeViewPath<Object>> getPaths(Object pobj)
		{
			return Collections.singletonList(new TreeViewPath<Object>(pobj));
		}

	};
	private boolean searchMode = false;
	private DisplayableFilter filter = null;
	private FilteredTreeViewModel<C, E> filteredModel = null;
	private C context = null;
	private TreeView tempView;

	public FilteredTreeViewTable()
	{
		setTableHeader(new FilteredTreeViewHeader());
	}

	public void refilter()
	{
		filteredModel.refilter();
		sortModel();
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

	public void setTreeViewModel(TreeViewModel<E> viewModel)
	{
		filteredModel = new FilteredTreeViewModel<C, E>();
		filteredModel.setBaseModel(viewModel);
		if (filter != null)
		{
			filteredModel.setFilter(filter);
			filteredModel.setContext(context);
		}
		super.setTreeViewModel(filteredModel);
	}

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
					setTreeView(searchView);
				}
				else
				{
					setTreeView(tempView);
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

	public boolean isSearchEnabled()
	{
		return searchMode;
	}

	private class FilteredTreeViewHeader extends JTreeViewHeader
	{

		@Override
		protected void maybeShowPopup(MouseEvent e)
		{
			if (!searchMode)
			{
				super.maybeShowPopup(e);
			}
		}

	}

}
