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
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.LoadContext;

import org.junit.jupiter.api.Test;

/**
 * {@code PreEquipSecondaryTest} tests that the PREEQUIPSECONDARY tag is
 * working correctly.
 */
public class PreEquipSecondaryTest extends AbstractCharacterTestCase
{

    /*
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
        longsword.setLocation(EquipmentLocation.EQUIPPED_SECONDARY);
        character.doAfavorForAunitTestThatIgnoresEquippingRules();

        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("equipsecondary");
        prereq.setKey("LONGSWORD");
        prereq.setOperand("1");
        prereq.setOperator(PrerequisiteOperator.EQ);

        final boolean passes = PrereqHandler.passes(prereq, character, null);
        assertTrue(passes);

        longsword.setName("Longsword (Masterwork)");

        assertFalse("Should be an exact match only", PrereqHandler.passes(
                prereq, character, null));

        prereq.setKey("LONGSWORD%");

        assertTrue("Should allow wildcard match", PrereqHandler.passes(prereq,
                character, null));
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
        longsword.setLocation(EquipmentLocation.EQUIPPED_SECONDARY);
        character.doAfavorForAunitTestThatIgnoresEquippingRules();

        Prerequisite prereq = new Prerequisite();
        prereq.setKind("equipsecondary");
        prereq.setKey("TYPE=Weapon");
        prereq.setOperand("1");
        prereq.setOperator(PrerequisiteOperator.EQ);

        assertFalse("Equipment has no type", PrereqHandler.passes(prereq,
                character, null));

        longsword.addType(Type.WEAPON);

        assertTrue("Equipment is weapon", PrereqHandler.passes(prereq,
                character, null));

        prereq.setKey("TYPE.Armor");

        assertFalse("Equipment is not armor", PrereqHandler.passes(prereq,
                character, null));
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
        CDOMDirectSingleRef<SizeAdjustment> smallRef = CDOMDirectSingleRef.getRef(small);
        race.put(FormulaKey.SIZE, new FixedSizeFormula(mediumRef));

        character.setRace(race);
        LoadContext context = Globals.getContext();

        final Equipment longsword = new Equipment();
        longsword.setName("Longsword");

        character.addEquipment(longsword);
        longsword.setIsEquipped(true, character);
        longsword.setLocation(EquipmentLocation.EQUIPPED_SECONDARY);
        character.doAfavorForAunitTestThatIgnoresEquippingRules();

        Prerequisite prereq = new Prerequisite();
        prereq.setKind("equipsecondary");
        prereq.setKey("WIELDCATEGORY=Light");
        prereq.setOperand("1");
        prereq.setOperator(PrerequisiteOperator.EQ);

        // Test 3.0 Style
        longsword.put(ObjectKey.SIZE, smallRef);
        longsword.put(ObjectKey.BASESIZE, smallRef);

        assertTrue("Weapon is S therefore Light", PrereqHandler.passes(prereq,
                character, null));

        longsword.put(ObjectKey.SIZE, mediumRef);
        longsword.put(ObjectKey.BASESIZE, mediumRef);

        assertFalse("Weapon is M therefore OneHanded", PrereqHandler.passes(
                prereq, character, null));

        // Test 3.5 style
        longsword.put(ObjectKey.SIZE, mediumRef);
        longsword.put(ObjectKey.BASESIZE, mediumRef);
        longsword.put(ObjectKey.WIELD, context.getReferenceContext().silentlyGetConstructedCDOMObject(
                WieldCategory.class, "OneHanded"));

        assertFalse("Weapon is OneHanded", PrereqHandler.passes(prereq,
                character, null));

        longsword.put(ObjectKey.WIELD, context.getReferenceContext().silentlyGetConstructedCDOMObject(
                WieldCategory.class, "Light"));

        assertTrue("Weapon is Light", PrereqHandler.passes(prereq, character,
                null));

    }

    /**
     * Verify that negated PREEQUIPSECONDARY tests work.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testNotEquipped() throws PersistenceLayerException
    {
        final PlayerCharacter character = getCharacter();

        final Equipment longsword = new Equipment();
        longsword.setName("Longsword");

        final Equipment dagger = new Equipment();
        dagger.setName("Dagger");

        character.addEquipment(longsword);
        longsword.setIsEquipped(true, character);
        longsword.setLocation(EquipmentLocation.EQUIPPED_SECONDARY);
        character.doAfavorForAunitTestThatIgnoresEquippingRules();

        final Prerequisite prereqDaggerNotSec =
                PreParserFactory.getInstance().parse(
                        "!PREEQUIPSECONDARY:1,Dagger");
        assertTrue("Dagger not equipped",
                PrereqHandler.passes(prereqDaggerNotSec, character, null));

        character.addEquipment(dagger);
        dagger.setIsEquipped(true, character);
        dagger.setLocation(EquipmentLocation.EQUIPPED_PRIMARY);
        assertTrue("Dagger not equipped",
                PrereqHandler.passes(prereqDaggerNotSec, character, null));

        final Prerequisite prereqLongswordNotSec =
                PreParserFactory.getInstance().parse(
                        "!PREEQUIPSECONDARY:1,Longsword");
        assertFalse("Longsword equipped",
                PrereqHandler.passes(prereqLongswordNotSec, character, null));
    }
}
