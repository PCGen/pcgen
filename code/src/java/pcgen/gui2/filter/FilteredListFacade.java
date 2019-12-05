/*
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
 */
package pcgen.gui2.filter;

import java.util.ArrayList;
import java.util.List;

import pcgen.facade.util.AbstractListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;

public class FilteredListFacade<C, E> extends AbstractListFacade<E> implements ListListener<E>
{

    private final List<E> data = new ArrayList<>();
    private ListFacade<E> delegate = null;
    private Filter<? super C, ? super E> filter = null;
    private C context = null;

    @Override
    public E getElementAt(int index)
    {
        return data.get(index);
    }

    @Override
    public int getSize()
    {
        return data.size();
    }

    public void setContext(C context)
    {
        C oldContext = this.context;
        this.context = context;
        if (oldContext != context)
        {
            refilter();
        }
    }

    public void setFilter(Filter<? super C, ? super E> filter)
    {
        Filter<? super C, ? super E> oldFilter = this.filter;
        this.filter = filter;
        if (oldFilter != filter)
        {
            refilter();
        }
    }

    public void setDelegate(ListFacade<E> list)
    {
        ListFacade<E> oldList = this.delegate;
        if (oldList != null)
        {
            oldList.removeListListener(this);
        }
        this.delegate = list;
        if (list != null)
        {
            list.addListListener(this);
        }
        refilter();
    }

    public void refilter()
    {
        data.clear();
        if (delegate != null)
        {
            List<E> list = new ArrayList<>(delegate.getSize());
            for (E element : delegate)
            {
                if (filter == null || filter.accept(context, element))
                {
                    list.add(element);
                }
            }
            data.addAll(list);
        }
        fireElementsChanged(this);
    }

    @Override
    public void elementAdded(ListEvent<E> e)
    {
        if (filter == null || filter.accept(context, e.getElement()))
        {
            int size = data.size();
            data.add(e.getElement());
            fireElementAdded(this, e.getElement(), size);
        }
    }

    @Override
    public void elementRemoved(ListEvent<E> e)
    {
        int index = data.indexOf(e.getElement());
        data.remove(e.getElement());
        fireElementRemoved(this, e.getElement(), index);
    }

    @Override
    public void elementsChanged(ListEvent<E> e)
    {
        refilter();
    }

    @Override
    public void elementModified(ListEvent<E> e)
    {
        if (data.contains(e.getElement()))
        {
            if (filter != null && !filter.accept(context, e.getElement()))
            {
                elementRemoved(e);
            }
        } else
        {
            elementAdded(e);
        }

    }

}
