/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.util;

/**
 * A IndirectConverter is an object designed to manage the convert certain forms of
 * objects.
 * 
 * @param <T>
 *            The type of object for which this Converter provides services
 */
public interface Converter<T>
{

	/**
	 * Converts the given String into an object of the type for which this Converter
	 * provides services.
	 * 
	 * Must throw a RuntimeException if the given String is not a properly formatted
	 * String for creation of the appropriate type of object. The actual type of
	 * RuntimeException is implementation dependent.
	 * 
	 * @param inputStr
	 *            The input String which should be converted into the appropriate object
	 * 
	 * @return An object of the type for which this Converter provides services
	 */
	public T convert(String inputStr);

}
