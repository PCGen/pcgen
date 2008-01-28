/*
 * UrlToken.java
 * Copyright 2008 (C) James Dempsey
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

package plugin.lsttokens.campaign;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.StringTokenizer;

import pcgen.core.Campaign;
import pcgen.core.CampaignURL;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.util.Logging;

/**
 * <code>UrlToken</code> is responsible for parsing the URL campaign token.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class UrlToken implements CampaignLstToken
{

	private static final String URL_KIND_NAME_WEBSITE = "WEBSITE";
	private static final String URL_KIND_NAME_SURVEY = "SURVEY";

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.CampaignLstToken#parse(pcgen.core.Campaign, java.lang.String, java.net.URI)
	 */
	public boolean parse(Campaign campaign, String value, URI sourceURI)
	{
		final StringTokenizer tok = new StringTokenizer(value, "|");
		if (tok.countTokens() != 3)
		{
			Logging.log(Logging.LST_ERROR,
				"URL token requires three arguments. Link kind, "
					+ "link and description.  : " + value);
			return false;
		}
		String urlTypeName = tok.nextToken(); 
		String urlText = tok.nextToken(); 
		String urlDesc = tok.nextToken();
		CampaignURL.URLKind urlType;

		if (urlTypeName.equalsIgnoreCase(URL_KIND_NAME_WEBSITE))
		{
			if (!urlTypeName.equals(URL_KIND_NAME_WEBSITE))
			{
				Logging.log(Logging.LST_WARNING,
					"URL type should be WEBSITE in upper case : " + value);
			}
			urlType = CampaignURL.URLKind.WEBSITE;
			urlTypeName = "";
		}
		else if (urlTypeName.equalsIgnoreCase(URL_KIND_NAME_SURVEY))
		{
			if (!urlTypeName.equals(URL_KIND_NAME_SURVEY))
			{
				Logging.log(Logging.LST_WARNING,
					"URL type should be SURVEY in upper case : " + value);
			}
			urlType = CampaignURL.URLKind.SURVEY;
			urlTypeName = "";
		}
		else
		{
			urlType = CampaignURL.URLKind.PURCHASE;
		}

		URL url;
		try
		{
			url = new URL(urlText);
		}
		catch (MalformedURLException e)
		{
			Logging.log(Logging.LST_ERROR,
				"Invalid URL (" + e.getMessage() + ") : " + value);
			return false;
		}
		// Create URL object
		CampaignURL campUrl = new CampaignURL(urlType, urlTypeName, url, urlDesc);
		
		// Add URL Object to campaign
		campaign.addURL(campUrl);
 
		return true;
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "URL";
	}

}
