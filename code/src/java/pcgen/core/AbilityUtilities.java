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

import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.ListKey;
import pcgen.util.Logging;

import java.math.BigDecimal;
import java.util.*;

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
	 *
	 * @param ability Ability to add the choices to
	 * @param choices the iterable collection of choices to add
	 */
	private static void addChoicesToAbility(
			final Ability ability,
			final Iterable<String> choices)
	{
		for ( final String choice : choices )
		{
			if (ability.canAddAssociation(choice))
			{
				ability.addAssociated(choice);
			}
		}
	}


	/**
	 * Clone anAbility, apply choices and add it to the addList, provided the
	 * Ability allows it (if not isMultiples check if it's already there before
	 * adding it).
	 *
	 * @param   anAbility
	 * @param   choices
	 * @param   addList
	 * @return the Ability added, or null if Ability was not added to the list.
	 */
	public static Ability addCloneOfAbilityToListwithChoices(
		final Ability anAbility,
		final List<String>    choices,
		final List<Ability>    addList)
	{
		Ability newAbility = null;

		if (anAbility != null && (anAbility.isMultiples() || getAbilityFromList(addList, anAbility) == null))
		{
			newAbility = anAbility.clone();

			if (choices != null)
			{
				addChoicesToAbility(newAbility, choices);
			}
			addList.add(newAbility);
		}
		return newAbility;
	}


	/**
	 * Search the List passed in for an instance of the Ability matching
	 * category and Ability name.  If we don't find it, attempt to locate
	 * it in global Storage and, if it's there, clone it and add it the List.
	 * If we got an ability, then add any sub choices from the name to the
	 * associated list of the ability.
	 *
	 * @param  theAbilityList  A list of abilities to add to
	 * @param category The category of Ability to add
	 * @param  abilityName     The name of the Ability to Add
	 *
	 * @return The Ability processed
	 */
	public static Ability addCloneOfGlobalAbilityToListWithChoices(
			final List<Ability>   theAbilityList,
			final String category,
			final String abilityName)
	{
		final Collection<String> choices = new ArrayList<String>();
		getUndecoratedName(abilityName, choices);

		Ability anAbility = getAbilityFromList(theAbilityList, "FEAT", abilityName, Ability.Nature.ANY);

		if (anAbility == null)
		{
			anAbility = cloneGlobalAbility(category, abilityName);

			if (anAbility != null)
			{
				theAbilityList.add(anAbility);
			}
		}

		if (anAbility != null)
		{
			addChoicesToAbility(anAbility, choices);
		}

		return anAbility;
	}

	public static Ability addCloneOfGlobalAbilityToListWithChoices(
			final List<Ability>   anAbilityList,
			final AbilityCategory aCategory,
			final String aKey)
	{
		final Collection<String> choices = new ArrayList<String>();
		getUndecoratedName(aKey, choices);

		Ability anAbility = getAbilityFromList(anAbilityList, aCategory.getAbilityCategory(), aKey, Ability.Nature.ANY);

		if (anAbility == null)
		{
			anAbility = cloneGlobalAbility(aCategory.getAbilityCategory(), aKey);

			if (anAbility != null)
			{
				anAbilityList.add(anAbility);
			}
		}

		if (anAbility != null)
		{
			addChoicesToAbility(anAbility, choices);
		}

		return anAbility;
	}

	/**
	 * Add a virtual feat to the character and include it in the List.
	 *
	 * @param   anAbility
	 * @param   choices
	 * @param   addList
	 * @param   levelInfo
	 * @return the Ability added
	 */
	public static Ability addVirtualAbility(
		final Ability     anAbility,
		final List<String>        choices,
		final List<Ability>        addList,
		final PCLevelInfo levelInfo)
	{
		if (anAbility == null)
		{
			return null;
		}

		final Ability newAbility = addCloneOfAbilityToListwithChoices(anAbility, choices, addList);

		if (newAbility != null)
		{
			newAbility.setFeatType(Ability.Nature.VIRTUAL);
			newAbility.clearPreReq();
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
	 * @param   levelInfo
	 *
	 * @return  the Ability added
	 */
	public static Ability addVirtualAbility(
		final String          category,
		final String          aFeatKey,
		final List<Ability>   abilityList,
		final PCLevelInfo     levelInfo)
	{
		final List<String> choices = new ArrayList<String>();
		final String    abilityKey      = getUndecoratedName(aFeatKey, choices);
		final Ability   anAbility       = Globals.getAbilityKeyed(category, abilityKey);

		return addVirtualAbility(anAbility, choices, abilityList, levelInfo);
	}


	/**
	 * Do the Categorisable objects passed in represent the same ability?
	 *
	 * @param first
	 * @param second
	 * @return true if the same object is represented
	 */
	public static boolean areSameAbility(
			final Categorisable first,
			final Categorisable second)
	{
		if (first == null || second == null) {
			return false;
		}

		final boolean multFirst  = getIsMultiples(first);
		final boolean multSecond = getIsMultiples(second);
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
	 * Do the Categorisable object and the string passed in represent the
	 * same ability?  the string is assumed to be in the same Category as the
	 * Categorisable object.
	 *
	 * @param first
	 * @param second
	 * @return true if the same object is represented
	 */
	public static boolean areSameAbility(
			final Categorisable first,
			final String second)
	{
		if (first == null || second == null) {
			return false;
		}
		final Categorisable newSecond = new AbilityInfo(first.getCategory(), second);
		return areSameAbility(first, newSecond);
	}

	/**
	 * Do the strings passed in represent the same Ability object in the
	 * Category category?
	 *
	 * @param category
	 * @param first
	 * @param second
	 * @return true if the same object is represented
	 */
	public static boolean areSameAbility(
			final String category,
			final String first,
			final String second)
	{
		if (category == null || first == null || second == null) {
			return false;
		}
		final Categorisable newFirst = new AbilityInfo(category, first);
		return areSameAbility(newFirst, second);
	}


	/**
	 * If an ability in Global storage matches the category and name passed
	 * in, then return a clone of that Ability.
	 *
	 * @param category
	 * @param anAbilityKey
	 *
	 * @return a clone of a global ability
	 */
	private static Ability cloneGlobalAbility(
			final String category,
			final String anAbilityKey)
	{
		final Collection<String> choices  = new ArrayList<String>();
		final String    baseKey = getUndecoratedName(anAbilityKey, choices);

		Ability anAbility = Globals.getAbilityKeyed(category, anAbilityKey);

		if ((anAbility == null) && (baseKey.length() != 0))
		{
			anAbility = Globals.getAbilityKeyed(category, baseKey);
		}

		if (anAbility == null)
		{
			Logging.errorPrint("Attempted to add unknown feat: " + anAbilityKey);
		}
		else
		{
			anAbility = anAbility.clone();

			if (choices.size() > 0)
			{
				addChoicesToAbility(anAbility, choices);
			}

		}

		return anAbility;
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
	public static int finaliseAbility(
			final Ability         ability,
			final String          choice,
			final PlayerCharacter aPC,
			final boolean         addIt,
			final boolean         singleChoice,
			final AbilityCategory category)
	{
		// how many sub-choices to make
		double abilityCount = (ability.getAssociatedCount() * ability.getCost(aPC));

		boolean adjustedAbilityPool = false;

		// adjust the associated List
		if (singleChoice && (addIt || ability.isMultiples()))
		{
			if ("".equals(choice) || choice == null)
			{
				// Get modChoices to adjust the associated list and Feat Pool
				adjustedAbilityPool = ability.modChoices(aPC, addIt, category);
			}
			else if (addIt)
			{
				if (ability.canAddAssociation(choice))
				{
					ability.addAssociated(choice);
				}
			}
			else
			{
				ability.removeAssociated(choice);
			}
		}

		/* 
		 * This modifyChoice method is a bit like mod choices, but it uses a
		 * different tag to set the chooser string.  The Tag MODIFYABILITYCHOICE
		 * which doesn't appear to be used anywhere, so this code is totally
		 * redundant.
		 */
		ability.modifyChoice(aPC);

		if (addIt)
		{
			final List<String> kitList = ability.getSafeListFor(ListKey.KITS);
			for (int i = 0; i < kitList.size(); i++)
			{
				KitUtilities.makeKitSelections(0, kitList.get(i), 1, aPC);
			}
		}

		// if no sub choices made (i.e. all of them removed in Chooser box),
		// then remove the Feat
		boolean removed = false;
		boolean result  = (ability.isMultiples() && singleChoice) ? (ability.getAssociatedCount() > 0) : addIt ; 

		if (! result)
		{
			removed = aPC.removeRealAbility(category, ability);
			aPC.removeNaturalWeapons(ability);

			for (int x = 0; x < ability.templatesAdded().size(); ++x)
			{
				aPC.removeTemplate(aPC.getTemplateKeyed(ability.templatesAdded().get(x)));
			}
			ability.subAddsForLevel(-9, aPC);
		}

		if (singleChoice && !adjustedAbilityPool)
		{
			if (!addIt && !ability.isMultiples() && removed)
			{
				// We don't need to adjust the pool for abilities here as it is recalculated each time it is queried.
				if (category == AbilityCategory.FEAT)
				{
					abilityCount += ability.getCost(aPC);
				}
			}
			else if (addIt && !ability.isMultiples())
			{
				abilityCount -= ability.getCost(aPC);
			}
			else if (category == AbilityCategory.FEAT)
			{
				int listSize = ability.getAssociatedCount();

				for (Ability myAbility : aPC.getRealAbilitiesList(AbilityCategory.FEAT))
				{
					if (myAbility.getKeyName().equalsIgnoreCase(ability.getKeyName()))
					{
						listSize = myAbility.getAssociatedCount();
					}
				}

				abilityCount -= (listSize * ability.getCost(aPC));
			}


			if (category == AbilityCategory.FEAT)
			{
				aPC.adjustAbilities(category, BigDecimal.valueOf(abilityCount));
			}
		}

		aPC.setAutomaticAbilitiesStable(null, false);
		aPC.adjustMoveRates();

		if (addIt && !aPC.isImporting())
		{
			ability.globalChecks(false, aPC);
			ability.checkRemovals(aPC);
		}

		return result ? 1 : 0;
	}


	/**
	 * Find an ability in a list that matches a given Ability or AbilityInfo
	 * Object.
	 *
	 * @param anAbilityList
	 * @param abilityInfo
	 *
	 * @return the Ability if found, otherwise null
	 */
	public static Ability getAbilityFromList(
			final List<Ability>          anAbilityList,
			final Categorisable abilityInfo)
	{
		return getAbilityFromList(anAbilityList, abilityInfo, Ability.Nature.ANY);
	}


	/**
	 * Find an ability in a list that matches a given Ability or AbilityInfo
	 * Object. Also takes an integer representing a type, -1 always matches,
	 * otherwise an ability will only be returned if its type is the same as
	 * abilityType
	 *
	 * @param anAbilityList
	 * @param abilityInfo
	 * @param abilityType
	 *
	 * @return the Ability if found, otherwise null
	 */
	public static Ability getAbilityFromList(
		final List<Ability> anAbilityList,
		final Categorisable abilityInfo,
		final Ability.Nature           abilityType)
	{
		if (anAbilityList.isEmpty()) {
			return null;
		}

		for ( Ability ability : anAbilityList )
		{
			if (AbilityUtilities.areSameAbility(ability, abilityInfo) &&
					((abilityType == Ability.Nature.ANY) || (ability.getFeatType() == abilityType)))
			{
				return ability;
			}
		}

		return null;
	}

	/**
	 * Find an ability in a list that matches a given AbilityInfo Object. Also
	 * takes an integer representing a type, -1 always matches, otherwise an
	 * ability will only be returned if its type is the same as abilityType
	 *
	 * @param anAbilityList
	 * @param aCat
	 * @param aName
	 * @param abilityType
	 *
	 * @return the Ability if found, otherwise null
	 */
	public static Ability getAbilityFromList(
			final List<Ability>   anAbilityList,
			final String aCat,
			final String aName,
			final Ability.Nature    abilityType)
	{
		final AbilityInfo abInfo = new AbilityInfo(aCat, aName);
		return getAbilityFromList(anAbilityList, abInfo, abilityType);
	}

	/**
	 * Find out if this Categorisable Object can be applied to a character
	 * multiple times
	 *
	 * @param aCatObj
	 * @return true if can be applied multiple times
	 */
	private static boolean getIsMultiples(
			final Categorisable aCatObj)
	{
		if (aCatObj instanceof Ability)
		{
			return ((Ability) aCatObj).isMultiples();
		}
		else if (aCatObj instanceof AbilityInfo)
		{
			final Ability ability = ((AbilityInfo) aCatObj).getAbility();
			if (ability == null)
			{
				return false;
			}
			return ability.isMultiples();
		}
		return false;
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

	public static int modAbility(
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
			return create ? 1 : 0;
		}

		if (aPC.isNotImporting()) {aPC.getSpellList();}

		final List<Ability> realAbilities = aPC.getRealAbilitiesList(category);
		Ability pcAbility = getAbilityFromList(realAbilities, argAbility);

		// (pcAbility == null) means we don't have this feat,
		// so we need to add it
		if (create && (pcAbility == null))
		{
			// adding feat for first time
			pcAbility = argAbility.clone();

			aPC.addAbility(category, pcAbility, levelInfo);
			pcAbility.getTemplates(aPC.isImporting(), aPC);
		}
		if (pcAbility == null)
		{
			Logging.errorPrint("Can't process ability " + argAbility + " not present in character.");
			return create ? 1 : 0;
		}
		

		return finaliseAbility(pcAbility, choice, aPC, create, true, category);
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
	public static int modFeat(
		final PlayerCharacter aPC,
		final PCLevelInfo     LevelInfo,
		final String          aFeatKey,
		final boolean         addIt,
		final boolean         singleChoice)
	{
		boolean singleChoice1 = !singleChoice;
		if (!aPC.isImporting())
		{
			aPC.getSpellList();
		}

		final Collection<String> choices = new ArrayList<String>();
		final String             baseKey = getUndecoratedName(aFeatKey, choices);
			  String             subKey  = choices.size() > 0 ? choices.iterator().next() : "";

		// See if our choice is not auto or virtual
		Ability anAbility = aPC.getRealFeatKeyed(aFeatKey);

		// if a feat keyed aFeatKey doesn't exist, and aFeatKey
		// contains a (blah) descriptor, try removing it.
		if (anAbility == null)
		{
			anAbility = aPC.getRealFeatKeyed(baseKey);

			if (!singleChoice1 && (anAbility != null) && (subKey.length() != 0))
			{
				singleChoice1 = true;
			}
		}

		// (anAbility == null) means we don't have this feat, so we need to add it
		if ((anAbility == null) && addIt)
		{
			// Adding feat for first time
			anAbility = Globals.getAbilityKeyed("FEAT", baseKey);

			if (anAbility == null)
			{
				anAbility = Globals.getAbilityKeyed("FEAT", aFeatKey);

				if (anAbility != null)
				{
					subKey  = "";
				}
			}

			if (anAbility == null)
			{
				Logging.errorPrint("Feat not found: " + aFeatKey);

				return 1;
			}

			anAbility = anAbility.clone();

			aPC.addFeat(anAbility, LevelInfo);
			anAbility.getTemplates(aPC.isImporting(), aPC);
		}

		/*
		 * Could not find the Ability: addIt true means that no global Ability called
		 * featName exists, addIt false means that the PC does not have this ability
		 */
		if (anAbility == null)
		{
			return addIt ? 1 : 0;
		}

		return finaliseAbility(anAbility, subKey, aPC, addIt, singleChoice1, AbilityCategory.FEAT);
	}

	/**
	 * Add multiple feats from a String list separated by commas.
	 * @param aPC
	 * @param LevelInfo
	 * @param aList
	 * @param addIt
	 * @param all
	 */
	static void modFeatsFromList(
			final PlayerCharacter aPC,
			final PCLevelInfo     LevelInfo,
			final String          aList,
			final boolean         addIt,
			final boolean         all)
	{
		final StringTokenizer aTok = new StringTokenizer(aList, ",");

		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			Ability anAbility = aPC.getFeatNamed(aString);
			StringTokenizer bTok = null;

			if (anAbility != null)
			{
				continue;
			}

			// Get ability from global storage by Name
			anAbility = Globals.getAbilityKeyed("FEAT", aString);

			if (anAbility == null)
			{
				// could not find Feat, try trimming off contents of parenthesis
				bTok = new StringTokenizer(aString, "()", true);

				final String bString = bTok.nextToken();
				final int beginIndex = bString.length() + 1;
				final int endIndex = aString.lastIndexOf(')');

				if (beginIndex <= aString.length())
				{
					if (endIndex >= beginIndex)
					{
						bTok = new StringTokenizer(aString.substring(beginIndex, endIndex), ",");
					}
					else
					{
						bTok = new StringTokenizer(aString.substring(beginIndex), ",");
					}
				}
				else
				{
					bTok = null;
				}
				aString = bString.replace('(', ' ').replace(')', ' ').trim();
			}
			else
			{
				final Ability tempAbility = aPC.getFeatKeyed(anAbility.getKeyName());
				if (tempAbility != null)
				{
					anAbility = tempAbility;
				}
				else
				{
					// add the Feat found, as a CharacterFeat
					anAbility = anAbility.clone();
					aPC.addFeat(anAbility, LevelInfo);
				}
			}

			if (anAbility == null)
			{
				// if we still haven't found it, try a different string
				if (!addIt)
				{
					return;
				}

				anAbility = Globals.getAbilityKeyed("FEAT", aString);

				if (anAbility == null)
				{
					Logging.errorPrint("Feat not found in PlayerCharacter.modFeatsFromList: " + aString);

					return;
				}

				anAbility = anAbility.clone();
				aPC.addFeat(anAbility, LevelInfo);
			}

			if ((bTok != null) && bTok.hasMoreTokens())
			{
				while (bTok.hasMoreTokens())
				{
					aString = bTok.nextToken();

					if ("DEITYWEAPON".equals(aString))
					{
						WeaponProf wp = null;

						if (aPC.getDeity() != null)
						{
							wp = Globals.getWeaponProfKeyed(aPC.getDeity().getFavoredWeapon());
						}

						if (wp != null)
						{
							if (addIt)
							{
								anAbility.addAssociated(wp.getKeyName());
							}
							else
							{
								anAbility.removeAssociated(wp.getKeyName());
							}
						}
					}
					else
					{
						if (addIt)
						{
							anAbility.addAssociated(aString);
						}
						else
						{
							anAbility.removeAssociated(aString);
						}
					}
				}
			}
			else
			{
				if (!all && !anAbility.isMultiples())
				{
					if (addIt)
					{
						aPC.adjustFeats(anAbility.getCost(aPC));
					}
					else
					{
						aPC.adjustFeats(-anAbility.getCost(aPC));
					}
				}

				modFeat(aPC, LevelInfo, aString, addIt, all);
			}
		}

		aPC.setAutomaticAbilitiesStable(null, false);
//		aPC.setAutomaticFeatsStable(false);
	}

	/**
	 * Build and return a list of the Ability objects associated with the given
	 * PlayerCharacter object
	 * @param aPc
	 *
	 * @return a List of the Abilities this Character has
	 */

//	static public List<Ability> rebuildAutoAbilityList(PlayerCharacter aPc)
//	{
//		final List<Ability> autoFeatList;
//		autoFeatList = new ArrayList<Ability>();
//
//		//
//		// add racial feats
//		//
//		if ((aPc.getRace() != null) && !PlayerCharacterUtilities.canReassignRacialFeats())
//		{
//			final StringTokenizer aTok = new StringTokenizer(aPc.getRace().getFeatList(aPc), Constants.PIPE);
//
//			while (aTok.hasMoreTokens())
//			{
//				Ability added = addCloneOfGlobalAbilityToListWithChoices(autoFeatList, Constants.FEAT_CATEGORY, aTok.nextToken());
//				if ( added != null )
//				{
//					added.setFeatType(Ability.Nature.AUTOMATIC);
//				}
//			}
//		}
//
//		for (PCClass aClass : aPc.getClassList())
//		{
//			for (Iterator<String> e1 = aClass.getFeatAutos().iterator(); e1.hasNext();)
//			{
//				//
//				// PCClass object have auto feats stored in format:
//				// lvl|feat_name
//				//
//				final String aString = e1.next();
//
//				if (aString.indexOf('|') < 1)
//				{
//					continue;
//				}
//
//				final StringTokenizer aTok = new StringTokenizer(aString, "|");
//				int i;
//
//				try
//				{
//					i = Integer.parseInt(aTok.nextToken());
//				}
//				catch (NumberFormatException exc)
//				{
//					continue;
//				}
//
//				if (i > aClass.getLevel())
//				{
//					continue;
//				}
//
//				String autoFeat = aTok.nextToken();
//				final int idx = autoFeat.indexOf('[');
//
//				if (idx >= 0)
//				{
//					final StringTokenizer bTok = new StringTokenizer(autoFeat.substring(idx + 1), "[]");
//					final List<Prerequisite> preReqList = new ArrayList<Prerequisite>();
//
//					while (bTok.hasMoreTokens())
//					{
//						final String prereqString = bTok.nextToken();
//						Logging.debugPrint("Why is the prerequisite '"+prereqString+
//								"' parsed in PlayerCharacter.featAutoList() rather than the persistence layer");
//						try {
//							final PreParserFactory factory = PreParserFactory.getInstance();
//							final Prerequisite prereq = factory.parse(prereqString);
//							preReqList.add(prereq);
//						}
//						catch (PersistenceLayerException ple){
//							Logging.errorPrint(ple.getMessage(), ple);
//						}
//					}
//
//					autoFeat = autoFeat.substring(0, idx);
//
//					if (preReqList.size() != 0)
//					{
//						//
//						// To avoid possible infinite loop
//						//
//						if (!aPc.isAutomaticFeatsStable())
//						{
//							aPc.setStableAutomaticFeatList(autoFeatList);
//						}
//
//						if (! PrereqHandler.passesAll(preReqList, aPc, null ))
//						{
//							continue;
//						}
//					}
//				}
//
//				Ability added = addCloneOfGlobalAbilityToListWithChoices(autoFeatList, "FEAT", autoFeat);
//				if ( added != null )
//				{
//					added.setFeatType(Ability.Nature.AUTOMATIC);
//				}
//			}
//		}
//
//		if (!PlayerCharacterUtilities.canReassignTemplateFeats() && !aPc.getTemplateList().isEmpty())
//		{
//			for (PCTemplate aTemplate : aPc.getTemplateList())
//			{
//				aPc.setStableAutomaticFeatList(autoFeatList);
//				final List<String> templateFeats = aTemplate.feats(aPc.getTotalLevels(), aPc.totalHitDice(), aPc, false);
//
//				if (!templateFeats.isEmpty())
//				{
//					for (Iterator<String> e2 = templateFeats.iterator(); e2.hasNext();)
//					{
//						final String aString = e2.next();
//						final StringTokenizer aTok = new StringTokenizer(aString, ",");
//
//						while (aTok.hasMoreTokens())
//						{
//							Ability added = addCloneOfGlobalAbilityToListWithChoices(autoFeatList, "FEAT", aTok.nextToken());
//							if ( added != null )
//							{
//								added.setFeatType(Ability.Nature.AUTOMATIC);
//							}
//						}
//					}
//				}
//			}
//		}
//
//		if (!aPc.getCharacterDomainList().isEmpty())
//		{
//			for (CharacterDomain aCD : aPc.getCharacterDomainList())
//			{
//				final Domain aDomain = aCD.getDomain();
//
//				if (aDomain != null)
//				{
//					for (int e2 = 0; e2 < aDomain.getAssociatedCount(); ++e2)
//					{
//						final String aString = aDomain.getAssociated(e2);
//
//						if (aString.startsWith("FEAT"))
//						{
//							final int idx = aString.indexOf('?');
//
//							if (idx > -1)
//							{
//								Ability added = addCloneOfGlobalAbilityToListWithChoices(autoFeatList, "FEAT", aString.substring(idx + 1));
//								if ( added != null )
//								{
//									added.setFeatType(Ability.Nature.AUTOMATIC);
//								}
//							}
//							else
//							{
//								Logging.errorPrint("no '?' in Domain assocatedList entry: " + aString);
//							}
//						}
//					}
//
//					final Iterator<Categorisable> anIt = aDomain.getFeatIterator();
//
//					for (; anIt.hasNext();)
//					{
//						final Ability abI = (Ability)anIt.next();
//						Ability added = addCloneOfGlobalAbilityToListWithChoices(autoFeatList, "FEAT", abI.getKeyName());
//						if ( added != null )
//						{
//							added.setFeatType(Ability.Nature.AUTOMATIC);
//						}
//					}
//				}
//			}
//		}
//
////		aPc.setStableAutomaticFeatList(autoFeatList);
//		return autoFeatList;
//	}

	/**
	 * This method attempts to get an Ability Object from the Global Store keyed
	 * by token. If this fails, it checks if token has info in parenthesis
	 * appended to it.  If it does, it strips this and attempts to get an
	 * Ability Keyed by the stripped token.  If this works, it passes back this
	 * Ability, if it does not work, it returns null.
	 *
	 * @param   cat    The category of Ability Object to retrieve
	 * @param   token  The name of the Ability Object
	 *
	 * @return  The ability in category "cat" called "token"
	 */
	public static Ability retrieveAbilityKeyed(
		final String cat,
		final String token)
	{
		AbilityCategory aCat = AbilityUtilities.getAbilityCategory(cat);
		Ability ability = Globals.getAbilityKeyed(aCat, token);

		if (ability != null)
		{
			return ability;
		}

		final String stripped = removeChoicesFromName(token);
		ability = Globals.getAbilityKeyed(aCat, stripped);

		if (ability != null)
		{
			return ability;
		}

		return null;
	}

	/**
	 * Convenience method to retrieve an <tt>AbilityCategory</tt> by 
	 * its key name.
	 * 
	 * @param aKey The key of the <tt>AbilityCategory</tt> to retrieve.
	 * @return The requested <tt>AbilityCategory</tt> or <tt>null</tt> if the
	 * category is not found in the current game mode.
	 */
	public static AbilityCategory getAbilityCategory(final String aKey)
	{
		return SettingsHandler.getGame().getAbilityCategory(aKey);
	}

	/**
	 * Extracts the choiceless form of a name, for example, with all choices removed
	 *
	 * @param aName
	 *
	 * @return the name with sub-choices stripped from it
	 */
	public static String removeChoicesFromName(String aName)
	{
		final int anInt = aName.indexOf('(');

		return (anInt >= 0) ? aName.substring(0, anInt).trim() : aName;
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

		final String altName = removeChoicesFromName(name);

		specifics.clear();
		final int start = name.indexOf('(') + 1;
		final int end = name.lastIndexOf(')');

		if (start >= 0 && end > start)
		{

			// we want what is inside the outermost parenthesis.
			final String subName = name.substring(start, end);

			specifics.addAll(CoreUtility.split(subName, ','));
		}

		return altName;
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
	
}
