/**
 * FeatAddChoiceManager.java
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
import pcgen.core.AbilityUtilities;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.PrereqHandler;
import pcgen.util.Logging;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This is one of the choosers that deals with choosing from among a set 
 * of Ability objects of Category FEAT.
 */
public class FeatAddChoiceManager extends AbstractComplexChoiceManager {

	Ability  anAbility = null;
	
	/**
	 * Make a Feat chooser.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public FeatAddChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		title = "Add a Feat";
		chooserHandled = "FEATADD";
		
		if (choices != null && choices.size() > 0 &&
				((String) choices.get(0)).equals(chooserHandled)) {
			choices = choices.subList(1, choices.size());
		}
	}

	/**
	 * Parse the Choice string and build a list of available choices.
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	public void getChoices(
			final PlayerCharacter aPc,
			final List            availableList,
			final List            selectedList)
	{
		anAbility = null;
		Iterator choiceIt  = choices.iterator();

		while (choiceIt.hasNext())
		{
			final String aString = (String) choiceIt.next();

			if (aString.startsWith("TYPE=") || aString.startsWith("TYPE."))
			{
				final String featType = aString.substring(5);

				for (Iterator it = Globals.getAbilityKeyIterator("FEAT"); it.hasNext(); )
				{
					final Ability ability = (Ability) it.next();

					if (
						ability.isType(featType) &&
						aPc.canSelectAbility(ability) &&
						!availableList.contains(ability.getName())
					   ) {

						availableList.add(ability.getName());
					}
				}
			}

			else
			{
				final StringTokenizer bTok = new StringTokenizer(aString, ",");
				String featName = bTok.nextToken().trim();
				String subName = "";
				anAbility = Globals.getAbilityNamed("FEAT", featName);

				if (anAbility == null)
				{
					Logging.errorPrint("Feat not found: " + featName);

					//return false;
				}

				if (!featName.equalsIgnoreCase(anAbility.getName()))
				{
					subName = featName.substring(anAbility.getName().length());
					featName = anAbility.getName();

					final int si = subName.indexOf('(');

					if (si > -1)
					{
						subName = subName.substring(si + 1);
					}
				}

				if (PrereqHandler.passesAll(anAbility.getPreReqList(), aPc, anAbility))
				{
					if (anAbility.isMultiples())
					{
						//
						// If already have taken the feat, use it so we can remove
						// any choices already selected
						//
						final Ability pcFeat = aPc.getFeatNamed(featName);

						if (pcFeat != null)
						{
							anAbility = pcFeat;
						}

						final int percIdx = subName.indexOf('%');

						if (percIdx > -1)
						{
							subName = subName.substring(0, percIdx);
						}
						else if (subName.length() != 0)
						{
							final int idx = subName.lastIndexOf(')');

							if (idx > -1)
							{
								subName = subName.substring(0, idx);
							}
						}

						final List aavailableList = new ArrayList(); // available list of choices
						final List sselectedList = new ArrayList(); // selected list of choices
						anAbility.modChoices(aPc, true, availableList, selectedList, false);

						//
						// Remove any that don't match
						//
						if (subName.length() != 0)
						{
							for (int n = aavailableList.size() - 1; n >= 0; --n)
							{
								final String bString = (String) aavailableList.get(n);

								if (!bString.startsWith(subName))
								{
									aavailableList.remove(n);
								}
							}

							//
							// Example: ADD:FEAT(Skill Focus(Craft (Basketweaving)))
							// If you have no ranks in Craft (Basketweaving), the available list will be empty
							//
							// Make sure that the specified feat is available, even though it does not meet the prerequisite
							//
							if ((percIdx == -1) && (aavailableList.size() == 0))
							{
								aavailableList.add(subName);
							}
						}

						//
						// Remove any already selected
						//
						if (!anAbility.isStacks())
						{
							for (Iterator e = sselectedList.iterator(); e.hasNext();)
							{
								final int idx = aavailableList.indexOf(e.next().toString());

								if (idx > -1)
								{
									aavailableList.remove(idx);
								}
							}
						}

						for (Iterator e = aavailableList.iterator(); e.hasNext();)
						{
							availableList.add(featName + "(" + (String) e.next() + ")");
						}

						//return false;
					}
					else if (!aPc.hasRealFeatNamed(featName) && !aPc.hasFeatAutomatic(featName))
					{
						availableList.add(aString);
					}
				}
			}
		}
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

			if (anAbility != null)
			{
				if (!aPC.hasRealFeatNamed(chosenItem))
				{
					aPC.adjustFeats(1);
				}

				AbilityUtilities.modFeat(aPC, null, chosenItem, true, false);
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
