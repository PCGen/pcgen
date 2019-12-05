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
 * {@code PreAttTest} tests that the PREATT tag is
 * working correctly.
 */
public class PreAttTest extends AbstractCharacterTestCase
{
    PCClass myClass = new PCClass();

    /**
     * Test the PREATT code.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testAtt() throws PersistenceLayerException
    {
        final PlayerCharacter character = getCharacter();
        LoadContext context = Globals.getContext();

        character.incrementClassLevel(1, myClass, true);

        character.calcActiveBonuses();

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PREATT:6");

        assertTrue("Character's BAB should be 6", PrereqHandler.passes(prereq,
                character, null));

        prereq = factory.parse("PREATT:7");

        assertFalse("Character's BAB should be less than 7", PrereqHandler
                .passes(prereq, character, null));

        final BonusObj toHitBonus = Bonus.newBonus(context, "COMBAT|TOHIT|1");
        myClass.getOriginalClassLevel(1).addToListFor(ListKey.BONUS, toHitBonus);
        character.calcActiveBonuses();

        assertFalse("Character's BAB should be less than 7", PrereqHandler
                .passes(prereq, character, null));
    }

    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        LoadContext context = Globals.getContext();

        myClass.setName("My Class");
        myClass.put(FormulaKey.START_SKILL_POINTS, FormulaFactory.getFormulaFor(3));
        final BonusObj babClassBonus = Bonus.newBonus(context, "COMBAT|BASEAB|CL+5");
        myClass.getOriginalClassLevel(1).addToListFor(ListKey.BONUS, babClassBonus);
        Globals.getContext().getReferenceContext().importObject(myClass);
    }
}
