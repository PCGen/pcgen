/*
 * CollectionMap.java
 *
 * Copyright 2007 (C) Connor Petty <mistercpp2000@gmail.com>
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
 * Created on December 15, 2007, 6:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pcgen.util;

import java.util.Collection;
import java.util.Map;

/**
 *
 * @author Connor Petty
 */
public interface CollectionMap<K, V, C extends Collection<V>> extends Map<K, C>{
    /**
     * Associates the specified collection with the specified key in this map
     * (optional operation).  If the map previously contained a mapping for
     * the key, the old collection is replaced by the specified value.  (A map
     * <tt>m</tt> is said to contain a mapping for a key <tt>k</tt> if and only
     * if {@link #containsKey(Object) m.containsKey(k)} would return
     * <tt>true</tt>.)
     *
     * @param key key with which the specified value is to be associated
     * @param collection collection to be associated with the specified key
     * @return the previous collection associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     * @throws UnsupportedOperationException if the <tt>put</tt> operation
     *         is not supported by this map
     * @throws ClassCastException if the class of the specified key or value
     *         prevents it from being stored in this map
     * @throws NullPointerException if the specified key or value is null
     *         and this map does not permit null keys or values
     * @throws IllegalArgumentException if some property of the specified key
     *         or value prevents it from being stored in this map
     */
    @Override
    C put(K key, C collection);
    
    boolean add(K key, V value);
    boolean addAll(K key, Collection<? extends V> values);
    boolean containsValue(Object key, Object value);
    /**
     * Removes the mapping for a key from this map if it is present
     * (optional operation).   More formally, if this map contains a mapping
     * from key <tt>k</tt> to collection <tt>v</tt> such that
     * {@code (key==null ?  k==null : key.equals(k))}, that mapping
     * is removed.  (The map can contain at most one such mapping.)
     *
     * <p>Returns the collection to which this map previously associated the key,
     * or <tt>null</tt> if the map contained no mapping for the key.
     *
     * <p>The map will not contain a mapping for the specified key once the
     * call returns.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous collection associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     * @throws UnsupportedOperationException if the <tt>remove</tt> operation
     *         is not supported by this map
     * @throws ClassCastException if the key is of an inappropriate type for
     *         this map (optional)
     * @throws NullPointerException if the specified key is null and this
     *         map does not permit null keys (optional)
     */
    @Override
    C remove(Object key);
    
    boolean remove(Object key, Object value);
    boolean removeAll(Object key, Collection<?> c);
    boolean retainAll(Object key, Collection<?> c);
    Collection<V> getAll();
    int size(Object key);
}
