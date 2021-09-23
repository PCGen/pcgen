/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.Optional;

/**
 * A FormatManager is an object designed to manage the creation and
 * serialization of certain forms of objects. This serialization is in a
 * human-readable format, as defined by the FormatManager.
 * 
 * The contract on a FormatManager is that any String produced by unconvert must
 * be able to be fed into one or more of convert, convertIndirect, or
 * convertObjectContainer without throwing an exception. Also, any object
 * produced by convert, by the Indirect returned from convertIndirect, or by the
 * ObjectContainer returned by convertObjectContainer must be able to be fed
 * into unconvert and produce a human-readable serialization of the object.
 * 
 * @param <T>
 *            The type of object for which this FormatManager provides services
 */
public interface FormatManager<T> extends ReferenceConverter<T>
{

	/**
	 * Returns a non-null identifier indicating the type of object upon which this
	 * FormatManager operates.
	 * 
	 * For convenience, this will typically be equivalent to the short name of
	 * the Class of the type of object upon which this FormatManager operates,
	 * but that is not strictly required.
	 * 
	 * @return an identifier indicating the type of object upon which this
	 *         FormatManager operates
	 */
	public String getIdentifierType();

	/**
	 * Returns the Optional FormatManager for a component of the format managed by this
	 * FormatManager, much like getComponentClass() on Class.class can return
	 * the component in an array.
	 * 
	 * @return The Optional FormatManager for a component of the format managed by this
	 *         FormatManager
	 */
	public Optional<FormatManager<?>> getComponentManager();

	/**
	 * Initializes an instance of the class managed by this FormatManager with underlying
	 * values of native formats as they are contained in the provided ValueStore.
	 * 
	 * Generally, this is a method for setting up an initialized and valid object of the
	 * given class underlying the FormatManager. For complex types, such as a Compound,
	 * the FormatManager can use its internal knowledge along with the values in the
	 * ValueStore to build a valid Compound for the exact format managed by the
	 * FormatManager. For native objects, like a manager for Strings, it would just grab
	 * the value from the ValueStore. Note the underlying intent here is to hold default
	 * values for a formula system, without having to specify a default value for complex
	 * formats which could be derived from the simple native formats.
	 * 
	 * @param valueStore
	 *            The ValueStore from which values should be retrieved for native formats
	 * @return An instance of the class managed by this FormatManager with underlying
	 *         values from the ValueStore
	 */
	@SuppressWarnings("unchecked")
	public default T initializeFrom(ValueStore valueStore)
	{
		return (T) valueStore.getValueFor(getIdentifierType());
	}
}
