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
 * 
 */
package pcgen.cdom.choiceset;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.core.PlayerCharacter;
import pcgen.util.Logging;

/**
 * A CompoundAndChoiceSet is a PrimitiveChoiceSet which is intended to contain
 * one or more PrimitiveChoiceSets that this object "joins" in an "and" format.
 * In other words, only if all of the underlying PrimitiveChoiceSet objects
 * contains an object will this CompoundAndChoiceSet contain the object.
 * 
 * @param <T>
 *            The Class of the underlying objects contained by this
 *            CompoundAndChoiceSet
 */
public class CompoundAndChoiceSet<T> implements PrimitiveChoiceSet<T>
{

	/**
	 * The list of underlying PrimitiveChoiceSets that this CompoundAndChoiceSet
	 * contains
	 */
	private final Set<PrimitiveChoiceSet<T>> pcsSet = new TreeSet<>(ChoiceSetUtilities::compareChoiceSets);

	/**
	 * Constructs a new CompoundAndChoiceSet which will contain objects
	 * contained by all of the PrimitiveChoiceSets in the given Collection.
	 * 
	 * This constructor is reference-semantic and value-semantic. Ownership of
	 * the Collection provided to this constructor is not transferred.
	 * Modification of the Collection (after this constructor completes) does
	 * not result in modifying the CompoundAndChoiceSet, and the
	 * CompoundAndChoiceSet will not modify the given Collection. However,
	 * strong references are maintained to the PrimitiveChoiceSet objects
	 * contained within the given Collection.
	 * 
	 * @param pcsCollection
	 *            A Collection of PrimitiveChoiceSets which define the Set of
	 *            objects contained within the CompoundAndChoiceSet
	 * @throws IllegalArgumentException
	 *             if the given Collection is null or empty.
	 */
	public CompoundAndChoiceSet(Collection<PrimitiveChoiceSet<T>> pcsCollection)
	{
		if (pcsCollection == null)
		{
			throw new IllegalArgumentException();
		}
		pcsSet.addAll(pcsCollection);
		if (pcsSet.size() != pcsCollection.size())
		{
			if (Logging.isLoggable(Level.WARNING))
			{
				Logging.log(Level.WARNING, "Found duplicate item in " + pcsCollection);
			}
			pcsSet.add(PrimitiveChoiceSet.getInvalid());
		}
	}

	/**
	 * Returns a Set containing the Objects which this CompoundAndChoiceSet
	 * contains.
	 * 
	 * Ownership of the Set returned by this method will be transferred to the
	 * calling object. Modification of the returned Set should not result in
	 * modifying the CompoundAndChoiceSet, and modifying the
	 * CompoundAndChoiceSet after the Set is returned should not modify the Set.
	 * However, modification of the PCClass objects contained within the
	 * returned set will result in modification of the PCClass objects contained
	 * within this CompoundAndChoiceSet.
	 * 
	 * @param pc
	 *            The PlayerCharacter for which the choices in this
	 *            CompoundAndChoiceSet should be returned.
	 * @return A Set containing the Objects which this CompoundAndChoiceSet
	 *         contains.
	 */
	@Override
	public Collection<? extends T> getSet(PlayerCharacter pc)
	{
		Collection<? extends T> returnSet = null;
		for (PrimitiveChoiceSet<T> cs : pcsSet)
		{
			if (returnSet == null)
			{
				returnSet = cs.getSet(pc);
			}
			else
			{
				returnSet.retainAll(cs.getSet(pc));
			}
		}
		return returnSet;
	}

	/**
	 * Returns a representation of this CompoundAndChoiceSet, suitable for
	 * storing in an LST file.
	 * 
	 * @param useAny
	 *            use "ANY" for the global "ALL" reference when creating the LST
	 *            format
	 * @return A representation of this CompoundAndChoiceSet, suitable for
	 *         storing in an LST file.
	 */
	@Override
	public String getLSTformat(boolean useAny)
	{
		return ChoiceSetUtilities.joinLstFormat(pcsSet, Constants.COMMA, useAny);
	}

	/**
	 * The class of object this CompoundAndChoiceSet contains.
	 * 
	 * @return The class of object this CompoundAndChoiceSet contains.
	 */
	@Override
	public Class<? super T> getChoiceClass()
	{
		return pcsSet == null ? null : pcsSet.iterator().next().getChoiceClass();
	}

	/**
	 * Returns the consistent-with-equals hashCode for this CompoundAndChoiceSet
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return pcsSet.hashCode();
	}

	/**
	 * Returns true if this CompoundAndChoiceSet is equal to the given Object.
	 * Equality is defined as being another CompoundAndChoiceSet object with
	 * equal underlying contents.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof CompoundAndChoiceSet) && ((CompoundAndChoiceSet<?>) obj).pcsSet.equals(pcsSet);
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
		GroupingState state = GroupingState.EMPTY;
		for (PrimitiveChoiceSet<T> pcs : pcsSet)
		{
			state = pcs.getGroupingState().add(state);
		}
		return state.compound(GroupingState.ALLOWS_INTERSECTION);
	}
}
