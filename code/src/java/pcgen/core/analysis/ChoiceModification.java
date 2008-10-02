/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * Derived from Ability.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.analysis;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;
import pcgen.util.enumeration.Tab;

public class ChoiceModification
{
	/**
	 * Modify the Ability as per the info from this.getChoiceToModify() and the
	 * choices made by the user in the GUI.
	 *
	 * @param   aPC  The Player Character object this Ability belongs to.
	 *
	 * @return  whether we modified the Ability
	 */
	public static boolean modifyChoice(PlayerCharacter aPC, Ability a)
	{
		String abilityName = a.getChoiceToModify();

		if (abilityName.length() == 0)
		{
			return false;
		}

		final List<String> abilityList = new ArrayList<String>();
		final List<String> selectedList = new ArrayList<String>();

        if (abilityName.startsWith("TYPE=") || abilityName.startsWith("TYPE."))
		{
			final String anAbilityType = abilityName.substring(5);

			//
			// Get a list of all ability possessed by the character that
			// are the specified type
			//
			for ( final PObject ability : aPC.aggregateFeatList() )
			{
				if (ability.isType(anAbilityType))
				{
					abilityList.add(ability.getKeyName());
				}
			}

			//
			// Get the user to select one if there is more than 1.
			//
			switch (abilityList.size())
			{
				case 0:
					Logging.debugPrint("PC does not have an ability of type: "
							+ anAbilityType);
					return false; // no ability to modify

				case 1:
					abilityName = abilityList.get(0);
					break;

				default:

					final ChooserInterface chooser = ChooserFactory.getChooserInstance();
					chooser.setPoolFlag(false); // user is not required to make any

					// changes
					chooser.setTotalChoicesAvail(1);

					chooser.setTitle("Select a "
							+ SettingsHandler.getGame().getSingularTabName(Tab.ABILITIES)
							+ " to modify");

					Globals.sortChooserLists(abilityList, selectedList);
					chooser.setAvailableList(abilityList);
					chooser.setSelectedList(selectedList);
					chooser.setVisible(true);

					final int selectedSize = chooser.getSelectedList().size();

					if (selectedSize == 0)
					{
						return false; // no ability chosen, so nothing was modified
					}

					abilityName = (String) chooser.getSelectedList().get(0);

					break;
			}
		}

        final Ability anAbility = aPC.getFeatNamed(abilityName);

        if (anAbility == null)
		{
			Logging.debugPrint("PC does not have ability: " + abilityName);

			return false;
		}

		//
		// Ability doesn't allow choices, so we cannot modify
		//
		if (!anAbility.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			Logging.debugPrint("MULT:NO for: " + abilityName);

			return false;
		}

		// build a list of available choices and choices already made.
		ChooserUtilities.modChoices(
		anAbility,
		abilityList,
		selectedList,
		false,
		aPC,
		true,
		SettingsHandler.getGame().getAbilityCategory(a.getCategory()));

		final int currentSelections = selectedList.size();

		//
		// If nothing to choose, or nothing selected, then leave
		//
		if ((abilityList.size() == 0) || (currentSelections == 0))
		{
			return false;
		}

		final ChooserInterface chooser = ChooserFactory.getChooserInstance();
		chooser.setPoolFlag(true); // user is required to use all available
								   // pool points
		chooser.setTotalChoicesAvail(selectedList.size()); // need to remove 1 to add another

		chooser.setTitle("Modify selections for " + abilityName);
		Globals.sortChooserLists(abilityList, selectedList);
		chooser.setAvailableList(abilityList);
		chooser.setSelectedList(selectedList);
		chooser.setVisible(true);

		final int selectedSize = chooser.getSelectedList().size();

		if (selectedSize != currentSelections)
		{
			return false; // need to have the same number of selections when finished
		}

		// replace old selection(s) with new and update bonuses
		//
		aPC.removeAllAssociations(anAbility);

		for (int i = 0; i < selectedSize; ++i)
		{
			aPC.addAssociation(anAbility, (String) chooser.getSelectedList().get(i));
		}

		// aPC.calcActiveBonuses();
		return true;
	}


}
