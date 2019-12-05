/*
 * Copyright 2007-18 (c) Thomas Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package pcgen.cdom.reference;

import pcgen.base.util.ObjectContainer;
import pcgen.cdom.base.CDOMReference;

/**
 * A CDOMGroupRef is a CDOMReference which is intended to contain more than one
 * object of a given Type for the Class this CDOMGroupRef represents.
 *
 * @param <T> The Class of the underlying objects contained by this CDOMGroupRef
 */
public abstract class CDOMGroupRef<T> extends CDOMReference<T> implements ObjectContainer<T>
{

    /**
     * Constructs a new CDOMGroupRef for the given name.
     *
     * @param groupName An identifier of the objects this CDOMGroupRef contains.
     */
    public CDOMGroupRef(String groupName)
    {
        super(groupName);

    }

    /**
     * Returns true if the given Object is included in the Collection of Objects
     * to which this CDOMGroupRef refers.
     * <p>
     * Note that the behavior of this class is undefined if the CDOMGroupRef has
     * not yet been resolved.
     *
     * @param item The object to be tested to see if it is referred to by this
     *             CDOMGroupRef.
     * @return true if the given Object is included in the Collection of Objects
     * to which this CDOMGroupRef refers; false otherwise.
     */
    @Override
    public abstract boolean contains(T item);
}
