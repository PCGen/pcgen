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

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.EquipmentModifier;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class EqBuilderSpellToken implements CDOMSecondaryToken<EquipmentModifier>
{

    @Override
    public String getTokenName()
    {
        return "EQBUILDER.SPELL";
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
            context.getObjectContext().put(obj, StringKey.CHOICE_STRING, getTokenName());
            return ParseResult.SUCCESS;
        }
        if (value.indexOf('[') != -1)
        {
            return new ParseResult.Fail("CHOOSE:" + getTokenName() + " arguments may not contain [] : " + value);
        }
        if (value.charAt(0) == '|')
        {
            return new ParseResult.Fail("CHOOSE:" + getTokenName() + " arguments may not start with | : " + value);
        }
        if (value.charAt(value.length() - 1) == '|')
        {
            return new ParseResult.Fail("CHOOSE:" + getTokenName() + " arguments may not end with | : " + value);
        }
        if (value.contains("||"))
        {
            return new ParseResult.Fail("CHOOSE:" + getTokenName() + " arguments uses double separator || : " + value);
        }
        StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
        if (tok.countTokens() != 3)
        {
            return new ParseResult.Fail("COUNT:" + getTokenName() + " requires three arguments: " + value);
        }
        tok.nextToken();
        if (tok.hasMoreTokens())
        {
            String second = tok.nextToken();
            try
            {
                Integer.parseInt(second);
            } catch (NumberFormatException nfe)
            {
                return new ParseResult.Fail(
                        "CHOOSE:" + getTokenName() + " second argument must be an Integer : " + value);
            }
        }
        if (tok.hasMoreTokens())
        {
            String third = tok.nextToken();
            if (!third.equals("MAXLEVEL"))
            {
                try
                {
                    Integer.parseInt(third);
                } catch (NumberFormatException nfe)
                {
                    return new ParseResult.Fail(
                            "CHOOSE:" + getTokenName() + " third argument must be an Integer or 'MAXLEVEL': " + value);
                }
            }
        }
        if (tok.hasMoreTokens())
        {
            return new ParseResult.Fail(
                    "CHOOSE:" + getTokenName() + " must have 1 to 3 | delimited arguments: " + value);
        }
        context.getObjectContext().put(obj, StringKey.CHOICE_STRING, getTokenName() + '|' + value);
        return ParseResult.SUCCESS;
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
