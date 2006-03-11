/*
 * PlayerCharacterTest.java
 *
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 09-Jan-2004
 *
 * Current Ver: $Revision: 1.25 $
 *
 * Last Editor: $Author: binkley $
 *
 * Last Edited: $Date: 2005/10/23 17:05:07 $
 *
 */
package pcgen.core;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;
import pcgen.AbstractCharacterTestCase;
import pcgen.gui.utils.SwingChooser;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellBook;
import pcgen.core.spell.Spell;
import pcgen.io.exporttoken.AttackToken;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;

import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wardc
 *
 */
public class PlayerCharacterTest extends AbstractCharacterTestCase {
	Race giantRace = null;
	PCClass giantClass = null;
	PCClass pcClass = null;
	PCClass classWarmind = null;
	Race human = null;

	public static void main(final String[] args)
	{
		TestRunner.run(PlayerCharacterTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PlayerCharacterTest.class);
	}

	/**
	 * @throws Exception
	 */
	public void testGetBonusFeatsForNewLevel1() throws Exception
	{
		SettingsHandler.setMonsterDefault(false);
		final PlayerCharacter character = new PlayerCharacter();

		character.setRace(human);
		character.incrementClassLevel(1, pcClass);
		assertEquals(2, (int) character.getFeats());
	}

	/**
	 * @throws Exception
	 */
	public void testGetBonusFeatsForNewLevel1Default() throws Exception
	{
		SettingsHandler.setMonsterDefault(true);
		final PlayerCharacter character = new PlayerCharacter();

		character.setRace(human);
		character.incrementClassLevel(1, pcClass);
		assertEquals(2, (int) character.getFeats());
	}

	/**
	 * @throws Exception
	 */
	public void testGetBonusFeatsForNewLevel3() throws Exception
	{
		SettingsHandler.setMonsterDefault(false);
		final PlayerCharacter character = new PlayerCharacter();

		character.setRace(human);
		character.incrementClassLevel(3, pcClass);
		assertEquals(3, (int) character.getFeats());
	}


	/**
	 * @throws Exception
	 */
	public void testGetBonusFeatsForNewLevel3Default() throws Exception
	{
		SettingsHandler.setMonsterDefault(true);
		final PlayerCharacter character = new PlayerCharacter();

		character.setRace(human);
		character.incrementClassLevel(3, pcClass);
		assertEquals(3, (int) character.getFeats());
	}


	/**
	 * @throws Exception
	 */
	public void testGetMonsterBonusFeatsForNewLevel1() throws Exception
	{
		SettingsHandler.setMonsterDefault(false);
		final PlayerCharacter character = new PlayerCharacter();

		character.setRace(giantRace);
		assertEquals(2, (int) character.getFeats());
		character.incrementClassLevel(1, pcClass);
		assertEquals(2, (int) character.getFeats());
		character.incrementClassLevel(1, pcClass);
		assertEquals(3, (int) character.getFeats());
	}

	/**
	 * @throws Exception
	 */
	public void testGetMonsterBonusFeatsForNewLevel1Default() throws Exception
	{
		SettingsHandler.setMonsterDefault(true);
		final PlayerCharacter character = new PlayerCharacter();

		character.setRace(giantRace);
		character.incrementClassLevel(1, pcClass);
		assertEquals(0, (int) character.getFeats());
	}

	public void testBabDefaultOgre() throws Exception
	{
		SettingsHandler.setMonsterDefault(true);

		final PlayerCharacter character = new PlayerCharacter();
		character.setRace(giantRace);
		assertEquals(3, character.baseAttackBonus());
	}

	public void testBabDefaultOgreLvl4() throws Exception
	{
		SettingsHandler.setMonsterDefault(true);

		final PlayerCharacter character = new PlayerCharacter();
		character.setRace(giantRace);
		character.incrementClassLevel(4, giantClass);
		assertEquals(6, character.baseAttackBonus());
	}

	public void testBabNonDefaultOgre() throws Exception
	{
		SettingsHandler.setMonsterDefault(true);

		final PlayerCharacter character = new PlayerCharacter();
		character.setRace(giantRace);
		assertEquals(3, character.baseAttackBonus());
	}

	public void testBabNonDefaultOgreLvl4() throws Exception
	{
		SettingsHandler.setMonsterDefault(true);

		final PlayerCharacter character = new PlayerCharacter();
		character.setRace(giantRace);
		character.incrementClassLevel(4, giantClass);
		assertEquals(6, character.baseAttackBonus());
	}

	public void testGetVariableValue1() throws Exception
	{
		//Logging.setDebugMode(true);
		SettingsHandler.setMonsterDefault(false);
		Logging.debugPrint("\n\n\ntestGetVariableValue1()");
		giantRace.addVariable(-9, "GiantVar1", "0");
		final BonusObj raceBonus = Bonus.newBonus("1|VAR|GiantVar1|7+HD");
		giantClass.addBonusList(raceBonus);

		giantClass.addVariable(1, "GiantClass1", "0");
		final BonusObj babClassBonus = Bonus.newBonus("1|VAR|GiantClass1|CL=Giant");
		giantClass.addBonusList(babClassBonus);


		final PlayerCharacter character = new PlayerCharacter();
		character.setRace(giantRace);
		character.incrementClassLevel(4, giantClass);

		assertEquals(new Float(7.0), character.getVariableValue("GiantVar1", "CLASS:Giant"));
		assertEquals(new Float(8.0), character.getVariableValue("GiantClass1", "CLASS:Giant"));


	}

	public void testGetVariableValue2() throws Exception
	{
		//Logging.setDebugMode(true);
		Logging.debugPrint("\n\n\ntestGetVariableValue2()");
		SettingsHandler.setMonsterDefault(true);
		giantRace.addVariable(-9, "GiantVar1", "0");
		final BonusObj raceBonus = Bonus.newBonus("1|VAR|GiantVar1|7+HD");
		giantClass.addBonusList(raceBonus);

		giantClass.addVariable(1, "GiantClass1", "0");
		final BonusObj babClassBonus = Bonus.newBonus("1|VAR|GiantClass1|CL=Giant");
		giantClass.addBonusList(babClassBonus);

		final PlayerCharacter character = new PlayerCharacter();
		character.setRace(giantRace);
		character.incrementClassLevel(4, giantClass);

		assertEquals(new Float(7.0), character.getVariableValue("GiantVar1", "CLASS:Giant"));
		assertEquals(new Float(4.0), character.getVariableValue("GiantClass1", "CLASS:Giant"));

		final AttackToken token = new AttackToken();
		assertEquals("+6/+1", token.getToken("ATTACK.MELEE.TOTAL", character, null));
	}

	public void testGetVariableValueStatMod() throws Exception
	{
		SettingsHandler.setMonsterDefault(false);
		Logging.setDebugMode(true);
		Logging.debugPrint("\n\n\ntestGetVariableValueStatMod()");
		final PlayerCharacter character = new PlayerCharacter();
		character.setRace(human);
		final StatList statList = character.getStatList();
		final List list = statList.getStats();
		final PCStat stat = (PCStat) list.get(0);
		stat.setBaseScore(16);
		character.incrementClassLevel(2, pcClass);


		final Float result = character.getVariableValue("(SCORE/2).TRUNC-5", "STAT:STR");
		assertEquals("Stat modifier not correct", 3.0, result.doubleValue(), 0.1);
	}


	public void testGetVariableValueStatModNew() throws Exception
	{
		SettingsHandler.setMonsterDefault(false);
		Logging.debugPrint("\n\n\ntestGetVariableValueStatModNew()");
		Logging.setDebugMode(true);
		final PlayerCharacter character = new PlayerCharacter();
		character.setRace(human);
		final StatList statList = character.getStatList();
		final List list = statList.getStats();
		final PCStat stat = (PCStat) list.get(0);
		stat.setBaseScore(16);
		character.incrementClassLevel(2, pcClass);


		final Float result = character.getVariableValue("floor(SCORE/2)-5", "STAT:STR");
		assertEquals("Stat modifier not correct", 3.0, result.doubleValue(), 0.1);
	}

	/**
	 * Test the processing of modFeat. Checks that when in select single and
	 * close mode, only one instance of a feat with a sub-choice is added.
	 */
	public void testModFeat()
	{
		final PlayerCharacter character = new PlayerCharacter();
		character.setRace(human);
		character.incrementClassLevel(1, pcClass);

		SettingsHandler
			.setSingleChoicePreference(Constants.CHOOSER_SINGLECHOICEMETHOD_SELECTEXIT);
		ChooserFactory.setInterfaceClassname(SwingChooser.class.getName());

		is(new Integer((int) character.getFeats()), eq(2), "Start with 2 feats");
		try
		{
			AbilityUtilities.modFeat(character, null, "Toughness", true, false);
			is(new Integer((int) character.getFeats()), eq(1), "Only 1 feat used");
		}
		catch(HeadlessException e)
		{
			Logging.debugPrint("Ignoring Headless excpetion.");
		}
	}

	/**
	 * Test that multiple exotic weapon proficiencies work correctly.
	 */
	public void testExoticWpnProf()
	{
		final PlayerCharacter character = new PlayerCharacter();
		character.setRace(human);
		character.incrementClassLevel(1, pcClass);

		assertFalse("Not yet proficient in Weapon A", character.hasWeaponProfNamed("Weapon A"));
		assertFalse("Not yet proficient in Weapon B", character.hasWeaponProfNamed("Weapon B"));
		assertFalse("Not yet proficient in Weapon C", character.hasWeaponProfNamed("Weapon C"));

		AbilityUtilities.modFeat(character, null, "Exotic Weapon Proficiency (Weapon A)", true, false);
		assertTrue("First Proficient in Weapon A", character.hasWeaponProfNamed("Weapon A"));
		assertFalse("Not yet proficient in Weapon B", character.hasWeaponProfNamed("Weapon B"));
		assertFalse("Not yet proficient in Weapon C", character.hasWeaponProfNamed("Weapon C"));

		AbilityUtilities.modFeat(character, null, "Exotic Weapon Proficiency (Weapon B)", true, false);
		assertTrue("Second Proficient in Weapon A", character.hasWeaponProfNamed("Weapon A"));
		assertTrue("Proficient in Weapon B", character.hasWeaponProfNamed("Weapon B"));
		assertFalse("Not yet proficient in Weapon C", character.hasWeaponProfNamed("Weapon C"));

		AbilityUtilities.modFeat(character, null, "Exotic Weapon Proficiency (Weapon C)", true, false);
		assertTrue("Third Proficient in Weapon A", character.hasWeaponProfNamed("Weapon A"));
		assertTrue("Proficient in Weapon B", character.hasWeaponProfNamed("Weapon B"));
		assertTrue("Proficient in Weapon C", character.hasWeaponProfNamed("Weapon C"));
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();

		// Human
		human = new Race();
		human.setBonusInitialFeats(2);


		// Giant Race
		giantRace = new Race();
		giantRace.setName("Ogre");
		giantRace.setBonusInitialFeats(1);
		giantRace.setMonsterClass("Giant");
		giantRace.setMonsterClassLevels(4);
		final BonusObj babBonus = Bonus.newBonus("COMBAT|BAB|3|PREDEFAULTMONSTER:Y");
		giantRace.addBonusList(babBonus);
		Globals.getRaceMap().put("Ogre", giantRace);

		// Giant Class
		giantClass = new PCClass();
		giantClass.setName("Giant");
		giantClass.setAbbrev("Gnt");
		giantClass.addMyType("MONSTER");
		final BonusObj babClassBonus = Bonus.newBonus("1|COMBAT|BAB|CL*3/4");
		giantClass.addBonusList(babClassBonus);
		Globals.getClassList().add(giantClass);


		pcClass = new PCClass();
		pcClass.setName("MyClass");
		pcClass.setAbbrev("My");
		pcClass.setSpellType("ARCANE");
		Globals.getClassList().add(pcClass);

		classWarmind = new PCClass();
		classWarmind.setName("Warmind");
		classWarmind.setAbbrev("WM");
		Globals.getClassList().add(classWarmind);

		Ability toughness = new Ability();
		toughness.setName("Toughness");
		toughness.setMultiples("Y");
		toughness.setStacks("Y");
		toughness.setChoiceString("foo");
		toughness.setCategory("FEAT");
		Globals.addAbility(toughness);

		Ability exoticWpnProf = new Ability();
		exoticWpnProf.setName("Exotic Weapon Proficiency");
		exoticWpnProf.setMultiples("Y");
		exoticWpnProf.setChoiceString("Exotic");
		exoticWpnProf.setCategory("FEAT");
		Globals.addAbility(exoticWpnProf);

		WeaponProf wpnProfTestA = new WeaponProf();
		wpnProfTestA.setName("Weapon A");
		wpnProfTestA.setKeyName("Weapon A");
		wpnProfTestA.setTypeInfo("Exotic");
		Globals.addWeaponProf(wpnProfTestA);

		WeaponProf wpnProfTestB = new WeaponProf();
		wpnProfTestB.setName("Weapon B");
		wpnProfTestB.setKeyName("Weapon B");
		wpnProfTestB.setTypeInfo("Exotic");
		Globals.addWeaponProf(wpnProfTestB);

		WeaponProf wpnProfTestC = new WeaponProf();
		wpnProfTestC.setName("Weapon C");
		wpnProfTestC.setKeyName("Weapon C");
		wpnProfTestC.setTypeInfo("Exotic");
		Globals.addWeaponProf(wpnProfTestC);
	}

	public void testGetClassVar() throws Exception
	{
		Logging.setDebugMode(true);
		Logging.debugPrint("\n\n\ntestGetClassVar()");
		final PlayerCharacter character = new PlayerCharacter();
		character.setRace(human);
		character.incrementClassLevel(2, classWarmind);


		final Float result = character.getVariableValue("var(\"CL=Warmind\")", "");
		assertEquals("CL count not correct", 2.0, result.doubleValue(), 0.1);
	}


	/**
	 * Test the skills visibility fucntionality. We want to ensure that
	 * each call retreives the right set of skills.
	 */
	public void testSkillsVisibility()
	{
		PlayerCharacter pc = getCharacter();

		Skill guiSkill = new Skill();
		Skill outputSkill = new Skill();
		Skill defaultSkill = new Skill();

		guiSkill.addClassList("MyClass");
		guiSkill.setName("GUI");
		guiSkill.setTypeInfo("INT");
		guiSkill.setVisible(Skill.VISIBILITY_DISPLAY_ONLY);
		guiSkill.modRanks(1.0, pcClass, true, pc);
		pc.addSkill(guiSkill);

		outputSkill.addClassList("MyClass");
		outputSkill.setName("Output");
		outputSkill.setTypeInfo("INT");
		outputSkill.setVisible(Skill.VISIBILITY_OUTPUT_ONLY);
		outputSkill.modRanks(1.0, pcClass, true, pc);
		pc.addSkill(outputSkill);

		defaultSkill.addClassList("MyClass");
		defaultSkill.setName("Default");
		defaultSkill.setTypeInfo("INT");
		defaultSkill.setVisible(Skill.VISIBILITY_DEFAULT);
		defaultSkill.modRanks(1.0, pcClass, true, pc);
		pc.addSkill(defaultSkill);

		// Test retreived list
		List skillList = pc.getSkillList();
		assertEquals("Full skill list should have all 3 skills", 3, skillList
			.size());

		skillList = pc.getPartialSkillList(Skill.VISIBILITY_DISPLAY_ONLY);
		assertEquals("GUI skill list should have 2 skills", 2, skillList
			.size());

		skillList = pc.getPartialSkillList(Skill.VISIBILITY_OUTPUT_ONLY);
		assertEquals("Output skill list should have 2 skills", 2, skillList
			.size());

		skillList = pc.getPartialSkillList(Skill.VISIBILITY_DEFAULT);
		assertEquals("Full skill list should have 3 skills", 3, skillList
			.size());

	}
	
	public void testAddSpells()
	{
		final PlayerCharacter character = new PlayerCharacter();
		character.setRace(human);
		character.incrementClassLevel(1, pcClass);

		String response = character.addSpell(null, new ArrayList(), pcClass.getName(), null, 1, 1);
		assertEquals("Add spell should be rejected due to no spell",
			"Invalid parameter to add spell", response);

		Spell spell = new Spell();
		spell.setName("test spell 1");
		CharacterSpell charSpell = new CharacterSpell(pcClass, spell);
		response = character.addSpell(charSpell, new ArrayList(), pcClass.getName(), null, 1, 1);
		assertEquals("Add spell should be rejected due to no book",
			"Invalid spell list/book name.", response);
		response = character.addSpell(charSpell, new ArrayList(), pcClass.getName(), "", 1, 1);
		assertEquals("Add spell should be rejected due to no book",
			"Invalid spell list/book name.", response);
		
		// Add a non existant spell to a non existant spellbook
		String spellBookName = "Test book";
		response = character.addSpell(charSpell, new ArrayList(), pcClass.getName(), spellBookName, 1, 1);
		assertEquals("Add spell should be rejected due to book not existing",
			"Could not find spell list/book Test book", response);

		
		character.addSpellBook(spellBookName);
		response = character.addSpell(charSpell, new ArrayList(), pcClass.getName(), spellBookName, 1, 1);
		assertEquals(
			"Add spell should be rejected due to no levels.",
			"You can only prepare 0 spells for level 1\nand there are no higher-level slots available",
			response);

		response = character.addSpell(charSpell, new ArrayList(), "noclass", spellBookName, 1, 1);
		assertEquals("Add spell should be rejected due to no matching class",
			"No class named noclass", response);
		
		SpellBook book = character.getSpellBookByName(spellBookName);
		book.setType(SpellBook.TYPE_PREPARED_LIST);
		character.addSpellBook(spellBookName);
		response = character.addSpell(charSpell, new ArrayList(), pcClass.getName(), spellBookName, 1, 1);
		assertEquals(
			"Add spell should be rejected due to no levels.",
			"You can only prepare 0 spells for level 1\nand there are no higher-level slots available",
			response);

		book.setType(SpellBook.TYPE_SPELL_BOOK);
		book.setPageFormula("SPELLLEVEL");
		book.setNumPages(3);
		character.addSpellBook(spellBookName);
		response = character.addSpell(charSpell, new ArrayList(), pcClass
			.getName(), spellBookName, 1, 1);
		assertEquals("Add spell should not be rejected.", "", response);
		// Add a second time to cover multiples
		response = character.addSpell(charSpell, new ArrayList(), pcClass
			.getName(), spellBookName, 1, 1);
		assertEquals("Add spell should not be rejected.", "", response);
		response = character.addSpell(charSpell, new ArrayList(), giantClass
			.getName(), spellBookName, 1, 1);
		assertEquals("Add spell should not be rejected.", "", response);
		response = character.addSpell(charSpell, new ArrayList(), giantClass
			.getName(), spellBookName, 1, 1);
		assertEquals(
			"Add spell should be rejected due to the book being full.",
			"There are not enough pages left to add this spell to the spell book.",
			response);
		
		PCClass c = character.getClassNamed(pcClass.getName());
		List aList = c.getSpellSupport().getCharacterSpell(null, spellBookName, 1);
		CharacterSpell addedSpell = (CharacterSpell) (aList.get(0));
		response = character.delSpell(addedSpell.getSpellInfoFor(spellBookName,
			1, -1, new ArrayList()), pcClass, spellBookName);
		assertEquals(
			"Delete spell should not be rejected.",
			"",
			response);

		aList = giantClass.getSpellSupport().getCharacterSpell(null, spellBookName, 1);
		addedSpell = (CharacterSpell) (aList.get(0));
		response = character.delSpell(addedSpell.getSpellInfoFor(spellBookName,
			1, -1), giantClass, spellBookName);
		assertEquals(
			"Delete spell should not be rejected.",
			"",
			response);
	}

}
