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

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.BonusCheckingFacet;
import pcgen.cdom.facet.CDOMObjectConsolidationFacet;
import pcgen.cdom.facet.FormulaResolvingFacet;
import pcgen.cdom.facet.base.AbstractStorageFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.helper.FollowerLimit;
import pcgen.cdom.list.CompanionList;

/**
 * FollowerLimitFacet is a Facet that tracks the Follower Limits that have been
 * set for a Player Character.
 */
public class FollowerLimitFacet extends AbstractStorageFacet<CharID>
        implements DataFacetChangeListener<CharID, CDOMObject>
{
    private FormulaResolvingFacet formulaResolvingFacet;

    private BonusCheckingFacet bonusCheckingFacet;

    private CDOMObjectConsolidationFacet consolidationFacet;

    /**
     * Adds the FollowerLimit objects granted by CDOMObjects added to the Player
     * Character to this FollowerLimitFacet.
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
        List<FollowerLimit> followers = cdo.getListFor(ListKey.FOLLOWERS);
        if (followers != null)
        {
            addAll(dfce.getCharID(), followers, cdo);
        }
    }

    /**
     * Removes the FollowerLimit objects granted by CDOMObjects removed from the
     * Player Character from this FollowerLimitFacet.
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

    private void addAll(CharID id, List<FollowerLimit> list, CDOMObject cdo)
    {
        for (FollowerLimit fo : list)
        {
            add(id, fo, cdo);
        }
    }

    private void add(CharID id, FollowerLimit fo, CDOMObject cdo)
    {
        Objects.requireNonNull(fo, "Object to add may not be null");
        CompanionList cl = fo.getCompanionList().get();
        Map<FollowerLimit, Set<CDOMObject>> foMap = getConstructingCachedMap(id, cl);
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
        Map<CompanionList, Map<FollowerLimit, Set<CDOMObject>>> componentMap = getCachedMap(id);
        if (componentMap != null)
        {
            for (Iterator<Map<FollowerLimit, Set<CDOMObject>>> it = componentMap.values().iterator();it.hasNext();)
            {
                Map<FollowerLimit, Set<CDOMObject>> foMap = it.next();
                foMap.values().removeIf(set -> set.remove(source) && set.isEmpty());
                if (foMap.isEmpty())
                {
                    it.remove();
                }
            }
        }
    }

    /**
     * Returns the type-safe Map for this FollowerLimitFacet and the given
     * CharID. May return null if no information has been set in this
     * FollowerLimitFacet for the given CharID.
     * <p>
     * Note that this method SHOULD NOT be public. The Map is owned by
     * FollowerLimitFacet, and since it can be modified, a reference to that
     * object should not be exposed to any object other than FollowerLimitFacet.
     *
     * @param id The CharID for which the Set should be returned
     * @return The Set for the Player Character represented by the given CharID;
     * null if no information has been set in this FollowerLimitFacet
     * for the Player Character
     */
    @SuppressWarnings("unchecked")
    private Map<CompanionList, Map<FollowerLimit, Set<CDOMObject>>> getCachedMap(CharID id)
    {
        return (Map<CompanionList, Map<FollowerLimit, Set<CDOMObject>>>) getCache(id);
    }

    /**
     * Returns a type-safe Map for this FollowerLimitFacet and the given CharID.
     * Will return a new, empty Map if no information has been set in this
     * FollowerLimitFacet for the given CharID. Will not return null.
     * <p>
     * Note that this method SHOULD NOT be public. The Map object is owned by
     * FollowerLimitFacet, and since it can be modified, a reference to that
     * object should not be exposed to any object other than FollowerLimitFacet.
     *
     * @param id The CharID for which the Map should be returned
     * @return The Map for the Player Character represented by the given CharID
     */
    private Map<FollowerLimit, Set<CDOMObject>> getConstructingCachedMap(CharID id, CompanionList cl)
    {
        Map<CompanionList, Map<FollowerLimit, Set<CDOMObject>>> componentMap = getCachedMap(id);
        if (componentMap == null)
        {
            componentMap = new HashMap<>();
            setCache(id, componentMap);
        }
        Map<FollowerLimit, Set<CDOMObject>> foMap = componentMap.get(cl);
        if (foMap == null)
        {
            foMap = new IdentityHashMap<>();
            componentMap.put(cl, foMap);
        }
        return foMap;
    }

    /**
     * Returns the maximum number of Followers of a given CompanionList for the
     * Player Character identified by the given CharID.
     *
     * @param id The CharID identifying the Player Character for which the
     *           maximum number of Followers will be returned
     * @param cl The CompanionList for which the maximum number of Followers
     *           will be returned
     * @return The maximum number of Followers of a given CompanionList for the
     * Player Character identified by the given CharID.
     */
    public int getMaxFollowers(CharID id, CompanionList cl)
    {
        Map<CompanionList, Map<FollowerLimit, Set<CDOMObject>>> componentMap = getCachedMap(id);
        if (componentMap == null)
        {
            return -1;
        }
        Map<FollowerLimit, Set<CDOMObject>> foMap = componentMap.get(cl);
        if (foMap == null)
        {
            return -1;
        }
        int ret = -1;
        for (Map.Entry<FollowerLimit, Set<CDOMObject>> me : foMap.entrySet())
        {
            FollowerLimit fl = me.getKey();
            Set<CDOMObject> set = me.getValue();
            for (CDOMObject source : set)
            {
                int val = formulaResolvingFacet.resolve(id, fl.getValue(), source.getQualifiedKey()).intValue();
                ret = Math.max(ret, val);
            }
        }
        if (ret != -1)
        {
            ret += bonusCheckingFacet.getBonus(id, "FOLLOWERS", cl.getKeyName().toUpperCase());
        }
        return ret;
    }

    public void setFormulaResolvingFacet(FormulaResolvingFacet formulaResolvingFacet)
    {
        this.formulaResolvingFacet = formulaResolvingFacet;
    }

    public void setBonusCheckingFacet(BonusCheckingFacet bonusCheckingFacet)
    {
        this.bonusCheckingFacet = bonusCheckingFacet;
    }

    public void setConsolidationFacet(CDOMObjectConsolidationFacet consolidationFacet)
    {
        this.consolidationFacet = consolidationFacet;
    }

    /**
     * Initializes the connections for FollowerLimitFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the FollowerLimitFacet.
     */
    public void init()
    {
        consolidationFacet.addDataFacetChangeListener(this);
    }

    /**
     * Copies the contents of the FollowerLimitFacet from one Player Character
     * to another Player Character, based on the given CharIDs representing
     * those Player Characters.
     * <p>
     * This is a method in FollowerLimitFacet in order to avoid exposing the
     * mutable Map object to other classes. This should not be inlined, as the
     * Map is internal information to FollowerLimitFacet and should not be
     * exposed to other classes.
     * <p>
     * Note also the copy is a one-time event and no references are maintained
     * between the Player Characters represented by the given CharIDs (meaning
     * once this copy takes place, any change to the FollowerLimitFacet of one
     * Player Character will only impact the Player Character where the
     * FollowerLimitFacet was changed).
     *
     * @param source The CharID representing the Player Character from which the
     *               information should be copied
     * @param copy   The CharID representing the Player Character to which the
     *               information should be copied
     */
    @Override
    public void copyContents(CharID source, CharID copy)
    {
        Map<CompanionList, Map<FollowerLimit, Set<CDOMObject>>> map = getCachedMap(source);
        if (map != null)
        {
            for (Map<FollowerLimit, Set<CDOMObject>> fm : map.values())
            {
                for (Map.Entry<FollowerLimit, Set<CDOMObject>> fme : fm.entrySet())
                {
                    FollowerLimit fl = fme.getKey();
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
        Map<CompanionList, Map<FollowerLimit, Set<CDOMObject>>> map = getCachedMap(id);
        int count = 0;
        if (map != null)
        {
            for (Map<FollowerLimit, Set<CDOMObject>> fm : map.values())
            {
                count += fm.size();
            }
        }
        return count;
    }
}
