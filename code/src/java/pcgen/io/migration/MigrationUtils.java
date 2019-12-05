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

import java.util.ArrayList;
import java.util.List;

import pcgen.core.SystemCollections;
import pcgen.core.system.MigrationRule;
import pcgen.core.system.MigrationRule.ObjectType;

/**
 * MigrationUtils provides common helper functions for migration classes.
 */
public final class MigrationUtils
{

    private MigrationUtils()
    {
    }

    /**
     * Retrieve a list of migration rules which should be applied based on the supplied filters.
     *
     * @param pcgVer       The PCGen version the character was saved in.
     * @param gameModeName The character's game mode.
     * @param objectType   The type of object being migrated.
     * @return A list of migration rules.
     */
    protected static List<MigrationRule> getChangeList(int[] pcgVer, String gameModeName, ObjectType objectType)
    {
        List<MigrationRule> sourceChangeList = new ArrayList<>();
        List<MigrationRule> migrationRuleList = SystemCollections.getUnmodifiableMigrationRuleList(gameModeName);
        for (MigrationRule migrationRule : migrationRuleList)
        {
            if (migrationRule.getObjectType() == objectType && migrationRule.changeAppliesToVer(pcgVer))
            {
                sourceChangeList.add(migrationRule);
            }
        }
        return sourceChangeList;
    }

}
