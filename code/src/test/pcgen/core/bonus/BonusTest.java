/*
 * BonusTest.java
 * Copyright 2004 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on Sep 5, 2004
 *
 * $Id$
 *
 */
package pcgen.core.bonus;

import pcgen.AbstractCharacterTestCase;
import pcgen.core.Equipment;
import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.character.EquipSet;
import plugin.bonustokens.Var;

/**
 * <code>BonusTest</code> test that the Bonus class is functioning
 * correctly.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */

public class BonusTest extends AbstractCharacterTestCase
{

	/**
	 *
	 */
	public BonusTest() {
		super();
	}

	/**
	 * @param arg0
	 */
	public BonusTest(final String arg0) {
		super(arg0);
	}

	/**
	 * Test the skill pre reqs
	 * @throws Exception
	 */
	public void testSkillPrereq() throws Exception
	{
		final Skill rideSkill = new Skill();
		rideSkill.setName("Ride");
		rideSkill.setKeyName("Ride");
		rideSkill.setTypeInfo("DEX");
		final Ability skillFocus = new Ability();
		skillFocus.setName("Skill Focus");
		skillFocus.setKeyName("Skill Focus");

		final PlayerCharacter character = getCharacter();
		Globals.setCurrentPC(character);
		final BonusObj saddleBonus = Bonus
			.newBonus("SKILL|Ride|-5|!PREITEM:1,TYPE.Saddle");
		saddleBonus.setCreatorObject(rideSkill);
		rideSkill.addBonusList(saddleBonus);

		final Equipment saddle = new Equipment();
		saddle.setName("Saddle, Test");
		saddle.setTypeInfo("SADDLE");

		final PlayerCharacter pc = getCharacter();
		rideSkill.activateBonuses(pc);
		final double iBonus = rideSkill.calcBonusFrom(saddleBonus, pc, pc);
		assertEquals("Bonus value", -5.0, iBonus, 0.05);
		assertTrue("No saddle, should have a penalty", saddleBonus.isApplied());

		pc.addEquipment(saddle);
		final EquipSet eqSet = new EquipSet("Test", "Test", "", saddle);
		pc.addEquipSet(eqSet);
		pc.calcActiveBonuses();
		rideSkill.activateBonuses(pc);
		assertFalse("Saddle, should not have a penalty", saddleBonus.isApplied());
	}
	
	/**
	 * Test the processing of bonusing variables using both 
	 * abilities and equipment.
	 */
	public void testVarBonus()
	{
		Ability dummyFeat = new Ability();
		dummyFeat.setName("DummyFeat");
		final PlayerCharacter pc = getCharacter();
		Globals.setCurrentPC(pc);
		
		// Create a variable
		dummyFeat.addVariable(-1, "NegLevels", "0");
		
		// Create a bonus to it
		Ability dummyFeat2 = new Ability();
		dummyFeat2.setName("DummyFeat2");
		dummyFeat2.addBonusList("VAR|NegLevels|7");

		Equipment equip = new Equipment();
		equip.setName("DummyEquip");
		equip.addBonusList("VAR|NegLevels|2");

		assertEquals("Variable value", 0.0, pc
			.getVariableValue("NegLevels", "").doubleValue(), 0.05);
		pc.addFeat(dummyFeat, null);
		assertEquals("Variable value", 0.0, pc
			.getVariableValue("NegLevels", "").doubleValue(), 0.05);
		assertEquals("Variable value", -0.0, pc
			.getVariableValue("-1*NegLevels", "").doubleValue(), 0.05);

		// Add a bonus to it
		pc.addFeat(dummyFeat2, null);
		assertEquals("Variable value", 7.0, pc
			.getVariableValue("NegLevels", "").doubleValue(), 0.05);
		assertEquals("Variable value", -7.0, pc
			.getVariableValue("-1*NegLevels", "").doubleValue(), 0.05);

		// Add the equipment that gives a bonus to NegLevels
		EquipSet def = new EquipSet("0.1", "Default");
		pc.addEquipSet(def);
		final EquipSet eqSet = new EquipSet("0.1.1", "Equipped", "Test", equip);
		pc.addEquipSet(eqSet);
		pc.setCalcEquipmentList();
		pc.calcActiveBonuses();
		dummyFeat.activateBonuses(pc);

		assertEquals("Variable value", 9.0, pc
			.getVariableValue("NegLevels", "").doubleValue(), 0.05);
		assertEquals("Variable value", -9.0, pc
			.getVariableValue("-1*NegLevels", "").doubleValue(), 0.05);
	}
	
	/**
	 * Test the buildDepends method of BonusObj. 
	 */
	public void testBuildDepends()
	{
		BonusObj maxDexStr = new Var();
		maxDexStr.setValue("VAR|NegLevels|max(STR,DEX)-STR");
		assertTrue("Should have flagged a dependancy on DEX", maxDexStr.getDependsOn("DEX"));
		assertTrue("Should have flagged a dependancy on STR", maxDexStr.getDependsOn("STR"));

		BonusObj monkMove = new Var();
		monkMove.setValue("VAR|MonkMove|floor(var(\"monkLvl\")/3)*10");
		assertTrue("Should have flagged a dependancy on monkLvl", monkMove.getDependsOn("MONKLVL"));
	}
}
