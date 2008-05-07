/*
 * Copyright 2005 (C) Tom Parker <thpr@sourceforge.net>
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
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
 * @param <K1>
 *            The type of the primary keys in this DoubleKeyMapToList
 * @param <K2>
 *            The type of the secondary keys in this DoubleKeyMapToList
 * @param <V>
 *            The type of the values in this DoubleKeyMapToList
 */
public class DoubleKeyMapToList<K1, K2, V> implements Cloneable
{
	private final Class<? extends Map> firstClass;
	private final Class<? extends Map> secondClass;

	/**
	 * The actual map containing the map to map to Lists
	 */
	private Map<K1, MapToList<K2, V>> mtmtl = new HashMap<K1, MapToList<K2, V>>();

	/**
	 * Constructs a new DoubleKeyMapToList
	 */
	public DoubleKeyMapToList()
	{
		super();
		firstClass = secondClass = HashMap.class;
	}

	/**
	 * Constructs a new DoubleKeyMapToList
	 */
	public DoubleKeyMapToList(Class<? extends Map> cl1, Class<? extends Map> cl2)
	{
		super();
		firstClass = cl1;
		secondClass = cl2;
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
	 * @param value
	 *            The value to be added to the List for the given keys.
	 */
	public void addToListFor(K1 key1, K2 key2, V value)
	{
		MapToList<K2, V> localMap = mtmtl.get(key1);
		if (localMap == null)
		{
			localMap = GenericMapToList.getMapToList(secondClass);
			mtmtl.put(key1, localMap);
		}
		localMap.addToListFor(key2, value);
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
		if (localMap == null)
		{
			return null;
		}
		return localMap.getListFor(key2);
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
		if (localMap == null)
		{
			return false;
		}
		return localMap.containsListFor(key2);
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
		List<V> o = localMap.removeListFor(key2);
		// cleanup!
		if (localMap.isEmpty())
		{
			mtmtl.remove(key1);
		}
		return o;
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
	 * @param value
	 *            The value to be removed from the List for the given keys
	 * @return true if the value was successfully removed from the list for the
	 *         given keys; false otherwise
	 */
	public boolean removeFromListFor(K1 key1, K2 key2, V value)
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
		boolean b = localMap.removeFromListFor(key2, value);
		// cleanup!
		if (b && localMap.isEmpty())
		{
			mtmtl.remove(key1);
		}
		return b;
	}

	/**
	 * Returns a Set indicating the primary Keys of this DoubleKeyMapToList.
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
		return new WrappedMapSet<K1>(firstClass, mtmtl.keySet());
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
	 * Note: This Set is reference-semantic. The ownership of the Set is
	 * transferred to the calling Object; therefore, changes to the returned Set
	 * will NOT impact the DoubleKeyMapToList.
	 * 
	 * @param aPrimaryKey
	 *            The primary key to retrieve keys for.
	 * 
	 * @return A <tt>Set</tt> of secondary key objects for the given primary
	 *         key.
	 */
	public Set<K2> getSecondaryKeySet(final K1 aPrimaryKey)
	{
		MapToList<K2, V> localMap = mtmtl.get(aPrimaryKey);
		if (localMap == null)
		{
			return Collections.emptySet();
		}
		return localMap.getKeySet();
	}

	/**
	 * Clears this DoubleKeyMapToList
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

	@Override
	public DoubleKeyMapToList clone() throws CloneNotSupportedException
	{
		DoubleKeyMapToList<K1, K2, V> dkm = (DoubleKeyMapToList<K1, K2, V>) super
				.clone();
		dkm.mtmtl = createGlobalMap();
		for (Iterator<K1> it = mtmtl.keySet().iterator(); it.hasNext();)
		{
			K1 key = it.next();
			MapToList<K2, V> m = mtmtl.get(key);
			MapToList<K2, V> hmtl = GenericMapToList.getMapToList(secondClass);
			hmtl.addAllLists(m);
			dkm.mtmtl.put(key, hmtl);
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
	 * @param value
	 *            The value to find in the List for the given keys.
	 * @return true if this DoubleKeyMapToList contains a List for the given
	 *         keys AND that list contains the given value; false otherwise.
	 */
	public boolean containsInList(K1 key1, K2 key2, V value)
	{
		return containsListFor(key1, key2)
				&& mtmtl.get(key1).containsInList(key2, value);
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
		return localMap == null ? 0 : localMap.sizeOfListFor(key2);
	}

	@Override
	public int hashCode()
	{
		return mtmtl.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof DoubleKeyMapToList
				&& mtmtl.equals(((DoubleKeyMapToList<?, ?, ?>) o).mtmtl);
	}

	private Map<K1, MapToList<K2, V>> createGlobalMap()
	{
		try
		{
			return firstClass.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new IllegalArgumentException(
					"Class for DoubleKeyMap must possess a zero-argument constructor",
					e);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalArgumentException(
					"Class for DoubleKeyMap must possess a public zero-argument constructor",
					e);
		}
	}
}
