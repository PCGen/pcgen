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
 */

package plugin.lsttokens.campaign;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.content.CampaignURL;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

/**
 * {@code UrlToken} is responsible for parsing the URL campaign token.
 * <p>
 * -0500 (Sun, 27 Jan 2008) $
 */
public class UrlToken implements CDOMPrimaryToken<Campaign>
{

    private static final String URL_KIND_NAME_WEBSITE = "WEBSITE";
    private static final String URL_KIND_NAME_SURVEY = "SURVEY";

    @Override
    public String getTokenName()
    {
        return "URL";
    }

    @Override
    public ParseResult parseToken(LoadContext context, Campaign campaign, String value)
    {
        final StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
        if (tok.countTokens() != 3)
        {
            return new ParseResult.Fail(
                    "URL token requires three arguments. Link kind, " + "link and description.  : " + value);
        }
        String urlTypeName = tok.nextToken();
        String urlText = tok.nextToken();
        String urlDesc = tok.nextToken();
        CampaignURL.URLKind urlType;

        if (urlTypeName.equalsIgnoreCase(URL_KIND_NAME_WEBSITE))
        {
            if (!urlTypeName.equals(URL_KIND_NAME_WEBSITE))
            {
                Logging.log(Logging.LST_WARNING, "URL type should be WEBSITE in upper case : " + value);
            }
            urlType = CampaignURL.URLKind.WEBSITE;
            urlTypeName = "";
        } else if (urlTypeName.equalsIgnoreCase(URL_KIND_NAME_SURVEY))
        {
            if (!urlTypeName.equals(URL_KIND_NAME_SURVEY))
            {
                Logging.log(Logging.LST_WARNING, "URL type should be SURVEY in upper case : " + value);
            }
            urlType = CampaignURL.URLKind.SURVEY;
            urlTypeName = "";
        } else
        {
            urlType = CampaignURL.URLKind.PURCHASE;
        }

        URI uri;
        try
        {
            uri = new URI(urlText);
        } catch (URISyntaxException e)
        {
            return new ParseResult.Fail("Invalid URL (" + e.getMessage() + ") : " + value);
        }
        // Create URL object
        CampaignURL campUrl = new CampaignURL(urlType, urlTypeName, uri, urlDesc);

        // Add URL Object to campaign
        context.getObjectContext().addToList(campaign, ListKey.CAMPAIGN_URL, campUrl);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Campaign campaign)
    {
        Changes<CampaignURL> changes = context.getObjectContext().getListChanges(campaign, ListKey.CAMPAIGN_URL);
        if (changes == null || changes.isEmpty())
        {
            return null;
        }
        Collection<CampaignURL> added = changes.getAdded();
        if (added != null && !added.isEmpty())
        {
            List<String> list = new ArrayList<>();
            for (CampaignURL curl : added)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(curl.getUrlKind());
                sb.append(Constants.PIPE);
                sb.append(curl.getUri().toString());
                sb.append(Constants.PIPE);
                sb.append(curl.getUrlDesc());
            }
            return list.toArray(new String[0]);
        }
        return null;
    }

    @Override
    public Class<Campaign> getTokenClass()
    {
        return Campaign.class;
    }

}
