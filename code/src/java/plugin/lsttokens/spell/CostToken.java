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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with COST Token
 */
public class CostToken extends AbstractNonEmptyToken<Spell> implements CDOMPrimaryToken<Spell>
{

    @Override
    public String getTokenName()
    {
        return "COST";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, Spell spell, String value)
    {
        if (Constants.LST_DOT_CLEAR.equals(value))
        {
            context.getObjectContext().remove(spell, ObjectKey.COST);
        } else
        {
            try
            {
                BigDecimal cost = new BigDecimal(value);
                if (cost.compareTo(BigDecimal.ZERO) <= 0)
                {
                    return new ParseResult.Fail(getTokenName() + " requires a positive Integer");
                }
                context.getObjectContext().put(spell, ObjectKey.COST, cost);
            } catch (NumberFormatException nfe)
            {
                return new ParseResult.Fail(
                        getTokenName() + " expected an integer.  Tag must be of the form: " + getTokenName() + ":<int>");
            }
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Spell spell)
    {
        BigDecimal i = context.getObjectContext().getObject(spell, ObjectKey.COST);
        boolean globalClear = context.getObjectContext().wasRemoved(spell, ObjectKey.COST);
        List<String> list = new ArrayList<>();
        if (globalClear)
        {
            list.add(Constants.LST_DOT_CLEAR);
        }
        if (i != null)
        {
            if (i.compareTo(BigDecimal.ZERO) <= 00)
            {
                context.addWriteMessage(getTokenName() + " requires a positive Integer");
                return null;
            }
            list.add(i.toString());
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
