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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import pcgen.AbstractCharacterTestCase;
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
 * {@code PreAlignTest} tests that the PREALIGN tag is
 * working correctly.
 */
public class PreAlignTest extends AbstractCharacterTestCase
{
	private Deity deity;

	/**
	 * Test that negative (!) alignment checks work correctly in Align tests.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	public void testNegative() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();
		AlignmentCompat.setCurrentAlignment(character.getCharID(), ng);

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("!PREALIGN:TN");

		assertTrue("Not TN should match character's alignment of NG",
			PrereqHandler.passes(prereq, character, null));

		prereq = factory.parse("!PREALIGN:NG");

		assertFalse("Not TN should not match character's alignment of NG",
			PrereqHandler.passes(prereq, character, null));
	}

	/**
	 * Test that alignment abbreviation values work correctly in Align tests.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	public void testAbbrev() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();
		AlignmentCompat.setCurrentAlignment(character.getCharID(), ng);

		Prerequisite prereq = new Prerequisite();
		prereq.setKind("align");
		prereq.setKey("NG");
		prereq.setOperator(PrerequisiteOperator.EQ);

		assertTrue("Abbrev NG should match character's alignment of NG",
			PrereqHandler.passes(prereq, character, null));

		prereq = new Prerequisite();
		prereq.setKind("align");
		prereq.setKey("LG");
		prereq.setOperator(PrerequisiteOperator.EQ);

		assertFalse("Abbrev LG should not match character's alignment of NG",
			PrereqHandler.passes(prereq, character, null));

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREALIGN:NG");
		assertTrue("Abbrev NG should match character's alignment of NG",
			PrereqHandler.passes(prereq, character, null));
		prereq = factory.parse("PREALIGN:LG");
		assertFalse("Abbrev LG should not match character's alignment of NG",
			PrereqHandler.passes(prereq, character, null));
	}

	/**
	 * Tests that this only passes if the character's alignment matches his
	 * diety's alignment.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	public void testDeity() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();
		AlignmentCompat.setCurrentAlignment(character.getCharID(), ng);
		DeityCompat.setCurrentDeity(character.getCharID(), deity);
		assertEquals("Deity should have been set for character.", deity,
			DeityCompat.getCurrentDeity(character.getCharID()));

		final PreParserFactory factory = PreParserFactory.getInstance();
		Prerequisite prereq = factory.parse("PREALIGN:Deity");

		assertTrue("Number 3 should match deity's alignment of NG",
			PrereqHandler.passes(prereq, character, null));

		AlignmentCompat.setCurrentAlignment(character.getCharID(), cg);

		assertFalse("Number 6 should not match deity's alignment of NG",
			PrereqHandler.passes(prereq, character, null));
	}

	@Test
	public void testMulti() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		AlignmentCompat.setCurrentAlignment(character.getCharID(), ng);

		final PreParserFactory factory = PreParserFactory.getInstance();
		Prerequisite prereq = factory.parse("PREALIGN:LE,NG,NE");
		assertTrue("LE, NG, or NE should match character's alignment of NG",
			PrereqHandler.passes(prereq, character, null));

		prereq = factory.parse("PREALIGN:LE,NE,CE");
		assertFalse(
			"LE, NE, or CE should not match character's alignment of NG",
			PrereqHandler.passes(prereq, character, null));
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
