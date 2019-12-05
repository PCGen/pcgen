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
package plugin.lsttokens.equipment;

import java.math.BigDecimal;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Deals with COST token
 */
public class CostToken extends AbstractNonEmptyToken<Equipment> implements CDOMPrimaryToken<Equipment>
{

    @Override
    public String getTokenName()
    {
        return "COST";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, Equipment eq, String value)
    {
        try
        {
            BigDecimal cost = new BigDecimal(value);
            // CONSIDER This apparently is not a requirement, since some items
            // in the RSRD have negative COST?
            //
            // if (cost.compareTo(BigDecimal.ZERO) < 0)
            // {
            // return new ParseResult.Fail(getTokenName()
            // + " must be a positive number: " + value, context);
            // return false;
            // }
            context.getObjectContext().put(eq, ObjectKey.COST, cost);
            return ParseResult.SUCCESS;
        } catch (NumberFormatException e)
        {
            return new ParseResult.Fail(getTokenName() + " expected a number: " + value);
        }
    }

    @Override
    public String[] unparse(LoadContext context, Equipment eq)
    {
        BigDecimal bd = context.getObjectContext().getObject(eq, ObjectKey.COST);
        if (bd == null)
        {
            return null;
        }
        return new String[]{bd.toString()};
    }

    @Override
    public Class<Equipment> getTokenClass()
    {
        return Equipment.class;
    }
}
