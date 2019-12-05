/*
 * Copyright (c) Thomas Parker, 2010-2012.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractListFacet;
import pcgen.core.pclevelinfo.PCLevelInfo;

/**
 * LevelInfoFacet stores the PCLevelInfo objects contained in a Player
 * Character. These store information about a specific Level (such as stat
 * increases)
 */
public class LevelInfoFacet extends AbstractListFacet<CharID, PCLevelInfo>
{

    /**
     * Overrides the default behavior of AbstractListFacet, since we need to
     * ensure we are storing the PCLevelInfo objects in an ordered list (since
     * we are implicitly storing the level of the PCLevelInfo by its location)
     */
    @Override
    protected Collection<PCLevelInfo> getComponentSet()
    {
        return new ArrayList<>();
    }

    /**
     * Returns the PCLevelInfo in this LevelInfoFacet for the Player Character
     * represented by the given CharID and the given location in the list of
     * items (list index starts at zero)
     *
     * @param id       The CharID representing the Player Character for which the
     *                 specified item in this LevelInfoFacet should be returned.
     * @param location The location of the item in this LevelInfoFacet to be returned
     * @return The object in this LevelInfoFacet for the Player Character
     * represented by the given CharID and location.
     */
    public PCLevelInfo get(CharID id, int location)
    {
        List<PCLevelInfo> componentSet = (List<PCLevelInfo>) getCachedSet(id);
        if (componentSet == null || location < 0 || location >= componentSet.size())
        {
            return null;
        }
        return componentSet.get(location);
    }

}
