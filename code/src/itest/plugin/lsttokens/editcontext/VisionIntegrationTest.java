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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.core.Ability;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.VisionLst;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

class VisionIntegrationTest extends
		AbstractIntegrationTestCase<CDOMObject>
{
	private static VisionLst token = new VisionLst();
	private static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<>();

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
	void testRoundRobinSimple() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Normal (30')");
		commit(modCampaign, tc, "Darkvision (20')");
		completeRoundRobin(tc);
	}

	@Test
	void testRoundRobinRemove() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Darkvision (20')");
		commit(modCampaign, tc, "Darkvision (40')");
		completeRoundRobin(tc);
	}

	@Test
	void testRoundRobinNoSet() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "Darkvision (20')");
		completeRoundRobin(tc);
	}

	@Test
	void testRoundRobinNoReset() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Darkvision (20')");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}

	@Test
	void testRoundRobinclearMod() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Normal (30')");
		commit(modCampaign, tc, Constants.LST_DOT_CLEAR);
		completeRoundRobin(tc);
	}

	@Test
	void testRoundRobinClearBase() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, Constants.LST_DOT_CLEAR);
		commit(modCampaign, tc, "Normal (30')");
		completeRoundRobin(tc);
	}

	@Test
	void testRoundRobinClearBoth() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, Constants.LST_DOT_CLEAR);
		commit(modCampaign, tc, Constants.LST_DOT_CLEAR);
		completeRoundRobin(tc);
	}

	@Test
	void testRoundRobinClearNoSet() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, Constants.LST_DOT_CLEAR);
		completeRoundRobin(tc);
	}

	@Test
	void testRoundRobinClearNoReset() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, Constants.LST_DOT_CLEAR);
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}


	@Test
	void testRoundRobinClearDotMod() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Normal (30')");
		commit(modCampaign, tc, ".CLEAR.Normal");
		completeRoundRobin(tc);
	}

	@Test
	void testRoundRobinClearDotBase() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, ".CLEAR.Normal");
		commit(modCampaign, tc, "Normal (30')");
		completeRoundRobin(tc);
	}

	@Test
	void testRoundRobinClearDotBoth() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, ".CLEAR.Normal");
		commit(modCampaign, tc, ".CLEAR.Normal");
		completeRoundRobin(tc);
	}

	@Test
	void testRoundRobinClearDotNoSet() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, ".CLEAR.Normal");
		completeRoundRobin(tc);
	}

	@Test
	void testRoundRobinClearDotNoReset() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, ".CLEAR.Normal");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}

	@Override
	protected Ability construct(LoadContext context, String name)
	{
		Ability a = BuildUtilities.getFeatCat().newInstance();
		a.setName(name);
		context.getReferenceContext().importObject(a);
		return a;
	}
}
