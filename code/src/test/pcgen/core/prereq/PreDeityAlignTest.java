/*
 * Copyright 2005 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * {@code PreDeityAlignTest} tests that the PREDEITYALIGN tag is
 * working correctly.
 */
public class PreDeityAlignTest extends AbstractCharacterTestCase
{
	private Deity deity;

	/**
	 * Test that alignment abbreviation values work correctly in Deity Align tests.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	public void testAbbrev() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();
		AlignmentCompat.setCurrentAlignment(character.getCharID(), ng);
		ChannelUtilities.setControlledChannel(character.getCharID(),
			CControl.DEITYINPUT, deity);
		assertEquals("Deity should have been set for character.", deity,
			ChannelUtilities.readControlledChannel(
				character.getCharID(), CControl.DEITYINPUT));

		Prerequisite prereq = new Prerequisite();
		prereq.setKind("deityAlign");
		prereq.setOperand("NG");
		prereq.setOperator(PrerequisiteOperator.EQ);

		assertTrue("Abbrev NG should match deity's alignment of NG",
			PrereqHandler.passes(prereq, character, null));

		prereq = new Prerequisite();
		prereq.setKind("deityAlign");
		prereq.setOperand("LG");
		prereq.setOperator(PrerequisiteOperator.EQ);

		assertFalse("Abbrev LG should not match deity's alignment of NG",
			PrereqHandler.passes(prereq, character, null));

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDEITYALIGN:NG");
		assertTrue("Abbrev NG should match deity's alignment of NG",
			PrereqHandler.passes(prereq, character, null));
		prereq = factory.parse("PREDEITYALIGN:LG");
		assertFalse("Abbrev LG should not match deity's alignment of NG",
			PrereqHandler.passes(prereq, character, null));
	}

	@BeforeEach
    @Override
    public void setUp() throws Exception
	{
		super.setUp();
		deity = new Deity();
		deity.setName("TestDeity");
		deity.put(ObjectKey.ALIGNMENT, CDOMDirectSingleRef.getRef(ng));
	}

}
