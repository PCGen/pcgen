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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.BeforeEach;

import pcgen.core.system.MigrationRule;
import pcgen.core.system.MigrationRule.ObjectType;

/**
 * NewCategoryTokenTest verifies that NewCategoryToken is operating correctly.
 * 
 * 
 */
public class NewCategoryTokenTest
{

	private MigrationRule migrationRule;
	private NewCategoryToken token;
	private String gameModeName;

	
	@BeforeEach
	public void setUp()
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
		assertTrue("Parse should have been successful", token.parse(migrationRule, "ValidCat", gameModeName));
		assertEquals("New category", "ValidCat", migrationRule.getNewCategory());

		assertTrue("Parse should have been successful", token.parse(migrationRule, "v 123", gameModeName));
		assertEquals("New category", "v 123", migrationRule.getNewCategory());
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
			assertFalse("Cat containing " + invalid
				+ " should have been rejected.", token.parse(migrationRule,
				"InvalidKey" + invalid, gameModeName));
			assertNull("New category", migrationRule.getNewCategory());
		}

		for (char invalid : invalidChars.toCharArray())
		{
			assertFalse("Cat containing " + invalid
				+ " should have been rejected.", token.parse(migrationRule,
				invalid+"InvalidKey", gameModeName));
			assertNull("New category", migrationRule.getNewCategory());
		}


		for (char invalid : invalidChars.toCharArray())
		{
			assertFalse("Cat containing " + invalid
				+ " should have been rejected.", token.parse(migrationRule,
				"Invalid"+invalid+"Cat", gameModeName));
			assertNull("New category", migrationRule.getNewCategory());
		}
		
	}

}
