/*
 * PCClassTest.java
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
 * Created on 13-Jan-2004
 *
 * Current Ver: $Revision: 1.10 $
 *
 * Last Editor: $Author: soulcatcher $
 *
 * Last Edited: $Date: 2006/02/14 02:46:17 $
 *
 */
package pcgen.core;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.PCGenTestCase;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.PCClassLoader;
import plugin.pretokens.parser.PreVariableParser;

/**
 * @author wardc
 */
public class PCClassTest extends AbstractCharacterTestCase {
	
	PCClass humanoidClass;
	SizeAdjustment sizeL;
	Race bugbearRace;
	Race bigBugbearRace;
	PCClass nymphClass;
	Race nymphRace;
	Prerequisite prereq;
	RuleCheck classPreRule;
	PCClass prClass;
	PCClass qClass;
	PCClass nqClass;
	
	/**
	 * Constructs a new <code>PCClassTest</code>.
	 *
	 * @see PCGenTestCase#PCGenTestCase()
	 */
	public PCClassTest()
	{
		// Do Nothing
	}

	/**
	 * Constructs a new <code>PCClassTest</code> with the given <var>name</var>.
	 *
	 * @param name the test case name
	 *
	 * @see PCGenTestCase#PCGenTestCase(String)
	 */
	public PCClassTest(final String name)
	{
		super(name);
	}

	public static void main(final String[] args)
	{
		junit.textui.TestRunner.run(PCClassTest.class);
	}

	public static Test suite()
	{
		// quick method, adds all methods beginning with "test"
		return new TestSuite(PCClassTest.class);
	}

	public void testFireNameChangedVariable()
	{
		final PCClass myClass = new PCClass();
		myClass.setName("myClass");

		myClass.addVariable(2, "someVar", "(CL=myClass/2) + CL=myClass");

		Variable var = myClass.getVariable(0);
		assertEquals("someVar", var.getName());
		assertEquals(2, var.getLevel());
		assertEquals("(CL=myClass/2) + CL=myClass", var.getValue());

		myClass.fireNameChanged("myClass", "someOtherClass");
		var = myClass.getVariable(0);
		assertEquals("someVar", var.getName());
		assertEquals(2, var.getLevel());
		assertEquals("(CL=someOtherClass/2) + CL=someOtherClass", var.getValue());

	}
	
	public void testMonsterSkillPoints()
	{
		SettingsHandler.setMonsterDefault(false);
		// Create a medium bugbear first level
		PlayerCharacter bugbear = new PlayerCharacter();
		bugbear.setRace(bugbearRace);
		setPCStat(bugbear, "INT", 12);
		
		// Test skills granted for each level
		bugbear.incrementClassLevel(1, humanoidClass);
		PCLevelInfo levelInfo = (PCLevelInfo) bugbear.getLevelInfo().get(0);
		assertEquals("First level of bugbear", 7, levelInfo.getSkillPointsGained());

		bugbear.incrementClassLevel(1, humanoidClass);
		levelInfo = (PCLevelInfo) bugbear.getLevelInfo().get(1);
		assertEquals("2nd level of bugbear", 1, levelInfo.getSkillPointsGained());

		bugbear.incrementClassLevel(1, humanoidClass);
		levelInfo = (PCLevelInfo) bugbear.getLevelInfo().get(2);
		assertEquals("3rd level of bugbear", 1, levelInfo.getSkillPointsGained());

		// Craete a huge bugbear first level
		bugbear = new PlayerCharacter();
		bugbear.setRace(bigBugbearRace);
		assertEquals("big bugbear", "L", bugbear.getSize());
		setPCStat(bugbear, "INT", 10);
		bugbear.incrementClassLevel(1, humanoidClass);
		// Test skills granted for each level
		levelInfo = (PCLevelInfo) bugbear.getLevelInfo().get(0);
		assertEquals("First level of big bugbear", 6, levelInfo.getSkillPointsGained());

		bugbear.incrementClassLevel(1, humanoidClass);
		levelInfo = (PCLevelInfo) bugbear.getLevelInfo().get(1);
		assertEquals("2nd level of big bugbear", 0, levelInfo.getSkillPointsGained());

		bugbear.incrementClassLevel(1, humanoidClass);
		levelInfo = (PCLevelInfo) bugbear.getLevelInfo().get(2);
		assertEquals("3rd level of big bugbear", 1, levelInfo.getSkillPointsGained());
		
		// Create a nymph - first level
		PlayerCharacter nymph = new PlayerCharacter();
		nymph.setRace(nymphRace);
		assertEquals("nymph", "M", nymph.getSize());
		setPCStat(nymph, "INT", 10);
		nymph.incrementClassLevel(1, nymphClass);
		// Test skills granted for each level
		levelInfo = (PCLevelInfo) nymph.getLevelInfo().get(0);
		assertEquals("First level of nymph", 24, levelInfo.getSkillPointsGained());

		nymph.incrementClassLevel(1, nymphClass);
		levelInfo = (PCLevelInfo) nymph.getLevelInfo().get(1);
		assertEquals("2nd level of nymph", 6, levelInfo.getSkillPointsGained());
		
	}
	
	/**
	 * Test the interaction of prerequisites on PCClasses and bonuses and the 
	 * Bypass Class Prereqs flag.
	 * @throws Exception
	 */
	public void testBypassClassPrereqs() throws Exception
	{
		// Setup class with prereqs and var based abilities with prereqs.
		final PreVariableParser parser = new PreVariableParser();
		final Prerequisite aPrereq = parser.parse("VARGTEQ", "Foo,1", false,
			false);
		final RuleCheck aClassPreRule = new RuleCheck();
		aClassPreRule.setName("CLASSPRE");
		aClassPreRule.setDefault("N");
		final GameMode gameMode = SettingsHandler.getGame();
		gameMode.addRule(aClassPreRule);

		final PCClass aPrClass = new PCClass();
		aPrClass.setName("PreReqClass");
		aPrClass.setAbbrev("PCl");
		aPrClass.addBonusList("0|MISC|SR|10|PREVARGTEQ:Foo,2");
		aPrClass.addPreReq(aPrereq);
		final PCClass aQClass = new PCClass();
		aQClass.setName("QualClass");
		aQClass.setAbbrev("QC1");
		aQClass.setQualifyString("PreReqClass|PreReqVar");

		final PCClass aNqClass = new PCClass();
		aNqClass.setName("NonQualClass");
		aNqClass.setAbbrev("NQC");
		aNqClass.addVariable(0, "Foo", "1");
		aNqClass.addVariable(2, "Foo", "2");

		// Setup character without prereqs
		final PlayerCharacter character = getCharacter();

		// Test no prereqs and no bypass fails class and var
		assertFalse("PC with no prereqs should fail class qual test.", aPrClass
			.isQualified(character));
		assertEquals("PC with no prereqs should fail var qual test.", 0.0,
			aPrClass.getBonusTo("MISC", "SR", 1, character), 0.1);

		// Test no prereqs and bypass passes class and fails var
		aClassPreRule.setDefault("Y");
		assertTrue(
			"PC with no prereqs should pass class qual test when bypassing prereqs is on.",
			aPrClass.isQualified(character));
		assertEquals(
			"PC with no prereqs should fail var qual test when bypass prereqs is on.",
			0.0, aPrClass.getBonusTo("MISC", "SR", 1, character), 0.1);

		// Test prereqs and bypass pass class and var
		character.incrementClassLevel(1, aNqClass);
		assertTrue("PC with prereqs and bypass should pass class qual test.",
			aPrClass.isQualified(character));
		character.incrementClassLevel(1, aNqClass);
		assertEquals("PC with prereqs and bypass should pass var qual test.",
			10.0, aPrClass.getBonusTo("MISC", "SR", 1, character), 0.1);

		// Test prereqs and no bypass passes class and var
		aClassPreRule.setDefault("N");
		assertTrue(
			"PC with prereqs and no bypass should pass class qual test.",
			aPrClass.isQualified(character));
		assertEquals(
			"PC with prereqs and no bypass should pass var qual test.", 10.0,
			aPrClass.getBonusTo("MISC", "SR", 1, character), 0.1);

	}

	/**
	 * Test the interaction of prerequisites on PCClasses and bonuses and the 
	 * Qualifies functionality associated with a class.
	 * @throws Exception
	 */
	public void testQualifies() throws Exception
	{
		// Setup character without prereqs
		final PlayerCharacter character = getCharacter();

		// Test no prereqs and no qualifies fails class and var
		assertFalse("PC with no prereqs should fail class qual test.", prClass
			.isQualified(character));
		assertEquals("PC with no prereqs should fail var qual test.", 0.0,
			prClass.getBonusTo("MISC", "SR", 1, character), 0.1);

		// Test no prereqs and qualifies passes class and fails var
		character.incrementClassLevel(1, qClass);
		assertTrue(
			"PC with no prereqs but a qualifies should pass class qual test.",
			prClass.isQualified(character));
		assertEquals(
			"PC with no prereqs but a qualifies should fail var qual test.",
			0.0, prClass.getBonusTo("MISC", "SR", 1, character), 0.1);

		// Test prereqs and qualifies pass class and var
		character.incrementClassLevel(1, nqClass);
		assertTrue(
			"PC with prereqs and qualifies should pass class qual test.",
			prClass.isQualified(character));
		character.incrementClassLevel(1, nqClass);
		assertEquals(
			"PC with prereqs and qualifies should pass var qual test.", 10.0,
			prClass.getBonusTo("MISC", "SR", 1, character), 0.1);
	} 

	/**
	 * Test the processing of getPCCText to ensure that it correctly produces
	 * an LST representation of an object and that the LST can then be reloaded
	 * to recrete the object.
	 *
	 * @throws PersistenceLayerException
	 */
	public void testGetPCCText() throws PersistenceLayerException
	{
		// Test a basic class
		String classPCCText = humanoidClass.getPCCText();
		assertNotNull("PCC Text for race should not be null", classPCCText);

		CampaignSourceEntry source = new CampaignSourceEntry(new Campaign(),
				getClass().getName() + ".java");
		PCClass reconstClass = null;
		System.out.println("Got text:" + classPCCText);
		reconstClass = parsePCClassText(classPCCText, source);
		assertEquals(
			"getPCCText should be the same after being encoded and reloaded",
			classPCCText, reconstClass.getPCCText());
		assertEquals("Class abbrev was not restored after saving and reloading.",
			humanoidClass.getAbbrev(), reconstClass.getAbbrev());
		
		// Test a class with some innate spells
		String b = "1" + "\t" + "SPELLS:" + "Humanoid|TIMES=1|CASTERLEVEL=var(\"TCL\")|Create undead,11+WIS";
		PCClassLoader classLoader = new PCClassLoader();
		classLoader.parseLine(humanoidClass, b, source);
		classPCCText = humanoidClass.getPCCText();
		assertNotNull("PCC Text for race should not be null", classPCCText);

		reconstClass = null;
		System.out.println("Got text:" + classPCCText);
		reconstClass = parsePCClassText(classPCCText, source);
		assertEquals(
			"getPCCText should be the same after being encoded and reloaded",
			classPCCText, reconstClass.getPCCText());
		assertEquals("Class abbrev was not restored after saving and reloading.",
			humanoidClass.getAbbrev(), reconstClass.getAbbrev());
		final List startSpells = humanoidClass.getSpellSupport().getSpellList(-1);
		final List reconstSpells = reconstClass.getSpellSupport().getSpellList(-1);
		assertEquals("All spell should have been reconstituted.",
			startSpells.size(), reconstSpells.size());
		assertEquals("Spell names should been preserved.",
			((PCSpell)startSpells.get(0)).getName(), ((PCSpell)reconstSpells.get(0)).getName());

	}

	/**
	 * Parse a class definition and return the populated PCClass object.
	 * 
	 * @param classPCCText The textual definition of the class.
	 * @param source The source that the class is from.
	 * @return The populated class.
	 * @throws PersistenceLayerException
	 */
	private PCClass parsePCClassText(String classPCCText, CampaignSourceEntry source) throws PersistenceLayerException
	{
		PCClassLoader pcClassLoader = new PCClassLoader();
		pcClassLoader.setCurrentSource(source);
		PCClass reconstClass = null;
		StringTokenizer tok = new StringTokenizer(classPCCText, "\n");
		while (tok.hasMoreTokens())
		{
			String line = tok.nextToken();
			if (line.trim().length() > 0)
			{
				System.out.println("Processing line:'" + line + "'.");
				reconstClass = (PCClass) pcClassLoader.parseLine(reconstClass, line, source);
			}
		}
		return reconstClass;
	}
	
	protected void setUp() throws Exception
	{
		super.setUp();

		Campaign customCampaign = new Campaign();
		customCampaign.setName("Unit Test");
		customCampaign.setDescription("Unit Test data");
		CampaignSourceEntry unitTestSource = new CampaignSourceEntry(
			customCampaign, "PCClassTest");

		// Create the monseter class type
		SettingsHandler.getGame().addClassType("Monster		CRFORMULA:0			ISMONSTER:YES	XPPENALTY:NO");
		SettingsHandler.getGame().setSkillMultiplierLevels("4");
		
		
		// Create the humanoid class
		String classDef = "CLASS:Humanoid	HD:8		TYPE:Monster	STARTSKILLPTS:1	"
			+ "MODTOSKILLS:NO	MONSKILL:6+INT	MONNONSKILLHD:1|PRESIZELTEQ:M	"
			+ "MONNONSKILLHD:2|PRESIZEEQ:L";
		PCClassLoader classLoader = new PCClassLoader();
		humanoidClass = (PCClass) classLoader.parseLine(null, classDef, unitTestSource);
		Globals.getClassList().add(humanoidClass);

		classDef = "CLASS:Nymph		TYPE:Monster	HD:6	STARTSKILLPTS:6	MODTOSKILLS:YES	";
		classLoader = new PCClassLoader();
		nymphClass = (PCClass) classLoader.parseLine(null, classDef, unitTestSource);
		Globals.getClassList().add(nymphClass);

		// Create the large size mod
		sizeL = new SizeAdjustment();
		sizeL.setName("Large");
		sizeL.setAbbreviation("L");
		sizeL.setIsDefaultSize(false);
		SettingsHandler.getGame().addToSizeAdjustmentList(sizeL);
		
		// Create the BugBear race
		bugbearRace = new Race();
		bugbearRace.setName("Bugbear");
		bugbearRace.setSize("M");
		bugbearRace.setAdvancementUnlimited(true);
		bugbearRace.setInitialSkillMultiplier(1);
		bigBugbearRace = new Race();
		bigBugbearRace.setName("BigBugbear");
		bigBugbearRace.setSize("L");
		bigBugbearRace.setAdvancementUnlimited(true);
		bigBugbearRace.setInitialSkillMultiplier(1);
		
		// Create the Nymph race
		nymphRace = new Race();
		nymphRace.setName("Nymph");
		nymphRace.setSize("M");
		nymphRace.setAdvancementUnlimited(true);
		
		final Map raceMap = Globals.getRaceMap();
		raceMap.put("Bugbear", bugbearRace);
		raceMap.put("BigBugbear", bigBugbearRace);
		raceMap.put("Nymph", nymphRace);
		
		// Setup class with prereqs and var based abilities with prereqs.
		PreVariableParser parser = new PreVariableParser();
		prereq = parser.parse("VARGTEQ", "Foo,1", false,
			false);
		classPreRule = new RuleCheck();
		classPreRule.setName("CLASSPRE");
		classPreRule.setDefault("N");
		GameMode gameMode = SettingsHandler.getGame();
		gameMode.addRule(classPreRule);

		prClass = new PCClass();
		prClass.setName("PreReqClass");
		prClass.setAbbrev("PCl");
		prClass.addBonusList("0|MISC|SR|10|PREVARGTEQ:Foo,2");
		prClass.addPreReq(prereq);
		qClass = new PCClass();
		qClass.setName("QualClass");
		qClass.setAbbrev("QC1");
		qClass.setQualifyString("PreReqClass|PreReqVar");
		nqClass = new PCClass();
		nqClass.setName("NonQualClass");
		nqClass.setAbbrev("NQC");
		nqClass.addVariable(0, "Foo", "1");
		nqClass.addVariable(2, "Foo", "2");
		
	}
	
	protected void tearDown() throws Exception
	{
		
		// TODO Auto-generated method stub
		super.tearDown();
	}
}
