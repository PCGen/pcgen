/*
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pcgen.core.prereq;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.SimpleAssociatedObject;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Vision;
import pcgen.util.enumeration.VisionType;

import org.junit.jupiter.api.Test;

/**
 * Tests PREVISION token
 */
public class PreVisionTest extends AbstractCharacterTestCase
{
    @Test
    public void testVision2Pass()
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


    @Test
    public void testVisionFail()
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

    @Test
    public void testVisionPass()
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


    @Test
    public void testVisionPresentTen()
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

    @Test
    public void testVisionNotPresentTen()
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

    @Test
    public void testVisionPresentZero()
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

    @Test
    public void testVisionNotPresentZero()
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

    @Test
    public void testVisionZeroTen()
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

    @Test
    public void testVisionNotZeroTen()
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

    @Test
    public void testVisionZeroZero()
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

    @Test
    public void testVisionNotZeroZero()
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
