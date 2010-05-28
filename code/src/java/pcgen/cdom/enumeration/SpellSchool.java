/*
 * Copyright 2010 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.enumeration;

import java.util.Collection;
import java.util.Collections;

import pcgen.base.enumeration.TypeSafeConstant;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.cdom.choiceset.ObjectContainer;

public class SpellSchool implements TypeSafeConstant, Comparable<SpellSchool>
{

	/**
	 * This Map contains the mappings from Strings to the Type Safe Constant
	 */
	private static CaseInsensitiveMap<SpellSchool> typeMap;

	/**
	 * This is used to provide a unique ordinal to each constant in this class
	 */
	private static int ordinalCount = 0;

	private static ObjectContainer<SpellSchool> container;

	/**
	 * The name of this Constant
	 */
	private final String fieldName;

	/**
	 * The ordinal of this Constant
	 */
	private final transient int ordinal;

	private SpellSchool(String name)
	{
		if (name == null)
		{
			throw new IllegalArgumentException(
					"Name for Pantheon cannot be null");
		}
		ordinal = ordinalCount++;
		fieldName = name;
	}

	/**
	 * Converts this Constant to a String (returns the name of this Constant)
	 * 
	 * @return The string representatin (name) of this Constant
	 */
	@Override
	public String toString()
	{
		return fieldName;
	}

	/**
	 * Gets the ordinal of this Constant
	 */
	public int getOrdinal()
	{
		return ordinal;
	}

	/**
	 * Returns the constant for the given String (the search for the constant is
	 * case insensitive). If the constant does not already exist, a new Constant
	 * is created with the given String as the name of the Constant.
	 * 
	 * @param s
	 *            The name of the constant to be returned
	 * @return The Constant for the given name
	 */
	public static SpellSchool getConstant(String s)
	{
		initializeTypeMap();
		SpellSchool o = typeMap.get(s);
		if (o == null)
		{
			o = new SpellSchool(s);
			typeMap.put(s, o);
		}
		return o;
	}

	/**
	 * Thread safe construction of typeMap
	 */
	private static synchronized void initializeTypeMap()
	{
		if (typeMap == null)
		{
			typeMap = new CaseInsensitiveMap<SpellSchool>();
		}
	}

	/**
	 * Returns the constant for the given String (the search for the constant is
	 * case insensitive). If the constant does not already exist, an
	 * IllegalArgumentException is thrown.
	 * 
	 * @param s
	 *            The name of the constant to be returned
	 * @return The Constant for the given name
	 * @throws IllegalArgumentException
	 *             if the given String is not a previously defined Pantheon
	 */
	public static SpellSchool valueOf(String s)
	{
		initializeTypeMap();
		SpellSchool o = typeMap.get(s);
		if (o == null)
		{
			throw new IllegalArgumentException(s
					+ " is not a previously defined SpellSchool");
		}
		return o;
	}

	/**
	 * Returns a Collection of all of the Constants in this Class.
	 * 
	 * This collection maintains a reference to the Constants in this Class, so
	 * if a new Constant is created, the Collection returned by this method will
	 * be modified. (Beware of ConcurrentModificationExceptions)
	 * 
	 * @return a Collection of all of the Constants in this Class.
	 */
	public static Collection<SpellSchool> getAllConstants()
	{
		if (typeMap == null)
		{
			return Collections.emptyList();
		}
		return Collections.unmodifiableCollection(typeMap.values());
	}

	/**
	 * Clears all of the Constants in this Class (forgetting the mapping from
	 * the String to the Constant).
	 */
	/*
	 * CONSIDER Need to consider the ramifications of this on TypeSafeMap, since
	 * this does not (and really cannot) reset the ordinal count... Does this
	 * method need to be renamed, such that it is clearConstantMap? - Tom
	 * Parker, Feb 28, 2007
	 */
	public static void clearConstants()
	{
		if (typeMap != null)
		{
			typeMap.clear();
		}
	}

	public static ObjectContainer<SpellSchool> getContainer()
	{
		if (container == null)
		{
			container = new SpellObjectContainer();
		}
		return container;
	}


	public static class SpellObjectContainer implements ObjectContainer<SpellSchool>
	{

		public Collection<? extends SpellSchool> getContainedObjects()
		{
			return getAllConstants();
		}

		public Class<SpellSchool> getReferenceClass()
		{
			return SpellSchool.class;
		}

	}


	public int compareTo(SpellSchool arg0)
	{
		return fieldName.compareTo(arg0.fieldName);
	}

}
