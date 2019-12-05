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
 * A CDOMTransparentSingleRef is a CDOMReference which is intended to contain a
 * another CDOMSingleRef, to which the CDOMTransparentSingleRef will delegate
 * behavior.
 * <p>
 * A CDOMTransparentSingleRef, unlike many CDOMReference objects, can be
 * cleared, and the underlying CDOMSingleRef can be changed.
 *
 * @param <T> The Class of the underlying object contained by this
 *            CDOMTransparentSingleRef
 * @see TransparentReference for a description of cases in which
 * TransparentReferences like CDOMTransparentSingleRef are typically used
 */
public class CDOMTransparentSingleRef<T extends Loadable> extends CDOMSingleRef<T> implements TransparentReference<T>
{

    /**
     * The Class that indicates the types of objects objects contained in this
     * CDOMTransparentSingleRef.
     */
    private final Class<T> refClass;

    /**
     * Holds the reference to which this CDOMTransparentSingleRef will delegate
     * behavior.
     */
    private CDOMSingleRef<T> subReference = null;

    /**
     * The String representation of the Format of objects in this CDOMTransparentSingleRef (e.g.
     * "ABILITY=FEAT").
     */
    private final String formatRepresentation;

    /**
     * Constructs a new CDOMTransparentSingleRef for the given Class and name.
     *
     * @param formatRepresentation the persistent representation of the ClassIdentity of the objects to be
     *                             stored in this CDOMTransparentSingleRef
     * @param objClass             The Class of the underlying object contained by this
     *                             CDOMTransparentSingleRef.
     * @param key                  An identifier of the object this CDOMTransparentSingleRef contains.
     */
    public CDOMTransparentSingleRef(String formatRepresentation, Class<T> objClass, String key)
    {
        super(key);
        this.formatRepresentation = Objects.requireNonNull(formatRepresentation);
        refClass = Objects.requireNonNull(objClass);
    }

    /**
     * Returns true if the given Object is the Object to which this
     * CDOMTransparentSingleRef refers.
     * <p>
     * Note that the behavior of this class is undefined if the underlying
     * CDOMSingleRef has not yet been resolved.
     *
     * @param item The object to be tested to see if it is referred to by this
     *             CDOMTransparentSingleRef.
     * @return true if the given Object is the Object to which this
     * CDOMTransparentSingleRef refers; false otherwise.
     * @throws IllegalStateException if no underlying CDOMSingleRef has been defined.
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
     * Returns the given Object this CDOMTransparentSingleRef contains.
     * <p>
     * Note that the behavior of this class is undefined if the underlying
     * CDOMSingleRef has not yet been resolved.
     *
     * @return the given Object this CDOMTransparentSingleRef contains.
     * @throws IllegalStateException if no underlying CDOMSingleRef has been defined.
     */
    @Override
    public T get()
    {
        if (subReference == null)
        {
            throw new IllegalStateException("Cannot ask for resolution: Reference has not been resolved");
        }
        return subReference.get();
    }

    /**
     * Check if the reference has been resolved yet. i.e. load of the object has been completed.
     *
     * @return true if the reference has been resolved, false if not.
     */
    @Override
    public boolean hasBeenResolved()
    {
        return subReference != null && subReference.hasBeenResolved();
    }

    /**
     * Returns a representation of this CDOMTransparentSingleRef, suitable for
     * storing in an LST file.
     * <p>
     * Note that this will return the identifier of the underlying reference (of
     * the types given at construction), often the "key" in LST terminology.
     *
     * @return A representation of this CDOMTransparentSingleRef, suitable for
     * storing in an LST file.
     */
    @Override
    public String getLSTformat(boolean useAny)
    {
        return getName();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof CDOMTransparentSingleRef)
        {
            CDOMTransparentSingleRef<?> ref = (CDOMTransparentSingleRef<?>) obj;
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
     * CDOMTransparentSingleRef is resolved using an underlying CDOMSingleRef.
     *
     * @param item ignored
     * @throws IllegalStateException because a CDOMTransparentSingleRef is resolved using an
     *                               underlying CDOMSingleRef.
     * @see CDOMTransparentSingleRef#resolve(ReferenceManufacturer)
     */
    @Override
    public void addResolution(T item)
    {
        throw new IllegalStateException("Cannot resolve a Transparent Reference");
    }

    /**
     * Resolves this CDOMTransparentSingleRef using the given
     * ReferenceManufacturer. The underlying CDOMSingleRef for this
     * CDOMTransparentSingleRef will be set to a CDOMSingleRef constructed by
     * the given ReferenceManufacturer (using the identifier provided during
     * construction of this CDOMTransparentSingleRef)
     * <p>
     * This method may be called more than once; each time it is called it will
     * overwrite the existing CDOMSingleRef to which this
     * CDOMTransparentSingleRef delegates its behavior.
     *
     * @throws IllegalArgumentException if the Reference Class of the given ReferenceManufacturer is
     *                                  different than the Reference Class of this
     *                                  CDOMTransparentSingleRef
     * @throws NullPointerException     if the given ReferenceManufacturer is null
     */
    @Override
    public void resolve(ReferenceManufacturer<T> rm)
    {
        if (rm.getReferenceClass().equals(getReferenceClass()))
        {
            subReference = rm.getReference(getName());
        } else
        {
            throw new IllegalArgumentException("Cannot resolve a " + getReferenceClass().getSimpleName()
                    + " Reference to a " + rm.getReferenceClass().getSimpleName());
        }
    }

    /**
     * Returns a Collection containing the single Object to which this
     * CDOMTransparentSingleRef refers.
     * <p>
     * The semantics of this method are defined solely by the semantics of the
     * underlying CDOMSingleRef. Ownership of the Collection returned by this
     * method may or may not be transferred to the calling object (check the
     * documentation of the underlying CDOMSingleRef).
     * <p>
     * Note that if you know this CDOMTransparentSingleRef is a CDOMSingleRef,
     * you are better off using resolvesTo() as the result will be much faster
     * than having to extract the object out of the Collection returned by this
     * method.
     *
     * @return A Collection containing the single Object to which this
     * CDOMTransparentSingleRef refers.
     */
    @Override
    public Collection<T> getContainedObjects()
    {
        return subReference.getContainedObjects();
    }

    /**
     * Returns the GroupingState for this CDOMTransparentSingleRef. The
     * GroupingState indicates how this CDOMTransparentSingleRef can be combined
     * with other PrimitiveChoiceFilters.
     *
     * @return The GroupingState for this CDOMTransparentSingleRef.
     */
    @Override
    public GroupingState getGroupingState()
    {
        return GroupingState.ALLOWS_UNION;
    }

    @Override
    public String getChoice()
    {
        return subReference == null ? null : subReference.getChoice();
    }

    @Override
    public void setChoice(String choice)
    {
        throw new IllegalStateException("Cannot set Choice on a Transparent Reference");
    }

    @Override
    public Class<T> getReferenceClass()
    {
        return refClass;
    }

    @Override
    public String getReferenceDescription()
    {
        return (subReference == null) ? (refClass.getSimpleName() + " " + getName())
                : subReference.getReferenceDescription();
    }

    @Override
    public String getPersistentFormat()
    {
        // TODO Auto-generated method stub
        return formatRepresentation;
    }
}
