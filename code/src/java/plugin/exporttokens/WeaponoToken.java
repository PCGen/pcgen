/*
 * WeaponoToken.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.WeaponToken;

/**
 * {@code WeaponoToken}.
 */
public class WeaponoToken extends WeaponToken
{
    /**
     * Weapono Token.
     */
    public static final String TOKEN_NAME = "WEAPONO";

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
     * Get the value of the supplied output token.
     *
     * @param tokenSource The full source of the token
     * @param pc          The character to retrieve the value for.
     * @param eh          The ExportHandler that is managing the export
     *                    (may be null for a once off conversion).
     * @return The value of the token.
     */
    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        StringTokenizer aTok = new StringTokenizer(tokenSource, ".", false);
        //Weapono Token
        aTok.nextToken();

        final String ind = aTok.nextToken();
        final int index = Integer.parseInt(ind);
        Equipment eq = getWeaponEquipment(pc.getDisplay(), index);

        if (eq != null)
        {
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

    /**
     * Creates equipment based on the first Secondary Weapon.
     *
     * @param display The character used to generate the size.
     * @return The equipment.
     */
    public static Equipment getWeaponEquipment(CharacterDisplay display, final int anIndex)
    {
        final List<Equipment> secWeapons = new ArrayList<>(display.getSecondaryWeapons());
        if (!secWeapons.isEmpty() && anIndex < secWeapons.size())
        {
            return secWeapons.get(anIndex);
        }
        return null;
    }
}
