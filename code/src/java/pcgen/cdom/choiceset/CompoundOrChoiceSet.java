/*
 * Copyright 2006 (C) Tom Parker <thpr@sourceforge.net>
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
 * 
 * Created on October 29, 2006.
 * 
 * Current Ver: $Revision: 1111 $ Last Editor: $Author: boomer70 $ Last Edited:
 * $Date: 2006-06-22 21:22:44 -0400 (Thu, 22 Jun 2006) $
 */
package pcgen.cdom.choiceset;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.core.PlayerCharacter;

/**
 * A CompoundOrChoiceSet is a PrimitiveChoiceSet which is intended to contain
 * one or more PrimitiveChoiceSets that this object "joins" in an "or" format.
 * In other words, if any one of the underlying PrimitiveChoiceSet objects
 * contains an object, then this CompoundOrChoiceSet will contain the object.
 * 
 * @param <T>
 *            The Class of the underlying objects contained by this
 *            CompoundOrChoiceSet
 */
public class CompoundOrChoiceSet<T> implements PrimitiveChoiceSet<T>
{

	/**
	 * The list of underlying PrimitiveChoiceSets that this CompoundOrChoiceSet
	 * contains
	 */
	private final Set<PrimitiveChoiceSet<T>> set = new TreeSet<PrimitiveChoiceSet<T>>(
			ChoiceSetUtilities.WRITEABLE_SORTER);

	/**
	 * Constructs a new CompoundOrChoiceSet which will contain objects contained
	 * by the PrimitiveChoiceSets in the given Collection.
	 * 
	 * This constructor is reference-semantic and value-semantic. Ownership of
	 * the Collection provided to this constructor is not transferred.
	 * Modification of the Collection (after this constructor completes) does
	 * not result in modifying the CompoundOrChoiceSet, and the
	 * CompoundOrChoiceSet will not modify the given Collection. However, strong
	 * references are maintained to the PrimitiveChoiceSet objects contained
	 * within the given Collection.
	 * 
	 * @param col
	 *            A Collection of PrimitiveChoiceSets which define the Set of
	 *            objects contained within the CompoundOrChoiceSet
	 * @throws IllegalArgumentException
	 *             if the given Collection is null or empty.
	 */
	public CompoundOrChoiceSet(Collection<PrimitiveChoiceSet<T>> col)
	{
		if (col == null)
		{
			throw new IllegalArgumentException();
		}
		set.addAll(col);
	}

	/**
	 * Returns a Set containing the Objects which this CompoundOrChoiceSet
	 * contains.
	 * 
	 * Ownership of the Set returned by this method will be transferred to the
	 * calling object. Modification of the returned Set should not result in
	 * modifying the CompoundOrChoiceSet, and modifying the CompoundOrChoiceSet
	 * after the Set is returned should not modify the Set. However,
	 * modification of the PCClass objects contained within the returned set
	 * will result in modification of the PCClass objects contained within this
	 * CompoundOrChoiceSet.
	 * 
	 * @param pc
	 *            The PlayerCharacter for which the choices in this
	 *            CompoundOrChoiceSet should be returned.
	 * @return A Set containing the Objects which this CompoundOrChoiceSet
	 *         contains.
	 */
	public Set<T> getSet(PlayerCharacter pc)
	{
		Set<T> returnSet = new HashSet<T>();
		for (PrimitiveChoiceSet<T> cs : set)
		{
			returnSet.addAll(cs.getSet(pc));
		}
		return returnSet;
	}

	/**
	 * Returns a representation of this CompoundOrChoiceSet, suitable for
	 * storing in an LST file.
	 * 
	 * @param useAny
	 *            use "ANY" for the global "ALL" reference when creating the LST
	 *            format
	 * @return A representation of this CompoundOrChoiceSet, suitable for
	 *         storing in an LST file.
	 */
	public String getLSTformat(boolean useAny)
	{
		return ChoiceSetUtilities.joinLstFormat(set, Constants.COMMA, useAny);
	}

	/**
	 * The class of object this CompoundOrChoiceSet contains.
	 * 
	 * @return The class of object this CompoundOrChoiceSet contains.
	 */
	public Class<? super T> getChoiceClass()
	{
		return set == null ? null : set.iterator().next().getChoiceClass();
	}

	/**
	 * Returns the consistent-with-equals hashCode for this CompoundOrChoiceSet
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return set.hashCode();
	}

	/**
	 * Returns true if this CompoundOrChoiceSet is equal to the given Object.
	 * Equality is defined as being another CompoundOrChoiceSet object with
	 * equal underlying contents.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		return (o instanceof CompoundOrChoiceSet)
				&& ((CompoundOrChoiceSet<?>) o).set.equals(set);
	}

	/**
	 * Returns the GroupingState for this CompoundOrChoiceSet. The GroupingState
	 * indicates how this CompoundOrChoiceSet can be combined with other
	 * PrimitiveChoiceSets.
	 * 
	 * @return The GroupingState for this CompoundOrChoiceSet.
	 */
	public GroupingState getGroupingState()
	{
		GroupingState gs = GroupingState.EMPTY;
		for (PrimitiveChoiceSet<T> cs : set)
		{
			gs = cs.getGroupingState().add(gs);
		}
		return gs.compound(GroupingState.ALLOWS_UNION);
	}
}
