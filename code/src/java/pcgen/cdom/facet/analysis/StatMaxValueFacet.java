/*
 * Copyright (c) James Dempsey, 2013.
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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.CDOMObjectConsolidationFacet;
import pcgen.cdom.facet.FormulaResolvingFacet;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.helper.StatLock;
import pcgen.core.PCStat;

/**
 * StatMaxValueFacet  is a Facet that tracks the Stats that have had a maximum
 * value set on a Player Character.
 */
public class StatMaxValueFacet extends AbstractSourcedListFacet<CharID, StatLock>
        implements DataFacetChangeListener<CharID, CDOMObject>
{
    private FormulaResolvingFacet formulaResolvingFacet;

    private CDOMObjectConsolidationFacet consolidationFacet;

    /**
     * Adds max value StatLock objects granted by a CDOMObject which has been
     * added to a Player Character.
     * <p>
     * Triggered when one of the Facets to which StatLockFacet listens fires a
     * DataFacetChangeEvent to indicate a CDOMObject was added to a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CDOMObject cdo = dfce.getCDOMObject();
        List<StatLock> locks = cdo.getListFor(ListKey.STAT_MAXVALUE);
        if (locks != null)
        {
            addAll(dfce.getCharID(), locks, cdo);
        }
    }

    /**
     * Removes max value StatLock objects granted by a CDOMObject which has been
     * removed from a Player Character.
     * <p>
     * Triggered when one of the Facets to which StatLockFacet listens fires a
     * DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
     * Character.
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
     * Returns the numerical maximum value for the given PCStat which has had a
     * max value set for the Player Character identified by the given CharID.
     * Returns null if no max value StatLock exists on the Player Character for
     * the given PCStat.
     *
     * @param id   The CharID identifying the Player Character for which the
     *             maximum stat value is to be returned
     * @param stat The PCStat for which the numerical lock value is to be
     *             returned
     * @return The numerical value for the given PCStat which has been locked
     * for the Player Character identified by the given CharID; null if
     * no StatLock exists on the Player Character for the given PCStat
     */
    public Number getStatMaxValue(CharID id, PCStat stat)
    {
        Number max = Double.POSITIVE_INFINITY;
        boolean hit = false;

        Map<StatLock, Set<Object>> componentMap = getCachedMap(id);
        if (componentMap != null)
        {
            for (Entry<StatLock, Set<Object>> me : componentMap.entrySet())
            {
                Set<Object> set = me.getValue();
                StatLock lock = me.getKey();
                if (lock.getLockedStat().equals(stat))
                {
                    for (Object source : set)
                    {
                        String sourceString =
                                (source instanceof CDOMObject) ? ((CDOMObject) source).getQualifiedKey() : "";
                        Number val = formulaResolvingFacet.resolve(id, lock.getLockValue(), sourceString);
                        if (val.doubleValue() < max.doubleValue())
                        {
                            hit = true;
                            max = val;
                        }
                    }
                }
            }
        }
        return hit ? max : null;
    }

    public void setFormulaResolvingFacet(FormulaResolvingFacet formulaResolvingFacet)
    {
        this.formulaResolvingFacet = formulaResolvingFacet;
    }

    public void setConsolidationFacet(CDOMObjectConsolidationFacet consolidationFacet)
    {
        this.consolidationFacet = consolidationFacet;
    }

    /**
     * Initializes the connections for StatLockFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the StatLockFacet.
     */
    public void init()
    {
        consolidationFacet.addDataFacetChangeListener(this);
    }
}
