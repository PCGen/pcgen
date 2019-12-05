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

/**
 * An AbstractAdapter is a framework object used to implement a WriteableReferenceFacade.
 *
 * @param <T> The format of the data maintained by this AbstractAdapter
 */
public class AbstractAdapter<T>
{
    /**
     * The list of listeners that listen to this VariableChannel for
     * ReferenceEvents.
     */
    private final EventListenerList listenerList = new EventListenerList();

    /**
     * Adds a ReferenceListener to this AbstractAdapter.
     *
     * @param listener The ReferenceListener to be added to this AbstractAdapter
     */
    public void addReferenceListener(ReferenceListener<? super T> listener)
    {
        listenerList.add(ReferenceListener.class, listener);
    }

    /**
     * Removes a ReferenceListener from this AbstractAdapter.
     *
     * @param listener The ReferenceListener to be removed from this AbstractAdapter
     */
    public void removeReferenceListener(ReferenceListener<? super T> listener)
    {
        listenerList.remove(ReferenceListener.class, listener);
    }

    protected void fireReferenceChangedEvent(Object source, T oldValue, T newValue)
    {
        Object[] listeners = listenerList.getListenerList();
        ReferenceEvent<T> e = null;
        for (int i = listeners.length - 2;i >= 0;i -= 2)
        {
            if (listeners[i] == ReferenceListener.class)
            {
                if (e == null)
                {
                    e = new ReferenceEvent<>(source, oldValue, newValue);
                }
                @SuppressWarnings("unchecked")
                ReferenceListener<T> referenceListener = (ReferenceListener<T>) listeners[i + 1];
                referenceListener.referenceChanged(e);
            }
        }
    }

}
