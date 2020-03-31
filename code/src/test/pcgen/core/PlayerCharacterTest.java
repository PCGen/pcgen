/*
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
 */
package pcgen.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.HeadlessException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.BasicClassIdentity;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MovementType;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.list.CompanionList;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSimpleSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.analysis.DomainApplication;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellBook;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.spell.Spell;
import pcgen.core.system.LoadInfo;
import pcgen.gui2.UIPropertyContext;
import pcgen.io.exporttoken.StatToken;
import pcgen.persistence.lst.SimpleLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;
import pcgen.util.TestHelper;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.enumeration.View;
import pcgen.util.enumeration.Visibility;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TestURI;

/**
 * The Class {@code PlayerCharacterTest} is responsible for testing
 * that PlayerCharacter is working correctly.
 */
public class PlayerCharacterTest extends AbstractCharacterTestCase
{
	Race giantRace = null;
	PCClass giantClass = null;
	PCClass pcClass = null;
	PCClass classWarmind = null;
	PCClass class2LpfM = null;
	PCClass class3LpfM = null;
	PCClass class3LpfBlank = null;
	Race human = null;
	Ability toughness = null;
	AbilityCategory specialFeatCat;
	AbilityCategory specialAbilityCat;
	private PCClass classMemDivine;
	private Domain luckDomain;
	private Spell luckDomainLvl1Spell;
	private Spell luckDomainLvl2Spell;

	@BeforeEach
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		LoadContext context = Globals.getContext();

		// Giant Class
		giantClass = new PCClass();
		giantClass.setName("Giant");
		BuildUtilities.setFact(giantClass, "ClassType", "Monster");
		final BonusObj babClassBonus = Bonus.newBonus(context, "COMBAT|BASEAB|CL*3/4");
		giantClass.getOriginalClassLevel(1).addToListFor(ListKey.BONUS, babClassBonus);
		context.getReferenceContext().importObject(giantClass);
	
		// Human
		human = new Race();
		final BonusObj humanRaceFeatBonus = Bonus.newBonus(context, "FEAT|POOL|2");
		human.addToListFor(ListKey.BONUS, humanRaceFeatBonus);
		human.ownBonuses(human);

		// Giant Race
		giantRace = new Race();
		giantRace.setName("Ogre");
		giantRace.put(ObjectKey.MONSTER_CLASS, new LevelCommandFactory(
				CDOMDirectSingleRef.getRef(giantClass), FormulaFactory
						.getFormulaFor(4)));
		giantRace.addToListFor(ListKey.HITDICE_ADVANCEMENT, 100);

		final BonusObj giantRaceFeatBonus = Bonus.newBonus(context, "FEAT|POOL|1");
	
		giantRace.addToListFor(ListKey.BONUS, giantRaceFeatBonus);
		giantRace.ownBonuses(giantRace);
	
		context.getReferenceContext().importObject(giantRace);
	
		// Create the monster class type
		SimpleLoader<ClassType> methodLoader = new SimpleLoader<>(ClassType.class);
		methodLoader.parseLine(SettingsHandler.getGameAsProperty().get().getModeContext(),
			"Monster		CRFORMULA:0			ISMONSTER:YES	XPPENALTY:NO",
			TestURI.getURI());

		pcClass = new PCClass();
		pcClass.setName("MyClass");
		BuildUtilities.setFact(pcClass, "SpellType", "Arcane");
		context.getReferenceContext().importObject(pcClass);
		
		classMemDivine = new PCClass();
		classMemDivine.setName("MemDivine");
		BuildUtilities.setFact(classMemDivine, "SpellType", "Divine");
		classMemDivine.put(ObjectKey.MEMORIZE_SPELLS, true);
		context.unconditionallyProcess(classMemDivine, "SPELLSTAT", "WIS");
		context.unconditionallyProcess(classMemDivine.getOriginalClassLevel(1), "CAST", "3,2,2");
		context.unconditionallyProcess(classMemDivine, "BONUS", "DOMAIN|NUMBER|1");
		context.getReferenceContext().importObject(classMemDivine);
	
		classWarmind = new PCClass();
		classWarmind.setName("Warmind");
		context.getReferenceContext().importObject(classWarmind);
	
		class2LpfM = new PCClass();
		class2LpfM.setName("2LpfM");
		BuildUtilities.setFact(class2LpfM, "ClassType", "Monster");
		class2LpfM.put(IntegerKey.LEVELS_PER_FEAT, 2);
		class2LpfM.put(StringKey.LEVEL_TYPE, "MONSTER");
		context.getReferenceContext().importObject(class2LpfM);
		
		class3LpfM = new PCClass();
		class3LpfM.setName("3LpfM");
		BuildUtilities.setFact(class3LpfM, "ClassType", "Monster");
		class3LpfM.put(IntegerKey.LEVELS_PER_FEAT, 3);
		class3LpfM.put(StringKey.LEVEL_TYPE, "MONSTER");
		context.getReferenceContext().importObject(class3LpfM);
		
		class3LpfBlank = new PCClass();
		class3LpfBlank.setName("3LpfBlank");
		BuildUtilities.setFact(class3LpfBlank, "ClassType", "Foo");
		class3LpfBlank.put(IntegerKey.LEVELS_PER_FEAT, 3);
		context.getReferenceContext().importObject(class3LpfBlank);

		toughness = new Ability();
		toughness.setName("Toughness");
		toughness.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
		toughness.put(ObjectKey.STACKS, Boolean.TRUE);
		context.unconditionallyProcess(toughness, "CHOOSE", "NOCHOICE");
		toughness.setCDOMCategory(BuildUtilities.getFeatCat());
		final BonusObj aBonus = Bonus.newBonus(context, "HP|CURRENTMAX|3");
		
		if (aBonus != null)
		{
			toughness.addToListFor(ListKey.BONUS, aBonus);
		}
		context.getReferenceContext().importObject(toughness);
	
		Ability exoticWpnProf =
				TestHelper.makeAbility("Exotic Weapon Proficiency", BuildUtilities.getFeatCat(),
					"General.Fighter");
		exoticWpnProf.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
		context.unconditionallyProcess(exoticWpnProf, "CHOOSE", "WEAPONPROFICIENCY|!PC[TYPE.Exotic]");
		context.unconditionallyProcess(exoticWpnProf, "AUTO", "WEAPONPROF|%LIST");
	
		WeaponProf wpnProfTestA = new WeaponProf();
		wpnProfTestA.setName("Weapon A");
		wpnProfTestA.put(StringKey.KEY_NAME, "Weapon A");
		wpnProfTestA.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
		context.getReferenceContext().importObject(wpnProfTestA);
	
		WeaponProf wpnProfTestB = new WeaponProf();
		wpnProfTestB.setName("Weapon B");
		wpnProfTestB.put(StringKey.KEY_NAME, "Weapon B");
		wpnProfTestB.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
		context.getReferenceContext().importObject(wpnProfTestB);
	
		WeaponProf wpnProfTestC = new WeaponProf();
		wpnProfTestC.setName("Weapon C");
		wpnProfTestC.put(StringKey.KEY_NAME, "Weapon C");
		wpnProfTestC.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
		context.getReferenceContext().importObject(wpnProfTestC);
	
		UIPropertyContext.setSingleChoiceAction(Constants.CHOOSER_SINGLE_CHOICE_METHOD_SELECT_EXIT);
		ChooserFactory.useRandomChooser();
	
		context.unconditionallyProcess(pcClass.getOriginalClassLevel(1), "ADD",
				"FEAT|KEY_Exotic Weapon Proficiency (Weapon B)");
		context.unconditionallyProcess(pcClass.getOriginalClassLevel(2), "ADD",
				"FEAT|KEY_Exotic Weapon Proficiency (Weapon A)");
		context.unconditionallyProcess(pcClass.getOriginalClassLevel(3), "ADD",
				"FEAT|KEY_Exotic Weapon Proficiency (Weapon C)");
		
		specialFeatCat = Globals.getContext().getReferenceContext()
				.constructNowIfNecessary(AbilityCategory.class, "Special Feat");
		specialFeatCat.setAbilityCategory(CDOMDirectSingleRef.getRef(BuildUtilities.getFeatCat()));
		specialAbilityCat = Globals.getContext().getReferenceContext()
				.constructNowIfNecessary(AbilityCategory.class, "Special Ability");
		
		luckDomain = TestHelper.makeDomain("Luck");
		context.getReferenceContext().buildDerivedObjects();
		
		luckDomainLvl1Spell = TestHelper.makeSpell("true strike");
		luckDomainLvl2Spell = TestHelper.makeSpell("aid");
		TestHelper.makeSpell("protection from energy");
		context
			.unconditionallyProcess(
				luckDomain,
				"SPELLLEVEL",
				"DOMAIN|Luck=1|KEY_True Strike|Luck=2|KEY_Aid|Luck=3|KEY_Protection from Energy");
		
	}

	private void readyToRun()
	{
		LoadContext context = Globals.getContext();
		context.resolveDeferredTokens();
		assertTrue(context.getReferenceContext().resolveReferences(null));
	}

	@AfterEach
	@Override
	public void tearDown() throws Exception
	{
		Logging.setDebugMode(false);
		human.removeListFor(ListKey.BONUS);
		giantRace.removeListFor(ListKey.BONUS);
		super.tearDown();
	}
	

	@Test
	public void testGetBonusFeatsForNewLevel1() throws Exception
	{
		readyToRun();
		final PlayerCharacter character = new PlayerCharacter();

		character.setRace(human);
		character.incrementClassLevel(1, pcClass, true);
		assertEquals(2, (int) character.getRemainingFeatPoints(true));
	}

	@Test
	public void testGetBonusFeatsForNewLevel3() throws Exception
	{
		readyToRun();
		final PlayerCharacter character = new PlayerCharacter();

		character.setRace(human);
		character.incrementClassLevel(3, pcClass, true);
		assertEquals(3, (int) character.getRemainingFeatPoints(true));
	}

	/**
	 * Test bonus monster feats where there default monster mode is off.
	 * Note: As PCClass grants feats which do not exist, the feat pool gets 
	 * incremented instead.
	 */
	@Test
	public void testGetMonsterBonusFeatsForNewLevel1()
	{
		readyToRun();
		final PlayerCharacter character = new PlayerCharacter();

		character.setRace(giantRace);
		character.incrementClassLevel(1, pcClass, true);
		assertEquals(
				2,
				(int)character.getRemainingFeatPoints(true),
				"One level of PCClass, PC has one feat for levels of monster class and one for a missing feat."
		);
		character.incrementClassLevel(1, pcClass, true);
		assertEquals(
				3,
				(int)character.getRemainingFeatPoints(true),
				"Three levels of PCClass (6 total), feats increment"
		);
	}

	/**
	 * Test level per feat bonus to feats. 
	 */
	@Test
	public void testGetNumFeatsFromLevels()
	{
		readyToRun();
		final PlayerCharacter pc = new PlayerCharacter();
		pc.setRace(human);
		assertEquals(0, pc.getNumFeatsFromLevels(), 0.001, "Should start at 0");

		pc.incrementClassLevel(1, class3LpfM, true);
		assertEquals(0, pc.getNumFeatsFromLevels(), 0.001, "1/3 truncs to 0");
		pc.incrementClassLevel(1, class3LpfM, true);
		assertEquals(0, pc.getNumFeatsFromLevels(), 0.001, "2/3 truncs to 0");
		pc.incrementClassLevel(1, class3LpfM, true);
		assertEquals(1, pc.getNumFeatsFromLevels(), 0.001, "3/3 truncs to 1");
		pc.incrementClassLevel(1, class3LpfM, true);
		assertEquals(1, pc.getNumFeatsFromLevels(), 0.001, "4/3 truncs to 1");
		pc.incrementClassLevel(1, class2LpfM, true);
		assertEquals(1, pc.getNumFeatsFromLevels(),
			0.001, "4/3 + 1/2 truncs to 1"
		);
		pc.incrementClassLevel(1, class3LpfBlank, true);
		assertEquals(1, pc
			.getNumFeatsFromLevels(), 0.001, "4/3 + 1/2 truncs to 1 + 1/3 truncs to 0");
		pc.incrementClassLevel(1, class2LpfM, true);
		assertEquals(2, pc
			.getNumFeatsFromLevels(), 0.001, "5/3 + 2/2 truncs to 2 + 1/3 truncs to 0");
	}

	/**
	 * Test stacking rules for a mixture of normal progression and 
	 * levelsperfeat progression. Stacking should only occur within like 
	 * leveltypes or within standard progression.
	 */
	@Test
	public void testGetMonsterBonusFeatsForNewLevel2()
	{
		readyToRun();
		final PlayerCharacter pc = new PlayerCharacter();

		pc.setRace(giantRace);
		assertEquals(
				2, pc.getRemainingFeatPoints(true), 0.1,
				"Four levels from race (4/3), PC has one racial feat."
		);

		pc.incrementClassLevel(1, class3LpfM, true);
		assertEquals(
				2, (int)pc.getRemainingFeatPoints(true),
				"One level of 3LpfM (1/3), four levels from race(4/3), PC has one racial feat.");
		pc.incrementClassLevel(1, class3LpfM, true);
		assertEquals(
				2, (int)pc.getRemainingFeatPoints(true),
				"Two level of 3LpfM (2/3), four levels from race(4/3), PC has one racial feat."
		);
		pc.incrementClassLevel(1, class3LpfM, true);
		assertEquals(
				3, (int)pc.getRemainingFeatPoints(true),
				"Three level of 3LpfM (3/3), four levels from race(4/3), PC has one racial feat."
		);
	}
	
	/**
	 * Tests getVariableValue.
	 */
	@Test
	public void testGetVariableValue1()
	{
		readyToRun();
		LoadContext context = Globals.getContext();

		//Logging.setDebugMode(true);
		Logging.debugPrint("\n\n\ntestGetVariableValue1()");
		giantRace.put(VariableKey.getConstant("GiantVar1"), FormulaFactory.ZERO);
		final BonusObj raceBonus = Bonus.newBonus(context, "VAR|GiantVar1|7+HD");
		giantClass.getOriginalClassLevel(1).addToListFor(ListKey.BONUS, raceBonus);

		giantClass.getOriginalClassLevel(1).put(VariableKey.getConstant("GiantClass1"),
				FormulaFactory.ZERO);
		final BonusObj babClassBonus =
				Bonus.newBonus(context, "VAR|GiantClass1|CL=Giant");
		giantClass.getOriginalClassLevel(1).addToListFor(ListKey.BONUS, babClassBonus);

		final PlayerCharacter character = new PlayerCharacter();
		// NOTE: This will add 4 levels of giantClass to the character 
		character.setRace(giantRace);
		character.incrementClassLevel(4, giantClass, true);

		assertEquals(15.0f, character.getVariableValue("GiantVar1","CLASS:Giant"), 0.1);
		assertEquals(8.0f, character.getVariableValue("GiantClass1","CLASS:Giant"), 0.1);

	}

	/**
	 * Tests getVariableValue for stat modifier.
	 */
	@Test
	public void testGetVariableValueStatMod()
	{
		readyToRun();
		//Logging.setDebugMode(true);
		Logging.debugPrint("\n\n\ntestGetVariableValueStatMod()");
		final PlayerCharacter character = new PlayerCharacter();
		character.setRace(human);
		character.setStat(str, 16);
		character.incrementClassLevel(2, pcClass, true);

		final Float result =
				character.getVariableValue("floor(SCORE/2)-5", "STAT:STR");
		assertEquals(3.0, result.doubleValue(),
			0.1, "Stat modifier not correct"
		);
	}

	@Test
	public void testGetVariableValueStatModNew()
	{
		readyToRun();
		//Logging.setDebugMode(true);
		Logging.debugPrint("\n\n\ntestGetVariableValueStatModNew()");
		final PlayerCharacter character = new PlayerCharacter();
		character.setRace(human);
		character.setStat(str, 16);
		character.incrementClassLevel(2, pcClass, true);

		final Float result =
				character.getVariableValue("floor(SCORE/2)-5", "STAT:STR");
		assertEquals(3.0, result.doubleValue(),
			0.1, "Stat modifier not correct"
		);
	}

	/**
	 * Test out the caching of variable values.
	 */
	@Test
	public void testGetVariableCaching()
	{
		readyToRun();
		final PlayerCharacter character = new PlayerCharacter();
		character.setRace(human);
		character.setStat(str, 16);
		character.incrementClassLevel(2, pcClass, true);

		int iVal = character.getVariableValue("roll(\"3d6\")+5", "").intValue();
		boolean match = true;
		for (int i = 0; i < 10; i++)
		{
			match =
					(iVal == character.getVariableValue("roll(\"3d6\")+5", "")
						.intValue());
			if (!match)
			{
				break;
			}
		}

		assertFalse(match, "Roll function should not be cached.");
	}

	/**
	 * Test the processing of modFeat. Checks that when in select single and
	 * close mode, only one instance of a feat with a sub-choice is added.
	 */
	@Test
	public void testModFeat()
	{
		readyToRun();
		final PlayerCharacter character = new PlayerCharacter();
		character.setRace(human);
		character.incrementClassLevel(1, pcClass, true);

		UIPropertyContext.setSingleChoiceAction(Constants.CHOOSER_SINGLE_CHOICE_METHOD_SELECT_EXIT);
		ChooserFactory.useRandomChooser();

		assertEquals(
				2, (int)character.getRemainingFeatPoints(true),
				"Start with 2 feats"
		);
		try
		{
			AbstractCharacterTestCase.applyAbility(character, BuildUtilities.getFeatCat(), toughness, "");
		}
		catch (HeadlessException e)
		{
			Logging.debugPrint("Ignoring Headless exception.");
		}
		assertEquals(1, (int)character.getRemainingFeatPoints(true), "Only 1 feat used");
	}

	/**
	 * Test that multiple exotic weapon proficiencies work correctly.
	 */
	@Test
	public void testExoticWpnProf()
	{
		readyToRun();
		PlayerCharacter character = new PlayerCharacter();
		character.setRace(human);

		assertFalse(TestHelper.hasWeaponProfKeyed(character, "Weapon A"), "Not yet proficient in Weapon A");
		assertFalse(TestHelper.hasWeaponProfKeyed(character, "Weapon B"), "Not yet proficient in Weapon B");
		assertFalse(TestHelper.hasWeaponProfKeyed(character, "Weapon C"), "Not yet proficient in Weapon C");

		character.incrementClassLevel(1, pcClass, true);

		assertFalse(TestHelper.hasWeaponProfKeyed(character, "Weapon A"), "First Proficient in Weapon A");
		assertTrue(TestHelper.hasWeaponProfKeyed(character, "Weapon B"), "Not yet proficient in Weapon B");
		assertFalse(TestHelper.hasWeaponProfKeyed(character, "Weapon C"), "Not yet proficient in Weapon C");

		character.incrementClassLevel(1, pcClass, true);

		assertTrue(TestHelper.hasWeaponProfKeyed(character, "Weapon A"), "Second Proficient in Weapon A");
		assertTrue(TestHelper.hasWeaponProfKeyed(character, "Weapon B"), "Proficient in Weapon B");
		assertFalse(TestHelper.hasWeaponProfKeyed(character, "Weapon C"), "Not yet proficient in Weapon C");

		character.incrementClassLevel(1, pcClass, true);

		assertTrue(TestHelper.hasWeaponProfKeyed(character, "Weapon A"), "Third Proficient in Weapon A");
		assertTrue(TestHelper.hasWeaponProfKeyed(character, "Weapon B"), "Proficient in Weapon B");
		assertTrue(TestHelper.hasWeaponProfKeyed(character, "Weapon C"), "Proficient in Weapon C");
	}

	/**
	 * Tests CL variable.
	 */
	@Test
	public void testGetClassVar()
	{
		readyToRun();
		//Logging.setDebugMode(true);
		Logging.debugPrint("\n\n\ntestGetClassVar()");
		final PlayerCharacter character = new PlayerCharacter();
		character.setRace(human);
		character.incrementClassLevel(2, classWarmind, true);

		final Float result =
				character.getVariableValue("var(\"CL=Warmind\")", "");
		assertEquals(2.0, result.doubleValue(), 0.1, "CL count not correct");
	}

	/**
	 * Test the processing of the MAX function with respect to character stats.
	 */
	public void testMaxValue()
	{
		readyToRun();
		PlayerCharacter pc = getCharacter();
		LoadContext context = Globals.getContext();

		setPCStat(pc, str, 8);
		setPCStat(pc, dex, 14);
		pc.setUseTempMods(true);

		assertEquals(-1.0, pc.getVariableValue("STR", ""),
			0.1, "STR"
		);
		assertEquals(2.0, pc.getVariableValue("DEX", ""),
			0.1, "DEX"
		);
		assertEquals(2.0, pc.getVariableValue("max(STR,DEX)",
				""), 0.1, "max(STR,DEX)");

		StatToken statTok = new StatToken();
		assertEquals("14", statTok.getToken("STAT.1", pc, null), "Total stat.");
		assertEquals("14", statTok.getToken("STAT.1.NOEQUIP", pc,
			null), "Temp stat.");
		assertEquals("14", statTok.getToken("STAT.1.NOTEMP", pc,
			null), "Equip stat.");
		assertEquals("14", statTok.getToken(
			"STAT.1.NOEQUIP.NOTEMP", pc, null), "No equip/temp stat.");
		assertEquals("14", statTok.getToken(
			"STAT.1.NOEQUIP.NOTEMP", pc, null), "Base stat.");

		final BonusObj raceBonus = Bonus.newBonus(context, "STAT|DEX|-2");
		giantClass.addToListFor(ListKey.BONUS, raceBonus);
		pc.setRace(giantRace);
		pc.incrementClassLevel(4, giantClass, true);

		assertEquals("12", statTok.getToken("STAT.1", pc, null), "Total stat.");
		assertEquals("12", statTok.getToken("STAT.1.NOEQUIP", pc,
			null), "Temp stat.");
		assertEquals("12", statTok.getToken(
			"STAT.1.NOEQUIP.NOTEMP", pc, null), "Base stat.");
		assertEquals(1.0, pc.getVariableValue("DEX", ""),
			0.1, "DEX"
		);
		assertEquals(1.0, pc.getVariableValue("max(STR,DEX)",
				""), 0.1, "max(STR,DEX)");

		Spell spell2 = new Spell();
		spell2.setName("Concrete Boots");
		final BonusObj aBonus = Bonus.newBonus(context, "STAT|DEX|-2");
		
		if (aBonus != null)
		{
			spell2.addToListFor(ListKey.BONUS, aBonus);
		}
		BonusObj penalty = spell2.getRawBonusList(pc).get(0);
		pc.addTempBonus(penalty, spell2, pc);
		pc.calcActiveBonuses();

		assertEquals("10", statTok.getToken("STAT.1", pc, null), "Total stat.");
		assertEquals("10", statTok.getToken("STAT.1.NOEQUIP", pc,
			null), "Temp stat.");
		assertEquals("12", statTok.getToken(
			"STAT.1.NOEQUIP.NOTEMP", pc, null), "Base stat.");
		assertEquals(0.0, pc.getVariableValue("DEX", ""),
			0.1, "DEX"
		);
		assertEquals(1.0, pc.getVariableValue(
				"max(STR,DEX)-STR", ""), 0.1, "max(STR,DEX)-STR");
	}

	/**
	 * Test the skills visibility functionality. We want to ensure that
	 * each call retrieves the right set of skills.
	 */
	@Test
	public void testSkillsVisibility()
	{
		readyToRun();
		PlayerCharacter pc = getCharacter();

		Skill guiSkill = new Skill();
		Skill outputSkill = new Skill();
		Skill defaultSkill = new Skill();

		LoadContext context = Globals.getContext();

		context.unconditionallyProcess(guiSkill, "CLASSES", "MyClass");
		guiSkill.setName("GUI");
		guiSkill.addToListFor(ListKey.TYPE, Type.getConstant("INT"));
		guiSkill.put(ObjectKey.VISIBILITY, Visibility.DISPLAY_ONLY);
		SkillRankControl.modRanks(1.0, pcClass, true, pc, guiSkill);

		context.unconditionallyProcess(outputSkill, "CLASSES", "MyClass");
		outputSkill.setName("Output");
		outputSkill.addToListFor(ListKey.TYPE, Type.getConstant("INT"));
		outputSkill.put(ObjectKey.VISIBILITY, Visibility.OUTPUT_ONLY);
		SkillRankControl.modRanks(1.0, pcClass, true, pc, outputSkill);

		context.unconditionallyProcess(defaultSkill, "CLASSES", "MyClass");
		defaultSkill.setName("Default");
		defaultSkill.addToListFor(ListKey.TYPE, Type.getConstant("INT"));
		defaultSkill.put(ObjectKey.VISIBILITY, Visibility.DEFAULT);
		SkillRankControl.modRanks(1.0, pcClass, true, pc, defaultSkill);

		// Test retrieved list
		Collection<Skill> skillList = pc.getSkillSet();
		assertEquals(3, skillList
			.size(), "Full skill list should have all 3 skills");

		skillList = pc.getDisplay().getPartialSkillList(View.VISIBLE_DISPLAY);
		assertEquals(2, skillList.size(), "GUI skill list should have 2 skills");

		skillList = pc.getDisplay().getPartialSkillList(View.VISIBLE_EXPORT);
		assertEquals(2, skillList
			.size(), "Output skill list should have 2 skills");

		skillList = pc.getDisplay().getPartialSkillList(View.ALL);
		assertEquals(3, skillList
			.size(), "Full skill list should have 3 skills");

	}

	/**
	 * Tests adding a spell.
	 */
	@Test
	public void testAddSpells()
	{
		readyToRun();
		final PlayerCharacter character = new PlayerCharacter();
		character.setRace(human);
		character.incrementClassLevel(1, pcClass, true);

		final List<Ability> none = Collections.emptyList();
		String response =
				character
					.addSpell(null, none, pcClass.getKeyName(), null, 1, 1);
		assertEquals(
				"Invalid parameter to add spell", response, "Add spell should be rejected due to no spell");

		Spell spell = new Spell();
		spell.setName("test spell 1");
		CharacterSpell charSpell = new CharacterSpell(pcClass, spell);
		response =
				character.addSpell(charSpell, none, pcClass.getKeyName(), null,
					1, 1);
		assertEquals(
				"Invalid spell list/book name.", response, "Add spell should be rejected due to no book");
		response =
				character.addSpell(charSpell, none, pcClass.getKeyName(), "",
					1, 1);
		assertEquals(
				"Invalid spell list/book name.", response, "Add spell should be rejected due to no book");

		// Add a non existant spell to a non existent spellbook
		String spellBookName = "Test book";
		response =
				character.addSpell(charSpell, none, pcClass.getKeyName(),
					spellBookName, 1, 1);
		assertEquals(
				"Could not find spell list/book Test book", response,
				"Add spell should be rejected due to book not existing");

		character.addSpellBook(spellBookName);
		response =
				character.addSpell(charSpell, none, pcClass.getKeyName(),
					spellBookName, 1, 1);
		assertEquals(
				"You can only prepare 0 spells for level 1 \nand there are no higher-level slots available.",
			response, "Add spell should be rejected due to no levels."
		);

		response =
				character.addSpell(charSpell, none, "noclass", spellBookName,
					1, 1);
		assertEquals(
				"No class keyed noclass", response, "Add spell should be rejected due to no matching class");

		SpellBook book = character.getSpellBookByName(spellBookName);
		book.setType(SpellBook.TYPE_PREPARED_LIST);
		character.addSpellBook(spellBookName);
		response =
				character.addSpell(charSpell, none, pcClass.getKeyName(),
					spellBookName, 1, 1);
		assertEquals(
				"You can only prepare 0 spells for level 1 \nand there are no higher-level slots available.",
			response, "Add spell should be rejected due to no levels."
		);

		book.setType(SpellBook.TYPE_SPELL_BOOK);
		book.setPageFormula(FormulaFactory.getFormulaFor("SPELLLEVEL"));
		book.setNumPages(3);
		character.addSpellBook(spellBookName);
		response =
				character.addSpell(charSpell, none, pcClass.getKeyName(),
					spellBookName, 1, 1);
		assertEquals("", response, "Add spell should not be rejected.");
		// Add a second time to cover multiples
		response =
				character.addSpell(charSpell, none, pcClass.getKeyName(),
					spellBookName, 1, 1);
		assertEquals("", response, "Add spell should not be rejected.");
		response =
				character.addSpell(charSpell, none, giantClass.getKeyName(),
					spellBookName, 1, 1);
		assertEquals("", response, "Add spell should not be rejected.");
		response =
				character.addSpell(charSpell, none, giantClass.getKeyName(),
					spellBookName, 1, 1);
		assertEquals(
				"There are not enough pages left to add this spell to the spell book.",
			response, "Add spell should be rejected due to the book being full."
		);

		PCClass c = character.getClassKeyed(pcClass.getKeyName());
		List<CharacterSpell> aList =
				character.getCharacterSpells(c, null, spellBookName, 1);
		CharacterSpell addedSpell = aList.get(0);
		response =
				character.delSpell(addedSpell.getSpellInfoFor(spellBookName, 1,
					none), pcClass, spellBookName);
		assertEquals("", response, "Delete spell should not be rejected.");

		aList =
				character.getCharacterSpells(giantClass, null, spellBookName, 1);
		addedSpell = aList.get(0);
		response =
				character.delSpell(
					addedSpell.getSpellInfoFor(spellBookName, 1),
					giantClass, spellBookName);
		assertEquals("", response, "Delete spell should not be rejected.");
	}

	/**
	 * Tests available spell slot calculations for a divine caster who 
	 * memorizes spells.
	 */
	@Test
	public void testAvailableSpellsMemorizedDivine()
	{
		readyToRun();
		final PlayerCharacter character = new PlayerCharacter();
		character.setRace(human);
		character.setStat(wis, 15);
		character.incrementClassLevel(1, classMemDivine, true);
		PCClass pcMdClass = character.getClassKeyed(classMemDivine.getKeyName());

		Spell spellNonSpec0 = new Spell();
		spellNonSpec0.setName("Basic Spell Lvl0");
		CharacterSpell charSpellNonSpec0 = new CharacterSpell(pcMdClass, spellNonSpec0);
		Spell spellNonSpec1 = new Spell();
		spellNonSpec1.setName("Basic Spell Lvl1");
		CharacterSpell charSpellNonSpec1 = new CharacterSpell(pcMdClass, spellNonSpec1);
		Spell spellNonSpec2 = new Spell();
		spellNonSpec2.setName("Basic Spell Lvl2");
		CharacterSpell charSpellNonSpec2 = new CharacterSpell(pcMdClass, spellNonSpec2);
		
		final List<Ability> none = Collections.emptyList();
		boolean available =
				character.availableSpells(1, pcMdClass, Globals.getDefaultSpellBook(), true, false);
		assertFalse(available,
				"availableSpells should not be called when there ar eno limits on known spells");
		
		// Test specialty/non with no spells, some spells, all spells, spells from lower level
		String spellBookName = "Town Spells";
		SpellBook townSpells = new SpellBook(spellBookName, SpellBook.TYPE_PREPARED_LIST);
		assertTrue(character.addSpellBook(townSpells), "Adding spellbook " + townSpells);
		assertTrue(character.addDomain(luckDomain), "Adding domain " + luckDomain);
		DomainApplication.applyDomain(character, luckDomain);

		// Test for spell availability with no spells in list
		for (int i = 0; i < 3; i++)
		{
			assertTrue(
					character.availableSpells(i, pcMdClass, townSpells.getName(), false, false),
					"Empty list - Non specialty available for level " + i
			);
			assertEquals(i>0,
					character.availableSpells(i, pcMdClass, townSpells.getName(), false, true),
					"Empty list - Specialty available for level " + i
			);
		}

		// Test for spell availability with some spells in list
		assertEquals("", character.addSpell(charSpellNonSpec0, none, pcMdClass.getKeyName(),
			spellBookName, 0, 0));
		assertEquals("", character.addSpell(charSpellNonSpec1, none, pcMdClass.getKeyName(),
			spellBookName, 1, 1));
		assertEquals("", character.addSpell(charSpellNonSpec2, none, pcMdClass.getKeyName(),
			spellBookName, 2, 2));
		for (int i = 0; i < 3; i++)
		{
			assertTrue(
					character.availableSpells(i, pcMdClass, townSpells.getName(), false, false),
					"Partial list - Non specialty available for level " + i
			);
			assertEquals(i>0,
					character.availableSpells(i, pcMdClass, townSpells.getName(), false, true),
					"Partial list - Specialty available for level " + i
			);
		}

		// Test for spell availability with only 1st level with a spare slot
		assertEquals("", character.addSpell(charSpellNonSpec0, none, pcMdClass.getKeyName(),
			spellBookName, 0, 0));
		assertEquals("", character.addSpell(charSpellNonSpec0, none, pcMdClass.getKeyName(),
			spellBookName, 0, 0));
		assertEquals("", character.addSpell(charSpellNonSpec2, none, pcMdClass.getKeyName(),
			spellBookName, 2, 2));
		for (int i = 0; i < 3; i++)
		{
			assertEquals(i==1,
					character.availableSpells(i, pcMdClass, townSpells.getName(), false, false),
					"Full lvl0, lvl2 list - Non specialty available for level " + i
			);
			// TODO: The current implementation only finds the domain specialty slot if a domain spell is already
			// prepared.
			// So the domain spell can't be the last added. Once fixed, i==1 should be i>=1
			assertEquals(i>=1,
					character.availableSpells(i, pcMdClass, townSpells.getName(), false, true),
					"Full lvl0, lvl2 list - Specialty available for level " + i
			);
		}

		// Test for spell availability with 1st having one domain spell full and one non domain free
		CharacterSpell charSpellSpec1 = new CharacterSpell(luckDomain, luckDomainLvl1Spell);
		assertEquals("", character.addSpell(charSpellSpec1, none, pcMdClass.getKeyName(),
			spellBookName, 1, 1));
		for (int i = 0; i < 3; i++)
		{
			assertEquals(i==1,
					character.availableSpells(i, pcMdClass, townSpells.getName(), false, false),
					"Specialty: No, Level: " + i + ". 1st lvl non domain only free"
			);
			assertEquals(i==2,
					character.availableSpells(i, pcMdClass, townSpells.getName(), false, true),
					"Specialty: Yes, Level: " + i + ". 1st lvl non domain only free"
			);
		}

		// Test for spell availability with 2nd having both domain and normal full
		CharacterSpell charSpellSpec2 = new CharacterSpell(luckDomain, luckDomainLvl2Spell);
		assertEquals("", character.addSpell(charSpellSpec2, none, pcMdClass.getKeyName(),
			spellBookName, 2, 2));
		for (int i = 0; i < 3; i++)
		{
			assertEquals(i==1,
					character.availableSpells(i, pcMdClass, townSpells.getName(), false, false),
					"Specialty: No, Level: " + i + ". 1st lvl non domain only free"
			);
			assertFalse(
					character.availableSpells(i, pcMdClass, townSpells.getName(), false, true),
					"Specialty: Yes, Level: " + i + ". 1st lvl non domain only free"
			);
		}
	}

	@Test
	public void testIsNonAbility()
	{
		readyToRun();
		PlayerCharacter pc = getCharacter();

		//Base
		assertFalse(pc.isNonAbility(str), "Initially character should not have a locked ability");

		// With template lock
		PCTemplate nonAbilityLocker = new PCTemplate();
		nonAbilityLocker.setName("locker");
		CDOMDirectSingleRef<PCStat> strRef = CDOMDirectSingleRef.getRef(str);
		nonAbilityLocker.addToListFor(ListKey.NONSTAT_STATS, strRef);
		pc.addTemplate(nonAbilityLocker);
		assertTrue(pc.isNonAbility(str), "STR now locked to non ability");
		pc.removeTemplate(nonAbilityLocker);
		assertFalse(pc.isNonAbility(str), "STR no longer locked to non ability");
		
		// With race lock
		Race nonAbilityLockerRace = new Race();
		nonAbilityLockerRace.setName("locker");
		nonAbilityLockerRace.addToListFor(ListKey.NONSTAT_STATS, strRef);
		pc.setRace(nonAbilityLockerRace);
		assertTrue(pc.isNonAbility(str), "STR now locked to non ability");
		
		// With template unlock
		nonAbilityLocker.addToListFor(ListKey.NONSTAT_TO_STAT_STATS, strRef);
		pc.addTemplate(nonAbilityLocker);
		assertFalse(pc.isNonAbility(str), "STR now unlocked from a non ability by template");
		pc.removeTemplate(nonAbilityLocker);
		assertTrue(pc.isNonAbility(str), "STR no longer locked to non ability");
		
		// With race unlock
		nonAbilityLockerRace.addToListFor(ListKey.NONSTAT_TO_STAT_STATS, strRef);
		//This weirdness is because we are altering the race after application (no-no at runtime)
		pc.setRace(null);
		pc.setRace(nonAbilityLockerRace);
		assertFalse(pc.isNonAbility(str), "STR now unlocked from a non ability by race");
	}
	
	/**
	 * Test the stacking of the same ability added via different abiltiy 
	 * categories.
	 */
	@Test
	public void testStackDifferentAbiltyCat()
	{
		readyToRun();
		PlayerCharacter pc = getCharacter();
		double base = pc.getTotalBonusTo("HP", "CURRENTMAX");
		
		assertEquals(base, pc.getTotalBonusTo(
			"HP", "CURRENTMAX"), "Check repeatability of bonus");
		
		try
		{
			AbstractCharacterTestCase.applyAbility(pc, BuildUtilities.getFeatCat(), toughness, "");
			//pc.calcActiveBonuses();
			assertEquals(base+3, pc.getTotalBonusTo(
				"HP", "CURRENTMAX"), "Check application of single bonus");
			AbstractCharacterTestCase.applyAbility(pc, BuildUtilities.getFeatCat(), toughness, "");
			pc.calcActiveBonuses();
			assertEquals(base+6, pc.getTotalBonusTo(
				"HP", "CURRENTMAX"), "Check application of second bonus");

			AbstractCharacterTestCase.applyAbility(pc, specialFeatCat, toughness,
					"Toughness");
			pc.calcActiveBonuses();
			assertEquals(
					base + 9, pc.getTotalBonusTo("HP", "CURRENTMAX"),
					"Check application of third bonus in different catgeory");
		}
		catch (HeadlessException e)
		{
			Logging.debugPrint("Ignoring Headless exception.");
		}
	}
	
	/**
	 * Verify that bested abilities are processed correctly.
	 */
	@Test
	public void testNestedAbilities()
	{
		PlayerCharacter pc = getCharacter();
		Ability resToAcid =
				TestHelper.makeAbility("Resistance To Acid", specialAbilityCat, "Foo");
		Ability resToAcidOutputVirt =
			TestHelper.makeAbility("Resistance To Acid Output Virt",
				specialAbilityCat, "Foo");
		Ability resToAcidOutputAuto =
			TestHelper.makeAbility("Resistance To Acid Output Auto",
				specialAbilityCat, "Foo");

		LoadContext context = Globals.getContext();
		context.unconditionallyProcess(human, "ABILITY", specialAbilityCat
				.getKeyName()
				+ "|AUTOMATIC|" + resToAcid.getKeyName());

		context.unconditionallyProcess(resToAcid, "ABILITY", specialAbilityCat
				.getKeyName()
				+ "|VIRTUAL|" + resToAcidOutputVirt.getKeyName());

		context.unconditionallyProcess(resToAcid, "ABILITY", specialAbilityCat
				.getKeyName()
				+ "|AUTOMATIC|" + resToAcidOutputAuto.getKeyName());
		readyToRun();
		pc.setRace(human);
		assertEquals(human, pc.getRace(), "PC should now have a race of human");
		assertFalse(pc.getMatchingCNAbilities(resToAcid).isEmpty(), "Character should have the first feat");
		assertFalse(pc.getMatchingCNAbilities(resToAcidOutputVirt).isEmpty(),
				"Character should have the second feat");
		assertFalse(pc.getMatchingCNAbilities(resToAcidOutputAuto).isEmpty(),
				"Character should have the third feat");
		
	}

	@Test
	public void testGetPartialStatFor()
	{
		readyToRun();
		PlayerCharacter pc = getCharacter();
		LoadContext context = Globals.getContext();

		setPCStat(pc, str, 14);

		Ability strBonusAbility =
				TestHelper.makeAbility("Strength power up", BuildUtilities.getFeatCat(),
					"General.Fighter");
		final BonusObj strBonus = Bonus.newBonus(context, "STAT|STR|2");
		strBonusAbility.addToListFor(ListKey.BONUS, strBonus);

		assertEquals(14, pc.getPartialStatFor(str, false, false), "Before bonus, no temp no equip");
		assertEquals(14, pc.getPartialStatFor(str, true, false), "Before bonus, temp no equip");

		AbstractCharacterTestCase.applyAbility(pc, BuildUtilities.getFeatCat(), strBonusAbility, null);
		pc.calcActiveBonuses();

		assertEquals(16, pc.getPartialStatFor(str, false, false), "After bonus, no temp no equip");
		assertEquals(16, pc.getPartialStatFor(str, true, false), "After bonus, temp no equip");

//		final BonusObj strBonusViaList = Bonus.newBonus("STAT|%LIST|3");
//		strBonusAbility.addBonusList(strBonusViaList);
//		strBonusAbility.addAssociated("STR");
//		strBonusAbility.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
//		pc.calcActiveBonuses();
//
//		assertEquals("After list bonus, no temp no equip", 3, pc.getPartialStatBonusFor("STR", false, false));
//		assertEquals("After list bonus, temp no equip", 3, pc.getPartialStatBonusFor("STR", true, false));
		
	}
	
	/**
	 * Validate the getAvailableFollowers function.
	 */
	@Test
	public void testGetAvailableFollowers()
	{
		readyToRun();
		Ability ab = TestHelper.makeAbility("Tester1", BuildUtilities.getFeatCat(), "Empty Container");
		Ability mab = TestHelper.makeAbility("Tester2", BuildUtilities.getFeatCat(), "Mount Container");
		Ability fab = TestHelper.makeAbility("Tester3", BuildUtilities.getFeatCat(), "Familiar Container");
		PlayerCharacter pc = getCharacter();
		CharacterDisplay display = pc.getDisplay();
		
		addAbility(BuildUtilities.getFeatCat(), ab);
		CDOMSingleRef<CompanionList> ref = new CDOMSimpleSingleRef<>(
				BasicClassIdentity.getIdentity(CompanionList.class), "Mount");
		CDOMReference<Race> race  = new CDOMDirectSingleRef<>(giantRace);
		FollowerOption option = new FollowerOption(race, ref);
		mab.addToListFor(ListKey.COMPANIONLIST, option);
		ref = new CDOMSimpleSingleRef<>(
				BasicClassIdentity.getIdentity(CompanionList.class), "Familiar");
		race  = new CDOMDirectSingleRef<>(human);
		option = new FollowerOption(race, ref);
		fab.addToListFor(ListKey.COMPANIONLIST, option);
		
		Set<FollowerOption> fo = display.getAvailableFollowers("Familiar", null).keySet();
		assertTrue(fo.isEmpty(), "Initially familiar list should be empty");
		fo = display.getAvailableFollowers("MOUNT", null).keySet();
		assertTrue(fo.isEmpty(), "Initially mount list should be empty");
		
		addAbility(BuildUtilities.getFeatCat(), mab);
		fo = display.getAvailableFollowers("Familiar", null).keySet();
		assertTrue(fo.isEmpty(), "Familiar list should still be empty");
		fo = display.getAvailableFollowers("MOUNT", null).keySet();
		assertFalse(fo.isEmpty(), "Mount list should not be empty anymore");
		assertEquals(
				giantRace.getKeyName(), fo.iterator().next().getRace().getKeyName(), "Mount should be the giant race");
		assertEquals(1, fo.size(), "Mount list should only have one entry");

		addAbility(BuildUtilities.getFeatCat(), fab);
		fo = display.getAvailableFollowers("Familiar", null).keySet();
		assertFalse(fo.isEmpty(), "Familiar list should not be empty anymore");
		assertEquals(human.getKeyName(),
			fo.iterator().next().getRace().getKeyName(), "Familiar should be the human race"
		);
		assertEquals(1, fo.size(), "Familiar list should only have one entry");
		fo = display.getAvailableFollowers("MOUNT", null).keySet();
		assertFalse(fo.isEmpty(), "Mount list should not be empty anymore");
		assertEquals(giantRace.getKeyName(),
			fo.iterator().next().getRace().getKeyName(), "Mount should be the giant race"
		);
		assertEquals(1, fo.size(), "Mount list should only have one entry");
	}

	@Test
	public void testGetAggregateAbilityList()
	{
		Ability resToAcid =
				TestHelper.makeAbility("Swelter",
					BuildUtilities.getFeatCat().getKeyName(), "Foo");
		LoadContext context = Globals.getContext();
		context.unconditionallyProcess(resToAcid, "MULT", "YES");
		context.unconditionallyProcess(resToAcid, "STACK", "YES");
		context.unconditionallyProcess(resToAcid, "CHOOSE", "NOCHOICE");
		PCTemplate template = TestHelper.makeTemplate("TemplateVirt"); 
		PCTemplate templateNorm = TestHelper.makeTemplate("TemplateNorm"); 
		context.getReferenceContext().importObject(resToAcid);
		context.unconditionallyProcess(human, "ABILITY", "FEAT|AUTOMATIC|KEY_Swelter");
		context.unconditionallyProcess(template, "ABILITY", "FEAT|VIRTUAL|KEY_Swelter");
		context.unconditionallyProcess(templateNorm, "ABILITY", "FEAT|NORMAL|KEY_Swelter");
		readyToRun();
		PlayerCharacter pc = getCharacter();
		
		List<Ability> abList = pc.getAggregateAbilityListNoDuplicates(BuildUtilities.getFeatCat());
		assertEquals(0, abList.size());

		pc.setRace(human);
		abList = pc.getAggregateAbilityListNoDuplicates(BuildUtilities.getFeatCat());
		assertEquals(1, abList.size());
		
		pc.addTemplate(template);
		abList = pc.getAggregateAbilityListNoDuplicates(BuildUtilities.getFeatCat());
		assertEquals(1, abList.size());
		
		pc.addTemplate(templateNorm);
		abList = pc.getAggregateAbilityListNoDuplicates(BuildUtilities.getFeatCat());
		assertEquals(1, abList.size());
	}

	/**
	 * Test the processing and order of operations of the adjustMoveRates method.
	 */
	@Test
	public void testAdjustMoveRates()
	{
		Ability quickFlySlowSwim =
				TestHelper.makeAbility("quickFlySlowSwim", BuildUtilities.getFeatCat()
					.getKeyName(), "Foo");
		PCTemplate template = TestHelper.makeTemplate("slowFlyQuickSwim");
		PCTemplate template2 = TestHelper.makeTemplate("dig");
		LoadContext context = Globals.getContext();
		final BonusObj digBonus = Bonus.newBonus(context, "MOVEADD|TYPE.Dig|60");
		assertNotNull(digBonus, "Failed to create bonus");
		template2.addToListFor(ListKey.BONUS, digBonus);

		//template.addm
		context.getReferenceContext().importObject(quickFlySlowSwim);
		context.getReferenceContext().importObject(template2);
		context.unconditionallyProcess(human, "MOVE", "Walk,30");
		context.unconditionallyProcess(quickFlySlowSwim, "MOVE",
			"Swim,10,Fly,30");
		context.unconditionallyProcess(template, "MOVE", "Swim,30,Fly,10");
		readyToRun();
		GameMode game = SettingsHandler.getGameAsProperty().get();
		LoadInfo li = game.getModeContext().getReferenceContext().constructNowIfNecessary(
				LoadInfo.class, game.getName());
		li.addLoadScoreValue(0, new BigDecimal("100.0"));
		li.addLoadScoreValue(10, new BigDecimal("100.0"));
		li.addLoadMultiplier("LIGHT", 100f, "100", 0);

		PlayerCharacter pc = getCharacter();
		setPCStat(pc, str, 10);
		pc.setRace(human);
		pc.calcActiveBonuses();
		pc.adjustMoveRates();
		CharacterDisplay display = pc.getDisplay();
		assertEquals(0.0, display.movementOfType(MovementType.getConstant("Swim")), 0.1);
		assertEquals(0.0, display.movementOfType(MovementType.getConstant("Fly")), 0.1);

		addAbility(BuildUtilities.getFeatCat(), quickFlySlowSwim);
		pc.calcActiveBonuses();
		pc.adjustMoveRates();
		assertEquals(10.0, display.movementOfType(MovementType.getConstant("Swim")), 0.1);
		assertEquals(30.0, display.movementOfType(MovementType.getConstant("Fly")), 0.1);

		pc.addTemplate(template);
		pc.adjustMoveRates();
		assertEquals(30.0, display.movementOfType(MovementType.getConstant("Swim")), 0.1);
		assertEquals(30.0, display.movementOfType(MovementType.getConstant("Fly")), 0.1);

		pc.addTemplate(template2);
		pc.adjustMoveRates();
		assertEquals(30.0, display.movementOfType(MovementType.getConstant("Swim")), 0.1);
		assertEquals(30.0, display.movementOfType(MovementType.getConstant("Fly")), 0.1);
		assertEquals(60.0, display.movementOfType(MovementType.getConstant("Dig")), 0.1);
	}

	@Test
	public void testMakeIntoExClass()
	{
		// Prepare class and ex-class
		LoadContext context = Globals.getContext();
		PCClass paladin = new PCClass();
		paladin.setName("Paladin");
		context.getReferenceContext().importObject(paladin);
		PCClass exPaladin = new PCClass();
		exPaladin.setName("exPaladin");
		context.getReferenceContext().importObject(exPaladin);
		paladin.put(
			ObjectKey.EX_CLASS, context.getReferenceContext().getCDOMReference(PCClass.class, exPaladin.getKeyName()));
		readyToRun();
		
		PlayerCharacter pc = getCharacter();
		// Add a level of the class
		pc.incrementClassLevel(2, paladin, true, false);
		PCClass pcPalClass = pc.getClassKeyed(paladin.getKeyName());
		pc.setHP(pc.getActiveClassLevel(pcPalClass, 0), 10);
		pc.setHP(pc.getActiveClassLevel(pcPalClass, 1), 6);
		
		// Make it into an ex-class
		pc.makeIntoExClass(pcPalClass);
		
		assertNull(pc.getClassKeyed(paladin.getKeyName()), "Paladin class should not be held");
		PCClass pcExPalClass = pc.getClassKeyed(exPaladin.getKeyName());
		assertNotNull(pcExPalClass, "Ex-Paladin class should be held");
		PCClassLevel pcLvl1 = pc.getActiveClassLevel(pcExPalClass, 0);
		assertNotNull(pcLvl1, "Level 1 should be Ex-Paladin");
		assertEquals(2, pc.getTotalLevels(), "Should still be level 2 character");
		assertEquals(10, (int) pc.getHP(pcLvl1), "Hp at first level incorrect");
	}

	@Test
	public void testGetVariableCachingRollTopNode()
	{
		readyToRun();
		final PlayerCharacter character = new PlayerCharacter();
		character.setRace(human);
		character.setStat(str, 16);
		character.incrementClassLevel(2, pcClass, true);

		int iVal = character.getVariableValue("roll(\"1d100\")", "").intValue();
		boolean match = true;
		for (int i = 0; i < 10; i++)
		{
			int rolledValue = character.getVariableValue("roll(\"1d100\")", "")
					.intValue();
			match = (iVal == rolledValue);
			if (!match)
			{
				break;
			}
		}

		assertFalse(match, "Roll function should not be cached.");
	}

	/**
	 * Validate the checkSkillModChange correctly handles non bonused
	 * skill pools.
	 */
	@Test
	public void testCheckSkillModChangeNoBonus()
	{
		readyToRun();
		PlayerCharacter character = getCharacter();
		character.setRace(human);
		character.setStat(intel, 10);
		character.incrementClassLevel(2, pcClass, true);
		
		List<PCLevelInfo> levelInfoList = new ArrayList<>(character.getLevelInfo());
		
		assertEquals(1, levelInfoList.get(0)
		                                        .getClassLevel(), "Level number lvl 1");
		assertEquals(2, levelInfoList.get(1)
		                                        .getClassLevel(), "Level number lvl 2");
		assertEquals(1, levelInfoList.get(0)
		                                        .getSkillPointsRemaining(), "Skills remaining lvl 1");
		assertEquals(1, levelInfoList.get(1)
		                                        .getSkillPointsGained(character), "Skills gained lvl 2");
		assertEquals(1, levelInfoList.get(1)
		                                        .getSkillPointsRemaining(), "Skills remaining lvl 2");

		character.checkSkillModChange();
		
		assertEquals(1, levelInfoList.get(0)
		                                        .getSkillPointsGained(character), "Skills gained lvl 1");
		assertEquals(1, levelInfoList.get(0)
		                                        .getSkillPointsRemaining(), "Skills remaining lvl 1");
		assertEquals(1, levelInfoList.get(1)
		                                        .getSkillPointsGained(character), "Skills gained lvl 2");
		assertEquals(1, levelInfoList.get(1)
		                                        .getSkillPointsRemaining(), "Skills remaining lvl 2");
	}

	/**
	 * Validate the checkSkillModChange correctly handles SKILLPOOL bonuses
	 */
	@Test
	public void testCheckSkillModChangeWithBonus()
	{
		readyToRun();
		PlayerCharacter character = getCharacter();
		character.setRace(human);
		character.setStat(intel, 10);
		PCTemplate template = TestHelper.makeTemplate("grantsskills");
		LoadContext context = Globals.getContext();
		final BonusObj skillBonusLvl1 = Bonus.newBonus(context, "SKILLPOOL|CLASS=MyClass;LEVEL=1|2");
		assertNotNull(skillBonusLvl1, "Failed to create bonus");
		template.addToListFor(ListKey.BONUS, skillBonusLvl1);
		character.addTemplate(template);
		character.incrementClassLevel(2, pcClass, true);
		
		List<PCLevelInfo> levelInfoList = new ArrayList<>(character.getLevelInfo());
		
		assertEquals(1, levelInfoList.get(0)
		                                        .getClassLevel(), "Level number lvl 1");
		assertEquals(2, levelInfoList.get(1)
		                                        .getClassLevel(), "Level number lvl 2");
		assertEquals(3, levelInfoList.get(0)
		                                        .getSkillPointsGained(character), "Skills gained lvl 1");
		assertEquals(3, levelInfoList.get(0)
		                                        .getSkillPointsRemaining(), "Skills remaining lvl 1");
		assertEquals(1, levelInfoList.get(1)
		                                        .getSkillPointsGained(character), "Skills gained lvl 2");
		assertEquals(1, levelInfoList.get(1)
		                                        .getSkillPointsRemaining(), "Skills remaining lvl 2");

		character.checkSkillModChange();
		character.checkSkillModChange();
		
		assertEquals(3, levelInfoList.get(0)
		                                        .getSkillPointsGained(character), "Skills gained lvl 1");
		assertEquals(3, levelInfoList.get(0)
		                                        .getSkillPointsRemaining(), "Skills remaining lvl 1");
		assertEquals(1, levelInfoList.get(1)
		                                        .getSkillPointsGained(character), "Skills gained lvl 2");
		assertEquals(1, levelInfoList.get(1)
		                                        .getSkillPointsRemaining(), "Skills remaining lvl 2");

	}

	/**
	 * Test method for pcgen.core.PlayerCharacter.baseAttackBonus()
	 *  and for method pcgen.core.PlayerCharacter.getNumAttacks()
	 *
	 * Testing with a fighter class from level 1 to level 20
	 *
	 * @throws Exception
	 *
	 * TODO Testing at epic levels 21+ needs to be fixed.
	 */
	@Test
	public void testbaseAttackBonusAndgetNumAttacks() throws Exception 
	{
		readyToRun();
		LoadContext context = Globals.getContext();
		GameMode gamemode = SettingsHandler.getGameAsProperty().get();
		gamemode.setMaxNonEpicLevel(20);
		PCClass fighterClass;
		fighterClass = new PCClass();
		fighterClass.setName("Fighter");
		BuildUtilities.setFact(fighterClass, "ClassType", "Base.PC");
		final BonusObj babClassBonus = Bonus.newBonus(context,
				"COMBAT|BASEAB|classlevel(\"APPLIEDAS=NONEPIC\")|TYPE=Base.REPLACE");
		fighterClass.getOriginalClassLevel(1).addToListFor(ListKey.BONUS, babClassBonus);
		context.getReferenceContext().importObject(fighterClass);
		final PlayerCharacter character = new PlayerCharacter();
		character.setRace(human);
		character.incrementClassLevel(1, fighterClass, true);
		assertEquals(1, character.baseAttackBonus());
		assertEquals(1, character.getNumAttacks());
		character.incrementClassLevel(1, fighterClass, true);
		assertEquals(2, character.baseAttackBonus());
		assertEquals(1, character.getNumAttacks());
		character.incrementClassLevel(1, fighterClass, true);
		assertEquals(3, character.baseAttackBonus());
		assertEquals(1, character.getNumAttacks());
		character.incrementClassLevel(1, fighterClass, true);
		assertEquals(4, character.baseAttackBonus());
		assertEquals(1, character.getNumAttacks());
		character.incrementClassLevel(1, fighterClass, true);
		assertEquals(5, character.baseAttackBonus());
		assertEquals(1, character.getNumAttacks());
		character.incrementClassLevel(1, fighterClass, true);
		assertEquals(6, character.baseAttackBonus());
		assertEquals(2, character.getNumAttacks());
		character.incrementClassLevel(1, fighterClass, true);
		assertEquals(7, character.baseAttackBonus());
		assertEquals(2, character.getNumAttacks());
		character.incrementClassLevel(1, fighterClass, true);
		assertEquals(8, character.baseAttackBonus());
		assertEquals(2, character.getNumAttacks());
		character.incrementClassLevel(1, fighterClass, true);
		assertEquals(9, character.baseAttackBonus());
		assertEquals(2, character.getNumAttacks());
		character.incrementClassLevel(1, fighterClass, true);
		assertEquals(10, character.baseAttackBonus());
		assertEquals(2, character.getNumAttacks());
		character.incrementClassLevel(1, fighterClass, true);
		assertEquals(11, character.baseAttackBonus());
		assertEquals(3, character.getNumAttacks());
		character.incrementClassLevel(1, fighterClass, true);
		assertEquals(12, character.baseAttackBonus());
		assertEquals(3, character.getNumAttacks());
		character.incrementClassLevel(1, fighterClass, true);
		assertEquals(13, character.baseAttackBonus());
		assertEquals(3, character.getNumAttacks());
		character.incrementClassLevel(1, fighterClass, true);
		assertEquals(14, character.baseAttackBonus());
		assertEquals(3, character.getNumAttacks());
		character.incrementClassLevel(1, fighterClass, true);
		assertEquals(15, character.baseAttackBonus());
		assertEquals(3, character.getNumAttacks());
		character.incrementClassLevel(1, fighterClass, true);
		assertEquals(16, character.baseAttackBonus());
		assertEquals(4, character.getNumAttacks());
		character.incrementClassLevel(1, fighterClass, true);
		assertEquals(17, character.baseAttackBonus());
		assertEquals(4, character.getNumAttacks());
		character.incrementClassLevel(1, fighterClass, true);
		assertEquals(18, character.baseAttackBonus());
		assertEquals(4, character.getNumAttacks());
		character.incrementClassLevel(1, fighterClass, true);
		assertEquals(19, character.baseAttackBonus());
		assertEquals(4, character.getNumAttacks());
		character.incrementClassLevel(1, fighterClass, true);
		assertEquals(20, character.baseAttackBonus());
		assertEquals(4, character.getNumAttacks());
		//
		// Disabled testing for level 21+ as it is not correctly implemented.
		//
		// character.incrementClassLevel(1, fighterClass, true);
		// assertEquals(20, (int) character.baseAttackBonus());
		// assertEquals(4, (int) character.getNumAttacks());
	}
}
