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
import pcgen.cdom.enumeration.Type;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;

import org.junit.jupiter.api.Test;

/**
 * {@code PreItemTest} tests that the PREITEM tag is
 * working correctly.
 */
public class PreItemTest extends AbstractCharacterTestCase
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

        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("item");
        prereq.setKey("LONGSWORD");
        prereq.setOperand("1");
        prereq.setOperator(PrerequisiteOperator.EQ);

        final boolean passes = PrereqHandler.passes(prereq, character, null);
        assertTrue(passes);

        longsword.setName("Longsword (Masterwork)");

        assertFalse("Should be an exact match only", PrereqHandler.passes(
                prereq, character, null));

        prereq.setKey("LONGSWORD%");

        assertTrue("Should be allow wildcard match", PrereqHandler.passes(
                prereq, character, null));
    }

    /**
     * Test equipment type tests.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testType() throws PersistenceLayerException
    {
        final PlayerCharacter character = getCharacter();

        final Equipment longsword = new Equipment();
        longsword.setName("Longsword");

        character.addEquipment(longsword);

        Prerequisite prereq = new Prerequisite();
        prereq.setKind("item");
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

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PREITEM:2,TYPE=Armor,Longsword%");

        assertFalse("Doesn't have armor", PrereqHandler.passes(prereq,
                character, null));

        final Equipment leather = new Equipment();
        leather.setName("Leather");
        leather.addType(Type.getConstant("ARMOR"));

        character.addEquipment(leather);

        assertTrue("Armor and sword present", PrereqHandler.passes(prereq,
                character, null));
    }

}
