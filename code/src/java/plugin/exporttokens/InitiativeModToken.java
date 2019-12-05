/*
 * InitiativeModToken.java
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

import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.util.Delta;

/**
 * Deal with the INITIATIVEMOD
 */
public class InitiativeModToken extends Token
{
    @Override
    public String getTokenName()
    {
        return "INITIATIVEMOD";
    }

    //TODO: Merge InitiativeBonusToken, InitiativeMiscToken and InitiativeModToken
    //to become INITIATIVE.BONUS, INITAITIVE.MISC & INITIATIVE.MOD
    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        return Delta.toString(getInitiativeModToken(pc));
    }

    /**
     * Get the token
     *
     * @param pc PlayerCharacter
     * @return int Initiative Modifier
     */
    public static int getInitiativeModToken(PlayerCharacter pc)
    {
        String initiativeVar = ControlUtilities.getControlToken(Globals.getContext(), CControl.INITIATIVE);
        if (initiativeVar == null)
        {
            return pc.getDisplay().processOldInitiativeMod();
        }
        return ((Number) pc.getGlobal(initiativeVar)).intValue();
    }
}
