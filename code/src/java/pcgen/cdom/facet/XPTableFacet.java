/*
 * Copyright (c) Thomas Parker, 2009.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractItemFacet;
import pcgen.core.LevelInfo;
import pcgen.core.XPTable;

/**
 * XPTableFacet is a Facet that tracks the XP table assigned to a Player
 * Character.
 */
public class XPTableFacet extends AbstractItemFacet<CharID, XPTable>
{

    /**
     * Returns Level information for the given Level
     *
     * @param level the level for which Level Info should be returned
     * @return The LevelInfo for the given level
     */
    public LevelInfo getLevelInfo(CharID id, int level)
    {
        if (level < 1)
        {
            return null;
        }
        XPTable table = get(id);

        if (table == null)
        {
            return null;
        }
        LevelInfo lInfo = table.getLevelInfo(String.valueOf(level));

        if (lInfo == null)
        {
            lInfo = table.getLevelInfo("LEVEL");
        }
        return lInfo;
    }
}
