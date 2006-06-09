/**
 * FeatListChoiceManager.java
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
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

import java.util.Iterator;
import java.util.List;

/**
 * This is one of the choosers that deals with choosing from among a set
 * of Ability objects of Category FEAT.
 */
public class FeatListChoiceManager extends AbstractComplexChoiceManager<Ability>
{

	/**
	 * Make a new Feat chooser.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public FeatListChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		chooserHandled = "FEATLIST";

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
			final List<Ability>            availableList,
			final List<Ability>            selectedList)
	{
		String   aString;
		Iterator<String> choiceIt = choices.iterator();

		while (choiceIt.hasNext()){

			aString = choiceIt.next();

			if (aString.startsWith("TYPE=") || aString.startsWith("TYPE."))
			{
				aString = aString.substring(5);

				for (Iterator<Ability> e1 = aPc.aggregateFeatList().iterator(); e1.hasNext();)
				{
					final Ability theFeat = e1.next();

					if (theFeat.isType(aString)
						&& (dupsAllowed || (!dupsAllowed && !availableList.contains(theFeat))))
					{
						availableList.add(theFeat);
					}
				}
			}
			else
			{
				Ability feat = aPc.getFeatKeyed(aString);
				if (feat != null)
				{
					if (dupsAllowed
						|| (!dupsAllowed && !availableList.contains(feat)))
					{
						availableList.add(feat);
					}
				}
			}
		}

		for ( Ability ability : selectedList )
		{
			pobject.addAssociated( ability.getKeyName() );
		}
	}

}
