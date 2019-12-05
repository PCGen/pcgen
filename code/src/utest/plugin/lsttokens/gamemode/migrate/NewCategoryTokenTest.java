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
 * NewCategoryTokenTest verifies that NewCategoryToken is operating correctly.
 */
class NewCategoryTokenTest
{

    private MigrationRule migrationRule;
    private NewCategoryToken token;
    private String gameModeName;


    @BeforeEach
    void setUp()
    {
        migrationRule = new MigrationRule(ObjectType.SOURCE, "OldKey");
        token = new NewCategoryToken();
        gameModeName = "Pathfinder";
    }

    /**
     * Test method for {@link NewCategoryToken#parse(MigrationRule, String, String)}.
     */
    @Test
    public void testParseValidCat()
    {
        assertTrue(token.parse(migrationRule, "ValidCat", gameModeName), "Parse should have been successful");
        assertEquals("ValidCat", migrationRule.getNewCategory(), "New category");

        assertTrue(token.parse(migrationRule, "v 123", gameModeName), "Parse should have been successful");
        assertEquals("v 123", migrationRule.getNewCategory(), "New category");
    }

    /**
     * Test that invalid characters get rejected and the new category field is not set.
     */
    @Test
    public void testParseInvalidCat()
    {
        String invalidChars = ",|\\:;.%*=[]";
        for (char invalid : invalidChars.toCharArray())
        {
            assertFalse(token.parse(migrationRule,
                    "InvalidKey" + invalid, gameModeName), "Cat containing " + invalid
                    + " should have been rejected.");
            assertNull(migrationRule.getNewCategory(), "New category");
        }

        for (char invalid : invalidChars.toCharArray())
        {
            assertFalse(token.parse(migrationRule,
                    invalid + "InvalidKey", gameModeName), "Cat containing " + invalid
                    + " should have been rejected.");
            assertNull(migrationRule.getNewCategory(), "New category");
        }


        for (char invalid : invalidChars.toCharArray())
        {
            assertFalse(token.parse(migrationRule,
                    "Invalid" + invalid + "Cat", gameModeName), "Cat containing " + invalid
                    + " should have been rejected.");
            assertNull(migrationRule.getNewCategory(), "New category");
        }

    }

}
