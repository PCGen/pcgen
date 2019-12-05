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
 * Implements the CRMODPRIORITY token on ClassType
 */
public class CRModPriorityToken extends AbstractNonEmptyToken<ClassType> implements CDOMPrimaryToken<ClassType>
{

    @Override
    public String getTokenName()
    {
        return "CRMODPRIORITY";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, ClassType classType, String value)
    {
        try
        {
            classType.setCRModPriority(Integer.parseInt(value));
            return ParseResult.SUCCESS;
        } catch (NumberFormatException e)
        {
            return new ParseResult.Fail("Illegal value for miscinfo.CLASSTYPE.CRMODPRIORITY: " + value);
        }
    }

    @Override
    public String[] unparse(LoadContext context, ClassType classType)
    {
        return new String[]{Integer.toString(classType.getCRModPriority())};
    }

    @Override
    public Class<ClassType> getTokenClass()
    {
        return ClassType.class;
    }
}
