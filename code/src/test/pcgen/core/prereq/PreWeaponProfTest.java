/*
 * Copyright 2007 (C) Koen Van Daele <kador@foeffighters.be>
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


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;

import pcgen.AbstractCharacterTestCase;
import pcgen.base.lang.UnreachableError;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Ability;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.WeaponProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.FeatLoader;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.LoadContext;
import pcgen.util.TestHelper;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code PreWeaponProfTest} tests that the PREWEAPONPROF tag is
 * working correctly.
 */
public class PreWeaponProfTest extends AbstractCharacterTestCase
{
    /**
     * Test with a simple weapon proficiency.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testOneOption() throws PersistenceLayerException
    {
        final PlayerCharacter character = getCharacter();

        PCTemplate pct = new PCTemplate();
        LoadContext context = Globals.getContext();
        context.unconditionallyProcess(pct, "AUTO", "WEAPONPROF|Longsword|Dagger");
        assertTrue(context.getReferenceContext().resolveReferences(null));

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PREWEAPONPROF:1,Longsword");

        assertFalse(PrereqHandler.passes(
                prereq, character, null), "Character has no proficiencies");

        character.addTemplate(pct);

        assertTrue(
                PrereqHandler.passes(prereq, character, null), "Character has the Longsword proficiency.");

        prereq = factory.parse("PREWEAPONPROF:1,Longbow");

        assertFalse(
                PrereqHandler.passes(prereq, character, null), "Character does not have the Longbow proficiency");

        prereq = factory.parse("PREWEAPONPROF:1,Dagger");

        assertTrue(
                PrereqHandler.passes(prereq, character, null), "Character has the Dagger proficiency.");
    }


    /**
     * Tests to see if a character has a certain number of weaponprofs from a list.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testMultiple() throws PersistenceLayerException
    {
        final PlayerCharacter character = getCharacter();
        PCTemplate pct = new PCTemplate();
        LoadContext context = Globals.getContext();
        context.unconditionallyProcess(pct, "AUTO", "WEAPONPROF|Longsword|Dagger");
        assertTrue(context.getReferenceContext().resolveReferences(null));

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PREWEAPONPROF:1,Longsword,Dagger");

        assertFalse(PrereqHandler.passes(
                prereq, character, null), "Character has no proficiencies");

        character.addTemplate(pct);

        assertTrue(
                PrereqHandler.passes(prereq, character, null), "Character has one of Longsword or Dagger proficiency");

        prereq = factory.parse("PREWEAPONPROF:2,Longsword,Dagger");

        assertTrue(
                PrereqHandler.passes(prereq, character, null), "Character has both Longsword and Dagger proficiency");

        prereq = factory.parse("PREWEAPONPROF:3,Longsword,Dagger,Longbow");

        assertFalse(
                PrereqHandler.passes(prereq, character, null),
                "Character has both Longsword and Dagger proficiency but not Longbow");

    }

    /**
     * Test a preweaponprof that checks for a number of profs of a certain type.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testType() throws PersistenceLayerException
    {
        final PlayerCharacter character = getCharacter();
        PCTemplate pctls = new PCTemplate();
        PCTemplate pctlb = new PCTemplate();
        LoadContext context = Globals.getContext();
        context.unconditionallyProcess(pctls, "AUTO", "WEAPONPROF|Longsword");
        context.unconditionallyProcess(pctlb, "AUTO", "WEAPONPROF|Longbow");
        assertTrue(context.getReferenceContext().resolveReferences(null));

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PREWEAPONPROF:1,TYPE.Martial");

        assertFalse(PrereqHandler.passes(
                prereq, character, null), "Character has no proficiencies");

        character.addTemplate(pctls);

        assertTrue(
                PrereqHandler.passes(prereq, character, null), "Character has one Martial Weapon Proficiency");

        prereq = factory.parse("PREWEAPONPROF:2,TYPE.Martial");

        assertFalse(PrereqHandler.passes(
                prereq, character, null), "Character only has one proficiency");

        character.addTemplate(pctlb);

        assertTrue(
                PrereqHandler.passes(prereq, character, null), "Character has two Martial Weapon Proficiencies");

    }

    /**
     * Test with negation.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testInverse() throws PersistenceLayerException
    {
        final PlayerCharacter character = getCharacter();
        PCTemplate pct = new PCTemplate();
        LoadContext context = Globals.getContext();
        context.unconditionallyProcess(pct, "AUTO", "WEAPONPROF|Longsword|Dagger");
        assertTrue(context.getReferenceContext().resolveReferences(null));

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("!PREWEAPONPROF:1,Longsword");

        assertTrue(PrereqHandler.passes(
                prereq, character, null), "Character has no proficiencies");

        character.addTemplate(pct);

        assertFalse(
                PrereqHandler.passes(prereq, character, null), "Character has the Longsword proficiency.");

        prereq = factory.parse("!PREWEAPONPROF:1,Longbow");

        assertTrue(
                PrereqHandler.passes(prereq, character, null), "Character does not have the Longbow proficiency");

        prereq = factory.parse("!PREWEAPONPROF:1,Dagger");

        assertFalse(
                PrereqHandler.passes(prereq, character, null), "Character has the Dagger proficiency.");

    }

    /**
     * Test the preweaponprof with weaponprofs added by a AUTO:WEAPONPROF tag
     * This is probably more an integration test than a unit test
     * This test was written to help find the source of bug 1699779.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testWeaponProfAddedWithAutoWeaponProf() throws PersistenceLayerException
    {
        final PlayerCharacter character = getCharacter();

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PREWEAPONPROF:1,Longsword");

        assertFalse(PrereqHandler.passes(
                prereq, character, null), "Character has no proficiencies");

        final Ability martialProf =
                TestHelper.makeAbility("Weapon Proficiency (Martial)", BuildUtilities.getFeatCat(), "General");
        Globals.getContext().unconditionallyProcess(martialProf, "AUTO",
                "WEAPONPROF|TYPE.Martial");
        assertTrue(Globals.getContext().getReferenceContext().resolveReferences(null));

        AbstractCharacterTestCase.applyAbility(character, BuildUtilities.getFeatCat(), martialProf, null);

        assertTrue(
                PrereqHandler.passes(prereq, character, null), "Character has the Longsword proficiency.");

        prereq = factory.parse("PREWEAPONPROF:1,Longbow");
        assertTrue(
                PrereqHandler.passes(prereq, character, null), "Character has the Longbow proficiency.");

        prereq = factory.parse("PREWEAPONPROF:1,Dagger");
        assertFalse(
                PrereqHandler.passes(prereq, character, null), "Character does not have the Dagger proficiency.");

        prereq = factory.parse("PREWEAPONPROF:1,TYPE.Martial");
        assertTrue(
                PrereqHandler.passes(prereq, character, null), "Character has martial weaponprofs.");

    }

    /**
     * Test Preweaponprof with a feat that has a bonus tag
     * This test was written to help find the source of bug 1699779.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testWithFeatThatGrantsBonus() throws PersistenceLayerException
    {
        final PlayerCharacter character = getCharacter();
        PCTemplate pctls = new PCTemplate();
        LoadContext context = Globals.getContext();
        context.unconditionallyProcess(pctls, "AUTO", "WEAPONPROF|Longsword");
        assertTrue(context.getReferenceContext().resolveReferences(null));

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

        assertEquals(
                baseHp + 50,
                character.hitPoints(),
                "Character should have 50 bonus hp added."
        );

        character.addTemplate(pctls);

        Ability foo = new Ability();
        final String fooStr =
                "Foo	TYPE:General	DESC:See Text	BONUS:HP|CURRENTMAX|50|PREWEAPONPROF:1,Longsword";
        featLoader.parseLine(Globals.getContext(), foo, fooStr, cse);
        addAbility(BuildUtilities.getFeatCat(), foo);

        assertEquals(
                baseHp + 50 + 50,
                character.hitPoints(),
                "Character has the longsword proficiency so the bonus should be added"
        );

    }

    @BeforeEach
    @Override
    public void setUp() throws Exception

    {
        super.setUp();

        WeaponProf Longsword = new WeaponProf();
        Longsword.setName("Longsword");
        Longsword.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
        Globals.getContext().getReferenceContext().importObject(Longsword);

        WeaponProf Longbow = new WeaponProf();
        Longbow.setName("Longbow");
        Longbow.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
        Globals.getContext().getReferenceContext().importObject(Longbow);

        WeaponProf Dagger = new WeaponProf();
        Dagger.setName("Dagger");
        Dagger.addToListFor(ListKey.TYPE, Type.SIMPLE);
        Globals.getContext().getReferenceContext().importObject(Dagger);

    }
}
