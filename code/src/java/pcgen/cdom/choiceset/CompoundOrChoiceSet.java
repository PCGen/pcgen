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
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.core.PlayerCharacter;
import pcgen.util.Logging;

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
	private final Set<PrimitiveChoiceSet<T>> pcsSet = new TreeSet<>(ChoiceSetUtilities::compareChoiceSets);

	private final String separator;

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
	 * @param pcsCollection
	 *            A Collection of PrimitiveChoiceSets which define the Set of
	 *            objects contained within the CompoundOrChoiceSet
	 * @throws IllegalArgumentException
	 *             if the given Collection is null or empty.
	 */
	public CompoundOrChoiceSet(Collection<PrimitiveChoiceSet<T>> pcsCollection)
	{
		this(pcsCollection, Constants.PIPE);
	}

	public CompoundOrChoiceSet(Collection<PrimitiveChoiceSet<T>> pcsCollection, String sep)
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
		separator = sep;
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
	@Override
	public Set<T> getSet(PlayerCharacter pc)
	{
		Set<T> returnSet = new HashSet<>();
		for (PrimitiveChoiceSet<T> cs : pcsSet)
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
	@Override
	public String getLSTformat(boolean useAny)
	{
		return ChoiceSetUtilities.joinLstFormat(pcsSet, separator, useAny);
	}

	/**
	 * The class of object this CompoundOrChoiceSet contains.
	 * 
	 * @return The class of object this CompoundOrChoiceSet contains.
	 */
	@Override
	public Class<? super T> getChoiceClass()
	{
		return pcsSet.iterator().next().getChoiceClass();
	}

	@Override
	public int hashCode()
	{
		return pcsSet.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof CompoundOrChoiceSet) && ((CompoundOrChoiceSet<?>) obj).pcsSet.equals(pcsSet);
	}

	/**
	 * Returns the GroupingState for this CompoundOrChoiceSet. The GroupingState
	 * indicates how this CompoundOrChoiceSet can be combined with other
	 * PrimitiveChoiceSets.
	 * 
	 * @return The GroupingState for this CompoundOrChoiceSet.
	 */
	@Override
	public GroupingState getGroupingState()
	{
		GroupingState state = GroupingState.EMPTY;
		for (PrimitiveChoiceSet<T> pcs : pcsSet)
		{
			state = pcs.getGroupingState().add(state);
		}
		return state.compound(GroupingState.ALLOWS_UNION);
	}
}
