/*
 * Copyright 2006 (C) Tom Parker <thpr@users.sourceforge.net>
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
 *
 *
 */
package pcgen.core;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code XPTable}.
 */
@SuppressWarnings("serial")
public final class XPTable extends PObject
{
    private final String name;
    private final Map<String, LevelInfo> infoMap;

    public XPTable()
    {
        this.name = "Default";
        this.infoMap = new HashMap<>();
    }

    public XPTable(String xpTable)
    {
        this.name = xpTable;
        this.infoMap = new HashMap<>();
    }

    public String getName()
    {
        return name;
    }

    public void addLevelInfo(String level, LevelInfo levelInfo)
    {
        infoMap.put(level, levelInfo);
    }

    public LevelInfo getLevelInfo(String levelString)
    {
        return infoMap.get(levelString);
    }

    /**
     * Returns Level information for the given Level
     *
     * @param level the level for which Level Info should be returned
     * @return The LevelInfo for the given level
     */
    public LevelInfo getLevelInfo(int level)
    {
        if (level < 1)
        {
            return null;
        }
        LevelInfo lInfo = getLevelInfo(String.valueOf(level));

        if (lInfo == null)
        {
            lInfo = getLevelInfo("LEVEL");
        }
        return lInfo;
    }

    public boolean validateSequence(String levelValue)
    {
        int value = getIntValue(levelValue);
        return infoMap.values()
                .stream()
                .mapToInt(levelInfo -> getIntValue(levelInfo.getLevelString()))
                .noneMatch(intValue -> value < intValue);
    }

    private static int getIntValue(String level)
    {
        try
        {
            return Integer.parseInt(level);
        } catch (NumberFormatException e)
        {
            return 0;
        }
    }
}
