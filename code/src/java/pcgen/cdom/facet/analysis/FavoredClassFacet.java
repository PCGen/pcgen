/*
 * Copyright (c) Thomas Parker, 2009.
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
import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.SetFacet;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.ClassFacet;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PCClass;
import pcgen.output.publish.OutputDB;

/**
 * FavoredClassFacet is a Facet that tracks the Favored Classes that have been
 * granted to a Player Character.
 */
public class FavoredClassFacet extends AbstractSourcedListFacet<CharID, PCClass>
        implements DataFacetChangeListener<CharID, CDOMObject>, SetFacet<CharID, PCClass>
{

    private HasAnyFavoredClassFacet hasAnyFavoredClassFacet;

    private ClassFacet classFacet;

    private RaceFacet raceFacet;

    private TemplateFacet templateFacet;

    /**
     * Identifies CDOMObjects that grant a FavoredClass and adds the granted
     * FavoredClass to this FavoredClassFacet.
     * <p>
     * Triggered when one of the Facets to which FavoredClassFacet listens fires
     * a DataFacetChangeEvent to indicate a CDOMObject was added to a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CDOMObject cdo = dfce.getCDOMObject();
        List<CDOMReference<? extends PCClass>> list = cdo.getListFor(ListKey.FAVORED_CLASS);
        if (list != null)
        {
            for (CDOMReference<? extends PCClass> ref : list)
            {
                addAll(dfce.getCharID(), ref.getContainedObjects(), cdo);
            }
        }
    }

    /**
     * Identifies CDOMObjects that grant a FavoredClass and removes the granted
     * FavoredClass from this FavoredClassFacet.
     * <p>
     * Triggered when one of the Facets to which FavoredClassFacet listens fires
     * a DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
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
     * Returns the number of levels the Player Character has in favored classes.
     *
     * @param id The CharID identifying the Player Character for which the
     *           number of levels in favored classes will be returned.
     * @return The number of levels the Player Character has in favored classes.
     */
    public int getFavoredClassLevel(CharID id)
    {
        Set<PCClass> aList = getSet(id);
        int level = 0;
        int max = 0;

        boolean isAny = hasAnyFavoredClassFacet.contains(id, Boolean.TRUE);
        for (PCClass cl : aList)
        {
            for (PCClass pcClass : classFacet.getSet(id))
            {
                if (isAny)
                {
                    max = Math.max(max, classFacet.getLevel(id, pcClass));
                }
                if (cl.getKeyName().equals(pcClass.getKeyName()))
                {
                    level += classFacet.getLevel(id, pcClass);
                    break;
                }
            }
        }
        return Math.max(level, max);
    }

    public void setHasAnyFavoredClassFacet(HasAnyFavoredClassFacet hasAnyFavoredClassFacet)
    {
        this.hasAnyFavoredClassFacet = hasAnyFavoredClassFacet;
    }

    public void setClassFacet(ClassFacet classFacet)
    {
        this.classFacet = classFacet;
    }

    public void setRaceFacet(RaceFacet raceFacet)
    {
        this.raceFacet = raceFacet;
    }

    public void setTemplateFacet(TemplateFacet templateFacet)
    {
        this.templateFacet = templateFacet;
    }

    /**
     * Initializes the connections for FavoredClassFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the FavoredClassFacet.
     */
    public void init()
    {
        raceFacet.addDataFacetChangeListener(this);
        templateFacet.addDataFacetChangeListener(this);
        OutputDB.register("favoredclass", this);
    }
}
