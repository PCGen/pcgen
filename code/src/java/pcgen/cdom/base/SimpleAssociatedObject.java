/*
 * Copyright 2007, 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.base;

import java.util.Collection;

import pcgen.cdom.enumeration.AssociationKey;

/**
 * SimpleAssociatedObject is a minimal implementation of the
 * AssociatedPrereqObject interface.
 */
public class SimpleAssociatedObject extends ConcretePrereqObject implements AssociatedPrereqObject
{

    /**
     * Helper AssociationSupport to support the Association functions of this
     * SimpleAssociatedObject
     */
    private final AssociationSupport assoc = new AssociationSupport();

    /**
     * Returns the value associated with the given AssociationKey. Returns null
     * if this SimpleAssociatedObject contains no association for the given
     * AssociationKey.
     *
     * @param <T>  The type of the AssociationKey and the Class of the object to
     *             be returned
     * @param name The AssociationKey for which the associated value is to be
     *             returned
     * @return The value associated with the given AssociationKey.
     */
    @Override
    public <T> T getAssociation(AssociationKey<T> name)
    {
        return assoc.getAssociation(name);
    }

    /**
     * Returns a Collection of the AssociationKeys that are in this
     * SimpleAssociatedObject.
     * <p>
     * This method is value-semantic, meaning that ownership of the Collection
     * returned by this method will be transferred to the calling object.
     * Modification of the returned Collection should not result in modifying
     * the SimpleAssociatedObject, and modifying the SimpleAssociatedObject
     * after the Collection is returned should not modify the Collection.
     * <p>
     * Note that it may be possible for an association to have a null value.
     * This method should include the AssociationKey for that association, if it
     * is present in the SimpleAssociatedObject, even if the value of the
     * association is null.
     *
     * @return a Collection of the AssociationKeys that are in this
     * SimpleAssociatedObject.
     */
    @Override
    public Collection<AssociationKey<?>> getAssociationKeys()
    {
        return assoc.getAssociationKeys();
    }

    /**
     * Returns true if this SimpleAssociatedObject has any associations.
     * <p>
     * Note that it may be possible for an association to have a null value.
     * This method should return true if the association is present, even if
     * null.
     *
     * @return true if this SimpleAssociatedObject has any associations; false
     * otherwise.
     */
    @Override
    public boolean hasAssociations()
    {
        return assoc.hasAssociations();
    }

    /**
     * Sets an Association (as defined by the given key) to the given value.
     * Overwrites any previous value associated with the given AssociationKey.
     *
     * @param <T>   The type of the AssociationKey and the Class of the object to
     *              be associated with the given AssociationKey.
     * @param name  The AssociationKey used to form the association with the given
     *              value
     * @param value The value to be associated with the given AssociationKey
     */
    @Override
    public <T> void setAssociation(AssociationKey<T> name, T value)
    {
        assoc.setAssociation(name, value);
    }

    @Override
    public int hashCode()
    {
        return assoc.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof SimpleAssociatedObject)
        {
            SimpleAssociatedObject other = (SimpleAssociatedObject) obj;
            return assoc.equals(other.assoc) && equalsPrereqObject(other);
        }
        return false;
    }
}
