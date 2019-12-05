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

import java.util.StringJoiner;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.identifier.SpellSchool;
import pcgen.core.spell.Spell;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with SCHOOL Token
 */
public class SchoolToken extends AbstractTokenWithSeparator<Spell> implements CDOMPrimaryToken<Spell>
{

    @Override
    public String getTokenName()
    {
        return "SCHOOL";
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
            String tokString = aTok.nextToken();
            if (Constants.LST_DOT_CLEAR.equals(tokString))
            {
                if (!first)
                {
                    return new ParseResult.Fail(
                            "  Non-sensical " + getTokenName() + ": .CLEAR was not the first list item: " + value);
                }
                context.getObjectContext().removeList(spell, ListKey.SPELL_SCHOOL);
            } else if (Constants.LST_ALL.equals(tokString))
            {
                return new ParseResult.Fail(getTokenName() + "used reserved word ALL: " + value);
            } else if (Constants.LST_ANY.equals(tokString))
            {
                return new ParseResult.Fail(getTokenName() + "used reserved word ANY: " + value);
            } else
            {
                SpellSchool ss = context.getReferenceContext().constructNowIfNecessary(SpellSchool.class, tokString);
                context.getObjectContext().addToList(spell, ListKey.SPELL_SCHOOL, ss);
            }
            first = false;
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Spell spell)
    {
        Changes<SpellSchool> changes = context.getObjectContext().getListChanges(spell, ListKey.SPELL_SCHOOL);
        if (changes == null || changes.isEmpty())
        {
            return null;
        }
        StringJoiner joiner = new StringJoiner(Constants.PIPE);
        if (changes.includesGlobalClear())
        {
            joiner.add(Constants.LST_DOT_CLEAR);
        }
        if (changes.hasAddedItems())
        {
            changes.getAdded().forEach(added -> joiner.add(added.toString()));
        }
        return new String[]{joiner.toString()};
    }

    @Override
    public Class<Spell> getTokenClass()
    {
        return Spell.class;
    }
}
