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
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.core.PlayerCharacter;

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
	private final Set<PrimitiveChoiceSet<T>> set = new TreeSet<PrimitiveChoiceSet<T>>(
			ChoiceSetUtilities.WRITEABLE_SORTER);

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
	 * @param col
	 *            A Collection of PrimitiveChoiceSets which define the Set of
	 *            objects contained within the CompoundAndChoiceSet
	 * @throws IllegalArgumentException
	 *             if the given Collection is null or empty.
	 */
	public CompoundAndChoiceSet(Collection<PrimitiveChoiceSet<T>> coll)
	{
		if (coll == null)
		{
			throw new IllegalArgumentException();
		}
		set.addAll(coll);
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
	public Set<T> getSet(PlayerCharacter pc)
	{
		Set<T> returnSet = null;
		for (PrimitiveChoiceSet<T> cs : set)
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
	public String getLSTformat(boolean useAny)
	{
		return ChoiceSetUtilities.joinLstFormat(set, Constants.COMMA, useAny);
	}

	/**
	 * The class of object this CompoundAndChoiceSet contains.
	 * 
	 * @return The class of object this CompoundAndChoiceSet contains.
	 */
	public Class<? super T> getChoiceClass()
	{
		return set == null ? null : set.iterator().next().getChoiceClass();
	}

	/**
	 * Returns the consistent-with-equals hashCode for this CompoundAndChoiceSet
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return set.hashCode();
	}

	/**
	 * Returns true if this CompoundAndChoiceSet is equal to the given Object.
	 * Equality is defined as being another CompoundAndChoiceSet object with
	 * equal underlying contents.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		return (o instanceof CompoundAndChoiceSet)
				&& ((CompoundAndChoiceSet<?>) o).set.equals(set);
	}
}
