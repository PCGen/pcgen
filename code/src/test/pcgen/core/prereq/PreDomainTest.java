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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.SimpleAssociatedObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.ClassSource;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.output.channel.compat.AlignmentCompat;
import pcgen.output.channel.compat.DeityCompat;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code PreDomainTest} tests that the PREDOMAIN tag is
 * working correctly.
 */
public class PreDomainTest extends AbstractCharacterTestCase
{
	private Deity deity;

	/**
	 * Test to make sure it is not looking at deity domains.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	public void testDeity() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();

		PCClass cl = new PCClass();
		character.setDefaultDomainSource(new ClassSource(cl, 1));
		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDOMAIN:1,Good");

		assertFalse("Character has no deity selected", PrereqHandler.passes(
			prereq, character, null));

		AlignmentCompat.setCurrentAlignment(character.getCharID(), ng);
		DeityCompat.setCurrentDeity(character.getCharID(), deity);

		assertFalse("Character's deity has Good domain", PrereqHandler.passes(
			prereq, character, null));

		character.addDomain(Globals.getContext().getReferenceContext()
				.silentlyGetConstructedCDOMObject(Domain.class, "Good"));

		assertTrue("Character has Good domain", PrereqHandler.passes(prereq,
			character, null));
	}

	/**
	 * Test with multiple options.
	 * @throws PersistenceLayerException 
	 */
	@Test
	public void testMultiple() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();
		PCClass cl = new PCClass();
		character.setDefaultDomainSource(new ClassSource(cl, 1));

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDOMAIN:1,Good,Law");

		assertFalse("Character has no deity selected", PrereqHandler.passes(
			prereq, character, null));

		AlignmentCompat.setCurrentAlignment(character.getCharID(), ng);
		DeityCompat.setCurrentDeity(character.getCharID(), deity);

		assertFalse("Character's deity has Good domain", PrereqHandler.passes(
			prereq, character, null));

		character.addDomain(Globals.getContext().getReferenceContext()
				.silentlyGetConstructedCDOMObject(Domain.class, "Good"));

		assertTrue("Character has Good domain", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PREDOMAIN:2,Good,Law");

		assertFalse("Character doesn't have Law domain", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PREDOMAIN:2,Good,Animal");

		character.addDomain(Globals.getContext().getReferenceContext()
				.silentlyGetConstructedCDOMObject(Domain.class, "Animal"));

		assertTrue("Character's deity has Good and animal domains",
			PrereqHandler.passes(prereq, character, null));
	}
	
	/**
	 * Test for any domain.
	 * @throws PersistenceLayerException 
	 */
	@Test
	public void testAny() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();
		PCClass cl = new PCClass();
		character.setDefaultDomainSource(new ClassSource(cl, 1));

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDOMAIN:1,ANY");

		assertFalse("Character has no domains", PrereqHandler.passes(
			prereq, character, null));

		character.addDomain(Globals.getContext().getReferenceContext()
				.silentlyGetConstructedCDOMObject(Domain.class, "Good"));

		assertTrue("Character has one domain", PrereqHandler.passes(prereq,
			character, null));
		
		prereq = factory.parse("PREDOMAIN:2,ANY");

		assertFalse("Character has only one domain", PrereqHandler.passes(
				prereq, character, null));
		
		character.addDomain(Globals.getContext().getReferenceContext()
				.silentlyGetConstructedCDOMObject(Domain.class, "Animal"));
		
		assertTrue("Character has two domains", PrereqHandler.passes(
				prereq, character, null));
		
	}

	@BeforeEach
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
