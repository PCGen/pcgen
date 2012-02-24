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

/**
 * LocalAddedSkillCostFacet stores directly set SkillCost objects, which are the
 * result of a number of possibilities: ADD:CLASSSKILLS as well as CSKILL:%LIST or
 * CCSKILL:%LIST in a Domain are both examples that use LocalAddedSkillCostFacet.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class LocalAddedSkillCostFacet extends AbstractStorageFacet
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
	private Map<PCClass, Map<SkillCost, Map<Skill, Set<CDOMObject>>>> getConstructingInfo(
			CharID id)
	{
		Map<PCClass, Map<SkillCost, Map<Skill, Set<CDOMObject>>>> rci = getInfo(id);
		if (rci == null)
		{
			rci = new IdentityHashMap<PCClass, Map<SkillCost, Map<Skill, Set<CDOMObject>>>>();
			setCache(id, thisClass, rci);
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
	private Map<PCClass, Map<SkillCost, Map<Skill, Set<CDOMObject>>>> getInfo(
			CharID id)
	{
		return (Map<PCClass, Map<SkillCost, Map<Skill, Set<CDOMObject>>>>) getCache(
			id, thisClass);
	}

	/**
	 * Adds the given SkillCost for the given Skill (as granted by the given
	 * source) for the given PCClass
	 * 
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            SkillCost for the given Skill is being added
	 * @param cl
	 *            The PCClass for which the given SkillCost for the given Skill
	 *            is being added
	 * @param skill
	 *            The Skill for which the SkillCost is being added
	 * @param sc
	 *            The SkillCost for the given Skill to be added for the given
	 *            PCClass
	 * @param source
	 *            The source object which granted the given SkillCost for the
	 *            given Skill
	 */
	public void add(CharID id, PCClass cl, Skill skill, SkillCost sc,
			CDOMObject source)
	{
		Map<PCClass, Map<SkillCost, Map<Skill, Set<CDOMObject>>>> map = getConstructingInfo(id);
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

	/**
	 * Removes the given SkillCost for the given Skill (as granted by the given
	 * source) for the given PCClass
	 * 
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            SkillCost for the given Skill is being removed
	 * @param cl
	 *            The PCClass for which the given SkillCost for the given Skill
	 *            is being removed
	 * @param skill
	 *            The Skill for which the SkillCost is being removed
	 * @param sc
	 *            The SkillCost for the given Skill to be removed for the given
	 *            PCClass
	 * @param source
	 *            The source object which granted the given SkillCost for the
	 *            given Skill
	 */
	public void remove(CharID id, PCClass cl, Skill skill, SkillCost sc,
			CDOMObject source)
	{
		Map<PCClass, Map<SkillCost, Map<Skill, Set<CDOMObject>>>> map = getInfo(id);
		if (map != null)
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
	}

	/**
	 * Returns true if this LocalAddedSkillCostFacet has the given SkillCost for
	 * the given Skill on the given ClassSkillList for the Player Character
	 * identified by the given CharID.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character which will be
	 *            checked to determine if it contains the requested SkillCost
	 * @param cl
	 *            The ClassSkillList which will be checked to determine if it
	 *            contains the requested SkillCost for the given Skill
	 * @param skill
	 *            The Skill which will be checked to determine if it contains
	 *            the requested SkillCost
	 * @param sc
	 *            The SkillCost to be checked to see if the Player Character has
	 *            this SkillCost for the given Skill on the given ClassSkillList
	 * @return true if this LocalAddedSkillCostFacet has the given Skill Cost
	 *         for the given Skill on the given ClassSkillList for the Player
	 *         Character identified by the given CharID; false otherwise
	 * 
	 */
	public boolean contains(CharID id, PCClass cl, Skill skill, SkillCost sc)
	{
		Map<PCClass, Map<SkillCost, Map<Skill, Set<CDOMObject>>>> map = getInfo(id);
		if (map == null)
		{
			return false;
		}
		Map<SkillCost, Map<Skill, Set<CDOMObject>>> scMap = map.get(cl);
		if (scMap == null)
		{
			return false;
		}
		Map<Skill, Set<CDOMObject>> skMap = scMap.get(sc);
		return (skMap != null) && skMap.containsKey(skill);
	}

	/**
	 * Copies the contents of the LocalAddedSkillCostFacet from one Player
	 * Character to another Player Character, based on the given CharIDs
	 * representing those Player Characters.
	 * 
	 * This is a method in LocalAddedSkillCostFacet in order to avoid exposing
	 * the mutable Map object to other classes. This should not be inlined, as
	 * the Map is internal information to LocalAddedSkillCostFacet and should
	 * not be exposed to other classes.
	 * 
	 * Note also the copy is a one-time event and no references are maintained
	 * between the Player Characters represented by the given CharIDs (meaning
	 * once this copy takes place, any change to the LocalAddedSkillCostFacet of
	 * one Player Character will only impact the Player Character where the
	 * LocalAddedSkillCostFacet was changed).
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
		Map<PCClass, Map<SkillCost, Map<Skill, Set<CDOMObject>>>> map = getInfo(source);
		if (map != null)
		{
			for (Map.Entry<PCClass, Map<SkillCost, Map<Skill, Set<CDOMObject>>>> me : map
				.entrySet())
			{
				PCClass pcc = me.getKey();
				for (Map.Entry<SkillCost, Map<Skill, Set<CDOMObject>>> fme : me
					.getValue().entrySet())
				{
					SkillCost sc = fme.getKey();
					for (Map.Entry<Skill, Set<CDOMObject>> apme : fme
						.getValue().entrySet())
					{
						Skill sk = apme.getKey();
						for (CDOMObject cdo : apme.getValue())
						{
							add(copy, pcc, sk, sc, cdo);
						}
					}
				}
			}
		}
	}
}
