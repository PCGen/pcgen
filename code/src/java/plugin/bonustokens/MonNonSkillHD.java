/*
 * MonNonSkillHD.java
 * Copyright 2004 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.bonustokens;

import pcgen.core.bonus.MultiTagBonusObj;

/**
 * {@code MonNonSkillHD} defines the MonNonSkillHD tag which
 * allows the LST code to specify how many of a monster's hit dice
 * do not gain skills.
 */

public final class MonNonSkillHD extends MultiTagBonusObj
{
    private static final String[] BONUS_TAGS = {"NUMBER", "LOCKNUMBER"};

    /**
     * Return the bonus tag handled by this class.
     *
     * @return The bonus handled by this class.
     */
    @Override
    public String getBonusHandled()
    {
        return "MONNONSKILLHD";
    }

    /**
     * Get by index, an monster non-skill hit die attribute that may be bonused.
     *
     * @param tagNumber the index of the monster non-skill hit die attribute.
     * @return The monster non-skill hit die attribute.
     */
    @Override
    protected String getBonusTag(final int tagNumber)
    {
        return BONUS_TAGS[tagNumber];
    }

    /**
     * Get the number of monster non-skill hit die attributes that may be bonused.
     *
     * @return The number of monster non-skill hit die attributes.
     */
    @Override
    protected int getBonusTagLength()
    {
        return BONUS_TAGS.length;
    }
}
