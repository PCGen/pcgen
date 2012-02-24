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
 * GlobalSkillCostFacet is a Facet to track Skill costs as applied by direct
 * skill references in CSKILL and CCSKILL
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class GlobalSkillCostFacet extends AbstractStorageFacet implements
		DataFacetChangeListener<CDOMObject>
{
	private final Class<?> thisClass = getClass();

	private CDOMObjectConsolidationFacet consolidationFacet;

	/**
	 * Adds the SkillCost objects granted by CDOMObjects added to the Player
	 * Character to this GlobalSkillCostFacet.
	 * 
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
	 * Removes the SkillCost objects granted by CDOMObjects removed from the
	 * Player Character from this GlobalSkillCostFacet.
	 * 
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
		CacheInfo ci = getInfo(dfce.getCharID());
		if (ci != null)
		{
			ci.removeAll(dfce.getCDOMObject());
		}
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
			setCache(id, thisClass, rci);
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
		return (CacheInfo) getCache(id, thisClass);
	}

	/**
	 * CacheInfo is the data structure used by SkillCostFacet to store a Player
	 * Character's Skill Costs
	 */
	private static class CacheInfo
	{
		Map<SkillCost, Map<Skill, Set<Object>>> map = new HashMap<SkillCost, Map<Skill, Set<Object>>>();

		/**
		 * Adds the given SkillCost for the given Skill (as granted by the given
		 * source) to this CacheInfo
		 * 
		 * @param skill
		 *            The Skill for which the SkillCost is being added
		 * @param sc
		 *            The SkillCost for the given Skill to be added to this
		 *            CacheInfo
		 * @param source
		 *            The source object which granted the given SkillCost for
		 *            the given Skill
		 */
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

		//		public void remove(Skill skill, SkillCost sc, Object source)
		//		{
		//			Map<Skill, Set<Object>> skMap = map.get(sc);
		//			if (skMap != null)
		//			{
		//				Set<Object> set = skMap.get(skill);
		//				if (set != null)
		//				{
		//					if (set.remove(source) && set.isEmpty())
		//					{
		//						skMap.remove(skill);
		//						if (skMap.isEmpty())
		//						{
		//							map.remove(sc);
		//						}
		//					}
		//				}
		//			}
		//		}

		/**
		 * Removes all SkillCosts from this CacheInfo for the given Source.
		 * 
		 * @param source
		 *            The source Object for which all SkillCosts in this
		 *            CacheInfo will be removed
		 */
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

		/**
		 * Returns true if CacheInfo contains the given SkillCost for the given
		 * Skill.
		 * 
		 * @param sc
		 *            The SkillCost to be tested to see if this CacheInfo
		 *            contains this SkillCost for the given Skill
		 * @param sk
		 *            The Skill to be tested to see if if this CacheInfo
		 *            contains the given SkillCost
		 * @return true if this GlobalSkillCostFacet contains the given
		 *         SkillCost for the given Skill; false otherwise
		 */
		public boolean contains(SkillCost sc, Skill skill)
		{
			Map<Skill, Set<Object>> skMap = map.get(sc);
			return (skMap != null) && skMap.containsKey(skill);
		}
	}

	private void add(CharID id, Skill skill, SkillCost sc, Object source)
	{
		getConstructingInfo(id).add(skill, sc, source);
	}

	/**
	 * Returns true if GlobalSkillCostFacet contains the given SkillCost for the
	 * given Skill and Player Character identified by the given CharID.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character to be tested to
	 *            see if this GlobalSkillCostFacet contains the given SkillCost
	 *            for the given Skill
	 * @param sc
	 *            The SkillCost to be tested to see if this GlobalSkillCostFacet
	 *            contains this SkillCost for the given Skill and Player
	 *            Character identified by the given CharID
	 * @param sk
	 *            The Skill to be tested to see if if this GlobalSkillCostFacet
	 *            contains the given SkillCost
	 * @return true if this GlobalSkillCostFacet contains the given SkillCost
	 *         for the given Skill and Player Character identified by the given
	 *         CharID; false otherwise
	 */
	public boolean contains(CharID id, SkillCost sc, Skill sk)
	{
		CacheInfo ci = getInfo(id);
		return ci != null && ci.contains(sc, sk);
	}

	public void setConsolidationFacet(CDOMObjectConsolidationFacet consolidationFacet)
	{
		this.consolidationFacet = consolidationFacet;
	}

	/**
	 * Initializes the connections for GlobalSkillCostFacet to other facets.
	 * 
	 * This method is automatically called by the Spring framework during
	 * initialization of the GlobalSkillCostFacet.
	 */
	public void init()
	{
		consolidationFacet.addDataFacetChangeListener(this);
	}

	/**
	 * Copies the contents of the GlobalSkillCostFacet from one Player Character
	 * to another Player Character, based on the given CharIDs representing
	 * those Player Characters.
	 * 
	 * This is a method in GlobalSkillCostFacet in order to avoid exposing the
	 * mutable Map object to other classes. This should not be inlined, as the
	 * Map is internal information to GlobalSkillCostFacet and should not be
	 * exposed to other classes.
	 * 
	 * Note also the copy is a one-time event and no references are maintained
	 * between the Player Characters represented by the given CharIDs (meaning
	 * once this copy takes place, any change to the GlobalSkillCostFacet of one
	 * Player Character will only impact the Player Character where the
	 * GlobalSkillCostFacet was changed).
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
		CacheInfo rci = getInfo(source);
		if (rci != null)
		{
			CacheInfo copyci = getConstructingInfo(copy);
			for (Map.Entry<SkillCost, Map<Skill, Set<Object>>> fme : rci.map.entrySet())
			{
				SkillCost sc = fme.getKey();
				for (Map.Entry<Skill, Set<Object>> apme : fme.getValue().entrySet())
				{
					Skill sk = apme.getKey();
					for (Object cdo : apme.getValue())
					{
						copyci.add(sk, sc, cdo);
					}
				}
			}
		}
	}
}
