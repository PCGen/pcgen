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
 * {@code Vision} deals with bonuses to a character's vision.
 */
public final class Vision extends MultiTagBonusObj
{
    private static final String[] BONUS_TAGS =
            {"NORMAL", "LOW-LIGHT", "DARKVISION", "BLINDSIGHT", "TREMORSENSE", "ECHOSENSE", "X-RAY"};

    /**
     * Return the bonus tag handled by this class.
     *
     * @return The bonus handled by this class.
     */
    @Override
    public String getBonusHandled()
    {
        return "VISION";
    }

    /**
     * Get by index, an individual type of vision that may be bonused.
     *
     * @param tagNumber the index of the vision type.
     * @return The type of vision.
     */
    @Override
    protected String getBonusTag(final int tagNumber)
    {
        return BONUS_TAGS[tagNumber];
    }

    /**
     * Get the number of types of vision that may be bonused.
     *
     * @return The number of vision types.
     */
    @Override
    protected int getBonusTagLength()
    {
        return BONUS_TAGS.length;
    }
}
