/*
 * PreSkillTest.java
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
 * Created on 12-Jan-2004
 *
 * Current Ver: $Revision$
 *
 * Last Editor: $Author$
 *
 * Last Edited: $Date$
 *
 */
package pcgen.core.prereq;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.AbstractCharacterTestCase;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.persistence.lst.prereq.PreParserFactory;

public class PreSkillTest extends AbstractCharacterTestCase
{

	public static void main(final String[] args)
	{
		TestRunner.run(PreSkillTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreSkillTest.class);
	}

	Skill balance = null;
	Skill knowledge = null;
	Skill tumble = null;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		final PlayerCharacter character = getCharacter();

		final PCClass myClass = new PCClass();
		myClass.setName("My Class");

		knowledge = new Skill();
		knowledge.addClassList("My Class");
		knowledge.setName("KNOWLEDGE (ARCANA)");
		knowledge.setTypeInfo("KNOWLEDGE.INT");
		knowledge.modRanks(8.0, myClass, true, character);
		character.addSkill(knowledge);

		tumble = new Skill();
		tumble.addClassList("My Class");
		tumble.setName("Tumble");
		tumble.setTypeInfo("DEX");
		tumble.modRanks(8.0, myClass, true, character);
		character.addSkill(tumble);

		balance = new Skill();
		balance.addClassList("My Class");
		balance.setName("Balance");
		balance.setTypeInfo("DEX");
		balance.modRanks(4.0, myClass, true, character);
		character.addSkill(balance);

	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		knowledge = null;
		balance = null;
		tumble = null;

		super.tearDown();
	}

	/**
	 * @throws Exception
	 */
	public void testDexType() throws Exception
	{
		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("skill");
		prereq.setKey("TYPE.DEX");
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("5");

		final PlayerCharacter character = getCharacter();
		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testDexTypeEqualsFails() throws Exception
	{
		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("skill");
		prereq.setKey("TYPE.DEX");
		prereq.setOperator(PrerequisiteOperator.EQ);
		prereq.setOperand("5");

		final PlayerCharacter character = getCharacter();
		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testDexTypeEqualsPasses() throws Exception
	{
		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("skill");
		prereq.setKey("TYPE.DEX");
		prereq.setOperator(PrerequisiteOperator.EQ);
		prereq.setOperand("8");

		final PlayerCharacter character = getCharacter();
		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testKnowedgeSubType() throws Exception
	{
		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("skill");
		prereq.setKey("knowledge");
		prereq.setSubKey("arcana");
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("2");

		final PlayerCharacter character = getCharacter();

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testKnowedgeSubTypeFail() throws Exception
	{
		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("skill");
		prereq.setKey("knowledge");
		prereq.setSubKey("arcana");
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("9");

		final PlayerCharacter character = getCharacter();
		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testKnowedgeSubTypePasesExact() throws Exception
	{
		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("skill");
		prereq.setKey("knowledge");
		prereq.setSubKey("arcana");
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("8");

		final PlayerCharacter character = getCharacter();
		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testKnowedgeType() throws Exception
	{
		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("skill");
		prereq.setKey("TYPE.knowledge");
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("2");

		final PlayerCharacter character = getCharacter();
		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testKnowedgeWrongSubType() throws Exception
	{
		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("skill");
		prereq.setKey("knowledge");
		prereq.setSubKey("religion");
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("8");

		final PlayerCharacter character = getCharacter();
		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testPass() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final PCClass myClass = new PCClass();
		myClass.setName("My Class");

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("skill");
		prereq.setKey("tumble");
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("2");

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testTotalType() throws Exception
	{
		//		PreSkillTotalParser producer = new PreSkillTotalParser();

		//		Prerequisite prereq = producer.parse("SKILLTOT","TYPE.Knowledge=20", false, false);

		final Prerequisite prereq = new Prerequisite();
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("10");

		final Prerequisite subreq = new Prerequisite();
		subreq.setKind("skill");
		subreq.setKey("TYPE.DEX");
		subreq.setOperator(PrerequisiteOperator.GTEQ);
		subreq.setOperand("1");
		subreq.setTotalValues(true);

		prereq.addPrerequisite(subreq);

		final PlayerCharacter character = getCharacter();
		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testType() throws Exception
	{
		final Prerequisite subreq = new Prerequisite();
		subreq.setKind("skill");
		subreq.setKey("TYPE.DEX");
		subreq.setOperator(PrerequisiteOperator.GTEQ);
		subreq.setOperand("7");
		subreq.setTotalValues(true);

		final PlayerCharacter character = getCharacter();
		final boolean passes = PrereqHandler.passes(subreq, character, null);
		assertTrue(passes);
	}


	public void testLevelsTwoClasses() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final PreParserFactory factory = PreParserFactory.getInstance();
		Prerequisite prereq = factory.parse("PRESKILL:2,Balance=4,Tumble=2");

		assertEquals(true, PrereqHandler.passes(prereq, character, null));
	}
}
