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
 * Handles the BONUS:EQMARMOR token.
 */
public final class EqmArmor extends MultiTagBonusObj
{
    private static final String[] BONUS_TAGS = {"AC", "ACCHECK", "DEFBONUS", "EDR", "MAXDEX", "SPELLFAILURE"};

    /**
     * Return the bonus tag handled by this class.
     *
     * @return The bonus handled by this class.
     */
    @Override
    public String getBonusHandled()
    {
        return "EQMARMOR";
    }

    /**
     * Get by index, an individual armour equipment attribute that may be bonused.
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
     * Get the number of armour equipment attributes that may be bonused.
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
        if (ControlUtilities.hasControlToken(context, CControl.EDR))
        {
            if ("EDR".equals(token))
            {
                Logging.errorPrint("BONUS:EQMARMOR|EDR is disabled when EDR control is used: " + token, context);
                return false;
            }
        }
        if (ControlUtilities.hasControlToken(context, CControl.EQSPELLFAILURE))
        {
            if ("SPELLFAILURE".equals(token))
            {
                Logging.errorPrint(
                        "BONUS:EQMARMOR|SPELLFAILURE is disabled " + "when EQSPELLFAILURE control is used: " + token,
                        context);
                return false;
            }
        }
        if (ControlUtilities.hasControlToken(context, CControl.EQMAXDEX))
        {
            if ("MAXDEX".equals(token))
            {
                Logging.errorPrint("BONUS:EQMARMOR|MAXDEX is disabled " + "when EQMAXDEX control is used: " + token,
                        context);
                return false;
            }
        }
        if (ControlUtilities.hasControlToken(context, CControl.ACVARTOTAL))
        {
            if ("AC".equals(token))
            {
                Logging.errorPrint("BONUS:EQMARMOR|AC is deprecated when ACVARTOTAL control is used: " + token,
                        context);
                return false;
            }
        }
        if (ControlUtilities.hasControlToken(context, CControl.EQACCHECK))
        {
            if ("ACCHECK".equals(token))
            {
                Logging.errorPrint("BONUS:EQMARMOR|ACCHECK is disabled " + "when EQACCHECK control is used: " + token,
                        context);
                return false;
            }
        }
        return super.parseToken(context, token);
    }
}
