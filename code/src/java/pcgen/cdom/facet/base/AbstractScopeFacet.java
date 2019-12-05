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
import pcgen.base.util.GenericMapToList;
import pcgen.base.util.MapToList;
import pcgen.cdom.base.PCGenIdentifier;
import pcgen.cdom.facet.event.ScopeFacetChangeEvent;
import pcgen.cdom.facet.event.ScopeFacetChangeListener;

public class AbstractScopeFacet<IDT extends PCGenIdentifier, S, T> extends AbstractStorageFacet<IDT>
{
    private Map<S, Map<T, Set<Object>>> getConstructingInfo(IDT id)
    {
        Map<S, Map<T, Set<Object>>> map = getInfo(id);
        if (map == null)
        {
            map = new IdentityHashMap<>();
            setCache(id, map);
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private Map<S, Map<T, Set<Object>>> getInfo(IDT id)
    {
        return (Map<S, Map<T, Set<Object>>>) getCache(id);
    }

    public void add(IDT id, S scope, T obj, Object source)
    {
        Objects.requireNonNull(scope, "Scope cannot be null");
        Objects.requireNonNull(obj, "Object cannot be null");
        Map<S, Map<T, Set<Object>>> map = getConstructingInfo(id);
        Map<T, Set<Object>> scopeMap = map.computeIfAbsent(scope, k -> new IdentityHashMap<>());
        Set<Object> sources = scopeMap.get(obj);
        boolean isNew = (sources == null);
        if (isNew)
        {
            sources = Collections.newSetFromMap(new IdentityHashMap<>());
            scopeMap.put(obj, sources);
        }
        sources.add(source);
        if (isNew)
        {
            fireScopeFacetChangeEvent(id, scope, obj, ScopeFacetChangeEvent.DATA_ADDED);
        }
    }

    public void addAll(IDT id, S scope, Collection<T> coll, Object source)
    {
        Objects.requireNonNull(scope, "Scope cannot be null");
        Objects.requireNonNull(coll, "Collection cannot be null");
        Map<S, Map<T, Set<Object>>> map = getConstructingInfo(id);
        Map<T, Set<Object>> scopeMap = map.computeIfAbsent(scope, k -> new IdentityHashMap<>());
        for (T obj : coll)
        {
            Set<Object> sources = scopeMap.get(obj);
            boolean isNew = (sources == null);
            if (isNew)
            {
                sources = Collections.newSetFromMap(new IdentityHashMap<>());
                scopeMap.put(obj, sources);
            }
            sources.add(source);
            if (isNew)
            {
                fireScopeFacetChangeEvent(id, scope, obj, ScopeFacetChangeEvent.DATA_ADDED);
            }
        }
    }

    public void remove(IDT id, S scope, T obj, Object source)
    {
        Objects.requireNonNull(scope, "Scope cannot be null");
        Objects.requireNonNull(obj, "Object cannot be null");
        Map<S, Map<T, Set<Object>>> map = getInfo(id);
        if (map == null)
        {
            return;
        }
        Map<T, Set<Object>> scopeMap = map.get(scope);
        if (scopeMap == null)
        {
            return;
        }
        Set<Object> sources = scopeMap.get(obj);
        if (sources == null)
        {
            return;
        }
        if (sources.remove(source) && sources.isEmpty())
        {
            fireScopeFacetChangeEvent(id, scope, obj, ScopeFacetChangeEvent.DATA_REMOVED);
            scopeMap.remove(obj);
        }
        if (scopeMap.isEmpty())
        {
            map.remove(scope);
        }
        if (map.isEmpty())
        {
            removeCache(id);
        }
    }

    public Collection<T> getSet(IDT id, S scope)
    {
        Map<S, Map<T, Set<Object>>> map = getInfo(id);
        if (map == null)
        {
            return Collections.emptyList();
        }
        Map<T, Set<Object>> scopeMap = map.get(scope);
        if (scopeMap == null)
        {
            return Collections.emptyList();
        }
        return new ArrayList<>(scopeMap.keySet());
    }

    public Collection<S> getScopes(IDT id)
    {
        Map<S, Map<T, Set<Object>>> map = getInfo(id);
        if (map == null)
        {
            return Collections.emptyList();
        }
        return new ArrayList<>(map.keySet());
    }

    public boolean contains(IDT id, S scope, T obj)
    {
        Map<S, Map<T, Set<Object>>> map = getInfo(id);
        if (map == null)
        {
            return false;
        }
        Map<T, Set<Object>> scopeMap = map.get(scope);
        return (scopeMap != null) && scopeMap.containsKey(obj);
    }

    public void removeAllFromSource(IDT id, Object source)
    {
        Map<S, Map<T, Set<Object>>> map = getInfo(id);
        /*
         * This list exists primarily to eliminate the possibility of a
         * concurrent modification exception on a recursive remove
         */
        MapToList<S, T> removed = GenericMapToList.getMapToList(IdentityHashMap.class);
        if (map != null)
        {
            for (Iterator<Map.Entry<S, Map<T, Set<Object>>>> it = map.entrySet().iterator();it.hasNext();)
            {
                Entry<S, Map<T, Set<Object>>> entry = it.next();
                S scope = entry.getKey();
                Map<T, Set<Object>> scopeMap = entry.getValue();
                for (Iterator<Map.Entry<T, Set<Object>>> lmit = scopeMap.entrySet().iterator();lmit.hasNext();)
                {
                    Entry<T, Set<Object>> lme = lmit.next();
                    Set<Object> sources = lme.getValue();
                    if (sources.remove(source) && sources.isEmpty())
                    {
                        T obj = lme.getKey();
                        lmit.remove();
                        removed.addToListFor(scope, obj);
                    }
                }
                if (scopeMap.isEmpty())
                {
                    it.remove();
                }
            }
            if (map.isEmpty())
            {
                removeCache(id);
            }
            for (S scope : removed.getKeySet())
            {
                for (T obj : removed.getListFor(scope))
                {
                    fireScopeFacetChangeEvent(id, scope, obj, ScopeFacetChangeEvent.DATA_REMOVED);
                }
            }
        }
    }

    /**
     * Copies the contents of the AbstractScopeFacet from one resource to
     * another resource, based on the given IDTs representing those resources.
     * <p>
     * This is a method in AbstractScopeFacet in order to avoid exposing the
     * mutable Map object to other classes. This should not be inlined, as the
     * Map is internal information to AbstractScopeFacet and should not be
     * exposed to other classes.
     * <p>
     * Note also the copy is a one-time event and no references are maintained
     * between the resources represented by the given IDTs (meaning once this
     * copy takes place, any change to the AbstractScopeFacet of one resource
     * will only impact the resource where the AbstractScopeFacet was changed).
     *
     * @param source The IDT representing the resource from which the information
     *               should be copied
     * @param copy   The IDT representing the resource to which the information
     *               should be copied
     */
    @Override
    public void copyContents(IDT source, IDT copy)
    {
        Map<S, Map<T, Set<Object>>> map = getInfo(source);
        if (map != null)
        {
            for (Entry<S, Map<T, Set<Object>>> lme : map.entrySet())
            {
                S scope = lme.getKey();
                for (Entry<T, Set<Object>> ome : lme.getValue().entrySet())
                {
                    T sp = ome.getKey();
                    for (Object spsource : ome.getValue())
                    {
                        add(copy, scope, sp, spsource);
                    }
                }
            }
        }
    }

    private final Map<Integer, ScopeFacetChangeListener<? super IDT, ? super S, ? super T>[]> listeners =
            new TreeMap<>();

    /**
     * Adds a new ScopeFacetChangeListener to receive ScopeFacetChangeEvents
     * (EdgeChangeEvent and NodeChangeEvent) from this AbstractScopeFacet. The
     * given ScopeFacetChangeListener is added at the default priority (zero).
     * <p>
     * Note that the ScopeFacetChangeListeners are a list, meaning a given
     * ScopeFacetChangeListener can be added more than once at a given priority,
     * and if that occurs, it must be removed an equivalent number of times in
     * order to no longer receive events from this AbstractScopeFacet.
     *
     * @param listener The ScopeFacetChangeListener to receive ScopeFacetChangeEvents
     *                 from this AbstractScopeFacet
     */
    public void addScopeFacetChangeListener(ScopeFacetChangeListener<? super IDT, ? super S, ? super T> listener)
    {
        addScopeFacetChangeListener(0, listener);
    }

    /**
     * Adds a new ScopeFacetChangeListener to receive ScopeFacetChangeEvents
     * (EdgeChangeEvent and NodeChangeEvent) from this AbstractScopeFacet.
     * <p>
     * The ScopeFacetChangeListener is added at the given priority.
     * <p>
     * Note that the ScopeFacetChangeListeners are a list, meaning a given
     * ScopeFacetChangeListener can be added more than once at a given priority,
     * and if that occurs, it must be removed an equivalent number of times in
     * order to no longer receive events from this AbstractScopeFacet.
     *
     * @param listener The ScopeFacetChangeListener to receive ScopeFacetChangeEvents
     *                 from this AbstractScopeFacet
     */
    @SuppressWarnings("unchecked")
    public void addScopeFacetChangeListener(int priority,
            ScopeFacetChangeListener<? super IDT, ? super S, ? super T> listener)
    {
        ScopeFacetChangeListener<? super IDT, ? super S, ? super T>[] dfcl =
                listeners.get(priority);
        dfcl = Optional.ofNullable(dfcl).orElse(new ScopeFacetChangeListener[0]);
        listeners.put(priority, ArrayUtilities.prependOnCopy(listener, dfcl,
                ScopeFacetChangeListener.class));
    }

    /**
     * Removes a ScopeFacetChangeListener so that it will no longer receive
     * ScopeFacetChangeEvents from this AbstractScopeFacet. This will remove the
     * data facet change listener from the default priority (zero).
     * <p>
     * Note that if the given ScopeFacetChangeListener has been registered under
     * a different priority, it will still receive events at that priority
     * level.
     *
     * @param listener The ScopeFacetChangeListener to be removed
     */
    public void removeScopeFacetChangeListener(ScopeFacetChangeListener<? super IDT, ? super S, ? super T> listener)
    {
        removeScopeFacetChangeListener(0, listener);
    }

    /**
     * Removes a ScopeFacetChangeListener so that it will no longer receive
     * ScopeFacetChangeEvents from the source DataFacet. This will remove the
     * data facet change listener from the given priority.
     * <p>
     * Note that if the given ScopeFacetChangeListener has been registered under
     * a different priority, it will still receive events at that priority
     * level.
     *
     * @param listener The ScopeFacetChangeListener to be removed
     */
    public void removeScopeFacetChangeListener(int priority,
            ScopeFacetChangeListener<? super IDT, ? super S, ? super T> listener)
    {
        ScopeFacetChangeListener<? super IDT, ? super S, ? super T>[] dfcl = listeners.get(priority);
        if (dfcl == null)
        {
            // No worries
            return;
        }
        int foundLoc = -1;
        int newSize = dfcl.length - 1;
        for (int i = newSize;i >= 0;i--)
        {
            if (dfcl[i] == listener)
            {
                foundLoc = i;
                break;
            }
        }
        if (foundLoc != -1)
        {
            if (dfcl.length == 1)
            {
                listeners.remove(priority);
            } else
            {
                ScopeFacetChangeListener<? super IDT, ? super S, ? super T>[] newArray =
                        new ScopeFacetChangeListener[newSize];
                if (foundLoc != 0)
                {
                    System.arraycopy(dfcl, 0, newArray, 0, foundLoc);
                }
                if (foundLoc != newSize)
                {
                    System.arraycopy(dfcl, foundLoc + 1, newArray, foundLoc, newSize - foundLoc);
                }
                listeners.put(priority, newArray);
            }
        }
    }

    /**
     * Sends a NodeChangeEvent to the ScopeFacetChangeListeners that are
     * receiving ScopeFacetChangeEvents from this AbstractScopeFacet.
     *
     * @param id    The PCGenIdentifier identifying the resource to which the
     *              NodeChangeEvent relates
     * @param scope The Scope through which this facet's contents are viewed
     * @param node  The Node that has been added to or removed from this
     *              AbstractScopeFacet for the given PCGenIdentifier
     * @param type  An identifier indicating whether the given CDOMObject was
     *              added to or removed from this AbstractScopeFacet
     */
    @SuppressWarnings("rawtypes")
    protected void fireScopeFacetChangeEvent(IDT id, S scope, T node, int type)
    {
        for (ScopeFacetChangeListener<? super IDT, ? super S, ? super T>[] dfclArray : listeners.values())
        {
            /*
             * This list is decremented from the end of the list to the
             * beginning in order to maintain consistent operation with how Java
             * AWT and Swing listeners are notified of Events. This is obviously
             * subordinate to the priority (loop above).
             */
            ScopeFacetChangeEvent<IDT, S, T> ccEvent = null;
            for (int i = dfclArray.length - 1;i >= 0;i--)
            {
                // Lazily create event
                if (ccEvent == null)
                {
                    ccEvent = new ScopeFacetChangeEvent<>(id, scope, node, this, type);
                }
                ScopeFacetChangeListener dfcl = dfclArray[i];
                switch (ccEvent.getEventType())
                {
                    case ScopeFacetChangeEvent.DATA_ADDED:
                        dfcl.dataAdded(ccEvent);
                        break;
                    case ScopeFacetChangeEvent.DATA_REMOVED:
                        dfcl.dataRemoved(ccEvent);
                        break;
                    default:
                        break;
                }
            }
        }
    }

}
