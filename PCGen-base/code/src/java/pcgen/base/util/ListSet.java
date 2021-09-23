/*
 * Copyright 2005-2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * Created on June 18, 2005.
 */
package pcgen.base.util;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

/**
 * ListSet is an implementation of the Set Interface that uses a List (rather
 * than the Map that is typically used) as the internal representation of the
 * Set. This is most useful to minimize the memory footprint for very small sets
 * where search time will not be a concern.
 * 
 * @param <T>
 *            The type of object stored in this ListSet
 */
public class ListSet<T> extends AbstractSet<T>
{

	/**
	 * The comparator, if any, for determining equality in this Set.
	 */
	private final Comparator<T> comparator;

	/**
	 * The List used to represent the members of this Set.
	 */
	@SuppressWarnings("PMD.LooseCoupling")
	private final ArrayList<T> list;

	/**
	 * Construct a new, empty ListSet.
	 */
	public ListSet()
	{
		this(10, null);
	}

	/**
	 * Construct a new, empty ListSet, initializing the internal List to the
	 * given size. This constructor should be used when the final size of the
	 * set is known at Set construction, as the size of the ListSet in memory
	 * will be minimized and insertion performance may also improve for large
	 * Sets.
	 * 
	 * @param size
	 *            The initial size of the List backing this Set
	 */
	public ListSet(int size)
	{
		this(size, null);
	}

	/**
	 * Construct a new, empty ListSet, which will use the given Comparator to
	 * determine equality for purposes of determining presence in the Set.
	 * 
	 * @param comp
	 *            The Comparator this Set will use to determine equality
	 */
	public ListSet(Comparator<T> comp)
	{
		this(10, comp);
	}

	/**
	 * Construct a new, empty ListSet, which will use the given Comparator to
	 * determine equality for purposes of determining presence in the Set, and
	 * initializing the internal List to the given size. This constructor should
	 * be used when the final size of the set is known at Set construction, as
	 * the size of the ListSet in memory will be minimized and insertion
	 * performance may also improve for large Sets.
	 * 
	 * @param size
	 *            The initial size of the List backing this Set
	 * @param comp
	 *            The Comparator this Set will use to determine equality
	 */
	public ListSet(int size, Comparator<T> comp)
	{
		list = new ArrayList<>(size);
		comparator = comp;
	}

	/**
	 * Construct a new ListSet with the contents of the given Set.
	 * 
	 * This method is both reference-semantic and value-semantic. No reference
	 * is maintained to the internal structure of the given Collection, so
	 * modifications to this Set are not reflected in the given Collection (and
	 * vice versa). However, objects from the given Collection are maintained by
	 * reference, so modification to the objects contained in either this Set or
	 * the given Collection will be reflected in the other (this is consistent
	 * behavior with the analogous constructors in the java.util.Set
	 * implementations)
	 * 
	 * @param otherSet
	 *            The Set to use as a source of objects for initializing this
	 *            ListSet
	 * @throws NullPointerException
	 *             if the given Set is null
	 */
	public ListSet(Collection<T> otherSet)
	{
		list = new ArrayList<>(otherSet);
		comparator = null;
	}

	/**
	 * Returns the number of Objects in this Set.
	 * 
	 * @return The number of objects in this ListSet
	 */
	@Override
	public int size()
	{
		return list.size();
	}

	/**
	 * Returns an Iterator over the Set.
	 */
	@Override
	public Iterator<T> iterator()
	{
		return list.iterator();
	}

	/**
	 * Adds the given Object to this set if it was not already part of the Set.
	 * returns true if the Object was added to the Set; false otherwise.
	 * 
	 * @param element
	 *            The Object to be added to this Set.
	 * @return true if the Object was added to the Set; false otherwise
	 */
	@Override
	public boolean add(T element)
	{
		boolean contains = false;
		if (comparator == null)
		{
			contains = list.contains(element);
		}
		else
		{
			for (T aList : list)
			{
				if (comparator.compare(aList, element) == 0)
				{
					contains = true;
					break;
				}
			}
		}
		if (!contains)
		{
			return list.add(element);
		}
		return false;
	}

	/**
	 * Ensures that the list underlying this Set has the given capacity. This
	 * method is used for performance optimization when a large number of
	 * objects will be added to the Set
	 * 
	 * @param capacity
	 *            The desired capacity of the underlying List
	 */
	public void ensureCapacity(int capacity)
	{
		list.ensureCapacity(capacity);
	}

	/**
	 * Trims the list underlying this Set to a capacity equal to the number of
	 * elements in the Set. This method is used for memory optimization.
	 */
	public void trimToSize()
	{
		list.trimToSize();
	}

	/**
	 * Clears this Set (removes all Objects from the Set).
	 */
	@Override
	public void clear()
	{
		list.clear();
	}

	/**
	 * Returns true if the given object is present in this Set.
	 * 
	 * @param element
	 *            The Object to be tested
	 * @return true if the Object is present in this Set; false otherwise
	 */
	@Override
	public boolean contains(Object element)
	{
		if (comparator == null)
		{
			return list.contains(element);
		}
		@SuppressWarnings("unchecked")
		T comp = (T) element;
		for (T object : list)
		{
			if (comparator.compare(comp, object) == 0)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if this Set is Empty.
	 * 
	 * @return true if the this Set is Empty; false otherwise
	 */
	@Override
	public boolean isEmpty()
	{
		return list.isEmpty();
	}

	/**
	 * Removes the given Object from this Set. Returns true if the object was
	 * removed, false otherwise.
	 * 
	 * @param element
	 *            The Object to be removed from this Set.
	 * @return true if the Object was removed from the Set; false otherwise
	 */
	@Override
	public boolean remove(Object element)
	{
		if (comparator == null)
		{
			return list.remove(element);
		}
		@SuppressWarnings("unchecked")
		T comp = (T) element;
		for (Iterator<T> it = list.iterator(); it.hasNext();)
		{
			if (comparator.compare(comp, it.next()) == 0)
			{
				it.remove();
				return true;
			}
		}
		return false;
	}
}
