/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net>
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
import pcgen.rules.context.LoadContext;
import pcgen.util.TestHelper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SkillSitTokenTest extends AbstractCharacterTestCase
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
		aBonus = Bonus.newBonus(context, "SITUATION|Tumble=On Hot Concrete|2");
		if (aBonus != null)
		{
			knowledge[0].addToListFor(ListKey.BONUS, aBonus);
		}

		knowledge[1] = new Skill();
		context.unconditionallyProcess(knowledge[1], "CLASSES", "MyClass");
		knowledge[1].setName("KNOWLEDGE (RELIGION)");
		TestHelper.addType(knowledge[1], "KNOWLEDGE.INT");
		knowledge[1].put(ObjectKey.KEY_STAT, intelRef);
		aBonus = Bonus.newBonus(context, "SITUATION|Balance=On a Ball|2");
		if (aBonus != null)
		{
			knowledge[1].addToListFor(ListKey.BONUS, aBonus);
		}
		context.getReferenceContext().importObject(knowledge[1]);

		tumble = new Skill();
		context.unconditionallyProcess(tumble, "CLASSES", "MyClass");
		tumble.setName("Tumble");
		tumble.addToListFor(ListKey.TYPE, Type.getConstant("DEX"));
		tumble.addToListFor(ListKey.SITUATION, "On Hot Concrete");
		tumble.addToListFor(ListKey.SITUATION, "Down a Mountain");
		CDOMDirectSingleRef<PCStat> dexRef = CDOMDirectSingleRef.getRef(dex);
		tumble.put(ObjectKey.KEY_STAT, dexRef);
		context.getReferenceContext().importObject(tumble);

		balance = new Skill();
		context.unconditionallyProcess(balance, "CLASSES", "MyClass");
		balance.setName("Balance");
		balance.addToListFor(ListKey.TYPE, Type.getConstant("DEX"));
		balance.addToListFor(ListKey.SITUATION, "On a Ball");
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
		
		SkillRankControl.modRanks(8.0, myClass, true, character, knowledge[0]);
		SkillRankControl.modRanks(5.0, myClass, true, character, knowledge[1]);
		SkillRankControl.modRanks(7.0, myClass, true, character, tumble);
		SkillRankControl.modRanks(4.0, myClass, true, character, balance);

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

		SkillSitToken token = new SkillSitToken();

		// First test each sub token
		assertEquals("SkillToken", "Balance", token.getToken("SKILLSIT.0",
			character, null));
		assertEquals("SkillToken", "DEX", token.getToken("SKILLSIT.0.ABILITY",
			character, null));
		assertEquals("SkillToken", "9", token.getToken("SKILLSIT.0.TOTAL",
				character, null));
		assertEquals("SkillToken", "3", token.getToken("SKILLSIT.0.ABMOD",
			character, null));
		assertEquals("SkillToken", "4.0", token.getToken("SKILLSIT.0.RANK",
			character, null));
		assertEquals("SkillToken", "2", token.getToken("SKILLSIT.0.MISC",
			character, null));
		assertEquals("SkillToken", "N", token.getToken("SKILLSIT.0.EXCLUSIVE",
			character, null));
		assertEquals("SkillToken", "Y", token.getToken("SKILLSIT.0.UNTRAINED",
			character, null));
		assertEquals("SkillToken", "9", token.getToken(
			"SKILLSIT.0.EXCLUSIVE_TOTAL", character, null));
		assertEquals("SkillToken", "9", token.getToken("SKILLSIT.0.TRAINED_TOTAL",
			character, null));
		assertEquals("SkillToken", "+2[TUMBLE|Balance] +3[STAT]", token.getToken(
			"SKILLSIT.0.EXPLAIN", character, null));

		// Test the indexed retrieval
		assertEquals("SkillToken", "Balance (On a Ball)", token.getToken("SKILLSIT.1",
			character, null));
		assertEquals("SkillToken", "DEX", token.getToken("SKILLSIT.1.ABILITY",
			character, null));
		assertEquals("SkillToken", "11", token.getToken("SKILLSIT.1.TOTAL",
				character, null));
		assertEquals("SkillToken", "3", token.getToken("SKILLSIT.1.ABMOD",
			character, null));
		assertEquals("SkillToken", "4.0", token.getToken("SKILLSIT.1.RANK",
			character, null));
		assertEquals("SkillToken", "4", token.getToken("SKILLSIT.1.MISC",
			character, null));
		assertEquals("SkillToken", "N", token.getToken("SKILLSIT.1.EXCLUSIVE",
			character, null));
		assertEquals("SkillToken", "Y", token.getToken("SKILLSIT.1.UNTRAINED",
			character, null));
		assertEquals("SkillToken", "11", token.getToken(
			"SKILLSIT.1.EXCLUSIVE_TOTAL", character, null));
		assertEquals("SkillToken", "11", token.getToken("SKILLSIT.1.TRAINED_TOTAL",
			character, null));
		assertEquals("SkillToken", "+2[TUMBLE|Balance] +3[STAT] situational: +2[KNOWLEDGE (RELIGION)]", token.getToken(
			"SKILLSIT.1.EXPLAIN", character, null));

		
		assertEquals("SkillToken", "KNOWLEDGE (ARCANA)", token.getToken("SKILLSIT.2",
			character, null));
		assertEquals("SkillToken", "KNOWLEDGE (RELIGION)", token.getToken("SKILLSIT.3",
			character, null));
		assertEquals("SkillToken", "Tumble", token.getToken("SKILLSIT.4",
			character, null));
		//Alphabetical!
		assertEquals("SkillToken", "Tumble (On Hot Concrete)", token.getToken("SKILLSIT.5",
			character, null));

		// Test the named retrieval
		assertEquals("SkillToken", "Tumble", token.getToken("SKILLSIT.TUMBLE",
			character, null));
		assertEquals("SkillToken", "Tumble (On Hot Concrete)",
			token.getToken("SKILLSIT.TUMBLE=On Hot Concrete", character, null));
	}

	@Override
	protected void defaultSetupEnd()
	{
		//We will handle this locally
	}
}
