/*
 * Copyright (c) 2008-14 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.content;

import pcgen.cdom.base.Category;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.QualifyingObject;
import pcgen.cdom.enumeration.Nature;
import pcgen.core.Ability;

/**
 * An CNAbility represents an "unresolved" (categorized) Ability & Nature.
 */
public class CNAbility extends ConcretePrereqObject implements
		QualifyingObject, Comparable<CNAbility>
{

	private final Category<Ability> category;

	/**
	 * The Ability that this CNAbility represents
	 */
	private Ability ability;
//	private final Ability ability;

	/**
	 * The Nature of the Ability
	 */
	private final Nature nature;

	/**
	 * Creates a new CNAbility for the given Ability.
	 * 
	 * @param abil
	 *            The Ability which this CNAbility will contain
	 * @param nat
	 *            The Nature of the given Ability
	 */
	public CNAbility(Category<Ability> cat, Ability abil, Nature nat)
	{
		category = cat;
		ability = abil;
		nature = nat;
	}

	/**
	 * Returns the key for the Ability in this CNAbility.
	 * 
	 * @return The key for the Ability in this CNAbility.
	 */
	public String getAbilityKey()
	{
		return ability.getKeyName();
	}

	/**
	 * Returns the Category for the Ability in this CNAbility.
	 * 
	 * @return The Category for the Ability in this CNAbility.
	 */
	public Category<Ability> getAbilityCategory()
	{
		return category;
	}

	/**
	 * Returns a String representation of this CNAbility. 
	 * 
	 * @return A String representation of this CNAbility.
	 */
	@Override
	public String toString()
	{
		return ability.getDisplayName();
	}

	/**
	 * Returns the Nature of the Ability
	 * 
	 * @return The Nature of the Ability
	 */
	public Nature getNature()
	{
		return nature;
	}

	/**
	 * Returns the Ability that this CNAbility represents
	 * 
	 * @return The Ability that this CNAbility represents
	 */
	public Ability getAbility()
	{
		return ability;
	}

	@Override
	public int hashCode()
	{
		return ability.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof CNAbility)
		{
			CNAbility other = (CNAbility) o;
			return category.equals(other.category)
					&& ability.equals(other.ability)
					&& nature.equals(other.nature);
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(CNAbility other)
	{
	    final int EQUAL = 0;

		if (this == other)
		{
			return EQUAL;
		}
		
		// ability details
		int compare = this.ability.compareTo(other.ability);
		if (compare != EQUAL)
		{
			return compare;
		}
		compare = this.category.toString().compareTo(other.category.toString());
		if (compare != EQUAL)
		{
			return compare;
		}
		compare = this.nature.compareTo(other.nature);
		return compare;
	}

	public void doMagicalAndEvilThings(Ability clone)
	{
		ability = clone;
	}

}
