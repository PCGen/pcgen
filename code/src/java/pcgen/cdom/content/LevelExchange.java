/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.content;

import java.util.Objects;

import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCClass;

/**
 * A LevelExchange is a storage container identifying the level exchange that
 * can take place when a given class is taken.
 */
public class LevelExchange extends ConcretePrereqObject
{

    /**
     * A CDOMReference to the class which can be exchanged from (the class where
     * levels will be reduced, not the class where the token was present)
     */
    private final CDOMSingleRef<PCClass> exchangeClass;

    /**
     * Identifies the minimum number of levels that must be donated into the
     * class in which the LevelExchange is present.
     */
    private final int minDonatingLevel;

    /**
     * Identifies the maximum number of levels that may be donated into the
     * class in which the LevelExchange is present.
     */
    private final int maxDonatedLevels;

    /**
     * Identifies the minimum level that may be reached in the exchangeClass
     * during any level exchange. (In other words, the number of levels in the
     * exchangeClass cannot be reduced below this level by the LevelExchange)
     */
    private final int donatingLowerLevelBound;

    /**
     * Creates a new LevelExchange for the given Class (identifed by a
     * CDOMReference), and given limits on the exchange.
     *
     * @param pcc                The class which levels can be exchanged from
     * @param minDonatingLvl     The minimum number of levels that must be donated into the
     *                           class in which the LevelExchange is present.
     * @param maxDonated         The maximum number of levels that may be donated into the
     *                           class in which the LevelExchange is present.
     * @param donatingLowerBound The minimum level that may be reached in the exchangeClass
     *                           during any level exchange.
     */
    public LevelExchange(CDOMSingleRef<PCClass> pcc, int minDonatingLvl, int maxDonated, int donatingLowerBound)
    {
        Objects.requireNonNull(pcc, "Error: Exchange Class must not be null");
        if (minDonatingLvl <= 0)
        {
            throw new IllegalArgumentException(
                    "Error: Min Donating Level <= 0: " + "Cannot Allow Donations to produce negative levels");
        }
        if (maxDonated <= 0)
        {
            throw new IllegalArgumentException(
                    "Error: Max Donated Levels <= 0: " + "Cannot Allow Donations to produce negative levels");
        }
        if (donatingLowerBound < 0)
        {
            throw new IllegalArgumentException(
                    "Error: Max Remaining Levels < 0: " + "Cannot Allow Donations to produce negative levels");
        }
        if (minDonatingLvl - maxDonated > donatingLowerBound)
        {
            throw new IllegalArgumentException("Error: Donating Lower Bound cannot be reached");
        }
        exchangeClass = pcc;
        minDonatingLevel = minDonatingLvl;
        maxDonatedLevels = maxDonated;
        donatingLowerLevelBound = donatingLowerBound;
    }

    /**
     * Returns the minimum level that may be reached in the exchange class
     * during any level exchange.
     *
     * @return The minimum level that may be reached in the exchange class
     * during any level exchange.
     */
    public int getDonatingLowerLevelBound()
    {
        return donatingLowerLevelBound;
    }

    /**
     * Returns a Reference to the class which can be exchanged from (the class
     * where levels will be reduced, not the class where the token was present)
     *
     * @return A Reference to the class which can be exchanged from
     */
    public CDOMSingleRef<PCClass> getExchangeClass()
    {
        return exchangeClass;
    }

    /**
     * Returns the maximum number of levels that may be donated into the class
     * in which the LevelExchange is present.
     *
     * @return The maximum number of levels that may be donated into the class
     * in which the LevelExchange is present.
     */
    public int getMaxDonatedLevels()
    {
        return maxDonatedLevels;
    }

    /**
     * Returns the minimum number of levels that must be donated into the class
     * in which the LevelExchange is present.
     *
     * @return The minimum number of levels that must be donated into the class
     * in which the LevelExchange is present.
     */
    public int getMinDonatingLevel()
    {
        return minDonatingLevel;
    }

    @Override
    public int hashCode()
    {
        return minDonatingLevel * 23 + maxDonatedLevels * 31 + donatingLowerLevelBound;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof LevelExchange))
        {
            return false;
        }
        LevelExchange other = (LevelExchange) obj;
        return minDonatingLevel == other.minDonatingLevel && maxDonatedLevels == other.maxDonatedLevels
                && donatingLowerLevelBound == other.donatingLowerLevelBound && exchangeClass.equals(other.exchangeClass);
    }
}
