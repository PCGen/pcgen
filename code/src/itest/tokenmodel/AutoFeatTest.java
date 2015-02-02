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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.facet.DirectAbilityFacet;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.cdom.helper.ClassSource;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Domain;
import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.ability.StackToken;
import plugin.lsttokens.auto.FeatToken;
import plugin.lsttokens.choose.NoChoiceToken;
import plugin.lsttokens.testsupport.TokenRegistration;
import tokenmodel.testsupport.AbstractGrantedListTokenTest;
import tokenmodel.testsupport.CASAssocCheck;
import tokenmodel.testsupport.NoAssociations;

public class AutoFeatTest extends AbstractGrantedListTokenTest<Ability>
{

	private static final FeatToken AUTO_FEAT_TOKEN = new FeatToken();
	private CASAssocCheck assocCheck;

	@Override
	protected void finishLoad()
	{
		super.finishLoad();
		assocCheck = new NoAssociations(pc);
	}

	
	@Override
	public void processToken(CDOMObject source)
	{
		ParseResult result = runToken(source);
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		finishLoad();
	}

	private ParseResult runToken(CDOMObject source)
	{
		ParseResult result = AUTO_FEAT_TOKEN.parseToken(context, source, "Granted");
		return result;
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
		return AUTO_LANG_TOKEN;
	}

	@Override
	protected int getCount()
	{
		return getTargetFacet().size(id);
	}

	@Override
	protected boolean containsExpected(Ability granted)
	{
		Collection<CNAbilitySelection> casSet =
				getTargetFacet().getSet(id);
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
			boolean selectionExpected = assocCheck.check(cnas);
			if (featExpected && abilityExpected && natureExpected
				&& selectionExpected)
			{
				return true;
			}
		}
		return false;
	}

	@Override
	protected Ability createGrantedObject()
	{
		Ability a = super.createGrantedObject();
		context.getReferenceContext().reassociateCategory(AbilityCategory.FEAT, a);
		return a;
	}

	@Test
	public void testMult() throws PersistenceLayerException
	{
		TokenRegistration.register(new NoChoiceToken());
		TokenRegistration.register(new StackToken());
		Domain source = create(Domain.class, "Source");
		PCClass pcc = create(PCClass.class, "Class");
		Ability a = createGrantedObject();
		context.unconditionallyProcess(a, "MULT", "YES");
		context.unconditionallyProcess(a, "STACK", "YES");
		context.unconditionallyProcess(a, "CHOOSE", "NOCHOICE");
		runToken(source);
		processToken(source);
		assocCheck = new CASAssocCheck()
		{
			public boolean check(CNAbilitySelection cas)
			{
				return "".equals(cas.getSelection());
			}
		};
		assertEquals(0, getCount());
		ClassSource classSource = new ClassSource(pcc);
		domainFacet.add(id, source, classSource);
		assertTrue(containsExpected(a));
		assertEquals(2, getCount());
		domainFacet.remove(id, source);
		assertEquals(0, getCount());
	}

}
