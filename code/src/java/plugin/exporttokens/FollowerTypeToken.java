/*
 * FollowerTypeToken.java
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
 */
package plugin.exporttokens;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.PlayerCharacter;
import pcgen.core.character.Follower;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.FollowerToken;
import pcgen.io.exporttoken.Token;
import pcgen.util.Logging;

/**
 * Deal with FOLLOWERTYPE Token
 */
public class FollowerTypeToken extends Token
{
    /**
     * Token Name
     */
    public static final String TOKENNAME = "FOLLOWERTYPE";

    @Override
    public String getTokenName()
    {
        return TOKENNAME;
    }

    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        // Handle FOLLOWERTYPE.<type>x.subtag stuff
        // New token syntax FOLLOWERTYPE.<type>.x instead of FOLLOWERTYPE.<type>x
        StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
        aTok.nextToken(); // FOLLOWERTYPE

        String typeString = aTok.nextToken();
        StringBuilder restString = new StringBuilder();
        int followerIndex = -1;

        if (aTok.hasMoreTokens())
        {
            String startString = aTok.nextToken();

            // When removing old token syntax, remove the catch code
            try
            {
                followerIndex = Integer.parseInt(startString);
            } catch (NumberFormatException exc)
            {
                // Error, not debug.  We want users to report
                // use of the deprecated syntax so we can fix
                // them as they are found.
                Logging.errorPrint("Old syntax FOLLOWERTYPEx will be replaced for FOLLOWERTYPE.x");

                restString.append(startString);

                int numCharToRemove = 0;

                for (int i = typeString.length() - 1;i > 0;i--)
                {
                    if ((typeString.charAt(i) >= '0') && (typeString.charAt(i) <= '9'))
                    {
                        followerIndex = Integer.parseInt(typeString.substring(i));
                        numCharToRemove++;
                    } else
                    {
                        i = 0;
                    }
                }

                if (numCharToRemove > 0)
                {
                    typeString = typeString.substring(0, typeString.length() - numCharToRemove);
                }
            }

            while (aTok.hasMoreTokens())
            {
                restString.append('.').append(aTok.nextToken());
            }

            if (restString.indexOf(".") == 0)
            {
                restString.deleteCharAt(0);
            }
        }

        String result = "";
        List<Follower> aList = getFollowersOfType(pc.getDisplay(), typeString);
        if (followerIndex > -1 && followerIndex < aList.size())
        {
            result = FollowerToken.getFollowerOutput(eh, restString.toString(), aList.get(followerIndex));
        }

        return result;
    }

    /**
     * Retrieve a list of followers of the desired type.
     *
     * @param display    The display for the target character
     * @param typeString The follower type being looked for
     * @return The list of qualifying followers.
     */
    private List<Follower> getFollowersOfType(CharacterDisplay display, String typeString)
    {
        List<Follower> aList = new ArrayList<>();
        for (Follower fol : display.getFollowerList())
        {
            if (fol.getType().getKeyName().equalsIgnoreCase(typeString))
            {
                aList.add(fol);
            }
        }
        return aList;
    }
}
