/*
 * Copyright 2009 (C) Tom Parker <thpr@users.sourceforge.net>
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

import static pcgen.base.util.ArrayUtilities.usingArray;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * IdentityList is an implementation of the List Interface that uses Identity
 * (==) rather than equality (.equals() ) to establish behavior for remove. This
 * is useful to maintain an input ordered identity list (not possible with
 * IdentityHashMap because it does not maintain input order).
 * 
 * @param <T>
 *            The type of object stored in this IdentityList
 */
@SuppressWarnings("PMD.TooManyMethods")
public class IdentityList<T> implements List<T>
{
	/**
	 * The underlying map providing storage of Identity structures.
	 */
	private final List<Identity<T>> embeddedList =
			new LinkedList<>();

	/**
	 * Creates a new (empty) IdentityList.
	 */
	public IdentityList()
	{
		super();
	}

	/**
	 * Creates a new IdentityList which will be initialized with the contents
	 * of the given List.
	 * 
	 * @param list
	 *            The list of objects used to initialize the contents of this
	 *            IdentityList
	 */
	public IdentityList(List<T> list)
	{
		addAll(list);
	}

	@Override
	public void add(int index, T element)
	{
		embeddedList.add(index, Identity.valueOf(element));
	}

	@Override
	public final boolean add(T element)
	{
		return embeddedList.add(Identity.valueOf(element));
	}

	@Override
	public final boolean addAll(Collection<? extends T> collection)
	{
		collection.forEach(this::add);
		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> collection)
	{
		int location = index;
		for (T element : collection)
		{
			add(location++, element);
		}
		return true;
	}

	@Override
	public void clear()
	{
		embeddedList.clear();
	}

	@Override
	public boolean contains(Object element)
	{
		return embeddedList.contains(Identity.valueOf(element));
	}

	@Override
	public boolean containsAll(Collection<?> collection)
	{
		for (Object element : collection)
		{
			if (!embeddedList.contains(Identity.valueOf(element)))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof IdentityList
			&& embeddedList.equals(((IdentityList<?>) obj).embeddedList);
	}

	@Override
	public T get(int index)
	{
		return embeddedList.get(index).getUnderlying();
	}

	@Override
	public int hashCode()
	{
		return embeddedList.hashCode();
	}

	@Override
	public int indexOf(Object element)
	{
		return embeddedList.indexOf(Identity.valueOf(element));
	}

	@Override
	public boolean isEmpty()
	{
		return embeddedList.isEmpty();
	}

	@Override
	public Iterator<T> iterator()
	{
		return new IdentityIterator<>(embeddedList.listIterator());
	}

	@Override
	public int lastIndexOf(Object element)
	{
		return embeddedList.lastIndexOf(Identity.valueOf(element));
	}

	@Override
	public ListIterator<T> listIterator()
	{
		return new IdentityIterator<>(embeddedList.listIterator());
	}

	@Override
	public ListIterator<T> listIterator(int index)
	{
		return new IdentityIterator<>(embeddedList.listIterator(index));
	}

	@Override
	public T remove(int index)
	{
		return embeddedList.remove(index).getUnderlying();
	}

	@Override
	public boolean remove(Object element)
	{
		return embeddedList.remove(Identity.valueOf(element));
	}

	@Override
	public boolean removeAll(Collection<?> collection)
	{
		boolean result = true;
		for (Object o : collection)
		{
			result &= remove(o);
		}
		return result;
	}

	@Override
	public boolean retainAll(Collection<?> collection)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public T set(int index, T element)
	{
		return embeddedList.set(index, Identity.valueOf(element)).getUnderlying();
	}

	@Override
	public int size()
	{
		return embeddedList.size();
	}

	@Override
	public List<T> subList(int startIndex, int endIndex)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString()
	{
		return embeddedList.stream().map(o -> o.getUnderlying().toString())
			.collect(Collectors.joining(",", "[", "]"));
	}

	@Override
	public Object[] toArray()
	{
		return embeddedList.stream()
						   .map(x -> x.getUnderlying())
						   .toArray();
	}

	@Override
	public <V> V[] toArray(V[] newArray)
	{
		return embeddedList.stream()
						   .map(x -> x.getUnderlying())
						   .toArray(usingArray(newArray));
	}

	/**
	 * An Iterator used to dynamically wrap and unwrap objects inside of
	 * Identity.
	 * 
	 * @param <I>
	 *            The type of object underlying this IdentityIterator
	 */
	private static final class IdentityIterator<I> implements ListIterator<I>
	{
		/**
		 * The ListIterator underlying this IdentityIterator.
		 */
		private final ListIterator<Identity<I>> iter;

		/**
		 * Constructs a new IdentityIterator with the given underlying
		 * ListIterator.
		 * 
		 * @param iterator
		 *            The ListIterator underlying this IdentityIterator
		 */
		private IdentityIterator(ListIterator<Identity<I>> iterator)
		{
			iter = iterator;
		}

		@Override
		public void add(I item)
		{
			iter.add(Identity.valueOf(item));
		}

		@Override
		public boolean hasNext()
		{
			return iter.hasNext();
		}

		@Override
		public boolean hasPrevious()
		{
			return iter.hasPrevious();
		}

		@Override
		public I next()
		{
			return iter.next().getUnderlying();
		}

		@Override
		public int nextIndex()
		{
			return iter.nextIndex();
		}

		@Override
		public I previous()
		{
			return iter.previous().getUnderlying();
		}

		@Override
		public int previousIndex()
		{
			return iter.previousIndex();
		}

		@Override
		public void remove()
		{
			iter.remove();
		}

		@Override
		public void set(I item)
		{
			iter.set(Identity.valueOf(item));
		}

	}
}
