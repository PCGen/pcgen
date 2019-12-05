/*
 * Copyright 2003 (C) Devon Jones
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
package pcgen.core.doomsdaybook;

public class DataValue
{
    private DataSubValue subvalue;
    private final String value;

    /**
     * Constructor
     *
     * @param value
     */
    public DataValue(String value)
    {
        this.value = value;
    }

    /**
     * Get SubValue
     *
     * @param key
     * @return SubValue
     */
    public String getSubValue(String key)
    {
        if (subvalue != null)
        {
            return subvalue.get(key);
        }
        return null;
    }

    /**
     * Get value
     *
     * @return value
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Add sub value
     *
     * @param key
     * @param subValue
     */
    public void addSubValue(String key, String subValue)
    {
        if (subvalue != null)
        {
            subvalue.put(new DataSubValue(key, subValue));
        } else
        {
            subvalue = new DataSubValue(key, subValue);
        }
    }
}
