/*
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
 * {@code PREPCLEVELTest} tests that the PREPCLEVEL tag is
 * working correctly.
 */
public class PrePCLevelTest extends AbstractCharacterTestCase
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
        prereq = factory.parse("PREPCLEVEL:MIN=2");
        assertFalse("Character is not 2nd level", PrereqHandler.passes(prereq,
                character, null));

        character.incrementClassLevel(1, myClass, true);

        assertTrue("Character has 2 levels", PrereqHandler.passes(prereq,
                character, null));

        character.incrementClassLevel(1, myClass, true);
        prereq = factory.parse("PREPCLEVEL:MIN=2,MAX=3");
        assertTrue("Character is 2nd or 3rd level", PrereqHandler.passes(prereq,
                character, null));

        character.incrementClassLevel(1, myClass, true);
        assertFalse("Character is not 2nd or 3rd level", PrereqHandler.passes(prereq,
                character, null));

        prereq = factory.parse("!PREPCLEVEL:MIN=2,MAX=3");
        assertTrue("Character is 2nd or 3rd level", PrereqHandler.passes(prereq,
                character, null));

        prereq = factory.parse("!PREPCLEVEL:MIN=4");
        assertFalse("Character is 4 or higher level", PrereqHandler.passes(prereq,
                character, null));

        prereq = factory.parse("!PREPCLEVEL:MAX=3");
        assertTrue("Character is 3rd or higher level", PrereqHandler.passes(prereq,
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

        prereq = factory.parse("PREPCLEVEL:MIN=4");
        assertFalse("Character doesn't have 4 levels", PrereqHandler.passes(
                prereq, character, null));

        character.setRace(race);

        assertFalse("Character has 4 levels", PrereqHandler.passes(prereq,
                character, null));

        prereq = factory.parse("!PREPCLEVEL:MIN=5");
        assertTrue("Character doesn't have 5 or more levels", PrereqHandler.passes(
                prereq, character, null));

        prereq = factory.parse("!PREPCLEVEL:MIN=3");
        assertTrue("Character doesn't have 5 or more levels", PrereqHandler.passes(
                prereq, character, null));

        prereq = factory.parse("!PREPCLEVEL:MAX=3");
        assertFalse("Character doesn't have 3 or more levels", PrereqHandler.passes(
                prereq, character, null));

        prereq = factory.parse("!PREPCLEVEL:MIN=6,MAX=7");
        assertTrue("Character doesn't have between 6 and 7 levels", PrereqHandler.passes(
                prereq, character, null));

        prereq = factory.parse("PREPCLEVEL:MIN=4,MAX=6");
        assertFalse("Character doesn't have 4-6 levels", PrereqHandler.passes(
                prereq, character, null));

        prereq = factory.parse("PREPCLEVEL:MIN=6,MAX=7");
        assertFalse("Character doesn't have 6-7 levels", PrereqHandler.passes(
                prereq, character, null));

        prereq = factory.parse("PREPCLEVEL:MAX=7");
        assertTrue("Character has no more than 5 levels", PrereqHandler.passes(
                prereq, character, null));

        character.incrementClassLevel(4, myClass, true);
        prereq = factory.parse("PREPCLEVEL:MAX=6");
        assertTrue("Character has no more than 6 levels", PrereqHandler.passes(
                prereq, character, null));


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

        prereq = factory.parse("PREPCLEVEL:MIN=6");

        final BonusObj levelBonus = Bonus.newBonus(context, "PCLEVEL|MY_CLASS|2");
        myClass.addToListFor(ListKey.BONUS, levelBonus);
        character.calcActiveBonuses();

        assertFalse("Character has only 4 levels", PrereqHandler.passes(prereq,
                character, null));


        prereq = factory.parse("PREPCLEVEL:MAX=6");
        assertTrue("Character has more than 6 levels", PrereqHandler.passes(prereq,
                character, null));

        prereq = factory.parse("!PREPCLEVEL:MAX=6");
        assertFalse("Character is less than 6 levels", PrereqHandler.passes(prereq,
                character, null));

        prereq = factory.parse("!PREPCLEVEL:MIN=5");
        assertTrue("Character has only 4 levels", PrereqHandler.passes(prereq,
                character, null));

        prereq = factory.parse("PREPCLEVEL:MIN=4,MAX=6");
        assertFalse("Character does not have 4-6 levels", PrereqHandler.passes(prereq,
                character, null));

        prereq = factory.parse("PREPCLEVEL:MIN=6,MAX=8");
        assertFalse("Character does not have 6-8 levels", PrereqHandler.passes(prereq,
                character, null));

        prereq = factory.parse("!PREPCLEVEL:MIN=6,MAX=8");
        assertTrue("Character is not 6-8 levels", PrereqHandler.passes(prereq,
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
