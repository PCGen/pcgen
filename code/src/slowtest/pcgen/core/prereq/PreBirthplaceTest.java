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
import pcgen.cdom.enumeration.PCStringKey;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;

import org.junit.jupiter.api.Test;

/**
 * {@code PreBirthplaceTest} tests that the PREBIRTHPLACE tag is
 * working correctly.
 */
class PreBirthplaceTest extends AbstractCharacterTestCase
{
	/**
	 * Test the PREBIRTHPLACE code.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	void testAtt() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();
		character.setPCAttribute(PCStringKey.BIRTHPLACE, "Klamath");

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREBIRTHPLACE:Klamath");

		assertTrue(PrereqHandler.passes(prereq,
			character, null), "Character is from Klamath");

		prereq = factory.parse("PREBIRTHPLACE:KLAMATH");

		assertTrue(PrereqHandler.passes(prereq,
			character, null), "Case is not significant");

		prereq = factory.parse("PREBIRTHPLACE:Klam");

		assertFalse(PrereqHandler.passes(prereq,
			character, null), "Requires a full match");
	}
}
