/*
 * Copyright 2005, 2007, 2014 (C) Tom Parker <thpr@sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.util;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import pcgen.base.util.HashMapToList;
import pcgen.base.util.Indirect;
import pcgen.base.util.ObjectContainer;
import pcgen.cdom.enumeration.FactSetKey;

/**
 * This encapsulates a MapToList in a typesafe and value-semantic way.
 * <p>
 * Specifically, when Generics are properly used by a class using a
 * FactSetKeyMapToList, this class ensures that any FactSetKey will only return
 * a List of the same Generic type as the FactSetKey. Note this relationship is
 * only enforced with Generics, and could be violated if Generics are not
 * properly used.
 * <p>
 * This Class also is reference-semantic with respect to the Map and the List.
 * In other words, the modification of any Collection returned by a
 * FactSetKeyMapToList will not impact the internal contents of the
 * FactSetKeyMapToList. Also, any Collection used as a parameter to a method is
 * not stored directly, the Collection can be modified after the method has
 * returned without impacting the internal contents of the FactSetKeyMapToList.
 * <p>
 * **NOTE** This class is NOT thread safe.
 */
public class FactSetKeyMapToList
{

    /*
     * This must remain generic, as far as I know. The challenge here is that
     * this really wants to be HashMapToList<FactSetKey<T>, T>, but the T needs
     * to change for each individual FactSetKey/List contained within the
     * HashMapToList. I don't believe it is possible with Generics in Java.
     * Thus, this entire class is filled with Generic warnings.
     *
     * The advantage of having this class is two-fold: 1) It is value-semantic
     * with respect to the Map and the List [but not the contents of the Lists])
     * 2) It hides all of the generic warnings in once place where they can be
     * easily analysed as innocent. -- Tom Parker 1/15/07
     */
    /**
     * The internal storage of this FactSetKeyMapToList
     */
    @SuppressWarnings("rawtypes")
    private final HashMapToList map = new HashMapToList();

    /**
     * Adds all of the Lists in the given FactSetKeyMapToList to this
     * FactSetKeyMapToList. The resulting lists are independent (protecting the
     * internal structure of FactSetKeyMapToList), however, since
     * FactSetKeyMapToList is reference-semantic, the List keys and values in
     * each list are identical.
     * <p>
     * This method is reference-semantic and this FactSetKeyMapToList will
     * maintain a strong reference to all key objects and objects in each list
     * of the given FactSetKeyMapToList.
     *
     * @param lcs The FactSetKeyMapToList from which all of the Lists should be
     *            imported
     * @throws NullPointerException if the specified FactSetKeyMapToList is null.
     */
    @SuppressWarnings("unchecked")
    public void addAllLists(FactSetKeyMapToList lcs)
    {
        map.addAllLists(lcs.map);
    }

    /**
     * Adds all of the Objects in the given list to the (internal) List for the
     * given FactSetKey. The null value cannot be used as a key in a
     * FactSetKeyMapToList. This method will automatically initialize the list
     * for the given key if there is not already a List for that key.
     * <p>
     * This method is reference-semantic and this FactSetKeyMapToList will
     * maintain a strong reference to both the key object and the object in the
     * given list.
     *
     * @param key    The FactSetKey indicating which List the objects in the given
     *               List should be added to.
     * @param values A List containing the items to be added to the List for the
     *               given key.
     */
    @SuppressWarnings("unchecked")
    public <T> void addAllToListFor(FactSetKey<T> key, Collection<ObjectContainer<T>> values)
    {
        map.addAllToListFor(key, values);
    }

    /**
     * Adds the given value to the List for the given FactSetKey. The null value
     * cannot be used as a key in a FactSetKeyMapToList. This method will
     * automatically initialize the list for the given key if there is not
     * already a List for that key.
     * <p>
     * This method is reference-semantic and this FactSetKeyMapToList will
     * maintain a strong reference to both the key object and the value object
     * given as arguments to this method.
     *
     * @param key          The FactSetKey indicating which List the given object should
     *                     be added to.
     * @param valueElement The value to be added to the List for the given key.
     */
    @SuppressWarnings("unchecked")
    public <T> void addToListFor(FactSetKey<T> key, Indirect<T> valueElement)
    {
        map.addToListFor(key, valueElement);
    }

    /**
     * Returns true if this FactSetKeyMapToList contains a List for the given
     * FactSetKey. This method returns false if the given key is not in this
     * FactSetKeyMapToList.
     * <p>
     * This method is value-semantic in that no changes are made to the object
     * passed into the method.
     *
     * @param key The FactSetKey being tested.
     * @return true if this FactSetKeyMapToList contains a List for the given
     * key; false otherwise.
     */
    @SuppressWarnings("unchecked")
    public boolean containsListFor(FactSetKey<?> key)
    {
        return map.containsListFor(key);
    }

    /**
     * Returns a copy of the List contained in this FactSetKeyMapToList for the
     * given FactSetKey. This method returns null if the given key is not in
     * this FactSetKeyMapToList.
     * <p>
     * This method is value-semantic in that no changes are made to the object
     * passed into the method and ownership of the returned List is transferred
     * to the class calling this method.
     *
     * @param key The FactSetKey for which a copy of the list should be
     *            returned.
     * @return a copy of the List contained in this FactSetKeyMapToList for the
     * given key; null if the given key is not a key in this
     * FactSetKeyMapToList.
     */
    @SuppressWarnings("unchecked")
    public <T> List<Indirect<T>> getListFor(FactSetKey<T> key)
    {
        return map.getListFor(key);
    }

    /**
     * Initializes a List for the given FactSetKey. The null value cannot be
     * used as a key in a FactSetKeyMapToList.
     * <p>
     * This method is reference-semantic and this FactSetKeyMapToList will
     * maintain a strong reference to the key object given as an argument to
     * this method.
     *
     * @param key The FactSetKey for which a List should be initialized in this
     *            FactSetKeyMapToList.
     */
    @SuppressWarnings("unchecked")
    public <T> void initializeListFor(FactSetKey<T> key)
    {
        map.initializeListFor(key);
    }

    /**
     * Removes the given value from the list for the given FactSetKey. Returns
     * true if the value was successfully removed from the list for the given
     * FactSetKey. Returns false if there is not a list for the given FactSetKey
     * or if the list for the given FactSetKey did not contain the given value
     * object.
     *
     * @param key          The FactSetKey indicating which List the given object should
     *                     be removed from
     * @param valueElement The value to be removed from the List for the given key
     * @return true if the value was successfully removed from the list for the
     * given key; false otherwise
     */
    @SuppressWarnings("unchecked")
    public <T> boolean removeFromListFor(FactSetKey<T> key, Indirect<T> valueElement)
    {
        return map.removeFromListFor(key, valueElement);
    }

    /**
     * Removes the List for the given FactSetKey. Note there is no requirement
     * that the list for the given key be empty before this method is called.
     * <p>
     * Ownership of the returned List is transferred to the object calling this
     * method.
     *
     * @param key The FactSetKey indicating which List the given object should
     *            be removed from
     * @return The List which this FactSetKeyMapToList previous mapped the given
     * key
     */
    @SuppressWarnings("unchecked")
    public <T> List<ObjectContainer<T>> removeListFor(FactSetKey<T> key)
    {
        return map.removeListFor(key);
    }

    /**
     * Returns the number of objects in the List for the given FactSetKey. This
     * method will throw a NullPointerException if this FactSetKeyMapToList does
     * not contain a List for the given key.
     * <p>
     * This method is value-semantic in that no changes are made to the object
     * passed into the method.
     *
     * @param key The key being tested.
     * @return the number of objects in the List for the given key
     */
    @SuppressWarnings("unchecked")
    public int sizeOfListFor(FactSetKey<?> key)
    {
        return map.sizeOfListFor(key);
    }

    /**
     * Returns true if this FactSetKeyMapToList contains a List for the given
     * FactSetKey and that list contains the given value. Note, this method
     * returns false if the given FactSetKey is not in this FactSetKeyMapToList.
     * <p>
     * This method is value-semantic in that no changes are made to the objects
     * passed into the method.
     *
     * @param key          The key for the List being tested.
     * @param valueElement The value to find in the List for the given key.
     * @return true if this FactSetKeyMapToList contains a List for the given
     * key AND that list contains the given value; false otherwise.
     */
    @SuppressWarnings("unchecked")
    public <T> boolean containsInList(FactSetKey<T> key, ObjectContainer<T> valueElement)
    {
        return map.containsInList(key, valueElement);
    }

    /**
     * Returns true if this FactSetKeyMapToList contains a List for the given
     * key and that list contains one or more of the values in the given
     * collection. Note, this method returns false if the given key is not in
     * this FactSetKeyMapToList.
     * <p>
     * This method is value-semantic in that no changes are made to the objects
     * passed into the method.
     *
     * @param key    The key for the List being tested.
     * @param values The collection of values to find in the List for the given
     *               key.
     * @return true if this FactSetKeyMapToList contains a List for the given
     * key AND that list contains one or more of the given values; false
     * otherwise.
     */
    @SuppressWarnings("unchecked")
    public <T> boolean containsAnyInList(FactSetKey<T> key, Collection<ObjectContainer<T>> values)
    {
        return map.containsAnyInList(key, values);
    }

    /**
     * Returns a Set indicating the Keys of this FactSetKeyMapToList. Ownership
     * of the Set is transferred to the calling Object, no association is kept
     * between the Set and this FactSetKeyMapToList. (Thus, removal of a key
     * from the returned Set will not remove that key from this
     * FactSetKeyMapToList)
     * <p>
     * NOTE: This method returns all of the keys this FactSetKeyMapToList
     * contains. It DOES NOT determine whether the Lists defined for the keys
     * are empty. Therefore, it is possible that this FactSetKeyMapToList
     * contains one or more keys, and all of the lists associated with those
     * keys are empty, yet this method will return a non-zero length Set.
     *
     * @return a Set containing the keys in this FactSetKeyMapToList
     */
    @SuppressWarnings("unchecked")
    public Set<FactSetKey<?>> getKeySet()
    {
        return map.getKeySet();
    }

    /**
     * Returns true if this structure contains no Lists.
     *
     * @return true if this structure contains no Lists; false otherwise
     */
    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    @Override
    public int hashCode()
    {
        return map.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof FactSetKeyMapToList && map.equals(((FactSetKeyMapToList) obj).map);
    }
}
