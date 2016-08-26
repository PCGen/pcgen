/*
 * Copyright (c) Thomas Parker, 2016.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.output.channel.compat;

import javax.swing.event.EventListenerList;

import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;

public class AbstractAdapter<T>
{
	/**
	 * The list of listeners that listen to this VariableChannel for
	 * ReferenceEvents.
	 */
	private final EventListenerList listenerList = new EventListenerList();

	public void addReferenceListener(ReferenceListener<? super T> listener)
	{
		listenerList.add(ReferenceListener.class, listener);
	}

	public void removeReferenceListener(ReferenceListener<? super T> listener)
	{
		listenerList.remove(ReferenceListener.class, listener);
	}

	protected final void fireReferenceChangedEvent(Object source, T oldValue, T newValue)
	{
		Object[] listeners = listenerList.getListenerList();
		ReferenceEvent<T> e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == ReferenceListener.class)
			{
				if (e == null)
				{
					e = new ReferenceEvent<>(source, oldValue, newValue);
				}
				((ReferenceListener) listeners[i + 1]).referenceChanged(e);
			}
		}
	}

}
