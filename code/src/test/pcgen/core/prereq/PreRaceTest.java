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
 * Current Ver: $Revision: 1.10 $
 *
 * Last Editor: $Author: byngl $
 *
 * Last Edited: $Date: 2005/10/03 13:56:33 $
 *
 */
package pcgen.core.prereq;

import java.util.Map;

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
public class PreRaceTest extends AbstractCharacterTestCase
{
	public static void main(final String[] args)
	{
		junit.swingui.TestRunner.run(PreRaceTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreRaceTest.class);
	}

	/**
	 * @throws Exception
	 */
	public void testFail() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final Race race = new Race();
		race.setName("Human");
		final Map raceMap = Globals.getRaceMap();
		raceMap.put("Human", race);

		character.setRace(race);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("race");
		prereq.setKey("Orc");
		prereq.setOperator(PrerequisiteOperator.EQ);

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testNeqFails() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final Race race = new Race();
		race.setName("Human");
		final Map raceMap = Globals.getRaceMap();
		raceMap.put("Human", race);

		character.setRace(race);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("race");
		prereq.setKey("Human");
		prereq.setOperator(PrerequisiteOperator.NEQ);

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testNeqPasses() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final Race race = new Race();
		race.setName("Human");
		final Map raceMap = Globals.getRaceMap();
		raceMap.put("Human", race);

		character.setRace(race);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("race");
		prereq.setKey("Orc");
		prereq.setOperator(PrerequisiteOperator.NEQ);

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testPass() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final Race race = new Race();
		race.setName("Human");
		final Map raceMap = Globals.getRaceMap();
		raceMap.put("Human", race);

		character.setRace(race);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("race");
		prereq.setKey("human");
		prereq.setOperator(PrerequisiteOperator.EQ);

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}
}
