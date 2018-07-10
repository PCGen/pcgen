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

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.PCAttribute;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * <code>PreCityTest</code> tests that the PRECITY tag is
 * working correctly.
 */
public class PreCityTest extends AbstractCharacterTestCase
{
	public static void main(final String[] args)
	{
		TestRunner.run(PreCityTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreCityTest.class);
	}

	/**
	 * Test the PRECITY code.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	public void testCity() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();
		character.setPCAttribute(PCAttribute.RESIDENCE, "Klamath");

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PRECITY:Klamath");

		assertTrue("Character is from Klamath", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PRECITY:KLAMATH");

		assertTrue("Case is not significant", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PRECITY:Klam");

		assertFalse("Requires a full match", PrereqHandler.passes(prereq,
			character, null));
	}
}
