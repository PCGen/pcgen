/*
 * Copyright 2018 (C) Thomas Parker
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.gui2.facade;

import java.util.Objects;

import pcgen.facade.util.AbstractListFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;

/**
 * A DelegatingSingleton is wraps a ReferenceFacade to allow that singleton object
 * reference to instead appear as a ListFacade.
 * <p>
 * This class is useful because certain items which are recognized by most game modes as a
 * singleton (Race) are nonetheless stored and displayed within list structures in PCGen.
 * This centralizes the decoration of the singleton into a list.
 *
 * @param <E> The type of object stored in this DelegatingSingleton
 */
public class DelegatingSingleton<E> extends AbstractListFacade<E>
        implements ReferenceListener<E>
{

    /**
     * The underlying ReferenceFacade for this DelegatingSingleton.
     */
    private final ReferenceFacade<E> underlying;

    /**
     * Constructs a new DelegatingSingleton given the underlying ReferenceFacade.
     *
     * @param underlying The ReferenceFacade for this DelegatingSingleton
     */
    public DelegatingSingleton(ReferenceFacade<E> underlying)
    {
        this.underlying = Objects.requireNonNull(underlying);
        underlying.addReferenceListener(this);
    }

    @Override
    public E getElementAt(int index)
    {
        if (index != 0)
        {
            throw new IndexOutOfBoundsException(
                    "Index: " + index + ", Size: " + getSize());
        }
        E item = underlying.get();
        if (item == null)
        {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + 0);
        }
        return item;
    }

    @Override
    public int getSize()
    {
        return (underlying.get() == null) ? 0 : 1;
    }

    @Override
    public boolean containsElement(E element)
    {
        E item = underlying.get();
        return (item != null) && item.equals(element);
    }

    @Override
    public void referenceChanged(ReferenceEvent<E> e)
    {
        E oldRef = e.getOldReference();
        E newRef = e.getNewReference();
        if (oldRef != null)
        {
            fireElementRemoved(this, oldRef, 0);
        }
        if (newRef != null)
        {
            fireElementAdded(this, newRef, 0);
        }
    }
}

