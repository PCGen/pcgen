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
package pcgen.cdom.facet;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.core.Skill;

/**
 * GlobalSkillCostFacet is a Facet to track Skill costs
 */
public class GlobalSkillCostFacet implements
		DataFacetChangeListener<CDOMObject>
{
	private final Class<?> thisClass = getClass();

	private CDOMObjectConsolidationFacet consolidationFacet;

	/**
	 * Triggered when one of the Facets to which GlobalSkillCostFacet listens
	 * fires a DataFacetChangeEvent to indicate a CDOMObject was added to a
	 * Player Character.
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
		CharID id = dfce.getCharID();
		for (CDOMReference<Skill> ref : cdo.getSafeListFor(ListKey.CSKILL))
		{
			for (Skill sk : ref.getContainedObjects())
			{
				add(id, sk, SkillCost.CLASS, cdo);
			}
		}
		for (CDOMReference<Skill> ref : cdo.getSafeListFor(ListKey.CCSKILL))
		{
			for (Skill sk : ref.getContainedObjects())
			{
				add(id, sk, SkillCost.CROSS_CLASS, cdo);
			}
		}
	}

	/**
	 * Triggered when one of the Facets to which GlobalSkillCostFacet listens
	 * fires a DataFacetChangeEvent to indicate a CDOMObject was removed from a
	 * Player Character.
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

	/**
	 * Returns the type-safe CacheInfo for this SkillCostFacet and the given
	 * CharID. Will return a new, empty CacheInfo if no Skill information has
	 * been set for the given CharID. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The CacheInfo object is owned
	 * by SkillCostFacet, and since it can be modified, a reference to that
	 * object should not be exposed to any object other than SkillCostFacet.
	 * 
	 * @param id
	 *            The CharID for which the CacheInfo should be returned
	 * @return The CacheInfo for the Player Character represented by the given
	 *         CharID.
	 */
	private CacheInfo getConstructingInfo(CharID id)
	{
		CacheInfo rci = getInfo(id);
		if (rci == null)
		{
			rci = new CacheInfo();
			FacetCache.set(id, thisClass, rci);
		}
		return rci;
	}

	/**
	 * Returns the type-safe CacheInfo for this SkillCostFacet and the given
	 * CharID. May return null if no Skill information has been set for the
	 * given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The CacheInfo object is owned
	 * by SkillCostFacet, and since it can be modified, a reference to that
	 * object should not be exposed to any object other than SkillCostFacet.
	 * 
	 * @param id
	 *            The CharID for which the CacheInfo should be returned
	 * @return The CacheInfo for the Player Character represented by the given
	 *         CharID; null if no Skill information has been set for the Player
	 *         Character.
	 */
	private CacheInfo getInfo(CharID id)
	{
		return (CacheInfo) FacetCache.get(id, thisClass);
	}

	/**
	 * CacheInfo is the data structure used by SkillCostFacet to store a Player
	 * Character's Skill Costs
	 */
	private static class CacheInfo
	{
		Map<SkillCost, Map<Skill, Set<Object>>> map = new HashMap<SkillCost, Map<Skill, Set<Object>>>();

		public void add(Skill skill, SkillCost sc, Object source)
		{
			Map<Skill, Set<Object>> skMap = map.get(sc);
			if (skMap == null)
			{
				skMap = new IdentityHashMap<Skill, Set<Object>>();
				map.put(sc, skMap);
			}
			Set<Object> set = skMap.get(skill);
			if (set == null)
			{
				set = new WrappedMapSet<Object>(IdentityHashMap.class);
				skMap.put(skill, set);
			}
			set.add(source);
		}

		public void remove(Skill skill, SkillCost sc, Object source)
		{
			Map<Skill, Set<Object>> skMap = map.get(sc);
			if (skMap != null)
			{
				Set<Object> set = skMap.get(skill);
				if (set != null)
				{
					if (set.remove(source) && set.isEmpty())
					{
						skMap.remove(skill);
						if (skMap.isEmpty())
						{
							map.remove(sc);
						}
					}
				}
			}
		}

		public void removeAll(Object source)
		{
			for (Iterator<Map<Skill, Set<Object>>> mit = map.values()
					.iterator(); mit.hasNext();)
			{
				Map<Skill, Set<Object>> skMap = mit.next();
				for (Iterator<Set<Object>> sit = skMap.values().iterator(); sit
						.hasNext();)
				{
					Set<Object> set = sit.next();
					if (set.remove(source) && set.isEmpty())
					{
						sit.remove();
					}
				}
				if (skMap.isEmpty())
				{
					mit.remove();
				}
			}
		}

		public boolean contains(SkillCost sc, Skill skill)
		{
			Map<Skill, Set<Object>> skMap = map.get(sc);
			return (skMap != null) && skMap.containsKey(skill);
		}
	}

	public void add(CharID id, Skill skill, SkillCost sc, Object source)
	{
		getConstructingInfo(id).add(skill, sc, source);
	}

	public void remove(CharID id, Skill skill, SkillCost sc, Object source)
	{
		CacheInfo info = getInfo(id);
		if (info != null)
		{
			info.remove(skill, sc, source);
		}
	}

	public void removeAll(CharID id, Object source)
	{
		CacheInfo ci = getInfo(id);
		if (ci != null)
		{
			ci.removeAll(source);
		}
	}

	public boolean contains(CharID id, SkillCost sc, Skill sk)
	{
		CacheInfo ci = getInfo(id);
		return ci != null && ci.contains(sc, sk);
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
