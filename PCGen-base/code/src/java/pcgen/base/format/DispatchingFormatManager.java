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

import java.util.function.Function;

import pcgen.base.util.Converter;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Tuple;

/**
 * An DispatchingFormatManager produces an object but has the ability to deal with complex
 * format interactions that require dispatch (such as right-associative formats).
 * 
 * @param <T>
 *            The type of object for which this DispatchingFormatManager provides services
 */
public interface DispatchingFormatManager<T> extends FormatManager<T>
{
	/**
	 * Processes the given instructions and produces an Object as defined by the given
	 * Function.
	 * 
	 * @param processor
	 *            The Function to work on the items prepared by this
	 *            DispatchingFormatManager
	 * @param instructions
	 *            The instructions to be converted to an Object through dispatching
	 * @return The Object produced from the given instructions
	 * @param <OUT>
	 *            The format for the return value from the given Function.
	 */
	public <OUT> OUT convertViaDispatch(
		Function<FormatManager<T>, Converter<OUT>> processor, String instructions);

	/**
	 * Unconverts the given Object into two String - the first representing the primary
	 * Object and the second representing any trailing (right-associative) information
	 * from the provided Object.
	 * 
	 * Note that the combined String getFirst()+getSecond() must be able to be provided to
	 * convertViaDispatch or convertIndirectViaDispatch in order for a Class implementing
	 * this interface to meet the contract of this interface.
	 * 
	 * @param object
	 *            The Object to be unconverted by this DispatchingFormatManager.
	 * @return A Tuple containing a separated version of the String representation of the
	 *         given Object.
	 */
	public Tuple<String, String> unconvertSeparated(T object);
}
