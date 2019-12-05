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
package plugin.lsttokens.equipmentmodifier;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.EquipmentModifier;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Deals with CHARGES token
 */
public class ChargesToken extends AbstractNonEmptyToken<EquipmentModifier>
        implements CDOMPrimaryToken<EquipmentModifier>
{

    @Override
    public String getTokenName()
    {
        return "CHARGES";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, EquipmentModifier mod, String value)
    {
        int pipeLoc = value.indexOf(Constants.PIPE);
        if (pipeLoc == -1)
        {
            return new ParseResult.Fail(
                    getTokenName() + " has no | : must be of format <min charges>|<max charges>: " + value);
        }
        if (value.lastIndexOf(Constants.PIPE) != pipeLoc)
        {
            return new ParseResult.Fail(
                    getTokenName() + " has two | : must be of format <min charges>|<max charges>: " + value);
        }
        String minChargeString = value.substring(0, pipeLoc);
        int minCharges;
        try
        {
            minCharges = Integer.parseInt(minChargeString);
            if (minCharges < 0)
            {
                return new ParseResult.Fail(getTokenName() + " min charges must be >= zero: " + value);
            }
        } catch (NumberFormatException nfe)
        {
            return new ParseResult.Fail(getTokenName() + " min charges is not an integer: " + value);
        }

        String maxChargeString = value.substring(pipeLoc + 1);
        int maxCharges;
        try
        {
            maxCharges = Integer.parseInt(maxChargeString);
            /*
             * No need to test max for negative, since min was tested and there
             * is a later test for max >= min
             */
        } catch (NumberFormatException nfe)
        {
            return new ParseResult.Fail(getTokenName() + " max charges is not an integer: " + value);
        }

        if (minCharges > maxCharges)
        {
            return new ParseResult.Fail(getTokenName() + " max charges must be >= min charges: " + value);
        }

        context.getObjectContext().put(mod, IntegerKey.MIN_CHARGES, minCharges);
        context.getObjectContext().put(mod, IntegerKey.MAX_CHARGES, maxCharges);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, EquipmentModifier mod)
    {
        Integer max = context.getObjectContext().getInteger(mod, IntegerKey.MAX_CHARGES);
        Integer min = context.getObjectContext().getInteger(mod, IntegerKey.MIN_CHARGES);
        if (max == null && min == null)
        {
            return null;
        }
        if (max == null || min == null)
        {
            context.addWriteMessage("EquipmentModifier requires both MAX_CHARGES and MIN_CHARGES for " + getTokenName()
                    + " if one of the two is present");
            return null;
        }
        int minInt = min;
        if (minInt < 0)
        {
            context.addWriteMessage("EquipmentModifier requires MIN_CHARGES be > 0");
            return null;
        }
        if (max < minInt)
        {
            context.addWriteMessage(
                    "EquipmentModifier requires MAX_CHARGES be " + "greater than MIN_CHARGES for " + getTokenName());
            return null;
        }
        return new String[]{min + Constants.PIPE + max};
    }

    @Override
    public Class<EquipmentModifier> getTokenClass()
    {
        return EquipmentModifier.class;
    }
}
