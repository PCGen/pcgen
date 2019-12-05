/*
 * TempBonusToken.java
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

import java.util.StringTokenizer;

import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

/**
 * Deals with returning the value of the TEMPBONUS token
 */
public class TempBonusToken extends Token
{
    /**
     * Token name
     */
    public static final String TOKENNAME = "TEMPBONUS";

    @Override
    public String getTokenName()
    {
        return TOKENNAME;
    }

    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        String retString = "";
        StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
        aTok.nextToken();

        if (aTok.hasMoreTokens())
        {
            int tempIndex = 0;

            try
            {
                tempIndex = Integer.parseInt(aTok.nextToken());
            } catch (NumberFormatException ne)
            {
                // Weird
            }

            String subToken = (aTok.hasMoreTokens()) ? aTok.nextToken() : "NAME";

            if ("NAME".equals(subToken))
            {
                retString = getNameToken(pc, tempIndex);
            } else if ("DESC".equals(subToken))
            {
                retString = getDescToken(pc, tempIndex);
            }
        }

        return retString;
    }

    /**
     * Get the indicated TEMPBONUS value if present.
     *
     * @param pc
     * @param tempIndex
     * @return the TEMPBONUS value or empty string
     */
    public static String getNameToken(PlayerCharacter pc, int tempIndex)
    {
        if (tempIndex >= pc.getNamedTempBonusList().size())
        {
            return "";
        }
        return pc.getNamedTempBonusList().get(tempIndex);
    }

    public static String getDescToken(PlayerCharacter pc, int tempIndex)
    {
        if (tempIndex >= pc.getNamedTempBonusDescList().size())
        {
            return "";
        }
        return pc.getNamedTempBonusDescList().get(tempIndex);
    }
}
