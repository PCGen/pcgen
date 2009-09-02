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

import pcgen.cdom.enumeration.CharID;

public class WeightFacet
{

	private final Class<?> thisClass = getClass();

	public void setWeight(CharID id, Integer obj)
	{
		FacetCache.set(id, thisClass, obj);
	}

	public void removeWeight(CharID id)
	{
		FacetCache.remove(id, thisClass);
	}

	public Integer getWeight(CharID id)
	{
		return (Integer) FacetCache.get(id, thisClass);
	}

	public boolean matchesWeight(CharID id, Integer h)
	{
		Integer current = getWeight(id);
		return (h == null && current == null)
				|| (h != null && h.equals(current));
	}
}
