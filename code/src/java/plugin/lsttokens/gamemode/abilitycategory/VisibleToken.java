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
import pcgen.util.enumeration.Visibility;

/**
 * Handles the VISIBLE token on an ABILITYCATEGORY line.
 */
public class VisibleToken extends AbstractNonEmptyToken<AbilityCategory> implements CDOMPrimaryToken<AbilityCategory>
{
    @Override
    public String getTokenName()
    {
        return "VISIBLE";
    }

    @Override
    public ParseResult parseNonEmptyToken(LoadContext context, AbilityCategory ac, String value)
    {
        Visibility vis;
        switch (value)
        {
            case "YES":
                vis = Visibility.DEFAULT;
                break;
            case "QUALIFY":
                vis = Visibility.QUALIFY;
                break;
            case "NO":
                vis = Visibility.HIDDEN;
                break;
            default:
                return new ParseResult.Fail("Unable to understand " + getTokenName() + " tag: " + value);
        }
        ac.setVisible(vis);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, AbilityCategory ac)
    {
        Visibility vis = ac.getVisibility();
        String visString;
        if (vis.equals(Visibility.DEFAULT))
        {
            visString = "YES";
        } else if (vis.equals(Visibility.QUALIFY))
        {
            visString = "QUALIFY";
        } else if (vis.equals(Visibility.HIDDEN))
        {
            visString = "NO";
        } else
        {
            context.addWriteMessage("Visibility " + vis + " is not a valid Visibility for "
                    + ac.getClass().getSimpleName() + ' ' + ac.getKeyName());
            return null;
        }
        return new String[]{visString};
    }

    @Override
    public Class<AbilityCategory> getTokenClass()
    {
        return AbilityCategory.class;
    }
}
