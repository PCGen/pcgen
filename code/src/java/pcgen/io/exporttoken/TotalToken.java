/*
 * Copyright 2003 (C) Devon Jones <soulcatcher@evilsoft.org>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 *
 */
package pcgen.io.exporttoken;

import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.utils.CoreUtility;
import pcgen.io.ExportHandler;
import pcgen.util.BigDecimalHelper;
import pcgen.util.Logging;
import pcgen.util.enumeration.Load;

/**
 * Deal with returning TOTAL Tokens
 * <p>
 * TOTAL.WEIGHT
 * TOTAL.VALUE
 * TOTAL.CAPACITY
 * TOTAL.LOAD
 */
public class TotalToken extends Token
{
    /**
     * Token name
     */
    public static final String TOKENNAME = "TOTAL";

    @Override
    public String getTokenName()
    {
        return TOKENNAME;
    }

    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        String retString = "";

        if ("TOTAL.WEIGHT".equals(tokenSource))
        {
            retString = getWeightToken(pc.getDisplay());
        } else if ("TOTAL.VALUE".equals(tokenSource))
        {
            retString = getValueToken(pc);
        } else if ("TOTAL.CAPACITY".equals(tokenSource))
        {
            retString = getCapacityToken(pc.getDisplay());
        } else if ("TOTAL.LOAD".equals(tokenSource))
        {
            retString = getLoadToken(pc.getDisplay());
        }

        return retString;
    }

    /**
     * Get the CAPACITY sub token
     *
     * @param display
     * @return the CAPACITY sub token
     */
    public static String getCapacityToken(CharacterDisplay display)
    {
        return Globals.getGameModeUnitSet().displayWeightInUnitSet(display.getMaxLoad().doubleValue());
    }

    /**
     * Get the LOAD sub token
     *
     * @param display
     * @return the LOAD sub token
     */
    public static String getLoadToken(CharacterDisplay display)
    {
        Load load = display.getLoadType();

        switch (load)
        {
            case LIGHT:
                return CoreUtility.capitalizeFirstLetter(Load.LIGHT.toString());

            case MEDIUM:
                return CoreUtility.capitalizeFirstLetter(Load.MEDIUM.toString());

            case HEAVY:
                return CoreUtility.capitalizeFirstLetter(Load.HEAVY.toString());

            case OVERLOAD:
                return CoreUtility.capitalizeFirstLetter(Load.OVERLOAD.toString());

            default:
                Logging.errorPrint(
                        "Unknown load constant detected in TokenTotal.getLoadToken, the constant was " + load + '.');

                return "Unknown";
        }
    }

    /**
     * Get the VALUE sub token
     *
     * @param pc
     * @return the VALUE sub token
     */
    public static String getValueToken(PlayerCharacter pc)
    {
        return BigDecimalHelper.trimZeros(pc.totalValue()) + ' ' + SettingsHandler.getGame().getCurrencyDisplay();
    }

    /**
     * Get the WEIGHT sub token
     *
     * @param display
     * @return the WEIGHT sub token
     */
    public static String getWeightToken(CharacterDisplay display)
    {
        return Globals.getGameModeUnitSet().displayWeightInUnitSet(display.totalWeight().doubleValue())
                + Globals.getGameModeUnitSet().getWeightUnit();
    }
}
