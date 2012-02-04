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

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import pcgen.base.formula.Formula;
import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.VariableKey;

/**
 * VariableFacet is a Facet that tracks the Variables that are contained in a
 * Player Character.
 */
public class VariableFacet implements DataFacetChangeListener<CDOMObject>
{
	private FormulaResolvingFacet formulaResolvingFacet;

	private CDOMObjectConsolidationFacet consolidationFacet;

	/**
	 * Triggered when one of the Facets to which VariableFacet listens fires a
	 * DataFacetChangeEvent to indicate a CDOMObject was added to a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataAdded(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<CDOMObject> dfce)
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
	 * Triggered when one of the Facets to which VariableFacet listens fires a
	 * DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataRemoved(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataRemoved(DataFacetChangeEvent<CDOMObject> dfce)
	{
		removeAll(dfce.getCharID(), dfce.getCDOMObject());
	}

	private void add(CharID id, VariableKey vk, Formula formula, CDOMObject cdo)
	{
		Map<VariableKey, Map<Formula, Set<CDOMObject>>> map = getConstructingCachedMap(id);
		Map<Formula, Set<CDOMObject>> subMap = map.get(vk);
		if (subMap == null)
		{
			subMap = new HashMap<Formula, Set<CDOMObject>>();
			map.put(vk, subMap);
		}
		Set<CDOMObject> sources = subMap.get(formula);
		if (sources == null)
		{
			sources = new WrappedMapSet<CDOMObject>(IdentityHashMap.class);
			subMap.put(formula, sources);
		}
		sources.add(cdo);
	}

	/**
	 * Returns the type-safe Map for this AbstractSourcedListFacet and the given
	 * CharID. May return null if no information has been set in this
	 * AbstractSourcedListFacet for the given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The Map is owned by
	 * AbstractSourcedListFacet, and since it can be modified, a reference to
	 * that object should not be exposed to any object other than
	 * AbstractSourcedListFacet.
	 * 
	 * @param id
	 *            The CharID for which the Set should be returned
	 * @return The Set for the Player Character represented by the given CharID;
	 *         null if no information has been set in this
	 *         AbstractSourcedListFacet for the Player Character.
	 */
	private Map<VariableKey, Map<Formula, Set<CDOMObject>>> getCachedMap(
			CharID id)
	{
		return (Map<VariableKey, Map<Formula, Set<CDOMObject>>>) FacetCache
				.get(id, getClass());
	}

	/**
	 * Returns a type-safe Map for this VariableFacet and the given CharID. Will
	 * return a new, empty Map if no information has been set in this
	 * VariableFacet for the given CharID. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The Map object is owned by
	 * VariableFacet, and since it can be modified, a reference to that object
	 * should not be exposed to any object other than VariableFacet.
	 * 
	 * @param id
	 *            The CharID for which the Map should be returned
	 * @return The Map for the Player Character represented by the given CharID.
	 */
	private Map<VariableKey, Map<Formula, Set<CDOMObject>>> getConstructingCachedMap(
			CharID id)
	{
		Map<VariableKey, Map<Formula, Set<CDOMObject>>> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			componentMap = new HashMap<VariableKey, Map<Formula, Set<CDOMObject>>>();
			FacetCache.set(id, getClass(), componentMap);
		}
		return componentMap;
	}

	public void removeAll(CharID id, Object source)
	{
		Map<VariableKey, Map<Formula, Set<CDOMObject>>> vkMap = getCachedMap(id);
		if (vkMap != null)
		{
			for (Iterator<Map<Formula, Set<CDOMObject>>> mit = vkMap.values()
					.iterator(); mit.hasNext();)
			{
				Map<Formula, Set<CDOMObject>> fMap = mit.next();
				for (Iterator<Set<CDOMObject>> sit = fMap.values().iterator(); sit
						.hasNext();)
				{
					Set<CDOMObject> set = sit.next();
					if (set.remove(source) && set.isEmpty())
					{
						sit.remove();
					}
				}
				if (fMap.isEmpty())
				{
					mit.remove();
				}
			}
		}
	}

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
				double newVal = formulaResolvingFacet.resolve(id, f,
						source.getQualifiedKey()).doubleValue();
				if (returnValue == null)
				{
					returnValue = newVal;
				}
				else if ((returnValue > newVal) ^ isMax)
				{
					returnValue = newVal;
				}
			}
		}
		return returnValue;
	}

	public boolean contains(CharID id, VariableKey vk)
	{
		Map<VariableKey, Map<Formula, Set<CDOMObject>>> vkMap = getCachedMap(id);
		return (vkMap != null) && vkMap.containsKey(vk);
	}

	public void setFormulaResolvingFacet(FormulaResolvingFacet formulaResolvingFacet)
	{
		this.formulaResolvingFacet = formulaResolvingFacet;
	}

	public void setConsolidationFacet(CDOMObjectConsolidationFacet consolidationFacet)
	{
		this.consolidationFacet = consolidationFacet;
	}
	
	public void init()
	{
		consolidationFacet.addDataFacetChangeListener(this);
	}
}
