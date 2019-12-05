/*
 * Copyright 2009 (c) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
package pcgen.core.analysis;

import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;

public final class StatAnalysis
{
    private StatAnalysis()
    {
    }

    /**
     * Retrieve a correctly calculated attribute value where one or more
     * types are excluded.
     *
     * @param stat     The abbreviation of the stat to be calculated
     * @param useTemp  Should temporary bonuses be included?
     * @param useEquip Should equipment bonuses be included?
     * @return The value of the stat
     */
    public static int getPartialStatFor(PlayerCharacter aPC, PCStat stat, boolean useTemp, boolean useEquip)
    {
        if (aPC.hasNonStatStat(stat))
        {
            return 10;
        }

        // Only check for a lock if the stat hasn't been unlocked
        if (!aPC.hasUnlockedStat(stat))
        {
            Number val = aPC.getLockedStat(stat);
            if (val != null)
            {
                return val.intValue();
            }
        }

        int y = aPC.getPartialStatFor(stat, useTemp, useEquip);
        return y;
    }

}
