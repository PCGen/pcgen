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

/**
 * DataSubValue
 */
class DataSubValue
{
    private DataSubValue next;
    private final String key;
    private final String value;

    /**
     * Constructor
     *
     * @param key
     * @param value
     */
    DataSubValue(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    /**
     * Get the value
     *
     * @param searchKey
     * @return value
     */
    public String get(String searchKey)
    {
        if (key.equals(searchKey))
        {
            return value;
        }

        if (next == null)
        {
            return null;
        }

        return next.get(searchKey);
    }

    /**
     * Put the value
     *
     * @param sub
     */
    public void put(DataSubValue sub)
    {
        if (next == null)
        {
            next = sub;
        } else
        {
            next.put(sub);
        }
    }
}
