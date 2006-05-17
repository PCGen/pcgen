/**
 * HPChoiceManager.java
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

import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

import java.util.Iterator;
import java.util.List;

/**
 * This is the chooser that deals with choosing HP.
 */
public class HPChoiceManager extends AbstractComplexChoiceManager {

	/**
	 * Make a new HP chooser.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public HPChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		chooserHandled = "HP";

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
			final List            availableList,
			final List            selectedList)
	{
		Iterator choiceIt = choices.iterator();

		String choiceSec = (String) (choiceIt.hasNext()
				? choiceIt.next()
				: pobject.getKeyName());

		availableList.add(choiceSec);

		for (int e1 = 0; e1 < pobject.getAssociatedCount(); ++e1)
		{
			selectedList.add(choiceSec);
		}
	}


	/**
	 * Associate a choice with the pobject.
	 * @param name
	 */
	protected void associateChoice(PlayerCharacter aPc, final String name, String prefix) {
		pobject.addAssociated(name);
	}

}
