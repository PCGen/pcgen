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
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * IdentityList is an implementation of the List Interface that uses Identity
 * (==) rather than equality (.equals() ) to establish behavior for remove. This
 * is useful to maintain an input ordered identity list (not possible with
 * IdentityHashMap because it does not maintain input order).
 * 
 * @param <T>
 *            The type of object stored in this IdentityList
 */
public class IdentityList<T> implements List<T>
{
	/**
	 * The underlying map providing storage of Identity structures
	 */
	List<Identity<T>> embeddedList = new LinkedList<Identity<T>>();

	/**
	 * Creates a new (empty) IdentityList
	 */
	public IdentityList()
	{
	}

	/**
	 * Createss a new IdentityList which will be initialized with the contents
	 * of the given List
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
	private <V> Identity<V> getIdentity(V o)
	{
		return new Identity<V>(o);
	}

	/**
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(int arg0, T arg1)
	{
		embeddedList.add(arg0, getIdentity(arg1));
	}

	/**
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean add(T arg0)
	{
		return embeddedList.add(getIdentity(arg0));
	}

	/**
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends T> arg0)
	{
		for (T t : arg0)
		{
			add(t);
		}
		return true;
	}

	/**
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int arg0, Collection<? extends T> arg1)
	{
		for (T t : arg1)
		{
			add(arg0++, t);
		}
		return true;
	}

	/**
	 * @see java.util.List#clear()
	 */
	public void clear()
	{
		embeddedList.clear();
	}

	/**
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(Object arg0)
	{
		return embeddedList.contains(getIdentity(arg0));
	}

	/**
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> arg0)
	{
		for (Object o : arg0)
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
	public boolean equals(Object arg0)
	{
		return arg0 instanceof IdentityList
				&& embeddedList.equals(((IdentityList<?>) arg0).embeddedList);
	}

	/**
	 * @see java.util.List#get(int)
	 */
	public T get(int arg0)
	{
		Identity<T> und = embeddedList.get(arg0);
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
	public int indexOf(Object arg0)
	{
		return embeddedList.indexOf(arg0);
	}

	/**
	 * @see java.util.List#isEmpty()
	 */
	public boolean isEmpty()
	{
		return embeddedList.isEmpty();
	}

	/**
	 * @see java.util.List#iterator()
	 */
	public Iterator<T> iterator()
	{
		return new IdentityIterator<T>(embeddedList.listIterator());
	}

	/**
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object arg0)
	{
		return embeddedList.lastIndexOf(getIdentity(arg0));
	}

	/**
	 * @see java.util.List#listIterator()
	 */
	public ListIterator<T> listIterator()
	{
		return new IdentityIterator<T>(embeddedList.listIterator());
	}

	/**
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator<T> listIterator(int arg0)
	{
		return new IdentityIterator<T>(embeddedList.listIterator(arg0));
	}

	/**
	 * @see java.util.List#remove(int)
	 */
	public T remove(int arg0)
	{
		Identity<T> und = embeddedList.remove(arg0);
		return und == null ? null : und.getUnderlying();
	}

	/**
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean remove(Object arg0)
	{
		return embeddedList.remove(getIdentity(arg0));
	}

	/**
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> arg0)
	{
		boolean result = true;
		for (Object o : arg0)
		{
			result &= remove(o);
		}
		return result;
	}

	/**
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> arg0)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public T set(int arg0, T arg1)
	{
		Identity<T> und = embeddedList.set(arg0, getIdentity(arg1));
		return und == null ? null : und.getUnderlying();
	}

	/**
	 * @see java.util.List#size()
	 */
	public int size()
	{
		return embeddedList.size();
	}

	/**
	 * @see java.util.List#subList(int, int)
	 */
	public List<T> subList(int arg0, int arg1)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @see java.util.List#toArray()
	 */
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
	 * @see java.util.List#toArray(T[])
	 */
	public <V> V[] toArray(V[] arg0)
	{
		Object[] array = embeddedList.toArray();
		int size = embeddedList.size();
		// Protect for small array
		if (arg0.length < size)
		{
			arg0 = (V[]) java.lang.reflect.Array.newInstance(arg0.getClass()
					.getComponentType(), size);
		}
		putIntoArray(array, arg0);
		return arg0;
	}

	private static final class Identity<T>
	{

		private final T underlying;

		public Identity(T o)
		{
			underlying = o;
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

	public class IdentityIterator<I> implements ListIterator<I>
	{
		private final ListIterator<Identity<I>> iter;

		public IdentityIterator(ListIterator<Identity<I>> iterator)
		{
			iter = iterator;
		}

		/**
		 * @see java.util.ListIterator#add(java.lang.Object)
		 */
		public void add(I o)
		{
			iter.add(getIdentity(o));
		}

		/**
		 * @see java.util.ListIterator#hasNext()
		 */
		public boolean hasNext()
		{
			return iter.hasNext();
		}

		/**
		 * @see java.util.ListIterator#hasPrevious()
		 */
		public boolean hasPrevious()
		{
			return iter.hasPrevious();
		}

		/**
		 * @see java.util.ListIterator#next()
		 */
		public I next()
		{
			Identity<I> und = iter.next();
			return und == null ? null : und.getUnderlying();
		}

		/**
		 * @see java.util.ListIterator#nextIndex()
		 */
		public int nextIndex()
		{
			return iter.nextIndex();
		}

		/**
		 * @see java.util.ListIterator#previous()
		 */
		public I previous()
		{
			Identity<I> und = iter.previous();
			return und == null ? null : und.getUnderlying();
		}

		/**
		 * @see java.util.ListIterator#previousIndex()
		 */
		public int previousIndex()
		{
			return iter.previousIndex();
		}

		/**
		 * @see java.util.ListIterator#remove()
		 */
		public void remove()
		{
			iter.remove();
		}

		/**
		 * @see java.util.ListIterator#set(java.lang.Object)
		 */
		public void set(I o)
		{
			iter.set(getIdentity(o));
		}

	}
}
