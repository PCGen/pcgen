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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import pcgen.base.util.CaseInsensitiveMap;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.CDOMObjectConsolidationFacet;
import pcgen.cdom.facet.base.AbstractStorageFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.core.FollowerOption;

/**
 * FollowerOptionFacet is a Facet that tracks the FollowerOptions that have been
 * granted to a Player Character.
 */
public class FollowerOptionFacet extends AbstractStorageFacet<CharID>
        implements DataFacetChangeListener<CharID, CDOMObject>
{
    private CDOMObjectConsolidationFacet consolidationFacet;

    /**
     * Adds the FollowerOption objects granted by CDOMObjects added to the
     * Player Character to this FollowerLimitFacet.
     * <p>
     * Triggered when one of the Facets to which FollowerOptionFacet listens
     * fires a DataFacetChangeEvent to indicate a FollowerOption was added to a
     * Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CDOMObject cdo = dfce.getCDOMObject();
        List<FollowerOption> lists = cdo.getListFor(ListKey.COMPANIONLIST);
        if (lists != null)
        {
            addAll(dfce.getCharID(), lists, cdo);
        }
    }

    /**
     * Removes the FollowerOption objects granted by CDOMObjects removed from
     * the Player Character from this FollowerLimitFacet.
     * <p>
     * Triggered when one of the Facets to which FollowerOptionFacet listens
     * fires a DataFacetChangeEvent to indicate a FollowerOption was removed
     * from a Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        removeAll(dfce.getCharID(), dfce.getCDOMObject());
    }

    private void addAll(CharID id, List<FollowerOption> list, CDOMObject cdo)
    {
        for (FollowerOption fo : list)
        {
            add(id, fo, cdo);
        }
    }

    private void add(CharID id, FollowerOption fo, CDOMObject cdo)
    {
        Objects.requireNonNull(fo, "Object to add may not be null");
        String name = fo.getListRef().getName();
        Map<FollowerOption, Set<CDOMObject>> foMap = getConstructingCachedMap(id, name);
        Set<CDOMObject> set = foMap.get(fo);
        if (set == null)
        {
            set = Collections.newSetFromMap(new IdentityHashMap<>());
            foMap.put(fo, set);
        }
        set.add(cdo);
    }

    private void removeAll(CharID id, CDOMObject source)
    {
        CaseInsensitiveMap<Map<FollowerOption, Set<CDOMObject>>> componentMap = getCachedMap(id);
        if (componentMap != null)
        {
            for (Iterator<Map<FollowerOption, Set<CDOMObject>>> it = componentMap.values().iterator();it.hasNext();)
            {
                Map<FollowerOption, Set<CDOMObject>> foMap = it.next();
                foMap.values().removeIf(set -> set.remove(source) && set.isEmpty());
                if (foMap.isEmpty())
                {
                    it.remove();
                }
            }
        }
    }

    /**
     * Returns the type-safe Map for this FollowerOptionFacet and the given
     * CharID. May return null if no information has been set in this
     * FollowerOptionFacet for the given CharID.
     * <p>
     * Note that this method SHOULD NOT be public. The Map is owned by
     * FollowerOptionFacet, and since it can be modified, a reference to that
     * object should not be exposed to any object other than
     * FollowerOptionFacet.
     *
     * @param id The CharID for which the Set should be returned
     * @return The Set for the Player Character represented by the given CharID;
     * null if no information has been set in this FollowerOptionFacet
     * for the Player Character
     */
    @SuppressWarnings("unchecked")
    private CaseInsensitiveMap<Map<FollowerOption, Set<CDOMObject>>> getCachedMap(CharID id)
    {
        return (CaseInsensitiveMap<Map<FollowerOption, Set<CDOMObject>>>) getCache(id);
    }

    /**
     * Returns a type-safe Map for this FollowerOptionFacet and the given
     * CharID. Will return a new, empty Map if no information has been set in
     * this FollowerOptionFacet for the given CharID. Will not return null.
     * <p>
     * Note that this method SHOULD NOT be public. The Map object is owned by
     * FollowerOptionFacet, and since it can be modified, a reference to that
     * object should not be exposed to any object other than
     * FollowerOptionFacet.
     *
     * @param id The CharID for which the Map should be returned
     * @return The Map for the Player Character represented by the given CharID
     */
    private Map<FollowerOption, Set<CDOMObject>> getConstructingCachedMap(CharID id, String name)
    {
        CaseInsensitiveMap<Map<FollowerOption, Set<CDOMObject>>> componentMap = getCachedMap(id);
        if (componentMap == null)
        {
            componentMap = new CaseInsensitiveMap<>();
            setCache(id, componentMap);
        }
        Map<FollowerOption, Set<CDOMObject>> foMap = componentMap.get(name);
        if (foMap == null)
        {
            foMap = new IdentityHashMap<>();
            componentMap.put(name, foMap);
        }
        return foMap;
    }

    /**
     * Returns a non-null copy of the available FollowerOptions of a given type
     * for the Player Character represented by the given CharID. This method
     * returns an empty Map if no objects are in this FollowerOptionFacet for
     * the Player Character identified by the given CharID.
     * <p>
     * This method is value-semantic in that ownership of the returned Map is
     * transferred to the class calling this method. Modification of the
     * returned Map will not modify this FollowerOptionFacet and modification of
     * this FollowerOptionFacet will not modify the returned Map. Modifications
     * to the returned Set will also not modify any future or previous objects
     * returned by this (or other) methods on FollowerOptionFacet. If you wish
     * to modify the information stored in this FollowerOptionFacet, you must
     * use the add*() and remove*() methods of FollowerOptionFacet.
     *
     * @param id   The CharID representing the Player Character for which the
     *             items in this FollowerOptionFacet should be returned
     * @param type The type of FollowerOption that should be returned
     * @param comp An optional Comparator to be used to sort the FollowerOption
     *             objects in the returned Map. null is a legal value, and will
     *             result in the FollowerOptions being sorted by their type
     * @return A non-null copy of the Map of FollowerOptions in this
     * FollowerOptionFacet for the Player Character represented by the
     * given CharID
     */
    public Map<FollowerOption, CDOMObject> getAvailableFollowers(CharID id, String type,
            Comparator<FollowerOption> comp)
    {
        CaseInsensitiveMap<Map<FollowerOption, Set<CDOMObject>>> componentMap = getCachedMap(id);
        if (componentMap == null)
        {
            return Collections.emptyMap();
        }
        Map<FollowerOption, Set<CDOMObject>> foMap = componentMap.get(type);
        if (foMap == null)
        {
            return Collections.emptyMap();
        }
        Map<FollowerOption, CDOMObject> ret = new TreeMap<>(comp);
        for (Map.Entry<FollowerOption, Set<CDOMObject>> me : foMap.entrySet())
        {
            FollowerOption fo = me.getKey();
            Set<CDOMObject> target = me.getValue();
            Collection<FollowerOption> expanded = fo.getExpandedOptions();
            for (CDOMObject source : target)
            {
                for (FollowerOption efo : expanded)
                {
                    /*
                     * TODO This is a bug, and will overwrite the first source
                     * :(
                     */
                    ret.put(efo, source);
                }
            }
        }
        return ret;
    }

    public void setConsolidationFacet(CDOMObjectConsolidationFacet consolidationFacet)
    {
        this.consolidationFacet = consolidationFacet;
    }

    /**
     * Initializes the connections for FollowerOptionFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the FollowerOptionFacet.
     */
    public void init()
    {
        consolidationFacet.addDataFacetChangeListener(this);
    }

    /**
     * Copies the contents of the FollowerOptionFacet from one Player Character
     * to another Player Character, based on the given CharIDs representing
     * those Player Characters.
     * <p>
     * This is a method in FollowerOptionFacet in order to avoid exposing the
     * mutable Map object to other classes. This should not be inlined, as the
     * Map is internal information to FollowerOptionFacet and should not be
     * exposed to other classes.
     * <p>
     * Note also the copy is a one-time event and no references are maintained
     * between the Player Characters represented by the given CharIDs (meaning
     * once this copy takes place, any change to the FollowerOptionFacet of one
     * Player Character will only impact the Player Character where the
     * FollowerOptionFacet was changed).
     *
     * @param source The CharID representing the Player Character from which the
     *               information should be copied
     * @param copy   The CharID representing the Player Character to which the
     *               information should be copied
     */
    @Override
    public void copyContents(CharID source, CharID copy)
    {
        CaseInsensitiveMap<Map<FollowerOption, Set<CDOMObject>>> map = getCachedMap(source);
        if (map != null)
        {
            for (Map<FollowerOption, Set<CDOMObject>> fm : map.values())
            {
                for (Map.Entry<FollowerOption, Set<CDOMObject>> fme : fm.entrySet())
                {
                    FollowerOption fl = fme.getKey();
                    for (CDOMObject cdo : fme.getValue())
                    {
                        add(copy, fl, cdo);
                    }
                }
            }
        }
    }

    public int getCount(CharID id)
    {
        CaseInsensitiveMap<Map<FollowerOption, Set<CDOMObject>>> map = getCachedMap(id);
        int count = 0;
        if (map != null)
        {
            for (Map<FollowerOption, Set<CDOMObject>> fm : map.values())
            {
                count += fm.size();
            }
        }
        return count;
    }
}
