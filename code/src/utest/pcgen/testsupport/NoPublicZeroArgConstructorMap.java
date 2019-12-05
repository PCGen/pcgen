/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.testsupport;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"ALL", "PMD.MissingStaticMethodInNonInstantiatableClass"})
public final class NoPublicZeroArgConstructorMap<K, V> implements Map<K, V>
{

    private NoPublicZeroArgConstructorMap()
    {
        // Just need to avoid a public zero argument constructor
    }

    @Override
    public void clear()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(Object arg0)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsValue(Object arg0)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public V get(Object arg0)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<K> keySet()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public V put(K arg0, V arg1)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> arg0)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(Object arg0)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<V> values()
    {
        throw new UnsupportedOperationException();
    }

}
