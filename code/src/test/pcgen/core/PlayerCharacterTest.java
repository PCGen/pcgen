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
 * Current Ver: $Revision$
 *
 * Last Editor: $Author$
 *
 * Last Edited: $Date$
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
import pcgen.io.exporttoken.StatToken;
import pcgen.util.Logging;
import pcgen.util.TestHelper;
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
		final PCStat stat = statList.getStatAt(0);
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
		final PCStat stat = statList.getStatAt(0);
		stat.setBaseScore(16);
		character.incrementClassLevel(2, pcClass);


		final Float result = character.getVariableValue("floor(SCORE/2)-5", "STAT:STR");
		assertEquals("Stat modifier not correct", 3.0, result.doubleValue(), 0.1);
	}

	/**
	 * Test out the caching of variable values.
	 */
	public void testGetVariableCaching()
	{
		final PlayerCharacter character = new PlayerCharacter();
		character.setRace(human);
		final StatList statList = character.getStatList();
		final PCStat stat = statList.getStatAt(0);
		stat.setBaseScore(16);
		character.incrementClassLevel(2, pcClass);

		int iVal = character.getVariableValue("roll(\"3d6\")+5","").intValue();
		boolean match = true;
		for (int i = 0; i < 10; i++)
		{
			match = (iVal == character.getVariableValue("roll(\"3d6\")+5","").intValue());
			if (!match)
			{
				break;
			}
		}

		assertFalse("Roll function should not be cached.", match);
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

		is((int) character.getFeats(), eq(2), "Start with 2 feats");
		try
		{
			AbilityUtilities.modFeat(character, null, "Toughness", true, false);
			is((int) character.getFeats(), eq(1), "Only 1 feat used");
		}
		catch(HeadlessException e)
		{
			Logging.debugPrint("Ignoring Headless exception.");
		}
	}

	/**
	 * Test that multiple exotic weapon proficiencies work correctly.
	 */
	public void testExoticWpnProf()
	{
		PlayerCharacter character = new PlayerCharacter();
		character.setRace(human);

		assertFalse("Not yet proficient in Weapon A", character.hasWeaponProfKeyed("Weapon A"));
		assertFalse("Not yet proficient in Weapon B", character.hasWeaponProfKeyed("Weapon B"));
		assertFalse("Not yet proficient in Weapon C", character.hasWeaponProfKeyed("Weapon C"));

		character.incrementClassLevel(1, pcClass);

		assertTrue("First Proficient in Weapon A",    character.hasWeaponProfKeyed("Weapon A"));
		assertFalse("Not yet proficient in Weapon B", character.hasWeaponProfKeyed("Weapon B"));
		assertFalse("Not yet proficient in Weapon C", character.hasWeaponProfKeyed("Weapon C"));

		character.incrementClassLevel(1, pcClass);

		assertTrue("Second Proficient in Weapon A",   character.hasWeaponProfKeyed("Weapon A"));
		assertTrue("Proficient in Weapon B",          character.hasWeaponProfKeyed("Weapon B"));
		assertFalse("Not yet proficient in Weapon C", character.hasWeaponProfKeyed("Weapon C"));

		character.incrementClassLevel(1, pcClass);

		assertTrue("Third Proficient in Weapon A", character.hasWeaponProfKeyed("Weapon A"));
		assertTrue("Proficient in Weapon B",       character.hasWeaponProfKeyed("Weapon B"));
		assertTrue("Proficient in Weapon C",       character.hasWeaponProfKeyed("Weapon C"));
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
		giantRace.setHitDiceAdvancement(new int[] {100});
		final BonusObj babBonus = Bonus.newBonus("COMBAT|BAB|3|PREDEFAULTMONSTER:Y|TYPE=Base.REPLACE");
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
		toughness.setChoiceString("NOCHOICE");
		toughness.setCategory("FEAT");
		Globals.addAbility(toughness);

		Ability exoticWpnProf = TestHelper.makeAbility("Exotic Weapon Proficiency", "FEAT", "General.Fighter");
		exoticWpnProf.setMultiples("YES");
		exoticWpnProf.setChoiceString("CHOOSE:PROFICIENCY|WEAPON|UNIQUE|TYPE.Exotic");
		exoticWpnProf.addAutoArray("WEAPONPROF|%LIST");

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


		SettingsHandler	.setSingleChoicePreference(Constants.CHOOSER_SINGLECHOICEMETHOD_SELECTEXIT);
		ChooserFactory.setInterfaceClassname(SwingChooser.class.getName());

		pcClass.addAddList(1, "FEAT(KEY_Exotic Weapon Proficiency (Weapon A))");
		pcClass.addAddList(2, "FEAT(KEY_Exotic Weapon Proficiency (Weapon B))");
		pcClass.addAddList(3, "FEAT(KEY_Exotic Weapon Proficiency (Weapon C))");
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
	 * Test the processing of the MAX function with respect to character stats.
	 */
	public void testMaxValue()
	{
		PlayerCharacter pc = getCharacter();
		setPCStat(pc, "STR", 8);
		setPCStat(pc, "DEX", 14);
		pc.setUseTempMods(true);

		assertEquals("STR", -1.0, pc.getVariableValue("STR", "").floatValue(),
			0.1);
		assertEquals("DEX", 2.0, pc.getVariableValue("DEX", "").floatValue(),
			0.1);
		assertEquals("max(STR,DEX)", 2.0, pc.getVariableValue("max(STR,DEX)",
			"").floatValue(), 0.1);

		StatToken statTok = new StatToken();
		assertEquals("Total stat.", "14", statTok.getToken("STAT.1", pc, null));
		assertEquals("Temp stat.", "14", statTok.getToken("STAT.1.NOEQUIP", pc, null));
		assertEquals("Equip stat.", "14", statTok.getToken("STAT.1.NOTEMP", pc, null));
		assertEquals("No equip/temp stat.", "14", statTok.getToken("STAT.1.NOEQUIP.NOTEMP", pc, null));
		assertEquals("Base stat.", "14", statTok.getToken("STAT.1.NOEQUIP.NOTEMP", pc, null));

		final BonusObj raceBonus = Bonus.newBonus("1|STAT|DEX|-2");
		giantClass.addBonusList(raceBonus);
		pc.setRace(giantRace);
		pc.incrementClassLevel(4, giantClass);

		assertEquals("Total stat.", "12", statTok.getToken("STAT.1", pc, null));
		assertEquals("Temp stat.", "12", statTok.getToken("STAT.1.NOEQUIP", pc, null));
		assertEquals("Base stat.", "12", statTok.getToken("STAT.1.NOEQUIP.NOTEMP", pc, null));
		assertEquals("DEX", 1.0, pc.getVariableValue("DEX", "").floatValue(),
			0.1);
		assertEquals("max(STR,DEX)", 1.0, pc.getVariableValue("max(STR,DEX)",
			"").floatValue(), 0.1);

		Spell spell2 = new Spell();
		spell2.setName("Concrete Boots");
		spell2.addBonusList("STAT|DEX|-2");
		BonusObj penalty = (BonusObj) spell2.getBonusList().get(0);
		pc.addTempBonus(penalty);
		penalty.setTargetObject(pc);
		pc.calcActiveBonuses();

		assertEquals("Total stat.", "10", statTok.getToken("STAT.1", pc, null));
		assertEquals("Temp stat.", "10", statTok.getToken("STAT.1.NOEQUIP", pc, null));
		assertEquals("Base stat.", "12", statTok.getToken("STAT.1.NOEQUIP.NOTEMP", pc, null));
		assertEquals("DEX", 0.0, pc.getVariableValue("DEX", "").floatValue(),
			0.1);
		assertEquals("max(STR,DEX)-STR", 1.0, pc.getVariableValue("max(STR,DEX)-STR",
			"").floatValue(), 0.1);
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

		String response = character.addSpell(null, new ArrayList(), pcClass.getKeyName(), null, 1, 1);
		assertEquals("Add spell should be rejected due to no spell",
			"Invalid parameter to add spell", response);

		Spell spell = new Spell();
		spell.setName("test spell 1");
		CharacterSpell charSpell = new CharacterSpell(pcClass, spell);
		response = character.addSpell(charSpell, new ArrayList(), pcClass.getKeyName(), null, 1, 1);
		assertEquals("Add spell should be rejected due to no book",
			"Invalid spell list/book name.", response);
		response = character.addSpell(charSpell, new ArrayList(), pcClass.getKeyName(), "", 1, 1);
		assertEquals("Add spell should be rejected due to no book",
			"Invalid spell list/book name.", response);

		// Add a non existant spell to a non existant spellbook
		String spellBookName = "Test book";
		response = character.addSpell(charSpell, new ArrayList(), pcClass.getKeyName(), spellBookName, 1, 1);
		assertEquals("Add spell should be rejected due to book not existing",
			"Could not find spell list/book Test book", response);


		character.addSpellBook(spellBookName);
		response = character.addSpell(charSpell, new ArrayList(), pcClass.getKeyName(), spellBookName, 1, 1);
		assertEquals(
			"Add spell should be rejected due to no levels.",
			"You can only prepare 0 spells for level 1\nand there are no higher-level slots available.",
			response);

		response = character.addSpell(charSpell, new ArrayList(), "noclass", spellBookName, 1, 1);
		assertEquals("Add spell should be rejected due to no matching class",
			"No class keyed noclass", response);

		SpellBook book = character.getSpellBookByName(spellBookName);
		book.setType(SpellBook.TYPE_PREPARED_LIST);
		character.addSpellBook(spellBookName);
		response = character.addSpell(charSpell, new ArrayList(), pcClass.getKeyName(), spellBookName, 1, 1);
		assertEquals(
			"Add spell should be rejected due to no levels.",
			"You can only prepare 0 spells for level 1\nand there are no higher-level slots available.",
			response);

		book.setType(SpellBook.TYPE_SPELL_BOOK);
		book.setPageFormula("SPELLLEVEL");
		book.setNumPages(3);
		character.addSpellBook(spellBookName);
		response = character.addSpell(charSpell, new ArrayList(), pcClass
			.getKeyName(), spellBookName, 1, 1);
		assertEquals("Add spell should not be rejected.", "", response);
		// Add a second time to cover multiples
		response = character.addSpell(charSpell, new ArrayList(), pcClass
			.getKeyName(), spellBookName, 1, 1);
		assertEquals("Add spell should not be rejected.", "", response);
		response = character.addSpell(charSpell, new ArrayList(), giantClass
			.getKeyName(), spellBookName, 1, 1);
		assertEquals("Add spell should not be rejected.", "", response);
		response = character.addSpell(charSpell, new ArrayList(), giantClass
			.getKeyName(), spellBookName, 1, 1);
		assertEquals(
			"Add spell should be rejected due to the book being full.",
			"There are not enough pages left to add this spell to the spell book.",
			response);

		PCClass c = character.getClassKeyed(pcClass.getKeyName());
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
