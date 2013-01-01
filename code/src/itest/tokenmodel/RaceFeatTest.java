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
package tokenmodel;

import java.util.Collection;

import org.junit.Test;

import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.base.AbstractListFacet;
import pcgen.cdom.facet.input.RaceInputFacet;
import pcgen.cdom.helper.CategorizedAbilitySelection;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Race;
import pcgen.core.WeaponProf;
import pcgen.gui2.facade.MockUIDelegate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.chooser.ChooserFactory;
import plugin.lsttokens.choose.WeaponProficiencyToken;
import plugin.lsttokens.race.FeatToken;
import plugin.lsttokens.testsupport.TokenRegistration;
import tokenmodel.testsupport.AbstractTokenModelTest;

public class RaceFeatTest extends AbstractTokenModelTest
{

	private static FeatToken token = new FeatToken();
	private static WeaponProficiencyToken CHOOSE_WP_TOKEN = new WeaponProficiencyToken();
	protected RaceInputFacet raceInputFacet = FacetLibrary
			.getFacet(RaceInputFacet.class);

	@Test
	public void testSimple() throws PersistenceLayerException
	{
		Race source = create(Race.class, "Source");
		Ability granted = createGrantedObject();
		ParseResult result = token.parseToken(context, source, "Granted");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		finishLoad();
		assertEquals(0, directAbilityFacet.getCount(id));
		raceFacet.set(id, getSelectionObject(source));
		assertTrue(containsExpected(granted, null));
		assertEquals(1, directAbilityFacet.getCount(id));
		raceFacet.remove(id);
		assertEquals(0, directAbilityFacet.getCount(id));
	}

	@Test
	public void testList() throws PersistenceLayerException
	{
		Race source = create(Race.class, "Source");
		create(WeaponProf.class, "Longsword");
		create(WeaponProf.class, "Dagger");
		Ability granted = createGrantedObject();
		granted.put(ObjectKey.MULTIPLE_ALLOWED, true);
		ParseResult result = token.parseToken(context, source, "Granted (%LIST)");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		result =
				CHOOSE_TOKEN.parseToken(context, source,
					"WEAPONPROFICIENCY|Longsword");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		result =
				CHOOSE_TOKEN.parseToken(context, granted,
					"WEAPONPROFICIENCY|Longsword|Dagger");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		finishLoad();
		assertEquals(0, directAbilityFacet.getCount(id));
		raceInputFacet.set(id, source);
		assertTrue(containsExpected(granted, "Longsword"));
		assertEquals(1, directAbilityFacet.getCount(id));
		raceInputFacet.remove(id);
		assertEquals(0, directAbilityFacet.getCount(id));
	}

	protected boolean containsExpected(Ability granted, String selection)
	{
		Collection<CategorizedAbilitySelection> casSet =
				getTargetFacet().getSet(id);
		for (CategorizedAbilitySelection cas : casSet)
		{
			boolean categoryExpected =
					cas.getAbilityCategory() == AbilityCategory.FEAT;
			if (!categoryExpected)
			{
				System.err.println("Category Mismatch");
				return false;
			}
			boolean abilityExpected =
					cas.getAbility().equals(
						context.ref.silentlyGetConstructedCDOMObject(
							Ability.class, AbilityCategory.FEAT, "Granted"));
			if (!abilityExpected)
			{
				System.err.println("Ability Mismatch");
				return false;
			}
			boolean natureExpected = cas.getNature() == Nature.AUTOMATIC;
			if (!natureExpected)
			{
				System.err.println("Nature Mismatch");
				return false;
			}
			if (selection == null)
			{
				if (cas.getSelection() != null)
				{
					System.err.println("Selection Mismatch");
					return false;
				}
			}
			else
			{
				boolean selectionExpected = cas.getSelection().equals(selection);
				if (!selectionExpected)
				{
					System.err.println("Selection Mismatch");
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private AbstractListFacet<CategorizedAbilitySelection> getTargetFacet()
	{
		return directAbilityFacet;
	}

	@Override
	public CDOMToken<?> getToken()
	{
		return token;
	}

	protected Ability createGrantedObject()
	{
		Ability a = create(Ability.class, "Granted");;
		context.ref.reassociateCategory(AbilityCategory.FEAT, a);
		return a;
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(CHOOSE_WP_TOKEN);
		ChooserFactory.setDelegate(new MockUIDelegate());
	}
	
	
}
