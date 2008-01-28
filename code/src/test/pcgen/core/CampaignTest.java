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

import java.net.URI;

import pcgen.PCGenTestCase;
import plugin.lsttokens.campaign.DescToken;
import plugin.lsttokens.campaign.UrlToken;

/**
 * <code>CampaignTest</code> is ...
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class CampaignTest extends PCGenTestCase
{

	Campaign testCamp = new Campaign();
	
	public void testURL() throws Exception
	{
		final String eCommerceName = "Barcommerce";
		final String eCommerceURL = "http://www.barcommercesite.com/product_info.php?products_id=12345&affiliate_id=54321";
		final String eCommerceDesc = "Support PCGen by buying this source now!";

		UrlToken tok = new UrlToken();
		assertEquals("No URLs in the campaign to start", true, testCamp
			.getUrlList().isEmpty());
		
		tok.parse(testCamp, "WEBSITE|http://pcgen.sf.net|PCGen Main Site",
			new URI("file://pcgen.core.CampaignTest"));
		assertEquals("New URL in the campaign", 1, testCamp
			.getUrlList().size());
		CampaignURL theURL = testCamp.getUrlList().get(0);
		assertEquals("Checking kind", CampaignURL.URLKind.WEBSITE, theURL
			.getUrlKind());
		assertEquals("Checking name", "", theURL.getUrlName());
		assertEquals("Checking URL", "http://pcgen.sf.net", theURL.getUrl()
			.toString());
		assertEquals("Checking description", "PCGen Main Site", theURL
			.getUrlDesc());

		tok.parse(testCamp, "SURVEY|http://pcgen.sf.net/survey|PCGen Survey",
			new URI("file://pcgen.core.CampaignTest"));
		assertEquals("Second new URL in the campaign", 2, testCamp
			.getUrlList().size());
		theURL = testCamp.getUrlList().get(1);
		assertEquals("Checking kind", CampaignURL.URLKind.SURVEY, theURL
			.getUrlKind());
		assertEquals("Checking name", "", theURL.getUrlName());
		assertEquals("Checking URL", "http://pcgen.sf.net/survey", theURL.getUrl()
			.toString());
		assertEquals("Checking description", "PCGen Survey", theURL
			.getUrlDesc());

		tok.parse(testCamp, eCommerceName + "|" + eCommerceURL + "|"
			+ eCommerceDesc, new URI("file://pcgen.core.CampaignTest"));
		assertEquals("Third new URL in the campaign", 3, testCamp
			.getUrlList().size());
		theURL = testCamp.getUrlList().get(2);
		assertEquals("Checking kind", CampaignURL.URLKind.PURCHASE, theURL
			.getUrlKind());
		assertEquals("Checking name", eCommerceName, theURL.getUrlName());
		assertEquals("Checking URL", eCommerceURL, theURL.getUrl()
			.toString());
		assertEquals("Checking description", eCommerceDesc, theURL
			.getUrlDesc());
	}


	public void testDesc() throws Exception
	{
		final String firstDescription = "An initial entry which should be overwritten";
		final String secondDescription = "The second entry";

		DescToken tok = new DescToken();
		assertEquals("No description in the campaign to start", "", testCamp
			.getDescription());
		
		tok.parse(testCamp, firstDescription,
			new URI("file://pcgen.core.CampaignTest"));
		assertEquals("First description should appear", firstDescription, testCamp
			.getDescription());

		tok.parse(testCamp, secondDescription,
			new URI("file://pcgen.core.CampaignTest"));
		assertEquals("New URL in the campaignsecondDescription", secondDescription, testCamp
			.getDescription());
	}
}
