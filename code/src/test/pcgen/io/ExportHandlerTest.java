/*
 * SkillTokenTest.java
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
 * Created on Aug 7, 2004
 *
 * $Id$
 *
 */
package pcgen.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.core.Ability;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.LevelInfo;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.core.character.EquipSet;

/**
 * <code>SkillTokenTest</code> contains tests to verify that the
 * SKILL token and its subtokens are working correctly.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
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

		final LevelInfo levelInfo = new LevelInfo();
		levelInfo.setLevelString("LEVEL");
		levelInfo.setMaxClassSkillString("LEVEL+3");
		levelInfo.setMaxCrossClassSkillString("(LEVEL+3)/2");
		SettingsHandler.getGame().addLevelInfo(levelInfo);

		//Stats
		setPCStat(character, "DEX", 16);
		setPCStat(character, "INT", 17);
		PCStat stat = character.getStatList().getStatAt(3);
		stat.addBonusList("MODSKILLPOINTS|NUMBER|INT");

		// Race
		Race testRace = new Race();
		testRace.setName("TestRace");
		character.setRace(testRace);

		// Class
		PCClass myClass = new PCClass();
		myClass.setName("My Class");
		myClass.setSkillPointFormula("3");
		character.incrementClassLevel(5, myClass, true);

		//Skills
		knowledge = new Skill[2];
		knowledge[0] = new Skill();
		knowledge[0].addClassList("My Class");
		knowledge[0].setName("KNOWLEDGE (ARCANA)");
		knowledge[0].setTypeInfo("KNOWLEDGE.INT");
		knowledge[0].setKeyStat("INT");
		knowledge[0].modRanks(8.0, myClass, true, character);
		knowledge[0].setOutputIndex(2);
		Globals.getSkillList().add(knowledge[0]);
		character.addSkill(knowledge[0]);

		knowledge[1] = new Skill();
		knowledge[1].addClassList("My Class");
		knowledge[1].setName("KNOWLEDGE (RELIGION)");
		knowledge[1].setTypeInfo("KNOWLEDGE.INT");
		knowledge[1].setKeyStat("INT");
		knowledge[1].modRanks(5.0, myClass, true, character);
		knowledge[1].setOutputIndex(3);
		Globals.getSkillList().add(knowledge[1]);
		character.addSkill(knowledge[1]);

		tumble = new Skill();
		tumble.addClassList("My Class");
		tumble.setName("Tumble");
		tumble.setTypeInfo("DEX");
		tumble.setKeyStat("DEX");
		tumble.modRanks(7.0, myClass, true, character);
		tumble.setOutputIndex(4);
		Globals.getSkillList().add(tumble);
		character.addSkill(tumble);

		balance = new Skill();
		balance.addClassList("My Class");
		balance.setName("Balance");
		balance.setTypeInfo("DEX");
		balance.setKeyStat("DEX");
		balance.modRanks(4.0, myClass, true, character);
		balance.setOutputIndex(1);
		balance
			.addBonusList("SKILL|Balance|2|PRESKILL:1,Tumble=5|TYPE=Synergy.STACK");
		Globals.getSkillList().add(balance);
		character.addSkill(balance);

		character.calcActiveBonuses();

		weapon = new Equipment();
		weapon.setName("TestWpn");
		weapon.setTypeInfo("weapon");

		gem = new Equipment();
		gem.setName("TestGem");
		gem.setTypeInfo("gem");
		gem.setQty(1);
		
		armor = new Equipment();
		armor.setName("TestArmorSuit");
		armor.setTypeInfo("armor.suit");
		
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

		super.tearDown();
	}

	/**
	 * Test the output of old format tokens
	 * @throws IOException
	 */
	public void testOldFormat() throws IOException
	{
		PlayerCharacter character = getCharacter();

		// Test each token for old and new syntax processing.

		assertEquals("New format SKILL Token", "2", evaluateToken(
			"SKILL.0.MISC", character));
		assertEquals("Old format SKILL Token", evaluateToken("SKILL.0.MISC",
			character), evaluateToken("SKILL0.MISC", character));

		assertEquals("New format SKILLLEVEL Token", "6", evaluateToken(
			"SKILLLEVEL.1.TOTAL", character));

		assertEquals("New format SKILLSUBSET Token", "KNOWLEDGE (RELIGION)",
			evaluateToken("SKILLSUBSET.1.KNOWLEDGE.NAME", character));
		assertEquals("Old format SKILLSUBSET Token", evaluateToken(
			"SKILLSUBSET.1.KNOWLEDGE.NAME", character), evaluateToken(
			"SKILLSUBSET1.KNOWLEDGE.NAME", character));

		assertEquals("New format SKILLTYPE Token", "Balance", evaluateToken(
			"SKILLTYPE.0.DEX.NAME", character));
		assertEquals("Old format SKILLTYPE Token", evaluateToken(
			"SKILLTYPE.0.DEX.NAME", character), evaluateToken(
			"SKILLTYPE0.DEX.NAME", character));
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
		assertEquals("Test for evaluates correctly", "----------------",
			evaluateToken(
				"FOR.1,((24-STRLEN[SKILL.0])).INTVAL,24,-,NONE,NONE,1", pc));
		assertEquals("Test for evaluates correctly", "                ",
			evaluateToken(
				"FOR.1,((24-STRLEN[SKILL.0])).INTVAL,24, ,NONE,NONE,1", pc));
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
		
		Ability dummyFeat3 = new Ability();
		dummyFeat3.setName("DummyFeat3");
		dummyFeat3.setCategory("Maneuver");		
		
		Ability dummyFeat4 = new Ability();
		dummyFeat4.setName("DummyFeat4");
		dummyFeat4.setCategory("Maneuver(Special)");
		
		pc.addFeat(dummyFeat, null);
		pc.addFeat(dummyFeat2, null);
		pc.addFeat(dummyFeat3, null);
		pc.addFeat(dummyFeat4, null);
		
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
}
