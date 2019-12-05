/*
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.analysis.SkillRankControl;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.LoadContext;
import pcgen.util.TestHelper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PreSkillTest extends AbstractCharacterTestCase
{
    private Skill balance = null;
    private Skill knowledge = null;
    private Skill tumble = null;

    @BeforeEach
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        final PlayerCharacter character = getCharacter();

        final PCClass myClass = new PCClass();
        myClass.setName("My Class");

        LoadContext context = Globals.getContext();

        knowledge = new Skill();
        context.unconditionallyProcess(knowledge, "CLASSES", "MyClass");
        knowledge.setName("KNOWLEDGE (ARCANA)");
        TestHelper.addType(knowledge, "KNOWLEDGE.INT");
        context.getReferenceContext().importObject(knowledge);
        SkillRankControl.modRanks(6.0, myClass, true, character, knowledge);

        Skill knowledge2 = new Skill();
        context.unconditionallyProcess(knowledge2, "CLASSES", "MyClass");
        knowledge2.setName("KNOWLEDGE (NATURE)");
        TestHelper.addType(knowledge2, "KNOWLEDGE.INT");
        context.getReferenceContext().importObject(knowledge2);
        SkillRankControl.modRanks(8.0, myClass, true, character, knowledge2);

        tumble = new Skill();
        context.unconditionallyProcess(tumble, "CLASSES", "MyClass");
        tumble.setName("Tumble");
        tumble.addToListFor(ListKey.TYPE, Type.getConstant("DEX"));
        context.getReferenceContext().importObject(tumble);
        SkillRankControl.modRanks(8.0, myClass, true, character, tumble);

        balance = new Skill();
        context.unconditionallyProcess(balance, "CLASSES", "MyClass");
        balance.setName("Balance");
        balance.addToListFor(ListKey.TYPE, Type.getConstant("DEX"));
        context.getReferenceContext().importObject(balance);
        SkillRankControl.modRanks(4.0, myClass, true, character, balance);

        Skill target = new Skill();
        context.unconditionallyProcess(target, "CLASSES", "MyClass");
        target.setName("Target");
        target.addToListFor(ListKey.TYPE, Type.getConstant("STR"));
        context.getReferenceContext().importObject(target);

        Skill target2 = new Skill();
        context.unconditionallyProcess(target2, "CLASSES", "MyClass");
        target2.setName("Target2");
        target2.addToListFor(ListKey.TYPE, Type.getConstant("STR"));
        context.getReferenceContext().importObject(target2);

        Skill fake = new Skill();
        context.unconditionallyProcess(fake, "CLASSES", "MyClass");
        fake.setName("Fake");
        fake.addToListFor(ListKey.TYPE, Type.getConstant("WIS"));
        fake.addToListFor(ListKey.SERVES_AS_SKILL, CDOMDirectSingleRef.getRef(target));
        fake.addToListFor(ListKey.SERVES_AS_SKILL, CDOMDirectSingleRef.getRef(target2));
        context.getReferenceContext().importObject(fake);
        SkillRankControl.modRanks(6.0, myClass, true, character, fake);

        Skill fake2 = new Skill();
        context.unconditionallyProcess(fake2, "CLASSES", "MyClass");
        fake2.setName("Fake 2");
        fake2.addToListFor(ListKey.TYPE, Type.getConstant("INT"));
        context.getReferenceContext().importObject(fake2);
        SkillRankControl.modRanks(8.0, myClass, true, character, fake2);

        context.getReferenceContext().buildDerivedObjects();
        context.getReferenceContext().resolveReferences(null);
    }

    @AfterEach
    @Override
    public void tearDown() throws Exception
    {
        knowledge = null;
        balance = null;
        tumble = null;

        super.tearDown();
    }

    @Test
    void testDexType()
    {
        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("skill");
        prereq.setKey("TYPE.DEX");
        prereq.setOperator(PrerequisiteOperator.GTEQ);
        prereq.setOperand("5");

        final PlayerCharacter character = getCharacter();
        final boolean passes = PrereqHandler.passes(prereq, character, null);
        assertTrue(passes);
    }

    @Test
    void testDexTypeEqualsFails()
    {
        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("skill");
        prereq.setKey("TYPE.DEX");
        prereq.setOperator(PrerequisiteOperator.EQ);
        prereq.setOperand("5");

        final PlayerCharacter character = getCharacter();
        final boolean passes = PrereqHandler.passes(prereq, character, null);
        assertFalse(passes);
    }

    @Test
    void testDexTypeEqualsPasses()
    {
        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("skill");
        prereq.setKey("TYPE.DEX");
        prereq.setOperator(PrerequisiteOperator.EQ);
        prereq.setOperand("8");

        final PlayerCharacter character = getCharacter();
        final boolean passes = PrereqHandler.passes(prereq, character, null);
        assertTrue(passes);
    }

    @Test
    void testKnowedgeSubType()
    {
        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("skill");
        prereq.setKey("knowledge");
        prereq.setSubKey("arcana");
        prereq.setOperator(PrerequisiteOperator.GTEQ);
        prereq.setOperand("2");

        final PlayerCharacter character = getCharacter();

        final boolean passes = PrereqHandler.passes(prereq, character, null);
        assertTrue(passes);
    }

    @Test
    void testKnowedgeSubTypeFail()
    {
        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("skill");
        prereq.setKey("knowledge");
        prereq.setSubKey("arcana");
        prereq.setOperator(PrerequisiteOperator.GTEQ);
        prereq.setOperand("9");

        final PlayerCharacter character = getCharacter();
        final boolean passes = PrereqHandler.passes(prereq, character, null);
        assertFalse(passes);
    }

    @Test
    void testKnowedgeSubTypePasesExact()
    {
        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("skill");
        prereq.setKey("knowledge");
        prereq.setSubKey("arcana");
        prereq.setOperator(PrerequisiteOperator.GTEQ);
        prereq.setOperand("6");

        final PlayerCharacter character = getCharacter();
        final boolean passes = PrereqHandler.passes(prereq, character, null);
        assertTrue(passes);
    }

    @Test
    void testKnowedgeType()
    {
        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("skill");
        prereq.setKey("TYPE.knowledge");
        prereq.setOperator(PrerequisiteOperator.GTEQ);
        prereq.setOperand("8");

        final PlayerCharacter character = getCharacter();
        final boolean passes = PrereqHandler.passes(prereq, character, null);
        assertTrue(passes);
    }

    @Test
    public void testKnowedgeWrongSubType()
    {
        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("skill");
        prereq.setKey("knowledge");
        prereq.setSubKey("religion");
        prereq.setOperator(PrerequisiteOperator.GTEQ);
        prereq.setOperand("8");

        final PlayerCharacter character = getCharacter();
        final boolean passes = PrereqHandler.passes(prereq, character, null);
        assertFalse(passes);
    }

    @Test
    public void testPass()
    {
        final PlayerCharacter character = getCharacter();

        final PCClass myClass = new PCClass();
        myClass.setName("My Class");

        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("skill");
        prereq.setKey("tumble");
        prereq.setOperator(PrerequisiteOperator.GTEQ);
        prereq.setOperand("2");

        final boolean passes = PrereqHandler.passes(prereq, character, null);
        assertTrue(passes);
    }

    @Test
    void testTotalType()
    {
        //		PreSkillTotalParser producer = new PreSkillTotalParser();

        //		Prerequisite prereq = producer.parse("SKILLTOT","TYPE.Knowledge=20", false, false);

        final Prerequisite prereq = new Prerequisite();
        prereq.setOperator(PrerequisiteOperator.GTEQ);
        prereq.setOperand("10");

        final Prerequisite subreq = new Prerequisite();
        subreq.setKind("skill");
        subreq.setKey("TYPE.DEX");
        subreq.setOperator(PrerequisiteOperator.GTEQ);
        subreq.setOperand("1");
        subreq.setTotalValues(true);

        prereq.addPrerequisite(subreq);

        final PlayerCharacter character = getCharacter();
        final boolean passes = PrereqHandler.passes(prereq, character, null);
        assertTrue(passes);
    }

    @Test
    void testType()
    {
        final Prerequisite subreq = new Prerequisite();
        subreq.setKind("skill");
        subreq.setKey("TYPE.DEX");
        subreq.setOperator(PrerequisiteOperator.GTEQ);
        subreq.setOperand("7");
        subreq.setTotalValues(true);

        final PlayerCharacter character = getCharacter();
        final boolean passes = PrereqHandler.passes(subreq, character, null);
        assertTrue(passes);
    }

    @Test
    void testLevelsTwoClasses() throws Exception
    {
        final PlayerCharacter character = getCharacter();

        final PreParserFactory factory = PreParserFactory.getInstance();
        Prerequisite prereq = factory.parse("PRESKILL:2,Balance=4,Tumble=2");

        assertTrue(PrereqHandler.passes(prereq, character, null));
    }

    @Test
    void testServesAsExactMatch() throws Exception
    {
        final PlayerCharacter character = getCharacter();

        final PreParserFactory factory = PreParserFactory.getInstance();
        Prerequisite prereq = factory.parse("PRESKILL:2,Target=4,Target2=4");
        assertTrue(PrereqHandler.passes(prereq, character, null));

        prereq = factory.parse("PRESKILL:1,Target=5,Target2=5");
        assertTrue(PrereqHandler.passes(prereq, character, null));

        prereq = factory.parse("PRESKILL:2,Target=7,Target2=7");
        assertFalse(PrereqHandler.passes(prereq, character, null));

        prereq = factory.parse("PRESKILL:2,Target=4,Target2=7");
        assertFalse(PrereqHandler.passes(prereq, character, null));
    }

    @Test
    void testServesAsTypeMatch() throws Exception
    {
        final PlayerCharacter character = getCharacter();

        final PreParserFactory factory = PreParserFactory.getInstance();
        Prerequisite prereq = factory.parse("PRESKILL:1,TYPE.INT=4");
        assertTrue(PrereqHandler.passes(prereq, character, null));

        prereq = factory.parse("PRESKILL:1,TYPE.STR=6");
        assertTrue(PrereqHandler.passes(prereq, character, null));

        prereq = factory.parse("PRESKILL:1,TYPE.ST%=6");
        assertTrue(PrereqHandler.passes(prereq, character, null));

        prereq = factory.parse("PRESKILL:1,TYPE.STR=7");
        assertFalse(PrereqHandler.passes(prereq, character, null));

        prereq = factory.parse("PRESKILL:1,TYPE.ST%=7");
        assertFalse(PrereqHandler.passes(prereq, character, null));


        prereq = factory.parse("PRESKILL:1,TYPE.CHA=1");
        assertFalse(PrereqHandler.passes(prereq, character, null));

        prereq = factory.parse("PRESKILL:1,TYPE.CH%=7");
        assertFalse(PrereqHandler.passes(prereq, character, null));
    }

    @Test
    void testServesAsTotalsMatch() throws Exception
    {
        final PlayerCharacter character = getCharacter();

        final PreParserFactory factory = PreParserFactory.getInstance();

        Prerequisite prereq = factory.parse("PRESKILLTOT:Tumble,Target=18");
        assertFalse(PrereqHandler.passes(prereq, character, null));

        prereq = factory.parse("PRESKILLTOT:Tumble,Target2=14");
        assertTrue(PrereqHandler.passes(prereq, character, null));

        prereq = factory.parse("PRESKILLTOT:Foo,Target=40");
        assertFalse(PrereqHandler.passes(prereq, character, null));


    }
}
