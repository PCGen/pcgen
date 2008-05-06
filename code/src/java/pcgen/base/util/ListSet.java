/*
 * Copyright 2005-2007 (C) Tom Parker <thpr@sourceforge.net>
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
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * ListSet is an implementation of the Set Interface that uses a List (rather
 * than the Map that is typically used) as the internal representation of the
 * Set. This is most useful to minimize the memory footprint for very small sets
 * where search time will not be a concern.
 */
public class ListSet<T> extends AbstractSet<T> implements Set<T>
{

	/**
	 * The comparator, if any, for determining equality in this Set
	 */
	private final Comparator<T> comparator;

	/**
	 * The List used to represent the members of this Set
	 */
	private final ArrayList<T> list;

	/**
	 * Construct a new, empty ListSet
	 */
	public ListSet()
	{
		this(null);
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
	 * @param c
	 *            The Comparator this Set will use to determine equality
	 */
	public ListSet(Comparator<T> c)
	{
		this(10, c);
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
	 * @param c
	 *            The Comparator this Set will use to determine equality
	 */
	public ListSet(int size, Comparator<T> c)
	{
		list = new ArrayList<T>(size);
		comparator = c;
	}

	/**
	 * Returns the number of Objects in this Set
	 */
	@Override
	public int size()
	{
		return list.size();
	}

	/**
	 * Returns an Iterator over the Set
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
	 * @param arg0
	 *            The Object to be added to this Set.
	 * @return true if the Object was added to the Set; false otherwise
	 */
	@Override
	public boolean add(T arg0)
	{
		boolean contains = false;
		if (comparator == null)
		{
			contains = list.contains(arg0);
		}
		else
		{
			for (Iterator<T> it = list.iterator(); it.hasNext();)
			{
				if (comparator.compare(it.next(), arg0) == 0)
				{
					contains = true;
					break;
				}
			}
		}
		if (!contains)
		{
			return list.add(arg0);
		}
		return false;
	}

	/**
	 * Ensures that the list underlying this Set has the given capacity. This
	 * method is used for performance optimization when a large number of
	 * objects will be added to the Set
	 * 
	 * @param arg0
	 *            The desired capacity of the underlying List
	 */
	public void ensureCapacity(int arg0)
	{
		list.ensureCapacity(arg0);
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
	 * Clears this Set (removes all Objects from the Set)
	 */
	@Override
	public void clear()
	{
		list.clear();
	}

	/**
	 * Returns true if the given object is present in this Set.
	 * 
	 * @param arg0
	 *            The Object to be tested
	 * @return true if the Object is present in this Set; false otherwise
	 */
	@Override
	public boolean contains(Object arg0)
	{
		if (comparator == null)
		{
			return list.contains(arg0);
		}
		T comp = (T) arg0;
		for (Iterator<T> it = list.iterator(); it.hasNext();)
		{
			if (comparator.compare(comp, it.next()) == 0)
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
	 * @param arg0
	 *            The Object to be removed from this Set.
	 * @return true if the Object was removed from the Set; false otherwise
	 */
	@Override
	public boolean remove(Object arg0)
	{
		if (comparator == null)
		{
			return list.remove(arg0);
		}
		T comp = (T) arg0;
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
