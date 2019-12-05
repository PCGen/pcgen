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
package plugin.lsttokens.pcclass;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with MAXLEVEL Token
 */
public class MaxlevelToken implements CDOMPrimaryToken<PCClass>
{

    @Override
    public String getTokenName()
    {
        return "MAXLEVEL";
    }

    @Override
    public ParseResult parseToken(LoadContext context, PCClass pcc, String value)
    {
        int lim;
        if ("NOLIMIT".equalsIgnoreCase(value))
        {
            lim = Constants.NO_LEVEL_LIMIT;
        } else
        {
            try
            {
                lim = Integer.parseInt(value);
                if (lim <= 0)
                {
                    return new ParseResult.Fail("Value less than 1 is not valid for " + getTokenName() + ": " + value);
                }
            } catch (NumberFormatException nfe)
            {
                return new ParseResult.Fail("Value was not a number for " + getTokenName() + ": " + value);
            }
        }
        context.getObjectContext().put(pcc, IntegerKey.LEVEL_LIMIT, lim);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, PCClass pcc)
    {
        Integer lim = context.getObjectContext().getInteger(pcc, IntegerKey.LEVEL_LIMIT);
        if (lim == null)
        {
            return null;
        }
        String returnString = lim.toString();
        if (lim.equals(Constants.NO_LEVEL_LIMIT))
        {
            returnString = "NOLIMIT";
        } else if (lim <= 0)
        {
            context.addWriteMessage(getTokenName() + " must be an integer > 0");
            return null;
        }
        return new String[]{returnString};
    }

    @Override
    public Class<PCClass> getTokenClass()
    {
        return PCClass.class;
    }

}
