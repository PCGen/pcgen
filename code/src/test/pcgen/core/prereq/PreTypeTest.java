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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import plugin.pretokens.test.PreTypeTester;

import org.junit.jupiter.api.Test;

public class PreTypeTest extends AbstractCharacterTestCase
{
    /**
     * Ensure a character with a template correctly passes
     * PRETEMPLATE
     */
    @Test
    public void test996803_1()
    {
        final PlayerCharacter character = getCharacter();

        final PCTemplate template = new PCTemplate();
        template.setName("Half-Celestial");
        template.addToListFor(ListKey.TYPE, Type.getConstant("Outsider"));
        character.addTemplate(template);

        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("type");
        prereq.setKey("Outsider");
        prereq.setOperand("1");
        prereq.setOperator(PrerequisiteOperator.EQ);

        final boolean passes = PrereqHandler.passes(prereq, character, null);
        assertTrue(passes);
    }

    /**
     * Ensure a character with a template correctly passes
     * PRETEMPLATE
     */
    @Test
    public void test996803_2()
    {
        final PlayerCharacter character = getCharacter();

        final PCTemplate template = new PCTemplate();
        template.setName("Half-Celestial");
        template.addToListFor(ListKey.TYPE, Type.getConstant("Outsider"));
        character.addTemplate(template);

        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("type");
        prereq.setKey("Outsider");
        prereq.setOperand("1");
        prereq.setOperator(PrerequisiteOperator.NEQ);

        final boolean passes = PrereqHandler.passes(prereq, character, null);
        assertFalse(passes);
    }

    @Test
    public void test996803_3()
    {
        final PlayerCharacter character = getCharacter();

        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("type");
        prereq.setKey("Outsider");
        prereq.setOperand("1");
        prereq.setOperator(PrerequisiteOperator.EQ);

        final boolean passes = PrereqHandler.passes(prereq, character, null);
        assertFalse(passes);
    }

    @Test
    public void test996803_4()
    {
        final PlayerCharacter character = getCharacter();

        final PCTemplate template = new PCTemplate();
        template.setName("Fiendish");
        template.addToListFor(ListKey.TYPE, Type.getConstant("Magical-Beast"));
        character.addTemplate(template);

        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("type");
        prereq.setKey("Outsider");
        prereq.setOperand("1");
        prereq.setOperator(PrerequisiteOperator.NEQ);

        final boolean passes = PrereqHandler.passes(prereq, character, null);
        assertTrue(passes);
    }

    @Test
    public void testKindHandled()
    {
        final PreTypeTester preType = new PreTypeTester();

        assertEquals("TYPE", preType.kindHandled());
    }

}
