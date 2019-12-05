/*
 * Copyright (c) Thomas Parker, 2012
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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMObjectUtilities;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.ClassLevelFacet;
import pcgen.cdom.facet.model.DeityFacet;
import pcgen.cdom.facet.model.DomainFacet;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PlayerCharacter;

/**
 * AddFacet is a Facet that triggers when an object has an ADD token
 */
public class AddFacet implements DataFacetChangeListener<CharID, CDOMObject>
{
    private final PlayerCharacterTrackingFacet trackingFacet =
            FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);

    private RaceFacet raceFacet;

    private DeityFacet deityFacet;

    private TemplateFacet templateFacet;

    private DomainFacet domainFacet;

    private ClassLevelFacet classLevelFacet;

    /**
     * Drives the necessary selections for ADD tokens on a Player Character.
     * <p>
     * Triggered when one of the Facets to which AddFacet listens fires a
     * DataFacetChangeEvent to indicate a CDOMObject was added to a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CharID id = dfce.getCharID();
        PlayerCharacter aPC = trackingFacet.getPC(id);
        if (!aPC.isImporting())
        {
            CDOMObjectUtilities.addAdds(dfce.getCDOMObject(), aPC);
        }
    }

    /**
     * Triggered when one of the Facets to which AddFacet listens fires a
     * DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
     * Character.
     * <p>
     * Long term this method needs to be symmetric with dataAdded.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CharID id = dfce.getCharID();
        PlayerCharacter aPC = trackingFacet.getPC(id);
        if (!aPC.isImporting())
        {
            CDOMObjectUtilities.removeAdds(dfce.getCDOMObject(), aPC);
        }
    }

    public void setRaceFacet(RaceFacet raceFacet)
    {
        this.raceFacet = raceFacet;
    }

    public void setDeityFacet(DeityFacet deityFacet)
    {
        this.deityFacet = deityFacet;
    }

    public void setTemplateFacet(TemplateFacet templateFacet)
    {
        this.templateFacet = templateFacet;
    }

    public void setDomainFacet(DomainFacet domainFacet)
    {
        this.domainFacet = domainFacet;
    }

    public void setClassLevelFacet(ClassLevelFacet classLevelFacet)
    {
        this.classLevelFacet = classLevelFacet;
    }

    /**
     * Initializes the connections for AddFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the AddFacet.
     */
    public void init()
    {
        raceFacet.addDataFacetChangeListener(this);
        deityFacet.addDataFacetChangeListener(this);
        templateFacet.addDataFacetChangeListener(this);
        domainFacet.addDataFacetChangeListener(this);
        classLevelFacet.addDataFacetChangeListener(this);
    }
}
