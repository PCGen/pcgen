/*
 * Copyright 2008-18 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * Creates a Map which is intended to only possess a given Key or Value one
 * time. Each Key references a single Value and each Value may only be
 * referenced by a single Key. In this way, any Value can be determined uniquely
 * for any Key and any Key can be determined uniquely for any Value.
 * 
 * @param <V>
 *            The Class of the Value for this KeyMap
 */
@SuppressWarnings("PMD.TooManyMethods")
public class KeyMap<V>
{

	/**
	 * The underlying map used to store references from the Keys to the Values.
	 */
	private final Map<String, V> forwardMap;

	/**
	 * The underlying map used to store references from the Values back to the
	 * Keys.
	 */
	private final Map<V, String> reverseMap;

	/**
	 * Creates a new, empty DoubleKeyMap using HashMap as the underlying Map
	 * class for both the primary and secondary underlying Map.
	 */
	public KeyMap()
	{
		forwardMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		reverseMap = new IdentityHashMap<>();
	}

	/**
	 * Clears the KeyMap (removes all keys and values).
	 */
	public void clear()
	{
		forwardMap.clear();
		reverseMap.clear();
	}

	/**
	 * Returns true if the KeyMap contains the given Key.
	 * 
	 * @param key
	 *            the Key to be tested to determine if it is present in the
	 *            KeyMap
	 * @return true if the KeyMap contains the given Key; false otherwise
	 */
	public boolean containsKey(Object key)
	{
		return forwardMap.containsKey(key);
	}

	/**
	 * Returns true if the KeyMap contains the given Value.
	 * 
	 * @param value
	 *            the Value to be tested to determine if it is present in the
	 *            KeyMap
	 * @return true if the KeyMap contains the given Value; false otherwise
	 */
	public boolean containsValue(Object value)
	{
		return reverseMap.containsKey(value);
	}

	/**
	 * Returns the Value in the KeyMap for the given Key.
	 * 
	 * @param key
	 *            the Key for which the Value should be returned
	 * @return V the Value stored in the KeyMap for the given Key; null if the
	 *         given Key is not contained within the KeyMap
	 */
	public V get(Object key)
	{
		return forwardMap.get(key);
	}

	/**
	 * Returns the Key in the KeyMap for the given Value.
	 * 
	 * @param key
	 *            the Value for which the Key should be returned
	 * @return V the Key in the KeyMap for the given Value; null if the given
	 *         Value is not contained within the KeyMap
	 */
	public String getKeyFor(Object key)
	{
		return reverseMap.get(key);
	}

	/**
	 * Returns true if the KeyMap is empty; false otherwise.
	 * 
	 * @return true if the KeyMap is empty; false otherwise
	 */
	public boolean isEmpty()
	{
		return forwardMap.isEmpty();
	}

	/**
	 * Returns a Set of the keys for this KeyMap
	 * 
	 * Note: The returned Set is value-semantic. The ownership of the Set is
	 * transferred to the calling Object; therefore, changes to the returned Set
	 * will NOT impact the KeyMap.
	 * 
	 * @return A Set of the keys for this KeyMap
	 */
	public Set<String> keySet()
	{
		return new LinkedHashSet<>(forwardMap.keySet());
	}

	/**
	 * Put the given value into this KeyMap for the given key.
	 * 
	 * If this KeyMap already contained a mapping for the given key, the
	 * previous value is returned. Otherwise, null is returned.
	 * 
	 * If this KeyMap already contained a key mapping to the given value, the
	 * previous mapping is destroyed without warning.
	 * 
	 * @param key
	 *            The key for storing the given value
	 * @param value
	 *            The value to be stored for the given key
	 * @return Object The previous value stored for the given key; null if the
	 *         given key did not previously have a mapping
	 */
	public V put(String key, V value)
	{
		String oldKey = reverseMap.get(Objects.requireNonNull(value));
		V oldValue = forwardMap.get(Objects.requireNonNull(key));
		if (oldKey != null)
		{
			forwardMap.remove(oldKey);
		}
		reverseMap.remove(oldValue);
		forwardMap.put(key, value);
		reverseMap.put(value, key);
		return oldValue;
	}

	/**
	 * Removes the value from KeyMap for the given key. Returns the value that
	 * was removed from the KeyMap. If this KeyMap did not have a mapping for
	 * the given key, null is returned.
	 * 
	 * @param key
	 *            The key used to identify which object to remove from this
	 *            KeyMap
	 * @return Object The value previously mapped to the given keys
	 */
	public V remove(Object key)
	{
		if (key == null)
		{
			return null;
		}
		V value = forwardMap.remove(key);
		reverseMap.remove(value);
		return value;
	}

	/**
	 * Returns the number of entries (key-value pairs) in the KeyMap.
	 * 
	 * @return the number of entries (key-value pairs) in the KeyMap
	 */
	public int size()
	{
		return forwardMap.size();
	}

	/**
	 * Returns a Collection of the values for this KeyMap.
	 * 
	 * Note: This Collection is both reference-semantic and value-semantic. The
	 * ownership of the Collection is transferred to the calling Object;
	 * therefore, changes to the returned Collection will NOT impact the KeyMap.
	 * However, changes to the objects contained within the returned Collection
	 * will change objects contained within this KeyMap.
	 * 
	 * @return A Collection of the values for this KeyMap
	 */
	public Collection<V> keySortedValues()
	{
		return new ListSet<>(forwardMap.values());
	}

	/**
	 * Returns a String representation of this KeyMap, primarily for purposes of
	 * debugging. It is strongly advised that no dependency on this method be
	 * created, as the return value may be changed without warning.
	 */
	@Override
	public String toString()
	{
		return "KeyMap: " + forwardMap;
	}
}
