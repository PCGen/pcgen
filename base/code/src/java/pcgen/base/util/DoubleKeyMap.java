/*
 * Copyright 2005, 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
import java.util.function.BiFunction;

/**
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
@SuppressWarnings("PMD.TooManyMethods")
public class DoubleKeyMap<K1, K2, V> implements Cloneable
{

	/**
	 * Stores the Class to be used as the underlying Map for the map from the
	 * first key of the DoubleKeyMap to the second underlying Map.
	 */
	@SuppressWarnings("rawtypes")
	private final Class<? extends Map> firstClass;

	/**
	 * Stores the Class to be used as the underlying Map for the map from the
	 * second key of the DoubleKeyMap to the value stored for the given keys.
	 */
	@SuppressWarnings("rawtypes")
	private final Class<? extends Map> secondClass;

	/**
	 * The internal Map to Map structure used to store the objects in this
	 * DoubleKeyMap.
	 */
	private Map<K1, Map<K2, V>> map;

	/**
	 * Identifies whether the primary map (the map field) should have the top
	 * level key removed if the inner map for that key is empty. In almost all
	 * situations, this is a useful cleanup to perform. The only exception to
	 * this is if the getReadOnlyMapFor method is called, in which case, it is
	 * "dangerous" to clean up and eliminate the outer map entry, since it will
	 * result in the returned view becoming detached from the DoubleKeyMap. This
	 * is thus automatically deactivated on any DoubleKeyMap instance where
	 * getReadOnlyMapFor is called.
	 * 
	 * Note: This only protects against AUTOMATIC cleanup, not *intentional
	 * destruction* caused by calling remove(K1) or clear()
	 */
	private boolean cleanup = true;

	/**
	 * Creates a new, empty DoubleKeyMap using HashMap as the underlying Map
	 * class for both the primary and secondary underlying Map.
	 */
	@SuppressWarnings("PMD.LooseCoupling")
	public DoubleKeyMap()
	{
		super();
		firstClass = HashMap.class;
		secondClass = firstClass;
		map = new HashMap<>();
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
	 *             if one or both of the given Classes does not have
	 *             a public, zero argument constructor.
	 */
	@SuppressWarnings("rawtypes")
	public DoubleKeyMap(Class<? extends Map> cl1, Class<? extends Map> cl2)
	{
		super();
		firstClass = Objects.requireNonNull(cl1);
		secondClass = Objects.requireNonNull(cl2);
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
	public DoubleKeyMap(DoubleKeyMap<K1, K2, V> otherMap)
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
		return map.computeIfAbsent(key1, k -> createLocalMap()).put(key2, value);
	}

	/**
	 * Copies the key/value combinations from the given DoubleKeyMap into this
	 * DoubleKeyMap. If this DoubleKeyMap already contained a mapping for the
	 * any of the key combinations in the given DoubleKeyMap, the previous value
	 * is overwritten.
	 * 
	 * No reference is maintained to the internal structure of the given
	 * DoubleKeyMap, so modifications to this Map are not reflected in the given
	 * Map (and vice versa). However, the Key and Value objects from the given
	 * Map are maintained by reference, so modification to the Keys or Values of
	 * either this Map or the given Map will be reflected in the other Map (this
	 * is consistent behavior with the analogous constructors in the
	 * java.util.Map implementations)
	 * 
	 * @param dkm
	 *            The DoubleKeyMap for which the key/value combinations should
	 *            be placed into this DoubleKeyMap
	 * @throws NullPointerException
	 *             if the given DoubleKeyMap is null
	 */
	public final void putAll(DoubleKeyMap<K1, K2, V> dkm)
	{
		for (Map.Entry<K1, Map<K2, V>> me : dkm.map.entrySet())
		{
			Map<K2, V> localMap = map.computeIfAbsent(me.getKey(), k -> createLocalMap());
			localMap.putAll(me.getValue());
		}
	}

	/**
	 * If the specified key is not already associated with a value (or is null), computes
	 * the value using the given BiFunction and puts the computed value into this map.
	 *
	 * @param key1
	 *            The primary key for storing the given value
	 * @param key2
	 *            The secondary key for storing the given value
	 * @param mappingFunction
	 *            The mappingFunction used to compute a new value for the given keys if no
	 *            current value is present (or the current vaule is null).
	 * @return The existing or computed value for the given keys
	 */
	public V computeIfAbsent(K1 key1, K2 key2,
		BiFunction<K1, K2, V> mappingFunction)
	{
		return map.computeIfAbsent(key1, k -> createLocalMap())
			.computeIfAbsent(key2, k2 -> mappingFunction.apply(key1, k2));
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
	 * Retrieves the Map from DoubleKeyMap for the given primary key. If this
	 * DoubleKeyMap does not a mapping for the given key, an empty map is
	 * returned.
	 * 
	 * This method is value-semantic in that no changes are made to the object
	 * passed into the method and ownership of the returned List is transferred
	 * to the class calling this method.
	 * 
	 * @param key1
	 *            The primary key for retrieving the map
	 * @return The map stored for the given key
	 */
	public Map<K2, V> getMapFor(K1 key1)
	{
		Map<K2, V> localMap = map.get(key1);
		Map<K2, V> copy = createLocalMap();
		if (localMap != null)
		{
			copy.putAll(localMap);
		}
		return copy;
	}

	/**
	 * Returns true if the DoubleKeyMap contains a map stored under the given
	 * primary key. This may include information stored under any secondary key
	 * OR a previous call to getReadOnlyMapFor(K1) with the same primary key
	 * provided to this method [and no subsequent call to remove(K1) or
	 * clear()].
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
	 * Returns true if the DoubleKeyMap contains a value for the given keys.
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
		return (localMap != null) && localMap.containsKey(key2);
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
		V removed = localMap.remove(key2);
		// cleanup!
		if (cleanup && localMap.isEmpty())
		{
			map.remove(key1);
		}
		return removed;
	}

	/**
	 * Removes all objects with the given primary key from the DoubleKeyMap.
	 * 
	 * This method is value-semantic in that no changes are made to the object
	 * passed into the method and ownership of the returned Map is transferred
	 * to the class calling this method (no reference to the returned Map is
	 * maintained by DoubleKeyMap)
	 * 
	 * As a side effect, detaches the view for any Map that was previously
	 * returned by getReadOnlyMapFor(K1) with the primary key given to this
	 * method.
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
	 * Returns a Set of the primary keys for this DoubleKeyMap. This set will
	 * include primary keys where there is information stored under any
	 * secondary key OR a previous call to getReadOnlyMapFor(K1) was made with
	 * that primary key [and no subsequent call to remove(K1) or clear()].
	 * 
	 * Note: Ownership of the Set is transferred to the calling Object;
	 * therefore, changes to the returned Set will NOT impact the DoubleKeyMap.
	 * However, changes to the underlying object will impact the objects
	 * contained within this DoubleKeyMap.
	 * 
	 * @return A Set of the primary keys for this DoubleKeyMap
	 */
	public Set<K1> getKeySet()
	{
		Set<K1> set = Collections.newSetFromMap(createGlobalMap());
		set.addAll(map.keySet());
		return set;
	}

	/**
	 * Returns a Set of the secondary keys for the given primary key in this
	 * DoubleKeyMap
	 * 
	 * Note: This Set is reference-semantic. The ownership of the Set is
	 * transferred to the calling Object; therefore, changes to the returned Set
	 * will NOT impact the DoubleKeyMap.
	 * 
	 * @param key1
	 *            The primary key to retrieve keys for.
	 * 
	 * @return A Set of secondary key objects for the given primary key.
	 */
	public Set<K2> getSecondaryKeySet(K1 key1)
	{
		Map<K2, V> localMap = map.get(key1);
		if (localMap == null)
		{
			return Collections.emptySet();
		}
		Set<K2> set = Collections.newSetFromMap(createLocalMap());
		set.addAll(localMap.keySet());
		return set;
	}

	/**
	 * Clears this DoubleKeyMap.
	 * 
	 * As a side effect, detaches the view for any Map that was previously
	 * returned by getReadOnlyMapFor(K1).
	 */
	public void clear()
	{
		cleanup = true;
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
		Map<K2, V> localMap = map.get(key1);
		if (localMap == null)
		{
			return Collections.emptySet();
		}
		return new HashSet<>(localMap.values());
	}

	/**
	 * Returns true if the DoubleKeyMap is empty; false otherwise
	 * 
	 * Note: This method evaluates information stored under any primary and
	 * secondary key OR a previous call to getReadOnlyMapFor(K1) for any primary
	 * key [and no subsequent call to remove(K1) or clear()].
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
	 * Note: This method evaluates information stored under any primary and
	 * secondary key OR a previous call to getReadOnlyMapFor(K1) for any primary
	 * key [and no subsequent call to remove(K1) or clear()].
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
	 * @throws CloneNotSupportedException
	 *             (should not be thrown)
	 */
	@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
	@Override
	public DoubleKeyMap<K1, K2, V> clone() throws CloneNotSupportedException
	{
		@SuppressWarnings("unchecked")
		DoubleKeyMap<K1, K2, V> dkm = (DoubleKeyMap<K1, K2, V>) super.clone();
		dkm.map = createGlobalMap();
		for (Map.Entry<K1, Map<K2, V>> me : map.entrySet())
		{
			/*
			 * Can be empty if a read-only view was previously captured, but we
			 * don't need to keep those around since nothing is attached to the
			 * copy
			 */
			if (!me.getValue().isEmpty())
			{
				dkm.map.put(me.getKey(), new HashMap<>(me.getValue()));
			}
		}
		//Nothing can be connected (see above)
		dkm.cleanup = true;
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
		Map<K2, V> localMap = map.get(key1);
		return (localMap != null) && localMap.values().remove(obj);
	}

	/**
	 * A consistent-with-equals hashCode for DoubleKeyMap.
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
	 */
	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof DoubleKeyMap)
			&& map.equals(((DoubleKeyMap<?, ?, ?>) obj).map);
	}

	/**
	 * Creates a new secondary map (map from the second key to the value of the
	 * DoubleKeyMap).
	 * 
	 * @return a new secondary map
	 */
	@SuppressWarnings("unchecked")
	private <MV> Map<K2, MV> createLocalMap()
	{
		try
		{
			return secondClass.getConstructor().newInstance();
		}
		catch (ReflectiveOperationException e)
		{
			throw new IllegalArgumentException(
				"Class for DoubleKeyMap must possess "
					+ "a public zero-argument constructor", e);
		}
	}

	/**
	 * Creates a new primary map (map from the first key to the map storing the
	 * second key and value).
	 * 
	 * @return a new primary map
	 */
	@SuppressWarnings("unchecked")
	private <MV> Map<K1, MV> createGlobalMap()
	{
		try
		{
			return firstClass.getConstructor().newInstance();
		}
		catch (ReflectiveOperationException e)
		{
			throw new IllegalArgumentException(
				"Class for DoubleKeyMap must possess "
					+ "a public zero-argument constructor", e);
		}
	}

	/**
	 * Returns a read-only map containing the submap for the primary key in this
	 * DoubleKeyMap.
	 * 
	 * The returned map is guaranteed to be a view into this DoubleKeyMap until
	 * remove(K1) [for the same primary key provided in this method] or clear()
	 * is called on this DoubleKeyMap. In either of those cases, since it was
	 * directly instructed to do so, the DoubleKeyMap releases knowledge of the
	 * map returned through this method and the returned view will no longer
	 * represent the contents of the DoubleKeyMap.
	 * 
	 * It is not the intent of this class to provide any notification if the
	 * view returned from this method is ever detached.
	 * 
	 * If containsKey(K1) would return false for the given primary key, then
	 * this initializes the map for the primary key given to this method. This
	 * allows calling this method prior to any information being loaded into the
	 * DoubleKeyMap for the given key, while maintaining the view of the inner
	 * map once information is finally loaded. As a result, after calling this
	 * method, containsKey will ALWAYS return true for the key given to this
	 * method [until remove(K1) is called for that key or until clear() is
	 * called].
	 * 
	 * Since the returned Map is read-only, the value here is in that it is a
	 * direct reference to the contents of this DoubleKeyMap, and is therefore
	 * reference-semantic (the contents of the returned map will change as the
	 * contents of this DoubleKeyMap are changed). Ownership of the returned Map
	 * is transferred to the caller, although since it is read-only, that is
	 * perhaps only relevant for determining the garbage collection time of the
	 * decorator that makes the returned Map an unmodifiable view into this
	 * DoubleKeyMap.
	 * 
	 * Note that while this is a read-only map, there is no guarantee that this
	 * returned map is thread-safe. Use in threaded situations with caution.
	 * 
	 * Note the use of this method changes the behavior of this DoubleKeyMap,
	 * meaning if this method is never called, getKeySet will only return keys
	 * for which there are values. Once this method is called (and until clear()
	 * is called), getKeySet will return keys for which there may be no values.
	 * 
	 * @param key1
	 *            The primary key for which the submap in this DoubleKeyMap
	 *            should be returned
	 * @return A read-only map containing the submap for the primary key in this
	 *         DoubleKeyMap.
	 */
	public Map<K2, V> getReadOnlyMapFor(K1 key1)
	{
		cleanup = false;
		Map<K2, V> localMap = map.computeIfAbsent(key1, k -> createLocalMap());
		return Collections.unmodifiableMap(localMap);
	}
}
