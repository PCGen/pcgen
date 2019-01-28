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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.List;

import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.core.system.MigrationRule;
import pcgen.core.system.MigrationRule.ObjectType;
import pcgen.util.TestHelper;

import org.junit.Before;
import org.junit.Test;

/**
 * MigrationLoaderTest checks the function of the MigrationLoader class.
 */
public class MigrationLoaderTest
{
	MigrationLoader migrationLoader = new MigrationLoader();
	URI sourceURI;
	
	@Before
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
		assertEquals("Object type", ObjectType.SOURCE,  migrationRule.getObjectType());
		assertEquals("Old key", "Old Key",  migrationRule.getOldKey());
		assertNull("Old category", migrationRule.getOldCategory());
	}
	
	@Test
	public void testParseFirstTokenValidAbility()
	{
		MigrationRule migrationRule = migrationLoader.parseFirstToken("ABILITY:OldCat|Old Key", "", sourceURI);
		assertNotNull("Should have been able to parse valid ability", migrationRule);
		assertEquals("Object type", ObjectType.ABILITY,  migrationRule.getObjectType());
		assertEquals("Old key", "Old Key",  migrationRule.getOldKey());
		assertEquals("Old category", "OldCat",  migrationRule.getOldCategory());
	}
	
	@Test
	public void testParseFirstTokenValidEquipment()
	{
		MigrationRule migrationRule = migrationLoader.parseFirstToken("EQUIPMENT:Old Key", "", sourceURI);
		assertNotNull(migrationRule);
		assertEquals("Object type", ObjectType.EQUIPMENT,  migrationRule.getObjectType());
		assertEquals("Old key", "Old Key",  migrationRule.getOldKey());
		assertNull("Old category", migrationRule.getOldCategory());
	}
	
	@Test
	public void testParseFirstTokenValidRace()
	{
		MigrationRule migrationRule = migrationLoader.parseFirstToken("RACE:Old Key", "", sourceURI);
		assertNotNull(migrationRule);
		assertEquals("Object type", ObjectType.RACE,  migrationRule.getObjectType());
		assertEquals("Old key", "Old Key",  migrationRule.getOldKey());
		assertNull("Old category", migrationRule.getOldCategory());
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
			assertNull("Key containing " + invalid + " should have been rejected.", migrationRule);
		}
	}
	
	@Test
	public void testParseFirstTokenValidCharsInSourceKey()
	{
		String sourceKey = "Paizo - Second Darkness, Chapter 6: Descent into Midnight.";
		MigrationRule migrationRule = migrationLoader.parseFirstToken("SOURCE:" + sourceKey, "", sourceURI);
		assertNotNull("Key should have been accepted.", migrationRule);
		assertEquals("Source key should have been recorded", sourceKey, migrationRule.getOldKey());
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
			assertNull("Key containing " + invalid + " should have been rejected.", migrationRule);
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
			assertNull("Key containing " + invalid + " should have been rejected.", migrationRule);
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
				assertNull("new category", migrationRule.getNewCategory());
				assertEquals("new key", "Animal Fury ~ Rage Power", migrationRule.getNewKey());
				assertEquals("max ver", "6.00.00", migrationRule.getMaxVer());
				assertEquals("max dev ver", "6.01.01", migrationRule.getMaxDevVer());
				found = true;
			}
		}
		
		assertTrue("Unable to find migration rule", found);
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
				assertNull("new category", migrationRule.getNewCategory());
				assertEquals("new key", "XYZ - Bobs Magic Store", migrationRule.getNewKey());
				assertEquals("max ver", "5.17.10", migrationRule.getMaxVer());
				assertNull("max dev ver", migrationRule.getMaxDevVer());
				found = true;
			}
		}
		
		assertTrue("Unable to find migration rule", found);
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
				&& migrationRule.getOldKey().equals("Bob's Magic Store"))
			{
				found = true;
			}
		}
		
		assertFalse("Invalid source line was accepted.", found);
	}
}
