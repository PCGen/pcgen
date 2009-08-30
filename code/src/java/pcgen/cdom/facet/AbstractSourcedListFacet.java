/*
 * Copyright (c) Thomas Parker, 2009.
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
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A AbstractListFacet is a DataFacet that contains information about
 * CDOMObjects that are contained in a PlayerCharacter when a PlayerCharacter
 * may have more than one of that type of CDOMObject (e.g. Language, PCTemplate)
 * and the source of that object should be tracked.
 */
public abstract class AbstractSourcedListFacet<T extends CDOMObject> extends
		AbstractDataFacet<T>
{
	public void add(CharID id, T obj, Object source)
	{
		Map<T, Set<Object>> map = getConstructingCachedMap(id);
		Set<Object> set = map.get(obj);
		boolean fireNew = (set == null);
		if (fireNew)
		{
			set = new WrappedMapSet<Object>(IdentityHashMap.class);
			map.put(obj, set);
		}
		set.add(source);
		if (fireNew)
		{
			fireGraphNodeChangeEvent(id, obj, DataFacetChangeEvent.DATA_ADDED);
		}
	}

	public void addAll(CharID id, Collection<T> c, Object source)
	{
		for (T obj : c)
		{
			add(id, obj, source);
		}
	}

	public void remove(CharID id, T obj, Object source)
	{
		Map<T, Set<Object>> componentMap = getCachedSet(id);
		if (componentMap != null)
		{
			processRemoval(id, componentMap, obj, source);
		}
	}

	public void removeAll(CharID id, Collection<T> c, Object source)
	{
		Map<T, Set<Object>> componentMap = getCachedSet(id);
		if (componentMap != null)
		{
			for (T obj : c)
			{
				processRemoval(id, componentMap, obj, source);
			}
		}
	}

	public Map<T, Set<Object>> removeAll(CharID id)
	{
		Map<T, Set<Object>> componentMap = getCachedSet(id);
		if (componentMap != null)
		{
			FacetCache.remove(id, getClass());
			for (T obj : componentMap.keySet())
			{
				fireGraphNodeChangeEvent(id, obj,
						DataFacetChangeEvent.DATA_REMOVED);
			}
		}
		return componentMap;
	}

	public Set<T> getSet(CharID id)
	{
		Map<T, Set<Object>> componentMap = getCachedSet(id);
		if (componentMap == null)
		{
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(componentMap.keySet());
	}

	public int getCount(CharID id)
	{
		Map<T, Set<Object>> componentMap = getCachedSet(id);
		if (componentMap == null)
		{
			return 0;
		}
		return componentMap.size();
	}

	public boolean isEmpty(CharID id)
	{
		Map<T, Set<Object>> componentMap = getCachedSet(id);
		return componentMap == null || componentMap.isEmpty();
	}

	public boolean contains(CharID id, T obj)
	{
		Map<T, Set<Object>> componentMap = getCachedSet(id);
		return componentMap != null && componentMap.containsKey(obj);
	}

	private Set<Object> getConstructingCachedSetFor(CharID id, T obj)
	{
		Map<T, Set<Object>> map = getConstructingCachedMap(id);
		Set<Object> set = map.get(obj);
		if (set == null)
		{
			set = new WrappedMapSet<Object>(IdentityHashMap.class);
			map.put(obj, set);
		}
		return set;
	}

	private Map<T, Set<Object>> getCachedSet(CharID id)
	{
		return (Map<T, Set<Object>>) FacetCache.get(id, getClass());
	}

	private Map<T, Set<Object>> getConstructingCachedMap(CharID id)
	{
		Map<T, Set<Object>> componentMap = getCachedSet(id);
		if (componentMap == null)
		{
			componentMap = new IdentityHashMap<T, Set<Object>>();
			FacetCache.set(id, getClass(), componentMap);
		}
		return componentMap;
	}

	public void copyContents(CharID source, CharID destination)
	{
		Map<T, Set<Object>> sourceMap = getCachedSet(source);
		if (sourceMap != null)
		{
			for (Map.Entry<T, Set<Object>> me : sourceMap.entrySet())
			{
				Set<Object> sourceSet = me.getValue();
				if (sourceSet != null)
				{
					T obj = me.getKey();
					Set<Object> targetSet = getConstructingCachedSetFor(
							destination, obj);
					targetSet.addAll(sourceSet);
				}
			}
		}
	}

	private void processRemoval(CharID id, Map<T, Set<Object>> componentMap,
			T obj, Object source)
	{
		Set<Object> set = componentMap.get(obj);
		if (set != null)
		{
			set.remove(source);
			if (set.isEmpty())
			{
				componentMap.put(obj, null);
				fireGraphNodeChangeEvent(id, obj,
						DataFacetChangeEvent.DATA_REMOVED);
			}
		}
	}

}
