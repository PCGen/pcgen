/*
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
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
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.RuleCheck;
import pcgen.core.SettingsHandler;
import pcgen.persistence.PersistenceLayerException;
import plugin.pretokens.parser.PreRuleParser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code PreRuleTest} checks the function of the rule
 * prereq tester.
 */
@SuppressWarnings("nls")
public class PreRuleTest extends AbstractCharacterTestCase
{
	@BeforeEach
	@Override
	protected void setUp() throws Exception
	{
		// TODO Auto-generated method stub
		super.setUp();
		RuleCheck preRule = new RuleCheck();
		preRule.setName("PRERULE");
		preRule.setDefault(false);
		GameMode gameMode = SettingsHandler.getGameAsProperty().get();
		gameMode.getModeContext().getReferenceContext().importObject(preRule);
	}

	@Test
	public void testRule() throws Exception
	{
		// if ruleEnabled is launch before disabled, the disabled assert are wrong.
		ruleDisabled();
		ruleEnabled();
	}

	/**
	 * Test to ensure that we return false when races don't match.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	private void ruleDisabled() throws PersistenceLayerException
	{
		assertFalse("Our rule should start as false", Globals
			.checkRule("PRERULE"));

		PreRuleParser parser = new PreRuleParser();
		Prerequisite prereq = parser.parse("RULE", "1,PRERULE", false, false);

		boolean passes = PrereqHandler.passes(prereq, getCharacter(), null);
		assertFalse("PreRule should fail when rule is disabled.", passes);

		prereq = parser.parse("RULE", "1,PRERULE", true, false);
		passes = PrereqHandler.passes(prereq, getCharacter(), null);
		assertTrue("!PreRule should pass when rule is disabled.", passes);
	}

	/**
	 * Test to ensure that we return false when races don't match.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	private void ruleEnabled() throws PersistenceLayerException
	{
		RuleCheck preRule = SettingsHandler.getGameAsProperty().get().getModeContext().getReferenceContext()
				.silentlyGetConstructedCDOMObject(RuleCheck.class, "PRERULE");
		preRule.setDefault(true);
		
		assertTrue("Our rule should now be true", Globals
			.checkRule("PRERULE"));

		PreRuleParser parser = new PreRuleParser();
		Prerequisite prereq = parser.parse("RULE", "1,PRERULE", false, false);

		boolean passes = PrereqHandler.passes(prereq, getCharacter(), null);
		assertTrue("PreRule should pass when rule is enabled.", passes);

		prereq = parser.parse("RULE", "1,PRERULE", true, false);
		passes = PrereqHandler.passes(prereq, getCharacter(), null);
		assertFalse("!PreRule should fail when rule is enabled.", passes);
	}
}
