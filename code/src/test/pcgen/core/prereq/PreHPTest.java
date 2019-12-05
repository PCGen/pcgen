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
import pcgen.cdom.inst.PCClassLevel;
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
 * {@code PreHPTest} tests that the PREHP tag is
 * working correctly.
 */
public class PreHPTest extends AbstractCharacterTestCase
{
    PCClass myClass = new PCClass();

    /**
     * Test the PREHP code.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testHP() throws PersistenceLayerException
    {
        final PlayerCharacter character = getCharacter();
        LoadContext context = Globals.getContext();

        character.incrementClassLevel(1, myClass, true);
        myClass = character.getClassList().get(0);
        PCClassLevel pcl = character.getActiveClassLevel(myClass, 1);
        character.setHP(pcl, 4);

        character.calcActiveBonuses();

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PREHP:4");

        assertTrue("Character should have 4 hp", PrereqHandler.passes(prereq,
                character, null));

        prereq = factory.parse("PREHP:5");

        assertFalse("Character should have less than 5 hp", PrereqHandler
                .passes(prereq, character, null));

        final BonusObj hpBonus = Bonus.newBonus(context, "HP|CURRENTMAX|1");
        myClass.addToListFor(ListKey.BONUS, hpBonus);
        character.calcActiveBonuses();

        assertTrue("Character should have 5 hp", PrereqHandler.passes(prereq,
                character, null));
    }

    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        myClass.setName("My Class");
        myClass.put(FormulaKey.START_SKILL_POINTS, FormulaFactory.getFormulaFor(3));
        Globals.getContext().getReferenceContext().importObject(myClass);
    }
}
