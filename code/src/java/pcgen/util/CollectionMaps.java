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
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CollectionMaps
{

    private CollectionMaps()
    {
    }

    private static <T> T createInstance(Class<T> c)
    {
        try
        {
            return c.newInstance();
        } catch (Exception ex)
        {
            Logger.getLogger(c.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static <K, V, C extends List<V>> ListMap<K, V, C> createListMap(Class<? extends Map> mapClass,
            Class<? extends List> listClass)
    {
        try
        {
            return new BasicListMap(mapClass, listClass);
        } catch (Exception ex)
        {
            Logger.getLogger(CollectionMaps.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static final class BasicListMap<K, V, C extends List<V>> extends AbstractListMap<K, V, C>
    {

        private final Map<K, C> map;
        private final Class<C> collectionClass;

        @SuppressWarnings("unchecked")
        public BasicListMap(Class<? extends Map> mapClass, Class<C> collectionClass)
                throws InstantiationException, IllegalAccessException
        {
            this.map = mapClass.newInstance();
            this.collectionClass = collectionClass;
        }

        @Override
        public void add(K key, int index, V value)
        {
            C list = get(key);
            if (list == null)
            {
                list = createInstance(collectionClass);
                list.add(index, value);
                put(key, list);
            } else
            {
                list.add(index, value);
            }
        }

        @Override
        public boolean addAll(K key, Collection<? extends V> values)
        {
            C collection = get(key);
            if (collection == null)
            {
                collection = createInstance(collectionClass);
                if (collection.addAll(values))
                {
                    put(key, collection);
                    return true;
                }
                return false;
            }
            return collection.addAll(values);
        }

        @Override
        public C get(Object key)
        {
            return map.get(key);
        }

        @Override
        public C put(K key, C value)
        {
            if (value == null)
            {
                return remove(key);
            }
            return map.put(key, value);
        }

        @Override
        public Set<Entry<K, C>> entrySet()
        {
            return map.entrySet();
        }

        @Override
        public Set<K> keySet()
        {
            return map.keySet();
        }

        @Override
        public boolean containsKey(Object key)
        {
            return map.containsKey(key);
        }

        @Override
        public void clear()
        {
            map.clear();
        }

        @Override
        public int size()
        {
            return map.size();
        }

        @Override
        public C remove(Object key)
        {
            return map.remove(key);
        }

        @Override
        public V remove(Object key, int index)
        {
            List<V> list = get(key);
            if (list != null)
            {
                V value = list.remove(index);
                if (list.isEmpty())
                {
                    remove(key);
                }
                return value;
            }

            return null;
        }

    }

    private static final class BasicCollectionMap<K, V, C extends Collection<V>> extends AbstractCollectionMap<K, V, C>
    {

        private final Map<K, C> map;
        private final Class<C> collectionClass;

        @SuppressWarnings("unchecked")
        public BasicCollectionMap(Class<? extends Map> mapClass, Class<C> collectionClass)
                throws InstantiationException, IllegalAccessException
        {
            this.map = mapClass.newInstance();
            this.collectionClass = collectionClass;
        }

        @Override
        public boolean add(K key, V value)
        {
            C collection = get(key);
            if (collection == null)
            {
                collection = createInstance(collectionClass);
                if (collection.add(value))
                {
                    put(key, collection);
                    return true;
                }
                return false;
            }
            return collection.add(value);
        }

        @Override
        public boolean addAll(K key, Collection<? extends V> values)
        {
            C collection = get(key);
            if (collection == null)
            {
                collection = createInstance(collectionClass);
                if (collection.addAll(values))
                {
                    put(key, collection);
                    return true;
                }
                return false;
            }
            return collection.addAll(values);
        }

        @Override
        public C get(Object key)
        {
            return map.get(key);
        }

        @Override
        public C put(K key, C value)
        {
            if (value == null)
            {
                return remove(key);
            }
            return map.put(key, value);
        }

        @Override
        public Set<Entry<K, C>> entrySet()
        {
            return map.entrySet();
        }

        @Override
        public Set<K> keySet()
        {
            return map.keySet();
        }

        @Override
        public boolean containsKey(Object key)
        {
            return map.containsKey(key);
        }

        @Override
        public void clear()
        {
            map.clear();
        }

        @Override
        public int size()
        {
            return map.size();
        }

        @Override
        public C remove(Object key)
        {
            return map.remove(key);
        }

        @Override
        public boolean remove(Object key, Object value)
        {
            C collection = get(key);
            if (collection != null && collection.remove(value))
            {
                if (collection.isEmpty())
                {
                    remove(key);
                }
                return true;
            }
            return false;
        }

    }
}
