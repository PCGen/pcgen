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

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import pcgen.base.enumeration.TypeSafeConstant;
import pcgen.base.lang.UnreachableError;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.cdom.base.Constants;

/**
 * 
 * This Class is a Type Safe Constant. It is designed to hold Types in a
 * type-safe fashion, so that they can be quickly compared and use less memory
 * when identical Types exist in two CDOMObjects.
 */
public final class Type implements TypeSafeConstant, Comparable<Type>
{
	/**
	 * This Map contains the mappings from Strings to the Type Safe Constant
	 */
	private static final CaseInsensitiveMap<Type> TYPE_MAP = new CaseInsensitiveMap<>();

	public static final Type NATURAL = getConstant("Natural");

	public static final Type CUSTOM = getConstant(Constants.TYPE_CUSTOM);

	public static final Type NONE = getConstant("None");

	public static final Type HUMANOID = getConstant("Humanoid");

	public static final Type WEAPON = getConstant("Weapon");

	public static final Type MELEE = getConstant("Melee");

	public static final Type SIMPLE = getConstant("Simple");

	public static final Type UNARMED = getConstant("Unarmed");

	public static final Type SUBDUAL = getConstant("Subdual");

	public static final Type STANDARD = getConstant("Standard");

	public static final Type MONK = getConstant("Monk");

	public static final Type BLUDGEONING = getConstant("Bludgeoning");

	public static final Type AUTO_GEN = getConstant("AUTO_GEN");

	public static final Type BOTH = getConstant("Both");

	public static final Type THROWN = getConstant("Thrown");

	public static final Type RANGED = getConstant("Ranged");

	public static final Type DOUBLE = getConstant("Double");

	public static final Type HEAD1 = getConstant("Head1");

	public static final Type HEAD2 = getConstant("Head2");

	public static final Type TEMPORARY = getConstant("TEMPORARY");

	public static final Type DIVINE = getConstant("Divine");

	public static final Type POTION = getConstant("Potion");

	public static final Type RING = getConstant("Ring");

	public static final Type SCROLL = getConstant("Scroll");

	public static final Type WAND = getConstant("Wand");

	public static final Type MONSTER = getConstant("Monster");

	public static final Type SHIELD = getConstant("Shield");

	public static final Type ARMOR = getConstant("Armor");

	public static final Type MAGIC = getConstant("Magic");

	public static final Type MASTERWORK = getConstant("Masterwork");

  public static final Type ANY = getConstant("ANY");

	static
	{
		buildMap();
	}

	/**
	 * This is used to provide a unique ordinal to each constant in this class
	 */
	private static int ordinalCount = 0;

	/**
	 * The name of this Constant
	 */
	private final String fieldName;

	/**
	 * The ordinal of this Constant
	 */
	private final int ordinal;

	private Type(String name)
	{
		Objects.requireNonNull(name, "Name for Type cannot be null");
		ordinal = ordinalCount++;
		fieldName = name;
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
	 * Gets the ordinal of this Constant
	 */
	@Override
	public int getOrdinal()
	{
		return ordinal;
	}

	/**
	 * Converts this Constant to a String (returns the name of this Constant)
	 * that is suitable for backwards compatible comparison. The returned value
	 * will always be uppercase.
	 * 
	 * @return The string representation (name) of this Constant
	 */
	public String getComparisonString()
	{
		return fieldName.toUpperCase();
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
	public static Type getConstant(String name)
	{
		Type type = TYPE_MAP.get(name);
		if (type == null)
		{
			type = new Type(name);
			TYPE_MAP.put(name, type);
		}
		return type;
	}

	/**
	 * Returns the constant for the given String (the search for the constant is
	 * case insensitive). If the constant does not already exist, an
	 * IllegalArgumentException is thrown.
	 * 
	 * @param name
	 *            The name of the constant to be returned
	 * @return The Constant for the given name
	 * @throws IllegalArgumentException
	 *             if the given String is not a previously defined Type
	 */
	public static Type valueOf(String name)
	{
		Type type = TYPE_MAP.get(name);
		if (type == null)
		{
			throw new IllegalArgumentException(name + " is not a previously defined Type");
		}
		return type;
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
	public static Collection<Type> getAllConstants()
	{
		return Collections.unmodifiableCollection(TYPE_MAP.values());
	}

	@Override
	public int compareTo(Type type)
	{
		/*
		 * Note: Some tools will report a problem here because Type implements
		 * compareTo, but does not implement custom implementations of hashCode
		 * or equals(). Because this is intended as a TypeSafeConstant, and Type
		 * has a private constructor, it is unnecessary to implement a custom
		 * hashCode or equals.
		 */
		return fieldName.compareTo(type.fieldName);
	}

	public static void buildMap()
	{
		TYPE_MAP.clear();
		Field[] fields = Type.class.getDeclaredFields();
        for (Field field : fields)
        {
            int mod = field.getModifiers();

            if (java.lang.reflect.Modifier.isStatic(mod) && java.lang.reflect.Modifier.isFinal(mod)
                    && java.lang.reflect.Modifier.isPublic(mod))
            {
                try
                {
                    Object obj = field.get(null);
                    if (obj instanceof Type)
                    {
                        TYPE_MAP.put(field.getName(), (Type) obj);
                    }
                } catch (IllegalArgumentException | IllegalAccessException e)
                {
                    throw new UnreachableError(e);
                }
            }
        }
	}

}
