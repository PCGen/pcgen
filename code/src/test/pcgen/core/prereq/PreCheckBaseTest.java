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
import pcgen.core.Globals;
import pcgen.core.PCCheck;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.LoadContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code PreCheckBaseTest} tests that the PRECHECKBASE tag is
 * working correctly.
 */
public class PreCheckBaseTest extends AbstractCharacterTestCase
{
    PCClass myClass = new PCClass();

    /**
     * Test that Base Checks work.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testBase() throws PersistenceLayerException
    {
        final PlayerCharacter character = getCharacter();
        character.incrementClassLevel(1, myClass, true);

        character.calcActiveBonuses();

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PRECHECKBASE:1,Fortitude=0");

        assertTrue("Character's Fort save should be 0", PrereqHandler.passes(
                prereq, character, null));

        prereq = factory.parse("PRECHECKBASE:1,Will=2");

        assertTrue("Character's Will save should be 2", PrereqHandler.passes(
                prereq, character, null));

        prereq = factory.parse("PRECHECKBASE:1,Fortitude=1,Will=2");
        assertTrue("Character's Will save should be 2", PrereqHandler.passes(
                prereq, character, null));
        prereq = factory.parse("PRECHECKBASE:2,Fortitude=1,Will=2");
        assertFalse("Character's Fort save not 1", PrereqHandler.passes(prereq,
                character, null));
    }

    @Test
    public void testBonus() throws Exception
    {
        final PlayerCharacter character = getCharacter();
        LoadContext context = Globals.getContext();

        final BonusObj fortBonus = Bonus.newBonus(context, "SAVE|Fortitude|1");
        myClass.getOriginalClassLevel(1).addToListFor(ListKey.BONUS, fortBonus);

        character.incrementClassLevel(1, myClass, true);

        character.calcActiveBonuses();

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PRECHECKBASE:1,Fortitude=1");

        assertFalse("Character's base Fort save should be 0", PrereqHandler
                .passes(prereq, character, null));

        prereq = factory.parse("PRECHECKBASE:1,Will=2");

        assertTrue("Character's Will save should be 2", PrereqHandler.passes(
                prereq, character, null));

        prereq = factory.parse("PRECHECKBASE:1,Fortitude=1,Will=3");
        assertFalse("Character's Will save should be 2", PrereqHandler.passes(
                prereq, character, null));
        prereq = factory.parse("PRECHECKBASE:2,Fortitude=1,Will=2");
        assertFalse("Character's base Fort save not 1", PrereqHandler.passes(
                prereq, character, null));
    }

    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        LoadContext context = Globals.getContext();

        PCCheck obj = new PCCheck();
        obj.setName("Fortitude");
        Globals.getContext().getReferenceContext().importObject(obj);

        obj = new PCCheck();
        obj.setName("Reflex");
        Globals.getContext().getReferenceContext().importObject(obj);

        obj = new PCCheck();
        obj.setName("Will");
        Globals.getContext().getReferenceContext().importObject(obj);

        myClass.setName("My Class");
        myClass.put(FormulaKey.START_SKILL_POINTS, FormulaFactory.getFormulaFor(3));
        final BonusObj fortRefBonus =
                Bonus.newBonus(context, "SAVE|BASE.Fortitude,BASE.Reflex|CL/3");
        myClass.getOriginalClassLevel(1).addToListFor(ListKey.BONUS, fortRefBonus);
        final BonusObj willBonus = Bonus.newBonus(context, "SAVE|BASE.Will|CL/2+2");
        myClass.getOriginalClassLevel(1).addToListFor(ListKey.BONUS, willBonus);
        Globals.getContext().getReferenceContext().importObject(myClass);
    }
}
