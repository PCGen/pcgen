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
package pcgen.cdom.facet.model;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.core.Campaign;

/**
 * ExpandedCampaignFacet is a Facet that tracks the Campaign objects that have
 * been loaded with a Player Character. This includes the explicitly loaded
 * Campaigns as well as the implicitly loaded campaigns called by the PCC: token
 * within the Campaign files.
 * <p>
 * Just the explicitly loaded Campaign objects are stored in CampaignFacet
 *
 * @see pcgen.cdom.facet.input.CampaignFacet
 */
public class ExpandedCampaignFacet extends AbstractSourcedListFacet<CharID, Campaign>
        implements DataFacetChangeListener<CharID, Campaign>
{

    /**
     * Expands Campaigns loaded with the Player Character to include the
     * subcampaigns. Stores a complete list of Campaigns (explicit and implicit)
     * loaded with the Player Character.
     * <p>
     * Triggered when one of the Facets to which ExpandedCampaignFacet listens
     * fires a DataFacetChangeEvent to indicate a Campaign loaded with a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, Campaign> dfce)
    {
        add(dfce.getCharID(), dfce.getCDOMObject(), dfce.getSource());
        addAll(dfce.getCharID(), dfce.getCDOMObject().getSubCampaigns(), dfce.getSource());
    }

    /**
     * Expands Campaigns loaded with the Player Character to include the
     * subcampaigns. Removes campaigns and subcampaigns (explicit and implicit)
     * no longer loaded with the Player Character.
     * <p>
     * Triggered when one of the Facets to which ExpandedCampaignFacet listens
     * fires a DataFacetChangeEvent to indicate a Campaign was unloaded.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, Campaign> dfce)
    {
        remove(dfce.getCharID(), dfce.getCDOMObject(), dfce.getSource());
        removeAll(dfce.getCharID(), dfce.getCDOMObject().getSubCampaigns(), dfce.getSource());
    }

}
