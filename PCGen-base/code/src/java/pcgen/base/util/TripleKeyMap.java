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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a map where the objects are stored using three keys rather than
 * the traditional single key (single key is provided by the Map interface from
 * java.util).
 * 
 * This class protects its internal structure from modification, but
 * TripleKeyMap is generally reference-semantic. TripleKeyMap will not modify
 * any of the Objects it is passed; however, it reserves the right to return
 * references to Objects it contains to other Objects.
 * 
 * In order to protect its internal structure, any Collection returned by the
 * methods of TripleKeyMap (with the exception of actual keys or values that
 * happen to be Collections) is NOT associated with the TripleKeyMap, and
 * modification of the returned Collection will not modify the internal
 * structure of TripleKeyMap.
 * 
 * CAUTION: If you are not looking for the value-semantic protection of this
 * class (of preventing accidental modification of underlying parts of a
 * three-key Map structure, then this is a convenience method and is not
 * appropriate for use in Java 1.5 (Typed Collections are probably more
 * appropriate).
 * 
 * @param <K1>
 *            The Class of the primary key for this TripleKeyMap
 * @param <K2>
 *            The Class of the secondary key for this TripleKeyMap
 * @param <K3>
 *            The Class of the tertiary key for this TripleKeyMap
 * @param <V>
 *            The Class of the Value for this TripleKeyMap
 */
public class TripleKeyMap<K1, K2, K3, V> implements Cloneable
{

	/**
	 * Stores the Class to be used as the underlying Map for the map from the
	 * third key of the TripleKeyMapToList to the value stored for the given
	 * keys.
	 */
	@SuppressWarnings("rawtypes")
	private final Class<? extends Map> thirdClass;

	/**
	 * The underlying map - of primary keys to Maps - for the TripleKeyMap. This
	 * class protects its internal structure, so no method should ever return an
	 * object capable of modifying the maps. All modifications should be done
	 * through direct calls to the methods of TripleKeyMap.
	 */
	private DoubleKeyMap<K1, K2, Map<K3, V>> map;

	/**
	 * Constructs a new (empty) TripleKeyMap.
	 */
	@SuppressWarnings("PMD.LooseCoupling")
	public TripleKeyMap()
	{
		thirdClass = HashMap.class;
		map = new DoubleKeyMap<>(HashMap.class, HashMap.class);
	}

	/**
	 * Constructs a new (empty) TripleKeyMap.
	 * 
	 * All of the classes provided as parameters must be non-null, extend Map, and
	 * must have a public zero argument constructor.
	 * 
	 * @param cl1
	 *            The Class to be used for the first of the underlying maps for
	 *            the TripleKeyMap
	 * @param cl2
	 *            The Class to be used for the second of the underlying maps for
	 *            the TripleKeyMap
	 * @param cl3
	 *            The Class to be used for the third of the underlying maps for
	 *            the TripleKeyMap
	 */
	@SuppressWarnings("rawtypes")
	public TripleKeyMap(Class<? extends Map> cl1, Class<? extends Map> cl2,
		Class<? extends Map> cl3)
	{
		super();
		map = new DoubleKeyMap<>(cl1, cl2);
		thirdClass = Objects.requireNonNull(cl3);
		/*
		 * This "useless" call is designed to exercise the code to ensure that
		 * the given class meets the restrictions imposed by TripleKeyMapToList
		 * (public, zero-argument constructor)
		 */
		createLocalMap();
	}

	/**
	 * Puts a new object into the TripleKeyMap.
	 * 
	 * This method is reference-semantic and this TripleKeyMap will maintain a
	 * strong reference to both the key object and the value object given as
	 * arguments to this method.
	 * 
	 * @param key1
	 *            The primary key used to store the value in this TripleKeyMap.
	 * @param key2
	 *            The secondary key used to store the value in this
	 *            TripleKeyMap.
	 * @param key3
	 *            The tertiary key used to store the value in this TripleKeyMap.
	 * @param value
	 *            The value to be stored in this TripleKeyMap.
	 * @return the Object previously stored in this TripleKeyMap with the given
	 *         keys. null if this TripleKeyMap did not previously have an object
	 *         stored with the given keys.
	 */
	public V put(K1 key1, K2 key2, K3 key3, V value)
	{
		Map<K3, V> localMap = map.get(key1, key2);
		if (localMap == null)
		{
			localMap = createLocalMap();
			map.put(key1, key2, localMap);
		}
		return localMap.put(key3, value);
	}

	/**
	 * Gets an object from the TripleKeyMap.
	 * 
	 * @param key1
	 *            The primary key used to get the value in this TripleKeyMap.
	 * @param key2
	 *            The secondary key used to get the value in this TripleKeyMap.
	 * @param key3
	 *            The tertiary key used to get the value in this TripleKeyMap.
	 * @return the Object stored in this TripleKeyMap for the given keys. null
	 *         if this TripleKeyMap does not have an object stored with the
	 *         given keys.
	 */
	public V get(K1 key1, K2 key2, K3 key3)
	{
		Map<K3, V> localMap = map.get(key1, key2);
		return (localMap == null) ? null : localMap.get(key3);
	}

	/**
	 * Returns true if an object is stored in this TripleKeyMap for the given
	 * keys.
	 * 
	 * @param key1
	 *            The primary key to be tested for containing a value in this
	 *            TripleKeyMap.
	 * @param key2
	 *            The secondary key to be tested for containing a value in this
	 *            TripleKeyMap.
	 * @param key3
	 *            The tertiary key to be tested for containing a value in this
	 *            TripleKeyMap.
	 * @return true if this TripleKeyMap has an Object stored in this
	 *         TripleKeyMap for the given keys; false otherwise
	 */
	public boolean containsKey(K1 key1, K2 key2, K3 key3)
	{
		Map<K3, V> localMap = map.get(key1, key2);
		return (localMap != null) && localMap.containsKey(key3);
	}

	/**
	 * Removes an object from the TripleKeyMap.
	 * 
	 * @param key1
	 *            The primary key used to remove the value in this TripleKeyMap.
	 * @param key2
	 *            The secondary key used to remove the value in this
	 *            TripleKeyMap.
	 * @param key3
	 *            The tertiary key used to remove the value in this
	 *            TripleKeyMap.
	 * @return the Object stored in this TripleKeyMap for the given keys. null
	 *         if this TripleKeyMap does not have an object stored with the
	 *         given keys.
	 */
	public V remove(K1 key1, K2 key2, K3 key3)
	{
		Map<K3, V> localMap = map.get(key1, key2);
		if (localMap == null)
		{
			return null;
		}
		V removed = localMap.remove(key3);
		/*
		 * Clean up the primary maps if the secondary maps are empty. This is
		 * required to avoid a false report from get*KeySet. Generally, if an
		 * object is added with the keys KEY1 and KEY2, then subsequently
		 * removed (and no other objects were stored with those keys), then
		 * getKeySet() should never return KEY1 (and there is a corollary for
		 * KEY2 cleanup, though that is implicit and does not require special
		 * code)
		 */
		if (localMap.isEmpty())
		{
			map.remove(key1, key2);
		}
		return removed;
	}

	/**
	 * Returns a Set which contains the primary keys for this TripleKeyMap.
	 * Returns an empty Set if this TripleKeyMap is empty (has no primary keys)
	 * 
	 * Ownership of the returned Set is transferred to the Object that called
	 * this method. Modification of the returned Set will not modify this
	 * TripleKeyMap, and modification of this TripleKeyMap will not alter the
	 * returned Set.
	 * 
	 * @return A Set containing the primary keys for this TripleKeyMap.
	 */
	public Set<K1> getKeySet()
	{
		return map.getKeySet();
	}

	/**
	 * Returns a Set which contains the secondary keys for the given primary key
	 * within this TripleKeyMap. Returns an empty Set if there are no objects
	 * stored in the TripleKeyMap with the given primary key.
	 * 
	 * Ownership of the returned Set is transferred to the Object that called
	 * this method. Modification of the returned Set will not modify this
	 * TripleKeyMap, and modification of this TripleKeyMap will not alter the
	 * returned Set.
	 * 
	 * @param key1
	 *            The primary key used to identify the secondary Key Set in this
	 *            TripleKeyMap.
	 * @return A Set containing the secondary keys for the given primary key
	 *         within this TripleKeyMap.
	 */
	public Set<K2> getSecondaryKeySet(K1 key1)
	{
		return map.getSecondaryKeySet(key1);
	}

	/**
	 * Returns a Set which contains the tertiary keys for the given primary key
	 * within this TripleKeyMap. Returns an empty Set if there are no objects
	 * stored in the TripleKeyMap with the given primary key.
	 * 
	 * Ownership of the returned Set is transferred to the Object that called
	 * this method. Modification of the returned Set will not modify this
	 * TripleKeyMap, and modification of this TripleKeyMap will not alter the
	 * returned Set.
	 * 
	 * @param key1
	 *            The primary key used to identify the Tertiary Key Set in this
	 *            TripleKeyMap.
	 * @param key2
	 *            The secondary key used to identify the Tertiary Key Set in
	 *            this TripleKeyMap.
	 * @return A Set containing the Tertiary keys for the given primary and
	 *         secondary keys within this TripleKeyMap.
	 */
	public Set<K3> getTertiaryKeySet(K1 key1, K2 key2)
	{
		Map<K3, V> localMap = map.get(key1, key2);
		return (localMap == null) ? Collections.emptySet()
			: new HashSet<>(localMap.keySet());
	}

	/**
	 * Clears this TripleKeyMap.
	 */
	public void clear()
	{
		map.clear();
	}

	/**
	 * Returns true if the TripleKeyMap is empty.
	 * 
	 * @return true if the TripleKeyMap is empty; false otherwise
	 */
	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	/**
	 * Returns the number of primary keys in this TripleKeyMap.
	 * 
	 * @return the number of primary keys in this TripleKeyMap
	 */
	public int firstKeyCount()
	{
		return map.primaryKeyCount();
	}

	/**
	 * Clones this TripleKeyMap. The contents of the TripleKeyMap (the keys and
	 * values) are not cloned - this is not a truly deep clone. However, the
	 * internal structure of the TripleKeyMap is sufficiently cloned in order to
	 * protect the internal structure of the original or the clone from being
	 * modified by the other object.
	 * 
	 * @return A clone of this TripleKeyMap that contains the same keys and
	 *         values as the original TripleKeyMap.
	 * @throws CloneNotSupportedException
	 *             in the rare case that the super class actually throws the
	 *             exception
	 */
	@Override
	public TripleKeyMap<K1, K2, K3, V> clone()
		throws CloneNotSupportedException
	{
		/*
		 * This cast will cause a Generic type safety warning. This is
		 * impossible to avoid, given that super.clone() will not return a
		 * TripleKeyMap with the proper Generic arguments. - Thomas Parker
		 * 1/26/07
		 */
		@SuppressWarnings("unchecked")
		TripleKeyMap<K1, K2, K3, V> tkm =
				(TripleKeyMap<K1, K2, K3, V>) super.clone();
		/*
		 * This provides a semi-deep clone of the TripleKeyMap, in order to
		 * protect the internal structure of the TripleKeyMap from modification.
		 * Note the key and value objects are not cloned, so this is not truly a
		 * deep clone, but is deep enough to protect the internal structure.
		 */
		tkm.map = new DoubleKeyMap<>();
		for (K1 key1 : map.getKeySet())
		{
			for (K2 key2 : map.getSecondaryKeySet(key1))
			{
				Map<K3, V> local = map.get(key1, key2);
				for (Map.Entry<K3, V> me : local.entrySet())
				{
					tkm.put(key1, key2, me.getKey(), me.getValue());
				}
			}
		}
		return tkm;
	}

	/**
	 * Returns a Set of the values stored in this TripleKeyMap for the given
	 * primary and secondary keys.
	 * 
	 * The ownership of the Set is transferred to the calling Object; therefore,
	 * changes to the returned Set will NOT impact the TripleKeyMap. However,
	 * changes to the underlying keys can impact this TripleKeyMap.
	 * 
	 * @param key1
	 *            The primary key for which the values will be returned
	 * @param key2
	 *            The secondary key for which the values will be returned
	 * @return a Set of the values stored in this TripleKeyMap for the given
	 *         primary and secondary keys
	 */
	public Set<V> values(K1 key1, K2 key2)
	{
		Map<K3, V> localMap = map.get(key1, key2);
		return (localMap == null) ? Collections.emptySet()
			: new HashSet<>(localMap.values());
	}

	/**
	 * A consistent-with-equals hashCode for TripleKeyMap.
	 */
	@Override
	public int hashCode()
	{
		return map.hashCode();
	}

	/**
	 * Returns true if the TripleKeyMap is equal to the given Object. Equality
	 * is defined as the given Object being a TripleKeyMap with equal keys and
	 * values as defined by the underlying Maps.
	 */
	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof TripleKeyMap)
			&& map.equals(((TripleKeyMap<?, ?, ?, ?>) obj).map);
	}

	/**
	 * Creates a new local map (map from the third key to the value of the
	 * TripleKeyMap).
	 * 
	 * @return a new local map
	 */
	@SuppressWarnings("unchecked")
	private Map<K3, V> createLocalMap()
	{
		try
		{
			return thirdClass.getConstructor().newInstance();
		}
		catch (ReflectiveOperationException e)
		{
			throw new IllegalArgumentException(
				"Class for TripleKeyMap must possess "
					+ "a public zero-argument constructor", e);
		}
	}

}
