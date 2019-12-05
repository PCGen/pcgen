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
package pcgen.cdom.facet;

import java.util.Collection;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.facet.base.AbstractSubScopeFacet;
import pcgen.cdom.facet.event.ScopeFacetChangeEvent;
import pcgen.cdom.facet.event.ScopeFacetChangeListener;
import pcgen.cdom.facet.model.SkillListFacet;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.PCClass;
import pcgen.core.Skill;

/**
 * SkillListFacet stores the SkillList objects for a PCClass of a Player
 * Character
 */
public class SkillListToCostFacet extends AbstractSubScopeFacet<PCClass, SkillCost, Skill>
        implements ScopeFacetChangeListener<CharID, PCClass, ClassSkillList>
{

    private SkillListFacet skillListFacet;

    private MasterSkillFacet masterSkillFacet;

    @Override
    public void dataAdded(ScopeFacetChangeEvent<CharID, PCClass, ClassSkillList> dfce)
    {
        CharID id = dfce.getCharID();
        ClassSkillList skilllist = dfce.getCDOMObject();
        Collection<Skill> set = masterSkillFacet.getSet(id.getDatasetID(), skilllist);
        if (set != null)
        {
            PCClass pcc = dfce.getScope();
            for (Skill s : set)
            {
                add(id, pcc, SkillCost.CLASS, s, skilllist);
            }
        }
    }

    @Override
    public void dataRemoved(ScopeFacetChangeEvent<CharID, PCClass, ClassSkillList> dfce)
    {
        removeAllFromSource(dfce.getCharID(), dfce.getCDOMObject());
    }

    public void setSkillListFacet(SkillListFacet skillListFacet)
    {
        this.skillListFacet = skillListFacet;
    }

    public void setMasterSkillFacet(MasterSkillFacet masterSkillFacet)
    {
        this.masterSkillFacet = masterSkillFacet;
    }

    public void init()
    {
        skillListFacet.addScopeFacetChangeListener(this);
    }
}
