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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PCTemplate;
import pcgen.core.Race;

/**
 * RacialSubTypesFacet is a Facet that tracks the Racial Sub Types of a
 * PlayerCharacter
 */
public class RacialSubTypesFacet
{
    private TemplateFacet templateFacet;
    private RaceFacet raceFacet;

    /**
     * Returns a non-null Collection of the Racial Sub Types for the Player
     * Character represented by the given CharID.
     * <p>
     * This method is value-semantic in that ownership of the returned
     * Collection is transferred to the class calling this method. Modification
     * of the returned Collection will not modify this RacialSubTypesFacet and
     * modification of this RacialSubTypesFacet will not modify the returned
     * Collection. Modifications to the returned Collection will also not modify
     * any future or previous objects returned by this (or other) methods on
     * RacialSubTypesFacet.
     *
     * @param id The CharID representing the Player Character for which the
     *           Racial Sub Types should be returned
     * @return A non-null Collection of the Racial Sub Types for the Player
     * Character represented by the given CharID
     */
    public Collection<RaceSubType> getRacialSubTypes(CharID id)
    {
        List<RaceSubType> racialSubTypes = new ArrayList<>();
        Race race = raceFacet.get(id);
        if (race != null)
        {
            racialSubTypes.addAll(race.getSafeListFor(ListKey.RACESUBTYPE));
        }
        Collection<PCTemplate> templates = templateFacet.getSet(id);
        if (!templates.isEmpty())
        {
            List<RaceSubType> added = new ArrayList<>();
            List<RaceSubType> removed = new ArrayList<>();
            for (PCTemplate aTemplate : templates)
            {
                added.addAll(aTemplate.getSafeListFor(ListKey.RACESUBTYPE));
                removed.addAll(aTemplate.getSafeListFor(ListKey.REMOVED_RACESUBTYPE));
            }
            racialSubTypes.addAll(added);
            racialSubTypes.removeAll(removed);
        }

        return Collections.unmodifiableList(racialSubTypes);
    }

    /**
     * Returns true if this RacialSubTypesFacet contains the given RaceSubType
     * for the Player Character represented by the given CharID.
     *
     * @param id      The CharID representing the Player Character used for testing
     * @param subType The object to test if this RacialSubTypesFacet contains that
     *                RaceSubType for the Player Character represented by the given
     *                CharID
     * @return true if this RacialSubTypesFacet contains the given RaceSubType
     * for the Player Character represented by the given CharID; false
     * otherwise
     */
    public boolean contains(CharID id, RaceSubType subType)
    {
        return getRacialSubTypes(id).contains(subType);
    }

    /**
     * Returns the count of RaceSubTypes in this RacialSubTypesFacet for the
     * Player Character represented by the given CharID
     *
     * @param id The CharID representing the Player Character for which the
     *           count of RaceSubTypes should be returned
     * @return The count of RaceSubTypes in this RacialSubTypesFacet for the
     * Player Character represented by the given CharID
     */
    public int getCount(CharID id)
    {
        return getRacialSubTypes(id).size();
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
