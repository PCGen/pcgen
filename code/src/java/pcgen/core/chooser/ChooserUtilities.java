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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.util.Logging;

/**
 * The guts of chooser moved from PObject
 * 
 * @author Andrew Wilson
 * @version $Revision$
 */

public class ChooserUtilities
{
	private static Map<String, String> classLookup = null;
	private static boolean mapconstructed = false;

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
	public static final boolean modChoices(final PObject aPObject,
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
			final PObject aPObject, final PlayerCharacter aPC,
			final AbilityCategory category, List<String> reservedList)
	{
		ChoiceManagerList aMan = getChoiceManager(aPObject, "", aPC);

		if (aMan == null)
		{
			return null;
		}

		if (aMan instanceof ControllableChoiceManager
			&& aPObject instanceof Ability)
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
			ControllableChoiceManager abcm = (ControllableChoiceManager) aMan;
			abcm.setController(new AbilityChooseController(a, cat, aPC, abcm));
			for (Ability ab : aPC.getAllAbilities())
			{
				if (ab.getKeyName().equals(a.getKeyName()))
				{
					reservedList.addAll(aPC.getAssociationList(ab));
				}
			}
		}
		else if (aMan instanceof ControllableChoiceManager
				&& aPObject instanceof Skill)
		{
			Skill s = (Skill) aPObject;
			ControllableChoiceManager abcm = (ControllableChoiceManager) aMan;
			abcm.setController(new SkillChooseController(s, aPC, abcm));
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

		if (cat.getAbilityKeys().isEmpty())
		{
			// Do nothing if there aren't any restrictions
			return;
		}

		Set<String> allowedSet = new HashSet<String>();
		for (String decoratedKey : cat.getAbilityKeys())
		{
			List<String> allowedChoice = new ArrayList<String>();
			String bareKey =
					AbilityUtilities.getUndecoratedName(decoratedKey,
						allowedChoice);
			if (bareKey.equals(ability.getKeyName()))
			{
				allowedSet.addAll(allowedChoice);
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
	 * Make a mapping so that we can look up the name of the class that
	 * implements a given ChoiceManager for specific type of Chooser.
	 * 
	 */
	private static void constructMap()
	{
		classLookup = new HashMap<String, String>();
		classLookup.put("SPELLLIST", SpellListChoiceManager.class.getName());

		mapconstructed = true;
	}

	/**
	 * Make a ChoiceManager Object for the chooser appropriate for
	 * aPObject.getChoiceString();
	 * 
	 * @param aPObject
	 * @param theChoices
	 * @param aPC
	 * 
	 * @return an initialised ChoiceManager
	 */
	public static ChoiceManagerList getChoiceManager(PObject aPObject,
		String theChoices, PlayerCharacter aPC)
	{
		if (!mapconstructed)
		{
			constructMap();
		}

		String choiceString;
		if (theChoices != null && theChoices.length() > 0)
		{
			choiceString = theChoices;
		}
		else
		{
			choiceString = aPObject.getSafe(StringKey.CHOICE_STRING);
		}
		// Note: Number is special temp mod only chooser and should not be
		// actioned except by temp mods.
		if (choiceString != null && choiceString.startsWith("NUMBER"))
		{
			return null;
		}
		if (choiceString == null || choiceString.length() == 0)
		{
			ChooseInformation<?> chooseInfo =
					aPObject.get(ObjectKey.CHOOSE_INFO);
			if (chooseInfo != null)
			{
				Formula selectionsPerUnitCost =
						aPObject.getSafe(FormulaKey.SELECT);
				int cost = selectionsPerUnitCost.resolve(aPC, "").intValue();
				return chooseInfo.getChoiceManager(aPObject, cost);
			}
			return null;
		}

		List<String> mainList = Arrays.asList(choiceString.split("[|]"));

		/*
		 * Find the first element of the array that does not contain an equals
		 * sign, this is the type of chooser.
		 */
		int i = 0;
		while (i <= mainList.size() - 1
			&& mainList.get(i).indexOf("=") > 0
			&& !(mainList.get(i).startsWith("FEAT=") || mainList.get(i)
				.startsWith("FEAT.")))
		{
			i++;
		}

		/*
		 * Use the name of the chooser to look up the full canonical class name
		 * of the ChoiceManager that handles that type of chooser
		 */
		String type = (i >= mainList.size()) ? "MISC" : mainList.get(i);

		if (type.startsWith("FEAT=") || type.startsWith("FEAT."))
		{
			type = "SINGLEFEAT";
			choiceString = "SINGLEFEAT|" + choiceString.substring(5);
		}
		String className = classLookup.get(type);

		/* Construct and return the ChoiceManager */
		try
		{
			Class aClass = Class.forName(className);
			Class[] argsClass =
					{PObject.class, choiceString.getClass(),
						PlayerCharacter.class};
			Object[] argsObject = {aPObject, choiceString, aPC};

			Constructor constructor = aClass.getConstructor(argsClass);
			ChoiceManagerList cm =
					(ChoiceManagerList) constructor.newInstance(argsObject);
			return cm;
		}
		catch (ClassNotFoundException e)
		{
			Logging.errorPrint("Can't create Choice Manager: " + type
				+ " Class not found", e);
		}
		catch (InstantiationException e)
		{
			Logging.errorPrint("Can't create Choice Manager: " + type
				+ " Can't instantiate class", e);
		}
		catch (IllegalAccessException e)
		{
			Logging.errorPrint("Can't create Choice Manager: " + type
				+ " Illegal access", e);
		}
		catch (NoSuchMethodException e)
		{
			Logging.errorPrint("Can't create Choice Manager: " + type
				+ " no constructor found", e);
		}
		catch (InvocationTargetException e)
		{
			Logging.errorPrint("Can't create Choice Manager: " + type
				+ " class threw an error", e);
		}
		return null;
	}

	/**
	 * Mod choices can send back weaponprofs, abilities or strings, so we have
	 * to do a conversion here.
	 * 
	 * @param choiceList
	 *            The list of choices provided by modChoices
	 * @param stringList
	 *            The list of strings representing the choices.
	 */
	public static void convertChoiceListToStringList(final List choiceList,
		final List<String> stringList)
	{
		for (Iterator iter = choiceList.iterator(); iter.hasNext();)
		{
			stringList.add(String.valueOf(iter.next()));
		}
	}

}
