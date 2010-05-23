/*
 * Copyright (c) Thomas Parker, 2009.
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
package pcgen.cdom.facet;

import javax.swing.event.EventListenerList;

import pcgen.cdom.enumeration.CharID;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A AbstractDataFacet is a DataFacet that contains information about
 * CDOMObjects that are contained in a PlayerCharacter. This serves the basic
 * functions of managing the DataFacetChangeListeners for a DataFacet.
 */
public abstract class AbstractDataFacet<T>
{
	/**
	 * The listeners to which DataFacetChangeEvents will be fired when a change
	 * in the source DataFacet occurs.
	 */
	private final EventListenerList listenerList = new EventListenerList();

	/**
	 * Adds a new DataFacetChangeListener to receive DataFacetChangeEvents
	 * (EdgeChangeEvent and NodeChangeEvent) from the source DataFacet.
	 * 
	 * @param listener
	 *            The DataFacetChangeListener to receive DataFacetChangeEvents
	 */
	public void addDataFacetChangeListener(
			DataFacetChangeListener<? super T> listener)
	{
		listenerList.add(DataFacetChangeListener.class, listener);
	}

	/**
	 * Returns an Array of DataFacetChangeListeners receiving
	 * DataFacetChangeEvents from the source DataFacet.
	 * 
	 * Ownership of the returned Array is transferred to the calling Object. No
	 * reference to the Array is maintained by DataFacetChangeSupport. However,
	 * the DataFacetChangeListeners contained in the Array are (obviously!)
	 * returned BY REFERENCE, and care should be taken with modifying those
	 * DataFacetChangeListeners.*
	 * 
	 * @return An Array of DataFacetChangeListeners receiving
	 *         DataFacetChangeEvents from the source DataFacet
	 */
	public synchronized DataFacetChangeListener<? super T>[] getDataFacetChangeListeners()
	{
		return listenerList.getListeners(DataFacetChangeListener.class);
	}

	/**
	 * Removes a DataFacetChangeListener so that it will no longer receive
	 * DataFacetChangeEvents from the source DataFacet.
	 * 
	 * @param listener
	 *            The DataFacetChangeListener to be removed
	 */
	public void removeDataFacetChangeListener(
			DataFacetChangeListener<? super T> listener)
	{
		listenerList.remove(DataFacetChangeListener.class, listener);
	}

	/**
	 * Sends a NodeChangeEvent to the DataFacetChangeListeners that are
	 * receiving DataFacetChangeEvents from the source DataFacet.
	 * 
	 * @param node
	 *            The Node that has beed added to or removed from the source
	 *            DataFacet
	 * @param type
	 *            An identifier indicating whether the given CDOMObject was
	 *            added to or removed from the source DataFacet
	 */
	protected void fireDataFacetChangeEvent(CharID id, T node, int type)
	{
		DataFacetChangeListener<T>[] listeners = listenerList
				.getListeners(DataFacetChangeListener.class);
		/*
		 * This list is decremented from the end of the list to the beginning in
		 * order to maintain consistent operation with how Java AWT and Swing
		 * listeners are notified of Events (they are in reverse order to how
		 * they were added to the Event-owning object).
		 */
		DataFacetChangeEvent<T> ccEvent = null;
		for (int i = listeners.length - 1; i >= 0; i--)
		{
			// Lazily create event
			if (ccEvent == null)
			{
				ccEvent = new DataFacetChangeEvent<T>(id, node, type);
			}
			switch (ccEvent.getEventType())
			{
			case DataFacetChangeEvent.DATA_ADDED:
				listeners[i].dataAdded(ccEvent);
				break;
			case DataFacetChangeEvent.DATA_REMOVED:
				listeners[i].dataRemoved(ccEvent);
				break;
			default:
				break;
			}
		}
	}
}
