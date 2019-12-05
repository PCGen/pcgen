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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.event.DataFacetChangeListener;

/**
 * CDOMObjectSourceFacet consolidates all of the CDOMObjects that are added to a
 * Player Character. By consolidating all of the CDOMObjects into one location,
 * behaviors which are consistent across all CDOMObjects can be performed based
 * on events from a single source Facet.
 * <p>
 * Note: CDOMObjectConsolidationFacet should be used in preference to this facet
 * where possible. CDOMObjectSourceFacet is for use when the use of
 * CDOMObjectConsolidationFacet would produce a cycle (and thus Spring would be
 * unable to construct the facets in that cycle)
 *
 * @see pcgen.cdom.facet.CDOMObjectConsolidationFacet
 * @see pcgen.cdom.facet.CDOMObjectBridge
 */
public class CDOMObjectSourceFacet
{

    private CDOMObjectBridge bridgeFacet;

    public void setBridgeFacet(CDOMObjectBridge bridge)
    {
        bridgeFacet = bridge;
    }

    /**
     * Adds a new DataFacetChangeListener to receive DataFacetChangeEvents
     * (EdgeChangeEvent and NodeChangeEvent) from CDOMObjectSourceFacet. The
     * given DataFacetChangeListener is added at the default priority (zero).
     * <p>
     * Note that the DataFacetChangeListeners are a list, meaning a given
     * DataFacetChangeListener can be added more than once at a given priority,
     * and if that occurs, it must be removed an equivalent number of times in
     * order to no longer receive events from this CDOMObjectSourceFacet.
     *
     * @param listener The DataFacetChangeListener to receive DataFacetChangeEvents
     *                 from this CDOMObjectSourceFacet
     */
    public void addDataFacetChangeListener(DataFacetChangeListener<CharID, ? super CDOMObject> listener)
    {
        bridgeFacet.addDataFacetChangeListener(listener);
    }

}
