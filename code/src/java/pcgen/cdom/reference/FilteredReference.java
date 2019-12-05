/*
 * Copyright 2010 (C) Tom Parker <thpr@sourceforge.net>
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
package pcgen.cdom.reference;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.PrimitiveCollection;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.primitive.PrimitiveUtilities;

public class FilteredReference<T> extends CDOMGroupRef<T>
{

    private final Set<CDOMSingleRef<? super T>> filterSet = new HashSet<>();

    private final CDOMGroupRef<T> baseSet;

    public FilteredReference(CDOMGroupRef<T> allRef)
    {
        super("Filtered Reference");
        Objects.requireNonNull(allRef, "Base Set for FilteredReference cannot be null");
        baseSet = allRef;
    }

    public void addProhibitedItem(CDOMSingleRef<? super T> prohibitedRef)
    {
        Objects.requireNonNull(prohibitedRef, "CDOMSingleRef to be added cannot be null");
        Class<?> refClass = prohibitedRef.getReferenceClass();
        if (!baseSet.getReferenceClass().isAssignableFrom(refClass))
        {
            throw new IllegalArgumentException("CDOMSingleRef to be added " + refClass
                    + " is a different class type than " + baseSet.getReferenceClass().getSimpleName());
        }
        filterSet.add(prohibitedRef);
    }

    public Class<? super T> getChoiceClass()
    {
        return baseSet.getReferenceClass();
    }

    @Override
    public int hashCode()
    {
        return baseSet.hashCode() + filterSet.size();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof FilteredReference)
        {
            FilteredReference<?> other = (FilteredReference<?>) obj;
            return baseSet.equals(other.baseSet) && filterSet.equals(other.filterSet);
        }
        return false;
    }

    @Override
    public GroupingState getGroupingState()
    {
        GroupingState state = GroupingState.EMPTY;
        for (PrimitiveCollection<? super T> pcf : filterSet)
        {
            state = pcf.getGroupingState().add(state);
        }
        return (filterSet.size() == 1) ? state : state.compound(GroupingState.ALLOWS_UNION);
    }

    @Override
    public boolean contains(T item)
    {
        return getContainedObjects().contains(item);
    }

    @Override
    public void addResolution(T item)
    {
        throw new IllegalStateException("CompoundReference cannot be given a resolution");
    }

    @Override
    public Collection<T> getContainedObjects()
    {
        Set<T> choices = new HashSet<>(baseSet.getContainedObjects());
        RETAIN:
        for (Iterator<T> it = choices.iterator();it.hasNext();)
        {
            T choice = it.next();
            for (CDOMSingleRef<? super T> prohibitedRef : filterSet)
            {
                if (prohibitedRef.contains(choice))
                {
                    it.remove();
                    continue RETAIN;
                }
            }
        }
        return choices;
    }

    @Override
    public String getLSTformat(boolean useAny)
    {
        Set<PrimitiveCollection<? super T>> sortSet = new TreeSet<>(PrimitiveUtilities.COLLECTION_SORTER);
        sortSet.addAll(filterSet);
        return "ALL|!" + PrimitiveUtilities.joinLstFormat(sortSet, "|!", useAny);
    }

    @Override
    public int getObjectCount()
    {
        return baseSet.getContainedObjects().size() - filterSet.size();
    }

    @Override
    public String getChoice()
    {
        return null;
    }

    @Override
    public Class<T> getReferenceClass()
    {
        return baseSet.getReferenceClass();
    }

    @Override
    public String getReferenceDescription()
    {
        StringJoiner joiner = new StringJoiner(", ", baseSet.getReferenceDescription() + " except: [", "]");
        filterSet.stream().map(CDOMReference::getReferenceDescription).forEach(joiner::add);
        return joiner.toString();
    }

    @Override
    public String getPersistentFormat()
    {
        return baseSet.getPersistentFormat();
    }
}
