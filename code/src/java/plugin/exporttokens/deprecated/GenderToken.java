/*
 * GenderToken.java
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
package plugin.exporttokens.deprecated;

import pcgen.cdom.enumeration.BiographyField;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

/**
 * Deal with:
 * <p>
 * GENDER
 * GENDER.SHORT
 * GENDER.LONG
 */
public class GenderToken extends Token
{
    @Override
    public String getTokenName()
    {
        return "GENDER";
    }

    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        String retString = "";
        if (!pc.getDisplay().getSuppressBioField(BiographyField.GENDER))
        {
            /*
             * TODO Short and long result are the same as Gender is no longer
             * abbreviated in PC (what to do?)
             */
            if ("GENDER".equals(tokenSource) || "GENDER.SHORT".equals(tokenSource))
            {
                retString = pc.getGenderString();
            } else if ("GENDER.LONG".equals(tokenSource))
            {
                retString = pc.getGenderString();
            }
        }

        return retString;
    }
}
