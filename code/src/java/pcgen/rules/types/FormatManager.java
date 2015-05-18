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
package pcgen.rules.types;

import pcgen.base.util.Indirect;
import pcgen.base.util.ObjectContainer;
import pcgen.rules.context.LoadContext;

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
public interface FormatManager<T>
{

	/**
	 * Converts the given String into an object of the type for which this
	 * FormatManager provides services.
	 * 
	 * @param context
	 *            The LoadContext used to resolve items
	 * @param inputStr
	 *            The input String which should be converted into the
	 *            appropriate object
	 * 
	 * @return An object of the type for which this FormatManager provides
	 *         services
	 * @throws IllegalArgumentException
	 *             if the given String is not a properly formatted String for
	 *             creation of the appropriate type of object
	 */
	T convert(LoadContext context, String inputStr);

	/**
	 * Converts the given String into an Indirect, which is capable of producing
	 * an object of the type for which this FormatManager provides services.
	 * 
	 * This indirection is sometimes necessary as objects may not be able to be
	 * produced during data load
	 * 
	 * @param context
	 *            The LoadContext used to resolve items
	 * @param inputStr
	 *            The input String which should be converted into the
	 *            appropriate object
	 * 
	 * @return An Indirect, which is capable of producing an object of the type
	 *         for which this FormatManager provides services
	 * @throws IllegalArgumentException
	 *             if the given String is not a properly formatted String for
	 *             creation of the appropriate type of object
	 */
	Indirect<T> convertIndirect(LoadContext context, String inputStr);

	/**
	 * Serializes (unconverts) the given object into a human-readable serialized
	 * form.
	 * 
	 * @param obj
	 *            The object to be converted into a human-readable serialized
	 *            form
	 * @return The human-readable serialized form of the given object
	 */
	String unconvert(T obj);

	/**
	 * Returns the Class of the type of object upon which this FormatManager
	 * operates.
	 * 
	 * @return the Class of the type of object upon which this FormatManager
	 *         operates
	 */
	Class<T> getType();

	/**
	 * Returns an identifier indicating the type of object upon which this
	 * FormatManager operates.
	 * 
	 * For convenience, this will typically be equivalent to the short name of
	 * the Class of the type of object upon which this FormatManager operates,
	 * but that is not strictly required.
	 * 
	 * @return an identifier indicating the type of object upon which this
	 *         FormatManager operates
	 */
	String getIdentifierType();

	/**
	 * Converts the given String into an ObjectContainer which contains zero or
	 * more objects of the type of object upon which this FormatManager
	 * operates.
	 * 
	 * @param context
	 *            The LoadContext used to resolve items
	 * @param inputStr
	 *            The String to be converted into an ObjectContainer which
	 *            contains zero or more objects of the type of object upon which
	 *            this FormatManager operates
	 * 
	 * @return An ObjectContainer that is capable of producing zero or more
	 *         objects of the type for which this FormatManager provides
	 *         services
	 * @throws IllegalArgumentException
	 *             if the given String is not a properly formatted String for
	 *             creation of the appropriate type of ObjectContainer
	 */
	ObjectContainer<T> convertObjectContainer(LoadContext context,
		String inputStr);

}
