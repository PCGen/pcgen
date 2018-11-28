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
import java.util.function.BiFunction;
import java.util.function.Function;

import pcgen.base.lang.CaseInsensitiveString;

/**
 * A {@code CaseInsensitiveMap} is a {@code HashMap} that has uses a
 * {@code CaseInsensitiveString} as the key.
 * <p>
 * This is a facilitating wrapper
 * around HashMap to allow easy use of {@code CaseInsensitiveString} as the Key
 * to a {@code Map}.
 * <br>
 * {@code CaseInsensitiveMap} gracefully handles non-String keys (defaults back
 * to the same behavior as {@code HashMap}), including the null key. Note that
 * this allows appropriate matching behavior when a
 * {@code CaseInsensitiveString} is passed in as the key to one of the methods
 * of {@code CaseInsensitiveMap}.
 * <p>
 * NOTE: It is generally preferred to use {@code new TreeMap<String,
 * blah>(String.CASE_INSENSITIVE_ORDER)} when dealing with case insensitive
 * Maps. This should only be avoided in rare cases where performance is an issue
 * in fetching items from large maps. In reality, it may be best to remove
 * {@code CaseInsensitiveMap} from use entirely.
 * <p>
 * **WARNING**: This class could be considered to be broken or incomplete.
 * Unfortunately, in the case of the iterator methods of {@code Map} (e.g.
 * keySet()), this class will expose the {@code CaseInsensitiveString} that is
 * used internally to this Class. It is therefore a poor implementation to use
 * when iteration over the entries in this Map is required.
 *
 * @param <V> The Type of the Values stored in this CaseInsensitiveMap
 */
@SuppressWarnings("PMD.TooManyMethods")
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
	private static Object resolveObject(Object key)
	{
		return (key instanceof String) ? new CaseInsensitiveString((String) key) : key;
	}

	@Override
	public boolean containsKey(Object key)
	{
		return super.containsKey(resolveObject(key));
	}

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

	@Override
	public V remove(Object key)
	{
		return super.remove(resolveObject(key));
	}

	@Override
	public V getOrDefault(Object key, V defaultValue)
	{
		return super.getOrDefault(resolveObject(key), defaultValue);
	}

	@Override
	public V putIfAbsent(Object key, V value)
	{
		return super.putIfAbsent(resolveObject(key), value);
	}

	@Override
	public boolean replace(Object key, V oldValue, V newValue)
	{
		return super.replace(resolveObject(key), oldValue, newValue);
	}

	@Override
	public V replace(Object key, V value)
	{
		return super.replace(resolveObject(key), value);
	}

	@Override
	public V computeIfAbsent(Object key,
		Function<? super Object, ? extends V> mappingFunction)
	{
		return super.computeIfAbsent(resolveObject(key), mappingFunction);
	}

	@Override
	public V computeIfPresent(Object key,
		BiFunction<? super Object, ? super V, ? extends V> remappingFunction)
	{
		return super.computeIfPresent(resolveObject(key), remappingFunction);
	}

	@Override
	public V compute(Object key,
		BiFunction<? super Object, ? super V, ? extends V> remappingFunction)
	{
		return super.compute(resolveObject(key), remappingFunction);
	}

	@Override
	public V merge(Object key, V value,
		BiFunction<? super V, ? super V, ? extends V> remappingFunction)
	{
		return super.merge(resolveObject(key), value, remappingFunction);
	}
}
