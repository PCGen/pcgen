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
package pcgen.cdom.base;

import pcgen.cdom.enumeration.GroupingState;
import pcgen.core.PlayerCharacter;

/**
 * A PrimitiveChoiceFilter is an object that is designed to answer whether a
 * particular item is allowed to be presented as a choice for a given
 * PlayerCharacter.
 * 
 * The exact logic to determine what is allowed is up to the implementing class,
 * and is not constrained by PrimitiveChoiceFilter.
 * 
 * Note that some classes that implement PrimitiveChoiceFilter may return the
 * same results regardless of the PlayerCharacter given to the allow method,
 * while others may perform analysis that involves the PlayerCharacter
 * 
 * @param <T>
 *            The type of object analyzed by this PrimitiveChoiceFilter
 */
public interface PrimitiveChoiceFilter<T>
{

	PrimitiveChoiceFilter INVALID = new PrimitiveChoiceFilter(){

		public boolean allow(PlayerCharacter pc, Object item)
		{
			return false;
		}

		public GroupingState getGroupingState()
		{
			return GroupingState.INVALID;
		}

		public String getLSTformat()
		{
			return "ERROR";
		}

		public Class getReferenceClass()
		{
			return Object.class;
		}};

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
	public boolean allow(PlayerCharacter pc, T item);

	/**
	 * Returns the Class object representing the Class that this
	 * PrimitiveChoiceFilter evaluates.
	 * 
	 * @return Class object representing the Class that this
	 *         PrimitiveChoiceFilter evaluates
	 */
	public Class<? super T> getReferenceClass();

	/**
	 * Returns a representation of this PrimitiveChoiceFilter, suitable for
	 * storing in an LST file.
	 * 
	 * @return A representation of this PrimitiveChoiceFilter, suitable for
	 *         storing in an LST file.
	 */
	public String getLSTformat();

	/**
	 * Returns the GroupingState for this PrimitiveChoiceFilter. The
	 * GroupingState indicates how this PrimitiveChoiceFilter can be combined
	 * with other PrimitiveChoiceFilters.
	 * 
	 * @return The GroupingState for this PrimitiveChoiceFilter.
	 */
	public GroupingState getGroupingState();
}