/*
 * Copyright (c) Thomas Parker, 2009.
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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import pcgen.base.util.ListSet;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.event.DataFacetChangeEvent;

/**
 * An AbstractSingleSourceListFacet is a DataFacet that contains information
 * about Objects that are contained in a PlayerCharacter when a PlayerCharacter
 * may have more than one of that type of Object (e.g. Language, PCTemplate) and
 * the source of that object should be tracked.
 * <p>
 * Using this class, an object may have only one source. If the object is
 * re-added with a second source, this will not trigger a DATA_ADDED event, but
 * the object considered the source of the item in the list of this
 * AbstractSingleSourceListFacet will be updated to the source provided in the
 * call to add.
 * <p>
 * null is NOT a valid source.
 */
public abstract class AbstractSingleSourceListFacet<T, ST> extends AbstractDataFacet<CharID, T>
{
    /**
     * Add the given object with the given source to the list of objects stored
     * in this AbstractSingleSourceListFacet for the Player Character
     * represented by the given CharID
     *
     * @param id     The CharID representing the Player Character for which the
     *               given item should be added
     * @param obj    The object to be added to the list of objects stored in this
     *               AbstractSingleSourceListFacet for the Player Character
     *               represented by the given CharID
     * @param source The source for the given object
     */
    public void add(CharID id, T obj, ST source)
    {
        Objects.requireNonNull(obj, "Object to add may not be null");
        Objects.requireNonNull(source, "Source may not be null");
        Map<T, ST> map = getConstructingCachedMap(id);
        Object oldsource = map.get(obj);
        boolean fireNew = (oldsource == null);
        map.put(obj, source);
        if (fireNew)
        {
            fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_ADDED);
        }
    }

    /**
     * Adds all of the objects with the given source in the given Collection to
     * the list of objects stored in this AbstractSingleSourceListFacet for the
     * Player Character represented by the given CharID
     *
     * @param id     The CharID representing the Player Character for which the
     *               given items should be added
     * @param c      The Collection of objects to be added to the list of objects
     *               stored in this AbstractSingleSourceListFacet for the Player
     *               Character represented by the given CharID
     * @param source The source for the given object
     * @throws NullPointerException if the given Collection is null
     */
    public void addAll(CharID id, Collection<? extends T> c, ST source)
    {
        for (T obj : c)
        {
            add(id, obj, source);
        }
    }

    /**
     * Removes the given source entry for the given object stored in this
     * AbstractSingleSourceListFacet for the Player Character represented by the
     * given CharID.
     * <p>
     * If the given source is the source of the object recognized by this
     * AbstractSingleSourceListFacet, then the object is removed from the list
     * of objects stored in this AbstractSingleSourceListFacet for the Player
     * Character represented by the given CharID.
     * <p>
     * If the given source is not the source recognized by this
     * AbstractSingleSourceListFacet, then this call to remove is ignored.
     *
     * @param id     The CharID representing the Player Character from which the
     *               given item source should be removed
     * @param obj    The object for which the source should be removed
     * @param source The source for the given object to be removed from the list of
     *               sources.
     */
    public void remove(CharID id, T obj, ST source)
    {
        Map<T, ST> componentMap = getCachedMap(id);
        if (componentMap != null)
        {
            processRemoval(id, componentMap, obj, source);
        }
    }

    /**
     * Removes the given source entry from the list of sources for all of the
     * objects in the given Collection for the Player Character represented by
     * the given CharID.
     * <p>
     * If the given source is the source (recognized by this
     * AbstractSingleSourceListFacet) of an object in the given collection, then
     * that object is removed from the list of objects stored in this
     * AbstractSingleSourceListFacet for the Player Character represented by the
     * given CharID.
     * <p>
     * If the given source is not the source recognized by this
     * AbstractSingleSourceListFacet of an object in the given collection, then
     * no change is made for that object to the list of objects stored in this
     * AbstractSingleSourceListFacet for the Player Character represented by the
     * given CharID.
     *
     * @param id     The CharID representing the Player Character from which the
     *               given items should be removed
     * @param c      The Collection of objects to be removed from the list of
     *               objects stored in this AbstractSingleSourceListFacet for the
     *               Player Character represented by the given CharID
     * @param source The source for the objects in the given Collection to be
     *               removed from the list of sources.
     * @throws NullPointerException if the given Collection is null
     */
    public void removeAll(CharID id, Collection<T> c, ST source)
    {
        Map<T, ST> componentMap = getCachedMap(id);
        if (componentMap != null)
        {
            for (T obj : c)
            {
                processRemoval(id, componentMap, obj, source);
            }
        }
    }

    /**
     * Removes all objects (and all sources for those objects) from the list of
     * objects stored in this AbstractSingleSourceListFacet for the Player
     * Character represented by the given CharID
     * <p>
     * This method is value-semantic in that ownership of the returned Map is
     * transferred to the class calling this method. Since this is a remove all
     * function, modification of the returned Map will not modify this
     * AbstractSingleSourceListFacet and modification of this
     * AbstractSingleSourceListFacet will not modify the returned Map.
     * Modifications to the returned Map will also not modify any future or
     * previous objects returned by this (or other) methods on
     * AbstractSingleSourceListFacet. If you wish to modify the information
     * stored in this AbstractSingleSourceListFacet, you must use the add*() and
     * remove*() methods of AbstractSingleSourceListFacet.
     *
     * @param id The CharID representing the Player Character from which all
     *           items should be removed
     * @return A non-null Set of objects removed from the list of objects stored
     * in this AbstractSingleSourceListFacet for the Player Character
     * represented by the given CharID
     */
    public Map<T, ST> removeAll(CharID id)
    {
        Map<T, ST> componentMap = getCachedMap(id);
        if (componentMap == null)
        {
            return Collections.emptyMap();
        }
        removeCache(id);
        for (T obj : componentMap.keySet())
        {
            fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_REMOVED);
        }
        return componentMap;
    }

    /**
     * Returns a non-null copy of the Set of objects in this
     * AbstractSingleSourceListFacet for the Player Character represented by the
     * given CharID. This method returns an empty set if no objects are in this
     * AbstractSingleSourceListFacet for the Player Character identified by the
     * given CharID.
     * <p>
     * This method is value-semantic in that ownership of the returned List is
     * transferred to the class calling this method. Modification of the
     * returned List will not modify this AbstractSingleSourceListFacet and
     * modification of this AbstractSingleSourceListFacet will not modify the
     * returned List. Modifications to the returned List will also not modify
     * any future or previous objects returned by this (or other) methods on
     * AbstractSingleSourceListFacet. If you wish to modify the information
     * stored in this AbstractSingleSourceListFacet, you must use the add*() and
     * remove*() methods of AbstractSingleSourceListFacet.
     *
     * @param id The CharID representing the Player Character for which the
     *           items in this AbstractSingleSourceListFacet should be
     *           returned.
     * @return A non-null Set of objects in this AbstractSingleSourceListFacet
     * for the Player Character represented by the given CharID
     */
    public Set<T> getSet(CharID id)
    {
        Map<T, ST> componentMap = getCachedMap(id);
        if (componentMap == null)
        {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(new ListSet<>(componentMap.keySet()));
    }

    /**
     * Returns the count of items in this AbstractSingleSourceListFacet for the
     * Player Character represented by the given CharID
     *
     * @param id The CharID representing the Player Character for which the
     *           count of items should be returned
     * @return The count of items in this AbstractSingleSourceListFacet for the
     * Player Character represented by the given CharID
     */
    public int getCount(CharID id)
    {
        Map<T, ST> componentMap = getCachedMap(id);
        if (componentMap == null)
        {
            return 0;
        }
        return componentMap.size();
    }

    /**
     * Returns true if this AbstractSingleSourceListFacet does not contain any
     * items for the Player Character represented by the given CharID
     *
     * @param id The CharId representing the PlayerCharacter to test if any
     *           items are contained by this AbstractSingleSourceListFacet
     * @return true if this AbstractSingleSourceListFacet does not contain any
     * items for the Player Character represented by the given CharID;
     * false otherwise (if it does contain items for the Player
     * Character)
     */
    public boolean isEmpty(CharID id)
    {
        Map<T, ST> componentMap = getCachedMap(id);
        return componentMap == null || componentMap.isEmpty();
    }

    /**
     * Returns true if this AbstractSingleSourceListFacet contains the given
     * value in the list of items for the Player Character represented by the
     * given CharID.
     *
     * @param id  The CharID representing the Player Character used for testing
     * @param obj The object to test if this AbstractSingleSourceListFacet
     *            contains that item for the Player Character represented by the
     *            given CharID
     * @return true if this AbstractSingleSourceListFacet contains the given
     * value for the Player Character represented by the given CharID;
     * false otherwise
     */
    public boolean contains(CharID id, T obj)
    {
        Map<T, ST> componentMap = getCachedMap(id);
        return componentMap != null && componentMap.containsKey(obj);
    }

    /**
     * Returns the type-safe Map for this AbstractSingleSourceListFacet and the
     * given CharID. May return null if no information has been set in this
     * AbstractSingleSourceListFacet for the given CharID.
     * <p>
     * Note that this method SHOULD NOT be public. The Map is owned by
     * AbstractSingleSourceListFacet, and since it can be modified, a reference
     * to that object should not be exposed to any object other than
     * AbstractSingleSourceListFacet.
     *
     * @param id The CharID for which the Set should be returned
     * @return The Set for the Player Character represented by the given CharID;
     * null if no information has been set in this
     * AbstractSingleSourceListFacet for the Player Character.
     */
    @SuppressWarnings("unchecked")
    protected Map<T, ST> getCachedMap(CharID id)
    {
        return (Map<T, ST>) getCache(id);
    }

    /**
     * Returns a type-safe Map for this AbstractSingleSourceListFacet and the
     * given CharID. Will return a new, empty Map if no information has been set
     * in this AbstractSingleSourceListFacet for the given CharID. Will not
     * return null.
     * <p>
     * Note that this method SHOULD NOT be public. The Map object is owned by
     * AbstractSingleSourceListFacet, and since it can be modified, a reference
     * to that object should not be exposed to any object other than
     * AbstractSingleSourceListFacet.
     *
     * @param id The CharID for which the Map should be returned
     * @return The Map for the Player Character represented by the given CharID.
     */
    private Map<T, ST> getConstructingCachedMap(CharID id)
    {
        Map<T, ST> componentMap = getCachedMap(id);
        if (componentMap == null)
        {
            componentMap = getComponentMap();
            setCache(id, componentMap);
        }
        return componentMap;
    }

    /**
     * Returns a new (empty) Map for this AbstractSingleSourceListFacet. Can be
     * overridden by classes that extend AbstractSingleSourceListFacet if a Map
     * other than an IdentityHashMap is desired for storing the information in
     * the AbstractSingleSourceListFacet.
     * <p>
     * Note that this method SHOULD NOT be public. The Map object is owned by
     * AbstractSingleSourceListFacet, and since it can be modified, a reference
     * to that object should not be exposed to any object other than
     * AbstractSingleSourceListFacet.
     * <p>
     * Note that this method should always be the only method used to construct
     * a Map for this AbstractSingleSourceListFacet. It is actually preferred to
     * use getConstructingCacheMap(CharID) in order to implicitly call this
     * method.
     *
     * @return A new (empty) Map for use in this AbstractSingleSourceListFacet.
     */
    protected Map<T, ST> getComponentMap()
    {
        return new IdentityHashMap<>();
    }

    /**
     * Copies the contents of the AbstractSingleSourceListFacet from one Player
     * Character to another Player Character, based on the given CharIDs
     * representing those Player Characters.
     * <p>
     * This is a method in AbstractSingleSourceListFacet in order to avoid
     * exposing the mutable Map object to other classes. This should not be
     * inlined, as the Map is internal information to
     * AbstractSingleSourceListFacet and should not be exposed to other classes.
     * <p>
     * Note also the copy is a one-time event and no references are maintained
     * between the Player Characters represented by the given CharIDs (meaning
     * once this copy takes place, any change to the
     * AbstractSingleSourceListFacet of one Player Character will only impact
     * the Player Character where the AbstractSingleSourceListFacet was
     * changed).
     *
     * @param source      The CharID representing the Player Character from which the
     *                    information should be copied
     * @param destination The CharID representing the Player Character to which the
     *                    information should be copied
     */
    @Override
    public void copyContents(CharID source, CharID destination)
    {
        Map<T, ST> sourceMap = getCachedMap(source);
        if (sourceMap != null)
        {
            getConstructingCachedMap(destination).putAll(sourceMap);
        }
    }

    /**
     * This method implements removal of a source for an object contained by
     * this AbstractSingleSourceListFacet. This implements the actual check that
     * determines if the given source was the source for the given object. If
     * so, then that object is removed from the list of objects stored in this
     * AbstractQualifiedListFacet for the Player Character represented by the
     * given CharID.
     *
     * @param id           The CharID representing the Player Character which may have
     *                     the given item removed.
     * @param componentMap The (private) Map for this AbstractSingleSourceListFacet that
     *                     will be removed if the given source is the recognized source
     *                     for the given object in this AbstractSingleSourceListFacet.
     * @param obj          The object which may be removed if the given source is the
     *                     only source for this object in the Player Character
     *                     represented by the given CharID
     * @param source       The source for the given object to be removed from the list of
     *                     sources for that object
     */
    private void processRemoval(CharID id, Map<T, ST> componentMap, T obj, ST source)
    {
        /*
         * TODO obj Null?
         *
         * Behavior should be consistent with AbstractlistFacet & others on
         * remove
         */
        Object oldSource = componentMap.get(obj);
        if (oldSource != null)
        {
            if (oldSource.equals(source))
            {
                componentMap.remove(obj);
                fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_REMOVED);
            }
        }
    }

    /**
     * Removes all information for the given source from this
     * AbstractSingleSourceListFacet for the PlayerCharacter represented by the
     * given CharID.
     *
     * @param id     The CharID representing the Player Character for which items
     *               from the given source will be removed
     * @param source The source for the objects to be removed from the list of
     *               items stored for the Player Character identified by the given
     *               CharID
     */
    public void removeAll(CharID id, ST source)
    {
        Map<T, ST> componentMap = getCachedMap(id);
        if (componentMap != null)
        {
            /*
             * This list exists primarily to eliminate the possibility of a
             * concurrent modification exception on a recursive remove
             */
            List<T> removedKeys = new ArrayList<>();
            for (Iterator<Map.Entry<T, ST>> it = componentMap.entrySet().iterator();it.hasNext();)
            {
                Entry<T, ST> me = it.next();
                Object currentsource = me.getValue();
                if (currentsource.equals(source))
                {
                    T obj = me.getKey();
                    it.remove();
                    removedKeys.add(obj);
                }
            }
            if (componentMap.isEmpty())
            {
                removeCache(id);
            }
            for (T obj : removedKeys)
            {
                fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_REMOVED);
            }
        }
    }

    /**
     * Returns a non-null copy of the Set of objects in this
     * AbstractSingleSourceListFacet for the Player Character represented by the
     * given CharID and the given source. This method returns an empty set if no
     * objects are in this AbstractSingleSourceListFacet for the Player
     * Character identified by the given CharID and source.
     * <p>
     * This method is value-semantic in that ownership of the returned List is
     * transferred to the class calling this method. Modification of the
     * returned List will not modify this AbstractSingleSourceListFacet and
     * modification of this AbstractSingleSourceListFacet will not modify the
     * returned List. Modifications to the returned List will also not modify
     * any future or previous objects returned by this (or other) methods on
     * AbstractSingleSourceListFacet. If you wish to modify the information
     * stored in this AbstractSingleSourceListFacet, you must use the add*() and
     * remove*() methods of AbstractSingleSourceListFacet.
     *
     * @param id    The CharID representing the Player Character for which the
     *              items in this AbstractSingleSourceListFacet should be
     *              returned.
     * @param owner The source object for which a copy of the List of objects in
     *              this AbstractSingleSourceListFacet should be returned.
     * @return A non-null Set of objects in this AbstractSingleSourceListFacet
     * for the Player Character represented by the given CharID
     */
    public List<? extends T> getSet(CharID id, ST owner)
    {
        List<T> list = new ArrayList<>();
        Map<T, ST> componentMap = getCachedMap(id);
        if (componentMap != null)
        {
            for (Entry<T, ST> me : componentMap.entrySet())
            {
                Object source = me.getValue();
                if (source.equals(owner))
                {
                    list.add(me.getKey());
                }
            }
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * Gets the source for the Player Character (identified by the given CharID)
     * and the given object.
     *
     * @param id  The CharID identifying the Player Character for which the
     *            association get is being performed.
     * @param obj The object for which the source is to be returned.
     * @return The source of the given object for the Player Character
     * identified by the given CharID
     */
    public ST getSource(CharID id, T obj)
    {
        Map<T, ST> map = getCachedMap(id);
        if (map == null)
        {
            return null;
        }
        return map.get(obj);
    }

    /**
     * Removes (unconditionally, regardless of the source) the given object
     * stored in this AbstractSingleSourceListFacet for the Player Character
     * represented by the given CharID.
     *
     * @param id  The CharID representing the Player Character from which the
     *            object should be removed
     * @param obj The object which should be removed
     */
    public void remove(CharID id, T obj)
    {
        Map<T, ST> map = getCachedMap(id);
        if (map != null)
        {
            if (map.remove(obj) != null)
            {
                fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_REMOVED);
            }
        }
    }

}
