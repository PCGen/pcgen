/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.editcontext;

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.QualifyToken;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class QualifyIntegrationTest extends
		AbstractIntegrationTestCase<CDOMObject>
{
	static QualifyToken token = new QualifyToken();
	static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<CDOMObject>();

	@Override
	public Class<Ability> getCDOMClass()
	{
		return Ability.class;
	}

	@Override
	public CDOMLoader<CDOMObject> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
	{
		return token;
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		verifyCleanStart();
		Ability a = primaryContext.getReferenceContext().constructCDOMObject(Ability.class,
				"My Feat");
		primaryContext.getReferenceContext().reassociateCategory(AbilityCategory.FEAT, a);
		a = secondaryContext.getReferenceContext().constructCDOMObject(Ability.class, "My Feat");
		secondaryContext.getReferenceContext().reassociateCategory(AbilityCategory.FEAT, a);
		primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Lightning Bolt");
		secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Lightning Bolt");
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "FEAT|My Feat");
		commit(modCampaign, tc, "SPELL|Lightning Bolt");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinRemove() throws PersistenceLayerException
	{
		verifyCleanStart();
		primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
		primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Lightning Bolt");
		secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Lightning Bolt");
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "SPELL|Lightning Bolt");
		commit(modCampaign, tc, "SPELL|Fireball");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoSet() throws PersistenceLayerException
	{
		verifyCleanStart();
		AbilityCategory pac = primaryContext.getReferenceContext().constructCDOMObject(
				AbilityCategory.class, "NEWCAT");
		AbilityCategory sac = secondaryContext.getReferenceContext().constructCDOMObject(
				AbilityCategory.class, "NEWCAT");
		Ability ab = primaryContext.getReferenceContext().constructCDOMObject(Ability.class,
				"Abil3");
		primaryContext.getReferenceContext().reassociateCategory(pac, ab);
		ab = secondaryContext.getReferenceContext().constructCDOMObject(Ability.class, "Abil3");
		secondaryContext.getReferenceContext().reassociateCategory(sac, ab);
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "ABILITY=NEWCAT|Abil3");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoReset() throws PersistenceLayerException
	{
		verifyCleanStart();
		AbilityCategory pac = primaryContext.getReferenceContext().constructCDOMObject(
				AbilityCategory.class, "NEWCAT");
		AbilityCategory sac = secondaryContext.getReferenceContext().constructCDOMObject(
				AbilityCategory.class, "NEWCAT");
		Ability ab = primaryContext.getReferenceContext().constructCDOMObject(Ability.class,
				"Abil3");
		primaryContext.getReferenceContext().reassociateCategory(pac, ab);
		ab = secondaryContext.getReferenceContext().constructCDOMObject(Ability.class, "Abil3");
		secondaryContext.getReferenceContext().reassociateCategory(sac, ab);
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "ABILITY=NEWCAT|Abil3");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}

}
