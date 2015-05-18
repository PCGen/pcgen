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

import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Loadable;

/**
 * A CDOMTransparentCategorizedSingleRef is a CDOMReference which is intended to
 * contain a another CDOMSingleRef, to which the
 * CDOMTransparentCategorizedSingleRef will delegate behavior.
 * 
 * A CDOMTransparentCategorizedSingleRef, unlike many CDOMReference objects, can
 * be cleared, and the underlying CDOMSingleRef can be changed.
 * 
 * @see TransparentReference for a description of cases in which
 *      TransparentReferences like CDOMTransparentCategorizedSingleRef are
 *      typically used
 * 
 * @param <T>
 *            The Class of the underlying object contained by this
 *            CDOMTransparentCategorizedSingleRef
 */
// Should be T extends CategorizedCDOMObject<T>
public class CDOMTransparentCategorizedSingleRef<T extends Loadable & CategorizedCDOMObject<T>>
		extends CDOMTransparentSingleRef<T> implements TransparentReference<T>,
		CategorizedCDOMReference<T>
{
	/**
	 * The Category of the object of the Class this CDOMCategorizedSingleRef
	 * represents
	 */
	private final String category;

	/**
	 * Constructs a new CDOMTransparentCategorizedSingleRef for the given Class
	 * and name.
	 * 
	 * @param objClass
	 *            The Class of the underlying object contained by this
	 *            CDOMTransparentCategorizedSingleRef.
	 * @param cat
	 *            The Category of objects that this
	 *            CDOMTransparentCategorizedSingleRef will reference.
	 * @param key
	 *            An identifier of the object this
	 *            CDOMTransparentCategorizedSingleRef contains.
	 */
	public CDOMTransparentCategorizedSingleRef(Class<T> objClass, String cat,
			String key)
	{
		super(objClass, key);
		if (cat == null)
		{
			throw new IllegalArgumentException(
					"Cannot built CDOMTransparentCategorizedSingleRef with null category");
		}
		category = cat;
	}

	@Override
	public int hashCode()
	{
		return getReferenceClass().hashCode() ^ getName().hashCode();
	}

	/**
	 * Returns true if this CDOMTransparentCategorizedSingleRef is equal to the
	 * given Object. Equality is defined as being another
	 * CDOMTransparentCategorizedSingleRef object with equal Class represented
	 * by the reference and equal name of the underlying reference. This is NOT
	 * a deep .equals, in that neither the actual contents of this
	 * CDOMTransparentCategorizedSingleRef nor the underlying CDOMSingleRef are
	 * tested.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof CDOMTransparentCategorizedSingleRef)
		{
			CDOMTransparentCategorizedSingleRef<?> ref = (CDOMTransparentCategorizedSingleRef<?>) obj;
			return getReferenceClass().equals(ref.getReferenceClass())
					&& category.equals(ref.getLSTCategory())
					&& getName().equals(ref.getName());
		}
		return false;
	}

	/**
	 * Returns the Category of the object this
	 * CDOMTransparentCategorizedSingleRef contains
	 * 
	 * @return the Category of the object this
	 *         CDOMTransparentCategorizedSingleRef contains
	 */
	@Override
	public String getLSTCategory()
	{
		return category;
	}

	@Override
	public Category<T> getCDOMCategory()
	{
		throw new UnsupportedOperationException("Don't have resolved category");
	}
}
