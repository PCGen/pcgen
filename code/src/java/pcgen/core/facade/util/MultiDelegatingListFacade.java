/*
 * MultiDelegatingListFacade.java
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
 * Created on Apr 30, 2010, 4:26:19 PM
 */
package pcgen.core.facade.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class MultiDelegatingListFacade<E> extends AbstractListFacade<E>
{

	private List<E> elementList = new ArrayList<E>();
	private Map<ListFacade<E>, DelegateListener> delegateMap = new HashMap<ListFacade<E>, DelegateListener>();

	public void addDelegate(ListFacade<E> delegate)
	{
		DelegateListener listener = new DelegateListener();
		delegate.addListListener(listener);
		delegateMap.put(delegate, listener);

		elementList.addAll(ListFacades.wrap(delegate));
		fireElementsChanged(this);
	}

	public void removeDelegate(ListFacade<E> delegate)
	{
		DelegateListener listener = delegateMap.remove(delegate);
		if (listener != null)
		{
			delegate.removeListListener(listener);
		}

		elementList.clear();
		for (ListFacade<E> l : delegateMap.keySet())
		{
			elementList.addAll(ListFacades.wrap(l));
		}
		fireElementsChanged(MultiDelegatingListFacade.this);
	}

	private void removeAllDelegates()
	{
		Iterator<ListFacade<E>> iterator = delegateMap.keySet().iterator();
		while (iterator.hasNext())
		{
			ListFacade<E> list = iterator.next();
			list.removeListListener(delegateMap.get(list));
			iterator.remove();
		}
	}

	public void setDelegates(List<ListFacade<E>> delegates)
	{
		removeAllDelegates();
		for (ListFacade<E> delegate : delegates)
		{
			DelegateListener listener = new DelegateListener();
			delegate.addListListener(listener);
			delegateMap.put(delegate, listener);
			elementList.addAll(ListFacades.wrap(delegate));
		}
		fireElementsChanged(this);
	}

	public E getElementAt(int index)
	{
		return elementList.get(index);
	}

	public int getSize()
	{
		return elementList.size();
	}

	private class DelegateListener implements ListListener<E>
	{

		public void elementAdded(ListEvent<E> e)
		{
			E element = e.getElement();
			int index = elementList.size();
			elementList.add(index, element);
			fireElementAdded(MultiDelegatingListFacade.this, element, index);
		}

		public void elementRemoved(ListEvent<E> e)
		{
			E element = e.getElement();
			int index = elementList.indexOf(element);
			if (elementList.remove(element))
			{
				fireElementRemoved(MultiDelegatingListFacade.this, element, index);
			}
		}

		public void elementsChanged(ListEvent<E> e)
		{
			elementList.clear();
			for (ListFacade<E> l : delegateMap.keySet())
			{
				elementList.addAll(ListFacades.wrap(l));
			}
			fireElementsChanged(MultiDelegatingListFacade.this);
		}

	}

}
