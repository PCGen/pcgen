/*
 * Copyright 2007 (C) James Dempsey
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package pcgen.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.cdom.content.CampaignURL;
import pcgen.cdom.enumeration.ListKey;
import pcgen.rules.context.LoadContext;
import plugin.lsttokens.campaign.UrlToken;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CampaignTest
{

	private Campaign testCamp = new Campaign();

	@BeforeEach
	public void setUp() {
		TokenRegistration.register(new UrlToken());
	}

	@AfterEach
	public void tearDown()
	{
		TokenRegistration.clearTokens();
		testCamp = null;
	}

	@Test
	public void testURL() {
		final String eCommerceName = "Barcommerce";
		final String eCommerceURL =
				"http://www.barcommercesite.com/product_info.php?products_id=12345&affiliate_id=54321";
		final String eCommerceDesc = "Support PCGen by buying this source now!";

		assertTrue(testCamp
				.getSafeListFor(ListKey.CAMPAIGN_URL).isEmpty(), "No URLs in the campaign to start");

		LoadContext context = Globals.getContext();
		context.unconditionallyProcess(testCamp, "URL",
				"WEBSITE|http://pcgen.sf.net|PCGen Main Site");
		assertEquals(1, testCamp.getSafeListFor(
				ListKey.CAMPAIGN_URL).size(), "New URL in the campaign");
		CampaignURL theURL = testCamp.getSafeListFor(ListKey.CAMPAIGN_URL).get(
				0);
		assertEquals(CampaignURL.URLKind.WEBSITE, theURL
				.getUrlKind(), "Checking kind");
		assertEquals("", theURL.getUrlName(), "Checking name");
		assertEquals("http://pcgen.sf.net", theURL.getUri()
		                                                     .toString(), "Checking URL");
		assertEquals("PCGen Main Site", theURL
				.getUrlDesc(), "Checking description");

		context.unconditionallyProcess(testCamp, "URL", "SURVEY|http://pcgen.sf.net/survey|PCGen Survey");
		assertEquals(2, testCamp
				.getSafeListFor(ListKey.CAMPAIGN_URL).size(), "Second new URL in the campaign");
		theURL = testCamp.getSafeListFor(ListKey.CAMPAIGN_URL).get(1);
		assertEquals(CampaignURL.URLKind.SURVEY, theURL
				.getUrlKind(), "Checking kind");
		assertEquals("", theURL.getUrlName(), "Checking name");
		assertEquals("http://pcgen.sf.net/survey", theURL
				.getUri().toString(), "Checking URL");
		assertEquals("PCGen Survey", theURL
				.getUrlDesc(), "Checking description");

		context.unconditionallyProcess(testCamp, "URL", eCommerceName + "|" + eCommerceURL + "|"
				+ eCommerceDesc);
		assertEquals(3, testCamp
				.getSafeListFor(ListKey.CAMPAIGN_URL).size(), "Third new URL in the campaign");
		theURL = testCamp.getSafeListFor(ListKey.CAMPAIGN_URL).get(2);
		assertEquals(CampaignURL.URLKind.PURCHASE, theURL
				.getUrlKind(), "Checking kind");
		assertEquals(eCommerceName, theURL.getUrlName(), "Checking name");
		assertEquals(eCommerceURL, theURL.getUri().toString(), "Checking URL");
		assertEquals(eCommerceDesc, theURL.getUrlDesc(), "Checking description");
	}
}
