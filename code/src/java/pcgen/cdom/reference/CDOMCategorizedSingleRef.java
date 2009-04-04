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

import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.enumeration.GroupingState;

/**
 * A CDOMCategorizedSingleRef is a CDOMReference which is intended to contain a
 * single categorized object of a given Type for the Class this
 * CDOMCategorizedSingleRef represents.
 * 
 * @param <T>
 *            The Class of the underlying object contained by this
 *            CDOMCategorizedSingleRef
 */
public class CDOMCategorizedSingleRef<T extends CategorizedCDOMObject<T>>
		extends CDOMSingleRef<T> implements CategorizedCDOMReference<T>
{

	/**
	 * The object of the Class this CDOMCategorizedSingleRef represents
	 */
	private T referencedObject = null;

	/**
	 * The Category of the object of the Class this CDOMCategorizedSingleRef
	 * represents
	 */
	private final Category<T> category;

	/**
	 * Constructs a new CDOMCategorizedSingleRef for the given Class and name.
	 * 
	 * @param cl
	 *            The Class of the underlying object contained by this
	 *            CDOMCategorizedSingleRef.
	 * @param cat
	 *            The Category of the underlying object contained by this
	 *            CDOMCategorizedSingleRef.
	 * @param nm
	 *            An identifier of the object this CDOMCategorizedSingleRef
	 *            contains.
	 * @throws IllegalArgumentException
	 *             if the given Cagegory is null
	 */
	public CDOMCategorizedSingleRef(Class<T> cl, Category<T> cat, String nm)
	{
		super(cl, nm);
		if (cat == null)
		{
			throw new IllegalArgumentException(
					"Cannot built CDOMCategorizedSingleRef with null category");
		}
		category = cat;
	}

	/**
	 * Returns true if the given Object matches the object to which this
	 * CDOMCategorizedSingleRef refers.
	 * 
	 * Note that the behavior of this class is undefined if the
	 * CDOMCategorizedSingleRef has not yet been resolved.
	 * 
	 * @param obj
	 *            The object to be tested to see if it matches the object to
	 *            which this CDOMCategorizedSingleRef contains.
	 * @return true if the given Object is the object this
	 *         CDOMCategorizedSingleRef contains; false otherwise.
	 * @throws IllegalStateException
	 *             if this CDOMCategorizedSingleRef has not been resolved
	 */
	@Override
	public boolean contains(T obj)
	{
		if (referencedObject == null)
		{
			throw new IllegalStateException(
					"Cannot ask for contains: Reference has not been resolved");
		}
		return referencedObject.equals(obj);
	}

	/**
	 * Returns a representation of this CDOMCategorizedSingleRef, suitable for
	 * storing in an LST file.
	 * 
	 * Note that this will return the identifier of the underlying reference (of
	 * the types given at construction), often the "key" in LST terminology.
	 * 
	 * @return A representation of this CDOMCategorizedSingleRef, suitable for
	 *         storing in an LST file.
	 * @see pcgen.cdom.base.CDOMReference#getLSTformat()
	 */
	@Override
	public String getLSTformat()
	{
		return getName();
	}

	/**
	 * Returns the given Object this CDOMCategorizedSingleRef contains.
	 * 
	 * Note that the behavior of this class is undefined if the
	 * CDOMCategorizedSingleRef has not yet been resolved.
	 * 
	 * @return the given Object this CDOMCategorizedSingleRef contains.
	 * @throws IllegalStateException
	 *             if this CDOMCategorizedSingleRef has not been resolved
	 */
	@Override
	public T resolvesTo()
	{
		if (referencedObject == null)
		{
			throw new IllegalStateException(
					"Cannot ask for resolution: Reference has not been resolved");
		}
		return referencedObject;
	}

	/**
	 * Returns true if this CDOMCategorizedSingleRef is equal to the given
	 * Object. Equality is defined as being another CDOMCategorizedSingleRef
	 * object with equal Class represented by the reference and equal name of
	 * the underlying reference. This is NOT a deep .equals, in that the actual
	 * contents of this CDOMCategorizedSingleRef are not tested.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof CDOMCategorizedSingleRef)
		{
			CDOMCategorizedSingleRef<?> ref = (CDOMCategorizedSingleRef<?>) o;
			return getReferenceClass().equals(ref.getReferenceClass())
					&& getName().equals(ref.getName())
					&& category.equals(ref.category);
		}
		return false;
	}

	/**
	 * Returns the consistent-with-equals hashCode for this
	 * CDOMCategorizedSingleRef
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return getReferenceClass().hashCode() ^ getName().hashCode();
	}

	/**
	 * Returns the Category of the object this CDOMCategorizedSingleRef contains
	 * 
	 * @return the Category of the object this CDOMCategorizedSingleRef contains
	 */
	public Category<T> getCDOMCategory()
	{
		return category;
	}

	/**
	 * Defines the object to which this CDOMCategorizedSingleRef will refer.
	 * 
	 * Note that this method may only be called once - a
	 * CDOMCategorizedSingleRef is a one-shot resolution. If you are looking for
	 * a Reference which can be redefined, check out objects that extend the
	 * TransparentReference interface.
	 * 
	 * @param obj
	 *            The object to which this CDOMCategorizedSingleRef refers.
	 * @throws IllegalArgumentException
	 *             if the given object for addition to this
	 *             CDOMCategorizedSingleRef is not of the class that this
	 *             CDOMCategorizedSingleRef represents or not of the Category
	 *             that this CDOMCategorizedSingleRef represents
	 * @throws IllegalStateException
	 *             if this method is called a second time
	 * @throws NullPointerException
	 *             if the given object is null
	 */
	@Override
	public void addResolution(T obj)
	{
		if (referencedObject != null)
		{
			throw new IllegalStateException(
					"Cannot resolve a Single Reference twice");
		}
		if (!obj.getClass().equals(getReferenceClass()))
		{
			throw new IllegalArgumentException("Cannot resolve a "
					+ getReferenceClass().getSimpleName() + " Reference to a "
					+ obj.getClass().getSimpleName());
		}
		if (!category.equals(obj.getCDOMCategory()))
		{
			Category<T> parent = category.getParentCategory();
			if (parent != null && !parent.equals(obj.getCDOMCategory()))
			{
				throw new IllegalArgumentException("Cannot resolve "
						+ getReferenceClass().getSimpleName() + " " + getName()
						+ obj.getCDOMCategory() + " Reference to category "
						+ category);
			}
		}
		referencedObject = obj;
	}

	/**
	 * Returns a Collection containing the single Object to which this
	 * CDOMCategorizedSingleRef refers.
	 * 
	 * This method is reference-semantic, meaning that ownership of the
	 * Collection returned by this method is transferred to the calling object.
	 * Modification of the returned Collection should not result in modifying
	 * the CDOMCategorizedSingleRef, and modifying the CDOMCategorizedSingleRef
	 * after the Collection is returned should not modify the Collection.
	 * 
	 * Note that if you know this reference is a CDOMSingleRef, you are better
	 * off using resolvesTo() as the result will be much faster than having to
	 * extract the object out of the Collection returned by this method.
	 * 
	 * Note that the behavior of this class is undefined if the
	 * CDOMCategorizedSingleRef has not yet been resolved. (It may return null
	 * or an empty Collection; that is implementation dependent)
	 * 
	 * @return A Collection containing the single Object to which this
	 *         CDOMCategorizedSingleRef refers.
	 */
	@Override
	public Collection<T> getContainedObjects()
	{
		return Collections.singleton(referencedObject);
	}

	/**
	 * Returns the GroupingState for this CDOMCategorizedSingleRef. The
	 * GroupingState indicates how this CDOMCategorizedSingleRef can be combined
	 * with other PrimitiveChoiceFilters.
	 * 
	 * @return The GroupingState for this CDOMCategorizedSingleRef.
	 */
	public GroupingState getGroupingState()
	{
		return GroupingState.ALLOWS_UNION;
	}
}