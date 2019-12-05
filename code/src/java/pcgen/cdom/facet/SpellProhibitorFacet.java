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
package pcgen.cdom.facet;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.base.AbstractScopeFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.ClassFacet;
import pcgen.core.PCClass;
import pcgen.core.SpellProhibitor;

/**
 * SpellProhibitorFacet is a Facet to track SpellProhibitor costs for each
 * PCClass in a Player Character
 */
public class SpellProhibitorFacet extends AbstractScopeFacet<CharID, PCClass, SpellProhibitor>
        implements DataFacetChangeListener<CharID, PCClass>
{
    private ClassFacet classFacet;

    /**
     * Adds the SpellProhibitor objects granted by PCClasses added to the Player
     * Character to this SpellProhibitorFacet.
     * <p>
     * Triggered when one of the Facets to which SpellProhibitorFacet listens
     * fires a DataFacetChangeEvent to indicate a PCClass was added to a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, PCClass> dfce)
    {
        PCClass pcc = dfce.getCDOMObject();
        CharID id = dfce.getCharID();
        Object source = dfce.getSource();
        for (SpellProhibitor prohibit : pcc.getSafeListFor(ListKey.PROHIBITED_SPELLS))
        {
            add(id, pcc, prohibit, source);
        }

        for (SpellProhibitor prohibit : pcc.getSafeListFor(ListKey.SPELL_PROHIBITOR))
        {
            add(id, pcc, prohibit, source);
        }
    }

    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, PCClass> dfce)
    {
        removeAllFromSource(dfce.getCharID(), dfce.getCDOMObject());
    }

    public void setClassFacet(ClassFacet classFacet)
    {
        this.classFacet = classFacet;
    }

    /**
     * Initializes the connections for SpellProhibitorFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the SpellProhibitorFacet.
     */
    public void init()
    {
        classFacet.addDataFacetChangeListener(this);
    }
}
