/*
 * PreRaceTest.java
 *
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
 *
 * Created on 13-Jan-2004
 *
 * Current Ver: $Revision$
 *
 * Last Editor: $Author$
 *
 * Last Edited: $Date$
 *
 */
package pcgen.core.prereq;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.cdom.enumeration.RaceType;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;

/**
 * @author wardc
 *
 */
@SuppressWarnings("nls")
public class PreRaceTest extends AbstractCharacterTestCase
{
	/**
	 * Runs the test.
	 * @param args
	 */
	public static void main(final String[] args)
	{
		TestRunner.run(PreRaceTest.class);
	}

	/**
	 * Returns a TestSuite consisting of all the tests in this class.
	 * 
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreRaceTest.class);
	}

	/**
	 * Test to ensure that we return false when races don't match.
	 * 
	 * @throws Exception
	 */
	public void testFail() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final Race race = new Race();
		race.setName("Human");
		Globals.getContext().ref.importObject(race);

		character.setRace(race);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("race");
		prereq.setKey("Orc");
		prereq.setOperator(PrerequisiteOperator.EQ);

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);
	}

	/**
	 * Test to make sure we return false when race is equal but NOT is specificed.
	 * 
	 * @throws Exception
	 */
	public void testNeqFails() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final Race race = new Race();
		race.setName("Human");
		Globals.getContext().ref.importObject(race);

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
	 * 
	 * @throws Exception
	 */
	public void testNeqPasses() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final Race race = new Race();
		race.setName("Human");
		Globals.getContext().ref.importObject(race);

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
	 * 
	 * @throws Exception
	 */
	public void testPass() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final Race race = new Race();
		race.setName("Human");
		Globals.getContext().ref.importObject(race);

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
	 * 
	 * @throws Exception
	 */
	public void testPassServesAsName() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final Race race = new Race();
		race.setName("Human");
		Globals.getContext().ref.importObject(race);
		
		final Race fake = new Race();
		fake.setName("NotHuman");
		Globals.getContext().ref.importObject(fake);

		race.putServesAs(fake.getDisplayName(), "");
		character.setRace(fake);

		
		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("race");
		prereq.setKey("human");
		prereq.setOperator(PrerequisiteOperator.EQ);

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}
	/**
	 * Test to make sure that we return true when races RACESUBTYPE are equal using ServesAs.
	 * 
	 * @throws Exception
	 */
	public void testPassServesAsRaceSubType() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final Race race = new Race();
		race.setName("Human");
		race.setTypeInfo("Outsider");
		race.addToListFor(ListKey.RACESUBTYPE, RaceSubType.getConstant("aquatic"));
		race.addToListFor(ListKey.RACESUBTYPE, RaceSubType.getConstant("foo"));
		Globals.getContext().ref.importObject(race);
		
		final Race fake = new Race();
		fake.setName("NotHuman");
		fake.setTypeInfo("Humanoid");
		race.addToListFor(ListKey.RACESUBTYPE, RaceSubType.getConstant("desert"));
		race.addToListFor(ListKey.RACESUBTYPE, RaceSubType.getConstant("none"));
		Globals.getContext().ref.importObject(fake);

		fake.putServesAs(race.getDisplayName(), "");
		character.setRace(fake);
		
		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("race");
		prereq.setKey("RACESUBTYPE=aquatic");
		prereq.setOperator(PrerequisiteOperator.EQ);

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
		
		final Prerequisite prereq2 = new Prerequisite();
		prereq2.setKind("race");
		prereq2.setKey("RACESUBTYPE=foo");
		prereq2.setOperator(PrerequisiteOperator.EQ);

		final boolean passes2 = PrereqHandler.passes(prereq2, character, null);
		assertTrue(passes2);
		
		
	}
	/**
	 * Test to make sure that we return true when races RACETYPE are equal using ServesAs.
	 * 
	 * @throws Exception
	 */
	public void testPassServesAsRaceType() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final Race race = new Race();
		race.setName("Human");
		race.put(ObjectKey.RACETYPE, RaceType.getConstant("Outsider"));
		race.setTypeInfo("Outsider");
		Globals.getContext().ref.importObject(race);
		
		final Race fake = new Race();
		fake.setName("NotHuman");
		fake.put(ObjectKey.RACETYPE, RaceType.getConstant("Humanoid"));
		fake.setTypeInfo("Humanoid");
		Globals.getContext().ref.importObject(fake);

		race.putServesAs(fake.getDisplayName(), "");
		character.setRace(fake);

		
		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("race");
		prereq.setKey("RACETYPE=Humanoid");
		prereq.setOperator(PrerequisiteOperator.EQ);

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}
	
	/**
	 * Test to make sure that we return true when races TYPE are equal.
	 * 
	 * @throws Exception
	 */
	public void testPassServesAsType() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final Race race = new Race();
		race.setName("Human");
		race.setTypeInfo("Outsider");
		Globals.getContext().ref.importObject(race);
		
		final Race fake = new Race();
		fake.setName("NotHuman");
		fake.setTypeInfo("Humanoid");
		Globals.getContext().ref.importObject(fake);

		race.putServesAs(fake.getDisplayName(), "");
		character.setRace(fake);

		
		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("race");
		prereq.setKey("TYPE=Humanoid");
		prereq.setOperator(PrerequisiteOperator.EQ);

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}
}
