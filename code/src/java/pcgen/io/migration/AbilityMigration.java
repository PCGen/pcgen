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
 * AbilityMigration translates old ability keys into their current values. This is
 * used to allow clean loading of older characters which were saved with ability
 * keys that have now been changed in the data.
 */
public final class AbilityMigration
{

    private static Map<int[], List<MigrationRule>> abilityChangesForVer = new HashMap<>();

    private AbilityMigration()
    {
    }

    /**
     * Find the new ability key to replace the provided one.
     *
     * @param abilityCategory The original ability category as found in the character file.
     * @param abilityKey      The original ability key as found in the character file.
     * @param pcgVer          The version of PCGen in which the character was created.
     * @return The new ability key, or the passed in one if it has not changed.
     */
    public static CategorisedKey getNewAbilityKey(String abilityCategory, String abilityKey, int[] pcgVer,
            String gameModeName)
    {
        List<MigrationRule> abilityChangeList = abilityChangesForVer.computeIfAbsent(pcgVer,
                v -> MigrationUtils.getChangeList(v, gameModeName, ObjectType.ABILITY));

        for (MigrationRule rule : abilityChangeList)
        {
            if (rule.getOldKey().equalsIgnoreCase(abilityKey)
                    && rule.getOldCategory().equalsIgnoreCase(abilityCategory))
            {
                return new CategorisedKey((rule.getNewCategory() == null) ? abilityCategory : rule.getNewCategory(),
                        rule.getNewKey());
            }
        }
        return new CategorisedKey(abilityCategory, abilityKey);
    }

    /**
     * CategorisedKey is a container for a category and a key.
     */
    public static final class CategorisedKey
    {
        private final String category;
        private final String key;

        private CategorisedKey(String category, String key)
        {
            this.category = category;
            this.key = key;
        }

        public String getCategory()
        {
            return category;
        }

        public String getKey()
        {
            return key;
        }
    }
}
