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

import pcgen.cdom.base.CDOMObjectUtilities;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.AbilitySelection;
import pcgen.core.analysis.AddObjectActions;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.LastGroupSeparator;
import pcgen.util.Logging;

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
	 * Add the choices in the List to the ability if it is legal to do so.
	 * @param pc TODO
	 * @param ability Ability to add the choices to
	 * @param choices the iterable collection of choices to add
	 */
	private static void addChoicesToAbility(
			PlayerCharacter pc,
			final Ability ability, final Iterable<String> choices)
	{
		for ( final String choice : choices )
		{
			if (canAddAssociation(pc, ability, choice))
			{
				pc.addAssociation(ability, choice);
			}
		}
	}


	/**
	 * Clone anAbility, apply choices and add it to the addList, provided the
	 * Ability allows it (if not isMultiples check if it's already there before
	 * adding it).
	 * @param pc TODO
	 * @param   anAbility
	 * @param   choices
	 * @param   addList
	 *
	 * @return the Ability added, or null if Ability was not added to the list.
	 */
	private static Ability addCloneOfAbilityToVirtualListwithChoices(
		PlayerCharacter pc, 
		final Ability anAbility,
		final List<String>    choices, AbilityCategory cat)
	{
		Ability newAbility = null;

		if (needToAddVirtualAbility(pc, cat, anAbility))
		{
			newAbility = anAbility.clone();

			if (choices != null)
			{
				addChoicesToAbility(pc, newAbility, choices);
			}
			pc.addUserVirtualAbility(cat, newAbility);
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
	 * Add a virtual feat to the character and include it in the List.
	 *
	 * @param   anAbility
	 * @param   choices
	 * @param   addList
	 * @param pc TODO
	 * @param   levelInfo
	 * @return the Ability added
	 */
	public static Ability addVirtualAbility(
		final Ability     anAbility,
		final List<String>        choices,
		final AbilityCategory cat,
		PlayerCharacter pc, final PCLevelInfo levelInfo)
	{
		if (anAbility == null)
		{
			return null;
		}

		final Ability newAbility = addCloneOfAbilityToVirtualListwithChoices(pc, anAbility, choices, cat);

		if (newAbility != null)
		{
			newAbility.clearPrerequisiteList();
			if (levelInfo != null)
			{
				levelInfo.addObject(newAbility);
			}
		}
		return newAbility;
	}


	/**
	 * Add a virtual Ability to abilityList by category and name.  Any choices made
	 * (by including in parenthesis e.g. "Weapon Focus (Longsword)", are extracted
	 * from the name and added appropriately
	 *
	 * @param   category
	 * @param   aFeatKey
	 * @param   abilityList
	 * @param pc TODO
	 * @param   levelInfo
	 * @return  the Ability added
	 */
	public static Ability addVirtualAbility(
		final String          aFeatKey,
		final AbilityCategory   cat,
		PlayerCharacter pc, final PCLevelInfo     levelInfo)
	{
		final List<String> choices = new ArrayList<String>();
		final String    abilityKey      = getUndecoratedName(aFeatKey, choices);
		//TODO I think this is a bug, what if the ability naturally has parens??
		final Ability anAbility = Globals.getContext().ref
				.silentlyGetConstructedCDOMObject(Ability.class, cat,
						abilityKey);

		return addVirtualAbility(anAbility, choices, cat, pc, levelInfo);
	}


	/**
	 * Do the Categorisable objects passed in represent the same ability?
	 *
	 * @param first
	 * @param second
	 * @return true if the same object is represented
	 */
	public static boolean areSameAbility(
			final Ability first,
			final Ability second)
	{
		if (first == null || second == null) {
			return false;
		}

		final boolean multFirst  = first.getSafe(ObjectKey.MULTIPLE_ALLOWED);
		final boolean multSecond = second.getSafe(ObjectKey.MULTIPLE_ALLOWED);
		boolean nameCheck  = false;
		if (multFirst && multSecond) {
			/*
			 * The are both Multiply applicable, so strip the decorations (parts
			 * in brackets) from the name, then check the undecorated names are
			 * equal.
			 */
			final Collection<String> decorationsThis = new ArrayList<String>();
			final Collection<String> decorationsThat = new ArrayList<String>();
			final String undecoratedThis = getUndecoratedName(first.getKeyName(), decorationsThis);
			final String undecoratedThat = getUndecoratedName(second.getKeyName(), decorationsThat);
			nameCheck = undecoratedThis.compareToIgnoreCase(undecoratedThat) == 0;

		} else if (multFirst || multSecond) {

			// one is multiply applicable but the other isn't. They can't be the
			// same Ability
			return false;
		} else {

			/*
			 * They're not multiply selectable, so anything in brackets isn't a
			 * choice, it's part of the name
			 */
			nameCheck = first.getKeyName().compareToIgnoreCase(second.getKeyName()) == 0;
		}

		return (nameCheck && first.getCategory().compareToIgnoreCase(second.getCategory()) == 0);
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
	 * @param   addIt
	 * @param   singleChoice
	 * @param   category The AbilityCategory to add or remove the ability from.
	 * @return 1 if adding the Ability, or 0 if removing it.
	 */
	public static void finaliseAbility(
			final Ability         ability,
			final String          choice,
			final PlayerCharacter aPC,
			final boolean         addIt,
			final boolean         singleChoice,
			final AbilityCategory category)
	{
		// how many sub-choices to make
		double abilityCount = (aPC.getSelectCorrectedAssociationCount(ability) * ability.getSafe(ObjectKey.SELECTION_COST).doubleValue());

		boolean adjustedAbilityPool = false;

		// adjust the associated List
		if (singleChoice && (addIt || ability.getSafe(ObjectKey.MULTIPLE_ALLOWED)))
		{
			if ("".equals(choice) || choice == null)
			{
				// Get modChoices to adjust the associated list and Feat Pool
				adjustedAbilityPool = ChooserUtilities.modChoices(
				ability,
				new ArrayList(),
				new ArrayList(),
				true,
				aPC,
				addIt,
				category);
			}
			else if (addIt)
			{
				if (canAddAssociation(aPC, ability, choice))
				{
					aPC.addAssociation(ability, choice);
				}
			}
			else
			{
				aPC.removeAssociation(ability, choice);
			}
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

		if (addIt)
		{
			for (TransitionChoice<Kit> kit : ability.getSafeListFor(ListKey.KIT_CHOICE))
			{
				kit.act(kit.driveChoice(aPC), ability, aPC);
			}
		}

		// if no sub choices made (i.e. all of them removed in Chooser box),
		// then remove the Feat
		boolean removed = false;
		boolean result  = (ability.getSafe(ObjectKey.MULTIPLE_ALLOWED) && singleChoice) ? aPC.hasAssociations(ability) : addIt ; 

		if (! result)
		{
			removed = aPC.removeRealAbility(category, ability);
			aPC.removeTemplatesFrom(ability);
			CDOMObjectUtilities.removeAdds(ability, aPC);
			CDOMObjectUtilities.restoreRemovals(ability, aPC);
		}

		if (singleChoice && !adjustedAbilityPool)
		{
			if (!addIt && !ability.getSafe(ObjectKey.MULTIPLE_ALLOWED) && removed)
			{
				// We don't need to adjust the pool for abilities here as it is recalculated each time it is queried.
				if (category == AbilityCategory.FEAT)
				{
					abilityCount += ability.getSafe(ObjectKey.SELECTION_COST).doubleValue();
				}
			}
			else if (addIt && !ability.getSafe(ObjectKey.MULTIPLE_ALLOWED))
			{
				abilityCount -= ability.getSafe(ObjectKey.SELECTION_COST).doubleValue();
			}
			else if (category == AbilityCategory.FEAT)
			{
				int listSize = aPC.getSelectCorrectedAssociationCount(ability);

				for (Ability myAbility : aPC.getRealAbilitiesList(AbilityCategory.FEAT))
				{
					if (myAbility.getKeyName().equalsIgnoreCase(ability.getKeyName()))
					{
						listSize = aPC.getSelectCorrectedAssociationCount(myAbility);
					}
				}

				abilityCount -= (listSize * ability.getSafe(ObjectKey.SELECTION_COST).doubleValue());
			}


			if (category == AbilityCategory.FEAT)
			{
				aPC.adjustAbilities(category, BigDecimal.valueOf(abilityCount));
			}
		}

		aPC.adjustMoveRates();

		if (addIt && !aPC.isImporting())
		{
			AddObjectActions.globalChecks(ability, aPC);
		}
	}

	/**
	 * Add an Ability to a character, allowing sub-choices if necessary. Always adds
	 * weapon proficiencies, either a single choice if addAll is false, or all
	 * possible choices if addAll is true.
	 * @param   aPC                       the PC to add or remove the Feat from
	 * @param   levelInfo  LevelInfo object to adjust.
	 * @param   argAbility                The ability to process
	 * @param   choice                    For an isMultiples() Ability
	 * @param   create false means the character must already have the Ability (which
	 *                 only makes sense if it allows multiples); true means a new
	 *                 instance of the global Ability will be cloned and added to the
	 *                 character as a real Ability (this is the only way to add real
	 *                 non-virtual Ability objects).
	 * @param   category The AbilityCategory to add or remove the ability from.
	 * @return  1 if adding the Ability or 0 if removing it.
	 */

	public static void modAbility(
		final PlayerCharacter aPC,
		final PCLevelInfo     levelInfo,
		final Ability         argAbility,
		final String          choice,
		final boolean         create,
		final AbilityCategory category)
	{
		if (argAbility == null)
		{
			Logging.errorPrint("Can't process null Ability");
			return;
		}

		if (!aPC.isImporting()) {aPC.getSpellList();}

		Ability pcAbility = aPC.getMatchingAbility(category, argAbility,
				Nature.NORMAL);

		// (pcAbility == null) means we don't have this feat,
		// so we need to add it
		if (create && (pcAbility == null))
		{
			// adding feat for first time
			pcAbility = argAbility.clone();

			aPC.addAbility(category, pcAbility, levelInfo);
			aPC.selectTemplates(pcAbility, aPC.isImporting());
		}
		if (pcAbility != null)
		{
			finaliseAbility(pcAbility, choice, aPC, create, true, category);
		}
	}

	/**
	 * Add a Feat to a character, allowing sub-choices if necessary. Always adds
	 * weapon proficiencies, either a single choice if singleChoice is true, or all
	 * possible choices if singleChoice is false.
	 *
	 * @param   aPC          the PC to add or remove the Feat from
	 * @param   LevelInfo
	 * @param   aFeatKey     the name of the Feat to add.
	 * @param   addIt        false means the character must already have the
	 *                       feat (which only makes sense if it
	 *                       allows multiples); true means
	 *                       to add the feat (the only way to add
	 *                       new feats).
	 * @param   singleChoice false means allow sub-choices; true means no sub-choices.
	 *
	 * @return  1 if adding the Ability but it wasn't there or 0 if the PC does
	 *          not currently have the Ability.
	 */
	public static void modFeat(
		final PlayerCharacter aPC,
		final PCLevelInfo     LevelInfo,
		Ability ability,
		String selection,
		final boolean         addIt,
		final boolean         singleChoice)
	{
		if (!aPC.isImporting())
		{
			aPC.getSpellList();
		}

		// See if our choice is not auto or virtual
		Ability anAbility = aPC.getRealFeatKeyed(ability.getKeyName());

		// (anAbility == null) means we don't have this feat, so we need to add it
		if ((anAbility == null) && addIt)
		{
			// Adding feat for first time
			anAbility = ability.clone();
			aPC.addFeat(anAbility, LevelInfo);
			aPC.selectTemplates(anAbility, aPC.isImporting());
		}

		if (anAbility != null)
		{
			finaliseAbility(anAbility, selection, aPC, addIt, !singleChoice, AbilityCategory.FEAT);
		}
	}

	/**
	 * Add multiple feats from a String list separated by commas.
	 * @param aPC
	 * @param LevelInfo
	 * @param aList
	 * @param addIt
	 * @param all
	 */
	static void modFeatsFromList(final PlayerCharacter aPC,
			final AbilitySelection as)
	{
		Ability anAbility = aPC.getFeatNamed(as.getAbilityKey());

		if (anAbility != null)
		{
			return;
		}

		// Get ability from global storage by Name
		anAbility = as.getAbility().clone();
		aPC.addFeat(anAbility, null);

		String choice = as.getSelection();
		if (choice != null)
		{
			if ("DEITYWEAPON".equals(choice))
			{
				if (aPC.getDeity() != null)
				{
					List<CDOMReference<WeaponProf>> dwp = aPC.getDeity()
							.getSafeListFor(ListKey.DEITYWEAPON);
					for (CDOMReference<WeaponProf> ref : dwp)
					{
						for (WeaponProf wp : ref.getContainedObjects())
						{
							aPC.addAssociation(anAbility, wp.getKeyName());
						}
					}
				}
			}
			else
			{
				aPC.addAssociation(anAbility, choice);
			}
		}
		else
		{
			if (!anAbility.getSafe(ObjectKey.MULTIPLE_ALLOWED))
			{
				aPC.adjustFeats(anAbility.getSafe(ObjectKey.SELECTION_COST)
						.doubleValue());
			}

			modFeat(aPC, null, anAbility, null, true, false);
		}
	}

	static Ability retrieveAbilityKeyed(AbilityCategory aCat,
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
	 * @param aName
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
	 */
	public static String getUndecoratedName(
			final String name, 
			final Collection<String> specifics)
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
	 * Retrieve a list of all abilities in all categories associated 
	 * with a given key. e.g. Fighter feats are included when feats 
	 * are requested.
	 * @param catKey The key of the category to be retrieved
	 * @param pc The character to query
	 * @return List of matching abilities.
	 */
	public static List<Ability> getAggregateAbilitiesListForKey(String catKey, PlayerCharacter pc)
	{
		Collection<AbilityCategory> cats =
				SettingsHandler.getGame().getAllAbilityCatsForKey(catKey);
		List<Ability> abilityList = new ArrayList<Ability>();
		for (AbilityCategory abilityCategory : cats)
		{
			abilityList.addAll(pc.getAggregateAbilityList(
				abilityCategory));
		}
		return abilityList;
	}

	/**
	 * Whether we can add newAssociation to the associated list of this
	 * Ability
	 * @param pc TODO
	 * @param newAssociation The thing to be associated with this Ability
	 *
	 * @return true if we can add the association
	 */
	public static boolean canAddAssociation(PlayerCharacter pc, Ability a, final String newAssociation)
	{
		return a.getSafe(ObjectKey.STACKS)
				|| (a.getSafe(ObjectKey.MULTIPLE_ALLOWED) && !pc
						.containsAssociated(a, newAssociation));
	}

	public static void applyAbility(PlayerCharacter aPC,
			PCLevelInfo pcLevelInfo, AbilityCategory abilityCat,
			Ability ab, String choice, boolean isVirtual)
	{
		if (isVirtual)
		{
			final List<String> choiceList = new ArrayList<String>();
			choiceList.add(choice);
	
			final Ability pcAbility = addVirtualAbility(ab,
					choiceList, abilityCat, aPC, pcLevelInfo);
	
			aPC.setDirty(true);
	
			if (pcAbility != null)
			{
				if (pcAbility.getSafe(ObjectKey.MULTIPLE_ALLOWED))
				{
					final double x = aPC.getRemainingFeatPoints(false);
					aPC.setFeats(1); // temporarily assume 1 choice
					ChooserUtilities.modChoices(pcAbility, new ArrayList(),
							new ArrayList(), true, aPC, true, abilityCat);
					aPC.setFeats(x); // reset to original count
				}
	
				aPC.setAssoc(pcAbility, AssociationKey.NEEDS_SAVING, Boolean.TRUE);
			}
			else
			{
				Logging.errorPrint("Error:" + ab.getKeyName()
						+ " not added, aPC.getFeatNamedInList() == NULL");
			}
		}
		else
		{
			aPC.adjustAbilities(abilityCat, ab.getSafe(ObjectKey.SELECTION_COST));
	
			modAbility(aPC, pcLevelInfo, ab, choice, true,
					abilityCat);
		}
	}
}
