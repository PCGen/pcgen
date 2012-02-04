/*
 * AbstractCollectionMap.java
 * Copyright 2008 (C) Connor Petty <mistercpp2000@gmail.com>
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
 * Created on Feb 2, 2008, 10:07:00 PM
 */
package pcgen.util;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public abstract class AbstractCollectionMap<K, V, C extends Collection<V>>
	extends AbstractMap<K, C> implements CollectionMap<K, V, C>
{

    private volatile Collection<V> values = null;

    public boolean add(K key, V value)
    {
	throw new UnsupportedOperationException();
    }

    public boolean addAll(K key, Collection<? extends V> values)
    {
	C collection = get(key);
	return collection != null && collection.addAll(values);
    }

    @Override
    public boolean containsValue(Object value)
    {
	for (K key : keySet())
	{
	    if (get(key).contains(value))
	    {
		return true;
	    }
	}
	return false;
    }

    public boolean containsValue(Object key, Object value)
    {
	C collection = get(key);
	return collection != null && collection.contains(value);
    }

    public boolean remove(Object key, Object value)
    {
	throw new UnsupportedOperationException();
    }

    public boolean removeAll(Object key, Collection<?> c)
    {
	C collection = get(key);
	return collection != null && collection.removeAll(c);
    }

    public boolean retainAll(Object key, Collection<?> c)
    {
	C collection = get(key);
	return collection != null && collection.retainAll(c);
    }
    
    public int size(Object key)
    {
	C collection = get(key);
	if(collection != null)
	    return collection.size();
	return 0;
    }
    
    public Collection<V> getAll()
    {
	if (values == null)
	{
	    values = new AbstractCollection<V>()
	    {

		public Iterator<V> iterator()
		{
		    return new Iterator<V>()
		    {

			private Iterator<C> ci = values().iterator();
			private Iterator<V> vi = null;

			public boolean hasNext()
			{
			    return ci.hasNext() || vi != null && vi.hasNext();
			}

			public V next()
			{
			    if (vi == null || !vi.hasNext())
			    {
				vi = ci.next().iterator();
			    }
			    return vi.next();
			}

			public void remove()
			{
			    vi.remove();
			}

		    };
		}

		public int size()
		{
		    int size = 0;
		    for (C collection : values())
		    {
			size += collection.size();
		    }
		    return size;
		}

		@Override
		public boolean contains(Object v)
		{
		    return AbstractCollectionMap.this.containsValue(v);
		}

	    };
	}
	return values;
    }

}
