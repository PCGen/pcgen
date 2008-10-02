/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * Derived from EquipmentModifier.java
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

import pcgen.core.Equipment;
import pcgen.core.EquipmentChoice;
import pcgen.core.EquipmentModifier;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.util.Delta;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

public class EquipmentChoiceDriver
{
	/**
	 * @param pool
	 * @param parent
	 * @param bAdd being added
	 * @return an integer where apparently (from how it's used) only 0 is significant
	 */
	public static boolean getChoice(final int pool, final Equipment parent, EquipmentModifier eqMod, final boolean bAdd, PlayerCharacter pc)
	{
		String choiceString = eqMod.getChoiceString();

		if (choiceString.length() == 0)
		{
			return true;
		}

		final boolean forEqBuilder = choiceString.startsWith("EQBUILDER.");

		if (bAdd && forEqBuilder)
		{
			return true;
		}

		final ChooserInterface chooser = ChooserFactory.getChooserInstance();
		chooser.setPoolFlag(false);
		chooser.setVisible(false);
		List<String> selectedList = parent.getAssociationList(eqMod);

		final EquipmentChoice equipChoice = buildEquipmentChoice(
				pool,
				parent,
				eqMod,
				bAdd,
				forEqBuilder,
				selectedList.size(),
				pc);

		if (equipChoice.isBAdd())
		{
			chooser.setTotalChoicesAvail(selectedList.size() + equipChoice.getMaxSelect());
		}
		else
		{
			chooser.setTotalChoicesAvail(selectedList.size());
		}

		chooser.setAllowsDups(equipChoice.isAllowDuplicates());
		chooser.setSelectedListTerminator("|");
		chooser.setTitle("Select " + equipChoice.getTitle() + " (" + eqMod.getDisplayName() + ")");
		Globals.sortChooserLists(equipChoice.getAvailableList(), selectedList);
		chooser.setAvailableList(equipChoice.getAvailableList());
		chooser.setSelectedList(selectedList);
		chooser.setVisible(true);

		selectedList = chooser.getSelectedList();
		setChoice(parent, eqMod, selectedList, equipChoice);

		return parent.hasAssociations(eqMod);
	}

	public static void setChoice(Equipment parent, EquipmentModifier eqMod, final String choice, final EquipmentChoice equipChoice)
	{
		final List<String> tempList = new ArrayList<String>();
		tempList.add(choice);
		setChoice(parent, eqMod, tempList, equipChoice);
	}

	private static void setChoice(Equipment parent, EquipmentModifier eqMod, final List<String> selectedList, final EquipmentChoice equipChoice)
	{
		parent.removeAllAssociations(eqMod);

		for (int i = 0; i < selectedList.size(); i++)
		{
			String aString = selectedList.get(i);

			if (equipChoice.getMinValue() < equipChoice.getMaxValue())
			{
				final int idx = aString.indexOf('|');

				if (idx < 0)
				{
					final List<String> secondaryChoice = new ArrayList<String>();

					for (
						int j = equipChoice.getMinValue();
						j <= equipChoice.getMaxValue();
						j += equipChoice.getIncValue())
					{
						if (j != 0)
						{
							secondaryChoice.add(Delta.toString(j));
						}
					}

					final ChooserInterface chooser = ChooserFactory.getChooserInstance();
					chooser.setPoolFlag(false);
					chooser.setVisible(false);
					chooser.setTitle("Select modifier (" + aString + ")");
					chooser.setAvailableList(secondaryChoice);
					chooser.setSelectedList(new ArrayList());
					chooser.setTotalChoicesAvail(1);
					chooser.setVisible(true);

					if (chooser.getSelectedList().size() == 0)
					{
						continue;
					}

					aString += ('|' + ((String) chooser.getSelectedList().get(0)));
				}
			}

			if (equipChoice.isAllowDuplicates() || !parent.containsAssociated(eqMod, aString))
			{
				parent.addAssociation(eqMod, aString);
			}
		}
	}

	/**
	 * Build up the details of a required choice
	 *
	 * @param   pool
	 * @param   parent the equipment this modifer will be applied to
	 * @param   bAdd is a choice being added or removed
	 * @param   forEqBuilder
	 * @param   numSelected
	 *
	 * @return  A populated EquipmentChoice object
	 */
	public static EquipmentChoice buildEquipmentChoice(
		final int       pool,
		final Equipment parent,
		EquipmentModifier eqMod,
		final boolean   bAdd,
		final boolean   forEqBuilder,
		final int       numSelected,
		PlayerCharacter pc)
	{
		final EquipmentChoice equipChoice  = new EquipmentChoice(bAdd, pool);
		String                choiceString = eqMod.getChoiceString();

		if (choiceString.length() == 0)
		{
			return equipChoice;
		}

		equipChoice.constructFromChoiceString(
			choiceString,
			parent,
			pool,
			numSelected,
			forEqBuilder,
			pc);

		return equipChoice;
	}


}
