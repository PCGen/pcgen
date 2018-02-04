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
package tokencontent;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.analysis.LocalSkillCostFacet;
import pcgen.cdom.facet.input.DomainInputFacet;
import pcgen.cdom.facet.input.LocalAddedSkillCostFacet;
import pcgen.cdom.helper.ClassSource;
import pcgen.core.Domain;
import pcgen.core.PCClass;
import pcgen.core.Skill;
import pcgen.gui2.facade.MockUIDelegate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.chooser.ChooserFactory;
import plugin.lsttokens.choose.SkillToken;
import plugin.lsttokens.domain.CcskillToken;
import plugin.lsttokens.skill.ExclusiveToken;

import org.junit.Test;
import tokenmodel.testsupport.AbstractTokenModelTest;

public class DomainCcSkillTest extends AbstractTokenModelTest
{

	private static CcskillToken token = new CcskillToken();
	private Skill sk;
	private PCClass dragon;
	private LocalSkillCostFacet lscFacet;
	private LocalAddedSkillCostFacet lascFacet;
	private static SkillToken CHOOSE_SKILL_TOKEN = new SkillToken();
	protected DomainInputFacet domainInputFacet = FacetLibrary
		.getFacet(DomainInputFacet.class);

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		lscFacet = FacetLibrary.getFacet(LocalSkillCostFacet.class);
		lascFacet = FacetLibrary.getFacet(LocalAddedSkillCostFacet.class);
		sk = context.getReferenceContext().constructCDOMObject(Skill.class, "MySkill");
		dragon = context.getReferenceContext().constructCDOMObject(PCClass.class, "Dragon");
		dragon.addToListFor(ListKey.TYPE, Type.MONSTER);
		ChooserFactory.setDelegate(new MockUIDelegate());
	}

	@Test
	public void testDirect() throws PersistenceLayerException
	{
		Domain source = create(Domain.class, "Source");
		ParseResult result = token.parseToken(context, source, "MySkill");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		new ExclusiveToken().parseToken(context, sk, "Yes");
		finishLoad();
		assertEquals(SkillCost.EXCLUSIVE, pc.getSkillCostForClass(sk, dragon));
		domainInputFacet.add(id, source, new ClassSource(dragon, 0));
		assertTrue(lscFacet.contains(id, dragon, SkillCost.CROSS_CLASS, sk));
		pc.addClass(dragon);
		pc.setDirty(true);
		assertEquals(SkillCost.CROSS_CLASS, pc.getSkillCostForClass(sk, dragon));
		domainInputFacet.remove(id, source);
		assertFalse(lscFacet.contains(id, dragon, SkillCost.CROSS_CLASS, sk));
	}

	@Test
	public void testList() throws PersistenceLayerException
	{
		Domain source = create(Domain.class, "Source");
		ParseResult result = token.parseToken(context, source, "LIST");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		result = CHOOSE_SKILL_TOKEN.parseToken(context, source, "MySkill");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		new ExclusiveToken().parseToken(context, sk, "Yes");
		finishLoad();
		assertFalse(lascFacet.contains(id, dragon, SkillCost.CROSS_CLASS, sk));
		assertEquals(SkillCost.EXCLUSIVE, pc.getSkillCostForClass(sk, dragon));
		domainInputFacet.add(id, source, new ClassSource(dragon, 0));
		assertTrue(lascFacet.contains(id, dragon, SkillCost.CROSS_CLASS, sk));
		pc.addClass(dragon);
		pc.setDirty(true);
		assertEquals(SkillCost.CROSS_CLASS, pc.getSkillCostForClass(sk, dragon));
		domainInputFacet.remove(id, source);
		assertFalse(lascFacet.contains(id, dragon, SkillCost.CROSS_CLASS, sk));
	}

	@Override
	public CDOMToken<?> getToken()
	{
		return token;
	}
}
