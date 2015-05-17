/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.cdom.base.UserSelection;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.facet.DirectAbilityFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Language;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.ability.MultToken;
import plugin.lsttokens.auto.FeatToken;
import plugin.lsttokens.choose.LangToken;
import tokenmodel.testsupport.AbstractTokenModelTest;

public class AutoWeaponProfListTargetTest extends AbstractTokenModelTest
{
	private static FeatToken token = new FeatToken();

	@Test
	public void testFromTemplate() throws PersistenceLayerException
	{
		PCTemplate source = create(PCTemplate.class, "Source");
		Ability granted = createGrantedObject();
		context.getReferenceContext().constructCDOMObject(Language.class, "English");
		ParseResult result =
				new MultToken().parseToken(context, granted, "YES");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		result = new LangToken().parseToken(context, granted, "ALL");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		result = new LangToken().parseToken(context, source, "ALL");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		result = token.parseToken(context, source, "Granted (%LIST)");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		finishLoad();
		assertEquals(0, directAbilityFacet.size(id));
		Object sel = getAssoc();
		templateInputFacet.directAdd(id, source, sel);
		assertTrue(containsExpected());
		assertEquals(1, directAbilityFacet.size(id));
		templateInputFacet.remove(id, source);
		assertEquals(0, directAbilityFacet.size(id));
	}

	@Test
	public void testFromAbility() throws PersistenceLayerException
	{
		Ability source = create(Ability.class, "Source");
		context.getReferenceContext().reassociateCategory(AbilityCategory.FEAT, source);
		Ability granted = createGrantedObject();
		context.getReferenceContext().constructCDOMObject(Language.class, "English");
		ParseResult result =
				new MultToken().parseToken(context, granted, "YES");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		result = new MultToken().parseToken(context, source, "YES");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		result = new LangToken().parseToken(context, granted, "ALL");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		result = new LangToken().parseToken(context, source, "ALL");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		result = token.parseToken(context, source, "Granted (%LIST)");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		finishLoad();
		assertEquals(0, directAbilityFacet.size(id));
		CNAbilitySelection cas =
				new CNAbilitySelection(CNAbilityFactory.getCNAbility(AbilityCategory.FEAT, Nature.AUTOMATIC, source), "English");
		directAbilityFacet.add(id, cas, UserSelection.getInstance());
		assertTrue(containsExpected());
		assertEquals(2, directAbilityFacet.size(id));
		directAbilityFacet.remove(id, cas, UserSelection.getInstance());
		assertEquals(0, directAbilityFacet.size(id));
	}

	@Override
	protected Language getAssoc()
	{
		return context.getReferenceContext().silentlyGetConstructedCDOMObject(Language.class,
				"English");
	}

	private boolean containsExpected()
	{
		Collection<CNAbilitySelection> casSet =
				directAbilityFacet.getSet(id);
		for (CNAbilitySelection cnas : casSet)
		{
			CNAbility cas = cnas.getCNAbility();
			boolean featExpected =
					cas.getAbilityCategory() == AbilityCategory.FEAT;
			boolean abilityExpected =
					cas.getAbility().equals(
						context.getReferenceContext().silentlyGetConstructedCDOMObject(
							Ability.class, AbilityCategory.FEAT, "Granted"));
			boolean natureExpected = cas.getNature() == Nature.AUTOMATIC;
			boolean selectionExpected = "English".equals(cnas.getSelection());
			if (featExpected && abilityExpected && natureExpected
				&& selectionExpected)
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public CDOMToken<?> getToken()
	{
		return token;
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		directAbilityFacet = FacetLibrary.getFacet(DirectAbilityFacet.class);
	}

	protected Ability createGrantedObject()
	{
		Ability a = create(Ability.class, "Granted");
		context.getReferenceContext().reassociateCategory(AbilityCategory.FEAT, a);
		return a;
	}
}