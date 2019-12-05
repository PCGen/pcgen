/*
 * DescToken.java
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

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * {@code DescToken} parses the DESC token for campaigns.
 */
public class DescToken extends AbstractNonEmptyToken<Campaign> implements CDOMPrimaryToken<Campaign>
{

    @Override
    public String getTokenName()
    {
        return "DESC";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, Campaign campaign, String value)
    {
        ParseResult pr = checkForInvalidXMLChars(value);
        if (pr.passed())
        {
            context.getObjectContext().put(campaign, StringKey.DESCRIPTION, value);
        }
        return pr;
    }

    @Override
    public String[] unparse(LoadContext context, Campaign campaign)
    {
        String title = context.getObjectContext().getString(campaign, StringKey.DESCRIPTION);
        if (title == null)
        {
            return null;
        }
        return new String[]{title};
    }

    @Override
    public Class<Campaign> getTokenClass()
    {
        return Campaign.class;
    }
}
