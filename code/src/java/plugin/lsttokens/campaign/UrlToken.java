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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.content.CampaignURL;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * <code>UrlToken</code> is responsible for parsing the URL campaign token.
 * 
 * Last Editor: $Author$ Last Edited: $Date: 2008-01-27 22:03:36
 * -0500 (Sun, 27 Jan 2008) $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class UrlToken implements CDOMPrimaryToken<Campaign>
{

	private static final String URL_KIND_NAME_WEBSITE = "WEBSITE";
	private static final String URL_KIND_NAME_SURVEY = "SURVEY";

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "URL";
	}

	public boolean parse(LoadContext context, Campaign obj, String value)
			throws PersistenceLayerException
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
			Logging.log(Logging.LST_ERROR, "Invalid URL (" + e.getMessage()
					+ ") : " + value);
			return false;
		}
		// Create URL object
		CampaignURL campUrl = new CampaignURL(urlType, urlTypeName, url,
				urlDesc);

		// Add URL Object to campaign
		context.obj.addToList(obj, ListKey.CAMPAIGN_URL, campUrl);

		return true;
	}

	public String[] unparse(LoadContext context, Campaign obj)
	{
		Changes<CampaignURL> changes = context.getObjectContext()
				.getListChanges(obj, ListKey.CAMPAIGN_URL);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		Collection<CampaignURL> added = changes.getAdded();
		if (added != null && !added.isEmpty())
		{
			List<String> list = new ArrayList<String>();
			for (CampaignURL curl : added)
			{
				StringBuilder sb = new StringBuilder();
				sb.append(curl.getUrlKind());
				sb.append(Constants.PIPE);
				sb.append(curl.getUrl().toString());
				sb.append(Constants.PIPE);
				sb.append(curl.getUrlDesc());
			}
			return list.toArray(new String[list.size()]);
		}
		return null;
	}

	public Class<Campaign> getTokenClass()
	{
		return Campaign.class;
	}

}
