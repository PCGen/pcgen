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
package pcgen.core.prereq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.analysis.SkillRankControl;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.util.TestHelper;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.pretokens.parser.PreAbilityParser;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreSkillParser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import plugin.pretokens.test.PreMultTester;

/**
 * {@code PreMultTest} tests that the PreMult class
 * is working correctly.
 */

public class PreMultTest extends AbstractCharacterTestCase
{

	private Skill knowledge;
	private PCClass myClass;

	@BeforeEach
    @Override
    public void setUp() throws Exception
	{
		super.setUp();
		final PlayerCharacter character = getCharacter();

		myClass = new PCClass();
		myClass.setName("My Class");

		knowledge = new Skill();
		Globals.getContext().unconditionallyProcess(knowledge, "CLASSES", "My Class");
		knowledge.setName("KNOWLEDGE (ARCANA)");
		TestHelper.addType(knowledge, "KNOWLEDGE.INT");
		SkillRankControl.modRanks(8.0, myClass, true, character, knowledge);

	}

	@AfterEach
    @Override
    public void tearDown() throws Exception
	{
		knowledge = null;

		super.tearDown();
	}

	/**
	 * Test to ensure that a character will fail a test
	 * if it does not have the correct number of levels
	 * in the class.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 * @throws PrerequisiteException the prerequisite exception
	 */
	@Test
	public void testCharWithMultipleSpellClasses() throws PersistenceLayerException, PrerequisiteException
	{
		LoadContext context = Globals.getContext();
		final PCClass pcClass = context.getReferenceContext().constructCDOMObject(PCClass.class, "MyClass");
		context.unconditionallyProcess(pcClass, "SPELLSTAT", "CHA");
		BuildUtilities.setFact(pcClass, "SpellType", "Arcane");
		context.unconditionallyProcess(pcClass.getOriginalClassLevel(1), "CAST", "5,4");

		final PCClass pcClass2 = context.getReferenceContext().constructCDOMObject(PCClass.class, "Other Class");
		context.unconditionallyProcess(pcClass2, "SPELLSTAT", "INT");
		BuildUtilities.setFact(pcClass2, "SpellType", "Arcane");
		context.unconditionallyProcess(pcClass2.getOriginalClassLevel(1), "CAST", "5,4");
		context.getReferenceContext().buildDerivedObjects();
		context.getReferenceContext().resolveReferences(null);
		context.loadCampaignFacets();

		final PlayerCharacter character = getCharacter();
		setPCStat(character, cha, 12);
		setPCStat(character, intel, 12);
		character.incrementClassLevel(1, pcClass);
		character.incrementClassLevel(2, pcClass2);

		final PreClassParser producer = new PreClassParser();

		final Prerequisite prereq =
				producer.parse("CLASS",
					"1,SPELLCASTER.Arcane,SPELLCASTER.Arcane=2", false, false);

		final PreMultTester test = new PreMultTester();
		final int passes = test.passes(prereq, character, null);
		assertEquals(1, passes);
	}

	/**
	 * Test to ensure that a number of feat test will
	 * correctly require a number of separate feats in
	 * any combination of two types.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 * @throws PrerequisiteException the prerequisite exception
	 */
	@Test
	public void testMultiFeats() throws PersistenceLayerException, PrerequisiteException
	{
		final Ability metamagic1 = new Ability();
		metamagic1.addToListFor(ListKey.TYPE, Type.getConstant("METAMAGIC"));
		metamagic1.setName("MM1");
		metamagic1.put(StringKey.KEY_NAME, "MM1");
		metamagic1.setCDOMCategory(BuildUtilities.getFeatCat());

		final Ability metamagic2 = new Ability();
		metamagic2.addToListFor(ListKey.TYPE, Type.getConstant("METAMAGIC"));
		metamagic2.setName("MM2");
		metamagic2.put(StringKey.KEY_NAME, "MM2");
		metamagic2.setCDOMCategory(BuildUtilities.getFeatCat());

		final Ability metamagic3 = new Ability();
		metamagic3.addToListFor(ListKey.TYPE, Type.getConstant("METAMAGIC"));
		metamagic3.setName("MM3");
		metamagic3.put(StringKey.KEY_NAME, "MM3");
		metamagic3.setCDOMCategory(BuildUtilities.getFeatCat());

		final Ability item1 = new Ability();
		item1.addToListFor(ListKey.TYPE, Type.getConstant("ItemCreation"));
		item1.setName("IC1");
		item1.put(StringKey.KEY_NAME, "IC1");
		item1.setCDOMCategory(BuildUtilities.getFeatCat());

		final Ability item2 = new Ability();
		item2.addToListFor(ListKey.TYPE, Type.getConstant("ItemCreation"));
		item2.setName("IC2");
		item2.put(StringKey.KEY_NAME, "IC2");
		item2.setCDOMCategory(BuildUtilities.getFeatCat());

		final Ability item3 = new Ability();
		item3.addToListFor(ListKey.TYPE, Type.getConstant("ItemCreation"));
		item3.setName("IC3");
		item3.put(StringKey.KEY_NAME, "IC3");
		item3.setCDOMCategory(BuildUtilities.getFeatCat());

		final PlayerCharacter character = getCharacter();

		final PreAbilityParser producer = new PreAbilityParser();

		final Prerequisite prereq =
				producer.parse("FEAT", "3,TYPE=Metamagic,TYPE=ItemCreation",
					false, false);

		final PreMultTester test = new PreMultTester();
		int passes = test.passes(prereq, character, null);
		assertEquals(0, passes, "No feats should not pass");

		addAbility(BuildUtilities.getFeatCat(), metamagic1);
		passes = test.passes(prereq, character, null);
		assertEquals(0, passes, "One feat should not pass");

		addAbility(BuildUtilities.getFeatCat(), metamagic2);
		passes = test.passes(prereq, character, null);
		assertEquals(0, passes, "Two feats should not pass");

		addAbility(BuildUtilities.getFeatCat(), metamagic3);
		passes = test.passes(prereq, character, null);
		assertEquals(1, passes, "Three feats should pass");

		removeAbility(BuildUtilities.getFeatCat(), metamagic3);
		addAbility(BuildUtilities.getFeatCat(), item1);
		passes = test.passes(prereq, character, null);
		assertEquals(1, passes, "Three feats should pass");

		addAbility(BuildUtilities.getFeatCat(), item2);
		addAbility(BuildUtilities.getFeatCat(), item3);
		addAbility(BuildUtilities.getFeatCat(), metamagic3);
		passes = test.passes(prereq, character, null);
		assertEquals(1, passes, "Six feats should pass");

		removeAbility(BuildUtilities.getFeatCat(), metamagic3);
		removeAbility(BuildUtilities.getFeatCat(), item3);
		removeAbility(BuildUtilities.getFeatCat(), item2);
		removeAbility(BuildUtilities.getFeatCat(), item1);
		removeAbility(BuildUtilities.getFeatCat(), metamagic2);
		removeAbility(BuildUtilities.getFeatCat(), metamagic1);
	}

	/**
	 * Test to ensure that a number of skills test will
	 * correctly require a number of separate skills at
	 * the required level.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	public void testMultiSkills() throws PersistenceLayerException
	{
		final PreSkillParser producer = new PreSkillParser();

		final Prerequisite prereq =
				producer.parse("SKILL", "2,TYPE.Knowledge=4",
					false, false);
		final PlayerCharacter character = getCharacter();
		boolean passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(
				passes, "Should not pass 2 knowledge skill test with 1 skill");

		final Skill extraKnow = new Skill();
		Globals.getContext().unconditionallyProcess(extraKnow, "CLASSES", "MyClass");
		extraKnow.setName("KNOWLEDGE (RELIGION)");
		TestHelper.addType(extraKnow, "KNOWLEDGE.INT");
		SkillRankControl.modRanks(5.0, myClass, true, character, extraKnow);

		passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes, "Should pass 2 knowledge skill test with 2 skills");
	}
}
