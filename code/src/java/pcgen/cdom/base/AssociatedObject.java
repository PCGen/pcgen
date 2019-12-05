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
 * An AssociatedObject is an object which carries a set of Associations.
 * <p>
 * This is effectively a Map from an AssociationKey to a value, though the value
 * is "type safe" based on the Generic parameter on the AssocaitionKey.
 */
public interface AssociatedObject
{

    /**
     * Sets an Association (as defined by the given key) to the given value.
     * Overwrites any previous value associated with the given AssociationKey.
     *
     * @param <T>   The type of the AssociationKey and the Class of the object to
     *              be associated with the given AssociationKey.
     * @param key   The AssociationKey used to form the association with the given
     *              value
     * @param value The value to be associated with the given AssociationKey
     */
    <T> void setAssociation(AssociationKey<T> key, T value);

    /**
     * Returns the value associated with the given AssociationKey. Returns null
     * if this AssociatedObject contains no association for the given
     * AssociationKey.
     *
     * @param <T> The type of the AssociationKey and the Class of the object to
     *            be returned
     * @param key The AssociationKey for which the associated value is to be
     *            returned
     * @return The value associated with the given AssociationKey.
     */
    <T> T getAssociation(AssociationKey<T> key);

    /**
     * Returns a Collection of the AssociationKeys that are in this
     * AssociatedObject.
     * <p>
     * It is intended that classes which implement the AssociatedObject
     * interface will make this method value-semantic, meaning that ownership of
     * the Collection returned by this method will be transferred to the calling
     * object. Modification of the returned Collection should not result in
     * modifying the AssociatedObject, and modifying the AssocaitedObject after
     * the Collection is returned should not modify the Collection.
     * <p>
     * Note that it may be possible for an association to have a null value.
     * This method should include the AssociationKey for that association, if it
     * is present in the AssociatedObject, even if the value of the association
     * is null.
     *
     * @return a Collection of the AssociationKeys that are in this
     * AssociatedObject.
     */
    Collection<AssociationKey<?>> getAssociationKeys();

    /**
     * Returns true if this AssociatedObject has any associations.
     * <p>
     * Note that it may be possible for an association to have a null value.
     * This method should return true if the association is present, even if
     * null.
     *
     * @return true if this AssociatedObject has any associations; false
     * otherwise.
     */
    boolean hasAssociations();

}
