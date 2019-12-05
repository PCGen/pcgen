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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
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
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.DamageReduction;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code PreDRTest} tests that the PREDR tag is
 * working correctly.
 */
public class PreDRTest extends AbstractCharacterTestCase
{
    private Race race = new Race();
    private DamageReduction drPlus1;

    /**
     * Test basic functionality.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testDR() throws PersistenceLayerException
    {
        final PlayerCharacter character = getCharacter();
        character.setRace(race);

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PREDR:1,+1=10");

        assertFalse("Character has no DR", PrereqHandler.passes(prereq,
                character, null));

        race.addToListFor(ListKey.DAMAGE_REDUCTION, drPlus1);
        //This weirdness is because we are altering the race after application (no-no at runtime)
        character.setRace(null);
        character.setRace(race);

        assertFalse("Character DR not 10", PrereqHandler.passes(prereq,
                character, null));

        DamageReduction drPlus1_10 = new DamageReduction(FormulaFactory.getFormulaFor(10), "+1");
        race.addToListFor(ListKey.DAMAGE_REDUCTION, drPlus1_10);
        //This weirdness is because we are altering the race after application (no-no at runtime)
        character.setRace(null);
        character.setRace(race);

        assertTrue("Character has DR 10/+1", PrereqHandler.passes(prereq,
                character, null));
    }

    /**
     * Make sure or case works.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testMultiOr() throws PersistenceLayerException
    {
        final PlayerCharacter character = getCharacter();
        character.setRace(race);

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PREDR:1,+1=10,+2=5");

        assertFalse("Character has no DR", PrereqHandler.passes(prereq,
                character, null));

        race.addToListFor(ListKey.DAMAGE_REDUCTION, drPlus1);
        //This weirdness is because we are altering the race after application (no-no at runtime)
        character.setRace(null);
        character.setRace(race);

        assertFalse("Character DR not 10", PrereqHandler.passes(prereq,
                character, null));

        DamageReduction drPlus2_5 = new DamageReduction(FormulaFactory.getFormulaFor(5), "+2");
        race.addToListFor(ListKey.DAMAGE_REDUCTION, drPlus2_5);
        //This weirdness is because we are altering the race after application (no-no at runtime)
        character.setRace(null);
        character.setRace(race);

        assertTrue("Character has DR 5/+2", PrereqHandler.passes(prereq,
                character, null));
    }

    @Test
    public void testMultiAnd() throws Exception
    {
        final PlayerCharacter character = getCharacter();
        character.setRace(race);

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PREDR:2,+1=10,+2=5");

        assertFalse("Character has no DR", PrereqHandler.passes(prereq,
                character, null));

        race.addToListFor(ListKey.DAMAGE_REDUCTION, drPlus1);
        character.setRace(race);

        assertFalse("Character DR not 10", PrereqHandler.passes(prereq,
                character, null));

        DamageReduction drPlus2_5 = new DamageReduction(FormulaFactory.getFormulaFor(5), "+2");
        race.addToListFor(ListKey.DAMAGE_REDUCTION, drPlus2_5);
        //This weirdness is because we are altering the race after application (no-no at runtime)
        character.setRace(null);
        character.setRace(race);

        assertFalse("Character has DR 5/+2", PrereqHandler.passes(prereq,
                character, null));

        DamageReduction drPlus1_10 = new DamageReduction(FormulaFactory.getFormulaFor(10), "+1");
        race.addToListFor(ListKey.DAMAGE_REDUCTION, drPlus1_10);
        //This weirdness is because we are altering the race after application (no-no at runtime)
        character.setRace(null);
        character.setRace(race);

        assertTrue("Character has DR 10/+1 and 5/+2", PrereqHandler.passes(
                prereq, character, null));
    }

    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        drPlus1 = new DamageReduction(FormulaFactory.getFormulaFor(5), "+1");
    }
}
