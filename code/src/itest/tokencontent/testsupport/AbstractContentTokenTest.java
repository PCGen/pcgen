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
package tokencontent.testsupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.UserSelection;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.cdom.helper.ClassSource;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.list.CompanionList;
import pcgen.core.Ability;
import pcgen.core.Campaign;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.core.PCCheck;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.character.CompanionMod;
import pcgen.output.channel.compat.AlignmentCompat;
import pcgen.output.channel.compat.DeityCompat;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.Test;
import tokenmodel.testsupport.AbstractTokenModelTest;

public abstract class AbstractContentTokenTest extends AbstractTokenModelTest
{
	@Test
	public void testFromAbility()
	{
		Ability source = BuildUtilities.buildFeat(context, "Source");
		processToken(source);
		assertEquals(baseCount(), targetFacetCount());
		CNAbilitySelection cas = new CNAbilitySelection(CNAbilityFactory
			.getCNAbility(BuildUtilities.getFeatCat(), Nature.AUTOMATIC, source));
		directAbilityFacet.add(id, cas, UserSelection.getInstance());
		assertTrue(containsExpected());
		assertEquals(baseCount() + 1, targetFacetCount());
		directAbilityFacet.remove(id, cas, UserSelection.getInstance());
		assertEquals(baseCount(), targetFacetCount());
	}

	@Test
	public void testFromAlignment()
	{
		processToken(lg);
		assertEquals(baseCount(), targetFacetCount());
		AlignmentCompat.setCurrentAlignment(pc.getCharID(), lg);
		assertTrue(containsExpected());
		assertEquals(baseCount() + 1, targetFacetCount());
		AlignmentCompat.setCurrentAlignment(pc.getCharID(), ng);
		assertEquals(baseCount(), targetFacetCount());
	}

	//BioSet not *supposed* to do things like this

	@Test
	public void testFromCampaign()
	{
		Campaign source = create(Campaign.class, "Source");
		processToken(source);
		assertEquals(baseCount(), targetFacetCount());
		expandedCampaignFacet.add(id, source, this);
		assertTrue(containsExpected());
		assertEquals(baseCount() + 1, targetFacetCount());
		expandedCampaignFacet.remove(id, source, this);
		assertEquals(baseCount(), targetFacetCount());
	}

	@Test
	public void testFromCheck()
	{
		PCCheck source = create(PCCheck.class, "Source");
		processToken(source);
		assertTrue(containsExpected());
		assertEquals(baseCount() + 1, targetFacetCount());
		checkFacet.remove(id, source);
		assertEquals(baseCount(), targetFacetCount());
	}

	@Test
	public void testFromClass()
	{
		PCClass source = create(PCClass.class, "Source");
		processToken(source);
		assertEquals(baseCount(), targetFacetCount());
		classFacet.addClass(id, source);
		assertTrue(containsExpected());
		assertEquals(baseCount() + 1, targetFacetCount());
		classFacet.removeClass(id, source);
		assertEquals(baseCount(), targetFacetCount());
	}

	@Test
	public void testFromClassLevel()
	{
		PCClassLevel source = create(PCClassLevel.class, "Source");
		processToken(source);
		assertEquals(baseCount(), targetFacetCount());
		classLevelFacet.add(id, source, this);
		assertTrue(containsExpected());
		assertEquals(baseCount() + 1, targetFacetCount());
		classLevelFacet.remove(id, source, this);
		assertEquals(baseCount(), targetFacetCount());
	}

	@Test
	public void testFromCompanionMod()
	{
		CompanionList cat = create(CompanionList.class, "Category");
		context.getReferenceContext().importObject(cat);
		CompanionMod source = cat.newInstance();
		cat.setKeyName("Source");
		context.getReferenceContext().importObject(source);
		processToken(source);
		assertEquals(baseCount(), targetFacetCount());
		companionModFacet.add(id, source);
		assertTrue(containsExpected());
		assertEquals(baseCount() + 1, targetFacetCount());
		companionModFacet.remove(id, source);
		assertEquals(baseCount(), targetFacetCount());
	}

	@Test
	public void testFromDeity()
	{
		Deity source = create(Deity.class, "Source");
		processToken(source);
		assertEquals(baseCount(), targetFacetCount());
		DeityCompat.setCurrentDeity(id, source);
		assertTrue(containsExpected());
		assertEquals(baseCount() + 1, targetFacetCount());
		DeityCompat.setCurrentDeity(id, null);
		assertEquals(baseCount(), targetFacetCount());
	}

	@Test
	public void testFromDomain()
	{
		Domain source = create(Domain.class, "Source");
		PCClass pcc = create(PCClass.class, "Class");
		processToken(source);
		assertEquals(baseCount(), targetFacetCount());
		ClassSource classSource = new ClassSource(pcc);
		domainFacet.add(id, source, classSource);
		assertTrue(containsExpected());
		assertEquals(baseCount() + 1, targetFacetCount());
		domainFacet.remove(id, source, classSource);
		assertEquals(baseCount(), targetFacetCount());
	}

	@Test
	public void testFromEqMod()
	{
		EquipmentModifier source = create(EquipmentModifier.class, "Source");
		Equipment equipment = create(Equipment.class, "Parent");
		source.setVariableParent(equipment);
		processToken(source);
		assertEquals(baseCount(), targetFacetCount());
		activeEqModFacet.add(id, source, this);
		assertTrue(containsExpected());
		assertEquals(baseCount() + 1, targetFacetCount());
		activeEqModFacet.remove(id, source, this);
		assertEquals(baseCount(), targetFacetCount());
	}

	//Language not *supposed* to do things like this

	@Test
	public void testFromRace()
	{
		Race source = create(Race.class, "Source");
		processToken(source);
		assertEquals(baseCount(), targetFacetCount());
		raceFacet.directSet(id, source, getAssoc());
		assertTrue(containsExpected());
		assertEquals(baseCount() + 1, targetFacetCount());
		raceFacet.remove(id);
		assertEquals(baseCount(), targetFacetCount());
	}

	//TODO SizeFacet is not a very good model for doing this by hand :(
	//Need to separate the setting of size from the facet that holds it

	//Skill not *supposed* to do things like this

	@Test
	public void testFromStat()
	{
		PCStat source = create(PCStat.class, "Source");
		source.put(StringKey.SORT_KEY, "Source");
		processToken(source);
		assertTrue(containsExpected());
		assertEquals(baseCount() + 1, targetFacetCount());
		statFacet.remove(id, source);
		assertEquals(baseCount(), targetFacetCount());
	}

	@Test
	public void testFromTemplate()
	{
		PCTemplate source = create(PCTemplate.class, "Source");
		processToken(source);
		assertEquals(baseCount(), targetFacetCount());
		templateInputFacet.directAdd(id, source, getAssoc());
		assertTrue(containsExpected());
		assertEquals(baseCount() + 1, targetFacetCount());
		templateInputFacet.remove(id, source);
		assertEquals(baseCount(), targetFacetCount());
	}

	//WeaponProf not *supposed* to do things like this

	protected abstract void processToken(CDOMObject source);

	protected abstract boolean containsExpected();

	protected abstract int targetFacetCount();

	protected abstract int baseCount();

}
