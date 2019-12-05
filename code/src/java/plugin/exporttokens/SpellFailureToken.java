/*
 * SpellFailureToken.java
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
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.EqToken;
import pcgen.io.exporttoken.Token;

//SPELLFAILURE
public class SpellFailureToken extends Token
{
    public static final String TOKENNAME = "SPELLFAILURE";

    @Override
    public String getTokenName()
    {
        return TOKENNAME;
    }

    //TODO: Rip the processing of this token out of PlayerCharacter
    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        return Integer.toString(getSpellFailure(pc));
    }

    private int getSpellFailure(PlayerCharacter pc)
    {
        String spellFailVar = ControlUtilities.getControlToken(Globals.getContext(), CControl.PCSPELLFAILURE);
        if (spellFailVar == null)
        {
            int bonus = 0;
            for (Equipment eq : pc.getEquippedEquipmentSet())
            {
                bonus += EqToken.getSpellFailureTokenInt(pc, eq);
            }
            return bonus + (int) pc.getTotalBonusTo("MISC", "SPELLFAILURE");
        }
        return ((Number) pc.getGlobal(spellFailVar)).intValue();
    }
}
