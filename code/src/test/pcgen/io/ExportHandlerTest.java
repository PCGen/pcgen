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
package pcgen.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Equipment;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.LevelInfo;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.EquipSet;
import pcgen.rules.context.LoadContext;
import pcgen.util.TestHelper;
import plugin.lsttokens.testsupport.BuildUtilities;

/**
 * <code>SkillTokenTest</code> contains tests to verify that the
 * SKILL token and its subtokens are working correctly.
 */

@SuppressWarnings("nls")
public class ExportHandlerTest extends AbstractCharacterTestCase
{
	private Skill balance = null;
	private Skill[] knowledge = null;
	private Skill tumble = null;
	private Equipment weapon = null;
	private Equipment gem = null;
	private Equipment armor = null;

	/**
	 * Quick test suite creation - adds all methods beginning with "test"
	 * @return The Test suite
	 */
	public static Test suite()
	{
		return new TestSuite(ExportHandlerTest.class);
	}

	/**
	 * Basic constructor, name only.
	 * @param name The name of the test class.
	 */
	public ExportHandlerTest(String name)
	{
		super(name);
	}

	/**
	 * @see pcgen.AbstractCharacterTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		PlayerCharacter character = getCharacter();
		LoadContext context = Globals.getContext();

		final LevelInfo levelInfo = new LevelInfo();
		levelInfo.setLevelString("LEVEL");
		levelInfo.setMaxClassSkillString("LEVEL+3");
		levelInfo.setMaxCrossClassSkillString("(LEVEL+3)/2");
		GameMode gamemode = SettingsHandler.getGame();
		gamemode.addLevelInfo("Default", levelInfo);

		//Stats
		setPCStat(character, dex, 16);
		setPCStat(character, intel, 17);
		BonusObj aBonus = Bonus.newBonus(context, "MODSKILLPOINTS|NUMBER|INT");
		
		if (aBonus != null)
		{
			intel.addToListFor(ListKey.BONUS, aBonus);
		}

		// Race
		Race testRace = new Race();
		testRace.setName("TestRace");
		character.setRace(testRace);

		// Class
		PCClass myClass = new PCClass();
		myClass.setName("My Class");
		myClass.put(FormulaKey.START_SKILL_POINTS, FormulaFactory.getFormulaFor(3));
		character.incrementClassLevel(5, myClass, true);

		// Skills
		knowledge = new Skill[2];
		knowledge[0] = new Skill();
		context.unconditionallyProcess(knowledge[0], "CLASSES", "MyClass");
		knowledge[0].setName("KNOWLEDGE (ARCANA)");
		TestHelper.addType(knowledge[0], "KNOWLEDGE.INT");
		CDOMDirectSingleRef<PCStat> intelRef = CDOMDirectSingleRef.getRef(intel);
		knowledge[0].put(ObjectKey.KEY_STAT, intelRef);
		character.setSkillOrder(knowledge[0], 2);
		Globals.getContext().getReferenceContext().importObject(knowledge[0]);
		SkillRankControl.modRanks(8.0, myClass, true, character, knowledge[0]);

		knowledge[1] = new Skill();
		context.unconditionallyProcess(knowledge[1], "CLASSES", "MyClass");
		knowledge[1].setName("KNOWLEDGE (RELIGION)");
		TestHelper.addType(knowledge[1], "KNOWLEDGE.INT");
		knowledge[1].put(ObjectKey.KEY_STAT, intelRef);
		character.setSkillOrder(knowledge[1], 3);
		Globals.getContext().getReferenceContext().importObject(knowledge[1]);
		SkillRankControl.modRanks(5.0, myClass, true, character, knowledge[1]);

		tumble = new Skill();
		context.unconditionallyProcess(tumble, "CLASSES", "MyClass");
		tumble.setName("Tumble");
		tumble.addToListFor(ListKey.TYPE, Type.getConstant("DEX"));
		CDOMDirectSingleRef<PCStat> dexRef = CDOMDirectSingleRef.getRef(dex);
		tumble.put(ObjectKey.KEY_STAT, dexRef);
		character.setSkillOrder(tumble, 4);
		Globals.getContext().getReferenceContext().importObject(tumble);
		SkillRankControl.modRanks(7.0, myClass, true, character, tumble);

		balance = new Skill();
		context.unconditionallyProcess(balance, "CLASSES", "MyClass");
		balance.setName("Balance");
		balance.addToListFor(ListKey.TYPE, Type.getConstant("DEX"));
		balance.put(ObjectKey.KEY_STAT, dexRef);
		character.setSkillOrder(balance, 1);
		aBonus = Bonus.newBonus(context, "SKILL|Balance|2|PRESKILL:1,Tumble=5|TYPE=Synergy.STACK");
		
		if (aBonus != null)
		{
			balance.addToListFor(ListKey.BONUS, aBonus);
		}
		Globals.getContext().getReferenceContext().importObject(balance);
		SkillRankControl.modRanks(4.0, myClass, true, character, balance);

		character.calcActiveBonuses();

		weapon = new Equipment();
		weapon.setName("TestWpn");
		weapon.addToListFor(ListKey.TYPE, Type.WEAPON);

		gem = new Equipment();
		gem.setName("TestGem");
		gem.addToListFor(ListKey.TYPE, Type.getConstant("gem"));
		gem.setQty(1);
		
		armor = new Equipment();
		armor.setName("TestArmorSuit");
		TestHelper.addType(armor, "armor.suit");

		context.getReferenceContext().buildDerivedObjects();
		context.getReferenceContext().resolveReferences(null);
	}

	/**
	 * @see pcgen.AbstractCharacterTestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		knowledge = null;
		balance = null;
		tumble = null;
		intel.removeListFor(ListKey.BONUS);

		super.tearDown();
	}

	/**
	 * Test the behaviour of the weapon loop
	 * @throws IOException
	 */
	public void testWpnLoop() throws IOException
	{
		PlayerCharacter character = getCharacter();

		// Test each token for old and new syntax processing.

		assertEquals("New format SKILL Token", "****", evaluateToken(
			"FOR.0,100,1,**\\WEAPON.%.NAME\\**,NONE,NONE,1", character));
		// Now assign a weapon
		character.addEquipment(weapon);
		EquipSet es = new EquipSet("1", "Default", "", weapon);
		character.addEquipSet(es);
		assertEquals("New format SKILL Token", "**TestWpn**", evaluateToken(
			"FOR.0,100,1,**\\WEAPON.%.NAME\\**,NONE,NONE,1", character));
	}

	/**
	 * Test the export of equipment using the eqtype token in a loop.
	 * 
	 * @throws Exception
	 */
	public void testEqtypeLoop() throws Exception
	{
		PlayerCharacter character = getCharacter();
		final String gemLoop =
				"FOR.0,COUNT[EQTYPE.Gem],1,\\EQTYPE.Gem.%.NAME\\: \\EQTYPE.Gem.%.QTY\\, ,<br/>,1";

		assertEquals("Gem Loop - no gems", "",
			evaluateToken(gemLoop, character));

		// Now assign a gem
		character.addEquipment(gem);
		EquipSet es = new EquipSet("1", "Default", "", gem);
		character.addEquipSet(es);
		character.setDirty(true);
		assertEquals("Gem loop - 1 gem", " TestGem: 1<br/>", evaluateToken(
			gemLoop, character));
	}

	public void testJepIif() throws IOException
	{
		PlayerCharacter character = getCharacter();
		assertEquals("Basic JEP boolean", new Float(1.0), character
			.getVariableValue("max(0,2)==2", ""));
		assertEquals("JEP boolean in IF", "true", evaluateToken(
			"OIF(max(0,2)==2,true,false)", character));
//		assertEquals("JEP boolean in IF", "true", evaluateToken(
//			"|OIF(max(0,2)==2)|\ntrue\n|ELSE|\nfalse\n|ENDIF|", character));
	}
	
	public void testFor() throws IOException
	{
		PlayerCharacter pc = getCharacter();
		Ability dummyFeat1 = new Ability();
		dummyFeat1.setName("1");
		dummyFeat1.setCDOMCategory(BuildUtilities.getFeatCat());
		
		Ability dummyFeat2 = new Ability();
		dummyFeat2.setName("2");
		dummyFeat2.setCDOMCategory(BuildUtilities.getFeatCat());
		
		Ability dummyFeat3 = new Ability();
		dummyFeat3.setName("3");
		dummyFeat3.setCDOMCategory(BuildUtilities.getFeatCat());
		
		Ability dummyFeat4 = new Ability();
		dummyFeat4.setName("4");
		dummyFeat4.setCDOMCategory(BuildUtilities.getFeatCat());
		
		Ability dummyFeat5 = new Ability();
		dummyFeat5.setName("5");
		dummyFeat5.setCDOMCategory(BuildUtilities.getFeatCat());
		
		Ability dummyFeat6 = new Ability();
		dummyFeat6.setName("6");
		dummyFeat6.setCDOMCategory(BuildUtilities.getFeatCat());
		
		Ability dummyFeat7 = new Ability();
		dummyFeat7.setName("7");
		dummyFeat7.setCDOMCategory(BuildUtilities.getFeatCat());	
		
		addAbility(BuildUtilities.getFeatCat(), dummyFeat1);
		addAbility(BuildUtilities.getFeatCat(), dummyFeat2);
		addAbility(BuildUtilities.getFeatCat(), dummyFeat3);
		addAbility(BuildUtilities.getFeatCat(), dummyFeat4);
		addAbility(BuildUtilities.getFeatCat(), dummyFeat5);
		addAbility(BuildUtilities.getFeatCat(), dummyFeat6);
		addAbility(BuildUtilities.getFeatCat(), dummyFeat7);
		
		assertEquals("Test for evaluates correctly", "----------------",
			evaluateToken(
				"FOR.1,((24-STRLEN[SKILL.0])),24,-,NONE,NONE,1", pc));
		assertEquals("Test for evaluates correctly", "                ",
			evaluateToken(
				"FOR.1,((24-STRLEN[SKILL.0])),24, ,NONE,NONE,1", pc));
		
		String tok = "DFOR." +
		"0" +
		",${((count(\"ABILITIES\";\"CATEGORY=FEAT\")+1)/2)}" +
		",1" +
		",${(count(\"ABILITIES\";\"CATEGORY=FEAT\")+1)}" +
		",${((count(\"ABILITIES\";\"CATEGORY=FEAT\")+1)/2)}" +
		", \\FEAT.%.NAME\\ " +
		",[" +
		",]" +
		",0";
		
		
		//Logging.errorPrint( "DFOR Test: " + evaluateToken(tok, pc));
		
		
		// Test DFOR with alternate syntax for jep passthrough.  ie, anything 
		// surrounded by ${x} will tbe sent straight to be processed.  We
		// will assume that x is a well formed type of value.  This was to get around 
		// the problems with DFOR not taking ((count("ABILITIES";"CATEGORY=FEAT")+1)
		// since it could not figure out how to parse it to send to the right place.
		assertEquals("Test for DFOR ","[ 1  5 ][ 2  6 ][ 3  7 ][ 4   ]", 
				evaluateToken(tok, pc)	);
					
	}
	
	public void testForNoMoreItems() throws IOException
	{
		PlayerCharacter pc = getCharacter();
		assertEquals("Test for evaluates correctly", "SF",
			evaluateToken(
				"FOR.0,100,1,\\ARMOR.SUIT.ALL.%.NAME\\,S,F,1", pc));

		// Now assign a gem
		pc.addEquipment(armor);
		EquipSet es = new EquipSet("1", "Default", "", armor);
		pc.addEquipSet(es);
		assertEquals("Test for evaluates correctly", "STestArmorSuitFSF",
			evaluateToken(
				"FOR.0,100,1,\\ARMOR.SUIT.ALL.%.NAME\\,S,F,1", pc));
		
	}
	
	public void testExpressionOutput() throws IOException
	{
		LoadContext context = Globals.getContext();
		Ability dummyFeat = new Ability();
		dummyFeat.setName("DummyFeat");
		dummyFeat.setCDOMCategory(BuildUtilities.getFeatCat());
		final PlayerCharacter pc = getCharacter();

		// Create a variable
		dummyFeat.put(VariableKey.getConstant("NegLevels"), FormulaFactory
				.getFormulaFor(0));

		// Create a bonus to it
		Ability dummyFeat2 = new Ability();
		dummyFeat2.setName("DummyFeat2");
		dummyFeat2.setCDOMCategory(BuildUtilities.getFeatCat());
		final BonusObj aBonus = Bonus.newBonus(context, "VAR|NegLevels|7");
		
		if (aBonus != null)
		{
			dummyFeat2.addToListFor(ListKey.BONUS, aBonus);
		}
		
		AbilityCategory cat = context.getReferenceContext().constructCDOMObject(
				AbilityCategory.class, "Maneuver");
		AbilityCategory cat2 = context.getReferenceContext().constructCDOMObject(
				AbilityCategory.class, "Maneuver(Special)");
		Ability dummyFeat3 = new Ability();
		dummyFeat3.setName("DummyFeat3");
		dummyFeat3.setCDOMCategory(cat);
		
		Ability dummyFeat4 = new Ability();
		dummyFeat4.setName("DummyFeat4");
		dummyFeat4.setCDOMCategory(cat2);
		
		addAbility(BuildUtilities.getFeatCat(), dummyFeat);
		addAbility(BuildUtilities.getFeatCat(), dummyFeat2);
		addAbility(cat, dummyFeat3);
		addAbility(cat2, dummyFeat4);
		
		assertEquals("Unsigned output", "7", evaluateToken(
			"VAR.NegLevels.INTVAL", pc));
		assertEquals("Signed output", "+7", evaluateToken(
			"VAR.NegLevels.INTVAL.SIGN", pc));
	
		String tok ="";
	
		tok = "count(\"ABILITIES\", \"CATEGORY=Maneuver\")";		
		// if this evaluates math wise, the values should be string "1.0"
		assertFalse("Token: |" + tok + "| != 1.0: ", evaluateToken(tok, pc).equals("1.0"));
		
		tok = "VAR.count(\"ABILITIES\", \"CATEGORY=Maneuver\")";		
		assertTrue("Token: |" + tok + "| == 1.0: ",  evaluateToken(tok, pc).equals("1.0"));
	
		tok ="COUNT[\"ABILITIES\", \"CATEGORY=Maneuver\"]";		
		assertFalse("Token: |" + tok + "| != 1.0: ",  evaluateToken(tok, pc).equals("1.0"));
		
		tok = "count(\"ABILITIES\", \"CATEGORY=Maneuver(Special)\")";
		assertFalse("Token: |" + tok + "| != 1.0 ",  evaluateToken(tok, pc).equals("1.0"));
		
		tok = "${count(\"ABILITIES\", \"CATEGORY=Maneuver(Special)\")+5}";
		assertFalse("Token: |" + tok + "| == 5.0 ",  evaluateToken(tok, pc).equals("5.0"));
		
		tok = "${count(\"ABILITIES\", \"CATEGORY=Maneuver(Special)\")+5}";
		assertTrue("Token: |" + tok + "| != 6.0 ",  evaluateToken(tok, pc).equals("6.0"));
		
		tok = "${(count(\"ABILITIES\", \"CATEGORY=Maneuver(Special)\")+5)/3}";
		assertFalse("Token: |" + tok + "| == 3.0 ",  evaluateToken(tok, pc).equals("3.0"));
		
		tok = "${(count(\"ABILITIES\", \"CATEGORY=Maneuver(Special)\")+5)/3}";
		assertTrue("Token: |" + tok + "| != 2.0 ",  evaluateToken(tok, pc).equals("2.0"));
		
		
	}

	public void testPartyFor() throws IOException
	{
		String outputToken =
				"   <combatants>\n"
					+ "|FOR.0,50,1,\n"
					+ "	<name>\\\\%.NAME\\\\</name>\n"
					+ "	<skills>\\\\%.FOR.0,COUNT[SKILLS],1,\\SKILL.%\\: \\SKILL.%.TOTAL.SIGN\\, ,; ,1\\\\</skills>\n"
					+ ",<combatant>,</combatant>,1|\n" + "   </combatants>";
		List<PlayerCharacter> pcs = new ArrayList<>();
		pcs.add(getCharacter());
		String result = evaluatePartyToken(outputToken, pcs).trim();
		assertEquals(
			"Party skills output",
			"<combatants>" + System.getProperty("line.separator") + 
			"<combatant>	<name></name>	<skills> Balance: +9;  KNOWLEDGE (ARCANA): +11;  KNOWLEDGE (RELIGION): +8;  Tumble: +10; </skills></combatant>   </combatants>",
			result);
	}
	
	private String evaluateToken(String token, PlayerCharacter pc)
		throws IOException
	{
		StringWriter retWriter = new StringWriter();
		BufferedWriter bufWriter = new BufferedWriter(retWriter);
		ExportHandler export = new ExportHandler(new File(""));
		export.replaceToken(token, bufWriter, pc);
		retWriter.flush();

		bufWriter.flush();

		return retWriter.toString();
	}
	
	private String evaluatePartyToken(String token, List<PlayerCharacter> pcs)
		throws IOException
	{
        // Create temp file.
        File temp = File.createTempFile("testTemplate", ".txt");
    
        // Delete temp file when program exits.
        temp.deleteOnExit();
    
        // Write to temp file
        BufferedWriter out = new BufferedWriter(new FileWriter(temp));
        out.write(token);
        out.close();
		
		StringWriter retWriter = new StringWriter();
		BufferedWriter bufWriter = new BufferedWriter(retWriter);
		ExportHandler export = new ExportHandler(temp);
		export.write(pcs, bufWriter);
		retWriter.flush();

		bufWriter.flush();

		return retWriter.toString();
	}
}
