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
 * MISC
 * Handles the BONUS:MISC token.
 */
public final class Misc extends MultiTagBonusObj
{
    private static final String[] BONUS_TAGS = {"ACCHECK", "MAXDEX", "SPELLFAILURE", "SR", "CR"};

    /**
     * Return the bonus tag handled by this class.
     *
     * @return The bonus handled by this class.
     */
    @Override
    public String getBonusHandled()
    {
        return "MISC";
    }

    /**
     * Get by index, an individual miscellaneous attribute that may be bonused.
     *
     * @param tagNumber the index of the miscellaneous attribute.
     * @return The miscellaneous attribute.
     */
    @Override
    protected String getBonusTag(final int tagNumber)
    {
        return BONUS_TAGS[tagNumber];
    }

    /**
     * Get the number of miscellaneous attributes that may be bonused.
     *
     * @return The number of miscellaneous attributes.
     */
    @Override
    protected int getBonusTagLength()
    {
        return BONUS_TAGS.length;
    }

    @Override
    protected boolean parseToken(LoadContext context, String token)
    {
        if (ControlUtilities.hasControlToken(context, CControl.PCSPELLFAILURE))
        {
            if ("SPELLFAILURE".equals(token))
            {
                Logging.errorPrint(
                        "BONUS:MISC|SPELLFAILURE is disabled " + "when PCSPELLFAILURE control is used: " + token, context);
                return false;
            }
        }
        if (ControlUtilities.hasControlToken(context, CControl.PCMAXDEX))
        {
            if ("MAXDEX".equals(token))
            {
                Logging.errorPrint("BONUS:MISC|MAXDEX is disabled " + "when PCMAXDEX control is used: " + token,
                        context);
                return false;
            }
        }
        if (ControlUtilities.hasControlToken(context, CControl.PCACCHECK))
        {
            if ("ACCHECK".equals(token))
            {
                Logging.errorPrint("BONUS:MISC|ACCHECK is disabled " + "when PCACCHECK control is used: " + token,
                        context);
                return false;
            }
        }
        return super.parseToken(context, token);
    }

}
