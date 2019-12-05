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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.core.PlayerCharacter;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.BonusPair;

/**
 * This is a transition class, designed to allow things to be taken out of
 * PlayerCharacter while a transition is made to a system where bonuses are
 * captured when items are entered into the PlayerCharacter and can be
 * subscribed to by facets... and is thus different than today's (5.x) core.
 * <p>
 * Note that this does not refer to the BonusObj objects being captured and
 * added to the PlayerCharacter in BonusActivationFacet. This facet is focused
 * on the actual bonus values themselves (meaning the aggregate results of
 * calculations).
 */
public class BonusCheckingFacet
{
    private final PlayerCharacterTrackingFacet trackingFacet =
            FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);

    /**
     * Returns a specific bonus for a given Bonus type and Bonus name on the
     * Player Character identified by the given CharID.
     *
     * @param id        The CharID identifying the Player Character for which the
     *                  bonus of a given Bonus type and Bonus name should be returned
     * @param bonusType The Bonus type for which the bonus should be returned
     * @param bonusName The Bonus name for which the bonus should be returned
     * @return A specific bonus for a given Bonus type and Bonus name on the
     * Player Character identified by the given CharID
     */
    public double getBonus(CharID id, String bonusType, String bonusName)
    {
        PlayerCharacter pc = trackingFacet.getPC(id);
        return pc.getTotalBonusTo(bonusType, bonusName);
    }

    /**
     * Calculates the bonus for the Player Character identified by the given
     * CharID. The Bonus is calculated based on the BonusObj objects passed in
     * the given map. The value in the map indicates the source object of the
     * BonusObj.
     * <p>
     * This method is reference-semantic in that ownership of the Map which is
     * passed to this method is not transferred to this method. This method
     * guarantees that no modifications are made to the provided Map, and no
     * reference to the given Map will be retained by BonusCheckingFacet.
     *
     * @param id  The CharID of the Player Character for which the bonus should
     *            be calculated
     * @param map A Map indicating the BonusObj objects to be used in the
     *            calculation and the source CDOMObjects for those BonusObj
     *            objects
     * @return The bonus for the Player Character identified by the given
     * CharID, calculated from the BonusObj objects provided in the
     * given Map
     */
    public double calcBonus(CharID id, Map<BonusObj, ? extends CDOMObject> map)
    {
        double iBonus = 0;

        for (Map.Entry<BonusObj, ? extends CDOMObject> me : map.entrySet())
        {
            BonusObj bonus = me.getKey();
            CDOMObject source = me.getValue();
            iBonus += getBonusValue(id, bonus, source.getQualifiedKey()).doubleValue();
        }

        return iBonus;
    }

    /**
     * Calculates the bonus provided by a specific BonusObj object when provided
     * by the source identified by the given String.
     *
     * @param id               The CharID identifying the Player Character for which the
     *                         bonus provided by the given BonusObj should be returned
     * @param bonus            The BonusObj for which the bonus calculation should take place
     * @param sourceIdentifier The String identifier of the source of the BonusObj; required
     *                         in order to calculate object-specific variables which may be
     *                         in the formula of the BonusObj object
     * @return The bonus provided by a specific BonusObj object when provided by
     * the source identified by the given String
     */
    private Number getBonusValue(CharID id, BonusObj bonus, String sourceIdentifier)
    {
        PlayerCharacter pc = trackingFacet.getPC(id);
        return bonus.resolve(pc, sourceIdentifier);
    }

    /**
     * Returns the sum of all bonus values provided by the Collection of
     * BonusObj objects when provided by the source identified by the given
     * String.
     * <p>
     * This method is reference-semantic in that ownership of the Collection
     * which is passed to this method is not transferred to this method. This
     * method guarantees that no modifications are made to the provided
     * Collection, and no reference to the given Collection will be retained by
     * BonusCheckingFacet.
     *
     * @param id               The CharID identifying the Player Character for which the
     *                         bonus provided by the given Collection of BonusObj objects
     *                         should be returned
     * @param bonuses          The Collection of BonusObj objects for which the bonus
     *                         calculation should take place
     * @param sourceIdentifier The String identifier of the source of the BonusObj; required
     *                         in order to calculate object-specific variables which may be
     *                         in the formula of the BonusObj object
     * @return The sum of all bonus values provided by the Collection of
     * BonusObj objects when provided by the source identified by the
     * given String
     */
    public double getAllBonusValues(CharID id, Collection<BonusObj> bonuses, String sourceIdentifier)
    {
        PlayerCharacter pc = trackingFacet.getPC(id);
        double value = 0;
        for (BonusObj bo : bonuses)
        {
            value += bo.resolve(pc, sourceIdentifier).doubleValue();
        }
        return value;
    }

    /**
     * Get back a Collection of bonus info with %LIST entries replaced with the
     * choices made.
     * <p>
     * This method is value-semantic in that ownership of the returned
     * Collection is transferred to the class calling this method. Since this is
     * a remove all function, modification of the returned Collection will not
     * modify this BonusCheckingFacet and modification of this
     * BonusCheckingFacet will not modify the returned Collection. Modifications
     * to the returned Collection will also not modify any future or previous
     * objects returned by this (or other) methods on BonusCheckingFacet. If you
     * wish to modify the information stored in this BonusCheckingFacet, you
     * must use the add*() and remove*() methods of BonusCheckingFacet.
     *
     * @param id        The CharID identifying the Player Character for which the list
     *                  of bonus information should be returned
     * @param bonusName The Bonus name used to select which Bonus objects to expand to
     *                  get their information
     * @return A Collection of bonus info with %LIST entries replaced with the
     * choices made
     */
    public Collection<String> getExpandedBonusInfo(CharID id, String bonusName)
    {
        PlayerCharacter pc = trackingFacet.getPC(id);
        List<String> list = new ArrayList<>();
        for (BonusObj bonus : pc.getActiveBonusList())
        {
            if (bonus.getTypeOfBonus().equals(bonusName))
            {
                String bonusInfo = bonus.getBonusInfo();
                if (bonusInfo.contains("%LIST"))
                {
                    // We have a %LIST that needs to be expanded
                    List<BonusPair> bpList = pc.getStringListFromBonus(bonus);
                    for (BonusPair bonusPair : bpList)
                    {
                        String key = bonusPair.fullyQualifiedBonusType;
                        // Strip off the bonus name and the trailing .
                        if (key.startsWith(bonusName))
                        {
                            key = key.substring(bonusName.length() + 1);
                        }
                        list.add(key);
                    }

                } else
                {
                    list.add(bonus.getBonusInfo());
                }
            }
        }
        return list;
    }

}
