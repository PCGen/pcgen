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

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.spell.Spell;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with ITEM Token
 */
public class ItemToken extends AbstractTokenWithSeparator<Spell> implements CDOMPrimaryToken<Spell>
{

    @Override
    public String getTokenName()
    {
        return "ITEM";
    }

    @Override
    protected char separator()
    {
        return ',';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, Spell spell, String value)
    {
        StringTokenizer aTok = new StringTokenizer(value, Constants.COMMA);

        while (aTok.hasMoreTokens())
        {
            String tokString = aTok.nextToken();
            int bracketLoc = tokString.indexOf('[');
            if (bracketLoc == 0)
            {
                // Check ends with bracket
                if (tokString.lastIndexOf(']') != tokString.length() - 1)
                {
                    return new ParseResult.Fail(
                            "Invalid " + getTokenName() + ": mismatched open Bracket: " + tokString + " in " + value);
                }
                String substring = tokString.substring(1, tokString.length() - 1);
                if (substring.isEmpty())
                {
                    return new ParseResult.Fail("Invalid " + getTokenName() + ": cannot be empty item in brackets []");
                }
                context.getObjectContext().addToList(spell, ListKey.PROHIBITED_ITEM, Type.getConstant(substring));
            } else
            {
                if (tokString.lastIndexOf(']') != -1)
                {
                    return new ParseResult.Fail(
                            "Invalid " + getTokenName() + ": mismatched close Bracket: " + tokString + " in " + value);
                }
                context.getObjectContext().addToList(spell, ListKey.ITEM, Type.getConstant(tokString));
            }
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Spell spell)
    {
        Changes<Type> changes = context.getObjectContext().getListChanges(spell, ListKey.ITEM);
        Changes<Type> proChanges = context.getObjectContext().getListChanges(spell, ListKey.PROHIBITED_ITEM);
        Collection<Type> changeAdded = changes.getAdded();
        Collection<Type> proAdded = proChanges.getAdded();
        StringBuilder sb = new StringBuilder();
        boolean needComma = false;
        if (changeAdded != null)
        {
            for (Type t : changeAdded)
            {
                if (needComma)
                {
                    sb.append(Constants.COMMA);
                }
                sb.append(t.toString());
                needComma = true;
            }
        }
        if (proAdded != null)
        {
            for (Type t : proAdded)
            {
                if (needComma)
                {
                    sb.append(Constants.COMMA);
                }
                sb.append('[').append(t.toString()).append(']');
                needComma = true;
            }
        }
        if (sb.length() == 0)
        {
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
