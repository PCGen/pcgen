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
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.cdom.enumeration.RaceType;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;

import org.junit.jupiter.api.Test;


@SuppressWarnings("nls")
public class PreRaceTest extends AbstractCharacterTestCase
{
    /**
     * Test to ensure that we return false when races don't match.
     */
    @Test
    public void testFail()
    {
        final PlayerCharacter character = getCharacter();

        final Race race = new Race();
        race.setName("Human");
        Globals.getContext().getReferenceContext().importObject(race);

        character.setRace(race);

        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("race");
        prereq.setKey("Orc");
        prereq.setOperator(PrerequisiteOperator.EQ);

        final boolean passes = PrereqHandler.passes(prereq, character, null);
        assertFalse(passes);
    }

    /**
     * Test to make sure we return false when race is equal but NOT is specified.
     */
    @Test
    public void testNeqFails()
    {
        final PlayerCharacter character = getCharacter();

        final Race race = new Race();
        race.setName("Human");
        Globals.getContext().getReferenceContext().importObject(race);

        character.setRace(race);

        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("race");
        prereq.setKey("Human");
        prereq.setOperator(PrerequisiteOperator.NEQ);

        final boolean passes = PrereqHandler.passes(prereq, character, null);
        assertFalse(passes);
    }

    /**
     * Test to make sure that NOT returns true if races don't match.
     */
    @Test
    public void testNeqPasses()
    {
        final PlayerCharacter character = getCharacter();

        final Race race = new Race();
        race.setName("Human");
        Globals.getContext().getReferenceContext().importObject(race);

        character.setRace(race);

        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("race");
        prereq.setKey("Orc");
        prereq.setOperator(PrerequisiteOperator.NEQ);

        final boolean passes = PrereqHandler.passes(prereq, character, null);
        assertTrue(passes);
    }

    /**
     * Test to make sure that we return true when races are equal.
     */
    @Test
    public void testPass()
    {
        final PlayerCharacter character = getCharacter();

        final Race race = new Race();
        race.setName("Human");
        Globals.getContext().getReferenceContext().importObject(race);

        character.setRace(race);

        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("race");
        prereq.setKey("human");
        prereq.setOperator(PrerequisiteOperator.EQ);

        final boolean passes = PrereqHandler.passes(prereq, character, null);
        assertTrue(passes);
    }

    /**
     * Test to make sure that we return true when races are equal using ServesAs.
     */
    @Test
    public void testPassServesAsName()
    {
        final PlayerCharacter character = getCharacter();

        final Race race = new Race();
        race.setName("Human");
        Globals.getContext().getReferenceContext().importObject(race);

        final Race fake = new Race();
        fake.setName("NotHuman");
        Globals.getContext().getReferenceContext().importObject(fake);

        fake.addToListFor(ListKey.SERVES_AS_RACE, CDOMDirectSingleRef.getRef(race));
        character.setRace(fake);


        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("race");
        prereq.setKey(race.getKeyName());
        prereq.setOperator(PrerequisiteOperator.EQ);

        final boolean passes = PrereqHandler.passes(prereq, character, null);
        assertTrue("Expected prereq " + prereq + " to pass for race " + fake
                + " with SERVESAS", passes);
    }

    @Test
    public void testRaceTypeEq()
    {
        final PlayerCharacter character = getCharacter();

        final Race race = new Race();
        race.setName("Human");
        race.put(ObjectKey.RACETYPE, RaceType.getConstant("Humanoid"));
        Globals.getContext().getReferenceContext().importObject(race);
        character.setRace(race);

        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("race");
        prereq.setKey("RACETYPE=Humanoid");
        prereq.setOperator(PrerequisiteOperator.EQ);

        final boolean passes = PrereqHandler.passes(prereq, character, null);
        assertTrue(prereq + " should pass", passes);
    }

    @Test
    public void testRaceTypeNeq()
    {
        final PlayerCharacter character = getCharacter();

        final Race race = new Race();
        race.setName("Human");
        race.put(ObjectKey.RACETYPE, RaceType.getConstant("Humanoid"));
        Globals.getContext().getReferenceContext().importObject(race);
        character.setRace(race);

        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("race");
        prereq.setKey("RACETYPE=Dragon");
        prereq.setOperator(PrerequisiteOperator.LT);

        boolean passes = PrereqHandler.passes(prereq, character, null);
        assertTrue(prereq + " should pass", passes);

        prereq.setKey("RACETYPE=Humanoid");
        passes = PrereqHandler.passes(prereq, character, null);
        assertFalse(prereq + " should not pass", passes);
    }

    /**
     * Test to make sure that we return true when races RACESUBTYPE are equal using ServesAs.
     */
    @Test
    public void testPassServesAsRaceSubType()
    {
        final PlayerCharacter character = getCharacter();

        final Race race = new Race();
        race.setName("Human");
        race.addToListFor(ListKey.TYPE, Type.getConstant("Outsider"));
        race.addToListFor(ListKey.RACESUBTYPE, RaceSubType.getConstant("aquatic"));
        race.addToListFor(ListKey.RACESUBTYPE, RaceSubType.getConstant("foo"));
        Globals.getContext().getReferenceContext().importObject(race);

        final Race fake = new Race();
        fake.setName("NotHuman");
        fake.addToListFor(ListKey.TYPE, Type.getConstant("Humanoid"));
        fake.addToListFor(ListKey.RACESUBTYPE, RaceSubType.getConstant("desert"));
        fake.addToListFor(ListKey.RACESUBTYPE, RaceSubType.getConstant("none"));
        Globals.getContext().getReferenceContext().importObject(fake);
        fake.addToListFor(ListKey.SERVES_AS_RACE, CDOMDirectSingleRef.getRef(race));

        final Race gnome = new Race();
        gnome.setName("Gnome");
        gnome.addToListFor(ListKey.RACESUBTYPE, RaceSubType.getConstant("SpikyHair"));
        Globals.getContext().getReferenceContext().importObject(gnome);

        final Race bugbear = new Race();
        bugbear.setName("Bugbear");
        bugbear.addToListFor(ListKey.RACESUBTYPE, RaceSubType.getConstant("SpikyClub"));
        Globals.getContext().getReferenceContext().importObject(bugbear);
        bugbear.addToListFor(ListKey.SERVES_AS_RACE, CDOMDirectSingleRef.getRef(gnome));

        character.setRace(fake);

        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("race");
        prereq.setKey("RACESUBTYPE=aquatic");
        prereq.setOperator(PrerequisiteOperator.EQ);

        boolean passes = PrereqHandler.passes(prereq, character, null);
        assertTrue(passes);

        final Prerequisite prereq2 = new Prerequisite();
        prereq2.setKind("race");
        prereq2.setKey("RACESUBTYPE=foo");
        prereq2.setOperator(PrerequisiteOperator.EQ);

        passes = PrereqHandler.passes(prereq2, character, null);
        assertTrue(passes);

        prereq.setKey("RACESUBTYPE=SpikyHair");
        passes = PrereqHandler.passes(prereq, character, null);
        assertFalse("Prereq " + prereq
                + " should not be passed by character without a "
                + "race or servesas link.", passes);

    }

    /**
     * Test to make sure that we return true when races RACETYPE are equal using ServesAs.
     */
    @Test
    public void testPassServesAsRaceType()
    {
        final PlayerCharacter character = getCharacter();

        final Race race = new Race();
        race.setName("Human");
        race.put(ObjectKey.RACETYPE, RaceType.getConstant("Outsider"));
        race.addToListFor(ListKey.TYPE, Type.getConstant("Outsider"));
        Globals.getContext().getReferenceContext().importObject(race);

        final Race fake = new Race();
        fake.setName("NotHuman");
        fake.put(ObjectKey.RACETYPE, RaceType.getConstant("Humanoid"));
        fake.addToListFor(ListKey.TYPE, Type.getConstant("Humanoid"));
        Globals.getContext().getReferenceContext().importObject(fake);
        fake.addToListFor(ListKey.SERVES_AS_RACE, CDOMDirectSingleRef.getRef(race));

        final Race gnome = new Race();
        gnome.setName("Gnome");
        gnome.put(ObjectKey.RACETYPE, RaceType.getConstant("Smaller"));
        Globals.getContext().getReferenceContext().importObject(gnome);

        character.setRace(fake);


        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("race");
        prereq.setKey("RACETYPE=Outsider");
        prereq.setOperator(PrerequisiteOperator.EQ);

        boolean passes = PrereqHandler.passes(prereq, character, null);
        assertTrue("Prereq " + prereq + " should pass due to SERVESAS", passes);

        prereq.setKey("RACETYPE=Smaller");
        passes = PrereqHandler.passes(prereq, character, null);
        assertFalse("Prereq " + prereq
                + " should not be passed by character without a "
                + "race or servesas link.", passes);
    }

    /**
     * Test to make sure that we return true when races TYPE are equal.
     */
    @Test
    public void testPassServesAsType()
    {
        final PlayerCharacter character = getCharacter();

        final Race race = new Race();
        race.setName("Human");
        race.addToListFor(ListKey.TYPE, Type.getConstant("Outsider"));
        Globals.getContext().getReferenceContext().importObject(race);

        final Race fake = new Race();
        fake.setName("NotHuman");
        fake.addToListFor(ListKey.TYPE, Type.getConstant("Humanoid"));
        Globals.getContext().getReferenceContext().importObject(fake);
        fake.addToListFor(ListKey.SERVES_AS_RACE, CDOMDirectSingleRef.getRef(race));

        final Race gnome = new Race();
        gnome.setName("Gnome");
        gnome.addToListFor(ListKey.TYPE, Type.getConstant("Smaller"));
        Globals.getContext().getReferenceContext().importObject(gnome);

        character.setRace(fake);


        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("race");
        prereq.setKey("TYPE=Outsider");
        prereq.setOperator(PrerequisiteOperator.EQ);

        boolean passes = PrereqHandler.passes(prereq, character, null);
        assertTrue("Prereq " + prereq + " should pass due to SERVESAS", passes);

        prereq.setKey("TYPE=Smaller");
        passes = PrereqHandler.passes(prereq, character, null);
        assertFalse("Prereq " + prereq
                + " should not be passed by character without a "
                + "race or servesas link.", passes);
    }

    /**
     * Test to make sure that PRERACE with wildcarded names functions
     * correctly with SERVESAS
     */
    @Test
    public void testPassServesAsNameWildcard()
    {
        final PlayerCharacter character = getCharacter();

        final Race race = new Race();
        race.setName("Human");
        Globals.getContext().getReferenceContext().importObject(race);

        final Race fake = new Race();
        fake.setName("NotHuman");
        Globals.getContext().getReferenceContext().importObject(fake);
        fake.addToListFor(ListKey.SERVES_AS_RACE, CDOMDirectSingleRef
                .getRef(race));

        final Race gnome = new Race();
        gnome.setName("Gnome");
        Globals.getContext().getReferenceContext().importObject(gnome);

        character.setRace(fake);

        // Check the servesas condition
        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("race");
        prereq.setKey("human%");
        prereq.setOperator(PrerequisiteOperator.EQ);
        boolean passes = PrereqHandler.passes(prereq, character, null);
        assertTrue("PRERACE:1,human% should have been passed", passes);

        prereq.setKey("NotHuman%");
        passes = PrereqHandler.passes(prereq, character, null);
        assertTrue("PRERACE:1,NotHuman% should have been passed", passes);

        prereq.setKey("Elf%");
        passes = PrereqHandler.passes(prereq, character, null);
        assertFalse("PRERACE:1,Elf% should not have been passed", passes);

        prereq.setKey("Gno%");
        passes = PrereqHandler.passes(prereq, character, null);
        assertFalse("PRERACE:1,Gno% should not have been passed", passes);
    }
}
