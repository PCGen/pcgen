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

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with CT Token
 */
public class CtToken implements CDOMPrimaryToken<Spell>
{

    @Override
    public String getTokenName()
    {
        return "CT";
    }

    @Override
    public String[] unparse(LoadContext context, Spell spell)
    {
        Integer i = context.getObjectContext().getInteger(spell, IntegerKey.CASTING_THRESHOLD);
        if (i == null)
        {
            return null;
        }
        if (i < 0)
        {
            context.addWriteMessage(getTokenName() + " requires a positive Integer");
            return null;
        }
        return new String[]{i.toString()};
    }

    @Override
    public Class<Spell> getTokenClass()
    {
        return Spell.class;
    }

    @Override
    public ParseResult parseToken(LoadContext context, Spell spell, String value)
    {
        try
        {
            int ct = Integer.parseInt(value);
            if (ct < 0)
            {
                return new ParseResult.Fail(getTokenName() + " requires a positive Integer");
            }
            context.getObjectContext().put(spell, IntegerKey.CASTING_THRESHOLD, ct);
            return ParseResult.SUCCESS;
        } catch (NumberFormatException nfe)
        {
            return new ParseResult.Fail(
                    getTokenName() + " expected an integer.  Tag must be of the form: " + getTokenName() + ":<int>");
        }
    }
}
