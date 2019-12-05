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
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.core.Equipment;

/**
 * NaturalEquipmentFacet is a Facet that tracks the Equipment that is
 * TYPE=Natural that has been granted to a Player Character.
 */
public class NaturalEquipmentFacet extends AbstractSourcedListFacet<CharID, Equipment>
        implements DataFacetChangeListener<CharID, Equipment>
{
    /**
     * If the Equipment is TYPE=Natural, adds a piece of Equipment to this facet
     * when it is added to a Player Character.
     * <p>
     * Triggered when one of the Facets to which NaturalEquipmentFacet listens
     * fires a DataFacetChangeEvent to indicate a piece of Equipment was added
     * to a Player Character. If the added Equipment is TYPE=Natural, then it
     * will be added to this NaturalEquipmentFacet.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, Equipment> dfce)
    {
        Equipment eq = dfce.getCDOMObject();
        if (eq.isNatural())
        {
            add(dfce.getCharID(), eq, dfce.getSource());
        }
    }

    /**
     * Removes a piece of Equipment to this facet when it is removed from a
     * Player Character.
     * <p>
     * Triggered when one of the Facets to which NaturalEquipmentFacet listens
     * fires a DataFacetChangeEvent to indicate a piece of Equipment was removed
     * from a Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, Equipment> dfce)
    {
        /*
         * Note that no check is made here to validate that the Equipment
         * removed was TYPE=Natural. This is safe because "false" removals will
         * not trigger false events. This is a performance trade-off, and
         * assumes that the cost of a false removal is less than testing for the
         * TYPE of a piece of Equipment (this is likely to be true at the moment
         * due to the ability of an EquipmentModifier to modify the TYPE of a
         * piece of Equipment (thus TYPE can be an expensive calculation)
         */
        remove(dfce.getCharID(), dfce.getCDOMObject(), dfce.getSource());
    }

}
