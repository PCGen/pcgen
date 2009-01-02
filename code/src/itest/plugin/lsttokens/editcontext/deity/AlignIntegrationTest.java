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
package plugin.lsttokens.editcontext.deity;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Deity;
import pcgen.core.PCAlignment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.deity.AlignToken;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class AlignIntegrationTest extends AbstractIntegrationTestCase<Deity>
{
	static AlignToken token = new AlignToken();
	static CDOMTokenLoader<Deity> loader = new CDOMTokenLoader<Deity>(
			Deity.class);

	@Override
	@Before
	public final void setUp() throws PersistenceLayerException,
			URISyntaxException
	{
		super.setUp();
		PCAlignment lg = primaryContext.ref.constructCDOMObject(
				PCAlignment.class, "Lawful Good");
		primaryContext.ref.registerAbbreviation(lg, "LG");
		PCAlignment ln = primaryContext.ref.constructCDOMObject(
				PCAlignment.class, "Lawful Neutral");
		primaryContext.ref.registerAbbreviation(ln, "LN");
		PCAlignment slg = secondaryContext.ref.constructCDOMObject(
				PCAlignment.class, "Lawful Good");
		secondaryContext.ref.registerAbbreviation(slg, "LG");
		PCAlignment sln = secondaryContext.ref.constructCDOMObject(
				PCAlignment.class, "Lawful Neutral");
		secondaryContext.ref.registerAbbreviation(sln, "LN");
	}

	@Override
	public Class<Deity> getCDOMClass()
	{
		return Deity.class;
	}

	@Override
	public CDOMLoader<Deity> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<Deity> getToken()
	{
		return token;
	}

	public Object getConstant(String string)
	{
		return primaryContext.ref.getAbbreviatedObject(PCAlignment.class,
				string);
	}

	public ObjectKey<?> getObjectKey()
	{
		return ObjectKey.ALIGNMENT;
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "LN");
		commit(modCampaign, tc, "LG");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoSet() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "LG");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoReset() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "LN");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}
}
