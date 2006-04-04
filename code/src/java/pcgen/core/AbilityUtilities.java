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
import pcgen.core.utils.ListKey;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * General utilities related to the Ability class.
 *
 * @author   Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version  $Revision$
 */
public class AbilityUtilities
{
//	/**
//	 * Find an ability that matches a given name in a list (using this is
//	 * probably a really bad idea since it doesn't pay attention to category)
//	 *
//	 * @param   aFeatList  A list of Ability Objects
//	 * @param cat TODO
//	 * @param   featName   The name of the Ability being looked for
//	 * @param type TODO
//	 * @return  the Ability if found, otherwise null
//	 */
//	public static Ability getFeatNamedInList(
//		final List   aFeatList,
//		String cat, final String featName, int type)
//	{
//		AbilityInfo abInfo = new AbilityInfo("FEAT", featName);
//		return getAbilityFromList(aFeatList, abInfo, -1);
//	}
//
//	/**
//	 * Find an ability that matches a given name in a list (using this is
//	 * probably a really bad idea since it doesn't pay attention to category).
//	 * Also takes an integer representing a type, -1 always matches, otherwise
//	 * an ability will only be returned if its type is the same as featType
//	 *
//	 * @param   aFeatList
//	 * @param   featName
//	 * @param   featType
//	 *
//	 * @return  the Ability if found, otherwise null
//	 */
//	public static Ability getFeatNamedInList(
//		final List   aFeatList,
//		final String featName,
//		final int    featType)
//	{
//		if (aFeatList.isEmpty())
//		{
//			return null;
//		}
//
//		for (Iterator e = aFeatList.iterator(); e.hasNext();)
//		{
//			final Ability aFeat = (Ability) e.next();
//
//			if (aFeat.getName().equalsIgnoreCase(featName))
//			{
//				if ((featType == -1) || (aFeat.getFeatType() == featType))
//				{
//					return aFeat;
//				}
//			}
//		}
//
//		return null;
//	}
//
//	/**
//	 * Find an ability that matches a given name in a list (If you use this it
//	 * defaults to category FEAT). Also takes an integer representing a type, -1
//	 * always matches, otherwise an ability will only be returned if its type is
//	 * the same as featType
//	 *
//	 * @param   anAbilityList
//	 * @param   featName
//	 * @param   abilityType
//	 *
//	 * @return  the Ability if found, otherwise null
//	 */
//	public static Ability getFeatNamedInList(
//		final List   anAbilityList,
//		final String featName,
//		final int    abilityType)
//	{
//		AbilityInfo abInfo = new AbilityInfo("FEAT", featName);
//		return getAbilityFromList(anAbilityList, abInfo, abilityType);
//	}


	/**
	 * Get an Ability (that is the same basic ability as the argument, but may
	 * have had choices applied) from the list.
	 *
	 * @param   abilityList
	 * @param   argAbility
	 *
	 * @return  the Ability if found, otherwise null
	 */
	public static Ability getMatchingFeatInList(
		final List    abilityList,
		final Ability argAbility)
	{
		if (abilityList.isEmpty())
		{
			return null;
		}

		for (Iterator it = abilityList.iterator(); it.hasNext();)
		{
			final Ability anAbility = (Ability) it.next();

			if (anAbility.isSameBaseAbility(argAbility))
			{
				return anAbility;
			}
		}

		return null;
	}

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
		String       cat,
		final String token)
	{
		Ability ab = Globals.getAbilityKeyed(cat, token);

		if (ab != null)
		{
			return ab;
		}

		String stripped = EquipmentUtilities.removeChoicesFromName(token);
		ab = Globals.getAbilityKeyed(cat, stripped);

		if (ab != null)
		{
			return ab;
		}

		return null;
	}


	/**
	 * Add a virtual feat to the character and include it in the List.
	 *
	 * @param   anAbility
	 * @param   choices
	 * @param   addList
	 * @param   aPC
	 * @param   levelInfo
	 *
	 * @return  the list with the new Ability added
	 */
	public static List addVirtualFeat(
		Ability               anAbility,
		final List            choices,
		final List            addList,
		final PlayerCharacter aPC,
		final PCLevelInfo     levelInfo)
	{
		if (anAbility != null)
		{
			Ability newAbility = (Ability) anAbility.clone();
			newAbility.setFeatType(Ability.ABILITY_VIRTUAL);
			newAbility.clearPreReq();

			if (choices != null)
			{
				final Iterator it = choices.iterator();

				while (it.hasNext())
				{
					final String assoc = (String) it.next();

					if (!newAbility.containsAssociated(assoc))
					{
						newAbility.addAssociated(assoc);
					}
				}
			}

			if (newAbility.isMultiples())
			{
				addList.add(newAbility);

				if (levelInfo != null)
				{
					levelInfo.addObject(newAbility);
				}
			}
			else if (getAbilityFromList(addList, newAbility) == null)
			{
				addList.add(newAbility);

				if (levelInfo != null)
				{
					levelInfo.addObject(newAbility);
				}
			}
		}
		aPC.setDirty(true);

		return addList;
	}


	/**
	 * Add a virtual feat to the character by name and include it in the List.
	 * Any choices are extracted from the name and added appropriately
	 *
	 * @param   featName
	 * @param   addList
	 * @param   levelInfo
	 * @param   aPC
	 *
	 * @return  the list with the new Ability added
	 */
	public static List addVirtualFeat(
		final String      featName,
		final List        addList,
		final PCLevelInfo levelInfo,
		final PlayerCharacter   aPC)
	{
		ArrayList choices     = new ArrayList();
		String    abilityName = EquipmentUtilities.getUndecoratedName(featName, choices);
		Ability   anAbility   = Globals.getAbilityNamed("FEAT", abilityName);

		return addVirtualFeat(anAbility, choices, addList, aPC, levelInfo);
	}


	/**
	 * Add a Feat to a character, allowing sub-choices if necessary. Always adds
	 * weapon proficiencies, either a single choice if addAll is false, or all
	 * possible choices if addAll is true.
	 * @param   aPC                       the PC to add or remove the Feat from
	 * @param   playerCharacterLevelInfo
	 * @param   featName                  the name of the Feat to add.
	 * @param   addIt {<code>false</code>} means the character must already have the
	 *                                    feat (which only makes sense if it
	 *                                    allows multiples); {<code>true</code>} means
	 *                                    to add the feat (the only way to add
	 *                                    new feats).
	 * @param   addAll {<code>false</code>} means allow sub-choices; {<code>true</code>} means
	 *                                    no sub-choices.
	 *
	 * @return  1 if adding the Ability but it wasn't there or 0 if the PC does
	 *          not currently have the Ability.
	 */
	public static int modFeat(
		final PlayerCharacter aPC,
		final PCLevelInfo     playerCharacterLevelInfo,
		      String          featName,
		final boolean         addIt,
		      boolean         addAll)
	{
		if (!aPC.isImporting())
		{
			aPC.getSpellList();
		}

		      ArrayList choices        = new ArrayList();
		      int       retVal         = addIt ? 1 : 0;
		      boolean   added          = false;
		final String    undoctoredName = featName;
		final String    baseName       = EquipmentUtilities.getUndecoratedName(featName, choices);
		      String    subName        = choices.size() > 0 ? (String) choices.get(0) : "";

		// See if our choice is not auto or virtual
		Ability anAbility = aPC.getRealFeatNamed(undoctoredName);

		// if a feat named featName doesn't exist, and featName
		// contains a (blah) descriptor, try removing it.
		if (anAbility == null)
		{
			anAbility = aPC.getRealFeatNamed(baseName);

			if (addAll && (anAbility != null) && (subName.length() != 0))
			{
				addAll = false;
			}
		}

		// (anAbility == null) means we don't have this feat,
		// so we need to add it
		if (addIt && (anAbility == null))
		{
			// adding feat for first time
			anAbility = Globals.getAbilityNamed("FEAT", baseName);

			if (anAbility == null)
			{
				anAbility = Globals.getAbilityNamed("FEAT", undoctoredName);

				if (anAbility != null)
				{
					subName  = "";
				}
			}

			if (anAbility != null)
			{
				anAbility = (Ability) anAbility.clone();
			}
			else
			{
				Logging.errorPrint("Feat not found: " + undoctoredName);

				return retVal;
			}

			aPC.addFeat(anAbility, playerCharacterLevelInfo);
			anAbility.getTemplates(aPC.isImporting(), aPC);
		}

		/*
		 * could not find the Ability: addIt true means that no global Ability called
		 * featName exists, addIt false means that the PC does not have this ability
		 */
		if (anAbility == null)
		{
			return retVal;
		}

		return finaliseAbility(anAbility, subName, aPC, addIt, addAll, added, retVal);
	}


	/**
	 * Add an Ability to a character, allowing sub-choices if necessary. Always adds
	 * weapon proficiencies, either a single choice if addAll is false, or all
	 * possible choices if addAll is true.
	 * @param   aPC                       the PC to add or remove the Feat from
	 * @param   playerCharacterLevelInfo  LevelInfo object to adjust.
	 * @param   argAbility                The ability to process
	 * @param   choice                    For an isMultiples() Ability
	 * @param   addIt {<code>false</code>} means the character must already have the
	 *                                    feat (which only makes sense if it
	 *                                    allows multiples); {<code>true</code>} means
	 *                                    to add the feat (the only way to add
	 *                                    new feats).
	 * @param   addAll {<code>false</code>} means allow sub-choices; {<code>true</code>} means
	 *                                    no sub-choices.
	 * @return  1 if adding the Ability or 0 if removing it.
	 */

	public static int modAbility(
		final PlayerCharacter aPC,
		final PCLevelInfo     playerCharacterLevelInfo,
		final Ability         argAbility,
		final String          choice,
		final boolean         addIt,
		      boolean         addAll)
	{

		int     retVal  = addIt ? 1 : 0;
		boolean added   = false;

		if (argAbility == null)
		{
			Logging.errorPrint("Can't process null Ability");
			return retVal;
		}

		if (aPC.isNotImporting()) {aPC.getSpellList();}

		List realAbilities = aPC.getRealFeatsList();
		Ability pcAbility = getAbilityFromList(realAbilities, argAbility);

		if (addAll && (pcAbility != null) && (choice.length() != 0))
		{
			addAll = false;
		}

		// (pcAbility == null) means we don't have this feat,
		// so we need to add it
		if (addIt && (pcAbility == null))
		{
			// adding feat for first time
			pcAbility = (Ability) argAbility.clone();

			aPC.addFeat(pcAbility, playerCharacterLevelInfo);
			pcAbility.getTemplates(aPC.isImporting(), aPC);
		}

		return finaliseAbility(pcAbility, choice, aPC, addIt, addAll, added, retVal);
	}

	/**
	 * Finishes off the processing necessary to add or remove an Ability to/from
	 * a PC.  modFeat or modAbility have identified the Ability (either one
	 * already owned by the PC, or a clone of the Globals copy.  They have added
	 * the Ability to the character, this method ensures that all necessary
	 * adjustments (choices to add etc.) are made.
	 *
	 * @param   anAbility
	 * @param   choice
	 * @param   aPC
	 * @param   addIt
	 * @param   addAll
	 * @param   added
	 * @param   retVal
	 *
	 * @return 1 if adding the Ability, or 0 if removing it.
	 */
	private static int finaliseAbility(
		final Ability         anAbility,
		final String          choice,
		final PlayerCharacter aPC,
		final boolean   addIt,
		final boolean         addAll,
		boolean         added,
		int             retVal)
	{
		// how many sub-choices to make
		double j = (anAbility.getAssociatedCount() * anAbility.getCost(aPC)) + aPC.getFeats();

//		// process ADD tags from the feat definition
		// Don't need this anymore since ADD tags are parsed globally
//		if (!addIt)
//		{
//			anAbility.modAdds(addIt, aPC);
//		}

		boolean canSetFeats = true;

		if (addIt || anAbility.isMultiples())
		{
			if (!addAll)
			{
				if ("".equals(choice))
				{
					// Allow sub-choices
					canSetFeats = !anAbility.modChoices(aPC, addIt);
				}
				else
				{
					if (addIt && anAbility.isWeaponProficiency())
					{
						aPC.addWeaponProfToChosenFeats(choice);
						added = true;
					}
					else if (
						addIt &&
						(anAbility.isStacks() || !anAbility.containsAssociated(choice)))
					{
						anAbility.addAssociated(choice);
					}
					else if (!addIt && anAbility.containsAssociated(choice))
					{
						anAbility.removeAssociated(choice);
					}
				}
			}
			else
			{
				if (
					(anAbility.getChoiceString().lastIndexOf('|') >= 0) &&
					Globals.weaponTypesContains(
						anAbility.getChoiceString().substring(0,
							anAbility.getChoiceString().lastIndexOf('|'))))
				{
					final String aName =
						anAbility.getChoiceString().substring(0,
							anAbility.getChoiceString().lastIndexOf('|'));
					aPC.addWeaponProfToChosenFeats(aName);
				}
			}
		}

		anAbility.modifyChoice(aPC);

		if (anAbility.isMultiples() && !addAll)
		{
			retVal = (anAbility.getAssociatedCount() > 0) ? 1 : 0;
		}

		// process ADD tags from the feat definition
		if (!added && addIt)
		{
			List l = anAbility.getSafeListFor(ListKey.KITS);
			for (int i = 0; i < l.size(); i++)
			{
				KitUtilities.makeKitSelections(0, (String)l.get(i), 1, aPC);
			}
			// anAbility.modAdds(addIt, aPC);
		}

		// if no sub choices made (i.e. all of them removed in Chooser box),
		// then remove the Feat
		boolean removed = false;

		if (retVal == 0)
		{
			removed = aPC.removeRealFeat(anAbility);
			aPC.removeNaturalWeapons(anAbility);

			for (int x = 0; x < anAbility.templatesAdded().size(); ++x)
			{
				aPC.removeTemplate(
					aPC.getTemplateNamed((String) anAbility.templatesAdded().get(x)));
			}
			anAbility.subAddsForLevel(-9, aPC);
		}

		if (!addIt && !anAbility.isMultiples() && removed)
		{
			j += anAbility.getCost(aPC);
		}
		else if (addIt && !anAbility.isMultiples())
		{
			j -= anAbility.getCost(aPC);
		}
		else
		{
			int associatedListSize = anAbility.getAssociatedCount();

			for (Iterator e1 = aPC.getRealFeatsIterator(); e1.hasNext();)
			{
				final Ability myFeat = (Ability) e1.next();

				if (myFeat.getName().equalsIgnoreCase(anAbility.getName()))
				{
					associatedListSize = myFeat.getAssociatedCount();
				}
			}

			j -= (associatedListSize * anAbility.getCost(aPC));
		}

		if (!addAll && canSetFeats)
		{
			aPC.adjustFeats(j - aPC.getFeats());
		}

		aPC.setAutomaticFeatsStable(false);

		if (addIt && !aPC.isImporting())
		{
			anAbility.globalChecks(false, aPC);
			anAbility.checkRemovals(aPC);
		}

		return retVal;
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
	 */
	static void addToAbilityList(
			final List   theAbilityList,
			final String category,
			final String abilityName)
	{
		ArrayList choices = new ArrayList();
		EquipmentUtilities.getUndecoratedName(abilityName, choices);
		String  subName = choices.size() > 0 ? (String) choices.get(0) : "";

		AbilityInfo abInf = new AbilityInfo("FEAT", abilityName);
		Ability anAbility = getAbilityFromList(theAbilityList, abInf);

		if (anAbility == null)
		{
			anAbility = cloneGlobalAbility(category, abilityName);

			if (anAbility != null)
			{
				anAbility.setFeatType(Ability.ABILITY_AUTOMATIC);
				theAbilityList.add(anAbility);
			}
		}

		if (anAbility == null &&
				subName.length() != 0 && 
				anAbility.canAddAssociation(subName))
		{
			anAbility.addAssociated(subName);
		}

	}

	/**
	 * If an ability in Global storage matches the category and name passed
	 * in, then return a clone of that Ability.  If there are any choices in
	 * parenthesis, they will be discarded (i.e. the caller of this routine
	 * should add them if appropriate).
	 * 
	 * @param category
	 * @param abilityName
	 *
	 * @return a clone of a global ability
	 */
	private static Ability cloneGlobalAbility(
			final String category,
			final String abilityName)
	{
		ArrayList choices          = new ArrayList();
		final     String  baseName = EquipmentUtilities.getUndecoratedName(abilityName, choices);
		final     String  subName  = choices.size() > 0 ? (String) choices.get(0) : "";
		          Ability anAbility;

		anAbility = Globals.getAbilityNamed(category, abilityName);

		if ((anAbility == null) && (baseName.length() != 0))
		{
			anAbility = Globals.getAbilityNamed(category, baseName);
		}

		if (anAbility != null)
		{
			anAbility = (Ability) anAbility.clone();
			if (subName.length() > 0)
			{
				anAbility.addAssociated(subName);
			}
		}
		else
		{
			ShowMessageDelegate.showMessageDialog(
				"Adding unknown feat: " + abilityName,
				Constants.s_APPNAME,
				MessageType.INFORMATION);
		}

		return anAbility;
	}


	/**
	 * Add multiple feats from a String list separated by commas.
	 * @param aPC
	 * @param playerCharacterLevelInfo
	 * @param aList
	 * @param addIt
	 * @param all
	 */
	static void modFeatsFromList(final PlayerCharacter aPC,
								 final PCLevelInfo     playerCharacterLevelInfo,
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
			anAbility = Globals.getAbilityNamed("FEAT", aString);

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
				final Ability tempAbility = aPC.getFeatNamed(anAbility.getName());
				if (tempAbility != null)
				{
					anAbility = tempAbility;
				}
				else
				{
					// add the Feat found, as a CharacterFeat
					anAbility = (Ability) anAbility.clone();
					aPC.addFeat(anAbility, playerCharacterLevelInfo);
				}
			}

			if (anAbility == null)
			{
				// if we still haven't found it, try a different string
				if (!addIt)
				{
					return;
				}

				anAbility = Globals.getAbilityNamed("FEAT", aString);

				if (anAbility == null)
				{
					Logging.errorPrint("Feat not found in PlayerCharacter.modFeatsFromList: " + aString);

					return;
				}

				anAbility = (Ability) anAbility.clone();
				aPC.addFeat(anAbility, playerCharacterLevelInfo);
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
							wp = Globals.getWeaponProfNamed(aPC.getDeity().getFavoredWeapon());
						}

						if (wp != null)
						{
							if (addIt) // TODO: condition always true
							{
								anAbility.addAssociated(wp.getName());
							}
							else
							{
								anAbility.removeAssociated(wp.getName());
							}
						}
					}
					else
					{
						if (addIt) // TODO: condition always true
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

				modFeat(aPC, playerCharacterLevelInfo, aString, addIt, all);
			}
		}

		aPC.setAutomaticFeatsStable(false);
	}


	/**
	 * Build and return a list of the Ability objects associated with the given
	 * PlayerCharacter object
	 * @param aPc
	 *
	 * @return a List of the Abilities this Character has
	 */

	static public List rebuildAutoAbilityList(PlayerCharacter aPc) {

		final List autoFeatList;
		autoFeatList = new ArrayList();

		//
		// add racial feats
		//
		if ((aPc.getRace() != null) && !PlayerCharacterUtilities.canReassignRacialFeats())
		{
			final StringTokenizer aTok = new StringTokenizer(aPc.getRace().getFeatList(aPc), "|");

			while (aTok.hasMoreTokens())
			{
				addToAbilityList(autoFeatList, "FEAT", aTok.nextToken());
			}
		}

		for (Iterator e = aPc.getClassListIterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();

			for (Iterator e1 = aClass.getFeatAutos().iterator(); e1.hasNext();)
			{
				//
				// PCClass object have auto feats stored in format:
				// lvl|feat_name
				//
				final String aString = (String) e1.next();

				if (aString.indexOf('|') < 1)
				{
					continue;
				}

				final StringTokenizer aTok = new StringTokenizer(aString, "|");
				int i;

				try
				{
					i = Integer.parseInt(aTok.nextToken());
				}
				catch (NumberFormatException exc)
				{
					i = 9999; //TODO: Replace magic value with an appropriate constant. Constants.INVALID_LEVEL perhaps?
				}

				if (i > aClass.getLevel())
				{
					continue;
				}

				String autoFeat = aTok.nextToken();
				final int idx = autoFeat.indexOf('[');

				if (idx >= 0)
				{
					final StringTokenizer bTok = new StringTokenizer(autoFeat.substring(idx + 1), "[]");
					final List preReqList = new ArrayList();

					while (bTok.hasMoreTokens())
					{
						final String prereqString = bTok.nextToken();
						Logging.debugPrint("Why is the prerequisite '"+prereqString+
								"' parsed in PlayerCharacter.featAutoList() rather than the persistence layer");
						try {
							final PreParserFactory factory = PreParserFactory.getInstance();
							final Prerequisite prereq = factory.parse(prereqString);
							preReqList.add(prereq);
						}
						catch (PersistenceLayerException ple){
							Logging.errorPrint(ple.getMessage(), ple);
						}
					}

					autoFeat = autoFeat.substring(0, idx);

					if (preReqList.size() != 0)
					{
						//
						// To avoid possible infinite loop
						//
						if (!aPc.isAutomaticFeatsStable())
						{
							aPc.setStableAutomaticFeatList(autoFeatList);
						}

						if (! PrereqHandler.passesAll(preReqList, aPc, null ))
						{
							continue;
						}
					}
				}

				addToAbilityList(autoFeatList, "FEAT", autoFeat);
			}
		}

		if (!PlayerCharacterUtilities.canReassignTemplateFeats() && !aPc.getTemplateList().isEmpty())
		{
			for (Iterator e = aPc.getTemplateListIterator(); e.hasNext();)
			{
				aPc.setStableAutomaticFeatList(autoFeatList);
				final PCTemplate aTemplate = (PCTemplate) e.next();
				final List templateFeats = aTemplate.feats(aPc.getTotalLevels(), aPc.totalHitDice(), aPc, false);

				if (!templateFeats.isEmpty())
				{
					for (Iterator e2 = templateFeats.iterator(); e2.hasNext();)
					{
						final String aString = (String) e2.next();
						final StringTokenizer aTok = new StringTokenizer(aString, ",");

						while (aTok.hasMoreTokens())
						{
							addToAbilityList(autoFeatList, "FEAT", aTok.nextToken());
						}
					}
				}
			}
		}

		if (!aPc.getCharacterDomainList().isEmpty())
		{
			for (Iterator e = aPc.getCharacterDomainListIterator(); e.hasNext();)
			{
				final CharacterDomain aCD = (CharacterDomain) e.next();
				final Domain aDomain = aCD.getDomain();

				if (aDomain != null)
				{
					for (int e2 = 0; e2 < aDomain.getAssociatedCount(); ++e2)
					{
						final String aString = aDomain.getAssociated(e2);

						if (aString.startsWith("FEAT"))
						{
							final int idx = aString.indexOf('?');

							if (idx > -1)
							{
								addToAbilityList(autoFeatList, "FEAT", aString.substring(idx + 1));
							}
							else
							{
								Logging.errorPrint("no '?' in Domain assocatedList entry: " + aString);
							}
						}
					}

					final Iterator anIt = aDomain.getFeatIterator();

					for (; anIt.hasNext();)
					{
						final AbilityInfo abI = (AbilityInfo) anIt.next();
						addToAbilityList(autoFeatList, "FEAT", abI.getKeyName());
					}
				}
			}
		}

		//
		// Need to save current as stable as getAutoWeaponProfs() needs it
		//
		aPc.setStableAutomaticFeatList(autoFeatList);
		aPc.getAutoWeaponProfs(autoFeatList);
		aPc.setStableAutomaticFeatList(autoFeatList);
		return autoFeatList;
	}


	/**
	 * Extracts the contents of the first set of balanced parenthesis (including
	 * any properly nested parenthesis).  "foo (bar, baz)" returns "bar, baz".
	 *
	 * @param aString the input string
	 * @return the contents of the parenthesis
	 */
	static public String extractContentsOfFirstBalancedParens(String aString) {

		int open  = 0;
		int start = aString.indexOf('(');
		int end   = start;

		if (end > -1) {
			while (end < aString.length()) {
				switch (aString.charAt(end)) {
				case '(':
					open += 1;
					break;

				case ')':
					open -= 1;
					break;

				default:
				}

				if (open < 1) {
					break;
				}
				end++;
			}
		}

		if (open < 1) {
			aString = aString.substring(start, end);
		}
		return aString;
	}


	/**
	 * Given the string "token<Prereq1|Prereq2|Prereq3>", this will clear preReqArray,
	 * then populate it with "Prereq1", "Prereq2", "Prereq3" and return token.
	 *
	 * @param aString "token<Prereq1|Prereq2|Prereq3>"
	 * @param preReqArray will contain any prereqs after the routine returns
	 *
	 * @return the token
	 */
	public static String extractTokenPrerequities(String aString, final List preReqArray) {

		preReqArray.clear();
		String tokenString = aString;
		String pString     = "";

		final StringTokenizer preTok  = new StringTokenizer(aString, "<>|", true);

		if (preTok.hasMoreTokens()) {
			tokenString = preTok.nextToken();
		}

		while (preTok.hasMoreTokens() && !(">").equals(pString))
		{
			pString = preTok.nextToken();

			if ((pString.startsWith("PRE") || pString.startsWith("!PRE")))
			{
				preReqArray.add(pString);
			}
		}

		return tokenString;
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
			final List   anAbilityList,
			final String aCat,
			final String aName,
			final int    abilityType) 
	{
		AbilityInfo abInfo = new AbilityInfo(aCat, aName);
		return getAbilityFromList(anAbilityList, abInfo, abilityType);
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
			final List          anAbilityList,
			final Categorisable abilityInfo)
	{
		return getAbilityFromList(anAbilityList, abilityInfo, -1);
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
		final List          anAbilityList,
		final Categorisable abilityInfo,
		final int           abilityType)
	{
		if (anAbilityList.isEmpty()) {
			return null;
		}

		for (Iterator abListIt = anAbilityList.iterator(); abListIt.hasNext();) {
			final Ability anAbility = (Ability) abListIt.next();

			if (AbilityUtilities.areSameAbility(anAbility, abilityInfo)) {
				if ((abilityType == -1) || (anAbility.getFeatType() == abilityType)) {
					return anAbility;
				}
			}
		}

		return null;
	}

	/**
	 * Do the strings passed in represent the same Ability object in the
	 * Category category?
	 *
	 * @param first
	 * @param second
	 * @return true if the same object is represented
	 */
	static public boolean areSameAbility(
			final String category,
			final String first,
			final String second)
	{
		if (category == null || first == null || second == null) {
			return false;
		}
		Categorisable newFirst = new AbilityInfo(category, first);
		return areSameAbility(newFirst, second);
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
	static public boolean areSameAbility(
			final Categorisable first,
			final String second)
	{
		if (first == null || second == null) {
			return false;
		}
		Categorisable newSecond = new AbilityInfo(first.getCategory(), second);
		return areSameAbility(first, newSecond);
	}

	/**
	 * Do the Categorisable objects passed in represent the same ability?
	 *
	 * @param first
	 * @param second
	 * @return true if the same object is represented
	 */
	static public boolean areSameAbility(
			final Categorisable first,
			final Categorisable second)
	{
		if (first == null || second == null) {
			return false;
		}

		boolean multFirst  = getIsMultiples(first);
		boolean multSecond = getIsMultiples(second);
		boolean nameCheck  = false;

		if (multFirst && multSecond) {
			/*
			 * The are both Multiply applicable, so strip the decorations (parts
			 * in brackets) from the name, then check the undecorated names are
			 * equal.
			 */
			ArrayList decorationsThis = new ArrayList();
			ArrayList decorationsThat = new ArrayList();
			String undecoratedThis = EquipmentUtilities.getUndecoratedName(first.getKeyName(), decorationsThis);
			String undecoratedThat = EquipmentUtilities.getUndecoratedName(second.getKeyName(), decorationsThat);
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
			Ability ability = ((AbilityInfo) aCatObj).getAbility();
			if (ability == null)
			{
				return false;
			}
			return ability.isMultiples();
		}
		return false;
	}

}
