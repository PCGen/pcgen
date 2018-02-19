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

import org.junit.Before;
import org.junit.BeforeClass;

import junit.framework.TestCase;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.Loadable;
import pcgen.core.AbilityCategory;
import pcgen.core.Campaign;
import pcgen.core.bonus.BonusObj;
import pcgen.output.publish.OutputDB;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.LstToken;
import pcgen.persistence.lst.TokenStore;
import pcgen.rules.context.EditorLoadContext;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.TokenRegistration;
import util.TestURI;

public abstract class AbstractIntegrationTestCase<T extends ConcretePrereqObject & Loadable> extends
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

	public abstract CDOMPrimaryToken<? super T> getToken();

	@BeforeClass
	public static final void classSetUp() throws URISyntaxException
	{
		OutputDB.reset();
		testCampaign = new CampaignSourceEntry(new Campaign(), TestURI.getURI());
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
		primaryContext.getReferenceContext().importObject(AbilityCategory.FEAT);
		secondaryContext.getReferenceContext().importObject(AbilityCategory.FEAT);
		primaryProf = construct(primaryContext, "TestObj");
		secondaryProf = construct(secondaryContext, "TestObj");
	}

	protected T construct(LoadContext context, String name)
	{
		return context.getReferenceContext().constructCDOMObject(getCDOMClass(),
				name);
	}

	public abstract Class<? extends T> getCDOMClass();

	public static void addToken(LstToken tok)
	{
		TokenStore.inst().addToTokenMap(tok);
	}

	public static void addBonus(Class<? extends BonusObj> clazz)
	{
		try
		{
			TokenLibrary.addBonusClass(clazz);
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
		assertEquals("The graphs are not the same at test start", primaryProf,
			secondaryProf);
		// Ensure the graphs are the same at the start
		assertTrue("The graphs are not the same at test start", primaryContext
			.getListContext().masterListsEqual(
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
		assertTrue("Parsing of " + unparsedBuilt.toString()
			+ " failed unexpectedly", getLoader().parseLine(primaryContext,
			primaryProf, unparsedBuilt.toString(), campaign.getURI()));
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
		tc.putText(uri, (String[]) null);
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
		assertEquals("Re parse of unparsed string gave a different value",
			primaryProf, secondaryProf);

		// Ensure the graphs are the same
		assertTrue("Re parse of unparsed string gave a different graph",
			primaryContext.getListContext().masterListsEqual(
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

		assertTrue("First parse context was not valid", primaryContext.getReferenceContext().validate(null));
		assertTrue("Unprased/reparsed context was not valid", secondaryContext.getReferenceContext().validate(null));
		assertEquals(
			"First parse and unparse/reparse had different number of messages",
			expectedPrimaryMessageCount, primaryContext.getWriteMessageCount());
		assertEquals("Unexpected messages in unparse/reparse", 0,
			secondaryContext.getWriteMessageCount());
	}
}
