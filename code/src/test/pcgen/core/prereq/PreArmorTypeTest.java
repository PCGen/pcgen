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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
public class PreArmorTypeTest extends AbstractCharacterTestCase
{
    /*
     * Class under test for int passes(Prerequisite, PlayerCharacter)
     */
    @Test
    public void testPassesPrerequisitePlayerCharacter()
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

        assertFalse("Doesn't have chainmail equipped", PrereqHandler.passes(
                prereq, character, null));

        armor.setName("Chainmail");

        assertFalse("Chainmail is not equipped", PrereqHandler.passes(prereq,
                character, null));

        armor.setIsEquipped(true, character);
        character.doAfavorForAunitTestThatIgnoresEquippingRules();

        assertTrue("Chainmail is equipped", PrereqHandler.passes(prereq,
                character, null));

        armor.setName("Chainmail (Masterwork)");

        assertFalse("Should be an exact match only", PrereqHandler.passes(
                prereq, character, null));

        prereq.setKey("CHAINMAIL%");

        assertTrue("Should be allow wildcard match", PrereqHandler.passes(
                prereq, character, null));
    }

    /**
     * Test armor type tests.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testType() throws PersistenceLayerException
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

        assertFalse("Equipment has no type", PrereqHandler.passes(prereq,
                character, null));

        armor.addType(Type.getConstant("ARMOR"));
        armor.addType(Type.getConstant("MEDIUM"));

        assertTrue("Armor is medium", PrereqHandler.passes(prereq, character,
                null));

        prereq.setKey("TYPE.Heavy");

        assertFalse("Armor is not heavy", PrereqHandler.passes(prereq,
                character, null));

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PREARMORTYPE:2,TYPE=Medium,Full%");

        assertFalse("Armor is not Full something", PrereqHandler.passes(prereq,
                character, null));

        prereq = factory.parse("PREARMORTYPE:2,TYPE=Medium,Chain%");
        assertTrue("Armor is medium and Chain", PrereqHandler.passes(prereq,
                character, null));
    }

    /**
     * Test LIST.
     */
    @Test
    public void testList()
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

        assertFalse("No armor equipped", PrereqHandler.passes(prereq,
                character, null));

        character.addEquipment(chainmail);
        chainmail.setIsEquipped(true, character);
        character.doAfavorForAunitTestThatIgnoresEquippingRules();

        assertTrue("Proficient armor equipped", PrereqHandler.passes(prereq,
                character, null));

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

        assertFalse("Not Proficient in armor equipped", PrereqHandler.passes(
                prereq, character, null));
    }
}
