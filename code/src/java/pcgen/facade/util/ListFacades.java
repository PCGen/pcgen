/*
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
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
 */
package pcgen.facade.util;

import java.util.AbstractList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import pcgen.facade.util.event.ListListener;

public final class ListFacades
{

	private ListFacades()
	{
		//Do not instantiate Utility Class
	}

	/**
	 * An immutable, empty {@link ListFacade}.
	 *
	 * @param <T> the element type
	 * @return an empty list facade
	 */
	public static <T> ListFacade<T> emptyList()
	{
		return new EmptyList<>();
	}

	public static <T> List<T> wrap(final ListFacade<T> list)
	{
		return new AbstractList<>()
		{

			@Override
			public T get(int index)
			{
				return list.getElementAt(index);
			}

			@Override
			public int size()
			{
				return list.getSize();
			}

		};
	}

	private static final class EmptyList<E> implements ListFacade<E>
	{

		@Override
		public void addListListener(ListListener<? super E> listener)
		{
			//This list never changes so there's no point in listening to it
		}

		@Override
		public E getElementAt(int index)
		{
			throw new IndexOutOfBoundsException(index);
		}

		@Override
		public int getSize()
		{
			return 0;
		}

		@Override
		public void removeListListener(ListListener<? super E> listener)
		{
			//This list never changes so there's no point in listening to it
		}

		@Override
		public Iterator<E> iterator()
		{
			return Collections.emptyIterator();
		}

		@Override
		public boolean isEmpty()
		{
			return true;
		}

		@Override
		public boolean containsElement(E element)
		{
			return false;
		}
	}
}
