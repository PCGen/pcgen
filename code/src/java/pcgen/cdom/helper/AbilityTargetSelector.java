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

import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.QualifyingObject;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.PersistenceLayerException;

/**
 * An AbilitySelection represents a "resolved" Ability, Nature and any choice
 * associated with that Ability.
 * 
 * This is generally used as the storage container when a selection has been
 * made from a token like ADD:FEAT
 * 
 * @param <T> The type of object that this AbilityTargetSelector can select
 */
public class AbilityTargetSelector<T> extends ConcretePrereqObject implements QualifyingObject, ChooseSelectionActor<T>
{

	private final String source;

	private final CDOMSingleRef<AbilityCategory> category;

	/**
	 * The Ability that this AbilitySelection represents
	 */
	private final CDOMSingleRef<Ability> ability;

	/**
	 * The Nature of the Ability as it should be applied to a PlayerCharacter
	 */
	private final Nature nature;

	/**
	 * Creates a new AbilitySelection for the given Ability. The given Ability
	 * must be a MULT:NO Ability or this constructor will throw an exception.
	 * 
	 * @param abil
	 *            The Ability which this AbilitySelection will contain
	 * @param nat
	 *            The Nature of the given Ability as it should be applied to a
	 *            PlayerCharacter
	 */
	public AbilityTargetSelector(String token, CDOMSingleRef<AbilityCategory> cat, CDOMSingleRef<Ability> abil,
		Nature nat)
	{
		category = cat;
		ability = abil;
		nature = nat;
		source = token;
	}

	/**
	 * Returns the Category for the Ability in this AbilitySelection.
	 * 
	 * @return The Category for the Ability in this AbilitySelection.
	 */
	public CDOMSingleRef<AbilityCategory> getAbilityCategory()
	{
		return category;
	}

	/**
	 * Returns a String representation of this AbilitySelection. The choice is
	 * encoded in parenthesis after the Ability's name.
	 * 
	 * Note: Since this does not depend on the key of the underlying Ability, it
	 * is an unreliable method to persistently store information about this
	 * AbilitySelection. If persistent storage is required, you should be using
	 * getPersistentFormat()
	 * 
	 * @return A String representation of this AbilitySelection.
	 */
	@Override
	public String toString()
	{
		String sb = ability.get().getDisplayName()
				+ '('
				+ Constants.LST_PERCENT_LIST
				+ ')';
		return sb;
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

	/**
	 * Returns the Ability that this AbilitySelection represents
	 * 
	 * @return The Ability that this AbilitySelection represents
	 */
	public Ability getAbility()
	{
		return ability.get();
	}

	@Override
	public void applyChoice(ChooseDriver obj, T choice, PlayerCharacter pc)
	{
		Ability ab = ability.get();
		ChooseInformation ci = ab.get(ObjectKey.CHOOSE_INFO);
		detailedApply(obj, ci, choice, pc);
	}

	private void detailedApply(ChooseDriver obj, ChooseInformation<T> ci, T choice, PlayerCharacter pc)
	{
		String string = ci.encodeChoice(choice);
		CNAbilitySelection appliedSelection =
				new CNAbilitySelection(CNAbilityFactory.getCNAbility(category.get(), nature, ability.get()), string);
		appliedSelection.addAllPrerequisites(getPrerequisiteList());
		pc.addAbility(appliedSelection, obj, this);
	}

	@Override
	public String getLstFormat() {
		return ability.getLSTformat(false);
	}

	@Override
	public String getSource()
	{
		return source;
	}

	@Override
	public void removeChoice(ChooseDriver obj, T choice, PlayerCharacter pc)
	{
		Ability ab = ability.get();
		ChooseInformation ci = ab.get(ObjectKey.CHOOSE_INFO);
		detailedRemove(obj, ci, choice, pc);
	}

	private void detailedRemove(ChooseDriver obj, ChooseInformation<T> ci, T choice, PlayerCharacter pc)
	{
		String string = ci.encodeChoice(choice);
		CNAbilitySelection appliedSelection =
				new CNAbilitySelection(CNAbilityFactory.getCNAbility(category.get(), nature, ability.get()), string);
		pc.removeAbility(appliedSelection, obj, this);
	}

	@Override
	public int hashCode()
	{
		return ability.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof AbilityTargetSelector)
		{
			AbilityTargetSelector<?> other = (AbilityTargetSelector<?>) o;
			return source.equals(other.source) && category.equals(other.category) && ability.equals(other.ability)
				&& nature == other.nature;
		}
		return false;
	}

	@Override
	public Class<T> getChoiceClass()
	{
		return (Class<T>) ability.get().get(ObjectKey.CHOOSE_INFO).getReferenceClass();
	}
}
