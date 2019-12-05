/*
 * Copyright 2004 (C) Chris Ward <frugal@purplewombat.co.uk>
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
import pcgen.core.PlayerCharacter;

import org.junit.jupiter.api.Test;

/**
 * The Class {@code PreStatTest} checks that PreStatTester is working correctly.
 */
class PreStatTest extends AbstractCharacterTestCase
{

    /**
     * Ensure a PRESTAT for a positive value works correctly.
     */
    @Test
    public void testPrePositive()
    {
        final PlayerCharacter character = getCharacter();

        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("stat");
        prereq.setKey("STR");
        prereq.setOperator(PrerequisiteOperator.GTEQ);
        prereq.setOperand("1");

        character.setStat(str, -1);
        assertFalse("prestat:1,str=1 for str of -1", PrereqHandler.passes(prereq, character, null));
        character.setStat(str, 0);
        assertFalse("prestat:1,str=1 for str of 0", PrereqHandler.passes(prereq, character, null));
        character.setStat(str, 1);
        assertTrue("prestat:1,str=1 for str of 1", PrereqHandler.passes(prereq, character, null));
        character.setStat(str, 2);
        assertTrue("prestat:1,str=1 for str of 2", PrereqHandler.passes(prereq, character, null));
    }

    /**
     * Ensure a PRESTAT for zero works correctly.
     */
    @Test
    public void testPreZero()
    {
        final PlayerCharacter character = getCharacter();

        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("stat");
        prereq.setKey("STR");
        prereq.setOperator(PrerequisiteOperator.GTEQ);
        prereq.setOperand("0");

        character.setStat(str, -1);
        assertFalse("prestat:1,str=1 for str of -1", PrereqHandler.passes(prereq, character, null));
        character.setStat(str, 0);
        assertTrue("prestat:1,str=0 for str of 0", PrereqHandler.passes(prereq, character, null));
        character.setStat(str, 1);
        assertTrue("prestat:1,str=0 for str of 1", PrereqHandler.passes(prereq, character, null));
        character.setStat(str, 2);
        assertTrue("prestat:1,str=0 for str of 2", PrereqHandler.passes(prereq, character, null));
    }

    /**
     * Ensure a PRESTAT for a negative value works correctly.
     */
    @Test
    public void testPreNegative()
    {
        final PlayerCharacter character = getCharacter();

        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("stat");
        prereq.setKey("STR");
        prereq.setOperator(PrerequisiteOperator.GTEQ);
        prereq.setOperand("-1");

        character.setStat(str, -2);
        assertFalse("prestat:1,str=-1 for str of -2", PrereqHandler.passes(prereq, character, null));
        character.setStat(str, 0);
        assertTrue("prestat:1,str=-1 for str of 0", PrereqHandler.passes(prereq, character, null));
        character.setStat(str, 1);
        assertTrue("prestat:1,str=-1 for str of 1", PrereqHandler.passes(prereq, character, null));
        character.setStat(str, -1);
        assertTrue("prestat:1,str=-1 for str of -1", PrereqHandler.passes(prereq, character, null));
    }

}
