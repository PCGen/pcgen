/*
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
 */
package pcgen.core;

import java.util.Collections;
import java.util.List;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.TestHelper;
import plugin.lsttokens.testsupport.BuildUtilities;

/**
 * This class tests the handling of DESC fields in PCGen
 */
@SuppressWarnings("nls")
public class DescriptionTest extends AbstractCharacterTestCase
{

	/**
	 * Tests outputting an empty description.
	 */
	public void testEmptyDesc()
	{
		final Ability dummy =
				TestHelper.makeAbility("dummy", BuildUtilities.getFeatCat(), "Foo");
		final Description desc = new Description(Constants.EMPTY_STRING);
		List<CNAbility> singletonAbility = Collections.singletonList(CNAbilityFactory
			.getCNAbility(BuildUtilities.getFeatCat(), Nature.NORMAL, dummy));
		assertTrue(desc.getDescription(this.getCharacter(), singletonAbility).isEmpty());
	}

	/**
	 * Tests outputting a simple description.
	 */
	public void testSimpleDesc()
	{
		final Ability dummy =
				TestHelper.makeAbility("dummy", BuildUtilities.getFeatCat(), "Foo");
		final String simpleDesc = "This is a test";
		final Description desc = new Description(simpleDesc);
		List<CNAbility> singletonAbility = Collections.singletonList(CNAbilityFactory
			.getCNAbility(BuildUtilities.getFeatCat(), Nature.NORMAL, dummy));
		assertTrue(
			desc.getDescription(getCharacter(), singletonAbility).equals(simpleDesc));
	}

	/**
	 * Test PREREQs for Desc
	 * @throws Exception
	 */
	public void testPreReqs() throws Exception
	{
		final Ability dummy =
				TestHelper.makeAbility("dummy", BuildUtilities.getFeatCat(), "Foo");
		final String simpleDesc = "This is a test";
		final Description desc = new Description(simpleDesc);

		final PreParserFactory factory = PreParserFactory.getInstance();

		final Prerequisite prereqNE = factory.parse("PRETEMPLATE:1,KEY_Natural Lycanthrope");
		desc.addPrerequisite(prereqNE);
		List<CNAbility> singletonAbility = Collections.singletonList(CNAbilityFactory
			.getCNAbility(BuildUtilities.getFeatCat(), Nature.NORMAL, dummy));
		is(desc.getDescription(getCharacter(), singletonAbility), strEq(""));

		PCTemplate template = new PCTemplate();
		template.setName("Natural Lycanthrope");
		template.put(StringKey.KEY_NAME, "KEY_Natural Lycanthrope");
		Globals.getContext().getReferenceContext().importObject(template);
		getCharacter().addTemplate(template);
		is(desc.getDescription(getCharacter(), singletonAbility), strEq(simpleDesc));
	}

	/**
	 * Tests a simple string replacement.
	 */
	public void testSimpleReplacement()
	{
		final Ability dummy =
				TestHelper.makeAbility("dummy", BuildUtilities.getFeatCat(), "Foo");
		final Description desc = new Description("%1");
		desc.addVariable("\"Variable\"");
		List<CNAbility> singletonAbility = Collections.singletonList(CNAbilityFactory
			.getCNAbility(BuildUtilities.getFeatCat(), Nature.NORMAL, dummy));
		assertTrue(
			desc.getDescription(getCharacter(), singletonAbility).equals("Variable"));
	}

	/**
	 * Test name replacement
	 */
	public void testSimpleNameReplacement()
	{
		final PCTemplate pobj = new PCTemplate();
		pobj.setName("PObject");

		final Description desc = new Description("%1");
		desc.addVariable("%NAME");
		pobj.addToListFor(ListKey.DESCRIPTION, desc);
		assertTrue(getCharacter().getDescription(pobj).equals("PObject"));
	}

	/**
	 * Tests simple variable replacement
	 */
	public void testSimpleVariableReplacement()
	{
		final Race dummy = new Race();
		dummy.put(VariableKey.getConstant("TestVar"), FormulaFactory
				.getFormulaFor(2));

		final Description desc = new Description("%1");
		desc.addVariable("TestVar");
		dummy.addToListFor(ListKey.DESCRIPTION, desc);
		assertTrue(getCharacter().getDescription(dummy).equals("0"));

		getCharacter().setRace(dummy);
		assertTrue(getCharacter().getDescription(dummy).equals("2"));
	}

	/**
	 * Tests simple replacement of %CHOICE
	 */
	public void testSimpleChoiceReplacement()
	{
		final PCTemplate pobj = new PCTemplate();
		Globals.getContext().unconditionallyProcess(pobj, "CHOOSE", "LANG|ALL");
		Globals.getContext().getReferenceContext().constructCDOMObject(Language.class, "Foo");

		final Description desc = new Description("%1");
		desc.addVariable("%CHOICE");
		pobj.addToListFor(ListKey.DESCRIPTION, desc);
		PlayerCharacter pc = getCharacter();
		assertTrue(getCharacter().getDescription(pobj).isEmpty());

		add(ChooserUtilities.getChoiceManager(pobj, pc), pc, pobj, "Foo");
		assertTrue(getCharacter().getDescription(pobj).equals("Foo"));
	}

	/**
	 * Tests simple %LIST replacement.
	 */
	public void testSimpleListReplacement()
	{
		final Domain pobj = new Domain();
		Globals.getContext().unconditionallyProcess(pobj, "CHOOSE", "LANG|ALL");
		Globals.getContext().getReferenceContext().constructCDOMObject(Language.class, "Foo");

		final Description desc = new Description("%1");
		desc.addVariable("%LIST");
		pobj.addToListFor(ListKey.DESCRIPTION, desc);
		PlayerCharacter pc = getCharacter();
		assertTrue(getCharacter().getDescription(pobj).isEmpty());

		add(ChooserUtilities.getChoiceManager(pobj, pc), pc, pobj, "Foo");
		
		assertTrue(getCharacter().getDescription(pobj).equals("Foo"));
	}

	/**
	 * Test a replacement with missing variables.
	 */
	public void testEmptyReplacement()
	{
		final Deity pobj = new Deity();

		final Description desc = new Description("%1");
		assertTrue(getCharacter().getDescription(pobj).isEmpty());
	}

	/**
	 * Test having extra variables present
	 */
	public void testExtraVariables()
	{
		final Race pobj = new Race();
		Globals.getContext().unconditionallyProcess(pobj, "CHOOSE", "LANG|ALL");
		Globals.getContext().getReferenceContext().constructCDOMObject(Language.class, "Foo");

		final Description desc = new Description("Testing");
		desc.addVariable("%LIST");
		pobj.addToListFor(ListKey.DESCRIPTION, desc);
		PlayerCharacter pc = getCharacter();
		assertTrue(getCharacter().getDescription(pobj).equals("Testing"));

		add(ChooserUtilities.getChoiceManager(pobj, pc), pc, pobj, "Foo");
		assertTrue(getCharacter().getDescription(pobj).equals("Testing"));
	}

	/**
	 * Test complex replacements.
	 */
	public void testComplexVariableReplacement()
	{
		final Ability dummy = BuildUtilities.getFeatCat().newInstance();
		dummy.setKeyName("Dummy");
		Globals.getContext().unconditionallyProcess(dummy, "CATEGORY", "FEAT");
		Globals.getContext().unconditionallyProcess(dummy, "CHOOSE", "LANG|ALL");
		Globals.getContext().unconditionallyProcess(dummy, "MULT", "YES");
		Globals.getContext().getReferenceContext().constructCDOMObject(Language.class, "Associated 1");
		Globals.getContext().getReferenceContext().constructCDOMObject(Language.class, "Associated 2");
		dummy.put(VariableKey.getConstant("TestVar"), FormulaFactory
				.getFormulaFor(2));
		Globals.getContext().getReferenceContext().resolveReferences(null);
		PlayerCharacter pc = getCharacter();

		final Description desc = new Description("%1 test %3 %2");
		desc.addVariable("TestVar");
		dummy.addToListFor(ListKey.DESCRIPTION, desc);
		List<CNAbility> wrappedDummy = Collections.singletonList(CNAbilityFactory
			.getCNAbility(BuildUtilities.getFeatCat(), Nature.NORMAL, dummy));
		assertEquals("0 test  ", desc.getDescription(pc, wrappedDummy));

		AbilityCategory category = BuildUtilities.getFeatCat();

		CNAbility cna = finalizeTest(dummy, "Associated 1", pc, category);
		finalizeTest(dummy, "Associated 2", pc, category);
		assertEquals("2 test  ", desc.getDescription(pc, wrappedDummy));

		desc.addVariable("%CHOICE");
		dummy.addToListFor(ListKey.DESCRIPTION, desc);
		List<CNAbility> wrappedPCA = Collections.singletonList(cna);
		assertEquals("2 test  Associated 1",
			desc.getDescription(pc, wrappedPCA));

		desc.addVariable("%LIST");
		assertEquals("Replacement of %LIST failed",
			"2 test Associated 1 and Associated 2 Associated 1",
			desc.getDescription(pc, wrappedPCA));
	}

	private static <T> void add(ChoiceManagerList<T> aMan, PlayerCharacter pc,
		ChooseDriver obj, String choice)
	{
		T sel = aMan.decodeChoice(choice);
		aMan.applyChoice(pc, obj, sel);
	}

}
