/*
 * Copyright (c) Thomas Parker, 2010.
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

import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;

/**
 * CDOMObjectConsolidationFacet consolidates all of the CDOMObjects that are
 * added to a Player Character. By consolidating all of the CDOMObjects into one
 * location, behaviors which are consistent across all CDOMObjects can be
 * performed based on events from a single source Facet.
 * <p>
 * Note: If you attempt to use this class and receive an error that you have
 * created a cycle in the Spring graph of objects, you may use
 * CDOMObjectSourceFacet as the source of events instead of this Facet. This
 * will cause the receiving object to receive the same events, but will not
 * cause a cycle. This is possible because the underlying data store for the two
 * facets is the same.
 *
 * @see pcgen.cdom.facet.CDOMObjectSourceFacet
 * @see pcgen.cdom.facet.CDOMObjectBridge
 */
public class CDOMObjectConsolidationFacet implements DataFacetChangeListener<CharID, CDOMObject>
{
    private CDOMObjectBridge bridgeFacet;

    public void setBridgeFacet(CDOMObjectBridge bridge)
    {
        bridgeFacet = bridge;
    }

    /**
     * Add the given object with the given source to the list of objects stored
     * in this CDOMObjectConsolidationFacet for the Player Character represented
     * by the given CharID.
     *
     * @param id     The CharID representing the Player Character for which the
     *               given item should be added
     * @param obj    The object to be added to the list of objects stored in this
     *               CDOMObjectConsolidationFacet for the Player Character
     *               represented by the given CharID
     * @param source The source for the given object
     */
    public void add(CharID id, CDOMObject obj, Object source)
    {
        bridgeFacet.add(id, obj, source);
    }

    /**
     * Removes the given source entry from the list of sources for the given
     * object stored in this CDOMObjectConsolidationFacet for the Player
     * Character represented by the given CharID. If the given source was the
     * only source for the given object, then the object is removed from the
     * list of objects stored in this CDOMObjectConsolidationFacet for the
     * Player Character represented by the given CharID.
     *
     * @param id     The CharID representing the Player Character from which the
     *               given item source should be removed
     * @param obj    The object for which the source should be removed
     * @param source The source for the given object to be removed from the list of
     *               sources
     */
    public void remove(CharID id, CDOMObject obj, Object source)
    {
        bridgeFacet.remove(id, obj, source);
    }

    /**
     * Adds a new DataFacetChangeListener to receive DataFacetChangeEvents
     * (EdgeChangeEvent and NodeChangeEvent) from CDOMObjectConsolidationFacet.
     * The given DataFacetChangeListener is added at the default priority
     * (zero).
     * <p>
     * Note that the DataFacetChangeListeners are a list, meaning a given
     * DataFacetChangeListener can be added more than once at a given priority,
     * and if that occurs, it must be removed an equivalent number of times in
     * order to no longer receive events from this CDOMObjectConsolidationFacet.
     *
     * @param listener The DataFacetChangeListener to receive DataFacetChangeEvents
     *                 from this CDOMObjectConsolidationFacet
     */
    public void addDataFacetChangeListener(DataFacetChangeListener<CharID, ? super CDOMObject> listener)
    {
        bridgeFacet.addDataFacetChangeListener(listener);
    }

    /**
     * Detects the addition of a CDOMObject to a Player Character and adds the
     * CDOMObject to the list of CDOMObjects stored in this
     * CDOMObjectConsolidationFacet for the Player Character identified by the
     * CharID in the DataFacetChangeEvent.
     * <p>
     * Triggered when one of the Facets to which CDOMObjectConsolidationFacet
     * listens fires a DataFacetChangeEvent to indicate a CDOMObject was added
     * to a Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CDOMObject cdo = dfce.getCDOMObject();
        add(dfce.getCharID(), cdo, dfce.getSource());
    }

    /**
     * Detects the removal of a CDOMObject to a Player Character and removes the
     * CDOMObject to the list of CDOMObjects stored in this
     * CDOMObjectConsolidationFacet for the Player Character identified by the
     * CharID in the DataFacetChangeEvent.
     * <p>
     * Triggered when one of the Facets to which CDOMObjectConsolidationFacet
     * listens fires a DataFacetChangeEvent to indicate a CDOMObject was removed
     * from a Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CDOMObject cdo = dfce.getCDOMObject();
        remove(dfce.getCharID(), cdo, dfce.getSource());
    }

    /**
     * Returns the Set of objects contained in this
     * CDOMObjectConsolidationFacet.
     *
     * @param id The CharID representing the Player Character for which the
     *           given items in this CDOMObjectConsolidationFacet should be
     *           returned
     * @return the Set of objects contained in this CDOMObjectConsolidationFacet
     */
    public Set<CDOMObject> getSet(CharID id)
    {
        return bridgeFacet.getSet(id);
    }

}
