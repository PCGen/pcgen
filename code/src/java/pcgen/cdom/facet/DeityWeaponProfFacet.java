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
package pcgen.cdom.facet;

import java.util.List;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.DeityFacet;
import pcgen.core.Deity;
import pcgen.core.WeaponProf;

/**
 * DeityWeaponProfFacet is a Facet that tracks the WeaponProfs that have been
 * granted to a Player Character via the Deity selection of the Player
 * Character.
 */
public class DeityWeaponProfFacet extends AbstractSourcedListFacet<CharID, WeaponProf>
        implements DataFacetChangeListener<CharID, Deity>
{

    private DeityFacet deityFacet;

    /**
     * Adds WeaponProfs granted to the Player Character due to the Deity
     * selection of the Player Character.
     * <p>
     * Triggered when one of the Facets to which DeityWeaponProfFacet listens
     * fires a DataFacetChangeEvent to indicate a Deity was added to a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, Deity> dfce)
    {
        Deity deity = dfce.getCDOMObject();
        List<CDOMReference<WeaponProf>> weaponList = deity.getListFor(ListKey.DEITYWEAPON);
        if (weaponList != null)
        {
            for (CDOMReference<WeaponProf> ref : weaponList)
            {
                for (WeaponProf wp : ref.getContainedObjects())
                {
                    /*
                     * CONSIDER This is an open question, IMHO - why is natural
                     * excluded here? This is magic to me - thpr Oct 14, 2008
                     */
                    if (!wp.isType("Natural"))
                    {
                        add(dfce.getCharID(), wp, dfce.getCDOMObject());
                    }
                }
            }
        }
    }

    /**
     * Removes WeaponProfs (previously) granted to the Player Character due to
     * the Deity selection when the Deity is unset.
     * <p>
     * Triggered when one of the Facets to which DeityWeaponProfFacet listens
     * fires a DataFacetChangeEvent to indicate a Deity was removed from a
     * Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, Deity> dfce)
    {
        removeAll(dfce.getCharID(), dfce.getCDOMObject());
    }

    public void setDeityFacet(DeityFacet deityFacet)
    {
        this.deityFacet = deityFacet;
    }

    /**
     * Initializes the connections for DeityWeaponProfFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the DeityWeaponProfFacet.
     */
    public void init()
    {
        deityFacet.addDataFacetChangeListener(this);
    }
}
