/*
 * 
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.add;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.ConcretePersistentTransitionChoice;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.choiceset.AbilityRefChoiceSet;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.AddLst;
import plugin.lsttokens.testsupport.AbstractSelectionTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class VFeatTokenTest extends
		AbstractSelectionTokenTestCase<CDOMObject, Ability>
{

	private static final Nature NATURE = Nature.VIRTUAL;
	static AddLst token = new AddLst();
	static VFeatToken subtoken = new VFeatToken();
	static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<CDOMObject>();

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public CDOMLoader<CDOMObject> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
	{
		return token;
	}

	@Override
	public CDOMSecondaryToken<?> getSubToken()
	{
		return subtoken;
	}

	@Override
	public Class<Ability> getTargetClass()
	{
		return Ability.class;
	}

	@Override
	public boolean isAllLegal()
	{
		return true;
	}

	@Override
	public boolean isTypeLegal()
	{
		return true;
	}

	@Test
	public void testEmpty()
	{
		// Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}

	@Override
	public boolean allowsParenAsSub()
	{
		return true;
	}

	@Override
	protected Ability construct(LoadContext loadContext, String one)
	{
		Ability obj = loadContext.getReferenceContext().constructCDOMObject(Ability.class, one);
		loadContext.getReferenceContext().reassociateCategory(AbilityCategory.FEAT, obj);
		return obj;
	}

	@Override
	protected Ability constructTyped(LoadContext loadContext, String one)
	{
		Ability obj = loadContext.getReferenceContext().constructCDOMObject(Ability.class, one);
		loadContext.getReferenceContext().reassociateCategory(AbilityCategory.FEAT, obj);
		return obj;
	}

	@Override
	public boolean allowsFormula()
	{
		return true;
	}

	@Test
	public void testRoundRobinStacks() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		construct(secondaryContext, "TestWP3");
		runRoundRobin(getSubTokenName() + '|' + "STACKS,TestWP1"
				+ getJoinCharacter() + "TestWP2" + getJoinCharacter()
				+ "TestWP3");
	}

	@Test
	public void testRoundRobinStacksValue() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		construct(secondaryContext, "TestWP3");
		runRoundRobin(getSubTokenName() + '|' + "STACKS=5,TestWP1"
				+ getJoinCharacter() + "TestWP2" + getJoinCharacter()
				+ "TestWP3");
	}

	@Test
	public void testInvalidInputDoubleStacks() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "STACKS,STACKS,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoubleStack() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "STACKS=3,STACKS=2,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputStacksStack() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "STACKS,STACKS=2,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNegativeStack()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "STACKS=-4,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputZeroStack() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "STACKS=0,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputStacksNaN() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "STACKS=x,TestWP1"
				+ getJoinCharacter() + "TestWP2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOnlyStacks() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "STACKS=4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputMultTarget() throws PersistenceLayerException
	{
		boolean ret = parse(getSubTokenName() + '|' + "TestWP1(Foo,Bar)"
				+ getJoinCharacter() + "TestWP2");
		if (ret)
		{
			assertConstructionError();
		}
		else
		{
			assertNoSideEffects();
		}
	}

	@Test
	public void testRoundRobinDupe() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin(getSubTokenName() + '|' + "TestWP1", getSubTokenName()
				+ '|' + "TestWP1");
	}

	@Test
	public void testUnparseSingle() throws PersistenceLayerException
	{
		List<CDOMReference<Ability>> refs = createSingle("TestWP1");
		createTC(refs, FormulaFactory.ONE);
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		expectSingle(unparsed, getSubTokenName() + '|' + "TestWP1");
	}

	private List<CDOMReference<Ability>> createSingle(String name)
	{
		List<CDOMReference<Ability>> refs = new ArrayList<CDOMReference<Ability>>();
		Ability obj = primaryContext.getReferenceContext().constructCDOMObject(Ability.class,
				name);
		primaryContext.getReferenceContext().reassociateCategory(AbilityCategory.FEAT, obj);
		CDOMDirectSingleRef<Ability> ar = CDOMDirectSingleRef.getRef(obj);
		refs.add(ar);
		if (name.indexOf('(') != -1)
		{
			List<String> choices = new ArrayList<String>();
			AbilityUtilities.getUndecoratedName(name, choices);
			assertEquals(1, choices.size());
			ar.setChoice(choices.get(0));
		}
		return refs;
	}

	@Test
	public void testUnparseType() throws PersistenceLayerException
	{
		List<CDOMReference<Ability>> refs = new ArrayList<CDOMReference<Ability>>();
		CDOMGroupRef<Ability> ref = primaryContext.getReferenceContext().getCDOMTypeReference(
				Ability.class, AbilityCategory.FEAT, "Foo", "Bar");
		refs.add(ref);

		createTC(refs, FormulaFactory.ONE);
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		expectSingle(unparsed, getSubTokenName() + '|' + "TYPE=Bar.Foo");
	}

	private void createTC(List<CDOMReference<Ability>> refs, Formula count)
	{
		AbilityRefChoiceSet rcs = new AbilityRefChoiceSet(AbilityCategory.FEAT,
				refs, NATURE);
		// TODO: Should this be present for the unit tests?
		//assertTrue("Invalid grouping state " + rcs.getGroupingState(), rcs.getGroupingState().isValid());
		ChoiceSet<CNAbilitySelection> cs = new ChoiceSet<CNAbilitySelection>(
				getSubToken().getTokenName(), rcs);
		cs.setTitle("Virtual Feat Selection");
		PersistentTransitionChoice<CNAbilitySelection> tc = new ConcretePersistentTransitionChoice<CNAbilitySelection>(
				cs, count);
		tc.allowStack(false);
		// if (dupChoices != 0)
		// {
		// tc.setStackLimit(dupChoices);
		// }
		tc.setChoiceActor(subtoken);
		primaryProf.addToListFor(ListKey.ADD, tc);
	}

	@Test
	public void testUnparseSingleThree() throws PersistenceLayerException
	{
		List<CDOMReference<Ability>> refs = createSingle("TestWP1");
		createTC(refs, FormulaFactory.getFormulaFor(3));
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		expectSingle(unparsed, getSubTokenName() + '|' + "3|TestWP1");
	}

	@Test
	public void testUnparseSingleNegative() throws PersistenceLayerException
	{
		List<CDOMReference<Ability>> refs = createSingle("TestWP1");
		createTC(refs, FormulaFactory.getFormulaFor(-2));
		assertBadUnparse();
	}

	@Test
	public void testUnparseSingleZero() throws PersistenceLayerException
	{
		List<CDOMReference<Ability>> refs = createSingle("TestWP1");
		createTC(refs, FormulaFactory.getFormulaFor(0));
		assertBadUnparse();
	}

	@Test
	public void testUnparseSingleVariable() throws PersistenceLayerException
	{
		List<CDOMReference<Ability>> refs = createSingle("TestWP1");
		createTC(refs, FormulaFactory.getFormulaFor("Formula"));
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		expectSingle(unparsed, getSubTokenName() + '|' + "Formula|TestWP1");
	}

	@Test
	public void testUnparseSingleAll() throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			List<CDOMReference<Ability>> refs = createSingle("TestWP1");
			CDOMGroupRef<Ability> ref = primaryContext.getReferenceContext().getCDOMAllReference(
					Ability.class, AbilityCategory.FEAT);
			refs.add(ref);
			createTC(refs, FormulaFactory.ONE);
			assertBadUnparse();
		}
	}

	@Test
	public void testUnparseAll() throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			List<CDOMReference<Ability>> refs = new ArrayList<CDOMReference<Ability>>();
			CDOMGroupRef<Ability> ref = primaryContext.getReferenceContext().getCDOMAllReference(
					Ability.class, AbilityCategory.FEAT);
			refs.add(ref);
			createTC(refs, FormulaFactory.ONE);
			String[] unparsed = getToken().unparse(primaryContext, primaryProf);
			expectSingle(unparsed, getSubTokenName() + '|' + "ALL");
		}
	}

	@Test
	public void testUnparseTypeAll() throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			List<CDOMReference<Ability>> refs = new ArrayList<CDOMReference<Ability>>();
			CDOMGroupRef<Ability> ref = primaryContext.getReferenceContext().getCDOMTypeReference(
					Ability.class, AbilityCategory.FEAT, "Foo", "Bar");
			refs.add(ref);
			ref = primaryContext.getReferenceContext().getCDOMAllReference(
					Ability.class, AbilityCategory.FEAT);
			refs.add(ref);
			createTC(refs, FormulaFactory.ONE);
			assertBadUnparse();
		}
	}

	@Test
	public void testUnparseComplex() throws PersistenceLayerException
	{
		List<CDOMReference<Ability>> refs = createSingle("TestWP1");
		AbilityRefChoiceSet rcs = new AbilityRefChoiceSet(AbilityCategory.FEAT,
				refs, NATURE);
		assertTrue("Invalid grouping state " + rcs.getGroupingState(), rcs.getGroupingState().isValid());
		ChoiceSet<CNAbilitySelection> cs = new ChoiceSet<CNAbilitySelection>(
				getSubToken().getTokenName(), rcs);
		cs.setTitle("Virtual Feat Selection");
		PersistentTransitionChoice<CNAbilitySelection> tc = new ConcretePersistentTransitionChoice<CNAbilitySelection>(
				cs, FormulaFactory.getFormulaFor(3));
		tc.allowStack(true);
		tc.setStackLimit(2);
		tc.setChoiceActor(subtoken);
		primaryProf.addToListFor(ListKey.ADD, tc);
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		expectSingle(unparsed, getSubTokenName() + '|' + "3|STACKS=2,TestWP1");
	}

}
