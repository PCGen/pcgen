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
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PCTemplate;
import pcgen.core.Race;

/**
 * LegsFacet is a Facet that tracks the number of Legs possessed by a Player
 * Character
 */
public class LegsFacet
{

    private TemplateFacet templateFacet;
    private RaceFacet raceFacet;

    /**
     * Returns the number of Legs possessed by the Player Character represented
     * by the given CharID
     *
     * @param id The CharID representing the Player Character for which the
     *           number of Legs will be returned
     * @return The number of Legs possessed by the Player Character represented
     * by the given CharID
     */
    public int getLegs(CharID id)
    {
        final Race aRace = raceFacet.get(id);
        int legs = 0;
        if (aRace != null)
        {
            legs = aRace.getSafe(IntegerKey.LEGS);
        }

        // Scan templates for any overrides
        for (PCTemplate template : templateFacet.getSet(id))
        {
            Integer l = template.get(IntegerKey.LEGS);
            if (l != null)
            {
                legs = l;
            }
        }
        return legs;
    }

    public void setTemplateFacet(TemplateFacet templateFacet)
    {
        this.templateFacet = templateFacet;
    }

    public void setRaceFacet(RaceFacet raceFacet)
    {
        this.raceFacet = raceFacet;
    }

}
