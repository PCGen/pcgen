/*
 * Copyright 2005 (C) Tom Parker <thpr@sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on Jun 16, 2005
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package pcgen.util;

import java.util.*;

/**
 * A map representation for a dual key entity
 * @param <K1> 
 * @param <K2> 
 * @param <V> 
 */
public class DoubleKeyMap<K1, K2, V> implements Cloneable
{

	private Map<K1, Map<K2, V>> map = new HashMap<K1, Map<K2, V>>();

	/**
	 * Constructor
	 */
	public DoubleKeyMap()
	{
		super();
	}

	public DoubleKeyMap( final DoubleKeyMap<K1, K2, V> otherMap )
	{
		map.putAll(otherMap.map);
	}
	
	/**
	 * Put an object in a map
	 * @param key1
	 * @param key2
	 * @param value
	 * @return Object
	 */
	public V put(K1 key1, K2 key2, V value)
	{
		Map<K2, V> localMap = map.get(key1);
		if (localMap == null)
		{
			localMap = new HashMap<K2, V>();
			map.put(key1, localMap);
		}
		return localMap.put(key2, value);
	}

	/**
	 * Get an object from the map
	 * @param key1
	 * @param key2
	 * @return Object
	 */
	public V get(K1 key1, K2 key2)
	{
		Map<K2, V> localMap = map.get(key1);
		if (localMap == null)
		{
			return null;
		}
		return localMap.get(key2);
	}

	/**
	 * Returns true if an object is in the map given two keys
	 * @param key1
	 * @param key2
	 * @return true if an object is in the map given two keys
	 */
	public boolean containsKey(K1 key1, K2 key2)
	{
		Map<K2, V> localMap = map.get(key1);
		if (localMap == null)
		{
			return false;
		}
		return localMap.containsKey(key2);
	}

	/**
	 * Remove the object from the map
	 * @param key1
	 * @param key2
	 * @return Object
	 */
	public V remove(K1 key1, K2 key2)
	{
		Map<K2, V> localMap = map.get(key1);
		if (localMap == null)
		{
			return null;
		}
		V o = localMap.remove(key2);
		//cleanup!
		if (localMap.isEmpty())
		{
			map.remove(key1);
		}
		return o;
	}

	/**
	 * Get the Set of keys
	 * @return set of keys
	 */
	public Set<K1> getKeySet()
	{
		return new HashSet<K1>(map.keySet());
	}

	/**
	 * Gets a <tt>Set</tt> of the secondary keys for the given primary key.
	 * 
	 * @param aPrimaryKey The primary key to retrieve keys for.
	 * 
	 * @return A <tt>Set</tt> of secondary key objects.
	 */
	public Set<K2> getSecondaryKeySet(final K1 aPrimaryKey)
	{
		final Map<K2, V> localMap = map.get(aPrimaryKey);
		if ( localMap == null )
		{
			return Collections.emptySet();
		}
		return new HashSet<K2>(localMap.keySet());
	}
	
	/**
	 * Clear
	 */
	public void clear()
	{
		map.clear();
	}

	/**
	 * Returns true if the map is empty
	 * @return true if the map is empty
	 */
	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	public Object clone() throws CloneNotSupportedException
	{
		DoubleKeyMap<K1, K2, V> dkm = (DoubleKeyMap<K1, K2, V>) super.clone();
		dkm.map = new HashMap<K1, Map<K2, V>>();
		for (Iterator<K1> it = map.keySet().iterator(); it.hasNext();) {
			K1 key = it.next();
			Map<K2, V> m = map.get(key);
			dkm.map.put(key, new HashMap<K2, V>(m));
		}
		return dkm;
	}
}
