/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.GroupingState;

/**
 * A CDOMDirectSingleRef is a CDOMReference to an object known at construction
 * of the CDOMDirectSingleRef. This is primarily used in special cases or where
 * construction is taking place after reference resolution has taken place.
 * (This can be used in an editor system to directy refer to a known,
 * constructed object)
 * 
 * @param <T>
 *            The Class of the underlying object contained by this
 *            CDOMDirectSingleRef
 */
public class CDOMDirectSingleRef<T extends CDOMObject> extends CDOMSingleRef<T>
{

	/**
	 * The object of the Class this CDOMDirectSingleRef represents
	 */
	private final T referencedObject;

	/**
	 * Constructs a new CDOMDirectSingleRef referring to the given object
	 * 
	 * @param obj
	 *            The object this CDOMDirectSingleRef will contain
	 */
	public CDOMDirectSingleRef(T obj)
	{
		super((Class<T>) obj.getClass(), obj.getLSTformat());
		referencedObject = obj;
	}

	/**
	 * Returns true if the given Object matches the object to which this
	 * CDOMDirectSingleRef refers.
	 * 
	 * @param obj
	 *            The object to be tested to see if it matches the object to
	 *            which this CDOMDirectSingleRef contains.
	 * @return true if the given Object is the object this CDOMDirectSingleRef
	 *         contains; false otherwise.
	 */
	@Override
	public boolean contains(T obj)
	{
		return referencedObject.equals(obj);
	}

	/**
	 * Returns the given Object this CDOMDirectSingleRef contains.
	 * 
	 * @return the given Object this CDOMDirectSingleRef contains.
	 */
	@Override
	public T resolvesTo()
	{
		return referencedObject;
	}

	/**
	 * Returns a representation of this CDOMDirectSingleRef, suitable for
	 * storing in an LST file.
	 * 
	 * Note that this will return the identifier of the underlying reference (of
	 * the types given at construction), often the "key" in LST terminology.
	 * 
	 * @return A representation of this CDOMDirectSingleRef, suitable for
	 *         storing in an LST file.
	 * @see pcgen.cdom.base.CDOMReference#getLSTformat()
	 */
	@Override
	public String getLSTformat()
	{
		return referencedObject.getLSTformat();
	}

	/**
	 * Returns true if this CDOMDirectSingleRef is equal to the given Object.
	 * Equality is defined as being another CDOMDirectSingleRef object with
	 * equal underlying object.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		return o instanceof CDOMDirectSingleRef
				&& referencedObject
						.equals(((CDOMDirectSingleRef<?>) o).referencedObject);
	}

	/**
	 * Returns the consistent-with-equals hashCode for this CDOMDirectSingleRef
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return getReferenceClass().hashCode();
	}

	/**
	 * Throws an exception. This method may not be called because a
	 * CDOMDirectSingleRef was resolved at construction.
	 * 
	 * @param obj
	 *            ignored
	 * @throws IllegalStateException
	 *             because a CDOMDirectSingleRef was resolved at construction.
	 */
	@Override
	public void addResolution(T obj)
	{
		throw new IllegalStateException("Cannot resolve a Direct Reference");
	}

	/**
	 * Returns a Collection containing the single Object to which this
	 * CDOMDirectSingleRef refers.
	 * 
	 * This method is reference-semantic, meaning that ownership of the
	 * Collection returned by this method is transferred to the calling object.
	 * Modification of the returned Collection should not result in modifying
	 * the CDOMDirectSingleRef, and modifying the CDOMDirectSingleRef after the
	 * Collection is returned should not modify the Collection.
	 * 
	 * Note that if you know this reference is a CDOMSingleRef, you are better
	 * off using resolvesTo() as the result will be much faster than having to
	 * extract the object out of the Collection returned by this method.
	 * 
	 * @return A Collection containing the single Object to which this
	 *         CDOMDirectSingleRef refers.
	 */
	@Override
	public Collection<T> getContainedObjects()
	{
		return Collections.singleton(referencedObject);
	}

	/**
	 * Static method to construct a new CDOMDirectSingleRef. This is provided
	 * mainly for convenience to allow less distraction of code with Generics.
	 * 
	 * @param <R>
	 *            The Class of the underlying object contained by the returned
	 *            CDOMDirectSingleRef
	 * @param obj
	 *            The object the returned CDOMDirectSingleRef will contain
	 * @return A new CDOMDirectSingleRef referring to the given object
	 */
	public static <R extends CDOMObject> CDOMDirectSingleRef<R> getRef(R obj)
	{
		return new CDOMDirectSingleRef<R>(obj);
	}

	/**
	 * Returns the GroupingState for this CDOMDirectSingleRef. The GroupingState
	 * indicates how this CDOMDirectSingleRef can be combined with other
	 * PrimitiveChoiceFilters.
	 * 
	 * @return The GroupingState for this CDOMDirectSingleRef.
	 */
	public GroupingState getGroupingState()
	{
		return GroupingState.ALLOWS_UNION;
	}
}
