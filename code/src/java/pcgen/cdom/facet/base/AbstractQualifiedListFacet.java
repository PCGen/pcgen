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
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import pcgen.cdom.base.QualifiedActor;
import pcgen.cdom.base.QualifyingObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.PrerequisiteFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;

/**
 * An AbstractQualifiedListFacet is a DataFacet that contains information about
 * QualifyingObjects that are contained in a PlayerCharacter when a
 * PlayerCharacter may have more than one of that type of QualifyingObject (e.g.
 * Language, PCTemplate), the source of that object should be tracked, and the
 * PlayerCharacter can qualify for the object (they have prerequisites)
 * <p>
 * This class is designed to assume that each QualifyingObject may only be
 * contained one time by the PlayerCharacter, even if received from multiple
 * sources. The QualifyingObject will only trigger one DATA_ADDED event (when
 * added by the first source) and if removed by some sources, will only trigger
 * one DATA_REMOVED event (when it is removed by the last remaining source).
 * Sources do not need to be removed in the order in which they are added, and
 * the first source to be added does not possess special status with respect to
 * triggering a DATA_REMOVED event (it will only trigger removal if it was the
 * last source when removed)
 * <p>
 * The sources stored in this AbstractQualifiedListFacet are stored as a List,
 * meaning the list of sources may contain the same source multiple times. If
 * so, each call to remove will only remove that source one time from the list
 * of sources.
 * <p>
 * In general, QualifyingObjects that are stored in an
 * AbstractQualifiedListFacet are those where the Prerequisites are those that
 * are considered requirements. This means that as the Player Character changes,
 * the state of the Prerequisite can change and alter whether the underlying
 * object is granted to the Player Character. For PCGen 5.16, this will mean
 * things like the Prerequisite on the end of an ABILITY token (which are
 * continuously evaluated) not the PRExxx: tokens that appear directly on the
 * line of an Ability in the Ability LST file (those are evaluated only once,
 * when the Ability is first added to the Player Character)
 * <p>
 * null is a valid source but a valid item to be added to the list of objects
 * stored by AbstractQualifiedListFacet.
 */
public abstract class AbstractQualifiedListFacet<T extends QualifyingObject> extends AbstractDataFacet<CharID, T>
{

    private PrerequisiteFacet prereqFacet = FacetLibrary.getFacet(PrerequisiteFacet.class);

    /**
     * Add the given object with the given source to the list of objects stored
     * in this AbstractQualifiedListFacet for the Player Character represented
     * by the given CharID
     *
     * @param id     The CharID representing the Player Character for which the
     *               given item should be added
     * @param obj    The object to be added to the list of objects stored in this
     *               AbstractQualifiedListFacet for the Player Character
     *               represented by the given CharID
     * @param source The source for the given object
     */
    public void add(CharID id, T obj, Object source)
    {
        Objects.requireNonNull(obj, "Object to add may not be null");
        Map<T, Set<Object>> map = getConstructingCachedMap(id);
        Set<Object> set = map.get(obj);
        boolean fireNew = (set == null);
        if (fireNew)
        {
            set = Collections.newSetFromMap(new IdentityHashMap<>());
            map.put(obj, set);
        }
        set.add(source);
        if (fireNew)
        {
            fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_ADDED);
        }
    }

    /**
     * Adds all of the objects in the given Collection to the list of objects
     * stored in this AbstractQualifiedListFacet for the Player Character
     * represented by the given CharID. All objects are added as if granted with
     * the given source.
     *
     * @param id     The CharID representing the Player Character for which the
     *               given items should be added
     * @param c      The Collection of objects to be added to the list of objects
     *               stored in this AbstractQualifiedListFacet for the Player
     *               Character represented by the given CharID
     * @param source The source for the objects in the given Collection
     * @throws NullPointerException if the given Collection is null
     */
    public void addAll(CharID id, Collection<? extends T> c, Object source)
    {
        for (T obj : c)
        {
            add(id, obj, source);
        }
    }

    /**
     * Removes the given source entry from the list of sources for the given
     * object stored in this AbstractQualifiedListFacet for the Player Character
     * represented by the given CharID. If the given source was the only source
     * for the given object, then the object is removed from the list of objects
     * stored in this AbstractQualifiedListFacet for the Player Character
     * represented by the given CharID and a removal event is fired.
     *
     * @param id     The CharID representing the Player Character from which the
     *               given item source should be removed
     * @param obj    The object for which the source should be removed
     * @param source The source for the given object to be removed from the list of
     *               sources.
     */
    public void remove(CharID id, T obj, Object source)
    {
        Map<T, Set<Object>> componentMap = getCachedMap(id);
        if (componentMap != null)
        {
            processRemoval(id, componentMap, obj, source);
        }
    }

    /**
     * Removes the given source entry from the list of sources for all of the
     * objects in the given Collection for the Player Character represented by
     * the given CharID. If the given source was the only source for any of the
     * objects in the collection, then those objects are removed from the list
     * of objects stored in this AbstractQualifiedListFacet for the Player
     * Character represented by the given CharID and a removal event is fired.
     *
     * @param id     The CharID representing the Player Character from which the
     *               given items should be removed
     * @param c      The Collection of objects to be removed from the list of
     *               objects stored in this AbstractQualifiedListFacet for the
     *               Player Character represented by the given CharID
     * @param source The source for the objects in the given Collection to be
     *               removed from the list of sources.
     * @throws NullPointerException if the given Collection is null
     */
    public void removeAll(CharID id, Collection<T> c, Object source)
    {
        Map<T, Set<Object>> componentMap = getCachedMap(id);
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
     * objects stored in this AbstractQualifiedListFacet for the Player
     * Character represented by the given CharID
     * <p>
     * This method is value-semantic in that ownership of the returned Map is
     * transferred to the class calling this method. Since this is a remove all
     * function, modification of the returned Map will not modify this
     * AbstractQualifiedListFacet and modification of this
     * AbstractQualifiedListFacet will not modify the returned Map.
     * Modifications to the returned Map will also not modify any future or
     * previous objects returned by this (or other) methods on
     * AbstractQualifiedListFacet. If you wish to modify the information stored
     * in this AbstractQualifiedListFacet, you must use the add*() and remove*()
     * methods of AbstractQualifiedListFacet.
     *
     * @param id The CharID representing the Player Character from which all
     *           items should be removed
     * @return A non-null Set of objects removed from the list of objects stored
     * in this AbstractQualifiedListFacet for the Player Character
     * represented by the given CharID
     */
    public Map<T, Set<Object>> removeAll(CharID id)
    {
        Map<T, Set<Object>> componentMap = getCachedMap(id);
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
     * AbstractQualifiedListFacet for the Player Character represented by the
     * given CharID. This method returns an empty set if no objects are in this
     * AbstractQualifiedListFacet for the Player Character identified by the
     * given CharID.
     * <p>
     * This method is value-semantic in that ownership of the returned List is
     * transferred to the class calling this method. Modification of the
     * returned List will not modify this AbstractQualifiedListFacet and
     * modification of this AbstractQualifiedListFacet will not modify the
     * returned List. Modifications to the returned List will also not modify
     * any future or previous objects returned by this (or other) methods on
     * AbstractQualifiedListFacet. If you wish to modify the information stored
     * in this AbstractQualifiedListFacet, you must use the add*() and remove*()
     * methods of AbstractQualifiedListFacet.
     *
     * @param id The CharID representing the Player Character for which the
     *           items in this AbstractQualifiedListFacet should be returned.
     * @return A non-null Set of objects in this AbstractQualifiedListFacet for
     * the Player Character represented by the given CharID
     */
    public Set<T> getSet(CharID id)
    {
        Map<T, Set<Object>> componentMap = getCachedMap(id);
        if (componentMap == null)
        {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(componentMap.keySet());
    }

    /**
     * Returns the count of items in this AbstractQualifiedListFacet for the
     * Player Character represented by the given CharID
     *
     * @param id The CharID representing the Player Character for which the
     *           count of items should be returned
     * @return The count of items in this AbstractQualifiedListFacet for the
     * Player Character represented by the given CharID
     */
    public int getCount(CharID id)
    {
        Map<T, Set<Object>> componentMap = getCachedMap(id);
        if (componentMap == null)
        {
            return 0;
        }
        return componentMap.size();
    }

    /**
     * Returns true if this AbstractQualifiedListFacet does not contain any
     * items for the Player Character represented by the given CharID
     *
     * @param id The CharId representing the PlayerCharacter to test if any
     *           items are contained by this AbstractsSourcedListFacet
     * @return true if this AbstractQualifiedListFacet does not contain any
     * items for the Player Character represented by the given CharID;
     * false otherwise (if it does contain items for the Player
     * Character)
     */
    public boolean isEmpty(CharID id)
    {
        Map<T, Set<Object>> componentMap = getCachedMap(id);
        return componentMap == null || componentMap.isEmpty();
    }

    /**
     * Returns true if this AbstractQualifiedListFacet contains the given value
     * in the list of items for the Player Character represented by the given
     * CharID.
     *
     * @param id  The CharID representing the Player Character used for testing
     * @param obj The object to test if this AbstractQualifiedListFacet contains
     *            that item for the Player Character represented by the given
     *            CharID
     * @return true if this AbstractQualifiedListFacet contains the given value
     * for the Player Character represented by the given CharID; false
     * otherwise
     */
    public boolean contains(CharID id, T obj)
    {
        /*
         * TODO obj == null? - log an error?
         *
         * This should share behavior with AbstractListFacet
         */
        Map<T, Set<Object>> componentMap = getCachedMap(id);
        return componentMap != null && componentMap.containsKey(obj);
    }

    /**
     * Returns a Set of sources for this AbstractQualifiedListFacet, the
     * PlayerCharacter represented by the given CharID, and the given object.
     * Will add the given object to the list of items for the PlayerCharacter
     * represented by the given CharID and will return a new, empty Set if no
     * information has been set in this AbstractQualifiedListFacet for the given
     * CharID and given object. Will not return null.
     * <p>
     * Note that this method SHOULD NOT be public. The Set object is owned by
     * AbstractQualifiedListFacet, and since it can be modified, a reference to
     * that object should not be exposed to any object other than
     * AbstractQualifiedListFacet.
     *
     * @param id  The CharID for which the Set should be returned
     * @param obj The object for which the Set of sources should be returned
     * @return The Set of sources for the given object and Player Character
     * represented by the given CharID.
     */
    private Set<Object> getConstructingCachedSetFor(CharID id, T obj)
    {
        Map<T, Set<Object>> map = getConstructingCachedMap(id);
        Set<Object> set = map.get(obj);
        if (set == null)
        {
            set = Collections.newSetFromMap(new IdentityHashMap<>());
            map.put(obj, set);
        }
        return set;
    }

    /**
     * Returns the type-safe Map for this AbstractQualifiedListFacet and the
     * given CharID. May return null if no information has been set in this
     * AbstractQualifiedListFacet for the given CharID.
     * <p>
     * Note that this method SHOULD NOT be public. The Map is owned by
     * AbstractQualifiedListFacet, and since it can be modified, a reference to
     * that object should not be exposed to any object other than
     * AbstractQualifiedListFacet.
     *
     * @param id The CharID for which the Set should be returned
     * @return The Set for the Player Character represented by the given CharID;
     * null if no information has been set in this
     * AbstractQualifiedListFacet for the Player Character.
     */
    @SuppressWarnings("unchecked")
    private Map<T, Set<Object>> getCachedMap(CharID id)
    {
        return (Map<T, Set<Object>>) getCache(id);
    }

    /**
     * Returns a type-safe Map for this AbstractQualifiedListFacet and the given
     * CharID. Will return a new, empty Map if no information has been set in
     * this AbstractQualifiedListFacet for the given CharID. Will not return
     * null.
     * <p>
     * Note that this method SHOULD NOT be public. The Map object is owned by
     * AbstractQualifiedListFacet, and since it can be modified, a reference to
     * that object should not be exposed to any object other than
     * AbstractQualifiedListFacet.
     *
     * @param id The CharID for which the Map should be returned
     * @return The Map for the Player Character represented by the given CharID.
     */
    private Map<T, Set<Object>> getConstructingCachedMap(CharID id)
    {
        Map<T, Set<Object>> componentMap = getCachedMap(id);
        if (componentMap == null)
        {
            componentMap = getComponentMap();
            setCache(id, componentMap);
        }
        return componentMap;
    }

    /**
     * Returns a new (empty) Map for this AbstractQualifiedListFacet. Can be
     * overridden by classes that extend AbstractQualifiedListFacet if a Map
     * other than an IdentityHashMap is desired for storing the information in
     * the AbstractQualifiedListFacet.
     * <p>
     * Note that this method SHOULD NOT be public. The Map object is owned by
     * AbstractQualifiedListFacet, and since it can be modified, a reference to
     * that object should not be exposed to any object other than
     * AbstractQualifiedListFacet.
     * <p>
     * Note that this method should always be the only method used to construct
     * a Map for this AbstractQualifiedListFacet. It is actually preferred to
     * use getConstructingCacheMap(CharID) in order to implicitly call this
     * method.
     *
     * @return A new (empty) Map for use in this AbstractQualifiedListFacet.
     */
    protected Map<T, Set<Object>> getComponentMap()
    {
        return new IdentityHashMap<>();
    }

    /**
     * Copies the contents of the AbstractQualifiedListFacet from one Player
     * Character to another Player Character, based on the given CharIDs
     * representing those Player Characters.
     * <p>
     * This is a method in AbstractQualifiedListFacet in order to avoid exposing
     * the mutable Map object to other classes. This should not be inlined, as
     * the Map is internal information to AbstractQualifiedListFacet and should
     * not be exposed to other classes.
     * <p>
     * Note also the copy is a one-time event and no references are maintained
     * between the Player Characters represented by the given CharIDs (meaning
     * once this copy takes place, any change to the AbstractQualifiedListFacet
     * of one Player Character will only impact the Player Character where the
     * AbstractQualifiedListFacet was changed).
     *
     * @param source      The CharID representing the Player Character from which the
     *                    information should be copied
     * @param destination The CharID representing the Player Character to which the
     *                    information should be copied
     */
    @Override
    public void copyContents(CharID source, CharID destination)
    {
        Map<T, Set<Object>> sourceMap = getCachedMap(source);
        if (sourceMap != null)
        {
            for (Map.Entry<T, Set<Object>> me : sourceMap.entrySet())
            {
                T obj = me.getKey();
                Set<Object> sourceSet = me.getValue();
                Set<Object> targetSet = getConstructingCachedSetFor(destination, obj);
                targetSet.addAll(sourceSet);
            }
        }
    }

    /**
     * This method implements removal of a source for an object contained by
     * this AbstractQualifiedListFacet. This implements the actual check that
     * determines if the given source was the only source for the given object.
     * If so, then that object is removed from the list of objects stored in
     * this AbstractQualifiedListFacet for the Player Character represented by
     * the given CharID.
     *
     * @param id           The CharID representing the Player Character which may have
     *                     the given item removed.
     * @param componentMap The (private) Map for this AbstractQualifiedListFacet that
     *                     will as least have the given source removed from the list for
     *                     the given object.
     * @param obj          The object which may be removed if the given source is the
     *                     only source for this object in the Player Character
     *                     represented by the given CharID
     * @param source       The source for the given object to be removed from the list of
     *                     sources for that object
     */
    private void processRemoval(CharID id, Map<T, Set<Object>> componentMap, T obj, Object source)
    {
        Objects.requireNonNull(obj, "Object to remove may not be null");
        Set<Object> set = componentMap.get(obj);
        if (set != null)
        {
            set.remove(source);
            if (set.isEmpty())
            {
                componentMap.remove(obj);
                fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_REMOVED);
            }
        }
    }

    /**
     * Removes all information for the given source from this
     * AbstractQualifiedListFacet for the PlayerCharacter represented by the
     * given CharID.
     *
     * @param id     The CharID representing the Player Character for which items
     *               from the given source will be removed
     * @param source The source for the objects to be removed from the list of
     *               items stored for the Player Character identified by the given
     *               CharID
     */
    public void removeAll(CharID id, Object source)
    {
        Map<T, Set<Object>> componentMap = getCachedMap(id);
        if (componentMap != null)
        {
            /*
             * This list exists primarily to eliminate the possibility of a
             * concurrent modification exception on a recursive remove
             */
            List<T> removedKeys = new ArrayList<>();
            for (Iterator<Map.Entry<T, Set<Object>>> it = componentMap.entrySet().iterator();it.hasNext();)
            {
                Entry<T, Set<Object>> me = it.next();
                Set<Object> set = me.getValue();
                if (set.remove(source) && set.isEmpty())
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
     * AbstractQualifiedListFacet for the Player Character represented by the
     * given CharID and the given source. This method returns an empty set if no
     * objects are in this AbstractQualifiedListFacet for the Player Character
     * identified by the given CharID and source.
     * <p>
     * This method is value-semantic in that ownership of the returned List is
     * transferred to the class calling this method. Modification of the
     * returned List will not modify this AbstractQualifiedListFacet and
     * modification of this AbstractQualifiedListFacet will not modify the
     * returned List. Modifications to the returned List will also not modify
     * any future or previous objects returned by this (or other) methods on
     * AbstractQualifiedListFacet. If you wish to modify the information stored
     * in this AbstractQualifiedListFacet, you must use the add*() and remove*()
     * methods of AbstractQualifiedListFacet.
     *
     * @param id    The CharID representing the Player Character for which the
     *              items in this AbstractQualifiedListFacet should be returned.
     * @param owner The source object for which a copy of the List of objects in
     *              this AbstractQualifiedListFacet should be returned.
     * @return A non-null Set of objects in this AbstractQualifiedListFacet for
     * the Player Character represented by the given CharID
     */
    public List<? extends T> getSet(CharID id, Object owner)
    {
        List<T> list = new ArrayList<>();
        Map<T, Set<Object>> componentMap = getCachedMap(id);
        if (componentMap != null)
        {
            for (Entry<T, Set<Object>> me : componentMap.entrySet())
            {
                Set<Object> set = me.getValue();
                if (set.contains(owner))
                {
                    list.add(me.getKey());
                }
            }
        }
        return list;
    }

    /**
     * Returns a non-null copy of the Set of objects the character qualifies for
     * in this AbstractQualifiedListFacet for the Player Character represented
     * by the given CharID. This method returns an empty set if the Player
     * Character identified by the given CharID qualifies for none of the
     * objects in this AbstractQualifiedListFacet.
     * <p>
     * This method is value-semantic in that ownership of the returned
     * Collection is transferred to the class calling this method. Modification
     * of the returned Collection will not modify this
     * AbstractQualifiedListFacet and modification of this
     * AbstractQualifiedListFacet will not modify the returned Collection.
     * Modifications to the returned Collection will also not modify any future
     * or previous objects returned by this (or other) methods on
     * AbstractQualifiedListFacet. If you wish to modify the information stored
     * in this AbstractQualifiedListFacet, you must use the add*() and remove*()
     * methods of AbstractQualifiedListFacet.
     *
     * @param id The CharID representing the Player Character for which the
     *           items in this AbstractQualifiedListFacet should be returned.
     * @return A non-null Set of objects the Player Character represented by the
     * given CharID qualifies for in this AbstractQualifiedListFacet
     */
    public Collection<T> getQualifiedSet(CharID id)
    {
        Set<T> set = new HashSet<>();
        Map<T, Set<Object>> componentMap = getCachedMap(id);
        if (componentMap != null)
        {
            for (Map.Entry<T, Set<Object>> me : componentMap.entrySet())
            {
                T obj = me.getKey();
                Set<Object> sources = me.getValue();
                for (Object source : sources)
                {
                    if (prereqFacet.qualifies(id, obj, source))
                    {
                        set.add(obj);
                        break;
                    }
                }
            }
        }
        return set;
    }

    /**
     * Returns a non-null copy of the Set of objects the character qualifies for
     * in this AbstractQualifiedListFacet for the Player Character represented
     * by the given CharID and the given source. This method returns an empty
     * set if the Player Character identified by the given CharID qualifies for
     * none of the objects in this AbstractQualifiedListFacet granted by the
     * given source.
     * <p>
     * This method is value-semantic in that ownership of the returned List is
     * transferred to the class calling this method. Modification of the
     * returned List will not modify this AbstractQualifiedListFacet and
     * modification of this AbstractQualifiedListFacet will not modify the
     * returned List. Modifications to the returned List will also not modify
     * any future or previous objects returned by this (or other) methods on
     * AbstractQualifiedListFacet. If you wish to modify the information stored
     * in this AbstractQualifiedListFacet, you must use the add*() and remove*()
     * methods of AbstractQualifiedListFacet.
     * <p>
     * Generally, use of this method is discouraged in general operational
     * aspects. However, it is recognized that certain output tokens can list
     * certain items by source, and thus this method is required, and it is
     * unreasonable to expect complete elimination of this method or entirely
     * prohibit future use of this method.
     *
     * @param id     The CharID representing the Player Character for which the
     *               items in this AbstractQualifiedListFacet should be returned.
     * @param source The source object for which a copy of the List of objects the
     *               Player Character qualifies for should be returned.
     * @return A non-null Set of objects the Player Character represented by the
     * given CharID qualifies for in this AbstractQualifiedListFacet
     */
    public Collection<T> getQualifiedSet(CharID id, Object source)
    {
        Set<T> set = Collections.newSetFromMap(new IdentityHashMap<>());
        Map<T, Set<Object>> componentMap = getCachedMap(id);
        if (componentMap != null)
        {
            for (Map.Entry<T, Set<Object>> me : componentMap.entrySet())
            {
                T obj = me.getKey();
                Set<Object> sources = me.getValue();
                if (sources.contains(source))
                {
                    if (prereqFacet.qualifies(id, obj, source))
                    {
                        set.add(obj);
                    }
                }
            }
        }
        return set;
    }

    /**
     * Acts on the Set of objects the character qualifies for in this
     * AbstractQualifiedListFacet for the Player Character represented by the
     * given CharID. The results of each action as provided by the given
     * QualifiedActor are returned in a non-null List.
     * <p>
     * This method returns an empty List if the Player Character identified by
     * the given CharID qualifies for none of the objects in this
     * AbstractQualifiedListFacet.
     * <p>
     * This method is value-semantic in that ownership of the returned List is
     * transferred to the class calling this method. Modification of the
     * returned List will not modify this AbstractQualifiedListFacet and
     * modification of this AbstractQualifiedListFacet will not modify the
     * returned List. Modifications to the returned List will also not modify
     * any future or previous objects returned by this (or other) methods on
     * AbstractQualifiedListFacet. If you wish to modify the information stored
     * in this AbstractQualifiedListFacet, you must use the add*() and remove*()
     * methods of AbstractQualifiedListFacet.
     * <p>
     * Note: If a particular item has been granted by more than one source, then
     * the QualifiedActor will only be called for the first source that
     * (successfully grants) the underlying object.
     *
     * @param id The CharID representing the Player Character for which the
     *           items in this AbstractQualifiedListFacet should be returned.
     * @param qa The QualifiedActor which will act on each of the items in this
     *           AbstractQualifiedListFacet for which the Player Character
     *           qualifies.
     * @return A non-null List of objects created by the QualifiedActor from
     * each of the objects in this AbstractQualifiedListFacet for which
     * the Player Character qualifies.
     */
    public <R> List<R> actOnQualifiedSet(CharID id, QualifiedActor<T, R> qa)
    {
        List<R> list = new ArrayList<>();
        Map<T, Set<Object>> componentMap = getCachedMap(id);
        if (componentMap != null)
        {
            for (Map.Entry<T, Set<Object>> me : componentMap.entrySet())
            {
                T obj = me.getKey();
                Set<Object> sources = me.getValue();
                for (Object source : sources)
                {
                    if (prereqFacet.qualifies(id, obj, source))
                    {
                        list.add(qa.act(obj, source));
                    }
                }
            }
        }
        return list;
    }

    public Collection<Object> getSources(CharID id, T obj)
    {
        Map<T, Set<Object>> componentMap = getCachedMap(id);
        if (componentMap != null)
        {
            Set<Object> sources = componentMap.get(obj);
            if (sources != null)
            {
                return Collections.unmodifiableSet(sources);
            }
        }
        return Collections.emptySet();
    }
}
