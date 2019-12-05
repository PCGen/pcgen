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
 */
package pcgen.core.system;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * MigrationRuleTest checks the functions of the MigrationRule class.
 */
class MigrationRuleTest
{

    private MigrationRule migrationRule;

    @BeforeEach
    void setUp()
    {
        migrationRule = new MigrationRule(MigrationRule.ObjectType.SOURCE, "OldKey");
    }

    /**
     * Test changeAppliesToVer when only a maxver is specified.
     */
    @Test
    public void testChangeAppliesToVerMaxOnly()
    {
        migrationRule.setMaxVer("6.0.1");

        int[][] validVersions = {{5, 17, 10}, {6, 0, 0}, {6, 0, 1}};
        confirmMigrationDoesApply(validVersions);

        int[][] invalidVersions = {{6, 0, 2}, {6, 1, 0}, {6, 1, 5}, {6, 2, 0}, {7, 0, 0}};
        confirmMigrationDoesNotApply(invalidVersions);
    }

    /**
     * Test changeAppliesToVer when a maxver and maxdev is specified.
     */
    @Test
    public void testChangeAppliesToVerMaxDev()
    {
        migrationRule.setMaxVer("6.0.1");
        migrationRule.setMaxDevVer("6.1.3");

        int[][] validVersions = {{5, 17, 10}, {6, 0, 0}, {6, 0, 1}, {6, 1, 0}, {6, 1, 3}};
        confirmMigrationDoesApply(validVersions);

        int[][] invalidVersions = {{6, 0, 2}, {6, 1, 4}, {6, 2, 0}, {7, 0, 0}};
        confirmMigrationDoesNotApply(invalidVersions);
    }

    /**
     * Test changeAppliesToVer when a maxver and minver is specified.
     */
    @Test
    public void testChangeAppliesToVerMinMax()
    {
        migrationRule.setMaxVer("6.0.1");
        migrationRule.setMinVer("5.17.7");

        int[][] validVersions = {{5, 17, 7}, {5, 17, 10}, {6, 0, 0}, {6, 0, 1}};
        confirmMigrationDoesApply(validVersions);

        int[][] invalidVersions = {{5, 17, 6}, {5, 16, 8}, {6, 0, 2}, {6, 0, 2},
                {6, 1, 0}, {6, 1, 5}, {6, 2, 0}, {7, 0, 0}};
        confirmMigrationDoesNotApply(invalidVersions);
    }

    /**
     * Test changeAppliesToVer when a maxver, minver and mindevver are specified.
     */
    @Test
    public void testChangeAppliesToVerMinDevMinMax()
    {
        migrationRule.setMaxVer("6.0.1");
        migrationRule.setMinVer("5.16.4");
        migrationRule.setMinDevVer("5.17.7");

        int[][] validVersions = {{5, 17, 7}, {5, 17, 10}, {5, 16, 4}, {5, 16, 5}, {6, 0, 0}, {6, 0, 1}};
        confirmMigrationDoesApply(validVersions);

        int[][] invalidVersions = {{5, 17, 6}, {5, 16, 3}, {6, 0, 2}, {6, 0, 2},
                {6, 1, 0}, {6, 1, 5}, {6, 2, 0}, {7, 0, 0}};
        confirmMigrationDoesNotApply(invalidVersions);
    }


    private void confirmMigrationDoesApply(int[][] validVersions)
    {
        for (int[] pcgVer : validVersions)
        {
            assertTrue(
                    migrationRule.changeAppliesToVer(pcgVer),
                    () -> "Migration rule should apply for "
                            + displayVersion(pcgVer));
        }
    }

    private void confirmMigrationDoesNotApply(int[][] invalidVersions)
    {
        for (int[] pcgVer : invalidVersions)
        {
            assertFalse(
                    migrationRule.changeAppliesToVer(pcgVer),
                    () -> "Migration rule should not apply for "
                            + displayVersion(pcgVer));
        }
    }

    private static String displayVersion(int[] pcgVer)
    {
        return pcgVer[0] + "." + pcgVer[1] + "." + pcgVer[2];
    }
}
