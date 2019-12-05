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
package pcgen.cdom.facet.analysis;

import pcgen.cdom.enumeration.CharID;
import pcgen.core.PCStat;

/**
 * NonAbilityFacet is a Facet that tracks the Non-Abilities (PCStat objects)
 * that have been set on a Player Character.
 */
public class NonAbilityFacet
{
    private NonStatStatFacet nonStatStatFacet;
    private NonStatToStatFacet nonStatToStatFacet;

    /**
     * Returns true if the given PCStat is not an ability for the Player
     * Character identified by the given CharID.
     *
     * @param id   The CharID identifying the Player Character for which the
     *             given PCStat will be tested to see if it is a non-ability
     * @param stat The PCStat to be checked to see if it is a non-ability for the
     *             Player Character identified by the given CharID
     * @return true if the given PCStat is not an ability for the Player
     * Character identified by the given CharID; false otherwise
     */
    public boolean isNonAbility(CharID id, PCStat stat)
    {
        if (nonStatToStatFacet.contains(id, stat))
        {
            return false;
        }
        return nonStatStatFacet.contains(id, stat);
    }

    public void setNonStatStatFacet(NonStatStatFacet nonStatStatFacet)
    {
        this.nonStatStatFacet = nonStatStatFacet;
    }

    public void setNonStatToStatFacet(NonStatToStatFacet nonStatToStatFacet)
    {
        this.nonStatToStatFacet = nonStatToStatFacet;
    }

}
