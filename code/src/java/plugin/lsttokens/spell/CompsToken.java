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
package plugin.lsttokens.spell;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.spell.Spell;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with COMPS Token
 */
public class CompsToken extends AbstractTokenWithSeparator<Spell> implements CDOMPrimaryToken<Spell>
{

    @Override
    public String getTokenName()
    {
        return "COMPS";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, Spell spell, String value)
    {
        StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);

        boolean first = true;
        while (aTok.hasMoreTokens())
        {
            String tok = aTok.nextToken();
            if (Constants.LST_DOT_CLEAR.equals(tok))
            {
                if (!first)
                {
                    return new ParseResult.Fail("Non-sensical use of .CLEAR in " + getTokenName() + ": " + value);
                }
                context.getObjectContext().removeList(spell, ListKey.COMPONENTS);
            } else
            {
                context.getObjectContext().addToList(spell, ListKey.COMPONENTS, tok);
            }
            first = false;
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Spell spell)
    {
        Changes<String> changes = context.getObjectContext().getListChanges(spell, ListKey.COMPONENTS);
        if (changes == null || changes.isEmpty())
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        Collection<?> added = changes.getAdded();
        boolean globalClear = changes.includesGlobalClear();
        if (globalClear)
        {
            sb.append(Constants.LST_DOT_CLEAR);
        }
        if (added != null && !added.isEmpty())
        {
            if (globalClear)
            {
                sb.append(Constants.PIPE);
            }
            sb.append(StringUtil.join(added, Constants.PIPE));
        }
        if (sb.length() == 0)
        {
            context.addWriteMessage(
                    getTokenName() + " was expecting non-empty changes to include " + "added items or global clear");
            return null;
        }
        return new String[]{sb.toString()};
    }

    @Override
    public Class<Spell> getTokenClass()
    {
        return Spell.class;
    }
}
