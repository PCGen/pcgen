/*
 * 
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net> This program is
 * free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version.
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
package selectionactor.auto;

import org.junit.Test;

import pcgen.base.test.InequalityTester;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.content.AbilitySelection;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.base.AbstractStorageFacet;
import pcgen.cdom.helper.AbilitySelector;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.testsupport.AbstractCharacterUsingTestCase;
import plugin.lsttokens.auto.FeatToken;
import plugin.lsttokens.choose.StringToken;

import compare.InequalityTesterInst;

public class FeatTokenTest extends AbstractCharacterUsingTestCase
{

	private static final Class<Ability> ABILITY_CLASS = Ability.class;
	static FeatToken cra = new FeatToken();

	@Test
	public void testEmpty()
	{
		// Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}

	public ChooseSelectionActor<AbilitySelection> getActor()
	{
		return new AbilitySelector(cra.getTokenName(), AbilityCategory.FEAT,
			Nature.AUTOMATIC);
	}

	public Class<CNAbilitySelection> getCDOMClass()
	{
		return CNAbilitySelection.class;
	}

	public boolean isGranted()
	{
		return true;
	}

	protected Ability construct(String name)
	{
		AbstractReferenceContext refContext = Globals.getContext().getReferenceContext();
		Ability obj = refContext.constructCDOMObject(ABILITY_CLASS, name);
		refContext.reassociateCategory(AbilityCategory.FEAT, obj);
		return obj;
	}

	@Test
	public void testAddRemoveSimple() throws PersistenceLayerException
	{
		setUpPC();
		finishLoad(Globals.getContext());
		InequalityTester it = InequalityTesterInst.getInstance();
		ChooseDriver owner = getOwner();
		Ability a = construct("Templ");
		Ability a2 = construct("Templ2");
		AbilitySelection t = new AbilitySelection(a, null);
		AbilitySelection t2 = new AbilitySelection(a2, null);
		PlayerCharacter pc1 = new PlayerCharacter();
		PlayerCharacter pc2 = new PlayerCharacter();
		preparePC(pc1, owner);
		preparePC(pc2, owner);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		ChooseSelectionActor<AbilitySelection> actor = getActor();
		actor.applyChoice(owner, t, pc2);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		actor.applyChoice(owner, t, pc1);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		actor.removeChoice(owner, t, pc2);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		actor.removeChoice(owner, t, pc1);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		actor.applyChoice(owner, t, pc2);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		actor.applyChoice(owner, t, pc1);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		actor.removeChoice(owner, t, pc2);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		actor.removeChoice(owner, t, pc1);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		actor.applyChoice(owner, t, pc1);
		actor.applyChoice(owner, t2, pc1);
		actor.removeChoice(owner, t, pc1);
		actor.applyChoice(owner, t2, pc2);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
	}

	@Test
	public void testAddRemoveHasChild() throws PersistenceLayerException
	{
		if (isGranted())
		{
			setUpPC();
			Ability a = construct("Templ");
			Ability a2 = construct("Templ2");
			Globals.getContext().unconditionallyProcess(a, "AUTO",
					"LANG|Universal");
			Globals.getContext().unconditionallyProcess(a2, "AUTO",
					"LANG|Other");
			ChooseDriver owner = getOwner();
			AbilitySelection t = new AbilitySelection(a, null);
			AbilitySelection t2 = new AbilitySelection(a2, null);
			finishLoad(Globals.getContext());
			InequalityTester it = InequalityTesterInst.getInstance();
			PlayerCharacter pc1 = new PlayerCharacter();
			PlayerCharacter pc2 = new PlayerCharacter();
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			ChooseSelectionActor<AbilitySelection> actor = getActor();
			actor.applyChoice(owner, t, pc2);
			assertTrue(pc2.hasLanguage(universal));
			assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			actor.applyChoice(owner, t, pc1);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			actor.removeChoice(owner, t, pc2);
			assertFalse(pc2.hasLanguage(universal));
			assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			actor.removeChoice(owner, t, pc1);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			actor.applyChoice(owner, t, pc2);
			assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			actor.applyChoice(owner, t, pc1);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			actor.removeChoice(owner, t, pc2);
			assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			actor.removeChoice(owner, t, pc1);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			actor.applyChoice(owner, t, pc1);
			actor.applyChoice(owner, t2, pc1);
			actor.removeChoice(owner, t, pc1);
			actor.applyChoice(owner, t2, pc2);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
		}
	}

	@Test
	public void testAddRemoveParen() throws PersistenceLayerException
	{
		setUpPC();
		finishLoad(Globals.getContext());
		InequalityTester it = InequalityTesterInst.getInstance();
		ChooseDriver owner = getOwner();
		Ability a = construct("Templ");
		Ability a2 = construct("Templ2");
		AbilitySelection t = new AbilitySelection(a, null);
		AbilitySelection t2 = new AbilitySelection(a2, null);
		PlayerCharacter pc1 = new PlayerCharacter();
		PlayerCharacter pc2 = new PlayerCharacter();
		preparePC(pc1, owner);
		preparePC(pc2, owner);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		ChooseSelectionActor<AbilitySelection> actor = getActor();
		actor.applyChoice(owner, t, pc2);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		actor.applyChoice(owner, t, pc1);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		actor.removeChoice(owner, t, pc2);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		actor.removeChoice(owner, t, pc1);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		actor.applyChoice(owner, t, pc2);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		actor.applyChoice(owner, t, pc1);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		actor.removeChoice(owner, t, pc2);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		actor.removeChoice(owner, t, pc1);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		actor.applyChoice(owner, t, pc1);
		actor.applyChoice(owner, t2, pc1);
		actor.removeChoice(owner, t, pc1);
		actor.applyChoice(owner, t2, pc2);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
	}

	@Test
	public void testAddRemoveParenHasChild() throws PersistenceLayerException
	{
		if (isGranted())
		{
			setUpPC();
			Ability a = construct("Templ (other)");
			Ability a2 = construct("Templ2 (paren)");
			ChooseDriver owner = getOwner();
			Globals.getContext().unconditionallyProcess(a, "AUTO",
					"LANG|Universal");
			Globals.getContext().unconditionallyProcess(a2, "AUTO",
					"LANG|Other");
			AbilitySelection t = new AbilitySelection(a, null);
			AbilitySelection t2 = new AbilitySelection(a2, null);
			finishLoad(Globals.getContext());
			InequalityTester it = InequalityTesterInst.getInstance();
			PlayerCharacter pc1 = new PlayerCharacter();
			PlayerCharacter pc2 = new PlayerCharacter();
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			ChooseSelectionActor<AbilitySelection> actor =
					getActor();
			actor.applyChoice(owner, t, pc2);
			assertTrue(pc2.hasLanguage(universal));
			assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			actor.applyChoice(owner, t, pc1);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			actor.removeChoice(owner, t, pc2);
			assertFalse(pc2.hasLanguage(universal));
			assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			actor.removeChoice(owner, t, pc1);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			actor.applyChoice(owner, t, pc2);
			assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			actor.applyChoice(owner, t, pc1);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			actor.removeChoice(owner, t, pc2);
			assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			actor.removeChoice(owner, t, pc1);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			actor.applyChoice(owner, t, pc1);
			actor.applyChoice(owner, t2, pc1);
			actor.removeChoice(owner, t, pc1);
			actor.applyChoice(owner, t2, pc2);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
		}
	}

	@Test
	public void testAddRemoveAssoc() throws PersistenceLayerException
	{
		setUpPC();
		InequalityTester it = InequalityTesterInst.getInstance();
		ChooseDriver owner = getOwner();
		Ability a = construct("Templ");
		a.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
		Ability a2 = construct("Templ2");
		a2.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
		StringToken st = new plugin.lsttokens.choose.StringToken();
		LoadContext context = Globals.getContext();
		ParseResult pr = st.parseToken(context, a, "Perception|Acrobatics");
		assertTrue(pr.passed());
		pr = st.parseToken(context, a2, "Knowledge|Diplomacy");
		assertTrue(pr.passed());
		context.commit();
		finishLoad(context);
		AbilitySelection t = new AbilitySelection(a, "Perception");
		AbilitySelection t2 = new AbilitySelection(a2, "Diplomacy");
		PlayerCharacter pc1 = new PlayerCharacter();
		PlayerCharacter pc2 = new PlayerCharacter();
		preparePC(pc1, owner);
		preparePC(pc2, owner);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		ChooseSelectionActor<AbilitySelection> actor = getActor();
		actor.applyChoice(owner, t, pc2);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		actor.applyChoice(owner, t, pc1);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		actor.removeChoice(owner, t, pc2);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		actor.removeChoice(owner, t, pc1);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		actor.applyChoice(owner, t, pc2);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		actor.applyChoice(owner, t, pc1);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		actor.removeChoice(owner, t, pc2);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		actor.removeChoice(owner, t, pc1);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
		actor.applyChoice(owner, t, pc1);
		actor.applyChoice(owner, t2, pc1);
		actor.removeChoice(owner, t, pc1);
		actor.applyChoice(owner, t2, pc2);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
			pc2.getCharID(), it));
	}

	@Test
	public void testAddRemoveAssocHasChild() throws PersistenceLayerException
	{
		if (isGranted())
		{
			setUpPC();
			Ability a = construct("Templ");
			a.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
			Ability a2 = construct("Templ2");
			a2.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
			LoadContext context = Globals.getContext();
			context.unconditionallyProcess(a, "AUTO", "LANG|Universal");
			context.unconditionallyProcess(a2, "AUTO", "LANG|Other");
			StringToken st = new plugin.lsttokens.choose.StringToken();
			ParseResult pr = st.parseToken(context, a, "Perception|Acrobatics");
			assertTrue(pr.passed());
			pr = st.parseToken(context, a2, "Knowledge|Diplomacy");
			assertTrue(pr.passed());
			context.commit();
			ChooseDriver owner = getOwner();
			finishLoad(context);
			AbilitySelection t = new AbilitySelection(a, "Perception");
			AbilitySelection t2 = new AbilitySelection(a2, "Diplomacy");
			InequalityTester it = InequalityTesterInst.getInstance();
			PlayerCharacter pc1 = new PlayerCharacter();
			PlayerCharacter pc2 = new PlayerCharacter();
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			ChooseSelectionActor<AbilitySelection> actor =
					getActor();
			actor.applyChoice(owner, t, pc2);
			assertTrue(pc2.hasLanguage(universal));
			assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			actor.applyChoice(owner, t, pc1);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			actor.removeChoice(owner, t, pc2);
			assertFalse(pc2.hasLanguage(universal));
			assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			actor.removeChoice(owner, t, pc1);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			actor.applyChoice(owner, t, pc2);
			assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			actor.applyChoice(owner, t, pc1);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			actor.removeChoice(owner, t, pc2);
			assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			actor.removeChoice(owner, t, pc1);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
			actor.applyChoice(owner, t, pc1);
			actor.applyChoice(owner, t2, pc1);
			actor.removeChoice(owner, t, pc1);
			actor.applyChoice(owner, t2, pc2);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(),
				pc2.getCharID(), it));
		}
	}

	protected void preparePC(PlayerCharacter pc1, ChooseDriver owner)
	{
	}

	protected ChooseDriver getOwner()
	{
		return new Domain();
	}

}