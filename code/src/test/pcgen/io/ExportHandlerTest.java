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
import pcgen.core.Equipment;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.core.character.EquipSet;
import pcgen.io.ExportHandler;

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

public class ExportHandlerTest extends AbstractCharacterTestCase
{
	private Skill balance = null;
	private Skill[] knowledge = null;
	private Skill tumble = null;
	private Equipment weapon = null;

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
	public ExportHandlerTest(String name) {
		super(name);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		PlayerCharacter character = getCharacter();

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
		character.incrementClassLevel(1, myClass, true);

		//Skills
		knowledge = new Skill[2];
		knowledge[0] = new Skill();
		knowledge[0].addClassList("My Class");
		knowledge[0].setName("KNOWLEDGE (ARCANA)");
		knowledge[0].setTypeInfo("KNOWLEDGE.INT");
		knowledge[0].setKeyStat("INT");
		knowledge[0].modRanks(8.0, myClass, true, character);
		knowledge[0].setOutputIndex(2);
		character.addSkill(knowledge[0]);

		knowledge[1] = new Skill();
		knowledge[1].addClassList("My Class");
		knowledge[1].setName("KNOWLEDGE (RELIGION)");
		knowledge[1].setTypeInfo("KNOWLEDGE.INT");
		knowledge[1].setKeyStat("INT");
		knowledge[1].modRanks(5.0, myClass, true, character);
		knowledge[1].setOutputIndex(3);
		character.addSkill(knowledge[1]);

		tumble = new Skill();
		tumble.addClassList("My Class");
		tumble.setName("Tumble");
		tumble.setTypeInfo("DEX");
		tumble.setKeyStat("DEX");
		tumble.modRanks(7.0, myClass, true, character);
		tumble.setOutputIndex(4);
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
		character.addSkill(balance);

		character.calcActiveBonuses();

		weapon = new Equipment();
		weapon.setName("TestWpn");
		weapon.setTypeInfo("weapon");
	}

	/*
	 * @see TestCase#tearDown()
	 */
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

		assertEquals("New format SKILL Token", "2",
			evaluateToken("SKILL.0.MISC", character));
		assertEquals("Old format SKILL Token",
			evaluateToken("SKILL.0.MISC",	character),
			evaluateToken("SKILL0.MISC", character));

		assertEquals("New format SKILLLEVEL Token", "6",
			evaluateToken("SKILLLEVEL.1.TOTAL",	character));

		assertEquals("New format SKILLSUBSET Token", "KNOWLEDGE (RELIGION)",
			evaluateToken("SKILLSUBSET.1.KNOWLEDGE.NAME",	character));
		assertEquals("Old format SKILLSUBSET Token",
			evaluateToken("SKILLSUBSET.1.KNOWLEDGE.NAME",	character),
			evaluateToken("SKILLSUBSET1.KNOWLEDGE.NAME", character));

		assertEquals("New format SKILLTYPE Token", "Balance",
			evaluateToken("SKILLTYPE.0.DEX.NAME",	character));
		assertEquals("Old format SKILLTYPE Token",
			evaluateToken("SKILLTYPE.0.DEX.NAME",	character),
			evaluateToken("SKILLTYPE0.DEX.NAME", character));
	}

	/**
	 * Test the behaviour of the weapon loop
	 * @throws IOException
	 */
	public void testWpnLoop() throws IOException
	{
		PlayerCharacter character = getCharacter();

		// Test each token for old and new syntax processing.

		assertEquals("New format SKILL Token", "****",
			evaluateToken("FOR.0,100,1,**\\WEAPON.%.NAME\\**,NONE,NONE,1", character));
		// Now assign a weapon
		character.addEquipment(weapon);
		EquipSet es = new EquipSet("1", "Default", "", weapon);
		character.addEquipSet(es);
		assertEquals("New format SKILL Token", "**TestWpn**",
			evaluateToken("FOR.0,100,1,**\\WEAPON.%.NAME\\**,NONE,NONE,1", character));
	}

	private String evaluateToken(String token, PlayerCharacter pc)
		throws IOException
	{
		StringWriter retWriter = new StringWriter();
		BufferedWriter bufWriter = new BufferedWriter(retWriter);
		ExportHandler export = new ExportHandler(new File(""));
		export.replaceTokenSkipMath(pc, token, bufWriter);
		retWriter.flush();

		bufWriter.flush();

		return retWriter.toString();
	}
}
