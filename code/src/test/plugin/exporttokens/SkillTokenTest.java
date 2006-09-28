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
 * Created on Aug 6, 2004
 *
 * $Id$
 *
 */
package plugin.exporttokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.core.Globals;
import pcgen.core.LevelInfo;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.io.exporttoken.SkillToken;
import plugin.exporttokens.SkillLevelToken;
import plugin.exporttokens.SkillSubsetToken;
import plugin.exporttokens.SkillTypeToken;

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
public class SkillTokenTest extends AbstractCharacterTestCase
{
	private Skill balance = null;
	private Skill[] knowledge = null;
	private Skill tumble = null;

	/**
	 * Quick test suite creation - adds all methods beginning with "test"
	 * @return The Test suite
	 */
	public static Test suite()
	{
		return new TestSuite(SkillTokenTest.class);
	}

	/**
	 * Basic constructor, name only.
	 * @param name The name of the test class.
	 */
	public SkillTokenTest(String name) {
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
		Globals.getSkillList().add(knowledge[0]);
		character.addSkill(knowledge[0]);

		knowledge[1] = new Skill();
		knowledge[1].addClassList("My Class");
		knowledge[1].setName("KNOWLEDGE (RELIGION)");
		knowledge[1].setTypeInfo("KNOWLEDGE.INT");
		knowledge[1].setKeyStat("INT");
		knowledge[1].modRanks(5.0, myClass, true, character);
		Globals.getSkillList().add(knowledge[1]);
		character.addSkill(knowledge[1]);

		tumble = new Skill();
		tumble.addClassList("My Class");
		tumble.setName("Tumble");
		tumble.setTypeInfo("DEX");
		tumble.setKeyStat("DEX");
		tumble.modRanks(7.0, myClass, true, character);
		Globals.getSkillList().add(tumble);
		character.addSkill(tumble);

		balance = new Skill();
		balance.addClassList("My Class");
		balance.setName("Balance");
		balance.setTypeInfo("DEX");
		balance.setKeyStat("DEX");
		balance.modRanks(4.0, myClass, true, character);
		balance
			.addBonusList("SKILL|Balance|2|PRESKILL:1,Tumble=5|TYPE=Synergy.STACK");
		Globals.getSkillList().add(balance);
		character.addSkill(balance);

		character.calcActiveBonuses();
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
	 * Test the SKILL token.
	 */
	public void testSkillToken()
	{
		PlayerCharacter character = getCharacter();

		SkillToken token = new SkillToken();

		// First test each sub token
		assertEquals("SkillToken", "Balance", token.getToken("SKILL.0",
			character, null));
		assertEquals("SkillToken", "DEX", token.getToken("SKILL.0.ABILITY",
			character, null));
		assertEquals("SkillToken", "9", token.getToken("SKILL.0.TOTAL",
			character, null));
		assertEquals("SkillToken", "3", token.getToken("SKILL.0.ABMOD",
			character, null));
		assertEquals("SkillToken", "4.0", token.getToken("SKILL.0.RANK",
			character, null));
		assertEquals("SkillToken", "2", token.getToken("SKILL.0.MISC",
			character, null));
		assertEquals("SkillToken", "N", token.getToken("SKILL.0.EXCLUSIVE",
			character, null));
		assertEquals("SkillToken", "Y", token.getToken("SKILL.0.UNTRAINED",
			character, null));
		assertEquals("SkillToken", "9", token.getToken(
			"SKILL.0.EXCLUSIVE_TOTAL", character, null));
		assertEquals("SkillToken", "9", token.getToken("SKILL.0.TRAINED_TOTAL",
			character, null));
		assertEquals("SkillToken", "+2[TUMBLE] +3[STAT]", token.getToken(
			"SKILL.0.EXPLAIN", character, null));

		// Test the indexed retrieval
		assertEquals("SkillToken", "Tumble", token.getToken("SKILL.3",
			character, null));

		// Test the named retrieval
		assertEquals("SkillToken", "Tumble", token.getToken("SKILL.TUMBLE",
			character, null));
	}

	/**
	 * Test the SKILLLEVEL token.
	 */
	public void testSkillLevelToken()
	{
		PlayerCharacter character = getCharacter();

		SkillToken token = new SkillLevelToken();

		// First test each sub token
		assertEquals("SKILLLEVEL.1.TOTAL", "6", token.getToken(
			"SKILLLEVEL.1.TOTAL", character, null));
	}

	/**
	 * Test the SKILLSUBSET token.
	 */
	public void testSkillSubsetToken()
	{
		PlayerCharacter character = getCharacter();

		SkillToken token = new SkillSubsetToken();

		// First test each sub token
		assertEquals("SkillSubsetToken", "KNOWLEDGE (RELIGION)", token
			.getToken("SKILLSUBSET.1.KNOWLEDGE.NAME", character, null));
		assertEquals("SkillSubsetToken", "8.0", token.getToken(
			"SKILLSUBSET.0.KNOWLEDGE.RANK", character, null));
	}

	/**
	 * Test the SKILLTYPE token.
	 */
	public void testSkillTypeToken()
	{
		PlayerCharacter character = getCharacter();

		SkillToken token = new SkillTypeToken();

		// First test each sub token
		assertEquals("SkillTypeToken", "Balance", token.getToken(
			"SKILLTYPE.0.DEX.NAME", character, null));
		assertEquals("SkillTypeToken", "10", token.getToken(
			"SKILLTYPE.1.DEX.TOTAL", character, null));
	}
}
