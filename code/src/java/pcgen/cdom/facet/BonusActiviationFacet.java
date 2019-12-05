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
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.CompanionModFacet;
import pcgen.cdom.facet.model.DeityFacet;
import pcgen.cdom.facet.model.DomainFacet;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.SkillFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PlayerCharacter;

/**
 * BonusActiviationFacet is a Facet that triggers to activate BonusObj objects
 * on CDOMObjects which are added to a Player Character.
 */
public class BonusActiviationFacet implements DataFacetChangeListener<CharID, CDOMObject>
{
    private final PlayerCharacterTrackingFacet trackingFacet =
            FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);

    private RaceFacet raceFacet;

    private CompanionModFacet companionModFacet;

    private DeityFacet deityFacet;

    private DomainFacet domainFacet;

    private SkillFacet skillFacet;

    private TemplateFacet templateFacet;

    /**
     * Activates BonusObj objects on the CDOMObject that was added to the Player
     * Character.
     * <p>
     * Triggered when one of the Facets to which BonusActivationFacet listens
     * fires a DataFacetChangeEvent to indicate a CDOMObject was added to a
     * Player Character.
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
            CDOMObject cdo = dfce.getCDOMObject();
            cdo.activateBonuses(aPC);
        }
    }

    /**
     * Triggered when one of the Facets to which BonusActivationFacet listens
     * fires a DataFacetChangeEvent to indicate a CDOMObject was removed from a
     * Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        /*
         * Nothing for now?
         *
         * TODO It is likely that the lack of symmetry here is incorrect. Need
         * to consider if this needs to call BonusActivation.deactivateBonuses
         */
    }

    public void setRaceFacet(RaceFacet raceFacet)
    {
        this.raceFacet = raceFacet;
    }

    public void setCompanionModFacet(CompanionModFacet companionModFacet)
    {
        this.companionModFacet = companionModFacet;
    }

    public void setDeityFacet(DeityFacet deityFacet)
    {
        this.deityFacet = deityFacet;
    }

    public void setDomainFacet(DomainFacet domainFacet)
    {
        this.domainFacet = domainFacet;
    }

    public void setSkillFacet(SkillFacet skillFacet)
    {
        this.skillFacet = skillFacet;
    }

    public void setTemplateFacet(TemplateFacet templateFacet)
    {
        this.templateFacet = templateFacet;
    }

    /**
     * Initializes the connections for BonusActivationFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the ActiveSpellsFacet.
     */
    public void init()
    {
        raceFacet.addDataFacetChangeListener(1000, this);
        companionModFacet.addDataFacetChangeListener(1000, this);
        deityFacet.addDataFacetChangeListener(1000, this);
        domainFacet.addDataFacetChangeListener(1000, this);
        skillFacet.addDataFacetChangeListener(1000, this);
        templateFacet.addDataFacetChangeListener(1000, this);
    }
}
