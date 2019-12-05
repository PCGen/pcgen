/*
 * Copyright 2014 (C) Stefan Radermacher
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.util.enumeration;

public enum Load
{

    LIGHT("LIGHT"), MEDIUM("MEDIUM"), HEAVY("HEAVY"), OVERLOAD("OVERLOAD");

    private final String text;

    Load(String s)
    {
        text = s;
    }

    @Override
    public String toString()
    {
        return text;
    }

    public boolean checkLtEq(Load x)
    {
        return ordinal() <= x.ordinal();
    }

    public Load max(Load x)
    {
        return checkLtEq(x) ? x : this;
    }

    /**
     * @param val should be a string value to be checked for equality (case-insensitive) with
     *            one of the enum values for this enumeration
     * @return the enumeration that matches the given string, or null if none match
     */
    public static Load getLoadType(String val)
    {
        Load r = null;

        if (LIGHT.toString().equalsIgnoreCase(val))
        {
            r = LIGHT;
        }
        if (MEDIUM.toString().equalsIgnoreCase(val))
        {
            r = MEDIUM;
        }
        if (HEAVY.toString().equalsIgnoreCase(val))
        {
            r = HEAVY;
        }
        if (OVERLOAD.toString().equalsIgnoreCase(val))
        {
            r = OVERLOAD;
        }
        return r;
    }
}
