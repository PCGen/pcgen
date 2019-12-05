/*
 * Copyright (c) Thomas Parker, 2019.
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
package pcgen.cdom.facet.analysis;

import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.CDOMObjectConsolidationFacet;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.core.MoveClone;

/**
 * MovementFacet is a Facet that tracks the MoveClone objects that are contained
 * in a Player Character.
 */
public class MoveCloneFacet extends AbstractSourcedListFacet<CharID, MoveClone>
        implements DataFacetChangeListener<CharID, CDOMObject>
{

    private CDOMObjectConsolidationFacet consolidationFacet;

    /**
     * Adds to this Facet the MoveClone objects contained within a CDOMObject
     * granted to the Player Character.
     * <p>
     * Triggered when one of the Facets to which MoveCloneFacet listens fires a
     * DataFacetChangeEvent to indicate a CDOMObject was added to a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CDOMObject cdo = dfce.getCDOMObject();
        List<MoveClone> ml = cdo.getListFor(ListKey.MOVEMENTCLONE);
        if (ml != null)
        {
            addAll(dfce.getCharID(), ml, cdo);
        }
    }

    /**
     * Removes from this Facet the MoveClone objects contained within a
     * CDOMObject removed from the Player Character.
     * <p>
     * Triggered when one of the Facets to which MoveCloneFacet listens fires a
     * DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        removeAll(dfce.getCharID(), dfce.getCDOMObject());
    }

    public void setConsolidationFacet(CDOMObjectConsolidationFacet consolidationFacet)
    {
        this.consolidationFacet = consolidationFacet;
    }

    /**
     * Initializes the connections for MoveCloneFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the MoveCloneFacet.
     */
    public void init()
    {
        consolidationFacet.addDataFacetChangeListener(this);
    }
}
