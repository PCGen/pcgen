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
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.FollowerOption;

/**
 * FollowerOptionFacet is a Facet that tracks the FollowerOptions that have been
 * granted to a Player Character.
 */
public class FollowerOptionFacet extends AbstractStorageFacet implements
		DataFacetChangeListener<CDOMObject>
{
	private CDOMObjectConsolidationFacet consolidationFacet;

	/**
	 * Triggered when one of the Facets to which FollowerOptionFacet listens
	 * fires a DataFacetChangeEvent to indicate a FollowerOption was added to a
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
		List<FollowerOption> lists = cdo.getListFor(ListKey.COMPANIONLIST);
		if (lists != null)
		{
			addAll(dfce.getCharID(), lists, cdo);
		}
	}

	/**
	 * Triggered when one of the Facets to which FollowerOptionFacet listens
	 * fires a DataFacetChangeEvent to indicate a FollowerOption was removed
	 * from a Player Character.
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

	private void addAll(CharID id, List<FollowerOption> list, CDOMObject cdo)
	{
		for (FollowerOption fo : list)
		{
			add(id, fo, cdo);
		}
	}

	private void add(CharID id, FollowerOption fo, CDOMObject cdo)
	{
		if (fo == null)
		{
			throw new IllegalArgumentException("Object to add may not be null");
		}
		String name = fo.getListRef().getName();
		Map<FollowerOption, Set<CDOMObject>> foMap = getConstructingCachedMap(
				id, name);
		Set<CDOMObject> set = foMap.get(fo);
		if (set == null)
		{
			set = new WrappedMapSet<CDOMObject>(IdentityHashMap.class);
			foMap.put(fo, set);
		}
		set.add(cdo);
	}

	private void removeAll(CharID id, CDOMObject source)
	{
		CaseInsensitiveMap<Map<FollowerOption, Set<CDOMObject>>> componentMap = getCachedMap(id);
		if (componentMap != null)
		{
			for (Iterator<Map<FollowerOption, Set<CDOMObject>>> it = componentMap
					.values().iterator(); it.hasNext();)
			{
				Map<FollowerOption, Set<CDOMObject>> foMap = it.next();
				for (Iterator<Set<CDOMObject>> it2 = foMap.values().iterator(); it2
						.hasNext();)
				{
					Set<CDOMObject> set = it2.next();
					if (set.remove(source) && set.isEmpty())
					{
						it2.remove();
					}
				}
				if (foMap.isEmpty())
				{
					it.remove();
				}
			}
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
	private CaseInsensitiveMap<Map<FollowerOption, Set<CDOMObject>>> getCachedMap(
			CharID id)
	{
		return (CaseInsensitiveMap<Map<FollowerOption, Set<CDOMObject>>>) getCache(
			id, getClass());
	}

	/**
	 * Returns a type-safe Map for this AbstractSourcedListFacet and the given
	 * CharID. Will return a new, empty Map if no information has been set in
	 * this AbstractSourcedListFacet for the given CharID. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The Map object is owned by
	 * AbstractSourcedListFacet, and since it can be modified, a reference to
	 * that object should not be exposed to any object other than
	 * AbstractSourcedListFacet.
	 * 
	 * @param id
	 *            The CharID for which the Map should be returned
	 * @return The Map for the Player Character represented by the given CharID.
	 */
	private Map<FollowerOption, Set<CDOMObject>> getConstructingCachedMap(
			CharID id, String name)
	{
		CaseInsensitiveMap<Map<FollowerOption, Set<CDOMObject>>> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			componentMap = new CaseInsensitiveMap<Map<FollowerOption, Set<CDOMObject>>>();
			setCache(id, getClass(), componentMap);
		}
		Map<FollowerOption, Set<CDOMObject>> foMap = componentMap.get(name);
		if (foMap == null)
		{
			foMap = new IdentityHashMap<FollowerOption, Set<CDOMObject>>();
			componentMap.put(name, foMap);
		}
		return foMap;
	}

	public Map<FollowerOption, CDOMObject> getAvailableFollowers(CharID id,
			String type, Comparator<FollowerOption> comp)
	{
		CaseInsensitiveMap<Map<FollowerOption, Set<CDOMObject>>> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			return Collections.emptyMap();
		}
		Map<FollowerOption, Set<CDOMObject>> foMap = componentMap.get(type);
		if (foMap == null)
		{
			return Collections.emptyMap();
		}
		Map<FollowerOption, CDOMObject> ret = new TreeMap<FollowerOption, CDOMObject>(
				comp);
		for (Map.Entry<FollowerOption, Set<CDOMObject>> me : foMap.entrySet())
		{
			FollowerOption fo = me.getKey();
			Set<CDOMObject> target = me.getValue();
			Collection<FollowerOption> expanded = fo.getExpandedOptions();
			for (CDOMObject source : target)
			{
				for (FollowerOption efo : expanded)
				{
					/*
					 * TODO This is a bug, and will overwrite the first source :(
					 */
					ret.put(efo, source);
				}
			}
		}
		return ret;
	}

	public void setConsolidationFacet(CDOMObjectConsolidationFacet consolidationFacet)
	{
		this.consolidationFacet = consolidationFacet;
	}
	
	public void init()
	{
		consolidationFacet.addDataFacetChangeListener(this);
	}

	@Override
	public void copyContents(CharID source, CharID copy)
	{
		CaseInsensitiveMap<Map<FollowerOption, Set<CDOMObject>>> map = getCachedMap(source);
		if (map != null)
		{
			for (Map<FollowerOption, Set<CDOMObject>> fm : map.values())
			{
				for (Map.Entry<FollowerOption, Set<CDOMObject>> fme : fm.entrySet())
				{
					FollowerOption fl = fme.getKey();
					for (CDOMObject cdo : fme.getValue())
					{
						add(copy, fl, cdo);
					}
				}
			}
		}
	}
}
