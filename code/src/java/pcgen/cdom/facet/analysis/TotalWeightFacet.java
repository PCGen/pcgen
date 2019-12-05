/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.EquipmentFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.PlayerCharacterTrackingFacet;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.RuleConstants;

/**
 * TotalWeightFacet performs calculations related to the total weight of
 * Equipment carried by a Player Character (does not include the body weight of
 * the Player Character).
 */
public class TotalWeightFacet
{
    private final PlayerCharacterTrackingFacet trackingFacet =
            FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);

    private EquipmentFacet equipmentFacet;

    /**
     * Returns the total Equipment weight for the Player Character identified by
     * the given CharID.
     *
     * @param id The CharID identifying the Player Character for which the
     *           total Equipment weight is to be returned
     * @return The total Equipment weight for the Player Character identified by
     * the given CharID
     */
    public Float getTotalWeight(CharID id)
    {
        float totalWeight = 0;
        final Float floatZero = 0.0f;
        boolean firstClothing = !Globals.checkRule(RuleConstants.CLOTHINGENCUMBRANCE);

        PlayerCharacter pc = trackingFacet.getPC(id);
        for (Equipment eq : equipmentFacet.getSet(id))
        {
            // Loop through the list of top
            if ((eq.getCarried().compareTo(floatZero) > 0) && (eq.getParent() == null))
            {
                if (eq.getChildCount() > 0)
                {
                    totalWeight += (eq.getWeightAsDouble(pc) + eq.getContainedWeight(pc));
                } else
                {
                    if (firstClothing && eq.isEquipped() && eq.isType("CLOTHING"))
                    {
                        // The first equipped set of clothing should have a
                        // weight of 0. Feature #437410
                        firstClothing = false;
                        totalWeight += (eq.getWeightAsDouble(pc) * Math.max(eq.getCarried() - 1, 0));
                    } else
                    {
                        totalWeight += (eq.getWeightAsDouble(pc) * eq.getCarried());
                    }
                }
            }
        }

        return totalWeight;
    }

    public void setEquipmentFacet(EquipmentFacet equipmentFacet)
    {
        this.equipmentFacet = equipmentFacet;
    }

}
