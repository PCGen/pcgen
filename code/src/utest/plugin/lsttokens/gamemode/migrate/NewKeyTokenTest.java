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
 * NewKeyTokenTest verifies that NewKeyToken is operating correctly.
 */
class NewKeyTokenTest
{
    private MigrationRule migrationRule;
    private MigrationRule migrationRuleEquip;
    private NewKeyToken token;
    private String gameModeName;


    @BeforeEach
    void setUp()
    {
        migrationRule = new MigrationRule(ObjectType.SOURCE, "OldKey");
        migrationRuleEquip = new MigrationRule(ObjectType.EQUIPMENT, "OldKey");
        token = new NewKeyToken();
        gameModeName = "Pathfinder";
    }

    /**
     * Test method for {@link NewKeyToken#parse(MigrationRule, String, String)}.
     */
    @Test
    public void testParseValidKey()
    {
        assertTrue(token.parse(migrationRule, "ValidKey", gameModeName), "Parse should have been successful");
        assertEquals("ValidKey", migrationRule.getNewKey(), "Newkey");

        assertTrue(token.parse(migrationRule, "v 123", gameModeName), "Parse should have been successful");
        assertEquals("v 123", migrationRule.getNewKey(), "Newkey");
    }

    /**
     * Test that invalid characters get rejected and the new key field is not set.
     */
    @Test
    public void testParseInvalidKeyEquip()
    {
        String invalidChars = ",|\\:;.%*=[]";
        for (char invalid : invalidChars.toCharArray())
        {
            assertFalse(token.parse(migrationRuleEquip,
                    "InvalidKey" + invalid, gameModeName),
                    () -> "Key containing " + invalid
                            + " should have been rejected."
            );
            assertNull(migrationRule.getNewKey(), "Newkey");
        }

        for (char invalid : invalidChars.toCharArray())
        {
            assertFalse(token.parse(migrationRuleEquip,
                    invalid + "InvalidKey", gameModeName),
                    () -> "Key containing " + invalid
                            + " should have been rejected."
            );
            assertNull(migrationRule.getNewKey(), "Newkey");
        }


        for (char invalid : invalidChars.toCharArray())
        {
            assertFalse(token.parse(migrationRuleEquip,
                    "Invalid" + invalid + "Key", gameModeName),
                    () -> "Key containing " + invalid
                            + " should have been rejected."
            );
            assertNull(migrationRule.getNewKey(), "Newkey");
        }

    }

    /**
     * Test that invalid characters get rejected and the new key field is not set.
     */
    @Test
    public void testParseInvalidKeySource()
    {
        String invalidChars = "|\\;%*=[]";
        for (char invalid : invalidChars.toCharArray())
        {
            assertFalse(token.parse(migrationRule,
                    "InvalidKey" + invalid, gameModeName),
                    () -> "Key containing " + invalid
                            + " should have been rejected."
            );
            assertNull(migrationRule.getNewKey(), "Newkey");
        }

        for (char invalid : invalidChars.toCharArray())
        {
            assertFalse(token.parse(migrationRule,
                    invalid + "InvalidKey", gameModeName),
                    () -> "Key containing " + invalid
                            + " should have been rejected."
            );
            assertNull(migrationRule.getNewKey(), "Newkey");
        }


        for (char invalid : invalidChars.toCharArray())
        {
            assertFalse(token.parse(migrationRule,
                    "Invalid" + invalid + "Key", gameModeName),
                    () -> "Key containing " + invalid
                            + " should have been rejected."
            );
            assertNull(migrationRule.getNewKey(), "Newkey");
        }

    }


    @Test
    public void testParseValidKeySource()
    {
        String validChars = ",:.";
        for (char valid : validChars.toCharArray())
        {
            String keyValue = "ValidKey" + valid;
            assertTrue(token.parse(migrationRule,
                    keyValue, gameModeName), () -> "Key containing " + valid
                    + " should have been accepted.");
            assertEquals(keyValue, migrationRule.getNewKey(), "Newkey");
        }

        for (char valid : validChars.toCharArray())
        {
            String keyValue = valid + "ValidKey";
            assertTrue(token.parse(migrationRule,
                    keyValue, gameModeName), () -> "Key containing " + valid
                    + " should have been accepted.");
            assertEquals(keyValue, migrationRule.getNewKey(), "Newkey");
        }


        for (char valid : validChars.toCharArray())
        {
            String keyValue = "Valid" + valid + "Key";
            assertTrue(token.parse(migrationRule,
                    keyValue, gameModeName), () -> "Key containing " + valid
                    + " should have been accepted.");
            assertEquals(keyValue, migrationRule.getNewKey(), "Newkey");
        }

    }

}
