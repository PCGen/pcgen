/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.cdom.base.Loadable;

/**
 * An ObjectDatabase has the ability to return objects based on a name of that
 * object. It is effectively a form of read-only Two-key Map, although that does
 * not imply any Map-based constraints exist on ObjectDatabase.
 */
public interface ObjectDatabase
{

	/**
	 * Returns the object of the given name of the given class from the
	 * ObjectDatabase.
	 * 
	 * @param cl
	 *            The Class for which the object is to be returned
	 * @param name
	 *            The name of the object of the given class to be returned
	 * @return The object of the given name of the given class from the
	 *         ObjectDatabase
	 */
	public <T extends Loadable> T get(Class<T> cl, String name);

	/**
	 * Returns an Indirect referring to the object of the given name of the
	 * given class from the ObjectDatabase.
	 * 
	 * Note that the ObjectDatabase interface does not define any steps that may
	 * be necessary to allow the Indirect to be safely de-referenced.
	 * 
	 * @param cl
	 *            The Class for which the Indirect is to be returned
	 * @param name
	 *            The name of the object of the given class to be referred to by
	 *            the returned Indirect
	 * @return An Indirect referring to the object of the given name of the
	 *         given class from the ObjectDatabase
	 */
	public <T extends Loadable> Indirect<T> getIndirect(Class<T> cl, String name);

//	/**
//	 * Returns a String representation of the object provided. The String
//	 * representation is the name of the object that would be provided to the
//	 * get() method to return the object.
//	 * 
//	 * If the ObjectDatabase does not contain the given object, then the results
//	 * of this method are undefined.
//	 * 
//	 * @param o
//	 *            The Object which should be converted to the String
//	 *            representation.
//	 * @return A String representation of the object provided
//	 */
//	public String getName(Loadable o);

}
