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
import java.util.LinkedHashSet;
import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A AbstractListFacet is a DataFacet that contains information about
 * CDOMObjects that are contained in a PlayerCharacter when a PlayerCharacter
 * may have more than one of that type of CDOMObject (e.g. Language,
 * PCTemplate). This is not used for CDOMObjects where the PlayerCharacter only
 * possesses one of that type of object (e.g. Race, Deity)
 */
public abstract class AbstractListFacet<T extends CDOMObject> extends
		AbstractDataFacet<T>
{
	private final Class<?> thisClass = getClass();

	public void add(CharID id, T obj)
	{
		if (getConstructingCachedSet(id).add(obj))
		{
			fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_ADDED);
		}
	}

	public void addAll(CharID id, Collection<T> c)
	{
		Set<T> set = getConstructingCachedSet(id);
		for (T obj : c)
		{
			if (set.add(obj))
			{
				fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_ADDED);
			}
		}
	}

	public void remove(CharID id, T obj)
	{
		Set<T> componentSet = getCachedSet(id);
		if (componentSet != null)
		{
			if (componentSet.remove(obj))
			{
				fireDataFacetChangeEvent(id, obj,
						DataFacetChangeEvent.DATA_REMOVED);
			}
		}
	}

	public void removeAll(CharID id, Collection<T> c)
	{
		Set<T> componentSet = getCachedSet(id);
		if (componentSet != null)
		{
			for (T obj : c)
			{
				if (componentSet.remove(obj))
				{
					fireDataFacetChangeEvent(id, obj,
							DataFacetChangeEvent.DATA_REMOVED);
				}
			}
		}
	}

	public Set<T> removeAll(CharID id)
	{
		Set<T> componentSet = (Set<T>) FacetCache.remove(id, thisClass);
		if (componentSet == null)
		{
			return Collections.emptySet();
		}
		for (T obj : componentSet)
		{
			fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_REMOVED);
		}
		return componentSet;
	}

	public Set<T> getSet(CharID id)
	{
		Set<T> componentSet = getCachedSet(id);
		if (componentSet == null)
		{
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(componentSet);
	}

	public int getCount(CharID id)
	{
		Set<T> componentSet = getCachedSet(id);
		if (componentSet == null)
		{
			return 0;
		}
		return componentSet.size();
	}

	public boolean isEmpty(CharID id)
	{
		Set<T> componentSet = getCachedSet(id);
		return componentSet == null || componentSet.isEmpty();
	}

	public boolean contains(CharID id, T obj)
	{
		Set<T> componentSet = getCachedSet(id);
		return componentSet != null && componentSet.contains(obj);
	}

	private Set<T> getCachedSet(CharID id)
	{
		return (Set<T>) FacetCache.get(id, thisClass);
	}

	private Set<T> getConstructingCachedSet(CharID id)
	{
		Set<T> componentSet = getCachedSet(id);
		if (componentSet == null)
		{
			componentSet = new LinkedHashSet<T>();
			FacetCache.set(id, thisClass, componentSet);
		}
		return componentSet;
	}
}
