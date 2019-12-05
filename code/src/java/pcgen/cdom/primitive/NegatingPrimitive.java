/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import pcgen.cdom.base.Converter;
import pcgen.cdom.base.PrimitiveCollection;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.core.PlayerCharacter;

public class NegatingPrimitive<T> implements PrimitiveCollection<T>
{

    private final PrimitiveCollection<T> primitive;
    private final PrimitiveCollection<T> all;

    public NegatingPrimitive(PrimitiveCollection<T> prim, PrimitiveCollection<T> all)
    {
        Objects.requireNonNull(prim, "PrimitiveCollection cannot be null");
        Objects.requireNonNull(all, "All Collection cannot be null");
        primitive = prim;
        this.all = all;
    }

    @Override
    public <R> Collection<? extends R> getCollection(PlayerCharacter pc, Converter<T, R> c)
    {
        Collection<? extends R> result = all.getCollection(pc, c);
        ArrayList<R> list = new ArrayList<>(result);
        list.removeAll(primitive.getCollection(pc, c));
        return list;
    }

    @Override
    public Class<? super T> getReferenceClass()
    {
        return primitive.getReferenceClass();
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
        return primitive.getGroupingState().negate();
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
        return "!" + primitive.getLSTformat(useAny);
    }

    @Override
    public int hashCode()
    {
        return primitive.hashCode() - 1;
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof NegatingPrimitive) && ((NegatingPrimitive<?>) obj).primitive.equals(primitive);
    }
}
