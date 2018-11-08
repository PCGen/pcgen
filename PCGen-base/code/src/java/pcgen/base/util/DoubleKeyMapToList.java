/*
 * Copyright 2005 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * Created on Jun 16, 2005
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
 * Represents a DoubleKeyMap of objects to Lists. List management is done
 * internally to this class (while copies are accessible, the lists are kept
 * private to this class).
 * 
 * This class is reference-semantic. In appropriate cases (such as calling the
 * addToListFor method), DoubleKeyMapToList will maintain a reference to the
 * given Object. DoubleKeyMapToList will not modify any of the Objects it is
 * passed; however, it reserves the right to return references to Objects it
 * contains to other Objects.
 * 
 * However, when any method in which DoubleKeyMapToList returns a Collection,
 * ownership of the Collection itself is transferred to the calling Object, but
 * the contents of the Collection (keys, values, etc.) are references whose
 * ownership should be respected.
 * 
 * Since DoubleKeyMapToList leverages existing classes that implement
 * java.util.Map, it also inherits any limitations on those classes. For
 * example, if the underlying Map is a java.util.HashMap, then modifying an
 * object in this set to alter the hashCode of that object may result in
 * unpredictable behavior from the DoubleKeyMapToList. Be careful to read the
 * documentation on the underlying Map class to ensure appropriate treatment of
 * objects placed in the DoubleKeyMapToList.
 * 
 * @param <K1>
 *            The type of the primary keys in this DoubleKeyMapToList
 * @param <K2>
 *            The type of the secondary keys in this DoubleKeyMapToList
 * @param <V>
 *            The type of the values in this DoubleKeyMapToList
 */
@SuppressWarnings("PMD.TooManyMethods")
public class DoubleKeyMapToList<K1, K2, V> implements Cloneable
{
	/**
	 * Stores the Class to be used as the underlying Map for the map from the
	 * first key of the DoubleKeyMapToList to the second underlying Map.
	 */
	@SuppressWarnings("rawtypes")
	private final Class<? extends Map> firstClass;

	/**
	 * Stores the Class to be used as the underlying Map for the map from the
	 * second key of the DoubleKeyMapToList to the value stored for the given
	 * keys.
	 */
	@SuppressWarnings("rawtypes")
	private final Class<? extends Map> secondClass;

	/**
	 * The actual map containing the map to map to Lists.
	 */
	private Map<K1, MapToList<K2, V>> mtmtl;

	/**
	 * Constructs a new DoubleKeyMapToList.
	 */
	@SuppressWarnings("PMD.LooseCoupling")
	public DoubleKeyMapToList()
	{
		super();
		firstClass = HashMap.class;
		secondClass = firstClass;
		mtmtl = new HashMap<>();
	}

	/**
	 * Creates a new, empty DoubleKeyMapToList using the given classes as the
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
	public DoubleKeyMapToList(Class<? extends Map> cl1, Class<? extends Map> cl2)
	{
		super();
		firstClass = Objects.requireNonNull(cl1);
		secondClass = Objects.requireNonNull(cl2);
		mtmtl = createGlobalMap();
		/*
		 * This "useless" call is designed to exercise the code to ensure that
		 * the given class meets the restrictions imposed by DoubleKeyMapToList
		 * (public, zero-argument constructor)
		 */
		GenericMapToList.getMapToList(secondClass);
	}

	/**
	 * Adds the given value to the List for the given keys. The null value
	 * cannot be used as a key in a DoubleKeyMapToList. This method will
	 * automatically initialize the list for the given key if there is not
	 * already a List for that key.
	 * 
	 * This method is reference-semantic and this DoubleKeyMapToList will
	 * maintain a strong reference to both the key object and the value object
	 * given as arguments to this method.
	 * 
	 * @param key1
	 *            The primary key indicating which List the given object should
	 *            be added to.
	 * @param key2
	 *            The secondary key indicating which List the given object
	 *            should be added to.
	 * @param valueElement
	 *            The value to be added to the List for the given keys.
	 */
	public void addToListFor(K1 key1, K2 key2, V valueElement)
	{
		getMapToListFor(key1).addToListFor(key2, valueElement);
	}

	/**
	 * Adds all of the Objects in the given list to the (internal) List for the
	 * given keys. The null value cannot be used as a key in a
	 * DoubleKeyMapToList. This method will automatically initialize the list
	 * for the given key if there is not already a List for that key.
	 * 
	 * This method is both reference-semantic and value-semantic. This
	 * DoubleKeyMapToList will not maintain a reference to the given Collection.
	 * However, This DoubleKeyMapToList will maintain a strong reference to both
	 * the key objects and the objects in the given Collection.
	 * 
	 * @param key1
	 *            The primary key indicating which List the given objects should
	 *            be added to.
	 * @param key2
	 *            The secondary key indicating which List the given objects
	 *            should be added to.
	 * @param values
	 *            A Collection containing the items to be added to the List for
	 *            the given keys.
	 */
	public void addAllToListFor(K1 key1, K2 key2, Collection<V> values)
	{
		getMapToListFor(key1).addAllToListFor(key2, values);
	}

	/**
	 * Adds all of the contents of the given DoubleKeyMapToList to this
	 * DoubleKeyMapToList.
	 * 
	 * No reference is maintained to the internal structure of the given
	 * DoubleKeyMapToList, so modifications to this DoubleKeyMapToList are not
	 * reflected in the given DoubleKeyMapToList (and vice versa). However, the
	 * Keys and Value objects from the given DoubleKeyMapToList are maintained
	 * by reference, so modification to the Keys or Values of either this
	 * DoubleKeyMapToList or the given DoubleKeyMapToList will be reflected in
	 * the other DoubleKeyMapToList (this is consistent behavior with the
	 * analogous classes in the java.util.Map implementations)
	 * 
	 * @param dkmtl
	 *            The DoubleKeyMapToList from which the contents should be
	 *            copied into this DoubleKeyMapToList.
	 */
	public void addAll(DoubleKeyMapToList<K1, K2, V> dkmtl)
	{
		for (Map.Entry<K1, MapToList<K2, V>> me : dkmtl.mtmtl.entrySet())
		{
			MapToList<K2, V> localMap = getMapToListFor(me.getKey());
			localMap.addAllLists(me.getValue());
		}
	}

	/**
	 * Returns a copy of the List contained in this DoubleKeyMapToList for the
	 * given keys. This method returns null if the given key is not in this
	 * DoubleKeyMapToList.
	 * 
	 * This method is value-semantic in that no changes are made to the object
	 * passed into the method and ownership of the returned List is transferred
	 * to the class calling this method.
	 * 
	 * @param key1
	 *            The primary key for retrieving the given List
	 * @param key2
	 *            The secondary key for retrieving the given List
	 * @return a copy of the List contained in this DoubleKeyMapToList for the
	 *         given key; null if the given key is not a key in this
	 *         DoubleKeyMapToList.
	 */
	public List<V> getListFor(K1 key1, K2 key2)
	{
		MapToList<K2, V> localMap = mtmtl.get(key1);
		return (localMap == null) ? null : localMap.getListFor(key2);
	}

	/**
	 * Returns true if this DoubleKeyMapToList contains a List for the given
	 * primary key (and any secondary key). This method returns false if the
	 * given keys is not in this DoubleKeyMapToList.
	 * 
	 * This method is value-semantic in that no changes are made to the objects
	 * passed into the method.
	 * 
	 * @param key1
	 *            The primary key for testing presence of a List
	 * @return true if this DoubleKeyMapToList contains a List for the given
	 *         primary key; false otherwise.
	 */
	public boolean containsListFor(K1 key1)
	{
		return mtmtl.containsKey(key1);
	}

	/**
	 * Returns true if this DoubleKeyMapToList contains a List for the given
	 * keys. This method returns false if the given keys are not in this
	 * DoubleKeyMapToList.
	 * 
	 * This method is value-semantic in that no changes are made to the objects
	 * passed into the method.
	 * 
	 * @param key1
	 *            The primary key for testing presence of a List
	 * @param key2
	 *            The secondary key for testing presence of a List
	 * @return true if this DoubleKeyMapToList contains a List for the given
	 *         keys; false otherwise.
	 */
	public boolean containsListFor(K1 key1, K2 key2)
	{
		MapToList<K2, V> localMap = mtmtl.get(key1);
		return (localMap != null) && localMap.containsListFor(key2);
	}

	/**
	 * Removes the Lists for the given primary key. Note there is no requirement
	 * that the lists for the given primary key be empty before this method is
	 * called.
	 * 
	 * Obviously, ownership of the returned Map is transferred to the object
	 * calling this method.
	 * 
	 * @param key1
	 *            The primary key indicating the Lists to remove
	 * @return The Map representing the secondary keys and lists previously
	 *         stored in this DoubleKeyMapToList for the given primary key
	 */
	public MapToList<K2, V> removeListsFor(K1 key1)
	{
		return mtmtl.remove(key1);
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
	 * @return The List which this DoubleKeyMapToList previous mapped the given
	 *         keys
	 */
	public List<V> removeListFor(K1 key1, K2 key2)
	{
		MapToList<K2, V> localMap = mtmtl.get(key1);
		if (localMap == null)
		{
			return null;
		}
		List<V> removed = localMap.removeListFor(key2);
		// cleanup!
		if (localMap.isEmpty())
		{
			mtmtl.remove(key1);
		}
		return removed;
	}

	/**
	 * Removes the given value from the list for the given keys. Returns true if
	 * the value was successfully removed from the list for the given key.
	 * Returns false if there is not a list for the given keys or if the list
	 * for the given keys did not contain the given value object.
	 * 
	 * @param key1
	 *            The primary key indicating which List the given object should
	 *            be removed from
	 * @param key2
	 *            The secondary key indicating which List the given object
	 *            should be removed from
	 * @param valueElement
	 *            The value to be removed from the List for the given keys
	 * @return true if the value was successfully removed from the list for the
	 *         given keys; false otherwise
	 */
	public boolean removeFromListFor(K1 key1, K2 key2, V valueElement)
	{
		/*
		 * Note there is no requirement that a Key is added before this method
		 * is called
		 */
		MapToList<K2, V> localMap = mtmtl.get(key1);
		if (localMap == null)
		{
			return false;
		}
		boolean wasRemoved = localMap.removeFromListFor(key2, valueElement);
		// cleanup!
		if (wasRemoved && localMap.isEmpty())
		{
			mtmtl.remove(key1);
		}
		return wasRemoved;
	}

	/**
	 * Returns a Set indicating the primary Keys of this DoubleKeyMapToList.
	 * 
	 * Ownership of the Set is transferred to the calling Object, no association
	 * is kept between the Set and this MapToList. (Thus, removal of a key from
	 * the returned Set will not remove that key from this DoubleKeyMapToList)
	 * 
	 * NOTE: This method returns all of the keys this DoubleKeyMapToList
	 * contains. It DOES NOT determine whether the Lists defined for the keys
	 * are empty. Therefore, it is possible that this DoubleKeyMapToList
	 * contains one or more keys, and all of the lists associated with those
	 * keys are empty, yet this method will return a non-zero length Set.
	 * 
	 * @return a Set containing the primary keys in this DoubleKeyMapToList
	 */
	public Set<K1> getKeySet()
	{
		// Need to 'clone' the Set, since Map returns a set that is still
		// associated with the Map
		Set<K1> set = Collections.newSetFromMap(createGlobalMap());
		set.addAll(mtmtl.keySet());
		return set;
	}

	/**
	 * Returns a Set of the secondary keys for the given primary key in this
	 * DoubleKeyMapToList
	 * 
	 * NOTE: This method returns all of the secondary keys this
	 * DoubleKeyMapToList contains for the given primary key. It DOES NOT
	 * determine whether the Lists defined for the keys are empty. Therefore, it
	 * is possible that this DoubleKeyMapToList contains one or more keys, and
	 * all of the lists associated with those keys are empty, yet this method
	 * will return a non-zero length Set.
	 * 
	 * Note: Ownership of the Set is transferred to the calling Object;
	 * therefore, changes to the returned Set will NOT impact the
	 * DoubleKeyMapToList.
	 * 
	 * @param key1
	 *            The primary key to retrieve keys for.
	 * 
	 * @return A Set of secondary key objects for the given primary key.
	 */
	public Set<K2> getSecondaryKeySet(K1 key1)
	{
		MapToList<K2, V> localMap = mtmtl.get(key1);
		return (localMap == null) ? Collections.emptySet() : localMap.getKeySet();
	}

	/**
	 * Clears this DoubleKeyMapToList.
	 */
	public void clear()
	{
		mtmtl.clear();
	}

	/**
	 * Returns true if this DoubleKeyMapToList contains no Lists.
	 * 
	 * NOTE: This method checks whether this DoubleKeyMapToList contains any
	 * Lists for any key. It DOES NOT test whether all Lists defined for all
	 * keys are empty. Therefore, it is possible that this DoubleKeyMapToList
	 * contains one or more keys, and all of the lists associated with those
	 * keys are empty, yet this method will return false.
	 * 
	 * @return true if this DoubleKeyMapToList contains no Lists; false
	 *         otherwise
	 */
	public boolean isEmpty()
	{
		return mtmtl.isEmpty();
	}

	/**
	 * Returns the number of primary key maps contained by this
	 * DoubleKeyMapToList.
	 * 
	 * NOTE: This method counts the number of Lists this DoubleKeyMapToList
	 * contains. It DOES NOT determine whether all Lists defined for all keys
	 * are empty. Therefore, it is possible that this DoubleKeyMapToList
	 * contains one or more keys, and all of the lists associated with those
	 * keys are empty, yet this method will return a non-zero value.
	 * 
	 * @return The number of lists contained by this DoubleKeyMapToList.
	 */
	public int firstKeyCount()
	{
		return mtmtl.size();
	}

	/**
	 * Produces a clone of the DoubleKeyMapToList. This means the internal maps
	 * used to store keys and values are not shared between the original
	 * DoubleKeyMapToList and the clone (modifying one DoubleKeyMapToList will
	 * not impact the other). However, this does not perform a true "deep"
	 * clone, in the sense that the actual keys and values are not cloned.
	 * 
	 * @throws CloneNotSupportedException
	 *             (should not be thrown)
	 */
	@Override
	public DoubleKeyMapToList<K1, K2, V> clone()
		throws CloneNotSupportedException
	{
		@SuppressWarnings("unchecked")
		DoubleKeyMapToList<K1, K2, V> dkm =
				(DoubleKeyMapToList<K1, K2, V>) super.clone();
		dkm.mtmtl = createGlobalMap();
		for (Map.Entry<K1, MapToList<K2, V>> entry : mtmtl.entrySet())
		{
			MapToList<K2, V> currentMTL = entry.getValue();
			MapToList<K2, V> newMTL =
					GenericMapToList.getMapToList(secondClass);
			newMTL.addAllLists(currentMTL);
			dkm.mtmtl.put(entry.getKey(), newMTL);
		}
		return dkm;
	}

	/**
	 * Returns true if this DoubleKeyMapToList contains a List for the given
	 * keys and that list contains the given value. Note, this method returns
	 * false if the given keys are not in this DoubleKeyMapToList.
	 * 
	 * This method is value-semantic in that no changes are made to the objects
	 * passed into the method.
	 * 
	 * @param key1
	 *            The primary key for retrieving the List to be checked
	 * @param key2
	 *            The secondary key for retrieving the List to be checked
	 * @param valueElement
	 *            The value to find in the List for the given keys.
	 * @return true if this DoubleKeyMapToList contains a List for the given
	 *         keys AND that list contains the given value; false otherwise.
	 */
	public boolean containsInList(K1 key1, K2 key2, V valueElement)
	{
		return containsListFor(key1, key2)
			&& mtmtl.get(key1).containsInList(key2, valueElement);
	}

	/**
	 * Returns the number of objects in the List for the given keys. This method
	 * will throw a NullPointerException if this DoubleKeyMapToList does not
	 * contain a List for the given key.
	 * 
	 * This method is value-semantic in that no changes are made to the object
	 * passed into the method.
	 * 
	 * @param key1
	 *            The primary key for retrieving the List to be checked
	 * @param key2
	 *            The secondary key for retrieving the List to be checked
	 * @return the number of objects in the List for the given keys
	 */
	public int sizeOfListFor(K1 key1, K2 key2)
	{
		MapToList<K2, V> localMap = mtmtl.get(key1);
		return (localMap == null) ? 0 : localMap.sizeOfListFor(key2);
	}

	/**
	 * A consistent-with-equals hashCode for DoubleKeyMapToList.
	 */
	@Override
	public int hashCode()
	{
		return mtmtl.hashCode();
	}

	/**
	 * Returns true if the DoubleKeyMapToList is equal to the given Object.
	 * Equality is defined as the given Object being a DoubleKeyMapToList with
	 * equal keys and values as defined by the underlying Maps.
	 */
	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof DoubleKeyMapToList)
			&& mtmtl.equals(((DoubleKeyMapToList<?, ?, ?>) obj).mtmtl);
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
	 * This should remain PRIVATE as it exposes the internal structure of the
	 * DoubleKeyMapToList. It is used as an internal convenience method.
	 * 
	 * @param key1
	 *            The Key for which an internal MapToList should be fetched, or
	 *            created if it does not exist
	 * @return The (internal use only) MapToList for the given key
	 */
	private MapToList<K2, V> getMapToListFor(K1 key1)
	{
		return mtmtl.computeIfAbsent(key1,
			k -> GenericMapToList.getMapToList(secondClass));
	}

}
