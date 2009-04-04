/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.filter;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.base.ChoiceFilterUtilities;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrimitiveChoiceFilter;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.core.PlayerCharacter;

/**
 * A CompoundAndFilter is a PrimitiveChoiceFilter which is intended to contain
 * one or more PrimitiveChoiceFilters that this object "joins" in an "and"
 * format. In other words, only if all of the underlying PrimitiveChoiceFilter
 * objects allow an object will this CompoundAndFilter allow the object.
 * 
 * @param <T>
 *            The Class of the underlying objects contained by this
 *            CompoundAndFilter
 */
public class CompoundAndFilter<T> implements PrimitiveChoiceFilter<T>
{

	private final Class<T> refClass;

	/**
	 * The list of underlying PrimitiveChoiceFilters that this CompoundAndFilter
	 * contains
	 */
	private final Set<PrimitiveChoiceFilter<T>> set = new TreeSet<PrimitiveChoiceFilter<T>>(
			ChoiceFilterUtilities.FILTER_SORTER);

	/**
	 * Constructs a new CompoundAndFilter which will contain objects contained
	 * by all of the PrimitiveChoiceFilters in the given Collection.
	 * 
	 * This constructor is reference-semantic and value-semantic. Ownership of
	 * the Collection provided to this constructor is not transferred.
	 * Modification of the Collection (after this constructor completes) does
	 * not result in modifying the CompoundAndFilter, and the CompoundAndFilter
	 * will not modify the given Collection. However, strong references are
	 * maintained to the PrimitiveChoiceFilter objects contained within the
	 * given Collection.
	 * 
	 * @param col
	 *            A Collection of PrimitiveChoiceFilters which define the Set of
	 *            objects contained within the CompoundAndFilter
	 * @throws IllegalArgumentException
	 *             if the given Collection is null or empty.
	 */
	public CompoundAndFilter(Collection<PrimitiveChoiceFilter<T>> coll)
	{
		if (coll == null)
		{
			throw new IllegalArgumentException(
					"Collection for CompoundAndFilter cannot be null");
		}
		if (coll.isEmpty())
		{
			throw new IllegalArgumentException(
					"Collection for CompoundAndFilter cannot be empty");
		}
		refClass = coll.iterator().next().getReferenceClass();
		set.addAll(coll);
	}

	/**
	 * Return true if the given PlayerCharacter is allowed to select the given
	 * object
	 * 
	 * @param pc
	 *            The PlayerCharacter to be tested to determine if the given
	 *            object is allowed to be selected by this PlayerCharacter
	 * @param obj
	 *            The object to be tested to determine if the given
	 *            PlayerCharacter is allowed to select this object
	 * @return true if the given PlayerCharacter is allowed to select the given
	 *         object; false otherwise
	 */
	public boolean allow(PlayerCharacter pc, T obj)
	{
		for (PrimitiveChoiceFilter<T> cs : set)
		{
			if (!cs.allow(pc, obj))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the Class object representing the Class that this
	 * CompoundAndFilter evaluates.
	 * 
	 * @return Class object representing the Class that this CompoundAndFilter
	 *         evaluates
	 */
	public Class<T> getReferenceClass()
	{
		return refClass;
	}

	/**
	 * Returns a representation of this CompoundAndFilter, suitable for storing
	 * in an LST file.
	 * 
	 * @return A representation of this CompoundAndFilter, suitable for storing
	 *         in an LST file.
	 */
	public String getLSTformat()
	{
		return ChoiceFilterUtilities.joinLstFormat(set, Constants.COMMA);
	}

	/**
	 * Returns the consistent-with-equals hashCode for this CompoundAndFilter
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return refClass.hashCode() * 31 + set.size();
	}

	/**
	 * Returns true if this CompoundAndFilter is equal to the given Object.
	 * Equality is defined as being another CompoundAndFilter object with equal
	 * underlying contents.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof CompoundAndFilter)
		{
			CompoundAndFilter<?> other = (CompoundAndFilter<?>) obj;
			return refClass.equals(other.refClass) && set.equals(other.set);
		}
		return false;
	}

	/**
	 * Returns the GroupingState for this CompoundAndFilter. The GroupingState
	 * indicates how this CompoundAndFilter can be combined with other
	 * PrimitiveChoiceFilters.
	 * 
	 * @return The GroupingState for this CompoundAndFilter.
	 */
	public GroupingState getGroupingState()
	{
		GroupingState gs = GroupingState.EMPTY;
		for (PrimitiveChoiceFilter<T> cs : set)
		{
			gs = cs.getGroupingState().add(gs);
		}
		return gs.compound(GroupingState.ALLOWS_INTERSECTION);
	}
}
