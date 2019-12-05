/*
 * Copyright (c) 2016 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.facet.input;

import pcgen.cdom.base.Category;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractScopeFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.inst.Dynamic;

/**
 * DynamicFacet is a Facet that tracks all Dynamic Objects that have been
 * granted to a Player Character.
 */
public class DynamicFacet extends AbstractScopeFacet<CharID, Category<Dynamic>, Dynamic>
        implements DataFacetChangeListener<CharID, Dynamic>
{
    /**
     * Adds the active Dynamic to this facet.
     * <p>
     * Triggered when one of the Facets to which DynamicFacet listens fires a
     * DataFacetChangeEvent to indicate Dynamic was added to a Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, Dynamic> dfce)
    {
        Dynamic cdo = dfce.getCDOMObject();
        add(dfce.getCharID(), cdo.getCDOMCategory(), cdo, dfce.getSource());
    }

    /**
     * Removes the no-longer active Dynamic from this facet.
     * <p>
     * Triggered when one of the Facets to which DynamicFacet listens fires a
     * DataFacetChangeEvent to indicate Dynamic was removed from a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, Dynamic> dfce)
    {
        Dynamic cdo = dfce.getCDOMObject();
        remove(dfce.getCharID(), cdo.getCDOMCategory(), cdo, dfce.getSource());
    }
}
