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
	public void testLang()
		throws Exception
	{
		final PlayerCharacter character = getCharacter();
		character.addLanguage("Elven");

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PRELANG:1,Elven");

		assertTrue("Character should have elven",
				   PrereqHandler.passes(prereq, character, null));

		prereq = factory.parse("PRELANG:1,Elven,Dwarven");

		assertTrue("Character should have elven",
				   PrereqHandler.passes(prereq, character, null));

		prereq = factory.parse("PRELANG:2,Elven,Dwarven");

		assertFalse("Character doesn't have Dwarven",
					PrereqHandler.passes(prereq, character, null));

		character.addLanguage("Dwarven");

		assertTrue("Character has Elven and Dwarven",
				   PrereqHandler.passes(prereq, character, null));

		prereq = factory.parse("PRELANG:3,ANY");

		assertFalse("Character doesn't have 3 langs",
					PrereqHandler.passes(prereq, character, null));

		character.addLanguage("Halfling");

		assertTrue("Character has Elven, Dwarven, and Halfling",
				   PrereqHandler.passes(prereq, character, null));
	}

	protected void setUp()
		throws Exception
	{
		super.setUp();

		final Language elven = new Language();
		elven.setName("Elven");
		elven.setKeyName("Elven");
		elven.setTypeInfo("Spoken.Written");
		Globals.getLanguageList().add(elven);

		final Language dwarven = new Language();
		dwarven.setName("Dwarven");
		dwarven.setKeyName("Dwarven");
		dwarven.setTypeInfo("Spoken.Written");
		Globals.getLanguageList().add(dwarven);

		final Language halfling = new Language();
		halfling.setName("Halfling");
		halfling.setKeyName("Halfling");
		halfling.setTypeInfo("Spoken");
		Globals.getLanguageList().add(halfling);
	}
}
