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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.CDOMObjectConsolidationFacet;
import pcgen.cdom.facet.FormulaResolvingFacet;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.Race;
import pcgen.core.analysis.SizeUtilities;

/**
 * UnarmedDamageFacet is a Facet that tracks the Unarmed Damage info that have
 * been added to a Player Character.
 */
public class UnarmedDamageFacet extends AbstractSourcedListFacet<CharID, List<String>>
        implements DataFacetChangeListener<CharID, CDOMObject>
{
    private RaceFacet raceFacet;

    private FormulaResolvingFacet formulaResolvingFacet;

    private CDOMObjectConsolidationFacet consolidationFacet;

    /**
     * Stores in this facet any Unarmed Damage information from a CDOMObject
     * which has been added to a Player Character.
     * <p>
     * Triggered when one of the Facets to which UnarmedDamageFacet listens
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
        if (cdo instanceof PCClass || cdo instanceof PCClassLevel)
        {
            return;
        }
        List<String> damage = cdo.getListFor(ListKey.UNARMED_DAMAGE);
        if (damage != null)
        {
            add(dfce.getCharID(), damage, cdo);
        }
    }

    /**
     * Removes from this facet any Unarmed Damage information from a CDOMObject
     * which has been removed from a Player Character.
     * <p>
     * Triggered when one of the Facets to which UnarmedDamageFacet listens
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

    /**
     * Returns the unarmed damage String for the Race of the Player Character
     * identified by the given CharID.
     *
     * @param id The CharID identifying the Player Character
     * @return The unarmed damage String for the Race of the Player Character
     * identified by the given CharID
     */
    public String getUDamForRace(CharID id)
    {
        Race race = raceFacet.get(id);
        int iSize = formulaResolvingFacet
                .resolve(id, race.getSafe(FormulaKey.SIZE), race.getQualifiedKey())
                .intValue();
        int baseIndex = SizeUtilities.getDefaultSizeAdjustment().get(IntegerKey.SIZEORDER);
        return Globals.adjustDamage("1d3", iSize - baseIndex);
    }

    /**
     * Returns a new (empty) Map for this UnarmedDamageFacet. This does not
     * require the IdentityHashMap since {@code List<String>} is composed of only
     * well-formed Java objects that behave properly with .equals() and
     * .hashCode() in terms of maintaining identity (whereas many CDOMObjects do
     * not as of 5.16)
     * <p>
     * Note that this method should always be the only method used to construct
     * a Map for this UnarmedDamageFacet. It is actually preferred to use
     * getConstructingCacheMap(CharID) in order to implicitly call this method.
     *
     * @return A new (empty) Map for use in this UnarmedDamageFacet.
     */
    @Override
    protected Map<List<String>, Set<Object>> getComponentMap()
    {
        return new HashMap<>();
    }

    public void setRaceFacet(RaceFacet raceFacet)
    {
        this.raceFacet = raceFacet;
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
     * Initializes the connections for UnarmedDamageFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the UnarmedDamageFacet.
     */
    public void init()
    {
        consolidationFacet.addDataFacetChangeListener(this);
    }
}
