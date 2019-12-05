/*
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
 */
package pcgen.util;

import java.util.Collection;
import java.util.List;

public abstract class AbstractListMap<K, V, L extends List<V>> extends AbstractCollectionMap<K, V, L>
        implements ListMap<K, V, L>
{

    @Override
    public boolean add(K key, V value)
    {
        add(key, size(key), value);
        return true;
    }

    @Override
    public void add(K key, int index, V value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(K key, Collection<? extends V> values)
    {
        addAll(key, size(key), values);
        return true;
    }

    @Override
    public void addAll(K key, int index, Collection<? extends V> values)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public V get(Object key, int index)
    {
        L list = get(key);
        if ((list != null) && (list.size() > index) && (index >= 0))
        {
            return list.get(index);
        }
        return null;
    }

    @Override
    public V set(Object key, int index, V value)
    {
        L list = get(key);
        if (list != null)
        {
            return list.set(index, value);
        }
        return null;
    }

    @Override
    public int indexOf(Object key, Object value)
    {
        L list = get(key);
        if (list != null)
        {
            return list.indexOf(value);
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object key, Object value)
    {
        L list = get(key);
        if (list != null)
        {
            return list.lastIndexOf(value);
        }
        return -1;
    }

    @Override
    public boolean remove(Object key, Object value)
    {
        L list = get(key);

        if (list == null)
        {
            return false;
        }

        int index = list.indexOf(value);
        if (index == -1)
        {
            return false;
        }
        remove(key, index);
        return true;
    }

    @Override
    public V remove(Object key, int index)
    {
        throw new UnsupportedOperationException();
    }

}
