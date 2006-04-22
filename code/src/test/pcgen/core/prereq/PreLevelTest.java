/*
 * PreLevelTest.java
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
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * <code>PreLevelTest</code> tests that the PRELEVEL tag is
 * working correctly.
 *
 * Last Editor: $Author: $
 * Last Edited: $Date$
 *
 * @author Aaron Divinsky <boomer70@yahoo.com>
 * @version $Revision$
 */
public class PreLevelTest extends AbstractCharacterTestCase
{
	private PCClass myClass = new PCClass();
	private Race race = new Race();

	public static void main(final String[] args)
	{
		junit.swingui.TestRunner.run(PreLevelTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreLevelTest.class);
	}

	/**
	 * Test that Level works
	 * @throws Exception
	 */
	public void testLevel()
		throws Exception
	{
		final PlayerCharacter character = getCharacter();
		character.incrementClassLevel(1, myClass, true);

		myClass = character.getClassNamed("My Class");

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PRELEVEL:2");

		assertFalse("Character is not 2nd level",
					PrereqHandler.passes(prereq, character, null));

		character.incrementClassLevel(1, myClass, true);

		assertTrue("Character has 2 levels",
				   PrereqHandler.passes(prereq, character, null));
	}

	/**
	 * Test that HD are counted
	 * @throws Exception
	 */
	public void testHD()
		throws Exception
	{
		final PlayerCharacter character = getCharacter();
		character.incrementClassLevel(2, myClass, true);

		myClass = character.getClassNamed("My Class");

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();

		prereq = factory.parse("PRELEVEL:4");

		assertFalse("Character doesn't have 4 levels",
					PrereqHandler.passes(prereq, character, null));

		character.setRace(race);

		assertTrue("Character has 4 levels",
				   PrereqHandler.passes(prereq, character, null));
	}

	/**
	 * Make sure BONUS:PCLEVEL is not counted
	 * @throws Exception
	 */
	public void testPCLevel()
		throws Exception
	{
		final PlayerCharacter character = getCharacter();
		character.incrementClassLevel(2, myClass, true);

		myClass = character.getClassNamed("My Class");

		character.setRace(race);

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();

		prereq = factory.parse("PRELEVEL:6");

		final BonusObj levelBonus = Bonus.newBonus("PCLEVEL|My Class|2");
		levelBonus.setCreatorObject(myClass);
		myClass.addBonusList(levelBonus);
		character.calcActiveBonuses();

		assertFalse("Character has only 4 levels",
					PrereqHandler.passes(prereq, character, null));
	}

	protected void setUp()
		throws Exception
	{
		super.setUp();

		race.setName("Gnoll");
		race.setHitDice(2);

		myClass.setName("My Class");
		myClass.setAbbrev("Myc");
		myClass.setSkillPointFormula("3");
		Globals.getClassList().add(myClass);
	}
}
