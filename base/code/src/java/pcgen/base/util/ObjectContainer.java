/*
 * Copyright 2014-15 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.util;

import java.util.Collection;

/**
 * An ObjectContainer is a container for storing one or more objects that are
 * accessed indirectly (meaning the getContainedObjects() method of the
 * ObjectContainer will be called at Runtime). This is often necessary because
 * some objects cannot be known during LST load, but must be referred to (and
 * references to those objects passed to others) at LST load.
 * 
 * Consider as an example, how TYPE= works, in that a TYPE= reference can be
 * made in a CHOOSE or other location, and we cannot guarantee that we have
 * loaded all of the objects (and certainly have not evaluated the TYPE: token
 * on them)
 * 
 * The ObjectContainer provides a method for handing off a reference (the
 * ObjectContainer) that can later be resolved, with the getContainedObjects()
 * method used at runtime to get the underlying objects.
 * 
 * @param <T>
 *            The type of object that the ObjectContainer contains
 */
public interface ObjectContainer<T>
{

	/**
	 * Returns true if this ObjectContainer contains the given Object.
	 * 
	 * Implementations of this method should be value-semantic in that no
	 * changes should be made to the objects passed into the method.
	 * 
	 * @param obj
	 *            The object to check if it is contained by this ObjectContainer
	 * @return true if this ObjectContainer contains the given Object; false
	 *         otherwise
	 */
	public boolean contains(T obj);

	/**
	 * Returns the Class indicating the type of object that this ObjectContainer
	 * contains.
	 * 
	 * @return the Class indicating the type of object that this ObjectContainer
	 *         contains
	 */
	Class<T> getReferenceClass();

	/**
	 * Returns a copy of the collection of objects contained in this
	 * ObjectContainer.
	 * 
	 * This method should never return null.
	 * 
	 * It is intended that classes which extend ObjectContainer will make this
	 * method value-semantic, meaning that ownership of the Collection returned
	 * by this method will be transferred to the calling object. Modification of
	 * the returned Collection should not result in modifying the
	 * ObjectContainer, and modifying the ObjectContainer after the Collection
	 * is returned should not modify the Collection.
	 * 
	 * @return A copy of the collection of objects contained in this
	 *         ObjectContainer
	 */
	Collection<? extends T> getContainedObjects();

	/**
	 * Returns a representation of this ObjectContainer, suitable for storing in
	 * an LST file.
	 * 
	 * @param useAny
	 *            indicates if the "ALL/ANY" collection should use "ANY"
	 * @return A representation of this ObjectContainer, suitable for storing in
	 *         an LST file.
	 */
	String getLSTformat(boolean useAny);

}
