/*
 * Copyright (c) 2012-14 Tom Parker <thpr@users.sourceforge.net>
 * 
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
package tokenmodel.testsupport;

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.UserSelection;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.cdom.helper.ClassSource;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import plugin.lsttokens.testsupport.BuildUtilities;

public abstract class AbstractAddListTokenTest<T extends CDOMObject>
		extends AbstractTokenModelTest
{
	@Test
	public void testFromAbility() throws PersistenceLayerException
	{
		Ability source = BuildUtilities.buildFeat(context, "Source");
		T granted = createGrantedObject();
		processToken(source);
		assertEquals(0, getCount());
		CNAbilitySelection cas =
				new CNAbilitySelection(CNAbilityFactory.getCNAbility(AbilityCategory.FEAT, Nature.AUTOMATIC, source));
		directAbilityFacet.add(id, cas, UserSelection.getInstance());
		assertTrue(containsExpected(granted));
		assertEquals((directAbilityFacet == getTargetFacet()) ? 2 : 1,
			getCount());
		directAbilityFacet.remove(id, cas, UserSelection.getInstance());
		assertEquals(0, getCount());
		assertTrue(cleanedSideEffects());
	}

	//BioSet not *supposed* to do things like this

	@Test
	public void testFromClass() throws PersistenceLayerException
	{
		PCClass source = create(PCClass.class, "Source");
		T granted = createGrantedObject();
		processToken(source);
		assertEquals(0, getCount());
		classFacet.addClass(id, source);
		assertTrue(containsExpected(granted));
		assertEquals(1, getCount());
		classFacet.removeClass(id, source);
		assertEquals(0, getCount());
		assertTrue(cleanedSideEffects());
	}

	@Test
	public void testFromClassLevel() throws PersistenceLayerException
	{
		PCClassLevel source = create(PCClassLevel.class, "Source");
		T granted = createGrantedObject();
		processToken(source);
		assertEquals(0, getCount());
		classLevelFacet.add(id, source, this);
		assertTrue(containsExpected(granted));
		assertEquals((classLevelFacet == getTargetFacet()) ? 2 : 1, getCount());
		classLevelFacet.remove(id, source, this);
		assertEquals(0, getCount());
		assertTrue(cleanedSideEffects());
	}

	@Test
	public void testFromDeity() throws PersistenceLayerException
	{
		Deity source = create(Deity.class, "Source");
		T granted = createGrantedObject();
		processToken(source);
		assertEquals(0, getCount());
		deityFacet.set(id, source);
		assertTrue(containsExpected(granted));
		assertEquals(1, getCount());
		deityFacet.remove(id);
		assertEquals(0, getCount());
		assertTrue(cleanedSideEffects());
	}

	@Test
	public void testFromDomain() throws PersistenceLayerException
	{
		Domain source = create(Domain.class, "Source");
		PCClass pcc = create(PCClass.class, "Class");
		T granted = createGrantedObject();
		processToken(source);
		assertEquals(0, getCount());
		ClassSource classSource = new ClassSource(pcc);
		domainFacet.add(id, source, classSource);
		assertTrue(containsExpected(granted));
		assertEquals(1, getCount());
		domainFacet.remove(id, source, classSource);
		assertEquals(0, getCount());
		assertTrue(cleanedSideEffects());
	}

	//Language not *supposed* to do things like this

	@Test
	public void testFromRace() throws PersistenceLayerException
	{
		Race source = create(Race.class, "Source");
		T granted = createGrantedObject();
		processToken(source);
		assertEquals(0, getCount());
		raceFacet.directSet(id, source, getAssoc());
		assertTrue(containsExpected(granted));
		assertEquals(1, getCount());
		raceFacet.remove(id);
		assertEquals(0, getCount());
		assertTrue(cleanedSideEffects());
	}

	//TODO SizeFacet is not a very good model for doing this by hand :(
	//Need to separate the setting of size from the facet that holds it

	//Skill not *supposed* to do things like this

	@Test
	public void testFromTemplate() throws PersistenceLayerException
	{
		PCTemplate source = create(PCTemplate.class, "Source");
		T granted = createGrantedObject();
		processToken(source);
		assertEquals(0, getCount());
		templateInputFacet.directAdd(id, source, getAssoc());
		assertTrue(containsExpected(granted));
		assertEquals((templateConsolidationFacet == getTargetFacet()) ? 2 : 1, getCount());
		templateInputFacet.remove(id, source);
		assertEquals(0, getCount());
		assertTrue(cleanedSideEffects());
	}

	protected boolean cleanedSideEffects()
	{
		return true;
	}

	//WeaponProf not *supposed* to do things like this

	protected abstract void processToken(CDOMObject source);

	protected T createGrantedObject()
	{
		return create(getGrantClass(), "Granted");
	}

	protected abstract Class<T> getGrantClass();

	protected abstract Object getTargetFacet();

	protected abstract int getCount();

	protected abstract boolean containsExpected(T granted);

}
