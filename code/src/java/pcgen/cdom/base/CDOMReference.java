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
package pcgen.cdom.base;

import java.util.Collection;
import java.util.Objects;

import pcgen.base.util.ObjectContainer;
import pcgen.core.PlayerCharacter;

/**
 * A CDOMReference stores references to Objects. Often these are CDOMObjects,
 * but that is not strictly required.
 * <p>
 * The intent is for a CDOMReference to be created in order to identify that a
 * reference was made to an object. The CDOMReference can later be resolved to
 * identify the exact Objects to which the CDOMReference refers.
 * <p>
 * CDOMReference does not limit the quantity of object to which a single
 * CDOMReference can refer (it may be more than one).
 *
 * @param <T> The class of object this CDOMReference refers to.
 */
public abstract class CDOMReference<T> implements ObjectContainer<T>, PrimitiveCollection<T>
{

    /**
     * The name of this CDOMReference. This is the identifying information about
     * the CDOMReference, and may (or may not) be used to identify the objects
     * to which this CDOMReference resolves (will depend on the implementation).
     */
    private final String name;

    /**
     * Whether this CDOMReference requires a target. This is designed to be set by a
     * CONSUMER of a CDOMReference so that it can indicate back to a resolution system
     * whether certain information is required to properly resolve the CDOMReference.
     */
    private boolean requiresTarget = false;

    /**
     * Constructs a new CDOMReference with the given name.
     *
     * @param refName The name of this CDOMReference.
     * @throws IllegalArgumentException if the given name is null
     */
    protected CDOMReference(String refName)
    {
        name = Objects.requireNonNull(refName);
    }

    /**
     * Returns the name of this CDOMReference. Note that this name is suitable
     * for display, but it does not represent information that should be stored
     * in a persistent state (it is not sufficient information to reconstruct
     * this CDOMReference)
     *
     * @return The name of this CDOMReference.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Adds an object to be included in the Collection of objects to which this
     * CDOMReference refers.
     * <p>
     * Note that specific implementations may limit the number of times this
     * method may be called, and may throw an IllegalStateException if that
     * limit is exceeded. Note: The limit defined may be any value, including
     * zero (or "this is an optional method").
     *
     * @param item An object to be included in the Collection of objects to which
     *             this CDOMReference refers.
     */
    public abstract void addResolution(T item);

    /**
     * Returns the count of the number of objects included in the Collection of
     * Objects to which this CDOMReference refers.
     * <p>
     * Note that the behavior of this class is undefined if the CDOMReference
     * has not yet been resolved.
     *
     * @return The count of the number of objects included in the Collection of
     * Objects to which this CDOMReference refers.
     */
    public abstract int getObjectCount();

    /**
     * Returns a Collection containing the Objects to which this CDOMReference
     * refers.
     * <p>
     * It is intended that classes which extend CDOMReference will make this
     * method value-semantic, meaning that ownership of the Collection returned
     * by this method will be transferred to the calling object. Modification of
     * the returned Collection should not result in modifying the CDOMReference,
     * and modifying the CDOMReference after the Collection is returned should
     * not modify the Collection.
     * <p>
     * Note that the behavior of this class is undefined if the CDOMReference
     * has not yet been resolved. (It may return null, an empty Collection or
     * throw an exception; that is implementation dependent)
     *
     * @return A Collection containing the Objects to which this CDOMReference
     * refers.
     */
    @Override
    public abstract Collection<T> getContainedObjects();

    /**
     * Returns a String representation of this CDOMReference, primarily for
     * purposes of debugging. It is strongly advised that no dependency on this
     * method be created, as the return value may be changed without warning.
     *
     * @return A String representation of this CDOMReference
     */
    @Override
    public String toString()
    {
        return getClass().getSimpleName() + " " + getReferenceClass().getSimpleName() + " " + getName();
    }

    /**
     * Returns a description of the contents of this CDOMReference.
     * <p>
     * It is strongly advised that no dependency on this method be created, as it is
     * designed for human readability and the return value may be changed without warning.
     *
     * @return A description of the contents of this CDOMReference
     */
    public abstract String getReferenceDescription();

    @Override
    public <R> Collection<? extends R> getCollection(PlayerCharacter pc, Converter<T, R> c)
    {
        return c.convert(this);
    }

    /**
     * Returns the specific choice (association) for the Ability this
     * CDOMReference contains.
     *
     * @return The specific choice (association) for the Ability this
     * CDOMReference contains.
     */
    public abstract String getChoice();

    /**
     * Returns true if this CDOMReference requires a target. This is designed to be read
     * by a resolution system to identify whether the Name of this CDOMReference is
     * sufficiently detailed enough to indicate there is a target.
     *
     * @return true if this CDOMReference requires a target; false otherwise
     */
    public boolean requiresTarget()
    {
        return requiresTarget;
    }

    /**
     * Sets whether this CDOMReference requires that it contains a target. This is
     * designed to be set by a CONSUMER of a CDOMReference so that it can indicate back to
     * a resolution system whether certain information is required to properly resolve the
     * CDOMReference.
     *
     * @param required Whether this CDOMReference requires that it contains a target
     */
    public void setRequiresTarget(boolean required)
    {
        requiresTarget = required;
    }

    /**
     * Returns the persistent version of the ClassIdentity of the type of object that this
     * CDOMReference contains.
     *
     * @return The persistent version of the ClassIdentity of the type of object that this
     * CDOMReference contains
     */
    public abstract String getPersistentFormat();
}
