/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
import java.util.Objects;

import pcgen.base.enumeration.TypeSafeConstant;
import pcgen.base.util.CaseInsensitiveMap;

/**
 * This Class is a Type Safe Constant. It is designed to hold DisplayLocations
 * in a type-safe fashion, so that they can be quickly compared and use less
 * memory when identical DisplayLocations exist.
 */
public final class DisplayLocation implements TypeSafeConstant, Comparable<DisplayLocation>
{
    /**
     * This Map contains the mappings from Strings to the Type Safe Constant
     */
    private static CaseInsensitiveMap<DisplayLocation> typeMap = new CaseInsensitiveMap<>();

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

    private DisplayLocation(String name)
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
     * @param name The name of the constant to be returned
     * @return The Constant for the given name
     */
    public static DisplayLocation getConstant(String name)
    {
        DisplayLocation type = typeMap.get(name);
        if (type == null)
        {
            type = new DisplayLocation(name);
            typeMap.put(name, type);
        }
        return type;
    }

    /**
     * Returns the constant for the given String (the search for the constant is
     * case insensitive). If the constant does not already exist, an
     * IllegalArgumentException is thrown.
     *
     * @param name The name of the constant to be returned
     * @return The Constant for the given name
     * @throws IllegalArgumentException if the given String is not a previously defined Pantheon
     */
    public static DisplayLocation valueOf(String name)
    {
        DisplayLocation type = typeMap.get(name);
        if (type == null)
        {
            throw new IllegalArgumentException(name + " is not a previously defined Type");
        }
        return type;
    }

    /**
     * Returns a Collection of all of the Constants in this Class.
     * <p>
     * This collection maintains a reference to the Constants in this Class, so
     * if a new Constant is created, the Collection returned by this method will
     * be modified. (Beware of ConcurrentModificationExceptions)
     *
     * @return a Collection of all of the Constants in this Class.
     */
    public static Collection<DisplayLocation> getAllConstants()
    {
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
        typeMap.clear();
    }

    @Override
    public int compareTo(DisplayLocation type)
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
}
