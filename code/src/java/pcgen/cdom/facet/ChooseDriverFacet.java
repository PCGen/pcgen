/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.event.ScopeFacetChangeEvent;
import pcgen.cdom.facet.event.ScopeFacetChangeListener;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.ChooseActivation;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.util.Logging;

/**
 * ChooseDriverFacet is a Facet that drives the application of a CHOOSE on a
 * CDOMObject.
 */
public class ChooseDriverFacet
{

    private final PlayerCharacterTrackingFacet trackingFacet =
            FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);

    private RaceSelectionFacet raceSelectionFacet;

    private DomainSelectionFacet domainSelectionFacet;

    private TemplateSelectionFacet templateSelectionFacet;

    private Adder adder = new Adder();
    private Remover remover = new Remover();

    private class Adder implements ScopeFacetChangeListener<CharID, CDOMObject, Object>
    {
        @Override
        public void dataAdded(ScopeFacetChangeEvent<CharID, CDOMObject, Object> dfce)
        {
            PlayerCharacter pc = trackingFacet.getPC(dfce.getCharID());
            if (!pc.isAllowInteraction())
            {
                return;
            }
            CDOMObject obj = dfce.getScope();
            Object sel = dfce.getCDOMObject();
            if (obj instanceof ChooseDriver)
            {
                if (ChooseActivation.hasNewChooseToken(obj))
                {
                    ChooseDriver cd = (ChooseDriver) obj;
                    addAssoc(ChooserUtilities.getChoiceManager(cd, pc), pc, cd, sel);
                }
            } else
            {
                Logging.errorPrint("Object of type " + obj.getClass()
                        + " was sent to ChooseDriverFacet, but it is not a ChooseDriver");
            }
        }

        private <T> void addAssoc(ChoiceManagerList<T> aMan, PlayerCharacter pc, ChooseDriver obj, T sel)
        {
            aMan.applyChoice(pc, obj, sel);
        }

        @Override
        public void dataRemoved(ScopeFacetChangeEvent<CharID, CDOMObject, Object> dfce)
        {
            //ignore
        }
    }

    private class Remover implements ScopeFacetChangeListener<CharID, CDOMObject, Object>
    {
        @Override
        public void dataAdded(ScopeFacetChangeEvent<CharID, CDOMObject, Object> dfce)
        {
            //ignore
        }

        @Override
        public void dataRemoved(ScopeFacetChangeEvent<CharID, CDOMObject, Object> dfce)
        {
            PlayerCharacter pc = trackingFacet.getPC(dfce.getCharID());
            if (!pc.isAllowInteraction())
            {
                return;
            }
            Object assoc = dfce.getCDOMObject();
            CDOMObject cdo = dfce.getScope();
            if (cdo instanceof ChooseDriver)
            {
                if (ChooseActivation.hasNewChooseToken(cdo))
                {
                    ChooseDriver cd = (ChooseDriver) cdo;
                    removeAssoc(ChooserUtilities.getChoiceManager(cd, pc), pc, cd, assoc);
                }
            } else
            {
                Logging.errorPrint("Object of type " + cdo.getClass()
                        + " was sent to ChooseDriverFacet, but it is not a ChooseDriver");
            }
        }

        private <T> void removeAssoc(ChoiceManagerList<T> aMan, PlayerCharacter pc, ChooseDriver obj, T sel)
        {
            aMan.removeChoice(pc, obj, sel);
        }

    }

    public void setDomainSelectionFacet(DomainSelectionFacet domainSelectionFacet)
    {
        this.domainSelectionFacet = domainSelectionFacet;
    }

    public void setRaceSelectionFacet(RaceSelectionFacet raceSelectionFacet)
    {
        this.raceSelectionFacet = raceSelectionFacet;
    }

    public void setTemplateSelectionFacet(TemplateSelectionFacet templateSelectionFacet)
    {
        this.templateSelectionFacet = templateSelectionFacet;
    }

    public void init()
    {
        raceSelectionFacet.addScopeFacetChangeListener(1000, adder);
        domainSelectionFacet.addScopeFacetChangeListener(1000, adder);
        templateSelectionFacet.addScopeFacetChangeListener(1000, adder);
        raceSelectionFacet.addScopeFacetChangeListener(-1000, remover);
        domainSelectionFacet.addScopeFacetChangeListener(-1000, remover);
        templateSelectionFacet.addScopeFacetChangeListener(-1000, remover);
    }
}
