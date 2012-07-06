/*
 * ChooserUtilities.java
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;

/**
 * The guts of chooser moved from PObject
 * 
 * @author Andrew Wilson
 * @version $Revision$
 */

public class ChooserUtilities
{
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
	 * @param process
	 *            if false do not process the choice, just poplate the lists
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
	public static final boolean modChoices(final CDOMObject aPObject,
		List availableList, final List selectedList, final boolean process,
		final PlayerCharacter aPC, final boolean addIt,
		final AbilityCategory category)
	{
		availableList.clear();
		selectedList.clear();
		List reservedList = new ArrayList();

		ChoiceManagerList aMan = getConfiguredController(aPObject, aPC,
				category, reservedList);
		if (aMan == null)
		{
			return false;
		}

		aMan.getChoices(aPC, availableList, selectedList);
		if (aPObject instanceof Ability)
		{
			modifyAvailChoicesForAbilityCategory(availableList, category,
				(Ability) aPObject);
		}

		if (!process)
		{
			return false;
		}

		if (availableList.size() > 0 || selectedList.size() > 0)
		{
			if (addIt)
			{
				final List newSelections =
						aMan.doChooser(aPC, availableList,
						selectedList, reservedList);
				return aMan.applyChoices(aPC, newSelections);
			}
			else
			{
				aMan.doChooserRemove(aPC, availableList, selectedList,
					reservedList);
			}
			return true;
		}
		return false;
	}

	public static <T> ChoiceManagerList<T> getConfiguredController(
			final CDOMObject aPObject, final PlayerCharacter aPC,
			final AbilityCategory category, List<String> reservedList)
	{
		ChoiceManagerList aMan = getChoiceManager(aPObject, aPC);
		if (aMan == null)
		{
			return null;
		}

		if (aPObject instanceof Ability)
		{
			Ability a = (Ability) aPObject;
			AbilityCategory cat;
			if (category == null)
			{
				cat =
						SettingsHandler.getGame().getAbilityCategory(
							a.getCategory());
			}
			else
			{
				cat = category;
			}
			aMan.setController(new AbilityChooseController(a, cat, aPC, aMan));
			for (Ability ab : aPC.getAllAbilities())
			{
				if (ab.getKeyName().equals(a.getKeyName()))
				{
					reservedList.addAll(aPC.getAssociationList(ab));
				}
			}
		}
		else if (aPObject instanceof Skill)
		{
			Skill s = (Skill) aPObject;
			aMan.setController(new SkillChooseController(s, aPC, aMan));
		}
		return aMan;
	}

	/**
	 * Restrict the available choices to what is allowed by the ability
	 * category.
	 * 
	 * @param availableList
	 *            The list of available choices, will be modified.
	 * @param category
	 *            The ability category
	 * @param ability
	 *            The ability the choices are for.
	 */
	private static void modifyAvailChoicesForAbilityCategory(
		List availableList, AbilityCategory category, Ability ability)
	{
		AbilityCategory cat;
		if (category == null)
		{
			cat =
					SettingsHandler.getGame().getAbilityCategory(
						ability.getCategory());
		}
		else
		{
			cat = category;
		}

		if (!cat.hasDirectReferences())
		{
			// Do nothing if there aren't any restrictions
			return;
		}

		Set<String> allowedSet = new HashSet<String>();
		for (CDOMSingleRef<Ability> ref : cat.getAbilityRefs())
		{
			if (ref.contains(ability))
			{
				List<String> choices = new ArrayList<String>();
				AbilityUtilities.getUndecoratedName(ref.getLSTformat(false), choices);
				allowedSet.addAll(choices);
			}
		}

		if (allowedSet.isEmpty())
		{
			// Do nothing if there aren't any restrictions
			return;
		}

		// Remove any non allowed choices from the list
		for (Iterator iterator = availableList.iterator(); iterator.hasNext();)
		{
			Object obj = iterator.next();
			String key;
			if (obj instanceof CDOMObject)
			{
				key = ((CDOMObject) obj).getKeyName();
			}
			else
			{
				key = obj.toString();
			}
			if (!allowedSet.contains(key))
			{
				iterator.remove();
			}
		}
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
	public static ChoiceManagerList getChoiceManager(CDOMObject aPObject,
		PlayerCharacter aPC)
	{
		ChooseInformation<?> chooseInfo = aPObject.get(ObjectKey.CHOOSE_INFO);
		if (chooseInfo != null)
		{
			Formula selectionsPerUnitCost = aPObject.getSafe(FormulaKey.SELECT);
			int cost = selectionsPerUnitCost.resolve(aPC, "").intValue();
			return chooseInfo.getChoiceManager(aPObject, cost);
		}
		return null;
	}
}
