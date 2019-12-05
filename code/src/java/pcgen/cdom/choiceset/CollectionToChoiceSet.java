/*
 * Copyright 2010 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.choiceset;

import java.util.Collection;
import java.util.Objects;

import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.base.PrimitiveCollection;
import pcgen.cdom.converter.DereferencingConverter;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.core.PlayerCharacter;

public class CollectionToChoiceSet<T> implements PrimitiveChoiceSet<T>
{
    private final PrimitiveCollection<T> primitive;

    public CollectionToChoiceSet(PrimitiveCollection<T> prim)
    {
        Objects.requireNonNull(prim, "PrimitiveCollection cannot be null");
        primitive = prim;
    }

    @Override
    public Class<? super T> getChoiceClass()
    {
        return primitive.getReferenceClass();
    }

    @Override
    public GroupingState getGroupingState()
    {
        return primitive.getGroupingState();
    }

    @Override
    public String getLSTformat(boolean useAny)
    {
        return primitive.getLSTformat(useAny);
    }

    @Override
    public Collection<? extends T> getSet(PlayerCharacter pc)
    {
        return primitive.getCollection(pc, new DereferencingConverter<>(pc));
    }

    @Override
    public int hashCode()
    {
        return primitive.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof CollectionToChoiceSet) && ((CollectionToChoiceSet<?>) obj).primitive.equals(primitive);
    }
}
