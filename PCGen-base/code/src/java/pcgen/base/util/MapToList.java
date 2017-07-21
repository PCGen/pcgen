/*
 * Copyright 2004, 2005 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * 
 * Created on Aug 29, 2004 Imported into PCGen on June 18, 2005.
 */
package pcgen.base.util;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Represents a Map of objects to Lists.
 * 
 * It is expected that any class that implements MapToList makes a reasonable
 * attempt to be consistent in the application of the .equals(Object o) method.
 * In this case, proper implementation of .equals(Object o) is defined as
 * equality to any other class that implements MapToList. If a class implements
 * MapToList and does not define equality against any other MapToList, then that
 * difference in behavior should be clearly documented in the class implementing
 * MapToList without MapToList equality.
 * 
 * @param <K>
 *            The Class of the key for this MapToList
 * @param <V>
 *            The Class of the Value for this MapToList
 */
public interface MapToList<K, V>
{

	/**
	 * Adds the given value to the List for the given key.
	 * 
	 * @param key
	 *            The key indicating which List the given object should be added
	 *            to.
	 * @param value
	 *            The value to be added to the List for the given key.
	 */
	public void addToListFor(K key, V value);

	/**
	 * Adds all of the Objects in the given list to the (internal) List for the
	 * given key.
	 * 
	 * @param key
	 *            The key indicating which List the objects in the given List
	 *            should be added to.
	 * @param valueCollection
	 *            A Collection containing the items to be added to the List for
	 *            the given key.
	 * @throws NullPointerException
	 *             if the given Collection is null
	 */
	public void addAllToListFor(K key, Collection<? extends V> valueCollection);

	/**
	 * Adds all of the Lists in the given MapToList to this MapToList.
	 * 
	 * @param mtl
	 *            The MapToList from which all of the Lists should be imported
	 * @throws NullPointerException
	 *             if the given MapToList is null
	 */
	public void addAllLists(MapToList<K, V> mtl);

	/**
	 * Returns true if this MapToList contains a List for the given key. This
	 * method returns false if the given key is not in this MapToList.
	 * 
	 * @param key
	 *            The key being tested.
	 * @return true if this MapToList contains a List for the given key; false
	 *         otherwise.
	 */
	public boolean containsListFor(K key);

	/**
	 * Returns true if this MapToList contains a List for the given key and that
	 * list contains the given value. Note, this method returns false if the
	 * given key is not in this MapToList.
	 * 
	 * @param key
	 *            The key for the List being tested.
	 * @param value
	 *            The value to find in the List for the given key.
	 * @return true if this MapToList contains a List for the given key AND that
	 *         list contains the given value; false otherwise.
	 */
	public boolean containsInList(K key, V value);

	/**
	 * Returns the number of objects in the List for the given key. This method
	 * may throw a NullPointerException if this MapToList does not contain a
	 * List for the given key.
	 * 
	 * @param key
	 *            The key being tested.
	 * @return the number of objects in the List for the given key
	 */
	public int sizeOfListFor(K key);

	/**
	 * Returns a copy of the List contained in this MapToList for the given key.
	 * This method returns null if the given key is not in this MapToList.
	 * 
	 * @param key
	 *            The key for which a copy of the list should be returned.
	 * @return a copy of the List contained in this MapToList for the given key;
	 *         null if the given key is not a key in this MapToList.
	 */
	public List<V> getListFor(K key);

	/**
	 * Returns a copy of the List contained in this MapToList for the given key.
	 * This method returns an empty list if the given key is not in this MapToList.
	 * 
	 * @param key
	 *            The key for which a copy of the list should be returned.
	 * @return a copy of the List contained in this MapToList for the given key;
	 *         an empty list if the given key is not a key in this MapToList.
	 */
	public List<V> getSafeListFor(K key);
	
	/**
	 * Removes the given value from the list for the given key. Returns true if
	 * the value was successfully removed from the list for the given key.
	 * Returns false if there is not a list for the given key object or if the
	 * list for the given key object did not contain the given value object.
	 * 
	 * @param key
	 *            The key indicating which List the given object should be
	 *            removed from
	 * @param value
	 *            The value to be removed from the List for the given key
	 * @return true if the value was successfully removed from the list for the
	 *         given key; false otherwise
	 */
	public boolean removeFromListFor(K key, V value);

	/**
	 * Removes the List for the given key. Note there is no requirement that the
	 * list for the given key be empty before this method is called.
	 * 
	 * @param key
	 *            The key indicating which List the given object should be
	 *            removed from
	 * @return The List which this MapToList previous mapped the given key
	 */
	public List<V> removeListFor(K key);

	/**
	 * Returns true if this MapToList contains no Lists.
	 * 
	 * @return true if this MapToList contains no Lists; false otherwise
	 */
	public boolean isEmpty();

	/**
	 * Returns the number of lists contained by this MapToList.
	 * 
	 * @return The number of lists contained by this MapToList.
	 */
	public int size();

	/**
	 * Returns a Set indicating the Keys of this MapToList.
	 * 
	 * @return a Set containing the keys in this MapToList
	 */
	public Set<K> getKeySet();

	/**
	 * Returns a specific element in the list for the given key.
	 * 
	 * @param key
	 *            The key used to identify the list from which the specified
	 *            value will be returned
	 * @param index
	 *            The location in the list (for the given key) of the value to
	 *            be returned
	 * @return The value in the given location in the list for the given key
	 * @throws IllegalArgumentException
	 *             if the given key does not exist in this MapToList
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range for the list for the given key
	 *             (index is less than zero OR greater than or equal to the size
	 *             of the list)
	 */
	public V getElementInList(K key, int index);

	/**
	 * Clears this MapToList (removes all keys/list combinations).
	 */
	public void clear();

}
