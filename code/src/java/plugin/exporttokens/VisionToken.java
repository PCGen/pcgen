/*
 * VisionToken.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.core.Vision;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbstractExportToken;

/**
 * {@code VisionToken} produces the output for the output token
 * VISION.
 */
public class VisionToken extends AbstractExportToken
{
    @Override
    public String getTokenName()
    {
        return "VISION";
    }

    @Override
    public String getToken(String tokenSource, CharacterDisplay display, ExportHandler eh)
    {
        StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
        aTok.nextToken();

        List<Vision> visionList = new ArrayList<>(display.getVisionList());

        int visionIndex = 0;
        int startIndex = 0;

        if (aTok.hasMoreTokens())
        {
            try
            {
                startIndex = Integer.parseInt(aTok.nextToken());
                visionIndex = startIndex + 1;
            } catch (NumberFormatException e)
            {
                //TODO: Should this really be ignored?
            }
        } else
        {
            visionIndex = visionList.size();
        }

        if (visionList.isEmpty() || startIndex >= visionList.size())
        {
            return "";
        }

        List<Vision> subList = visionList.subList(Math.max(startIndex, 0), Math.min(visionIndex, visionList.size()));

        StringBuilder result = new StringBuilder();
        for (Vision vision : subList)
        {
            if (result.length() > 0)
            {
                result.append(", ");
            }
            result.append(vision.getType());
            String distStr = vision.getDistance().toString();
            int dist = 0;
            if ((distStr != null) && (!distStr.trim().isEmpty()))
            {
                dist = Integer.parseInt(distStr);
            }
            if (dist > 0)
            {
                result.append(" (");
                result.append(Globals.getGameModeUnitSet().displayDistanceInUnitSet(dist));
                result.append(Globals.getGameModeUnitSet().getDistanceUnit());
                result.append(')');
            }
        }

        return result.toString();
    }
}
