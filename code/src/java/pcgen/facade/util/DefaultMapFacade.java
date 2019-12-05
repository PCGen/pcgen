/*
 * Copyright 2012 (C) Connor Petty <cpmeister@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.facade.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DefaultMapFacade<K, V> extends AbstractMapFacade<K, V>
{

    private final Map<K, V> map;

    public DefaultMapFacade()
    {
        this.map = new HashMap<>();
    }

    public DefaultMapFacade(Map<? extends K, ? extends V> map)
    {
        this.map = new HashMap<>(map);
    }

    @Override
    public Set<K> getKeys()
    {
        return Collections.unmodifiableSet(map.keySet());
    }

    @Override
    public V getValue(K key)
    {
        return map.get(key);
    }

    public void putValue(K key, V value)
    {
        boolean hasKey = map.containsKey(key);
        V oldValue = map.put(key, value);
        if (hasKey)
        {
            fireValueChanged(this, key, oldValue, value);
        } else
        {
            fireKeyAdded(this, key, value);
        }
    }

    public void removeKey(K key)
    {
        if (map.containsKey(key))
        {
            V value = map.remove(key);
            fireKeyRemoved(this, key, value);
        }
    }

    public void setContents(Map<? extends K, ? extends V> newMap)
    {
        map.clear();
        map.putAll(newMap);
        fireKeysChanged(this);
    }

    public void clear()
    {
        map.clear();
        fireKeysChanged(this);
    }

    @Override
    public String toString()
    {
        return "DefaultMapFacade [map=" + map + "]"; //$NON-NLS-1$
    }

}
