/*
 * Created on 22-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pcgen.core.prereq;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;


public class PreVisionTest extends AbstractCharacterTestCase
{

	public static void main(final String[] args)
	{
		junit.swingui.TestRunner.run(PreVisionTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreVisionTest.class);
	}

	/**
	 * @throws Exception
	 */
	public void testVision2Pass() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final PCTemplate template = new PCTemplate();
		template.setVision("Darkvision (60')", character);
		character.addTemplate(template);

		final PCTemplate template2 = new PCTemplate();
		template2.setVision("Low-light (30')", character);
		character.addTemplate(template2);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("vision");
		prereq.setKey("darkvision");
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("30");

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testVisionFail() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final PCTemplate template = new PCTemplate();
		template.setVision("Normal (60')", character);

		character.addTemplate(template);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("vision");
		prereq.setKey("darkvision");
		prereq.setOperator("gteq");
		prereq.setOperand("30");

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testVisionPass() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		final PCTemplate template = new PCTemplate();
		template.setVision("Darkvision (60')", character);

		character.addTemplate(template);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("vision");
		prereq.setKey("darkvision");
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("30");

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}

}
