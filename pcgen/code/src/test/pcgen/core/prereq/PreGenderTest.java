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
import pcgen.cdom.enumeration.Gender;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * <code>PreGenderTest</code> tests that the PREGENDER tag is
 * working correctly.
 */
public class PreGenderTest extends AbstractCharacterTestCase
{
	public static void main(final String[] args)
	{
		TestRunner.run(PreGenderTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreGenderTest.class);
	}

	/**
	 * Test the PREGENDER code
	 * @throws Exception
	 */
	public void testGender() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		character.setGender(Gender.Male);

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREGENDER:M");

		assertTrue("Character is Male", PrereqHandler.passes(prereq, character,
			null));

		prereq = factory.parse("PREGENDER:m");

		assertFalse("Case is significant", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PREGENDER:Moose");

		assertFalse("Requires a full match", PrereqHandler.passes(prereq,
			character, null));
	}
}
