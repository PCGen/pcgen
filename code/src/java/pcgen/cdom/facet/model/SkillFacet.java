/*
 * Copyright (c) Thomas Parker, 2009-14.
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
package pcgen.cdom.facet.model;

import pcgen.cdom.base.SetFacet;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.SkillRankFacet.SkillRankChangeEvent;
import pcgen.cdom.facet.SkillRankFacet.SkillRankChangeListener;
import pcgen.cdom.facet.TotalSkillRankFacet;
import pcgen.cdom.facet.UsableSkillsFacet;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.AssociationChangeEvent;
import pcgen.cdom.facet.event.AssociationChangeListener;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.core.Skill;
import pcgen.output.publish.OutputDB;

/**
 * SkillFacet is a Facet that tracks the Skills possessed by a Player Character.
 */
public class SkillFacet extends AbstractSourcedListFacet<CharID, Skill> implements SkillRankChangeListener,
        DataFacetChangeListener<CharID, Skill>, AssociationChangeListener, SetFacet<CharID, Skill>
{

    private TotalSkillRankFacet totalSkillRankFacet;

    private UsableSkillsFacet usableSkillsFacet;

    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, Skill> dfce)
    {
        add(dfce.getCharID(), dfce.getCDOMObject(), dfce.getSource());
    }

    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, Skill> dfce)
    {
        remove(dfce.getCharID(), dfce.getCDOMObject(), dfce.getSource());
    }

    @Override
    public void rankChanged(SkillRankChangeEvent lce)
    {
        CharID id = lce.getCharID();
        Skill skill = lce.getSkill();
        if (lce.getNewRank() == 0.0f)
        {
            remove(id, skill, lce.getSource());
        } else
        {
            add(id, skill, lce.getSource());
        }
    }

    public void setTotalSkillRankFacet(TotalSkillRankFacet totalSkillRankFacet)
    {
        this.totalSkillRankFacet = totalSkillRankFacet;
    }

    public void setUsableSkillsFacet(UsableSkillsFacet usableSkillsFacet)
    {
        this.usableSkillsFacet = usableSkillsFacet;
    }

    public void init()
    {
        totalSkillRankFacet.addAssociationChangeListener(this);
        usableSkillsFacet.addDataFacetChangeListener(this);
        OutputDB.register("skills", this);
    }

    @Override
    public void bonusChange(AssociationChangeEvent dfce)
    {
        CharID id = dfce.getCharID();
        Skill sk = dfce.getSkill();
        Number ranks = dfce.getNewVal();
        if (ranks.doubleValue() > 0)
        {
            add(id, sk, dfce.getSource());
        } else
        {
            remove(id, sk, dfce.getSource());
        }
    }

}
