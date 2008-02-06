/**
 * FeatAddChoiceManager.java
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

import pcgen.core.Ability;
import pcgen.core.AbilityUtilities;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.PrereqHandler;
import pcgen.util.Logging;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.core.Categorisable;

/**
 * This is one of the choosers that deals with choosing from among a set
 * of Ability objects of Category FEAT.
 */
public class FeatAddChoiceManager extends AbstractBasicStringChoiceManager {


	/**
	 * Make a Feat chooser.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public FeatAddChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		setTitle("Add a Feat");
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
			final List<String>            availableList,
			final List<String>            selectedList)
	{
		for (String aString : getChoiceList())
		{
			if (aString.startsWith("TYPE=") || aString.startsWith("TYPE."))
			{
				final String featType = aString.substring(5);

				for (Iterator<? extends Categorisable> it = Globals.getAbilityKeyIterator("FEAT"); it.hasNext(); )
				{
					final Ability ability = (Ability)it.next();

					if (
						ability.isType(featType) &&
						aPc.canSelectAbility(ability) &&
						!availableList.contains(ability.toString())
					   )
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
					
					addChoiceToAvailable(aPc, availableList, tokString.trim());
				}
			}
		}
		pobject.addAssociatedTo(selectedList);
		setPreChooserChoices(selectedList.size());
	}

	/**
	 * Add a single feat to the list of available feat names.
	 * 
	 * @param aPc The character being processed.
	 * @param availableList The list of feats to be offered
	 * @param tokString The key of the feat to be added.
	 */
	private void addChoiceToAvailable(final PlayerCharacter aPc,
		final List<String> availableList, String featKey)
	{
		String subName = "";
		Ability anAbility = Globals.getAbilityKeyed("FEAT", featKey);

		if (anAbility == null)
		{
			Logging.errorPrint("Feat not found: " + featKey);
			return;
		}

		if (!featKey.equalsIgnoreCase(anAbility.getKeyName()))
		{
			subName = featKey.substring(anAbility.getKeyName().length());
			featKey = anAbility.getKeyName();

			final int si = subName.indexOf('(');

			if (si > -1)
			{
				subName = subName.substring(si + 1);
			}
		}

		if (PrereqHandler.passesAll(anAbility.getPreReqList(), aPc, anAbility))
		{
			if (anAbility.isMultiples())
			{
				//
				// If already have taken the feat, use it so we can remove
				// any choices already selected
				//
				final Ability pcFeat = aPc.getFeatKeyed(featKey);

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
				anAbility.modChoices(tempAvailList, tempSelList, false, aPc,
					true, null);
				final List<String> aavailableList = new ArrayList<String>(); // available list of choices
				final List<String> sselectedList = new ArrayList<String>(); // selected list of choices
				ChooserUtilities.convertChoiceListToStringList(tempAvailList, aavailableList);
				ChooserUtilities.convertChoiceListToStringList(tempSelList, sselectedList);
				
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
				if (!anAbility.isStacks())
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
					availableList.add(featKey + "(" + e.next() + ")");
				}
			}
			else if (!aPc.hasRealFeat(Globals.getAbilityKeyed("FEAT", featKey))
				&& !aPc.hasFeatAutomatic(featKey))
			{
				availableList.add(featKey);
			}
		}
	}

	/**
	 * Associate a choice with the pobject.
	 * @param aPc
	 * @param item the choice to associate
	 * @param prefix
	 */
	@Override
	protected void associateChoice(PlayerCharacter aPc, final String item)
	{
		super.associateChoice(aPc, item);

		boolean adjPool = !aPc.hasRealFeatNamed(item);
		if (adjPool)
		{
			aPc.adjustFeats(1);
		}

		if (0 == AbilityUtilities.modFeat(aPc, null, item, true, false) && adjPool)
		{
			aPc.adjustFeats(-1);
		}
	}

}
