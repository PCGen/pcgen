/*
 * CampaignTest.java
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
 *
 * Created on 28/01/2008
 *
 * $Id$
 */

package pcgen.core;

import pcgen.cdom.content.CampaignURL;
import pcgen.cdom.enumeration.ListKey;
import pcgen.rules.context.LoadContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import plugin.lsttokens.campaign.UrlToken;
import plugin.lsttokens.testsupport.TokenRegistration;

/**
 * <code>CampaignTest</code> is ...
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 */
public class CampaignTest
{

	Campaign testCamp = new Campaign();

	@Before
	public void setUp() throws Exception
	{
		TokenRegistration.register(new UrlToken());
	}

	@Test
	public void testURL() throws Exception
	{
		final String eCommerceName = "Barcommerce";
		final String eCommerceURL = "http://www.barcommercesite.com/product_info.php?products_id=12345&affiliate_id=54321";
		final String eCommerceDesc = "Support PCGen by buying this source now!";

		Assert.assertEquals("No URLs in the campaign to start", true, testCamp
				.getSafeListFor(ListKey.CAMPAIGN_URL).isEmpty());

		LoadContext context = Globals.getContext();
		context.unconditionallyProcess(testCamp, "URL",
				"WEBSITE|http://pcgen.sf.net|PCGen Main Site");
		Assert.assertEquals("New URL in the campaign", 1, testCamp.getSafeListFor(
				ListKey.CAMPAIGN_URL).size());
		CampaignURL theURL = testCamp.getSafeListFor(ListKey.CAMPAIGN_URL).get(
				0);
		Assert.assertEquals("Checking kind", CampaignURL.URLKind.WEBSITE, theURL
				.getUrlKind());
		Assert.assertEquals("Checking name", "", theURL.getUrlName());
		Assert.assertEquals("Checking URL", "http://pcgen.sf.net", theURL.getUri()
																		 .toString());
		Assert.assertEquals("Checking description", "PCGen Main Site", theURL
				.getUrlDesc());

		context.unconditionallyProcess(testCamp, "URL", "SURVEY|http://pcgen.sf.net/survey|PCGen Survey");
		Assert.assertEquals("Second new URL in the campaign", 2, testCamp
				.getSafeListFor(ListKey.CAMPAIGN_URL).size());
		theURL = testCamp.getSafeListFor(ListKey.CAMPAIGN_URL).get(1);
		Assert.assertEquals("Checking kind", CampaignURL.URLKind.SURVEY, theURL
				.getUrlKind());
		Assert.assertEquals("Checking name", "", theURL.getUrlName());
		Assert.assertEquals("Checking URL", "http://pcgen.sf.net/survey", theURL
				.getUri().toString());
		Assert.assertEquals("Checking description", "PCGen Survey", theURL
				.getUrlDesc());

		context.unconditionallyProcess(testCamp, "URL", eCommerceName + "|" + eCommerceURL + "|"
				+ eCommerceDesc);
		Assert.assertEquals("Third new URL in the campaign", 3, testCamp
				.getSafeListFor(ListKey.CAMPAIGN_URL).size());
		theURL = testCamp.getSafeListFor(ListKey.CAMPAIGN_URL).get(2);
		Assert.assertEquals("Checking kind", CampaignURL.URLKind.PURCHASE, theURL
				.getUrlKind());
		Assert.assertEquals("Checking name", eCommerceName, theURL.getUrlName());
		Assert.assertEquals("Checking URL", eCommerceURL, theURL.getUri().toString());
		Assert.assertEquals("Checking description", eCommerceDesc, theURL.getUrlDesc());
	}
}
