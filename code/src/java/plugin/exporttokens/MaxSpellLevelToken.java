/*
 * MaxSpellLevelToken.java
 * Copyright 2004 (C) Chris Ward <frugal@purplewombat.co.uk>
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
package plugin.exporttokens;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.util.Logging;

/**
 * Handle the MaxSpellLevel token which outputs the maximum level spell
 * castable by the character for the specified class.
 */
public class MaxSpellLevelToken extends Token
{
    /**
     * Token name
     */
    public static final String TOKENNAME = "MAXSPELLLEVEL";

    @Override
    public String getTokenName()
    {
        return TOKENNAME;
    }

    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        StringBuilder retValue = new StringBuilder();
        String[] tokens = tokenSource.split("\\.");

        if (tokens.length != 2)
        {
            Logging.errorPrint("MAXSPELLLEVEL token must be of the format 'MAXSPELLLEVEL.x' where 'x' is an integer. '"
                    + tokenSource + "' is not valid");
            return "";
        }
        int classNumber = -1;
        try
        {
            classNumber = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException nfe)
        {
            Logging.errorPrint("Unknown class number in token: " + tokenSource);
        }

        final CDOMObject aObject = pc.getSpellClassAtIndex(classNumber);
        if (aObject != null)
        {
            PCClass aClass = null;

            if (aObject instanceof PCClass)
            {
                aClass = (PCClass) aObject;
            }

            if (aClass != null)
            {
                retValue.append(pc.getSpellSupport(aClass).getHighestLevelSpell(pc));
            }
        }

        return retValue.toString();
    }

}
