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

import java.util.ArrayList;
import java.util.List;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.helper.PointCost;
import pcgen.core.Ability;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.analysis.SpellPoint;
import pcgen.core.character.EquipSet;
import pcgen.core.spell.Spell;
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

@SuppressWarnings("nls")
public class BonusTest extends AbstractCharacterTestCase
{

	/**
	 *
	 */
	public BonusTest()
	{
		super();
	}

	/**
	 * @param arg0
	 */
	public BonusTest(final String arg0)
	{
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
		rideSkill.put(StringKey.KEY_NAME, "Ride");
		rideSkill.addToListFor(ListKey.TYPE, Type.getConstant("DEX"));
		final Ability skillFocus = new Ability();
		skillFocus.setName("Skill Focus");
		skillFocus.put(StringKey.KEY_NAME, "Skill Focus");

		final PlayerCharacter character = getCharacter();
		Globals.setCurrentPC(character);
		final BonusObj saddleBonus =
				Bonus.newBonus("SKILL|Ride|-5|!PREITEM:1,TYPE.Saddle");
		rideSkill.addToListFor(ListKey.BONUS, saddleBonus);

		final Equipment saddle = new Equipment();
		saddle.setName("Saddle, Test");
		saddle.addToListFor(ListKey.TYPE, Type.getConstant("SADDLE"));

		final PlayerCharacter pc = getCharacter();
		rideSkill.activateBonuses(pc);
		double iBonus = saddleBonus.resolve(pc, "").doubleValue();
		assertEquals("Bonus value", -5.0, iBonus, 0.05);
		assertTrue("No saddle, should have a penalty", pc.isApplied(saddleBonus));

		pc.addEquipment(saddle);
		final EquipSet eqSet = new EquipSet("Test", "Test", "", saddle);
		pc.addEquipSet(eqSet);
		pc.calcActiveBonuses();
		rideSkill.activateBonuses(pc);
		assertFalse("Saddle, should not have a penalty", pc
				.isApplied(saddleBonus));
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
		dummyFeat.put(VariableKey.getConstant("NegLevels"), FormulaFactory.ZERO);

		// Create a bonus to it
		Ability dummyFeat2 = new Ability();
		dummyFeat2.setName("DummyFeat2");
		BonusObj aBonus = Bonus.newBonus("VAR|NegLevels|7");
		
		if (aBonus != null)
		{
			dummyFeat2.addToListFor(ListKey.BONUS, aBonus);
		}

		Equipment equip = new Equipment();
		equip.setName("DummyEquip");
		aBonus = Bonus.newBonus("VAR|NegLevels|2");
		
		if (aBonus != null)
		{
			equip.addToListFor(ListKey.BONUS, aBonus);
		}

		assertEquals("Variable value", 0.0, pc
			.getVariableValue("NegLevels", "").doubleValue(), 0.05);
		pc.addFeat(dummyFeat, null);
		assertEquals("Variable value", 0.0, pc
			.getVariableValue("NegLevels", "").doubleValue(), 0.05);
		assertEquals("Variable value", -0.0, pc.getVariableValue(
			"-1*NegLevels", "").doubleValue(), 0.05);

		// Add a bonus to it
		pc.addFeat(dummyFeat2, null);
		assertEquals("Variable value", 7.0, pc
			.getVariableValue("NegLevels", "").doubleValue(), 0.05);
		assertEquals("Variable value", -7.0, pc.getVariableValue(
			"-1*NegLevels", "").doubleValue(), 0.05);

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
		assertEquals("Variable value", -9.0, pc.getVariableValue(
			"-1*NegLevels", "").doubleValue(), 0.05);
	}

	/**
	 * Test the buildDepends method of BonusObj. 
	 */
	public void testBuildDepends()
	{
		BonusObj maxDexStr = new Var();
		maxDexStr.setValue("VAR|NegLevels|max(STR,DEX)-STR");
		assertTrue("Should have flagged a dependancy on DEX", maxDexStr
			.getDependsOn("DEX"));
		assertTrue("Should have flagged a dependancy on STR", maxDexStr
			.getDependsOn("STR"));

		BonusObj monkMove = new Var();
		monkMove.setValue("VAR|MonkMove|floor(var(\"monkLvl\")/3)*10");
		assertTrue("Should have flagged a dependancy on monkLvl", monkMove
			.getDependsOn("MONKLVL"));
	}
	public void testSpellPointCost()
	{
		Spell sp = new Spell();
		sp.setName("Test");
		sp.addToListFor(ListKey.SPELL_SCHOOL, "INFUSE");
		sp.addToListFor(ListKey.SPELL_POINT_COST, new PointCost("Duration", 4));
		sp.addToListFor(ListKey.SPELL_POINT_COST, new PointCost("Infuse Fire", 4));
		
		int spCosts = SpellPoint.getSpellPointCostActual(sp);
		
		final PlayerCharacter character = getCharacter();
		Globals.setCurrentPC(character);
		final BonusObj spCost =
				Bonus.newBonus("SPELLPOINTCOST|SCHOOL.Infuse;Duration|2|TYPE=Specialist");
		sp.addToListFor(ListKey.BONUS, spCost);
		sp.activateBonuses(character);
		
		int a = spCost.resolve(character, "").intValue();
		assertEquals(10, spCosts + a);
	}
	
	/**
	 * Test to make sure that fix for replacing %LIST within a 
	 * bonuses value will work.
	 */
	public void testBonuswithLISTValue()
	{
		final PlayerCharacter character = getCharacter();

		Globals.setCurrentPC(character);
		setPCStat(character, intel, 18);
		final BonusObj bonus =
				Bonus.newBonus("VISION|Darkvision|%LIST+10|TYPE=Magical Boon");
		ArrayList<BonusObj> bonusList = new ArrayList<BonusObj>();
		bonusList.add(bonus);
		final Ability testBonus = new Ability();
		testBonus.addToListFor(ListKey.BONUS, bonus);
		character.addAssociation(testBonus, "INT");
		character.addFeat(testBonus, null);
		List<BonusPair> bonusPairs = character.getStringListFromBonus(bonus);
		assertEquals(1, bonusPairs.size());
		BonusPair bp = bonusPairs.get(0);
		assertEquals("VISION.DARKVISION:MAGICAL BOON", bp.bonusKey);
		assertEquals(14, bp.resolve(character).intValue());
	}

	public void testBonuswithLISTValueTwoAssoc()
	{
		final PlayerCharacter character = getCharacter();

		Globals.setCurrentPC(character);
		setPCStat(character, intel, 18);
		setPCStat(character, str, 16);
		final BonusObj bonus =
				Bonus.newBonus("VISION|Darkvision|%LIST+10|TYPE=Magical Boon");
		ArrayList<BonusObj> bonusList = new ArrayList<BonusObj>();
		bonusList.add(bonus);
		final Ability testBonus = new Ability();
		testBonus.addToListFor(ListKey.BONUS, bonus);
		character.addAssociation(testBonus, "INT");
		character.addAssociation(testBonus, "STR");
		character.addFeat(testBonus, null);

		List<BonusPair> bonusPairs = character.getStringListFromBonus(bonus);
		assertEquals(2, bonusPairs.size());
		int totalBonus = 0;
		BonusPair bp = bonusPairs.get(0);
		assertEquals("VISION.DARKVISION:MAGICAL BOON", bp.bonusKey);
		totalBonus += bp.resolve(character).intValue();
		bp = bonusPairs.get(1);
		assertEquals("VISION.DARKVISION:MAGICAL BOON", bp.bonusKey);
		totalBonus += bp.resolve(character).intValue();
		assertEquals(27, totalBonus);
	}

	public void testBonuswithLISTValueTwoAssocInfoList()
	{
		final PlayerCharacter character = getCharacter();

		Globals.setCurrentPC(character);
		setPCStat(character, intel, 18);
		setPCStat(character, str, 16);
		final BonusObj bonus = Bonus.newBonus("STAT|%LIST|%LIST+1");
		ArrayList<BonusObj> bonusList = new ArrayList<BonusObj>();
		bonusList.add(bonus);
		final Ability testBonus = new Ability();
		testBonus.addToListFor(ListKey.BONUS, bonus);
		character.addAssociation(testBonus, "INT");
		character.addAssociation(testBonus, "STR");
		character.addFeat(testBonus, null);

		List<BonusPair> bonusPairs = character.getStringListFromBonus(bonus);
		assertEquals(2, bonusPairs.size());
		for (BonusPair bp : bonusPairs)
		{
			if (bp.bonusKey.equals("STAT.INT"))
			{
				assertEquals(5, bp.resolve(character).intValue());
			}
			else
			{
				assertEquals(4, bp.resolve(character).intValue());
			}
		}
	}
}
