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

import java.util.List;
import pcgen.core.Ability;
import pcgen.core.AssociatedChoice;
import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.WeaponProf;

/**
 * This is the chooser that deals with choosing a Weapon that currently has
 * Weapon focus applied.
 */
public class WeaponFocusChoiceManager extends AbstractBasicStringChoiceManager {

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
		setTitle("Weapon Focus Choice");
		List<String> list = getChoiceList();
		if (list == null || list.size() > 1)
		{
			throw new IllegalArgumentException(
					"Choice List for WeaponFocusChoiceManager must be 1 item");
		}
	}

	/**
	 * Parse the Choice string and build a list of available choices.
	 *
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	@Override
	public void getChoices(
			final PlayerCharacter aPc,
			final List<String>            availableList,
			final List<String>            selectedList)
	{
		final Ability wfFeat = aPc.getFeatNamed("Weapon Focus");

		List<String> choices = getChoiceList();
		
		if (choices != null && choices.get(0) != null)
		{
			final String aString = choices.get(0);

			if (aString.startsWith("TYPE."))
			{
				final List<AssociatedChoice<String>>   aList = wfFeat.getAssociatedList();
				final String aType = aString.substring(5);

				for ( AssociatedChoice<String> choice : aList )
				{
					final WeaponProf wp;
					final String strChoice = choice.getDefaultChoice();
					wp = Globals.getWeaponProfKeyed( strChoice );

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
						availableList.add( strChoice );
					}
				}
			}
		}
		else
		{
			wfFeat.addAssociatedTo(availableList);
		}

		pobject.addAssociatedTo(selectedList);
		setPreChooserChoices(selectedList.size());
	}

}
