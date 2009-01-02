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
package plugin.lsttokens.editcontext.testsupport;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.BeforeClass;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Campaign;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.LstToken;
import pcgen.persistence.lst.TokenStore;
import pcgen.rules.context.EditorLoadContext;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.TokenRegistration;

public abstract class AbstractIntegrationTestCase<T extends CDOMObject> extends
		TestCase
{
	protected LoadContext primaryContext;
	protected LoadContext secondaryContext;
	protected T primaryProf;
	protected T secondaryProf;
	protected String prefix = "";
	protected int expectedPrimaryMessageCount = 0;

	private static boolean classSetUpFired = false;
	protected static CampaignSourceEntry testCampaign;
	protected static CampaignSourceEntry modCampaign;

	public abstract CDOMLoader<T> getLoader();

	public abstract CDOMPrimaryToken<T> getToken();

	@BeforeClass
	public static final void classSetUp() throws URISyntaxException
	{
		testCampaign = new CampaignSourceEntry(new Campaign(), new URI(
				"file:/Test%20Case"));
		modCampaign = new CampaignSourceEntry(new Campaign(), new URI(
				"file:/Test%20Case%20Modifier"));
		classSetUpFired = true;
	}

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		if (!classSetUpFired)
		{
			classSetUp();
		}
		// Yea, this causes warnings...
		TokenRegistration.register(getToken());
		primaryContext = new EditorLoadContext();
		secondaryContext = new EditorLoadContext();
		primaryProf = primaryContext.ref.constructCDOMObject(getCDOMClass(),
				"TestObj");
		secondaryProf = secondaryContext.ref.constructCDOMObject(
				getCDOMClass(), "TestObj");
	}

	public abstract Class<? extends T> getCDOMClass();

	public static void addToken(LstToken tok)
	{
		TokenStore.inst().addToTokenMap(tok);
	}

	public static void addBonus(String name, Class<? extends BonusObj> clazz)
	{
		try
		{
			Bonus.addBonusClass(clazz, name);
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	protected void verifyCleanStart()
	{
		// Default is not to write out anything
		assertNull(getToken().unparse(primaryContext, primaryProf));
		assertNull(getToken().unparse(secondaryContext, secondaryProf));
		// Ensure the graphs are the same at the start
		assertEquals(primaryProf, secondaryProf);
		// Ensure the graphs are the same at the start
		assertTrue(primaryContext.getListContext().masterListsEqual(
				secondaryContext.getListContext()));
	}

	protected void commit(CampaignSourceEntry campaign, TestContext tc,
			String... str) throws PersistenceLayerException
	{
		StringBuilder unparsedBuilt = new StringBuilder();
		for (String s : str)
		{
			unparsedBuilt.append(getToken().getTokenName()).append(':').append(
					s).append('\t');
		}
		URI uri = campaign.getURI();
		primaryContext.setSourceURI(uri);
		assertTrue(getLoader().parseLine(primaryContext, primaryProf,
				unparsedBuilt.toString(), campaign.getURI()));
		tc.putText(uri, str);
		tc.putCampaign(uri, campaign);
	}

	protected void emptyCommit(CampaignSourceEntry campaign, TestContext tc)
			throws PersistenceLayerException
	{
		URI uri = campaign.getURI();
		primaryContext.setSourceURI(uri);
		getLoader().parseLine(primaryContext, primaryProf, null,
				campaign.getURI());
		tc.putText(uri, null);
		tc.putCampaign(uri, campaign);
	}

	public void completeRoundRobin(TestContext tc)
			throws PersistenceLayerException
	{
		for (URI uri : tc.getURIs())
		{
			List<String> str = tc.getText(uri);
			primaryContext.setExtractURI(uri);
			// Get back the appropriate token:
			String[] unparsed = getToken().unparse(primaryContext, primaryProf);
			if (str == null)
			{
				assertNull("Expecting empty unparsed", unparsed);
				getLoader().parseLine(secondaryContext, secondaryProf, null,
						uri);
				continue;
			}
			assertNotNull(unparsed);
			assertEquals(str.size(), unparsed.length);

			for (int i = 0; i < str.size(); i++)
			{
				assertEquals("Expected " + i + " item to be equal", str.get(i),
						unparsed[i]);
			}

			// Do round Robin
			StringBuilder unparsedBuilt = new StringBuilder();
			for (String s : unparsed)
			{
				unparsedBuilt.append(getToken().getTokenName()).append(':')
						.append(s).append('\t');
			}
			secondaryContext.setSourceURI(uri);
			getLoader().parseLine(secondaryContext, secondaryProf,
					unparsedBuilt.toString(), uri);
		}

		// Ensure the objects are the same
		assertEquals(primaryProf, secondaryProf);

		// Ensure the graphs are the same
		assertTrue(primaryContext.getListContext().masterListsEqual(
				secondaryContext.getListContext()));

		// And that it comes back out the same again
		for (URI uri : tc.getURIs())
		{
			List<String> str = tc.getText(uri);
			secondaryContext.setExtractURI(uri);
			// Get back the appropriate token:
			String[] unparsed = getToken().unparse(secondaryContext,
					secondaryProf);
			if (str == null)
			{
				assertNull(unparsed);
				continue;
			}
			assertEquals(str.size(), unparsed.length);

			for (int i = 0; i < str.size(); i++)
			{
				assertEquals("Expected " + i + " item to be equal", str.get(i),
						unparsed[i]);
			}
		}

		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
		assertEquals(expectedPrimaryMessageCount, primaryContext
				.getWriteMessageCount());
		assertEquals(0, secondaryContext.getWriteMessageCount());
	}

}
