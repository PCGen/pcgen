/*
 * AbilityChoiceManager.java
 * Copyright 2008 (C) James Dempsey
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
 * Created on 18/10/2008 22:22:14
 *
 * $Id: $
 */
package pcgen.core.chooser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Categorisable;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.util.Logging;

/**
 * The Class <code>AbilityChoiceManager</code> is responsible for setting up the 
 * list of available values in an ABILITY chooser. 
 * 
 * Last Editor: $Author: $
 * Last Edited: $Date:  $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision:  $
 */
public class AbilityChoiceManager extends AbstractBasicStringChoiceManager
{

	/**
	 * Make a new Ability chooser.
	 * 
	 * @param aPObject the object providing the choice
	 * @param choiceString the choice string
	 * @param aPC the character the choice is being made for
	 */
	public AbilityChoiceManager(PObject aPObject, String choiceString,
		PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		setTitle("Ability Choice");
		List<String> list = getChoiceList();
		if (list == null || list.size() < 2)
		{
			throw new IllegalArgumentException(
				"Choice List for AbilityChoiceManager must be at least 2 items");
		}
	}

	/**
	 * Parse the Choice string and build a list of available choices.
	 * 
	 * @param aPc The character being processed.
	 * @param availableList The list to be populated with the possible choices.
	 * @param selectedList The list to be populated with the choices that have already been selected.
	 */
	@Override
	public void getChoices(final PlayerCharacter aPc,
		final List<String> availableList, final List<String> selectedList)
	{
		String category = null;
		for (String aString : getChoiceList())
		{
			if (category == null)
			{
				category = aString;
				continue;
			}
			if (aString.startsWith("TYPE=") || aString.startsWith("TYPE."))
			{
				final String featType = aString.substring(5);

				for (Iterator<? extends Categorisable> it =
						Globals.getAbilityKeyIterator(category); it.hasNext();)
				{
					final Ability ability = (Ability) it.next();

					if (ability.isType(featType)
						&& aPc.canSelectAbility(ability)
						&& !availableList.contains(ability.toString()))
					{
						availableList.add(ability.toString());
					}
				}
			}
			else
			{
				final StringTokenizer bTok = new StringTokenizer(aString, ",");
				while (bTok.hasMoreElements())
				{
					String tokString = (String) bTok.nextElement();

					addChoiceToAvailable(aPc, availableList, category,
						tokString.trim());
				}
			}
		}
		selectedList.addAll(aPc.getAssociationList(pobject));
		setPreChooserChoices(selectedList.size());
	}

	/**
	 * Add a single ability to the list of available ability names.
	 * 
	 * @param aPc The character being processed.
	 * @param availableList The list of feats to be offered
	 * @param categoryKey the category key
	 * @param abilityKey the ability key
	 */
	private void addChoiceToAvailable(final PlayerCharacter aPc,
		final List<String> availableList, String categoryKey, String abilityKey)
	{
		String subName = "";
		Ability anAbility = Globals.getAbilityKeyed(categoryKey, abilityKey);
		AbilityCategory aCategory =
				SettingsHandler.getGame().getAbilityCategory(categoryKey);

		if (anAbility == null)
		{
			Logging.errorPrint("Feat not found: " + abilityKey);
			return;
		}

		if (!abilityKey.equalsIgnoreCase(anAbility.getKeyName()))
		{
			subName = abilityKey.substring(anAbility.getKeyName().length());
			abilityKey = anAbility.getKeyName();

			final int si = subName.indexOf('(');

			if (si > -1)
			{
				subName = subName.substring(si + 1);
			}
		}

		if (anAbility.qualifies(aPc, anAbility))
		{
			if (anAbility.getSafe(ObjectKey.MULTIPLE_ALLOWED))
			{
				//
				// If already have taken the feat, use it so we can remove
				// any choices already selected
				//
				final Ability pcFeat =
						aPc.getAbilityKeyed(aCategory, abilityKey);

				if (pcFeat != null)
				{
					anAbility = pcFeat;
				}

				final int percIdx = subName.indexOf('%');

				if (percIdx > -1)
				{
					subName = subName.substring(0, percIdx);
				}
				else if (subName.length() != 0)
				{
					final int idx = subName.lastIndexOf(')');

					if (idx > -1)
					{
						subName = subName.substring(0, idx);
					}
				}

				// Retrieve the choices offered by the ability
				final List tempAvailList = new ArrayList();
				final List tempSelList = new ArrayList();
				ChooserUtilities.modChoices(anAbility, tempAvailList,
					tempSelList, false, aPc, true, null);
				final List<String> aavailableList = new ArrayList<String>(); // available list of choices
				final List<String> sselectedList = new ArrayList<String>(); // selected list of choices
				ChooserUtilities.convertChoiceListToStringList(tempAvailList,
					aavailableList);
				ChooserUtilities.convertChoiceListToStringList(tempSelList,
					sselectedList);

				//
				// Remove any that don't match
				//
				if (subName.length() != 0)
				{
					for (int n = aavailableList.size() - 1; n >= 0; --n)
					{
						final String bString = aavailableList.get(n);

						if (!bString.startsWith(subName))
						{
							aavailableList.remove(n);
						}
					}

					//
					// Example: ADD:FEAT(Skill Focus(Craft (Basketweaving)))
					// If you have no ranks in Craft (Basketweaving), the available list will be empty
					//
					// Make sure that the specified feat is available, even though it does not meet the prerequisite
					//
					if ((percIdx == -1) && (aavailableList.size() == 0))
					{
						aavailableList.add(subName);
					}
				}

				//
				// Remove any already selected
				//
				if (!anAbility.getSafe(ObjectKey.STACKS))
				{
					for (Iterator<String> e = sselectedList.iterator(); e
						.hasNext();)
					{
						final int idx = aavailableList.indexOf(e.next());

						if (idx > -1)
						{
							aavailableList.remove(idx);
						}
					}
				}

				for (Iterator<String> e = aavailableList.iterator(); e
					.hasNext();)
				{
					availableList.add(abilityKey + "(" + e.next() + ")");
				}
			}
			else
			{
				Ability testAbility =
						Globals.getAbilityKeyed(categoryKey, abilityKey);
				if (!aPc.hasRealAbility(aCategory, testAbility)
					&& !aPc.hasAutomaticAbility(aCategory, testAbility))
				{
					availableList.add(abilityKey);
				}
			}
		}
	}

}
