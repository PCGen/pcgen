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

import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.enumeration.CharID;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * FacetCache is a container for cache objects that can be stored by the Facets
 * that process information about PlayerCharacters
 */
public class FacetCache
{

	private static final DoubleKeyMap<CharID, Class<?>, Object> cache = new DoubleKeyMap<CharID, Class<?>, Object>();

	public static Object get(CharID id, Class<?> cl)
	{
		return cache.get(id, cl);
	}

	public static Object set(CharID id, Class<?> cl, Object o)
	{
		return cache.put(id, cl, o);
	}

	public static Object remove(CharID id, Class<?> cl)
	{
		return cache.remove(id, cl);
	}
}
