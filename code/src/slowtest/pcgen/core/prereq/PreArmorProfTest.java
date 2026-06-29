/*
 * Copyright 2008 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
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

import java.net.URI;
import java.net.URISyntaxException;

import pcgen.AbstractCharacterTestCase;
import pcgen.base.lang.UnreachableError;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Ability;
import pcgen.core.ArmorProf;
import pcgen.core.Campaign;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.FeatLoader;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.TestHelper;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code PreArmorProfTest} tests that the PREARMORPROF tag is
 * working correctly.
 */
class PreArmorProfTest extends AbstractCharacterTestCase
{
	/**
	 * Test with a simple armor proficiency.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	void testOneOption() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREPROFWITHARMOR:1,Chainmail");

		assertFalse(PrereqHandler.passes(
			prereq, character, null), "Character has no proficiencies");

		final Ability martialProf = 
			TestHelper.makeAbility("Shield Proficiency (Single)", "FEAT", "General");

		Globals.getContext().unconditionallyProcess(martialProf, "AUTO", "ARMORPROF|Chainmail");
		Globals.getContext().unconditionallyProcess(martialProf, "AUTO", "ARMORPROF|Full Plate");
		assertTrue(Globals.getContext().getReferenceContext().resolveReferences(null));

		AbstractCharacterTestCase.applyAbility(character, BuildUtilities.getFeatCat(), martialProf, null);

		assertTrue(PrereqHandler.passes(prereq, character, null), "Character has the Chainmail proficiency.");
		
		prereq = factory.parse("PREPROFWITHARMOR:1,Leather");
		
		assertFalse(PrereqHandler.passes(prereq, character, null), "Character does not have the Leather proficiency");
		
		prereq = factory.parse("PREPROFWITHARMOR:1,Full Plate");
		
		assertTrue(PrereqHandler.passes(prereq, character, null), "Character has the Full Plate proficiency.");
	}


	/**
	 * Tests to see if a character has a certain number of weaponprofs from a list.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	void testMultiple() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREPROFWITHARMOR:1,Chainmail,Full Plate");
		assertFalse(PrereqHandler.passes(
			prereq, character, null), "Character has no proficiencies");

		final Ability martialProf = 
			TestHelper.makeAbility("Shield Proficiency (Single)", "FEAT", "General");

		Globals.getContext().unconditionallyProcess(martialProf, "AUTO", "ARMORPROF|Chainmail");
		Globals.getContext().unconditionallyProcess(martialProf, "AUTO", "ARMORPROF|Full Plate");
		assertTrue(Globals.getContext().getReferenceContext().resolveReferences(null));
		
		AbstractCharacterTestCase.applyAbility(character, BuildUtilities.getFeatCat(), martialProf, null);

		assertTrue(PrereqHandler.passes(prereq, character, null), "Character has one of Chainmail or Full Plate proficiency");

		prereq = factory.parse("PREPROFWITHARMOR:2,Chainmail,Full Plate");

		assertTrue(PrereqHandler.passes(prereq, character, null), "Character has both Chainmail and Full Plate proficiency");
		
		prereq = factory.parse("PREPROFWITHARMOR:3,Chainmail,Full Plate,Leather");

		assertFalse(PrereqHandler.passes(prereq, character, null), "Character has both Chainmail and Full Plate proficiency but not Leather");
		
	}
	
	/**
	 * Test a preweaponprof that checks for a number of profs of a certain type.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	void testType() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREPROFWITHARMOR:1,TYPE.Medium");

		assertFalse(PrereqHandler.passes(
			prereq, character, null), "Character has no proficiencies");
		
		final Ability martialProf = 
			TestHelper.makeAbility("Shield Proficiency (Single)", "FEAT", "General");
		Globals.getContext().unconditionallyProcess(martialProf, "AUTO", "ARMORPROF|ARMORTYPE=Medium");
		
		AbstractCharacterTestCase.applyAbility(character, BuildUtilities.getFeatCat(), martialProf, null);
		
		assertTrue(PrereqHandler.passes(prereq, character, null), "Character has Medium Armor Proficiency");
	}
	
	/**
	 * Test with negation.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	void testInverse() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("!PREPROFWITHARMOR:1,Breastplate");

		assertTrue(PrereqHandler.passes(
			prereq, character, null), "Character has no proficiencies");

		final Ability martialProf = 
			TestHelper.makeAbility("Shield Proficiency (Single)", "FEAT", "General");

		Globals.getContext().unconditionallyProcess(martialProf, "AUTO", "ARMORPROF|Chainmail");
		Globals.getContext().unconditionallyProcess(martialProf, "AUTO", "ARMORPROF|Breastplate");
		assertTrue(Globals.getContext().getReferenceContext().resolveReferences(null));
		
		AbstractCharacterTestCase.applyAbility(character, BuildUtilities.getFeatCat(), martialProf, null);

		assertFalse(PrereqHandler.passes(prereq, character, null), "Character has the Breastplate proficiency.");
		
		prereq = factory.parse("!PREPROFWITHARMOR:1,Leather");
		
		assertTrue(PrereqHandler.passes(prereq, character, null), "Character does not have the Leather proficiency");
		
		prereq = factory.parse("!PREPROFWITHARMOR:1,Chainmail");
		
		assertFalse(PrereqHandler.passes(prereq, character, null), "Character has the Chainmail proficiency.");
		
	}
	
	/**
	 * Test the prearmorprof with armorprofs added by a AUTO:ARMORPROF tag
	 * This is probably more an integration test than a unit test
	 * 
	 * @throws Exception the exception
	 */
	@Test
	void testArmorProfAddedWithAutoArmorProf() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREPROFWITHARMOR:1,Breastplate");

		assertFalse(PrereqHandler.passes(
			prereq, character, null), "Character has no proficiencies");
		
		final Ability martialProf = 
			TestHelper.makeAbility("Armor Proficiency (Single)", BuildUtilities.getFeatCat(), "General");
		Globals.getContext().unconditionallyProcess(martialProf, "AUTO", "ARMORPROF|ARMORTYPE=Medium");
		assertTrue(Globals.getContext().getReferenceContext().resolveReferences(null));

		AbstractCharacterTestCase.applyAbility(character, BuildUtilities.getFeatCat(), martialProf, null);

		assertTrue(PrereqHandler.passes(prereq, character, null), "Character has the Breastplate proficiency.");
		
		prereq = factory.parse("PREPROFWITHARMOR:1,Chainmail");
		assertTrue(PrereqHandler.passes(prereq, character, null), "Character has the Chainmail proficiency.");
		
		prereq = factory.parse("PREPROFWITHARMOR:1,Leather");
		assertFalse(PrereqHandler.passes(prereq, character, null), "Character does not have the Leather proficiency.");
		
		prereq = factory.parse("PREPROFWITHARMOR:1,TYPE.Medium");
		assertTrue(PrereqHandler.passes(prereq, character, null), "Character has martial weaponprofs.");
		
	}
	
	/**
	 * Test Preweaponprof with a feat that has a bonus tag
	 * This test was written to help find the source of bug 1699779.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	void testWithFeatThatGrantsBonus() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		
		final FeatLoader featLoader = new FeatLoader();
		
		CampaignSourceEntry cse;
		try
		{
			cse = new CampaignSourceEntry(new Campaign(),
					new URI("file:/" + getClass().getName() + ".java"));
		}
		catch (URISyntaxException e)
		{
			throw new UnreachableError(e);
		}
		
		int baseHp = character.hitPoints();
		
		Ability bar = new Ability();
		final String barStr =
			"Bar	TYPE:General	DESC:See Text	BONUS:HP|CURRENTMAX|50";
		featLoader.parseLine(Globals.getContext(), bar, barStr, cse);
		addAbility(BuildUtilities.getFeatCat(), bar);
		
		assertEquals(baseHp+50, character.hitPoints(), "Character should have 50 bonus hp added.");
		
		final Ability martialProf = 
			TestHelper.makeAbility("Shield Proficiency (Single)", "FEAT", "General");
		Globals.getContext().unconditionallyProcess(martialProf, "AUTO", "ARMORPROF|Full Plate");
		assertTrue(Globals.getContext().getReferenceContext().resolveReferences(null));
		
		AbstractCharacterTestCase.applyAbility(character, BuildUtilities.getFeatCat(), martialProf, null);
		
		Ability foo = new Ability();
		final String fooStr =
			"Foo	TYPE:General	DESC:See Text	BONUS:HP|CURRENTMAX|50|PREPROFWITHARMOR:1,Full Plate";
		featLoader.parseLine(Globals.getContext(), foo, fooStr, cse);
		addAbility(BuildUtilities.getFeatCat(), foo);
		
		assertEquals(baseHp+50+50, character.hitPoints(), "Character has the Full Plate proficiency so the bonus should be added");
	
	}

	@BeforeEach
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		Equipment chainmailArmor = new Equipment();
		chainmailArmor.setName("Chainmail");
		chainmailArmor.addToListFor(ListKey.TYPE, Type.getConstant("Armor"));
		chainmailArmor.addToListFor(ListKey.TYPE, Type.getConstant("Medium"));
		Globals.getContext().getReferenceContext().importObject(chainmailArmor);
		
		Equipment breastplateArmor = new Equipment();
		breastplateArmor.setName("Breastplate");
		breastplateArmor.addToListFor(ListKey.TYPE, Type.getConstant("Armor"));
		breastplateArmor.addToListFor(ListKey.TYPE, Type.getConstant("Medium"));
		Globals.getContext().getReferenceContext().importObject(breastplateArmor);
		
		Equipment leatherArmor = new Equipment();
		leatherArmor.setName("Leather");
		leatherArmor.addToListFor(ListKey.TYPE, Type.getConstant("Armor"));
		leatherArmor.addToListFor(ListKey.TYPE, Type.getConstant("Light"));
		Globals.getContext().getReferenceContext().importObject(leatherArmor);
		
		Equipment fullPlateArmor = new Equipment();
		fullPlateArmor.setName("Full Plate");
		fullPlateArmor.addToListFor(ListKey.TYPE, Type.getConstant("Armor"));
		fullPlateArmor.addToListFor(ListKey.TYPE, Type.getConstant("Heavy"));
		Globals.getContext().getReferenceContext().importObject(fullPlateArmor);
		
		ArmorProf leather = new ArmorProf();
		leather.setName("Leather");
		leather.addToListFor(ListKey.TYPE, Type.getConstant("Armor"));
		leather.addToListFor(ListKey.TYPE, Type.getConstant("Light"));
		Globals.getContext().getReferenceContext().importObject(leather);

		ArmorProf chainmail = new ArmorProf();
		chainmail.setName("Chainmail");
		chainmail.addToListFor(ListKey.TYPE, Type.getConstant("Armor"));
		chainmail.addToListFor(ListKey.TYPE, Type.getConstant("Medium"));
		Globals.getContext().getReferenceContext().importObject(chainmail);

		ArmorProf breastplate = new ArmorProf();
		breastplate.setName("Breastplate");
		breastplate.addToListFor(ListKey.TYPE, Type.getConstant("Armor"));
		breastplate.addToListFor(ListKey.TYPE, Type.getConstant("Medium"));
		Globals.getContext().getReferenceContext().importObject(breastplate);

		ArmorProf fpprof = new ArmorProf();
		fpprof.setName("Full Plate");
		fpprof.addToListFor(ListKey.TYPE, Type.getConstant("Armor"));
		fpprof.addToListFor(ListKey.TYPE, Type.getConstant("Heavy"));
		Globals.getContext().getReferenceContext().importObject(fpprof);
}
}
