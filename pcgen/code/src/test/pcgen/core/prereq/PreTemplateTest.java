/*
 * Copyright 2004 (C) Chris Ward <frugal@purplewombat.co.uk>
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
import pcgen.core.Globals;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import plugin.pretokens.test.PreTemplateTester;

public class PreTemplateTest extends AbstractCharacterTestCase
{
	public static void main(final String[] args)
	{
		TestRunner.run(PreTemplateTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreTemplateTest.class);
	}

	/**
	 * Ensure a character with a template correctly passes
	 * PRETEMPLATE
	 */
	public void test990007_1()
	{
		final PlayerCharacter character = getCharacter();

		final PCTemplate template = new PCTemplate();
		template.setName("Half-Dragon");
		Globals.getContext().getReferenceContext().importObject(template);
		character.addTemplate(template);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("template");
		prereq.setKey("half-dragon");
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("1");

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}

	/**
	 * Ensure that a character with no templates correctly
	 * passes !PRETEMPLATE
	 */
	public void test990007_2()
	{
		final PlayerCharacter character = getCharacter();

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("template");
		prereq.setKey("half-dragon");
		prereq.setOperator(PrerequisiteOperator.LT);
		prereq.setOperand("1");

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}

	/**
	 * Ensure that a character with templates, but not the
	 * required template correctly passes !PRETEMPLATE
	 */
	public void test990007_3()
	{
		final PlayerCharacter character = getCharacter();

		final PCTemplate template = new PCTemplate();
		template.setName("Half-Celestial");
		Globals.getContext().getReferenceContext().importObject(template);
		character.addTemplate(template);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("template");
		prereq.setKey("half-dragon");
		prereq.setOperator(PrerequisiteOperator.LT);
		prereq.setOperand("1");

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}

	/**
	 * Ensure a character with the requested template correctly fails
	 * !PRETEMPLATE
	 */
	public void test990007_4()
	{
		final PlayerCharacter character = getCharacter();

		final PCTemplate template = new PCTemplate();
		template.setName("Half-Dragon");
		Globals.getContext().getReferenceContext().importObject(template);
		character.addTemplate(template);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("template");
		prereq.setKey("half-dragon");
		prereq.setOperator(PrerequisiteOperator.LT);
		prereq.setOperand("1");

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);
	}

	/**
	 * Ensure a character with the requested template correctly passes
	 * a wildcard test
	 */
	public void test990007_5()
	{
		final PlayerCharacter character = getCharacter();

		final PCTemplate template = new PCTemplate();
		template.setName("Half-Dragon");
		Globals.getContext().getReferenceContext().importObject(template);
		character.addTemplate(template);

		final PCTemplate template2 = new PCTemplate();
		template2.setName("Half-Celestial");
		Globals.getContext().getReferenceContext().importObject(template2);
		character.addTemplate(template2);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("template");
		prereq.setKey("half%");
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("1");

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}

	/**
	 * Ensure a test with a non integer operand fails with a meaningfull
	 * error message
	 */
	public void testBadOperand()
	{
		final PlayerCharacter character = getCharacter();

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("template");
		prereq.setKey("half%");
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("One");

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);
	}

	public void testKindHandled()
	{
		final PreTemplateTester preTemplate = new PreTemplateTester();

		assertEquals("TEMPLATE", preTemplate.kindHandled());
	}

}
