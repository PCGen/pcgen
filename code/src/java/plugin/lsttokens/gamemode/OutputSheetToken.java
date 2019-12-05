/*
 * InfoSheetToken.java
 * Copyright 2010 (C) James Dempsey
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
package plugin.lsttokens.gamemode;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;
import pcgen.util.Logging;

/**
 * This class handles the INFOSHEET game mode token. The token allows a
 * game mode specific information output sheet to be specified that will be
 * displayed on the summary tab when editing a character.
 */
public class OutputSheetToken implements GameModeLstToken
{

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        String[] tokens = value.split("\\|");
        List<String> validTags = new ArrayList<>(Arrays.asList("HTM", "PDF", "TXT"));

        if (tokens.length == 2)
        {
            if (tokens[0].equals("DIRECTORY"))
            {
                gameMode.setOutputSheetDirectory(tokens[1]);
                return true;
            }
            if (tokens[0].startsWith("DEFAULT."))
            {
                String[] subtokens = tokens[0].split("\\.");
                if (subtokens.length == 2)
                {
                    if (validTags.contains(subtokens[1]))
                    {
                        gameMode.setOutputSheetDefault(subtokens[1], tokens[1]);
                        return true;
                    }
                    Logging.log(Logging.LST_ERROR, "Invalid token " + getTokenName() + Constants.COLON + value
                            + ". Invalid DEFAULT.x subtoken" + " in " + source.toString());
                    return false;
                }
            }
        }
        Logging.log(Logging.LST_ERROR, "Invalid token " + getTokenName() + Constants.COLON + value
                + ". Expected OUTPUTSHEET:DIRECTORY|x or OUTPUTSHEET:DEFAULT.x|y " + " in " + source.toString());
        return false;

    }

    /**
     * Returns the name of the token this class handles.
     */
    @Override
    public String getTokenName()
    {
        return "OUTPUTSHEET"; //$NON-NLS-1$
    }

}
