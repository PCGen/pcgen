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
import pcgen.cdom.content.Selection;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.helper.CategorizedAbilitySelection;
import pcgen.cdom.helper.ClassSource;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Campaign;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.EquipmentModifier;
import pcgen.core.PCCheck;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.character.CompanionMod;
import pcgen.persistence.PersistenceLayerException;

public abstract class AbstractGrantedListTokenTest<T extends CDOMObject>
		extends AbstractTokenModelTest
{
	@Test
	public void testFromAbility() throws PersistenceLayerException
	{
		Ability source = create(Ability.class, "Source");
		context.ref.reassociateCategory(AbilityCategory.FEAT, source);
		T granted = createGrantedObject();
		processToken(source);
		assertEquals(0, getCount());
		CategorizedAbilitySelection cas =
				new CategorizedAbilitySelection(AbilityCategory.FEAT, source,
					Nature.AUTOMATIC);
		directAbilityFacet.add(id, cas);
		assertTrue(containsExpected(granted));
		assertEquals((directAbilityFacet == getTargetFacet()) ? 2 : 1,
			getCount());
		directAbilityFacet.remove(id, cas);
		assertEquals(0, getCount());
	}

	@Test
	public void testFromAlignment() throws PersistenceLayerException
	{
		T granted = createGrantedObject();
		processToken(lg);
		assertEquals(0, getCount());
		alignmentFacet.set(id, lg);
		assertTrue(containsExpected(granted));
		assertEquals(1, getCount());
		alignmentFacet.set(id, ng);
		assertEquals(0, getCount());
	}

	//BioSet not *supposed* to do things like this

	@Test
	public void testFromCampaign() throws PersistenceLayerException
	{
		Campaign source = create(Campaign.class, "Source");
		T granted = createGrantedObject();
		processToken(source);
		assertEquals(0, getCount());
		expandedCampaignFacet.add(id, source, this);
		assertTrue(containsExpected(granted));
		assertEquals((expandedCampaignFacet == getTargetFacet()) ? 2 : 1,
			getCount());
		expandedCampaignFacet.remove(id, source, this);
		assertEquals(0, getCount());
	}

	@Test
	public void testFromCheck() throws PersistenceLayerException
	{
		PCCheck source = create(PCCheck.class, "Source");
		T granted = createGrantedObject();
		processToken(source);
		/*
		 * We never get a chance to test zero since the Checks are added at
		 * Player Character Construction :)
		 */
		assertTrue(containsExpected(granted));
		assertEquals(1, getCount());
		checkFacet.remove(id, source);
		assertEquals(0, getCount());
	}

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
	}

	@Test
	public void testFromCompanionMod() throws PersistenceLayerException
	{
		CompanionMod source = create(CompanionMod.class, "Source");
		T granted = createGrantedObject();
		processToken(source);
		assertEquals(0, getCount());
		companionModFacet.add(id, source);
		assertTrue(containsExpected(granted));
		assertEquals(1, getCount());
		companionModFacet.remove(id, source);
		assertEquals(0, getCount());
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
	}

	@Test
	public void testFromEqMod() throws PersistenceLayerException
	{
		EquipmentModifier source = create(EquipmentModifier.class, "Source");
		T granted = createGrantedObject();
		processToken(source);
		assertEquals(0, getCount());
		activeEqModFacet.add(id, source, this);
		assertTrue(containsExpected(granted));
		assertEquals((activeEqModFacet == getTargetFacet()) ? 2 : 1, getCount());
		activeEqModFacet.remove(id, source, this);
		assertEquals(0, getCount());
	}

	//Language not *supposed* to do things like this

	@Test
	public void testFromRace() throws PersistenceLayerException
	{
		Race source = create(Race.class, "Source");
		T granted = createGrantedObject();
		processToken(source);
		assertEquals(0, getCount());
		raceFacet.set(id, getSelectionObject(source));
		assertTrue(containsExpected(granted));
		assertEquals(1, getCount());
		raceFacet.remove(id);
		assertEquals(0, getCount());
	}

	//TODO SizeFacet is not a very good model for doing this by hand :(
	//Need to separate the setting of size from the facet that holds it

	//Skill not *supposed* to do things like this

	@Test
	public void testFromStat() throws PersistenceLayerException
	{
		PCStat source = create(PCStat.class, "Source");
		T granted = createGrantedObject();
		processToken(source);
		/*
		 * We never get a chance to test zero since the Stats are added at
		 * Player Character Construction :)
		 */
		assertTrue(containsExpected(granted));
		assertEquals(1, getCount());
		statFacet.remove(id, source);
		assertEquals(0, getCount());
	}

	@Test
	public void testFromTemplate() throws PersistenceLayerException
	{
		PCTemplate source = create(PCTemplate.class, "Source");
		T granted = createGrantedObject();
		processToken(source);
		assertEquals(0, getCount());
		Selection<PCTemplate, ?> sel = getSelectionObject(source);
		templateFacet.add(id, sel, this);
		assertTrue(containsExpected(granted));
		assertEquals((templateConsolidationFacet == getTargetFacet()) ? 2 : 1, getCount());
		templateFacet.remove(id, sel, this);
		assertEquals(0, getCount());
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
