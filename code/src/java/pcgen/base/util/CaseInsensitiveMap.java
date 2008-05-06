/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.util;

import java.util.HashMap;

import pcgen.base.lang.CaseInsensitiveString;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A CaseInsensitiveMap is a HashMap that has uses a CaseInsensitiveString as
 * the Key This is a facilitating wrapper around HashMap to allow easy use of
 * CaseInsensitiveString as the Key to a Map.
 * 
 * CaseInsensitiveMap gracefully handles non-String keys (defaults back to the
 * same behavior as HashMap), including the null key. Note that this allows
 * appropriate matching behavior when a CaseInsensitiveString is passed in as
 * the key to one of the methods of CaseInsensitiveMap.
 * 
 * @param <V>
 *            The Type of the Values stored in this CaseInsensitiveMap
 */
public class CaseInsensitiveMap<V> extends HashMap<Object, V>
{
	/*
	 * Note this is forced to be HashMap<Object, V> not <CaseInsensitiveString,
	 * V> due to generics an ensuring appropriate method overrides without
	 * conflicts. If you don't believe that, try to rewrite the put method,
	 * maintaining @Override and also avoiding any cast. - thpr 6/27/07
	 */

	/**
	 * Used to resolve an incoming key to a CaseInsensitiveString, if
	 * appropriate.
	 * 
	 * @param key
	 *            The key to be resolved, if necessary
	 * @return The key used for storing objects in the HashMap
	 */
	private Object resolveObject(Object key)
	{
		return key instanceof String ? new CaseInsensitiveString((String) key)
			: key;
	}

	/**
	 * Returns true if the CaseInsensitiveMap contains the given key
	 * 
	 * @see java.util.HashMap#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(Object key)
	{
		return super.containsKey(resolveObject(key));
	}

	/**
	 * Returns the value stored in this CaseInsensitiveMap for the given key.
	 * 
	 * @see java.util.HashMap#get(java.lang.Object)
	 */
	@Override
	public V get(Object key)
	{
		return super.get(resolveObject(key));
	}

	/**
	 * Puts the given key/value pair into this CaseInsensitiveMap
	 * 
	 * @return the value previously mapped to this key or null if no value was
	 *         previously mapped to the given key
	 */
	@Override
	public V put(Object key, V value)
	{
		return super.put(resolveObject(key), value);
	}

	/**
	 * Removes the value stored in this CaseInsensitiveMap for the given key
	 * 
	 * @see java.util.HashMap#remove(java.lang.Object)
	 */
	@Override
	public V remove(Object key)
	{
		return super.remove(resolveObject(key));
	}
}
