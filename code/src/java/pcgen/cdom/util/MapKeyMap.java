/*
 * Copyright 2008 (C) James Dempsey
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
 *
 */
package pcgen.cdom.util;

import java.util.Map;
import java.util.Set;

import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.enumeration.MapKey;

/**
 * This encapsulates a DoubleKeyMap in a typesafe and value-semantic way.
 * <p>
 * Specifically, when Generics are properly used by a class using a MapKeyMap,
 * this class ensures that any MapKey will only return a Map of the same Generic
 * type as the MapKey. Note this relationship is only enforced with Generics,
 * and could be violated if Generics are not properly used.
 * <p>
 * This Class also is reference-semantic with respect to the Maps. In other
 * words, the modification of any Collection returned by a MapKeyMap will not
 * impact the internal contents of the MapKeyMap. Also, any Collection used as a
 * parameter to a method is not stored directly, the Collection can be modified
 * after the method has returned without impacting the internal contents of the
 * MapKeyMap.
 * <p>
 * **NOTE** This class is NOT thread safe.
 */
public class MapKeyMap
{

    /*
     * Much as for ListKeyMapToList.map this must remain generic. See Tom's
     * comment in that class for the full explanation.
     */
    /**
     * The internal storage of this MapKeyMap.
     */
    @SuppressWarnings("rawtypes")
    private final DoubleKeyMap map = new DoubleKeyMap();

    /**
     * Copies the key/value combinations from the given MapKeyMap into this
     * MapKeyMap. If this MapKeyMap already contained a mapping for the any of
     * the key combinations in the given MapKeyMap, the previous value is
     * overwritten.
     *
     * @param mkm The MapKeyMap for which the key/value combinations should be
     *            placed into this MapKeyMap
     * @throws NullPointerException if the given MapKeyMap is null
     */
    @SuppressWarnings("unchecked")
    public final void putAll(MapKeyMap mkm)
    {
        map.putAll(mkm.map);
    }

    /**
     * Adds the given value to the List for the given ListKey. The null value
     * cannot be used as a key in a MapKeyMap. This method will automatically
     * initialize the map for the given primary key if there is not already a
     * Map for that primary key.
     * <p>
     * This method is reference-semantic and this MapKeyMap will maintain a
     * strong reference to both the key object and the value object given as
     * arguments to this method.
     *
     * @param key1  The MapKey indicating which Map the given object should be
     *              added to.
     * @param value The value to be added to the List for the given key.
     */
    @SuppressWarnings("unchecked")
    public <K, V> V addToMapFor(MapKey<K, V> key1, K key2, V value)
    {
        return (V) map.put(key1, key2, value);
    }

    /**
     * Returns true if this MapKeyMap contains a Map for the given MapKey. This
     * method returns false if the given key is not in this MapKeyMap.
     * <p>
     * This method is value-semantic in that no changes are made to the object
     * passed into the method.
     *
     * @param key The MapKey being tested.
     * @return true if this MapKeyMap contains a Map for the given key; false
     * otherwise.
     */
    @SuppressWarnings("unchecked")
    public boolean containsMapFor(MapKey<?, ?> key)
    {
        return map.containsKey(key);
    }

    /**
     * Returns a copy of the List contained in this MapKeyMap for the given
     * ListKey. This method returns null if the given key is not in this
     * MapKeyMap.
     * <p>
     * This method is value-semantic in that no changes are made to the object
     * passed into the method and ownership of the returned List is transferred
     * to the class calling this method.
     *
     * @param key1 The MapKeyMap to find
     * @param key2 The ListKey for which a copy of the list should be returned.
     * @return a copy of the List contained in this MapKeyMap for the given key;
     * null if the given key is not a key in this MapKeyMap.
     */
    @SuppressWarnings("unchecked")
    public <K, V> V get(MapKey<K, V> key1, K key2)
    {
        return (V) map.get(key1, key2);
    }

    /**
     * Returns a copy of the List contained in this MapKeyMap for the given
     * ListKey. This method returns null if the given key is not in this
     * MapKeyMap.
     * <p>
     * This method is value-semantic in that no changes are made to the object
     * passed into the method and ownership of the returned List is transferred
     * to the class calling this method.
     *
     * @param key The ListKey for which a copy of the list should be returned.
     * @return a copy of the List contained in this MapKeyMap for the given key;
     * null if the given key is not a key in this MapKeyMap.
     */
    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> getMapFor(MapKey<K, V> key)
    {
        return map.getMapFor(key);
    }

    /**
     * Returns a Set of the secondary keys for the given primary key in this
     * MapKeyMap. This method returns an empty set if the given key is not in
     * this MapKeyMap.
     * <p>
     * Note: This Set is reference-semantic. The ownership of the Set is
     * transferred to the calling Object; therefore, changes to the returned Set
     * will NOT impact the MapKeyMap.
     *
     * @param key The MapKey for which a copy of the keys should be returned.
     * @return A <tt>Set</tt> of secondary key objects for the given primary
     * key.
     */
    @SuppressWarnings("unchecked")
    public <K, V> Set<K> getKeysFor(MapKey<K, V> key)
    {
        return map.getSecondaryKeySet(key);
    }

    /**
     * Removes the given value from the map for the given MapKey. Returns true
     * if the value was successfully removed from the map for the given MapKey.
     * Returns false if there is not a map for the given MapKey or if the map
     * for the given MapKey did not contain the given secondary key.
     *
     * @param key1 The MapKey indicating which Map the given object should be
     *             removed from
     * @param key2 The key to be removed from the Map
     * @return true if the key and its associated value were successfully
     * removed from the map; false otherwise
     */
    @SuppressWarnings("unchecked")
    public <K, V> boolean removeFromMapFor(MapKey<K, V> key1, K key2)
    {
        return map.remove(key1, key2) != null;
    }

    /**
     * Removes the List for the given ListKey. Note there is no requirement that
     * the list for the given key be empty before this method is called.
     * <p>
     * Ownership of the returned Map is transferred to the object calling this
     * method.
     *
     * @return The Map which this MapKeyMap previous mapped the given key
     */
    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> removeMapFor(MapKey<K, V> key)
    {
        return map.removeAll(key);
    }

    /**
     * Returns a Set indicating the Keys of this MapKeyMap. Ownership of the Set
     * is transferred to the calling Object, no association is kept between the
     * Set and this MapKeyMap. (Thus, removal of a key from the returned Set
     * will not remove that key from this MapKeyMap)
     * <p>
     * NOTE: This method returns all of the keys this MapKeyMap contains. It
     * DOES NOT determine whether the Lists defined for the keys are empty.
     * Therefore, it is possible that this MapKeyMap contains one or more keys,
     * and all of the lists associated with those keys are empty, yet this
     * method will return a non-zero length Set.
     *
     * @return a Set containing the keys in this MapKeyMap
     */
    @SuppressWarnings("unchecked")
    public Set<MapKey<?, ?>> getKeySet()
    {
        return map.getKeySet();
    }

    /**
     * Returns true if this structure contains no Maps.
     *
     * @return true if this structure contains no Maps; false otherwise
     */
    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    @Override
    public int hashCode()
    {
        return map.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof MapKeyMap && map.equals(((MapKeyMap) obj).map);
    }
}
