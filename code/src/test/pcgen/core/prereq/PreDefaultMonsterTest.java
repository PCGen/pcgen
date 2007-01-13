/*
 * PreDefaultMonsterTest.java
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
 * Current Ver: $Revision$
 *
 * Last Editor: $Author: $
 *
 * Last Edited: $Date$
 *
 */
package pcgen.core.prereq;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.AbstractCharacterTestCase;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * Test the PREDEFAULTMONSTER tag
 * @author boomer70
 *
 */
public class PreDefaultMonsterTest extends AbstractCharacterTestCase
{

	public static void main(final String[] args)
	{
		TestRunner.run(PreDefaultMonsterTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreDefaultMonsterTest.class);
	}

	/**
	 * Tests for when the setting is on
	 * @throws Exception
	 */
	public void testOn() throws Exception
	{
		SettingsHandler.setMonsterDefault(true);
		PlayerCharacter pc = this.getCharacter();

		final PreParserFactory factory = PreParserFactory.getInstance();
		Prerequisite prereq = factory.parse("PREDEFAULTMONSTER:Y");

		assertTrue("Default Monsters should be true", PrereqHandler.passes(
			prereq, pc, null));

		prereq = factory.parse("PREDEFAULTMONSTER:YES");

		assertTrue("Should allow full word", PrereqHandler.passes(prereq, pc,
			null));

		prereq = factory.parse("PREDEFAULTMONSTER:yes");

		assertTrue("Should be case insensitive", PrereqHandler.passes(prereq,
			pc, null));

		prereq = factory.parse("PREDEFAULTMONSTER:N");

		assertFalse("Default Monsters should be true", PrereqHandler.passes(
			prereq, pc, null));

		prereq = factory.parse("PREDEFAULTMONSTER:NO");

		assertFalse("Should allow full word", PrereqHandler.passes(prereq, pc,
			null));

		prereq = factory.parse("PREDEFAULTMONSTER:no");

		assertFalse("Should be case insensitive", PrereqHandler.passes(prereq,
			pc, null));
	}

	/**
	 * Tests for when the setting is off
	 * @throws Exception
	 */
	public void testOff() throws Exception
	{
		SettingsHandler.setMonsterDefault(false);
		// PlayerCharacter caches the setting
		PlayerCharacter pc = new PlayerCharacter();

		final PreParserFactory factory = PreParserFactory.getInstance();
		Prerequisite prereq = factory.parse("PREDEFAULTMONSTER:Y");

		assertFalse("Default Monsters should not be true", PrereqHandler
			.passes(prereq, pc, null));

		prereq = factory.parse("PREDEFAULTMONSTER:YES");

		assertFalse("Should allow full word", PrereqHandler.passes(prereq, pc,
			null));

		prereq = factory.parse("PREDEFAULTMONSTER:yes");

		assertFalse("Should be case insensitive", PrereqHandler.passes(prereq,
			pc, null));

		prereq = factory.parse("PREDEFAULTMONSTER:N");

		assertTrue("Default Monsters should not be true", PrereqHandler.passes(
			prereq, pc, null));

		prereq = factory.parse("PREDEFAULTMONSTER:NO");

		assertTrue("Should allow full word", PrereqHandler.passes(prereq, pc,
			null));

		prereq = factory.parse("PREDEFAULTMONSTER:no");

		assertTrue("Should be case insensitive", PrereqHandler.passes(prereq,
			pc, null));
	}

	protected void setUp() throws Exception
	{
		super.setUp();
	}
}
