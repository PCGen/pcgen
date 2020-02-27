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
package plugin.exporttokens;

import static org.junit.Assert.assertEquals;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.reference.CDOMDirectSingleRef;
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
import pcgen.io.exporttoken.SkillToken;
import pcgen.rules.context.LoadContext;
import pcgen.util.TestHelper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code SkillTokenTest} contains tests to verify that the
 * SKILL token and its subtokens are working correctly.
 */

@SuppressWarnings("nls")
public class SkillTokenTest extends AbstractCharacterTestCase
{
	private Skill balance = null;
	private Skill[] knowledge = null;
	private Skill tumble = null;

	@BeforeEach
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		final LevelInfo levelInfo = new LevelInfo();
		levelInfo.setLevelString("LEVEL");
		levelInfo.setMaxClassSkillString("LEVEL+3");
		levelInfo.setMaxCrossClassSkillString("(LEVEL+3)/2");
		GameMode gamemode = SettingsHandler.getGameAsProperty().get();
		gamemode.addLevelInfo("Default", levelInfo);

		LoadContext context = Globals.getContext();
		BonusObj aBonus = Bonus.newBonus(context, "MODSKILLPOINTS|NUMBER|INT");
		
		if (aBonus != null)
		{
			intel.addToListFor(ListKey.BONUS, aBonus);
		}

		// Race
		Race testRace = new Race();
		testRace.setName("TestRace");
		context.getReferenceContext().importObject(testRace);

		// Class
		PCClass myClass = new PCClass();
		myClass.setName("MyClass");
		myClass.put(FormulaKey.START_SKILL_POINTS, FormulaFactory.getFormulaFor(3));
		context.getReferenceContext().importObject(myClass);

		//Skills
		knowledge = new Skill[2];
		knowledge[0] = new Skill();
		context.unconditionallyProcess(knowledge[0], "CLASSES", "MyClass");
		knowledge[0].setName("KNOWLEDGE (ARCANA)");
		TestHelper.addType(knowledge[0], "KNOWLEDGE.INT");
		CDOMDirectSingleRef<PCStat> intelRef = CDOMDirectSingleRef.getRef(intel);
		knowledge[0].put(ObjectKey.KEY_STAT, intelRef);
		context.getReferenceContext().importObject(knowledge[0]);

		knowledge[1] = new Skill();
		context.unconditionallyProcess(knowledge[1], "CLASSES", "MyClass");
		knowledge[1].setName("KNOWLEDGE (RELIGION)");
		TestHelper.addType(knowledge[1], "KNOWLEDGE.INT");
		knowledge[1].put(ObjectKey.KEY_STAT, intelRef);
		context.getReferenceContext().importObject(knowledge[1]);

		tumble = new Skill();
		context.unconditionallyProcess(tumble, "CLASSES", "MyClass");
		tumble.setName("Tumble");
		tumble.addToListFor(ListKey.TYPE, Type.getConstant("DEX"));
		CDOMDirectSingleRef<PCStat> dexRef = CDOMDirectSingleRef.getRef(dex);
		tumble.put(ObjectKey.KEY_STAT, dexRef);
		context.getReferenceContext().importObject(tumble);

		balance = new Skill();
		context.unconditionallyProcess(balance, "CLASSES", "MyClass");
		balance.setName("Balance");
		balance.addToListFor(ListKey.TYPE, Type.getConstant("DEX"));
		balance.put(ObjectKey.KEY_STAT, dexRef);
		aBonus = Bonus.newBonus(context, "SKILL|Balance|2|PRESKILL:1,Tumble=5|TYPE=Synergy.STACK");
		
		if (aBonus != null)
		{
			balance.addToListFor(ListKey.BONUS, aBonus);
		}
		context.getReferenceContext().importObject(balance);
		
		finishLoad();
		
		PlayerCharacter character = getCharacter();
		//Stats
		setPCStat(character, dex, 16);
		setPCStat(character, intel, 17);
		
		character.setRace(testRace);
		character.incrementClassLevel(5, myClass, true);
		SkillRankControl.modRanks(4.0, myClass, true, character, balance);
		SkillRankControl.modRanks(8.0, myClass, true, character, knowledge[0]);
		SkillRankControl.modRanks(5.0, myClass, true, character, knowledge[1]);
		SkillRankControl.modRanks(7.0, myClass, true, character, tumble);

		character.calcActiveBonuses();
	}

	@AfterEach
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
	 * Test the SKILL token.
	 */
	@Test
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
		assertEquals("SkillToken", "+2[TUMBLE|Balance] +3[STAT]", token.getToken(
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
	@Test
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
	@Test
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
	@Test
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

	@Override
	protected void defaultSetupEnd()
	{
		//Handle locally
	}
	
	
}
