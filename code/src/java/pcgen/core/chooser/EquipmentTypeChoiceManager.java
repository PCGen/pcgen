/**
 * EquipmentTypeChoiceManager.java
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

import java.util.ArrayList;
import java.util.List;

import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

/**
 * This is the chooser that deals with choosing from all equipment of a given type.
 */
public class EquipmentTypeChoiceManager extends AbstractBasicPObjectChoiceManager<Equipment> {

	/**
	 * Make a new Equipment Type chooser.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public EquipmentTypeChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		setTitle("Equipment Choice");
		List<String> list = getChoiceList();
		if (list == null || list.size() > 1)
		{
			throw new IllegalArgumentException(
					"Choice List for EquipmentTypeChoiceManager must be 1 item");
		}
	}

	/**
	 * Parse the Choice string and build a list of available choices.
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	@Override
	public void getChoices(
			final PlayerCharacter aPc,
			final List<Equipment>            availableList,
			final List<Equipment>            selectedList)
	{
		List<String> choices = getChoiceList();
		String choiceSec = choices.isEmpty() ? pobject.getKeyName() : choices
				.get(0);
		availableList.addAll(EquipmentList.getEquipmentOfType(choiceSec, ""));

		List<String> equipKeys = new ArrayList<String>();
		pobject.addAssociatedTo( equipKeys );
		for ( String key : equipKeys )
		{
			Equipment equip = Globals.getContext().ref.silentlyGetConstructedCDOMObject(
					Equipment.class,  key );
			if ( equip != null )
			{
				selectedList.add( equip );
			}
		}
		setPreChooserChoices(selectedList.size());
	}
}
