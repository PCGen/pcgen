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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a Map of objects to Lists. List management is done internally to
 * this class (while copies are accessible, the lists are kept private to this
 * class).
 * 
 * This class is reference-semantic. In appropriate cases (such as calling the
 * addToListFor method), AbstractMapToList will maintain a reference to the
 * given Object. AbstractMapToList will not modify any of the Objects it is
 * passed; however, it reserves the right to return references to Objects it
 * contains to other Objects.
 * 
 * However, when any method in which AbstractMapToList returns a Collection,
 * ownership of the Collection itself is transferred to the calling Object, but
 * the contents of the Collection (keys, values, etc.) are references whose
 * ownership should be respected.
 * 
 * NOTE: This is a convenience method for use in Java 1.4 and is less beneficial
 * in Java 1.5 (Typed Collections may be more appropriate)
 * 
 * @param <K>
 *            The Class of the key for this AbstractMapToList
 * @param <V>
 *            The Class of the Value for this AbstractMapToList
 */
@SuppressWarnings("PMD.TooManyMethods")
public abstract class AbstractMapToList<K, V> implements MapToList<K, V>
{

	/**
	 * The actual map containing the map of objects to Lists.
	 */
	private final Map<K, List<V>> mapToList;

	/**
	 * Creates a new AbstractMapToList.
	 * 
	 * @param mtl The MapToList to be used as the underlying MapToList
	 */
	protected AbstractMapToList(Map<K, List<V>> mtl)
	{
		super();
		mapToList = mtl;
	}

	/**
	 * Initializes a List for the given key. The null value cannot be used as a
	 * key in a MapToList.
	 * 
	 * This method is reference-semantic and this MapToList will maintain a
	 * strong reference to the key object given as an argument to this method.
	 * 
	 * Overuse of this method is discouraged, as it is *not required* to
	 * initialize a List (the AbstractMapToList will automatically initialize a
	 * List if an object is added to the list for a given key).
	 * 
	 * @param key
	 *            The key for which a List should be initialized in this
	 *            MapToList.
	 * @throws IllegalArgumentException
	 *             if this AbstractMapToList already contains a List for the
	 *             given key
	 */
	public void initializeListFor(K key)
	{
		if (mapToList.containsKey(key))
		{
			throw new IllegalArgumentException("Cannot re-initialize key: "
				+ key);
		}
		mapToList.put(key, new ArrayList<>());
	}

	/**
	 * Adds the given value to the List for the given key. The null value cannot
	 * be used as a key in a MapToList. This method will automatically
	 * initialize the list for the given key if there is not already a List for
	 * that key.
	 * 
	 * This method is reference-semantic and this MapToList will maintain a
	 * strong reference to both the key object and the value object given as
	 * arguments to this method.
	 * 
	 * @param key
	 *            The key indicating which List the given object should be added
	 *            to.
	 * @param valueElement
	 *            The value to be added to the List for the given key.
	 */
	@Override
	public void addToListFor(K key, V valueElement)
	{
		/*
		 * Note there is no requirement that a Key is added before this method
		 * is called
		 */
		if (!containsListFor(key))
		{
			initializeListFor(key);
		}
		mapToList.get(key).add(valueElement);
	}

	/**
	 * Adds the given value to the List for the given key at the given location.
	 * The null value cannot be used as a key in a MapToList. This method will
	 * automatically initialize the list for the given key if there is not
	 * already a List for that key if and only if the provided location is zero.
	 * 
	 * If the location is not a valid location in the list for an addition (as
	 * per the rules of add() in the List interface, then this will throw an
	 * exception.
	 * 
	 * This method is reference-semantic and this MapToList will maintain a
	 * strong reference to both the key object and the value object given as
	 * arguments to this method.
	 * 
	 * @param key
	 *            The key indicating which List the given object should be added
	 *            to
	 * @param location
	 *            The location within the list where the given object should be
	 *            added
	 * @param valueElement
	 *            The value to be added to the List for the given key
	 */
	public void addToListFor(K key, int location, V valueElement)
	{
		/*
		 * Note there is no requirement that a Key is added before this method
		 * is called
		 */
		if (!containsListFor(key))
		{
			if (location != 0)
			{
				throw new IllegalArgumentException(
					"Cannot add at location > 0 when list is not initialized");
			}
			initializeListFor(key);
		}
		mapToList.get(key).add(location, valueElement);
	}

	/**
	 * Adds all of the Objects in the given list to the (internal) List for the
	 * given key. The null value cannot be used as a key in a MapToList. This
	 * method will automatically initialize the list for the given key if there
	 * is not already a List for that key.
	 * 
	 * This method is both reference-semantic and value-semantic. This MapToList
	 * will not maintain a reference to or modify the given Collection. However,
	 * this MapToList will maintain a strong reference to both the key object
	 * and the objects in the given Collection.
	 * 
	 * @param key
	 *            The key indicating which List the objects in the given List
	 *            should be added to.
	 * @param values
	 *            A Collection containing the items to be added to the List for
	 *            the given key.
	 */
	@Override
	public void addAllToListFor(K key, Collection<? extends V> values)
	{
		if (values == null || values.isEmpty())
		{
			return;
		}
		/*
		 * Note there is no requirement that a Key is added before this method
		 * is called
		 */
		if (!containsListFor(key))
		{
			initializeListFor(key);
		}
		mapToList.get(key).addAll(values);
	}

	/**
	 * Adds all of the Lists in the given MapToList to this MapToList. The
	 * resulting lists are independent, however, since MapToList is
	 * reference-semantic, the List keys and values in each list are identical.
	 * 
	 * This method is both reference-semantic and value-semantic. This MapToList
	 * will not maintain a reference to or modify the given MapToList. However,
	 * this MapToList will maintain a strong reference to all key objects and
	 * objects in each list of the given MapToList.
	 * 
	 * @param mtl
	 *            The MapToList from which all of the Lists should be imported
	 */
	@Override
	public void addAllLists(MapToList<K, V> mtl)
	{
		/*
		 * Note, this grab of the list for the key is safe regardless of whether mtl's
		 * getListFor is reference or value-semantic, as addAllToListFor is committed to
		 * be value-semantic and not keep or modify the received list.
		 */
		mtl.getKeySet().forEach(key -> addAllToListFor(key, mtl.getListFor(key)));
	}

	/**
	 * Returns true if this MapToList contains a List for the given key. This
	 * method returns false if the given key is not in this MapToList.
	 * 
	 * This method is value-semantic in that no changes are made to the object
	 * passed into the method.
	 * 
	 * @param key
	 *            The key being tested.
	 * @return true if this MapToList contains a List for the given key; false
	 *         otherwise.
	 */
	@Override
	public boolean containsListFor(K key)
	{
		return mapToList.containsKey(key);
	}

	/**
	 * Returns true if this MapToList contains a List for the given key and that
	 * list contains the given value. Note, this method returns false if the
	 * given key is not in this MapToList.
	 * 
	 * This method is value-semantic in that no changes are made to the objects
	 * passed into the method.
	 * 
	 * @param key
	 *            The key for the List being tested.
	 * @param valueElement
	 *            The value to find in the List for the given key.
	 * @return true if this MapToList contains a List for the given key AND that
	 *         list contains the given value; false otherwise.
	 */
	@Override
	public boolean containsInList(K key, V valueElement)
	{
		return containsListFor(key)
			&& mapToList.get(key).contains(valueElement);
	}

	/**
	 * Returns true if this MapToList contains a List for the given key and that
	 * list contains one or more of the values in the given collection. Note,
	 * this method returns false if the given key is not in this MapToList.
	 * 
	 * This method is value-semantic in that no changes are made to the objects
	 * passed into the method.
	 * 
	 * @param key
	 *            The key for the List being tested.
	 * @param values
	 *            The collection of values to find in the List for the given
	 *            key.
	 * @return true if this MapToList contains a List for the given key AND that
	 *         list contains one or more of the given values; false otherwise.
	 */
	public boolean containsAnyInList(K key, Collection<V> values)
	{
		List<V> list = mapToList.get(key);
		if (list == null)
		{
			return false;
		}
		for (V value : values)
		{
			if (list.contains(value))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the number of objects in the List for the given key. This method
	 * will throw a NullPointerException if this MapToList does not contain a
	 * List for the given key.
	 * 
	 * This method is value-semantic in that no changes are made to the object
	 * passed into the method.
	 * 
	 * @param key
	 *            The key being tested.
	 * @return the number of objects in the List for the given key
	 */
	@Override
	public int sizeOfListFor(K key)
	{
		/*
		 * FUTURE It is possible for the context of PCGen that this class should
		 * not throw a NullPointerException if the key is not in the MapToList.
		 * It is possible that better behavior might be to return -1. However,
		 * at this time, that should not be implemented (That is VERY detailed
		 * tweaking) until there is more information on how MapToList gets used
		 * around the PCGen universe (this could be considered inconsistent with
		 * getListFor below, since it doesn't throw a NPE...) - thpr 6/19/05
		 */
		/*
		 * On the other hand, PCGen is also built with a lot of uninitialized
		 * items and at risk of NPEs... in order to fix the save problem in CVS
		 * this is currently set to zero if the key doesn't exist - thpr June
		 * 24, 2005
		 */
		List<V> list = mapToList.get(key);
		return (list == null) ? 0 : list.size();
	}

	/**
	 * Returns a copy of the List contained in this MapToList for the given key.
	 * This method returns null if the given key is not in this MapToList.
	 * 
	 * This method is value-semantic in that no changes are made to the object
	 * passed into the method and ownership of the returned List is transferred
	 * to the class calling this method. Modification of the returned List will
	 * not modify this MapToList and modification of this MapToList will not
	 * modify the returned List.
	 * 
	 * Note: If you are only acquiring a single instance from a given list (and
	 * acquiring that by the order in which it appears in the list) it is
	 * probably much faster to use getElementInList, as that does not require
	 * the internal list to be copied (getListFor copies the list in order to be
	 * value-semantic)
	 * 
	 * @param key
	 *            The key for which a copy of the list should be returned.
	 * @return a copy of the List contained in this MapToList for the given key;
	 *         null if the given key is not a key in this MapToList.
	 */
	@Override
	public List<V> getListFor(K key)
	{
		List<V> list = mapToList.get(key);
		return (list == null) ? null : new ArrayList<>(list);
	}

	/**
	 * Returns a copy of the List contained in this MapToList for the given key.
	 * This method returns an empty list if the given key is not in this MapToList.
	 * 
	 * This method is value-semantic in that no changes are made to the object
	 * passed into the method and ownership of the returned List is transferred
	 * to the class calling this method. Modification of the returned List will
	 * not modify this MapToList and modification of this MapToList will not
	 * modify the returned List.
	 * 
	 * Note: If you are only acquiring a single instance from a given list (and
	 * acquiring that by the order in which it appears in the list) it is
	 * probably much faster to use getElementInList, as that does not require
	 * the internal list to be copied (getListFor copies the list in order to be
	 * value-semantic)
	 * 
	 * @param key
	 *            The key for which a copy of the list should be returned.
	 * @return a copy of the List contained in this MapToList for the given key;
	 *         an empty list if the given key is not a key in this MapToList.
	 */
	@Override
	public List<V> getSafeListFor(K key)
	{
		return Optional.ofNullable(getListFor(key))
			.orElseGet(() -> Collections.emptyList());
	}

	/**
	 * Removes the given value from the list for the given key. Returns true if
	 * the value was successfully removed from the list for the given key.
	 * Returns false if there is not a list for the given key object or if the
	 * list for the given key object did not contain the given value object.
	 * 
	 * @param key
	 *            The key indicating which List the given object should be
	 *            removed from
	 * @param valueElement
	 *            The value to be removed from the List for the given key
	 * @return true if the value was successfully removed from the list for the
	 *         given key; false otherwise
	 */
	@Override
	public boolean removeFromListFor(K key, V valueElement)
	{
		List<V> list = mapToList.get(key);
		/*
		 * Note there is no requirement that a Key is added before this method
		 * is called
		 */
		if (list == null)
		{
			return false;
		}
		boolean removed = removeFromList(list, valueElement);
		if (removed && list.isEmpty())
		{
			mapToList.remove(key);
		}
		return removed;
	}

	/**
	 * Perform the actual list removal (package since this is reckless).
	 * 
	 * @param list
	 *            The list from which the object will be removed
	 * @param valueElement
	 *            The element to be removed from the given list
	 * @return true if the object is returned; false otherwise
	 */
	@SuppressWarnings("PMD.DefaultPackage")
	boolean removeFromList(List<V> list, V valueElement)
	{
		return list.remove(valueElement);
	}

	/**
	 * Removes the List for the given key. Note there is no requirement that the
	 * list for the given key be empty before this method is called.
	 * 
	 * Obviously, ownership of the returned List is transferred to the object
	 * calling this method.
	 * 
	 * @param key
	 *            The key indicating which List the given object should be
	 *            removed from
	 * @return The List which this MapToList previous mapped the given key
	 */
	@Override
	public List<V> removeListFor(K key)
	{
		return mapToList.remove(key);
	}

	/**
	 * Returns true if this MapToList contains no Lists.
	 * 
	 * NOTE: This method checks whether this MapToList contains any Lists for
	 * any key. It DOES NOT test whether all Lists defined for all keys are
	 * empty. Therefore, it is possible that this MapToList contains one or more
	 * keys, and all of the lists associated with those keys are empty, yet this
	 * method will return false.
	 * 
	 * @return true if this MapToList contains no Lists; false otherwise
	 */
	@Override
	public boolean isEmpty()
	{
		return mapToList.isEmpty();
	}

	/**
	 * Returns the number of lists contained by this MapToList.
	 * 
	 * NOTE: This method counts the number of Lists this MapToList contains. It
	 * DOES NOT determine whether all Lists defined for all keys are empty.
	 * Therefore, it is possible that this MapToList contains one or more keys,
	 * and all of the lists associated with those keys are empty, yet this
	 * method will return a non-zero value.
	 * 
	 * @return The number of lists contained by this MapToList.
	 */
	@Override
	public int size()
	{
		return mapToList.size();
	}

	/**
	 * Returns a specific element in the list for the given key. Note this is
	 * much faster than retrieving a list for a given key (using getListFor) and
	 * then performing a get on the returned list, because getListFor returns a
	 * clone of the list for the given key.
	 * 
	 * @param key
	 *            The key used to identify the list from which the specified
	 *            value will be returned
	 * @param index
	 *            The location in the list (for the given key) of the value to
	 *            be returned
	 * @return The value in the given location in the list for the given key
	 * @throws NullPointerException
	 *             if the given key does not exist in this AbstractMapToList
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range for the list for the given key
	 *             (index is less than zero OR greater than or equal to the size
	 *             of the list)
	 */
	@Override
	public V getElementInList(K key, int index)
	{
		List<V> subList = mapToList.get(key);
		return Objects
			.requireNonNull(subList, key + " is not a key in this AbstractMapToList")
			.get(index);
	}

	/**
	 * Clears this AbstractMapToList (removes all keys/list combinations).
	 */
	@Override
	public void clear()
	{
		mapToList.clear();
	}

	@Override
	public String toString()
	{
		return mapToList.toString();
	}

	/**
	 * Returns a Set indicating the Keys of this MapToList.
	 * 
	 * Ownership of the Set is transferred to the calling Object, no association
	 * is kept between the Set and this MapToList. (Thus, removal of a key from
	 * the returned Set will not remove that key from this MapToList)
	 * 
	 * NOTE: This method returns all of the keys this MapToList contains. It
	 * DOES NOT determine whether the Lists defined for the keys are empty.
	 * Therefore, it is possible that this MapToList contains one or more keys,
	 * and all of the lists associated with those keys are empty, yet this
	 * method will return a non-zero length Set.
	 * 
	 * @return a Set containing the keys in this MapToList
	 */
	@Override
	public Set<K> getKeySet()
	{
		// Need to 'clone' the Set, since Map returns a set that is still
		// associated with the Map
		Set<K> set = getEmptySet();
		set.addAll(mapToList.keySet());
		return set;
	}

	/**
	 * Returns an empty Set to be used as a value in the underlying Map. This
	 * method must be overridden by classes that extend AbstractMapToList in
	 * order to return a set of the appropriate type.
	 * 
	 * Each call to this method should produce a NEW Set, which is independent
	 * of each Set returned by previous calls to this method.
	 * 
	 * @return an empty Set to be used as a value in the underlying Map
	 */
	protected abstract Set<K> getEmptySet();

	/**
	 * Returns the consistent-with-equals hashCode for this AbstractMapToList.
	 */
	@Override
	public int hashCode()
	{
		return mapToList.hashCode();
	}

	/**
	 * Returns true if this AbstractMapToList is equal to the given Object
	 * (which must also be another AbstractMapToList).
	 * 
	 * Note that equality as defined by this method is equality of *contents* of
	 * the AbstractMapToList, not the exact class extending AbstractMapToList.
	 * However, the other object must extend AbstractMapToList not just
	 * implement MapToList.
	 * 
	 * **NOTE** This equals method may not be symmetric if other implementors of
	 * MapToList do not define equality in the same way.
	 */
	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof AbstractMapToList)
			&& mapToList.equals(((AbstractMapToList<?, ?>) obj).mapToList);
	}
}
