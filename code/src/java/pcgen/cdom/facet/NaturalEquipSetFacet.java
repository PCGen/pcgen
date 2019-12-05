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

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.character.EquipSet;

/**
 * NaturalEquipSetFacet is a Facet that tracks the Natural EquipSet for a
 * Player Character, and automatically adds Natural Equipment to that EquipSet.
 */
public class NaturalEquipSetFacet implements DataFacetChangeListener<CharID, Equipment>
{
    private final PlayerCharacterTrackingFacet trackingFacet =
            FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);

    private NaturalWeaponFacet naturalWeaponFacet;

    /**
     * Adds a piece of TYPE=Natural Equipment to the Natural EquipSet when the
     * Equipment is added to a Player Character.
     * <p>
     * Triggered when one of the Facets to which MovementResultFacet listens
     * fires a DataFacetChangeEvent to indicate a CDOMObject was added to a
     * Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, Equipment> dfce)
    {
        PlayerCharacter pc = trackingFacet.getPC(dfce.getCharID());
        EquipSet eSet = pc.getEquipSetByIdPath(EquipSet.DEFAULT_SET_PATH);
        if (eSet != null)
        {
            Equipment eq = dfce.getCDOMObject();
            EquipSet es = pc.addEquipToTarget(eSet, null, "", eq, null);
            if (es == null)
            {
                pc.addEquipToTarget(eSet, null, Constants.EQUIP_LOCATION_CARRIED, eq, null);
            }
        }
    }

    /**
     * Triggered when one of the Facets to which NaturalEquipSetFacet listens
     * fires a DataFacetChangeEvent to indicate a CDOMObject was removed from a
     * Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, Equipment> dfce)
    {
        // Ignore for now
        /*
         * TODO Need to establish why this is not symmetric, and what needs to
         * be done to make it symmetric.
         */
    }

    public void setNaturalWeaponFacet(NaturalWeaponFacet naturalWeaponFacet)
    {
        this.naturalWeaponFacet = naturalWeaponFacet;
    }

    /**
     * Initializes the connections for NaturalEquipSetFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the NaturalEquipSetFacet.
     */
    public void init()
    {
        naturalWeaponFacet.addDataFacetChangeListener(1, this);
    }
}
