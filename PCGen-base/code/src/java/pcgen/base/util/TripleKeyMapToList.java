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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a TripleKeyMap of objects to Lists. List management is done
 * internally to this class (while copies are accessible, the lists are kept
 * private to this class).
 * 
 * This class is reference-semantic. In appropriate cases (such as calling the
 * addToListFor method), TripleKeyMapToList will maintain a reference to the
 * given Object. TripleKeyMapToList will not modify any of the Objects it is
 * passed; however, it reserves the right to return references to Objects it
 * contains to other Objects.
 * 
 * However, when any method in which TripleKeyMapToList returns a Collection,
 * ownership of the Collection itself is transferred to the calling Object, but
 * the contents of the Collection (keys, values, etc.) are references whose
 * ownership should be respected.
 * 
 * @param <K1>
 *            The type of the primary keys in this TripleKeyMapToList
 * @param <K2>
 *            The type of the secondary keys in this TripleKeyMapToList
 * @param <K3>
 *            The type of the tertiary keys in this TripleKeyMapToList
 * @param <V>
 *            The type of the values in this TripleKeyMapToList
 */
public class TripleKeyMapToList<K1, K2, K3, V>
{

	/**
	 * Stores the Class to be used as the underlying Map for the map from the
	 * third key of the TripleKeyMapToList to the value stored for the given
	 * keys.
	 */
	@SuppressWarnings("rawtypes")
	private final Class<? extends Map> thirdClass;

	/**
	 * The underlying map for the TripleKeyMapToList. This class protects its
	 * internal structure, so no method should ever return an object capable of
	 * modifying the maps. All modifications should be done through direct calls
	 * to the methods of TripleKeyMapToList.
	 */
	private final DoubleKeyMap<K1, K2, MapToList<K3, V>> map;

	/**
	 * Constructs a new (empty) TripleKeyMapToList.
	 */
	@SuppressWarnings("PMD.LooseCoupling")
	public TripleKeyMapToList()
	{
		super();
		thirdClass = HashMap.class;
		map = new DoubleKeyMap<>(HashMap.class, HashMap.class);
	}

	/**
	 * Constructs a new (empty) TripleKeyMapToList.
	 * 
	 * All of the classes provided as parameters must be non-null, extend Map, and
	 * must have a public zero argument constructor.
	 * 
	 * @param cl1
	 *            The Class to be used for the first of the underlying maps for
	 *            the TripleKeyMapToList
	 * @param cl2
	 *            The Class to be used for the second of the underlying maps for
	 *            the TripleKeyMapToList
	 * @param cl3
	 *            The Class to be used for the third of the underlying maps for
	 *            the TripleKeyMapToList
	 */
	@SuppressWarnings("rawtypes")
	public TripleKeyMapToList(Class<? extends Map> cl1,
		Class<? extends Map> cl2, Class<? extends Map> cl3)
	{
		super();
		map = new DoubleKeyMap<>(cl1, cl2);
		thirdClass = Objects.requireNonNull(cl3);
		/*
		 * This "useless" call is designed to exercise the code to ensure that
		 * the given class meets the restrictions imposed by TripleKeyMapToList
		 * (public, zero-argument constructor)
		 */
		GenericMapToList.getMapToList(thirdClass);
	}

	/**
	 * Adds the given value to the List for the given keys. The null value
	 * cannot be used as a key in a TripleKeyMapToList. This method will
	 * automatically initialize the list for the given key if there is not
	 * already a List for that key.
	 * 
	 * This method is reference-semantic and this TripleKeyMapToList will
	 * maintain a strong reference to both the key object and the value object
	 * given as arguments to this method.
	 * 
	 * @param key1
	 *            The primary key indicating which List the given object should
	 *            be added to.
	 * @param key2
	 *            The secondary key indicating which List the given object
	 *            should be added to.
	 * @param key3
	 *            The tertiary key indicating which List the given object should
	 *            be added to.
	 * @param valueElement
	 *            The value to be added to the List for the given keys.
	 */
	public void addToListFor(K1 key1, K2 key2, K3 key3, V valueElement)
	{
		MapToList<K3, V> localMap = map.get(key1, key2);
		if (localMap == null)
		{
			localMap = GenericMapToList.getMapToList(thirdClass);
			map.put(key1, key2, localMap);
		}
		localMap.addToListFor(key3, valueElement);
	}

	/**
	 * Adds all of the Objects in the given list to the (internal) List for the
	 * given keys. The null value cannot be used as a key in a
	 * TripleKeyMapToList. This method will automatically initialize the list
	 * for the given key if there is not already a List for that key.
	 * 
	 * This method is both reference-semantic and value-semantic. It will not
	 * modify or maintain a reference to the given Collection of objects.
	 * However, this TripleKeyMapToList will maintain a strong reference to both
	 * the key objects and the objects contained in the given Collection.
	 * 
	 * @param key1
	 *            The primary key indicating which List the given object should
	 *            be added to.
	 * @param key2
	 *            The secondary key indicating which List the given object
	 *            should be added to.
	 * @param key3
	 *            The tertiary key indicating which List the given object should
	 *            be added to.
	 * @param values
	 *            A Collection containing the items to be added to the List for
	 *            the given keys.
	 */
	public void addAllToListFor(K1 key1, K2 key2, K3 key3, Collection<V> values)
	{
		MapToList<K3, V> localMap = map.get(key1, key2);
		if (localMap == null)
		{
			localMap = GenericMapToList.getMapToList(thirdClass);
			map.put(key1, key2, localMap);
		}
		localMap.addAllToListFor(key3, values);
	}

	/**
	 * Returns a copy of the List contained in this TripleKeyMapToList for the
	 * given keys. This method returns null if the given key is not in this
	 * TripleKeyMapToList.
	 * 
	 * This method is value-semantic in that no changes are made to the object
	 * passed into the method and ownership of the returned List is transferred
	 * to the class calling this method.
	 * 
	 * @param key1
	 *            The primary key for retrieving the given List
	 * @param key2
	 *            The secondary key for retrieving the given List
	 * @param key3
	 *            The tertiary key for retrieving the given List
	 * @return a copy of the List contained in this TripleKeyMapToList for the
	 *         given key; null if the given key is not a key in this
	 *         TripleKeyMapToList.
	 */
	public List<V> getListFor(K1 key1, K2 key2, K3 key3)
	{
		MapToList<K3, V> localMap = map.get(key1, key2);
		return (localMap == null) ? null : localMap.getListFor(key3);
	}

	/**
	 * Returns true if this TripleKeyMapToList contains a List for the given
	 * keys. This method returns false if the given keys are not in this
	 * TripleKeyMapToList.
	 * 
	 * This method is value-semantic in that no changes are made to the objects
	 * passed into the method.
	 * 
	 * @param key1
	 *            The primary key for testing presence of a List
	 * @param key2
	 *            The secondary key for testing presence of a List
	 * @param key3
	 *            The tertiary key for testing presence of a List
	 * @return true if this TripleKeyMapToList contains a List for the given
	 *         keys; false otherwise.
	 */
	public boolean containsListFor(K1 key1, K2 key2, K3 key3)
	{
		MapToList<K3, V> localMap = map.get(key1, key2);
		return (localMap != null) && localMap.containsListFor(key3);
	}

	/**
	 * Removes the List for the given keys. Note there is no requirement that
	 * the list for the given keys be empty before this method is called.
	 * 
	 * Obviously, ownership of the returned List is transferred to the object
	 * calling this method.
	 * 
	 * @param key1
	 *            The primary key indicating the List to remove
	 * @param key2
	 *            The secondary key indicating the List to remove
	 * @param key3
	 *            The tertiary key indicating the List to remove
	 * @return The List which this TripleKeyMapToList previous mapped the given
	 *         keys
	 */
	public List<V> removeListFor(K1 key1, K2 key2, K3 key3)
	{
		MapToList<K3, V> localMap = map.get(key1, key2);
		return (localMap == null) ? null : localMap.removeListFor(key3);
	}

	/**
	 * Removes the Lists for the given primary and secondary keys. Note there is
	 * no requirement that the lists for the given primary and secondary key be
	 * empty before this method is called.
	 * 
	 * Obviously, ownership of the returned MapToList is transferred to the
	 * object calling this method.
	 * 
	 * @param key1
	 *            The primary key indicating the Lists to remove
	 * @param key2
	 *            The secondary key indicating the Lists to remove
	 * @return The MapToList representing the tertiary keys and lists previously
	 *         stored in this TripleKeyMapToList for the given primary and
	 *         secondary key
	 */
	public MapToList<K3, V> removeListsFor(K1 key1, K2 key2)
	{
		return map.remove(key1, key2);
	}

	/**
	 * Returns a Set which contains the primary keys for this
	 * TripleKeyMapToList. Returns an empty Set if this TripleKeyMapToList is
	 * empty (has no primary keys)
	 * 
	 * NOTE: This method returns all of the primary keys this TripleKeyMapToList
	 * contains. It DOES NOT determine whether the Lists defined for the keys
	 * are empty. Therefore, it is possible that this TripleKeyMapToList
	 * contains one or more keys, and all of the lists associated with those
	 * keys are empty, yet this method will return a non-zero length Set.
	 * 
	 * Ownership of the returned Set is transferred to the Object that called
	 * this method. Modification of the returned Set will not modify this
	 * TripleKeyMapToList, and modification of this TripleKeyMapToList will not
	 * alter the returned Set.
	 * 
	 * @return A Set containing the primary keys for this TripleKeyMapToList.
	 */
	public Set<K1> getKeySet()
	{
		return map.getKeySet();
	}

	/**
	 * Returns a Set which contains the secondary keys for the given primary key
	 * within this TripleKeyMapToList. Returns an empty Set if there are no
	 * objects stored in the TripleKeyMapToList with the given primary key.
	 * 
	 * NOTE: This method returns all of the secondary keys this
	 * TripleKeyMapToList contains for the given primary key. It DOES NOT
	 * determine whether the Lists defined for the keys are empty. Therefore, it
	 * is possible that this TripleKeyMapToList contains one or more keys, and
	 * all of the lists associated with those keys are empty, yet this method
	 * will return a non-zero length Set.
	 * 
	 * Ownership of the returned Set is transferred to the Object that called
	 * this method. Modification of the returned Set will not modify this
	 * TripleKeyMapToList, and modification of this TripleKeyMapToList will not
	 * alter the returned Set.
	 * 
	 * @param key1
	 *            The primary key to retrieve keys for.
	 * 
	 * @return A Set containing the secondary keys for the given primary key
	 *         within this TripleKeyMapToList.
	 */
	public Set<K2> getSecondaryKeySet(K1 key1)
	{
		return map.getSecondaryKeySet(key1);
	}

	/**
	 * Returns a Set which contains the tertiary keys for the given primary key
	 * within this TripleKeyMapToList. Returns an empty Set if there are no
	 * objects stored in the TripleKeyMapToList with the given primary key.
	 * 
	 * NOTE: This method returns all of the tertiary keys this
	 * TripleKeyMapToList contains for the given primary and secondary keys. It
	 * DOES NOT determine whether the Lists defined for the keys are empty.
	 * Therefore, it is possible that this TripleKeyMapToList contains one or
	 * more keys, and all of the lists associated with those keys are empty, yet
	 * this method will return a non-zero length Set.
	 * 
	 * Ownership of the returned Set is transferred to the Object that called
	 * this method. Modification of the returned Set will not modify this
	 * TripleKeyMapToList, and modification of this TripleKeyMapToList will not
	 * alter the returned Set.
	 * 
	 * @param key1
	 *            The primary key used to identify the Tertiary Key Set in this
	 *            TripleKeyMapToList.
	 * @param key2
	 *            The secondary key used to identify the Tertiary Key Set in
	 *            this TripleKeyMapToList.
	 * @return A Set containing the Tertiary keys for the given primary and
	 *         secondary keys within this TripleKeyMapToList.
	 */
	public Set<K3> getTertiaryKeySet(K1 key1, K2 key2)
	{
		MapToList<K3, V> localMap = map.get(key1, key2);
		return (localMap == null) ? Collections.emptySet() : localMap.getKeySet();
	}

	/**
	 * Clears this TripleKeyMapToList.
	 */
	public void clear()
	{
		map.clear();
	}

	/**
	 * Returns true if the TripleKeyMapToList is empty
	 * 
	 * NOTE: This method checks whether this TripleKeyMapToList contains any
	 * Lists for any key. It DOES NOT test whether all Lists defined for all
	 * keys are empty. Therefore, it is possible that this TripleKeyMapToList
	 * contains one or more keys, and all of the lists associated with those
	 * keys are empty, yet this method will return false.
	 * 
	 * @return true if the TripleKeyMapToList is empty; false otherwise
	 */
	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	/**
	 * Returns the number of primary key maps contained by this
	 * TripleKeyMapToList.
	 * 
	 * NOTE: This method counts the number of Lists this TripleKeyMapToList
	 * contains. It DOES NOT determine whether all Lists defined for all keys
	 * are empty. Therefore, it is possible that this TripleKeyMapToList
	 * contains one or more keys, and all of the lists associated with those
	 * keys are empty, yet this method will return a non-zero value.
	 * 
	 * @return The number of lists contained by this TripleKeyMapToList.
	 */
	public int firstKeyCount()
	{
		return map.primaryKeyCount();
	}

	/**
	 * A consistent-with-equals hashCode for TripleKeyMapToList.
	 */
	@Override
	public int hashCode()
	{
		return map.hashCode();
	}

	/**
	 * Returns true if the TripleKeyMapToList is equal to the given Object.
	 * Equality is defined as the given Object being a TripleKeyMapToList with
	 * equal keys and values as defined by the underlying Maps.
	 */
	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof TripleKeyMapToList)
			&& map.equals(((TripleKeyMapToList<?, ?, ?, ?>) obj).map);
	}
}
