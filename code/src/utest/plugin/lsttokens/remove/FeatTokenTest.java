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
package plugin.lsttokens.remove;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.ConcretePersistentTransitionChoice;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.choiceset.AbilityRefChoiceSet;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.RemoveLst;
import plugin.lsttokens.testsupport.AbstractSelectionTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class FeatTokenTest extends
		AbstractSelectionTokenTestCase<CDOMObject, Ability>
{

	static RemoveLst token = new RemoveLst();
	static FeatToken subtoken = new FeatToken();
	static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<CDOMObject>();

	@Override
	public String getAllString()
	{
		return "ANY";
	}

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

	@Test
	public void testRoundRobinClass() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		construct(secondaryContext, "TestWP3");
		runRoundRobin(getSubTokenName() + '|' + "CLASS.Fighter,TestWP1"
				+ getJoinCharacter() + "TestWP2" + getJoinCharacter()
				+ "TestWP3");
	}

	@Test
	public void testRoundRobinClassValue() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		construct(secondaryContext, "TestWP3");
		runRoundRobin(getSubTokenName() + '|' + "CLASS.Fighter,TestWP1"
				+ getJoinCharacter() + "TestWP2" + getJoinCharacter()
				+ "TestWP3");
	}

	@Override
	public boolean allowsFormula()
	{
		return true;
	}

	@Test
	public void testInvalidInputClassUnbuilt() throws PersistenceLayerException
	{
		assertTrue(parse(getSubTokenName() + '|' + "CLASS=Fighter"));
		assertConstructionError();
	}

	@Test
	public void testInvalidInputDoubleEquals() throws PersistenceLayerException
	{
		boolean ret = parse(getSubTokenName() + '|' + "CLASS==Fighter");
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
	public void testInvalidInputOnlyClass() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "CLASS="));
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
	public void testUnparseNull() throws PersistenceLayerException
	{
		primaryProf.put(ObjectKey.CHOOSE_LANGAUTO, null);
		assertNull(getToken().unparse(primaryContext, primaryProf));
	}

	private AbilityRefChoiceSet build(String... names)
	{
		List<CDOMReference<Ability>> list = new ArrayList<CDOMReference<Ability>>();
		for (String name : names)
		{
			Ability ab = construct(primaryContext, name);
			CDOMDirectSingleRef<Ability> ar = CDOMDirectSingleRef.getRef(ab);
			if (name.indexOf('(') != -1)
			{
				List<String> choices = new ArrayList<String>();
				AbilityUtilities.getUndecoratedName(name, choices);
				assertEquals("Can't proceed if not true", 1, choices.size());
				ar.setChoice(choices.get(0));
			}
			list.add(ar);
		}
		AbilityRefChoiceSet rcs = new AbilityRefChoiceSet(AbilityCategory.FEAT,
				list, Nature.NORMAL);
		return rcs;
	}

	@Test
	public void testUnparseSingle() throws PersistenceLayerException
	{
		AbilityRefChoiceSet arcs = build("TestWP1");
		ChoiceSet<CNAbilitySelection> cs = new ChoiceSet<CNAbilitySelection>(
				getSubTokenName(), arcs, true);
		cs.setTitle("Select for removal");
		PersistentTransitionChoice<CNAbilitySelection> tc = new ConcretePersistentTransitionChoice<CNAbilitySelection>(
				cs, FormulaFactory.ONE);
		tc.setChoiceActor(subtoken);
		primaryProf.addToListFor(ListKey.REMOVE, tc);
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		expectSingle(unparsed, "FEAT|TestWP1");
	}

	@Test
	public void testUnparseBadCount() throws PersistenceLayerException
	{
		AbilityRefChoiceSet arcs = build("TestWP1");
		ChoiceSet<CNAbilitySelection> cs = new ChoiceSet<CNAbilitySelection>(
				getSubTokenName(), arcs, true);
		cs.setTitle("Select for removal");
		PersistentTransitionChoice<CNAbilitySelection> tc = new ConcretePersistentTransitionChoice<CNAbilitySelection>(
				cs, null);
		tc.setChoiceActor(subtoken);
		primaryProf.addToListFor(ListKey.REMOVE, tc);
		assertBadUnparse();
	}

	/*
	 * TODO Need to figure out who's responsibility this is!
	 */
	// @Test
	// public void testUnparseBadList() throws PersistenceLayerException
	// {
	// Language wp1 = construct(primaryContext, "TestWP1");
	// ReferenceChoiceSet<Language> rcs = buildRCS(CDOMDirectSingleRef
	// .getRef(wp1), primaryContext.ref
	// .getCDOMAllReference(getTargetClass()));
	// assertFalse(rcs.getGroupingState().isValid());
	// PersistentTransitionChoice<Language> tc = buildTC(rcs);
	// tc.setChoiceActor(subtoken);
	// primaryProf.put(ObjectKey.CHOOSE_LANGAUTO, tc);
	// assertBadUnparse();
	// }

	@Test
	public void testUnparseMultiple() throws PersistenceLayerException
	{
		AbilityRefChoiceSet arcs = build("TestWP1", "TestWP2");
		ChoiceSet<CNAbilitySelection> cs = new ChoiceSet<CNAbilitySelection>(
				getSubTokenName(), arcs, true);
		cs.setTitle("Select for removal");
		PersistentTransitionChoice<CNAbilitySelection> tc = new ConcretePersistentTransitionChoice<CNAbilitySelection>(
				cs, FormulaFactory.ONE);
		tc.setChoiceActor(subtoken);
		primaryProf.addToListFor(ListKey.REMOVE, tc);
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		expectSingle(unparsed, "FEAT|TestWP1" + getJoinCharacter() + "TestWP2");
	}

	@Test
	public void testUnparseMultipleParen() throws PersistenceLayerException
	{
		AbilityRefChoiceSet arcs = build("TestWP1 (Foo)", "TestWP2 (Bar)");
		ChoiceSet<CNAbilitySelection> cs = new ChoiceSet<CNAbilitySelection>(
				getSubTokenName(), arcs, true);
		cs.setTitle("Select for removal");
		PersistentTransitionChoice<CNAbilitySelection> tc = new ConcretePersistentTransitionChoice<CNAbilitySelection>(
				cs, FormulaFactory.ONE);
		tc.setChoiceActor(subtoken);
		primaryProf.addToListFor(ListKey.REMOVE, tc);
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		expectSingle(unparsed, "FEAT|TestWP1 (Foo)" + getJoinCharacter() + "TestWP2 (Bar)");
	}

	@Test
	public void testUnparseNullInList() throws PersistenceLayerException
	{
		List<CDOMReference<Ability>> list = new ArrayList<CDOMReference<Ability>>();
		Ability ab = construct(primaryContext, "TestWP1");
		CDOMDirectSingleRef<Ability> ar = CDOMDirectSingleRef.getRef(ab);
		list.add(ar);
		list.add(null);
		AbilityRefChoiceSet rcs = new AbilityRefChoiceSet(AbilityCategory.FEAT,
				list, Nature.NORMAL);
		AbilityRefChoiceSet arcs = rcs;
		ChoiceSet<CNAbilitySelection> cs = new ChoiceSet<CNAbilitySelection>(
				getSubTokenName(), arcs, true);
		cs.setTitle("Select for removal");
		PersistentTransitionChoice<CNAbilitySelection> tc = new ConcretePersistentTransitionChoice<CNAbilitySelection>(
				cs, FormulaFactory.ONE);
		tc.setChoiceActor(subtoken);
		primaryProf.addToListFor(ListKey.REMOVE, tc);
		try
		{
			getToken().unparse(primaryContext, primaryProf);
			fail();
		}
		catch (NullPointerException e)
		{
			// Yep!
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUnparseGenericsFail() throws PersistenceLayerException
	{
		ListKey listKey = ListKey.REMOVE;
		primaryProf.addToListFor(listKey, new Object());
		try
		{
			getToken().unparse(primaryContext, primaryProf);
			fail();
		}
		catch (ClassCastException e)
		{
			// Yep!
		}
	}
}
