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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractItemFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.BioSetFacet;
import pcgen.core.BioSet;
import pcgen.core.PlayerCharacter;

/**
 * BioSetTrackingFacet is a Facet that triggers when characteristics related to
 * the BioSet are changed and forces changes upon the PlayerCharacter (age,
 * height, weight)
 */
public class BioSetTrackingFacet extends AbstractItemFacet<CharID, BioSet>
        implements DataFacetChangeListener<CharID, CDOMObject>
{
    private PlayerCharacterTrackingFacet trackingFacet = FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);

    private BioSetFacet bioSetFacet;

    /**
     * Processes added CDOMObjects to ensure the Age, Weight, and Height of a
     * Player Character are appropriately (re)set when the Player Character is
     * changed.
     * <p>
     * Triggered when one of the Facets to which BioSetTrackingFacet listens
     * fires a DataFacetChangeEvent to indicate a CDOMObject was added to a
     * Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CharID id = dfce.getCharID();
        PlayerCharacter pc = trackingFacet.getPC(id);

        if (!pc.isImporting())
        {
            bioSetFacet.get(id).randomize("AGE.HT.WT", pc);
        }

    }

    /**
     * Triggered when one of the Facets to which BioSetTrackingFacet listens
     * fires a DataFacetChangeEvent to indicate a CDOMObject was removed from a
     * Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        /*
         * Nothing to do
         *
         * Unlike the intent of most other facets, this method is intentionally
         * not symmetric with dataAdded. The reason is that the contents of
         * dataAdded are currently a randomization. Since that does not have an
         * equivalent "undo" associated with the randomization, there is no need
         * to have this methods perform a removal operation on those items.
         *
         * CONSIDER If RaceFacet ever allows a null race (that should not be
         * possible based on the design of loading the empty Race when
         * PlayerCharacter is constructed), then it is possible that this should
         * actually do a "reset" of Age, Weight, and Height of the Player
         * Character (setting those values back to defaults that exist when no
         * race is defined)
         */
    }

    public void setBioSetFacet(BioSetFacet bioSetFacet)
    {
        this.bioSetFacet = bioSetFacet;
    }
}
