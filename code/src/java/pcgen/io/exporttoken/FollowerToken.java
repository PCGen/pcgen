/*
 * Copyright 2006 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 *
 *
 *
 */
package pcgen.io.exporttoken;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.character.Follower;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.util.Logging;

/**
 * Deal with FOLLOWER Token
 */
public class FollowerToken extends Token
{
    /**
     * Token Name
     */
    public static final String TOKENNAME = "FOLLOWER";

    @Override
    public String getTokenName()
    {
        return TOKENNAME;
    }

    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        /* FOLLOWER%.subtag stuff handled in here*/

        // New token syntax FOLLOWER.x instead of FOLLOWERx
        StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
        String fString = aTok.nextToken(); // FOLLOWER
        final int i;

        if ("FOLLOWER".equals(fString))
        {
            i = Integer.parseInt(aTok.nextToken());
        } else
        {
            Logging.errorPrint("Old syntax FOLLOWERx will be replaced for FOLLOWER.x");

            i = Integer.parseInt(tokenSource.substring(8, tokenSource.indexOf('.')));
        }

        StringBuilder restString = new StringBuilder();
        while (aTok.hasMoreTokens())
        {
            restString.append(".").append(aTok.nextToken());
        }
        if (restString.indexOf(".") == 0)
        {
            restString = restString.deleteCharAt(0);
        }

        String result = "";
        final List<Follower> followers = new ArrayList<>(pc.getDisplay().getFollowerList());
        if (i < followers.size())
        {
            result = FollowerToken.getFollowerOutput(eh, restString.toString(), followers.get(i));
        }

        return result;
    }

    /**
     * Process a token for a follower (must already be loaded) and return the output.
     *
     * @param eh            The ExportHandler being used for output.
     * @param followerToken The token to be processed.
     * @param follower      The follower to be reported upon.
     * @return The follower's token output
     */
    public static String getFollowerOutput(ExportHandler eh, String followerToken, final Follower follower)
    {
        StringWriter writer = new StringWriter();
        BufferedWriter bw = new BufferedWriter(writer);

        String token = followerToken != null && followerToken.isEmpty() ? "NAME" : followerToken;

        for (PlayerCharacter eachPC : Globals.getPCList())
        {
            CharacterDisplay eachDisplay = eachPC.getDisplay();
            if (follower.getFileName().equals(eachDisplay.getFileName())
                    && follower.getName().equals(eachDisplay.getName()))
            {
                PlayerCharacter newPC = eachPC;
                eh.replaceToken(token, bw, newPC);
            }
        }
        try
        {
            bw.flush();
        } catch (IOException e)
        {
            Logging.errorPrint("Ignoring error while processing FOLLOWER or FOLLOWERTYPE token", e);
        }
        return writer.toString();
    }

}
