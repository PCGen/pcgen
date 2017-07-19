/*
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
 */
package pcgen.cdom.helper;

import java.util.Collections;
import java.util.List;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PlayerCharacter;
import pcgen.util.TestHelper;

/**
 * This class tests the handling of ASPECT fields in PCGen
 * 
 * 
 */
@SuppressWarnings("nls")
public class AspectTest extends AbstractCharacterTestCase
{
	private static final String ASPECT_NAME = "UnitTest";

	/**
	 * Tests outputting an empty Aspect.
	 */
	public void testEmptyDesc()
	{
		final Ability dummy =
				TestHelper.makeAbility("dummy", AbilityCategory.FEAT, "Foo");
		final Aspect aspect = new Aspect(ASPECT_NAME, Constants.EMPTY_STRING);
		assertEquals("", aspect.getAspectText(this.getCharacter(), buildMap(dummy, AbilityCategory.FEAT, Nature.NORMAL)));
	}

	public void testNull()
	{
		final Aspect aspect = new Aspect(ASPECT_NAME, Constants.EMPTY_STRING);
		assertEquals("", aspect.getAspectText(this.getCharacter(), null));
	}

	/**
	 * Tests outputting a simple Aspect.
	 */
	public void testSimpleDesc()
	{
		final Ability dummy =
				TestHelper.makeAbility("dummy", AbilityCategory.FEAT, "Foo");
		final String simpleDesc = "This is a test";
		final Aspect aspect = new Aspect(ASPECT_NAME, simpleDesc);
		assertEquals(simpleDesc, aspect.getAspectText(getCharacter(), buildMap(dummy, AbilityCategory.FEAT, Nature.NORMAL)));
	}

	/**
	 * Tests a simple string replacement.
	 */
	public void testSimpleReplacement()
	{
		final Ability dummy =
				TestHelper.makeAbility("dummy", AbilityCategory.FEAT, "Foo");
		final Aspect aspect = new Aspect(ASPECT_NAME, "%1");
		aspect.addVariable("\"Variable\"");
		assertEquals("Variable", aspect.getAspectText(getCharacter(), buildMap(dummy, AbilityCategory.FEAT, Nature.NORMAL)));
	}

	/**
	 * Test name replacement
	 */
	public void testSimpleNameReplacement()
	{
		final Ability pobj = new Ability();
		pobj.setName("PObject");
		pobj.setCDOMCategory(AbilityCategory.FEAT);

		final Aspect aspect = new Aspect(ASPECT_NAME, "%1");
		aspect.addVariable("%NAME");
		assertEquals("PObject", aspect.getAspectText(getCharacter(), buildMap(pobj, AbilityCategory.FEAT, Nature.NORMAL)));
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
		assertEquals("0", aspect.getAspectText(getCharacter(), buildMap(dummy, AbilityCategory.FEAT, Nature.NORMAL)));

		addAbility(AbilityCategory.FEAT, dummy);
		assertEquals("2", aspect.getAspectText(getCharacter(), buildMap(dummy, AbilityCategory.FEAT, Nature.NORMAL)));
	}

	/**
	 * Tests simple %LIST replacement.
	 */
	public void testSimpleListReplacement()
	{
		final Ability pobj =
				TestHelper.makeAbility("dummy", AbilityCategory.FEAT, "Foo");
		Globals.getContext().unconditionallyProcess(pobj, "CHOOSE", "LANG|ALL");
		Globals.getContext().unconditionallyProcess(pobj, "MULT", "YES");
		Globals.getContext().getReferenceContext().constructCDOMObject(Language.class, "Foo");
		PlayerCharacter pc = getCharacter();

		final Aspect aspect = new Aspect(ASPECT_NAME, "%1");
		aspect.addVariable("%LIST");
		assertEquals("", aspect.getAspectText(pc, buildMap(pobj, AbilityCategory.FEAT, Nature.NORMAL)));
		AbilityCategory category = AbilityCategory.FEAT;

		CNAbility cna = finalizeTest(pobj, "Foo", pc, category);
		assertEquals("Foo", aspect.getAspectText(pc, Collections.singletonList(cna)));
	}

	/**
	 * Test a replacement with missing variables.
	 */
	public void testEmptyReplacement()
	{
		final Ability pobj =
				TestHelper.makeAbility("dummy", AbilityCategory.FEAT, "Foo");

		final Aspect aspect = new Aspect(ASPECT_NAME, "%1");
		assertEquals("", aspect.getAspectText(getCharacter(), buildMap(pobj, AbilityCategory.FEAT, Nature.NORMAL)));
	}

	/**
	 * Test having extra variables present
	 */
	public void testExtraVariables()
	{
		final Ability pobj =
				TestHelper.makeAbility("dummy", AbilityCategory.FEAT, "Foo");
		Globals.getContext().unconditionallyProcess(pobj, "CHOOSE", "LANG|ALL");
		Globals.getContext().unconditionallyProcess(pobj, "MULT", "YES");
		Globals.getContext().getReferenceContext().constructCDOMObject(Language.class, "Foo");

		final Aspect aspect = new Aspect(ASPECT_NAME, "Testing");
		aspect.addVariable("%LIST");
		PlayerCharacter pc = getCharacter();
		assertEquals("Testing", aspect.getAspectText(pc, buildMap(pobj, AbilityCategory.FEAT, Nature.NORMAL)));

		AbstractCharacterTestCase.applyAbility(pc, AbilityCategory.FEAT, pobj, "Foo");
		assertEquals("Testing", aspect.getAspectText(pc, buildMap(pobj, AbilityCategory.FEAT, Nature.NORMAL)));
	}

	/**
	 * Test complex replacements.
	 */
	public void testComplexVariableReplacement()
	{
		final Ability dummy =
			TestHelper.makeAbility("dummy", AbilityCategory.FEAT, "Foo");
		Globals.getContext().unconditionallyProcess(dummy, "CHOOSE", "LANG|ALL");
		Globals.getContext().unconditionallyProcess(dummy, "MULT", "YES");
		Globals.getContext().getReferenceContext().constructCDOMObject(Language.class, "Associated 1");
		Globals.getContext().getReferenceContext().constructCDOMObject(Language.class, "Associated 2");
		Globals.getContext().getReferenceContext().constructCDOMObject(Language.class, "Associated 3");
		dummy.put(VariableKey.getConstant("TestVar"), FormulaFactory
				.getFormulaFor(2));
		PlayerCharacter pc = getCharacter();

		final Aspect aspect = new Aspect(ASPECT_NAME, "%1 test %2");
		aspect.addVariable("TestVar");
		assertEquals("0 test ", aspect.getAspectText(pc, buildMap(dummy, AbilityCategory.FEAT, Nature.NORMAL)));

		CNAbility cna = finalizeTest(dummy, "Associated 1", pc, AbilityCategory.FEAT);
		finalizeTest(dummy, "Associated 2", pc, AbilityCategory.FEAT);
		assertEquals("2 test ", aspect.getAspectText(pc, Collections.singletonList(cna)));

		aspect.addVariable("%LIST");
		assertEquals("Replacement of %LIST failed",
			"2 test Associated 1 and Associated 2", aspect
				.getAspectText(pc, Collections.singletonList(cna)));

		finalizeTest(dummy, "Associated 3", pc, AbilityCategory.FEAT);
		aspect.addVariable("%LIST");
		assertEquals("Replacement of %LIST failed",
			"2 test Associated 1, Associated 2, Associated 3", aspect
				.getAspectText(pc, Collections.singletonList(cna)));
	}
	

	public List<CNAbility> buildMap(Ability a, Category<Ability> cat, Nature n)
	{
		return Collections.singletonList(CNAbilityFactory.getCNAbility(cat, n, a));
	}
}
