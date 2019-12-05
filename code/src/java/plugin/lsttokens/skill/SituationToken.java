/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.skill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Skill;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class SituationToken extends AbstractTokenWithSeparator<Skill> implements CDOMPrimaryToken<Skill>
{

    @Override
    public String getTokenName()
    {
        return "SITUATION";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, Skill skill, String value)
    {
        StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
        boolean first = true;

        while (tok.hasMoreTokens())
        {
            String tokString = tok.nextToken();
            if (Constants.LST_DOT_CLEAR_ALL.equals(tokString))
            {
                if (!first)
                {
                    return new ParseResult.Fail(
                            "  Non-sensical " + getTokenName() + ": .CLEARALL was not the first list item: " + value);
                }
                context.getObjectContext().removeList(skill, ListKey.SITUATION);
            } else
            {
                context.getObjectContext().addToList(skill, ListKey.SITUATION, tokString);
            }
            first = false;
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Skill skill)
    {
        Changes<String> changes = context.getObjectContext().getListChanges(skill, ListKey.SITUATION);
        if (changes == null || changes.isEmpty())
        {
            return null;
        }
        List<String> list = new ArrayList<>();
        Collection<String> removedItems = changes.getRemoved();
        if (removedItems != null && !removedItems.isEmpty())
        {
            context.addWriteMessage(".CLEAR. not supported");
            return null;
        }
        if (changes.includesGlobalClear())
        {
            list.add(Constants.LST_DOT_CLEAR);
        }
        Collection<String> added = changes.getAdded();
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
    public Class<Skill> getTokenClass()
    {
        return Skill.class;
    }
}
