/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.helper;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.core.Ability;

/**
 * An AbilityRef represents a reference to a Specific Ability with a set choice.
 * 
 * This is typically used for tokens where an Ability is directly granted, or a
 * selection of only a specific choice for an Ability is allowed.
 */
public class AbilityRef
{

	/**
	 * A reference to the Ability which this AbilityRef contains
	 */
	private final CDOMReference<Ability> abilities;

	/**
	 * The specific choice (association) for the Ability this AbilityRef
	 * contains. May remain null if the given Ability does not have a specific
	 * choice (or does not require a specific choice)
	 */
	private String choice = null;

	/**
	 * Constructs a new AbilityRef for the Ability in the given reference.
	 * 
	 * @param ab
	 *            A reference to the Ability which this AbilityRef contains
	 */
	public AbilityRef(CDOMReference<Ability> ab)
	{
		abilities = ab;
	}

	/**
	 * Sets the specific choice (association) for the Ability this AbilityRef
	 * contains.
	 * 
	 * @param s
	 *            The specific choice (association) for the Ability this
	 *            AbilityRef contains.
	 */
	public void setChoice(String s)
	{
		choice = s;
	}

	/**
	 * Returns the reference to the Ability that this AbilityRef contains
	 * 
	 * @return The reference to the Ability that this AbilityRef contains
	 */
	public CDOMReference<Ability> getRef()
	{
		return abilities;
	}

	/**
	 * Returns the specific choice (association) for the Ability this AbilityRef
	 * contains.
	 * 
	 * @return The specific choice (association) for the Ability this AbilityRef
	 *         contains.
	 */
	public String getChoice()
	{
		return choice;
	}

	/**
	 * Returns true if the given object is a AbilityRef with identical
	 * underlying Ability reference and choice String.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof AbilityRef)
		{
			AbilityRef other = (AbilityRef) obj;
			if (other.abilities.equals(abilities))
			{
				if (choice == null)
				{
					return other.choice == null;
				}
				else
				{
					return choice.equals(other.choice);
				}
			}
		}
		return false;
	}

	/**
	 * Returns a consistent-with-equals hashCode for this AbilityRef
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return 3 - abilities.hashCode();
	}

	/**
	 * Returns the GroupingState for this AbilityRef. The GroupingState
	 * indicates how this AbilityRef can be combined with other AbilityRefs.
	 * 
	 * @return The GroupingState for this AbilityRef.
	 */
	public GroupingState getGroupingState()
	{
		return abilities.getGroupingState();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "AbilityRef " + choice + " for " + abilities;
	}
	
	
}
