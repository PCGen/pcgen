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
import pcgen.cdom.enumeration.EquipmentLocation;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.formula.FixedSizeFormula;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SizeAdjustment;
import pcgen.core.character.WieldCategory;
import pcgen.rules.context.LoadContext;

import org.junit.jupiter.api.Test;

/**
 * {@code PreEquipBothTest} tests that the PREEQUIPBOTH tag is
 * working correctly.
 */
public class PreEquipBothTest extends AbstractCharacterTestCase
{

    /**
     * Class under test for int passes(Prerequisite, PlayerCharacter)
     */
    @Test
    public void testPassesPrerequisitePlayerCharacter()
    {
        final PlayerCharacter character = getCharacter();

        final Equipment longsword = new Equipment();
        longsword.setName("Longsword");

        character.addEquipment(longsword);
        longsword.setIsEquipped(true, character);
        longsword.setLocation(EquipmentLocation.EQUIPPED_BOTH);
        character.doAfavorForAunitTestThatIgnoresEquippingRules();

        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("equipboth");
        prereq.setKey("LONGSWORD");
        prereq.setOperand("1");
        prereq.setOperator(PrerequisiteOperator.EQ);

        final boolean passes = PrereqHandler.passes(prereq, character, null);
        assertTrue(passes);

        longsword.setName("Longsword (Large/Masterwork)");

        assertFalse(PrereqHandler.passes(
                prereq, character, null), "Should be an exact match only");

        prereq.setKey("LONGSWORD (LARGE%");

        assertTrue(PrereqHandler.passes(prereq,
                character, null), "Should allow wildcard match");
    }

    /**
     * Test equipment type tests.
     */
    @Test
    public void testType()
    {
        final PlayerCharacter character = getCharacter();

        final Equipment longsword = new Equipment();
        longsword.setName("Longsword");

        character.addEquipment(longsword);
        longsword.setIsEquipped(true, character);
        longsword.setLocation(EquipmentLocation.EQUIPPED_BOTH);
        character.doAfavorForAunitTestThatIgnoresEquippingRules();

        Prerequisite prereq = new Prerequisite();
        prereq.setKind("equipboth");
        prereq.setKey("TYPE=Weapon");
        prereq.setOperand("1");
        prereq.setOperator(PrerequisiteOperator.EQ);

        assertFalse(PrereqHandler.passes(prereq,
                character, null), "Equipment has no type");

        longsword.addType(Type.WEAPON);

        assertTrue(PrereqHandler.passes(prereq,
                character, null), "Equipment is weapon");

        prereq.setKey("TYPE.Armor");

        assertFalse(PrereqHandler.passes(prereq,
                character, null), "Equipment is not armor");
    }

    /**
     * Test wield category tests.
     */
    @Test
    public void testWield()
    {
        final PlayerCharacter character = getCharacter();

        final Race race = new Race();
        race.setName("Test Race");
        CDOMDirectSingleRef<SizeAdjustment> mediumRef = CDOMDirectSingleRef.getRef(medium);
        CDOMDirectSingleRef<SizeAdjustment> largeRef = CDOMDirectSingleRef.getRef(large);
        race.put(FormulaKey.SIZE, new FixedSizeFormula(mediumRef));

        character.setRace(race);
        LoadContext context = Globals.getContext();

        final Equipment longsword = new Equipment();
        longsword.setName("Longsword");

        character.addEquipment(longsword);
        longsword.setIsEquipped(true, character);
        longsword.setLocation(EquipmentLocation.EQUIPPED_BOTH);
        character.doAfavorForAunitTestThatIgnoresEquippingRules();

        Prerequisite prereq = new Prerequisite();
        prereq.setKind("equipboth");
        prereq.setKey("WIELDCATEGORY=OneHanded");
        prereq.setOperand("1");
        prereq.setOperator(PrerequisiteOperator.EQ);

        // Test 3.0 Style
        longsword.put(ObjectKey.SIZE, mediumRef);
        longsword.put(ObjectKey.BASESIZE, mediumRef);

        assertTrue(PrereqHandler.passes(
                prereq, character, null), "Weapon is M therefore OneHanded");

        longsword.put(ObjectKey.SIZE, largeRef);
        longsword.put(ObjectKey.BASESIZE, largeRef);

        assertFalse(PrereqHandler.passes(
                prereq, character, null), "Weapon is L therefore TwoHanded");

        // Test 3.5 style
        longsword.put(ObjectKey.SIZE, mediumRef);
        longsword.put(ObjectKey.BASESIZE, mediumRef);
        longsword.put(ObjectKey.WIELD, context.getReferenceContext().silentlyGetConstructedCDOMObject(
                WieldCategory.class, "TwoHanded"));

        assertFalse(PrereqHandler.passes(prereq,
                character, null), "Weapon is TwoHanded");

        longsword.put(ObjectKey.WIELD, context.getReferenceContext().silentlyGetConstructedCDOMObject(
                WieldCategory.class, "OneHanded"));

        assertTrue(PrereqHandler.passes(prereq,
                character, null), "Weapon is OneHanded");

    }
}
