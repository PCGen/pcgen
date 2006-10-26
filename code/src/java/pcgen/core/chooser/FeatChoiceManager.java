/**
 * FeatChoiceManager.java
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pcgen.core.Ability;
import pcgen.core.AssociatedChoice;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

/**
 * This is one of the choosers that deals with choosing from among a set
 * of Ability objects of Category FEAT.
 */
public class FeatChoiceManager extends AbstractComplexChoiceManager<String>
{

	/**
	 * Make a new Feat chooser.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public FeatChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		title = "Feat Choice";
		chooserHandled = "FEAT";

		if (choices != null && choices.size() > 0) {
			Matcher mat = Pattern.compile("^FEAT[=.]").matcher(
					choices.get(0));

			if (mat.find()) {
				ArrayList<String> newChoice = new ArrayList<String>();
				newChoice.add(mat.replaceFirst(""));
				newChoice.addAll(choices.subList(1,choices.size()));
				choices = newChoice;
			}
		}
	}

	/**
	 * Parse the Choice string and build a list of available choices.
	 * @param aPc The character being processed.
	 * @param availableList The list to be populated with the possible choices.
	 * @param selectedList The list to be populated with the choices that have already been selected.
	 */
	public void getChoices(
			final PlayerCharacter aPc,
			final List<String>            availableList,
			final List<String>            selectedList)
	{
		final Ability theFeat = aPc.getFeatNamed(choices.get(0));

		if (theFeat != null)
		{
			for (AssociatedChoice<String> choice : theFeat.getAssociatedList())
			{
				availableList.add(choice.getDefaultChoice());
			}
		}

		for (AssociatedChoice<String> choice : pobject.getAssociatedList())
		{
			selectedList.add(choice.getDefaultChoice());
		}
	}

}
