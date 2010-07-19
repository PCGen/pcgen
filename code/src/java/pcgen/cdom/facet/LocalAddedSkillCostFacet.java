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

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.core.PCClass;
import pcgen.core.Skill;

public class LocalAddedSkillCostFacet
{
	private final Class<?> thisClass = getClass();

	/**
	 * Returns the type-safe CacheInfo for this LocalAddedSkillCostFacet and the
	 * given CharID. Will return a new, empty CacheInfo if no Skill information
	 * has been set for the given CharID. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The CacheInfo object is owned
	 * by LocalAddedSkillCostFacet, and since it can be modified, a reference to
	 * that object should not be exposed to any object other than
	 * LocalAddedSkillCostFacet.
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
	 * Returns the type-safe CacheInfo for this LocalAddedSkillCostFacet and the
	 * given CharID. May return null if no Skill information has been set for
	 * the given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The CacheInfo object is owned
	 * by LocalAddedSkillCostFacet, and since it can be modified, a reference to
	 * that object should not be exposed to any object other than
	 * LocalAddedSkillCostFacet.
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
	 * CacheInfo is the data structure used by LocalAddedSkillCostFacet to store
	 * a Player Character's Skill Costs
	 */
	private static class CacheInfo
	{
		Map<PCClass, Map<SkillCost, Map<Skill, Set<CDOMObject>>>> map = new IdentityHashMap<PCClass, Map<SkillCost, Map<Skill, Set<CDOMObject>>>>();

		public void add(PCClass cl, Skill skill, SkillCost sc, CDOMObject source)
		{
			Map<SkillCost, Map<Skill, Set<CDOMObject>>> scMap = map.get(cl);
			if (scMap == null)
			{
				scMap = new IdentityHashMap<SkillCost, Map<Skill, Set<CDOMObject>>>();
				map.put(cl, scMap);
			}
			Map<Skill, Set<CDOMObject>> skMap = scMap.get(sc);
			if (skMap == null)
			{
				skMap = new IdentityHashMap<Skill, Set<CDOMObject>>();
				scMap.put(sc, skMap);
			}
			Set<CDOMObject> set = skMap.get(skill);
			if (set == null)
			{
				set = new WrappedMapSet<CDOMObject>(IdentityHashMap.class);
				skMap.put(skill, set);
			}
			set.add(source);
		}

		public void remove(PCClass cl, Skill skill, SkillCost sc,
				CDOMObject source)
		{
			Map<SkillCost, Map<Skill, Set<CDOMObject>>> scMap = map.get(cl);
			if (scMap == null)
			{
				return;
			}
			Map<Skill, Set<CDOMObject>> skMap = scMap.get(sc);
			if (skMap == null)
			{
				return;
			}
			Set<CDOMObject> set = skMap.get(skill);
			if (set == null)
			{
				return;
			}
			if (set.remove(source) && set.isEmpty())
			{
				skMap.remove(skill);
				if (skMap.isEmpty())
				{
					scMap.remove(sc);
					if (scMap.isEmpty())
					{
						map.remove(cl);
					}
				}
			}
		}

		public boolean contains(PCClass cl, SkillCost sc, Skill skill)
		{
			Map<SkillCost, Map<Skill, Set<CDOMObject>>> scMap = map.get(cl);
			if (scMap == null)
			{
				return false;
			}
			Map<Skill, Set<CDOMObject>> skMap = scMap.get(sc);
			return (skMap != null) && skMap.containsKey(skill);
		}
	}

	public void add(CharID id, PCClass cl, Skill skill, SkillCost sc,
			CDOMObject source)
	{
		getConstructingInfo(id).add(cl, skill, sc, source);
	}

	public void remove(CharID id, PCClass cl, Skill skill, SkillCost sc,
			CDOMObject source)
	{
		CacheInfo info = getInfo(id);
		if (info != null)
		{
			info.remove(cl, skill, sc, source);
		}
	}

	public boolean contains(CharID id, PCClass cl, Skill skill, SkillCost sc)
	{
		CacheInfo info = getInfo(id);
		return info != null && info.contains(cl, sc, skill);
	}

}
