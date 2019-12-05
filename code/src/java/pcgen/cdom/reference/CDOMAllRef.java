/*
 * Copyright (c) 2007-18 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.reference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import pcgen.cdom.base.ClassIdentity;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.GroupingState;

/**
 * A CDOMAllRef is a CDOMReference which is intended to contain all objects of
 * the ClassIdentity this CDOMAllRef represents.
 *
 * @param <T> The Class of the underlying objects contained by this reference
 */
public final class CDOMAllRef<T> extends CDOMGroupRef<T>
{

    /**
     * The ClassIdentity that represents the objects contained in this CDOMAllRef.
     */
    private final ClassIdentity<T> identity;

    /**
     * The objects (presumably all of the objects) of the Class this CDOMAllRef
     * represents.
     */
    private List<T> referencedList = null;

    /**
     * Constructs a new CDOMAllRef for the given ClassIdentity to be represented by this
     * CDOMAllRef.
     *
     * @param classIdentity The ClassIdentity of the underlying objects contained by this CDOMAllRef
     */
    public CDOMAllRef(ClassIdentity<T> classIdentity)
    {
        super(Constants.LST_ALL + ": " + classIdentity.getReferenceDescription());
        identity = Objects.requireNonNull(classIdentity);
    }

    /**
     * Returns a representation of this CDOMAllRef, suitable for storing in an
     * LST file.
     * <p>
     * Note that this will return the identifier of the "All" reference, not an
     * expanded list of the contents of this CDOMAllRef.
     *
     * @return A representation of this CDOMAllRef, suitable for storing in an
     * LST file.
     */
    @Override
    public String getLSTformat(boolean useAny)
    {
        return useAny ? Constants.LST_ANY : Constants.LST_ALL;
    }

    /**
     * Returns true if the given Object is included in the Collection of Objects
     * to which this CDOMAllRef refers.
     * <p>
     * Note that the behavior of this class is undefined if the CDOMAllRef has
     * not yet been resolved.
     *
     * @param item The object to be tested to see if it is referred to by this
     *             CDOMAllRef.
     * @return true if the given Object is included in the Collection of Objects
     * to which this CDOMAllRef refers; false otherwise.
     * @throws IllegalStateException if this CDOMAllRef has not been resolved
     */
    @Override
    public boolean contains(T item)
    {
        if (referencedList == null)
        {
            throw new IllegalStateException("Cannot ask for contains: Reference has not been resolved");
        }
        return referencedList.contains(item);
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof CDOMAllRef && identity.equals(((CDOMAllRef<?>) obj).identity);
    }

    @Override
    public int hashCode()
    {
        return identity.hashCode();
    }

    /**
     * Adds an object to be included in the Collection of objects to which this
     * CDOMAllRef refers.
     *
     * @param item An object to be included in the Collection of objects to which
     *             this CDOMAllRef refers.
     * @throws IllegalArgumentException if the given object for addition to this CDOMAllRef is not of
     *                                  the class that this CDOMAllRef represents
     * @throws NullPointerException     if the given object is null
     */
    @Override
    public void addResolution(T item)
    {
        if (item.getClass().equals(getReferenceClass()))
        {
            if (referencedList == null)
            {
                referencedList = new ArrayList<>();
            }
            referencedList.add(item);
        } else
        {
            throw new IllegalArgumentException("Cannot resolve a " + getReferenceClass().getSimpleName()
                    + " Reference to a " + item.getClass().getSimpleName());
        }
    }

    /**
     * Returns the count of the number of objects included in the Collection of
     * Objects to which this CDOMAllRef refers.
     * <p>
     * Note that the behavior of this class is undefined if the CDOMAllRef has
     * not yet been resolved.
     *
     * @return The count of the number of objects included in the Collection of
     * Objects to which this CDOMAllRef refers.
     */
    @Override
    public int getObjectCount()
    {
        return referencedList == null ? 0 : referencedList.size();
    }

    /**
     * Returns a Collection containing the Objects to which this CDOMAllRef
     * refers.
     * <p>
     * This method is reference-semantic, meaning that ownership of the
     * Collection returned by this method is transferred to the calling object.
     * Modification of the returned Collection should not result in modifying
     * the CDOMAllRef, and modifying the CDOMAllRef after the Collection is
     * returned should not modify the Collection.
     * <p>
     * Note that the behavior of this class is undefined if the CDOMAllRef has
     * not yet been resolved. (It may return null or an empty Collection; that
     * is implementation dependent)
     *
     * @return A Collection containing the Objects to which this CDOMAllRef
     * refers.
     */
    @Override
    public Collection<T> getContainedObjects()
    {
        return Collections.unmodifiableList(referencedList);
    }

    /**
     * Returns the GroupingState for this CDOMAllRef. The GroupingState
     * indicates how this CDOMAllRef can be combined with other
     * PrimitiveChoiceFilters.
     *
     * @return The GroupingState for this CDOMAllRef.
     */
    @Override
    public GroupingState getGroupingState()
    {
        return GroupingState.ALLOWS_NONE;
    }

    @Override
    public String getChoice()
    {
        return null;
    }

    @Override
    public Class<T> getReferenceClass()
    {
        return identity.getReferenceClass();
    }

    @Override
    public String getReferenceDescription()
    {
        return "ALL " + identity.getReferenceDescription();
    }

    @Override
    public String getPersistentFormat()
    {
        return identity.getPersistentFormat();
    }
}
