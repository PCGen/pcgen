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
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
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
 * {@code PreLevelMaxTest} tests that the PRELEVELMAX tag is
 * working correctly.
 */
public class PreLevelMaxTest extends AbstractCharacterTestCase
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
        prereq = factory.parse("PRELEVELMAX:1");

        assertTrue("Character is not 2nd level", PrereqHandler.passes(prereq,
                character, null));

        character.incrementClassLevel(1, myClass, true);

        assertFalse("Character has 2 levels", PrereqHandler.passes(prereq,
                character, null));
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

        prereq = factory.parse("PRELEVELMAX:3");

        assertTrue("Character doesn't have 4 levels", PrereqHandler.passes(
                prereq, character, null));

        character.setRace(race);

        assertTrue("Character has 4 levels", PrereqHandler.passes(prereq,
                character, null));
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

        prereq = factory.parse("PRELEVELMAX:5");

        final BonusObj levelBonus = Bonus.newBonus(context, "PCLEVEL|MY_CLASS|2");
        myClass.addToListFor(ListKey.BONUS, levelBonus);
        character.calcActiveBonuses();

        assertTrue("Character has only 4 levels", PrereqHandler.passes(prereq,
                character, null));
    }

    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        race.setName("Gnoll");

        myClass.setName("My Class");
        myClass.put(StringKey.KEY_NAME, "MY_CLASS");
        myClass.put(FormulaKey.START_SKILL_POINTS, FormulaFactory.getFormulaFor(3));
        Globals.getContext().getReferenceContext().importObject(myClass);
    }
}
