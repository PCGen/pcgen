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
package pcgen.io.migration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.core.system.MigrationRule;
import pcgen.core.system.MigrationRule.ObjectType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * SourceMigrationTest checks the function of SourceMigration.
 */
public class SourceMigrationTest
{

    private String gameMode;

    @BeforeEach
    public void setUp()
    {
        gameMode = SettingsHandler.getGame().getName();
        MigrationRule sourceRule = new MigrationRule(ObjectType.SOURCE, "OldKey1");
        sourceRule.setMaxVer("6.0.1");
        sourceRule.setNewKey("NewKey1");
        SystemCollections.addToMigrationRulesList(sourceRule, gameMode);
        MigrationRule sourceRule2 = new MigrationRule(ObjectType.SOURCE, "OldKey2");
        sourceRule2.setMaxVer("6.0.1");
        sourceRule2.setMinVer("5.17.7");
        sourceRule2.setNewKey("LateNewKey");
        SystemCollections.addToMigrationRulesList(sourceRule2, gameMode);
        MigrationRule sourceRule2A = new MigrationRule(ObjectType.SOURCE, "OldKey2");
        sourceRule2A.setMaxVer("5.16.4");
        sourceRule2A.setMaxDevVer("5.17.5");
        sourceRule2A.setNewKey("EarlyNewKey");
        SystemCollections.addToMigrationRulesList(sourceRule2A, gameMode);
        MigrationRule sourceRuleDiffGame = new MigrationRule(ObjectType.SOURCE, "OldKey3");
        sourceRuleDiffGame.setMaxVer("6.0.0");
        sourceRuleDiffGame.setNewKey("NewKeyModern");
        SystemCollections.addToMigrationRulesList(sourceRuleDiffGame, "modern");
    }

    @AfterEach
    public void tearDown()
    {
        SystemCollections.clearMigrationRuleMap();
        gameMode = null;
    }

    /**
     * Test that rules for max version only are applied correctly.
     */
    @Test
    public void testMaxVer()
    {
        assertEquals("NewKey1", SourceMigration.getNewSourceKey("OldKey1", new int[]{6, 0, 0}, gameMode));
        assertEquals("OldKey1", SourceMigration.getNewSourceKey("OldKey1", new int[]{6, 0, 2}, gameMode));
    }

    /**
     * Test that rules for version ranges are applied correctly.
     */
    @Test
    public void testMinMaxVer()
    {
        assertEquals("LateNewKey", SourceMigration.getNewSourceKey("OldKey2", new int[]{6, 0, 0}, gameMode));
        assertEquals("OldKey2", SourceMigration.getNewSourceKey("OldKey2", new int[]{6, 0, 2}, gameMode));
        assertEquals("LateNewKey", SourceMigration.getNewSourceKey("OldKey2", new int[]{5, 17, 8}, gameMode));
        assertEquals("OldKey2", SourceMigration.getNewSourceKey("OldKey2", new int[]{5, 17, 6}, gameMode));
        assertEquals("OldKey2", SourceMigration.getNewSourceKey("OldKey2", new int[]{5, 16, 5}, gameMode));
        assertEquals("EarlyNewKey", SourceMigration.getNewSourceKey("OldKey2", new int[]{5, 17, 5}, gameMode));
        assertEquals("EarlyNewKey", SourceMigration.getNewSourceKey("OldKey2", new int[]{5, 16, 4}, gameMode));
    }

    /**
     * Check that migration rules for other game modes don't affect each other.
     */
    @Test
    public void testNoCrossGameMode()
    {
        assertEquals("OldKey3", SourceMigration.getNewSourceKey("OldKey3", new int[]{6, 0, 0}, gameMode));
        assertEquals("OldKey3", SourceMigration.getNewSourceKey("OldKey3", new int[]{5, 17, 0}, gameMode));
    }

    /**
     * Test that matches are case insensitive.
     */
    @Test
    public void testCaseInsensitive()
    {
        assertEquals("NewKey1", SourceMigration.getNewSourceKey("OldKEY1", new int[]{6, 0, 0}, gameMode));
    }
}
