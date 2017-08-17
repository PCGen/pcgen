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
import pcgen.cdom.base.SimpleAssociatedObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * <code>PreDeityDomainTest</code> tests that the PREDEITYDOMAIN tag is
 * working correctly.
 */
public class PreDeityDomainTest extends AbstractCharacterTestCase
{
	private Deity deity;

	public static void main(final String[] args)
	{
		TestRunner.run(PreDeityDomainTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreDeityDomainTest.class);
	}

	/**
	 * Test for a single domain
	 * @throws Exception
	 */
	public void testSingle() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDEITYDOMAIN:1,Good");

		assertFalse("Character has no deity selected", PrereqHandler.passes(
			prereq, character, null));

		character.setAlignment(ng);
		character.setDeity(deity);

		assertTrue("Character's deity has Good domain", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PREDEITYDOMAIN:1,Law");

		assertFalse("Character's deity doesn't have Law domain", PrereqHandler
			.passes(prereq, character, null));

	}

	public void testMultiple() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDEITYDOMAIN:1,Good,Law");

		assertFalse("Character has no deity selected", PrereqHandler.passes(
			prereq, character, null));

		character.setAlignment(ng);
		character.setDeity(deity);

		assertTrue("Character's deity has Good domain", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PREDEITYDOMAIN:2,Good,Law");

		assertFalse("Character's deity doesn't have Law domain", PrereqHandler
			.passes(prereq, character, null));

		prereq = factory.parse("PREDEITYDOMAIN:2,Good,Animal");

		assertTrue("Character's deity has Good and animal domains",
			PrereqHandler.passes(prereq, character, null));
	}

    @Override
	protected void setUp() throws Exception
	{
		super.setUp();

		Domain goodDomain = new Domain();
		goodDomain.setName("Good");
		Globals.getContext().getReferenceContext().importObject(goodDomain);

		Domain animalDomain = new Domain();
		animalDomain.setName("Animal");
		Globals.getContext().getReferenceContext().importObject(animalDomain);

		deity = new Deity();
		deity.setName("Test Deity");
		deity.put(ObjectKey.ALIGNMENT, CDOMDirectSingleRef.getRef(ng));
		deity.putToList(Deity.DOMAINLIST, CDOMDirectSingleRef
				.getRef(goodDomain), new SimpleAssociatedObject());
		deity.putToList(Deity.DOMAINLIST, CDOMDirectSingleRef
				.getRef(animalDomain), new SimpleAssociatedObject());
	}
}
