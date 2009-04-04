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
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChoiceFilterUtilities;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrimitiveChoiceFilter;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.core.PlayerCharacter;

/**
 * A RetainingChooser is a PrimitiveChoiceSet which is intended to contain one
 * or more PrimitiveChoiceFilters that determine whether this RetainingChooser
 * contains objects that are in a provided base set of objects.
 * 
 * The RetainingChooser "joins" in an "or" format, meaning only one of the
 * PrimitiveChoiceFilters needs to allow a given object in the base set for the
 * RetainingChooser to contain that object. This also means that if a
 * RetainingChooser contains no PrimitiveChoiceFilters, then it will be empty.
 * 
 * @param <T>
 *            The Class of the underlying objects contained by this
 *            RetainingChooser
 */
public class RetainingChooser<T extends CDOMObject> implements
		PrimitiveChoiceSet<T>
{

	/**
	 * The Set of underlying PrimitiveChoiceFitlers that this RetainingChooser
	 * contains and uses to filter the base set.
	 */
	private final Set<PrimitiveChoiceFilter<? super T>> retainingSet = new HashSet<PrimitiveChoiceFilter<? super T>>();

	/**
	 * The base set of objects that this this RetainingChooser contains and
	 * filters with the PrimitiveChoiceFitlers that this RetainingChooser
	 * contains.
	 */
	private final CDOMGroupRef<T> baseSet;

	/**
	 * Constructs a new RetainingChooser for the given Class of objects, and
	 * which has the given CDOMGroupRef as the base set of objects (that the
	 * RetainingChooser will filter with the contained PrimitiveChoiceFilters)
	 * 
	 * @param cl
	 *            The class of objects that this RetainingChooser contains.
	 * @param allRef
	 *            The base set of objects from which this RetainingChooser
	 *            starts before it applies any PrimitiveChoiceFilters
	 */
	public RetainingChooser(Class<T> cl, CDOMGroupRef<T> allRef)
	{
		super();
		if (cl == null)
		{
			throw new IllegalArgumentException(
					"Class for RetainingChooser cannot be null");
		}
		if (allRef == null)
		{
			throw new IllegalArgumentException(
					"Base Set for RetainingChooser cannot be null");
		}
		baseSet = allRef;
	}

	/**
	 * Adds the given PrimitiveChoiceFilter to this RetainingChooser
	 * 
	 * @param cs
	 *            The PrimitiveChoiceFilter to be added to this RetainingChooser
	 */
	public void addRetainingChoiceFilter(PrimitiveChoiceFilter<? super T> cs)
	{
		if (cs == null)
		{
			throw new IllegalArgumentException(
					"PrimitiveChoiceFilter to be added cannot be null");
		}
		retainingSet.add(cs);
	}

	/**
	 * Adds all of the PrimitiveChoiceFilters in the given Collection to this
	 * RetainingChooser
	 * 
	 * This method is reference-semantic and value-semantic. Ownership of the
	 * Collection provided to this method is not transferred. Modification of
	 * the Collection (after this method completes) does not result in modifying
	 * the RetainingChooser, and the RetainingChooser will not modify the given
	 * Collection. However, strong references are maintained to the
	 * PrimitiveChoiceFilter objects contained within the given Collection.
	 * 
	 * @param cs
	 *            The PrimitiveChoiceFilter to be added to this RetainingChooser
	 */
	public void addAllRetainingChoiceFilters(
			Collection<PrimitiveChoiceFilter<T>> coll)
	{
		if (coll == null)
		{
			throw new IllegalArgumentException(
					"Collection of PrimitiveChoiceFilters to be added cannot be null");
		}
		retainingSet.addAll(coll);
	}

	/**
	 * Returns a Set containing the Objects which this RetainingChooser
	 * contains.
	 * 
	 * Ownership of the Set returned by this method will be transferred to the
	 * calling object. Modification of the returned Set should not result in
	 * modifying the RetainingChooser, and modifying the RetainingChooser after
	 * the Set is returned should not modify the Set. However, modification of
	 * the PCClass objects contained within the returned set will result in
	 * modification of the PCClass objects contained within this
	 * RetainingChooser.
	 * 
	 * @param pc
	 *            The PlayerCharacter for which the choices in this
	 *            RetainingChooser should be returned.
	 * @return A Set containing the Objects which this RetainingChooser
	 *         contains.
	 */
	public Set<T> getSet(PlayerCharacter pc)
	{
		Set<T> choices = new HashSet<T>();
		if (retainingSet != null)
		{
			choices.addAll(baseSet.getContainedObjects());
			RETAIN: for (Iterator<T> it = choices.iterator(); it.hasNext();)
			{
				for (PrimitiveChoiceFilter<? super T> cf : retainingSet)
				{
					if (cf.allow(pc, it.next()))
					{
						continue RETAIN;
					}
				}
				it.remove();
			}
		}
		return choices;
	}

	/**
	 * Returns a representation of this RetainingChooser, suitable for storing
	 * in an LST file.
	 * 
	 * @param useAny
	 *            use "ANY" for the global "ALL" reference when creating the LST
	 *            format
	 * @return A representation of this RetainingChooser, suitable for storing
	 *         in an LST file.
	 */
	public String getLSTformat(boolean useAny)
	{
		Set<PrimitiveChoiceFilter<? super T>> sortSet = new TreeSet<PrimitiveChoiceFilter<? super T>>(
				ChoiceFilterUtilities.FILTER_SORTER);
		sortSet.addAll(retainingSet);
		return ChoiceFilterUtilities.joinLstFormat(sortSet, Constants.PIPE);
	}

	/**
	 * The class of object this RetainingChooser contains.
	 * 
	 * @return The class of object this RetainingChooser contains.
	 */
	public Class<? super T> getChoiceClass()
	{
		return baseSet.getReferenceClass();
	}

	/**
	 * Returns the consistent-with-equals hashCode for this RetainingChooser
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return baseSet.hashCode() + retainingSet.size();
	}

	/**
	 * Returns true if this RetainingChooser is equal to the given Object.
	 * Equality is defined as being another RetainingChooser object with equal
	 * underlying contents.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof RetainingChooser)
		{
			RetainingChooser<?> other = (RetainingChooser<?>) o;
			return baseSet.equals(other.baseSet)
					&& retainingSet.equals(other.retainingSet);
		}
		return false;
	}

	/**
	 * Returns the GroupingState for this RetainingChooser. The GroupingState
	 * indicates how this RetainingChooser can be combined with other
	 * PrimitiveChoiceSets.
	 * 
	 * @return The GroupingState for this RetainingChooser.
	 */
	public GroupingState getGroupingState()
	{
		GroupingState gs = GroupingState.EMPTY;
		for (PrimitiveChoiceFilter<? super T> cs : retainingSet)
		{
			gs = cs.getGroupingState().add(gs);
		}
		return gs.compound(GroupingState.ALLOWS_UNION);
	}
}
