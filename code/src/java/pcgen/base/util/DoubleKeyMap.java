/*
 * Copyright 2005, 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
 * DoubleKeyMap is generally reference-semantic. DoubleKeyMap will not modify
 * any of the Objects it is passed; however, it reserves the right to return
 * references to Objects it contains to other Objects.
 * 
 * In order to protect its internal structure, any Collection returned by the
 * methods of DoubleKeyMap (with the exception of actual keys or values that
 * happen to be Collections) is NOT associated with the DoubleKeyMap, and
 * modification of the returned Collection will not modify the internal
 * structure of DoubleKeyMap.
 * 
 * Since DoubleKeyMap leverages existing classes that implement java.util.Map,
 * it also inherits any limitations on those classes. For example, if the
 * underlying Map is a java.util.HashMap, then modifying an object in this set
 * to alter the hashCode of that object may result in unpredictable behavior
 * from the DoubleKeyMap. Be careful to read the documentation on the underlying
 * Map class to ensure appropriate treatment of objects placed in the
 * DoubleKeyMap.
 * 
 * CAUTION: If you are not looking for the value-semantic protection of this
 * class (of preventing accidental modification of underlying parts of a two-key
 * Map structure, then this is a convenience method and is not appropriate for
 * use in Java 1.5 (Typed Collections are probably more appropriate).
 * 
 * @param <K1>
 *            The Class of the primary key for this DoubleKeyMap
 * @param <K2>
 *            The Class of the secondary key for this DoubleKeyMap
 * @param <V>
 *            The Class of the Value for this DoubleKeyMap
 */
public class DoubleKeyMap<K1, K2, V> implements Cloneable
{

	/**
	 * Stores the Class to be used as the underlying Map for the map from the
	 * first key of the DoubleKeyMap to the second underlying Map
	 */
	private final Class<? extends Map> firstClass;

	/**
	 * Stores the Class to be used as the underlying Map for the map from the
	 * second key of the DoubleKeyMap to the value stored for the given keys
	 */
	private final Class<? extends Map> secondClass;

	/**
	 * The internal Map to Map structure used to store the objects in this
	 * DoubleKeyMap
	 */
	private Map<K1, Map<K2, V>> map;

	/**
	 * Creates a new, empty DoubleKeyMap using HashMap as the underlying Map
	 * class for both the primary and secondary underlying Map
	 */
	public DoubleKeyMap()
	{
		super();
		firstClass = HashMap.class;
		secondClass = firstClass;
		map = new HashMap<K1, Map<K2, V>>();
	}

	/**
	 * Creates a new, empty DoubleKeyMap using the given classes as the
	 * underlying Map classes for the primary and secondary underlying Maps. The
	 * given Classes MUST have public, zero-argument constructors.
	 * 
	 * @param cl1
	 *            The Class to be used for the primary underlying map
	 * @param cl2
	 *            The Class to be used for the secondary underlying map
	 * @throws IllegalArgumentException
	 *             if one or both of the given Classes is null or does not have
	 *             a public, zero argument constructor.
	 */
	public DoubleKeyMap(Class<? extends Map> cl1, Class<? extends Map> cl2)
	{
		super();
		if (cl1 == null)
		{
			throw new IllegalArgumentException(
					"First underlying Class cannot be null for DoubleKeyMap");
		}
		if (cl2 == null)
		{
			throw new IllegalArgumentException(
					"Second underlying Class cannot be null for DoubleKeyMap");
		}
		firstClass = cl1;
		secondClass = cl2;
		map = createGlobalMap();
		/*
		 * This "useless" call is designed to exercise the code to ensure that
		 * the given class meets the restrictions imposed by DoubleKeyMap
		 * (public, zero-argument constructor)
		 */
		createLocalMap();
	}

	/**
	 * Constructs a new DoubleKeyMap with the same mappings and underlying
	 * classes as the given DoubleKeyMap.
	 * 
	 * No reference is maintained to the internal structure of the given
	 * DoubleKeyMap, so modifications to this Map are not reflected in the given
	 * Map (and vice versa). However, the Key and Value objects from the given
	 * Map are maintained by reference, so modification to the Keys or Values of
	 * either this Map or the given Map will be reflected in the other Map (this
	 * is consistent behavior with the analogous constructors in the
	 * java.util.Map implementations)
	 * 
	 * @param otherMap
	 *            The DoubleKeyMap to use as a source of mappings for
	 *            initializing this DoubleKeyMap
	 * @throws NullPointerException
	 *             if the given DoubleKeyMap is null
	 */
	public DoubleKeyMap(final DoubleKeyMap<K1, K2, V> otherMap)
	{
		this(otherMap.firstClass, otherMap.secondClass);
		putAll(otherMap);
	}

	/**
	 * Put the given value into this DoubleKeyMap for the given keys. If this
	 * DoubleKeyMap already contained a mapping for the given keys, the previous
	 * value is returned. Otherwise, null is returned.
	 * 
	 * @param key1
	 *            The primary key for storing the given value
	 * @param key2
	 *            The secondary key for storing the given value
	 * @param value
	 *            The value to be stored for the given keys
	 * @return Object The previous value stored for the given keys; null if the
	 *         given keys did not previously have a mapping
	 */
	public V put(K1 key1, K2 key2, V value)
	{
		Map<K2, V> localMap = map.get(key1);
		if (localMap == null)
		{
			localMap = createLocalMap();
			map.put(key1, localMap);
		}
		return localMap.put(key2, value);
	}

	/**
	 * Copies the key/value combinations from the given DoubleKeyMap into this
	 * DoubleKeyMap. If this DoubleKeyMap already contained a mapping for the
	 * any of the key combinations in the given DoubleKeyMap, the previous value
	 * is overwritten.
	 * 
	 * @param dkm
	 *            The DoubleKeyMap for which the key/value combinations should
	 *            be placed into this DoubleKeyMap
	 * @throws NullPointerException
	 *             if the given DoubleKeyMap is null
	 */
	public final void putAll(DoubleKeyMap<K1, K2, V> dkm)
	{
		for (Entry<K1, Map<K2, V>> me : dkm.map.entrySet())
		{
			Map<K2, V> localMap = map.get(me.getKey());
			if (localMap == null)
			{
				localMap = createLocalMap();
				map.put(me.getKey(), localMap);
			}
			localMap.putAll(me.getValue());
		}
	}

	/**
	 * Get the value from DoubleKeyMap for the given keys. If this DoubleKeyMap
	 * does not a mapping for the given keys, null is returned.
	 * 
	 * @param key1
	 *            The primary key for retrieving the given value
	 * @param key2
	 *            The secondary key for retrieving the given value
	 * @return Object The value stored for the given keys
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
	 * Retrieves the Map from DoubleKeyMap for the given primary key. If this DoubleKeyMap
	 * does not a mapping for the given key, an empty map is returned.
	 * 
	 * @param key1
	 *            The primary key for retrieving the map
	 * @return Map<K2, V> The map stored for the given key
	 */
	public Map<K2, V> getMapFor(K1 key1)
	{
		Map<K2, V> localMap = map.get(key1);
		Map<K2, V> copy = new HashMap<K2, V>();
		if (localMap != null)
		{
			copy.putAll(localMap);
		}
		return copy;
	}

	/**
	 * Returns trus if the DoubleKeyMap contains a value stored under the given
	 * primary key and any secondary key.
	 * 
	 * @param key1
	 *            The primary key for retrieving the given value
	 * @return true If a value is in the map under the given primary key
	 */
	public boolean containsKey(K1 key1)
	{
		return map.containsKey(key1);
	}

	/**
	 * Returns trus if the DoubleKeyMap contains a value for the given keys.
	 * 
	 * @param key1
	 *            The primary key for retrieving the given value
	 * @param key2
	 *            The secondary key for retrieving the given value
	 * @return true If a value is in the map given two keys
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
	 * Removes the value from DoubleKeyMap for the given keys and returns the
	 * value that was removed from the DoubleKeyMap. If this DoubleKeyMap did
	 * not have a mapping for the given keys, null is returned.
	 * 
	 * @param key1
	 *            The primary key for retrieving the given value
	 * @param key2
	 *            The secondary key for retrieving the given value
	 * @return Object The value previously mapped to the given keys
	 */
	public V remove(K1 key1, K2 key2)
	{
		Map<K2, V> localMap = map.get(key1);
		if (localMap == null)
		{
			return null;
		}
		V o = localMap.remove(key2);
		// cleanup!
		if (localMap.isEmpty())
		{
			map.remove(key1);
		}
		return o;
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
	 * Returns a Set of the primary keys for this DoubleKeyMap
	 * 
	 * Note: This Set is reference-semantic. The ownership of the Set is
	 * transferred to the calling Object; therefore, changes to the returned Set
	 * will NOT impact the DoubleKeyMap.
	 * 
	 * @return A Set of the primary keys for this DoubleKeyMap
	 */
	public Set<K1> getKeySet()
	{
		return new WrappedMapSet<K1>(firstClass, map.keySet());
	}

	/**
	 * Returns a Set of the secondary keys for the given primary key in this
	 * DoubleKeyMap
	 * 
	 * Note: This Set is reference-semantic. The ownership of the Set is
	 * transferred to the calling Object; therefore, changes to the returned Set
	 * will NOT impact the DoubleKeyMap.
	 * 
	 * @param aPrimaryKey
	 *            The primary key to retrieve keys for.
	 * 
	 * @return A <tt>Set</tt> of secondary key objects for the given primary
	 *         key.
	 */
	public Set<K2> getSecondaryKeySet(final K1 aPrimaryKey)
	{
		final Map<K2, V> localMap = map.get(aPrimaryKey);
		if (localMap == null)
		{
			return Collections.emptySet();
		}
		return new WrappedMapSet<K2>(secondClass, localMap.keySet());
	}

	/**
	 * Clears this DoubleKeyMap
	 */
	public void clear()
	{
		map.clear();
	}

	/**
	 * Returns a Set of the values stored in this DoubleKeyMap for the given
	 * primary key.
	 * 
	 * Note: This Set is reference-semantic. The ownership of the Set is
	 * transferred to the calling Object; therefore, changes to the returned Set
	 * will NOT impact the DoubleKeyMap.
	 * 
	 * @param key1
	 *            The primary key for which the values will be returned
	 * @return a Set of the values stored in this DoubleKeyMap for the given
	 *         primary key
	 */
	public Set<V> values(K1 key1)
	{
		final Map<K2, V> localMap = map.get(key1);
		if (localMap == null)
		{
			return Collections.emptySet();
		}
		return new HashSet<V>(localMap.values());
	}

	/**
	 * Returns true if the DoubleKeyMap is empty; false otherwise
	 * 
	 * @return true if the DoubleKeyMap is empty; false otherwise
	 */
	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	/**
	 * Returns the number of primary keys in this DoubleKeyMap
	 * 
	 * @return the number of primary keys in this DoubleKeyMap
	 */
	public int primaryKeyCount()
	{
		return map.size();
	}

	/**
	 * Produces a clone of the DoubleKeyMap. This means the internal maps used
	 * to store keys and values are not shared between the original DoubleKeyMap
	 * and the clone (modifying one DoubleKeyMap will not impact the other).
	 * However, this does not perform a true "deep" clone, in the sense that the
	 * actual keys and values are not cloned.
	 * 
	 * @see java.lang.Object#clone()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public DoubleKeyMap<K1, K2, V> clone() throws CloneNotSupportedException
	{
		DoubleKeyMap<K1, K2, V> dkm = (DoubleKeyMap<K1, K2, V>) super.clone();
		dkm.map = createGlobalMap();
		for (Map.Entry<K1, Map<K2, V>> me : map.entrySet())
		{
			dkm.map.put(me.getKey(), new HashMap<K2, V>(me.getValue()));
		}
		return dkm;
	}

	/**
	 * Removes the given value from DoubleKeyMap for the given primary key.
	 * Returns true if there was a mapping removed for the given value under the
	 * given primary key.
	 * 
	 * @param key1
	 *            The primary key for removing the given value
	 * @param obj
	 *            The object stored under the given primary key (and any
	 *            secondary key) to be removed from the DoubleKeyMap.
	 * @return Object true if there was a mapping removed for the given value
	 *         under the given primary key; false otherwise
	 */
	public boolean removeValue(K1 key1, V obj)
	{
		final Map<K2, V> localMap = map.get(key1);
		if (localMap != null)
		{
			return localMap.values().remove(obj);
		}
		return false;
	}

	/**
	 * Returns true if the DoubleKeyMap is empty
	 * 
	 * @return true if the DoubleKeyMap is empty; false otherwise
	 * 
	 * @deprecated This is bad form in checking for Collection - I mean, should
	 *             this be infinitely recursive? Users who are using Collection
	 *             should really be using DoubleKeyMapToList and adding deepSize
	 *             in that class
	 */
	public int deepSize()
	{
		int size = 0;
		for (K1 key1 : map.keySet())
		{
			for (K2 key2 : getSecondaryKeySet(key1))
			{
				Object val = get(key1, key2);
				if (val instanceof Collection)
				{
					size += ((Collection<?>) val).size();
				}
				else
				{
					size++;
				}
			}
		}
		return size;
	}

	/**
	 * A consistent-with-equals hashCode for DoubleKeyMap
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return map.hashCode();
	}

	/**
	 * Returns true if the DoubleKeyMap is equal to the given Object. Equality
	 * is defined as the given Object being a DoubleKeyMap with equal keys and
	 * values as defined by the underlying Maps.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		return o instanceof DoubleKeyMap
				&& map.equals(((DoubleKeyMap<?, ?, ?>) o).map);
	}

	/**
	 * Creates a new secondary map (map from the second key to the value of the
	 * DoubleKeyMap)
	 * 
	 * @return a new secondary map
	 */
	private Map<K2, V> createLocalMap()
	{
		try
		{
			return secondClass.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new IllegalArgumentException(
					"Class for DoubleKeyMap must possess a zero-argument constructor",
					e);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalArgumentException(
					"Class for DoubleKeyMap must possess a public zero-argument constructor",
					e);
		}
	}

	/**
	 * Creates a new primary map (map from the first key to the map storing the
	 * second key and value)
	 * 
	 * @return a new primary map
	 */
	private Map<K1, Map<K2, V>> createGlobalMap()
	{
		try
		{
			return firstClass.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new IllegalArgumentException(
					"Class for DoubleKeyMap must possess a zero-argument constructor",
					e);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalArgumentException(
					"Class for DoubleKeyMap must possess a public zero-argument constructor",
					e);
		}
	}
}
