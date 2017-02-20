/*
 * DefaultReferenceFacade.java
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

import javax.swing.event.EventListenerList;

import org.apache.commons.lang3.ObjectUtils;

import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;

/**
 *
 */
public class DefaultReferenceFacade<E> implements WriteableReferenceFacade<E>
{

	private EventListenerList listenerList = new EventListenerList();
	protected E object;

	public DefaultReferenceFacade()
	{
		this(null);
	}

	public DefaultReferenceFacade(E object)
	{
		this.object = object;
	}

    @Override
	public void addReferenceListener(ReferenceListener<? super E> listener)
	{
		listenerList.add(ReferenceListener.class, listener);
	}

    @Override
	public void removeReferenceListener(ReferenceListener<? super E> listener)
	{
		listenerList.remove(ReferenceListener.class, listener);
	}

    @Override
	public E get()
	{
		return object;
	}

	public void set(E object)
	{
		if (ObjectUtils.equals(this.object, object))
		{
			return;
		}
		E old = this.object;
		this.object = object;
		fireReferenceChangedEvent(this, old, object);
	}

	protected void fireReferenceChangedEvent(Object source, E old, E newer)
	{
		Object[] listeners = listenerList.getListenerList();
		ReferenceEvent<E> e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == ReferenceListener.class)
			{
				if (e == null)
				{
					e = new ReferenceEvent<>(source, old, newer);
				}
				((ReferenceListener) listeners[i + 1]).referenceChanged(e);
			}
		}
	}

	@Override
	public String toString()
	{
		return String.valueOf(object);
	}
}
