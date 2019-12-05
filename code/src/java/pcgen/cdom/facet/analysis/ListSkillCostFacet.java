/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.facet.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.facet.base.AbstractSubScopeFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.Skill;

/**
 * ListSkillCostFacet processes SkillCosts associated with the MONCSKILL and
 * MONCCSKILL tokens.
 */
public class ListSkillCostFacet extends AbstractSubScopeFacet<ClassSkillList, SkillCost, Skill>
        implements DataFacetChangeListener<CharID, CDOMObject>
{
    private RaceFacet raceFacet;

    /**
     * Adds the SkillCost objects granted by CDOMObjects, as applied directly to
     * a ClassSkillList, when a CDOMObject is added to a Player Character.
     * <p>
     * Triggered when one of the Facets to which ListSkillCostFacet listens
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
        for (CDOMReference ref : cdo.getModifiedLists())
        {
            List<ClassSkillList> useList = new ArrayList<>();
            for (Object list : ref.getContainedObjects())
            {
                if (list instanceof ClassSkillList)
                {
                    useList.add((ClassSkillList) list);
                }
            }
            if (!useList.isEmpty())
            {
                Collection<CDOMReference<Skill>> mods = cdo.getListMods(ref);
                for (CDOMReference<Skill> skRef : mods)
                {
                    for (AssociatedPrereqObject apo : (Iterable<AssociatedPrereqObject>) cdo.getListAssociations(ref,
                            skRef))
                    {
                        SkillCost sc = apo.getAssociation(AssociationKey.SKILL_COST);
                        for (ClassSkillList csl : useList)
                        {
                            for (Skill skill : skRef.getContainedObjects())
                            {
                                add(id, csl, sc, skill, cdo);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Removes the SkillCost objects granted by CDOMObjects, as applied directly
     * to a ClassSkillList, when a CDOMObject is removed from a Player
     * Character.
     * <p>
     * Triggered when one of the Facets to which ListSkillCostFacet listens
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

    public void setRaceFacet(RaceFacet raceFacet)
    {
        this.raceFacet = raceFacet;
    }

    /**
     * Initializes the connections for ListSkillCostFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the ListSkillCostFacet.
     */
    public void init()
    {
        raceFacet.addDataFacetChangeListener(this);
    }
}
