/*
 * Copyright (c) Thomas Parker, 2010.
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
 * AgeFacet stores the age of the Player Character.
 */
public class AgeFacet extends AbstractItemFacet<CharID, Integer> implements ItemFacet<CharID, Integer>
{

    /**
     * Returns the int value of the age of the Player Character identified by
     * the given CharID.
     * <p>
     * This method is convenient for wrapping the get(CharID) method to avoid a
     * null check if the age is not defined.
     *
     * @param id The CharID identifying the Player Character for which the age
     *           should be returned
     * @return The age of the Player Character identified by the given CharID;
     * zero if no age is defined
     */
    public int getAge(CharID id)
    {
        Integer age = get(id);
        return age == null ? 0 : age;
    }

    public void init()
    {
        OutputDB.register("age", this);
    }
}
