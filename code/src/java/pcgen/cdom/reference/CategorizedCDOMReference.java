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
 * A CategorizedCDOMReference is an object that contains one or more references
 * to Categorized CDOMObjects.
 * 
 * @see pcgen.cdom.base.Category
 * 
 * CategorizedCDOMReference does not limit the quantity of object to which a
 * single CategorizedCDOMReference can refer (it may be more than one).
 * 
 * @param <T>
 *            The class of object underlying this CategorizedCDOMReference.
 */
public interface CategorizedCDOMReference<T extends Loadable & CategorizedCDOMObject<T>>
{

	/**
	 * Returns the Category of the object this CategorizedCDOMReference contains
	 * 
	 * @return the Category of the object this CategorizedCDOMReference contains
	 */
	public Category<T> getCDOMCategory();

	/**
	 * Returns a representation of this CategorizedCDOMReference, suitable for
	 * storing in an LST file.
	 * 
	 * Note that this will return the identifier of the underlying reference (of
	 * the types given at construction), often the "key" in LST terminology.
	 * 
	 * @return A representation of this CategorizedCDOMReference, suitable for
	 *         storing in an LST file.
	 */
	public String getLSTformat(boolean useAny);

	/**
	 * Returns the name of this CategorizedCDOMReference. Note that this name is
	 * suitable for display, but it does not represent information that should
	 * be stored in a persistent state (it is not sufficient information to
	 * reconstruct this CategorizedCDOMReference)
	 * 
	 * @return The name of this CategorizedCDOMReference.
	 */
	public String getName();

	/**
	 * Adds an object to be included in the Collection of objects to which this
	 * CategorizedCDOMReference refers.
	 * 
	 * Note that specific implementations may limit the number of times this
	 * method may be called, and may throw an IllegalStateException if that
	 * limit is exceeded.
	 * 
	 * @param item
	 *            an object to be included in the Collection of objects to which
	 *            this CategorizedCDOMReference refers.
	 */
	public void addResolution(T item);

	public String getLSTCategory();

}
