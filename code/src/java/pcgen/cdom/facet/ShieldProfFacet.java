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
import pcgen.cdom.facet.model.ShieldProfProviderFacet;
import pcgen.cdom.helper.ShieldProfProvider;

/**
 * ShieldProfFacet is a Facet that tracks the ShieldProfs that have been granted
 * to a Player Character.
 */
public class ShieldProfFacet implements DataFacetChangeListener<CharID, CDOMObject>
{

    private ShieldProfProviderFacet shieldProfProviderFacet;

    private CDOMObjectConsolidationFacet consolidationFacet;

    /**
     * Processes added CDOMObjects to determine whether they contained an
     * AUTO:SHIELDPROF, and if so, processes the contents of that token to add
     * the appropriate ShieldProfProviders to the Player Character.
     * <p>
     * Triggered when one of the Facets to which ShieldProfFacet listens fires a
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
        List<ShieldProfProvider> shieldProfs = cdo.getListFor(ListKey.AUTO_SHIELDPROF);
        if (shieldProfs != null)
        {
            shieldProfProviderFacet.addAll(dfce.getCharID(), shieldProfs, cdo);
        }
    }

    /**
     * Processes removed CDOMObjects to determine whether they contained an
     * AUTO:SHIELDPROF, and if so, processes the contents of that token to
     * remove the appropriate ShieldProfProviders from the Player Character.
     * <p>
     * Triggered when one of the Facets to which ShieldProfFacet listens fires a
     * DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        shieldProfProviderFacet.removeAll(dfce.getCharID(), dfce.getCDOMObject());
    }

    public void setShieldProfProviderFacet(ShieldProfProviderFacet shieldProfProviderFacet)
    {
        this.shieldProfProviderFacet = shieldProfProviderFacet;
    }

    public void setConsolidationFacet(CDOMObjectConsolidationFacet consolidationFacet)
    {
        this.consolidationFacet = consolidationFacet;
    }

    /**
     * Initializes the connections for ShieldProfFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the ShieldProfFacet.
     */
    public void init()
    {
        consolidationFacet.addDataFacetChangeListener(this);
    }
}
