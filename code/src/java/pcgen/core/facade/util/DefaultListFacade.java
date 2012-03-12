/*
 * DefaultListFacade.java
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
 * Created on Apr 25, 2010, 3:51:05 PM
 */
package pcgen.core.facade.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class DefaultListFacade<E> extends AbstractListFacade<E>
{

	private ArrayList<E> elementList;

	public DefaultListFacade()
	{
		elementList = new ArrayList<E>();
	}

	public DefaultListFacade(Collection<? extends E> elements)
	{
		elementList = new ArrayList<E>(elements);
	}

	@Override
	public Iterator<E> iterator()
	{
		return new Iterator<E>()
		{

			private ListIterator<E> iterator = elementList.listIterator();
			private int index;

			public boolean hasNext()
			{
				return iterator.hasNext();
			}

			public E next()
			{
				index = iterator.nextIndex();
				return iterator.next();
			}

			public void remove()
			{
				E element = getElementAt(index);
				iterator.remove();
				fireElementRemoved(this, element, index);
			}

		};
	}

	public E getElementAt(int index)
	{
		return elementList.get(index);
	}

	public int getSize()
	{
		return elementList.size();
	}

	public boolean containsElement(E element)
	{
		return elementList.contains(element);
	}

	public int getIndexOfElement(E element)
	{
		return elementList.indexOf(element);
	}

	public void addElement(E element)
	{
		addElement(elementList.size(), element);
	}

	public void addElement(int index, E element)
	{
		elementList.add(index, element);
		fireElementAdded(this, element, index);
	}

	public void removeElement(E element)
	{
		int index = elementList.indexOf(element);
		if (elementList.remove(element))
		{
			fireElementRemoved(this, element, index);
		}
	}

	public void removeElement(int index)
	{
		fireElementRemoved(this, elementList.remove(index), index);
	}

	/**
	 * Signal that an element in the list has been modified in some way that
	 * subscribers to the list need to know about. This will advise all 
	 * capable subscribers to the list of the change. 
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
		elementList.clear();
		fireElementsChanged(this);
	}

	/**
	 * @return A copy of the contents of the list.
	 */
	public List<E> getContents()
	{
		return new ArrayList<E>(elementList);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return String.valueOf(elementList);
	}

}
