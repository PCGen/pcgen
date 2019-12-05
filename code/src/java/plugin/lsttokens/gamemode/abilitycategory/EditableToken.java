/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.gamemode.abilitycategory;

import pcgen.core.AbilityCategory;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class EditableToken extends AbstractNonEmptyToken<AbilityCategory> implements CDOMPrimaryToken<AbilityCategory>
{
    @Override
    public String getTokenName()
    {
        return "EDITABLE";
    }

    @Override
    public ParseResult parseNonEmptyToken(LoadContext context, final AbilityCategory ac, final String value)
    {
        boolean set;
        char firstChar = value.charAt(0);
        if (firstChar == 'y' || firstChar == 'Y')
        {
            if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
            {
                return new ParseResult.Fail("You should use 'YES' as the " + getTokenName() + ": " + value);
            }
            set = Boolean.TRUE;
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
            set = Boolean.FALSE;
        }
        ac.setEditable(set);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, AbilityCategory ac)
    {
        if (ac.isEditable())
        {
            return null;
        } else
        {
            return new String[]{"NO"};
        }
    }

    @Override
    public Class<AbilityCategory> getTokenClass()
    {
        return AbilityCategory.class;
    }
}
