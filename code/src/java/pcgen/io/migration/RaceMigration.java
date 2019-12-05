/*
 * Copyright 2014 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 *
 *
 */
package pcgen.io.migration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.core.system.MigrationRule;
import pcgen.core.system.MigrationRule.ObjectType;

/**
 * RaceMigration translates old race keys into their current values. This is
 * used to allow clean loading of older characters which were saved with race
 * keys that have now been changed in the data.
 */
public final class RaceMigration
{
    private static Map<int[], List<MigrationRule>> raceChangesForVer = new HashMap<>();

    private RaceMigration()
    {
    }

    /**
     * Find the new race key to replace the provided one.
     *
     * @param raceKey The original race key as found in the character file.
     * @param pcgVer  The version of PCGen in which the character was created.
     * @return The new race key, or the passed in one if it has not changed.
     */
    public static String getNewRaceKey(String raceKey, int[] pcgVer, String gameModeName)
    {
        List<MigrationRule> raceChangeList = raceChangesForVer.computeIfAbsent(pcgVer,
                v -> MigrationUtils.getChangeList(v, gameModeName, ObjectType.RACE));

        for (MigrationRule rule : raceChangeList)
        {
            if (rule.getOldKey().equalsIgnoreCase(raceKey))
            {
                return rule.getNewKey();
            }
        }
        return raceKey;
    }

}
