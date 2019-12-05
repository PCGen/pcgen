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
 * @param <T> The Type of object stored for the FactSetKey
 */
public final class FactSetKey<T>
{

    /**
     * This Map contains the mappings from Strings to the FactSetKey
     */
    private static CaseInsensitiveMap<FactSetKey<?>> typeMap = new CaseInsensitiveMap<>();

    /**
     * The name of this FactSetKey
     */
    private final String fieldName;

    private final FormatManager<T> formatManager;

    private FactSetKey(String name, FormatManager<T> fmtManager)
    {
        Objects.requireNonNull(name, "Name for FactSetKey cannot be null");
        Objects.requireNonNull(fmtManager, "FormatManager for FactSetKey cannot be null");
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
     * Returns the FactSetKey for the given String (the search for the constant
     * is case insensitive). If the constant does not already exist, a new
     * FactSetKey is created with the given String as the name of the
     * FactSetKey.
     *
     * @param name The name of the FactSetKey to be returned
     * @return The FactSetKey for the given name
     */
    public static <T> FactSetKey<T> getConstant(String name, FormatManager<T> fmtManager)
    {
        //This cast is checked by the "else" below
        @SuppressWarnings("unchecked")
        FactSetKey<T> key = (FactSetKey<T>) typeMap.get(name);
        if (key == null)
        {
            key = new FactSetKey<>(name, fmtManager);
            typeMap.put(name, key);
        } else if (!key.formatManager.equals(fmtManager))
        {
            throw new IllegalArgumentException("FactSetKey: " + name + " does not store objects of "
                    + fmtManager.getManagedClass().getCanonicalName());
        }
        return key;
    }

    /**
     * Returns the FactSetKey for the given String (the search for the constant
     * is case insensitive). If the FactSetKey does not already exist, an
     * IllegalArgumentException is thrown.
     *
     * @param name The name of the FactSetKey to be returned
     * @return The FactSetKey for the given name
     * @throws IllegalArgumentException if the given String is not a previously defined FactSetKey
     */
    public static <T> FactSetKey<T> valueOf(String name)
    {
        FactSetKey<T> key = (FactSetKey<T>) typeMap.get(name);
        if (key == null)
        {
            throw new IllegalArgumentException(name + " is not a previously defined FactSetKey");
        }
        return key;
    }

    /**
     * Returns a Collection of all of the FactSetKeys in this Class.
     * <p>
     * This collection maintains a reference to the FactSetKeys in this Class,
     * so if a new FactSetKey is created, the Collection returned by this method
     * will be modified. (Beware of ConcurrentModificationExceptions)
     *
     * @return a Collection of all of the FactSetKeys in this Class.
     */
    public static Collection<FactSetKey<?>> getAllConstants()
    {
        return Collections.unmodifiableCollection(typeMap.values());
    }

    /**
     * Clears all of the FactSetKeys in this Class (forgets the mappings from
     * Strings to FactSetKeys).
     */
    public static void clearConstants()
    {
        typeMap.clear();
    }

    /**
     * Designed to appropriately cast an object fetched with this FactSetKey.
     *
     * @param obj The object that should be cast to the format of item stored by
     *            this FactSetKey
     * @return An object cast to the format of item stored by this FactSetKey
     */
    @SuppressWarnings("unchecked")
    public T cast(Object obj)
    {
        return (T) obj;
    }

    /**
     * Returns the FormatManager used by this FactSetKey.
     *
     * @return the FormatManager used by this FactSetKey
     */
    public FormatManager<T> getFormatManager()
    {
        return formatManager;
    }
}
