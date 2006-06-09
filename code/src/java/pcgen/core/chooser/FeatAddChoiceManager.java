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

/**
 * This is one of the choosers that deals with choosing from among a set
 * of Ability objects of Category FEAT.
 */
public class FeatAddChoiceManager extends AbstractComplexChoiceManager<String> {

	Ability  anAbility = null;

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
		title = "Add a Feat";
		chooserHandled = "FEATADD";

		if (choices != null && choices.size() > 0 &&
				((String) choices.get(0)).equals(chooserHandled)) {
			choices = choices.subList(1, choices.size());
		}
	}

	/**
	 * Parse the Choice string and build a list of available choices.
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	public void getChoices(
			final PlayerCharacter aPc,
			final List<String>            availableList,
			final List<String>            selectedList)
	{
		anAbility = null;
		Iterator<String> choiceIt  = choices.iterator();

		while (choiceIt.hasNext())
		{
			final String aString = choiceIt.next();

			if (aString.startsWith("TYPE=") || aString.startsWith("TYPE."))
			{
				final String featType = aString.substring(5);

				for (Iterator<Ability> it = Globals.getAbilityKeyIterator("FEAT"); it.hasNext(); )
				{
					final Ability ability = it.next();

					if (
						ability.isType(featType) &&
						aPc.canSelectAbility(ability) &&
						!availableList.contains(ability)
					   )
					{
						availableList.add(ability.toString());
					}
				}
			}
			else
			{
				final StringTokenizer bTok = new StringTokenizer(aString, ",");
				String featKey = bTok.nextToken().trim();
				String subName = "";
				anAbility = Globals.getAbilityKeyed("FEAT", featKey);

				if (anAbility == null)
				{
					Logging.errorPrint("Feat not found: " + featKey);

					//return false;
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

						final List<String> aavailableList = new ArrayList<String>(); // available list of choices
						final List<String> sselectedList = new ArrayList<String>(); // selected list of choices
						anAbility.modChoices(aavailableList, sselectedList, false, aPc, true);

						//
						// Remove any that don't match
						//
						if (subName.length() != 0)
						{
							for (int n = aavailableList.size() - 1; n >= 0; --n)
							{
								final String bString = (String) aavailableList.get(n);

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
							for (Iterator e = sselectedList.iterator(); e.hasNext();)
							{
								final int idx = aavailableList.indexOf(e.next().toString());

								if (idx > -1)
								{
									aavailableList.remove(idx);
								}
							}
						}

						for (Iterator e = aavailableList.iterator(); e.hasNext();)
						{
							availableList.add(featKey + "(" + (String) e.next() + ")");
						}

						//return false;
					}
					else if (!aPc.hasRealFeat(Globals.getAbilityKeyed("FEAT", featKey)) && !aPc.hasFeatAutomatic(featKey))
					{
						availableList.add(aString);
					}
				}
			}
		}
	}

	/**
	 * Associate a choice with the pobject.
	 *
	 * @param aPc
	 * @param item the choice to associate
	 * @param prefix
	 */
	protected void associateChoice(
			final PlayerCharacter aPc,
			final String          item,
			final String          prefix)
	{
		super.associateChoice(aPc, item, prefix);

		if (anAbility != null)
		{
			if (!aPc.hasRealFeatNamed(item))
			{
				aPc.adjustFeats(1);
			}

			AbilityUtilities.modFeat(aPc, null, item, true, false);
		}
	}

}
