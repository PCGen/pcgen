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
 */

package pcgen.util;

import java.util.Collection;
import java.util.List;

public interface ListMap<K, V, C extends List<V>> extends CollectionMap<K, V, C>
{
    void add(K key, int index, V value);

    void addAll(K key, int index, Collection<? extends V> values);

    V get(Object key, int index);

    V set(Object key, int index, V value);

    int indexOf(Object key, Object value);

    int lastIndexOf(Object key, Object value);

    V remove(Object key, int index);
}
