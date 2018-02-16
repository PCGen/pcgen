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
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.AddLst;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class AddIntegrationTest extends AbstractIntegrationTestCase<CDOMObject>
{
	static AddLst token = new AddLst();
	static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<>();

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
		commit(testCampaign, tc, Constants.LST_DOT_CLEAR);
		commit(modCampaign, tc, Constants.LST_DOT_CLEAR);
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoSet() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, Constants.LST_DOT_CLEAR);
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoReset() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, Constants.LST_DOT_CLEAR);
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinLevelSimple() throws PersistenceLayerException
	{
		primaryProf = new PCClassLevel();
		primaryProf.put(IntegerKey.LEVEL, 1);
		secondaryProf = new PCClassLevel();
		secondaryProf.put(IntegerKey.LEVEL, 1);
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, ".CLEAR.LEVEL1");
		commit(modCampaign, tc, ".CLEAR.LEVEL1");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinLevelNoSet() throws PersistenceLayerException
	{
		primaryProf = new PCClassLevel();
		primaryProf.put(IntegerKey.LEVEL, 1);
		secondaryProf = new PCClassLevel();
		secondaryProf.put(IntegerKey.LEVEL, 1);
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, ".CLEAR.LEVEL1");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinLevelNoReset() throws PersistenceLayerException
	{
		primaryProf = new PCClassLevel();
		primaryProf.put(IntegerKey.LEVEL, 1);
		secondaryProf = new PCClassLevel();
		secondaryProf.put(IntegerKey.LEVEL, 1);
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, ".CLEAR.LEVEL1");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}

	@Override
	protected Ability construct(LoadContext context, String name)
	{
		Ability a = AbilityCategory.FEAT.newInstance();
		a.setName(name);
		context.getReferenceContext().importObject(a);
		return a;
	}
}
