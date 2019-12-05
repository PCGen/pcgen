/*
 * Copyright 2016 (C) Andrew Maitland <drew0500@yahoo.com>
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
 * SpellMigration translates old spell keys into their current values. This is
 * used to allow clean loading of older characters which were saved with spell
 * keys that have now been changed in the data.
 */
public final class SpellMigration
{
    private static Map<int[], List<MigrationRule>> spellChangesForVer = new HashMap<>();

    private SpellMigration()
    {
    }

    /**
     * Find the new spell key to replace the provided one.
     *
     * @param spellKey The original spell key as found in the character file.
     * @param pcgVer   The version of PCGen in which the character was created.
     * @return The new spell key, or the passed in one if it has not changed.
     */
    public static String getNewSpellKey(String spellKey, int[] pcgVer, String gameModeName)
    {
        List<MigrationRule> spellChangeList = spellChangesForVer.computeIfAbsent(pcgVer,
                v -> MigrationUtils.getChangeList(v, gameModeName, ObjectType.SPELL));

        for (MigrationRule rule : spellChangeList)
        {
            if (rule.getOldKey().equalsIgnoreCase(spellKey))
            {
                return rule.getNewKey();
            }
        }
        return spellKey;
    }

}
