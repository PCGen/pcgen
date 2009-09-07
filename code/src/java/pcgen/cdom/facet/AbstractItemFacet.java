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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A AbstractItemFacet is a DataFacet that contains information about
 * CDOMObjects that are contained in a PlayerCharacter when a PlayerCharacter
 * may have only one of that type of CDOMObject (e.g. Race, Deity). This is not
 * used for CDOMObjects where the PlayerCharacter may possesse more than one of
 * that type of object (e.g. Template, Language)
 */
public abstract class AbstractItemFacet<T extends CDOMObject> extends
		AbstractDataFacet<T>
{
	private final Class<?> thisClass = getClass();

	public void set(CharID id, T obj)
	{
		T old = get(id);
		if (old != null)
		{
			fireDataFacetChangeEvent(id, old, DataFacetChangeEvent.DATA_REMOVED);
		}
		FacetCache.set(id, thisClass, obj);
		fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_ADDED);
	}

	public void remove(CharID id)
	{
		T old = (T) FacetCache.remove(id, thisClass);
		if (old != null)
		{
			fireDataFacetChangeEvent(id, old, DataFacetChangeEvent.DATA_REMOVED);
		}
	}

	public T get(CharID id)
	{
		return (T) FacetCache.get(id, thisClass);
	}

	public boolean matches(CharID id, T obj)
	{
		T current = get(id);
		return (obj == null && current == null)
				|| (obj != null && obj.equals(current));
	}
}
