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
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.AbilityLst;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class AbilityIntegrationTest extends
		AbstractIntegrationTestCase<CDOMObject>
{
	static AbilityLst token = new AbilityLst();
	static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<CDOMObject>(
			CDOMObject.class);

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
		Ability ab = primaryContext.ref.constructCDOMObject(Ability.class,
				"Abil1");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class, "Abil1");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = primaryContext.ref.constructCDOMObject(Ability.class, "Abil2");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class, "Abil2");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Feat|NORMAL|Abil1");
		commit(modCampaign, tc, "Feat|VIRTUAL|TYPE=TestType");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinRemove() throws PersistenceLayerException
	{
		verifyCleanStart();
		Ability ab = primaryContext.ref.constructCDOMObject(Ability.class,
				"Abil1");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class, "Abil1");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = primaryContext.ref.constructCDOMObject(Ability.class, "Abil2");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class, "Abil2");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Feat|VIRTUAL|Abil1|Abil2");
		commit(modCampaign, tc, "Feat|VIRTUAL|.CLEAR.Abil2");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinMixed() throws PersistenceLayerException
	{
		verifyCleanStart();
		Ability ab = primaryContext.ref.constructCDOMObject(Ability.class,
				"Abil1");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class, "Abil1");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = primaryContext.ref.constructCDOMObject(Ability.class, "Abil2");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class, "Abil2");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Feat|VIRTUAL|.CLEAR.Abil2|Abil1");
		commit(modCampaign, tc, "Feat|AUTOMATIC|.CLEAR.Abil1|Abil2");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoSet() throws PersistenceLayerException
	{
		verifyCleanStart();
		Ability ab = primaryContext.ref.constructCDOMObject(Ability.class,
				"Abil1");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class, "Abil1");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = primaryContext.ref.constructCDOMObject(Ability.class, "Abil2");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class, "Abil2");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "Feat|VIRTUAL|Abil1|Abil2");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoReset() throws PersistenceLayerException
	{
		verifyCleanStart();
		Ability ab = primaryContext.ref.constructCDOMObject(Ability.class,
				"Abil1");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class, "Abil1");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = primaryContext.ref.constructCDOMObject(Ability.class, "Abil2");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class, "Abil2");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Feat|VIRTUAL|Abil1|Abil2");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoSetDotClear() throws PersistenceLayerException
	{
		verifyCleanStart();
		Ability ab = primaryContext.ref.constructCDOMObject(Ability.class,
				"Abil2");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class, "Abil2");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "Feat|VIRTUAL|.CLEAR.Abil2");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoResetDotClear()
			throws PersistenceLayerException
	{
		verifyCleanStart();
		Ability ab = primaryContext.ref.constructCDOMObject(Ability.class,
				"Abil2");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class, "Abil2");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Feat|VIRTUAL|.CLEAR.Abil2");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}

	public void testRoundRobinMixedClearDot() throws PersistenceLayerException
	{
		verifyCleanStart();
		Ability ab = primaryContext.ref.constructCDOMObject(Ability.class,
				"Abil2");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class, "Abil2");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Feat|VIRTUAL|.CLEAR");
		commit(modCampaign, tc, "Feat|VIRTUAL|.CLEAR.Abil2");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinMixedDotClear() throws PersistenceLayerException
	{
		verifyCleanStart();
		Ability ab = primaryContext.ref.constructCDOMObject(Ability.class,
				"Abil2");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class, "Abil2");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Feat|VIRTUAL|.CLEAR.Abil2");
		commit(modCampaign, tc, "Feat|VIRTUAL|.CLEAR");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoSetClear() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "Feat|VIRTUAL|.CLEAR");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoResetClear() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Feat|VIRTUAL|.CLEAR");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinClearOrder() throws PersistenceLayerException
	{
		verifyCleanStart();
		Ability ab = primaryContext.ref.constructCDOMObject(Ability.class,
				"Abil1");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class, "Abil1");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = primaryContext.ref.constructCDOMObject(Ability.class, "Abil2");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class, "Abil2");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Feat|VIRTUAL|.CLEAR",
				"Feat|VIRTUAL|Abil1|Abil2");
		commit(modCampaign, tc, "Feat|VIRTUAL|.CLEAR");
		completeRoundRobin(tc);
	}
}
