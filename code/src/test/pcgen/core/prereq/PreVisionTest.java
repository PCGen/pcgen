/*
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pcgen.core.prereq;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.SimpleAssociatedObject;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Vision;
import pcgen.util.enumeration.VisionType;

/**
 * Tests PREVISION token
 */
public class PreVisionTest extends AbstractCharacterTestCase
{

	/**
	 * Main
	 * 
	 * @param args
	 */
	public static void main(final String[] args)
	{
		TestRunner.run(PreVisionTest.class);
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
		template.putToList(Vision.VISIONLIST,
				CDOMDirectSingleRef.getRef(new Vision(VisionType
						.getVisionType("Darkvision"), FormulaFactory.getFormulaFor(60))),
				new SimpleAssociatedObject());
		character.addTemplate(template);

		final PCTemplate template2 = new PCTemplate();
		template2.putToList(Vision.VISIONLIST,
				CDOMDirectSingleRef.getRef(new Vision(VisionType
						.getVisionType("Low-Light"), FormulaFactory.getFormulaFor(30))),
				new SimpleAssociatedObject());
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
		template.putToList(Vision.VISIONLIST, CDOMDirectSingleRef
				.getRef(new Vision(VisionType.getVisionType("Normal"), FormulaFactory.getFormulaFor(60))),
				new SimpleAssociatedObject());

		character.addTemplate(template);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("vision");
		prereq.setKey("darkvision");
		prereq.setOperator(PrerequisiteOperator.GTEQ);
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
		template.putToList(Vision.VISIONLIST,
				CDOMDirectSingleRef.getRef(new Vision(VisionType
						.getVisionType("Darkvision"), FormulaFactory.getFormulaFor(60))),
				new SimpleAssociatedObject());

		character.addTemplate(template);

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
	public void testVisionPresentTen() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		final PCTemplate template = new PCTemplate();

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("vision");
		prereq.setKey("darkvision");
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("ANY");

		boolean passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);

		template.putToList(Vision.VISIONLIST,
				CDOMDirectSingleRef.getRef(new Vision(VisionType
						.getVisionType("Darkvision"), FormulaFactory.getFormulaFor(10))),
				new SimpleAssociatedObject());

		character.addTemplate(template);

		passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testVisionNotPresentTen() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		final PCTemplate template = new PCTemplate();

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("vision");
		prereq.setKey("darkvision");
		prereq.setOperator(PrerequisiteOperator.LT);
		prereq.setOperand("ANY");

		boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);

		template.putToList(Vision.VISIONLIST,
				CDOMDirectSingleRef.getRef(new Vision(VisionType
						.getVisionType("Darkvision"), FormulaFactory.getFormulaFor(10))),
				new SimpleAssociatedObject());

		character.addTemplate(template);

		passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testVisionPresentZero() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		final PCTemplate template = new PCTemplate();

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("vision");
		prereq.setKey("darkvision");
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("ANY");

		boolean passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);
		template.putToList(Vision.VISIONLIST,
				CDOMDirectSingleRef.getRef(new Vision(VisionType
						.getVisionType("Darkvision"), FormulaFactory.ZERO)),
				new SimpleAssociatedObject());

		character.addTemplate(template);

		passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testVisionNotPresentZero() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		final PCTemplate template = new PCTemplate();

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("vision");
		prereq.setKey("darkvision");
		prereq.setOperator(PrerequisiteOperator.LT);
		prereq.setOperand("ANY");

		boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);

		template.putToList(Vision.VISIONLIST,
				CDOMDirectSingleRef.getRef(new Vision(VisionType
						.getVisionType("Darkvision"), FormulaFactory.ZERO)),
				new SimpleAssociatedObject());

		character.addTemplate(template);

		passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testVisionZeroTen() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		final PCTemplate template = new PCTemplate();

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("vision");
		prereq.setKey("darkvision");
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("0");

		boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);

		template.putToList(Vision.VISIONLIST,
				CDOMDirectSingleRef.getRef(new Vision(VisionType
						.getVisionType("Darkvision"), FormulaFactory.getFormulaFor(10))),
				new SimpleAssociatedObject());

		character.addTemplate(template);
		passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testVisionNotZeroTen() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		final PCTemplate template = new PCTemplate();

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("vision");
		prereq.setKey("darkvision");
		prereq.setOperator(PrerequisiteOperator.LT);
		prereq.setOperand("0");

		boolean passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);

		template.putToList(Vision.VISIONLIST,
				CDOMDirectSingleRef.getRef(new Vision(VisionType
						.getVisionType("Darkvision"), FormulaFactory.getFormulaFor(10))),
				new SimpleAssociatedObject());

		character.addTemplate(template);

		passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testVisionZeroZero() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		final PCTemplate template = new PCTemplate();

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("vision");
		prereq.setKey("darkvision");
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("0");

		boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);

		template.putToList(Vision.VISIONLIST,
				CDOMDirectSingleRef.getRef(new Vision(VisionType
						.getVisionType("Darkvision"), FormulaFactory.ZERO)),
				new SimpleAssociatedObject());

		character.addTemplate(template);
		passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testVisionNotZeroZero() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		final PCTemplate template = new PCTemplate();

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("vision");
		prereq.setKey("darkvision");
		prereq.setOperator(PrerequisiteOperator.LT);
		prereq.setOperand("0");

		boolean passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);

		template.putToList(Vision.VISIONLIST,
				CDOMDirectSingleRef.getRef(new Vision(VisionType
						.getVisionType("Darkvision"), FormulaFactory.ZERO)),
				new SimpleAssociatedObject());

		character.addTemplate(template);

		passes = PrereqHandler.passes(prereq, character, null);
		assertFalse(passes);
	}

}
