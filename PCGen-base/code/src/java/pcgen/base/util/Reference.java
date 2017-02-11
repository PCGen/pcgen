/*
 * Copyright 2014-16 (C) Tom Parker <thpr@users.sourceforge.net>
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

/**
 * A Reference is a container for storing an object that is accessed indirectly
 * (meaning the get() method of the Reference will be called).
 * 
 * This is often necessary because some objects cannot be known when someone
 * wants to store a reference to the object, so it must be referred to (and
 * references to those objects passed to others) prior to construction or
 * discovery.
 * 
 * Reference provides a method for handing off a object that can later be
 * resolved, with the get() method used at runtime to get the underlying object.
 * 
 * @param <T>
 *            The type of object that the Reference contains
 */
@FunctionalInterface
public interface Reference<T>
{
	/**
	 * Returns the object contained or referred to by this Reference.
	 * 
	 * Objects implementing the Reference interface should choose and document
	 * one of two behaviors. Implementations may return null, or an
	 * IllegalStateException should be thrown indicating a prerequisite
	 * operation has not been performed to resolve the Reference prior to use.
	 * 
	 * @return The object contained or referred to by this Reference
	 */
	public T get();
}
