/*
 * Copyright 2006 (C) Tom Parker <thpr@sourceforge.net>
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
 * Created on Oct 31, 2006
 *
 * Current Ver: $Revision: 1060 $
 * Last Editor: $Author: boomer70 $
 * Last Edited: $Date: 2006-06-08 23:25:16 -0400 (Thu, 08 Jun 2006) $
 */
package pcgen.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/*
 * 
 */
public class MapCollection implements Collection<Object>
{

	private final Map<?, ?> map;

	public MapCollection(Map<?, ?> m)
	{
		if (m == null)
		{
			throw new IllegalArgumentException(
				"Cannot provide null to MapCollection");
		}
		map = new HashMap<Object, Object>(m);
	}

	public boolean add(Object arg0)
	{
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection<?> arg0)
	{
		throw new UnsupportedOperationException();
	}

	public void clear()
	{
		throw new UnsupportedOperationException();
	}

	public boolean contains(Object arg0)
	{
		return map.containsKey(arg0) || map.containsValue(arg0);
	}

	public boolean containsAll(Collection<?> arg0)
	{
		for (Object obj : arg0)
		{
			if (!map.containsKey(obj) && !map.containsValue(obj))
			{
				return false;
			}
		}
		return true;
	}

	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	public Iterator<Object> iterator()
	{
		return new MapCollectionIterator(map);
	}

	public boolean remove(Object arg0)
	{
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection<?> arg0)
	{
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection<?> arg0)
	{
		throw new UnsupportedOperationException();
	}

	public int size()
	{
		return map.size();
	}

	public Object[] toArray()
	{
		// FIXME Auto-generated method stub
		return null;
	}

	public Object[] toArray(Object[] arg0)
	{
		// FIXME Auto-generated method stub
		return null;
	}

	private class MapCollectionIterator implements Iterator<Object>
	{
		Entry<?, ?> workingEntry;

		private boolean returnedKey = false;

		private final Iterator hashIterator;

		MapCollectionIterator(Map<?, ?> m)
		{
			hashIterator = m.entrySet().iterator();
		}

		public boolean hasNext()
		{
			return returnedKey || hashIterator.hasNext();
		}

		public void remove()
		{
			throw new UnsupportedOperationException();
		}

		public Object next()
		{
			if (returnedKey)
			{
				returnedKey = false;
				return workingEntry.getValue();
			}
			else
			{
				workingEntry = (Entry<?, ?>) hashIterator.next();
				returnedKey = true;
				return workingEntry.getKey();
			}
		}

	}

}
