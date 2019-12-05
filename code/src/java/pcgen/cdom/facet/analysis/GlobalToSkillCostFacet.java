/*
 * Copyright (c) Thomas Parker, 2014.
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
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.facet.base.AbstractSubScopeFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.event.ScopeFacetChangeEvent;
import pcgen.cdom.facet.event.ScopeFacetChangeListener;
import pcgen.cdom.facet.input.GlobalAddedSkillCostFacet;
import pcgen.cdom.facet.input.MasterUsableSkillFacet;
import pcgen.cdom.facet.model.ClassFacet;
import pcgen.core.PCClass;
import pcgen.core.Skill;

public class GlobalToSkillCostFacet extends AbstractSubScopeFacet<PCClass, SkillCost, Skill>
        implements ScopeFacetChangeListener<CharID, SkillCost, Skill>, DataFacetChangeListener<CharID, PCClass>
{

    private ClassFacet classFacet;

    private GlobalSkillCostFacet globalSkillCostFacet;

    private GlobalAddedSkillCostFacet globalAddedSkillCostFacet;

    private MasterUsableSkillFacet masterUsableSkillFacet;

    @Override
    public void dataAdded(ScopeFacetChangeEvent<CharID, SkillCost, Skill> dfce)
    {
        CharID id = dfce.getCharID();
        SkillCost cost = dfce.getScope();
        Skill sk = dfce.getCDOMObject();
        Object source = dfce.getSource();
        for (PCClass cl : classFacet.getSet(id))
        {
            add(id, cl, cost, sk, source);
        }
    }

    @Override
    public void dataRemoved(ScopeFacetChangeEvent<CharID, SkillCost, Skill> dfce)
    {
        CharID id = dfce.getCharID();
        SkillCost cost = dfce.getScope();
        Skill sk = dfce.getCDOMObject();
        Object source = dfce.getSource();
        for (PCClass cl : classFacet.getSet(id))
        {
            remove(id, cl, cost, sk, source);
        }
    }

    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, PCClass> dfce)
    {
        CharID id = dfce.getCharID();
        PCClass cl = dfce.getCDOMObject();
        DataSetID dsID = id.getDatasetID();
        for (Skill sk : masterUsableSkillFacet.getSet(dsID))
        {
            add(id, cl, SkillCost.CROSS_CLASS, sk, masterUsableSkillFacet);
        }
        for (SkillCost cost : globalSkillCostFacet.getScopes(id))
        {
            for (Skill sk : globalSkillCostFacet.getSet(id, cost))
            {
                add(id, cl, cost, sk, globalSkillCostFacet);
            }
        }
        for (SkillCost cost : globalAddedSkillCostFacet.getScopes(id))
        {
            for (Skill sk : globalAddedSkillCostFacet.getSet(id, cost))
            {
                add(id, cl, cost, sk, globalAddedSkillCostFacet);
            }
        }
    }

    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, PCClass> dfce)
    {
        CharID id = dfce.getCharID();
        PCClass cl = dfce.getCDOMObject();
        DataSetID dsID = id.getDatasetID();
        for (Skill sk : masterUsableSkillFacet.getSet(dsID))
        {
            remove(id, cl, SkillCost.CROSS_CLASS, sk, masterUsableSkillFacet);
        }
        for (SkillCost cost : globalSkillCostFacet.getScopes(id))
        {
            for (Skill sk : globalSkillCostFacet.getSet(id, cost))
            {
                remove(id, cl, cost, sk, globalSkillCostFacet);
            }
        }
        for (SkillCost cost : globalAddedSkillCostFacet.getScopes(id))
        {
            for (Skill sk : globalAddedSkillCostFacet.getSet(id, cost))
            {
                remove(id, cl, cost, sk, globalAddedSkillCostFacet);
            }
        }
    }

    public void setGlobalSkillCostFacet(GlobalSkillCostFacet globalSkillCostFacet)
    {
        this.globalSkillCostFacet = globalSkillCostFacet;
    }

    public void setGlobalAddedSkillCostFacet(GlobalAddedSkillCostFacet globalAddedSkillCostFacet)
    {
        this.globalAddedSkillCostFacet = globalAddedSkillCostFacet;
    }

    public void setClassFacet(ClassFacet classFacet)
    {
        this.classFacet = classFacet;
    }

    public void setMasterUsableSkillFacet(MasterUsableSkillFacet masterUsableSkillFacet)
    {
        this.masterUsableSkillFacet = masterUsableSkillFacet;
    }

    public void init()
    {
        classFacet.addDataFacetChangeListener(this);
        globalSkillCostFacet.addScopeFacetChangeListener(this);
        globalAddedSkillCostFacet.addScopeFacetChangeListener(this);
    }

}
