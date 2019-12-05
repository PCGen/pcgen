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

import javax.swing.event.EventListenerList;

import pcgen.facade.util.event.MapEvent;
import pcgen.facade.util.event.MapListener;

public abstract class AbstractMapFacade<K, V> implements MapFacade<K, V>
{

    private final EventListenerList listenerList = new EventListenerList();

    @Override
    public void addMapListener(MapListener<? super K, ? super V> listener)
    {
        listenerList.add(MapListener.class, listener);
    }

    @Override
    public void removeMapListener(MapListener<? super K, ? super V> listener)
    {
        listenerList.remove(MapListener.class, listener);
    }

    @Override
    public int getSize()
    {
        return getKeys().size();
    }

    @Override
    public boolean containsKey(K key)
    {
        return getKeys().contains(key);
    }

    @Override
    public boolean isEmpty()
    {
        return getKeys().isEmpty();
    }

    /**
     * {@code AbstractMapFacade} subclasses must call this method
     * <b>after</b> a new key-value pair is added to the model.
     *
     * @param source the
     *               {@code MapFacade} that changed, typically "this"
     * @param key    the new key
     * @param value  the value associated with the new key
     */
    protected void fireKeyAdded(Object source, K key, V value)
    {
        Object[] listeners = listenerList.getListenerList();
        MapEvent<K, V> e = null;
        for (int i = listeners.length - 2;i >= 0;i -= 2)
        {
            if (listeners[i] == MapListener.class)
            {
                if (e == null)
                {
                    e = new MapEvent<>(source, MapEvent.KEY_ADDED, key, null, value);
                }
                ((MapListener) listeners[i + 1]).keyAdded(e);
            }
        }
    }

    /**
     * {@code AbstractMapFacade} subclasses must call this method
     * <b>after</b> a key-value pair is removed from the model.
     *
     * @param source the
     *               {@code MapFacade} that changed, typically "this"
     * @param key    the removed key
     * @param value  the value associated with the key
     */
    protected void fireKeyRemoved(Object source, K key, V value)
    {
        Object[] listeners = listenerList.getListenerList();
        MapEvent<K, V> e = null;
        for (int i = listeners.length - 2;i >= 0;i -= 2)
        {
            if (listeners[i] == MapListener.class)
            {
                if (e == null)
                {
                    e = new MapEvent<>(source, MapEvent.KEY_REMOVED, key, value, null);
                }
                ((MapListener) listeners[i + 1]).keyRemoved(e);
            }
        }
    }

    /**
     * {@code AbstractMapFacade} subclasses must call this method
     * <b>after</b> a key has been modified.
     *
     * @param source the
     *               {@code MapFacade} that changed, typically "this"
     * @param key    the removed key
     * @param value  the value associated with the key
     */
    protected void fireKeyModified(Object source, K key, V value)
    {
        Object[] listeners = listenerList.getListenerList();
        MapEvent<K, V> e = null;
        for (int i = listeners.length - 2;i >= 0;i -= 2)
        {
            if (listeners[i] == MapListener.class)
            {
                if (e == null)
                {
                    e = new MapEvent<>(source, MapEvent.KEY_MODIFIED, key, value, value);
                }
                ((MapListener) listeners[i + 1]).keyModified(e);
            }
        }
    }

    /**
     * {@code AbstractMapFacade} subclasses must call this method
     * <b>after</b> a key is assigned a new value.
     *
     * @param source   the
     *                 {@code MapFacade} that changed, typically "this"
     * @param key      the removed key
     * @param oldValue the old value associated with the key
     * @param newValue the new value associated with the key
     */
    protected void fireValueChanged(Object source, K key, V oldValue, V newValue)
    {
        Object[] listeners = listenerList.getListenerList();
        MapEvent<K, V> e = null;
        for (int i = listeners.length - 2;i >= 0;i -= 2)
        {
            if (listeners[i] == MapListener.class)
            {
                if (e == null)
                {
                    e = new MapEvent<>(source, MapEvent.VALUE_CHANGED, key, oldValue, newValue);
                }
                ((MapListener) listeners[i + 1]).valueChanged(e);
            }
        }
    }

    /**
     * {@code AbstractMapFacade} subclasses must call this method
     * <b>after</b> a value has been modified.
     *
     * @param source the
     *               {@code MapFacade} that changed, typically "this"
     * @param key    the removed key
     * @param value  the value associated with the key
     */
    protected void fireValueModified(Object source, K key, V value)
    {
        Object[] listeners = listenerList.getListenerList();
        MapEvent<K, V> e = null;
        for (int i = listeners.length - 2;i >= 0;i -= 2)
        {
            if (listeners[i] == MapListener.class)
            {
                if (e == null)
                {
                    e = new MapEvent<>(source, MapEvent.VALUE_MODIFIED, key, value, value);
                }
                ((MapListener) listeners[i + 1]).valueModified(e);
            }
        }
    }

    /**
     * {@code AbstractMapFacade} subclasses must call this method
     * <b>after</b> the contents of the map have greatly changed.
     *
     * @param source the
     *               {@code MapFacade} that changed, typically "this"
     */
    protected void fireKeysChanged(Object source)
    {
        Object[] listeners = listenerList.getListenerList();
        MapEvent<K, V> e = null;
        for (int i = listeners.length - 2;i >= 0;i -= 2)
        {
            if (listeners[i] == MapListener.class)
            {
                if (e == null)
                {
                    e = new MapEvent<>(source);
                }
                ((MapListener) listeners[i + 1]).keysChanged(e);
            }
        }
    }

}
