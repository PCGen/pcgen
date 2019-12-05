/*
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
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
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.spell.Spell;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.LoadContext;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PreSpellSubSchoolTest extends AbstractCharacterTestCase
{

    private PCClass wiz;
    private PCClass cle;

    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        LoadContext context = Globals.getContext();
        wiz = context.getReferenceContext().constructCDOMObject(PCClass.class, "Wizard");
        BuildUtilities.setFact(wiz, "SpellType", "Arcane");
        context.unconditionallyProcess(wiz, "KNOWNSPELLS", "LEVEL=1|LEVEL=2");
        context.unconditionallyProcess(wiz.getOriginalClassLevel(1), "CAST", "1,1");
        context.unconditionallyProcess(wiz.getOriginalClassLevel(2), "CAST", "2,2,1");
        cle = context.getReferenceContext().constructCDOMObject(PCClass.class, "Cleric");
        BuildUtilities.setFact(cle, "SpellType", "Divine");
        context.unconditionallyProcess(cle, "KNOWNSPELLS", "LEVEL=1|LEVEL=2");
        context.unconditionallyProcess(cle.getOriginalClassLevel(1), "CAST", "1,1");
        context.unconditionallyProcess(cle.getOriginalClassLevel(2), "CAST", "1,1,1");

        Spell fireball = new Spell();
        fireball.setName("Fireball");
        context.getReferenceContext().importObject(fireball);
        context.unconditionallyProcess(fireball, "CLASSES", "Wizard=2");
        context.unconditionallyProcess(fireball, "SUBSCHOOL", "Fire");

        Spell lightning = new Spell();
        lightning.setName("Lightning Bolt");
        context.getReferenceContext().importObject(lightning);
        context.unconditionallyProcess(lightning, "CLASSES", "Wizard=2");
        context.unconditionallyProcess(lightning, "SUBSCHOOL", "Useful");

        Spell burning = new Spell();
        burning.setName("Burning Hands");
        context.getReferenceContext().importObject(burning);
        context.unconditionallyProcess(burning, "CLASSES", "Wizard=1");
        context.unconditionallyProcess(burning, "SUBSCHOOL", "Fire");

        Spell heal = new Spell();
        heal.setName("Heal");
        context.getReferenceContext().importObject(heal);
        context.unconditionallyProcess(heal, "CLASSES", "Cleric=2");
        context.unconditionallyProcess(heal, "SUBSCHOOL", "Useful");

        Spell cure = new Spell();
        cure.setName("Cure Light Wounds");
        context.getReferenceContext().importObject(cure);
        context.unconditionallyProcess(cure, "CLASSES", "Cleric=1");
        context.unconditionallyProcess(cure, "SUBSCHOOL", "Useful");

        finishLoad();
    }

    @Test
    public void testSimpleSUBSCHOOL()
    {
        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("SpellSCHOOLSUB");
        prereq.setKey("Fire");
        prereq.setOperator(PrerequisiteOperator.GTEQ);
        prereq.setOperand("2");

        final PlayerCharacter character = getCharacter();
        boolean passes = PrereqHandler.passes(prereq, character, null);
        assertFalse(passes);
        character.incrementClassLevel(1, wiz);
        passes = PrereqHandler.passes(prereq, character, null);
        assertFalse(passes);
        character.incrementClassLevel(1, wiz);
        passes = PrereqHandler.passes(prereq, character, null);
        assertTrue(passes);
    }

    @Test
    public void testTwoClassSUBSCHOOL() throws Exception
    {
        final PlayerCharacter character = getCharacter();

        final PreParserFactory factory = PreParserFactory.getInstance();
        Prerequisite prereq = factory
                .parse("PRESPELLSCHOOLSUB:3,Fire=2,Useful=2");

        assertFalse(PrereqHandler.passes(prereq, character, null));
        character.incrementClassLevel(1, wiz);
        boolean passes = PrereqHandler.passes(prereq, character, null);
        assertFalse(passes);
        character.incrementClassLevel(1, wiz);
        passes = PrereqHandler.passes(prereq, character, null);
        assertFalse(passes);
        character.incrementClassLevel(1, cle);
        passes = PrereqHandler.passes(prereq, character, null);
        assertFalse(passes);
        character.incrementClassLevel(1, cle);
        passes = PrereqHandler.passes(prereq, character, null);
        assertTrue(passes);
    }

    @Test
    public void testNotSimpleSUBSCHOOL()
    {
        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("SpellSCHOOLSUB");
        prereq.setKey("Fire");
        prereq.setOperator(PrerequisiteOperator.LT);
        prereq.setOperand("2");

        final PlayerCharacter character = getCharacter();
        boolean passes = PrereqHandler.passes(prereq, character, null);
        assertTrue(passes);
        character.incrementClassLevel(1, wiz);
        passes = PrereqHandler.passes(prereq, character, null);
        assertTrue(passes);
        character.incrementClassLevel(1, wiz);
        passes = PrereqHandler.passes(prereq, character, null);
        assertFalse(passes);
    }

    @Test
    public void testNotTwoClassSUBSCHOOL() throws Exception
    {
        final PlayerCharacter character = getCharacter();

        final PreParserFactory factory = PreParserFactory.getInstance();
        Prerequisite prereq = factory
                .parse("!PRESPELLSCHOOLSUB:3,Fire=2,Useful=2");

        assertTrue(PrereqHandler.passes(prereq, character, null));
        character.incrementClassLevel(1, wiz);
        boolean passes = PrereqHandler.passes(prereq, character, null);
        assertTrue(passes);
        character.incrementClassLevel(1, wiz);
        passes = PrereqHandler.passes(prereq, character, null);
        assertTrue(passes);
        character.incrementClassLevel(1, cle);
        passes = PrereqHandler.passes(prereq, character, null);
        assertTrue(passes);
        character.incrementClassLevel(1, cle);
        passes = PrereqHandler.passes(prereq, character, null);
        assertFalse(passes);
    }

    @Override
    protected void defaultSetupEnd()
    {
        //Nothing, we will trigger ourselves
    }
}
