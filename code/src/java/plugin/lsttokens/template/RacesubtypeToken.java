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
package plugin.lsttokens.template;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.core.PCTemplate;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with RACESUBTYPE Token
 */
public class RacesubtypeToken extends AbstractTokenWithSeparator<PCTemplate> implements CDOMPrimaryToken<PCTemplate>
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
    protected ParseResult parseTokenWithSeparator(LoadContext context, PCTemplate template, String value)
    {
        StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
        while (tok.hasMoreTokens())
        {
            String aType = tok.nextToken();

            if (aType.startsWith(".REMOVE."))
            {
                String substring = aType.substring(8);
                if (substring.isEmpty())
                {
                    return new ParseResult.Fail("Invalid .REMOVE. in " + getTokenName() + " requires an argument");
                }
                context.getObjectContext().addToList(template, ListKey.REMOVED_RACESUBTYPE,
                        RaceSubType.getConstant(substring));
            } else
            {
                context.getObjectContext().addToList(template, ListKey.RACESUBTYPE, RaceSubType.getConstant(aType));
            }
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, PCTemplate pct)
    {
        Changes<RaceSubType> addedChanges = context.getObjectContext().getListChanges(pct, ListKey.RACESUBTYPE);
        Changes<RaceSubType> removedChanges =
                context.getObjectContext().getListChanges(pct, ListKey.REMOVED_RACESUBTYPE);
        Collection<RaceSubType> added = addedChanges.getAdded();
        Collection<RaceSubType> removed = removedChanges.getAdded();
        if (added == null && removed == null)
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean needPipe = false;
        if (removed != null)
        {
            for (RaceSubType rst : removed)
            {
                if (needPipe)
                {
                    sb.append(Constants.PIPE);
                }
                sb.append(".REMOVE.").append(rst.toString());
                needPipe = true;
            }
        }
        if (added != null)
        {
            for (RaceSubType rst : added)
            {
                if (needPipe)
                {
                    sb.append(Constants.PIPE);
                }
                sb.append(rst.toString());
                needPipe = true;
            }
        }
        return new String[]{sb.toString()};
    }

    @Override
    public Class<PCTemplate> getTokenClass()
    {
        return PCTemplate.class;
    }
}
