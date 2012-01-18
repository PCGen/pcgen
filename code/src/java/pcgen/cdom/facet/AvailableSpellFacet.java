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

import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import pcgen.base.util.DoubleKeyMapToList;
import pcgen.base.util.HashMapToList;
import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.spell.Spell;

/**
 * AvailableSpellFacet is a Facet that tracks the Available Spells (and target
 * objects) that are contained in a Player Character.
 */
public class AvailableSpellFacet implements DataFacetChangeListener<CDOMObject>
{

	private static final Class<Spell> SPELL_CLASS = Spell.class;

	private PrerequisiteFacet prerequisiteFacet;

	/**
	 * Triggered when one of the Facets to which AvailableSpellFacet listens
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
		Collection<CDOMReference<? extends CDOMList<? extends PrereqObject>>> listrefs = cdo
				.getModifiedLists();
		CharID id = dfce.getCharID();
		for (CDOMReference<? extends CDOMList<? extends PrereqObject>> ref : listrefs)
		{
			processListRef(id, cdo, ref);
		}
	}

	private void processListRef(CharID id, CDOMObject cdo,
			CDOMReference<? extends CDOMList<? extends PrereqObject>> listref)
	{
		for (CDOMList<? extends PrereqObject> list : listref
				.getContainedObjects())
		{
			if (!list.getListClass().equals(SPELL_CLASS))
			{
				continue;
			}
			for (CDOMReference<Spell> objref : cdo
					.getListMods((CDOMReference<? extends CDOMList<Spell>>) listref))
			{
				for (AssociatedPrereqObject apo : cdo.getListAssociations(
						listref, objref))
				{
					Collection<Spell> spells = objref.getContainedObjects();
					addAll(id, (CDOMList<Spell>) list, spells, apo, cdo);
				}
			}
		}
	}

	/**
	 * Triggered when one of the Facets to which AvailableSpellFacet listens
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

	private <T extends PrereqObject> void addAll(CharID id,
			CDOMList<Spell> list, Collection<Spell> spells,
			AssociatedPrereqObject apo, CDOMObject cdo)
	{
		Map<CDOMList<Spell>, Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>> map = getConstructingCachedMap(id);
		Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>> subMap = map
				.get(list);
		boolean fireNew = (subMap == null);
		if (fireNew)
		{
			subMap = new HashMap<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>();
			map.put(list, subMap);
		}
		for (Spell spell : spells)
		{
			Map<AssociatedPrereqObject, Set<CDOMObject>> assocMap = subMap
					.get(spell);
			if (assocMap == null)
			{
				assocMap = new HashMap<AssociatedPrereqObject, Set<CDOMObject>>();
				subMap.put(spell, assocMap);
			}
			Set<CDOMObject> sources = assocMap.get(apo);
			if (sources == null)
			{
				sources = new WrappedMapSet<CDOMObject>(IdentityHashMap.class);
				assocMap.put(apo, sources);
			}
			sources.add(cdo);
		}
	}

	/**
	 * Returns the type-safe Map for this AbstractSourcedListFacet and the given
	 * CharID. May return null if no information has been set in this
	 * AbstractSourcedListFacet for the given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The Map is owned by
	 * AbstractSourcedListFacet, and since it can be modified, a reference to
	 * that object should not be exposed to any object other than
	 * AbstractSourcedListFacet.
	 * 
	 * @param id
	 *            The CharID for which the Set should be returned
	 * @return The Set for the Player Character represented by the given CharID;
	 *         null if no information has been set in this
	 *         AbstractSourcedListFacet for the Player Character.
	 */
	private Map<CDOMList<Spell>, Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>> getCachedMap(
			CharID id)
	{
		return (Map<CDOMList<Spell>, Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>>) FacetCache
				.get(id, getClass());
	}

	/**
	 * Returns a type-safe Map for this AvailableSpellFacet and the given
	 * CharID. Will return a new, empty Map if no information has been set in
	 * this AvailableSpellFacet for the given CharID. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The Map object is owned by
	 * AvailableSpellFacet, and since it can be modified, a reference to that
	 * object should not be exposed to any object other than
	 * AvailableSpellFacet.
	 * 
	 * @param id
	 *            The CharID for which the Map should be returned
	 * @return The Map for the Player Character represented by the given CharID.
	 */
	private Map<CDOMList<Spell>, Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>> getConstructingCachedMap(
			CharID id)
	{
		Map<CDOMList<Spell>, Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			componentMap = new HashMap<CDOMList<Spell>, Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>>();
			FacetCache.set(id, getClass(), componentMap);
		}
		return componentMap;
	}

	public void removeAll(CharID id, Object source)
	{
		Map<CDOMList<Spell>, Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>> listMap = getCachedMap(id);
		if (listMap != null)
		{
			for (Iterator<Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>> mit = listMap
					.values().iterator(); mit.hasNext();)
			{
				Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>> objMap = mit
						.next();
				for (Iterator<Map<AssociatedPrereqObject, Set<CDOMObject>>> ait = objMap
						.values().iterator(); ait.hasNext();)
				{
					Map<AssociatedPrereqObject, Set<CDOMObject>> apoMap = ait
							.next();
					for (Iterator<Set<CDOMObject>> sit = apoMap.values()
							.iterator(); sit.hasNext();)
					{
						Set<CDOMObject> set = sit.next();
						if (set.remove(source) && set.isEmpty())
						{
							sit.remove();
						}
					}
					if (apoMap.isEmpty())
					{
						ait.remove();
					}
				}
				if (objMap.isEmpty())
				{
					mit.remove();
				}
			}
		}
	}

	public HashMapToList<CDOMList<Spell>, Integer> getPCBasedLevelInfo(
			CharID id, Spell sp)
	{
		HashMapToList<CDOMList<Spell>, Integer> levelInfo = new HashMapToList<CDOMList<Spell>, Integer>();
		Map<CDOMList<Spell>, Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>> listMap = getCachedMap(id);
		if (listMap == null)
		{
			return levelInfo;
		}
		for (Map.Entry<CDOMList<Spell>, Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>> me : listMap
				.entrySet())
		{
			CDOMList<Spell> list = me.getKey();
			if (!(list instanceof ClassSpellList)
					&& !(list instanceof DomainSpellList))
			{
				continue;
			}
			Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>> spellMap = me
					.getValue();
			Map<AssociatedPrereqObject, Set<CDOMObject>> apoMap = spellMap
					.get(sp);
			if (apoMap == null)
			{
				continue;
			}
			for (Map.Entry<AssociatedPrereqObject, Set<CDOMObject>> ame : apoMap
					.entrySet())
			{
				AssociatedPrereqObject apo = ame.getKey();
				Set<CDOMObject> sources = ame.getValue();
				boolean passes = false;
				for (CDOMObject source : sources)
				{
					if (prerequisiteFacet.qualifies(id, apo, source))
					{
						passes = true;
						break;
					}
				}
				if (passes)
				{
					Integer lvl = apo
							.getAssociation(AssociationKey.SPELL_LEVEL);
					levelInfo.addToListFor(list, lvl);
				}
			}
		}
		return levelInfo;
	}

	public DoubleKeyMapToList<Spell, CDOMList<Spell>, Integer> getPCBasedLevelInfo(
			CharID id)
	{
		DoubleKeyMapToList<Spell, CDOMList<Spell>, Integer> levelInfo = new DoubleKeyMapToList<Spell, CDOMList<Spell>, Integer>();
		Map<CDOMList<Spell>, Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>> listMap = getCachedMap(id);
		if (listMap == null)
		{
			return levelInfo;
		}
		for (Map.Entry<CDOMList<Spell>, Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>> me : listMap
				.entrySet())
		{
			CDOMList<Spell> list = me.getKey();
			if (!(list instanceof ClassSpellList)
					&& !(list instanceof DomainSpellList))
			{
				continue;
			}
			for (Map.Entry<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>> sme : me
					.getValue().entrySet())
			{
				Spell sp = sme.getKey();
				for (Map.Entry<AssociatedPrereqObject, Set<CDOMObject>> ame : sme
						.getValue().entrySet())
				{
					AssociatedPrereqObject apo = ame.getKey();
					Set<CDOMObject> sources = ame.getValue();
					boolean passes = false;
					for (CDOMObject source : sources)
					{
						if (prerequisiteFacet.qualifies(id, apo, source))
						{
							passes = true;
							break;
						}
					}
					if (passes)
					{
						Integer lvl = apo
								.getAssociation(AssociationKey.SPELL_LEVEL);
						levelInfo.addToListFor(sp, list, lvl);
					}
				}
			}
		}
		return levelInfo;
	}

	public void setPrerequisiteFacet(PrerequisiteFacet prerequisiteFacet)
	{
		this.prerequisiteFacet = prerequisiteFacet;
	}

}
