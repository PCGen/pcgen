/*
 * Copyright (c) Thomas Parker, 2010.
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

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.CDOMObjectSourceFacet;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.util.enumeration.Load;

/**
 * UnencumberedArmorFacet is a Facet that tracks the Load objects for
 * Unencumbered Armor that have been locked on a Player Character.
 */
public class UnencumberedArmorFacet extends AbstractSourcedListFacet<CharID, Load>
        implements DataFacetChangeListener<CharID, CDOMObject>
{
    private CDOMObjectSourceFacet cdomSourceFacet;

    /**
     * Stores in this facet the Load for any Unencumbered Armor Values granted
     * by a CDOMObject added to a Player Character.
     * <p>
     * Triggered when one of the Facets to which UnencumberedArmorFacet listens
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
        Load load = cdo.get(ObjectKey.UNENCUMBERED_ARMOR);
        if (load != null)
        {
            add(dfce.getCharID(), load, cdo);
        }
    }

    /**
     * Removes from this facet the Load for any Unencumbered Armor Values
     * granted by a CDOMObject removed from a Player Character.
     * <p>
     * Triggered when one of the Facets to which UnencumberedArmorFacet listens
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
     * Returns a new (empty) Map for this UnencumberedArmorFacet. This overrides
     * the default provided in AbstractSourcedListFacet, in order to maintain a
     * sorted list of Load objects related to Unencumbered Armor calculations
     * for the Player Character. This does not require the IdentityHashMap since
     * Load is not cloned, and behaves properly with .equals() and .hashCode()
     * in terms of maintaining identity (whereas many CDOMObjects do not as of
     * 5.16)
     * <p>
     * Note that this method should always be the only method used to construct
     * a Map for this UnencumberedArmorFacet. It is actually preferred to use
     * getConstructingCacheMap(CharID) in order to implicitly call this method.
     *
     * @return A new (empty) Map for use in this UnencumberedArmorFacet.
     */
    @Override
    protected Map<Load, Set<Object>> getComponentMap()
    {
        return new TreeMap<>();
    }

    /**
     * Returns the best Load value to avoid encumberance from Armor for the
     * Player Character identified by the given CharID.
     *
     * @param id The CharID identifying the Player Character
     * @return The best Load value to avoid encumberance from Armor for the
     * Player Character identified by the given CharID.
     */
    public Load getBestLoad(CharID id)
    {
        TreeMap<Load, Set<Object>> map = (TreeMap<Load, Set<Object>>) getCachedMap(id);
        if (map == null || map.isEmpty())
        {
            return Load.LIGHT;
        }
        return map.lastKey();
    }

    /**
     * Returns true if the Player Character identified by the given CharID can
     * ignore the given Load for purposes of armor encumberance.
     *
     * @param id   The CharID identifying the Player Character being tested
     * @param load The Load to check if the Player Character identified by the
     *             given CharID can ignore the given Load for purposes of armor
     *             encumberance
     * @return true if the Player Character identified by the given CharID can
     * ignore the given Load for purposes of armor encumberance; false
     * otherwise
     */
    public boolean ignoreLoad(CharID id, Load load)
    {
        return getBestLoad(id).compareTo(load) >= 0;
    }

    public void setCdomSourceFacet(CDOMObjectSourceFacet cdomSourceFacet)
    {
        this.cdomSourceFacet = cdomSourceFacet;
    }

    /**
     * Initializes the connections for UnencumberedArmorFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the UnencumberedArmorFacet.
     */
    public void init()
    {
        cdomSourceFacet.addDataFacetChangeListener(this);
    }
}
