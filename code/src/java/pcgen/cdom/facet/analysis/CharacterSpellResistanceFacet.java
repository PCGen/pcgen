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

import java.util.Map;
import java.util.Set;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.SpellResistance;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.CDOMObjectConsolidationFacet;
import pcgen.cdom.facet.FormulaResolvingFacet;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;

/**
 * CharacterSpellResistanceFacet is a Facet that tracks the SpellResistance
 * objects that have been granted to a Player Character.
 */
public class CharacterSpellResistanceFacet extends AbstractSourcedListFacet<CharID, Formula>
        implements DataFacetChangeListener<CharID, CDOMObject>
{
    private FormulaResolvingFacet formulaResolvingFacet;

    private CDOMObjectConsolidationFacet consolidationFacet;

    /**
     * Captures any SpellResistance objects granted by CDOMObjects added to the
     * Player Character and adds those SpellResisteance objects to this
     * CharacterSpellResistanceFacet for the Player Character.
     * <p>
     * Triggered when one of the Facets to which CharacterSpellResistanceFacet
     * listens fires a DataFacetChangeEvent to indicate a CDOMObject was added
     * to a Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CDOMObject cdo = dfce.getCDOMObject();
        SpellResistance sr = cdo.get(ObjectKey.SR);
        if (sr != null)
        {
            add(dfce.getCharID(), sr.getReduction(), cdo);
        }
    }

    /**
     * Captures any SpellResistance objects granted by CDOMObjects removed from
     * the Player Character and removes those SpellResisteance objects to this
     * CharacterSpellResistanceFacet for the Player Character.
     * <p>
     * Triggered when one of the Facets to which CharacterSpellResistanceFacet
     * listens fires a DataFacetChangeEvent to indicate a CDOMObject was removed
     * from a Player Character.
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
     * Returns the Spell Resistance for the Player Character identified by the
     * given CharID.
     *
     * @param id The CharID identifying the Player Character for which the
     *           Spell Resistance will be returned
     * @return The Spell Resistance for the Player Character identified by the
     * given CharID
     */
    public int getSR(CharID id)
    {
        Map<Formula, Set<Object>> componentMap = getCachedMap(id);
        int sr = 0;
        if (componentMap != null)
        {
            for (Map.Entry<Formula, Set<Object>> me : componentMap.entrySet())
            {
                Formula f = me.getKey();
                Set<Object> sourceSet = me.getValue();
                for (Object source : sourceSet)
                {
                    String sourceString = (source instanceof CDOMObject) ? ((CDOMObject) source).getQualifiedKey() : "";
                    sr = Math.max(sr, formulaResolvingFacet.resolve(id, f, sourceString).intValue());
                }
            }
        }
        return sr;
    }

    public void setFormulaResolvingFacet(FormulaResolvingFacet formulaResolvingFacet)
    {
        this.formulaResolvingFacet = formulaResolvingFacet;
    }

    public void setConsolidationFacet(CDOMObjectConsolidationFacet consolidationFacet)
    {
        this.consolidationFacet = consolidationFacet;
    }

    /**
     * Initializes the connections for CharacterSpellResistanceFacet to other
     * facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the CharacterSpellResistanceFacet.
     */
    public void init()
    {
        consolidationFacet.addDataFacetChangeListener(this);
    }
}
