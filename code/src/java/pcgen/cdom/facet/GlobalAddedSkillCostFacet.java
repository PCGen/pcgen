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
import java.util.Map;
import java.util.Set;

import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.core.Skill;

/**
 * Stores the Global SkillCost values as applied by CSKILL:%LIST and
 * CCSKILL:%LIST
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class GlobalAddedSkillCostFacet extends AbstractStorageFacet
{
	private final Class<?> thisClass = getClass();

	/**
	 * Returns the type-safe CacheInfo for this GlobalAddedSkillCostFacet and
	 * the given CharID. Will return a new, empty CacheInfo if no Skill
	 * information has been set for the given CharID. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The CacheInfo object is owned
	 * by GlobalAddedSkillCostFacet, and since it can be modified, a reference
	 * to that object should not be exposed to any object other than
	 * GlobalAddedSkillCostFacet.
	 * 
	 * @param id
	 *            The CharID for which the CacheInfo should be returned
	 * @return The CacheInfo for the Player Character represented by the given
	 *         CharID.
	 */
	private Map<SkillCost, Map<Skill, Set<CDOMObject>>> getConstructingInfo(
			CharID id)
	{
		Map<SkillCost, Map<Skill, Set<CDOMObject>>> rci = getInfo(id);
		if (rci == null)
		{
			rci = new HashMap<SkillCost, Map<Skill, Set<CDOMObject>>>();
			setCache(id, thisClass, rci);
		}
		return rci;
	}

	/**
	 * Returns the type-safe CacheInfo for this GlobalAddedSkillCostFacet and
	 * the given CharID. May return null if no Skill information has been set
	 * for the given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The CacheInfo object is owned
	 * by GlobalAddedSkillCostFacet, and since it can be modified, a reference
	 * to that object should not be exposed to any object other than
	 * GlobalAddedSkillCostFacet.
	 * 
	 * @param id
	 *            The CharID for which the CacheInfo should be returned
	 * @return The CacheInfo for the Player Character represented by the given
	 *         CharID; null if no Skill information has been set for the Player
	 *         Character.
	 */
	private Map<SkillCost, Map<Skill, Set<CDOMObject>>> getInfo(CharID id)
	{
		return (Map<SkillCost, Map<Skill, Set<CDOMObject>>>) getCache(id,
			thisClass);
	}

	/**
	 * Adds a new SkillCost to this GlobalAddedSkillCostFacet for the given
	 * Skill and Player Character identified by the given CharID.
	 * 
	 * @param id
	 *            The CharID identifying the Character for which the SkillCost
	 *            is being added
	 * @param skill
	 *            The Skill to which the given SkillCost is being applied
	 * @param sc
	 *            The SkillCost to apply to the given Skill for the Player
	 *            Character identified by the given CharID
	 * @param source
	 *            The source CDOMObject of the SkillCost
	 */
	public void add(CharID id, Skill skill, SkillCost sc, CDOMObject source)
	{
		Map<SkillCost, Map<Skill, Set<CDOMObject>>> map = getConstructingInfo(id);
		Map<Skill, Set<CDOMObject>> skMap = map.get(sc);
		if (skMap == null)
		{
			skMap = new IdentityHashMap<Skill, Set<CDOMObject>>();
			map.put(sc, skMap);
		}
		Set<CDOMObject> set = skMap.get(skill);
		if (set == null)
		{
			set = new WrappedMapSet<CDOMObject>(IdentityHashMap.class);
			skMap.put(skill, set);
		}
		set.add(source);
	}

	/**
	 * Removes a SkillCost from this GlobalAddedSkillCostFacet for the given
	 * Skill and Player Character identified by the given CharID.
	 * 
	 * @param id
	 *            The CharID identifying the Character for which the SkillCost
	 *            is being removed
	 * @param skill
	 *            The Skill to which the given SkillCost is being removed
	 * @param sc
	 *            The SkillCost to remove for the given Skill and Player
	 *            Character identified by the given CharID
	 * @param source
	 *            The source CDOMObject of the SkillCost
	 */
	public void remove(CharID id, Skill skill, SkillCost sc, CDOMObject source)
	{
		Map<SkillCost, Map<Skill, Set<CDOMObject>>> map = getInfo(id);
		if (map != null)
		{
			Map<Skill, Set<CDOMObject>> skMap = map.get(sc);
			if (skMap != null)
			{
				Set<CDOMObject> set = skMap.get(skill);
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
	}

	/**
	 * Returns true if this GlobalAddedSkillCostFacet contains the given
	 * SkillCost for the given Skill and Player Character identified by the
	 * given CharID.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character to be tested to
	 *            see if this GlobalAddedSkillCostFacet contains the given
	 *            SkillCost for the given Skill
	 * @param skill
	 *            The Skill to be tested to see if if this
	 *            GlobalAddedSkillCostFacet contains the given SkillCost
	 * @param sc
	 *            The SkillCost to be tested to see if this
	 *            GlobalAddedSkillCostFacet contains this SkillCost for the
	 *            given Skill and Player Character identified by the given
	 *            CharID
	 * @return true if this GlobalAddedSkillCostFacet contains the given
	 *         SkillCost for the given Skill and Player Character identified by
	 *         the given CharID; false otherwise
	 */
	public boolean contains(CharID id, Skill skill, SkillCost sc)
	{
		Map<SkillCost, Map<Skill, Set<CDOMObject>>> map = getInfo(id);
		if (map == null)
		{
			return false;
		}
		Map<Skill, Set<CDOMObject>> skMap = map.get(sc);
		return (skMap != null) && skMap.containsKey(skill);
	}

	/**
	 * Copies the contents of the GlobalAddedSkillCostFacet from one Player
	 * Character to another Player Character, based on the given CharIDs
	 * representing those Player Characters.
	 * 
	 * This is a method in GlobalAddedSkillCostFacet in order to avoid exposing
	 * the mutable Map object to other classes. This should not be inlined, as
	 * the Map is internal information to GlobalAddedSkillCostFacet and should
	 * not be exposed to other classes.
	 * 
	 * Note also the copy is a one-time event and no references are maintained
	 * between the Player Characters represented by the given CharIDs (meaning
	 * once this copy takes place, any change to the GlobalAddedSkillCostFacet
	 * of one Player Character will only impact the Player Character where the
	 * GlobalAddedSkillCostFacet was changed).
	 * 
	 * @param source
	 *            The CharID representing the Player Character from which the
	 *            information should be copied
	 * @param destination
	 *            The CharID representing the Player Character to which the
	 *            information should be copied
	 */
	@Override
	public void copyContents(CharID source, CharID copy)
	{
		Map<SkillCost, Map<Skill, Set<CDOMObject>>> map = getInfo(source);
		if (map != null)
		{
			for (Map.Entry<SkillCost, Map<Skill, Set<CDOMObject>>> fme : map.entrySet())
			{
				SkillCost sc = fme.getKey();
				for (Map.Entry<Skill, Set<CDOMObject>> apme : fme.getValue().entrySet())
				{
					Skill sk = apme.getKey();
					for (CDOMObject cdo : apme.getValue())
					{
						add(copy, sk, sc, cdo);
					}
				}
			}
		}
	}
}
