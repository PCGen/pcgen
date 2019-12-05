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
package pcgen.facade.util;

import java.util.Arrays;
import java.util.Comparator;

import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.util.Logging;

import org.apache.commons.lang3.ArrayUtils;

public class SortedListFacade<E> extends AbstractListFacade<E> implements ListListener<E>
{

    private ListFacade<E> delegate = null;
    private Comparator<? super E> comparator;
    private final Comparator<Integer> indexComparator = new Comparator<>()
    {

        @Override
        public int compare(Integer o1, Integer o2)
        {
            E e1 = delegate.getElementAt(o1);
            E e2 = delegate.getElementAt(o2);
            return comparator.compare(e1, e2);
        }

    };

    public SortedListFacade(Comparator<? super E> comparator)
    {
        this.comparator = comparator;
    }

    public SortedListFacade(Comparator<? super E> comparator, ListFacade<E> list)
    {
        this.comparator = comparator;
        setDelegate(list);
    }

    private Integer[] transform = null;

    @Override
    public int getSize()
    {
        return delegate.getSize();
    }

    @Override
    public E getElementAt(int index)
    {
        return delegate.getElementAt(transform[index]);
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
        elementsChanged(null);
    }

    public void setComparator(Comparator<? super E> comparator)
    {
        this.comparator = comparator;
        elementsChanged(null);
    }

    @Override
    public void elementAdded(ListEvent<E> e)
    {
        transform = ArrayUtils.add(transform, transform.length);
        sanityCheck();
        Arrays.sort(transform, indexComparator);
        int index = Arrays.binarySearch(transform, e.getIndex(), indexComparator);
        fireElementAdded(this, e.getElement(), index);
    }

    @Override
    public void elementRemoved(ListEvent<E> e)
    {
        int index = ArrayUtils.indexOf(transform, e.getIndex());
        transform = ArrayUtils.removeElement(transform, transform.length - 1);
        sanityCheck();
        Arrays.sort(transform, indexComparator);
        fireElementRemoved(this, e.getElement(), index);
    }

    @Override
    public void elementsChanged(ListEvent<E> e)
    {
        transform = new Integer[delegate.getSize()];
        for (int i = 0;i < transform.length;i++)
        {
            transform[i] = i;
        }
        sanityCheck();
        Arrays.sort(transform, indexComparator);
        fireElementsChanged(this);
    }

    @Override
    public void elementModified(ListEvent<E> e)
    {
        sanityCheck();
        int index = Arrays.binarySearch(transform, e.getIndex(), indexComparator);
        fireElementModified(this, e.getElement(), index);
    }

    private boolean sanityCheck()
    {
        if (delegate.getSize() != transform.length)
        {
            String msg = String.format(
                    "Mismatched sizes between sorted facade %d and base list %d. " + "Delegate is %s. Transform is %s.",
                    transform.length, delegate.getSize(), delegate, transform);
            Logging.errorPrint(msg, new Throwable());
            return false;
        }
        return true;
    }
}
