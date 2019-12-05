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
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.facet.base.AbstractSubScopeFacet;
import pcgen.cdom.facet.event.ScopeFacetChangeEvent;
import pcgen.cdom.facet.event.ScopeFacetChangeListener;
import pcgen.cdom.facet.event.SubScopeFacetChangeEvent;
import pcgen.cdom.facet.event.SubScopeFacetChangeListener;
import pcgen.cdom.facet.model.SkillListFacet;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.PCClass;
import pcgen.core.Skill;

/**
 * ListSkillCostFacet processes SkillCosts associated with the MONCSKILL and
 * MONCCSKILL tokens.
 */
public class ListToSkillCostFacet extends AbstractSubScopeFacet<PCClass, SkillCost, Skill>
        implements ScopeFacetChangeListener<CharID, PCClass, ClassSkillList>,
        SubScopeFacetChangeListener<ClassSkillList, SkillCost, Skill>
{

    private SkillListFacet skillListFacet;

    private ListSkillCostFacet listSkillCostFacet;

    @Override
    public void dataAdded(ScopeFacetChangeEvent<CharID, PCClass, ClassSkillList> dfce)
    {
        CharID id = dfce.getCharID();
        PCClass pcc = dfce.getScope();
        ClassSkillList skilllist = dfce.getCDOMObject();
        for (Skill sk : listSkillCostFacet.getSet(id, skilllist, SkillCost.CLASS))
        {
            add(id, pcc, SkillCost.CLASS, sk, pcc);
        }
        for (Skill sk : listSkillCostFacet.getSet(id, skilllist, SkillCost.CROSS_CLASS))
        {
            add(id, pcc, SkillCost.CROSS_CLASS, sk, pcc);
        }
    }

    @Override
    public void dataRemoved(ScopeFacetChangeEvent<CharID, PCClass, ClassSkillList> dfce)
    {
        removeAllFromSource(dfce.getCharID(), dfce.getScope());
    }

    public void setSkillListFacet(SkillListFacet skillListFacet)
    {
        this.skillListFacet = skillListFacet;
    }

    public void setListSkillCostFacet(ListSkillCostFacet listSkillCostFacet)
    {
        this.listSkillCostFacet = listSkillCostFacet;
    }

    public void init()
    {
        skillListFacet.addScopeFacetChangeListener(this);
        listSkillCostFacet.addSubScopeFacetChangeListener(this);
    }

    @Override
    public void dataAdded(SubScopeFacetChangeEvent<ClassSkillList, SkillCost, Skill> dfce)
    {
        CharID id = dfce.getCharID();
        ClassSkillList skilllist = dfce.getScope1();
        SkillCost cost = dfce.getScope2();
        Skill sk = dfce.getCDOMObject();
        for (PCClass cl : skillListFacet.getScopes(id))
        {
            if (skillListFacet.contains(id, cl, skilllist))
            {
                add(id, cl, cost, sk, cl);
            }
        }
    }

    @Override
    public void dataRemoved(SubScopeFacetChangeEvent<ClassSkillList, SkillCost, Skill> dfce)
    {
        CharID id = dfce.getCharID();
        ClassSkillList skilllist = dfce.getScope1();
        SkillCost cost = dfce.getScope2();
        Skill sk = dfce.getCDOMObject();
        for (PCClass cl : skillListFacet.getScopes(id))
        {
            if (skillListFacet.contains(id, cl, skilllist))
            {
                remove(id, cl, cost, sk, cl);
            }
        }
    }
}
