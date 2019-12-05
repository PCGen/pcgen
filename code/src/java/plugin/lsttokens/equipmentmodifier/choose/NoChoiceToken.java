/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.equipmentmodifier.choose;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.EquipmentModifier;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class NoChoiceToken implements CDOMSecondaryToken<EquipmentModifier>
{

    @Override
    public String getTokenName()
    {
        return "NOCHOICE";
    }

    @Override
    public String getParentToken()
    {
        return "CHOOSE";
    }

    @Override
    public ParseResult parseToken(LoadContext context, EquipmentModifier obj, String value)
    {
        if (value == null)
        {
            // No args - legal
            context.getObjectContext().put(obj, StringKey.CHOICE_STRING, getTokenName());
            return ParseResult.SUCCESS;
        }
        return new ParseResult.Fail("CHOOSE:" + getTokenName() + " must not have arguments: " + value);
    }

    @Override
    public String[] unparse(LoadContext context, EquipmentModifier eqMod)
    {
        String chooseString = context.getObjectContext().getString(eqMod, StringKey.CHOICE_STRING);
        if (chooseString == null)
        {
            return null;
        }
        String returnString;
        if (getTokenName().equals(chooseString))
        {
            returnString = "";
        } else
        {
            if (!chooseString.contains(getTokenName() + '|'))
            {
                return null;
            }
            returnString = chooseString.substring(getTokenName().length() + 1);
        }
        return new String[]{returnString};
    }

    @Override
    public Class<EquipmentModifier> getTokenClass()
    {
        return EquipmentModifier.class;
    }
}
