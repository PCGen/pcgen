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
 * A CompoundOrFilter is a PrimitiveChoiceFilter which is intended to contain
 * one or more PrimitiveChoiceFilters that this object "joins" in an "or"
 * format. In other words, if any of the underlying PrimitiveChoiceFilter
 * objects allow an object will this CompoundOrFilter allow the object.
 * 
 * @param <T>
 *            The Class of the underlying objects contained by this
 *            CompoundOrFilter
 */
public class CompoundOrFilter<T> implements PrimitiveChoiceFilter<T>
{

	private final Class<? super T> refClass;

	/**
	 * The list of underlying PrimitiveChoiceFilters that this CompoundOrFilter
	 * contains
	 */
	private final Set<PrimitiveChoiceFilter<T>> pcfSet = new TreeSet<PrimitiveChoiceFilter<T>>(
			ChoiceFilterUtilities.FILTER_SORTER);

	/**
	 * Constructs a new CompoundOrFilter which will contain objects contained by
	 * all of the PrimitiveChoiceFilters in the given Collection.
	 * 
	 * This constructor is reference-semantic and value-semantic. Ownership of
	 * the Collection provided to this constructor is not transferred.
	 * Modification of the Collection (after this constructor completes) does
	 * not result in modifying the CompoundOrFilter, and the CompoundOrFilter
	 * will not modify the given Collection. However, strong references are
	 * maintained to the PrimitiveChoiceFilter objects contained within the
	 * given Collection.
	 * 
	 * @param col
	 *            A Collection of PrimitiveChoiceFilters which define the Set of
	 *            objects contained within the CompoundOrFilter
	 * @throws IllegalArgumentException
	 *             if the given Collection is null or empty.
	 */
	public CompoundOrFilter(Collection<PrimitiveChoiceFilter<T>> pcfCollection)
	{
		if (pcfCollection == null)
		{
			throw new IllegalArgumentException(
					"Collection for CompoundOrFilter cannot be null");
		}
		if (pcfCollection.isEmpty())
		{
			throw new IllegalArgumentException(
					"Collection for CompoundOrFilter cannot be empty");
		}
		Class<? super T> pcfClass = null;
		refClass = pcfClass;
		pcfSet.addAll(pcfCollection);
		for (PrimitiveChoiceFilter<T> pcf : pcfSet)
		{
			Class<? super T> refClass = pcf.getReferenceClass();
			if (pcfClass == null)
			{
				pcfClass = refClass;
			}
			else if (!pcfClass.isAssignableFrom(refClass))
			{
				if (refClass.isAssignableFrom(pcfClass))
				{
					pcfClass = refClass;
				}
				else
				{
					throw new IllegalArgumentException(
							"List contains incompatible types: "
							+ pcfClass.getSimpleName() + " and "
							+ refClass.getSimpleName());
				}
			}
		}
	}

	/**
	 * Return true if the given PlayerCharacter is allowed to select the given
	 * object
	 * 
	 * @param pc
	 *            The PlayerCharacter to be tested to determine if the given
	 *            object is allowed to be selected by this PlayerCharacter
	 * @param item
	 *            The object to be tested to determine if the given
	 *            PlayerCharacter is allowed to select this object
	 * @return true if the given PlayerCharacter is allowed to select the given
	 *         object; false otherwise
	 */
	public boolean allow(PlayerCharacter pc, T item)
	{
		for (PrimitiveChoiceFilter<T> pcf : pcfSet)
		{
			if (pcf.allow(pc, item))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the Class object representing the Class that this
	 * CompoundOrFilter evaluates.
	 * 
	 * @return Class object representing the Class that this CompoundOrFilter
	 *         evaluates
	 */
	public Class<? super T> getReferenceClass()
	{
		return refClass;
	}

	/**
	 * Returns a representation of this CompoundOrFilter, suitable for storing
	 * in an LST file.
	 * 
	 * @return A representation of this CompoundOrFilter, suitable for storing
	 *         in an LST file.
	 */
	public String getLSTformat()
	{
		return ChoiceFilterUtilities.joinLstFormat(pcfSet, Constants.PIPE);
	}

	/**
	 * Returns the consistent-with-equals hashCode for this CompoundOrFilter
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return refClass.hashCode() * 31 + pcfSet.size();
	}

	/**
	 * Returns true if this CompoundOrFilter is equal to the given Object.
	 * Equality is defined as being another CompoundOrFilter object with equal
	 * underlying contents.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof CompoundOrFilter)
		{
			CompoundOrFilter<?> other = (CompoundOrFilter<?>) obj;
			return refClass.equals(other.refClass)
					&& pcfSet.equals(other.pcfSet);
		}
		return false;
	}

	/**
	 * Returns the GroupingState for this CompoundOrFilter. The GroupingState
	 * indicates how this CompoundOrFilter can be combined with other
	 * PrimitiveChoiceFilters.
	 * 
	 * @return The GroupingState for this CompoundOrFilter.
	 */
	public GroupingState getGroupingState()
	{
		GroupingState state = GroupingState.EMPTY;
		for (PrimitiveChoiceFilter<T> pcf : pcfSet)
		{
			state = pcf.getGroupingState().add(state);
		}
		return state.compound(GroupingState.ALLOWS_UNION);
	}
}