/*
 *  EmptyIterator.java
 *  Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * @author Scott Ellsworth
 */
package pcgen.core.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator subclass for empty lists.
 * DO NOT DELETE
 * @author Scott Ellsworth
 * @version $Revision$
 */
public class EmptyIterator<T> implements Iterator<T>
{
	/** instance of this class */
	public static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();

	private EmptyIterator()
	{
		// Empty Constructor
	}

	public static <T> EmptyIterator<T> emptyIterator()
	{
		return (EmptyIterator<T>)EMPTY_ITERATOR;
	}

	public boolean hasNext()
	{
		return false;
	}

	public T next()
	{
		throw new NoSuchElementException();
	}

	public void remove()
	{
		throw new UnsupportedOperationException();
	}
}
