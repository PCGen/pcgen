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
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code PreCSkillTest} tests that the PRECSKILL tag is
 * working correctly.
 */
public class PreCSkillTest extends AbstractCharacterTestCase
{
    PCClass myClass = new PCClass();
    private Skill spy1;
    private Skill spy2;
    private Skill spy3;
    private Skill spot;

    /**
     * Test that CSkill works.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testCSkill() throws PersistenceLayerException
    {
        final PlayerCharacter character = getCharacter();
        character.incrementClassLevel(1, myClass, true);

        myClass = character.getClassKeyed("My Class");

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PRECSKILL:1,Spot,Listen");

        assertFalse("Character has no class skills", PrereqHandler.passes(
                prereq, character, null));

        character.addLocalCost(myClass, spot, SkillCost.CLASS, myClass);
        character.setDirty(true); //Need to throw out the cache

        assertTrue("Character has spot class skills", PrereqHandler.passes(
                prereq, character, null));

        character.addLocalCost(myClass, spy1, SkillCost.CLASS, myClass);
        character.setDirty(true); //Need to throw out the cache

        assertTrue("Character has spot class skills", PrereqHandler.passes(
                prereq, character, null));

        prereq = factory.parse("PRECSKILL:2,TYPE.Spy");

        assertFalse("Character has only one Spy Skill", PrereqHandler.passes(
                prereq, character, null));

        character.addLocalCost(myClass, spy2, SkillCost.CLASS, myClass);
        character.setDirty(true); //Need to throw out the cache

        assertTrue("Character has 2 Spy class skills", PrereqHandler.passes(
                prereq, character, null));

        prereq = factory.parse("PRECSKILL:3,Spot,TYPE.Spy");

        assertTrue("Character has 2 Spy and Spot class skills", PrereqHandler
                .passes(prereq, character, null));

        prereq = factory.parse("PRECSKILL:3,Listen,TYPE.Spy");

        assertFalse("Character has only 2 Spy Skills", PrereqHandler.passes(
                prereq, character, null));

        character.addLocalCost(myClass, spy3, SkillCost.CLASS, myClass);
        character.setDirty(true); //Need to throw out the cache

        prereq = factory.parse("PRECSKILL:3,Listen,TYPE.Spy");

        assertTrue("Character has 3 Spy Skills", PrereqHandler.passes(prereq,
                character, null));
    }

    @Test
    public void testCSkillServesAs() throws Exception
    {
        final PlayerCharacter character = getCharacter();
        character.incrementClassLevel(1, myClass, true);

        myClass = character.getClassKeyed("My Class");

        Prerequisite prereq;
        final PreParserFactory factory = PreParserFactory.getInstance();

        Skill bar = new Skill();
        bar.setName("Bar");
        bar.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
        Globals.getContext().getReferenceContext().importObject(bar);

        Skill foo = new Skill();
        foo.setName("Foo");
        foo.addToListFor(ListKey.SERVES_AS_SKILL, CDOMDirectSingleRef.getRef(bar));
        foo.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
        Globals.getContext().getReferenceContext().importObject(foo);

        Skill baz = new Skill();
        baz.setName("Baz");
        baz.addToListFor(ListKey.TYPE, Type.getConstant("Baz"));
        Globals.getContext().getReferenceContext().importObject(baz);

        Skill fee = new Skill();
        fee.setName("Fee");
        fee.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
        Globals.getContext().getReferenceContext().importObject(fee);

        character.addLocalCost(myClass, fee, SkillCost.CLASS, myClass);
        character.addLocalCost(myClass, foo, SkillCost.CLASS, myClass);
        prereq = factory.parse("PRECSKILL:1,Bar");
        assertTrue("Character has 1 Listen Skill", PrereqHandler.passes(prereq,
                character, null));


        prereq = factory.parse("PRECSKILL:2,Bar,Fee");
        assertTrue("Character has a Bar Skill and a Fee Skill", PrereqHandler.passes(prereq,
                character, null));

        prereq = factory.parse("PRECSKILL:2,Baz,Fee");
        assertFalse("Character does not have both Baz and Fee Skills", PrereqHandler.passes(prereq,
                character, null));


        prereq = factory.parse("PRECSKILL:1,TYPE=Bar");
        assertTrue("Character has 1 Bar Type Skill", PrereqHandler.passes(prereq,
                character, null));


        prereq = factory.parse("PRECSKILL:2,TYPE=Bar");
        assertTrue("Character has 2 Bar Type Skills", PrereqHandler.passes(prereq,
                character, null));

        prereq = factory.parse("PRECSKILL:3,TYPE=Bar");
        assertFalse("Character has less than 3 Bar Type Skills", PrereqHandler.passes(prereq,
                character, null));


    }

    @BeforeEach
    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        spot = new Skill();
        spot.setName("Spot");
        Globals.getContext().getReferenceContext().importObject(spot);

        Skill listen = new Skill();
        listen.setName("Listen");
        Globals.getContext().getReferenceContext().importObject(listen);

        spy1 = new Skill();
        spy1.setName("Spy 1");
        spy1.addToListFor(ListKey.TYPE, Type.getConstant("Spy"));
        Globals.getContext().getReferenceContext().importObject(spy1);

        spy2 = new Skill();
        spy2.setName("Spy 2");
        spy2.addToListFor(ListKey.TYPE, Type.getConstant("Spy"));
        Globals.getContext().getReferenceContext().importObject(spy2);

        spy3 = new Skill();
        spy3.setName("Spy 3");
        spy3.addToListFor(ListKey.TYPE, Type.getConstant("Spy"));
        Globals.getContext().getReferenceContext().importObject(spy3);

        Skill spy4 = new Skill();
        spy4.setName("Spy 4");
        spy4.addToListFor(ListKey.TYPE, Type.getConstant("Spy"));
        Globals.getContext().getReferenceContext().importObject(spy4);

        myClass.setName("My Class");
        myClass.put(FormulaKey.START_SKILL_POINTS, FormulaFactory.getFormulaFor(3));
        Globals.getContext().getReferenceContext().importObject(myClass);
    }
}
