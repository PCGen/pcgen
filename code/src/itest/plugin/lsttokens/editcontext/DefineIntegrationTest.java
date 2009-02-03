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

import java.net.URISyntaxException;

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Ability;
import pcgen.core.PCStat;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.DefineLst;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class DefineIntegrationTest extends
		AbstractIntegrationTestCase<CDOMObject>
{
	static DefineLst token = new DefineLst();
	static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<CDOMObject>(
			CDOMObject.class);

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		PCStat ps = primaryContext.ref.constructCDOMObject(PCStat.class, "Strength");
		primaryContext.ref.registerAbbreviation(ps, "STR");
		PCStat ss = secondaryContext.ref.constructCDOMObject(PCStat.class, "Strength");
		secondaryContext.ref.registerAbbreviation(ss, "STR");
		PCStat pi = primaryContext.ref.constructCDOMObject(PCStat.class, "Intelligence");
		primaryContext.ref.registerAbbreviation(pi, "INT");
		PCStat si = secondaryContext.ref.constructCDOMObject(PCStat.class, "Intelligence");
		secondaryContext.ref.registerAbbreviation(si, "INT");
	}

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
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Follower|4+1");
		commit(modCampaign, tc, "Follower|Formula1");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinRemove() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Follower|4+1");
		commit(modCampaign, tc, "Pet|PetForm");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoSet() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "Follower|4+1");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoReset() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Follower|4+1");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinSimpleLock() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "LOCK.INT|15");
		commit(modCampaign, tc, "LOCK.STR|15");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinRemoveLock() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "LOCK.STR|4");
		commit(modCampaign, tc, "LOCK.STR|15");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoSetLock() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "LOCK.INT|21");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoResetLock() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "LOCK.INT|6");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinSimpleUnLock() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "UNLOCK.INT");
		commit(modCampaign, tc, "UNLOCK.STR");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinRemoveUnLock() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "UNLOCK.STR");
		commit(modCampaign, tc, "UNLOCK.STR");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoSetUnLock() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "UNLOCK.INT");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoResetUnLock() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "UNLOCK.INT");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinSimpleLockUnlock() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "LOCK.INT|15");
		commit(modCampaign, tc, "UNLOCK.STR");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinAddRemoveLock() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "LOCK.STR|4");
		commit(modCampaign, tc, "UNLOCK.STR");
		completeRoundRobin(tc);
	}
}
