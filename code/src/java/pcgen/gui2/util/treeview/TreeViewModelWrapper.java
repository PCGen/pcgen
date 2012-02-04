/*
 * TreeViewModelWrapper.java
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Jul 2, 2008, 4:13:52 PM
 */
package pcgen.gui2.util.treeview;

import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.core.facade.util.ListFacades;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class TreeViewModelWrapper<E> implements TreeViewModel<E>
{

    protected final TreeViewModel<E> treeviewModel;
    protected final DefaultListFacade<E> dataModel;

    public TreeViewModelWrapper(TreeViewModel<E> treeviewModel)
    {
        this(treeviewModel, new DefaultListFacade<E>());
		dataModel.setContents(ListFacades.wrap(treeviewModel.getDataModel()));
    }

    public TreeViewModelWrapper(TreeViewModel<E> treeviewModel,
                                 DefaultListFacade<E> dataModel)
    {
        this.treeviewModel = treeviewModel;
        this.dataModel = dataModel;
    }

    public ListFacade<? extends TreeView<E>> getTreeViews()
    {
        return treeviewModel.getTreeViews();
    }

    public int getDefaultTreeViewIndex()
    {
        return treeviewModel.getDefaultTreeViewIndex();
    }

    public DataView<E> getDataView()
    {
        return treeviewModel.getDataView();
    }

    public DefaultListFacade<E> getDataModel()
    {
        return dataModel;
    }

}
