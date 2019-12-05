/*
 * Copyright (c) 2018 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.gamemode.classtype;

import pcgen.core.ClassType;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Implements the XPPENALTY token on ClassType
 */
public class XPPenaltyToken extends AbstractNonEmptyToken<ClassType> implements CDOMPrimaryToken<ClassType>
{

    @Override
    public String getTokenName()
    {
        return "XPPENALTY";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, ClassType classType, String value)
    {
        boolean set;
        char firstChar = value.charAt(0);
        if (firstChar == 'y' || firstChar == 'Y')
        {
            if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
            {
                return new ParseResult.Fail("You should use 'YES' as the " + getTokenName() + ": " + value);
            }
            set = true;
        } else
        {
            if (firstChar != 'N' && firstChar != 'n')
            {
                return new ParseResult.Fail("You should use 'YES' or 'NO' as the " + getTokenName() + ": " + value);
            }
            if (value.length() > 1 && !value.equalsIgnoreCase("NO"))
            {
                return new ParseResult.Fail("You should use 'YES' or 'NO' as the " + getTokenName() + ": " + value);
            }
            set = false;
        }
        classType.setXPPenalty(set);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, ClassType classType)
    {
        return new String[]{classType.getXPPenalty() ? "YES" : "NO"};
    }

    @Override
    public Class<ClassType> getTokenClass()
    {
        return ClassType.class;
    }
}
