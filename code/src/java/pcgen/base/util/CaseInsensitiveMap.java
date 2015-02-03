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
 * A CaseInsensitiveMap is a HashMap that has uses a CaseInsensitiveString as
 * the Key This is a facilitating wrapper around HashMap to allow easy use of
 * CaseInsensitiveString as the Key to a Map.
 * 
 * CaseInsensitiveMap gracefully handles non-String keys (defaults back to the
 * same behavior as HashMap), including the null key. Note that this allows
 * appropriate matching behavior when a CaseInsensitiveString is passed in as
 * the key to one of the methods of CaseInsensitiveMap.
 * 
 * NOTE: It is generally preferred to use new TreeMap<String,
 * blah>(String.CASE_INSENSITIVE_ORDER) when dealing with case insensitive Maps.
 * This should only be avoided in rare cases where performance is an issue in
 * fetching items from large maps. In reality, it may be best to remove
 * CaseInsensitiveMap from use entirely.
 * 
 * **WARNING**: This class could be considered to be broken or incomplete.
 * Unfortunately, in the case of the iterator methods of Map (e.g. keySet()),
 * this class will expose the CaseInsensitiveString that is used internally to
 * this Class. It is therefore a poor implementation to use when iteration over
 * the entries in this Map is requried.
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
	 * Returns true if the CaseInsensitiveMap contains the given key.
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
	 * Puts the given key/value pair into this CaseInsensitiveMap.
	 * 
	 * @param key
	 *            The key indicating the location in this CaseInsensitiveMap
	 *            where the given value should be stored
	 * @param value
	 *            The value to be stored in this CaseInsensensitiveMap under the
	 *            given key
	 * @return the value previously mapped to this key or null if no value was
	 *         previously mapped to the given key
	 */
	@Override
	public V put(Object key, V value)
	{
		return super.put(resolveObject(key), value);
	}

	/**
	 * Removes the value stored in this CaseInsensitiveMap for the given key.
	 * 
	 * @see java.util.HashMap#remove(java.lang.Object)
	 */
	@Override
	public V remove(Object key)
	{
		return super.remove(resolveObject(key));
	}
}
