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

import java.util.StringTokenizer;

import pcgen.cdom.base.Category;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;

/**
 * An AbilitySelection represents a "resolved" Ability, Nature and any choice
 * associated with that Ability.
 * 
 * This is generally used as the storage container when a selection has been
 * made from a token like ADD:FEAT
 */
public class AbilitySelection implements Comparable<AbilitySelection>
{

	/**
	 * The Ability that this AbilitySelection represents
	 */
	private final Ability ability;

	/**
	 * The Nature of the Ability as it should be applied to a PlayerCharacter
	 */
	private final Nature nature;

	/**
	 * The choice (association) made for the Ability in this AbilitySelection
	 */
	private final String selection;

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
	public AbilitySelection(Ability abil, Nature nat)
	{
		if (abil.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			throw new IllegalArgumentException(
					"AbilitySelection with MULT:YES Ability must have choices");
		}
		ability = abil;
		nature = nat;
		selection = null;
	}

	/**
	 * Creates a new AbilitySelection for the given Ability. The given Ability
	 * must be a MULT:YES Ability if the given selection is not null or this
	 * constructor will throw an exception.
	 * 
	 * @param abil
	 *            The Ability which this AbilitySelection will contain
	 * @param nat
	 *            The Nature of the given Ability as it should be applied to a
	 *            PlayerCharacter
	 * @param choice
	 *            The choice (association) made for the given Ability in this
	 *            AbilitySelection
	 */
	public AbilitySelection(Ability abil, Nature nat, String choice)
	{
		if (choice != null && !abil.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			throw new IllegalArgumentException(
					"AbilitySelection with MULT:NO Ability must not have choices");
		}
		if (choice == null && abil.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			throw new IllegalArgumentException(
					"AbilitySelection with MULT:YES Ability must have choices");
		}
		ability = abil;
		nature = nat;
		selection = choice;
	}

	/**
	 * Returns the key for the Ability in this AbilitySelection.
	 * 
	 * @return The key for the Ability in this AbilitySelection.
	 */
	public String getAbilityKey()
	{
		return ability.getKeyName();
	}

	/**
	 * Returns the Category for the Ability in this AbilitySelection.
	 * 
	 * @return The Category for the Ability in this AbilitySelection.
	 */
	public Category<Ability> getAbilityCategory()
	{
		return ability.getCDOMCategory();
	}

	/**
	 * Returns the "full" Key required to fully resolve both the Ability and the
	 * selection for this AbilitySelection. The choice is encoded in parenthesis
	 * after the ability key.
	 * 
	 * Note: This is primarily used for compatibility with "old" (5.x) style
	 * core objects and generally use of this method is discouraged.
	 * 
	 * @return The "full" Key required to fully resolve both the Ability and the
	 *         selection for this AbilitySelection.
	 */
	public String getFullAbilityKey()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(ability.getKeyName());
		if (selection != null && selection.length() > 0)
		{
			sb.append('(');
			sb.append(selection);
			sb.append(')');
		}
		return sb.toString();
	}

	/**
	 * Returns true if the choice for this AbilitySelection matches the given
	 * String. The null value is used to represent that this AbilitySelection
	 * has no choice (the underlying Ability is MULT:NO)
	 * 
	 * @param assoc
	 *            The String to be checked to determine if it matches the choice
	 *            for this AbilitySelection.
	 * @return true if the choice for this AbilitySelection matches the given
	 *         String; false otherwise
	 */
	public boolean containsAssociation(String assoc)
	{
		return assoc == null ? selection == null : assoc.equals(selection);
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
		StringBuilder sb = new StringBuilder();
		sb.append(ability.getDisplayName());
		if (selection != null && selection.length() > 0)
		{
			sb.append('(');
			sb.append(selection);
			sb.append(')');
		}
		return sb.toString();
	}

	/**
	 * Returns the choice (association) made for the Ability in this
	 * AbilitySelection
	 * 
	 * @return The choice (association) made for the Ability in this
	 *         AbilitySelection
	 */
	public String getSelection()
	{
		return selection;
	}

	/**
	 * Encodes the AbilitySelection into a String sufficient to uniquely
	 * identify the AbilitySelection. This may not sufficiently encode to be
	 * stored into a file or format which restricts certain characters (such as
	 * URLs), it simply encodes into an identifying String. There is no
	 * guarantee that this encoding is human readable, simply that the encoding
	 * is uniquely identifying such that the
	 * getAbilitySelectionFromPersistentFormat method of AbilitySelection is
	 * capable of decoding the String into an AbilitySelection.
	 * 
	 * @return A String sufficient to uniquely identify the AbilitySelection.
	 */
	public String getPersistentFormat()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("CATEGORY=");
		sb.append(ability.getCategory());
		sb.append('|');
		sb.append("NATURE=");
		sb.append(nature);
		sb.append('|');
		sb.append(ability.getDisplayName());
		if (selection != null)
		{
			sb.append('|');
			sb.append(selection);
		}
		return sb.toString();
	}

	/**
	 * Decodes the given String into an AbilitySelection. The String format to
	 * be passed into this method is defined solely by the return result of the
	 * getPersistentFormat method. There is no guarantee that the encoding is
	 * human readable, simply that the encoding is uniquely identifying such that
	 * this method is capable of decoding the String into an AbilitySelection.
	 * 
	 * @param persistentFormat
	 *            The String which should be decoded to provide an
	 *            AbilitySelection.
	 * 
	 * @return An AbilitySelection that was encoded in the given String.
	 */
	public static AbilitySelection getAbilitySelectionFromPersistentFormat(
			String persistentFormat)
	{
		StringTokenizer st = new StringTokenizer(persistentFormat,
				Constants.PIPE);
		String catString = st.nextToken();
		if (!catString.startsWith("CATEGORY="))
		{
			throw new IllegalArgumentException(
					"String in getAbilitySelectionFromPersistentFormat "
							+ "must start with CATEGORY=, found: "
							+ persistentFormat);
		}
		String cat = catString.substring(9);
		AbilityCategory ac = SettingsHandler.getGame().getAbilityCategory(cat);
		if (ac == null)
		{
			throw new IllegalArgumentException(
					"Category in getAbilitySelectionFromPersistentFormat "
							+ "must exist found: " + cat);
		}
		String natureString = st.nextToken();
		if (!natureString.startsWith("NATURE="))
		{
			throw new IllegalArgumentException(
					"Second argument in String in getAbilitySelectionFromPersistentFormat "
							+ "must start with NATURE=, found: "
							+ persistentFormat);
		}
		String natString = natureString.substring(7);
		Nature nat = Nature.valueOf(natString);
		String ab = st.nextToken();
		Ability a = Globals.getContext().ref.silentlyGetConstructedCDOMObject(
				Ability.class, ac, ab);
		if (a == null)
		{
			throw new IllegalArgumentException(
					"Third argument in String in getAbilitySelectionFromPersistentFormat "
							+ "must be an Ability, but it was not found: "
							+ persistentFormat);
		}
		String sel = null;
		if (st.hasMoreTokens())
		{
			/*
			 * No need to check for MULT:YES/NO here, as that is checked
			 * implicitly in the construction of AbilitySelection below
			 */
			sel = st.nextToken();
		}
		if (st.hasMoreTokens())
		{
			throw new IllegalArgumentException(
					"String in getAbilitySelectionFromPersistentFormat "
							+ "must have 3 or 4 arguments, but found more: "
							+ persistentFormat);
		}
		return new AbilitySelection(a, nat, sel);

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
		return ability;
	}

	@Override
	public int compareTo(AbilitySelection other)
	{
		int base = ability.compareTo(other.ability);
		if (base != 0)
		{
			return base;
		}
		if (selection == null)
		{
			return other.selection == null ? 0 : -1;
		}
		return other.selection == null ? 1 : selection
				.compareToIgnoreCase(other.selection);
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof AbilitySelection)
				&& compareTo((AbilitySelection) obj) == 0;
	}

	@Override
	public int hashCode()
	{
		return ability.hashCode();
	}
}
