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

import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.ArmorProfProviderFacet;
import pcgen.cdom.helper.ArmorProfProvider;

/**
 * ArmorProfFacet is a Facet that tracks the ArmorProfs that have been granted
 * to a Player Character by looking for AUTO:ARMORPROF entries on CDOMObjects
 * added to/removed from the Player Character.
 */
public class ArmorProfFacet implements DataFacetChangeListener<CharID, CDOMObject>
{

    private ArmorProfProviderFacet armorProfProviderFacet;

    private CDOMObjectConsolidationFacet consolidationFacet;

    /**
     * Processes added CDOMObjects to determine whether they contained an
     * AUTO:ARMORPROF, and if so, processes the contents of that token to add
     * the appropriate ArmorProfProviders to the Player Character.
     * <p>
     * Triggered when one of the Facets to which ArmorProfFacet listens fires a
     * DataFacetChangeEvent to indicate a CDOMObject was added to a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CDOMObject cdo = dfce.getCDOMObject();
        List<ArmorProfProvider> armorProfs = cdo.getListFor(ListKey.AUTO_ARMORPROF);
        if (armorProfs != null)
        {
            armorProfProviderFacet.addAll(dfce.getCharID(), armorProfs, cdo);
        }
    }

    /**
     * Processes removed CDOMObjects to determine whether they contained an
     * AUTO:ARMORPROF, and if so, processes the contents of that token to remove
     * the appropriate ArmorProfProviders from the Player Character.
     * <p>
     * Triggered when one of the Facets to which ArmorProfFacet listens fires a
     * DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        armorProfProviderFacet.removeAll(dfce.getCharID(), dfce.getCDOMObject());
    }

    public void setArmorProfProviderFacet(ArmorProfProviderFacet armorProfProviderFacet)
    {
        this.armorProfProviderFacet = armorProfProviderFacet;
    }

    public void setConsolidationFacet(CDOMObjectConsolidationFacet consolidationFacet)
    {
        this.consolidationFacet = consolidationFacet;
    }

    /**
     * Initializes the connections for ArmorProfFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the ArmorProfFacet.
     */
    public void init()
    {
        consolidationFacet.addDataFacetChangeListener(this);
    }
}
