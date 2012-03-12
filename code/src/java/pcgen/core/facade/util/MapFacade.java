/*
 * MapFacade.java
 * Copyright 2012 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Mar 11, 2012, 3:46:59 PM
 */
package pcgen.core.facade.util;

import pcgen.core.facade.event.MapListener;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public interface MapFacade<K, V>
{

	void addMapListener(MapListener<? super K, ? super V> listener);

	void removeMapListener(MapListener<? super K, ? super V> listener);

	/**
	 * returns a list of the keys in this map. This list is
	 * backed by the map and will be updated alongside 
	 * map events.
	 * @return a list containing the map's keys
	 */
	ListFacade<K> getKeys();

	V get(K key);

	/**
	 * behaves like the standard Map function with the addition
	 * that is fires events as appropriate.
	 * @param key
	 * @param value
	 * @return
	 */
	V put(K key, V value);

	/**
	 * tests whether the map has a value with the given key
	 * @param key
	 * @return true if a key is found, false otherwise
	 */
	boolean containsKey(K key);

	/**
	 * Note: This is shorthand for (getSize() == 0)
	 * @return whether this list is empty
	 */
	boolean isEmpty();

	boolean removeKey(K key);
}
