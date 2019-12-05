/*
 * Copyright (c) 2007-18 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.reference;

import pcgen.base.util.Indirect;
import pcgen.cdom.base.CDOMReference;

/**
 * A CDOMSingleRef is a CDOMReference which is intended to contain a single
 * object of a given Type for the Class this CDOMSingleRef represents.
 *
 * @param <T> The Class of the underlying object contained by this CDOMSingleRef
 */
public abstract class CDOMSingleRef<T> extends CDOMReference<T> implements Indirect<T>
{

    /**
     * Constructs a new CDOMSingleRef for the given name.
     *
     * @param key An identifier of the object this CDOMSingleRef contains.
     */
    public CDOMSingleRef(String key)
    {
        super(key);
    }

    /**
     * Returns one: The count of the number of objects included in the
     * Collection of Objects to which this CDOMSingleRef refers.
     *
     * @return one (since this is a single reference)
     */
    @Override
    public int getObjectCount()
    {
        return 1;
    }

    /**
     * Returns the given Object this CDOMSingleRef contains.
     * <p>
     * Note that the behavior of this class is undefined if the CDOMSingleRef
     * has not yet been resolved.
     *
     * @return the given Object this CDOMSingleRef contains.
     */
    @Override
    public abstract T get();

    /**
     * Returns true if this CDOMSingleRef has been resolved; false otherwise.
     *
     * @return true if this CDOMSingleRef has been resolved; false otherwise
     */
    public abstract boolean hasBeenResolved();

    /**
     * Sets the choice for this CDOMSingleRef.
     *
     * @param choice The choice to be set for this CDOMSingleRef
     */
    public abstract void setChoice(String choice);

    @Override
    public String getUnconverted()
    {
        return getName();
    }

}
