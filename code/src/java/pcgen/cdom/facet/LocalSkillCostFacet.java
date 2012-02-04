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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Domain;
import pcgen.core.PCClass;
import pcgen.core.Skill;

/**
 * LocalSkillCostFacet is a Facet to track Skill costs
 */
public class LocalSkillCostFacet implements DataFacetChangeListener<CDOMObject>
{
	private final Class<?> thisClass = getClass();

	private DomainFacet domainFacet;
	
	private ClassFacet classFacet;
	
	private ClassLevelFacet classLevelFacet;

	/**
	 * Triggered when one of the Facets to which LocalSkillCostFacet listens fires a
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
		CharID id = dfce.getCharID();
		PCClass owner;
		if (cdo instanceof Domain)
		{
			owner = domainFacet.getSource(id, (Domain) cdo).getPcclass();
		}
		else if (cdo instanceof PCClassLevel)
		{
			owner = (PCClass) cdo.get(ObjectKey.PARENT);
		}
		else if (cdo instanceof PCClass)
		{
			owner = (PCClass) cdo;
		}
		else
		{
			return;
		}
		for (CDOMReference<Skill> ref : cdo.getSafeListFor(ListKey.LOCALCSKILL))
		{
			for (Skill sk : ref.getContainedObjects())
			{
				add(id, owner, sk, SkillCost.CLASS, cdo);
			}
		}
		for (CDOMReference<Skill> ref : cdo.getSafeListFor(ListKey.LOCALCCSKILL))
		{
			for (Skill sk : ref.getContainedObjects())
			{
				add(id, owner, sk, SkillCost.CROSS_CLASS, cdo);
			}
		}
	}

	/**
	 * Triggered when one of the Facets to which LocalSkillCostFacet listens fires a
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
	 * CacheInfo is the data structure used by LocalSkillCostFacet to store a
	 * Player Character's Skill Costs
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

		public void removeAll(Object source)
		{
			for (Iterator<Map<SkillCost, Map<Skill, Set<CDOMObject>>>> clValues = map
					.values().iterator(); clValues.hasNext();)
			{
				Map<SkillCost, Map<Skill, Set<CDOMObject>>> scMap = clValues
						.next();
				for (Iterator<Map<Skill, Set<CDOMObject>>> scValues = scMap
						.values().iterator(); scValues.hasNext();)
				{
					Map<Skill, Set<CDOMObject>> skMap = scValues.next();
					for (Iterator<Set<CDOMObject>> skValues = skMap.values()
							.iterator(); skValues.hasNext();)
					{
						Set<CDOMObject> set = skValues.next();
						if (set.remove(source) && set.isEmpty())
						{
							skValues.remove();
						}
					}
					if (skMap.isEmpty())
					{
						scValues.remove();
					}
				}
				if (scMap.isEmpty())
				{
					clValues.remove();
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

	public void removeAll(CharID id, CDOMObject source)
	{
		CacheInfo ci = getInfo(id);
		if (ci != null)
		{
			ci.removeAll(source);
		}
	}

	public boolean contains(CharID id, PCClass cl, SkillCost sc, Skill sk)
	{
		CacheInfo ci = getInfo(id);
		return ci != null && ci.contains(cl, sc, sk);
	}

	public void setDomainFacet(DomainFacet domainFacet)
	{
		this.domainFacet = domainFacet;
	}

	public void setClassFacet(ClassFacet classFacet)
	{
		this.classFacet = classFacet;
	}

	public void setClassLevelFacet(ClassLevelFacet classLevelFacet)
	{
		this.classLevelFacet = classLevelFacet;
	}

	public void init()
	{
		classFacet.addDataFacetChangeListener(this);
		domainFacet.addDataFacetChangeListener(this);
		classLevelFacet.addDataFacetChangeListener(this);
	}
}
