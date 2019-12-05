/*
 * Copyright (c) Thomas Parker, 2013.
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
package pcgen.cdom.facet.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import pcgen.base.util.ArrayUtilities;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.event.SubScopeFacetChangeEvent;
import pcgen.cdom.facet.event.SubScopeFacetChangeListener;

public class AbstractSubScopeFacet<S1, S2, T> extends AbstractStorageFacet<CharID>
{
    private Map<S1, Map<S2, Map<T, Set<Object>>>> getConstructingInfo(CharID id)
    {
        Map<S1, Map<S2, Map<T, Set<Object>>>> map = getInfo(id);
        if (map == null)
        {
            map = new IdentityHashMap<>();
            setCache(id, map);
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private Map<S1, Map<S2, Map<T, Set<Object>>>> getInfo(CharID id)
    {
        return (Map<S1, Map<S2, Map<T, Set<Object>>>>) getCache(id);
    }

    public void add(CharID id, S1 scope1, S2 scope2, T obj, Object source)
    {
        Objects.requireNonNull(scope1, "Scope 1 cannot be null");
        Objects.requireNonNull(scope2, "Scope 2 cannot be null");
        Objects.requireNonNull(obj, "Object cannot be null");
        Map<S1, Map<S2, Map<T, Set<Object>>>> map = getConstructingInfo(id);
        Map<S2, Map<T, Set<Object>>> scope1Map = map.computeIfAbsent(scope1, k -> new IdentityHashMap<>());
        Map<T, Set<Object>> scope2Map = scope1Map.computeIfAbsent(scope2, k -> new IdentityHashMap<>());
        Set<Object> sources = scope2Map.get(obj);
        boolean isNew = (sources == null);
        if (isNew)
        {
            sources = Collections.newSetFromMap(new IdentityHashMap<>());
            scope2Map.put(obj, sources);
        }
        sources.add(source);
        if (isNew)
        {
            fireSubScopeFacetChangeEvent(id, scope1, scope2, obj, SubScopeFacetChangeEvent.DATA_ADDED);
        }
    }

    public void remove(CharID id, S1 scope1, S2 scope2, T obj, Object source)
    {
        Objects.requireNonNull(scope1, "Scope 1 cannot be null");
        Objects.requireNonNull(scope2, "Scope 2 cannot be null");
        Objects.requireNonNull(obj, "Object cannot be null");
        Map<S1, Map<S2, Map<T, Set<Object>>>> map = getInfo(id);
        if (map == null)
        {
            return;
        }
        Map<S2, Map<T, Set<Object>>> scope1Map = map.get(scope1);
        if (scope1Map == null)
        {
            return;
        }
        Map<T, Set<Object>> scope2Map = scope1Map.get(scope2);
        if (scope2Map == null)
        {
            return;
        }
        Set<Object> sources = scope2Map.get(obj);
        if (sources == null)
        {
            return;
        }
        if (sources.remove(source) && sources.isEmpty())
        {
            fireSubScopeFacetChangeEvent(id, scope1, scope2, obj, SubScopeFacetChangeEvent.DATA_REMOVED);
            scope2Map.remove(obj);
        }
        if (scope2Map.isEmpty())
        {
            scope1Map.remove(scope2);
        }
        if (scope1Map.isEmpty())
        {
            map.remove(scope1);
        }
        if (map.isEmpty())
        {
            removeCache(id);
        }
    }

    public Collection<T> getSet(CharID id, S1 scope1, S2 scope2)
    {
        Objects.requireNonNull(scope1, "Scope 1 cannot be null");
        Objects.requireNonNull(scope2, "Scope 2 cannot be null");
        Map<S1, Map<S2, Map<T, Set<Object>>>> map = getInfo(id);
        if (map == null)
        {
            return Collections.emptyList();
        }
        Map<S2, Map<T, Set<Object>>> scope1Map = map.get(scope1);
        if (scope1Map == null)
        {
            return Collections.emptyList();
        }
        Map<T, Set<Object>> scope2Map = scope1Map.get(scope2);
        if (scope2Map == null)
        {
            return Collections.emptyList();
        }
        return new ArrayList<>(scope2Map.keySet());
    }

    public int getSize(CharID id, S1 scope1, S2 scope2)
    {
        Objects.requireNonNull(scope1, "Scope 1 cannot be null");
        Objects.requireNonNull(scope2, "Scope 2 cannot be null");
        Map<S1, Map<S2, Map<T, Set<Object>>>> map = getInfo(id);
        if (map == null)
        {
            return 0;
        }
        Map<S2, Map<T, Set<Object>>> scope1Map = map.get(scope1);
        if (scope1Map == null)
        {
            return 0;
        }
        Map<T, Set<Object>> scope2Map = scope1Map.get(scope2);
        if (scope2Map == null)
        {
            return 0;
        }
        return scope2Map.size();
    }

    public boolean contains(CharID id, S1 scope1, S2 scope2, T obj)
    {
        Objects.requireNonNull(scope1, "Scope 1 cannot be null");
        Objects.requireNonNull(scope2, "Scope 2 cannot be null");
        Map<S1, Map<S2, Map<T, Set<Object>>>> map = getInfo(id);
        if (map == null)
        {
            return false;
        }
        Map<S2, Map<T, Set<Object>>> scope1Map = map.get(scope1);
        if (scope1Map == null)
        {
            return false;
        }
        Map<T, Set<Object>> scope2Map = scope1Map.get(scope2);
        return (scope2Map != null) && scope2Map.containsKey(obj);
    }

    public Collection<S1> getScopes1(CharID id)
    {
        Map<S1, Map<S2, Map<T, Set<Object>>>> map = getInfo(id);
        if (map == null)
        {
            return Collections.emptyList();
        }
        return new ArrayList<>(map.keySet());
    }

    public Collection<S2> getScopes2(CharID id, S1 scope1)
    {
        Map<S1, Map<S2, Map<T, Set<Object>>>> map = getInfo(id);
        if (map == null)
        {
            return Collections.emptyList();
        }
        Map<S2, Map<T, Set<Object>>> submap = map.get(scope1);
        if (submap == null)
        {
            return Collections.emptyList();
        }
        return new ArrayList<>(submap.keySet());
    }

    public void removeAllFromSource(CharID id, Object source)
    {
        Map<S1, Map<S2, Map<T, Set<Object>>>> map = getInfo(id);
        if (map != null)
        {
            for (Iterator<Entry<S1, Map<S2, Map<T, Set<Object>>>>> s1it = map.entrySet().iterator();s1it.hasNext();)
            {
                Entry<S1, Map<S2, Map<T, Set<Object>>>> s1entry = s1it.next();
                S1 scope1 = s1entry.getKey();
                Map<S2, Map<T, Set<Object>>> scope1Map = s1entry.getValue();
                for (Iterator<Entry<S2, Map<T, Set<Object>>>> s2it = scope1Map.entrySet().iterator();s2it.hasNext();)
                {
                    Entry<S2, Map<T, Set<Object>>> s2entry = s2it.next();
                    S2 scope2 = s2entry.getKey();
                    Map<T, Set<Object>> scope2Map = s2entry.getValue();
                    for (Iterator<Map.Entry<T, Set<Object>>> lmit = scope2Map.entrySet().iterator();lmit.hasNext();)
                    {
                        Entry<T, Set<Object>> lme = lmit.next();
                        Set<Object> sources = lme.getValue();
                        if (sources.remove(source) && sources.isEmpty())
                        {
                            T obj = lme.getKey();
                            lmit.remove();
                            fireSubScopeFacetChangeEvent(id, scope1, scope2, obj,
                                    SubScopeFacetChangeEvent.DATA_REMOVED);
                        }
                    }
                    if (scope2Map.isEmpty())
                    {
                        s2it.remove();
                    }
                }
                if (scope1Map.isEmpty())
                {
                    s1it.remove();
                }
            }
            if (map.isEmpty())
            {
                removeCache(id);
            }
        }
    }

    /**
     * Copies the contents of the AbstractScopeFacet from one Player Character
     * to another Player Character, based on the given CharIDs representing
     * those Player Characters.
     * <p>
     * This is a method in AbstractScopeFacet in order to avoid exposing the
     * mutable Map object to other classes. This should not be inlined, as the
     * Map is internal information to AbstractScopeFacet and should not be
     * exposed to other classes.
     * <p>
     * Note also the copy is a one-time event and no references are maintained
     * between the Player Characters represented by the given CharIDs (meaning
     * once this copy takes place, any change to the AbstractScopeFacet of one
     * Player Character will only impact the Player Character where the
     * AbstractScopeFacet was changed).
     *
     * @param source The CharID representing the Player Character from which the
     *               information should be copied
     * @param copy   The CharID representing the Player Character to which the
     *               information should be copied
     */
    @Override
    public void copyContents(CharID source, CharID copy)
    {
        Map<S1, Map<S2, Map<T, Set<Object>>>> map = getInfo(source);
        if (map != null)
        {
            for (Entry<S1, Map<S2, Map<T, Set<Object>>>> l1me : map.entrySet())
            {
                S1 scope1 = l1me.getKey();
                for (Entry<S2, Map<T, Set<Object>>> l2me : l1me.getValue().entrySet())
                {
                    S2 scope2 = l2me.getKey();
                    for (Entry<T, Set<Object>> ome : l2me.getValue().entrySet())
                    {
                        T sp = ome.getKey();
                        for (Object spsource : ome.getValue())
                        {
                            add(copy, scope1, scope2, sp, spsource);
                        }
                    }
                }
            }
        }
    }

    private final Map<Integer, SubScopeFacetChangeListener<? super S1, ? super S2, ? super T>[]> listeners =
            new TreeMap<>();

    /**
     * Adds a new ScopeFacetChangeListener to receive TwoScopeFacetChangeEvents
     * (EdgeChangeEvent and NodeChangeEvent) from this AbstractScopeFacet. The
     * given ScopeFacetChangeListener is added at the default priority (zero).
     * <p>
     * Note that the ScopeFacetChangeListeners are a list, meaning a given
     * ScopeFacetChangeListener can be added more than once at a given priority,
     * and if that occurs, it must be removed an equivalent number of times in
     * order to no longer receive events from this AbstractScopeFacet.
     *
     * @param listener The ScopeFacetChangeListener to receive
     *                 TwoScopeFacetChangeEvents from this AbstractScopeFacet
     */
    public void addSubScopeFacetChangeListener(SubScopeFacetChangeListener<? super S1, ? super S2, ? super T> listener)
    {
        addSubScopeFacetChangeListener(0, listener);
    }

    /**
     * Adds a new ScopeFacetChangeListener to receive TwoScopeFacetChangeEvents
     * (EdgeChangeEvent and NodeChangeEvent) from this AbstractScopeFacet.
     * <p>
     * The ScopeFacetChangeListener is added at the given priority.
     * <p>
     * Note that the ScopeFacetChangeListeners are a list, meaning a given
     * ScopeFacetChangeListener can be added more than once at a given priority,
     * and if that occurs, it must be removed an equivalent number of times in
     * order to no longer receive events from this AbstractScopeFacet.
     *
     * @param listener The ScopeFacetChangeListener to receive
     *                 TwoScopeFacetChangeEvents from this AbstractScopeFacet
     */
    @SuppressWarnings("unchecked")
    public void addSubScopeFacetChangeListener(int priority,
            SubScopeFacetChangeListener<? super S1, ? super S2, ? super T> listener)
    {
        SubScopeFacetChangeListener<? super S1, ? super S2, ? super T>[] dfcl =
                listeners.get(priority);
        dfcl = Optional.ofNullable(dfcl).orElse(new SubScopeFacetChangeListener[0]);
        listeners.put(priority, ArrayUtilities.prependOnCopy(listener, dfcl,
                SubScopeFacetChangeListener.class));
    }

    /**
     * Sends a NodeChangeEvent to the ScopeFacetChangeListeners that are
     * receiving TwoScopeFacetChangeEvents from this AbstractScopeFacet.
     *
     * @param id     The CharID identifying the Player Character to which the
     *               NodeChangeEvent relates.
     * @param scope1 A Scope through which this facet's contents are viewed.
     * @param scope2 Another Scope passed on to the listener.
     * @param node   The Node that has been added to or removed from this
     *               AbstractScopeFacet for the given CharID.
     * @param type   An identifier indicating whether the given CDOMObject was
     *               added to or removed from this AbstractScopeFacet.
     */
    @SuppressWarnings("rawtypes")
    protected void fireSubScopeFacetChangeEvent(CharID id, S1 scope1, S2 scope2, T node, int type)
    {
        for (SubScopeFacetChangeListener<? super S1, ? super S2, ? super T>[] dfclArray : listeners.values())
        {
            /*
             * This list is decremented from the end of the list to the
             * beginning in order to maintain consistent operation with how Java
             * AWT and Swing listeners are notified of Events. This is obviously
             * subordinate to the priority (loop above).
             */
            SubScopeFacetChangeEvent<S1, S2, T> ccEvent = null;
            for (int i = dfclArray.length - 1;i >= 0;i--)
            {
                // Lazily create event
                if (ccEvent == null)
                {
                    ccEvent = new SubScopeFacetChangeEvent<>(id, scope1, scope2, node, this, type);
                }
                SubScopeFacetChangeListener dfcl = dfclArray[i];
                switch (ccEvent.getEventType())
                {
                    case SubScopeFacetChangeEvent.DATA_ADDED:
                        dfcl.dataAdded(ccEvent);
                        break;
                    case SubScopeFacetChangeEvent.DATA_REMOVED:
                        dfcl.dataRemoved(ccEvent);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public boolean containsFor(CharID id, S1 scope1)
    {
        Map<S1, Map<S2, Map<T, Set<Object>>>> map = getInfo(id);
        return (map != null) && map.containsKey(scope1);
    }
}
