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

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Deals with FUMBLERANGE token
 */
public class FumblerangeToken extends AbstractNonEmptyToken<Equipment> implements CDOMPrimaryToken<Equipment>
{

    @Override
    public String getTokenName()
    {
        return "FUMBLERANGE";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, Equipment eq, String value)
    {
        if (ControlUtilities.hasControlToken(context, CControl.FUMBLERANGE))
        {
            return new ParseResult.Fail(getTokenName() + " is disabled when FUMBLERANGE control is used: " + value);
        }
        if (Constants.LST_DOT_CLEAR.equals(value))
        {
            context.getObjectContext().remove(eq, StringKey.FUMBLE_RANGE);
        } else
        {
            context.getObjectContext().put(eq, StringKey.FUMBLE_RANGE, value);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Equipment eq)
    {
        String range = context.getObjectContext().getString(eq, StringKey.FUMBLE_RANGE);
        boolean removed = context.getObjectContext().wasRemoved(eq, StringKey.FUMBLE_RANGE);
        List<String> list = new ArrayList<>();
        if (removed)
        {
            list.add(Constants.LST_DOT_CLEAR);
        }
        if (range != null)
        {
            list.add(range);
        }
        if (list.isEmpty())
        {
            return null;
        }
        return list.toArray(new String[0]);
    }

    @Override
    public Class<Equipment> getTokenClass()
    {
        return Equipment.class;
    }
}
