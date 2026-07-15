/*
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
 */
package pcgen.core.prereq;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.TestHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code PreLangTest} tests that the PRELANG tag is
 * working correctly.
 */
class PreLangTest extends AbstractCharacterTestCase
{
	final Language elven = new Language();
	final Language dwarven = new Language();
	final Language halfling = new Language();

	/**
	 * Test the PRELANG code.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	void testLang() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();
		character.addAutoLanguage(elven, elven);

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PRELANG:1,KEY_Elven");

		assertTrue(PrereqHandler.passes(prereq,
			character, null), "Character should have elven");

		prereq = factory.parse("PRELANG:1,KEY_Elven,KEY_Dwarven");

		assertTrue(PrereqHandler.passes(prereq,
			character, null), "Character should have elven");

		prereq = factory.parse("PRELANG:2,KEY_Elven,KEY_Dwarven");

		assertFalse(PrereqHandler.passes(
			prereq, character, null), "Character doesn't have Dwarven");

		character.addAutoLanguage(dwarven, dwarven);

		assertTrue(PrereqHandler.passes(
			prereq, character, null), "Character has Elven and Dwarven");

		prereq = factory.parse("PRELANG:3,ANY");

		assertFalse(PrereqHandler.passes(
			prereq, character, null), "Character doesn't have 3 langs");

		character.addAutoLanguage(halfling, halfling);

		assertTrue(PrereqHandler
			.passes(prereq, character, null), "Character has Elven, Dwarven, and Halfling");

		prereq = factory.parse("PRELANG:3,Elven");

		assertFalse(PrereqHandler.passes(
			prereq, character, null), "PRE test should look at keys");
	}

	@BeforeEach
    @Override
	public void setUp() throws Exception
	{
		super.setUp();

		elven.setName("Elven");
		elven.put(StringKey.KEY_NAME, "KEY_Elven");
		TestHelper.addType(elven, "Spoken.Written");
		Globals.getContext().getReferenceContext().importObject(elven);

		dwarven.setName("Dwarven");
		dwarven.put(StringKey.KEY_NAME, "KEY_Dwarven");
		TestHelper.addType(dwarven, "Spoken.Written");
		Globals.getContext().getReferenceContext().importObject(dwarven);

		halfling.setName("Halfling");
		halfling.put(StringKey.KEY_NAME, "KEY_Halfling");
		halfling.addToListFor(ListKey.TYPE, Type.getConstant("Spoken"));
		Globals.getContext().getReferenceContext().importObject(halfling);
	}
}
