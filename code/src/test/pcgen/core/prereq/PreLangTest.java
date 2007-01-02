/*
 * PreLangTest.java
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
import pcgen.core.Language;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * <code>PreLangTest</code> tests that the PRELANG tag is
 * working correctly.
 *
 * Last Editor: $Author: $
 * Last Edited: $Date$
 *
 * @author Aaron Divinsky <boomer70@yahoo.com>
 * @version $Revision$
 */
public class PreLangTest extends AbstractCharacterTestCase
{
	final Language elven = new Language();
	final Language dwarven = new Language();
	final Language halfling = new Language();

	public static void main(final String[] args)
	{
		junit.swingui.TestRunner.run(PreLangTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreLangTest.class);
	}

	/**
	 * Test the PRELANG code
	 * @throws Exception
	 */
	public void testLang() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		character.addLanguage(elven);

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PRELANG:1,KEY_Elven");

		assertTrue("Character should have elven", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PRELANG:1,KEY_Elven,KEY_Dwarven");

		assertTrue("Character should have elven", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PRELANG:2,KEY_Elven,KEY_Dwarven");

		assertFalse("Character doesn't have Dwarven", PrereqHandler.passes(
			prereq, character, null));

		character.addLanguage(dwarven);

		assertTrue("Character has Elven and Dwarven", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PRELANG:3,ANY");

		assertFalse("Character doesn't have 3 langs", PrereqHandler.passes(
			prereq, character, null));

		character.addLanguage(halfling);

		assertTrue("Character has Elven, Dwarven, and Halfling", PrereqHandler
			.passes(prereq, character, null));

		prereq = factory.parse("PRELANG:3,Elven");

		assertFalse("PRE test should look at keys", PrereqHandler.passes(
			prereq, character, null));
	}

	protected void setUp() throws Exception
	{
		super.setUp();

		elven.setName("Elven");
		elven.setKeyName("KEY_Elven");
		elven.setTypeInfo("Spoken.Written");
		Globals.getLanguageList().add(elven);

		dwarven.setName("Dwarven");
		dwarven.setKeyName("KEY_Dwarven");
		dwarven.setTypeInfo("Spoken.Written");
		Globals.getLanguageList().add(dwarven);

		halfling.setName("Halfling");
		halfling.setKeyName("KEY_Halfling");
		halfling.setTypeInfo("Spoken");
		Globals.getLanguageList().add(halfling);
	}
}
