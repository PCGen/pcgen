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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import pcgen.base.util.NamedValue;
import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractStorageFacet;
import pcgen.core.Skill;

/**
 * SkillRankFacet stores the number of Skill Ranks for a specific Skill for a
 * Player Character.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class SkillRankFacet extends AbstractStorageFacet
{
	private final Class<?> thisClass = getClass();

	/*
	 * TODO Storage inconsistent with other facet classes
	 * 
	 * This class stores information differently than other classes in the Facet
	 * structure. This class stores items in a NamedValue, and that is both a
	 * double value as well as the source PCClass. Since that is a unique and
	 * constructed object (as well as mutable), that makes it different than
	 * many other facets that store a value and then the source of those values.
	 * It should be considered whether this class should be more consistent in
	 * its storage relative to other facets.
	 * 
	 * In fact, you can go as far as saying this is "not good" in the sense that
	 * the design should probably limit the number of values to once per any
	 * given class, so this is probably best stored as a DoubleKeyMap (or
	 * equivalent since those structures are not generally used in the facets).
	 * First key would be skill, second key would be class.
	 * 
	 * Primarily this is done to ease the transition from the pre-facet
	 * infrastructure where this information was stored in a NamedValue List. So
	 * this transition was done as one small step, with possible future work. -
	 * thpr Dec 21, 2012
	 */

	/**
	 * Returns the type-safe CacheInfo for this SkillRankFacet and the given
	 * CharID. Will return a new, empty CacheInfo if no Skill information has
	 * been set for the given CharID. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The CacheInfo object is owned
	 * by SkillRankFacet, and since it can be modified, a reference to that
	 * object should not be exposed to any object other than SkillRankFacet.
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
			setCache(id, thisClass, rci);
		}
		return rci;
	}

	/**
	 * Returns the type-safe CacheInfo for this SkillRankFacet and the given
	 * CharID. May return null if no Skill information has been set for the
	 * given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The CacheInfo object is owned
	 * by SkillRankFacet, and since it can be modified, a reference to that
	 * object should not be exposed to any object other than SkillRankFacet.
	 * 
	 * @param id
	 *            The CharID for which the CacheInfo should be returned
	 * @return The CacheInfo for the Player Character represented by the given
	 *         CharID; null if no Skill information has been set for the Player
	 *         Character.
	 */
	private CacheInfo getInfo(CharID id)
	{
		return (CacheInfo) getCache(id, thisClass);
	}

	/**
	 * CacheInfo is the data structure used by SkillRankFacet to store a Player
	 * Character's Skill Ranks
	 */
	private static class CacheInfo
	{
		Map<Skill, Set<NamedValue>> map = new HashMap<Skill, Set<NamedValue>>();

		public void add(Skill skill, NamedValue value)
		{
			Set<NamedValue> set = map.get(skill);
			if (set == null)
			{
				set = new WrappedMapSet<NamedValue>(IdentityHashMap.class);
				map.put(skill, set);
			}
			set.add(value);
		}

		public Collection<NamedValue> getSet(Skill sk)
		{
			Set<NamedValue> ms = map.get(sk);
			if (ms == null)
			{
				return Collections.emptySet();
			}
			Set<NamedValue> set =
					new WrappedMapSet<NamedValue>(IdentityHashMap.class);
			set.addAll(ms);
			return set;
		}

		public void remove(Skill sk, NamedValue value)
		{
			Set<NamedValue> ms = map.get(sk);
			if (ms != null)
			{
				ms.remove(value);
			}
		}

	}

	public void add(CharID id, Skill skill, NamedValue value)
	{
		getConstructingInfo(id).add(skill, value);
	}

	/**
	 * Copies the contents of the SkillRankFacet from one Player Character to
	 * another Player Character, based on the given CharIDs representing those
	 * Player Characters.
	 * 
	 * This is a method in SkillRankFacet in order to avoid exposing the mutable
	 * Map object to other classes. This should not be inlined, as the Map is
	 * internal information to SkillRankFacet and should not be exposed to other
	 * classes.
	 * 
	 * Note also the copy is a one-time event and no references are maintained
	 * between the Player Characters represented by the given CharIDs (meaning
	 * once this copy takes place, any change to the SkillRankFacet of one
	 * Player Character will only impact the Player Character where the
	 * SkillRankFacet was changed).
	 * 
	 * @param source
	 *            The CharID representing the Player Character from which the
	 *            information should be copied
	 * @param copy
	 *            The CharID representing the Player Character to which the
	 *            information should be copied
	 */
	@Override
	public void copyContents(CharID source, CharID copy)
	{
		CacheInfo rci = getInfo(source);
		if (rci != null)
		{
			CacheInfo copyci = getConstructingInfo(copy);
			for (Map.Entry<Skill, Set<NamedValue>> fme : rci.map.entrySet())
			{
				Skill sk = fme.getKey();
				for (NamedValue value : fme.getValue())
				{
					copyci.add(sk,
						new NamedValue(value.getName(), value.getWeight()));
				}
			}
		}
	}

	public Collection<NamedValue> getSet(CharID id, Skill sk)
	{
		CacheInfo ci = getInfo(id);
		if (ci == null)
		{
			return Collections.emptySet();
		}
		return ci.getSet(sk);
	}

	public void remove(CharID id, Skill sk, NamedValue value)
	{
		CacheInfo ci = getInfo(id);
		if (ci != null)
		{
			ci.remove(sk, value);
		}
	}

	public Float getRank(CharID id, Skill sk)
	{
		double rank = 0.0;
		
		Collection<NamedValue> rankList = getSet(id, sk);
		if (rankList != null)
		{
			for (NamedValue sd : rankList)
			{
				rank += sd.getWeight();
			}
		}
	
		return new Float(rank);
	}
}
