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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.input.ActiveAbilityFacet;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.add.AbilityToken;
import tokenmodel.testsupport.AbstractAddListTokenTest;
import tokenmodel.testsupport.AssocCheck;
import tokenmodel.testsupport.NoAssociations;

public class AddAbilityNormalTest extends AbstractAddListTokenTest<Ability>
{

	private static final AbilityToken ADD_ABILITY_TOKEN = new AbilityToken();
	private ActiveAbilityFacet activeAbilityFacet = FacetLibrary
		.getFacet(ActiveAbilityFacet.class);
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
			result.printMessages();
			fail("Test Setup Failed");
		}
		finishLoad();
	}

	private ParseResult runToken(CDOMObject source)
	{
		ParseResult result =
				ADD_ABILITY_TOKEN.parseToken(context, source, "FEAT|NORMAL|Granted");
		return result;
	}

	@Override
	protected Class<Ability> getGrantClass()
	{
		return Ability.class;
	}

	@Override
	protected ActiveAbilityFacet getTargetFacet()
	{
		return activeAbilityFacet;
	}

	@Override
	public CDOMToken<?> getToken()
	{
		return AUTO_LANG_TOKEN;
	}

	@Override
	protected int getCount()
	{
		return getTargetFacet().getPoolAbilities(id, AbilityCategory.FEAT, Nature.NORMAL)
			.size();
	}

	@Override
	protected boolean containsExpected(Ability granted)
	{
		Collection<CNAbility> abilities =
				getTargetFacet().getPoolAbilities(id, AbilityCategory.FEAT, Nature.NORMAL);
		if (abilities.isEmpty())
		{
			System.err.println("No Abilities");
			return false;
		}
		for (CNAbility a : abilities)
		{
			boolean abilityExpected =
					a.getAbility().equals(context.ref.silentlyGetConstructedCDOMObject(
						Ability.class, AbilityCategory.FEAT, "Granted"));
			if (abilityExpected)
			{
				Ability g = pc.getAbilityKeyed(AbilityCategory.FEAT, "Granted");
				boolean c = assocCheck.check(g);
				if (!c)
				{
					System.err.println("Incorrect Associations");
				}
				return c;
			}
		}
		System.err.println("Did not find Ability: Granted");
		return false;
	}

	@Override
	protected Ability createGrantedObject()
	{
		Ability a = super.createGrantedObject();
		context.ref.reassociateCategory(AbilityCategory.FEAT, a);
		return a;
	}

	//TODO CODE-2016/CODE-1921 (needs to be consistent with other methods of ADD:)
	@Override
	public void testFromAbility() throws PersistenceLayerException
	{
		//Not supported equivalent to other methods
	}

	//TODO CODE-2016 (needs to be consistent with other methods of ADD:)
	@Override
	public void testFromClass() throws PersistenceLayerException
	{
		//Not supported equivalent to other methods
	}

	//TODO this appears to be a bug - is only applied once?
//	@Test
//	public void testMult() throws PersistenceLayerException
//	{
//		TokenRegistration.register(new NoChoiceToken());
//		TokenRegistration.register(new StackToken());
//		Domain source = create(Domain.class, "Source");
//		PCClass pcc = create(PCClass.class, "Class");
//		Ability a = createGrantedObject();
//		context.unconditionallyProcess(a, "MULT", "YES");
//		context.unconditionallyProcess(a, "STACK", "YES");
//		context.unconditionallyProcess(a, "CHOOSE", "NOCHOICE");
//		runToken(source);
//		processToken(source);
//		assocCheck = new AssocCheck()
//		{
//			
//			public boolean check(Ability g)
//			{
//				if (pc.getDetailedAssociationCount(g) == 1)
//				{
//					return true;
//				}
//				else
//				{
//					System.err.println("Incorrect Association Count");
//					return false;
//				}
//			}
//			
//		};
//		assertEquals(0, getCount());
//		ClassSource classSource = new ClassSource(pcc);
//		domainFacet.add(id, source, classSource);
//		assertTrue(containsExpected(a));
//		assertEquals(2, getCount());
//		domainFacet.remove(id, source);
//		assertEquals(0, getCount());
//	}
}
