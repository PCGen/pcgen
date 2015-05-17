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

import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.facet.DirectAbilityFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.input.DomainInputFacet;
import pcgen.cdom.facet.input.GlobalAddedSkillCostFacet;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.cdom.helper.ClassSource;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Domain;
import pcgen.core.PCClass;
import pcgen.core.Skill;
import pcgen.gui2.facade.MockUIDelegate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.chooser.ChooserFactory;
import plugin.lsttokens.CskillLst;
import plugin.lsttokens.ability.StackToken;
import plugin.lsttokens.choose.NoChoiceToken;
import plugin.lsttokens.choose.SkillToken;
import plugin.lsttokens.domain.FeatToken;
import plugin.lsttokens.testsupport.TokenRegistration;
import tokenmodel.testsupport.AbstractTokenModelTest;

public class DomainFeatTest extends AbstractTokenModelTest
{

	private static FeatToken token = new FeatToken();
	private static CskillLst CSKILL_TOKEN = new CskillLst();
	private static SkillToken CHOOSE_SKILL_TOKEN = new SkillToken();
	private GlobalAddedSkillCostFacet globalAddedSkillCostFacet = FacetLibrary
		.getFacet(GlobalAddedSkillCostFacet.class);
	protected DomainInputFacet domainInputFacet = FacetLibrary
			.getFacet(DomainInputFacet.class);

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		ChooserFactory.setDelegate(new MockUIDelegate());
	}

	@Test
	public void testSimple() throws PersistenceLayerException
	{
		Domain source = create(Domain.class, "Source");
		PCClass pcc = create(PCClass.class, "Class");
		createGrantedObject();
		ParseResult result = token.parseToken(context, source, "Granted");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		finishLoad();
		assertEquals(0, directAbilityFacet.size(id));
		ClassSource classSource = new ClassSource(pcc);
		domainFacet.add(id, source, classSource);
		assertTrue(containsExpected(null));
		assertEquals(1, directAbilityFacet.size(id));
		domainFacet.remove(id, source);
		assertEquals(0, directAbilityFacet.size(id));
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
		ParseResult result = token.parseToken(context, source, "Granted");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		//Do a second time!
		token.parseToken(context, source, "Granted");
		finishLoad();
		assertEquals(0, directAbilityFacet.size(id));
		ClassSource classSource = new ClassSource(pcc);
		domainFacet.add(id, source, classSource);
		assertTrue(containsExpected(""));
		assertEquals(2, directAbilityFacet.size(id));
		domainFacet.remove(id, source);
		assertEquals(0, directAbilityFacet.size(id));
	}

	@Test
	public void testTargetList() throws PersistenceLayerException
	{
		/*
		 * If this test breaks, please ensure that Global CSKILL:LIST is
		 * working. This test DEPENDS on that behavior!
		 */
		Domain source = create(Domain.class, "Source");
		PCClass pcc = create(PCClass.class, "Class");
		Ability passthru = create(Ability.class, "Passthru");
		context.getReferenceContext().reassociateCategory(AbilityCategory.FEAT, passthru);
		Skill granted = create(Skill.class, "GrantedSkill");
		create(Skill.class, "IgnoredSkill");
		ParseResult result = token.parseToken(context, source, "Passthru(%LIST)");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		result = CHOOSE_SKILL_TOKEN.parseToken(context, source, "GrantedSkill");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		result = CHOOSE_SKILL_TOKEN.parseToken(context, passthru, "GrantedSkill|IgnoredSkill");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		passthru.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
		result = CSKILL_TOKEN.parseToken(context, passthru, "LIST");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		finishLoad();
		assertFalse(globalAddedSkillCostFacet.contains(id, SkillCost.CLASS, granted));
		ClassSource classSource = new ClassSource(pcc);
		domainInputFacet.add(id, source, classSource);
		assertTrue(globalAddedSkillCostFacet.contains(id, SkillCost.CLASS, granted));
		domainInputFacet.remove(id, source);
		assertFalse(globalAddedSkillCostFacet.contains(id, SkillCost.CLASS, granted));
	}

	protected boolean containsExpected(String selection)
	{
		Collection<CNAbilitySelection> casSet =
				getTargetFacet().getSet(id);
		for (CNAbilitySelection cnas : casSet)
		{
			CNAbility cas = cnas.getCNAbility();
			boolean categoryExpected =
					cas.getAbilityCategory() == AbilityCategory.FEAT;
			if (!categoryExpected)
			{
				System.err.println("Category Mismatch");
				return false;
			}
			boolean abilityExpected =
					cas.getAbility().equals(
						context.getReferenceContext().silentlyGetConstructedCDOMObject(
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
				if (cnas.getSelection() != null)
				{
					System.err.println("Selection Mismatch");
					return false;
				}
			}
			else
			{
				boolean selectionExpected = cnas.getSelection().equals(selection);
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

	private DirectAbilityFacet getTargetFacet()
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
		Ability a = create(Ability.class, "Granted");
		context.getReferenceContext().reassociateCategory(AbilityCategory.FEAT, a);
		return a;
	}
}
