/*
 * Copyright 2018 (C) Tom Parker <thpr@sourceforge.net>
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
package pcgen.facade.util;

import javax.swing.event.EventListenerList;

import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;

/**
 * An AbstractReferenceFacade is a shared framework for classes that implement
 * WriteableReferenceFacade, in order to provide a convenient method of dealing with
 * ReferenceListeners and firing change events.
 *
 * @param <T>
 *            The Type of object stored by this AbstractReferenceFacade
 */
public abstract class AbstractReferenceFacade<T> implements ReferenceFacade<T>
{

	/**
	 * The underlying EventListenerList to receive events when the reference underlying
	 * this AbstractReferenceFacade changes.
	 */
	private final EventListenerList listenerList = new EventListenerList();

	@Override
	public void addReferenceListener(ReferenceListener<? super T> listener)
	{
		listenerList.add(ReferenceListener.class, listener);
	}

	@Override
	public void removeReferenceListener(ReferenceListener<? super T> listener)
	{
		listenerList.remove(ReferenceListener.class, listener);
	}

	/**
	 * Fires that a reference has changed. Should only be called by the class extending
	 * this class.
	 * 
	 * @param source
	 *            The source of the event
	 * @param old
	 *            The old value of the reference underlying this AbstractReferenceFacade
	 * @param newer
	 *            The new value of the reference underlying this AbstractReferenceFacade
	 */
	protected void fireReferenceChangedEvent(Object source, T old, T newer)
	{
		Object[] listeners = listenerList.getListenerList();
		ReferenceEvent<T> e = null;
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
}
