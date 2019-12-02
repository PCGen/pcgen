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
package pcgen.persistence.lst;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.List;

import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.core.system.MigrationRule;
import pcgen.core.system.MigrationRule.ObjectType;
import pcgen.util.TestHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * MigrationLoaderTest checks the function of the MigrationLoader class.
 */
public class MigrationLoaderTest
{
	MigrationLoader migrationLoader = new MigrationLoader();
	URI sourceURI;
	
	@BeforeEach
	public void setUp() throws Exception
	{
		sourceURI = new URI("http://www.pcgen.org");
		TestHelper.loadPlugins();
	}
	
	@Test
	public void testParseFirstTokenValidSource()
	{
		MigrationRule migrationRule = migrationLoader.parseFirstToken("SOURCE:Old Key", "", sourceURI);
		assertNotNull(migrationRule);
		assertEquals(ObjectType.SOURCE,  migrationRule.getObjectType(), "Object type");
		assertEquals("Old Key",  migrationRule.getOldKey(), "Old key");
		assertNull(migrationRule.getOldCategory(), "Old category");
	}
	
	@Test
	public void testParseFirstTokenValidAbility()
	{
		MigrationRule migrationRule = migrationLoader.parseFirstToken("ABILITY:OldCat|Old Key", "", sourceURI);
		assertNotNull(migrationRule, "Should have been able to parse valid ability");
		assertEquals(ObjectType.ABILITY,  migrationRule.getObjectType(), "Object type");
		assertEquals("Old Key",  migrationRule.getOldKey(), "Old key");
		assertEquals("OldCat",  migrationRule.getOldCategory(), "Old category");
	}
	
	@Test
	public void testParseFirstTokenValidEquipment()
	{
		MigrationRule migrationRule = migrationLoader.parseFirstToken("EQUIPMENT:Old Key", "", sourceURI);
		assertNotNull(migrationRule);
		assertEquals(ObjectType.EQUIPMENT,  migrationRule.getObjectType(), "Object type");
		assertEquals("Old Key",  migrationRule.getOldKey(), "Old key");
		assertNull(migrationRule.getOldCategory(), "Old category");
	}
	
	@Test
	public void testParseFirstTokenValidRace()
	{
		MigrationRule migrationRule = migrationLoader.parseFirstToken("RACE:Old Key", "", sourceURI);
		assertNotNull(migrationRule);
		assertEquals(ObjectType.RACE,  migrationRule.getObjectType(), "Object type");
		assertEquals("Old Key",  migrationRule.getOldKey(), "Old key");
		assertNull(migrationRule.getOldCategory(), "Old category");
	}
	
	@Test
	public void testParseFirstTokenInValidObjType()
	{
		MigrationRule migrationRule = migrationLoader.parseFirstToken("FOO:Old Key", "", sourceURI);
		assertNull(migrationRule);
	}
	
	/**
	 * Check that these invalid characters are rejected in a source key |;.%*=[]
	 */
	@Test
	public void testParseFirstTokenInValidCharsInSourceKey()
	{
		String invalidChars = "|;%*=[]";
		for (char invalid : invalidChars.toCharArray())
		{
			MigrationRule migrationRule = migrationLoader.parseFirstToken("SOURCE:Old"+invalid, "", sourceURI);
			assertNull(migrationRule, "Key containing " + invalid + " should have been rejected.");
		}
	}
	
	@Test
	public void testParseFirstTokenValidCharsInSourceKey()
	{
		String sourceKey = "Paizo - Second Darkness, Chapter 6: Descent into Midnight.";
		MigrationRule migrationRule = migrationLoader.parseFirstToken("SOURCE:" + sourceKey, "", sourceURI);
		assertNotNull(migrationRule, "Key should have been accepted.");
		assertEquals(sourceKey, migrationRule.getOldKey(), "Source key should have been recorded");
	}
	
	/**
	 * Check that these invalid characters are rejected in a category ,\\|\\:;.%*=[]
	 */
	@Test
	public void testParseFirstTokenInValidCharsInAbilityCategory()
	{
		String invalidChars = ",|\\:;%*=[]";
		for (char invalid : invalidChars.toCharArray())
		{
			MigrationRule migrationRule = migrationLoader.parseFirstToken("ABILITY:Old"+invalid+"|Key", "", sourceURI);
			assertNull(migrationRule, "Key containing " + invalid + " should have been rejected.");
		}
	}
	
	/**
	 * Check that these invalid characters are rejected in a key ,\\|\\:;.%*=[]
	 */
	@Test
	public void testParseFirstTokenInValidCharsInAbilityKey()
	{
		String invalidChars = ",|\\:;%*=[]";
		for (char invalid : invalidChars.toCharArray())
		{
			MigrationRule migrationRule = migrationLoader.parseFirstToken("ABILITY:Old|Key"+invalid, "", sourceURI);
			assertNull(migrationRule, "Key containing " + invalid + " should have been rejected.");
		}
	}

	@Test
	public void testParseAbilityLine() throws Exception
	{
		String abilityMigration = "ABILITY:Special Ability|Animal Fury	NEWKEY:Animal Fury ~ Rage Power	"
									+ "MAXVER:6.00.00	MAXDEVVER:6.01.01";
		migrationLoader.parseLine(null, abilityMigration, new URI("http://UNIT_TEST_CASE"));
		boolean found = false;
		List<MigrationRule> migrationRuleList =
				SystemCollections
					.getUnmodifiableMigrationRuleList(SettingsHandler.getGame()
						.getName());
		for (MigrationRule migrationRule : migrationRuleList)
		{
			if (migrationRule.getObjectType() == ObjectType.ABILITY
				&& migrationRule.getOldCategory().equals("Special Ability")
				&& migrationRule.getOldKey().equals("Animal Fury"))
			{
				assertNull(migrationRule.getNewCategory(), "new category");
				assertEquals("Animal Fury ~ Rage Power", migrationRule.getNewKey(), "new key");
				assertEquals("6.00.00", migrationRule.getMaxVer(), "max ver");
				assertEquals("6.01.01", migrationRule.getMaxDevVer(), "max dev ver");
				found = true;
			}
		}
		
		assertTrue(found, "Unable to find migration rule");
	}

	@Test
	public void testParseSourceLine() throws Exception
	{
		String sourceMigration = "SOURCE:Bob's Magic Store	NEWKEY:XYZ - Bobs Magic Store	MAXVER:5.17.10";
		migrationLoader.parseLine(null, sourceMigration, new URI("http://UNIT_TEST_CASE"));
		boolean found = false;
		List<MigrationRule> migrationRuleList =
				SystemCollections
					.getUnmodifiableMigrationRuleList(SettingsHandler.getGame()
						.getName());
		for (MigrationRule migrationRule : migrationRuleList)
		{
			if (migrationRule.getObjectType() == ObjectType.SOURCE
				&& migrationRule.getOldKey().equals("Bob's Magic Store"))
			{
				assertNull(migrationRule.getNewCategory(), "new category");
				assertEquals("XYZ - Bobs Magic Store", migrationRule.getNewKey(), "new key");
				assertEquals("5.17.10", migrationRule.getMaxVer(), "max ver");
				assertNull(migrationRule.getMaxDevVer(), "max dev ver");
				found = true;
			}
		}
		
		assertTrue(found, "Unable to find migration rule");
	}

	@Test
	public void testParseInvalidSourceLine() throws Exception
	{
		String sourceMigration = "SOURCE:Bob's Magic Store	NEWKEY:XYZ - Bobs Magic Store";
		migrationLoader.parseLine(null, sourceMigration, new URI("http://UNIT_TEST_CASE"));
		boolean found = false;
		List<MigrationRule> migrationRuleList =
				SystemCollections
					.getUnmodifiableMigrationRuleList(SettingsHandler.getGame()
						.getName());
		for (MigrationRule migrationRule : migrationRuleList)
		{
            if (migrationRule.getObjectType() == ObjectType.SOURCE
                    && migrationRule.getOldKey().equals("Bob's Magic Store")) {
                found = true;
                break;
            }
		}
		
		assertFalse(found, "Invalid source line was accepted.");
	}
}
