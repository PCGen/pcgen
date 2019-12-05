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

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.facet.CDOMObjectConsolidationFacet;
import pcgen.cdom.facet.FormulaResolvingFacet;
import pcgen.cdom.facet.base.AbstractStorageFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;

/**
 * VariableFacet is a Facet that tracks the Variables that are contained in a
 * Player Character.
 */
public class VariableFacet extends AbstractStorageFacet<CharID> implements DataFacetChangeListener<CharID, CDOMObject>
{
    private FormulaResolvingFacet formulaResolvingFacet;

    private CDOMObjectConsolidationFacet consolidationFacet;

    /**
     * Adds variables and their Formulas when a variable is granted by a
     * CDOMObject which is added to a Player Character.
     * <p>
     * Triggered when one of the Facets to which VariableFacet listens fires a
     * DataFacetChangeEvent to indicate a CDOMObject was added to a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CDOMObject cdo = dfce.getCDOMObject();
        Set<VariableKey> keys = cdo.getVariableKeys();
        CharID id = dfce.getCharID();
        for (VariableKey vk : keys)
        {
            add(id, vk, cdo.get(vk), cdo);
        }
    }

    /**
     * Removes variables and their Formulas when a variable is granted by a
     * CDOMObject which is removed from a Player Character.
     * <p>
     * Triggered when one of the Facets to which VariableFacet listens fires a
     * DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
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

    private void add(CharID id, VariableKey vk, Formula formula, CDOMObject cdo)
    {
        Map<VariableKey, Map<Formula, Set<CDOMObject>>> map = getConstructingCachedMap(id);
        Map<Formula, Set<CDOMObject>> subMap = map.computeIfAbsent(vk, k -> new HashMap<>());
        Set<CDOMObject> sources = subMap.get(formula);
        if (sources == null)
        {
            sources = Collections.newSetFromMap(new IdentityHashMap<>());
            subMap.put(formula, sources);
        }
        sources.add(cdo);
    }

    /**
     * Returns the type-safe Map for this VariableFacet and the given CharID.
     * May return null if no information has been set in this VariableFacet for
     * the given CharID.
     * <p>
     * Note that this method SHOULD NOT be public. The Map is owned by
     * VariableFacet, and since it can be modified, a reference to that object
     * should not be exposed to any object other than VariableFacet.
     *
     * @param id The CharID for which the Map should be returned
     * @return The Set for the Player Character represented by the given CharID;
     * null if no information has been set in this VariableFacet for the
     * Player Character
     */
    @SuppressWarnings("unchecked")
    private Map<VariableKey, Map<Formula, Set<CDOMObject>>> getCachedMap(CharID id)
    {
        return (Map<VariableKey, Map<Formula, Set<CDOMObject>>>) getCache(id);
    }

    /**
     * Returns a type-safe Map for this VariableFacet and the given CharID. Will
     * return a new, empty Map if no information has been set in this
     * VariableFacet for the given CharID. Will not return null.
     * <p>
     * Note that this method SHOULD NOT be public. The Map object is owned by
     * VariableFacet, and since it can be modified, a reference to that object
     * should not be exposed to any object other than VariableFacet.
     *
     * @param id The CharID for which the Map should be returned
     * @return The Map for the Player Character represented by the given CharID
     */
    private Map<VariableKey, Map<Formula, Set<CDOMObject>>> getConstructingCachedMap(CharID id)
    {
        Map<VariableKey, Map<Formula, Set<CDOMObject>>> componentMap = getCachedMap(id);
        if (componentMap == null)
        {
            componentMap = new HashMap<>();
            setCache(id, componentMap);
        }
        return componentMap;
    }

    /**
     * Removes all information for the given source from this VariableFacet for
     * the PlayerCharacter represented by the given CharID.
     *
     * @param id     The CharID representing the Player Character for which items
     *               from the given source will be removed
     * @param source The source for the variables to be removed from the list of
     *               variables stored for the Player Character identified by the
     *               given CharID
     */
    public void removeAll(CharID id, Object source)
    {
        Map<VariableKey, Map<Formula, Set<CDOMObject>>> vkMap = getCachedMap(id);
        if (vkMap != null)
        {
            for (Iterator<Map<Formula, Set<CDOMObject>>> mit = vkMap.values().iterator();mit.hasNext();)
            {
                Map<Formula, Set<CDOMObject>> fMap = mit.next();
                fMap.values().removeIf(set -> set.remove(source) && set.isEmpty());
                if (fMap.isEmpty())
                {
                    mit.remove();
                }
            }
        }
    }

    /**
     * Returns the numeric variable value for the given VariableKey on the
     * Player Character identified by the given CharID. If a variable has more
     * than one value, the given isMax argument is used to determine if this
     * method returns the maximum (true) or minimum (false) of the calculated
     * values.
     *
     * @param id    The CharID identifying the Player Character for which the
     *              numeric variable value is to be returned
     * @param key   The VariableKey identifying the variable which the value
     *              is to be returned
     * @param isMax Used to determine if this method returns the maximum (true) or
     *              minimum (false) of the calculated values when the Player
     *              Character contains more than one value for the given
     *              VariableKey
     * @return The numeric variable value for the given VariableKey on the
     * Player Character identified by the given CharID
     */
    public Double getVariableValue(CharID id, VariableKey key, boolean isMax)
    {
        Map<VariableKey, Map<Formula, Set<CDOMObject>>> vkMap = getCachedMap(id);
        if (vkMap == null)
        {
            return null;
        }
        Map<Formula, Set<CDOMObject>> fMap = vkMap.get(key);
        if (fMap == null)
        {
            return null;
        }
        Double returnValue = null;
        for (Map.Entry<Formula, Set<CDOMObject>> me : fMap.entrySet())
        {
            Formula f = me.getKey();
            Set<CDOMObject> sources = me.getValue();
            for (CDOMObject source : sources)
            {
                double newVal = formulaResolvingFacet.resolve(id, f, source.getQualifiedKey()).doubleValue();
                if (returnValue == null)
                {
                    returnValue = newVal;
                } else if ((returnValue > newVal) ^ isMax)
                {
                    returnValue = newVal;
                }
            }
        }
        return returnValue;
    }

    /**
     * Returns true if this VariableFacet contains the given VariableKey in the
     * list of variables for the Player Character represented by the given
     * CharID.
     *
     * @param id The CharID representing the Player Character used for testing
     * @param vk The VariableKey to test if this VariableFacet contains that
     *           VariableKey for the Player Character represented by the given
     *           CharID
     * @return true if this VariableFacet contains the given VariableKey for the
     * Player Character represented by the given CharID; false otherwise
     */
    public boolean contains(CharID id, VariableKey vk)
    {
        Map<VariableKey, Map<Formula, Set<CDOMObject>>> vkMap = getCachedMap(id);
        return (vkMap != null) && vkMap.containsKey(vk);
    }

    public int getVariableCount(CharID id)
    {
        Map<VariableKey, Map<Formula, Set<CDOMObject>>> vkMap = getCachedMap(id);
        return (vkMap == null) ? 0 : vkMap.size();
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
     * Initializes the connections for VariableFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the VariableFacet.
     */
    public void init()
    {
        consolidationFacet.addDataFacetChangeListener(this);
    }

    /**
     * Copies the contents of the VariableFacet from one Player Character to
     * another Player Character, based on the given CharIDs representing those
     * Player Characters.
     * <p>
     * This is a method in VariableFacet in order to avoid exposing the mutable
     * Map object to other classes. This should not be inlined, as the Map is
     * internal information to VariableFacet and should not be exposed to other
     * classes.
     * <p>
     * Note also the copy is a one-time event and no references are maintained
     * between the Player Characters represented by the given CharIDs (meaning
     * once this copy takes place, any change to the VariableFacet of one Player
     * Character will only impact the Player Character where the VariableFacet
     * was changed).
     *
     * @param source The CharID representing the Player Character from which the
     *               information should be copied
     * @param copy   The CharID representing the Player Character to which the
     *               information should be copied
     */
    @Override
    public void copyContents(CharID source, CharID copy)
    {
        Map<VariableKey, Map<Formula, Set<CDOMObject>>> cm = getCachedMap(source);
        if (cm != null)
        {
            for (Map.Entry<VariableKey, Map<Formula, Set<CDOMObject>>> me : cm.entrySet())
            {
                VariableKey variableKey = me.getKey();
                me.getValue()
                        .forEach((f, value) -> value.forEach(cdo -> add(copy, variableKey, f, cdo)));
            }
        }
    }
}
