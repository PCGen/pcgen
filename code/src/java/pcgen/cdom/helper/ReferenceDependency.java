/*
 * Copyright 2017 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import pcgen.base.util.Indirect;

/**
 * ReferenceDependency holds references, so formulas can have the appropriate dependencies
 * attached.
 */
public class ReferenceDependency
{

    private List<Indirect<?>> references;

    /**
     * Adds a reference to this ReferenceDependency
     *
     * @param reference The reference to be added to this ReferenceDependency
     */
    public void put(Indirect<?> reference)
    {
        if (references == null)
        {
            references = new ArrayList<>();
        }
        references.add(Objects.requireNonNull(reference));
    }

    /**
     * Adds all of the references in the given Collection to this ReferenceDependency.
     *
     * @param collection The Collection for which all of the included references should be added
     *                   to this ReferenceDependency
     */
    public void putAll(Collection<Indirect<?>> collection)
    {
        collection.stream().forEach(this::put);
    }

    /**
     * Returns a non-null, unmodifiable reference to the Collection of references
     * contained by this ReferenceDependency.
     *
     * @return A non-null, unmodifiable reference to the Collection of references
     * contained by this ReferenceDependency
     */
    public Collection<Indirect<?>> getReferences()
    {
        return (references == null) ? Collections.emptyList() : Collections.unmodifiableCollection(references);
    }

}
