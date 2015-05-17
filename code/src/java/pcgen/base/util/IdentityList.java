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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

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
			new LinkedList<Identity<T>>();

	/**
	 * Creates a new (empty) IdentityList.
	 */
	public IdentityList()
	{
		super();
	}

	/**
	 * Createss a new IdentityList which will be initialized with the contents
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

	/**
	 * Internal class used to convert an object to the Identity wrapper for that
	 * object.
	 */
	private <V> Identity<V> getIdentity(V value)
	{
		return new Identity<V>(value);
	}

	/**
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	@Override
	public void add(int index, T element)
	{
		embeddedList.add(index, getIdentity(element));
	}

	/**
	 * @see java.util.List#add(java.lang.Object)
	 */
	@Override
	public final boolean add(T element)
	{
		return embeddedList.add(getIdentity(element));
	}

	/**
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	@Override
	public final boolean addAll(Collection<? extends T> collection)
	{
		for (T t : collection)
		{
			add(t);
		}
		return true;
	}

	/**
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	@Override
	public boolean addAll(int index, Collection<? extends T> collection)
	{
		int location = index;
		for (T t : collection)
		{
			add(location++, t);
		}
		return true;
	}

	/**
	 * @see java.util.List#clear()
	 */
	@Override
	public void clear()
	{
		embeddedList.clear();
	}

	/**
	 * @see java.util.List#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object element)
	{
		return embeddedList.contains(getIdentity(element));
	}

	/**
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> collection)
	{
		for (Object o : collection)
		{
			if (!embeddedList.contains(getIdentity(o)))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof IdentityList
			&& embeddedList.equals(((IdentityList<?>) obj).embeddedList);
	}

	/**
	 * @see java.util.List#get(int)
	 */
	@Override
	public T get(int index)
	{
		Identity<T> und = embeddedList.get(index);
		return und == null ? null : und.getUnderlying();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return embeddedList.hashCode();
	}

	/**
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	@Override
	public int indexOf(Object element)
	{
		return embeddedList.indexOf(element);
	}

	/**
	 * @see java.util.List#isEmpty()
	 */
	@Override
	public boolean isEmpty()
	{
		return embeddedList.isEmpty();
	}

	/**
	 * @see java.util.List#iterator()
	 */
	@Override
	public Iterator<T> iterator()
	{
		return new IdentityIterator<T>(embeddedList.listIterator());
	}

	/**
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	@Override
	public int lastIndexOf(Object element)
	{
		return embeddedList.lastIndexOf(getIdentity(element));
	}

	/**
	 * @see java.util.List#listIterator()
	 */
	@Override
	public ListIterator<T> listIterator()
	{
		return new IdentityIterator<T>(embeddedList.listIterator());
	}

	/**
	 * @see java.util.List#listIterator(int)
	 */
	@Override
	public ListIterator<T> listIterator(int index)
	{
		return new IdentityIterator<T>(embeddedList.listIterator(index));
	}

	/**
	 * @see java.util.List#remove(int)
	 */
	@Override
	public T remove(int index)
	{
		Identity<T> und = embeddedList.remove(index);
		return und == null ? null : und.getUnderlying();
	}

	/**
	 * @see java.util.List#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object element)
	{
		return embeddedList.remove(getIdentity(element));
	}

	/**
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
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

	/**
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> collection)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	@Override
	public T set(int index, T element)
	{
		Identity<T> und = embeddedList.set(index, getIdentity(element));
		return und == null ? null : und.getUnderlying();
	}

	/**
	 * @see java.util.List#size()
	 */
	@Override
	public int size()
	{
		return embeddedList.size();
	}

	/**
	 * @see java.util.List#subList(int, int)
	 */
	@Override
	public List<T> subList(int startIndex, int endIndex)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @see java.util.List#toArray()
	 */
	@Override
	public Object[] toArray()
	{
		Object[] array = embeddedList.toArray();
		putIntoArray(array, array);
		return array;
	}

	private <V> void putIntoArray(Object[] source, V[] target)
	{
		for (int i = 0; i < source.length; i++)
		{
			Identity<V> identity = (Identity<V>) source[i];
			target[i] = identity == null ? null : identity.getUnderlying();
		}
	}

	/**
	 * @see java.util.List#toArray(Object[])
	 */
	@Override
	public <V> V[] toArray(V[] newArray)
	{
		Object[] array = embeddedList.toArray();
		int size = embeddedList.size();
		V[] returnArray = newArray;
		// Protect for small array
		if (newArray.length < size)
		{
			returnArray = (V[]) java.lang.reflect.Array.newInstance(newArray
					.getClass().getComponentType(), size);
		}
		putIntoArray(array, returnArray);
		return returnArray;
	}

	/**
	 * An object used to wrap an object to ensure checks are done with identity
	 * (==) not equality (.equals())
	 * 
	 * @param <T>
	 *            The type of object underlying this Identity
	 */
	private static final class Identity<T>
	{

		private final T underlying;

		public Identity(T item)
		{
			underlying = item;
		}

		@Override
		public boolean equals(Object obj)
		{
			return obj instanceof Identity
					&& ((Identity<?>) obj).underlying == underlying;
		}

		@Override
		public int hashCode()
		{
			return underlying.hashCode();
		}

		public T getUnderlying()
		{
			return underlying;
		}

	}

	/**
	 * An Iterator used to dynamically wrap and unwrap objects inside of
	 * Identity.
	 * 
	 * @param <I>
	 *            The type of object underlying this IdentityIterator
	 */
	private class IdentityIterator<I> implements ListIterator<I>
	{
		private final ListIterator<Identity<I>> iter;

		public IdentityIterator(ListIterator<Identity<I>> iterator)
		{
			iter = iterator;
		}

		/**
		 * @see java.util.ListIterator#add(java.lang.Object)
		 */
		@Override
		public void add(I item)
		{
			iter.add(getIdentity(item));
		}

		/**
		 * @see java.util.ListIterator#hasNext()
		 */
		@Override
		public boolean hasNext()
		{
			return iter.hasNext();
		}

		/**
		 * @see java.util.ListIterator#hasPrevious()
		 */
		@Override
		public boolean hasPrevious()
		{
			return iter.hasPrevious();
		}

		/**
		 * @see java.util.ListIterator#next()
		 */
		@Override
		public I next()
		{
			Identity<I> und = iter.next();
			return und == null ? null : und.getUnderlying();
		}

		/**
		 * @see java.util.ListIterator#nextIndex()
		 */
		@Override
		public int nextIndex()
		{
			return iter.nextIndex();
		}

		/**
		 * @see java.util.ListIterator#previous()
		 */
		@Override
		public I previous()
		{
			Identity<I> und = iter.previous();
			return und == null ? null : und.getUnderlying();
		}

		/**
		 * @see java.util.ListIterator#previousIndex()
		 */
		@Override
		public int previousIndex()
		{
			return iter.previousIndex();
		}

		/**
		 * @see java.util.ListIterator#remove()
		 */
		@Override
		public void remove()
		{
			iter.remove();
		}

		/**
		 * @see java.util.ListIterator#set(java.lang.Object)
		 */
		@Override
		public void set(I item)
		{
			iter.set(getIdentity(item));
		}

	}
}
