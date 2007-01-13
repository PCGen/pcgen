/*
 * PreDomainTest.java
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
import pcgen.core.CharacterDomain;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * <code>PreDomainTest</code> tests that the PREDOMAIN tag is
 * working correctly.
 *
 * Last Editor: $Author: $
 * Last Edited: $Date$
 *
 * @author Aaron Divinsky <boomer70@yahoo.com>
 * @version $Revision$
 */
public class PreDomainTest extends AbstractCharacterTestCase
{
	private Deity deity;

	public static void main(final String[] args)
	{
		TestRunner.run(PreDomainTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreDomainTest.class);
	}

	/**
	 * Test to make sure it is not looking at deity domains
	 * @throws Exception
	 */
	public void testDeity() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDOMAIN:1,Good");

		assertFalse("Character has no deity selected", PrereqHandler.passes(
			prereq, character, null));

		character.setAlignment(3, false);
		character.setDeity(deity);

		assertFalse("Character's deity has Good domain", PrereqHandler.passes(
			prereq, character, null));

		CharacterDomain cd = new CharacterDomain();
		cd.setDomain(Globals.getDomainKeyed("Good"), character);
		character.addCharacterDomain(cd);

		assertTrue("Character has Good domain", PrereqHandler.passes(prereq,
			character, null));
	}

	/**
	 * Test with multiple options
	 * @throws Exception
	 */
	public void testMultiple() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDOMAIN:1,Good,Law");

		assertFalse("Character has no deity selected", PrereqHandler.passes(
			prereq, character, null));

		character.setAlignment(3, false);
		character.setDeity(deity);

		assertFalse("Character's deity has Good domain", PrereqHandler.passes(
			prereq, character, null));

		CharacterDomain cd = new CharacterDomain();
		cd.setDomain(Globals.getDomainKeyed("Good"), character);
		character.addCharacterDomain(cd);

		assertTrue("Character has Good domain", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PREDOMAIN:2,Good,Law");

		assertFalse("Character doesn't have Law domain", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PREDOMAIN:2,Good,Animal");

		CharacterDomain cd1 = new CharacterDomain();
		cd1.setDomain(Globals.getDomainKeyed("Animal"), character);
		character.addCharacterDomain(cd1);

		assertTrue("Character's deity has Good and animal domains",
			PrereqHandler.passes(prereq, character, null));
	}

	protected void setUp() throws Exception
	{
		super.setUp();

		Domain goodDomain = new Domain();
		goodDomain.setName("Good");
		Globals.addDomain(goodDomain);

		Domain animalDomain = new Domain();
		animalDomain.setName("Animal");
		Globals.addDomain(animalDomain);

		deity = new Deity();
		deity.setName("Test Deity");
		deity.setAlignment("NG");
		deity.setFollowerAlignments("012345678");
		deity.addDomain("Good");
		deity.addDomain("Animal");
	}
}
