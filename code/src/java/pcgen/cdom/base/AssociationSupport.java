/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import pcgen.cdom.enumeration.AssociationKey;

/**
 * AssociationSupport is a helper class that can be used by classes that
 * implement the AssociatedObject interface.
 * 
 * An instance of AssociationSupport can be used as a delegate for the methods
 * from the AssociatedObject interface.
 */
public class AssociationSupport implements AssociatedObject
{
	/**
	 * The Map used to store associations
	 */
	private Map<AssociationKey<?>, Object> associationMap;

	/**
	 * Sets an Association (as defined by the given key) to the given value.
	 * Overwrites any previous value associated with the given AssociationKey.
	 * 
	 * @param <T>
	 *            The type of the AssociationKey and the Class of the object to
	 *            be associated with the given AssociationKey.
	 * @param key
	 *            The AssociationKey used to form the association with the given
	 *            value
	 * @param value
	 *            The value to be associated with the given AssociationKey
	 */
	@Override
	public <T> void setAssociation(AssociationKey<T> key, T value)
	{
		if (associationMap == null)
		{
			associationMap = new HashMap<>();
		}
		associationMap.put(key, value);
	}

	/**
	 * Returns the value associated with the given AssociationKey. Returns null
	 * if this AssociatedObject contains no association for the given
	 * AssociationKey.
	 * 
	 * @param <T>
	 *            The type of the AssociationKey and the Class of the object to
	 *            be returned
	 * @param key
	 *            The AssociationKey for which the associated value is to be
	 *            returned
	 * @return The value associated with the given AssociationKey.
	 */
	@Override
	public <T> T getAssociation(AssociationKey<T> key)
	{
		return (associationMap == null ? null : key.cast(associationMap.get(key)));
	}

	/**
	 * Returns a Collection of the AssociationKeys that are in this
	 * AssociatedObject.
	 * 
	 * This method is value-semantic, meaning that ownership of the Collection
	 * returned by this method will be transferred to the calling object.
	 * Modification of the returned Collection should not result in modifying
	 * the AssociatedObject, and modifying the AssocaitedObject after the
	 * Collection is returned should not modify the returned Collection.
	 * 
	 * Note that it may be possible for an association to have a null value.
	 * This method should include the AssociationKey for that association, if it
	 * is present in the AssociatedObject, even if the value of the association
	 * is null.
	 * 
	 * @return a Collection of the AssociationKeys that are in this
	 *         AssociatedObject.
	 */
	@Override
	public Collection<AssociationKey<?>> getAssociationKeys()
	{
		return new HashSet<>(associationMap.keySet());
	}

	/**
	 * Returns true if this AssociatedObject has any associations.
	 * 
	 * Note that it may be possible for an association to have a null value.
	 * This method should return true if the association is present, even if
	 * null.
	 * 
	 * @return true if this AssociatedObject has any associations; false
	 *         otherwise.
	 */
	@Override
	public boolean hasAssociations()
	{
		return associationMap != null && !associationMap.isEmpty();
	}

	/**
	 * Returns the consistent-with-equals hashCode for this AssociationSupport
	 */
	@Override
	public int hashCode()
	{
		return associationMap == null ? 0 : associationMap.size();
	}

	/**
	 * Returns true if this AssociationSupport is equal to the given Object.
	 * Equality is defined as being another AssociationSupport object with equal
	 * keys associated with equal values. Equality of the keys and values is as
	 * equality is defined by those objects in their .equals(java.lang.Object)
	 * method.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (obj instanceof AssociationSupport other)
		{
			if (associationMap == null || associationMap.isEmpty())
			{
				return (other.associationMap == null) || other.associationMap.isEmpty();
			}
			return associationMap.equals(other.associationMap);
		}
		return false;
	}
}
