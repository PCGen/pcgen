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

public class ListSkillCostFacet extends AbstractStorageFacet implements
		DataFacetChangeListener<CDOMObject>
{
	private final Class<?> thisClass = getClass();

	private RaceFacet raceFacet;

	/**
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
	 * Triggered when one of the Facets to which ShieldProfFacet listens fires a
	 * DataFacetChangeEvent to indicate a ShieldProf was removed from a Player
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

		public void remove(ClassSkillList cl, Skill skill, SkillCost sc,
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

	public void add(CharID id, ClassSkillList cl, Skill skill, SkillCost sc,
			CDOMObject source)
	{
		getConstructingInfo(id).add(cl, skill, sc, source);
	}

	public void remove(CharID id, ClassSkillList cl, Skill skill, SkillCost sc,
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

	public boolean contains(CharID id, ClassSkillList cl, SkillCost sc, Skill sk)
	{
		CacheInfo ci = getInfo(id);
		return ci != null && ci.contains(cl, sc, sk);
	}

	public void setRaceFacet(RaceFacet raceFacet)
	{
		this.raceFacet = raceFacet;
	}

	public void init()
	{
		raceFacet.addDataFacetChangeListener(this);
	}
	
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
