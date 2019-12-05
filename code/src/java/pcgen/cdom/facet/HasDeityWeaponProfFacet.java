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

import java.util.Collection;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.base.AbstractQualifiedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.core.QualifiedObject;

/**
 * HasDeityWeaponProfFacet is a Facet that tracks if the Deity Weapon Profs are
 * contained in the Player Character.
 */
public class HasDeityWeaponProfFacet extends AbstractQualifiedListFacet<QualifiedObject<Boolean>>
        implements DataFacetChangeListener<CharID, CDOMObject>
{

    private CDOMObjectConsolidationFacet consolidationFacet;

    /**
     * Adds the Deity Weapon Prof capability granted by CDOMObjects added to the
     * Player Character to this HasDeityWeaponProfFacet.
     * <p>
     * Triggered when one of the Facets to which HasDeityWeaponProfFacet listens
     * fires a DataFacetChangeEvent to indicate a CDOMObject was added to a
     * Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CDOMObject cdo = dfce.getCDOMObject();
        QualifiedObject<Boolean> hdw = cdo.get(ObjectKey.HAS_DEITY_WEAPONPROF);
        if (hdw != null)
        {
            add(dfce.getCharID(), hdw, cdo);
        }
    }

    /**
     * Removes the Deity Weapon Prof capability granted by CDOMObjects removed
     * from the Player Character from this HasDeityWeaponProfFacet.
     * <p>
     * Triggered when one of the Facets to which HasDeityWeaponProfFacet listens
     * fires a DataFacetChangeEvent to indicate a CDOMObject was removed from a
     * Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        removeAll(dfce.getCharID(), dfce.getCDOMObject());
    }

    /**
     * Returns true if the Player Character identified by the given CharID has
     * been granted Deity Weapon Profs.
     *
     * @param id The CharID identifying the Player Character to be checked if
     *           it has been granted Deity Weapon Profs
     * @return true if the Player Character identified by the given CharID has
     * been granted Deity Weapon Profs; false otherwise
     */
    public boolean hasDeityWeaponProf(CharID id)
    {
        Collection<QualifiedObject<Boolean>> set = getQualifiedSet(id);
        for (QualifiedObject<Boolean> qo : set)
        {
            if (qo.getRawObject())
            {
                return true;
            }
        }
        return false;
    }

    public void setConsolidationFacet(CDOMObjectConsolidationFacet consolidationFacet)
    {
        this.consolidationFacet = consolidationFacet;
    }

    /**
     * Initializes the connections for HasDeityWeaponProfFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the HasDeityWeaponProfFacet.
     */
    public void init()
    {
        consolidationFacet.addDataFacetChangeListener(this);
    }
}
