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

import java.util.function.Supplier;

/**
 * An Indirect is a container for storing an object that is accessed indirectly
 * (meaning the get() method of the Reference will be called) and has a String
 * representation.
 * 
 * This is often necessary because some objects cannot be known when someone
 * wants to store a reference to the object, so it must be referred to (and
 * references to those objects passed to others) prior to construction or
 * discovery. In addition, systems may wish to refer back to the String
 * representation for persistent storage.
 * 
 * @param <T>
 *            The type of object that the Indirect contains
 */
public interface Indirect<T> extends Supplier<T>
{

	/**
	 * Returns a String representation of the object contained or referred to by
	 * this Indirect.
	 * 
	 * It is expected that implementations of Indirect will not return null from
	 * this method. If for some reason this method cannot be executed
	 * successfully to return the String representation of an object, an
	 * IllegalStateException should be thrown indicating a prerequisite
	 * operation has not been performed to resolve the Indirect prior to use.
	 * 
	 * @return A String representation of the object contained or referred to by
	 *         this Indirect
	 */
	public String getUnconverted();

}
