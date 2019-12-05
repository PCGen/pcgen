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

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with BOOKTYPE Token
 */
public class BooktypeToken extends AbstractTokenWithSeparator<Campaign> implements CDOMPrimaryToken<Campaign>
{

    @Override
    public String getTokenName()
    {
        return "BOOKTYPE";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, Campaign campaign, String value)
    {
        context.getObjectContext().removeList(campaign, ListKey.BOOK_TYPE);

        StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);
        while (aTok.hasMoreTokens())
        {
            context.getObjectContext().addToList(campaign, ListKey.BOOK_TYPE, aTok.nextToken());
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Campaign campaign)
    {
        Changes<String> changes = context.getObjectContext().getListChanges(campaign, ListKey.BOOK_TYPE);
        if (changes == null || changes.isEmpty())
        {
            return null;
        }
        Collection<String> added = changes.getAdded();
        if (added == null || added.isEmpty())
        {
            return null;
        }
        return new String[]{StringUtil.join(added, Constants.PIPE)};
    }

    @Override
    public Class<Campaign> getTokenClass()
    {
        return Campaign.class;
    }
}
