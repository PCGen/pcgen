/*
 * Copyright 2008 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
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

import java.net.URI;
import java.net.URISyntaxException;

import pcgen.AbstractCharacterTestCase;
import pcgen.base.lang.UnreachableError;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Ability;
import pcgen.core.Campaign;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.ShieldProf;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.FeatLoader;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.TestHelper;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code PreShieldProfTest} tests that the PREPROFWITHSHIELD tag is
 * working correctly.
 */
public class PreShieldProfTest extends AbstractCharacterTestCase
{
    /**
     * Test with a simple shield proficiency.
     *
     * @throws Exception the exception
     */
    @Test
    public void testOneOption() throws Exception
    {
        final PlayerCharacter character = getCharacter();

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PREPROFWITHSHIELD:1,Heavy Wooden Shield");
        assertFalse("Character has no proficiencies", PrereqHandler.passes(
                prereq, character, null));

        final Ability martialProf =
                TestHelper.makeAbility("Shield Proficiency (Single)", "FEAT", "General");
        Globals.getContext().unconditionallyProcess(martialProf, "AUTO", "SHIELDPROF|Heavy Wooden Shield");
        Globals.getContext().unconditionallyProcess(martialProf, "AUTO", "SHIELDPROF|Heavy Steel Shield");
        assertTrue(Globals.getContext().getReferenceContext().resolveReferences(null));

        AbstractCharacterTestCase.applyAbility(character, BuildUtilities.getFeatCat(), martialProf, null);

        assertTrue("Character has the Heavy Wooden Shield proficiency.",
                PrereqHandler.passes(prereq, character, null));

        prereq = factory.parse("PREPROFWITHSHIELD:1,Light Wooden Shield");

        assertFalse("Character does not have the Light Wooden Shield proficiency",
                PrereqHandler.passes(prereq, character, null));

        prereq = factory.parse("PREPROFWITHSHIELD:1,Heavy Steel Shield");

        assertTrue("Character has the Heavy Steel Shield proficiency.",
                PrereqHandler.passes(prereq, character, null));
    }


    /**
     * Tests to see if a character has a certain number of shieldprofs from a list.
     *
     * @throws Exception the exception
     */
    @Test
    public void testMultiple() throws Exception
    {
        final PlayerCharacter character = getCharacter();

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PREPROFWITHSHIELD:1,Heavy Wooden Shield,Full Plate");

        assertFalse("Character has no proficiencies", PrereqHandler.passes(
                prereq, character, null));

        final Ability martialProf =
                TestHelper.makeAbility("Shield Proficiency (Single)", "FEAT", "General");
        Globals.getContext().unconditionallyProcess(martialProf, "AUTO", "SHIELDPROF|Heavy Wooden Shield");
        Globals.getContext().unconditionallyProcess(martialProf, "AUTO", "SHIELDPROF|Full Plate");
        assertTrue(Globals.getContext().getReferenceContext().resolveReferences(null));

        AbstractCharacterTestCase.applyAbility(character, BuildUtilities.getFeatCat(), martialProf, null);

        assertTrue("Character has one of Heavy Wooden Shield or Full Plate proficiency",
                PrereqHandler.passes(prereq, character, null));

        prereq = factory.parse("PREPROFWITHSHIELD:2,Heavy Wooden Shield,Full Plate");

        assertTrue("Character has both Heavy Wooden Shield and Full Plate proficiency",
                PrereqHandler.passes(prereq, character, null));

        prereq = factory.parse("PREPROFWITHSHIELD:3,Heavy Wooden Shield,Full Plate,Light Wooden Shield");

        assertFalse("Character has both Heavy Wooden Shield and Full Plate proficiency but not Light Wooden Shield",
                PrereqHandler.passes(prereq, character, null));

    }

    /**
     * Test a PREPROFWITHSHIELD that checks for a number of profs of a certain type.
     *
     * @throws Exception the exception
     */
    @Test
    public void testType() throws Exception
    {
        final PlayerCharacter character = getCharacter();
        Globals.getContext().getReferenceContext().constructCDOMObject(Equipment.class,
                "A Shield");
        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PREPROFWITHSHIELD:1,TYPE.Medium");

        assertFalse("Character has no proficiencies", PrereqHandler.passes(
                prereq, character, null));

        final Ability martialProf =
                TestHelper.makeAbility("Shield Proficiency (Single)", "FEAT", "General");
        Globals.getContext().unconditionallyProcess(martialProf, "AUTO", "SHIELDPROF|SHIELDTYPE=Medium");
        Globals.getContext().getReferenceContext().resolveReferences(null);

        AbstractCharacterTestCase.applyAbility(character, BuildUtilities.getFeatCat(), martialProf, null);

        assertTrue("Character has Medium Shield Proficiency",
                PrereqHandler.passes(prereq, character, null));
    }

    /**
     * Test with negation.
     *
     * @throws Exception the exception
     */
    @Test
    public void testInverse() throws Exception
    {
        final PlayerCharacter character = getCharacter();

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("!PREPROFWITHSHIELD:1,Heavy Steel Shield");

        assertTrue("Character has no proficiencies", PrereqHandler.passes(
                prereq, character, null));

        final Ability martialProf =
                TestHelper.makeAbility("Shield Proficiency (Single)", "FEAT", "General");
        Globals.getContext().unconditionallyProcess(martialProf, "AUTO", "SHIELDPROF|Heavy Wooden Shield");
        Globals.getContext().unconditionallyProcess(martialProf, "AUTO", "SHIELDPROF|Heavy Steel Shield");
        assertTrue(Globals.getContext().getReferenceContext().resolveReferences(null));

        AbstractCharacterTestCase.applyAbility(character, BuildUtilities.getFeatCat(), martialProf, null);

        assertFalse("Character has the Heavy Steel Shield proficiency.",
                PrereqHandler.passes(prereq, character, null));

        prereq = factory.parse("!PREPROFWITHSHIELD:1,Light Wooden Shield");

        assertTrue("Character does not have the Light Wooden Shield proficiency",
                PrereqHandler.passes(prereq, character, null));

        prereq = factory.parse("!PREPROFWITHSHIELD:1,Heavy Wooden Shield");

        assertFalse("Character has the Heavy Wooden Shield proficiency.",
                PrereqHandler.passes(prereq, character, null));

    }

    /**
     * Test the PREPROFWITHSHIELD with shieldprofs added by a AUTO:SHIELDPROF tag
     * This is probably more an integration test than a unit test
     *
     * @throws Exception the exception
     */
    @Test
    public void testShieldProfAddedWithAutoShieldProf() throws Exception
    {
        final PlayerCharacter character = getCharacter();

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PREPROFWITHSHIELD:1,Heavy Steel Shield");

        assertFalse("Character has no proficiencies", PrereqHandler.passes(
                prereq, character, null));

        final Ability martialProf =
                TestHelper.makeAbility("Shield Proficiency (Single)", BuildUtilities.getFeatCat(), "General");
        Globals.getContext().unconditionallyProcess(martialProf, "AUTO", "SHIELDPROF|SHIELDTYPE.Heavy");

        AbstractCharacterTestCase.applyAbility(character, BuildUtilities.getFeatCat(), martialProf, null);

        assertTrue("Character has the Heavy Steel Shield proficiency.",
                PrereqHandler.passes(prereq, character, null));

        prereq = factory.parse("PREPROFWITHSHIELD:1,Heavy Wooden Shield");
        assertTrue("Character has the Heavy Wooden Shield proficiency.",
                PrereqHandler.passes(prereq, character, null));

        prereq = factory.parse("PREPROFWITHSHIELD:1,Light Wooden Shield");
        assertFalse("Character does not have the Light Wooden Shield proficiency.",
                PrereqHandler.passes(prereq, character, null));

        prereq = factory.parse("PREPROFWITHSHIELD:1,TYPE.Heavy");
        assertTrue("Character has heavy shield prof.",
                PrereqHandler.passes(prereq, character, null));

    }

    /**
     * Test PREPROFWITHSHIELD with a feat that has a bonus tag
     *
     * @throws Exception the exception
     */
    @Test
    public void testWithFeatThatGrantsBonus() throws Exception
    {
        final PlayerCharacter character = getCharacter();

        final FeatLoader featLoader = new FeatLoader();

        CampaignSourceEntry cse;
        try
        {
            cse = new CampaignSourceEntry(new Campaign(),
                    new URI("file:/" + getClass().getName() + ".java"));
        } catch (URISyntaxException e)
        {
            throw new UnreachableError(e);
        }

        int baseHp = character.hitPoints();

        Ability bar = new Ability();
        final String barStr =
                "Bar	TYPE:General	DESC:See Text	BONUS:HP|CURRENTMAX|50";
        featLoader.parseLine(Globals.getContext(), bar, barStr, cse);
        addAbility(BuildUtilities.getFeatCat(), bar);

        assertEquals("Character should have 50 bonus hp added.",
                baseHp + 50,
                character.hitPoints()
        );

        final Ability martialProf =
                TestHelper.makeAbility("Shield Proficiency (Single)", "FEAT", "General");
        Globals.getContext().unconditionallyProcess(martialProf, "AUTO", "SHIELDPROF|Full Plate");
        assertTrue(Globals.getContext().getReferenceContext().resolveReferences(null));

        AbstractCharacterTestCase.applyAbility(character, BuildUtilities.getFeatCat(), martialProf, null);

        Ability foo = new Ability();
        final String fooStr =
                "Foo	TYPE:General	DESC:See Text	BONUS:HP|CURRENTMAX|50|PREPROFWITHSHIELD:1,Full Plate";
        featLoader.parseLine(Globals.getContext(), foo, fooStr, cse);
        addAbility(BuildUtilities.getFeatCat(), foo);

        assertEquals("Character has the Full Plate proficiency so the bonus should be added",
                baseHp + 50 + 50,
                character.hitPoints()
        );

    }

    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        Equipment heavySteelShield = new Equipment();
        heavySteelShield.setName("Heavy Steel Shield");
        heavySteelShield.addToListFor(ListKey.TYPE, Type.getConstant("Shield"));
        heavySteelShield.addToListFor(ListKey.TYPE, Type.getConstant("Heavy"));
        Globals.getContext().getReferenceContext().importObject(heavySteelShield);

        Equipment heavyWoodenShield = new Equipment();
        heavyWoodenShield.setName("Heavy Wooden Shield");
        heavyWoodenShield.addToListFor(ListKey.TYPE, Type.getConstant("Shield"));
        heavyWoodenShield.addToListFor(ListKey.TYPE, Type.getConstant("Heavy"));
        Globals.getContext().getReferenceContext().importObject(heavyWoodenShield);

        Equipment lightWoodenShield = new Equipment();
        lightWoodenShield.setName("Light Steel Shield");
        lightWoodenShield.addToListFor(ListKey.TYPE, Type.getConstant("Shield"));
        lightWoodenShield.addToListFor(ListKey.TYPE, Type.getConstant("Light"));
        Globals.getContext().getReferenceContext().importObject(lightWoodenShield);

        Equipment fullPlateEq = new Equipment();
        fullPlateEq.setName("Full Plate");
        fullPlateEq.addToListFor(ListKey.TYPE, Type.getConstant("Shield"));
        fullPlateEq.addToListFor(ListKey.TYPE, Type.getConstant("Heavy"));
        Globals.getContext().getReferenceContext().importObject(fullPlateEq);

        ShieldProf fullPlate = new ShieldProf();
        fullPlate.setName("Full Plate");
        fullPlate.addToListFor(ListKey.TYPE, Type.getConstant("Heavy"));
        Globals.getContext().getReferenceContext().importObject(fullPlate);

        ShieldProf lightWood = new ShieldProf();
        lightWood.setName("Light Wooden Shield");
        lightWood.addToListFor(ListKey.TYPE, Type.getConstant("Light"));
        Globals.getContext().getReferenceContext().importObject(lightWood);

        ShieldProf heavyWood = new ShieldProf();
        heavyWood.setName("Heavy Wooden Shield");
        heavyWood.addToListFor(ListKey.TYPE, Type.getConstant("Heavy"));
        Globals.getContext().getReferenceContext().importObject(heavyWood);

        ShieldProf heavySteel = new ShieldProf();
        heavySteel.setName("Heavy Steel Shield");
        heavySteel.addToListFor(ListKey.TYPE, Type.getConstant("Heavy"));
        Globals.getContext().getReferenceContext().importObject(heavySteel);
    }
}
