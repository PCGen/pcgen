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

import java.util.List;

import pcgen.core.Ability;
import pcgen.core.AssociatedChoice;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

/**
 * This is one of the choosers that deals with choosing from among a set
 * of Ability objects of Category FEAT.
 */
public class FeatChoiceManager extends AbstractBasicStringChoiceManager
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
		setTitle("Feat Choice");
		List<String> list = getChoiceList();
		if (list == null || list.size() > 1)
		{
			throw new IllegalArgumentException(
					"Choice List for FeatChoiceManager must be 1 item");
		}
	}

	/**
	 * Parse the Choice string and build a list of available choices.
	 * @param aPc The character being processed.
	 * @param availableList The list to be populated with the possible choices.
	 * @param selectedList The list to be populated with the choices that have already been selected.
	 */
	@Override
	public void getChoices(
			final PlayerCharacter aPc,
			final List<String>            availableList,
			final List<String>            selectedList)
	{
		// Grab a list of occurrences of the feat being chosen in any category
		List<Ability> theFeats = aPc.getFeatNamedAnyCat(getChoiceList().get(0)
				.substring(5));
		for (Ability ability : theFeats)
		{
			for (AssociatedChoice<String> choice : ability.getAssociatedList())
			{
				availableList.add(choice.getDefaultChoice());
			}
		}

		for (AssociatedChoice<String> choice : pobject.getAssociatedList())
		{
			selectedList.add(choice.getDefaultChoice());
		}
		setPreChooserChoices(selectedList.size());
	}

}
