/*
 * PreCheckTest.java
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
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * <code>PreCheckTest</code> tests that the PRECHECK tag is
 * working correctly.
 *
 * Last Editor: $Author: $
 * Last Edited: $Date$
 *
 * @author Aaron Divinsky <boomer70@yahoo.com>
 * @version $Revision$
 */
public class PreCheckTest extends AbstractCharacterTestCase
{
	PCClass myClass = new PCClass();

	public static void main(final String[] args)
	{
		TestRunner.run(PreCheckTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreCheckTest.class);
	}

	/**
	 * Test that Base Checks work
	 * @throws Exception
	 */
	public void testBase() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		character.incrementClassLevel(1, myClass, true);

		character.calcActiveBonuses();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PRECHECK:1,Fortitude=0");

		assertTrue("Character's Fort save should be 0", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PRECHECK:1,Will=2");

		assertTrue("Character's Will save should be 2", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PRECHECK:1,Fortitude=1,Will=2");
		assertTrue("Character's Will save should be 2", PrereqHandler.passes(
			prereq, character, null));
		prereq = factory.parse("PRECHECK:2,Fortitude=1,Will=2");
		assertFalse("Character's Fort save not 1", PrereqHandler.passes(prereq,
			character, null));
	}

	public void testBonus() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final BonusObj fortBonus = Bonus.newBonus("1|CHECKS|Fortitude|1");
		myClass.addBonusList(fortBonus);

		character.incrementClassLevel(1, myClass, true);

		character.calcActiveBonuses();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PRECHECK:1,Fortitude=1");

		assertTrue("Character's Fort save should be 1", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PRECHECK:1,Will=2");

		assertTrue("Character's Will save should be 2", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PRECHECK:1,Fortitude=1,Will=3");
		assertTrue("Character's Will save should be 2", PrereqHandler.passes(
			prereq, character, null));
		prereq = factory.parse("PRECHECK:2,Fortitude=2,Will=2");
		assertFalse("Character's Fort save not 1", PrereqHandler.passes(prereq,
			character, null));
	}

	protected void setUp() throws Exception
	{
		super.setUp();

		PObject obj = new PObject();
		obj.setName("Fortitude");
		SettingsHandler.getGame().addToCheckList(obj);

		obj = new PObject();
		obj.setName("Reflex");
		SettingsHandler.getGame().addToCheckList(obj);

		obj = new PObject();
		obj.setName("Will");
		SettingsHandler.getGame().addToCheckList(obj);

		myClass.setName("My Class");
		myClass.setAbbrev("Myc");
		myClass.put(FormulaKey.START_SKILL_POINTS, FormulaFactory.getFormulaFor(3));
		final BonusObj fortRefBonus =
				Bonus.newBonus("1|CHECKS|BASE.Fortitude,BASE.Reflex|CL/3");
		myClass.addBonusList(fortRefBonus);
		final BonusObj willBonus = Bonus.newBonus("1|CHECKS|BASE.Will|CL/2+2");
		myClass.addBonusList(willBonus);
		Globals.getClassList().add(myClass);
	}
}
