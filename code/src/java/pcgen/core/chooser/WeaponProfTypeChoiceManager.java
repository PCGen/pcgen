/**
 * WeaponProfTypeChoiceManager.java
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

import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.WeaponProf;

import java.util.Iterator;
import java.util.List;

/**
 * This is the chooser that deals with choosing a Weapon Proficiency by type.
 */
public class WeaponProfTypeChoiceManager extends AbstractComplexChoiceManager {

	String weaponType = "";
	/**
	 * Make a new Weapon Proficiency type chooser.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public WeaponProfTypeChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		if (choices != null && choices.size() > 1 &&
				((String) choices.get(0)).equalsIgnoreCase("WEAPONPROFTYPE") &&
				Globals.weaponTypesContains((String) choices.get(1)))
		{
			chooserHandled = "WEAPONPROFTYPE";
			title = (String) choices.get(1) + " Weapon Choice";
			weaponType = (String) choices.get(1);
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

	}

	void setWeaponProfTypeSelections(
			final PlayerCharacter aPc,
			final List            availableList,
			final List            selectedList)
	{
		final List       tArrayList = Globals.getWeaponProfs(weaponType, aPc);
		      Iterator   iter;
		      WeaponProf tempProf;

		for (iter = tArrayList.iterator(); iter.hasNext();)
		{
			tempProf = (WeaponProf) iter.next();
			availableList.add(tempProf.getName());
		}

		pobject.addAssociatedTo(selectedList);
	}

	/**
	 * Associate a choice with the pobject.  Only here so we can override part
	 * of the behaviour of applyChoices
	 * 
	 * @param aPc 
	 * @param item the choice to associate
	 * @param prefix 
	 */
	protected void associateChoice(
			final PlayerCharacter aPc,
			final String          item,
			final String          prefix)
	{
		super.associateChoice(aPc, item, prefix);
		
		if (Globals.weaponTypesContains(weaponType))
		{
			aPc.addWeaponProf(prefix + item);
		}
	}

}
