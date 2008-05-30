/*
 * PreCSkillTest.java
 *
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 *
 */
package pcgen.core.prereq;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * <code>PreCSkillTest</code> tests that the PRECSKILL tag is
 * working correctly.
 *
 * Last Editor: $Author: $
 * Last Edited: $Date$
 *
 * @author Aaron Divinsky <boomer70@yahoo.com>
 * @version $Revision$
 */
public class PreCSkillTest extends AbstractCharacterTestCase
{
	PCClass myClass = new PCClass();

	public static void main(final String[] args)
	{
		TestRunner.run(PreCSkillTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreCSkillTest.class);
	}

	/**
	 * Test that CSkill works
	 * @throws Exception
	 */
	public void testCSkill() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		character.incrementClassLevel(1, myClass, true);

		myClass = character.getClassKeyed("My Class");

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PRECSKILL:1,Spot,Listen");

		assertFalse("Character has no class skills", PrereqHandler.passes(
			prereq, character, null));

		myClass.addSkillToList("Spot");

		assertTrue("Character has spot class skills", PrereqHandler.passes(
			prereq, character, null));

		myClass.addCSkill("Spy 1");

		assertTrue("Character has spot class skills", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PRECSKILL:2,TYPE.Spy");

		assertFalse("Character has only one Spy Skill", PrereqHandler.passes(
			prereq, character, null));

		myClass.addCSkill("Spy 2");

		assertTrue("Character has 2 Spy class skills", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PRECSKILL:3,Spot,TYPE.Spy");

		assertTrue("Character has 2 Spy and Spot class skills", PrereqHandler
			.passes(prereq, character, null));

		prereq = factory.parse("PRECSKILL:3,Listen,TYPE.Spy");

		assertFalse("Character has only 2 Spy Skills", PrereqHandler.passes(
			prereq, character, null));

		myClass.addCSkill("Spy 3");

		prereq = factory.parse("PRECSKILL:3,Listen,TYPE.Spy");

		assertTrue("Character has 3 Spy Skills", PrereqHandler.passes(prereq,
			character, null));
	}
	public void testCSkillServesAs() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		character.incrementClassLevel(1, myClass, true);

		myClass = character.getClassKeyed("My Class");

		Prerequisite prereq;
		final PreParserFactory factory = PreParserFactory.getInstance();
		
		Skill foo = new Skill();
		foo.setName("Foo");
		foo.putServesAs("Bar","");
		foo.setTypeInfo("Foo");
		Globals.getSkillList().add(foo);
		
		Skill bar = new Skill();
		bar.setName("Bar");
		bar.setTypeInfo("Bar");
		Globals.getSkillList().add(bar); 
		
		Skill baz = new Skill();
		baz.setName("Baz");
		baz.setTypeInfo("Baz");
		Globals.getSkillList().add(baz); 
		
		Skill fee = new Skill();
		fee.setName("Fee");
		fee.setTypeInfo("Bar");
		Globals.getSkillList().add(fee); 
		
		
		myClass.addCSkill("Foo");
		myClass.addCSkill("Fee");
		prereq = factory.parse("PRECSKILL:1,Bar");		
		assertTrue("Character has 1 Listen Skill", PrereqHandler.passes(prereq,
			character, null));
		
		
		prereq = factory.parse("PRECSKILL:2,Bar,Fee");		
		assertTrue("Character has a Bar Skill and a Fee Skill", PrereqHandler.passes(prereq,
			character, null));
		
		prereq = factory.parse("PRECSKILL:2,Baz,Fee");		
		assertFalse("Character does not have both Baz and Fee Skills", PrereqHandler.passes(prereq,
			character, null));
		
		
		
		prereq = factory.parse("PRECSKILL:1,TYPE=Bar");		
		assertTrue("Character has 1 Bar Type Skill", PrereqHandler.passes(prereq,
			character, null));
		
		
		prereq = factory.parse("PRECSKILL:2,TYPE=Bar");		
		assertTrue("Character has 2 Bar Type Skills", PrereqHandler.passes(prereq,
			character, null));
		
		prereq = factory.parse("PRECSKILL:3,TYPE=Bar");		
		assertFalse("Character has less than 3 Bar Type Skills", PrereqHandler.passes(prereq,
			character, null));
		
		
	}
	protected void setUp() throws Exception
	{
		super.setUp();

		Skill spot = new Skill();
		spot.setName("Spot");
		Globals.getSkillList().add(spot);

		Skill listen = new Skill();
		listen.setName("Listen");
		Globals.getSkillList().add(listen);

		Skill spy1 = new Skill();
		spy1.setName("Spy 1");
		spy1.setTypeInfo("Spy");
		Globals.getSkillList().add(spy1);

		Skill spy2 = new Skill();
		spy2.setName("Spy 2");
		spy2.setTypeInfo("Spy");
		Globals.getSkillList().add(spy2);

		Skill spy3 = new Skill();
		spy3.setName("Spy 3");
		spy3.setTypeInfo("Spy");
		Globals.getSkillList().add(spy3);

		Skill spy4 = new Skill();
		spy4.setName("Spy 4");
		spy4.setTypeInfo("Spy");
		Globals.getSkillList().add(spy4);

		myClass.setName("My Class");
		myClass.setAbbrev("Myc");
		myClass.put(FormulaKey.START_SKILL_POINTS, FormulaFactory.getFormulaFor(3));
		Globals.getClassList().add(myClass);
	}
}
