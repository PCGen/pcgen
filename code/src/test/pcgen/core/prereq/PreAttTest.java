/*
 * PreAttTest.java
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
import junit.textui.TestRunner;
import pcgen.AbstractCharacterTestCase;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * <code>PreAttTest</code> tests that the PREATT tag is
 * working correctly.
 *
 * Last Editor: $Author: $
 * Last Edited: $Date$
 *
 * @author Aaron Divinsky <boomer70@yahoo.com>
 * @version $Revision$
 */
public class PreAttTest extends AbstractCharacterTestCase
{
	PCClass myClass = new PCClass();

	public static void main(final String[] args)
	{
		TestRunner.run(PreAttTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreAttTest.class);
	}

	/**
	 * Test the PREATT code
	 * @throws Exception
	 */
	public void testAtt() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		character.incrementClassLevel(1, myClass, true);

		character.calcActiveBonuses();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREATT:6");

		assertTrue("Character's BAB should be 6", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PREATT:7");

		assertFalse("Character's BAB should be less than 7", PrereqHandler
			.passes(prereq, character, null));

		final BonusObj toHitBonus = Bonus.newBonus("1|COMBAT|TOHIT|1");
		myClass.addBonusList(toHitBonus);
		character.calcActiveBonuses();

		assertFalse("Character's BAB should be less than 7", PrereqHandler
			.passes(prereq, character, null));
	}

	protected void setUp() throws Exception
	{
		super.setUp();

		myClass.setName("My Class");
		myClass.setAbbrev("Myc");
		myClass.setSkillPointFormula("3");
		final BonusObj babClassBonus = Bonus.newBonus("1|COMBAT|BAB|CL+5");
		myClass.addBonusList(babClassBonus);
		Globals.getClassList().add(myClass);
	}
}
