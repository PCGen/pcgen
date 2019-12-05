/*
 * Copyright (c) Thomas Parker, 2012.
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

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.model.StatFacet;
import pcgen.core.PCStat;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.BonusUtilities;

/**
 * StatBonusFacet is a Facet that calculates the bonus provided by PCStat objects on
 * a Player Character.
 */
public class StatBonusFacet
{
    private BonusCheckingFacet bonusCheckingFacet;
    private PrerequisiteFacet prerequisiteFacet;
    private StatFacet statFacet;

    /**
     * Returns a non-null Map indicating the bonuses of the given Bonus type and
     * given Bonus name, mapped to the PCStat objects which granted the Bonus.
     *
     * @param id    The CharID indicating the Player Character on which the
     *              bonuses for a given Bonus type and Bonus name are to be
     *              returned
     * @param aType The Bonus type of the bonuses to be returned
     * @param aName The Bonus name of the bonuses to be returned
     * @return A non-null Map indicating the bonuses of the given Bonus type and
     * given Bonus name, mapped to the PCStat objects which granted the
     * Bonus
     */
    public Map<BonusObj, PCStat> getBonusListOfType(CharID id, final String aType, final String aName)
    {
        final Map<BonusObj, PCStat> aList = new IdentityHashMap<>();

        for (PCStat stat : statFacet.getSet(id))
        {
            List<BonusObj> bonuses = BonusUtilities.getBonusFromList(stat.getSafeListFor(ListKey.BONUS), aType, aName);
            for (BonusObj bonus : bonuses)
            {
                aList.put(bonus, stat);
            }
        }

        return aList;
    }

    /**
     * Returns the aggregate Bonus value for the given Bonus type and given
     * Bonus name which are applied by PCStat objects to the Player Character
     * identified by the given CharID.
     *
     * @param id   The CharID identifying the Player Character for which the
     *             aggregate Bonus value is to be calculated
     * @param type The Bonus type for which the aggregate Bonus value is to be
     *             calculated
     * @param name The Bonus name for which the aggregate Bonus value is to be
     *             calculated
     * @return The aggregate Bonus value for the given Bonus type and given
     * Bonus name which are applied by PCStat objects to the Player
     * Character identified by the given CharID
     */
    public double getStatBonusTo(CharID id, String type, String name)
    {
        final Map<BonusObj, PCStat> map = getBonusListOfType(id, type.toUpperCase(), name.toUpperCase());
        for (Iterator<Map.Entry<BonusObj, PCStat>> it = map.entrySet().iterator();it.hasNext();)
        {
            Entry<BonusObj, PCStat> me = it.next();
            BonusObj bo = me.getKey();
            if (!prerequisiteFacet.qualifies(id, bo, me.getValue()))
            {
                it.remove();
            }
        }
        return bonusCheckingFacet.calcBonus(id, map);
    }

    public void setBonusCheckingFacet(BonusCheckingFacet bonusCheckingFacet)
    {
        this.bonusCheckingFacet = bonusCheckingFacet;
    }

    public void setPrerequisiteFacet(PrerequisiteFacet prerequisiteFacet)
    {
        this.prerequisiteFacet = prerequisiteFacet;
    }

    public void setStatFacet(StatFacet statFacet)
    {
        this.statFacet = statFacet;
    }

}
