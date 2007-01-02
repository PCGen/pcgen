/*
 * PreMultTest.java
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
 * Created on Sep 4, 2004
 *
 * $Id$
 *
 */
package pcgen.core.prereq;

import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.core.Ability;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreFeatParser;
import plugin.pretokens.parser.PreSkillParser;

/**
 * <code>PreMultTest</code> tests that the PreMult class
 * is working correctly.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */

public class PreMultTest extends AbstractCharacterTestCase
{

	private Skill knowledge;
	private PCClass myClass;

	public static void main(final String[] args)
	{
		junit.swingui.TestRunner.run(PreMultTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreMultTest.class);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		final PlayerCharacter character = getCharacter();

		myClass = new PCClass();
		myClass.setName("My Class");

		knowledge = new Skill();
		knowledge.addClassList("My Class");
		knowledge.setName("KNOWLEDGE (ARCANA)");
		knowledge.setTypeInfo("KNOWLEDGE.INT");
		knowledge.modRanks(8.0, myClass, true, character);
		character.addSkill(knowledge);

	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		knowledge = null;

		super.tearDown();
	}

	/**
	 * Test to ensure that a character will fail a test
	 * if it does not have the correct number of levels
	 * in the class.
	 * @throws Exception
	 */
	public void testCharWithMultipleSpellClasses() throws Exception
	{
		final PCClass pcClass = new PCClass();
		pcClass.setName("MyClass");
		pcClass.setAbbrev("My");
		pcClass.setSpellBaseStat("CHA");
		pcClass.setSpellType("ARCANE");
		pcClass.setCast(1, Arrays.asList("5,4".split(",")));

		final PCClass pcClass2 = new PCClass();
		pcClass2.setName("Other Class");
		pcClass2.setAbbrev("OC");
		pcClass2.setSpellBaseStat("INT");
		pcClass2.setSpellType("ARCANE");
		pcClass2.setCast(1, Arrays.asList("5,4".split(",")));

		final PlayerCharacter character = getCharacter();
		character.incrementClassLevel(1, pcClass);
		character.incrementClassLevel(2, pcClass2);

		final PreClassParser producer = new PreClassParser();

		final Prerequisite prereq =
				producer.parse("CLASS",
					"1,SPELLCASTER.Arcane,SPELLCASTER.Arcane=2", false, false);

		final PreMult test = new PreMult();
		final int passes = test.passes(prereq, character);
		assertEquals(1, passes);
	}

	/**
	 * Test to ensure that a number of feat test will
	 * correctly require a number of separate feats in
	 * any combination of two types.
	 * @throws Exception
	 */
	public void testMultiFeats() throws Exception
	{
		final Ability metamagic1 = new Ability();
		metamagic1.setTypeInfo("METAMAGIC");
		metamagic1.setName("MM1");
		metamagic1.setKeyName("MM1");
		metamagic1.setCategory("FEAT");

		final Ability metamagic2 = new Ability();
		metamagic2.setTypeInfo("METAMAGIC");
		metamagic2.setName("MM2");
		metamagic2.setKeyName("MM2");
		metamagic2.setCategory("FEAT");

		final Ability metamagic3 = new Ability();
		metamagic3.setTypeInfo("METAMAGIC");
		metamagic3.setName("MM3");
		metamagic3.setKeyName("MM3");
		metamagic3.setCategory("FEAT");

		final Ability item1 = new Ability();
		item1.setTypeInfo("ItemCreation");
		item1.setName("IC1");
		item1.setKeyName("IC1");
		item1.setCategory("FEAT");

		final Ability item2 = new Ability();
		item2.setTypeInfo("ItemCreation");
		item2.setName("IC2");
		item2.setKeyName("IC2");
		item2.setCategory("FEAT");

		final Ability item3 = new Ability();
		item3.setTypeInfo("ItemCreation");
		item3.setName("IC3");
		item3.setKeyName("IC3");
		item3.setCategory("FEAT");

		final PlayerCharacter character = getCharacter();

		final PreFeatParser producer = new PreFeatParser();

		final Prerequisite prereq =
				producer.parse("FEAT", "3,TYPE=Metamagic,TYPE=ItemCreation",
					false, false);

		final PreMult test = new PreMult();
		int passes = test.passes(prereq, character);
		assertEquals("No feats should not pass", 0, passes);

		character.addFeat(metamagic1, null);
		passes = test.passes(prereq, character);
		assertEquals("One feat should not pass", 0, passes);

		character.addFeat(metamagic2, null);
		passes = test.passes(prereq, character);
		assertEquals("Two feats should not pass", 0, passes);

		character.addFeat(metamagic3, null);
		passes = test.passes(prereq, character);
		assertEquals("Three feats should pass", 1, passes);

		character.removeRealFeat(metamagic3);
		character.addFeat(item1, null);
		passes = test.passes(prereq, character);
		assertEquals("Three feats should pass", 1, passes);

		character.addFeat(item2, null);
		character.addFeat(item3, null);
		character.addFeat(metamagic3, null);
		passes = test.passes(prereq, character);
		assertEquals("Six feats should pass", 1, passes);

		character.removeRealFeat(metamagic3);
		character.removeRealFeat(item3);
		character.removeRealFeat(item2);
		character.removeRealFeat(item1);
		character.removeRealFeat(metamagic2);
		character.removeRealFeat(metamagic1);
	}

	/**
	 * Test to ensure that a number of skills test will
	 * correctly require a number of separate skills at
	 * the required level.
	 * @throws Exception
	 */
	public void testMultiSkills() throws Exception
	{
		final PreSkillParser producer = new PreSkillParser();

		final Prerequisite prereq =
				producer.parse("SKILL", "2,TYPE.Knowledge,TYPE.Knowledge=4",
					false, false);
		final PlayerCharacter character = getCharacter();
		boolean passes = PrereqHandler.passes(prereq, character, null);
		assertFalse("Should not pass 2 knowledge skill test with 1 skill",
			passes);

		final Skill extraKnow = new Skill();
		extraKnow.addClassList("My Class");
		extraKnow.setName("KNOWLEDGE (RELIGION)");
		extraKnow.setTypeInfo("KNOWLEDGE.INT");
		extraKnow.modRanks(5.0, myClass, true, character);
		character.addSkill(extraKnow);

		passes = PrereqHandler.passes(prereq, character, null);
		assertTrue("Should pass 2 knowledge skill test with 2 skills", passes);

		character.getSkillList().remove(knowledge);
		character.calcActiveBonuses();

	}
}
