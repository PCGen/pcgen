/*
 * Copyright 2005, 2007 (C) Tom Parker <thpr@sourceforge.net>
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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author Thomas Parker
 * 
 * Represents a map where the objects are stored using two keys rather than the
 * traditional single key (single key is provided by the Map interface from
 * java.util).
 * 
 * This class protects its internal structure from modification, but
 * DoubleKeyMap is generally reference-semantic. HashMapToList will not modify
 * any of the Objects it is passed; however, it reserves the right to return
 * references to Objects it contains to other Objects.
 * 
 * In order to protect its internal structure, any Collection returned by the
 * methods of DoubleKeyMap (with the exception of actual keys or values that
 * happen to be Collections) is NOT associated with the DoubleKeyMap, and
 * modification of the returned Collection will not modify the internal
 * structure of DoubleKeyMap.
 * 
 * CAUTION: If you are not looking for the value-semantic protection of this
 * class (of preventing accidental modification of underlying parts of a two-key
 * Map structure, then this is a convenience method and is not appropriate for
 * use in Java 1.5 (Typed Collections are probably more appropriate).
 */
public class DoubleKeyMap<K1, K2, V> implements Cloneable
{

	/**
	 * The underlying map - of primary keys to Maps - for the DoubleKeyMap. This
	 * class protects its internal structure, so no method should ever return an
	 * object capable of modifying the maps. All modifications should be done
	 * through direct calls to the methods of DoubleKeyMap.
	 */
	private Map<K1, Map<K2, V>> map = new HashMap<K1, Map<K2, V>>();

	/**
	 * Constructs a new (empty) DoubleKeyMap
	 */
	public DoubleKeyMap()
	{
		super();
	}

	/**
	 * Constructs a new DoubleKeyMap, with the same contents as the given
	 * DoubleKeyMap.
	 * 
	 * The given DoubleKeyMap is not modified and the constructed DoubleKeyMap
	 * will be independent of the given DoubleKeyMap other than the Objects used
	 * to represent the keys and values. (In other words, modification of the
	 * given DoubleKeyMap will not alter the constructed DoubleKeyMap, and vice
	 * versa)
	 * 
	 * @param otherMap
	 *            The DoubleKeyMap whose contents should be copied into this
	 *            DoubleKeyMap.
	 */
	public DoubleKeyMap(final DoubleKeyMap<K1, K2, V> otherMap)
	{
		for (Entry<K1, Map<K2, V>> me : otherMap.map.entrySet())
		{
			map.put(me.getKey(), new HashMap<K2, V>(me.getValue()));
		}
	}

	/**
	 * Puts a new object into the DoubleKeyMap.
	 * 
	 * @param key1
	 *            The primary key used to store the value in this DoubleKeyMap.
	 * @param key2
	 *            The secondary key used to store the value in this
	 *            DoubleKeyMap.
	 * @param value
	 *            The value to be stored in this DoubleKeyMap.
	 * @return the Object previously stored in this DoubleKeyMap with the given
	 *         keys. null if this DoubleKeyMap did not previously have an object
	 *         stored with the given keys.
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
	 * Gets an object from the DoubleKeyMap.
	 * 
	 * @param key1
	 *            The primary key used to get the value in this DoubleKeyMap.
	 * @param key2
	 *            The secondary key used to get the value in this DoubleKeyMap.
	 * @param value
	 *            The value stored in this DoubleKeyMap for the given keys.
	 * @return the Object stored in this DoubleKeyMap for the given keys. null
	 *         if this DoubleKeyMap does not have an object stored with the
	 *         given keys.
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
	 * Returns true if an object is stored in this DoubleKeyMap for the given
	 * primary key.
	 * 
	 * @param key1
	 *            The primary key to be tested for containing a value in this
	 *            DoubleKeyMap.
	 * @return true if this DoubleKeyMap has an Object stored in this
	 *         DoubleKeyMap for the given primary key; false otherwise
	 */
	public boolean containsKey(K1 key1)
	{
		return map.containsKey(key1);
	}
	
	/**
	 * Returns true if an object is stored in this DoubleKeyMap for the given
	 * keys.
	 * 
	 * @param key1
	 *            The primary key to be tested for containing a value in this
	 *            DoubleKeyMap.
	 * @param key2
	 *            The secondary key to be tested for containing a value in this
	 *            DoubleKeyMap.
	 * @return true if this DoubleKeyMap has an Object stored in this
	 *         DoubleKeyMap for the given keys; false otherwise
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
	 * Removes all objects with the given primary key from the DoubleKeyMap.
	 * 
	 * @param key1
	 *            The primary key used to remove the value in this DoubleKeyMap.
	 * @return the Map of objects stored in this DoubleKeyMap for the given
	 *         primary keys. null if this DoubleKeyMap does not have an object
	 *         stored with the given primary key.
	 */
	public Map<K2, V> removeAll(K1 key1)
	{
		return map.remove(key1);
	}
	
	/**
	 * Removes an object from the DoubleKeyMap.
	 * 
	 * @param key1
	 *            The primary key used to remove the value in this DoubleKeyMap.
	 * @param key2
	 *            The secondary key used to remove the value in this DoubleKeyMap.
	 * @return the Object stored in this DoubleKeyMap for the given keys. null
	 *         if this DoubleKeyMap does not have an object stored with the
	 *         given keys.
	 */
	public V remove(K1 key1, K2 key2)
	{
		Map<K2, V> localMap = map.get(key1);
		if (localMap == null)
		{
			return null;
		}
		V o = localMap.remove(key2);
		/*
		 * Clean up the primary map if the secondary map is empty. This is
		 * required to avoid a false report from get*KeySet. Generally, if an
		 * object is added with the keys KEY1 and KEY2, then subsequently
		 * removed (and no other objects were stored with those keys), then
		 * getKeySet() should never return KEY1 (and there is a corollary for
		 * KEY2 cleanup, though that is implicit and does not require special
		 * code)
		 */
		if (localMap.isEmpty())
		{
			map.remove(key1);
		}
		return o;
	}

	/**
	 * Returns a Set which contains the primary keys for this DoubleKeyMap.
	 * Returns an empty Set if this DoubleKeyMap is empty (has no primary keys)
	 * 
	 * Ownership of the returned Set is transferred to the Object that called
	 * this method. Modification of the returned Set will not modify this
	 * DoubleKeyMap, and modification of this DoubleKeyMap will not alter the
	 * returned Set.
	 * 
	 * @return A Set containing the primary keys for this DoubleKeyMap.
	 */
	public Set<K1> getKeySet()
	{
		return new HashSet<K1>(map.keySet());
	}

	/**
	 * Returns a Set which contains the secondary keys for the given primary key
	 * within this DoubleKeyMap. Returns an empty Set if there are no objects
	 * stored in the DoubleKeyMap with the given primary key.
	 * 
	 * Ownership of the returned Set is transferred to the Object that called
	 * this method. Modification of the returned Set will not modify this
	 * DoubleKeyMap, and modification of this DoubleKeyMap will not alter the
	 * returned Set.
	 * 
	 * @return A Set containing the secondary keys for the given primary key
	 *         within this DoubleKeyMap.
	 */
	public Set<K2> getSecondaryKeySet(final K1 aPrimaryKey)
	{
		final Map<K2, V> localMap = map.get(aPrimaryKey);
		if (localMap == null)
		{
			return Collections.emptySet();
		}
		return new HashSet<K2>(localMap.keySet());
	}

	/**
	 * Clears this DoubleKeyMap.
	 */
	public void clear()
	{
		map.clear();
	}

	/**
	 * Returns true if the DoubleKeyMap is empty
	 * 
	 * @return true if the DoubleKeyMap is empty; false otherwise
	 */
	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	/**
	 * Clones this DoubleKeyMap. The contents of the DoubleKeyMap (the keys and
	 * values) are not cloned - this is not a truly deep clone. However, the
	 * internal structure of the DoubleKeyMap is sufficiently cloned in order to
	 * protect the internal structure of the original or the clone from being
	 * modified by the other object.
	 * 
	 * @return A clone of this DoubleKeyMap that contains the same keys and
	 *         values as the original DoubleKeyMap.
	 */
	@Override
	public DoubleKeyMap<K1, K2, V> clone() throws CloneNotSupportedException
	{
		/*
		 * This cast will cause a Generic type safety warning. This is
		 * impossible to avoid, given that super.clone() will not return a
		 * DoubleKeyMap with the proper Generic arguments. - Thomas Parker
		 * 1/15/07
		 */
		DoubleKeyMap<K1, K2, V> dkm = (DoubleKeyMap<K1, K2, V>) super.clone();
		/*
		 * This provides a semi-deep clone of the DoubleKeyMap, in order to
		 * protect the internal structure of the DoubleKeyMap from modification.
		 * Note the key and value objects are not cloned, so this is not truly a
		 * deep clone, but is deep enough to protect the internal structure.
		 */
		dkm.map = new HashMap<K1, Map<K2, V>>();
		for (Iterator<K1> it = map.keySet().iterator(); it.hasNext();)
		{
			K1 key = it.next();
			Map<K2, V> m = map.get(key);
			dkm.map.put(key, new HashMap<K2, V>(m));
		}
		return dkm;
	}
}
