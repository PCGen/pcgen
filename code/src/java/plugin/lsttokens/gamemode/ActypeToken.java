/*
 * ActypeToken.java
 * Copyright 2005 (C) Greg Bingleman <byngl@hotmail.com>
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
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.content.ACControl;
import pcgen.core.GameMode;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GameModeLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

/**
 * {@code ActypeToken}
 */
public class ActypeToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "ACTYPE";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        final StringTokenizer aTok = new StringTokenizer(value, "\t");

        if (!aTok.hasMoreTokens())
        {
            Logging.errorPrint("Empty tag in miscinfo.ACTYPE");

            return false;
        }

        final String acType = aTok.nextToken();

        while (aTok.hasMoreTokens())
        {
            final String aString = aTok.nextToken();

            if (aString.startsWith("ADD:"))
            {
                Collection<ACControl> controls = parseACControl(aString.substring(4));
                if (controls == null)
                {
                    return false;
                }
                gameMode.addACAdds(acType, controls);
            } else if (aString.startsWith("REMOVE:"))
            {
                Collection<ACControl> controls = parseACControl(aString.substring(7));
                if (controls == null)
                {
                    return false;
                }
                gameMode.addACRemoves(acType, controls);
            } else
            {
                Logging.errorPrint("Incorrect tag in miscinfo.ACTYPE: " + aString);
                return false;
            }
        }
        return true;
    }

    private Collection<ACControl> parseACControl(String str)
    {
        StringTokenizer st = new StringTokenizer(str, Constants.PIPE);
        List<ACControl> acTypes = new ArrayList<>();
        String token;
        while (true)
        {
            token = st.nextToken();
            if (PreParserFactory.isPreReqString(token))
            {
                break;
            }
            acTypes.add(new ACControl(token));
            if (!st.hasMoreTokens())
            {
                return acTypes;
            }
        }
        if (acTypes.isEmpty())
        {
            Logging.errorPrint("No types found in actype control: " + str);
            return null;
        }
        while (true)
        {
            try
            {
                PreParserFactory factory = PreParserFactory.getInstance();
                Prerequisite prereq = factory.parse(token);
                for (ACControl acc : acTypes)
                {
                    acc.addPrerequisite(prereq);
                }
            } catch (PersistenceLayerException ple)
            {
                Logging.errorPrint(ple.getMessage(), ple);
                return null;
            }
            if (!st.hasMoreTokens())
            {
                break;
            }
            token = st.nextToken();
            if (!PreParserFactory.isPreReqString(token))
            {
                Logging.errorPrint("ERROR: Type found after" + " PRExxx in actype control: " + str);
                return null;
            }
        }
        return acTypes;
    }

}
