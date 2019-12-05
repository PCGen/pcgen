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
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ClassIdentity;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.GroupingState;

/**
 * A CDOMCompoundOrReference is a CDOMReference which is intended to contain one
 * or more CDOMReferences that this object "joins" in an "or" format. In other
 * words, if any one of the underlying CDOMReference objects contains an object,
 * then this CDOMCompoundOrReference will contain the object.
 *
 * @param <T> The Class of the underlying objects contained by this
 *            CDOMCompoundOrReference
 */
public class CDOMCompoundOrReference<T extends PrereqObject> extends CDOMGroupRef<T>
{

    /**
     * The ClassIdentity that represents the objects contained in this
     * CDOMCompoundOrReference.
     */
    private final ClassIdentity<T> identity;

    /**
     * The list of underlying references that this CDOMCompoundOrReference
     * contains
     */
    private final ArrayList<CDOMReference<T>> references = new ArrayList<>();

    /**
     * Creates a new CDOMCompoundOrReference with the given name which will
     * contain CDOMReferences that contain objects of the given Class.
     *
     * @param classIdentity The ClassIdentity of the underlying object contained by this
     *                      CDOMCompoundOrReference.
     * @param refName       An identifier of the objects this CDOMCompoundOrReference
     *                      contains.
     */
    public CDOMCompoundOrReference(ClassIdentity<T> classIdentity, String refName)
    {
        super(refName);
        identity = Objects.requireNonNull(classIdentity);
    }

    /**
     * Adds a new CDOMReference to this CDOMCompoundOrReference.
     *
     * @param ref The CDOMReference to add to this CDOMCompoundOrReference
     * @throws IllegalArgumentException if the given CDOMReference for addition to this
     *                                  CDOMCompoundOrReference does not represent the same
     *                                  CDOMObject class as this CDOMCompoundOrReference
     * @throws NullPointerException     if the given CDOMReference is null
     */
    public void addReference(CDOMReference<T> ref)
    {
        /*
         * Extra protection in case generics fail
         */
        if (!getReferenceClass().equals(ref.getReferenceClass()))
        {
            throw new IllegalArgumentException("Cannot add reference of " + ref.getReferenceClass()
                    + " to CDOMCompoundOrReference of " + getReferenceClass());
        }
        references.add(ref);
    }

    /**
     * Returns true if the given Object is included in the Collection of Objects
     * to which this CDOMCompoundOrReference refers.
     * <p>
     * Note that the behavior of this class is undefined if CDOMReferences have
     * not been added to this CDOMCompoundOrReference or if any of the
     * underlying CDOMReference objects have not been resolved.
     *
     * @param item The object to be tested to see if it is referred to by this
     *             CDOMCompoundOrReference.
     * @return true if the given Object is included in the Collection of Objects
     * to which this CDOMCompoundOrReference refers; false otherwise.
     */
    @Override
    public boolean contains(T item)
    {
        for (CDOMReference<T> ref : references)
        {
            if (ref.contains(item))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * For purposes of memory optimization, allows the underlying collection of
     * CDOMReferences to be compacted to the exact size of the number of
     * CDOMReference objects that this CDOMCompoundOrReference contains.
     */
    public void trimToSize()
    {
        references.trimToSize();
    }

    /**
     * Returns a representation of this CDOMCompoundOrReference, suitable for
     * storing in an LST file.
     * <p>
     * Note that this will call the getLSTformat() method of the underlying
     * CDOMReference objects. Therefore, the contents of the String returned by
     * this method is partially governed by the response of the individual
     * CDOMReference objects contained by this CDOMCompoundOrReference.
     * <p>
     * Note that this will ALWAYS return a comma-delimited list of objects if
     * more than one reference is present in the CDOMCompoundOrReference.
     *
     * @return A representation of this CDOMCompoundOrReference, suitable for
     * storing in an LST file.
     */
    @Override
    public String getLSTformat(boolean useAny)
    {
        return ReferenceUtilities.joinLstFormat(references, Constants.COMMA);
    }

    /**
     * Throws an exception. This method may not be called because a
     * CDOMCompoundOrReference refers to objects indirectly through references.
     * <p>
     * To add items to this CDOMCompoundOrReference, see
     * addReference(CDOMReference)
     *
     * @param item ignored
     * @throws IllegalStateException because a CDOMCompoundOrReference does not get directly
     *                               resolved.
     */
    @Override
    public void addResolution(T item)
    {
        throw new IllegalStateException("CompoundReference cannot be given a resolution");
    }

    /**
     * Returns the count of the number of objects included in the Collection of
     * Objects to which this CDOMCompoundOrReference refers.
     * <p>
     * Note that the behavior of this class is undefined if CDOMReferences have
     * not been added to this CDOMCompoundOrReference or if any of the
     * underlying CDOMReference objects have not been resolved.
     *
     * @return the count of the number of objects included in the Collection of
     * Objects to which this CDOMCompoundOrReference refers.
     */
    @Override
    public int getObjectCount()
    {
        int count = 0;
        for (CDOMReference<T> ref : references)
        {
            count += ref.getObjectCount();
        }
        return count;
    }

    /**
     * Returns a Collection containing the Objects to which this
     * CDOMCompoundOrReference refers.
     * <p>
     * Note that the behavior of this class is undefined if CDOMReferences have
     * not been added to this CDOMCompoundOrReference or if any of the
     * underlying CDOMReference objects have not been resolved.
     * <p>
     * This method is reference-semantic, meaning that ownership of the
     * Collection returned by this method is transferred to the calling object.
     * Modification of the returned Collection should not result in modifying
     * the CDOMCompoundOrReference, and modifying the CDOMCompoundOrReference
     * after the Collection is returned should not modify the Collection.
     *
     * @return A Collection containing the Objects to which this
     * CDOMCompoundOrReference refers.
     */
    @Override
    public Collection<T> getContainedObjects()
    {
        Set<T> set = new HashSet<>();
        for (CDOMReference<T> ref : references)
        {
            set.addAll(ref.getContainedObjects());
        }
        return set;
    }

    /**
     * Returns the GroupingState for this CDOMCompoundOrReference. The
     * GroupingState indicates how this CDOMCompoundOrReference can be combined
     * with other PrimitiveChoiceFilters.
     *
     * @return The GroupingState for this CDOMCompoundOrReference.
     */
    @Override
    public GroupingState getGroupingState()
    {
        GroupingState state = GroupingState.EMPTY;
        for (CDOMReference<T> ref : references)
        {
            state = state.add(ref.getGroupingState());
        }
        return state.compound(GroupingState.ALLOWS_UNION);
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
        StringJoiner joiner = new StringJoiner(" OR ", identity.getReferenceDescription() + "[", "]");
        references.stream().map(CDOMReference::getReferenceDescription).forEach(joiner::add);
        return joiner.toString();
    }

    @Override
    public String getPersistentFormat()
    {
        return identity.getPersistentFormat();
    }
}
