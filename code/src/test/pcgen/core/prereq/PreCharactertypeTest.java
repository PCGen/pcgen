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

import pcgen.AbstractCharacterTestCase;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * {@code PreDomainTest} tests that the PREDOMAIN tag is
 * working correctly.
 */
public class PreCharactertypeTest extends AbstractCharacterTestCase
{
	/**
	 * Test to make sure it is not looking at deity domains.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	public void testCharactertype() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();
		character.setCharacterType("PC");

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();

		prereq = factory.parse("PRECHARACTERTYPE:1,PC");

		assertTrue("Character has character type 'PC'", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PRECHARACTERTYPE:1,NPC");

		assertFalse("Character doesn't have character type 'NPC'", PrereqHandler.passes(
			prereq, character, null));

	}
}
