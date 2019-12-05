/*
 * SkillToken.java
 * Copyright 2004 (C) James Dempsey <jdempsey@users.sourceforge.net>
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

import pcgen.core.PlayerCharacter;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.SkillToken;
import pcgen.util.Logging;

/**
 * {@code SkillLevelToken}  outputs the number of skills
 * the character obtained at the specified level. The format
 * for this tag is SKILLLEVEL.x.TOTAL
 */

// SKILLLEVEL
public class SkillLevelToken extends SkillToken
{
    /**
     * token name
     */
    public static final String TOKEN_NAME = "SKILLLEVEL";

    @Override
    public String getTokenName()
    {
        return TOKEN_NAME;
    }

    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        CharacterDisplay display = pc.getDisplay();
        SkillDetails details = buildSkillDetails(tokenSource);

        if (details.getPropertyCount() > 0 && "TOTAL".equals(details.getProperty(0)))
        {
            final int aLevelOffset;

            try
            {
                aLevelOffset = Integer.parseInt(details.getSkillId()) - 1;

                if ((aLevelOffset >= display.getLevelInfoSize()) || (aLevelOffset < 0))
                {
                    return "0";
                }

                final PCLevelInfo wLevelInfo = display.getLevelInfo(aLevelOffset);
                final int wOutput = wLevelInfo.getSkillPointsGained(pc);
                return Integer.toString(wOutput);
            } catch (NumberFormatException nfe)
            {
                Logging.errorPrint("Error replacing SKILLLEVEL." + tokenSource, nfe);

                return "";
            }
        }

        return "";
    }

}
