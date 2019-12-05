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
 * HandsFacet is a Facet that tracks the number of Hands possessed by a Player
 * Character.
 *
 * @deprecated due to HANDS CodeControl
 */
@Deprecated
public class HandsFacet
{

    private TemplateFacet templateFacet;
    private RaceFacet raceFacet;

    /**
     * Returns the number of Hands possessed by the Player Character represented
     * by the given CharID
     *
     * @param id The CharID representing the Player Character for which the
     *           number of Hands will be returned
     * @return The number of Hands possessed by the Player Character represented
     * by the given CharID
     */
    public int getHands(CharID id)
    {
        final Race aRace = raceFacet.get(id);
        int hands = 0;
        if (aRace != null)
        {
            hands = aRace.getSafe(IntegerKey.CREATURE_HANDS);
        }

        // Scan templates for any overrides
        for (PCTemplate template : templateFacet.getSet(id))
        {
            Integer h = template.get(IntegerKey.CREATURE_HANDS);
            if (h != null)
            {
                hands = h;
            }
        }
        return hands;
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
