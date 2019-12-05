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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.LoadContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code PreLevelTest} tests that the PRELEVEL tag is
 * working correctly.
 */
public class PreLevelTest extends AbstractCharacterTestCase
{
    private PCClass myClass = new PCClass();
    private Race race = new Race();

    /**
     * Test that Level works.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testLevel() throws PersistenceLayerException
    {
        final PlayerCharacter character = getCharacter();
        character.incrementClassLevel(1, myClass, true);

        myClass = character.getClassKeyed("MY_CLASS");

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PRELEVEL:MIN=2");
        assertFalse(PrereqHandler.passes(prereq,
                character, null), "Character is not 2nd level");

        character.incrementClassLevel(1, myClass, true);

        assertTrue(PrereqHandler.passes(prereq,
                character, null), "Character has 2 levels");

        character.incrementClassLevel(1, myClass, true);
        prereq = factory.parse("PRELEVEL:MIN=2,MAX=3");
        assertTrue(PrereqHandler.passes(prereq,
                character, null), "Character is 2nd or 3rd level");

        character.incrementClassLevel(1, myClass, true);
        assertFalse(PrereqHandler.passes(prereq,
                character, null), "Character is not 2nd or 3rd level");

        prereq = factory.parse("!PRELEVEL:MIN=2,MAX=3");
        assertTrue(PrereqHandler.passes(prereq,
                character, null), "Character is 2nd or 3rd level");

        prereq = factory.parse("!PRELEVEL:MIN=4");
        assertFalse(PrereqHandler.passes(prereq,
                character, null), "Character is 4 or higher level");

        prereq = factory.parse("!PRELEVEL:MAX=3");
        assertTrue(PrereqHandler.passes(prereq,
                character, null), "Character is 3rd or higher level");
    }

    /**
     * Test that HD are counted.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testHD() throws PersistenceLayerException
    {
        final PlayerCharacter character = getCharacter();
        character.incrementClassLevel(2, myClass, true);

        myClass = character.getClassKeyed("MY_CLASS");

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();

        prereq = factory.parse("PRELEVEL:MIN=4");
        assertFalse(PrereqHandler.passes(
                prereq, character, null), "Character doesn't have 4 levels");

        character.setRace(race);

        assertTrue(PrereqHandler.passes(prereq,
                character, null), "Character has 4 levels");

        prereq = factory.parse("!PRELEVEL:MIN=5");
        assertTrue(PrereqHandler.passes(
                prereq, character, null), "Character doesn't have 5 or more levels");

        prereq = factory.parse("!PRELEVEL:MAX=3");
        assertTrue(PrereqHandler.passes(
                prereq, character, null), "Character doesn't have 3 or more levels");

        prereq = factory.parse("!PRELEVEL:MIN=6,MAX=7");
        assertTrue(PrereqHandler.passes(
                prereq, character, null), "Character doesn't have between 6 and 7 levels");

        prereq = factory.parse("PRELEVEL:MIN=4,MAX=6");
        assertTrue(PrereqHandler.passes(
                prereq, character, null), "Character doesn't have 4-6 levels");

        prereq = factory.parse("PRELEVEL:MIN=6,MAX=7");
        assertFalse(PrereqHandler.passes(
                prereq, character, null), "Character doesn't have 6-7 levels");

        prereq = factory.parse("PRELEVEL:MAX=7");
        assertTrue(PrereqHandler.passes(
                prereq, character, null), "Character has no more than 5 levels");

        character.incrementClassLevel(4, myClass, true);
        prereq = factory.parse("PRELEVEL:MAX=7");
        assertFalse(PrereqHandler.passes(
                prereq, character, null), "Character has no more than 7 levels");


    }

    /**
     * Make sure BONUS:PCLEVEL is not counted.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testPCLevel() throws PersistenceLayerException
    {
        final PlayerCharacter character = getCharacter();
        LoadContext context = Globals.getContext();

        character.incrementClassLevel(2, myClass, true);

        myClass = character.getClassKeyed("MY_CLASS");

        character.setRace(race);

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();

        prereq = factory.parse("PRELEVEL:MIN=6");

        final BonusObj levelBonus = Bonus.newBonus(context, "PCLEVEL|MY_CLASS|2");
        myClass.addToListFor(ListKey.BONUS, levelBonus);
        character.calcActiveBonuses();

        assertFalse(PrereqHandler.passes(prereq,
                character, null), "Character has only 4 levels");


        prereq = factory.parse("PRELEVEL:MAX=6");
        assertTrue(PrereqHandler.passes(prereq,
                character, null), "Character has only 4 levels");

        prereq = factory.parse("!PRELEVEL:MAX=6");
        assertFalse(PrereqHandler.passes(prereq,
                character, null), "Character is less than 6 levels");

        prereq = factory.parse("!PRELEVEL:MIN=5");
        assertTrue(PrereqHandler.passes(prereq,
                character, null), "Character has only 4 levels");

        prereq = factory.parse("PRELEVEL:MIN=4,MAX=6");
        assertTrue(PrereqHandler.passes(prereq,
                character, null), "Character has 4-6 levels");

        prereq = factory.parse("PRELEVEL:MIN=6,MAX=8");
        assertFalse(PrereqHandler.passes(prereq,
                character, null), "Character does not have 6-8 levels");

        prereq = factory.parse("!PRELEVEL:MIN=6,MAX=8");
        assertTrue(PrereqHandler.passes(prereq,
                character, null), "Character is not 6-8 levels");


    }

    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        PCClass raceClass = new PCClass();
        raceClass.setName("Race Class");
        raceClass.put(StringKey.KEY_NAME, "RaceClass");
        Globals.getContext().getReferenceContext().importObject(raceClass);

        race.setName("Gnoll");
        race.put(ObjectKey.MONSTER_CLASS, new LevelCommandFactory(
                CDOMDirectSingleRef.getRef(raceClass), FormulaFactory
                .getFormulaFor(2)));

        myClass.setName("My Class");
        myClass.put(StringKey.KEY_NAME, "MY_CLASS");
        myClass.put(FormulaKey.START_SKILL_POINTS, FormulaFactory.getFormulaFor(3));
        Globals.getContext().getReferenceContext().importObject(myClass);
    }
}
