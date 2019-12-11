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
 * 
 */
package pcgen.cdom.choiceset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Deity;
import pcgen.core.PlayerCharacter;
import pcgen.core.WeaponProf;

/**
 * A AbilityRefChoiceSet contains references to AbilityRef Objects.
 * 
 * The contents of a AbilityRefChoiceSet is defined at construction of the
 * AbilityRefChoiceSet. The contents of a AbilityRefChoiceSet is fixed, and will
 * not vary by the PlayerCharacter used to resolve the AbilityRefChoiceSet.
 */
public class AbilityRefChoiceSet implements PrimitiveChoiceSet<CNAbilitySelection>
{

	/**
	 * The underlying Set of CDOMReferences that contain the objects in this
	 * AbilityRefChoiceSet
	 */
	private final Set<CDOMReference<Ability>> abilityRefSet;

	/**
	 * The underlying Ability Category for this AbilityRefChoiceSet.
	 */
	private final CDOMSingleRef<AbilityCategory> category;

	/**
	 * The underlying Ability Nature for this AbilityRefChoiceSet.
	 */
	private final Nature nature;

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
	 * @param arCollection
	 *            A Collection of AbilityRefs which define the Set of objects
	 *            contained within the AbilityRefChoiceSet
	 * @param nat
	 *            The Ability Nature of the Ability objects as they should be
	 *            applied to a PlayerCharacter
	 * @throws IllegalArgumentException
	 *             if the given Collection is null or empty.
	 */
	public AbilityRefChoiceSet(CDOMSingleRef<AbilityCategory> cat,
		Collection<? extends CDOMReference<Ability>> arCollection, Nature nat)
	{
		Objects.requireNonNull(arCollection, "Choice Collection cannot be null");
		if (arCollection.isEmpty())
		{
			throw new IllegalArgumentException("Choice Collection cannot be empty");
		}
		abilityRefSet = new HashSet<>(arCollection);
		Objects.requireNonNull(nat, "Choice Nature cannot be null");
		nature = nat;
		Objects.requireNonNull(cat, "Choice Category cannot be null");
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
	@Override
	public String getLSTformat(boolean useAny)
	{
		Set<CDOMReference<?>> sortedSet = new TreeSet<>(ReferenceUtilities.REFERENCE_SORTER);
		sortedSet.addAll(abilityRefSet);
		return ReferenceUtilities.joinLstFormat(sortedSet, Constants.COMMA, useAny);
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
	@Override
	public Class<CNAbilitySelection> getChoiceClass()
	{
		return CNAbilitySelection.class;
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
	@Override
	public Set<CNAbilitySelection> getSet(PlayerCharacter pc)
	{
		Set<CNAbilitySelection> returnSet = new LinkedHashSet<>();
		for (CDOMReference<Ability> ref : abilityRefSet)
		{
			for (Ability a : ref.getContainedObjects())
			{
				if (a.getSafe(ObjectKey.MULTIPLE_ALLOWED))
				{
					returnSet.addAll(addMultiplySelectableAbility(pc, a, ref.getChoice()));
				}
				else
				{
					returnSet.add(new CNAbilitySelection(CNAbilityFactory.getCNAbility(category.get(), nature, a)));
				}
			}
		}
		return returnSet;
	}

	private Collection<CNAbilitySelection> addMultiplySelectableAbility(final PlayerCharacter aPC, Ability ability,
		String subName)
	{
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
			else if (!subName.isEmpty())
			{
				nameRoot = subName;
			}
		}

		ChooseInformation<?> chooseInfo = ability.get(ObjectKey.CHOOSE_INFO);
		final List<String> availableList = getAvailableList(aPC, chooseInfo);

		// Remove any that don't match

		/*
		 * TODO Need a general solution for this special assignment in parens
		 */
		if ("DEITYWEAPON".equals(nameRoot) && chooseInfo.getReferenceClass().equals(WeaponProf.class))
		{
			Deity deity = aPC.getDeity();
			if (deity == null)
			{
				availableList.clear();
			}
			else
			{
				List<CDOMReference<WeaponProf>> dwp = deity.getSafeListFor(ListKey.DEITYWEAPON);
				Set<String> set = new HashSet<>();
				for (CDOMReference<WeaponProf> ref : dwp)
				{
					for (WeaponProf wp : ref.getContainedObjects())
					{
						set.add(wp.getKeyName());
					}
				}
				availableList.retainAll(set);
			}
		}
		else if ((nameRoot != null) && !nameRoot.isEmpty())
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

		List<CNAbilitySelection> returnList = new ArrayList<>(availableList.size());
		for (String s : availableList)
		{
			returnList.add(new CNAbilitySelection(CNAbilityFactory.getCNAbility(category.get(), nature, ability), s));
		}
		return returnList;
	}

	private <T> List<String> getAvailableList(final PlayerCharacter aPC, ChooseInformation<T> chooseInfo)
	{
		final List<String> availableList = new ArrayList<>();
		Collection<? extends T> tempAvailList = chooseInfo.getSet(aPC);
		// chooseInfo may have sent us back weaponprofs, abilities or
		// strings, so we have to do a conversion here
		for (T o : tempAvailList)
		{
			availableList.add(chooseInfo.encodeChoice(o));
		}
		return availableList;
	}

	@Override
	public int hashCode()
	{
		return abilityRefSet.size();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (obj instanceof AbilityRefChoiceSet)
		{
			AbilityRefChoiceSet other = (AbilityRefChoiceSet) obj;
			return abilityRefSet.equals(other.abilityRefSet);
		}
		return false;
	}

	/**
	 * Returns the underlying Ability Category for this AbilityRefChoiceSet
	 * 
	 * @return The underlying Ability Category for this AbilityRefChoiceSet
	 */
	public CDOMSingleRef<AbilityCategory> getCategory()
	{
		return category;
	}

	/**
	 * Returns the underlying Ability Nature for this AbilityRefChoiceSet
	 * 
	 * @return The underlying Ability Nature for this AbilityRefChoiceSet
	 */
	public Nature getNature()
	{
		return nature;
	}

	/**
	 * Returns the GroupingState for this AbilityRefChoiceSet. The GroupingState
	 * indicates how this AbilityRefChoiceSet can be combined with other
	 * PrimitiveChoiceSets.
	 * 
	 * @return The GroupingState for this AbilityRefChoiceSet.
	 */
	@Override
	public GroupingState getGroupingState()
	{
		GroupingState state = GroupingState.EMPTY;
		for (CDOMReference<Ability> ref : abilityRefSet)
		{
			state = state.add(ref.getGroupingState());
		}
		return state;
	}
}
