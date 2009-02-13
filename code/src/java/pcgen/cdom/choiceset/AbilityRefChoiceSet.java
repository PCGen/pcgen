/*
 * Copyright 2006 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.AbilityRef;
import pcgen.cdom.helper.AbilitySelection;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.core.Ability.Nature;
import pcgen.core.chooser.ChooserUtilities;

/**
 * A AbilityRefChoiceSet contains references to AbilityRef Objects.
 * 
 * The contents of a AbilityRefChoiceSet is defined at construction of the
 * AbilityRefChoiceSet. The contents of a AbilityRefChoiceSet is fixed, and will
 * not vary by the PlayerCharacter used to resolve the AbilityRefChoiceSet.
 */
public class AbilityRefChoiceSet implements
		PrimitiveChoiceSet<AbilitySelection>
{

	/**
	 * The underlying Set of CDOMReferences that contain the objects in this
	 * AbilityRefChoiceSet
	 */
	private final Set<AbilityRef> set;

	/**
	 * The underlying Ability Category for this AbilityRefChoiceSet.
	 */
	private final Category<Ability> category;

	/**
	 * The underlying Ability Nature for this AbilityRefChoiceSet.
	 */
	private final Ability.Nature nature;

	/**
	 * Constructs a new AbilityRefChoiceSet which contains the Set of objects
	 * contained within the given CDOMReferences. The CDOMReferences do not need
	 * to be resolved at the time of construction of the AbilityRefChoiceSet.
	 * 
	 * This constructor is reference-semantic and value-semantic, meaning that
	 * ownership of the Collection provided to this constructor is not
	 * transferred. Modification of the Collection (after this constructor
	 * completes) does not result in modifying the AbilityRefChoiceSet, and the
	 * AbilityRefChoiceSet will not modify the given Collection. However, strong
	 * references are kept to the AbilityRef objects contained within the given
	 * Collection.
	 * 
	 * @param cat
	 *            The Ability Category of Ability objects that this
	 *            AbilityRefChoiceSet refers to.
	 * @param n
	 *            The Ability Nature of the Ability objects as they should be
	 *            applied to a PlayerCharacter
	 * @param allowDupe
	 *            True if this AbilityRefChoiceSet should allow duplicates in
	 *            the returned Set of AbilitySelection objects; false otherwise
	 * @param dupCount
	 *            Sets the limit of duplicate objects (or stacking limit) to
	 *            identify how many times an Ability may be available to a
	 *            PlayerCharacter using an AbilityRefChoiceSet. Ignored if
	 *            allowDupe is false.
	 * @param col
	 *            A Collection of CDOMReferences which define the Set of objects
	 *            contained within the AbilityRefChoiceSet
	 * @throws IllegalArgumentException
	 *             if the given Collection is null or empty.
	 */
	public AbilityRefChoiceSet(Category<Ability> cat,
			Collection<? extends AbilityRef> col, Nature n, boolean allowDupe,
			int dupCount)
	{
		super();
		if (col == null)
		{
			throw new IllegalArgumentException(
					"Choice Collection cannot be null");
		}
		if (col.isEmpty())
		{
			throw new IllegalArgumentException(
					"Choice Collection cannot be empty");
		}
		set = new HashSet<AbilityRef>(col);
		if (n == null)
		{
			throw new IllegalArgumentException("Choice Nature cannot be null");
		}
		nature = n;
		if (cat == null)
		{
			throw new IllegalArgumentException("Choice Category cannot be null");
		}
		category = cat;
	}

	/**
	 * Returns a representation of this AbilityRefChoiceSet, suitable for
	 * storing in an LST file.
	 * 
	 * @param useAny
	 *            use "ANY" for the global "ALL" reference when creating the LST
	 *            format
	 * @return A representation of this AbilityRefChoiceSet, suitable for
	 *         storing in an LST file.
	 */
	public String getLSTformat(boolean useAny)
	{
		Set<CDOMReference<?>> sortedSet = new TreeSet<CDOMReference<?>>(
				ReferenceUtilities.REFERENCE_SORTER);
		for (AbilityRef ar : set)
		{
			sortedSet.add(ar.getRef());
		}
		return ReferenceUtilities.joinLstFormat(sortedSet, Constants.COMMA,
				useAny);
	}

	/**
	 * The class of object this AbilityRefChoiceSet contains.
	 * 
	 * The behavior of this method is undefined if the CDOMReference objects
	 * provided during the construction of this AbilityRefChoiceSet are not yet
	 * resolved.
	 * 
	 * @return The class of object this AbilityRefChoiceSet contains.
	 */
	public Class<AbilitySelection> getChoiceClass()
	{
		return AbilitySelection.class;
	}

	/**
	 * Returns a Set containing the Objects which this AbilityRefChoiceSet
	 * contains. The contents of a AbilityRefChoiceSet is fixed, and will not
	 * vary by the PlayerCharacter used to resolve the AbilityRefChoiceSet.
	 * 
	 * The behavior of this method is undefined if the CDOMReference objects
	 * provided during the construction of this AbilityRefChoiceSet are not yet
	 * resolved.
	 * 
	 * Ownership of the Set returned by this method will be transferred to the
	 * calling object. Modification of the returned Set should not result in
	 * modifying the AbilityRefChoiceSet, and modifying the AbilityRefChoiceSet
	 * after the Set is returned should not modify the Set.
	 * 
	 * @param pc
	 *            The PlayerCharacter for which the choices in this
	 *            AbilityRefChoiceSet should be returned.
	 * @return A Set containing the Objects which this AbilityRefChoiceSet
	 *         contains.
	 */
	public Set<AbilitySelection> getSet(PlayerCharacter pc)
	{
		Set<AbilitySelection> returnSet = new HashSet<AbilitySelection>();
		for (AbilityRef ref : set)
		{
			for (Ability a : ref.getRef().getContainedObjects())
			{
				if (a.getSafe(ObjectKey.MULTIPLE_ALLOWED))
				{
					returnSet.addAll(addMultiplySelectableAbility(pc, a, ref
							.getChoice()));
				}
				else
				{
					returnSet.add(new AbilitySelection(a, nature));
				}
			}
		}
		return returnSet;
	}

	private Collection<AbilitySelection> addMultiplySelectableAbility(
			final PlayerCharacter aPC, Ability ab, String subName)
	{
		// If already have taken the feat, use it so we can remove
		// any choices already selected
		final Ability pcFeat = aPC.getFeatNamed(ab.getKeyName());

		Ability a = ab;
		if (pcFeat != null)
		{
			a = pcFeat;
		}

		boolean isPattern = false;
		String nameRoot = null;
		if (subName != null)
		{
			final int percIdx = subName.indexOf('%');

			if (percIdx > -1)
			{
				isPattern = true;
				nameRoot = subName.substring(0, percIdx);
			}
			else if (subName.length() != 0)
			{
				nameRoot = subName;
			}
		}

		final List<String> availableList = new ArrayList<String>();
		final List<?> tempAvailList = new ArrayList<Object>();
		final List<?> tempSelList = new ArrayList<Object>();
		ChooserUtilities.modChoices(a, tempAvailList, tempSelList, false, aPC,
				true, AbilityCategory.FEAT);
		// Mod choices may have sent us back weaponprofs, abilities or
		// strings,
		// so we have to do a conversion here
		for (Object o : tempAvailList)
		{
			String choice = o.toString();
			if ("NOCHOICE".equals(choice))
			{
				availableList.add("");
			}
			else
			{
				availableList.add(choice);
			}
		}

		// Remove any that don't match

		if (nameRoot != null && nameRoot.length() != 0)
		{
			for (int n = availableList.size() - 1; n >= 0; --n)
			{
				final String aString = availableList.get(n);

				if (!aString.startsWith(nameRoot))
				{
					availableList.remove(n);
				}
			}

			// Example: ADD:FEAT(Skill Focus(Craft (Basketweaving))) If you
			// have no ranks in Craft (Basketweaving), the available list will
			// be empty
			//
			// Make sure that the specified feat is available, even though it
			// does not meet the prerequisite

			if (isPattern && !availableList.isEmpty())
			{
				availableList.add(nameRoot);
			}
		}

		List<AbilitySelection> returnList = new ArrayList<AbilitySelection>(
				availableList.size());
		for (String s : availableList)
		{
			returnList.add(new AbilitySelection(a, nature, s));
		}
		return returnList;
	}

	/**
	 * Returns the consistent-with-equals hashCode for this AbilityRefChoiceSet
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return set.size();
	}

	/**
	 * Returns true if this AbilityRefChoiceSet is equal to the given Object.
	 * Equality is defined as being another AbilityRefChoiceSet object with
	 * equal underlying contents.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (o instanceof AbilityRefChoiceSet)
		{
			AbilityRefChoiceSet other = (AbilityRefChoiceSet) o;
			return set.equals(other.set);
		}
		return false;
	}

	/**
	 * Returns the underlying Ability Category for this AbilityRefChoiceSet
	 * 
	 * @return The underlying Ability Category for this AbilityRefChoiceSet
	 */
	public Category<Ability> getCategory()
	{
		return category;
	}

	/**
	 * Returns the underlying Ability Nature for this AbilityRefChoiceSet
	 * 
	 * @return The underlying Ability Nature for this AbilityRefChoiceSet
	 */
	public Ability.Nature getNature()
	{
		return nature;
	}
}
