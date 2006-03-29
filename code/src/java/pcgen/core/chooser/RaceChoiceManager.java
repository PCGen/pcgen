/**
 * RaceChoiceManager.java
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Current Version: $Revision: 285 $
 * Last Editor:     $Author: nuance $
 * Last Edited:     $Date: 2006-03-17 15:19:49 +0000 (Fri, 17 Mar 2006) $
 *
 * Copyright 2006 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import pcgen.core.Ability;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This is the chooser that deals with choosing a race.
 */
public class RaceChoiceManager extends AbstractComplexChoiceManager
{
	/**
	 * Make a new Race chooser.
	 *
	 * @param  aPObject
	 * @param  choiceString
	 * @param  aPC
	 */
	public RaceChoiceManager(
	    PObject         aPObject,
	    String          choiceString,
	    PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		title          = "Choose Race";
		chooserHandled = "RACE";

		if (choices != null && choices.size() > 0 &&
				((String) choices.get(0)).equals(chooserHandled)) {
			choices = choices.subList(1, choices.size());
		}
	}


	/**
	 * Construct a list of available selections for race.
	 *
	 * @param  aPc
	 * @param  availableList
	 * @param  selectedList
	 */
	public void getChoices(
	    final PlayerCharacter aPc,
	    final List            availableList,
	    final List            selectedList)
	{
		// CHOOSE:RACE|RACETYPE=x,RACESUBTYPE=y,<racename>,TYPE=z
		// or CHOOSE:RACE|[RACETYPE=x,RACESUBTYPE=y]
		Collection races = Globals.getRaceMap().values();

		Iterator choiceIt = choices.iterator();

		while (choiceIt.hasNext())
		{
			String choice = (String) choiceIt.next();

			// All top-level comma-separated items are added to the list.
			if (choice.indexOf("[") != -1)
			{
				processTokenWithBrackets(availableList, races, choice);
			}

			if (choice.startsWith("RACETYPE=") || choice.startsWith("RACETYPE."))
			{
				// Add all races matching this racetype
				for (Iterator i = races.iterator(); i.hasNext();)
				{
					Race race = (Race) i.next();

					if (race.getRaceType().equals(choice.substring(9)))
					{
						availableList.add(race.getName());
					}
				}
			}
			else if (
			    choice.startsWith("RACESUBTYPE=") ||
			    choice.startsWith("RACESUBTYPE."))
			{
				// Add all races matching this racetype
				for (Iterator i = races.iterator(); i.hasNext();)
				{
					Race race = (Race) i.next();

					if (race.getRacialSubTypes().contains(choice.substring(9)))
					{
						availableList.add(race.getName());
					}
				}
			}
			else if (choice.startsWith("TYPE=") || choice.startsWith("TYPE."))
			{
				// Add all races matching this racetype
				for (Iterator i = races.iterator(); i.hasNext();)
				{
					Race race = (Race) i.next();

					if (race.getType().equals(choice.substring(5)))
					{
						availableList.add(race.getName());
					}
				}
			}
			else
			{
				Race race = Globals.getRaceNamed(choice);

				if (race != null)
				{
					availableList.add(race.getName());
				}
			}
		}
	}


	/**
	 * process a choice token of the form [RACETYPE=x,RACESUBTYPE=y].  A race
	 * will only be added to the available list if all of the given specifiers
	 * (RACETYPE, RACESUBTYPE, etc.) match
	 *
	 * @param  availableList
	 * @param  races
	 * @param  choice
	 */
	private void processTokenWithBrackets(
	    final List availableList,
	    Collection races,
	    String     choice)
	{
		ArrayList raceTypes    = new ArrayList();
		ArrayList raceSubTypes = new ArrayList();
		ArrayList types        = new ArrayList();

		choice = choice.substring(1, choice.length() - 1);

		StringTokenizer options = new StringTokenizer(choice, ",");

		while (options.hasMoreTokens())
		{
			String option = options.nextToken();

			if (option.startsWith("RACETYPE=") || option.startsWith("RACETYPE."))
			{
				raceTypes.add(option.substring(9));
			}
			else if (
			    option.startsWith("RACESUBTYPE=") ||
			    option.startsWith("RACESUBTYPE."))
			{
				raceSubTypes.add(option.substring(12));
			}
			else if (option.startsWith("TYPE=") || option.startsWith("TYPE."))
			{
				types.add(option.substring(5));
			}
		}

		for (Iterator i = races.iterator(); i.hasNext();)
		{
			Race race = (Race) i.next();

			if (checkRace(race, raceTypes, raceSubTypes, types))
			{
				availableList.add(race.getName());
			}
		}
	}

	/**
	 * Does race match all of the given raceTypes, raceSubtypes and types
	 *
	 * @param   race
	 * @param   raceTypes
	 * @param   raceSubTypes
	 * @param   types
	 *
	 * @return  true if race matches
	 */
	private static boolean checkRace(
	    Race race,
	    List raceTypes,
	    List raceSubTypes,
	    List types)
	{
		for (Iterator i = raceTypes.iterator(); i.hasNext();)
		{
			String raceType = (String) i.next();

			if (!race.getRaceType().equals(raceType))
			{
				return false;
			}
		}

		for (Iterator i = raceSubTypes.iterator(); i.hasNext();)
		{
			String raceSubType = (String) i.next();

			if (!race.getRacialSubTypes().contains(raceSubType))
			{
				return false;
			}
		}

		for (Iterator i = types.iterator(); i.hasNext();)
		{
			String rType = (String) i.next();

			if (!race.getType().equals(rType))
			{
				return false;
			}
		}

		return true;
	}


	/**
	 * Apply the choices selected to the associated PObject (the one passed
	 * to the constructor)
	 * @param aPC
	 * @param selected
	 *
	 */
	public void applyChoices(
			PlayerCharacter  aPC,
			List             selected)
	{
		pobject.clearAssociated();

		String objPrefix = "";

		if (pobject instanceof Domain)
		{
			objPrefix = chooserHandled + '?';
		}

		if (pobject instanceof Ability) {
			((Ability)pobject).clearSelectedWeaponProfBonus(); //Cleans up the feat
		}

		for (int i = 0; i < selected.size(); ++i)
		{
			final String chosenItem = (String) selected.get(i);

			if (multiples && !dupsAllowed)
			{
				if (!pobject.containsAssociated(objPrefix + chosenItem))
				{
					pobject.addAssociated(objPrefix + chosenItem);
				}
			}
			else
			{
				pobject.addAssociated(objPrefix + chosenItem);

			}

			if (Globals.weaponTypesContains(chooserHandled))
			{
				aPC.addWeaponProf(objPrefix + chosenItem);
			}
		}

		double featCount = aPC.getFeats();
		if (numberOfChoices > 0)
		{
			if (cost > 0)
			{
				featCount -= cost;
			}
		}
		else
		{
			if (cost > 0)
			{
				featCount = ((maxSelections - selected.size()) * cost);
			}
		}

		aPC.adjustFeats(featCount - aPC.getFeats());

		// This will get assigned by autofeat (if a feat)

		if (objPrefix.length() != 0)
		{
			aPC.setAutomaticFeatsStable(false);
		}
	}

}
