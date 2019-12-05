/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2006 (C) Devon Jones <soulcatcher@evilsoft.org>
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
package plugin.lsttokens.pointbuy.stat;

import pcgen.core.PointBuyCost;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class CostToken extends AbstractNonEmptyToken<PointBuyCost> implements CDOMPrimaryToken<PointBuyCost>
{

    @Override
    public String getTokenName()
    {
        return "COST";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, PointBuyCost pbc, String value)
    {
        try
        {
            pbc.setBuyCost(Integer.parseInt(value));
            return ParseResult.SUCCESS;
        } catch (NumberFormatException nfe)
        {
            return new ParseResult.Fail(
                    getTokenName() + " expected an integer.  Tag must be of the form: " + getTokenName() + ":<int>");
        }
    }

    @Override
    public String[] unparse(LoadContext context, PointBuyCost pbc)
    {
        return new String[]{String.valueOf(pbc.getBuyCost())};
    }

    @Override
    public Class<PointBuyCost> getTokenClass()
    {
        return PointBuyCost.class;
    }
}
