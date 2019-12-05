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
package pcgen.cdom.facet.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import pcgen.base.util.ArrayUtilities;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.PCGenIdentifier;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.facet.CategorizedDataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;

/**
 * A AbstractDataFacet is a DataFacet that contains information about
 * CDOMObjects that are contained in a resource. This serves the basic functions
 * of managing the DataFacetChangeListeners for a DataFacet.
 * <p>
 * Note that DataFacetChangeListeners registered with the AbstractDataFacet
 * through the methods in AbstractDataFacet will receive events from the
 * AbstractDataFacet in the order of the priority given during their
 * registration. DataFacetChangeListeners with a lower priority (starting with
 * Integer.MIN_VALUE) will receive events first. All DataFacetChangeListeners at
 * a given priority will receive events before DataFacetChangeListeners at a
 * higher priority.
 * <p>
 * Note also that AbstractDataFacet makes no guarantees as to the order in which
 * DataFacetChangeListners of the <b>same</b> priority will receive events from
 * the AbstractDataFacet.
 *
 * @param <IDT> The Type of identifier used in this AbstractDataFacet
 * @param <T>   The Type of object stored in this AbstractDataFacet
 */
public abstract class AbstractDataFacet<IDT extends PCGenIdentifier, T> extends AbstractStorageFacet<IDT>
{
    private final Map<Integer, DataFacetChangeListener<IDT, ? super T>[]> listeners = new TreeMap<>();

    /**
     * Adds a new DataFacetChangeListener to receive DataFacetChangeEvents
     * (EdgeChangeEvent and NodeChangeEvent) from the source DataFacet. The
     * given DataFacetChangeListener is added at the default priority (zero).
     * <p>
     * Note that the DataFacetChangeListeners are a list, meaning a given
     * DataFacetChangeListener can be added more than once at a given priority,
     * and if that occurs, it must be removed an equivalent number of times in
     * order to no longer receive events from this AbstractDataFacet.
     *
     * @param listener The DataFacetChangeListener to receive DataFacetChangeEvents
     *                 from this AbstractDataFacet
     */
    public void addDataFacetChangeListener(DataFacetChangeListener<IDT, ? super T> listener)
    {
        addDataFacetChangeListener(0, listener);
    }

    /**
     * Adds a new DataFacetChangeListener to receive DataFacetChangeEvents
     * (EdgeChangeEvent and NodeChangeEvent) from the source DataFacet.
     * <p>
     * The DataFacetChangeListener is added at the given priority.
     * <p>
     * Note that the DataFacetChangeListeners are a list, meaning a given
     * DataFacetChangeListener can be added more than once at a given priority,
     * and if that occurs, it must be removed an equivalent number of times in
     * order to no longer receive events from this AbstractDataFacet.
     *
     * @param priority The lower the priority the earlier in the list the new
     *                 listener will get advised of the change.
     * @param listener The DataFacetChangeListener to receive DataFacetChangeEvents
     *                 from this AbstractDataFacet
     */
    @SuppressWarnings("unchecked")
    public void addDataFacetChangeListener(int priority, DataFacetChangeListener<IDT, ? super T> listener)
    {
        DataFacetChangeListener<IDT, ? super T>[] dfcl = listeners.get(priority);
        dfcl = Optional.ofNullable(dfcl).orElse(new DataFacetChangeListener[0]);
        listeners.put(priority, ArrayUtilities.prependOnCopy(listener, dfcl,
                DataFacetChangeListener.class));
    }

    /**
     * Removes a DataFacetChangeListener so that it will no longer receive
     * DataFacetChangeEvents from the source DataFacet. This will remove the
     * data facet change listener from the default priority (zero).
     * <p>
     * Note that if the given DataFacetChangeListener has been registered under
     * a different priority, it will still receive events at that priority
     * level.
     *
     * @param listener The DataFacetChangeListener to be removed
     */
    public void removeDataFacetChangeListener(DataFacetChangeListener<IDT, ? super T> listener)
    {
        removeDataFacetChangeListener(0, listener);
    }

    /**
     * Removes a DataFacetChangeListener so that it will no longer receive
     * DataFacetChangeEvents from the source DataFacet. This will remove the
     * data facet change listener from the given priority.
     * <p>
     * Note that if the given DataFacetChangeListener has been registered under
     * a different priority, it will still receive events at that priority
     * level.
     *
     * @param listener The DataFacetChangeListener to be removed
     */
    public void removeDataFacetChangeListener(int priority, DataFacetChangeListener<IDT, ? super T> listener)
    {
        DataFacetChangeListener<IDT, ? super T>[] dfcl = listeners.get(priority);
        if (dfcl == null)
        {
            // No worries
            return;
        }
        int foundLoc = -1;
        int newSize = dfcl.length - 1;
        for (int i = newSize;i >= 0;i--)
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
            } else
            {
                DataFacetChangeListener<IDT, ? super T>[] newArray = new DataFacetChangeListener[newSize];
                if (foundLoc != 0)
                {
                    System.arraycopy(dfcl, 0, newArray, 0, foundLoc);
                }
                if (foundLoc != newSize)
                {
                    System.arraycopy(dfcl, foundLoc + 1, newArray, foundLoc, newSize - foundLoc);
                }
                listeners.put(priority, newArray);
            }
        }
    }

    /**
     * Sends a NodeChangeEvent to the DataFacetChangeListeners that are
     * receiving DataFacetChangeEvents from the source DataFacet.
     *
     * @param id   The PCGenIdentifier identifying the resource to which the
     *             NodeChangeEvent relates.
     * @param node The Node that has been added to or removed from the source
     *             DataFacet for the given PCGenIdentifier
     * @param type An identifier indicating whether the given CDOMObject was
     *             added to or removed from the source DataFacet
     */
    protected void fireDataFacetChangeEvent(IDT id, T node, int type)
    {
        fireDataFacetChangeEvent(id, node, type, null, null);
    }

    /**
     * Sends a NodeChangeEvent to the DataFacetChangeListeners that are
     * receiving DataFacetChangeEvents from the source DataFacet.
     *
     * @param id       The PCGenIdentifier identifying the resource to which the
     *                 NodeChangeEvent relates.
     * @param node     The Node that has been added to or removed from the source
     *                 DataFacet for the given PCGenIdentifier
     * @param type     An identifier indicating whether the given CDOMObject was
     *                 added to or removed from the source DataFacet
     * @param category The category (e.g. AbilityCategory) in which the node has been
     *                 changed.
     * @param nature   The optional nature in which the node has been changed.
     */
    @SuppressWarnings("rawtypes")
    protected void fireDataFacetChangeEvent(IDT id, T node, int type, Category category, Nature nature)
    {
        for (DataFacetChangeListener<IDT, ? super T>[] dfclArray : listeners.values())
        {
            /*
             * This list is decremented from the end of the list to the
             * beginning in order to maintain consistent operation with how Java
             * AWT and Swing listeners are notified of Events (they are in
             * reverse order to how they were added to the Event-owning object).
             * This is obviously subordinate to the priority (loop above).
             */
            DataFacetChangeEvent<IDT, T> ccEvent = null;
            for (int i = dfclArray.length - 1;i >= 0;i--)
            {
                // Lazily create event
                if (ccEvent == null)
                {
                    if (category == null)
                    {
                        ccEvent = new DataFacetChangeEvent<>(id, node, this, type);
                    } else
                    {
                        ccEvent = new CategorizedDataFacetChangeEvent<>(id, node, this, type, category, nature);
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

    public DataFacetChangeListener<IDT, ? super T>[] getDataFacetChangeListeners()
    {
        List<DataFacetChangeListener<IDT, ? super T>> list = new ArrayList<>();
        for (DataFacetChangeListener<IDT, ? super T>[] dfclArray : listeners.values())
        {
            Collections.addAll(list, dfclArray);
        }
        return list.toArray(new DataFacetChangeListener[0]);
    }
}
