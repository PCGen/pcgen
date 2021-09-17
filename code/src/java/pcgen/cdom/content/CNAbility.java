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

import java.util.List;
import java.util.Objects;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.QualifyingObject;
import pcgen.cdom.base.Reducible;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;

/**
 * An CNAbility represents an "unresolved" (categorized) Ability &amp; Nature.
 */
public class CNAbility extends ConcretePrereqObject
		implements QualifyingObject, Comparable<CNAbility>, ChooseDriver, Reducible
{

	private final Category<Ability> category;

	/**
	 * The Ability that this CNAbility represents
	 */
	private final Ability ability;

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
	CNAbility(Category<Ability> cat, Ability abil, Nature nat)
	{
		Objects.requireNonNull(cat, "Cannot build CNAbility with null Category");
		Objects.requireNonNull(abil, "Cannot build CNAbility with null Ability");
		Objects.requireNonNull(nat, "Cannot build CNAbility with null Nature");
		if (abil.getKeyName() == null || abil.getKeyName().isEmpty())
		{
			throw new IllegalArgumentException("Cannot build CNAbility when Ability has no key");
		}
		Category<Ability> origCategory = abil.getCDOMCategory();
		if (origCategory == null)
		{
			throw new IllegalArgumentException(
				"Cannot build CNAbility for " + abil + " when Ability has null original Category");
		}
		if (!cat.getParentCategory().equals(origCategory))
		{
			throw new IllegalArgumentException("Cannot build CNAbility for " + abil + " with incompatible Category: "
				+ cat + " is not compatible with Ability's Category: " + origCategory);
		}
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
		if (o instanceof CNAbility other)
		{
			return category.equals(other.category) && ability.equals(other.ability) && nature == other.nature;
		}
		return false;
	}

	@Override
	public int compareTo(CNAbility other)
	{
		final int equal = 0;

		if (this == other)
		{
			return equal;
		}

		// ability details
		int compare = this.ability.compareTo(other.ability);
		if (compare != equal)
		{
			return compare;
		}
		compare = this.category.toString().compareTo(other.category.toString());
		if (compare != equal)
		{
			return compare;
		}
		compare = this.nature.compareTo(other.nature);
		return compare;
	}

	@Override
	public ChooseInformation<?> getChooseInfo()
	{
		return ability.get(ObjectKey.CHOOSE_INFO);
	}

	@Override
	public Formula getSelectFormula()
	{
		return ability.getSafe(FormulaKey.SELECT);
	}

	@Override
	public List<ChooseSelectionActor<?>> getActors()
	{
		return ability.getListFor(ListKey.NEW_CHOOSE_ACTOR);
	}

	@Override
	public String getFormulaSource()
	{
		return ability.getKeyName();
	}

	@Override
	public Formula getNumChoices()
	{
		return ability.getSafe(FormulaKey.NUMCHOICES);
	}

	@Override
	public String getDisplayName()
	{
		return ability.getDisplayName();
	}

	@Override
	public CDOMObject getCDOMObject()
	{
		return ability;
	}
}
