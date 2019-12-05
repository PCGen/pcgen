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

import java.util.List;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.model.CheckFacet;
import pcgen.core.PCCheck;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.BonusUtilities;

/**
 * CheckFacet is a Facet that tracks the PCCheck objects available to a Player
 * Character.
 */
public class CheckBonusFacet
{

    private BonusCheckingFacet bonusCheckingFacet;

    private CheckFacet checkFacet;

    /**
     * Returns the Bonus value provided solely by Checks, for a given Bonus type
     * and Bonus name on the Player Character identified by the given CharID.
     *
     * @param id   CharId identifying the Player Character for which the Bonus
     *             value will be returned
     * @param type The Bonus type for which the Bonus value will be returned
     * @param name The Bonus name for which the Bonus value will be returned
     * @return The Bonus value provided solely by Checks, for a given Bonus type
     * and Bonus name on the Player Character identified by the given
     * CharID
     * @deprecated by STATMODSAVE Code Control
     */
    @Deprecated
    public double getCheckBonusTo(CharID id, String type, String name)
    {
        double bonus = 0;
        String upperType = type.toUpperCase();
        String upperName = name.toUpperCase();

        for (PCCheck check : checkFacet.getSet(id))
        {
            List<BonusObj> tempList =
                    BonusUtilities.getBonusFromList(check.getListFor(ListKey.BONUS), upperType, upperName);
            if (!tempList.isEmpty())
            {
                bonus += bonusCheckingFacet.getAllBonusValues(id, tempList, check.getQualifiedKey());
            }
        }

        return bonus;
    }

    public void setBonusCheckingFacet(BonusCheckingFacet bonusCheckingFacet)
    {
        this.bonusCheckingFacet = bonusCheckingFacet;
    }

    public void setCheckFacet(CheckFacet checkFacet)
    {
        this.checkFacet = checkFacet;
    }

}
