/*
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
 * Copyright 2005 Andrew Wilson <nuance@sourceforge.net>
 */

package pcgen.core.chooser;

import java.util.ArrayList;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.content.CNAbility;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;

/**
 * The guts of chooser moved from PObject
 * 
 */

public final class ChooserUtilities
{
	private ChooserUtilities()
	{
	}

	/**
	 * Deal with CHOOSE tags. The actual items the choice will be made from are
	 * based on the choiceString, as applied to current character. Choices
	 * already made (getAssociatedList) are indicated in the selectedList. This
	 * method may also be used to build a list of choices available and choices
	 * already made by passing false in the process parameter
	 * 
	 * @param availableList
	 *            the list of things not already chosen
	 * @param selectedList
	 *            the list of things already chosen
	 * @param aPC
	 *            the PC that owns the Ability
	 * @param addIt
	 *            Whether to add or remove a choice from this Ability
	 * @param category
	 *            The AbilityCategory whose pool will be charged for the ability
	 *            (if any). May be null.
	 * 
	 * @return true if we processed the list of choices, false if we used the
	 *         routine to build the list of choices without processing them.
	 */
	public static boolean modChoices(final ChooseDriver aPObject, List availableList, final List selectedList,
		final PlayerCharacter aPC, final boolean addIt, final AbilityCategory category)
	{
		availableList.clear();
		selectedList.clear();
		List reservedList = new ArrayList();

		ChoiceManagerList aMan = getConfiguredController(aPObject, aPC, category, reservedList);
		if (aMan == null)
		{
			return false;
		}

		aMan.getChoices(aPC, availableList, selectedList);

		if (!availableList.isEmpty() || !selectedList.isEmpty())
		{
			if (addIt)
			{
				final List newSelections = aMan.doChooser(aPC, availableList, selectedList, reservedList);
				return aMan.applyChoices(aPC, newSelections);
			}
			else
			{
				aMan.doChooserRemove(aPC, availableList, selectedList, reservedList);
			}
			return true;
		}
		return false;
	}

	public static <T> ChoiceManagerList<T> getConfiguredController(final ChooseDriver aPObject,
		final PlayerCharacter aPC, final AbilityCategory category, List<String> reservedList)
	{
		ChoiceManagerList aMan = getChoiceManager(aPObject, aPC);
		if (aMan == null)
		{
			return null;
		}

		if (aPObject instanceof CNAbility driver)
		{
			Ability a = driver.getAbility();
			AbilityCategory cat;
			if (category == null)
			{
				cat = SettingsHandler.getGameAsProperty().get().getAbilityCategory(a.getCategory());
			}
			else
			{
				cat = category;
			}
			aMan.setController(new AbilityChooseController(a, cat, aPC, aMan));
			List<CNAbility> abilities = aPC.getMatchingCNAbilities(a);
			for (CNAbility cna : abilities)
			{
				reservedList.addAll(aPC.getAssociationList(cna));
			}
		}
		else if (aPObject instanceof Skill s)
		{
			aMan.setController(new SkillChooseController(s, aPC));
		}
		return aMan;
	}

	/**
	 * Make a ChoiceManager Object for the chooser appropriate for
	 * aPObject.getChoiceString();
	 * 
	 * @param aPObject
	 * @param aPC
	 * 
	 * @return an initialized ChoiceManager
	 */
	public static ChoiceManagerList getChoiceManager(ChooseDriver aPObject, PlayerCharacter aPC)
	{
		ChooseInformation<?> chooseInfo = aPObject.getChooseInfo();
		if (chooseInfo != null)
		{
			Formula selectionsPerUnitCost = aPObject.getSelectFormula();
			int cost = selectionsPerUnitCost.resolve(aPC, "").intValue();
			return chooseInfo.getChoiceManager(aPObject, cost);
		}
		return null;
	}
}
