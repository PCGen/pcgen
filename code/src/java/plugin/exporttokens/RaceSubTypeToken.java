/*
 * RaceSubTypeToken.java
 * Copyright 2005 (C) Aaron Divinsky <boomer70@yahoo.com>
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

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbstractExportToken;

//RACESUBTYPE.x
public class RaceSubTypeToken extends AbstractExportToken
{
    @Override
    public String getTokenName()
    {
        return "RACESUBTYPE";
    }

    @Override
    public String getToken(String tokenSource, CharacterDisplay display, ExportHandler eh)
    {
        StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
        aTok.nextToken();

        int i = 0;
        if (aTok.hasMoreTokens())
        {
            try
            {
                i = Integer.parseInt(aTok.nextToken());
            } catch (NumberFormatException notUsed)
            {
                // This is an error. We will return the first item
            }
        }

        RaceSubType rst = getRaceSubTypeToken(display, i);
        return rst == null ? Constants.EMPTY_STRING : rst.toString();
    }

    private static RaceSubType getRaceSubTypeToken(CharacterDisplay display, int index)
    {
        //CONSIDER Why is this necessary to protect the index?  Calling code should be more careful
        List<RaceSubType> subTypes = new ArrayList<>(display.getRacialSubTypes());
        if (index >= 0 && index < subTypes.size())
        {
            return subTypes.get(index);
        }
        return null;
    }
}
