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
 * Handles the BONUS:COMBAT token.
 */
public final class Combat extends MultiTagBonusObj
{
    private static final String[] BONUS_TAGS = {"AC", "ATTACKS", "ATTACKS-SECONDARY", "DAMAGE", "DAMAGESIZE",
            "DAMAGE-PRIMARY", "DAMAGE-SECONDARY", "DAMAGE-SHORTRANGE", "DEFENSE", "INITIATIVE", "RANGEPENALTY", "REACH",
            "TOHIT", "TOHIT-PRIMARY", "TOHIT-SECONDARY", "TOHIT-SHORTRANGE", "BASEAB", "EPICAB"};

    @Override
    protected boolean parseToken(LoadContext context, String token)
    {
        if ("BAB".equalsIgnoreCase(token))
        {
            Logging
                    .errorPrint("BONUS:COMBAT|BAB has been removed due to " + "unusual behavior around epic class levels.  "
                            + "Please use BONUS:COMBAT|BASEAB or BONUS:COMBAT|EPICAB", context);
            return false;
        }
        if (ControlUtilities.hasControlToken(context, CControl.ACVARTOTAL))
        {
            if ("AC".equals(token))
            {
                Logging.errorPrint("BONUS:COMBAT|AC is deprecated when ACVARTOTAL control is used: " + token, context);
                return false;
            }
        }
        if (ControlUtilities.hasControlToken(context, CControl.INITIATIVE))
        {
            if ("INITIATIVE".equals(token))
            {
                Logging.errorPrint("BONUS:COMBAT|INITIATIVE is disabled when INITIATIVE control is used: " + token,
                        context);
                return false;
            }
        }
        if (ControlUtilities.hasControlToken(context, CControl.PCREACH))
        {
            if ("REACH".equals(token))
            {
                Logging.errorPrint("BONUS:COMBAT|REACH" + " is disabled when PCREACH control is used: " + token,
                        context);
                return false;
            }
        }
        return super.parseToken(context, token);
    }

    /**
     * Return the bonus tag handled by this class.
     *
     * @return The bonus handled by this class.
     */
    @Override
    public String getBonusHandled()
    {
        return "COMBAT";
    }

    /**
     * Get by index, an individual combat attribute that may be bonused.
     *
     * @param tagNumber the index of the combat attribute.
     * @return The combat attribute.
     */
    @Override
    protected String getBonusTag(final int tagNumber)
    {
        return BONUS_TAGS[tagNumber];
    }

    /**
     * Get the number of combat attributes that may be bonused.
     *
     * @return The number of combat attributes.
     */
    @Override
    protected int getBonusTagLength()
    {
        return BONUS_TAGS.length;
    }
}
