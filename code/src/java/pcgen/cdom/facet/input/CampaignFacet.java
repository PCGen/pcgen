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
package pcgen.cdom.facet.input;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractListFacet;
import pcgen.cdom.facet.model.ExpandedCampaignFacet;
import pcgen.core.Campaign;

/**
 * CampaignFacet is a Facet that tracks the Campaigns loaded and thus active on
 * a Player Character. This tracks the list of Campaigns selected by the user.
 * This list must be expanded if any of the Campaigns includes another Campaign
 * (PCC file).
 */
public class CampaignFacet extends AbstractListFacet<CharID, Campaign>
{
    private ExpandedCampaignFacet expandedCampaignFacet;

    public void setExpandedCampaignFacet(ExpandedCampaignFacet expandedCampaignFacet)
    {
        this.expandedCampaignFacet = expandedCampaignFacet;
    }

    public void init()
    {
        addDataFacetChangeListener(expandedCampaignFacet);
    }
}
