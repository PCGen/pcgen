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
import java.util.Objects;

import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.FormatManager;

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
    private static CaseInsensitiveMap<FactKey<?>> typeMap = new CaseInsensitiveMap<>();

    /**
     * The name of this FactKey
     */
    private final String fieldName;

    private final FormatManager<T> formatManager;

    private FactKey(String name, FormatManager<T> fmtManager)
    {
        Objects.requireNonNull(name, "Name for FactKey cannot be null");
        Objects.requireNonNull(fmtManager, "FormatManager for FactKey cannot be null");
        fieldName = name;
        formatManager = fmtManager;
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
     * @param name The name of the FactKey to be returned
     * @return The FactKey for the given name
     */
    public static <T> FactKey<T> getConstant(String name, FormatManager<T> fmtManager)
    {
        //This cast is checked by the "else" below
        @SuppressWarnings("unchecked")
        FactKey<T> key = (FactKey<T>) typeMap.get(name);
        if (key == null)
        {
            key = new FactKey<>(name, fmtManager);
            typeMap.put(name, key);
        } else if (!key.formatManager.equals(fmtManager))
        {
            throw new IllegalArgumentException(
                    "FactKey: " + name + " does not store objects of " + fmtManager.getManagedClass().getCanonicalName());
        }
        return key;
    }

    /**
     * Returns the FactKey for the given String (the search for the constant is
     * case insensitive). If the FactKey does not already exist, an
     * IllegalArgumentException is thrown.
     * <p>
     * Does not check the type of the FactKey
     *
     * @param name The name of the FactKey to be returned
     * @return The FactKey for the given name
     * @throws IllegalArgumentException if the given String is not a previously defined FactKey
     */
    public static <T> FactKey<T> valueOf(String name)
    {
        FactKey<T> key = (FactKey<T>) typeMap.get(name);
        if (key == null)
        {
            throw new IllegalArgumentException(name + " is not a previously defined FactKey");
        }
        return key;
    }

    /**
     * Returns a Collection of all of the FactKeys in this Class.
     * <p>
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

    /**
     * Designed to appropriately cast an object fetched with this FactKey.
     *
     * @param obj The object that should be cast to the format of item stored by
     *            this FactKey
     * @return An object cast to the format of item stored by this FactKey
     */
    @SuppressWarnings("unchecked")
    public T cast(Object obj)
    {
        return (T) obj;
    }

    /**
     * Returns the FormatManager used by this FactKey.
     *
     * @return the FormatManager used by this FactKey
     */
    public FormatManager<T> getFormatManager()
    {
        return formatManager;
    }
}
