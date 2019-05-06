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
package pcgen.cdom.facet.base;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

import pcgen.cdom.base.PCGenIdentifier;

public abstract class AbstractSubAssociationFacet<IDT extends PCGenIdentifier, S1, S2, A>
		extends AbstractStorageFacet<IDT>
{

	public A get(IDT id, S1 obj1, S2 obj2)
	{
		Objects.requireNonNull(obj1, "Object for getting association may not be null");
		Objects.requireNonNull(obj2, "Object for getting association may not be null");
		Map<S1, Map<S2, A>> map = getCachedMap(id);
		if (map == null)
		{
			return null;
		}
		Map<S2, A> subMap = map.get(obj1);
		if (subMap == null)
		{
			return null;
		}
		return subMap.get(obj2);
	}

	public boolean set(IDT id, S1 obj1, S2 obj2, A association)
	{
		Objects.requireNonNull(obj1, "Object to add may not be null");
		Objects.requireNonNull(obj2, "Object to add may not be null");
		Objects.requireNonNull(association, "Association may not be null");
		Map<S2, A> map = getConstructingCachedMap(id, obj1);
		A old = map.put(obj2, association);
		return old == null;
	}

	@SuppressWarnings("unchecked")
	protected Map<S1, Map<S2, A>> getCachedMap(IDT id)
	{
		return (Map<S1, Map<S2, A>>) getCache(id);
	}

	private Map<S2, A> getConstructingCachedMap(IDT id, S1 obj1)
	{
		Map<S1, Map<S2, A>> map = getCachedMap(id);
		if (map == null)
		{
			map = getComponentMap();
			setCache(id, map);
		}
		Map<S2, A> subMap = map.computeIfAbsent(obj1, k -> getSubComponentMap());
		return subMap;
	}

	protected <MV> Map<S1, MV> getComponentMap()
	{
		return new IdentityHashMap<>();
	}

	protected <MV> Map<S2, MV> getSubComponentMap()
	{
		return new IdentityHashMap<>();
	}

	@Override
	public void copyContents(IDT source, IDT destination)
	{
		Map<S1, Map<S2, A>> sourceMap = getCachedMap(source);
		if (sourceMap != null)
		{
			for (Map.Entry<S1, Map<S2, A>> me : sourceMap.entrySet())
			{
				getConstructingCachedMap(destination, me.getKey()).putAll(me.getValue());
			}
		}
	}

}
