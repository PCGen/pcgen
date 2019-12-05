/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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

/**
 * An ItemFacet is a Facet that stores a single item per identifier.
 *
 * @param <IDT> The Identifier Type used to set and store objects from the
 *              ItemFacet
 * @param <T>   The Type of object stored (per identifier) in the ItemFacet
 */
@FunctionalInterface
public interface ItemFacet<IDT, T>
{
    /**
     * Returns the object stored in the ItemFacet for the given Identifier.
     * <p>
     * It is expected that null is a legal response from an ItemFacet, as there
     * is no guarantee an item has been set for the given Identifier.
     *
     * @param id The identifier used to identify the object to be returned by
     *           the ItemFacet
     * @return the object stored in the ItemFacet for the given Identifier
     */
    T get(IDT id);
}
