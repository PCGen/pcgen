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
import pcgen.io.migration.AbilityMigration.CategorisedKey;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * AbilityMigrationTest checks the function of AbilityMigration.
 */
public class AbilityMigrationTest
{
	
	private String gameMode;

	@BeforeEach
	public void setUp() throws Exception
	{
		gameMode = SettingsHandler.getGameAsProperty().get().getName();
		MigrationRule abilityRule = new MigrationRule(ObjectType.ABILITY, "OldCat", "OldKey1");
		abilityRule.setMaxVer("6.0.1");
		abilityRule.setNewKey("NewKey1");
		SystemCollections.addToMigrationRulesList(abilityRule, gameMode);
		MigrationRule abilityRule2 = new MigrationRule(ObjectType.ABILITY, "OldCat", "OldKey2");
		abilityRule2.setMaxVer("6.0.1");
		abilityRule2.setMinVer("5.17.7");
		abilityRule2.setNewKey("LateNewKey");
		SystemCollections.addToMigrationRulesList(abilityRule2, gameMode);
		MigrationRule abilityRule2A = new MigrationRule(ObjectType.ABILITY, "OldCat", "OldKey2");
		abilityRule2A.setMaxVer("5.16.4");
		abilityRule2A.setMaxDevVer("5.17.5");
		abilityRule2A.setNewCategory("EarlyNewCat");
		abilityRule2A.setNewKey("EarlyNewKey");
		SystemCollections.addToMigrationRulesList(abilityRule2A, gameMode);
		MigrationRule abilityRuleDiffGame = new MigrationRule(ObjectType.ABILITY, "OldCat", "OldKey3");
		abilityRuleDiffGame.setMaxVer("6.0.0");
		abilityRuleDiffGame.setNewKey("NewKeyModern");
		SystemCollections.addToMigrationRulesList(abilityRuleDiffGame, "modern");
	}

	@AfterEach
	public void tearDown() throws Exception
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
		CategorisedKey catKey = AbilityMigration.getNewAbilityKey("OldCat", "OldKey1", new int[]{6, 0, 0}, gameMode);
		assertEquals("OldCat", catKey.getCategory());
		assertEquals("NewKey1", catKey.getKey());
		catKey = AbilityMigration.getNewAbilityKey("OldCat", "OldKey1", new int[]{6, 0, 2}, gameMode);
		assertEquals("OldCat", catKey.getCategory());
		assertEquals("OldKey1", catKey.getKey());
	}

	/**
	 * Test that rules for category changes are applied correctly.  
	 */
	@Test
	public void testCatChange()
	{
		CategorisedKey catKey = AbilityMigration.getNewAbilityKey("OldCat", "OldKey2", new int[]{5, 17, 5}, gameMode);
		assertEquals("EarlyNewCat", catKey.getCategory());
		assertEquals("EarlyNewKey", catKey.getKey());
	}

	/**
	 * Test that matches are case insensitive.  
	 */
	@Test
	public void testCaseInsensitive()
	{
		CategorisedKey catKey = AbilityMigration.getNewAbilityKey("OldCAT", "OldKey1", new int[]{6, 0, 0}, gameMode);
		assertEquals("OldCAT", catKey.getCategory());
		assertEquals("NewKey1", catKey.getKey());
		catKey = AbilityMigration.getNewAbilityKey("OldCat", "OldKEY1", new int[]{6, 0, 0}, gameMode);
		assertEquals("OldCat", catKey.getCategory());
		assertEquals("NewKey1", catKey.getKey());
	}

}
