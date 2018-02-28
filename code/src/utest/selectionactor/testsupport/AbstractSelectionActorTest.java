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
package selectionactor.testsupport;

import org.junit.Test;

import compare.InequalityTesterInst;
import pcgen.base.test.InequalityTester;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.facet.base.AbstractStorageFacet;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.PersistenceLayerException;
import pcgen.testsupport.AbstractCharacterUsingTestCase;

public abstract class AbstractSelectionActorTest<T extends CDOMObject> extends AbstractCharacterUsingTestCase
{

	public abstract ChooseSelectionActor<T> getActor();

	public abstract Class<T> getCDOMClass();

	public abstract boolean isGranted();

	@Test
	public void testAddRemoveSimple() throws PersistenceLayerException
	{
		setUpPC();
		finishLoad(Globals.getContext());
		InequalityTester it = InequalityTesterInst.getInstance();
		ChooseDriver owner = getOwner();
		T t = construct("Templ");
		T t2 = construct("Templ2");
		PlayerCharacter pc1 = new PlayerCharacter();
		PlayerCharacter pc2 = new PlayerCharacter();
		preparePC(pc1, owner);
		preparePC(pc2, owner);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		ChooseSelectionActor<T> actor = getActor();
		actor.applyChoice(owner, t, pc2);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		actor.applyChoice(owner, t, pc1);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		actor.removeChoice(owner, t, pc2);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		actor.removeChoice(owner, t, pc1);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		actor.applyChoice(owner, t, pc2);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		actor.applyChoice(owner, t, pc1);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		actor.removeChoice(owner, t, pc2);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		actor.removeChoice(owner, t, pc1);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		actor.applyChoice(owner, t, pc1);
		actor.applyChoice(owner, t2, pc1);
		actor.removeChoice(owner, t, pc1);
		actor.applyChoice(owner, t2, pc2);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
	}

	protected void preparePC(PlayerCharacter pc1, ChooseDriver owner)
	{
	}

	protected String getPersistentFormat(T cdo)
	{
		return cdo.getKeyName();
	}

	protected ChooseDriver getOwner()
	{
		return new Domain();
	}

	@Test
	public void testAddRemoveHasChild() throws PersistenceLayerException
	{
		if (isGranted())
		{
			setUpPC();
			T t = construct("Templ");
			T t2 = construct("Templ2");
			Globals.getContext().unconditionallyProcess(t, "AUTO", "LANG|Universal");
			Globals.getContext().unconditionallyProcess(t2, "AUTO", "LANG|Other");
			finishLoad(Globals.getContext());
			InequalityTester it = InequalityTesterInst.getInstance();
			ChooseDriver owner = getOwner();
			PlayerCharacter pc1 = new PlayerCharacter();
			PlayerCharacter pc2 = new PlayerCharacter();
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			ChooseSelectionActor<T> actor = getActor();
			actor.applyChoice(owner, t, pc2);
			assertTrue(pc2.hasLanguage(universal));
			assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			actor.applyChoice(owner, t, pc1);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			actor.removeChoice(owner, t, pc2);
			assertFalse(pc2.hasLanguage(universal));
			assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			actor.removeChoice(owner, t, pc1);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			actor.applyChoice(owner, t, pc2);
			assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			actor.applyChoice(owner, t, pc1);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			actor.removeChoice(owner, t, pc2);
			assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			actor.removeChoice(owner, t, pc1);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
			actor.applyChoice(owner, t, pc1);
			actor.applyChoice(owner, t2, pc1);
			actor.removeChoice(owner, t, pc1);
			actor.applyChoice(owner, t2, pc2);
			assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		}
	}

	protected T construct(String name)
	{
		return Globals.getContext().getReferenceContext().constructCDOMObject(getCDOMClass(), name);
	}

}
