/*
 * Copyright 2014-15 (C) Tom Parker <thpr@users.sourceforge.net>
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
 */
package pcgen.cdom.base;

import java.util.Collection;

/**
 * An SetFacet is a Facet that stores a set of items per identifier.
 *
 * @param <IDT> The Identifier Type used to set and store objects from the
 *              SetFacet
 * @param <T>   The Type of objects stored (per identifier) in the SetFacet
 */
public interface SetFacet<IDT, T>
{
    /**
     * Returns the object stored in the SetFacet for the given Identifier.
     * <p>
     * This method should never return null.
     * <p>
     * It is intended that classes which extend SetFacet will make this method
     * value-semantic, meaning that ownership of the Collection returned by this
     * method will be transferred to the calling object. Modification of the
     * returned Collection should not result in modifying the SetFacet, and
     * modifying the SetFacet after the Collection is returned should not modify
     * the Collection.
     *
     * @param id The identifier used to identify the object to be returned by
     *           the SetFacet
     * @return the object stored in the SetFacet for the given Identifier
     */
    Collection<T> getSet(IDT id);

    /**
     * Returns the count of objects stored in the SetFacet for the given
     * Identifier.
     *
     * @param id The identifier used to identify the objects for which the
     *           count is to be returned by the SetFacet
     * @return the count of objects stored in the SetFacet for the given
     * Identifier
     */
    int getCount(IDT id);
}
