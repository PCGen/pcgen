/*
 * PreDRTest.java
 *
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
 *
 *
 */
package pcgen.core.prereq;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.core.DamageReduction;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * <code>PreDRTest</code> tests that the PREDR tag is
 * working correctly.
 *
 * Last Editor: $Author: $
 * Last Edited: $Date$
 *
 * @author Aaron Divinsky <boomer70@yahoo.com>
 * @version $Revision$
 */
public class PreDRTest extends AbstractCharacterTestCase
{
	private Race race = new Race();
	private DamageReduction drPlus1;

	public static void main(final String[] args)
	{
		junit.swingui.TestRunner.run(PreDRTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreDRTest.class);
	}

	/**
	 * Test basic functionality
	 * @throws Exception
	 */
	public void testDR()
		throws Exception
	{
		final PlayerCharacter character = getCharacter();
		character.setRace(race);

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDR:1,+1=10");

		assertFalse("Character has no DR",
					PrereqHandler.passes(prereq, character, null));

		race.addDR(drPlus1);

		assertFalse("Character DR not 10",
					PrereqHandler.passes(prereq, character, null));

		DamageReduction drPlus1_10 = new DamageReduction("10", "+1");
		race.addDR(drPlus1_10);

		assertTrue("Character has DR 10/+1",
				   PrereqHandler.passes(prereq, character, null));
	}

	/**
	 * Make sure or case works
	 * @throws Exception
	 */
	public void testMultiOr()
		throws Exception
	{
		final PlayerCharacter character = getCharacter();
		character.setRace(race);

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDR:1,+1=10,+2=5");

		assertFalse("Character has no DR",
					PrereqHandler.passes(prereq, character, null));

		race.addDR(drPlus1);

		assertFalse("Character DR not 10",
					PrereqHandler.passes(prereq, character, null));

		DamageReduction drPlus2_5 = new DamageReduction("5", "+2");
		race.addDR(drPlus2_5);

		assertTrue("Character has DR 5/+2",
				   PrereqHandler.passes(prereq, character, null));
	}

	public void testMultiAnd()
		throws Exception
	{
		final PlayerCharacter character = getCharacter();
		character.setRace(race);

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDR:2,+1=10,+2=5");

		assertFalse("Character has no DR",
					PrereqHandler.passes(prereq, character, null));

		race.addDR(drPlus1);

		assertFalse("Character DR not 10",
					PrereqHandler.passes(prereq, character, null));

		DamageReduction drPlus2_5 = new DamageReduction("5", "+2");
		race.addDR(drPlus2_5);

		assertFalse("Character has DR 5/+2",
					PrereqHandler.passes(prereq, character, null));

		DamageReduction drPlus1_10 = new DamageReduction("10", "+1");
		race.addDR(drPlus1_10);

		assertTrue("Character has DR 10/+1 and 5/+2",
				   PrereqHandler.passes(prereq, character, null));
	}

	protected void setUp()
		throws Exception
	{
		super.setUp();

		drPlus1 = new DamageReduction("5", "+1");
	}
}
