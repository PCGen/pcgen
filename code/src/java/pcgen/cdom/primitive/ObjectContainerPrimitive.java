/*
 * Copyright (c) 2014-15 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.primitive;

import java.util.Collection;
import java.util.Objects;

import pcgen.base.util.ObjectContainer;
import pcgen.cdom.base.Converter;
import pcgen.cdom.base.PrimitiveCollection;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.core.PlayerCharacter;

/**
 * An ObjectContainerPrimitive wraps an ObjectContainer to become a
 * PrimitiveCollection
 *
 * @param <T> The Type of object contained in the ObjectContainer and
 *            PrimitiveCollection
 */
public class ObjectContainerPrimitive<T> implements PrimitiveCollection<T>
{

    /**
     * The underlying ObjectContainer
     */
    private ObjectContainer<T> group;

    /**
     * Constructs a new ObjectContainerPrimitive wrapping the given
     * ObjectContainer
     *
     * @param oc The ObjectContainer that is underlying this
     *           ObjectContainerPrimitive
     */
    public ObjectContainerPrimitive(ObjectContainer<T> oc)
    {
        Objects.requireNonNull(oc, "ObjectContainer cannot be null");
        group = oc;
    }

    @Override
    public <R> Collection<? extends R> getCollection(PlayerCharacter pc, Converter<T, R> c)
    {
        return c.convert(group);
    }

    @Override
    public Class<? super T> getReferenceClass()
    {
        return group.getReferenceClass();
    }

    /**
     * Returns the GroupingState for this CompoundAndChoiceSet. The
     * GroupingState indicates how this CompoundAndChoiceSet can be combined
     * with other PrimitiveChoiceSets.
     *
     * @return The GroupingState for this CompoundAndChoiceSet.
     */
    @Override
    public GroupingState getGroupingState()
    {
        return GroupingState.ANY;
    }

    /**
     * Returns a representation of this CompoundAndChoiceSet, suitable for
     * storing in an LST file.
     *
     * @return A representation of this CompoundAndChoiceSet, suitable for
     * storing in an LST file.
     */
    @Override
    public String getLSTformat(boolean useAny)
    {
        return group.getLSTformat(useAny);
    }

    @Override
    public int hashCode()
    {
        return group.hashCode() - 1;
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof ObjectContainerPrimitive) && ((ObjectContainerPrimitive<?>) obj).group.equals(group);
    }
}
