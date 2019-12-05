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
package plugin.lsttokens.race;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.core.Race;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with RACESUBTYPE Token
 */
public class RacesubtypeToken extends AbstractTokenWithSeparator<Race> implements CDOMPrimaryToken<Race>
{

    @Override
    public String getTokenName()
    {
        return "RACESUBTYPE";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, Race race, String value)
    {
        StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
        boolean first = true;

        while (tok.hasMoreTokens())
        {
            String tokString = tok.nextToken();
            if (Constants.LST_DOT_CLEAR.equals(tokString))
            {
                if (!first)
                {
                    return new ParseResult.Fail(
                            "  Non-sensical " + getTokenName() + ": .CLEAR was not the first list item: " + value);
                }
                context.getObjectContext().removeList(race, ListKey.RACESUBTYPE);
            } else if (tokString.startsWith(Constants.LST_DOT_CLEAR_DOT))
            {
                String clearText = tokString.substring(7);
                context.getObjectContext().removeFromList(race, ListKey.RACESUBTYPE,
                        RaceSubType.getConstant(clearText));
            } else
            {
                context.getObjectContext().addToList(race, ListKey.RACESUBTYPE, RaceSubType.getConstant(tokString));
            }
            first = false;
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Race race)
    {
        Changes<RaceSubType> changes = context.getObjectContext().getListChanges(race, ListKey.RACESUBTYPE);
        if (changes == null || changes.isEmpty())
        {
            return null;
        }
        List<String> list = new ArrayList<>();
        Collection<RaceSubType> removedItems = changes.getRemoved();
        if (changes.includesGlobalClear())
        {
            if (removedItems != null && !removedItems.isEmpty())
            {
                context.addWriteMessage(
                        "Non-sensical relationship in " + getTokenName() + ": global .CLEAR and local .CLEAR. performed");
                return null;
            }
            list.add(Constants.LST_DOT_CLEAR);
        } else if (removedItems != null && !removedItems.isEmpty())
        {
            list.add(Constants.LST_DOT_CLEAR_DOT + StringUtil.join(removedItems, "|.CLEAR."));
        }
        Collection<RaceSubType> added = changes.getAdded();
        if (added != null && !added.isEmpty())
        {
            list.add(StringUtil.join(added, Constants.PIPE));
        }
        if (list.isEmpty())
        {
            return null;
        }
        return list.toArray(new String[0]);
    }

    @Override
    public Class<Race> getTokenClass()
    {
        return Race.class;
    }
}
