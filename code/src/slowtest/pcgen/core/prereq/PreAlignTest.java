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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.AbstractCharacterTestCase;
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
 * {@code PreAlignTest} tests that the PREALIGN tag is
 * working correctly.
 */
class PreAlignTest extends AbstractCharacterTestCase
{
	private Deity deity;

	/**
	 * Test that negative (!) alignment checks work correctly in Align tests.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	void testNegative() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();
		AlignmentCompat.setCurrentAlignment(character.getCharID(), ng);

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("!PREALIGN:TN");

		assertTrue(PrereqHandler.passes(prereq, character, null), "Not TN should match character's alignment of NG");

		prereq = factory.parse("!PREALIGN:NG");

		assertFalse(PrereqHandler.passes(prereq, character, null), "Not TN should not match character's alignment of NG");
	}

	/**
	 * Test that alignment abbreviation values work correctly in Align tests.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	void testAbbrev() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();
		AlignmentCompat.setCurrentAlignment(character.getCharID(), ng);

		Prerequisite prereq = new Prerequisite();
		prereq.setKind("align");
		prereq.setKey("NG");
		prereq.setOperator(PrerequisiteOperator.EQ);

		assertTrue(PrereqHandler.passes(prereq, character, null), "Abbrev NG should match character's alignment of NG");

		prereq = new Prerequisite();
		prereq.setKind("align");
		prereq.setKey("LG");
		prereq.setOperator(PrerequisiteOperator.EQ);

		assertFalse(PrereqHandler.passes(prereq, character, null), "Abbrev LG should not match character's alignment of NG");

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREALIGN:NG");
		assertTrue(PrereqHandler.passes(prereq, character, null), "Abbrev NG should match character's alignment of NG");
		prereq = factory.parse("PREALIGN:LG");
		assertFalse(PrereqHandler.passes(prereq, character, null), "Abbrev LG should not match character's alignment of NG");
	}

	/**
	 * Tests that this only passes if the character's alignment matches his
	 * diety's alignment.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	void testDeity() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();
		AlignmentCompat.setCurrentAlignment(character.getCharID(), ng);
		ChannelUtilities.setControlledChannel(character.getCharID(),
			CControl.DEITYINPUT, deity);
		assertEquals(deity, ChannelUtilities.readControlledChannel(character.getCharID(),
				CControl.DEITYINPUT), "Deity should have been set for character.");

		final PreParserFactory factory = PreParserFactory.getInstance();
		Prerequisite prereq = factory.parse("PREALIGN:Deity");

		assertTrue(PrereqHandler.passes(prereq, character, null), "Number 3 should match deity's alignment of NG");

		AlignmentCompat.setCurrentAlignment(character.getCharID(), cg);

		assertFalse(PrereqHandler.passes(prereq, character, null), "Number 6 should not match deity's alignment of NG");
	}

	@Test
	void testMulti() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		AlignmentCompat.setCurrentAlignment(character.getCharID(), ng);

		final PreParserFactory factory = PreParserFactory.getInstance();
		Prerequisite prereq = factory.parse("PREALIGN:LE,NG,NE");
		assertTrue(PrereqHandler.passes(prereq, character, null), "LE, NG, or NE should match character's alignment of NG");

		prereq = factory.parse("PREALIGN:LE,NE,CE");
		assertFalse(PrereqHandler.passes(prereq, character, null), "LE, NE, or CE should not match character's alignment of NG");
	}

	@BeforeEach
    @Override
	protected void setUp() throws Exception
	{
		super.setUp();
		deity = new Deity();
		deity.setName("TestDeity");
		deity.put(ObjectKey.ALIGNMENT, CDOMDirectSingleRef.getRef(ng));
	}
}
