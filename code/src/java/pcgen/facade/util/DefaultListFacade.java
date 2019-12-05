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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class DefaultListFacade<E> extends AbstractListFacade<E>
        implements WriteableListFacade<E>
{

    private final ArrayList<E> elementList;

    public DefaultListFacade()
    {
        elementList = new ArrayList<>();
    }

    public DefaultListFacade(Collection<? extends E> elements)
    {
        elementList = new ArrayList<>(elements);
    }

    @Override
    public Iterator<E> iterator()
    {
        return new Iterator<>()
        {

            private final ListIterator<E> iterator = elementList.listIterator();
            private int index;

            @Override
            public boolean hasNext()
            {
                return iterator.hasNext();
            }

            @Override
            public E next()
            {
                index = iterator.nextIndex();
                return iterator.next();
            }

            @Override
            public void remove()
            {
                E element = getElementAt(index);
                iterator.remove();
                fireElementRemoved(this, element, index);
            }

        };
    }

    @Override
    public E getElementAt(int index)
    {
        return elementList.get(index);
    }

    @Override
    public int getSize()
    {
        return elementList.size();
    }

    @Override
    public boolean containsElement(E element)
    {
        return elementList.contains(element);
    }

    public int getIndexOfElement(E element)
    {
        return elementList.indexOf(element);
    }

    @Override
    public void addElement(E element)
    {
        addElement(elementList.size(), element);
    }

    @Override
    public void addElement(int index, E element)
    {
        elementList.add(index, element);
        fireElementAdded(this, element, index);
    }

    @Override
    public boolean removeElement(E element)
    {
        int index = elementList.indexOf(element);
        if (elementList.remove(element))
        {
            fireElementRemoved(this, element, index);
            return true;
        }
        return false;
    }

    @Override
    public void removeElement(int index)
    {
        fireElementRemoved(this, elementList.remove(index), index);
    }

    /**
     * Signal that an element in the list has been modified in some way that
     * subscribers to the list need to know about. This will advise all
     * capable subscribers to the list of the change.
     *
     * @param element The element that has been modified.
     */
    public void modifyElement(E element)
    {
        int index = getIndexOfElement(element);
        if (index >= 0)
        {
            fireElementModified(this, element, index);
        }
    }

    public void setContents(Collection<? extends E> elements)
    {
        elementList.clear();
        elementList.addAll(elements);
        fireElementsChanged(this);
    }

    public void clearContents()
    {
        if (!isEmpty())
        {
            elementList.clear();
            fireElementsChanged(this);
        }
    }

    /**
     * Makes the contents of this list match the provided list. This is done by making
     * individual add and remove of the elements of this list to make it match. The
     * lists must sorted in the same order for this method to be efficient. A fall back
     * to setContents is made if the current list is empty or there are large size
     * differences.
     *
     * @param newElements The new contents of the list.
     */
    public void updateContents(List<? extends E> newElements)
    {
        final int maxUpdateSize = 20;
        if (isEmpty() || newElements.isEmpty() || Math.abs(getSize() - newElements.size()) > maxUpdateSize)
        {
            setContents(newElements);
            return;
        }

        // Scan for items that need to be removed
        int currPos = 0;
        for (Iterator<E> iterator = elementList.iterator();iterator.hasNext();)
        {
            E e = iterator.next();
            int index = currPos;
            boolean found = false;
            while (index < newElements.size())
            {
                if (e.equals(newElements.get(index)))
                {
                    currPos = index + 1;
                    found = true;
                    break;
                }
                index++;
            }
            if (!found)
            {
                int loc = elementList.indexOf(e);
                iterator.remove();
                fireElementRemoved(this, e, loc);
            }
        }
        currPos = 0;
        for (E e : newElements)
        {
            if (elementList.size() <= currPos || !e.equals(elementList.get(currPos)))
            {
                addElement(currPos, e);
            }
            currPos++;
        }
    }

    /**
     * Makes the contents of this list match the provided list apart from element
     * ordering. This is done by making individual add and remove of the
     * elements of this list to make it match. The lists need not be sorted in
     * the same order for this method to be efficient. A fall back to setContents
     * is made if the current list is empty or there are large size differences.
     *
     * @param newElements The new contents of the list.
     */
    public void updateContentsNoOrder(List<? extends E> newElements)
    {
        final int maxUpdateSize = 20;
        if (isEmpty() || newElements.isEmpty() || Math.abs(getSize() - newElements.size()) > maxUpdateSize)
        {
            setContents(newElements);
            return;
        }

        for (E elem : newElements)
        {
            if (!containsElement(elem))
            {
                addElement(elem);
            }
        }

        for (Iterator<E> iterator = elementList.iterator();iterator.hasNext();)
        {
            E e = iterator.next();
            if (!newElements.contains(e))
            {
                int index = elementList.indexOf(e);
                iterator.remove();
                fireElementRemoved(this, e, index);
            }
        }
    }

    /**
     * @return A copy of the contents of the list.
     */
    public List<E> getContents()
    {
        return new ArrayList<>(elementList);
    }

    @Override
    public String toString()
    {
        return String.valueOf(elementList);
    }

}
