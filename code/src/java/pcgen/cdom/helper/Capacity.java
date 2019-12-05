/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.helper;

import java.math.BigDecimal;

/**
 * A Capacity represents the carrying capacity of a container (this is a
 * specific feature of certain types of Equipment)
 */
public class Capacity
{

    /**
     * The constants that represents the special case of unlimited carrying
     * capacity.
     */
    public static final BigDecimal UNLIMITED = new BigDecimal(-1);

    /*
     * CONSIDER Need to flesh out how this works; depends on how Capacity
     * interacts with the core... - Tom Parker 3/1/07
     */
    /**
     * The special case that represents unlimited capacity for any object.
     */
    public static final Capacity ANY = new Capacity(null, UNLIMITED);

    /**
     * The type of objects that this Capacity represents
     */
    private final String type;

    /**
     * The value indicating the upper limit of the number of objects (of the
     * type that this Capacity represents) that this Capacity allows.
     */
    private final BigDecimal limit;

    /**
     * Constructs a new Capacity for the given type with the given upper limit
     * on capacity.
     *
     * @param capacityType  The type of objects that this Capacity represents
     * @param capacityLimit The value indicating the upper limit of the number of objects
     *                      (of the given type) that this Capacity allows
     */
    public Capacity(String capacityType, BigDecimal capacityLimit)
    {
        type = capacityType;
        limit = capacityLimit;
    }

    /**
     * Returns the value indicating the upper limit of the number of objects (of
     * the type that this Capacity represents) that this Capacity allows.
     *
     * @return The value indicating the upper limit of the number of objects (of
     * the type that this Capacity represents) that this Capacity
     * allows.
     */
    public BigDecimal getCapacity()
    {
        return limit;
    }

    /**
     * Returns the type of objects that this Capacity represents.
     *
     * @return The type of objects that this Capacity represents
     */
    public String getType()
    {
        return type;
    }

    /**
     * Provides a convenience method for producing a "Total" capacity object for
     * a container.
     *
     * @param capacity The total capacity limit used to construct the returned
     *                 Capacity.
     * @return A new Capacity object for a container with the given total
     * capacity limit.
     */
    public static Capacity getTotalCapacity(BigDecimal capacity)
    {
        return new Capacity(null, capacity);
    }

    @Override
    public String toString()
    {
        String sb = "Capacity: "
                + (type == null ? "Total" : type)
                + '='
                + (UNLIMITED.equals(limit) ? "UNLIMITED" : limit);
        return sb;
    }

    @Override
    public int hashCode()
    {
        return type == null ? 0 : type.hashCode() ^ limit.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Capacity)
        {
            Capacity other = (Capacity) obj;
            if (type == null)
            {
                if (other.type != null)
                {
                    return false;
                }
            } else
            {
                if (!type.equals(other.type))
                {
                    return false;
                }
            }
            return limit.equals(other.limit);
        }
        return false;
    }
}
