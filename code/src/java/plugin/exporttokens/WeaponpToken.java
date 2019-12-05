/*
 * WeaponpToken.java
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

import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.WeaponToken;

/**
 * {@code WeaponpToken}.
 */
public class WeaponpToken extends WeaponToken
{
    /**
     * Weaponp Token.
     */
    public static final String TOKEN_NAME = "WEAPONP";

    /**
     * Gets the token name
     *
     * @return The token name.
     */
    @Override
    public String getTokenName()
    {
        return TOKEN_NAME;
    }

    /**
     * Get the value of the token.
     *
     * @param tokenSource The full source of the token
     * @param pc          The character to retrieve the value for.
     * @param eh          The ExportHandler that is managing the export.
     * @return The value of the token.
     */
    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        StringTokenizer aTok = new StringTokenizer(tokenSource, ".", false);
        //Weaponp Token
        aTok.nextToken();

        if (pc.getDisplay().hasPrimaryWeapons())
        {
            Equipment eq = pc.getDisplay().getPrimaryWeapons().iterator().next();
            return getWeaponToken(pc, eq, aTok, tokenSource);
        } else if (eh != null && eh.getExistsOnly())
        {
            eh.setNoMoreItems(true);
            if (eh.getCheckBefore())
            {
                eh.setCanWrite(false);
            }
        }
        return "";
    }
}
