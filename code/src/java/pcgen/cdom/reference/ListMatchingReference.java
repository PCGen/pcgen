/*
 * Copyright James Dempsey, 2011
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
 *
 */
package pcgen.cdom.reference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.ListKey;

/**
 * A ListMatchingReference is a CDOMReference that matches objects based on a
 * a ListKey and an expected value.
 * <p>
 * An underlying start list is provided during construction of the
 * ListMatchingReference. Generally, this will be the CDOMAllRef for the class
 * of object underlying this ListMatchingReference.
 *
 * @param <T> The class of object underlying this ListMatchingReference.
 * @param <V> The class of object stored by the List Key used by this
 *            ListMatchingReference.
 */
public class ListMatchingReference<T extends CDOMObject, V> extends CDOMReference<T>
{

    /**
     * The CDOMGroupRef containing the underlying list of objects from which
     * this ListMatchingReference will draw.
     */
    private final CDOMGroupRef<T> all;

    private final ListKey<V> key;

    private final V value;

    /*
     * CONSIDER is it necessary/useful to cache the results of the pattern
     * match? If that is done, under what conditions does the cache need to be
     * invalidated (how can the underlying CDOMGroupRef be known to not have
     * been modified)?
     */

    /**
     * Constructs a new ListMatchingReference
     *
     * @param unparse       The Class of the underlying objects contained by this
     *                      reference.
     * @param startingGroup The underlying list of objects from which this
     *                      ListMatchingReference will draw.
     * @throws IllegalArgumentException if the starting group is null or the provided pattern does
     *                                  not end with the PCGen pattern characters
     */
    public ListMatchingReference(String unparse, CDOMGroupRef<T> startingGroup, ListKey<V> targetKey, V expectedValue)
    {
        super(unparse);
        Objects.requireNonNull(startingGroup, "Starting Group cannot be null in ListMatchingReference");
        Objects.requireNonNull(targetKey, "Target Key cannot be null in ListMatchingReference");
        all = startingGroup;
        key = targetKey;
        value = expectedValue;
    }

    /**
     * Throws an exception. This method may not be called because a
     * ListMatchingReference is resolved based on the pattern provided at
     * construction.
     *
     * @param item ignored
     * @throws IllegalStateException because a ListMatchingReference is resolved based on the
     *                               key/value pair provided at construction.
     */
    @Override
    public void addResolution(T item)
    {
        throw new IllegalStateException("Cannot add resolution to ListMatchingReference");
    }

    /**
     * Returns true if the given Object is included in the Collection of Objects
     * to which this ListMatchingReference refers.
     * <p>
     * Note that the behavior of this class is undefined if the CDOMGroupRef
     * underlying this ListMatchingReference has not yet been resolved.
     *
     * @param item The object to be tested to see if it is referred to by this
     *             ListMatchingReference.
     * @return true if the given Object is included in the Collection of Objects
     * to which this ListMatchingReference refers; false otherwise.
     */
    @Override
    public boolean contains(T item)
    {
        if (!all.contains(item))
        {
            return false;
        }
        List<V> actualList = item.getListFor(key);
        if (actualList != null)
        {
            for (V actual : actualList)
            {
                if (value != null && value.equals(actual))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a Collection containing the Objects to which this
     * ListMatchingReference refers.
     * <p>
     * This method is reference-semantic, meaning that ownership of the
     * Collection returned by this method is transferred to the calling object.
     * Modification of the returned Collection should not result in modifying
     * the ListMatchingReference, and modifying the ListMatchingReference
     * after the Collection is returned should not modify the Collection.
     * <p>
     * Note that the behavior of this class is undefined if the CDOMGroupRef
     * underlying this ListMatchingReference has not yet been resolved.
     *
     * @return A Collection containing the Objects to which this
     * ListMatchingReference refers.
     */
    @Override
    public Collection<T> getContainedObjects()
    {
        List<T> list = new ArrayList<>();
        for (T obj : all.getContainedObjects())
        {
            List<V> actualList = obj.getListFor(key);
            if (actualList != null)
            {
                for (V actual : actualList)
                {
                    if (value != null && value.equals(actual))
                    {
                        list.add(obj);
                        continue;
                    }
                }
            }
        }
        return list;
    }

    /**
     * Returns a representation of this ListMatchingReference, suitable for
     * storing in an LST file.
     * <p>
     * Note that this will return the pattern String provided during
     * construction of the ListMatchingReference.
     *
     * @return A representation of this ListMatchingReference, suitable for
     * storing in an LST file.
     */
    @Override
    public String getLSTformat(boolean useAny)
    {
        return getName();
    }

    /**
     * Returns the count of the number of objects included in the Collection of
     * Objects to which this ListMatchingReference refers.
     * <p>
     * Note that the behavior of this class is undefined if the CDOMGroupRef
     * underlying this ListMatchingReference has not yet been resolved.
     *
     * @return The count of the number of objects included in the Collection of
     * Objects to which this ListMatchingReference refers.
     */
    @Override
    public int getObjectCount()
    {
        int count = 0;
        for (T obj : all.getContainedObjects())
        {
            List<V> actualList = obj.getListFor(key);
            if (actualList != null)
            {
                for (V actual : actualList)
                {
                    if (value != null && value.equals(actual))
                    {
                        count++;
                        continue;
                    }
                }
            }
        }
        return count;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ListMatchingReference)
        {
            ListMatchingReference<?, ?> other = (ListMatchingReference<?, ?>) obj;
            if (getReferenceClass().equals(other.getReferenceClass()) && all.equals(other.all) && key.equals(other.key))
            {
                if (value == null)
                {
                    return other.value == null;
                }
                return value.equals(other.value);
            }
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return getReferenceClass().hashCode() ^ key.hashCode() + (value == null ? -1 : value.hashCode());
    }

    /**
     * Returns the GroupingState for this ListMatchingReference. The
     * GroupingState indicates how this ListMatchingReference can be combined
     * with other PrimitiveChoiceFilters.
     *
     * @return The GroupingState for this ListMatchingReference.
     */
    @Override
    public GroupingState getGroupingState()
    {
        return GroupingState.ANY;
    }

    @Override
    public String getChoice()
    {
        return null;
    }

    @Override
    public Class<T> getReferenceClass()
    {
        return all.getReferenceClass();
    }

    @Override
    public String getReferenceDescription()
    {
        return all.getReferenceDescription() + " (List " + key + " = " + value + ")";
    }

    @Override
    public String getPersistentFormat()
    {
        return all.getPersistentFormat();
    }
}
