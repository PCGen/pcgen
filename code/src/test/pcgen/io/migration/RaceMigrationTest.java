/*
 * RaceMigrationTest.java
 * Copyright 2014 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 *
 * Created on 18/01/2014
 *
 * $Id$
 */
package pcgen.io.migration;

import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.core.system.MigrationRule;
import pcgen.core.system.MigrationRule.ObjectType;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * RaceMigrationTest checks the function of RaceMigration. 
 * 
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 */
public class RaceMigrationTest
{
	
	private String gameMode;

	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		gameMode = SettingsHandler.getGame().getName();
		MigrationRule raceRule = new MigrationRule(ObjectType.RACE, "OldKey1");
		raceRule.setMaxVer("6.0.1");
		raceRule.setNewKey("NewKey1");
		SystemCollections.addToMigrationRulesList(raceRule, gameMode);
		MigrationRule raceRule2 = new MigrationRule(ObjectType.RACE, "OldKey2");
		raceRule2.setMaxVer("6.0.1");
		raceRule2.setMinVer("5.17.7");
		raceRule2.setNewKey("LateNewKey");
		SystemCollections.addToMigrationRulesList(raceRule2, gameMode);
		MigrationRule raceRule2A = new MigrationRule(ObjectType.RACE, "OldKey2");
		raceRule2A.setMaxVer("5.16.4");
		raceRule2A.setMaxDevVer("5.17.5");
		raceRule2A.setNewKey("EarlyNewKey");
		SystemCollections.addToMigrationRulesList(raceRule2A, gameMode);
		MigrationRule raceRuleDiffGame = new MigrationRule(ObjectType.RACE, "OldKey3");
		raceRuleDiffGame.setMaxVer("6.0.0");
		raceRuleDiffGame.setNewKey("NewKeyModern");
		SystemCollections.addToMigrationRulesList(raceRuleDiffGame, "modern");
	}

	@After
	public void tearDown() throws Exception
	{
		SystemCollections.clearMigrationRuleMap();
	}


	/**
	 * Test that rules for max version only are applied correctly.  
	 */
	@Test
	public void testMaxVer()
	{
		Assert.assertEquals(
				"NewKey1",
				RaceMigration.getNewRaceKey("OldKey1", new int[]{6, 0, 0}, gameMode)
		);
		Assert.assertEquals(
				"OldKey1",
				RaceMigration.getNewRaceKey("OldKey1", new int[]{6, 0, 2}, gameMode)
		);
	}

	/**
	 * Check that migration rules for other game modes don't affect each other.  
	 */
	@Test
	public void testNoCrossGameMode()
	{
		Assert.assertEquals(
				"OldKey3",
				RaceMigration.getNewRaceKey("OldKey3", new int[]{6, 0, 0}, gameMode)
		);
		Assert.assertEquals(
				"OldKey3",
				RaceMigration.getNewRaceKey("OldKey3", new int[]{5, 17, 0}, gameMode)
		);
	}

	/**
	 * Test that matches are case insensitive.  
	 */
	@Test
	public void testCaseInsensitive()
	{
		Assert.assertEquals(
				"NewKey1",
				RaceMigration.getNewRaceKey("OldKEY1", new int[]{6, 0, 0}, gameMode)
		);
	}

}
