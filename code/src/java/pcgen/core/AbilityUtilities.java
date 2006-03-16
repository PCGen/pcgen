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
import java.util.Arrays;
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
	/**
	 * Find an ability that matches a given name in a list (using this is
	 * probably a really bad idea since it doesn't pay attention to category)
	 *
	 * @param   aFeatList  A list of Ability Objects
	 * @param   featName   The name of the Ability being looked for
	 *
	 * @return  the Ability if found, otherwise null
	 */
	public static Ability getFeatNamedInList(
	    final List   aFeatList,
	    final String featName)
	{
		return getFeatNamedInList(aFeatList, featName, -1);
	}


	/**
	 * Find an ability that matches a given name in a list (using this is
	 * probably a really bad idea since it doesn't pay attention to category).
	 * Also takes an integer representing a type, -1 always matches, otherwise
	 * an ability will only be returned if its type is the same as featType
	 *
	 * @param   aFeatList
	 * @param   featName
	 * @param   featType
	 *
	 * @return  the Ability if found, otherwise null
	 */
	public static Ability getFeatNamedInList(
	    final List   aFeatList,
	    final String featName,
	    final int    featType)
	{
		if (aFeatList.isEmpty())
		{
			return null;
		}

		for (Iterator e = aFeatList.iterator(); e.hasNext();)
		{
			final Ability aFeat = (Ability) e.next();

			if (aFeat.getName().equalsIgnoreCase(featName))
			{
				if ((featType == -1) || (aFeat.getFeatType() == featType))
				{
					return aFeat;
				}
			}
		}

		return null;
	}


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
	public static Ability retrieveAbilityKeyed(String cat, final String token)
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
			else if (getMatchingFeatInList(addList, newAbility) == null)
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
	    PlayerCharacter   aPC)
	{
		Ability anAbility = Globals.getAbilityNamed("FEAT", featName);
		List    choices   = null;

		if (!anAbility.getName().equalsIgnoreCase(featName))
		{
			final int i = featName.indexOf('(');
			final int j = featName.indexOf(')');

			if ((i >= 0) && (j >= 0))
			{
				choices = Arrays.asList(featName.substring(i + 1, j).split(","));
			}
		}

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
	    PlayerCharacter   aPC,
	    final PCLevelInfo playerCharacterLevelInfo,
	    String            featName,
	    final boolean     addIt,
	    boolean           addAll)
	{
		if (!aPC.isImporting())
		{
			aPC.getSpellList();
		}

		int     retVal  = addIt ? 1 : 0;
		boolean added   = false;
		String  subName = "";

		// See if our choice is not auto or virtual
		Ability      anAbility = aPC.getRealFeatNamed(featName);
		final String oldName   = featName;

		// if a feat named featName doesn't exist, and featName
		// contains a (blah) descriptor, try removing it.
		if ((anAbility == null) && featName.endsWith(")"))
		{
			final int idx = featName.indexOf('(');

			// we want what is inside the outermost parenthesis
			subName   = featName.substring(idx + 1, featName.lastIndexOf(')'));
			featName  = featName.substring(0, idx).trim();
			anAbility = aPC.getRealFeatNamed(featName);

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
			anAbility = Globals.getAbilityNamed("FEAT", featName);

			if (anAbility == null)
			{
				anAbility = Globals.getAbilityNamed("FEAT", oldName);

				if (anAbility != null)
				{
					featName = oldName;
					subName  = "";
				}
			}

			if (anAbility != null)
			{
				anAbility = (Ability) anAbility.clone();
			}
			else
			{
				Logging.errorPrint("Feat not found: " + oldName);

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

	public static int modAbility (
	    PlayerCharacter   aPC,
	    final PCLevelInfo playerCharacterLevelInfo,
	    Ability           argAbility,
	    String            choice,
	    final boolean     addIt,
	    boolean           addAll)
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
		Ability pcAbility  = getMatchingFeatInList(realAbilities, argAbility);

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
	    Ability         anAbility,
	    String          choice,
	    PlayerCharacter aPC,
	    final boolean   addIt,
	    boolean         addAll,
	    boolean         added,
	    int             retVal)
	{
		// how many sub-choices to make
		double j = (anAbility.getAssociatedCount() * anAbility.getCost(aPC)) + aPC.getFeats();

		// process ADD tags from the feat definition
		if (!addIt)
		{
			anAbility.modAdds(addIt, aPC);
		}

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
			anAbility.modAdds(addIt, aPC);
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
	 * Convert the name of an Ability (currently only handles FEATS) into an
	 * Ability object and add it to theFeatList TODO expand this routine so that
	 * it can handle more than feats.  This may involve changes where it is
	 * called so that they pass a different list depending on the category of
	 * Ability object, but they would need to pass the category as well and it
	 * may be simpler to pass AbilityInfo objects instead.
	 *
	 * @param  theAbilityList  A list of abilities to add to
	 * @param  abilityName     The name of the Ability to Add
	 */
	static void addToFeatList(final List theAbilityList, final String abilityName)
	{
		String altName = "";
		String subName = "";
	
		if (abilityName.endsWith(")"))
		{
			// we want what is inside the outermost parens.
			subName = abilityName.substring(
				    abilityName.indexOf('(') + 1,
				    abilityName.lastIndexOf(')'));
			altName = abilityName.substring(0, abilityName.indexOf('(')).trim();
		}
	
		Ability anAbility = getFeatNamedInList(
			    theAbilityList,
			    abilityName);
	
		if ((anAbility == null) && (altName.length() != 0))
		{
			anAbility = getFeatNamedInList(theAbilityList, altName);
		}
	
		/* This feat is not in autoFeatList, get the global definition, clone it, attach
		 * sub-type (if any) and add */
	
		if (anAbility == null)
		{
			anAbility = Globals.getAbilityNamed("FEAT", abilityName);
	
			if ((anAbility == null) && (altName.length() != 0))
			{
				anAbility = Globals.getAbilityNamed("FEAT", altName);
			}
	
			if (anAbility != null)
			{
				anAbility = (Ability) anAbility.clone();
	
				if (subName.length() != 0)
				{
					anAbility.addAssociated(subName);
				}
	
				anAbility.setFeatType(Ability.ABILITY_AUTOMATIC);
				theAbilityList.add(anAbility);
			}
			else
			{
				ShowMessageDelegate.showMessageDialog(
				    "Adding unknown feat: " + abilityName,
				    Constants.s_APPNAME,
				    MessageType.INFORMATION);
			}
		}
	
		/* Already have feat, add sub-type (if any) */
	
		else
		{
			if (subName.length() != 0)
			{
				if (
				    anAbility.isStacks() ||
				    (anAbility.isMultiples() &&
				        !anAbility.containsAssociated(subName)))
				{
					anAbility.addAssociated(subName);
				}
			}
		}
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
	
			// does not already have feat
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
	
			if (anAbility.getName().endsWith("Weapon Proficiency"))
			{
				for (int e = 0; e < anAbility.getAssociatedCount(); ++e)
				{
					final String wprof = anAbility.getAssociated(e);
					final WeaponProf wp = Globals.getWeaponProfNamed(wprof);
	
					if (wp != null)
					{
						aPC.addWeaponProfToChosenFeats(wprof);
					}
				}
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
				addToFeatList(autoFeatList, aTok.nextToken());
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
	
				addToFeatList(autoFeatList, autoFeat);
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
							addToFeatList(autoFeatList, aTok.nextToken());
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
								addToFeatList(autoFeatList, aString.substring(idx + 1));
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
						addToFeatList(autoFeatList, abI.getKeyName());
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
}
