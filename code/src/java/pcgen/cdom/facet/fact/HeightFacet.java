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
package pcgen.cdom.facet.fact;

import pcgen.cdom.base.ItemFacet;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractItemFacet;
import pcgen.output.publish.OutputDB;

/**
 * HeightFacet is a Facet that tracks the Player Character's height.
 */
public class HeightFacet extends AbstractItemFacet<CharID, Integer> implements ItemFacet<CharID, Integer>
{
    /**
     * Returns the height for the Player Character represented by the given
     * CharID.
     *
     * @param id The CharID of the Player Character for which the height will
     *           be returned
     * @return The height of the Player Character represented by the given
     * CharID
     */
    public int getHeight(CharID id)
    {
        Integer height = get(id);
        return (height == null) ? 0 : height;
    }

    /**
     * This method is automatically called by the Spring framework during
     * initialization of the HeightFacet.
     */
    public void init()
    {
        OutputDB.register("height", this);
    }
}
