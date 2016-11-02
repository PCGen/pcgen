/*
 * PreRuleTest.java
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
 *
 * Created on February 6, 2007
 *
 * Current Ver: $Revision: 1777 $
 *
 */
package pcgen.core.prereq;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.AbstractCharacterTestCase;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.RuleCheck;
import pcgen.core.SettingsHandler;
import plugin.pretokens.parser.PreRuleParser;

/**
 * <code>PreRuleTest</code> checks the function of the rule 
 * prereq tester.
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 */
@SuppressWarnings("nls")
public class PreRuleTest extends AbstractCharacterTestCase
{
	/**
	 * Runs the test.
	 * @param args
	 */
	public static void main(final String[] args)
	{
		TestRunner.run(PreRuleTest.class);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		// TODO Auto-generated method stub
		super.setUp();
		RuleCheck preRule = new RuleCheck();
		preRule.setName("PRERULE");
		preRule.setDefault(false);
		GameMode gameMode = SettingsHandler.getGame();
		gameMode.getModeContext().getReferenceContext().importObject(preRule);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		// TODO Auto-generated method stub
		super.tearDown();
	}

	/**
	 * Returns a TestSuite consisting of all the tests in this class.
	 * 
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreRuleTest.class);
	}
	
	public void testRule() throws Exception
	{
		// if ruleEnabled is launch before disabled, the disabled assert are wrong.
		ruleDisabled();
		ruleEnabled();
	}

	/**
	 * Test to ensure that we return false when races don't match.
	 * 
	 * @throws Exception
	 */
	public void ruleDisabled() throws Exception
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
	 * @throws Exception
	 */
	public void ruleEnabled() throws Exception
	{
		RuleCheck preRule = SettingsHandler.getGame().getModeContext().getReferenceContext()
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
