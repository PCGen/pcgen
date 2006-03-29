/**
 * ArmorTypeChoiceManager.java
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
import pcgen.util.Logging;

import java.util.Iterator;
import java.util.List;

/**
 * This is the chooser that deals with choosing an armor type that the PC
 * is already proficient with.
 */
public class ArmorTypeChoiceManager extends AbstractComplexChoiceManager {

	/**
	 * Make a new Armor Type chooser.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public ArmorTypeChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		title = "Armor Type Choice";
		chooserHandled = "ARMORTYPE";
		
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
		String temptype;

		for (Iterator it = Globals.getAbilityKeyIterator("FEAT"); it.hasNext(); ) {
			final Ability tempAbility = (Ability) it.next();

			if (tempAbility.getName().startsWith("Armor Proficiency ("))
			{
				final int idxbegin = tempAbility.getName().indexOf("(");
				final int idxend   = tempAbility.getName().indexOf(")");
				temptype = tempAbility.getName().substring((idxbegin + 1), idxend);

				if (aPc.getFeatNamed(tempAbility.getName()) != null)
				{
					availableList.add(temptype);
				}
			}
		}

		pobject.addAssociatedTo(selectedList);
	}

	/**
	 * Apply the choices selected to the associated PObject (the one passed
	 * to the constructor).  I took out all the code that was't doing anything
	 * I really can't see what this is doing at all.  I've checked back through
	 * the old code in CVS and it wasn't my refactorings that made this do
	 * nothing.
	 * 
	 * @param aPC
	 * @param selected
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
