/**
 * MiscChoiceManager.java
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
 * Current Version: $Revision$
 * Last Editor:     $Author$
 * Last Edited:     $Date$
 *
 * Copyright 2006 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import pcgen.core.Ability;
import pcgen.core.Domain;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

import java.util.Iterator;
import java.util.List;

/**
 * This is the chooser that deals with choosing from among a set 
 * of supplied strings.
 */
public class MiscChoiceManager extends AbstractComplexChoiceManager {

	/**
	 * Make a new Miscellaneous chooser.  This is the chooser that deals
	 * with choosing from among a set of supplied strings.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public MiscChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		chooserHandled = "MISC";
		
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
			PlayerCharacter aPc,
			List            availableList,
			List            selectedList)
	{
		Iterator it = choices.iterator();
		while (it.hasNext())
		{
			final String aString = (String) it.next();

			if (dupsAllowed || !availableList.contains(aString))
			{
				availableList.add(aString);
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
		String objPrefix = (pobject instanceof Domain)
				? chooserHandled + '?'
				: "";

		if (pobject instanceof Ability)
		{
		    ((Ability)pobject).clearSelectedWeaponProfBonus(); //Cleans up the feat
		}

		Iterator it = selected.iterator();
		while (it.hasNext())
		{
			final String chosenItem = (String) it.next();
			final String name       = objPrefix + chosenItem;

			if (!multiples || dupsAllowed || !pobject.containsAssociated(name))
			{
				pobject.addAssociated(name);
			}
		}

		double featCount = aPC.getFeats();

		if (cost > 0)
		{
			featCount = (numberOfChoices > 0)
					? featCount - cost
					: ((maxSelections - selected.size()) * cost);
		}

		aPC.adjustFeats(featCount - aPC.getFeats());

		if (objPrefix.length() != 0)
		{
			aPC.setAutomaticFeatsStable(false);
		}
	}

}
