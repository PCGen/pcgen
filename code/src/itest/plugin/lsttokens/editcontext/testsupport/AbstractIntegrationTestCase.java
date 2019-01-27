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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.Loadable;
import pcgen.core.Campaign;
import pcgen.core.bonus.BonusObj;
import pcgen.output.publish.OutputDB;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.EditorLoadContext;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeEachClass;
import util.TestURI;

public abstract class AbstractIntegrationTestCase<T extends ConcretePrereqObject & Loadable>
{
	protected LoadContext primaryContext;
	protected LoadContext secondaryContext;
	protected T primaryProf;
	protected T secondaryProf;
	protected String prefix = "";

	protected static CampaignSourceEntry testCampaign;
	protected static CampaignSourceEntry modCampaign;

	public abstract CDOMLoader<T> getLoader();

	public abstract CDOMPrimaryToken<? super T> getToken();

	@BeforeAll
	public static void classSetUp() throws URISyntaxException
	{
		OutputDB.reset();
		testCampaign = new CampaignSourceEntry(new Campaign(), TestURI.getURI());
		modCampaign = new CampaignSourceEntry(new Campaign(), new URI(
				"file:/Test%20Case%20Modifier"));
	}

	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		// Yea, this causes warnings...
		TokenRegistration.register(getToken());
		primaryContext = new EditorLoadContext();
		secondaryContext = new EditorLoadContext();
		primaryContext.getReferenceContext().importObject(BuildUtilities.getFeatCat());
		secondaryContext.getReferenceContext().importObject(BuildUtilities.getFeatCat());
		primaryProf = construct(primaryContext, "TestObj");
		secondaryProf = construct(secondaryContext, "TestObj");
	}

	protected T construct(LoadContext context, String name)
	{
		return context.getReferenceContext().constructCDOMObject(getCDOMClass(),
				name);
	}

	public abstract Class<? extends T> getCDOMClass();

	protected static void addBonus(Class<? extends BonusObj> clazz)
	{
		try
		{
			TokenLibrary.addBonusClass(clazz);
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	protected void verifyCleanStart()
	{
		// Default is not to write out anything
		Assert.assertNull(getToken().unparse(primaryContext, primaryProf));
		Assert.assertNull(getToken().unparse(secondaryContext, secondaryProf));
		// Ensure the graphs are the same at the start
		assertEquals("The graphs are not the same at test start", primaryProf,
				secondaryProf
		);
		// Ensure the graphs are the same at the start
		assertTrue("The graphs are not the same at test start", primaryContext
				.getListContext().masterListsEqual(
						secondaryContext.getListContext()));
	}

	protected void commit(CampaignSourceEntry campaign, TestContext tc,
			String... str) throws PersistenceLayerException
	{
		String unparsedBuilt =
				Arrays.stream(str)
				      .map(s -> getToken().getTokenName() + ':' + s + '\t')
				      .collect(Collectors.joining());
		URI uri = campaign.getURI();
		primaryContext.setSourceURI(uri);
		assertTrue("Parsing of " + unparsedBuilt
				+ " failed unexpectedly", getLoader().parseLine(primaryContext,
				primaryProf, unparsedBuilt, campaign.getURI()
		));
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
				Assert.assertNull("Expecting empty unparsed", unparsed);
				getLoader().parseLine(secondaryContext, secondaryProf, null,
						uri);
				continue;
			}
			Assert.assertNotNull(unparsed);
			assertEquals(str.size(), unparsed.length);

			for (int i = 0; i < str.size(); i++)
			{
				assertEquals("Expected " + i + " item to be equal", str.get(i),
						unparsed[i]
				);
			}

			// Do round Robin
			String unparsedBuilt = Arrays.stream(unparsed)
			                             .map(s -> getToken().getTokenName() + ':' + s + '\t')
			                             .collect(Collectors.joining());
			secondaryContext.setSourceURI(uri);
			getLoader().parseLine(secondaryContext, secondaryProf,
					unparsedBuilt, uri);
		}

		// Ensure the objects are the same
		assertEquals("Re parse of unparsed string gave a different value",
				primaryProf, secondaryProf
		);

		// Ensure the graphs are the same
		assertTrue(
				"Re parse of unparsed string gave a different graph",
				primaryContext.getListContext().masterListsEqual(
						secondaryContext.getListContext())
		);

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
				Assert.assertNull(unparsed);
				continue;
			}
			assertEquals(str.size(), unparsed.length);

			for (int i = 0; i < str.size(); i++)
			{
				assertEquals("Expected " + i + " item to be equal", str.get(i),
						unparsed[i]
				);
			}
		}

		assertTrue("First parse context was not valid", primaryContext.getReferenceContext().validate(null));
		assertTrue(
				"Unprased/reparsed context was not valid",
				secondaryContext.getReferenceContext().validate(null)
		);
		int expectedPrimaryMessageCount = 0;
		assertEquals(
				"First parse and unparse/reparse had different number of messages",
				expectedPrimaryMessageCount, primaryContext.getWriteMessageCount()
		);
		assertEquals("Unexpected messages in unparse/reparse", 0,
				secondaryContext.getWriteMessageCount()
		);
	}
}
