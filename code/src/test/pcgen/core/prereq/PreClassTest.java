/*
 * PreClassTest.java
 *
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
 *
 * Created on 15-Jan-2004
 *
 * Current Ver: $Revision$
 *
 * Last Editor: $Author$
 *
 * Last Edited: $Date$
 *
 */
package pcgen.core.prereq;

import java.util.Arrays;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.AbstractCharacterTestCase;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import plugin.pretokens.parser.PreClassLevelMaxParser;
import plugin.pretokens.test.PreClassTester;

/**
 * @author wardc
 *
 */
/**
 * @author frugal@purplewombat.co.uk
 *
 */
public class PreClassTest extends AbstractCharacterTestCase
{
	public static void main(final String[] args)
	{
		TestRunner.run(PreClassTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreClassTest.class);
	}

	/**
	 * Test to ensure that a character with a named class can be found
	 * @throws Exception
	 */
	public void testNamedClass() throws Exception
	{
		final PCClass pcClass = new PCClass();
		pcClass.setName("MyClass");
		pcClass.setAbbrev("My");
		pcClass.setSpellType("ARCANE");

		final PlayerCharacter character = getCharacter();
		character.incrementClassLevel(3, pcClass);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("class");
		prereq.setKey("myclass");
		prereq.setOperand("1");
		prereq.setOperator(PrerequisiteOperator.GTEQ);

		final PreClassTester test = new PreClassTester();
		final int passes = test.passes(prereq, character);
		assertEquals(1, passes);
	}

	/**
	 * Test to ensure that a character will fail a test
	 * if it does not have the correct number of levels
	 * in the class.
	 * @throws Exception
	 */
	public void testTooFewLevels() throws Exception
	{
		final PCClass pcClass = new PCClass();
		pcClass.setName("MyClass");
		pcClass.setAbbrev("My");
		pcClass.setSpellType("ARCANE");

		final PlayerCharacter character = getCharacter();
		character.incrementClassLevel(1, pcClass);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("class");
		prereq.setKey("myclass");
		prereq.setOperand("3");
		prereq.setOperator(PrerequisiteOperator.GTEQ);

		final PreClassTester test = new PreClassTester();
		final int passes = test.passes(prereq, character);
		assertEquals(0, passes);
	}

	/**
	 * Test to ensure that a character will fail a test
	 * if it does not have the correct number of levels
	 * in the class.
	 * @throws Exception
	 */
	public void testCharWithMultipleClasses() throws Exception
	{
		final PCClass pcClass = new PCClass();
		pcClass.setName("MyClass");
		pcClass.setAbbrev("My");
		pcClass.setSpellType("ARCANE");

		final PCClass pcClass2 = new PCClass();
		pcClass2.setName("Other Class");
		pcClass2.setAbbrev("OC");

		final PlayerCharacter character = getCharacter();
		character.incrementClassLevel(1, pcClass);
		character.incrementClassLevel(2, pcClass2);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("class");
		prereq.setKey("other class");
		prereq.setOperand("2");
		prereq.setOperator(PrerequisiteOperator.GTEQ);

		final PreClassTester test = new PreClassTester();
		final int passes = test.passes(prereq, character);
		assertEquals(1, passes);
	}

	/**
	 * Test to ensure that a character will fail a test
	 * if it does not have the correct number of levels
	 * in the class.
	 * @throws Exception
	 */
	public void testCharWithMultipleSpellClasses() throws Exception
	{
		final PCClass pcClass = new PCClass();
		pcClass.setName("MyClass");
		pcClass.setAbbrev("My");
		pcClass.setSpellBaseStat("CHA");
		pcClass.setSpellType("ARCANE");
		pcClass.setCast(1, Arrays.asList("5,4".split(",")));

		final PCClass pcClass2 = new PCClass();
		pcClass2.setName("Other Class");
		pcClass2.setAbbrev("OC");
		pcClass.setSpellBaseStat("INT");
		pcClass2.setSpellType("ARCANE");
		pcClass2.setCast(1, Arrays.asList("5,4".split(",")));

		final PlayerCharacter character = getCharacter();
		character.incrementClassLevel(1, pcClass);
		character.incrementClassLevel(2, pcClass2);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("class");
		prereq.setKey("SPELLCASTER.ARCANE");
		prereq.setOperand("3");
		prereq.setTotalValues(true);
		prereq.setOperator(PrerequisiteOperator.GTEQ);

		final PreClassTester test = new PreClassTester();
		final int passes = test.passes(prereq, character);
		assertEquals(3, passes);
	}

	/**
	 * Test to ensure that a character without a named class cannot be found
	 * @throws Exception
	 */
	public void testNamedClassFail() throws Exception
	{
		final PCClass pcClass = new PCClass();
		pcClass.setName("MyClass");
		pcClass.setAbbrev("My");
		pcClass.setSpellType("ARCANE");

		final PlayerCharacter character = getCharacter();
		character.incrementClassLevel(1, pcClass);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("class");
		prereq.setKey("Druid");
		prereq.setOperand("1");
		prereq.setOperator(PrerequisiteOperator.GTEQ);

		final PreClassTester test = new PreClassTester();
		final int passes = test.passes(prereq, character);
		assertEquals(0, passes);
	}

	/**
	 * Test to ensure that a character without a named class cannot be found
	 * @throws Exception
	 */
	public void testNoLevelsPass() throws Exception
	{
		final PCClass pcClass = new PCClass();
		pcClass.setName("Monk");
		pcClass.setAbbrev("Mnk");

		final PlayerCharacter character = getCharacter();
		character.incrementClassLevel(1, pcClass);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("class");
		prereq.setKey("Druid");
		prereq.setOperand("1");
		prereq.setOperator(PrerequisiteOperator.LT);

		final PreClassTester test = new PreClassTester();
		final int passes = test.passes(prereq, character);
		assertEquals(1, passes);
	}

	/**
	 * Test to ensure that a character without a named class cannot be found
	 * @throws Exception
	 */
	public void testNoLevelsFail() throws Exception
	{
		final PCClass pcClass = new PCClass();
		pcClass.setName("Monk");
		pcClass.setAbbrev("Mnk");

		final PlayerCharacter character = getCharacter();
		character.incrementClassLevel(1, pcClass);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("class");
		prereq.setKey("Monk");
		prereq.setOperand("1");
		prereq.setOperator(PrerequisiteOperator.LT);

		final PreClassTester test = new PreClassTester();
		final int passes = test.passes(prereq, character);
		assertEquals(0, passes);
	}

	/**
	 * Test to ensure that a character with a spellcasting class can be found
	 * @throws Exception
	 */
	public void testSpellcaster() throws Exception
	{
		final PCClass pcClass = new PCClass();
		pcClass.setSpellType("ARCANE");
		pcClass.setCast(1, Arrays.asList("5,4".split(",")));
		pcClass.setSpellBaseStat("CHA");

		final PlayerCharacter character = getCharacter();
		character.incrementClassLevel(1, pcClass);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("class");
		prereq.setKey("Spellcaster");
		prereq.setOperand("1");
		prereq.setOperator(PrerequisiteOperator.GTEQ);

		final PreClassTester test = new PreClassTester();
		final int passes = test.passes(prereq, character);
		assertEquals(1, passes);
	}

	/**
	 * Test to ensure that a character with a spellcasting class
	 * does not match a different spellcasting type
	 * @throws Exception
	 */
	public void testSpellcasterTypeFail() throws Exception
	{
		final PCClass pcClass = new PCClass();
		pcClass.setSpellType("ARCANE");

		final PlayerCharacter character = getCharacter();
		character.incrementClassLevel(1, pcClass);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("class");
		prereq.setKey("Spellcaster.DIVINE");
		prereq.setOperand("1");
		prereq.setOperator(PrerequisiteOperator.GTEQ);

		final PreClassTester test = new PreClassTester();
		final int passes = test.passes(prereq, character);
		assertEquals(0, passes);
	}

	/**
	 * Test to ensure that a character with a spellcasting class
	 * will pass a prerequisute that requires a level of that
	 * classes spell type.
	 * @throws Exception
	 */
	public void testSpellcasterTypePass() throws Exception
	{
		final PCClass pcClass = new PCClass();
		pcClass.setSpellType("ARCANE");
		pcClass.setCast(1, Arrays.asList("5,4".split(",")));
		pcClass.setSpellBaseStat("CHA");

		final PlayerCharacter character = getCharacter();
		character.incrementClassLevel(1, pcClass);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("class");
		prereq.setKey("Spellcaster.ARCANE");
		prereq.setOperand("1");
		prereq.setOperator(PrerequisiteOperator.GTEQ);

		final PreClassTester test = new PreClassTester();
		final int passes = test.passes(prereq, character);
		assertEquals(1, passes);
	}

	/**
	 * Test to ensure that a spellcaster type check is case insensitive
	 * @throws Exception
	 */
	public void testSpellcasterTypeWrongCasePass() throws Exception
	{
		final PCClass pcClass = new PCClass();
		pcClass.setSpellType("ARCANE");
		pcClass.setCast(1, Arrays.asList("5,4".split(",")));
		pcClass.setSpellBaseStat("CHA");

		final PlayerCharacter character = getCharacter();
		character.incrementClassLevel(1, pcClass);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("class");
		prereq.setKey("Spellcaster.Arcane");
		prereq.setOperand("1");
		prereq.setOperator(PrerequisiteOperator.GTEQ);

		final PreClassTester test = new PreClassTester();
		final int passes = test.passes(prereq, character);
		assertEquals(1, passes);
	}

	public void testPreClassLevelMax() throws Exception
	{
		final PreClassLevelMaxParser parser = new PreClassLevelMaxParser();
		final Prerequisite prereq =
				parser.parse("CLASSLEVELMAX", "1,Monk=1", false, false);
		final Prerequisite dualPrereq =
				parser.parse("CLASSLEVELMAX", "Monk,Fighter=1", false, false);
		final Prerequisite singlePrereq =
				parser.parse("CLASSLEVELMAX", "1,Monk,Fighter=1", false, false);

		final PlayerCharacter character = getCharacter();

		assertTrue("Should pass with no levels", PrereqHandler.passes(prereq,
			character, null));
		assertTrue("Should pass with no levels of either", PrereqHandler
			.passes(dualPrereq, character, null));

		final PCClass pcClass = new PCClass();
		pcClass.setName("Monk");
		pcClass.setAbbrev("Mnk");
		character.incrementClassLevel(1, pcClass);
		assertTrue("Should pass with 1 level", PrereqHandler.passes(prereq,
			character, null));
		assertTrue("Should pass with 1 level of one", PrereqHandler.passes(
			dualPrereq, character, null));

		final PCClass ftrClass = new PCClass();
		ftrClass.setName("Fighter");
		ftrClass.setAbbrev("Ftr");
		character.incrementClassLevel(1, ftrClass);
		assertTrue("Should pass with 1 level of each", PrereqHandler.passes(
			dualPrereq, character, null));

		character.incrementClassLevel(1, pcClass);
		assertFalse("Should not pass with 2 levels", PrereqHandler.passes(
			prereq, character, null));
		assertFalse("Should not pass with 2 levels of one", PrereqHandler
			.passes(dualPrereq, character, null));
		assertTrue("Should pass with 2 levels of one", PrereqHandler.passes(
			singlePrereq, character, null));

	}

	public void testOldPreClassLevelMax() throws Exception
	{
		final PreClassLevelMaxParser parser = new PreClassLevelMaxParser();
		final Prerequisite prereq =
				parser.parse("CLASSLEVELMAX", "Fighter=2", false, false);

		final PlayerCharacter character = getCharacter();

		assertTrue("Should pass with no levels", PrereqHandler.passes(prereq,
			character, null));

		final PCClass ftrClass = new PCClass();
		ftrClass.setName("Fighter");
		ftrClass.setAbbrev("Ftr");
		character.incrementClassLevel(1, ftrClass);
		assertTrue("Should pass with 1 level", PrereqHandler.passes(prereq,
			character, null));

		final PCClass pcClass = new PCClass();
		pcClass.setName("Monk");
		pcClass.setAbbrev("Mnk");
		character.incrementClassLevel(1, pcClass);
		assertTrue("Should pass with 1 level of something else", PrereqHandler
			.passes(prereq, character, null));

		character.incrementClassLevel(1, ftrClass);
		assertTrue("Should pass with 2 levels of ftr", PrereqHandler.passes(
			prereq, character, null));

		character.incrementClassLevel(1, ftrClass);
		assertFalse("Should not pass with 3 levels of ftr", PrereqHandler
			.passes(prereq, character, null));

	}

	/**
	 * Test to ensure that a character will fail a test
	 * if it does not have the correct number of levels
	 * in the class.
	 * @throws Exception
	 */
	public void testAnyLevelsOneClass() throws Exception
	{
		final PCClass pcClass = new PCClass();
		pcClass.setName("MyClass");
		pcClass.setAbbrev("My");
		pcClass.setSpellType("ARCANE");

		final PlayerCharacter character = getCharacter();
		character.incrementClassLevel(1, pcClass);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("class");
		prereq.setKey("Any");
		prereq.setOperand("3");
		prereq.setOperator(PrerequisiteOperator.GTEQ);

		final PreClassTester test = new PreClassTester();
		assertEquals(0, test.passes(prereq, character));

		character.incrementClassLevel(2, pcClass);
		assertEquals(1, test.passes(prereq, character));
	}

	public void testAnyLevelsTwoClasses() throws Exception
	{
		final PCClass pcClass = new PCClass();
		pcClass.setName("MyClass");
		pcClass.setAbbrev("My");
		pcClass.setSpellType("ARCANE");

		final PCClass pcClass2 = new PCClass();
		pcClass2.setName("MyClass2");
		pcClass2.setAbbrev("My2");
		pcClass2.setSpellType("DIVINE");

		final PlayerCharacter character = getCharacter();
		character.incrementClassLevel(1, pcClass);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("class");
		prereq.setKey("Any");
		prereq.setOperand("3");
		prereq.setOperator(PrerequisiteOperator.GTEQ);

		final PreClassTester test = new PreClassTester();
		assertEquals(0, test.passes(prereq, character));

		character.incrementClassLevel(2, pcClass2);
		assertEquals(1, test.passes(prereq, character));
	}
}
