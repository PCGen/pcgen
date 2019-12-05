/*
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
 */
package pcgen.core.bonus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.core.Ability;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.analysis.BonusActivation;
import pcgen.core.character.EquipSet;
import pcgen.rules.context.LoadContext;
import plugin.bonustokens.Var;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.Test;

/**
 * {@code BonusTest} test that the Bonus class is functioning
 * correctly.
 */

@SuppressWarnings("nls")
public class BonusTest extends AbstractCharacterTestCase
{
    /**
     * Test the skill pre reqs.
     */
    @Test
    public void testSkillPrereq()
    {
        LoadContext context = Globals.getContext();

        final Skill rideSkill = new Skill();
        rideSkill.setName("Ride");
        rideSkill.put(StringKey.KEY_NAME, "Ride");
        rideSkill.addToListFor(ListKey.TYPE, Type.getConstant("DEX"));
        context.getReferenceContext().importObject(rideSkill);
        final Ability skillFocus = new Ability();
        skillFocus.setName("Skill Focus");
        skillFocus.put(StringKey.KEY_NAME, "Skill Focus");

        final BonusObj saddleBonus =
                Bonus.newBonus(context, "SKILL|Ride|-5|!PREITEM:1,TYPE.Saddle");
        rideSkill.addToListFor(ListKey.BONUS, saddleBonus);

        final Equipment saddle = new Equipment();
        saddle.setName("Saddle, Test");
        saddle.addToListFor(ListKey.TYPE, Type.getConstant("SADDLE"));

        finishLoad();

        final PlayerCharacter pc = getCharacter();
        BonusActivation.activateBonuses(rideSkill, pc);
        double iBonus = saddleBonus.resolve(pc, "").doubleValue();
        assertEquals(-5.0, iBonus, 0.05, "Bonus value");
        BonusObj appliedBonus = rideSkill.getListFor(ListKey.BONUS).get(0);
        assertTrue(pc.isApplied(appliedBonus), "No saddle, should have a penalty");
        assertEquals(appliedBonus.toString(), saddleBonus.toString());

        pc.addEquipment(saddle);
        final EquipSet eqSet = new EquipSet("Test", "Test", "", saddle);
        pc.addEquipSet(eqSet);
        pc.calcActiveBonuses();
        BonusActivation.activateBonuses(rideSkill, pc);
        assertFalse(pc
                .isApplied(saddleBonus), "Saddle, should not have a penalty");
    }

    /**
     * Test the processing of bonusing variables using both
     * abilities and equipment.
     */
    @Test
    public void testVarBonus()
    {
        LoadContext context = Globals.getContext();

        Ability dummyFeat = new Ability();
        dummyFeat.setName("DummyFeat");
        dummyFeat.setCDOMCategory(BuildUtilities.getFeatCat());

        // Create a variable
        dummyFeat.put(VariableKey.getConstant("NegLevels"), FormulaFactory.ZERO);

        // Create a bonus to it
        Ability dummyFeat2 = new Ability();
        dummyFeat2.setName("DummyFeat2");
        dummyFeat2.setCDOMCategory(BuildUtilities.getFeatCat());
        BonusObj aBonus = Bonus.newBonus(context, "VAR|NegLevels|7");

        if (aBonus != null)
        {
            dummyFeat2.addToListFor(ListKey.BONUS, aBonus);
        }

        Equipment equip = new Equipment();
        equip.setName("DummyEquip");
        aBonus = Bonus.newBonus(context, "VAR|NegLevels|2");

        if (aBonus != null)
        {
            equip.addToListFor(ListKey.BONUS, aBonus);
        }

        finishLoad();

        final PlayerCharacter pc = getCharacter();
        assertEquals(0.0, pc
                .getVariableValue("NegLevels", "").doubleValue(), 0.05, "Variable value");
        addAbility(BuildUtilities.getFeatCat(), dummyFeat);
        assertEquals(0.0, pc
                .getVariableValue("NegLevels", "").doubleValue(), 0.05, "Variable value");
        assertEquals(-0.0, pc.getVariableValue(
                "-1*NegLevels", "").doubleValue(), 0.05, "Variable value");

        // Add a bonus to it
        addAbility(BuildUtilities.getFeatCat(), dummyFeat2);
        assertEquals(7.0, pc
                .getVariableValue("NegLevels", "").doubleValue(), 0.05, "Variable value");
        assertEquals(-7.0, pc.getVariableValue(
                "-1*NegLevels", "").doubleValue(), 0.05, "Variable value");

        // Add the equipment that gives a bonus to NegLevels
        EquipSet def = new EquipSet("0.1", "Default");
        pc.addEquipSet(def);
        final EquipSet eqSet = new EquipSet("0.1.1", "Equipped", "Test", equip);
        pc.addEquipSet(eqSet);
        pc.setCalcEquipmentList();
        pc.calcActiveBonuses();
        BonusActivation.activateBonuses(dummyFeat, pc);

        assertEquals(9.0, pc
                .getVariableValue("NegLevels", "").doubleValue(), 0.05, "Variable value");
        assertEquals(-9.0, pc.getVariableValue(
                "-1*NegLevels", "").doubleValue(), 0.05, "Variable value");
    }

    /**
     * Test the buildDepends method of BonusObj.
     */
    @Test
    public void testBuildDepends()
    {
        BonusObj maxDexStr = new Var();
        maxDexStr.setValue("VAR|NegLevels|max(STR,DEX)-STR");
        assertTrue(maxDexStr
                .getDependsOn("DEX"), "Should have flagged a dependancy on DEX");
        assertTrue(maxDexStr
                .getDependsOn("STR"), "Should have flagged a dependancy on STR");

        BonusObj monkMove = new Var();
        monkMove.setValue("VAR|MonkMove|floor(var(\"monkLvl\")/3)*10");
        assertTrue(monkMove
                .getDependsOn("MONKLVL"), "Should have flagged a dependancy on monkLvl");

        BonusObj condSkill = new plugin.bonustokens.Skill();
        condSkill.setValue("SKILL.CONCENTRATION.MISC");
        assertFalse(condSkill
                .getDependsOn("STAT"), "Should not have flagged a dependancy on stat");
        assertTrue(condSkill
                .getDependsOn("CONCENTRATION"), "Should have flagged a dependancy on concentration");
        assertTrue(condSkill
                .getDependsOnBonusName("STAT"), "Should have flagged a name dependancy on stat");

        condSkill = new plugin.bonustokens.Skill();
        condSkill.setValue("skillinfo(\"TOTALRANK\", \"SEARCH\")");
        assertFalse(condSkill
                .getDependsOn("STAT"), "Should not have flagged a dependancy on stat");
        assertTrue(condSkill
                .getDependsOn("SEARCH"), "Should have flagged a dependancy on search");
        assertTrue(condSkill
                .getDependsOnBonusName("STAT"), "Should have flagged a name dependancy on stat");
    }

    /**
     * Test to make sure that fix for replacing %LIST within a
     * bonuses value will work.
     */
    @Test
    public void testBonuswithLISTValue()
    {
        finishLoad();
        final PlayerCharacter character = getCharacter();
        LoadContext context = Globals.getContext();

        setPCStat(character, intel, 18);
        BonusObj bonus =
                Bonus.newBonus(context, "VISION|Darkvision|%LIST+10|TYPE=Magical Boon");
        List<BonusObj> bonusList = new ArrayList<>();
        bonusList.add(bonus);
        Ability testBonus = new Ability();
        testBonus.setName("TB1Assoc");
        testBonus.setCDOMCategory(BuildUtilities.getFeatCat());
        testBonus.addToListFor(ListKey.BONUS, bonus);
        Globals.getContext().unconditionallyProcess(testBonus, "CHOOSE", "PCSTAT|ALL");
        Globals.getContext().unconditionallyProcess(testBonus, "MULT", "YES");
        CNAbility cna = AbstractCharacterTestCase.applyAbility(character,
                BuildUtilities.getFeatCat(), testBonus, "INT");
        testBonus = cna.getAbility();
        character.calcActiveBonuses();
        bonus = testBonus.getSafeListFor(ListKey.BONUS).get(0);
        List<BonusPair> bonusPairs = character.getStringListFromBonus(bonus);
        assertEquals(1, bonusPairs.size());
        BonusPair bp = bonusPairs.get(0);
        assertEquals("VISION.DARKVISION:MAGICAL BOON", bp.fullyQualifiedBonusType);
        assertEquals(14, bp.resolve(character).intValue());
    }

    @Test
    public void testBonuswithLISTValueTwoAssoc()
    {
        finishLoad();
        final PlayerCharacter character = getCharacter();
        LoadContext context = Globals.getContext();

        setPCStat(character, intel, 18);
        setPCStat(character, str, 16);
        BonusObj bonus =
                Bonus.newBonus(context, "VISION|Darkvision|%LIST+10|TYPE=Magical Boon");
        List<BonusObj> bonusList = new ArrayList<>();
        bonusList.add(bonus);
        Ability testBonus = new Ability();
        testBonus.setName("TB2Assoc");
        testBonus.setCDOMCategory(BuildUtilities.getFeatCat());
        testBonus.addToListFor(ListKey.BONUS, bonus);
        Globals.getContext().unconditionallyProcess(testBonus, "CHOOSE", "PCSTAT|ALL");
        Globals.getContext().unconditionallyProcess(testBonus, "MULT", "YES");
        CNAbility cna = AbstractCharacterTestCase.applyAbility(character,
                BuildUtilities.getFeatCat(), testBonus, "INT");
        testBonus = cna.getAbility();
        AbstractCharacterTestCase.applyAbility(character, BuildUtilities.getFeatCat(),
                testBonus, "STR");
        character.calcActiveBonuses();
        bonus = testBonus.getSafeListFor(ListKey.BONUS).get(0);

        List<BonusPair> bonusPairs = character.getStringListFromBonus(bonus);
        assertEquals(2, bonusPairs.size());
        int totalBonus = 0;
        BonusPair bp = bonusPairs.get(0);
        assertEquals("VISION.DARKVISION:MAGICAL BOON", bp.fullyQualifiedBonusType);
        totalBonus += bp.resolve(character).intValue();
        bp = bonusPairs.get(1);
        assertEquals("VISION.DARKVISION:MAGICAL BOON", bp.fullyQualifiedBonusType);
        totalBonus += bp.resolve(character).intValue();
        assertEquals(27, totalBonus);
    }

    @Test
    public void testBonuswithLISTValueTwoAssocInfoList()
    {
        finishLoad();
        final PlayerCharacter character = getCharacter();
        LoadContext context = Globals.getContext();

        setPCStat(character, intel, 18);
        setPCStat(character, str, 16);
        BonusObj bonus = Bonus.newBonus(context, "STAT|%LIST|%LIST+1");
        List<BonusObj> bonusList = new ArrayList<>();
        bonusList.add(bonus);
        Ability testBonus = new Ability();
        testBonus.setName("TB2AssocList");
        testBonus.setCDOMCategory(BuildUtilities.getFeatCat());
        Globals.getContext().unconditionallyProcess(testBonus, "CHOOSE", "PCSTAT|ALL");
        Globals.getContext().unconditionallyProcess(testBonus, "MULT", "YES");
        testBonus.addToListFor(ListKey.BONUS, bonus);
        CNAbility cna = AbstractCharacterTestCase.applyAbility(character,
                BuildUtilities.getFeatCat(), testBonus, "INT");
        testBonus = cna.getAbility();
        AbstractCharacterTestCase.applyAbility(character, BuildUtilities.getFeatCat(),
                testBonus, "STR");
        character.calcActiveBonuses();
        bonus = testBonus.getSafeListFor(ListKey.BONUS).get(0);

        List<BonusPair> bonusPairs = character.getStringListFromBonus(bonus);
        assertEquals(2, bonusPairs.size());
        for (BonusPair bp : bonusPairs)
        {
            if (bp.fullyQualifiedBonusType.equals("STAT.INT"))
            {
                assertEquals(5, bp.resolve(character).intValue());
            } else
            {
                assertEquals(4, bp.resolve(character).intValue());
            }
        }
    }

    /**
     * Test to make sure that fix for replacing %LIST within a
     * bonuses value will work.
     */
    @Test
    public void testSpellKnownBonusWithLISTValue()
    {
        LoadContext context = Globals.getContext();
        context.getReferenceContext().constructNowIfNecessary(PCClass.class, "Wizard");
        finishLoad();
        final PlayerCharacter character = getCharacter();

        BonusObj bonus = Bonus.newBonus(context, "SPELLKNOWN|%LIST|1");
        List<BonusObj> bonusList = new ArrayList<>();
        bonusList.add(bonus);
        Ability testBonus = new Ability();
        testBonus.setName("TB1Assoc");
        testBonus.setCDOMCategory(BuildUtilities.getFeatCat());
        testBonus.addToListFor(ListKey.BONUS, bonus);
        Globals.getContext().unconditionallyProcess(testBonus, "CHOOSE", "SPELLLEVEL|Wizard|1|5");
        Globals.getContext().unconditionallyProcess(testBonus, "MULT", "YES");
        CNAbility cna = AbstractCharacterTestCase.applyAbility(character,
                BuildUtilities.getFeatCat(), testBonus, "CLASS.Wizard;LEVEL.1");
        testBonus = cna.getAbility();
        character.calcActiveBonuses();
        bonus = testBonus.getSafeListFor(ListKey.BONUS).get(0);
        List<BonusPair> bonusPairs = character.getStringListFromBonus(bonus);
        assertEquals(1, bonusPairs.size());
        BonusPair bp = bonusPairs.get(0);
        assertEquals("SPELLKNOWN.CLASS.Wizard;LEVEL.1", bp.fullyQualifiedBonusType);
    }

    @Override
    protected void defaultSetupEnd()
    {
        //Nothing, we will trigger ourselves
    }
}
