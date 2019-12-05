/*
 *  RPGeneration - A role playing utility generate interesting things
 *  Copyright (C) 2002 Devon Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.doomsdaybook;

/**
 * {@code Operation} encapsulates an action that can be performed
 * on a variable. These are actions such as setting, adding or
 * multiplying the variable's current value.
 */
class Operation implements Comparable<Object>
{
    /**
     * The identifying key of the variable the operation is to affect.
     */
    private final String key;
    /**
     * The name of the operation.
     */
    private final String name;
    /**
     * The type of action to take on the variable.
     */
    private final String type;
    /**
     * The value to be used in the operation.
     */
    private final String value;

    /**
     * Create a new Operation instance.
     *
     * @param type  The type of action to take on the variable.
     * @param key   The identifying key of the variable the operation is to affect.
     * @param value The value to be used in the operation.
     * @param name  The name of the operation.
     */
    private Operation(String type, String key, String value, String name)
    {
        this.type = type;
        this.key = key;
        this.value = value;
        this.name = name;
    }

    /**
     * Create a new unnamed Operation instance.
     *
     * @param type  The type of action to take on the variable.
     * @param key   The identifying key of the variable the operation is to affect.
     * @param value The value to be used in the operation.
     */
    public Operation(String type, String key, String value)
    {
        this(type, key, value, "");
    }

    /**
     * @return The current value of the key.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * @return The current value of the type.
     */
    public String getType()
    {
        return type;
    }

    /**
     * @return The current value of the value.
     */
    public String getValue()
    {
        return value;
    }

    @Override
    public int compareTo(Object obj)
    {
        String title = this.toString();
        String compared = obj.toString();

        return title.compareTo(compared);
    }

    @Override
    public String toString()
    {
        return name;
    }
}
