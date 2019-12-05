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
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.facet.base.AbstractSubScopeFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.ClassFacet;
import pcgen.cdom.facet.model.ClassLevelFacet;
import pcgen.cdom.facet.model.DomainFacet;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Domain;
import pcgen.core.PCClass;
import pcgen.core.Skill;
import pcgen.util.Logging;

/**
 * LocalSkillCostFacet is a Facet to track Skill costs
 */
public class LocalSkillCostFacet extends AbstractSubScopeFacet<PCClass, SkillCost, Skill>
        implements DataFacetChangeListener<CharID, CDOMObject>
{
    private DomainFacet domainFacet;

    private ClassFacet classFacet;

    private ClassLevelFacet classLevelFacet;

    /**
     * Adds the SkillCost objects granted by CDOMObjects, as applied directly to
     * a PCClass, when a CDOMObject is added to a Player Character.
     * <p>
     * Triggered when one of the Facets to which LocalSkillCostFacet listens
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
        PCClass owner;
        if (cdo instanceof Domain)
        {
            owner = domainFacet.getSource(id, (Domain) cdo).getPcclass();
        } else if (cdo instanceof PCClassLevel)
        {
            owner = (PCClass) cdo.get(ObjectKey.PARENT);
        } else if (cdo instanceof PCClass)
        {
            owner = (PCClass) cdo;
        } else
        {
            Logging
                    .errorPrint(getClass().getSimpleName() + " was given " + cdo + " which is not an expected object type");
            return;
        }
        for (CDOMReference<Skill> ref : cdo.getSafeListFor(ListKey.LOCALCSKILL))
        {
            for (Skill sk : ref.getContainedObjects())
            {
                add(id, owner, SkillCost.CLASS, sk, cdo);
            }
        }
        for (CDOMReference<Skill> ref : cdo.getSafeListFor(ListKey.LOCALCCSKILL))
        {
            for (Skill sk : ref.getContainedObjects())
            {
                add(id, owner, SkillCost.CROSS_CLASS, sk, cdo);
            }
        }
    }

    /**
     * Removes the SkillCost objects granted by CDOMObjects, as applied directly
     * to a PCClass, when a CDOMObject is removed from a Player Character.
     * <p>
     * Triggered when one of the Facets to which LocalSkillCostFacet listens
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

    public void setDomainFacet(DomainFacet domainFacet)
    {
        this.domainFacet = domainFacet;
    }

    public void setClassFacet(ClassFacet classFacet)
    {
        this.classFacet = classFacet;
    }

    public void setClassLevelFacet(ClassLevelFacet classLevelFacet)
    {
        this.classLevelFacet = classLevelFacet;
    }

    /**
     * Initializes the connections for LocalSkillCostFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the LocalSkillCostFacet.
     */
    public void init()
    {
        classFacet.addDataFacetChangeListener(this);
        domainFacet.addDataFacetChangeListener(this);
        classLevelFacet.addDataFacetChangeListener(this);
    }
}
