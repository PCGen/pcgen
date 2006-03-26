/**
 * FeatSelectChoiceManager.java
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is one of the choosers that deals with choosing from among a set 
 * of Ability objects of Category FEAT.
 */
public class FeatSelectChoiceManager extends AbstractComplexChoiceManager {

	/**
	 * Make a new Feat chooser.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public FeatSelectChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		title = "Choose a Feat";
		chooserHandled = "FEATSELECT";
		
		if (choices != null && choices.size() > 0 &&
				((String) choices.get(0)).equals(chooserHandled)) {
			choices.remove(0);
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
		Iterator choiceIt = choices.iterator();

		while (choiceIt.hasNext())
		{
			String aString = (String) choiceIt.next();

			if (aString.startsWith("TYPE=") || aString.startsWith("TYPE."))
			{
				aString = aString.substring(5);

				if (!dupsAllowed && availableList.contains(aString))
				{
					continue;
				}

				for (Iterator it = Globals.getAbilityKeyIterator("FEAT"); it.hasNext(); ) {
					final Ability ability = (Ability) it.next();

					if (ability.isType(aString) &&
							(dupsAllowed || !availableList.contains(ability.getName()
							  )))

					{
						availableList.add(ability.getName());
					}
				}
			}
			else
			{
				Ability theAbility = Globals.getAbilityNamed("ALL", aString);

				if (theAbility != null)
				{
					String subName = "";

					if (!aString.equalsIgnoreCase(theAbility.getName()))
					{
						subName = aString.substring(theAbility.getName().length());
						aString = theAbility.getName();

						final int idx = subName.indexOf('(');

						if (idx > -1)
						{
							subName = subName.substring(idx + 1);
						}
					}

					if (theAbility.isMultiples())
					{
						//
						// If already have taken the feat, use it so we can remove
						// any choices already selected
						//
						final Ability pcFeat = aPc.getFeatNamed(aString);

						if (pcFeat != null)
						{
							theAbility = pcFeat;
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

						final List xavailableList = new ArrayList(); // available list of choices
						final List xselectedList = new ArrayList(); // selected list of choices
						theAbility.modChoices(aPc, true, xavailableList, xselectedList, false);

						//
						// Remove any that don't match
						//
						if (subName.length() != 0)
						{
							for (int n = xavailableList.size() - 1; n >= 0; --n)
							{
								final String xString = (String) xavailableList.get(n);

								if (!xString.startsWith(subName))
								{
									xavailableList.remove(n);
								}
							}

							//
							// Example: ADD:FEAT(Skill Focus(Craft (Basketweaving)))
							// If you have no ranks in Craft (Basketweaving), the available list will be empty
							//
							// Make sure that the specified feat is available, even though it does not meet the prerequisite
							//
							if ((percIdx == -1) && (xavailableList.size() == 0))
							{
								xavailableList.add(aString + "(" + subName + ")");
							}
						}

						//
						// Remove any already selected
						//
						if (!theAbility.isStacks())
						{
							for (Iterator e = xselectedList.iterator(); e.hasNext();)
							{
								final int idx = xavailableList.indexOf(e.next().toString());

								if (idx > -1)
								{
									xavailableList.remove(idx);
								}
							}
						}

						for (Iterator e = xavailableList.iterator(); e.hasNext();)
						{
							availableList.add(aString + "(" + (String) e.next() + ")");
						}
					}
					else
					{
						availableList.add(aString);
					}
				}
			}
		}

		pobject.addAssociatedTo(selectedList);
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
