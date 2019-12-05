/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 *
 */
package pcgen.core;

import pcgen.cdom.base.Ungranted;

/**
 * {@code ShieldProf}.
 * <p>
 * DO NOT DELETE (waiting for use)
 */
public final class ShieldProf extends PObject implements Comparable<Object>, Ungranted
{
    /**
     * Compares keyName only
     *
     * @param o1 Object
     * @return int
     */
    @Override
    public int compareTo(final Object o1)
    {
        return getKeyName().compareToIgnoreCase(((ShieldProf) o1).getKeyName());
    }

    /**
     * Compares keyName only
     *
     * @param obj Object
     * @return boolean
     */
    @Override
    public boolean equals(final Object obj)
    {
        return obj instanceof ShieldProf && getKeyName().equalsIgnoreCase(((ShieldProf) obj).getKeyName());
    }

    /**
     * Hashcode of the keyName
     *
     * @return int
     */
    @Override
    public int hashCode()
    {
        return getKeyName().hashCode();
    }
}
