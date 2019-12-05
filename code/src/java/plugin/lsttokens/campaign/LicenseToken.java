/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.campaign;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with LICENSE Token
 */
public class LicenseToken extends AbstractNonEmptyToken<Campaign> implements CDOMPrimaryToken<Campaign>
{

    @Override
    public String getTokenName()
    {
        return "LICENSE";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, Campaign campaign, String value)
    {
        if (value.startsWith("FILE="))
        {
            String fileURI = value.substring(5);
            if (fileURI.isEmpty())
            {
                return new ParseResult.Fail("Cannot have empty FILE in " + getTokenName());
            }
            CampaignSourceEntry cse = context.getCampaignSourceEntry(campaign, fileURI);
            if (cse == null)
            {
                return ParseResult.INTERNAL_ERROR;
            }
            context.getObjectContext().addToList(campaign, ListKey.LICENSE_FILE, cse);
        } else
        {
            context.getObjectContext().addToList(campaign, ListKey.LICENSE, value);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Campaign campaign)
    {
        Changes<String> changes = context.getObjectContext().getListChanges(campaign, ListKey.LICENSE);
        Changes<CampaignSourceEntry> filechanges =
                context.getObjectContext().getListChanges(campaign, ListKey.LICENSE_FILE);
        List<String> set = new ArrayList<>();
        Collection<String> added = changes.getAdded();
        if (added != null && !added.isEmpty())
        {
            set.addAll(added);
        }
        Collection<CampaignSourceEntry> addeduri = filechanges.getAdded();
        if (addeduri != null && !addeduri.isEmpty())
        {
            for (CampaignSourceEntry cse : addeduri)
            {
                set.add("FILE=" + cse.getLSTformat());
            }
        }
        if (set.isEmpty())
        {
            //Okay, no license info
            return null;
        }
        return set.toArray(new String[0]);
    }

    @Override
    public Class<Campaign> getTokenClass()
    {
        return Campaign.class;
    }

}
