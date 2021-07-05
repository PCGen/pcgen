/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.editcontext.pcclass.level;

import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Domain;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractListIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.pcclass.level.AdddomainsToken;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class AddDomainsIntegrationTest extends
		AbstractListIntegrationTestCase<PCClassLevel, Domain>
{

	private static AdddomainsToken token = new AdddomainsToken();
	private static CDOMTokenLoader<PCClassLevel> loader = new CDOMTokenLoader<>();

	@Override
	@BeforeEach
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		prefix = "CLASS:";
	}

	@Override
	public Class<PCClassLevel> getCDOMClass()
	{
		return PCClassLevel.class;
	}

	@Override
	public CDOMLoader<PCClassLevel> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<PCClassLevel> getToken()
	{
		return token;
	}

	@Override
	public Class<Domain> getTargetClass()
	{
		return Domain.class;
	}

	@Override
	public boolean isTypeLegal()
	{
		return false;
	}

	@Override
	public char getJoinCharacter()
	{
		return '|';
	}

	@Test
	public void dummyTest()
	{
		// Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}

	@Override
	public boolean isClearDotLegal()
	{
		return false;
	}

	@Override
	public boolean isClearLegal()
	{
		return false;
	}

	@Override
	public boolean isPrereqLegal()
	{
		return false;
	}

	@Override
	public boolean isAllLegal()
	{
		return false;
	}

	@Test
	public void testRoundRobinAddBracketPrereq()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "TestWP1");
		commit(modCampaign, tc, "TestWP1[PRERACE:1,Human]");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinRemoveBracketPrereq()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "TestWP1[PRERACE:1,Human]");
		commit(modCampaign, tc, "TestWP1");
		completeRoundRobin(tc);
	}
}
