/*
 * Copyright 2017 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.format;

import pcgen.base.util.Tuple;

/**
 * A Dispatched is an object (related to Indirect) that can unconvert itself into two
 * separate Strings.
 * 
 * @see pcgen.base.util.Indirect
 */
public interface Dispatched
{
	/**
	 * Returns a Tuple containing String representation of the object and its
	 * supplementary information, as contained or referred to by this Dispatched.
	 * 
	 * It is expected that implementations of Dispatched will not return null from this
	 * method or as any of the contents of the Tuple. If for some reason this method
	 * cannot be executed successfully to return the String representations of an object,
	 * an IllegalStateException should be thrown indicating a prerequisite operation has
	 * not been performed to resolve the Dispatched prior to use.
	 * 
	 * @return A Tuple containing String representation of the object and its
	 *         supplementary information, as contained or referred to by this Dispatched
	 */
	public Tuple<String, String> unconvertSeparated();
}
