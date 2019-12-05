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
 * Deals with WT token
 */
public class WtToken extends AbstractNonEmptyToken<Equipment> implements CDOMPrimaryToken<Equipment>
{

    /**
     * Get token name
     *
     * @return token name
     */
    @Override
    public String getTokenName()
    {
        return "WT";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, Equipment eq, String value)
    {
        try
        {
            BigDecimal weight = new BigDecimal(value);
            if (weight.compareTo(BigDecimal.ZERO) < 0)
            {
                return new ParseResult.Fail(getTokenName() + " was expecting a decimal value >= 0 : " + value);
            }
            context.getObjectContext().put(eq, ObjectKey.WEIGHT, weight);
            return ParseResult.SUCCESS;
        } catch (NumberFormatException nfe)
        {
            return new ParseResult.Fail("Expected a Double for " + getTokenName() + ": " + value);
        }
    }

    @Override
    public String[] unparse(LoadContext context, Equipment eq)
    {
        BigDecimal weight = context.getObjectContext().getObject(eq, ObjectKey.WEIGHT);
        if (weight == null)
        {
            return null;
        }
        if (weight.compareTo(BigDecimal.ZERO) < 0)
        {
            context.addWriteMessage(getTokenName() + " was expecting a decimal value >= 0 : " + weight);
            return null;
        }
        return new String[]{weight.toString()};
    }

    @Override
    public Class<Equipment> getTokenClass()
    {
        return Equipment.class;
    }
}
