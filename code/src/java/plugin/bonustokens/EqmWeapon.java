/*
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
package plugin.bonustokens;

import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.bonus.MultiTagBonusObj;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * Handles the BONUS:EQMWEAPON token.
 */
public final class EqmWeapon extends MultiTagBonusObj
{
    private static final String[] BONUS_TAGS =
            {"CRITRANGEADD", "CRITRANGEDOUBLE", "DAMAGESIZE", "RANGEADD", "RANGEMULT"};

    /**
     * Return the bonus tag handled by this class.
     *
     * @return The bonus handled by this class.
     */
    @Override
    public String getBonusHandled()
    {
        return "EQMWEAPON";
    }

    /**
     * Get by index, an individual weapon equipment attribute that may be bonused.
     *
     * @param tagNumber the index of the equipment attribute.
     * @return The equipment attribute.
     */
    @Override
    protected String getBonusTag(final int tagNumber)
    {
        return BONUS_TAGS[tagNumber];
    }

    /**
     * Get the number of weapon equipment attributes that may be bonused.
     *
     * @return The number of equipment attributes.
     */
    @Override
    protected int getBonusTagLength()
    {
        return BONUS_TAGS.length;
    }

    @Override
    protected boolean parseToken(LoadContext context, String token)
    {
        if (ControlUtilities.hasControlToken(context, CControl.CRITRANGE))
        {
            if ("CRITRANGEADD".equals(token))
            {
                Logging.errorPrint("BONUS:EQMWEAPON|CRITRANGEADD is disabled when CRITRANGE control is used: " + token,
                        context);
                return false;
            }
            if ("CRITRANGEDOUBLE".equals(token))
            {
                Logging.errorPrint(
                        "BONUS:EQMWEAPON|CRITRANGEDOUBLE is disabled when CRITRANGE control is used: " + token, context);
                return false;
            }
        }
        if (ControlUtilities.hasControlToken(context, CControl.EQRANGE))
        {
            if ("RANGEADD".equals(token))
            {
                Logging.errorPrint("BONUS:EQMWEAPON|RANGEADD is disabled when EQRANGE control is used: " + token,
                        context);
                return false;
            }
            if ("RANGEMULT".equals(token))
            {
                Logging.errorPrint("BONUS:EQMWEAPON|RANGEMULT is disabled when EQRANGE control is used: " + token,
                        context);
                return false;
            }
        }
        return super.parseToken(context, token);
    }
}
