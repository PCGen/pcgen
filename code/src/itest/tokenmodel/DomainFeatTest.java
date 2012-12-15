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
import pcgen.cdom.facet.base.AbstractListFacet;
import pcgen.cdom.helper.CategorizedAbilitySelection;
import pcgen.cdom.helper.ClassSource;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Domain;
import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.domain.FeatToken;
import tokenmodel.testsupport.AbstractTokenModelTest;

public class DomainFeatTest extends AbstractTokenModelTest
{

	private static FeatToken token = new FeatToken();

	@Test
	public void testSimple() throws PersistenceLayerException
	{
		Domain source = create(Domain.class, "Source");
		PCClass pcc = create(PCClass.class, "Class");
		Ability granted = createGrantedObject();
		ParseResult result = token.parseToken(context, source, "Granted");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		finishLoad();
		assertEquals(0, directAbilityFacet.getCount(id));
		ClassSource classSource = new ClassSource(pcc);
		domainFacet.add(id, source, classSource);
		assertTrue(containsExpected(granted));
		assertEquals(1, directAbilityFacet.getCount(id));
		domainFacet.remove(id, source);
		assertEquals(0, directAbilityFacet.getCount(id));
	}

	protected boolean containsExpected(Ability granted)
	{
		Collection<CategorizedAbilitySelection> casSet =
				getTargetFacet().getSet(id);
		for (CategorizedAbilitySelection cas : casSet)
		{
			boolean featExpected =
					cas.getAbilityCategory() == AbilityCategory.FEAT;
			boolean abilityExpected =
					cas.getAbility().equals(
						context.ref.silentlyGetConstructedCDOMObject(
							Ability.class, AbilityCategory.FEAT, "Granted"));
			boolean natureExpected = cas.getNature() == Nature.AUTOMATIC;
			boolean selectionExpected = cas.getSelection() == null;
			if (featExpected && abilityExpected && natureExpected
				&& selectionExpected)
			{
				return true;
			}
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
}
