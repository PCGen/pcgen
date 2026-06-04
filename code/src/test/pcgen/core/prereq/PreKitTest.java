/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
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
import pcgen.core.Kit;
import pcgen.core.PlayerCharacter;
import pcgen.util.TestHelper;
import plugin.pretokens.test.PreKitTester;

import org.junit.jupiter.api.Test;

/**
 * PreKitTest verifies the behaviour of PREKIT processing.
 */
class PreKitTest extends AbstractCharacterTestCase
{
	/**
	 * Ensure a character with a kit correctly passes
	 * PREKIT
	 */
	@Test
	public void testKitPresence()
	{
		final PlayerCharacter character = getCharacter();

		Kit kit = TestHelper.makeKit("Dungeoneering Kit (Common)");
		// Note this is a shortcut rather than doing a full apply of the kit
		character.addKit(kit);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("kit");
		prereq.setKey("KEY_Dungeoneering Kit (Common)");
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("1");

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes, "Expected kit to be present");
	}

	/**
	 * Ensure that a character with no templates correctly
	 * passes !PRETEMPLATE
	 */
	@Test
	public void testKitAbsence()
	{
		final PlayerCharacter character = getCharacter();

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("kit");
		prereq.setKey("KEY_Dungeoneering Kit (Common)");
		prereq.setOperator(PrerequisiteOperator.LT);
		prereq.setOperand("1");

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes, "Expected kit not to be present");
	}

	/**
	 * Ensure that a character with kits, but not the
	 * required kit correctly passes !PREKIT
	 */
	@Test
	public void testSpecificKitAbsent()
	{
		final PlayerCharacter character = getCharacter();

		Kit kit = TestHelper.makeKit("Default Werebear");
		// Note this is a shortcut rather than doing a full apply of the kit
		character.addKit(kit);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("kit");
		prereq.setKey("KEY_Dungeoneering Kit (Common)");
		prereq.setOperator(PrerequisiteOperator.LT);
		prereq.setOperand("1");

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes, "Expected kit not to be present");
	}

	/**
	 * Ensure a character with the requested kit correctly fails
	 * !PREKIT
	 */
	@Test
	public void testNotAbsent()
	{
		final PlayerCharacter character = getCharacter();

		Kit kit = TestHelper.makeKit("Dungeoneering Kit (Common)");
		// Note this is a shortcut rather than doing a full apply of the kit
		character.addKit(kit);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("kit");
		prereq.setKey("KEY_Dungeoneering Kit (Common)");
		prereq.setOperator(PrerequisiteOperator.LT);
		prereq.setOperand("1");

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes, "Expected kit to be present");
	}

	/**
	 * Ensure a character with the requested kit correctly passes
	 * a wildcard test
	 */
	@Test
	public void testWildcard()
	{
		final PlayerCharacter character = getCharacter();

		Kit kit = TestHelper.makeKit("Dungeoneering Kit (Common)");
		// Note this is a shortcut rather than doing a full apply of the kit
		character.addKit(kit);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("kit");
		prereq.setKey("key_dungeoneering%");
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("1");

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes, "Expected wildcard to match");
	}

	/**
	 * Ensure a test with a non integer operand fails with a meaningfull
	 * error message
	 */
	@Test
	public void testBadOperand()
	{
		final PlayerCharacter character = getCharacter();

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("kit");
		prereq.setKey("half%");
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("One");

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);
	}

	@Test
	public void testKindHandled()
	{
		final PreKitTester preKit = new PreKitTester();

		assertEquals("KIT", preKit.kindHandled());
	}

}
