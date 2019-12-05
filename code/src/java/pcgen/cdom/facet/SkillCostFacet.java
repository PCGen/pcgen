/*
 * Copyright (c) Thomas Parker, 2012
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

import java.util.Objects;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.facet.analysis.GlobalToSkillCostFacet;
import pcgen.cdom.facet.analysis.ListToSkillCostFacet;
import pcgen.cdom.facet.analysis.LocalSkillCostFacet;
import pcgen.cdom.facet.analysis.MonCSkillToSkillCostFacet;
import pcgen.cdom.facet.base.AbstractSubScopeFacet;
import pcgen.cdom.facet.event.SubScopeFacetChangeEvent;
import pcgen.cdom.facet.event.SubScopeFacetChangeListener;
import pcgen.cdom.facet.input.LocalAddedSkillCostFacet;
import pcgen.core.PCClass;
import pcgen.core.Skill;

/**
 * SkillCostFacet is a Facet that tracks costs of Skills for each PCClass
 */
public class SkillCostFacet extends AbstractSubScopeFacet<Skill, SkillCost, PCClass>
        implements SubScopeFacetChangeListener<PCClass, SkillCost, Skill>
{
    private ListToSkillCostFacet listToSkillCostFacet;
    private LocalAddedSkillCostFacet localAddedSkillCostFacet;
    private LocalSkillCostFacet localSkillCostFacet;
    private SkillListToCostFacet skillListToCostFacet;
    private MonCSkillToSkillCostFacet monCSkillToSkillCostFacet;
    private GlobalToSkillCostFacet globalToSkillCostFacet;

    public SkillCost skillCostForPCClass(CharID id, Skill sk, PCClass aClass)
    {
        if (isClassSkill(id, aClass, sk))
        {
            return SkillCost.CLASS;
        } else if (sk.getSafe(ObjectKey.EXCLUSIVE) && !isCrossClassSkill(id, aClass, sk))
        {
            return SkillCost.EXCLUSIVE;
        } else
        {
            return SkillCost.CROSS_CLASS;
        }
    }

    public boolean isClassSkill(CharID id, PCClass pcc, Skill skill)
    {
        Objects.requireNonNull(pcc, "PCClass in isClassSkill cannot be null");
        Objects.requireNonNull(skill, "Skill in isClassSkill cannot be null");
        return contains(id, skill, SkillCost.CLASS, pcc);
    }

    public boolean isCrossClassSkill(CharID id, PCClass pcc, Skill skill)
    {
        Objects.requireNonNull(pcc, "PCClass in isCrossClassSkill cannot be null");
        Objects.requireNonNull(skill, "Skill in isCrossClassSkill cannot be null");
        return !contains(id, skill, SkillCost.CLASS, pcc) && contains(id, skill, SkillCost.CROSS_CLASS, pcc);
    }

    @Override
    public void dataAdded(SubScopeFacetChangeEvent<PCClass, SkillCost, Skill> dfce)
    {
        add(dfce.getCharID(), dfce.getCDOMObject(), dfce.getScope2(), dfce.getScope1(), dfce.getSource());
    }

    @Override
    public void dataRemoved(SubScopeFacetChangeEvent<PCClass, SkillCost, Skill> dfce)
    {
        remove(dfce.getCharID(), dfce.getCDOMObject(), dfce.getScope2(), dfce.getScope1(), dfce.getSource());
    }

    public void setGlobalToSkillCostFacet(GlobalToSkillCostFacet globalToSkillCostFacet)
    {
        this.globalToSkillCostFacet = globalToSkillCostFacet;
    }

    public void setListToSkillCostFacet(ListToSkillCostFacet listToSkillCostFacet)
    {
        this.listToSkillCostFacet = listToSkillCostFacet;
    }

    public void setLocalAddedSkillCostFacet(LocalAddedSkillCostFacet localAddedSkillCostFacet)
    {
        this.localAddedSkillCostFacet = localAddedSkillCostFacet;
    }

    public void setLocalSkillCostFacet(LocalSkillCostFacet localSkillCostFacet)
    {
        this.localSkillCostFacet = localSkillCostFacet;
    }

    public void setSkillListToCostFacet(SkillListToCostFacet skillListToCostFacet)
    {
        this.skillListToCostFacet = skillListToCostFacet;
    }

    public void setMonCSkillToSkillCostFacet(MonCSkillToSkillCostFacet monCSkillToSkillCostFacet)
    {
        this.monCSkillToSkillCostFacet = monCSkillToSkillCostFacet;
    }

    public void init()
    {
        skillListToCostFacet.addSubScopeFacetChangeListener(this);
        globalToSkillCostFacet.addSubScopeFacetChangeListener(this);
        localSkillCostFacet.addSubScopeFacetChangeListener(this);
        localAddedSkillCostFacet.addSubScopeFacetChangeListener(this);
        listToSkillCostFacet.addSubScopeFacetChangeListener(this);
        monCSkillToSkillCostFacet.addSubScopeFacetChangeListener(this);
    }
}
