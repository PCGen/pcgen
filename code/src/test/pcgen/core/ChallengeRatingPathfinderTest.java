/*
 * Copyright James Dempsey, 2012
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
package pcgen.core;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import pcgen.AbstractCharacterTestCase;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.GenericLoader;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.persistence.lst.SimpleLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.TestHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TestURI;

/**
 * The Class {@code ChallengeRatingPathfinderTest} checks the calculation
 * of challenge ratings for the Pathfinder RPG game mode
 */
public class ChallengeRatingPathfinderTest extends AbstractCharacterTestCase
{
	private Race standardRace;
	private Race koboldRace;
	private Race drowNobleRace;
	private Race babauRace;
	private Race dryadRace;
	private Race companionRace;
	private Race zombieRace;
	private Race direRatRace;
	private Race miteRace;
	private Race beetleRace;
	private Race centipedeRace;
	
	private PCClass pcClass;
	private PCClass pcClass2;
	private PCClass npcClass;
	private PCClass npcClass2;
	private PCClass companionClass;

	@BeforeEach
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		GameMode gameMode = SettingsHandler.getGameAsProperty().get();
		gameMode.addCRstep(0, "1/2");
		gameMode.addCRstep(-1, "1/3");
		gameMode.addCRstep(-2, "1/4");
		gameMode.addCRstep(-3, "1/6");
		gameMode.addCRstep(-4, "1/8");
		gameMode.setCRThreshold("BASECR");
		gameMode.setMonsterRoleList(new ArrayList<>(Arrays.asList("Combat", "Skill", "Druid")));
		SimpleLoader<ClassType> methodLoader = new SimpleLoader<>(ClassType.class);
		LoadContext modeContext = gameMode.getModeContext();
		methodLoader.parseLine(modeContext,
			"PC			CRFORMULA:CL	ISMONSTER:NO	CRMOD:-1	CRMODPRIORITY:1",
			TestURI.getURI());
		methodLoader.parseLine(modeContext,
			"NPC			CRFORMULA:CL	ISMONSTER:NO	CRMOD:-2	CRMODPRIORITY:2",
			TestURI.getURI());
		methodLoader.parseLine(modeContext,
			"Monster		CRFORMULA:0		ISMONSTER:YES", TestURI.getURI());
		methodLoader.parseLine(modeContext, "Companion	CRFORMULA:NONE	ISMONSTER:YES",
			TestURI.getURI());
		
		LoadContext context = Globals.getContext();

		CampaignSourceEntry source = TestHelper.createSource(getClass());
		GenericLoader<Race> raceLoader = new GenericLoader<>(Race.class);
		PCClassLoader classLoader = new PCClassLoader();

		final String standardRaceLine = "Standard Race";
		raceLoader.parseLine(context, null, standardRaceLine, source);
		standardRace = context.getReferenceContext().silentlyGetConstructedCDOMObject(Race.class, "Standard Race");

		final String koboldRaceLine = "Kobold	CRMOD:NPC|-3";
		raceLoader.parseLine(context, null, koboldRaceLine, source);
		koboldRace = context.getReferenceContext().silentlyGetConstructedCDOMObject(Race.class, "Kobold");

		final String drowNobleLine = "Drow Noble	CRMOD:PC.NPC|0";
		raceLoader.parseLine(context, null, drowNobleLine, source);
		drowNobleRace = context.getReferenceContext().silentlyGetConstructedCDOMObject(Race.class, "Drow Noble");

		final String babauLine = "Babau	MONSTERCLASS:TestMonsterClass:7	CR:6	ROLE:Combat.Skill";
		raceLoader.parseLine(context, null, babauLine, source);
		babauRace = context.getReferenceContext().silentlyGetConstructedCDOMObject(Race.class, "Babau");

		final String dryadLine = "Dryad	MONSTERCLASS:TestMonsterClass:8	CR:7	ROLE:Druid";
		raceLoader.parseLine(context, null, dryadLine, source);
		dryadRace = context.getReferenceContext().silentlyGetConstructedCDOMObject(Race.class, "Dryad");

		final String companionLine = "TestCompanion	MONSTERCLASS:TestCompanionClass:4";
		raceLoader.parseLine(context, null, companionLine, source);
		companionRace = context.getReferenceContext().silentlyGetConstructedCDOMObject(Race.class, "TestCompanion");

		final String zombieLine = "Zombie	MONSTERCLASS:TestMonsterClass:1	CR:1/2	ROLE:Combat";
		raceLoader.parseLine(context, null, zombieLine, source);
		zombieRace = context.getReferenceContext().silentlyGetConstructedCDOMObject(Race.class, "Zombie");

		final String direRatLine = "Dire Rat	MONSTERCLASS:TestMonsterClass:1	CR:1/3	ROLE:Combat";
		raceLoader.parseLine(context, null, direRatLine, source);
		direRatRace = context.getReferenceContext().silentlyGetConstructedCDOMObject(Race.class, "Dire rat");

		final String miteLine = "Mite	MONSTERCLASS:TestMonsterClass:1	CR:1/4	ROLE:Combat";
		raceLoader.parseLine(context, null, miteLine, source);
		miteRace = context.getReferenceContext().silentlyGetConstructedCDOMObject(Race.class, "Mite");

		final String beetleLine = "Beetle	MONSTERCLASS:TestMonsterClass:1	CR:1/6	ROLE:Combat";
		raceLoader.parseLine(context, null, beetleLine, source);
		beetleRace = context.getReferenceContext().silentlyGetConstructedCDOMObject(Race.class, "Beetle");

		final String centipedeLine = "Centipede	MONSTERCLASS:TestMonsterClass:1	CR:1/8	Centipede";
		raceLoader.parseLine(context, null, centipedeLine, source);
		centipedeRace = context.getReferenceContext().silentlyGetConstructedCDOMObject(Race.class, "Centipede");
		
		final String pcClassLine = "CLASS:TestPCClass	TYPE:PC		ROLE:Combat";
		pcClass = classLoader.parseLine(context, null, pcClassLine, source);
		
		final String pcClassLine2 = "CLASS:TestPCClass2	TYPE:PC		ROLE:Druid";
		pcClass2 = classLoader.parseLine(context, null, pcClassLine2, source);
		
		final String npcClassLine = "CLASS:TestNPCClass	TYPE:NPC";
		npcClass = classLoader.parseLine(context, null, npcClassLine, source);

		final String npcClassLine2 = "CLASS:TestNPCClass2	TYPE:NPC";
		npcClass2 = classLoader.parseLine(context, null, npcClassLine2, source);

		final String monsterClassLine = "CLASS:TestMonsterClass	HD:8	CLASSTYPE:Monster";
		classLoader.parseLine(context, null, monsterClassLine, source);

		final String companionClassLine = "CLASS:TestCompanionClass	HD:8	CLASSTYPE:Companion";
		companionClass = classLoader.parseLine(context, null, companionClassLine, source);

		finishLoad();
	}

	/**
	 * Test PC class level 1 => CR 1/2
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testPCClassLevel1() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(standardRace);
		pc.incrementClassLevel(1, pcClass);
		assertEquals(SettingsHandler.getGameAsProperty().get().getCRInteger("1/2"), pc.getDisplay().calcCR(), 0.01);
	}

	/**
	 * Test PC class level 2 => CR 1/4
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testPCClassLevel2() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(standardRace);
		pc.incrementClassLevel(2, pcClass);
		assertEquals(SettingsHandler.getGameAsProperty().get().getCRInteger("1"), pc.getDisplay().calcCR(), 0.01);
	}

	/**
	 * Test NPC class level 1 => CR 1/3
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testNPCClassLevel1() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(standardRace);
		pc.incrementClassLevel(1, npcClass);
		assertEquals(SettingsHandler.getGameAsProperty().get().getCRInteger("1/3"), pc.getDisplay().calcCR(), 0.011);
	}

	/**
	 * Test NPC class level 2 => CR 1/2
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testNPCClassLevel2() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(standardRace);
		pc.incrementClassLevel(2, npcClass);
		assertEquals(SettingsHandler.getGameAsProperty().get().getCRInteger("1/2"), pc.getDisplay().calcCR(), 0.01);
	}

	/**
	 * Test NPC class level 3 => CR 1
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testNPCClassLevel3() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(standardRace);
		pc.incrementClassLevel(3, npcClass);
		assertEquals(SettingsHandler.getGameAsProperty().get().getCRInteger("1"), pc.getDisplay().calcCR(), 0.01);
	}
	
	
	/**
	 * Test PC class multiclass level 4/4 => CR 7
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testMultiClassPCLevel4PCLevel4() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(standardRace);
		pc.incrementClassLevel(4, pcClass);
		pc.incrementClassLevel(4, pcClass2);
		assertEquals(SettingsHandler.getGameAsProperty().get().getCRInteger("7"), pc.getDisplay().calcCR(), 0.01);
	}
	
	/**
	 * Test NPC class multiclass level 4/4 => CR 6
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testMultiClassNPCLevel4NPCLevel4() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(standardRace);
		pc.incrementClassLevel(4, npcClass);
		pc.incrementClassLevel(4, npcClass2);
		assertEquals(SettingsHandler.getGameAsProperty().get().getCRInteger("6"), pc.getDisplay().calcCR(), 0.01);
	}

	/**
	 * Test NPC/PC class multiclass level 4/4 => CR 7
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testMultiClassNPCLevel4PCLevel4() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(standardRace);
		pc.incrementClassLevel(4, npcClass);
		pc.incrementClassLevel(4, pcClass);
		assertEquals(SettingsHandler.getGameAsProperty().get().getCRInteger("7"), pc.getDisplay().calcCR(), 0.01);
	}
	

	/**
	 * Test NPC class level 1, kobold (CRMOD:NPC|-3) => CR 1/4
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testNPCClassKoboldLevel1() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(koboldRace);
		pc.incrementClassLevel(1, npcClass);
		assertEquals(SettingsHandler.getGameAsProperty().get().getCRInteger("1/4"), pc.getDisplay().calcCR(), 0.01);
	}

	/**
	 * Test NPC class level 2, kobold (CRMOD:NPC|-3) => CR 1/3
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testNPCClassKoboldLevel2() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(koboldRace);
		pc.incrementClassLevel(2, npcClass);
		assertEquals(SettingsHandler.getGameAsProperty().get().getCRInteger("1/3"), pc.getDisplay().calcCR(), 0.011);
	}

	/**
	 * Test NPC class level 3, kobold (CRMOD:NPC|-3) => CR 1/2
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testNPCClassKoboldLevel3() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(koboldRace);
		pc.incrementClassLevel(3, npcClass);
		assertEquals(SettingsHandler.getGameAsProperty().get().getCRInteger("1/2"), pc.getDisplay().calcCR(), 0.01);
	}

	/**
	 * Test NPC class level 4, kobold (CRMOD:NPC|-3) => CR 1
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testNPCClassKoboldLevel4() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(koboldRace);
		pc.incrementClassLevel(4, npcClass);
		assertEquals(SettingsHandler.getGameAsProperty().get().getCRInteger("1"), pc.getDisplay().calcCR(), 0.01);
	}

	/**
	 * Test PC class level 4, kobold (CRMOD:NPC|-3) => CR 3
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testPCClassKoboldLevel4() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(koboldRace);
		pc.incrementClassLevel(4, pcClass);
		assertEquals(SettingsHandler.getGameAsProperty().get().getCRInteger("3"), pc.getDisplay().calcCR(), 0.01);
	}


	/**
	 * Test PC class level 4, drow noble (CRMOD:PC.NPC|0) => CR 4
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testPCClassDrowNobleLevel4() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(drowNobleRace);
		pc.incrementClassLevel(4, pcClass);
		assertEquals(SettingsHandler.getGameAsProperty().get().getCRInteger("4"), pc.getDisplay().calcCR(), 0.01);
	}

	/**
	 * Test NPC class level 4, drow noble (CRMOD:PC.NPC|0) => CR 4
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testNPCClassDrowNobleLevel4() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(drowNobleRace);
		pc.incrementClassLevel(4, npcClass);
		assertEquals(SettingsHandler.getGameAsProperty().get().getCRInteger("4"), pc.getDisplay().calcCR(), 0.01);
	}

	/**
	 * Test PC class key level 4 babau => CR 10
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testPCClassBabauKeyLevel4() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(babauRace);
		pc.incrementClassLevel(4, pcClass);
		assertEquals(SettingsHandler.getGameAsProperty().get().getCRInteger("10"), pc.getDisplay().calcCR(), 0.01);
	}

	/**
	 * Test PC class key level 4 babau => CR 8
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testPCClassBabauNonKeyLevel4() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(babauRace);
		pc.incrementClassLevel(4, pcClass2);
		assertEquals(SettingsHandler.getGameAsProperty().get().getCRInteger("8"), pc.getDisplay().calcCR(), 0.01);
	}
	/**
	 * Test PC class key level 10 babau => CR 13
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testPCClassBabauNonKeyLevel10() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(babauRace);
		pc.incrementClassLevel(10, pcClass2);
		assertEquals(SettingsHandler.getGameAsProperty().get().getCRInteger("13"), pc.getDisplay().calcCR(), 0.01);
	}

	/**
	 * Test PC class key level 8 dryad => CR 15
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testPCClassDyradKeyLevel8() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(dryadRace);
		pc.incrementClassLevel(8, pcClass2);
		assertEquals(SettingsHandler.getGameAsProperty().get().getCRInteger("15"), pc.getDisplay().calcCR(), 0.01);
	}
	/**
	 * Test PC class non key level 8 dryad => CR 11
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testPCClassDryadNonKeyLevel8() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(dryadRace);
		pc.incrementClassLevel(8, pcClass);
		assertEquals(SettingsHandler.getGameAsProperty().get().getCRInteger("11"), pc.getDisplay().calcCR(), 0.01);
	}
	
	/**
	 * Test Companion => CR 0
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testCompanion() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(companionRace);
		pc.incrementClassLevel(4, companionClass);
		assertNull(pc.getDisplay().calcCR());
	}

	/**
	 * Test zombie => CR 1/2
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testZombie() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(zombieRace);
		assertEquals(SettingsHandler.getGameAsProperty().get().getCRInteger("1/2"), pc.getDisplay().calcCR(), 0.01);
	}
	/**
	 * Test dire rat => CR 1/3
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testDireRat() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(direRatRace);
		assertEquals(SettingsHandler.getGameAsProperty().get().getCRInteger("1/3"), pc.getDisplay().calcCR(), 0.1);
	}
	/**
	 * Test mite => CR 1/4
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testMite() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(miteRace);
		assertEquals(SettingsHandler.getGameAsProperty().get().getCRInteger("1/4"), pc.getDisplay().calcCR(), 0.011);
	}
	/**
	 * Test beetle => CR 1/6
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testBeetle() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(beetleRace);
		assertEquals(SettingsHandler.getGameAsProperty().get().getCRInteger("1/6"), pc.getDisplay().calcCR(), 0.1);
	}
	/**
	 * Test centipede => CR 1/8
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testCentipede() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setRace(centipedeRace);
		assertEquals(SettingsHandler.getGameAsProperty().get().getCRInteger("1/8"), pc.getDisplay().calcCR(), 0.1);
	}

	@Override
	protected void defaultSetupEnd()
	{
		//Nothing, we will trigger ourselves
	}
}
