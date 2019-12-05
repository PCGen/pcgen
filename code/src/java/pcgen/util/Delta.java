/*
 * Copyright 2001 (C) B. K. Oxley (binkley) <binkley@alumni.rice.edu>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 */
package pcgen.util;

/**
 * A helper for {@code java.lang.Integer} which understands a
 * leading plus sign for string conversion.
 *
 * @see java.lang.Integer
 */
public final class Delta
{
    private Delta()
    {
    }

    /**
     * Construct a @see java.lang.Integer and strip a leading plus
     * sign since {@code Integer} does not understand it.
     *
     * @param s
     * @return Integer
     */
    public static Integer decode(String s)
    {
        if ((!s.isEmpty()) && (s.charAt(0) == '+'))
        {
            s = s.substring(1);
        }

        return Integer.decode(s);
    }

    /**
     * Parse a string with an option plus or minus followed by digits
     * into an int.
     *
     * @param s a string that may or may not be a valid delta
     * @return int
     * @throws java.lang.NumberFormatException This exception is thrown if the string does not match the
     *                                         required format for a delta.
     */
    public static int parseInt(String s)
    {
        if (s.charAt(0) == '+')
        {
            s = s.substring(1);
        }

        return Integer.parseInt(s);
    }

    /**
     * toString
     *
     * @param v
     * @return String
     */
    public static String toString(Integer v)
    {
        return toString(v.intValue());
    }

    /**
     * Returns a String representation of an integer value.  If the value is
     * positive a plus sign (+) will be prepended.
     *
     * @param v An integer to convert.
     * @return String value of integer.
     */
    public static String toString(final int v)
    {
        if (v >= 0)
        {
            return "+" + v;
        }

        return String.valueOf(v);
    }

    /**
     * toString
     *
     * @param v
     * @return String
     */
    public static String toString(Float v)
    {
        return toString(v.floatValue());
    }

    /**
     * toString
     *
     * @param v
     * @return String
     */
    public static String toString(float v)
    {
        if (v >= 0.0)
        {
            return "+" + v;
        }

        return Float.toString(v);
    }
}
