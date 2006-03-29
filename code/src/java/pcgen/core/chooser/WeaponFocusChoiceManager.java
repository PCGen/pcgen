/**
 * WeaponFocusChoiceManager.java
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
import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.WeaponProf;

import java.util.Iterator;
import java.util.List;

/**
 * This is the chooser that deals with choosing a Weapon that currently has
 * Weapon focus applied.
 */
public class WeaponFocusChoiceManager extends AbstractComplexChoiceManager {

	/**
	 * Make a new Weapon Focus chooser.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public WeaponFocusChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		title = "Weapon Focus Choice";
		chooserHandled = "WEAPONFOCUS";
		
		if (choices != null && choices.size() > 0 &&
				((String) choices.get(0)).equals(chooserHandled)) {
			choices = choices.subList(1, choices.size());
		}
	}

	/**
	 * Parse the Choice string and build a list of available choices.
	 * 
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	public void getChoices(
			final PlayerCharacter aPc,
			final List            availableList,
			final List            selectedList)
	{
		final Ability wfFeat = aPc.getFeatNamed("Weapon Focus");

		if (choices.get(0) != null)
		{
			final String aString = (String) choices.get(0);

			if (aString.startsWith("TYPE."))
			{
				final List   aList = wfFeat.getAssociatedList();
				final String aType = aString.substring(5);

				for (Iterator e = aList.iterator(); e.hasNext();)
				{
					final Object aObj = e.next();
					final WeaponProf wp;
					wp = Globals.getWeaponProfNamed(aObj.toString());

					if (wp == null)
					{
						continue;
					}

					final Equipment eq;
					eq = EquipmentList.getEquipmentKeyed(wp.getKeyName());

					if (eq == null)
					{
						continue;
					}

					if (eq.isType(aType))
					{
						availableList.add(aObj);
					}
				}
			}
		}
		else
		{
			wfFeat.addAssociatedTo(availableList);
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
