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
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.formula.FixedSizeFormula;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import org.junit.jupiter.api.Test;

/**
 * {@code PreHDTest} tests that the PREHD tag is
 * working correctly.
 */
@SuppressWarnings("nls")
public class PreHDTest extends AbstractCharacterTestCase
{
    Race race = new Race();
    Race race1 = new Race();
    PCClass monClass = new PCClass();

    /**
     * Test the PREHD code.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    public void testHD() throws PersistenceLayerException
    {
        race.setName("Human");
        CDOMDirectSingleRef<SizeAdjustment> mediumRef = CDOMDirectSingleRef.getRef(medium);
        race.put(FormulaKey.SIZE, new FixedSizeFormula(mediumRef));
        Globals.getContext().getReferenceContext().importObject(race);

        PCClass raceClass = new PCClass();
        raceClass.setName("Race Class");
        raceClass.put(StringKey.KEY_NAME, "RaceClass");
        raceClass.put(ObjectKey.IS_MONSTER, true);
        Globals.getContext().getReferenceContext().importObject(raceClass);

        race.put(ObjectKey.MONSTER_CLASS, new LevelCommandFactory(
                CDOMDirectSingleRef.getRef(raceClass), FormulaFactory
                .getFormulaFor(3)));

        final PlayerCharacter character = getCharacter();
        character.setRace(race);

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PREHD:MIN=4");

        assertFalse("Character doesn't have 4 HD", PrereqHandler.passes(prereq,
                character, null));

        prereq = factory.parse("PREHD:MIN=3");

        assertTrue("Character has 3 HD", PrereqHandler.passes(prereq,
                character, null));

        prereq = factory.parse("PREHD:MIN=1,MAX=3");

        assertTrue("Character has 3 HD", PrereqHandler.passes(prereq,
                character, null));

        prereq = factory.parse("PREHD:MIN=3,MAX=6");

        assertTrue("Character has 3 HD", PrereqHandler.passes(prereq,
                character, null));

        prereq = factory.parse("PREHD:MIN=4,MAX=7");

        assertFalse("Character doesn't have 4 HD", PrereqHandler.passes(prereq,
                character, null));

        prereq = factory.parse("PREHD:MIN=1,MAX=2");

        assertFalse("Character doesn't have 2 or less HD", PrereqHandler
                .passes(prereq, character, null));
    }

    /**
     * Tests using monster class levels.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testClassLevels() throws PersistenceLayerException
    {
        monClass.setName("Humanoid");
        monClass.put(ObjectKey.IS_MONSTER, true);
        Globals.getContext().getReferenceContext().importObject(monClass);

        race1.setName("Bugbear");
        CDOMDirectSingleRef<SizeAdjustment> largeRef = CDOMDirectSingleRef.getRef(large);
        race1.put(FormulaKey.SIZE, new FixedSizeFormula(largeRef));

        race1.put(ObjectKey.MONSTER_CLASS, new LevelCommandFactory(
                CDOMDirectSingleRef.getRef(monClass), FormulaFactory
                .getFormulaFor(3)));
        Globals.getContext().getReferenceContext().importObject(race1);

        final PlayerCharacter character = new PlayerCharacter();
        character.setRace(race1);

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PREHD:MIN=4");

        assertFalse("Character doesn't have 4 HD", PrereqHandler.passes(prereq,
                character, null));

        prereq = factory.parse("PREHD:MIN=3");

        assertTrue("Character has 3 HD", PrereqHandler.passes(prereq,
                character, null));

        prereq = factory.parse("PREHD:MIN=1,MAX=3");

        assertTrue("Character has 3 HD", PrereqHandler.passes(prereq,
                character, null));

        prereq = factory.parse("PREHD:MIN=3,MAX=6");

        assertTrue("Character has 3 HD", PrereqHandler.passes(prereq,
                character, null));

        prereq = factory.parse("PREHD:MIN=4,MAX=7");

        assertFalse("Character doesn't have 4 HD", PrereqHandler.passes(prereq,
                character, null));

        prereq = factory.parse("PREHD:MIN=1,MAX=2");

        assertFalse("Character doesn't have 2 or less HD", PrereqHandler
                .passes(prereq, character, null));
    }
}
