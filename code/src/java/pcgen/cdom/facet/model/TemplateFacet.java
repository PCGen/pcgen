/*
 * Copyright (c) Thomas Parker, 2012.
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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import pcgen.cdom.base.SetFacet;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.core.PCTemplate;
import pcgen.output.publish.OutputDB;

/**
 * TemplateFacet is a Facet that tracks all PCTemplates that have been granted
 * to a Player Character.
 */
public class TemplateFacet extends AbstractSourcedListFacet<CharID, PCTemplate>
        implements DataFacetChangeListener<CharID, PCTemplate>, SetFacet<CharID, PCTemplate>
{

    /**
     * Adds the active PCTemplate to this facet.
     * <p>
     * Triggered when one of the Facets to which TemplateFacet listens fires a
     * DataFacetChangeEvent to indicate PCTemplate was added to a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, PCTemplate> dfce)
    {
        add(dfce.getCharID(), dfce.getCDOMObject(), dfce.getSource());
    }

    /**
     * Removes the no-longer active PCTemplate from this facet.
     * <p>
     * Triggered when one of the Facets to which TemplateFacet listens fires a
     * DataFacetChangeEvent to indicate PCTemplate was removed from a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, PCTemplate> dfce)
    {
        remove(dfce.getCharID(), dfce.getCDOMObject(), dfce.getSource());
    }

    @Override
    protected Map<PCTemplate, Set<Object>> getComponentMap()
    {
        /*
         * This has to be LinkedHashMap since PCTemplates are order sensitive in when they
         * are applied to a PC.
         */
        return new LinkedHashMap<>();
    }

    public void init()
    {
        OutputDB.register("templates", this);
    }
}
