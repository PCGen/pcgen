/*
 * Copyright 2006 (C) Devon Jones <soulcatcher@evilsoft.org>
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
package plugin.lsttokens.level;

import pcgen.core.LevelInfo;
import pcgen.persistence.lst.LevelLstToken;
import pcgen.util.Logging;

/**
 * {@code LevelToken} parses the LEVEL tag for the game mode
 * file level.lst.
 */
public class LevelToken implements LevelLstToken
{

    @Override
    public String getTokenName()
    {
        return "LEVEL";
    }

    @Override
    public boolean parse(LevelInfo levelInfo, String value)
    {
        if (!value.equals("LEVEL"))
        {
            try
            {
                Integer.parseInt(value);
            } catch (NumberFormatException e)
            {
                Logging.errorPrint(
                        "Invalid " + getTokenName() + " value: '" + value + "'. Value must be either LEVEL or a number.");
                return false;
            }
        }
        levelInfo.setLevelString(value);
        return true;
    }
}
