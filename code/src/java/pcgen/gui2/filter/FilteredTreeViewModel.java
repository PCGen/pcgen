/*
 * FilteredTreeViewModel.java
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
 * Created on May 14, 2010, 2:06:44 PM
 */
package pcgen.gui2.filter;

import java.util.ArrayList;
import java.util.List;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.core.facade.util.ListFacades;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class FilteredTreeViewModel<C, E>
		implements TreeViewModel<E>, ListListener<E>
{

	private DefaultListFacade<E> data = new DefaultListFacade<E>();
	private Filter<C, E> filter;
	private TreeViewModel<E> model;
	private C context;

	@Override
	public ListFacade<? extends TreeView<E>> getTreeViews()
	{
		return model.getTreeViews();
	}

	@Override
	public int getDefaultTreeViewIndex()
	{
		return model.getDefaultTreeViewIndex();
	}

	@Override
	public DataView<E> getDataView()
	{
		return model.getDataView();
	}

	@Override
	public ListFacade<E> getDataModel()
	{
		return data;
	}

	public void setBaseModel(TreeViewModel<E> model)
	{
		if(this.model != null)
		{
			this.model.getDataModel().removeListListener(this);
		}
		this.model = model;
		if(this.model != null)
		{
			this.model.getDataModel().addListListener(this);
		}
		data.setContents(ListFacades.wrap(model.getDataModel()));
	}

	public void setContext(C context)
	{
		this.context = context;
		filterCheck();
	}

	public void setFilter(Filter<C, E> filter)
	{
		this.filter = filter;
		filterCheck();
	}

	private void filterCheck()
	{
		if (filter != null && context != null)
		{
			refilter();
		}
	}

	public void refilter()
	{
		ListFacade<E> base = model.getDataModel();
		List<E> list = new ArrayList<E>(base.getSize());
		for (E element : base)
		{
			if (filter.accept(context, element))
			{
				list.add(element);
			}
		}
		data.setContents(list);
	}

	@Override
	public void elementAdded(ListEvent<E> e)
	{
		if (filter.accept(context, e.getElement()))
		{
			data.addElement(e.getElement());
		}
	}

	@Override
	public void elementRemoved(ListEvent<E> e)
	{
		data.removeElement(e.getElement());
	}

	@Override
	public void elementsChanged(ListEvent<E> e)
	{
		refilter();
	}

}
