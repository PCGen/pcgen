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

import junit.framework.TestCase;
import pcgen.cdom.content.CampaignURL;
import pcgen.cdom.enumeration.ListKey;
import pcgen.rules.context.LoadContext;

import org.junit.Before;
import plugin.lsttokens.campaign.UrlToken;
import plugin.lsttokens.testsupport.TokenRegistration;

/**
 * <code>CampaignTest</code> is ...
 * 
 * (Tue, 11 Nov 2008) $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 */
public class CampaignTest extends TestCase
{

	Campaign testCamp = new Campaign();

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new UrlToken());
	}

	public void testURL() throws Exception
	{
		final String eCommerceName = "Barcommerce";
		final String eCommerceURL = "http://www.barcommercesite.com/product_info.php?products_id=12345&affiliate_id=54321";
		final String eCommerceDesc = "Support PCGen by buying this source now!";

		assertEquals("No URLs in the campaign to start", true, testCamp
				.getSafeListFor(ListKey.CAMPAIGN_URL).isEmpty());

		LoadContext context = Globals.getContext();
		context.unconditionallyProcess(testCamp, "URL",
				"WEBSITE|http://pcgen.sf.net|PCGen Main Site");
		assertEquals("New URL in the campaign", 1, testCamp.getSafeListFor(
				ListKey.CAMPAIGN_URL).size());
		CampaignURL theURL = testCamp.getSafeListFor(ListKey.CAMPAIGN_URL).get(
				0);
		assertEquals("Checking kind", CampaignURL.URLKind.WEBSITE, theURL
				.getUrlKind());
		assertEquals("Checking name", "", theURL.getUrlName());
		assertEquals("Checking URL", "http://pcgen.sf.net", theURL.getUri()
				.toString());
		assertEquals("Checking description", "PCGen Main Site", theURL
				.getUrlDesc());

		context.unconditionallyProcess(testCamp, "URL", "SURVEY|http://pcgen.sf.net/survey|PCGen Survey");
		assertEquals("Second new URL in the campaign", 2, testCamp
				.getSafeListFor(ListKey.CAMPAIGN_URL).size());
		theURL = testCamp.getSafeListFor(ListKey.CAMPAIGN_URL).get(1);
		assertEquals("Checking kind", CampaignURL.URLKind.SURVEY, theURL
				.getUrlKind());
		assertEquals("Checking name", "", theURL.getUrlName());
		assertEquals("Checking URL", "http://pcgen.sf.net/survey", theURL
				.getUri().toString());
		assertEquals("Checking description", "PCGen Survey", theURL
				.getUrlDesc());

		context.unconditionallyProcess(testCamp, "URL", eCommerceName + "|" + eCommerceURL + "|"
				+ eCommerceDesc);
		assertEquals("Third new URL in the campaign", 3, testCamp
				.getSafeListFor(ListKey.CAMPAIGN_URL).size());
		theURL = testCamp.getSafeListFor(ListKey.CAMPAIGN_URL).get(2);
		assertEquals("Checking kind", CampaignURL.URLKind.PURCHASE, theURL
				.getUrlKind());
		assertEquals("Checking name", eCommerceName, theURL.getUrlName());
		assertEquals("Checking URL", eCommerceURL, theURL.getUri().toString());
		assertEquals("Checking description", eCommerceDesc, theURL.getUrlDesc());
	}
}
