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
package pcgen.cdom.facet;

import java.util.Collection;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.ClassFacet;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.Language;

/**
 * StartingLanguageFacet is a Facet that tracks the Languages that are available
 * to a Player Character from the LANGBONUS token.
 */
public class StartingLanguageFacet extends AbstractSourcedListFacet<CharID, Language>
        implements DataFacetChangeListener<CharID, CDOMObject>
{

    private ClassFacet classFacet;

    private RaceFacet raceFacet;

    private TemplateFacet templateFacet;

    /**
     * Adds available Languages to this facet when a CDOMObject added to a
     * Player Character makes Languages available to the Player Character.
     * <p>
     * Triggered when one of the Facets to which StartingLanguageFacet listens
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
        Collection<CDOMReference<Language>> list = cdo.getListMods(Language.STARTING_LIST);
        if (list != null)
        {
            CharID id = dfce.getCharID();
            for (CDOMReference<Language> ref : list)
            {
                addAll(id, ref.getContainedObjects(), cdo);
            }
        }
    }

    /**
     * Removes available Languages from this facet when a CDOMObject removed
     * from a Player Character makes Languages available to the Player
     * Character.
     * <p>
     * Triggered when one of the Facets to which StartingLanguageFacet listens
     * fires a DataFacetChangeEvent to indicate a CDOMObject was removed from a
     * Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        removeAll(dfce.getCharID(), dfce.getCDOMObject());
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
     * Initializes the connections for StartingLanguageFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the StartingLanguageFacet.
     */
    public void init()
    {
        raceFacet.addDataFacetChangeListener(this);
        templateFacet.addDataFacetChangeListener(this);
        classFacet.addDataFacetChangeListener(this);
    }
}
