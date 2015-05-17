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
package pcgen.cdom.enumeration;

import java.util.Collection;
import java.util.Collections;

import pcgen.base.util.CaseInsensitiveMap;
import pcgen.rules.types.FormatManager;

/**
 * This is a Typesafe enumeration of legal FACTs of an object. It is designed to
 * act as an index to a specific facts within a CDOMObject.
 * 
 * @param <T> The Type of object stored for the FactKey
 */
public final class FactKey<T>
{

	/**
	 * This Map contains the mappings from Strings to the FactKey
	 */
	private static CaseInsensitiveMap<FactKey<?>> typeMap =
			new CaseInsensitiveMap<FactKey<?>>();

	/**
	 * The name of this FactKey
	 */
	private final String fieldName;

	private final FormatManager<T> formatManager;

	private FactKey(String name, FormatManager<T> mgr)
	{
		if (name == null)
		{
			throw new IllegalArgumentException(
				"Name for FactKey cannot be null");
		}
		if (mgr == null)
		{
			throw new IllegalArgumentException(
				"FormatManager for FactKey cannot be null");
		}
		fieldName = name;
		formatManager = mgr;
	}

	/**
	 * Converts this Constant to a String (returns the name of this Constant)
	 * 
	 * @return The string representation (name) of this Constant
	 */
	@Override
	public String toString()
	{
		return fieldName;
	}

	/**
	 * Returns the FactKey for the given String (the search for the constant is
	 * case insensitive). If the constant does not already exist, a new FactKey
	 * is created with the given String as the name of the FactKey.
	 * 
	 * @param name
	 *            The name of the FactKey to be returned
	 * @return The FactKey for the given name
	 */
	public static <T> FactKey<T> getConstant(String name, FormatManager<T> cl)
	{
		FactKey<T> key = (FactKey<T>) typeMap.get(name);
		if (key == null)
		{
			key = new FactKey<T>(name, cl);
			typeMap.put(name, key);
		}
		else if (!key.formatManager.equals(cl))
		{
			throw new IllegalArgumentException("FactKey: " + name
				+ " does not store objects of " + cl.getType().getCanonicalName());
		}
		return key;
	}

	/**
	 * Returns the FactKey for the given String (the search for the constant is
	 * case insensitive). If the FactKey does not already exist, an
	 * IllegalArgumentException is thrown.
	 * 
	 * Does not check the type of the FactKey
	 * 
	 * @param name
	 *            The name of the FactKey to be returned
	 * @return The FactKey for the given name
	 * @throws IllegalArgumentException
	 *             if the given String is not a previously defined FactKey
	 */
	public static <T> FactKey<T> valueOf(String name)
	{
		FactKey<T> key = (FactKey<T>) typeMap.get(name);
		if (key == null)
		{
			throw new IllegalArgumentException(name
				+ " is not a previously defined FactKey");
		}
		return key;
	}

	/**
	 * Returns a Collection of all of the FactKeys in this Class.
	 * 
	 * This collection maintains a reference to the FactKeys in this Class, so
	 * if a new FactKey is created, the Collection returned by this method will
	 * be modified. (Beware of ConcurrentModificationExceptions)
	 * 
	 * @return a Collection of all of the FactKeys in this Class.
	 */
	public static Collection<FactKey<?>> getAllConstants()
	{
		return Collections.unmodifiableCollection(typeMap.values());
	}

	/**
	 * Clears all of the FactKeys in this Class (forgets the mappings from
	 * Strings to FactKeys).
	 */
	public static void clearConstants()
	{
		typeMap.clear();
	}

	public T cast(Object obj)
	{
		return (T) obj;
	}
	
	public FormatManager<T> getFormatManager()
	{
		return formatManager;
	}
}
