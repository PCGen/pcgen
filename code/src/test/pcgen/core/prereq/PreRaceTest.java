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
import pcgen.AbstractCharacterTestCase;
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
		junit.swingui.TestRunner.run(PreRaceTest.class);
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
		Globals.addRace(race);

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
		Globals.addRace(race);

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
		Globals.addRace(race);

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
		Globals.addRace(race);

		character.setRace(race);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("race");
		prereq.setKey("human");
		prereq.setOperator(PrerequisiteOperator.EQ);

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}
}
