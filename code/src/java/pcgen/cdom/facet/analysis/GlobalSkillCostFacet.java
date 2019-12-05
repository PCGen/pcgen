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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.facet.CDOMObjectConsolidationFacet;
import pcgen.cdom.facet.base.AbstractScopeFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.core.Skill;

/**
 * GlobalSkillCostFacet is a Facet to track Skill costs as applied by direct
 * skill references in CSKILL and CCSKILL
 */
public class GlobalSkillCostFacet extends AbstractScopeFacet<CharID, SkillCost, Skill>
        implements DataFacetChangeListener<CharID, CDOMObject>
{
    private CDOMObjectConsolidationFacet consolidationFacet;

    /**
     * Adds the SkillCost objects granted by CDOMObjects added to the Player
     * Character to this GlobalSkillCostFacet.
     * <p>
     * Triggered when one of the Facets to which GlobalSkillCostFacet listens
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
        CharID id = dfce.getCharID();
        for (CDOMReference<Skill> ref : cdo.getSafeListFor(ListKey.CSKILL))
        {
            for (Skill sk : ref.getContainedObjects())
            {
                add(id, SkillCost.CLASS, sk, cdo);
            }
        }
        for (CDOMReference<Skill> ref : cdo.getSafeListFor(ListKey.CCSKILL))
        {
            for (Skill sk : ref.getContainedObjects())
            {
                add(id, SkillCost.CROSS_CLASS, sk, cdo);
            }
        }
    }

    /**
     * Removes the SkillCost objects granted by CDOMObjects removed from the
     * Player Character from this GlobalSkillCostFacet.
     * <p>
     * Triggered when one of the Facets to which GlobalSkillCostFacet listens
     * fires a DataFacetChangeEvent to indicate a CDOMObject was removed from a
     * Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        removeAllFromSource(dfce.getCharID(), dfce.getCDOMObject());
    }

    public void setConsolidationFacet(CDOMObjectConsolidationFacet consolidationFacet)
    {
        this.consolidationFacet = consolidationFacet;
    }

    /**
     * Initializes the connections for GlobalSkillCostFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the GlobalSkillCostFacet.
     */
    public void init()
    {
        consolidationFacet.addDataFacetChangeListener(this);
    }

}
