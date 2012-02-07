/*
 * 
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package resultactor.auto;

import org.junit.Test;

import pcgen.base.test.InequalityTester;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseResultActor;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.AbstractStorageFacet;
import pcgen.cdom.helper.AbilitySelector;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.ReferenceContext;
import plugin.lsttokens.auto.FeatToken;
import resultactor.testsupport.AbstractResultActorTest;
import selectionactor.testsupport.InequalityTesterInst;

public class FeatTokenTest extends AbstractResultActorTest<Ability>
{

	static FeatToken cra = new FeatToken();

	@Test
	public void testEmpty()
	{
		// Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}

	@Override
	public ChooseResultActor getActor()
	{
		return new AbilitySelector(cra.getTokenName(), AbilityCategory.FEAT,
				Nature.AUTOMATIC);
	}

	@Override
	public Class<Ability> getCDOMClass()
	{
		return Ability.class;
	}

	@Override
	public boolean isGranted()
	{
		return true;
	}

	@Override
	protected Ability construct(String name)
	{
		ReferenceContext refContext = Globals.getContext().ref;
		Ability obj = refContext.constructCDOMObject(getCDOMClass(), name);
		refContext.reassociateCategory(AbilityCategory.FEAT, obj);
		return obj;
	}

	@Test
	public void testAddRemoveParen() throws PersistenceLayerException
	{
		setUpPC();
		finishLoad(Globals.getContext());
		InequalityTester it = InequalityTesterInst.getInstance();
		CDOMObject owner = getOwner();
		Ability t = construct("Templ (paren)");
		Ability t2 = construct("Templ2 (other)");
		PlayerCharacter pc1 = new PlayerCharacter();
		PlayerCharacter pc2 = new PlayerCharacter();
		preparePC(pc1, owner);
		preparePC(pc2, owner);
		String key = getPersistentFormat(t);
		String key2 = getPersistentFormat(t2);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		ChooseResultActor actor = getActor();
		actor.apply(pc2, owner, key);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		actor.apply(pc1, owner, key);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		actor.remove(pc2, owner, key);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		actor.remove(pc1, owner, key);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		actor.apply(pc2, owner, key);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		actor.apply(pc1, owner, key);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		actor.remove(pc2, owner, key);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		actor.remove(pc1, owner, key);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		actor.apply(pc1, owner, key);
		actor.apply(pc1, owner, key2);
		actor.remove(pc1, owner, key);
		actor.apply(pc2, owner, key2);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
	}

	@Test
	public void testAddRemoveParenHasChild() throws PersistenceLayerException
	{
		if (isGranted())
		{
			setUpPC();
			Ability t = construct("Templ (other)");
			Ability t2 = construct("Templ2 (paren)");
			String key = getPersistentFormat(t);
			String key2 = getPersistentFormat(t2);
			Globals.getContext().unconditionallyProcess(t, "AUTO", "LANG|Universal");
			Globals.getContext().unconditionallyProcess(t2, "AUTO", "LANG|Other");
			finishLoad(Globals.getContext());
			InequalityTester it = InequalityTesterInst.getInstance();
			CDOMObject owner = getOwner();
			PlayerCharacter pc1 = new PlayerCharacter();
			PlayerCharacter pc2 = new PlayerCharacter();
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			ChooseResultActor actor = getActor();
			actor.apply(pc2, owner, key);
			assertTrue(pc2.hasLanguage(universal));
			assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			actor.apply(pc1, owner, key);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			actor.remove(pc2, owner, key);
			assertFalse(pc2.hasLanguage(universal));
			assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			actor.remove(pc1, owner, key);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			actor.apply(pc2, owner, key);
			assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			actor.apply(pc1, owner, key);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			actor.remove(pc2, owner, key);
			assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			actor.remove(pc1, owner, key);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			actor.apply(pc1, owner, key);
			actor.apply(pc1, owner, key2);
			actor.remove(pc1, owner, key);
			actor.apply(pc2, owner, key2);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		}
	}

	@Test
	public void testAddRemoveAssoc() throws PersistenceLayerException
	{
		setUpPC();
		finishLoad(Globals.getContext());
		InequalityTester it = InequalityTesterInst.getInstance();
		CDOMObject owner = getOwner();
		Ability t = construct("Templ");
		t.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
		Ability t2 = construct("Templ2");
		t2.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
		PlayerCharacter pc1 = new PlayerCharacter();
		PlayerCharacter pc2 = new PlayerCharacter();
		preparePC(pc1, owner);
		preparePC(pc2, owner);
		String key = getPersistentFormat(t) + "(target)";
		String key2 = getPersistentFormat(t2) + "(target)";
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		ChooseResultActor actor = getActor();
		actor.apply(pc2, owner, key);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		actor.apply(pc1, owner, key);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		actor.remove(pc2, owner, key);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		actor.remove(pc1, owner, key);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		actor.apply(pc2, owner, key);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		actor.apply(pc1, owner, key);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		actor.remove(pc2, owner, key);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		actor.remove(pc1, owner, key);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		actor.apply(pc1, owner, key);
		actor.apply(pc1, owner, key2);
		actor.remove(pc1, owner, key);
		actor.apply(pc2, owner, key2);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
	}

	@Test
	public void testAddRemoveAssocHasChild() throws PersistenceLayerException
	{
		if (isGranted())
		{
			setUpPC();
			Ability t = construct("Templ");
			t.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
			Ability t2 = construct("Templ2");
			t2.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
			String key = getPersistentFormat(t) + "(target)";
			String key2 = getPersistentFormat(t2) + "(target)";
			Globals.getContext().unconditionallyProcess(t, "AUTO", "LANG|Universal");
			Globals.getContext().unconditionallyProcess(t2, "AUTO", "LANG|Other");
			finishLoad(Globals.getContext());
			InequalityTester it = InequalityTesterInst.getInstance();
			CDOMObject owner = getOwner();
			PlayerCharacter pc1 = new PlayerCharacter();
			PlayerCharacter pc2 = new PlayerCharacter();
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			ChooseResultActor actor = getActor();
			actor.apply(pc2, owner, key);
			assertTrue(pc2.hasLanguage(universal));
			assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			actor.apply(pc1, owner, key);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			actor.remove(pc2, owner, key);
			assertFalse(pc2.hasLanguage(universal));
			assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			actor.remove(pc1, owner, key);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			actor.apply(pc2, owner, key);
			assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			actor.apply(pc1, owner, key);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			actor.remove(pc2, owner, key);
			assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			actor.remove(pc1, owner, key);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			actor.apply(pc1, owner, key);
			actor.apply(pc1, owner, key2);
			actor.remove(pc1, owner, key);
			actor.apply(pc2, owner, key2);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		}
	}
}