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

import java.util.Collection;
import java.util.Objects;

import pcgen.cdom.base.Loadable;
import pcgen.cdom.enumeration.GroupingState;

/**
 * A CDOMTransparentAllRef is a CDOMReference which is intended to contain a
 * CDOMGroupRef, to which the CDOMTransparentAllRef will delegate behavior.
 * <p>
 * A CDOMTransparentAllRef, unlike many CDOMReference objects, can be cleared,
 * and the underlying CDOMGroupRef can be changed.
 *
 * @param <T> The Class of the underlying object contained by this
 *            CDOMTransparentAllRef
 * @see TransparentReference for a description of cases in which
 * TransparentReferences like CDOMTransparentAllRef are typically used
 */
public class CDOMTransparentAllRef<T extends Loadable> extends CDOMGroupRef<T> implements TransparentReference<T>
{

    /**
     * The Class that indicates the types of objects contained in this
     * CDOMTransparentAllRef.
     */
    private final Class<T> refClass;

    /**
     * Holds the reference to which this CDOMTransparentAllRef will delegate
     * behavior.
     */
    private CDOMGroupRef<T> subReference = null;

    /**
     * The String representation of the Format of objects in this CDOMTransparentSingleRef (e.g.
     * "ABILITY=FEAT").
     */
    private final String formatRepresentation;

    /**
     * Constructs a new CDOMTransparentAllRef for the given Class.
     *
     * @param formatRepresentation the persistent representation of the ClassIdentity of the objects to be
     *                             stored in this CDOMTransparentAllRef
     * @param objClass             The Class of the underlying objects contained by this
     *                             CDOMTransparentTypeRef.
     */
    public CDOMTransparentAllRef(String formatRepresentation, Class<T> objClass)
    {
        super("ALL");
        this.formatRepresentation = Objects.requireNonNull(formatRepresentation);
        refClass = objClass;
    }

    /**
     * Returns true if the given Object is the Object to which this
     * CDOMTransparentAllRef refers.
     * <p>
     * Note that the behavior of this class is undefined if the underlying
     * CDOMGroupRef has not yet been resolved.
     *
     * @param item The object to be tested to see if it is referred to by this
     *             CDOMTransparentAllRef.
     * @return true if the given Object is the Object to which this
     * CDOMTransparentAllRef refers; false otherwise.
     * @throws IllegalStateException if no underlying CDOMGroupRef has been defined.
     */
    @Override
    public boolean contains(T item)
    {
        if (subReference == null)
        {
            throw new IllegalStateException("Cannot ask for contains: " + getReferenceClass().getName() + " Reference "
                    + getName() + " has not been resolved");
        }
        return subReference.contains(item);
    }

    /**
     * Returns a representation of this CDOMTransparentAllRef, suitable for
     * storing in an LST file.
     * <p>
     * Note that this will return the identifier of the underlying reference (of
     * the types given at construction), often the "key" in LST terminology.
     *
     * @return A representation of this CDOMTransparentAllRef, suitable for
     * storing in an LST file.
     */
    @Override
    public String getLSTformat(boolean useAny)
    {
        return subReference.getLSTformat(useAny);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof CDOMTransparentAllRef)
        {
            CDOMTransparentAllRef<?> ref = (CDOMTransparentAllRef<?>) obj;
            return getReferenceClass().equals(ref.getReferenceClass()) && getName().equals(ref.getName());
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return getReferenceClass().hashCode() ^ getName().hashCode();
    }

    /**
     * Throws an exception. This method may not be called because a
     * CDOMTransparentAllRef is resolved using an underlying CDOMGroupRef.
     *
     * @param item ignored
     * @throws IllegalStateException because a CDOMTransparentTypeRef is resolved using an
     *                               underlying CDOMGroupRef.
     * @see CDOMTransparentAllRef#resolve(ReferenceManufacturer)
     */
    @Override
    public void addResolution(T item)
    {
        throw new IllegalStateException("Cannot resolve a Transparent Reference");
    }

    /**
     * Resolves this CDOMTransparentAllRef using the given
     * ReferenceManufacturer. The underlying CDOMGroupRef for this
     * CDOMTransparentAllRef will be set to the "ALL" CDOMGroupRef constructed
     * by the given ReferenceManufacturer
     * <p>
     * This method may be called more than once; each time it is called it will
     * overwrite the existing CDOMGroupRef to which this CDOMTransparentAllRef
     * delegates its behavior.
     *
     * @throws IllegalArgumentException if the Reference Class of the given ReferenceManufacturer is
     *                                  different than the Reference Class of this
     *                                  CDOMTransparentAllRef
     * @throws NullPointerException     if the given ReferenceManufacturer is null
     */
    @Override
    public void resolve(ReferenceManufacturer<T> rm)
    {
        if (rm.getReferenceClass().equals(getReferenceClass()))
        {
            subReference = rm.getAllReference();
        } else
        {
            throw new IllegalArgumentException("Cannot resolve a " + getReferenceClass().getSimpleName()
                    + " Reference to a " + rm.getReferenceClass().getSimpleName());
        }
    }

    /**
     * Returns a Collection containing the single Object to which this
     * CDOMTransparentAllRef refers.
     * <p>
     * The semantics of this method are defined solely by the semantics of the
     * underlying CDOMGroupRef. Ownership of the Collection returned by this
     * method may or may not be transferred to the calling object (check the
     * documentation of the underlying CDOMGroupRef).
     * <p>
     * Note that if you know this CDOMTransparentAllRef is a CDOMGroupRef, you
     * are better off using resolvesTo() as the result will be much faster than
     * having to extract the object out of the Collection returned by this
     * method.
     *
     * @return A Collection containing the single Object to which this
     * CDOMTransparentAllRef refers.
     */
    @Override
    public Collection<T> getContainedObjects()
    {
        return subReference.getContainedObjects();
    }

    /**
     * Returns the count of the number of objects included in the Collection of
     * Objects to which this CDOMTransparentAllRef refers.
     * <p>
     * Note that the behavior of this class is undefined if the
     * CDOMTransparentAllRef has not yet been resolved.
     *
     * @return The count of the number of objects included in the Collection of
     * Objects to which this CDOMTransparentAllRef refers.
     */
    @Override
    public int getObjectCount()
    {
        return subReference == null ? 0 : subReference.getObjectCount();
    }

    /**
     * Returns the GroupingState for this CDOMTransparentAllRef. The
     * GroupingState indicates how this CDOMTransparentAllRef can be combined
     * with other PrimitiveChoiceFilters.
     *
     * @return The GroupingState for this CDOMTransparentAllRef.
     */
    @Override
    public GroupingState getGroupingState()
    {
        return GroupingState.ALLOWS_NONE;
    }

    @Override
    public String getChoice()
    {
        return (subReference == null) ? null : subReference.getChoice();
    }

    @Override
    public Class<T> getReferenceClass()
    {
        return refClass;
    }

    @Override
    public String getReferenceDescription()
    {
        return (subReference == null) ? "ALL " + refClass.getSimpleName() : subReference.getReferenceDescription();
    }

    @Override
    public String getPersistentFormat()
    {
        // TODO Auto-generated method stub
        return formatRepresentation;
    }
}
