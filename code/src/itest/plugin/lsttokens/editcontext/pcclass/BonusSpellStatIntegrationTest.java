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
package plugin.lsttokens.editcontext.pcclass;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.pcclass.BonusspellstatToken;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class BonusSpellStatIntegrationTest extends
		AbstractIntegrationTestCase<PCClass>
{

	static BonusspellstatToken token = new BonusspellstatToken();
	static CDOMTokenLoader<PCClass> loader = new CDOMTokenLoader<PCClass>();

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		prefix = "CLASS:";
		PCStat ps = primaryContext.getReferenceContext().constructCDOMObject(PCStat.class,
				"Strength");
		primaryContext.getReferenceContext().registerAbbreviation(ps, "STR");
		PCStat ss = secondaryContext.getReferenceContext().constructCDOMObject(PCStat.class,
				"Strength");
		secondaryContext.getReferenceContext().registerAbbreviation(ss, "STR");
		PCStat pi = primaryContext.getReferenceContext().constructCDOMObject(PCStat.class,
				"Intelligence");
		primaryContext.getReferenceContext().registerAbbreviation(pi, "INT");
		PCStat si = secondaryContext.getReferenceContext().constructCDOMObject(PCStat.class,
				"Intelligence");
		secondaryContext.getReferenceContext().registerAbbreviation(si, "INT");
	}

	@Override
	public Class<PCClass> getCDOMClass()
	{
		return PCClass.class;
	}

	@Override
	public CDOMLoader<PCClass> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<PCClass> getToken()
	{
		return token;
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "STR");
		commit(modCampaign, tc, "INT");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoSet() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "INT");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoReset() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "STR");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoneOne() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "STR");
		commit(modCampaign, tc, "NONE");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoneNoSet() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "NONE");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoneNoReset() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "NONE");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}

}
