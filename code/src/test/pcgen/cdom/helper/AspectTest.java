/*
 * AspectTest.java
 * Copyright 2008 (C) James Dempsey
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 17/08/2008 10:33:48
 *
 * $Id: $
 */
package pcgen.cdom.helper;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.util.TestHelper;

/**
 * This class tests the handling of ASPECT fields in PCGen
 * 
 * Last Editor: $Author: $
 * Last Edited: $Date:  $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision:  $
 */
@SuppressWarnings("nls")
public class AspectTest extends AbstractCharacterTestCase
{
	private static String ASPECT_NAME = "UnitTest";
	/**
	 * Constructs a new <code>AspectTest</code>.
	 */
	public AspectTest()
	{
		super();
	}

	/**
	 * Tests outputting an empty Aspect.
	 *
	 */
	public void testEmptyDesc()
	{
		final Aspect aspect = new Aspect(ASPECT_NAME, Constants.EMPTY_STRING);
		assertEquals("", aspect.getAspectText(this.getCharacter(), null));
	}

	/**
	 * Tests outputting a simple Aspect.
	 *
	 */
	public void testSimpleDesc()
	{
		final String simpleDesc = "This is a test";
		final Aspect aspect = new Aspect(ASPECT_NAME, simpleDesc);
		assertEquals(simpleDesc, aspect.getAspectText(getCharacter(), null));
	}

	/**
	 * Tests a simple string replacement.
	 */
	public void testSimpleReplacement()
	{
		final Aspect aspect = new Aspect(ASPECT_NAME, "%1");
		aspect.addVariable("\"Variable\"");
		assertEquals("Variable", aspect.getAspectText(getCharacter(), null));
	}

	/**
	 * Test name replacement
	 */
	public void testSimpleNameReplacement()
	{
		final Ability pobj = new Ability();
		pobj.setName("PObject");

		final Aspect aspect = new Aspect(ASPECT_NAME, "%1");
		aspect.addVariable("%NAME");
		assertEquals("PObject", aspect.getAspectText(getCharacter(), pobj));
	}

	/**
	 * Tests simple variable replacement
	 */
	public void testSimpleVariableReplacement()
	{
		final Ability dummy =
				TestHelper.makeAbility("dummy", AbilityCategory.FEAT, "Foo");
		dummy.put(VariableKey.getConstant("TestVar"), FormulaFactory
				.getFormulaFor(2));

		final Aspect aspect = new Aspect(ASPECT_NAME, "%1");
		aspect.addVariable("TestVar");
		assertEquals("0", aspect.getAspectText(getCharacter(), dummy));

		getCharacter().addAbility(AbilityCategory.FEAT, dummy, null);
		assertEquals("2", aspect.getAspectText(getCharacter(), dummy));
	}

	/**
	 * Tests simple replacement of %CHOICE
	 */
	public void testSimpleChoiceReplacement()
	{
		final Ability pobj =
				TestHelper.makeAbility("dummy", AbilityCategory.FEAT, "Foo");
		PlayerCharacter pc = getCharacter();
		pc.addAbility(AbilityCategory.FEAT, pobj, null);

		final Aspect aspect = new Aspect(ASPECT_NAME, "%1");
		aspect.addVariable("%CHOICE");
		assertEquals("", aspect.getAspectText(pc, pobj));

		pc.addAssociation(pobj, "Foo");

		assertEquals("Foo", aspect.getAspectText(pc, pobj));
	}

	/**
	 * Tests simple %LIST replacement.
	 */
	public void testSimpleListReplacement()
	{
		final Ability pobj =
				TestHelper.makeAbility("dummy", AbilityCategory.FEAT, "Foo");
		PlayerCharacter pc = getCharacter();
		pc.addAbility(AbilityCategory.FEAT, pobj, null);

		final Aspect aspect = new Aspect(ASPECT_NAME, "%1");
		aspect.addVariable("%LIST");
		assertEquals("", aspect.getAspectText(pc, pobj));

		pc.addAssociation(pobj, "Foo");
		assertEquals("Foo", aspect.getAspectText(pc, pobj));
	}

	/**
	 * Test a replacement with missing variables.
	 */
	public void testEmptyReplacement()
	{
		final Ability pobj =
				TestHelper.makeAbility("dummy", AbilityCategory.FEAT, "Foo");

		final Aspect aspect = new Aspect(ASPECT_NAME, "%1");
		assertEquals("", aspect.getAspectText(getCharacter(), pobj));
	}

	/**
	 * Test having extra variables present
	 */
	public void testExtraVariables()
	{
		final Ability pobj =
				TestHelper.makeAbility("dummy", AbilityCategory.FEAT, "Foo");

		final Aspect aspect = new Aspect(ASPECT_NAME, "Testing");
		aspect.addVariable("%LIST");
		PlayerCharacter pc = getCharacter();
		assertEquals("Testing", aspect.getAspectText(pc, pobj));

		pc.addAssociation(pobj, "Foo");
		assertEquals("Testing", aspect.getAspectText(pc, pobj));
	}

	/**
	 * Test complex replacements.
	 */
	public void testComplexVariableReplacement()
	{
		final Ability dummy =
			TestHelper.makeAbility("dummy", AbilityCategory.FEAT, "Foo");
		dummy.put(VariableKey.getConstant("TestVar"), FormulaFactory
				.getFormulaFor(2));
		PlayerCharacter pc = getCharacter();
		pc.addAssociation(dummy, "Associated 1");
		pc.addAssociation(dummy, "Associated 2");

		final Aspect aspect = new Aspect(ASPECT_NAME, "%1 test %3 %2");
		aspect.addVariable("TestVar");
		assertEquals("0 test  ", aspect.getAspectText(pc, dummy));

		pc.addAbility(AbilityCategory.FEAT, dummy, null);
		assertEquals("2 test  ", aspect.getAspectText(pc, dummy));

		aspect.addVariable("%CHOICE");
		assertEquals("2 test  Associated 1", aspect
			.getAspectText(pc, dummy));

		aspect.addVariable("%LIST");
		assertEquals("Replacement of %LIST failed",
			"2 test Associated 1 and Associated 2 Associated 1", aspect
				.getAspectText(pc, dummy));
	}
}
