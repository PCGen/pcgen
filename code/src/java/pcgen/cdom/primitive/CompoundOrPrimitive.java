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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Converter;
import pcgen.cdom.base.PrimitiveCollection;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.core.PlayerCharacter;
import pcgen.util.Logging;

public class CompoundOrPrimitive<T> implements PrimitiveCollection<T>
{

    private final Class<? super T> refClass;

    private final Set<PrimitiveCollection<T>> primCollection = new TreeSet<>(PrimitiveUtilities.COLLECTION_SORTER);

    public CompoundOrPrimitive(Collection<? extends PrimitiveCollection<T>> pcfCollection)
    {
        Objects.requireNonNull(pcfCollection, "Collection for CompoundAndPrimitive cannot be null");
        if (pcfCollection.isEmpty())
        {
            throw new IllegalArgumentException("Collection for CompoundAndPrimitive cannot be empty");
        }
        Class<? super T> pcfClass = null;
        primCollection.addAll(pcfCollection);
        if (primCollection.size() != pcfCollection.size())
        {
            if (Logging.isLoggable(Level.WARNING))
            {
                Logging.log(Level.WARNING, "Found duplicate item in " + pcfCollection);
            }
            primCollection.add(PrimitiveCollection.FIXED.invalid());
        }
        for (PrimitiveCollection<T> pcf : primCollection)
        {
            Class<? super T> thisPCFClass = pcf.getReferenceClass();
            if (pcfClass == null)
            {
                pcfClass = thisPCFClass;
            } else if (!pcfClass.isAssignableFrom(thisPCFClass))
            {
                if (thisPCFClass.isAssignableFrom(pcfClass))
                {
                    pcfClass = thisPCFClass;
                } else
                {
                    throw new IllegalArgumentException("List contains incompatible types: " + pcfClass.getSimpleName()
                            + " and " + thisPCFClass.getSimpleName());
                }
            }
        }
        refClass = pcfClass;
    }

    @Override
    public <R> Collection<R> getCollection(PlayerCharacter pc, Converter<T, R> c)
    {
        Set<R> returnSet = new LinkedHashSet<>();
        for (PrimitiveCollection<T> cs : primCollection)
        {
            returnSet.addAll(cs.getCollection(pc, c));
        }
        return returnSet;
    }

    @Override
    public Class<? super T> getReferenceClass()
    {
        return refClass;
    }

    /**
     * Returns the GroupingState for this CompoundOrPrimitive. The GroupingState
     * indicates how this CompoundOrPrimitive can be combined with other
     * PrimitiveChoiceSets.
     *
     * @return The GroupingState for this CompoundOrPrimitive.
     */
    @Override
    public GroupingState getGroupingState()
    {
        GroupingState state = GroupingState.EMPTY;
        for (PrimitiveCollection<T> pcs : primCollection)
        {
            state = pcs.getGroupingState().add(state);
        }
        return state.compound(GroupingState.ALLOWS_UNION);
    }

    /**
     * Returns a representation of this CompoundOrPrimitive, suitable for
     * storing in an LST file.
     *
     * @return A representation of this CompoundOrPrimitive, suitable for
     * storing in an LST file.
     */
    @Override
    public String getLSTformat(boolean useAny)
    {
        return PrimitiveUtilities.joinLstFormat(primCollection, Constants.PIPE, useAny);
    }

    @Override
    public int hashCode()
    {
        return primCollection.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof CompoundOrPrimitive)
                && ((CompoundOrPrimitive<?>) obj).primCollection.equals(primCollection);
    }
}
