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
import pcgen.base.formula.Formula;
import pcgen.base.util.CaseInsensitiveMap;

/**
 * @author Tom Parker (thpr [at] yahoo.com)
 * 
 * This Class is a Type Safe Constant.
 */
public final class FormulaKey implements TypeSafeConstant
{

	/**
	 * This Map contains the mappings from Strings to the Type Safe Constant
	 */
	private static CaseInsensitiveMap<FormulaKey> typeMap;

	/**
	 * This is used to provide a unique ordinal to each constant in this class
	 */
	private static int ordinalCount = 0;

	public static final FormulaKey LEVEL_ADJUSTMENT =
			getConstant("LEVEL_ADJUSTMENT");

	public static final FormulaKey START_SKILL_POINTS =
			getConstant("START_SKILL_POINTS");

	public static final FormulaKey COST = getConstant("COST");

	public static final FormulaKey BASECOST = getConstant("BASECOST");

	public static final FormulaKey PAGE_USAGE = getConstant("PAGE_USAGE");

	public static final FormulaKey COMPANION_HP = getConstant("COMPANION_HP");

	public static final FormulaKey COMPANION_BAB = getConstant("COMPANION_BAB");

	public static final FormulaKey COMPANION_CHECK =
			getConstant("COMPANION_CHECK");

	public static final FormulaKey ITEM_CREATION_MULTIPLIER =
			getConstant("ITEM_CREATION_MULTIPLIER");

	public static final FormulaKey CR = getConstant("CR");

	public static final FormulaKey MONSTER_SKILL_POINTS =
			getConstant("MONSTER_SKILL_POINTS");

	public static final FormulaKey MONSTER_NON_SKILL_HD =
			getConstant("MONSTER_NON_SKILL_HD");

	public static final FormulaKey SELECT = getConstant("SELECT");

	public static final FormulaKey STAT_MOD = getConstant("STAT_MOD");

	public static final FormulaKey POOL =  getConstant("POOL");

	public static final FormulaKey MASTER_BAB = getConstant("MASTER_BAB");

	public static final FormulaKey MASTER_HP = getConstant("MASTER_HP");

	public static final FormulaKey MASTER_CHECK = getConstant("MASTER_CHECK");

	public static final FormulaKey SIZE = getConstant("SIZE");

	/**
	 * The name of this Constant
	 */
	private final String fieldName;

	private final Formula defaultValue;
	
	/**
	 * The ordinal of this Constant
	 */
	private final transient int ordinal;

	private FormulaKey(String name, Formula def)
	{
		ordinal = ordinalCount++;
		fieldName = name;
		defaultValue = def;
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
	
	public Formula getDefault()
	{
		return defaultValue;
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
	public static FormulaKey getConstant(String s)
	{
		if (typeMap == null)
		{
			typeMap = new CaseInsensitiveMap<FormulaKey>();
		}
		FormulaKey o = typeMap.get(s);
		if (o == null)
		{
			o = new FormulaKey(s, Formula.ZERO);
			typeMap.put(s, o);
		}
		return o;
	}

	/**
	 * Returns the constant for the given String (the search for the constant is
	 * case insensitive). If the constant does not already exist, a new Constant
	 * is created with the given String as the name of the Constant.
	 * 
	 * @param s
	 *            The name of the constant to be returned
	 * @param f
	 *            The Formula to be used as the default value if the FormulaKey
	 *            is not set
	 * @return The Constant for the given name
	 */
	public static FormulaKey getConstant(String s, Formula f)
	{
		if (typeMap == null)
		{
			typeMap = new CaseInsensitiveMap<FormulaKey>();
		}
		FormulaKey o = typeMap.get(s);
		if (o == null)
		{
			o = new FormulaKey(s, f);
			typeMap.put(s, o);
		}
		return o;
	}

	/**
	 * Returns the constant for the given String (the search for the constant is
	 * case insensitive). If the constant does not already exist, an
	 * IllegalArgumentException is thrown.
	 * 
	 * @param s
	 *            The name of the constant to be returned
	 * @return The Constant for the given name
	 */
	public static FormulaKey valueOf(String s)
	{
		if (typeMap == null)
		{
			typeMap = new CaseInsensitiveMap<FormulaKey>();
		}
		FormulaKey o = typeMap.get(s);
		if (o == null)
		{
			throw new IllegalArgumentException(s);
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
	public static Collection<FormulaKey> getAllConstants()
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

}
