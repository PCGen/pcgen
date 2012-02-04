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

import java.util.Map;
import java.util.TreeMap;

import pcgen.cdom.base.Category;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.Nature;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A AbstractDataFacet is a DataFacet that contains information about
 * CDOMObjects that are contained in a PlayerCharacter. This serves the basic
 * functions of managing the DataFacetChangeListeners for a DataFacet.
 */
public abstract class AbstractDataFacet<T>
{
	private final Map<Integer, DataFacetChangeListener<? super T>[]> listeners = new TreeMap<Integer, DataFacetChangeListener<? super T>[]>();

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
		addDataFacetChangeListener(0, listener);
	}

	public void addDataFacetChangeListener(int priority,
			DataFacetChangeListener<? super T> listener)
	{
		DataFacetChangeListener<? super T>[] dfcl = listeners.get(priority);
		int newSize = (dfcl == null) ? 1 : (dfcl.length + 1);
		DataFacetChangeListener<? super T>[] newArray = new DataFacetChangeListener[newSize];
		if (dfcl != null)
		{
			System.arraycopy(dfcl, 0, newArray, 1, dfcl.length);
		}
		newArray[0] = listener;
		listeners.put(priority, newArray);
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
		removeDataFacetChangeListener(0, listener);
	}

	public void removeDataFacetChangeListener(int priority,
			DataFacetChangeListener<? super T> listener)
	{
		DataFacetChangeListener<? super T>[] dfcl = listeners.get(priority);
		if (dfcl == null)
		{
			// No worries
			return;
		}
		int foundLoc = -1;
		int newSize = dfcl.length - 1;
		for (int i = newSize; i >= 0; i--)
		{
			if (dfcl[i] == listener)
			{
				foundLoc = i;
				break;
			}
		}
		if (foundLoc != -1)
		{
			if (dfcl.length == 1)
			{
				listeners.remove(priority);
			}
			else
			{
				DataFacetChangeListener<? super T>[] newArray = new DataFacetChangeListener[newSize];
				if (foundLoc != 0)
				{
					System.arraycopy(dfcl, 0, newArray, 0, foundLoc);
				}
				if (foundLoc != newSize)
				{
					System.arraycopy(dfcl, foundLoc + 1, newArray,
							foundLoc, newSize - foundLoc);
				}
				listeners.put(priority, newArray);
			}
		}
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
		fireDataFacetChangeEvent(id, node, type, null, null);
	}

	/**
	 * Sends a NodeChangeEvent to the DataFacetChangeListeners that are
	 * receiving DataFacetChangeEvents from the source DataFacet.
	 * @param id 
	 * 
	 * @param node
	 *            The Node that has beed added to or removed from the source
	 *            DataFacet
	 * @param type
	 *            An identifier indicating whether the given CDOMObject was
	 *            added to or removed from the source DataFacet
	 * @param category 
	 * 		      The category *e.g. AbilityCategory in which the node has been changed. 
	 * @param nature 
	 * 		      The optional nature in which the node has been changed. 
	 */
	@SuppressWarnings("rawtypes")
	protected void fireDataFacetChangeEvent(CharID id, T node, int type, Category category, Nature nature)
	{
		for (DataFacetChangeListener<? super T>[] dfclArray : listeners
				.values())
		{
			/*
			 * This list is decremented from the end of the list to the
			 * beginning in order to maintain consistent operation with how Java
			 * AWT and Swing listeners are notified of Events (they are in
			 * reverse order to how they were added to the Event-owning object).
			 * This is obviously subordinate to the priority (loop above).
			 */
			DataFacetChangeEvent<T> ccEvent = null;
			for (int i = dfclArray.length - 1; i >= 0; i--)
			{
				// Lazily create event
				if (ccEvent == null)
				{
					if (category == null)
					{
						ccEvent = new DataFacetChangeEvent<T>(id, node, this, type);
					}
					else
					{
						ccEvent = new CategorizedDataFacetChangeEvent<T>(id, node, this, type, category, nature);
					}
				}
				DataFacetChangeListener dfcl = dfclArray[i];
				switch (ccEvent.getEventType())
				{
				case DataFacetChangeEvent.DATA_ADDED:
					dfcl.dataAdded(ccEvent);
					break;
				case DataFacetChangeEvent.DATA_REMOVED:
					dfcl.dataRemoved(ccEvent);
					break;
				default:
					break;
				}
			}
		}
	}
}
