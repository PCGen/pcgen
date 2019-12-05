/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * SourceMigration translates old source keys into their current values. This is
 * used to allow clean loading of older characters which were saved with source
 * keys that have now been changed in the data.
 */
public final class SourceMigration
{

    private static Map<int[], List<MigrationRule>> sourceChangesForVer = new HashMap<>();

    private SourceMigration()
    {
    }

    /**
     * Find the new source key to replace the provided one.
     *
     * @param sourceKey The original source key as found in the character file.
     * @param pcgVer    The version of PCGen in which the character was created.
     * @return The new source key, or the passed in one if it has not changed.
     */
    public static String getNewSourceKey(String sourceKey, int[] pcgVer, String gameModeName)
    {
        List<MigrationRule> sourceChangeList = sourceChangesForVer.computeIfAbsent(pcgVer,
                v -> MigrationUtils.getChangeList(v, gameModeName, ObjectType.SOURCE));

        for (MigrationRule rule : sourceChangeList)
        {
            if (rule.getOldKey().equalsIgnoreCase(sourceKey))
            {
                return rule.getNewKey();
            }
        }
        return sourceKey;
    }
}
