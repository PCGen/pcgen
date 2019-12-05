/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
package pcgen.core;

import java.math.BigDecimal;

public final class SkillUtilities
{

    private SkillUtilities()
    {
    }

    /**
     * Returns the maximum number of ranks a character can have in a class skill
     * at the specified level. <p>Should this be moved to PCClass?
     *
     * @param level character level to get max skill ranks for
     * @param pc
     * @return The maximum allowed skill ranks
     */
    public static BigDecimal maxClassSkillForLevel(final int level, final PlayerCharacter pc)
    {
        LevelInfo info = pc.getXPTableLevelInfo(level);
        if (info != null)
        {
            return info.getMaxClassSkillRank(level, pc);
        }
        /*
         * TODO Should this be a warning/error?
         */
        return BigDecimal.ZERO;
    }

    /**
     * Returns the maximum number of ranks a character can <p>have in a
     * cross-class skill at the specified level.
     *
     * @param level character level to get max skill ranks for
     * @param pc
     * @return The maximum allowed skill ranks
     */
    public static BigDecimal maxCrossClassSkillForLevel(final int level, final PlayerCharacter pc)
    {
        LevelInfo info = pc.getXPTableLevelInfo(level);
        if (info != null)
        {
            return info.getMaxCrossClassSkillRank(level, pc);
        }
        /*
         * TODO Should this be a warning/error?
         */
        return BigDecimal.ZERO;
    }
}
