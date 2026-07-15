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
import pcgen.base.format.StringManager;
import pcgen.base.util.BasicIndirect;
import pcgen.cdom.enumeration.FactSetKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.util.CControl;
import pcgen.core.Deity;
import pcgen.core.PlayerCharacter;
import pcgen.output.channel.ChannelUtilities;
import pcgen.output.channel.compat.AlignmentCompat;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code PreDeityTest} tests that the PREDEITY tag is
 * working correctly.
 */
class PreDeityTest extends AbstractCharacterTestCase
{
	private Deity deity;

	/**
	 * Test that the boolean version (Y/N) works.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	void testBoolean() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDEITY:1,Y");

		assertFalse(PrereqHandler.passes(
			prereq, character, null), "Character has no deity selected");

		prereq = factory.parse("PREDEITY:1,N");

		assertTrue(PrereqHandler.passes(
			prereq, character, null), "Character has no deity selected");

		AlignmentCompat.setCurrentAlignment(character.getCharID(), ng);
		ChannelUtilities.setControlledChannel(character.getCharID(),
			CControl.DEITYINPUT, deity);

		assertFalse(PrereqHandler.passes(
			prereq, character, null), "Character has deity selected");

		prereq = factory.parse("PREDEITY:1,Y");

		assertTrue(PrereqHandler.passes(prereq,
			character, null), "Character has deity selected");

	}

	/**
	 * Test different formats for the option.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	void testFormat() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDEITY:1,YES");

		assertFalse(PrereqHandler.passes(
			prereq, character, null), "Character has no deity selected");

		prereq = factory.parse("PREDEITY:1,NO");

		assertTrue(PrereqHandler.passes(
			prereq, character, null), "Character has no deity selected");

		AlignmentCompat.setCurrentAlignment(character.getCharID(), ng);
		ChannelUtilities.setControlledChannel(character.getCharID(),
			CControl.DEITYINPUT, deity);

		assertFalse(PrereqHandler.passes(
			prereq, character, null), "Character has deity selected");

		prereq = factory.parse("PREDEITY:1,YES");

		assertTrue(PrereqHandler.passes(prereq,
			character, null), "Character has deity selected");

		prereq = factory.parse("PREDEITY:1,yes");

		assertTrue(PrereqHandler.passes(prereq,
			character, null), "Character has deity selected");

		prereq = factory.parse("PREDEITY:1,Yesmeth");

		assertFalse(PrereqHandler
			.passes(prereq, character, null), "Character does not have Yesmeth as deity");
	}

	/**
	 * Test naming specific deities works as expected.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	void testName() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDEITY:1,Test Deity");

		assertFalse(PrereqHandler.passes(
			prereq, character, null), "Character has no deity selected");

		AlignmentCompat.setCurrentAlignment(character.getCharID(), ng);
		ChannelUtilities.setControlledChannel(character.getCharID(),
			CControl.DEITYINPUT, deity);

		assertTrue(PrereqHandler.passes(
			prereq, character, null), "Character has Test Deity selected");

		prereq = factory.parse("PREDEITY:1,Test Deity,Zeus,Odin");

		assertTrue(PrereqHandler.passes(
			prereq, character, null), "Character has Test Deity selected");
	}

	/**
	 * Test that the new standardized format works correctly.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	void testNewFormat() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDEITY:1,YES");

		assertFalse(PrereqHandler.passes(
			prereq, character, null), "Character has no deity selected");

		prereq = factory.parse("PREDEITY:1,NO");

		assertTrue(PrereqHandler.passes(
			prereq, character, null), "Character has no deity selected");

		AlignmentCompat.setCurrentAlignment(character.getCharID(), ng);
		ChannelUtilities.setControlledChannel(character.getCharID(),
			CControl.DEITYINPUT, deity);

		assertFalse(PrereqHandler.passes(
			prereq, character, null), "Character has deity selected");

		prereq = factory.parse("PREDEITY:1,YES");

		assertTrue(PrereqHandler.passes(prereq,
			character, null), "Character has deity selected");

		prereq = factory.parse("PREDEITY:1,yes");

		assertTrue(PrereqHandler.passes(prereq,
			character, null), "Character has deity selected");

		prereq = factory.parse("PREDEITY:1,Yesmeth");

		assertFalse(PrereqHandler
			.passes(prereq, character, null), "Character does not have Yesmeth as deity");
	}

	/**
	 * Test the pantheon fucntioanlity of the PREDEITY tag. 
	 * @throws PersistenceLayerException 
	 */
	@Test
	void testPantheon() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDEITY:1,PANTHEON.Celtic");

		assertFalse(PrereqHandler.passes(
			prereq, character, null), "Character has no deity selected");

		AlignmentCompat.setCurrentAlignment(character.getCharID(), ng);
		ChannelUtilities.setControlledChannel(character.getCharID(),
			CControl.DEITYINPUT, deity);
		assertTrue(PrereqHandler.passes(
			prereq, character, null), "Character has Celtic deity selected");

		prereq = factory.parse("PREDEITY:1,Zeus,PANTHEON.Celtic,Odin");

		assertTrue(PrereqHandler.passes(
			prereq, character, null), "Character has Celtic deity selected");
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
