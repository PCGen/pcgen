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
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.base.ChoiceFilterUtilities;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.ObjectContainer;
import pcgen.cdom.base.PrimitiveChoiceFilter;
import pcgen.cdom.enumeration.GroupingState;

public class FilteredReference<T> extends CDOMGroupRef<T>
{

	private final Set<CDOMSingleRef<? super T>> filterSet = new HashSet<CDOMSingleRef<? super T>>();

	private final ObjectContainer<T> baseSet;

	public FilteredReference(Class<T> cl, ObjectContainer<T> allRef)
	{
		super(cl, "Filtered Reference");
		if (cl == null)
		{
			throw new IllegalArgumentException(
					"Class for FilteredReference cannot be null");
		}
		if (allRef == null)
		{
			throw new IllegalArgumentException(
					"Base Set for FilteredReference cannot be null");
		}
		baseSet = allRef;
	}

	public void addProhibitedItem(CDOMSingleRef<? super T> cs)
	{
		if (cs == null)
		{
			throw new IllegalArgumentException(
					"CDOMSingleRef to be added cannot be null");
		}
		Class<?> refClass = cs.getReferenceClass();
		if (!baseSet.getReferenceClass().isAssignableFrom(refClass))
		{
			throw new IllegalArgumentException("CDOMSingleRef to be added "
					+ refClass + " is a different class type than "
					+ baseSet.getReferenceClass().getSimpleName());
		}
		filterSet.add(cs);
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
	public boolean equals(Object o)
	{
		if (o instanceof FilteredReference)
		{
			FilteredReference<?> other = (FilteredReference<?>) o;
			return baseSet.equals(other.baseSet)
					&& filterSet.equals(other.filterSet);
		}
		return false;
	}

	public GroupingState getGroupingState()
	{
		GroupingState gs = GroupingState.EMPTY;
		for (PrimitiveChoiceFilter<? super T> cs : filterSet)
		{
			gs = cs.getGroupingState().add(gs);
		}
		return (filterSet.size() == 1) ? gs : gs
				.compound(GroupingState.ALLOWS_UNION);
	}

	@Override
	public boolean contains(T obj)
	{
		return getContainedObjects().contains(obj);
	}

	@Override
	public void addResolution(T obj)
	{
		throw new IllegalStateException(
				"CompoundReference cannot be given a resolution");
	}

	@Override
	public Collection<T> getContainedObjects()
	{
		Set<T> choices = new HashSet<T>();
		choices.addAll(baseSet.getContainedObjects());
		RETAIN: for (Iterator<T> it = choices.iterator(); it.hasNext();)
		{
			T choice = it.next();
			for (CDOMSingleRef<? super T> cf : filterSet)
			{
				if (cf.contains(choice))
				{
					it.remove();
					continue RETAIN;
				}
			}
		}
		return choices;
	}

	@Override
	public String getLSTformat()
	{
		Set<PrimitiveChoiceFilter<? super T>> sortSet = new TreeSet<PrimitiveChoiceFilter<? super T>>(
				ChoiceFilterUtilities.FILTER_SORTER);
		sortSet.addAll(filterSet);
		return "ALL|!" + ChoiceFilterUtilities.joinLstFormat(sortSet, "|!");
	}

	@Override
	public int getObjectCount()
	{
		return baseSet.getContainedObjects().size() - filterSet.size();
	}
}
