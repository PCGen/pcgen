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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.QualifyingObject;
import pcgen.cdom.content.AbilitySelection;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.Nature;
import pcgen.core.Ability;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.PersistenceLayerException;

/**
 * An AbilitySelector represents an Ability to be applied from an
 * AbilitySelection.
 * 
 * This is for use in a case like AUTO:FEAT|%LIST where the Category and Nature
 * are known by the token, and the Ability and Selection are known by CHOOSE
 */
public class AbilitySelector extends ConcretePrereqObject implements
		QualifyingObject, ChooseSelectionActor<AbilitySelection>
{

	private final String source;

	private final Category<Ability> category;

	/**
	 * The Nature of the Ability as it should be applied to a PlayerCharacter
	 */
	private final Nature nature;

	/**
	 * Creates a new AbilitySelection for the given Ability. The given Ability
	 * must be a MULT:NO Ability or this constructor will throw an exception.
	 * 
	 * @param token
	 *            The Ability which this AbilitySelection will contain
	 * @param cat
	 * 			  The Ability category which this AbilitySelection will contain
	 * @param nat
	 *            The Nature of the given Ability as it should be applied to a
	 *            PlayerCharacter
	 */
	public AbilitySelector(String token, Category<Ability> cat, Nature nat)
	{
		category = cat;
		nature = nat;
		source = token;
	}

	/**
	 * Returns the Category for the Ability in this AbilitySelection.
	 * 
	 * @return The Category for the Ability in this AbilitySelection.
	 */
	public Category<Ability> getAbilityCategory()
	{
		return category;
	}

	/**
	 * Returns the Nature of the Ability as it should be applied to a
	 * PlayerCharacter
	 * 
	 * @return The Nature of the Ability as it should be applied to a
	 *         PlayerCharacter
	 */
	public Nature getNature()
	{
		return nature;
	}

	@Override
	public void applyChoice(CDOMObject obj, AbilitySelection as,
		PlayerCharacter pc)
	{
		CNAbility cna = new CNAbility(category, as.getObject(), nature);
		CNAbilitySelection cnas = new CNAbilitySelection(cna, as.getSelection());
		pc.associateSelection(as, cnas);
		pc.addAppliedAbility(obj, cnas);
	}

	@Override
	public String getLstFormat() throws PersistenceLayerException
	{
		return "%LIST";
	}

	@Override
	public void removeChoice(CDOMObject obj, AbilitySelection as,
		PlayerCharacter pc)
	{
		CNAbilitySelection cnas = pc.getAssociatedSelection(as);
		if (cnas == null)
		{
			//error??
		}
		else
		{
			pc.removeAppliedAbility(obj, cnas);
		}
	}

	@Override
	public String getSource()
	{
		return source;
	}

	@Override
	public int hashCode()
	{
		return category.hashCode() ^ nature.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof AbilitySelector)
		{
			AbilitySelector other = (AbilitySelector) o;
			return source.equals(other.source)
					&& category.equals(other.category)
					&& nature.equals(other.nature);
		}
		return false;
	}

	public Class<AbilitySelection> getChoiceClass()
	{
		return AbilitySelection.class;
	}
}
