/*
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Ability;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.TestHelper;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.Test;

/**
 * {@code PreArmorTypeTest} tests that the PREARMORTYPE tag is
 * working correctly.
 */
class PreArmorTypeTest extends AbstractCharacterTestCase
{
	/*
	 * Class under test for int passes(Prerequisite, PlayerCharacter)
	 */
	@Test
	void testPassesPrerequisitePlayerCharacter()
	{
		final PlayerCharacter character = getCharacter();

		final Equipment armor = new Equipment();
		armor.setName("Leather");
		armor.addToListFor(ListKey.TYPE, Type.getConstant("ARMOR"));

		character.addEquipment(armor);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("armortype");
		prereq.setKey("CHAINMAIL");
		prereq.setOperand("1");
		prereq.setOperator(PrerequisiteOperator.EQ);

		assertFalse(PrereqHandler.passes(
			prereq, character, null), "Doesn't have chainmail equipped");

		armor.setName("Chainmail");

		assertFalse(PrereqHandler.passes(prereq,
			character, null), "Chainmail is not equipped");

		armor.setIsEquipped(true, character);
		character.doAfavorForAunitTestThatIgnoresEquippingRules();

		assertTrue(PrereqHandler.passes(prereq,
			character, null), "Chainmail is equipped");

		armor.setName("Chainmail (Masterwork)");

		assertFalse(PrereqHandler.passes(
			prereq, character, null), "Should be an exact match only");

		prereq.setKey("CHAINMAIL%");

		assertTrue(PrereqHandler.passes(
			prereq, character, null), "Should be allow wildcard match");
	}

	/**
	 * Test armor type tests.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	void testType() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();

		final Equipment armor = new Equipment();
		armor.setName("Chainmail");

		character.addEquipment(armor);
		armor.setIsEquipped(true, character);
		character.doAfavorForAunitTestThatIgnoresEquippingRules();

		Prerequisite prereq = new Prerequisite();
		prereq.setKind("armortype");
		prereq.setKey("TYPE=Medium");
		prereq.setOperand("1");
		prereq.setOperator(PrerequisiteOperator.EQ);

		assertFalse(PrereqHandler.passes(prereq,
			character, null), "Equipment has no type");

		armor.addType(Type.getConstant("ARMOR"));
		armor.addType(Type.getConstant("MEDIUM"));
		
		assertTrue(PrereqHandler.passes(prereq, character,
			null), "Armor is medium");

		prereq.setKey("TYPE.Heavy");

		assertFalse(PrereqHandler.passes(prereq,
			character, null), "Armor is not heavy");

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREARMORTYPE:2,TYPE=Medium,Full%");

		assertFalse(PrereqHandler.passes(prereq,
			character, null), "Armor is not Full something");

		prereq = factory.parse("PREARMORTYPE:2,TYPE=Medium,Chain%");
		assertTrue(PrereqHandler.passes(prereq,
			character, null), "Armor is medium and Chain");
	}

	/**
	 * Test LIST.
	 */
	@Test
	void testList()
	{
		final PlayerCharacter character = getCharacter();

		final Ability mediumProf =
				TestHelper.makeAbility("Armor Proficiency (Medium)", BuildUtilities.getFeatCat(),
					"General");
		Globals.getContext().unconditionallyProcess(mediumProf, "AUTO",
				"ARMORPROF|ARMORTYPE.Medium");
		AbstractCharacterTestCase.applyAbility(character, BuildUtilities.getFeatCat(), mediumProf, null);

		final Equipment chainmail = new Equipment();
		chainmail.addToListFor(ListKey.TYPE, Type.getConstant("ARMOR"));
		chainmail.addToListFor(ListKey.TYPE, Type.getConstant("MEDIUM"));
		chainmail.setName("Chainmail");
		Globals.getContext().getReferenceContext().importObject(chainmail);
		assertTrue(Globals.getContext().getReferenceContext().resolveReferences(null));
		Prerequisite prereq = new Prerequisite();
		prereq.setKind("armortype");
		prereq.setKey("LIST");
		prereq.setOperand("1");
		prereq.setOperator(PrerequisiteOperator.EQ);

		assertFalse(PrereqHandler.passes(prereq,
			character, null), "No armor equipped");

		character.addEquipment(chainmail);
		chainmail.setIsEquipped(true, character);
		character.doAfavorForAunitTestThatIgnoresEquippingRules();

		assertTrue(PrereqHandler.passes(prereq,
			character, null), "Proficient armor equipped");

		chainmail.setIsEquipped(false, character);
		character.doAfavorForAunitTestThatIgnoresEquippingRules();

		final Equipment fullPlate = new Equipment();
		fullPlate.addToListFor(ListKey.TYPE, Type.getConstant("ARMOR"));
		fullPlate.addToListFor(ListKey.TYPE, Type.getConstant("HEAVY"));
		fullPlate.setName("Full Plate");
		Globals.getContext().getReferenceContext().importObject(fullPlate);
		
		/*
		 * TODO This doesn't make a lot of sense - false? Shouldn't this be true
		 * to be a useful test?
		 */
		fullPlate.setIsEquipped(false, character);
		character.doAfavorForAunitTestThatIgnoresEquippingRules();

		assertFalse(PrereqHandler.passes(
			prereq, character, null), "Not Proficient in armor equipped");
	}
}
