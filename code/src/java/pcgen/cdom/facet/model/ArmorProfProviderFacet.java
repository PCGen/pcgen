/*
 * Copyright (c) Thomas Parker, 2010-14.
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
import pcgen.cdom.facet.base.AbstractQualifiedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.helper.ProfProvider;
import pcgen.cdom.meta.CorePerspective;
import pcgen.cdom.meta.CorePerspectiveDB;
import pcgen.cdom.meta.FacetBehavior;
import pcgen.cdom.meta.PerspectiveLocation;
import pcgen.core.ArmorProf;
import pcgen.core.Equipment;

/**
 * ArmorProfFacet is a Facet that tracks the ArmorProf ProfProviders that have
 * been granted to a Player Character.
 * <p>
 * This is a required consolidation since ProfProviders that are directly
 * applied via AUTO:ARMORPROF are a distinct process from those that are
 * indirectly added via a %LIST within AUTO:ARMORPROF (and thus the result of a
 * CHOOSE). This facet consolidates those two sources into the complete list of
 * ArmorProf ProfProviders for a Player Character.
 */
public class ArmorProfProviderFacet extends AbstractQualifiedListFacet<ProfProvider<ArmorProf>>
        implements DataFacetChangeListener<CharID, ProfProvider<ArmorProf>>, PerspectiveLocation
{

    /**
     * Processes added ArmorProf ProfProviders to consolidate those objects into
     * one location.
     * <p>
     * Triggered when one of the Facets to which ArmorProfFacet listens fires a
     * DataFacetChangeEvent to indicate a CDOMObject was added to a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, ProfProvider<ArmorProf>> dfce)
    {
        add(dfce.getCharID(), dfce.getCDOMObject(), dfce.getSource());
    }

    /**
     * Processes added ArmorProf ProfProviders to consolidate those objects into
     * one location.
     * <p>
     * Triggered when one of the Facets to which ArmorProfFacet listens fires a
     * DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, ProfProvider<ArmorProf>> dfce)
    {
        remove(dfce.getCharID(), dfce.getCDOMObject(), dfce.getSource());
    }

    /**
     * Returns true if a Player Character is proficient with the given piece of
     * Armor; false otherwise.
     * <p>
     * While this method will accept any Equipment, it is only guaranteed to
     * have "good behavior" for Armor. All other equipment will - at least -
     * return false. No guarantee is made that this method will not throw an
     * exception if the given Equipment is not Armor.
     *
     * @param id The CharID identifying the Player Character for which the
     *           proficiency will be tested.
     * @param eq The Armor (as an Equipment object) for which the proficiency
     *           is being tested.
     * @return true if a Player Character is proficient with the given piece of
     * Armor; false otherwise.
     */
    public boolean isProficientWithArmor(CharID id, Equipment eq)
    {
        for (ProfProvider<ArmorProf> pp : getQualifiedSet(id))
        {
            if (pp.providesProficiencyFor(eq))
            {
                return true;
            }
        }
        return false;
    }

    public void init()
    {
        CorePerspectiveDB.register(CorePerspective.ARMORPROF, FacetBehavior.MODEL, this);
    }

    @Override
    public String getIdentity()
    {
        return "Armor Proficiencies";
    }
}
