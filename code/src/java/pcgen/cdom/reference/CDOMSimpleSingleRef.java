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
import java.util.Collections;

import pcgen.cdom.base.ClassIdentity;
import pcgen.cdom.enumeration.GroupingState;

/**
 * A CDOMSimpleSingleRef is a CDOMReference which is intended to contain a
 * single (non-categorized) object of a given Type for the Class this
 * CDOMSimpleSingleRef represents.
 *
 * @param <T> The Class of the underlying object contained by this
 *            CDOMSimpleSingleRef
 */
public class CDOMSimpleSingleRef<T> extends CDOMSingleRef<T>
{

    /**
     * The ClassIdentity that represents the objects contained in this CDOMSimpleSingleRef.
     */
    private final ClassIdentity<T> identity;

    /**
     * The object of the Class this CDOMSimpleSingleRef represents
     */
    private T referencedObject = null;

    /**
     * The specific choice (association) for the Ability this
     * CDOMSimpleSingleRef contains. May remain null if the given Ability does
     * not have a specific choice (or does not require a specific choice)
     */
    private String choice = null;

    /**
     * Constructs a new CDOMSimpleSingleRef for the given ClassIdentity and name.
     *
     * @param classIdentity The ClassIdentity of the underlying object contained by this
     *                      CDOMSimpleSingleRef.
     * @param key           An identifier of the object this CDOMSimpleSingleRef contains.
     */
    public CDOMSimpleSingleRef(ClassIdentity<T> classIdentity, String key)
    {
        super(key);
        identity = classIdentity;
    }

    /**
     * Returns true if the given Object matches the object to which this
     * CDOMSimpleSingleRef refers.
     * <p>
     * Note that the behavior of this class is undefined if the
     * CDOMSimpleSingleRef has not yet been resolved.
     *
     * @param item The object to be tested to see if it matches the object to
     *             which this CDOMSimpleSingleRef contains.
     * @return true if the given Object is the object this CDOMSimpleSingleRef
     * contains; false otherwise.
     * @throws IllegalStateException if this CDOMSimpleSingleRef has not been resolved
     */
    @Override
    public boolean contains(T item)
    {
        if (referencedObject == null)
        {
            throw new IllegalStateException("Cannot ask for contains: " + getReferenceClass().getName() + " Reference "
                    + getName() + " has not been resolved");
        }
        return referencedObject.equals(item);
    }

    /**
     * Returns the given Object this CDOMSimpleSingleRef contains.
     * <p>
     * Note that the behavior of this class is undefined if the
     * CDOMSimpleSingleRef has not yet been resolved.
     *
     * @return the given Object this CDOMSimpleSingleRef contains.
     * @throws IllegalStateException if this CDOMSimpleSingleRef has not been resolved
     */
    @Override
    public T get()
    {
        if (referencedObject == null)
        {
            throw new IllegalStateException(
                    "Cannot ask for resolution: Reference " + getName() + " has not been resolved");
        }
        return referencedObject;
    }

    /**
     * Check if the reference has been resolved yet. i.e. load of the object has been completed.
     *
     * @return true if the reference has been resolved, false if not.
     */
    @Override
    public boolean hasBeenResolved()
    {
        return referencedObject != null;
    }

    /**
     * Returns a representation of this CDOMSimpleSingleRef, suitable for
     * storing in an LST file.
     * <p>
     * Note that this will return the identifier of the underlying reference (of
     * the types given at construction), often the "key" in LST terminology.
     *
     * @param useAny Use any LST format.  Ignored in this specific implementation.
     * @return A representation of this CDOMSimpleSingleRef, suitable for
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
        if (obj instanceof CDOMSimpleSingleRef)
        {
            CDOMSimpleSingleRef<?> ref = (CDOMSimpleSingleRef<?>) obj;
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
     * Defines the object to which this CDOMSimpleSingleRef will refer.
     * <p>
     * Note that this method may only be called once - a CDOMSimpleSingleRef is
     * a one-shot resolution. If you are looking for a Reference which can be
     * redefined, check out objects that extend the TransparentReference
     * interface.
     *
     * @param item The object to which this CDOMSimpleSingleRef will refer.
     * @throws IllegalArgumentException if the given object for addition to this CDOMTypeRef is not
     *                                  of the class that this CDOMTypeRef represents
     * @throws IllegalStateException    if this method is called a second time
     * @throws NullPointerException     if the given object is null
     */
    @Override
    public void addResolution(T item)
    {
        if (referencedObject != null)
        {
            throw new IllegalStateException("Cannot resolve a Single Reference twice");
        }
        if (!item.getClass().equals(getReferenceClass()))
        {
            throw new IllegalArgumentException("Cannot resolve a " + getReferenceClass().getSimpleName()
                    + " Reference to a " + item.getClass().getSimpleName());
        }
        referencedObject = item;
    }

    /**
     * Returns a Collection containing the single Object to which this
     * CDOMSimpleSingleRef refers.
     * <p>
     * This method is reference-semantic, meaning that ownership of the
     * Collection returned by this method is transferred to the calling object.
     * Modification of the returned Collection should not result in modifying
     * the CDOMSimpleSingleRef, and modifying the CDOMSimpleSingleRef after the
     * Collection is returned should not modify the Collection.
     * <p>
     * Note that if you know this reference is a CDOMSingleRef, you are better
     * off using resolvesTo() as the result will be much faster than having to
     * extract the object out of the Collection returned by this method.
     * <p>
     * Note that the behavior of this class is undefined if the
     * CDOMSimpleSingleRef has not yet been resolved. (It may return null or an
     * empty Collection; that is implementation dependent)
     *
     * @return A Collection containing the single Object to which this
     * CDOMSimpleSingleRef refers.
     */
    @Override
    public Collection<T> getContainedObjects()
    {
        if (referencedObject == null)
        {
            throw new IllegalStateException("Cannot ask for contains: " + getReferenceClass().getName() + " Reference "
                    + getName() + " has not been resolved");
        }
        return Collections.singleton(referencedObject);
    }

    /**
     * Returns the GroupingState for this CDOMSimpleSingleRef. The GroupingState
     * indicates how this CDOMSimpleSingleRef can be combined with other
     * PrimitiveChoiceFilters.
     *
     * @return The GroupingState for this CDOMSimpleSingleRef.
     */
    @Override
    public GroupingState getGroupingState()
    {
        return GroupingState.ALLOWS_UNION;
    }

    @Override
    public void setChoice(String choice)
    {
        this.choice = choice;
    }

    @Override
    public String getChoice()
    {
        return choice;
    }

    @Override
    public Class<T> getReferenceClass()
    {
        return identity.getReferenceClass();
    }

    @Override
    public String getReferenceDescription()
    {
        return identity.getReferenceDescription() + " " + getName();
    }

    @Override
    public String getPersistentFormat()
    {
        return identity.getPersistentFormat();
    }
}
