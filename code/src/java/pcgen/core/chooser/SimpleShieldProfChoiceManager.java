/*
 * SimpleShieldProfChoiceManager.java
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
 * Last Editor: $Author$
 * Last Edited: $Date$
 * Copyright 2005 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.utils.ListKey;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Deal with choosing a shiled proficiency
 *
 * @author   Andrew Wilson <nuance@sourceforge.net>
 * @version  $Revision$
 */
public class SimpleShieldProfChoiceManager extends AbstractSimpleChoiceManager
{
	/**
	 * Creates a new SimpleShieldProfChoiceManager object.
	 *
	 * @param  aPObject
	 * @param  theChoices
	 * @param  aPC
	 */
	public SimpleShieldProfChoiceManager(
		PObject         aPObject,
		String          theChoices,
		PlayerCharacter aPC)
	{
		super(aPObject, theChoices, aPC);
	}

	/**
	 * Get a list of shield proficiencies
	 * @param  aPc
	 * @param  availableList
	 * @param  selectedList
	 */
	public void getChoices(
		PlayerCharacter aPc,
		List            availableList,
		List            selectedList)
	{
		selectedList.addAll(pobject.getSafeListFor(ListKey.SELECTED_SHIELD_PROFS));

		Iterator it         = choices.iterator();
		String   tempString;

		while (it.hasNext())
		{
			tempString = (String) it.next();

			if (tempString.startsWith("TYPE=") || tempString.startsWith("TYPE."))
			{
				tempString = tempString.substring(5);

				for (Iterator i = EquipmentList.getEquipmentListIterator(); i.hasNext();)
				{
					final Map.Entry entry = (Map.Entry) i.next();
					final Equipment eq    = (Equipment) entry.getValue();

					if (
						eq.isShield() &&
						eq.isType(tempString) &&
						!availableList.contains(eq.profKey(aPc)))
					{
						availableList.add(eq.profKey(aPc));
					}
				}
			}
			else
			{
				final Equipment eq = EquipmentList.getEquipmentNamed(tempString);

				if (
					(eq != null) &&
					eq.isShield() &&
					!availableList.contains(eq.profKey(aPc)))
				{
					availableList.add(eq.profKey(aPc));
				}
			}
		}
	}

	/**
	 * Add the selected shield proficiencies
	 *
	 * @param  aPC
	 * @param  selected
	 */
	public void applyChoices(
		PlayerCharacter  aPC,
		List selected)
	{
		pobject.addSelectedShieldProfs(selected);
	}

	/**
	 * what type of chooser does this handle
	 *
	 * @return type of chooser
	 */
	public String typeHandled() {
		return chooserHandled;
	}
}
