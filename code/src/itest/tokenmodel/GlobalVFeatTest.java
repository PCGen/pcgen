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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.facet.DirectAbilityFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.helper.CategorizedAbilitySelection;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.VFeatLst;
import tokenmodel.testsupport.AbstractGrantedListTokenTest;

public class GlobalVFeatTest extends AbstractGrantedListTokenTest<Ability>
{

	private static VFeatLst token = new VFeatLst();

	private static DirectAbilityFacet directAbilityFacet = FacetLibrary
		.getFacet(DirectAbilityFacet.class);

	@Override
	public void processToken(CDOMObject source)
	{
		ParseResult result = token.parseToken(context, source, "Granted");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		finishLoad();
	}

	@Override
	protected Class<Ability> getGrantClass()
	{
		return Ability.class;
	}

	@Override
	protected DirectAbilityFacet getTargetFacet()
	{
		return directAbilityFacet;
	}

	@Override
	public CDOMToken<?> getToken()
	{
		return token;
	}

	@Override
	protected int getCount()
	{
		return getTargetFacet().getCount(id);
	}

	@Override
	protected boolean containsExpected(Ability granted)
	{
		Collection<CategorizedAbilitySelection> casSet =
				getTargetFacet().getSet(id);
		boolean sizeExpected = (casSet.size() == 1);
		CategorizedAbilitySelection cas = casSet.iterator().next();
		boolean featExpected = cas.getAbilityCategory() == AbilityCategory.FEAT;
		boolean abilityExpected =
				cas.getAbility().equals(
					context.ref.silentlyGetConstructedCDOMObject(Ability.class,
						AbilityCategory.FEAT, "Granted"));
		boolean natureExpected = cas.getNature() == Nature.VIRTUAL;
		boolean selectionExpected = cas.getSelection() == null;
		return sizeExpected && featExpected && abilityExpected
			&& natureExpected && selectionExpected;
	}

	@Override
	protected Ability createGrantedObject()
	{
		Ability a = super.createGrantedObject();
		context.ref.reassociateCategory(AbilityCategory.FEAT, a);
		return a;
	}

}
