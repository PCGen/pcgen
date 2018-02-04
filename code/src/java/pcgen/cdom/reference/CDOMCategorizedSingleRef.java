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

import pcgen.cdom.base.Categorized;
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
public class CDOMCategorizedSingleRef<T extends Categorized<T>> extends
		CDOMSingleRef<T> implements CategorizedCDOMReference<T>
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
	 * The specific choice (association) for the Ability this
	 * CDOMCategorizedSingleRef contains. May remain null if the given Ability
	 * does not have a specific choice (or does not require a specific choice)
	 */
	private String choice = null;

	/**
	 * Constructs a new CDOMCategorizedSingleRef for the given Class and name.
	 * 
	 * @param objClass
	 *            The Class of the underlying object contained by this
	 *            CDOMCategorizedSingleRef.
	 * @param cat
	 *            The Category of the underlying object contained by this
	 *            CDOMCategorizedSingleRef.
	 * @param key
	 *            An identifier of the object this CDOMCategorizedSingleRef
	 *            contains.
	 * @throws IllegalArgumentException
	 *             if the given Cagegory is null
	 */
	public CDOMCategorizedSingleRef(Class<T> objClass, Category<T> cat,
			String key)
	{
		super(objClass, key);
		if (cat == null)
		{
			throw new IllegalArgumentException(
					"Cannot build CDOMCategorizedSingleRef with null category");
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
	 * @param item
	 *            The object to be tested to see if it matches the object to
	 *            which this CDOMCategorizedSingleRef contains.
	 * @return true if the given Object is the object this
	 *         CDOMCategorizedSingleRef contains; false otherwise.
	 * @throws IllegalStateException
	 *             if this CDOMCategorizedSingleRef has not been resolved
	 */
	@Override
	public boolean contains(T item)
	{
		if (referencedObject == null)
		{
			throw new IllegalStateException(
					"Cannot ask for contains: Reference has not been resolved");
		}
		return referencedObject.equals(item);
	}

	/**
	 * Returns a representation of this CDOMCategorizedSingleRef, suitable for
	 * storing in an LST file.
	 * 
	 * Note that this will return the identifier of the underlying reference (of
	 * the types given at construction), often the "key" in LST terminology.
	 * 
	 * @param useAny
	 * 		   Use any LST format.  Ignored in this specific implementation.
	 * 
	 * @return A representation of this CDOMCategorizedSingleRef, suitable for
	 *         storing in an LST file.
	 * @see pcgen.cdom.base.CDOMReference#getLSTformat(boolean)
	 */
	@Override
	public String getLSTformat(boolean useAny)
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
	public T get()
	{
		if (referencedObject == null)
		{
			throw new IllegalStateException(
					"Cannot ask for resolution: Reference has not been resolved");
		}
		return referencedObject;
	}

	/** 
	 * Check if the reference has been resolved yet. i.e. load of the object has been completed.
	 * @return true if the reference has been resolved, false if not.
	 */
	@Override
	public boolean hasBeenResolved()
	{
		return referencedObject != null;
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
	public boolean equals(Object obj)
	{
		if (obj instanceof CDOMCategorizedSingleRef)
		{
			CDOMCategorizedSingleRef<?> ref = (CDOMCategorizedSingleRef<?>) obj;
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
	@Override
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
	 * @param item
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
	public void addResolution(T item)
	{
		if (referencedObject != null)
		{
			throw new IllegalStateException(
					"Cannot resolve a Single Reference twice");
		}
		if (!item.getClass().equals(getReferenceClass()))
		{
			throw new IllegalArgumentException("Cannot resolve a "
					+ getReferenceClass().getSimpleName() + " Reference to a "
					+ item.getClass().getSimpleName());
		}
		if (!category.equals(item.getCDOMCategory()))
		{
			Category<T> parent = category.getParentCategory();
			if (parent != null && !parent.equals(item.getCDOMCategory()))
			{
				throw new IllegalArgumentException("Cannot resolve "
						+ getReferenceClass().getSimpleName() + " " + getName()
						+ item.getCDOMCategory() + " Reference to category "
						+ category);
			}
		}
		referencedObject = item;
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
	@Override
	public GroupingState getGroupingState()
	{
		return GroupingState.ALLOWS_UNION;
	}

	@Override
	public void setChoice(String c)
	{
		choice = c;
	}

	@Override
	public String getChoice()
	{
		return choice;
	}

	@Override
	public String getLSTCategory()
	{
		return category.getKeyName();
	}
}
