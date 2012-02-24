package pcgen.cdom.facet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.Skill;

/**
 * ListSkillCostFacet processes SkillCosts associated with the MONCSKILL and
 * MONCCSKILL tokens.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class ListSkillCostFacet extends AbstractStorageFacet implements
		DataFacetChangeListener<CDOMObject>
{
	private final Class<?> thisClass = getClass();

	private RaceFacet raceFacet;

	/**
	 * Adds the SkillCost objects granted by CDOMObjects, as applied directly to
	 * a ClassSkillList, when a CDOMObject is added to a Player Character.
	 * 
	 * Triggered when one of the Facets to which ListSkillCostFacet listens
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
		for (CDOMReference ref : cdo.getModifiedLists())
		{
			List<ClassSkillList> useList = new ArrayList<ClassSkillList>();
			for (Object list : ref.getContainedObjects())
			{
				if (list instanceof ClassSkillList)
				{
					useList.add((ClassSkillList) list);
				}
			}
			if (!useList.isEmpty())
			{
				Collection<CDOMReference<Skill>> mods = cdo.getListMods(ref);
				for (CDOMReference<Skill> skRef : mods)
				{
					for (AssociatedPrereqObject apo : cdo.getListAssociations(
							ref, skRef))
					{
						SkillCost sc = apo
								.getAssociation(AssociationKey.SKILL_COST);
						for (ClassSkillList csl : useList)
						{
							for (Skill skill : skRef.getContainedObjects())
							{
								add(id, csl, skill, sc, cdo);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Removes the SkillCost objects granted by CDOMObjects, as applied directly
	 * to a ClassSkillList, when a CDOMObject is removed from a Player
	 * Character.
	 * 
	 * Triggered when one of the Facets to which ListSkillCostFacet listens
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
	 * Returns the type-safe CacheInfo for this ListSkillCostFacet and the given
	 * CharID. Will return a new, empty CacheInfo if no Skill information has
	 * been set for the given CharID. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The CacheInfo object is owned
	 * by ListSkillCostFacet, and since it can be modified, a reference to that
	 * object should not be exposed to any object other than ListSkillCostFacet.
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
	 * CacheInfo is the data structure used by LocalSkillCostFacet to store a
	 * Player Character's Skill Costs
	 */
	private static class CacheInfo
	{
		Map<ClassSkillList, Map<SkillCost, Map<Skill, Set<CDOMObject>>>> map = new IdentityHashMap<ClassSkillList, Map<SkillCost, Map<Skill, Set<CDOMObject>>>>();

		/**
		 * Adds the given SkillCost for the given Skill (as granted by the given
		 * source) on the given ClassSkillList to this CacheInfo
		 * 
		 * @param cl
		 *            The ClassSkillList which will be checked to determine if
		 *            it contains the requested SkillCost for the given Skill
		 * @param skill
		 *            The Skill for which the SkillCost is being added
		 * @param sc
		 *            The SkillCost for the given Skill to be added to this
		 *            CacheInfo
		 * @param source
		 *            The source object which granted the given SkillCost for
		 *            the given Skill
		 */
		public void add(ClassSkillList cl, Skill skill, SkillCost sc,
				CDOMObject source)
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

		//		public void remove(ClassSkillList cl, Skill skill, SkillCost sc,
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
		 * Skill on the given ClassSkillList.
		 * 
		 * @param cl
		 *            The ClassSkillList which will be checked to determine if
		 *            it contains the requested SkillCost for the given Skill
		 * @param sc
		 *            The SkillCost to be checked to see if the CacheInfo has
		 *            this SkillCost for the given Skill on the given
		 *            ClassSkillList
		 * @param sk
		 *            The Skill which will be checked to determine if it
		 *            contains the requested SkillCost
		 * @return true if this CacheInfo has the given Skill Cost for the given
		 *         Skill on the given ClassSkillList; false otherwise
		 * 
		 */
		public boolean contains(ClassSkillList cl, SkillCost sc, Skill skill)
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

	private void add(CharID id, ClassSkillList cl, Skill skill, SkillCost sc,
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
	 * given Skill on the given ClassSkillList for the Player Character
	 * identified by the given CharID.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character which will be
	 *            checked to determine if it contains the requested SkillCost
	 * @param cl
	 *            The ClassSkillList which will be checked to determine if it
	 *            contains the requested SkillCost for the given Skill
	 * @param sc
	 *            The SkillCost to be checked to see if the Player Character has
	 *            this SkillCost for the given Skill on the given ClassSkillList
	 * @param sk
	 *            The Skill which will be checked to determine if it contains
	 *            the requested SkillCost
	 * @return true if this ListSkillCostFacet has the given Skill Cost for the
	 *         given Skill on the given ClassSkillList for the Player Character
	 *         identified by the given CharID; false otherwise
	 * 
	 */
	public boolean contains(CharID id, ClassSkillList cl, SkillCost sc, Skill sk)
	{
		CacheInfo ci = getInfo(id);
		return ci != null && ci.contains(cl, sc, sk);
	}

	public void setRaceFacet(RaceFacet raceFacet)
	{
		this.raceFacet = raceFacet;
	}

	/**
	 * Initializes the connections for ListSkillCostFacet to other facets.
	 * 
	 * This method is automatically called by the Spring framework during
	 * initialization of the ListSkillCostFacet.
	 */
	public void init()
	{
		raceFacet.addDataFacetChangeListener(this);
	}

	/**
	 * Copies the contents of the ListSkillCostFacet from one Player Character
	 * to another Player Character, based on the given CharIDs representing
	 * those Player Characters.
	 * 
	 * This is a method in ListSkillCostFacet in order to avoid exposing the
	 * mutable CacheInfo object to other classes. This should not be inlined, as
	 * the CacheInfo is internal information to ListSkillCostFacet and should
	 * not be exposed to other classes.
	 * 
	 * Note also the copy is a one-time event and no references are maintained
	 * between the Player Characters represented by the given CharIDs (meaning
	 * once this copy takes place, any change to the ListSkillCostFacet of one
	 * Player Character will only impact the Player Character where the
	 * ListSkillCostFacet was changed).
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
			for (Map.Entry<ClassSkillList, Map<SkillCost, Map<Skill, Set<CDOMObject>>>> me : rci.map
				.entrySet())
			{
				ClassSkillList csl = me.getKey();
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
							copyci.add(csl, sk, sc, cdo);
						}
					}
				}
			}
		}
	}
}
