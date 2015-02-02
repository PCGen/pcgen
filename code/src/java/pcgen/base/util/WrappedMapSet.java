/*
 * Copyright (c) Thomas Parker, 2005, 2007.
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.base.util;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A WrappedMapSet allows the conversion of an arbitrary Map into a Set
 * (WrappedMapSet uses the keys as the contents of the set).
 * 
 * Since WrappedMapSet leverages existing classes that implement java.util.Map,
 * it also inherits any limitations on those classes. For example, if the
 * underlying Map is a java.util.HashMap, then modifying an object in this set
 * to alter the hashCode of that object may result in unpredictable behavior
 * from the WrappedMapSet. Be careful to read the documentation on the
 * underlying Map class to ensure appropriate treatment of objects placed in the
 * WrappedMapSet.
 * 
 * This is inefficient in cases where the underlying class behavior is known;
 * both TreeSet and HashSet, for example, would be preferred to wrapping a
 * TreeMap or HashMap with a WrappedMapSet. (This preference is due to clarity
 * for developers who should be familiar with the base classes included in the
 * JDK, as well as performance, as it is expected that the classes in the JDK
 * will have better performance and test coverage than this class)
 * 
 * @param <T>
 *            The type of object stored in this WrappedMapSet
 */
public class WrappedMapSet<T> extends AbstractSet<T> implements Set<T>
{
	/**
	 * The object used to indicate that a given Key in the underlying Map is
	 * part of the Set represented by this WrappedMapSet.
	 */
	private static final Object PRESENCE = new Object();

	/**
	 * The underlying Map used to produce the Set.
	 */
	private Map<T, Object> map;

	/**
	 * Constructs a new WrappedMapSet with an instance of the given Class as the
	 * underlying Map. The given Class MUST have a public, zero-argument
	 * constructor.
	 * 
	 * @param <C>
	 *            Generic constraining construction of a WrappedMapSet to a
	 *            class that implements the Map interface.
	 * @param cl
	 *            The Class (must implement the java.util.Map interface) used
	 *            for the Map underlying this WrappedMapSet
	 * @throws IllegalArgumentException
	 *             if the given Class is null or does not have a public, zero
	 *             argument constructor.
	 */
	public <C extends Map> WrappedMapSet(Class<C> cl)
	{
		if (cl == null)
		{
			throw new IllegalArgumentException(
					"Class passed to WrappedMapSet must not be null");
		}
		try
		{
			map = cl.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new IllegalArgumentException(
					"Expected a Class passed to WrappedMapSet to "
							+ "have a zero argument constructor", e);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalArgumentException(
					"Expected a Class passed to WrappedMapSet to "
							+ "have a public, zero argument constructor", e);
		}
	}

	/**
	 * Constructs a new WrappedMapSet with an instance of the given Class as the
	 * underlying Map. The given Class MUST have a public, zero-argument
	 * constructor. The WrappedMapSet is then loaded with the contents of the
	 * given collection.
	 * 
	 * WrappedMapSet is does not maintain a reference to the given Collection.
	 * Once this method returns, the given Collection can be modified without
	 * altering the WrappedMapSet.
	 * 
	 * However, WrappedMapSet does maintain a reference to the objects contained
	 * within the given Collection. Alteration of those underlying objects will
	 * modify the objects in the WrappedMapSet.
	 * 
	 * @param <C>
	 *            Generic constraining construction of a WrappedMapSet to a
	 *            class that implements the Map interface.
	 * @param cl
	 *            The Class (must implement the java.util.Map interface) used
	 *            for the Map underlying this WrappedMapSet
	 * @param collection
	 *            A collection to be used to initialize the contents of the
	 *            WrappedMapSet.
	 * @throws IllegalArgumentException
	 *             if the given Class is null or does not have a public, zero
	 *             argument constructor.
	 * @throws NullPointerException
	 *             if the given Collection is null
	 */
	public <C extends Map> WrappedMapSet(Class<C> cl,
			Collection<? extends T> collection)
	{
		this(cl);
		addAll(collection);
	}

	/**
	 * Iterates over the contents of the WrappedMapSet. The iteration will occur
	 * in the order defined by the order of iteration over the keys in the
	 * underlying Map class.
	 * 
	 * @see java.util.AbstractCollection#iterator()
	 */
	@Override
	public Iterator<T> iterator()
	{
		return map.keySet().iterator();
	}

	/**
	 * Returns the number of objects contained within the WrappedMapSet.
	 * 
	 * @see java.util.AbstractCollection#size()
	 */
	@Override
	public int size()
	{
		return map.size();
	}

	/**
	 * Returns true if the WrappedMapSet is empty; false otherwise.
	 * 
	 * @see java.util.AbstractCollection#isEmpty()
	 */
	@Override
	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	/**
	 * Returns true if the WrappedMapSet contains the given Object; false
	 * otherwise.
	 * 
	 * @see java.util.AbstractCollection#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object item)
	{
		return map.containsKey(item);
	}

	/**
	 * Adds the given Object to the WrappedMapSet.
	 * 
	 * @see java.util.AbstractCollection#add(java.lang.Object)
	 */
	@Override
	public boolean add(T item)
	{
		return map.put(item, PRESENCE) == null;
	}

	/**
	 * Removes the given Object from the WrappedMapSet.
	 * 
	 * @see java.util.AbstractCollection#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object item)
	{
		return map.remove(item) == PRESENCE;
	}

	/**
	 * Removes all objects from the WrappedMapSet.
	 * 
	 * @see java.util.AbstractCollection#clear()
	 */
	@Override
	public void clear()
	{
		map.clear();
	}
}
