/*
 * Copyright 2017 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * WriteOnceReadManyList is a List designed to be written to for logging or other
 * functions that cannot be "erased" from the list if provided to other objects.
 * 
 * Note: As a corollary to Write-once, read-many behavior, sorting of the list is
 * prohibited, as that is considered a write.
 *
 * @param <T>
 *            The type of object stored in this WriteOnceReadManyList
 */
@SuppressWarnings("PMD.TooManyMethods")
public class WriteOnceReadManyList<T> implements List<T>
{
	/**
	 * The underlying list for this WriteOnceReadManyList.
	 */
	private final List<T> underlying;

	/**
	 * A read-only version of the underlying list.
	 */
	private final List<T> readonly;

	/**
	 * Constructs a new WriteOnceReadManyList.
	 * 
	 * The assumption here is that the list provided to this WriteOnceReadManyList is not
	 * retained by the caller. Obviously, that original list is writeable, and thus the
	 * list could be modified. Any behavior of WriteOnceReadManyList is not guaranteed
	 * unless ownership of the provided List is fully provided to this
	 * WriteOnceReadManyList.
	 * 
	 * @param list
	 *            The underlying List for this WriteOnceReadManyList
	 */
	public WriteOnceReadManyList(List<T> list)
	{
		underlying = Objects.requireNonNull(list);
		readonly = Collections.unmodifiableList(underlying);
	}

	@Override
	public void forEach(Consumer<? super T> action)
	{
		readonly.forEach(action);
	}

	@Override
	public int size()
	{
		return underlying.size();
	}

	@Override
	public boolean isEmpty()
	{
		return underlying.isEmpty();
	}

	@Override
	public boolean contains(Object o)
	{
		return underlying.contains(o);
	}

	@Override
	public Iterator<T> iterator()
	{
		return readonly.iterator();
	}

	@Override
	public Object[] toArray()
	{
		return underlying.toArray();
	}

	@Override
	public <AT> AT[] toArray(AT[] a)
	{
		return underlying.toArray(a);
	}

	@Override
	public boolean add(T e)
	{
		return underlying.add(e);
	}

	@Override
	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException("remove");
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		return underlying.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c)
	{
		return underlying.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c)
	{
		return underlying.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("removeAll");
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("retainAll");
	}

	@Override
	public void replaceAll(UnaryOperator<T> operator)
	{
		throw new UnsupportedOperationException("replaceAll");
	}

	@Override
	public boolean removeIf(Predicate<? super T> filter)
	{
		throw new UnsupportedOperationException("removeIf");
	}

	@Override
	public void sort(Comparator<? super T> c)
	{
		//Counted as a write - be defensive
		throw new UnsupportedOperationException("sort");
	}

	@Override
	public void clear()
	{
		throw new UnsupportedOperationException("clear");
	}

	@Override
	public T get(int index)
	{
		return underlying.get(index);
	}

	@Override
	public T set(int index, T element)
	{
		throw new UnsupportedOperationException("set");
	}

	@Override
	public void add(int index, T element)
	{
		underlying.add(index, element);
	}

	@Override
	public Stream<T> stream()
	{
		return underlying.stream();
	}

	@Override
	public T remove(int index)
	{
		throw new UnsupportedOperationException("remove");
	}

	@Override
	public Stream<T> parallelStream()
	{
		return readonly.parallelStream();
	}

	@Override
	public int indexOf(Object o)
	{
		return underlying.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o)
	{
		return underlying.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator()
	{
		return readonly.listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int index)
	{
		return readonly.listIterator(index);
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex)
	{
		return readonly.subList(fromIndex, toIndex);
	}

	@Override
	public Spliterator<T> spliterator()
	{
		return readonly.spliterator();
	}

	@Override
	public boolean equals(Object o)
	{
		return underlying.equals(o);
	}

	@Override
	public int hashCode()
	{
		return underlying.hashCode();
	}

}
