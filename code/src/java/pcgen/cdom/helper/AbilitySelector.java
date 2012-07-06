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

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.ChooseResultActor;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.QualifyingObject;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.PersistenceLayerException;

/**
 * An AbilitySelection represents a "resolved" Ability, Nature and any choice
 * associated with that Ability.
 * 
 * This is generally used as the storage container when a selection has been
 * made from a token like ADD:FEAT
 */
public class AbilitySelector extends ConcretePrereqObject implements
		QualifyingObject, ChooseResultActor
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
	public void apply(PlayerCharacter pc, CDOMObject obj, String choice)
	{
		pc.addAppliedAbility(decodeChoice(obj, choice));
	}

	@Override
	public String getLstFormat() throws PersistenceLayerException
	{
		return "%LIST";
	}

	@Override
	public void remove(PlayerCharacter pc, CDOMObject obj, String choice)
	{
		pc.removeAppliedAbility(decodeChoice(obj, choice));
	}

	public CategorizedAbilitySelection decodeChoice(Object owner, String s)
	{
		Ability ability = Globals.getContext().ref
				.silentlyGetConstructedCDOMObject(Ability.class,
						AbilityCategory.FEAT, s);

		if (ability == null)
		{
			List<String> choices = new ArrayList<String>();
			String baseKey = AbilityUtilities.getUndecoratedName(s, choices);
			ability = Globals.getContext().ref
					.silentlyGetConstructedCDOMObject(Ability.class,
							AbilityCategory.FEAT, baseKey);
			if (ability == null)
			{
				throw new IllegalArgumentException("String in decodeChoice "
						+ "must be a Feat Key "
						+ "(or Feat Key with Selection if appropriate), was: "
						+ s);
			}
			return new CategorizedAbilitySelection(owner, AbilityCategory.FEAT,
					ability, Nature.AUTOMATIC, choices.get(0));
		}
		else if (ability.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			/*
			 * MULT:YES, CHOOSE:NOCHOICE can land here
			 * 
			 * TODO There needs to be better validation at some point that this
			 * is proper (meaning it is actually CHOOSE:NOCHOICE!)
			 */
			return new CategorizedAbilitySelection(owner, AbilityCategory.FEAT,
					ability, Nature.AUTOMATIC, "");
		}
		else
		{
			return new CategorizedAbilitySelection(owner, AbilityCategory.FEAT,
					ability, Nature.AUTOMATIC);
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
}
