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
package plugin.lsttokens.gamemode.migrate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.core.system.MigrationRule;
import pcgen.core.system.MigrationRule.ObjectType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * MaxVerTokenTest checks the function of the MaxVerToken class.
 */
class MaxVerTokenTest
{
    private MigrationRule migrationRule;
    private MaxVerToken token;
    private String gameModeName;

    @BeforeEach
    void setUp()
    {
        migrationRule = new MigrationRule(ObjectType.SOURCE, "OldKey");
        token = new MaxVerToken();
        gameModeName = "Pathfinder";
    }

    /**
     * Test method for {@link MaxVerToken#parse(MigrationRule, String, String)}.
     */
    @Test
    public void testParseValidVer()
    {
        assertTrue(token.parse(migrationRule, "6.01.03", gameModeName), "Parse should have been successful");
        assertEquals("6.01.03", migrationRule.getMaxVer(), "MaxVer");
    }

    @Test
    public void testParseValidVerNumbers()
    {
        String[] goodVersions =
                {"5.17.12", "6.0.0", "6.0.1 RC2", "6.0.1-RC2", "6.01.02", "6.01.02-dev"};
        for (String verString : goodVersions)
        {
            assertTrue(token.parse(migrationRule, verString, gameModeName), "Valid version " + verString
                    + " should have been accepted");
            assertEquals(verString, migrationRule.getMaxVer(), "MaxVer");
        }
    }

    @Test
    public void testParseInvalidVerEmpty()
    {
        assertFalse(token.parse(migrationRule, "", gameModeName),
                "Empty version should not have been accepted");
        assertNull(migrationRule.getMaxVer(), "MaxVer");
    }

    @Test
    public void testParseInvalidVerFormat()
    {
        String[] badVersions =
                {"text", "a.b.c", "6.1", "6_0_1", "6.0.1d", "3.rc2", "6.0.1RC2"};
        for (String verString : badVersions)
        {
            assertFalse(token.parse(migrationRule, verString, gameModeName),
                    "Invalid version " + verString
                            + " should not have been accepted");
            assertNull(migrationRule.getMaxVer(), "MaxVer");
        }
    }

}
