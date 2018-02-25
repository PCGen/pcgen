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

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Deity;
import pcgen.core.PlayerCharacter;
import pcgen.output.channel.ChannelCompatibility;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * <code>PreDeityAlignTest</code> tests that the PREDEITYALIGN tag is 
 * working correctly.
 */
public class PreDeityAlignTest extends AbstractCharacterTestCase
{
	private Deity deity;

	public static void main(final String[] args)
	{
		TestRunner.run(PreDeityAlignTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreDeityAlignTest.class);
	}

	/**
	 * Test that alignment abbreviation values work correctly in Deity Align tests.
	 * @throws Exception
	 */
	public void testAbbrev() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		ChannelCompatibility.setCurrentAlignment(character.getCharID(), ng);
		character.setDeity(deity);
		assertEquals("Deity should have been set for character.", deity,
			character.getDeity());

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

    @Override
	protected void setUp() throws Exception
	{
		super.setUp();
		deity = new Deity();
		deity.setName("TestDeity");
		deity.put(ObjectKey.ALIGNMENT, CDOMDirectSingleRef.getRef(ng));
	}

    @Override
	protected void tearDown() throws Exception
	{
		// TODO Auto-generated method stub
		super.tearDown();
	}
}
