/*
 * AbilityUtilities.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on Aug 25, 2005
 *  Refactored from PlayerCharacter, created on April 21, 2001, 2:15 PM
 *
 *
 */
package pcgen.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMObjectUtilities;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.core.analysis.AddObjectActions;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.LastGroupSeparator;
import pcgen.core.utils.LastGroupSeparator.GroupingMismatchException;

/**
 * General utilities related to the Ability class.
 *
 * @author   Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version  $Revision$
 */
public class AbilityUtilities
{
	private AbilityUtilities ()
	{
		// private constructor, do nothing
	}

	/**
	 * Clone anAbility, apply choices and add it to the addList, provided the
	 * Ability allows it (if not isMultiples check if it's already there before
	 * adding it).
	 * @param pc TODO
	 * @param   anAbility
	 * @param   choice
	 * @param   cat
	 *
	 * @return the Ability added, or null if Ability was not added to the list.
	 */
	public static Ability addCloneOfAbilityToVirtualListwithChoices(
		PlayerCharacter pc, 
		final Ability anAbility,
		final String    choice, AbilityCategory cat)
	{
		Ability newAbility = null;

		if (needToAddVirtualAbility(pc, cat, anAbility))
		{
			newAbility = anAbility.clone();
			pc.addUserVirtualAbility(cat, newAbility);
			newAbility.clearPrerequisiteList();
		}
		return newAbility;
	}

	private static boolean needToAddVirtualAbility(PlayerCharacter pc, AbilityCategory cat, Ability anAbility)
	{
		if(anAbility == null)
		{
			return false;
		}
		/*
		 * TODO I believe this is a bug, but need to check.  This implies that anything
		 * that is MULT:YES and is added virtually is added without cause for whether it stacks
		 * - thpr Jul 24 08
		 */
		if (anAbility.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			return true;
		}
		return !pc.hasUserVirtualAbility(cat, anAbility);
	}

	/**
	 * Finishes off the processing necessary to add or remove an Ability to/from
	 * a PC.  modFeat or modAbility have identified the Ability (either one
	 * already owned by the PC, or a clone of the Globals copy.  They have added
	 * the Ability to the character, this method ensures that all necessary
	 * adjustments (choices to add etc.) are made.
	 *
	 * @param   ability
	 * @param   choice
	 * @param   aPC
	 * @param   category The AbilityCategory to add or remove the ability from.
	 */
	public static void finaliseAbility(
			final Ability         ability,
			final String          choice,
			final PlayerCharacter aPC,
			final AbilityCategory category)
	{
		// how many sub-choices to make
		double abilityCount = (aPC.getSelectCorrectedAssociationCount(ability) * ability.getSafe(ObjectKey.SELECTION_COST).doubleValue());

		boolean adjustedAbilityPool = false;

		// adjust the associated List
		if ("".equals(choice) || choice == null)
		{
			// Get modChoices to adjust the associated list and Feat Pool
			adjustedAbilityPool = ChooserUtilities.modChoices(
			ability,
			new ArrayList(),
			new ArrayList(),
			aPC,
			true,
			category);
		}
		else
		{
			ChoiceManagerList cm = ChooserUtilities.getChoiceManager(ability, aPC);
			add(cm, aPC, ability, choice);
		}

		/* 
		 * This modifyChoice method is a bit like mod choices, but it uses a
		 * different tag to set the chooser string.
		 */
		TransitionChoice<Ability> mc = ability.get(ObjectKey.MODIFY_CHOICE);
		if (mc != null)
		{
			mc.act(mc.driveChoice(aPC), ability, aPC);
		}

		for (TransitionChoice<Kit> kit : ability.getSafeListFor(ListKey.KIT_CHOICE))
		{
			kit.act(kit.driveChoice(aPC), ability, aPC);
		}

		// if no sub choices made (i.e. all of them removed in Chooser box),
		// then remove the Feat
		boolean removed = false;
		boolean result  = ability.getSafe(ObjectKey.MULTIPLE_ALLOWED) ? aPC.hasAssociations(ability) : true ; 

		if (! result)
		{
			removed = aPC.removeRealAbility(category, ability);
			CDOMObjectUtilities.removeAdds(ability, aPC);
			CDOMObjectUtilities.restoreRemovals(ability, aPC);
		}

		if (!adjustedAbilityPool && (category == AbilityCategory.FEAT))
		{
			adjustPool(ability, aPC, true, abilityCount, removed);
		}

		aPC.adjustMoveRates();

		if (!aPC.isImporting())
		{
			AddObjectActions.globalChecks(ability, aPC);
			/*
			 * Protection for CODE-1240. Note the better solution is when facets
			 * are association aware and thus trigger a change when an
			 * association is added. - thpr
			 */
			aPC.calcActiveBonuses();
			aPC.refreshSkillList();
		}
	}

	public static void finaliseAbility(PlayerCharacter aPC, CNAbilitySelection cnas)
	{
		CNAbility cna = cnas.getCNAbility();
		Ability ability = cna.getAbility();
		// how many sub-choices to make
		double abilityCount =
				(aPC.getSelectCorrectedAssociationCount(ability) * ability
					.getSafe(ObjectKey.SELECTION_COST).doubleValue());

		ChoiceManagerList cm =
				ChooserUtilities.getChoiceManager(ability, aPC);
		if (cm != null)
		{
			add(cm, aPC, ability, cnas.getSelection());
		}

		/*
		 * This modifyChoice method is a bit like mod choices, but it uses a
		 * different tag to set the chooser string.
		 */
		TransitionChoice<Ability> mc = ability.get(ObjectKey.MODIFY_CHOICE);
		if (mc != null)
		{
			mc.act(mc.driveChoice(aPC), ability, aPC);
		}

		for (TransitionChoice<Kit> kit : ability
			.getSafeListFor(ListKey.KIT_CHOICE))
		{
			kit.act(kit.driveChoice(aPC), ability, aPC);
		}

		if (cna.getAbilityCategory() == AbilityCategory.FEAT)
		{
			adjustPool(ability, aPC, true, abilityCount, false);
		}

		aPC.adjustMoveRates();

		if (!aPC.isImporting())
		{
			AddObjectActions.globalChecks(ability, aPC);
			/*
			 * Protection for CODE-1240. Note the better solution is when facets
			 * are association aware and thus trigger a change when an
			 * association is added. - thpr
			 */
			aPC.calcActiveBonuses();
			aPC.refreshSkillList();
		}
	}

	private static <T> void add(ChoiceManagerList<T> aMan, PlayerCharacter pc,
		CDOMObject obj, String choice)
	{
		T sel = aMan.decodeChoice(choice);
		aMan.applyChoice(pc, obj, sel);
	}

	public static void adjustPool(final Ability ability,
			final PlayerCharacter aPC, final boolean addIt,
			double abilityCount, boolean removed)
	{
		if (!addIt && !ability.getSafe(ObjectKey.MULTIPLE_ALLOWED) && removed)
		{
			// We don't need to adjust the pool for abilities here as it is recalculated each time it is queried.
			abilityCount += ability.getSafe(ObjectKey.SELECTION_COST).doubleValue();
		}
		else if (addIt && !ability.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			abilityCount -= ability.getSafe(ObjectKey.SELECTION_COST).doubleValue();
		}
		else
		{
			int listSize = aPC.getSelectCorrectedAssociationCount(ability);

			for (CNAbility cna : aPC.getPoolAbilities(AbilityCategory.FEAT, Nature.NORMAL))
			{
				if (cna.getAbilityKey().equalsIgnoreCase(ability.getKeyName()))
				{
					listSize = aPC.getSelectCorrectedAssociationCount(cna);
				}
			}

			abilityCount -= (listSize * ability.getSafe(ObjectKey.SELECTION_COST).doubleValue());
		}

		aPC.adjustAbilities(AbilityCategory.FEAT, BigDecimal.valueOf(abilityCount));
	}

	/**
	 * Add an Ability to a character, allowing sub-choices if necessary. Always adds
	 * weapon proficiencies, either a single choice if addAll is false, or all
	 * possible choices if addAll is true.
	 * @param   aPC                       the PC to add or remove the Feat from
	 * @param   argAbility                The ability to process
	 * @param   choice                    For an isMultiples() Ability
	 * @param   category The AbilityCategory to add or remove the ability from.
	 */
	public static void modAbility(PlayerCharacter aPC, CNAbilitySelection cnas)
	{
		if (!aPC.isImporting())
		{
			aPC.getSpellList();
		}

		CNAbility cna = cnas.getCNAbility();
		Ability pcAbility =
				aPC.addAbilityNeedCheck(cna.getAbilityCategory(),
					cna.getAbility());
		cna.doMagicalAndEvilThings(pcAbility);
		finaliseAbility(aPC, cnas);
	}

	public static Ability retrieveAbilityKeyed(AbilityCategory aCat,
			final String token)
	{
		Ability ability = Globals.getContext().ref
				.silentlyGetConstructedCDOMObject(Ability.class, aCat, token);

		if (ability != null)
		{
			return ability;
		}

		final String stripped = removeChoicesFromName(token);
		ability = Globals.getContext().ref.silentlyGetConstructedCDOMObject(
				Ability.class, aCat, stripped);

		if (ability != null)
		{
			return ability;
		}

		return null;
	}

	/**
	 * Extracts the choiceless form of a name, for example, with all choices removed
	 *
	 * @param name
	 *
	 * @return the name with sub-choices stripped from it
	 */
	public static String removeChoicesFromName(String name)
	{
		LastGroupSeparator lgs = new LastGroupSeparator(name);
		lgs.process();
		return lgs.getRoot().trim();
	}

	/**
	 * Takes a string of the form "foo (bar, baz)", populates the array with ["bar", "baz"]
	 * and returns foo.  All strings returned by this function have had leading.trailing
	 * whitespace removed.
	 *
	 * @param name      The full name with stuff in parenthesis
	 * @param specifics a list which will contain the specifics after the operation has
	 *                  completed
	 *
	 * @return the name with sub-choices stripped from it
	 * @throws GroupingMismatchException If there are mismatched brackets
	 */
	public static String getUndecoratedName(
			final String name, 
			final Collection<String> specifics) throws GroupingMismatchException
	{
		LastGroupSeparator lgs = new LastGroupSeparator(name);
		String subName = lgs.process();
		String altName = lgs.getRoot();

		specifics.clear();
		// we want what is inside the outermost parenthesis.
		if (subName != null)
		{
			specifics.addAll(CoreUtility.split(subName, ','));
		}
		return altName.trim();
	}

	/**
	 * Whether an association has already been selected for this PC.
	 * to the associated list of this
	 *
	 * @return true if the association has already been selected
	 */
	public static boolean alreadySelected(PlayerCharacter pc, Ability ability,
		String selection, boolean allowStack)
	{
		Collection<CNAbility> cnAbilities = pc.getMatchingCNAbilities(ability);
		if (cnAbilities.isEmpty())
		{
			//Don't have any form of it
			return false;
		}
		if (!ability.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			//Based on key name / category match
			return true;
		}
		if (allowStack && ability.getSafe(ObjectKey.STACKS))
		{
			//Must allow it because it stacks
			return false;
		}
		ChooseInformation<?> info = ability.get(ObjectKey.CHOOSE_INFO);
		Object decoded =
				info.decodeChoice(Globals.getContext(), selection);
		for (CNAbility cna : cnAbilities)
		{
			List<?> oldSelections = pc.getDetailedAssociations(cna);
			if ((oldSelections != null) && oldSelections.contains(decoded))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Identify if the object passed in is a feat.
	 * @param obj The object to be checked.
	 * @return true if this is a feat, false if not.
	 */
	public static boolean isFeat(Object obj)
	{
		if (!(obj instanceof Ability))
		{
			return false;
		}
		Ability ability = (Ability) obj;
		if (ability.getCDOMCategory() == null)
		{
			return false;
		}
		return ability.getCDOMCategory() == AbilityCategory.FEAT
			|| (ability.getCDOMCategory().getParentCategory() == AbilityCategory.FEAT);
	}

	public static Ability validateCNAList(List<CNAbility> list)
	{
		Ability a = null;
		for (CNAbility cna : list)
		{
			if (a == null)
			{
				a = cna.getAbility();
			}
			else
			{
				if (!cna.getAbility().getKeyName().equals(a.getKeyName())
					|| !a.getCDOMCategory().equals(
						cna.getAbilityCategory().getParentCategory()))
				{
					throw new IllegalArgumentException(
						"CNAbility list must be a consistent list of Abilities (same object)");
				}
			}
		}
		return a;
	}
}
