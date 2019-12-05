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
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.core.PlayerCharacter;

/**
 * This is a transition class, designed to allow things to be taken out of
 * PlayerCharacter while a transition is made to a system where the results of
 * object addition is handled entirely within facets.
 */
public class ObjectAdditionFacet implements DataFacetChangeListener<CharID, CDOMObject>
{
    private final PlayerCharacterTrackingFacet trackingFacet =
            FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);

    private CDOMObjectConsolidationFacet consolidationFacet;

    /**
     * Forces processing of certain items internal to PlayerCharacter when a
     * CDOMObject is added to the Player Character.
     * <p>
     * Triggered when one of the Facets to which ObjectAdditionFacet listens
     * fires a DataFacetChangeEvent to indicate a CDOMObject was added to a
     * Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CDOMObject cdo = dfce.getCDOMObject();
        CharID id = dfce.getCharID();
        PlayerCharacter pc = trackingFacet.getPC(id);
        pc.processAddition(cdo);
    }

    /**
     * Forces processing of certain items internal to PlayerCharacter when a
     * CDOMObject is removed from the Player Character.
     * <p>
     * Triggered when one of the Facets to which NaturalEquipSetFacet listens
     * fires a DataFacetChangeEvent to indicate a CDOMObject was removed from a
     * Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CDOMObject cdo = dfce.getCDOMObject();
        CharID id = dfce.getCharID();
        PlayerCharacter pc = trackingFacet.getPC(id);
        pc.processRemoval(cdo);
    }

    public void setConsolidationFacet(CDOMObjectConsolidationFacet consolidationFacet)
    {
        this.consolidationFacet = consolidationFacet;
    }

    /**
     * Initializes the connections for ObjectAdditionFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the ObjectAdditionFacet.
     */
    public void init()
    {
        consolidationFacet.addDataFacetChangeListener(this);
    }
}
