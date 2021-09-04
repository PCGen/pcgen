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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.ClassIdentity;
import pcgen.cdom.enumeration.GroupingState;

/**
 * A CDOMTypeRef is a CDOMReference which is intended to contain objects of a
 * given Type for the Class this CDOMTypeRef represents.
 * 
 * @param <T>
 *            The Class of the underlying objects contained by this CDOMTypeRef
 */
public final class CDOMTypeRef<T> extends CDOMGroupRef<T>
{

	/**
	 * The ClassIdentity that represents the objects contained in this CDOMTypeRef.
	 */
	private final ClassIdentity<T> identity;

	/**
	 * The objects of the Class this CDOMTypeRef represents
	 */
	private List<T> referencedList = null;

	/**
	 * The Types of objects this CDOMTypeRef contains
	 */
	private final String[] types;

	/**
	 * Constructs a new CDOMTypeRef for the given Class to be represented by
	 * this CDOMTypeRef and the given types.
	 * 
	 * @param objClass
	 *            The Class of the underlying objects contained by this
	 *            reference.
	 * @param typeArray
	 *            An array of the Types of objects this CDOMTypeRef contains.
	 */
	public CDOMTypeRef(ClassIdentity<T> objClass, String[] typeArray)
	{
		super(objClass.getReferenceDescription() + " " + Arrays.deepToString(typeArray));
		types = Arrays.copyOf(typeArray, typeArray.length);
		identity = objClass;
	}

	/**
	 * Returns a representation of this CDOMTypeRef, suitable for storing in an
	 * LST file.
	 * 
	 * Note that this will return the identifier of the "Type" reference (of the
	 * types given at construction), not an expanded list of the contents of
	 * this CDOMTypeRef.
	 * 
	 * @return A representation of this CDOMTypeRef, suitable for storing in an
	 *         LST file.
	 */
	@Override
	public String getLSTformat(boolean useAny)
	{
		return "TYPE=" + StringUtil.join(types, ".");
	}

	/**
	 * Returns true if the given Object is included in the Collection of Objects
	 * to which this CDOMTypeRef refers.
	 * 
	 * Note that the behavior of this class is undefined if the CDOMTypeRef has
	 * not yet been resolved.
	 * 
	 * @param item
	 *            The object to be tested to see if it is referred to by this
	 *            CDOMTypeRef.
	 * @return true if the given Object is included in the Collection of Objects
	 *         to which this CDOMTypeRef refers; false otherwise.
	 * @throws IllegalStateException
	 *             if the CDOMTypeRef has not been resolved
	 */
	@Override
	public boolean contains(T item)
	{
		if (referencedList == null)
		{
			throw new IllegalStateException("Cannot ask for contains: Reference has not been resolved");
		}
		return referencedList.contains(item);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof CDOMTypeRef<?> ref)
		{
			return getReferenceClass().equals(ref.getReferenceClass()) && getName().equals(ref.getName())
				&& Arrays.deepEquals(types, ref.types);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return getReferenceClass().hashCode() ^ getName().hashCode();
	}

	/**
	 * Adds an object to be included in the Collection of objects to which this
	 * CDOMTypeRef refers.
	 * 
	 * @param item
	 *            An object to be included in the Collection of objects to which
	 *            this CDOMTypeRef refers.
	 * @throws IllegalArgumentException
	 *             if the given object for addition to this CDOMTypeRef is not
	 *             of the class that this CDOMTypeRef represents
	 * @throws NullPointerException
	 *             if the given object is null
	 */
	@Override
	public void addResolution(T item)
	{
		if (item.getClass().equals(getReferenceClass()))
		{
			if (referencedList == null)
			{
				referencedList = new ArrayList<>();
			}
			referencedList.add(item);
		}
		else
		{
			throw new IllegalArgumentException("Cannot resolve a " + getReferenceClass().getSimpleName()
				+ " Reference to a " + item.getClass().getSimpleName());
		}
	}

	/**
	 * Returns the count of the number of objects included in the Collection of
	 * Objects to which this CDOMTypeRef refers.
	 * 
	 * Note that the behavior of this class is undefined if the CDOMTypeRef has
	 * not yet been resolved.
	 * 
	 * @return The count of the number of objects included in the Collection of
	 *         Objects to which this CDOMTypeRef refers.
	 */
	@Override
	public int getObjectCount()
	{
		return referencedList == null ? 0 : referencedList.size();
	}

	/**
	 * Returns a Collection containing the Objects to which this CDOMTypeRef
	 * refers.
	 * 
	 * This method is reference-semantic, meaning that ownership of the
	 * Collection returned by this method is transferred to the calling object.
	 * Modification of the returned Collection should not result in modifying
	 * the CDOMTypeRef, and modifying the CDOMTypeRef after the Collection is
	 * returned should not modify the Collection.
	 * 
	 * Note that the behavior of this class is undefined if the CDOMTypeRef has
	 * not yet been resolved. (It may return null or an empty Collection; that
	 * is implementation dependent)
	 * 
	 * @return A Collection containing the Objects to which this CDOMTypeRef
	 *         refers.
	 */
	@Override
	public Collection<T> getContainedObjects()
	{
		if (referencedList == null)
		{
			throw new IllegalStateException("Cannot ask for contained objects: Reference has not been resolved");
		}
		return Collections.unmodifiableList(referencedList);
	}

	/**
	 * Returns the GroupingState for this CDOMTypeRef. The GroupingState
	 * indicates how this CDOMTypeRef can be combined with other
	 * PrimitiveChoiceFilters.
	 * 
	 * @return The GroupingState for this CDOMTypeRef.
	 */
	@Override
	public GroupingState getGroupingState()
	{
		return GroupingState.ANY;
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
		return identity.getReferenceDescription() + " of TYPE=" + Arrays.asList(types);
	}

	@Override
	public String getPersistentFormat()
	{
		return identity.getPersistentFormat();
	}
}
