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
import pcgen.base.format.StringManager;
import pcgen.base.util.BasicIndirect;
import pcgen.cdom.enumeration.FactSetKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Deity;
import pcgen.core.PlayerCharacter;
import pcgen.output.channel.compat.AlignmentCompat;
import pcgen.output.channel.compat.DeityCompat;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code PreDeityTest} tests that the PREDEITY tag is
 * working correctly.
 */
public class PreDeityTest extends AbstractCharacterTestCase
{
	private Deity deity;

	/**
	 * Test that the boolean version (Y/N) works.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	public void testBoolean() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDEITY:1,Y");

		assertFalse("Character has no deity selected", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PREDEITY:1,N");

		assertTrue("Character has no deity selected", PrereqHandler.passes(
			prereq, character, null));

		AlignmentCompat.setCurrentAlignment(character.getCharID(), ng);
		DeityCompat.setCurrentDeity(character.getCharID(), deity);

		assertFalse("Character has deity selected", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PREDEITY:1,Y");

		assertTrue("Character has deity selected", PrereqHandler.passes(prereq,
			character, null));

	}

	/**
	 * Test different formats for the option.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	public void testFormat() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDEITY:1,YES");

		assertFalse("Character has no deity selected", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PREDEITY:1,NO");

		assertTrue("Character has no deity selected", PrereqHandler.passes(
			prereq, character, null));

		AlignmentCompat.setCurrentAlignment(character.getCharID(), ng);
		DeityCompat.setCurrentDeity(character.getCharID(), deity);

		assertFalse("Character has deity selected", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PREDEITY:1,YES");

		assertTrue("Character has deity selected", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PREDEITY:1,yes");

		assertTrue("Character has deity selected", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PREDEITY:1,Yesmeth");

		assertFalse("Character does not have Yesmeth as deity", PrereqHandler
			.passes(prereq, character, null));
	}

	/**
	 * Test naming specific deities works as expected.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	public void testName() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDEITY:1,Test Deity");

		assertFalse("Character has no deity selected", PrereqHandler.passes(
			prereq, character, null));

		AlignmentCompat.setCurrentAlignment(character.getCharID(), ng);
		DeityCompat.setCurrentDeity(character.getCharID(), deity);

		assertTrue("Character has Test Deity selected", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PREDEITY:1,Test Deity,Zeus,Odin");

		assertTrue("Character has Test Deity selected", PrereqHandler.passes(
			prereq, character, null));
	}

	/**
	 * Test that the new standardized format works correctly.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	public void testNewFormat() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDEITY:1,YES");

		assertFalse("Character has no deity selected", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PREDEITY:1,NO");

		assertTrue("Character has no deity selected", PrereqHandler.passes(
			prereq, character, null));

		AlignmentCompat.setCurrentAlignment(character.getCharID(), ng);
		DeityCompat.setCurrentDeity(character.getCharID(), deity);

		assertFalse("Character has deity selected", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PREDEITY:1,YES");

		assertTrue("Character has deity selected", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PREDEITY:1,yes");

		assertTrue("Character has deity selected", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PREDEITY:1,Yesmeth");

		assertFalse("Character does not have Yesmeth as deity", PrereqHandler
			.passes(prereq, character, null));
	}

	/**
	 * Test the pantheon fucntioanlity of the PREDEITY tag. 
	 * @throws PersistenceLayerException 
	 */
	@Test
	public void testPantheon() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDEITY:1,PANTHEON.Celtic");

		assertFalse("Character has no deity selected", PrereqHandler.passes(
			prereq, character, null));

		AlignmentCompat.setCurrentAlignment(character.getCharID(), ng);
		DeityCompat.setCurrentDeity(character.getCharID(), deity);
		assertTrue("Character has Celtic deity selected", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PREDEITY:1,Zeus,PANTHEON.Celtic,Odin");

		assertTrue("Character has Celtic deity selected", PrereqHandler.passes(
			prereq, character, null));
	}

	@BeforeEach
    @Override
	protected void setUp() throws Exception
	{
		super.setUp();
		deity = new Deity();
		deity.setName("Test Deity");
		deity.put(ObjectKey.ALIGNMENT, CDOMDirectSingleRef.getRef(ng));
		StringManager sm = new StringManager();
		FactSetKey<String> fsk = FactSetKey.getConstant("Pantheon", sm);
		deity.addToSetFor(fsk, new BasicIndirect<>(sm, "Celtic"));
	}
}
