/*
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.bonustokens;

import pcgen.core.bonus.MultiTagBonusObj;

/**
 * Handles the BONUS:SLOTS token.
 */
public final class Slots extends MultiTagBonusObj
{
    private static final String[] BONUS_TAGS = {"AMULET", "BELT", "BOOT", "BRACER", "CAPE", "EYEGEAR", "GLOVE", "HANDS",
            "HEADGEAR", "LEGS", "ROBE", "RING", "Shield", "SHIRT", "SUIT", "WEAPON"};

    /**
     * Return the bonus tag handled by this class.
     *
     * @return The bonus handled by this class.
     */
    @Override
    public String getBonusHandled()
    {
        return "SLOTS";
    }

    /**
     * Get by index, an individual slot type that may be bonused.
     *
     * @param tagNumber the index of the slot type.
     * @return The slot type.
     */
    @Override
    protected String getBonusTag(final int tagNumber)
    {
        return BONUS_TAGS[tagNumber];
    }

    /**
     * Get the number of slot types that may be bonused.
     *
     * @return The number of slot types.
     */
    @Override
    protected int getBonusTagLength()
    {
        return BONUS_TAGS.length;
    }
}
