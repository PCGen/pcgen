/*
 * SimpleArmorProfChoiceManager.java
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pcgen.core.AssociatedChoice;
import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

/**
 * Deal with choosing an armor proficiency
 */
public class SimpleArmorProfChoiceManager extends
		AbstractBasicChoiceManager<String>
{
	/**
	 * Creates a new SimpleArmorProfChoiceManager object.
	 * 
	 * @param aPObject
	 * @param theChoices
	 * @param aPC
	 */
	public SimpleArmorProfChoiceManager(PObject aPObject, String theChoices,
			PlayerCharacter aPC)
	{
		super(aPObject, theChoices, aPC);
	}

	/**
	 * Get a list of Armor proficiencies
	 * 
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	@Override
	public void getChoices(PlayerCharacter aPc, List<String> availableList,
			List<String> selectedList)
	{
		for (AssociatedChoice<String> choice : pobject.getAssociatedList())
		{
			selectedList.add(choice.getDefaultChoice());
		}
		setPreChooserChoices(selectedList.size());
		for (String tempString : getChoiceList())
		{
			if (tempString.equals("ANY") || tempString.startsWith("ALL"))
			{
				tempString = tempString.substring(5);

				for (Iterator<Map.Entry<String, Equipment>> i = EquipmentList
						.getEquipmentListIterator(); i.hasNext();)
				{
					final Equipment eq = i.next().getValue();

					String profKey = eq.profKey(aPc);
					if (eq.isArmor() && !availableList.contains(profKey))
					{
						availableList.add(profKey);
					}
				}
			}
			else if (tempString.startsWith("TYPE=")
					|| tempString.startsWith("TYPE."))
			{
				tempString = tempString.substring(5);

				for (Iterator<Map.Entry<String, Equipment>> i = EquipmentList
						.getEquipmentListIterator(); i.hasNext();)
				{
					final Equipment eq = i.next().getValue();

					if (eq.isArmor() && eq.isType(tempString)
							&& !availableList.contains(eq.profKey(aPc)))
					{
						availableList.add(eq.profKey(aPc));
					}
				}
			}
			else
			{
				final Equipment eq = EquipmentList
						.getEquipmentNamed(tempString);

				if ((eq != null) && eq.isArmor()
						&& !availableList.contains(eq.profKey(aPc)))
				{
					availableList.add(eq.profKey(aPc));
				}
			}
		}
	}

	/**
	 * Add the selected Armor proficiencies
	 * 
	 * @param aPC
	 * @param selected
	 */
	@Override
	public void applyChoices(PlayerCharacter aPC, List<String> selected)
	{
		pobject.clearAssociated();
		for (String st : selected)
		{
			if (isMultYes() && !isStackYes())
			{
				if (!pobject.containsAssociated(st))
				{
					pobject.addAssociated(st);
				}
			}
			else
			{
				pobject.addAssociated(st);
			}
		}
		adjustPool(selected);
	}
}
