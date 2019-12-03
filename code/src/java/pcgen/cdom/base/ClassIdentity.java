/*
 * Copyright 2012-18 (C) Tom Parker <thpr@users.sourceforge.net>
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

/**
 * ClassIdentity is a system used within PCGen to uniquely identify items. This is because
 * equality and other items cannot sufficiently be described by Class. (Abilities are
 * unique by Category, for example).
 * 
 * This consolidates equality by Class and Equality by Category into one object, so that
 * it can quickly be checked or leveraged in such a way as to avoid unique situations in
 * wide ranging sections of code for support of Categorized objects.
 */
public interface ClassIdentity<T>
{

	/**
	 * Returns the name of this ClassIdentity.
	 * 
	 * @return The name of this ClassIdentity
	 */
    String getName();

	/**
	 * Returns the underlying Class used as a component of determining uniqueness for this
	 * ClassIdentity.
	 * 
	 * @return The underlying Class used as a component of determining uniqueness for this
	 *         ClassIdentity
	 */
    Class<T> getReferenceClass();

	/**
	 * Returns a new instance of the object represented by this ClassIdentity. Note that
	 * any initialization necessary to indicate other state that defines ClassIdentity
	 * (such as Category) should be completed before the object is returned from this
	 * method.
	 * 
	 * @return A new instance of the object represented by this ClassIdentity
	 */
    T newInstance();

	/**
	 * Returns a description of the contents of this ClassIdentity.
	 * 
	 * It is strongly advised that no dependency on this method be created, as it is
	 * designed for human readability and the return value may be changed without warning.
	 * 
	 * @return A description of the contents of this ClassIdentity
	 */
    String getReferenceDescription();

	/**
	 * Returns true if the given object is a member of the type of object represented by
	 * this ClassIdentity.
	 * 
	 * @param item
	 *            The item to be checked to see if it is a member of the type of object
	 *            represented by this ClassIdentity
	 * @return true if the given object is a member of the type of object represented by
	 *         this ClassIdentity; false otherwise
	 */
    boolean isMember(T item);

	/**
	 * Returns the persistent format for this ClassIdentity.
	 * 
	 * @return The persistent format for this ClassIdentity
	 */
    String getPersistentFormat();

}
