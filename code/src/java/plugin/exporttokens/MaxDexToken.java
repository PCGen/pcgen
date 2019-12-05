/*
 * MaxDexToken.java
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
 */
package plugin.exporttokens;

import pcgen.cdom.base.Constants;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.util.Delta;

/**
 * Deals with retunring value from MAXDEX token
 */
public class MaxDexToken extends Token
{
    /**
     * Token name
     */
    public static final String TOKENNAME = "MAXDEX";

    @Override
    public String getTokenName()
    {
        return TOKENNAME;
    }

    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        String retString = "";
        int mod = process(pc);

        if (mod != Constants.MAX_MAXDEX)
        {
            retString = Delta.toString(mod);
        }

        return retString;
    }

    /**
     * Get the value for the MAXDEX token
     *
     * @param tokenSource
     * @param pc
     * @return the value for the MAXDEX token
     */
    public static String getMaxDexToken(String tokenSource, PlayerCharacter pc)
    {
        String retString = "";
        int mod = process(pc);

        if (mod != Constants.MAX_MAXDEX)
        {
            retString = Delta.toString(mod);
        }

        return retString;
    }

    private static int process(PlayerCharacter pc)
    {
        String maxDexVar = ControlUtilities.getControlToken(Globals.getContext(), CControl.PCMAXDEX);
        if (maxDexVar == null)
        {
            return pc.processOldMaxDex();
        }
        return ((Number) pc.getGlobal(maxDexVar)).intValue();
    }
}
