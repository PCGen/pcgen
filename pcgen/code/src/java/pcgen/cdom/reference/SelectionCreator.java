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

import pcgen.cdom.base.Loadable;


public interface SelectionCreator<T extends Loadable>
{
	/**
	 * Gets a reference to the Class or Class/Context provided by this
	 * SelectionCreator. The reference will be a reference to the object
	 * identified by the given key.
	 * 
	 * @param key
	 *            The key used to identify the object to which the returned
	 *            CDOMReference will refer.
	 * @return A CDOMReference that refers to the object identified by the given
	 *         key
	 */
	public CDOMSingleRef<T> getReference(String key);

	/**
	 * Gets a reference to the Class or Class/Context provided by this
	 * SelectionCreator. The reference will be a reference to the objects
	 * identified by the given types.
	 * 
	 * @param types
	 *            An array of the types of objects to which the returned
	 *            CDOMReference will refer.
	 * @return A CDOMReference which is intended to contain objects of a given
	 *         Type for the Class or Class/Context this SelectionCreator
	 *         represents.
	 */
	public CDOMGroupRef<T> getTypeReference(String... types);

	/**
	 * Returns a CDOMReference for the given Class or Class/Context provided by
	 * this SelectionCreator.
	 * 
	 * @return A CDOMReference which is intended to contain all the objects of
	 *         the Class or Class/Context this SelectionCreator represents.
	 */
	public CDOMGroupRef<T> getAllReference();

	/**
	 * The class of object this SelectionCreator represents.
	 * 
	 * @return The class of object this SelectionCreator represents.
	 */
	public Class<T> getReferenceClass();
}
