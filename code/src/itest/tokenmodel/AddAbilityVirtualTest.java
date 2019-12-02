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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collection;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.GrantedAbilityFacet;
import pcgen.cdom.helper.ClassSource;
import pcgen.core.Ability;
import pcgen.core.Domain;
import pcgen.core.PCClass;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.ability.StackToken;
import plugin.lsttokens.add.AbilityToken;
import plugin.lsttokens.choose.NoChoiceToken;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.Test;
import tokenmodel.testsupport.AbstractAddListTokenTest;
import tokenmodel.testsupport.AssocCheck;
import tokenmodel.testsupport.NoAssociations;
import util.TestURI;

public class AddAbilityVirtualTest extends AbstractAddListTokenTest<Ability>
{

	private static final AbilityToken ADD_ABILITY_TOKEN = new AbilityToken();
	private GrantedAbilityFacet grantedAbilityFacet = FacetLibrary
		.getFacet(GrantedAbilityFacet.class);
	private AssocCheck assocCheck;

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
			result.printMessages(TestURI.getURI());
			fail("Test Setup Failed");
		}
		finishLoad();
	}

	private ParseResult runToken(CDOMObject source)
	{
		ParseResult result =
				ADD_ABILITY_TOKEN.parseToken(context, source, "FEAT|VIRTUAL|STACKS,Granted");
		return result;
	}

	@Override
	protected Class<Ability> getGrantClass()
	{
		return Ability.class;
	}

	@Override
	protected GrantedAbilityFacet getTargetFacet()
	{
		return grantedAbilityFacet;
	}

	@Override
	public CDOMToken<?> getToken()
	{
		return AUTO_LANG_TOKEN;
	}

	@Override
	protected int getCount()
	{
		return getTargetFacet().getPoolAbilities(id, BuildUtilities.getFeatCat(), Nature.VIRTUAL)
			.size();
	}

	@Override
	protected boolean containsExpected(Ability granted)
	{
		Collection<CNAbility> abilities =
				getTargetFacet().getPoolAbilities(id, BuildUtilities.getFeatCat(), Nature.VIRTUAL);
		for (CNAbility a : abilities)
		{
			boolean abilityExpected = a.getAbility().equals(context.getReferenceContext()
				.getManufacturerId(BuildUtilities.getFeatCat()).getActiveObject("Granted"));
			if (abilityExpected)
			{
				boolean c = assocCheck.check(a);
				if (!c)
				{
					System.err.println("Incorrect Associations");
				}
				return c;
			}
		}
		System.err.println("Did not find Ability");
		return false;
	}

	@Override
	protected Ability createGrantedObject()
	{
		Ability a = BuildUtilities.getFeatCat().newInstance();
		a.setName("Granted");
		context.getReferenceContext().importObject(a);
		return a;
	}

	//TODO CODE-2016/CODE-1921 (needs to be consistent with other methods of ADD:)
	@Override
	public void testFromAbility()
	{
		//Not supported equivalent to other methods
	}

	//TODO CODE-2016 (needs to be consistent with other methods of ADD:)
	@Override
	public void testFromClass()
	{
		//Not supported equivalent to other methods
	}

	@Test
	public void testMult()
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
		assocCheck = g -> {
            if (pc.getDetailedAssociationCount(g) == 2)
            {
                return true;
            }
            else
            {
                System.err.println("Incorrect Association Count");
                return false;
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
