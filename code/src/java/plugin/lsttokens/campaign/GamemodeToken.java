/*
 * Copyright 2008 (C) Thomas Parker <thpr@users.sourceforge.net>
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
import pcgen.util.Logging;

/**
 * Class deals with GAMEMODE Token
 */
public class GamemodeToken extends AbstractTokenWithSeparator<Campaign> implements CDOMPrimaryToken<Campaign>
{

    @Override
    public String getTokenName()
    {
        return "GAMEMODE";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, Campaign campaign, String gameMode)
    {
        context.getObjectContext().removeList(campaign, ListKey.GAME_MODE);

        StringTokenizer aTok = new StringTokenizer(gameMode, Constants.PIPE);
        while (aTok.hasMoreTokens())
        {
            context.getObjectContext().addToList(campaign, ListKey.GAME_MODE, aTok.nextToken());
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Campaign campaign)
    {
        Changes<String> changes = context.getObjectContext().getListChanges(campaign, ListKey.GAME_MODE);
        if (changes == null || changes.isEmpty())
        {
            return null;
        }
        Collection<String> added = changes.getAdded();
        if (added == null || added.isEmpty())
        {
            Logging.errorPrint("Found Game Mode changes in " + campaign.getKeyName() + " but none were added");
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
