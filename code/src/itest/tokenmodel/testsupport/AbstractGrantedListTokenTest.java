/*
 * Copyright (c) 2012 Tom Parker <thpr@users.sourceforge.net>
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
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.helper.ClassSource;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Campaign;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.EquipmentModifier;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.character.CompanionMod;
import pcgen.persistence.PersistenceLayerException;

public abstract class AbstractGrantedListTokenTest<T extends CDOMObject>
		extends AbstractTokenModelTest
{

	@Test
	public void testFromAlignment() throws PersistenceLayerException
	{
		T granted = createGrantedObject();
		processToken(lg);
		assertEquals(0, getTargetFacet().getCount(id));
		alignmentFacet.set(id, lg);
		assertTrue(getTargetFacet().contains(id, granted));
		assertEquals(1, getTargetFacet().getCount(id));
	}

	//BioSet not *supposed* to do things like this

	@Test
	public void testFromCampaign() throws PersistenceLayerException
	{
		Campaign source = create(Campaign.class, "Source");
		T granted = createGrantedObject();
		processToken(source);
		assertEquals(0, getTargetFacet().getCount(id));
		expandedCampaignFacet.add(id, source, this);
		assertTrue(getTargetFacet().contains(id, granted));
		assertEquals((expandedCampaignFacet == getTargetFacet()) ? 2 : 1,
			getTargetFacet().getCount(id));
	}

	//Check not *supposed* to do things like this

	@Test
	public void testFromClass() throws PersistenceLayerException
	{
		PCClass source = create(PCClass.class, "Source");
		T granted = createGrantedObject();
		processToken(source);
		assertEquals(0, getTargetFacet().getCount(id));
		classFacet.addClass(id, source);
		assertTrue(getTargetFacet().contains(id, granted));
		assertEquals(1, getTargetFacet().getCount(id));
	}

	@Test
	public void testFromClassLevel() throws PersistenceLayerException
	{
		PCClassLevel source = create(PCClassLevel.class, "Source");
		T granted = createGrantedObject();
		processToken(source);
		assertEquals(0, getTargetFacet().getCount(id));
		classLevelFacet.add(id, source, this);
		assertTrue(getTargetFacet().contains(id, granted));
		assertEquals((classLevelFacet == getTargetFacet()) ? 2 : 1,
			getTargetFacet().getCount(id));
	}

	@Test
	public void testFromCompanionMod() throws PersistenceLayerException
	{
		CompanionMod source = create(CompanionMod.class, "Source");
		T granted = createGrantedObject();
		processToken(source);
		assertEquals(0, getTargetFacet().getCount(id));
		companionModFacet.add(id, source);
		assertTrue(getTargetFacet().contains(id, granted));
		assertEquals(1, getTargetFacet().getCount(id));
	}

	/*
	 * TODO An opportunity exists here to consolidate to allow this to test
	 * CompanionMod and Domain objects, however that requires AbstractListFacet
	 * and AbstractSourcedListFacet to share an interface that could be used
	 * here.
	 */

	@Test
	public void testFromDeity() throws PersistenceLayerException
	{
		Deity source = create(Deity.class, "Source");
		T granted = createGrantedObject();
		processToken(source);
		deityFacet.set(id, source);
		assertTrue(getTargetFacet().contains(id, granted));
		assertEquals(1, getTargetFacet().getCount(id));
	}

	@Test
	public void testFromDomain() throws PersistenceLayerException
	{
		Domain source = create(Domain.class, "Source");
		PCClass pcc = create(PCClass.class, "Class");
		T granted = createGrantedObject();
		processToken(source);
		assertEquals(0, getTargetFacet().getCount(id));
		domainFacet.add(id, source, new ClassSource(pcc));
		assertTrue(getTargetFacet().contains(id, granted));
		assertEquals(1, getTargetFacet().getCount(id));
	}

	@Test
	public void testFromEqMod() throws PersistenceLayerException
	{
		EquipmentModifier source = create(EquipmentModifier.class, "Source");
		T granted = createGrantedObject();
		processToken(source);
		assertEquals(0, getTargetFacet().getCount(id));
		activeEqModFacet.add(id, source, this);
		assertTrue(getTargetFacet().contains(id, granted));
		assertEquals((activeEqModFacet == getTargetFacet()) ? 2 : 1,
			getTargetFacet().getCount(id));
	}

	//Language not *supposed* to do things like this

	@Test
	public void testFromRace() throws PersistenceLayerException
	{
		Race source = create(Race.class, "Source");
		T granted = createGrantedObject();
		processToken(source);
		raceFacet.set(id, source);
		assertTrue(getTargetFacet().contains(id, granted));
		assertEquals(1, getTargetFacet().getCount(id));
	}

	//TODO SizeFacet is not a very good model for doing this by hand :(
	//Need to separate the setting of size from the facet that holds it

	//Skill not *supposed* to do things like this
	//Stat not *supposed* to do things like this

	@Test
	public void testFromTemplate() throws PersistenceLayerException
	{
		PCTemplate source = create(PCTemplate.class, "Source");
		T granted = createGrantedObject();
		processToken(source);
		assertEquals(0, templateFacet.getCount(id));
		templateFacet.add(id, source, this);
		assertTrue(getTargetFacet().contains(id, granted));
		assertEquals((templateFacet == getTargetFacet()) ? 2 : 1,
			getTargetFacet().getCount(id));
	}

	//WeaponProf not *supposed* to do things like this

	protected abstract void processToken(CDOMObject source);

	protected T createGrantedObject()
	{
		return create(getGrantClass(), "Granted");
	}

	protected abstract Class<T> getGrantClass();

	protected abstract AbstractSourcedListFacet<T> getTargetFacet();

}
