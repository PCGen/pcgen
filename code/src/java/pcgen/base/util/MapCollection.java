/*
 * Copyright 2006, 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * Created on Oct 31, 2006
 *
 * Current Ver: $Revision: 1060 $
 * Last Editor: $Author: boomer70 $
 * Last Edited: $Date: 2006-06-08 23:25:16 -0400 (Thu, 08 Jun 2006) $
 */
package pcgen.base.util;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Thomas Parker
 * 
 * A MapCollection is a facade used to convert a Map to a Collection. This is
 * useful if one wishes to treat the contents of the Map (both the keys and the
 * values) as a single Collection. For example, this could be used to join
 * together the contents of a Map.
 * 
 * As this is a facade emulating a simple data structure (with a complex
 * underlying data structure), certain functions are ill-defined. Modification
 * of the collection is prohibited, and all methods that would normally modify a
 * Collection (such as add, remove or clear) will throw an
 * UnsupportedOperationException.
 * 
 * This class is reference-semantic. A reference to the Map provided in the
 * constructor is kept by the MapCollection. ALL changes to the underlying Map
 * are not reflected within this Collection. Beware that an Iterator provided by
 * a MapCollection is therefore sensitive to modification of the underlying Map
 * and MAY cause a ConcurrentModificationException to occur if the underlying
 * Map is modified.
 * 
 * **WARNING** MapCollection is KNOWN to NOT fail fast in the case of a
 * Concurrent Modification - such a feature is considered more advanced than
 * this Class is trying to provide (the additional cost of memory and CPU of
 * monitoring the map is considered an unreasonable burden).
 */
public class MapCollection extends AbstractCollection<Object>
{

	/*
	 * Note that a MapCollection cannot provide a proper Generic operation for
	 * Collection, due to the potential conflicts between keys and objects in
	 * the underlying Map.
	 * 
	 * For example, a Map of <String, Runnable> becomes difficult to have
	 * Generics work correctly if one is converting a Map to a Collection.
	 */
	/**
	 * The map underlying this Collection.
	 */
	private final Map<?, ?> map;

	/**
	 * Builds a new MapCollection, providing a facade to the given Map. The
	 * given Map must be non-null.
	 * 
	 * This constructor is reference-semantic. A direct reference to the
	 * provided Map is maintained, in order to iterate over the contents of the
	 * Map.
	 * 
	 * @param otherMap
	 *            The map to be treated as a Collection.
	 * @throws IllegalArgumentException
	 *             if the given Map is null
	 */
	public MapCollection(Map<?, ?> otherMap)
	{
		if (otherMap == null)
		{
			throw new IllegalArgumentException(
					"Cannot provide null to MapCollection");
		}
		map = otherMap;
	}

	/**
	 * Attempts to modify the collection. This is an unsupported operation on a
	 * MapCollection, and an UnsupportedOperationException() will be thrown.
	 * 
	 * This should be kept as an override of AbstractCollection (even if it
	 * replicates function), as it ensures future changes will not provide very
	 * strange results.
	 * 
	 * @throws UnsupportedOperationException
	 *             unconditionally
	 */
	@Override
	public boolean add(Object element)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Attempts to modify the collection. This is an unsupported operation on a
	 * MapCollection, and an UnsupportedOperationException() will be thrown.
	 * 
	 * This should be kept as an override of AbstractCollection (even if it
	 * replicates function), as it ensures future changes will not provide very
	 * strange results.
	 * 
	 * @throws UnsupportedOperationException
	 *             unconditionally
	 */
	@Override
	public boolean addAll(Collection<?> coll)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Attempts to modify the collection. This is an unsupported operation on a
	 * MapCollection, and an UnsupportedOperationException() will be thrown.
	 * 
	 * This should be kept as an override of AbstractCollection (even if it
	 * replicates function), as it ensures future changes will not provide very
	 * strange results.
	 * 
	 * @throws UnsupportedOperationException
	 *             unconditionally
	 */
	@Override
	public void clear()
	{
		/*
		 * While it is possible to have this actually clear the underlying Map,
		 * I believe that is a bad design decision. This facade should be
		 * consistent in the fact that it DOES NOT modify the underlying Map.
		 * Having clear() as an exception to that is not a good design decision,
		 * in my opinion - Thomas Parker 1/17/07
		 */
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns true if the underlying Map contains the given Object as either a
	 * Key or a Value in the Map.
	 * 
	 * @param element
	 *            The Object to check to see if it is present in the underlying
	 *            Map.
	 * @return true if the underlying Map contains the given Object; false
	 *         otherwise.
	 */
	@Override
	public boolean contains(Object element)
	{
		return map.containsKey(element) || map.containsValue(element);
	}

	/**
	 * Returns true if the underlying Map contains all of the Objects in the
	 * given Collection, as either a Key or a Value in the Map. The order of the
	 * Objects in the given Collection is not relevant to the ability to match a
	 * Key or a Value in the underlying Map. The given Collection must be not be
	 * null.
	 * 
	 * This method is value-semantic in that the given Collection and the
	 * contents of the Collection are not modified by this method call.
	 * 
	 * @param collection
	 *            The Collection of Objects to be tested for presence in the
	 *            underlying Map.
	 * @return true if all of the Objects in the given Collection are present in
	 *         the underlying Map; false otherwise.
	 * @throws NullPointerException
	 *             if the given Collection is null
	 */
	@Override
	public boolean containsAll(Collection<?> collection)
	{
		for (Object element : collection)
		{
			if (!map.containsKey(element) && !map.containsValue(element))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns true if this MapCollection is empty. (It will be empty if the
	 * underlying Map is also empty).
	 * 
	 * @return true if the MapCollection is empty.
	 */
	@Override
	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	/**
	 * Provides a new Iterator for iterating over the contents of this
	 * MapCollection.
	 * 
	 * @return An Iterator over the contents of this MapCollection.
	 */
	@Override
	public Iterator<Object> iterator()
	{
		return new MapCollectionIterator(map);
	}

	/**
	 * Attempts to modify the collection. This is an unsupported operation on a
	 * MapCollection, and an UnsupportedOperationException() will be thrown.
	 * 
	 * This should be kept as an override of AbstractCollection (even if it
	 * replicates function), as it ensures future changes will not provide very
	 * strange results.
	 * 
	 * @throws UnsupportedOperationException
	 *             unconditionally
	 */
	@Override
	public boolean remove(Object element)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Attempts to modify the collection. This is an unsupported operation on a
	 * MapCollection, and an UnsupportedOperationException() will be thrown.
	 * 
	 * This should be kept as an override of AbstactCollection (even if it
	 * replicates function), as it ensures future changes will not provide very
	 * strange results.
	 * 
	 * @throws UnsupportedOperationException
	 *             unconditionally
	 */
	@Override
	public boolean removeAll(Collection<?> collection)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Attempts to modify the collection. This is an unsupported operation on a
	 * MapCollection, and an UnsupportedOperationException() will be thrown.
	 * 
	 * This should be kept as an override of AbstractCollection (even if it
	 * replicates function), as it ensures future changes will not provide very
	 * strange results.
	 * 
	 * @throws UnsupportedOperationException
	 *             unconditionally
	 */
	@Override
	public boolean retainAll(Collection<?> collection)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the size of this MapCollection. Note that the size of a
	 * MapCollection will be twice the size of the underlying Map.
	 * 
	 * @return The size of this MapCollection.
	 */
	@Override
	public int size()
	{
		return 2 * map.size();
	}

	private static class MapCollectionIterator implements Iterator<Object>
	{
		private Entry<?, ?> workingEntry;

		private boolean returnedKey = false;

		private final Iterator<?> hashIterator;

		MapCollectionIterator(Map<?, ?> map)
		{
			hashIterator = map.entrySet().iterator();
		}

		@Override
		public boolean hasNext()
		{
			return returnedKey || hashIterator.hasNext();
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Object next()
		{
			if (returnedKey)
			{
				returnedKey = false;
				return workingEntry.getValue();
			}
			else
			{
				workingEntry = (Entry<?, ?>) hashIterator.next();
				returnedKey = true;
				return workingEntry.getKey();
			}
		}

	}

}
