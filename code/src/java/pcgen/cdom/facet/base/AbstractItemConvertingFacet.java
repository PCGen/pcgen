/*
 * Copyright (c) Thomas Parker, 2010-14.
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

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.event.DataFacetChangeEvent;

/**
 * An AbstractItemConvertingFacet is a DataFacet that converts information from
 * one type to another when the source of that object should be tracked.
 * <p>
 * This class is designed to assume that each original object may only be
 * contained one time by the PlayerCharacter, even if received from multiple
 * sources. The original object will only trigger one DATA_ADDED event (when
 * added by the first source) and if removed by some sources, will only trigger
 * one DATA_REMOVED event (when it is removed by the last remaining source).
 * Sources do not need to be removed in the order in which they are added, and
 * the first source to be added does not possess special status with respect to
 * triggering a DATA_REMOVED event (it will only trigger removal if it was the
 * last source when removed)
 * <p>
 * The sources stored in this AbstractItemConvertingFacet are stored as a List,
 * meaning the list of sources may contain the same source multiple times. If
 * so, each call to remove will only remove that source one time from the list
 * of sources.
 * <p>
 * Note: There is no requirement that the conversion process is reversible. In
 * other words, more than once source object may produce the same (or equal)
 * destination objects.
 * <p>
 * null is a valid source.
 */
public abstract class AbstractItemConvertingFacet<S, D> extends AbstractDataFacet<CharID, D>
{
    /**
     * Add the converted version of the given object with the given source to
     * the list of (converted) objects stored in this
     * AbstractItemConvertingFacet for the Player Character represented by the
     * given CharID.
     *
     * @param id     The CharID representing the Player Character for which the
     *               given item should be added
     * @param obj    The object for which the converted version will be added to
     *               the list of (converted) objects stored in this
     *               AbstractItemConvertingFacet for the Player Character
     *               represented by the given CharID
     * @param source The source for the given object
     */
    public void add(CharID id, S obj, Object source)
    {
        Objects.requireNonNull(obj, "Object to add may not be null");
        Target target = getConstructingCachedSetFor(id, obj);
        target.set.add(source);
        if (target.dest == null)
        {
            target.dest = convert(obj);
            fireDataFacetChangeEvent(id, target.dest, DataFacetChangeEvent.DATA_ADDED);
        }
    }

    /**
     * Adds conversions of all of the objects in the given Collection to the
     * list of (converted) objects stored in this AbstractItemConvertingFacet
     * for the Player Character represented by the given CharID. All items are
     * added with the given source.
     *
     * @param id     The CharID representing the Player Character for which the
     *               given items should be added
     * @param c      The Collection of objects for which the converted versions
     *               will be added to the list of objects stored in this
     *               AbstractItemConvertingFacet for the Player Character
     *               represented by the given CharID
     * @param source The source for the given objects in the collection
     * @throws NullPointerException if the given Collection is null
     */
    public void addAll(CharID id, Collection<? extends S> c, Object source)
    {
        for (S obj : c)
        {
            add(id, obj, source);
        }
    }

    /**
     * Removes the given source entry from the list of sources for conversion of
     * the given object stored in this AbstractItemConvertingFacet for the
     * Player Character represented by the given CharID. If the given source was
     * the only source for the given object, then the converted object is
     * removed from the list of objects stored in this
     * AbstractItemConvertingFacet for the Player Character represented by the
     * given CharID.
     *
     * @param id     The CharID representing the Player Character from which the
     *               given item source should be removed
     * @param obj    The object for which the source should be removed from the
     *               converted version of that object
     * @param source The source for the given object is to be removed from the list
     *               of sources for the converted version of the given object
     */
    public void remove(CharID id, S obj, Object source)
    {
        Map<S, Target> componentMap = getCachedMap(id);
        if (componentMap != null)
        {
            processRemoval(id, componentMap, obj, source);
        }
    }

    /**
     * Removes the given source entry from the list of sources for the converted
     * version of all of the objects in the given Collection for the Player
     * Character represented by the given CharID. If the given source was the
     * only source for any of the (converted) objects in the collection, then
     * those objects are removed from the list of objects stored in this
     * AbstractItemConvertingFacet for the Player Character represented by the
     * given CharID.
     *
     * @param id     The CharID representing the Player Character from which the
     *               given items should be removed
     * @param c      The Collection of objects for which the conversions will be
     *               removed from the list of objects stored in this
     *               AbstractItemConvertingFacet for the Player Character
     *               represented by the given CharID
     * @param source The source for the objects in the given Collection to be
     *               removed from the list of sources
     * @throws NullPointerException if the given Collection is null
     */
    public void removeAll(CharID id, Collection<S> c, Object source)
    {
        Map<S, Target> componentMap = getCachedMap(id);
        if (componentMap != null)
        {
            for (S obj : c)
            {
                processRemoval(id, componentMap, obj, source);
            }
        }
    }

    /**
     * Removes all converted objects (and all sources for those objects) from
     * the list of objects stored in this AbstractItemConvertingFacet for the
     * Player Character represented by the given CharID.
     * <p>
     * This method is value-semantic in that ownership of the returned Map is
     * transferred to the class calling this method. Since this is a remove all
     * function, modification of the returned Map will not modify this
     * AbstractItemConvertingFacet and modification of this
     * AbstractItemConvertingFacet will not modify the returned Map.
     * Modifications to the returned Map will also not modify any future or
     * previous objects returned by this (or other) methods on
     * AbstractItemConvertingFacet. If you wish to modify the information stored
     * in this AbstractItemConvertingFacet, you must use the add*() and
     * remove*() methods of AbstractItemConvertingFacet.
     *
     * @param id The CharID representing the Player Character from which all
     *           items should be removed
     * @return A non-null Map of converted object mapped to their sources, all
     * of which were removed from the list of original objects stored in
     * this AbstractItemConvertingFacet for the Player Character
     * represented by the given CharID
     */
    public Map<S, Target> removeAll(CharID id)
    {
        Map<S, Target> componentMap = getCachedMap(id);
        if (componentMap == null)
        {
            return Collections.emptyMap();
        }
        removeCache(id);
        for (Target tgt : componentMap.values())
        {
            fireDataFacetChangeEvent(id, tgt.dest, DataFacetChangeEvent.DATA_REMOVED);
        }
        return componentMap;
    }

    /**
     * Returns the count of (non-equal) original objects in this
     * AbstractItemConvertingFacet for the Player Character represented by the
     * given CharID.
     * <p>
     * Note: This does not necessarily return the count of the number of
     * (non-equal) converted objects added. It may, but it will do so if and
     * only if the conversion process can not produce identical conversion
     * targets from two unequal sources.
     *
     * @param id The CharID representing the Player Character for which the
     *           count of items should be returned
     * @return The count of converted objects in this
     * AbstractItemConvertingFacet for the Player Character represented
     * by the given CharID
     */
    public int getCount(CharID id)
    {
        Map<S, Target> componentMap = getCachedMap(id);
        if (componentMap == null)
        {
            return 0;
        }
        return componentMap.size();
    }

    /**
     * Returns true if this AbstractItemConvertingFacet does not contain any
     * items for the Player Character represented by the given CharID.
     *
     * @param id The CharId representing the PlayerCharacter to test if any
     *           items are contained by this AbstractsSourcedListFacet
     * @return true if this AbstractItemConvertingFacet does not contain any
     * items for the Player Character represented by the given CharID;
     * false otherwise (if it does contain items for the Player
     * Character)
     */
    public boolean isEmpty(CharID id)
    {
        Map<S, Target> componentMap = getCachedMap(id);
        return (componentMap == null) || componentMap.isEmpty();
    }

    /**
     * Returns true if this AbstractItemConvertingFacet was provided with the
     * given source object to be converted and stored in the list of items for
     * the Player Character represented by the given CharID.
     *
     * @param id  The CharID representing the Player Character used for testing
     * @param obj The object to test if this AbstractItemConvertingFacet
     *            contains that original item for the Player Character
     *            represented by the given CharID
     * @return true if this AbstractItemConvertingFacet was provided with the
     * given source object to be converted and stored in the list of
     * items for the Player Character represented by the given CharID;
     * false otherwise
     */
    public boolean contains(CharID id, S obj)
    {
        Map<S, Target> componentMap = getCachedMap(id);
        return (componentMap != null) && componentMap.containsKey(obj);
    }

    /**
     * Returns a Target storage object for this AbstractItemConvertingFacet, the
     * PlayerCharacter represented by the given CharID, and the given source
     * object. Will add the given object to the list of items for the
     * PlayerCharacter represented by the given CharID and will return a new,
     * empty Target object if no information has been set in this
     * AbstractItemConvertingFacet for the given CharID and given object. Will
     * not return null.
     * <p>
     * Note that this method SHOULD NOT be public. The Set object is owned by
     * AbstractItemConvertingFacet, and since it can be modified, a reference to
     * that object should not be exposed to any object other than
     * AbstractItemConvertingFacet.
     *
     * @param id  The CharID for which the Target should be returned
     * @param obj The object for which the Target should be returned
     * @return The Target object for the given object and Player Character
     * represented by the given CharID.
     */
    private Target getConstructingCachedSetFor(CharID id, S obj)
    {
        Map<S, Target> map = getConstructingCachedMap(id);
        Target target = map.get(obj);
        if (target == null)
        {
            target = new Target();
            map.put(obj, target);
        }
        return target;
    }

    /**
     * Returns the type-safe Map for this AbstractItemConvertingFacet and the
     * given CharID. May return null if no information has been set in this
     * AbstractItemConvertingFacet for the given CharID.
     * <p>
     * Note that this method SHOULD NOT be public. The Map is owned by
     * AbstractItemConvertingFacet, and since it can be modified, a reference to
     * that object should not be exposed to any object other than
     * AbstractItemConvertingFacet.
     *
     * @param id The CharID for which the Set should be returned
     * @return The Set for the Player Character represented by the given CharID;
     * null if no information has been set in this
     * AbstractItemConvertingFacet for the Player Character.
     */
    @SuppressWarnings("unchecked")
    protected Map<S, Target> getCachedMap(CharID id)
    {
        return (Map<S, Target>) getCache(id);
    }

    /**
     * Returns a type-safe Map for this AbstractItemConvertingFacet and the
     * given CharID. Will return a new, empty Map if no information has been set
     * in this AbstractItemConvertingFacet for the given CharID. Will not return
     * null.
     * <p>
     * Note that this method SHOULD NOT be public. The Map object is owned by
     * AbstractItemConvertingFacet, and since it can be modified, a reference to
     * that object should not be exposed to any object other than
     * AbstractItemConvertingFacet.
     *
     * @param id The CharID for which the Map should be returned
     * @return The Map for the Player Character represented by the given CharID.
     */
    private Map<S, Target> getConstructingCachedMap(CharID id)
    {
        Map<S, Target> componentMap = getCachedMap(id);
        if (componentMap == null)
        {
            componentMap = getComponentMap();
            setCache(id, componentMap);
        }
        return componentMap;
    }

    /**
     * Returns a new (empty) Map for this AbstractItemConvertingFacet. Can be
     * overridden by classes that extend AbstractItemConvertingFacet if a Map
     * other than an IdentityHashMap is desired for storing the information in
     * the AbstractItemConvertingFacet.
     * <p>
     * Note that this method SHOULD NOT be public. The Map object is owned by
     * AbstractItemConvertingFacet, and since it can be modified, a reference to
     * that object should not be exposed to any object other than
     * AbstractItemConvertingFacet.
     * <p>
     * Note that this method should always be the only method used to construct
     * a Map for this AbstractItemConvertingFacet. It is actually preferred to
     * use getConstructingCacheMap(CharID) in order to implicitly call this
     * method.
     *
     * @return A new (empty) Map for use in this AbstractItemConvertingFacet.
     */
    protected Map<S, Target> getComponentMap()
    {
        return new IdentityHashMap<>();
    }

    /**
     * Copies the contents of the AbstractItemConvertingFacet from one Player
     * Character to another Player Character, based on the given CharIDs
     * representing those Player Characters.
     * <p>
     * This is a method in AbstractItemConvertingFacet in order to avoid
     * exposing the mutable Map object to other classes. This should not be
     * inlined, as the Map is internal information to
     * AbstractItemConvertingFacet and should not be exposed to other classes.
     * <p>
     * Note also the copy is a one-time event and no references are maintained
     * between the Player Characters represented by the given CharIDs (meaning
     * once this copy takes place, any change to the AbstractItemConvertingFacet
     * of one Player Character will only impact the Player Character where the
     * AbstractItemConvertingFacet was changed).
     *
     * @param source      The CharID representing the Player Character from which the
     *                    information should be copied
     * @param destination The CharID representing the Player Character to which the
     *                    information should be copied
     */
    @Override
    public void copyContents(CharID source, CharID destination)
    {
        Map<S, Target> sourceMap = getCachedMap(source);
        if (sourceMap != null)
        {
            for (Map.Entry<S, Target> me : sourceMap.entrySet())
            {
                Target origTarget = me.getValue();
                if (origTarget != null)
                {
                    S obj = me.getKey();
                    Target target = getConstructingCachedSetFor(destination, obj);
                    //This could be dangerous!
                    target.dest = origTarget.dest;
                    target.set.addAll(origTarget.set);
                }
            }
        }
    }

    /**
     * This method implements removal of a source for an object contained by
     * this AbstractItemConvertingFacet. This implements the actual check that
     * determines if the given source was the only source for the given object.
     * If so, then that object is removed from the list of objects stored in
     * this AbstractQualifiedListFacet for the Player Character represented by
     * the given CharID and a removal event is fired.
     *
     * @param id           The CharID representing the Player Character which may have
     *                     the given item removed.
     * @param componentMap The (private) Map for this AbstractItemConvertingFacet that
     *                     will as least have the given source removed from the list for
     *                     the given object.
     * @param obj          The object which may be removed if the given source is the
     *                     only source for this object in the Player Character
     *                     represented by the given CharID
     * @param source       The source for the given object to be removed from the list of
     *                     sources for that object
     */
    private void processRemoval(CharID id, Map<S, Target> componentMap, S obj, Object source)
    {
        Objects.requireNonNull(obj, "Object to remove may not be null");
        Target target = componentMap.get(obj);
        if (target != null)
        {
            target.set.remove(source);
            if (target.set.isEmpty())
            {
                componentMap.remove(obj);
                fireDataFacetChangeEvent(id, target.dest, DataFacetChangeEvent.DATA_REMOVED);
            }
        }
    }

    /**
     * Removes all information (converted and unconverted objects) for the given
     * source from this AbstractItemConvertingFacet for the PlayerCharacter
     * represented by the given CharID.
     *
     * @param id     The CharID representing the Player Character for which items
     *               from the given source will be removed
     * @param source The source for the objects to be removed from the list of
     *               items stored for the Player Character identified by the given
     *               CharID
     */
    public void removeAll(CharID id, Object source)
    {
        Map<S, Target> componentMap = getCachedMap(id);
        if (componentMap != null)
        {
            for (Iterator<Target> it = componentMap.values().iterator();it.hasNext();)
            {
                Target target = it.next();
                if (target != null)
                {
                    if (target.set.remove(source) && target.set.isEmpty())
                    {
                        it.remove();
                        fireDataFacetChangeEvent(id, target.dest, DataFacetChangeEvent.DATA_REMOVED);
                    }
                }
            }
        }
    }

    /**
     * Returns true if this AbstractItemConvertingFacet contains an object from
     * the given source for the Player Character identified by the given CharID.
     *
     * @param id     The CharID representing the Player Character which will be
     *               checked to see if this AbstractItemConvertingFacet contains
     *               any objects for that Player Character
     * @param source The source for the objects to be checked, along with the
     *               Player Character identified by the given CharID, to see if
     *               this AbstractItemConvertingFacet contains an object from the
     *               given source
     * @return true if this AbstractItemConvertingFacet contains an object from
     * the given source for the Player Character identified by the given
     * CharID; false otherwise
     */
    public boolean containsFrom(CharID id, Object source)
    {
        Map<S, Target> componentMap = getCachedMap(id);
        if (componentMap != null)
        {
            for (Entry<S, Target> me : componentMap.entrySet())
            {
                Target target = me.getValue();
                if (target != null)
                {
                    if (target.set.contains(source))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * The storage class for AbstractItemConvertingFacet. Used to store both the
     * converted object as well as the list of sources for the given destination
     * object.
     */
    private class Target
    {
        /**
         * The set of objects from which the converted object has been received
         */
        public Set<Object> set = Collections.newSetFromMap(new IdentityHashMap<>());

        /**
         * The converted ("destination") object
         */
        public D dest;

        @Override
        public int hashCode()
        {
            return dest.hashCode();
        }

        @Override
        public boolean equals(Object o)
        {
            if (o == this)
            {
                return true;
            }
            if (o instanceof AbstractItemConvertingFacet.Target)
            {
                Target other = (Target) o;
                return dest.equals(other.dest) && set.equals(other.set);
            }
            return false;
        }
    }

    /**
     * Converts the given object to the destination object type stored in this
     * AbstractItemConvertingFacet. Must be implemented by classes that extend
     * AbstractItemConvertingFacet.
     *
     * @param obj The original object stored in this AbstractItemConvertingFacet
     * @return The converted object to be stored in this
     * AbstractItemConvertingFacet for the given original object
     */
    protected abstract D convert(S obj);

    public Collection<S> getSourceObjects(CharID id)
    {
        Set<S> set = Collections.newSetFromMap(new IdentityHashMap<>());
        Map<S, Target> componentMap = getCachedMap(id);
        if (componentMap != null)
        {
            set.addAll(componentMap.keySet());
        }
        return set;
    }

    public D getResultFor(CharID id, S obj)
    {
        Map<S, Target> componentMap = getCachedMap(id);
        return (componentMap == null) ? null : componentMap.get(obj).dest;
    }

    public Collection<Object> getSourcesFor(CharID id, S obj)
    {
        Map<S, Target> componentMap = getCachedMap(id);
        Set<Object> set = Collections.newSetFromMap(new IdentityHashMap<>());
        if (componentMap == null)
        {
            return set;
        }
        set.addAll(componentMap.get(obj).set);
        return set;
    }
}
