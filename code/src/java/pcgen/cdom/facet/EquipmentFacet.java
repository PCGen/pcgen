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

import pcgen.cdom.base.SetFacet;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.core.Equipment;
import pcgen.output.publish.OutputDB;

/**
 * EquipmentFacet is a Facet that tracks the Equipment possessed by a Player
 * Character. Possessed in this case does not mean active, it could be carried
 * or owned. Therefore, items granted or allowed by the Equipment in this facet
 * are not part of the Player Character (the equipment must be equipped first).
 */
public class EquipmentFacet extends AbstractSourcedListFacet<CharID, Equipment>
        implements DataFacetChangeListener<CharID, Equipment>, SetFacet<CharID, Equipment>
{
    /**
     * Adds all of the Equipment that is possessed by a Player Character.
     * <p>
     * Triggered when one of the Facets to which EquipmentFacet listens fires a
     * DataFacetChangeEvent to indicate Equipment was added to a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, Equipment> dfce)
    {
        add(dfce.getCharID(), dfce.getCDOMObject(), dfce.getSource());
    }

    /**
     * Removes all of the Equipment that is no longer possessed by a Player
     * Character.
     * <p>
     * Triggered when one of the Facets to which EquipmentFacet listens fires a
     * DataFacetChangeEvent to indicate Equipment was removed from a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, Equipment> dfce)
    {
        remove(dfce.getCharID(), dfce.getCDOMObject(), dfce.getSource());
    }

    public void init()
    {
        OutputDB.register("equipment.all", this);
    }
}
