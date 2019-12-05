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

import java.util.ArrayList;
import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with TARGETAREA Token
 */
public class TargetareaToken extends AbstractNonEmptyToken<Spell> implements CDOMPrimaryToken<Spell>
{
    @Override
    public String getTokenName()
    {
        return "TARGETAREA";
    }

    @Override
    public ParseResult parseNonEmptyToken(LoadContext context, Spell spell, String value)
    {
        if (Constants.LST_DOT_CLEAR.equals(value))
        {
            context.getObjectContext().remove(spell, StringKey.TARGET_AREA);
        } else
        {
            if (!StringUtil.hasBalancedParens(value))
            {
                return new ParseResult.Fail(
                        "Unbalanced parentheses in " + getTokenName() + " '" + value + "' used in spell " + spell);
            }
            context.getObjectContext().put(spell, StringKey.TARGET_AREA, value);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Spell spell)
    {
        String target = context.getObjectContext().getString(spell, StringKey.TARGET_AREA);
        boolean globalClear = context.getObjectContext().wasRemoved(spell, StringKey.TARGET_AREA);
        List<String> list = new ArrayList<>();
        if (globalClear)
        {
            list.add(Constants.LST_DOT_CLEAR);
        }
        if (target != null)
        {
            list.add(target);
        }
        if (list.isEmpty())
        {
            return null;
        }
        return list.toArray(new String[0]);
    }

    @Override
    public Class<Spell> getTokenClass()
    {
        return Spell.class;
    }
}
