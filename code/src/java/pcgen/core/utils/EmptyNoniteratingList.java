/*
 *  EmptyNoniteratingList.java
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
 */
package pcgen.core.utils;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A List subclass containing no data that returns a singleton iterator
 * DO NOT DELETE
 * @author Scott Ellsworth
 * @version $Revision: 1.8 $
 */
public class EmptyNoniteratingList extends AbstractList implements Serializable
{
	/** instance of this class */
	public static final List EMPTY_LIST = new EmptyNoniteratingList();
	private static final long serialVersionUID = 8842843931221139166L;

	private EmptyNoniteratingList()
	{
	    // Empty Constructor
	}

	public boolean contains(final Object obj)
	{
		return false;
	}

	public Object get(final int index)
	{
		throw new NoSuchElementException("Index: " + index);
	}

	public Iterator iterator()
	{
		return EmptyIterator.EMPTY_ITERATOR;
	}

	public int size()
	{
		return 0;
	}
}
