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
package pcgen.cdom.facet.model;

import pcgen.cdom.base.SetFacet;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.meta.CorePerspective;
import pcgen.cdom.meta.CorePerspectiveDB;
import pcgen.cdom.meta.FacetBehavior;
import pcgen.cdom.meta.PerspectiveLocation;
import pcgen.core.Language;
import pcgen.output.publish.OutputDB;

/**
 * LanguageFacet is a Facet that tracks the Languages that have been granted to
 * a Player Character.
 */
public class LanguageFacet extends AbstractSourcedListFacet<CharID, Language>
        implements DataFacetChangeListener<CharID, Language>, PerspectiveLocation, SetFacet<CharID, Language>
{

    /**
     * Adds the Language object identified in the DataFacetChangeEvent to this
     * LanguageFacet for the Player Character identified by the CharID in the
     * DataFacetChangeEvent.
     * <p>
     * Triggered when one of the Facets to which LanguageFacet listens fires a
     * DataFacetChangeEvent to indicate a Language was added to a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, Language> dfce)
    {
        add(dfce.getCharID(), dfce.getCDOMObject(), dfce.getSource());
    }

    /**
     * Removes the Language object identified in the DataFacetChangeEvent from
     * this LanguageFacet for the Player Character identified by the CharID in
     * the DataFacetChangeEvent.
     * <p>
     * Triggered when one of the Facets to which LanguageFacet listens fires a
     * DataFacetChangeEvent to indicate a Language was removed from a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, Language> dfce)
    {
        remove(dfce.getCharID(), dfce.getCDOMObject(), dfce.getSource());
    }

    public void init()
    {
        CorePerspectiveDB.register(CorePerspective.LANGUAGE, FacetBehavior.MODEL, this);
        OutputDB.register("languages", this);
    }

    @Override
    public String getIdentity()
    {
        return "Character Languages";
    }
}
