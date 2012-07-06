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
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class LocalSkillCostFacet extends AbstractStorageFacet implements
		DataFacetChangeListener<CDOMObject>
{
	private final Class<?> thisClass = getClass();

	private DomainFacet domainFacet;
	
	private ClassFacet classFacet;
	
	private ClassLevelFacet classLevelFacet;

	/**
	 * Adds the SkillCost objects granted by CDOMObjects, as applied directly to
	 * a PCClass, when a CDOMObject is added to a Player Character.
	 * 
	 * Triggered when one of the Facets to which LocalSkillCostFacet listens
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
	 * Removes the SkillCost objects granted by CDOMObjects, as applied directly
	 * to a PCClass, when a CDOMObject is removed from a Player Character.
	 * 
	 * Triggered when one of the Facets to which LocalSkillCostFacet listens
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
	 * Returns the type-safe CacheInfo for this LocalSkillCostFacet and the
	 * given CharID. Will return a new, empty CacheInfo if no Skill information
	 * has been set for the given CharID. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The CacheInfo object is owned
	 * by LocalSkillCostFacet, and since it can be modified, a reference to that
	 * object should not be exposed to any object other than
	 * LocalSkillCostFacet.
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
	 * Returns the type-safe CacheInfo for this LocalSkillCostFacet and the
	 * given CharID. May return null if no Skill information has been set for
	 * the given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The CacheInfo object is owned
	 * by LocalSkillCostFacet, and since it can be modified, a reference to that
	 * object should not be exposed to any object other than
	 * LocalSkillCostFacet.
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
	 * CacheInfo is the data structure used by LocalSkillCostFacet to store a
	 * Player Character's Skill Costs.
	 */
	private static class CacheInfo
	{
		Map<PCClass, Map<SkillCost, Map<Skill, Set<CDOMObject>>>> map = new IdentityHashMap<PCClass, Map<SkillCost, Map<Skill, Set<CDOMObject>>>>();

		/**
		 * Adds the given SkillCost for the given Skill (as granted by the given
		 * source) for the given PCClass.
		 * 
		 * @param cl
		 *            The PCClass for which the given SkillCost for the given
		 *            Skill is being added
		 * @param skill
		 *            The Skill for which the SkillCost is being added
		 * @param sc
		 *            The SkillCost for the given Skill to be added for the
		 *            given PCClass
		 * @param source
		 *            The source object which granted the given SkillCost for
		 *            the given Skill
		 */
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

		//		/**
		//		 * Removes the given SkillCost for the given Skill (as granted by the
		//		 * given source) for the given PCClass
		//		 * 
		//		 * @param cl
		//		 *            The PCClass for which the given SkillCost for the given
		//		 *            Skill is being removed
		//		 * @param skill
		//		 *            The Skill for which the SkillCost is being removed
		//		 * @param sc
		//		 *            The SkillCost for the given Skill to be removed for the
		//		 *            given PCClass
		//		 * @param source
		//		 *            The source object which granted the given SkillCost for
		//		 *            the given Skill
		//		 */
		//		public void remove(PCClass cl, Skill skill, SkillCost sc,
		//			CDOMObject source)
		//		{
		//			Map<SkillCost, Map<Skill, Set<CDOMObject>>> scMap = map.get(cl);
		//			if (scMap == null)
		//			{
		//				return;
		//			}
		//			Map<Skill, Set<CDOMObject>> skMap = scMap.get(sc);
		//			if (skMap == null)
		//			{
		//				return;
		//			}
		//			Set<CDOMObject> set = skMap.get(skill);
		//			if (set == null)
		//			{
		//				return;
		//			}
		//			if (set.remove(source) && set.isEmpty())
		//			{
		//				skMap.remove(skill);
		//				if (skMap.isEmpty())
		//				{
		//					scMap.remove(sc);
		//					if (scMap.isEmpty())
		//					{
		//						map.remove(cl);
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

		/**
		 * Returns true if this CacheInfo has the given SkillCost for the given
		 * Skill for the given PCClass.
		 * 
		 * @param cl
		 *            The PCClass which will be checked to determine if it has
		 *            the requested SkillCost for the given Skill
		 * @param sc
		 *            The SkillCost to be checked to see if the CacheInfo has
		 *            this SkillCost for the given Skill on the given PCClass
		 * @param skill
		 *            The Skill which will be checked to determine if it
		 *            contains the requested SkillCost
		 * @return true if this CacheInfo has the given Skill Cost for the given
		 *         Skill for the given PCClass; false otherwise
		 * 
		 */
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

	private void add(CharID id, PCClass cl, Skill skill, SkillCost sc,
		CDOMObject source)
	{
		getConstructingInfo(id).add(cl, skill, sc, source);
	}

	private void removeAll(CharID id, CDOMObject source)
	{
		CacheInfo ci = getInfo(id);
		if (ci != null)
		{
			ci.removeAll(source);
		}
	}

	/**
	 * Returns true if this ListSkillCostFacet has the given SkillCost for the
	 * given Skill for the given PCClass for the Player Character identified by
	 * the given CharID.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character which will be
	 *            checked to determine if it contains the requested SkillCost
	 * @param cl
	 *            The PCClass which will be checked to determine if it has the
	 *            requested SkillCost for the given Skill
	 * @param sc
	 *            The SkillCost to be checked to see if the Player Character has
	 *            this SkillCost for the given Skill for the given PCClass
	 * @param sk
	 *            The Skill which will be checked to determine if it contains
	 *            the requested SkillCost
	 * @return true if this ListSkillCostFacet has the given Skill Cost for the
	 *         given Skill for the given PCClass for the Player Character
	 *         identified by the given CharID; false otherwise
	 * 
	 */
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

	/**
	 * Initializes the connections for LocalSkillCostFacet to other facets.
	 * 
	 * This method is automatically called by the Spring framework during
	 * initialization of the LocalSkillCostFacet.
	 */
	public void init()
	{
		classFacet.addDataFacetChangeListener(this);
		domainFacet.addDataFacetChangeListener(this);
		classLevelFacet.addDataFacetChangeListener(this);
	}

	/**
	 * Copies the contents of the LocalSkillCostFacet from one Player Character
	 * to another Player Character, based on the given CharIDs representing
	 * those Player Characters.
	 * 
	 * This is a method in LocalSkillCostFacet in order to avoid exposing the
	 * mutable CacheInfo object to other classes. This should not be inlined, as
	 * the CacheInfo is internal information to LocalSkillCostFacet and should
	 * not be exposed to other classes.
	 * 
	 * Note also the copy is a one-time event and no references are maintained
	 * between the Player Characters represented by the given CharIDs (meaning
	 * once this copy takes place, any change to the LocalSkillCostFacet of one
	 * Player Character will only impact the Player Character where the
	 * LocalSkillCostFacet was changed).
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
			for (Map.Entry<PCClass, Map<SkillCost, Map<Skill, Set<CDOMObject>>>> me : rci.map
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
							copyci.add(pcc, sk, sc, cdo);
						}
					}
				}
			}
		}
	}
}
