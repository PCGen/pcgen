/*
 * PreSkillTest.java
 *
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
 *
 * Created on 12-Jan-2004
 *
 * Current Ver: $Revision$
 *
 * Last Editor: $Author$
 *
 * Last Edited: $Date$
 *
 */
package pcgen.core.prereq;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.list.ClassSkillList;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.persistence.lst.prereq.PreParserFactory;

public class PreSkillTest extends AbstractCharacterTestCase
{

	public static void main(final String[] args)
	{
		TestRunner.run(PreSkillTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreSkillTest.class);
	}

	Skill balance = null;
	Skill knowledge = null;
	Skill tumble = null;
	Skill fake = null;
	Skill fake2 = null;
	Skill target = null;
	Skill target2 = null;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		final PlayerCharacter character = getCharacter();

		final PCClass myClass = new PCClass();
		myClass.setName("My Class");

		ClassSkillList csl = new ClassSkillList();
		csl.put(StringKey.NAME, "MyClass");
		
		knowledge = new Skill();
		knowledge.addToListFor(ListKey.CLASSES, CDOMDirectSingleRef.getRef(csl));
		knowledge.setName("KNOWLEDGE (ARCANA)");
		knowledge.setTypeInfo("KNOWLEDGE.INT");
		knowledge.modRanks(8.0, myClass, true, character);
		character.addSkill(knowledge);
		Globals.getContext().ref.importObject(knowledge);

		tumble = new Skill();
		tumble.addToListFor(ListKey.CLASSES, CDOMDirectSingleRef.getRef(csl));
		tumble.setName("Tumble");
		tumble.setTypeInfo("DEX");
		tumble.modRanks(8.0, myClass, true, character);
		character.addSkill(tumble);
		Globals.getContext().ref.importObject(tumble);

		balance = new Skill();
		balance.addToListFor(ListKey.CLASSES, CDOMDirectSingleRef.getRef(csl));
		balance.setName("Balance");
		balance.setTypeInfo("DEX");
		balance.modRanks(4.0, myClass, true, character);
		character.addSkill(balance);
		Globals.getContext().ref.importObject(balance);
		
		fake = new Skill();
		fake.addToListFor(ListKey.CLASSES, CDOMDirectSingleRef.getRef(csl));
		fake.setName("Fake");
		fake.setTypeInfo("WIS");
		fake.modRanks(6.0, myClass, true, character);
		character.addSkill(fake);
		Globals.getContext().ref.importObject(fake);
		
		fake2 = new Skill();
		fake2.addToListFor(ListKey.CLASSES, CDOMDirectSingleRef.getRef(csl));
		fake2.setName("Fake 2");
		fake2.setTypeInfo("INT");
		fake2.modRanks(8.0, myClass, true, character);
		character.addSkill(fake2);
		Globals.getContext().ref.importObject(fake2);
		
		target = new Skill();
		target.addToListFor(ListKey.CLASSES, CDOMDirectSingleRef.getRef(csl));
		target.setName("Target");
		target.setTypeInfo("STR");
		Globals.getContext().ref.importObject(target);
		
		target2 = new Skill();
		target2.addToListFor(ListKey.CLASSES, CDOMDirectSingleRef.getRef(csl));
		target2.setName("Target2");
		target2.setTypeInfo("STR");
		Globals.getContext().ref.importObject(target2);
		
		fake.putServesAs(target.getDisplayName(), "");		
		fake.putServesAs(target2.getDisplayName(), "");		

	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		knowledge = null;
		balance = null;
		tumble = null;

		super.tearDown();
	}

	/**
	 * @throws Exception
	 */
	public void testDexType() throws Exception
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

	/**
	 * @throws Exception
	 */
	public void testDexTypeEqualsFails() throws Exception
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

	/**
	 * @throws Exception
	 */
	public void testDexTypeEqualsPasses() throws Exception
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

	/**
	 * @throws Exception
	 */
	public void testKnowedgeSubType() throws Exception
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

	/**
	 * @throws Exception
	 */
	public void testKnowedgeSubTypeFail() throws Exception
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

	/**
	 * @throws Exception
	 */
	public void testKnowedgeSubTypePasesExact() throws Exception
	{
		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("skill");
		prereq.setKey("knowledge");
		prereq.setSubKey("arcana");
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("8");

		final PlayerCharacter character = getCharacter();
		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testKnowedgeType() throws Exception
	{
		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("skill");
		prereq.setKey("TYPE.knowledge");
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		prereq.setOperand("2");

		final PlayerCharacter character = getCharacter();
		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);
	}

	/**
	 * @throws Exception
	 */
	public void testKnowedgeWrongSubType() throws Exception
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

	/**
	 * @throws Exception
	 */
	public void testPass() throws Exception
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

	/**
	 * @throws Exception
	 */
	public void testTotalType() throws Exception
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

	/**
	 * @throws Exception
	 */
	public void testType() throws Exception
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


	public void testLevelsTwoClasses() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final PreParserFactory factory = PreParserFactory.getInstance();
		Prerequisite prereq = factory.parse("PRESKILL:2,Balance=4,Tumble=2");

		assertEquals(true, PrereqHandler.passes(prereq, character, null));
	}
	public void testServesAsExactMatch() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final PreParserFactory factory = PreParserFactory.getInstance();
		Prerequisite prereq = factory.parse("PRESKILL:2,Target,Target2=4");
		assertEquals(true, PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PRESKILL:1,Target,Target2=5");
		assertEquals(true, PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PRESKILL:2,Target,Target2=7");
		assertEquals(false, PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PRESKILL:2,Target=4,Target2=7");
		assertEquals(false, PrereqHandler.passes(prereq, character, null));
	}
	public void testServesAsTypeMatch() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final PreParserFactory factory = PreParserFactory.getInstance();
		Prerequisite prereq = factory.parse("PRESKILL:1,TYPE.INT=4");
		assertEquals(true, PrereqHandler.passes(prereq, character, null));
	
		prereq = factory.parse("PRESKILL:1,TYPE.STR=6");
		assertEquals(true, PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PRESKILL:1,TYPE.ST%=6");
		assertEquals(true, PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PRESKILL:1,TYPE.STR=7");
		assertEquals(false, PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PRESKILL:1,TYPE.ST%=7");
		assertEquals(false, PrereqHandler.passes(prereq, character, null));
		
		
		prereq = factory.parse("PRESKILL:1,TYPE.CHA=1");
		assertEquals(false, PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PRESKILL:1,TYPE.CH%=7");
		assertEquals(false, PrereqHandler.passes(prereq, character, null));
	}
	
	public void testServesAsTotalsMatch() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final PreParserFactory factory = PreParserFactory.getInstance();
		
		Prerequisite prereq = factory.parse("PRESKILLTOT:Tumble,Target=18");
		assertEquals(false, PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PRESKILLTOT:Tumble,Target2=14");
		assertEquals(true, PrereqHandler.passes(prereq, character, null));
		
		prereq = factory.parse("PRESKILLTOT:Foo,Target=40");
		assertEquals(false, PrereqHandler.passes(prereq, character, null));
		

	}
}
