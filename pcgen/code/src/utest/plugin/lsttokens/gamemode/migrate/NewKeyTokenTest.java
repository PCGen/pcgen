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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pcgen.core.system.MigrationRule;
import pcgen.core.system.MigrationRule.ObjectType;

/**
 * NewKeyTokenTest verifies that NewKeyToken is operating correctly.
 * 
 * 
 */
public class NewKeyTokenTest
{
	private MigrationRule migrationRule;
	private MigrationRule migrationRuleEquip;
	private NewKeyToken token;
	private String gameModeName;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		migrationRule = new MigrationRule(ObjectType.SOURCE, "OldKey");
		migrationRuleEquip = new MigrationRule(ObjectType.EQUIPMENT, "OldKey");
		token = new NewKeyToken();
		gameModeName = "Pathfinder";
	}

	/**
	 * Test method for {@link plugin.lsttokens.gamemode.migrate.NewKeyToken#parse(pcgen.core.system.MigrationRule, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testParseValidKey()
	{
		assertTrue("Parse should have been successful", token.parse(migrationRule, "ValidKey", gameModeName));
		assertEquals("Newkey", "ValidKey", migrationRule.getNewKey());

		assertTrue("Parse should have been successful", token.parse(migrationRule, "v 123", gameModeName));
		assertEquals("Newkey", "v 123", migrationRule.getNewKey());
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
			assertFalse("Key containing " + invalid
				+ " should have been rejected.", token.parse(migrationRuleEquip,
				"InvalidKey" + invalid, gameModeName));
			assertNull("Newkey", migrationRule.getNewKey());
		}

		for (char invalid : invalidChars.toCharArray())
		{
			assertFalse("Key containing " + invalid
				+ " should have been rejected.", token.parse(migrationRuleEquip,
				invalid+"InvalidKey", gameModeName));
			assertNull("Newkey", migrationRule.getNewKey());
		}


		for (char invalid : invalidChars.toCharArray())
		{
			assertFalse("Key containing " + invalid
				+ " should have been rejected.", token.parse(migrationRuleEquip,
				"Invalid"+invalid+"Key", gameModeName));
			assertNull("Newkey", migrationRule.getNewKey());
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
			assertFalse("Key containing " + invalid
				+ " should have been rejected.", token.parse(migrationRule,
				"InvalidKey" + invalid, gameModeName));
			assertNull("Newkey", migrationRule.getNewKey());
		}

		for (char invalid : invalidChars.toCharArray())
		{
			assertFalse("Key containing " + invalid
				+ " should have been rejected.", token.parse(migrationRule,
				invalid+"InvalidKey", gameModeName));
			assertNull("Newkey", migrationRule.getNewKey());
		}


		for (char invalid : invalidChars.toCharArray())
		{
			assertFalse("Key containing " + invalid
				+ " should have been rejected.", token.parse(migrationRule,
				"Invalid"+invalid+"Key", gameModeName));
			assertNull("Newkey", migrationRule.getNewKey());
		}
		
	}
	
	
	@Test
	public void testParseValidKeySource()
	{
		String validChars = ",:.";
		for (char valid : validChars.toCharArray())
		{
			String keyValue = "ValidKey" + valid;
			assertTrue("Key containing " + valid
				+ " should have been accepted.", token.parse(migrationRule,
				keyValue, gameModeName));
			assertEquals("Newkey", keyValue, migrationRule.getNewKey());
		}

		for (char valid : validChars.toCharArray())
		{
			String keyValue = valid+"ValidKey";
			assertTrue("Key containing " + valid
				+ " should have been accepted.", token.parse(migrationRule,
				keyValue, gameModeName));
			assertEquals("Newkey", keyValue, migrationRule.getNewKey());
		}


		for (char valid : validChars.toCharArray())
		{
			String keyValue = "Valid"+valid+"Key";
			assertTrue("Key containing " + valid
				+ " should have been accepted.", token.parse(migrationRule,
					keyValue, gameModeName));
			assertEquals("Newkey", keyValue, migrationRule.getNewKey());
		}
		
	}

}
