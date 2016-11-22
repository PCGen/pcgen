/*
 * Copyright (c) 2006 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.enumeration;

import java.util.Collection;
import java.util.Collections;

import pcgen.base.enumeration.TypeSafeConstant;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.cdom.base.Category;
import pcgen.cdom.inst.AbstractCategory;
import pcgen.cdom.reference.ManufacturableFactory;
import pcgen.core.SubClass;

/**
 * @author Tom Parker (thpr [at] yahoo.com)
 * 
 * This Class is a Type Safe Constant.
 */
public final class SubClassCategory extends AbstractCategory<SubClass>
		implements TypeSafeConstant, Category<SubClass>,
		ManufacturableFactory<SubClass>
{

	/**
	 * This Map contains the mappings from Strings to the Type Safe Constant
	 */
	private static CaseInsensitiveMap<SubClassCategory> typeMap;

	/**
	 * This is used to provide a unique ordinal to each constant in this class
	 */
	private static int ordinalCount = 0;

	/**
	 * The ordinal of this Constant
	 */
	private final transient int ordinal;

	private SubClassCategory(String name)
	{
		ordinal = ordinalCount++;
		super.setName(name);
	}

	/**
	 * Gets the ordinal of this Constant
	 */
	@Override
	public int getOrdinal()
	{
		return ordinal;
	}

	/**
	 * Returns the constant for the given String (the search for the constant is
	 * case insensitive). If the constant does not already exist, a new Constant
	 * is created with the given String as the name of the Constant.
	 * 
	 * @param name
	 *            The name of the constant to be returned
	 * @return The Constant for the given name
	 */
	public static SubClassCategory getConstant(String name)
	{
		initializeTypeMap();
		String lookup = name.replace('_', ' ');
		SubClassCategory category = typeMap.get(lookup);
		if (category == null)
		{
			/*
			 * TODO FIXME Should .,| or other stuff be banned here? (probably)
			 */
			if (name.isEmpty())
			{
				throw new IllegalArgumentException(
						"Type Name cannot be zero length");
			}
			category = new SubClassCategory(lookup);
			typeMap.put(lookup, category);
		}
		return category;
	}

	private static synchronized void initializeTypeMap()
	{
		if (typeMap == null)
		{
			typeMap = new CaseInsensitiveMap<>();
		}
	}

	/**
	 * Returns the constant for the given String (the search for the constant is
	 * case insensitive). If the constant does not already exist, an
	 * IllegalArgumentException is thrown.
	 * 
	 * @param name
	 *            The name of the constant to be returned
	 * @return The Constant for the given name
	 */
	public static SubClassCategory valueOf(String name)
	{
		initializeTypeMap();
		SubClassCategory category = typeMap.get(name);
		if (category == null)
		{
			throw new IllegalArgumentException(name);
		}
		return category;
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
	public static Collection<SubClassCategory> getAllConstants()
	{
		if (typeMap == null)
		{
			return null;
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

	@Override
	public Category<SubClass> getParentCategory()
	{
		return null;
	}

	@Override
	public void setName(String name)
	{
		throw new UnsupportedOperationException(
				"Cannot set name in SubClassCategory");
	}

	@Override
	public SubClass newInstance()
	{
		SubClass sc = new SubClass();
		sc.setCDOMCategory(this);
		return sc;
	}

	@Override
	public Class<SubClass> getReferenceClass()
	{
		return SubClass.class;
	}

	@Override
	public String getReferenceDescription()
	{
		return "SubClass Category " + getKeyName();
	}
}
