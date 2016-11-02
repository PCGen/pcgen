/*
 * EquipmentMigrationTest.java
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
 *
 * Created on 02/06/2013
 *
 * $Id$
 */
package pcgen.io.migration;

import junit.framework.TestCase;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.core.system.MigrationRule;
import pcgen.core.system.MigrationRule.ObjectType;

/**
 * EquipmentMigrationTest checks the function of EquipmentMigration. 
 * 
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 */
public class EquipmentMigrationTest extends TestCase
{
	
	private String gameMode;

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		gameMode = SettingsHandler.getGame().getName();
		MigrationRule equipRule = new MigrationRule(ObjectType.EQUIPMENT, "OldKey1");
		equipRule.setMaxVer("6.0.1");
		equipRule.setNewKey("NewKey1");
		SystemCollections.addToMigrationRulesList(equipRule, gameMode);
		MigrationRule equipRule2 = new MigrationRule(ObjectType.EQUIPMENT, "OldKey2");
		equipRule2.setMaxVer("6.0.1");
		equipRule2.setMinVer("5.17.7");
		equipRule2.setNewKey("LateNewKey");
		SystemCollections.addToMigrationRulesList(equipRule2, gameMode);
		MigrationRule equipRule2A = new MigrationRule(ObjectType.EQUIPMENT, "OldKey2");
		equipRule2A.setMaxVer("5.16.4");
		equipRule2A.setMaxDevVer("5.17.5");
		equipRule2A.setNewKey("EarlyNewKey");
		SystemCollections.addToMigrationRulesList(equipRule2A, gameMode);
		MigrationRule equipRuleDiffGame = new MigrationRule(ObjectType.EQUIPMENT, "OldKey3");
		equipRuleDiffGame.setMaxVer("6.0.0");
		equipRuleDiffGame.setNewKey("NewKeyModern");
		SystemCollections.addToMigrationRulesList(equipRuleDiffGame, "modern");
	}

	@Override
	public void tearDown() throws Exception
	{
		SystemCollections.clearMigrationRuleMap();
		super.tearDown();
	}


	/**
	 * Test that rules for max version only are applied correctly.  
	 */
	public void testMaxVer()
	{
		assertEquals("NewKey1", EquipmentMigration.getNewEquipmentKey("OldKey1", new int[]{6,0,0}, gameMode));
		assertEquals("OldKey1", EquipmentMigration.getNewEquipmentKey("OldKey1", new int[]{6,0,2}, gameMode));
	}

	/**
	 * Check that migration rules for other game modes don't affect each other.  
	 */
	public void testNoCrossGameMode()
	{
		assertEquals("OldKey3", EquipmentMigration.getNewEquipmentKey("OldKey3", new int[]{6,0,0}, gameMode));
		assertEquals("OldKey3", EquipmentMigration.getNewEquipmentKey("OldKey3", new int[]{5,17,0}, gameMode));
	}

	/**
	 * Test that matches are case insensitive.  
	 */
	public void testCaseInsensitive()
	{
		assertEquals("NewKey1", EquipmentMigration.getNewEquipmentKey("OldKEY1", new int[]{6,0,0}, gameMode));
	}

}
