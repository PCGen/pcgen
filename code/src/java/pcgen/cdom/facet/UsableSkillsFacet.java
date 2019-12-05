/*
 * Copyright (c) Thomas Parker, 2012-14
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

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.SubScopeFacetChangeEvent;
import pcgen.cdom.facet.event.SubScopeFacetChangeListener;
import pcgen.core.PCClass;
import pcgen.core.Skill;

/**
 * UsableSkillsFacet is a Facet that tracks Non-Exclusive Use-Untrained skills
 * for a PC (CLASS or CROSSCLASS for any PCClass)
 */
public class UsableSkillsFacet extends AbstractSourcedListFacet<CharID, Skill>
        implements SubScopeFacetChangeListener<Skill, SkillCost, PCClass>
{
    private SkillCostFacet skillCostFacet;

    @Override
    public void dataAdded(SubScopeFacetChangeEvent<Skill, SkillCost, PCClass> dfce)
    {
        Skill sk = dfce.getScope1();
        if (sk.getSafe(ObjectKey.USE_UNTRAINED))
        {
            add(dfce.getCharID(), sk, dfce.getSource());
        }
    }

    @Override
    public void dataRemoved(SubScopeFacetChangeEvent<Skill, SkillCost, PCClass> dfce)
    {
        CharID id = dfce.getCharID();
        Skill sk = dfce.getScope1();
        if (sk.getSafe(ObjectKey.USE_UNTRAINED) && !skillCostFacet.containsFor(id, sk))
        {
            remove(id, sk, dfce.getSource());
        }
    }

    public void setSkillCostFacet(SkillCostFacet skillCostFacet)
    {
        this.skillCostFacet = skillCostFacet;
    }

    public void init()
    {
        skillCostFacet.addSubScopeFacetChangeListener(this);
    }
}
