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
package pcgen.cdom.facet.analysis;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceType;
import pcgen.cdom.facet.model.CompanionModFacet;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.character.CompanionMod;

/**
 * RaceTypeFacet is a Facet that determines the RaceType of a Player Character
 */
public class RaceTypeFacet
{

    private TemplateFacet templateFacet;
    private RaceFacet raceFacet;
    private CompanionModFacet companionModFacet;

    /**
     * Returns the RaceType of the Player Character represented by the given
     * CharID.
     *
     * @param id The CharID representing the Player Character for which the
     *           RaceType will be returned
     * @return The RaceType of the Player Character represented by the given
     * CharID.
     */
    public RaceType getRaceType(CharID id)
    {
        RaceType raceType = null;
        Race race = raceFacet.get(id);
        if (race != null)
        {
            RaceType rt = race.get(ObjectKey.RACETYPE);
            if (rt != null)
            {
                raceType = rt;
            }
        }
        for (CompanionMod cm : companionModFacet.getSet(id))
        {
            RaceType rt = cm.get(ObjectKey.RACETYPE);
            if (rt != null)
            {
                raceType = rt;
            }
        }
        for (PCTemplate t : templateFacet.getSet(id))
        {
            RaceType rt = t.get(ObjectKey.RACETYPE);
            if (rt != null)
            {
                raceType = rt;
            }
        }
        return raceType;
    }

    public void setTemplateFacet(TemplateFacet templateFacet)
    {
        this.templateFacet = templateFacet;
    }

    public void setRaceFacet(RaceFacet raceFacet)
    {
        this.raceFacet = raceFacet;
    }

    public void setCompanionModFacet(CompanionModFacet companionModFacet)
    {
        this.companionModFacet = companionModFacet;
    }

}
