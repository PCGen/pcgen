/*
 * DefaultdatasetToken.java
 * Copyright 2008 (C) James Dempsey
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
import java.util.StringTokenizer;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * The Class {@code DefaultdatasetToken} is responsible for parsing
 * the value of the DEFAULTDATASET tag and processing it against the game mode..
 */
public class DefaultdatasetToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "DEFAULTDATASET";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        String[] tokens = value.split("\\|");

        final StringTokenizer aTok = new StringTokenizer(tokens[0], ",", false);
        gameMode.clearDefaultDataSetList();

        while (aTok.hasMoreTokens())
        {
            final String aString = aTok.nextToken();

            gameMode.addDefaultDataSet(aString);
        }
        if (tokens.length > 1)
        {
            gameMode.setDefaultSourceTitle(tokens[1]);
        }
        return true;
    }
}
