/**
 * SAListChoiceManager.java
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

import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.utils.CoreUtility;
import pcgen.io.PCGIOHandler;

/**
 * This is the chooser that deals with choosing from a list of SAs.
 */
public class SAListChoiceManager extends AbstractBasicStringChoiceManager {

	List<String>   aBonusList = null;

	/**
	 * Make a new Armor Type chooser.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public SAListChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		setTitle("Special Ability Choice");
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
		PCGIOHandler.buildSALIST("SALIST:"
				+ CoreUtility.join(getChoiceList(), "|"), availableList,
				aBonusList, aPc);
		pobject.addAssociatedTo(selectedList);
		setPreChooserChoices(selectedList.size());
	}

	/**
	 * Hook so we can add behaviour to some of the sub classes but not others.
	 *
	 */
	@Override
	protected void cleanUpAssociated(PlayerCharacter aPC)
	{
		// remove previous selections from special abilities
		// aBonusList contains all possible selections in form: <displayed info>|<special ability>
		for (int e = 0; e < pobject.getAssociatedCount(); ++e)
		{
			final String aString = pobject.getAssociated(e);
			final String prefix = aString + "|";

			for ( String bString : aBonusList )
			{
				if (bString.startsWith(prefix))
				{
					pobject.removeBonus(bString.substring(bString.indexOf('|') + 1), "", aPC);

					break;
				}
			}
		}

		super.cleanUpAssociated(aPC);
	}


	/**
	 * Associate a choice with the pobject.
	 *
	 * @param aPc
	 * @param name the choice to associate
	 */
	@Override
	protected void associateChoice(
			final PlayerCharacter aPc,
			final String          name)
	{
		if (isMultYes() && !isStackYes())
		{
			pobject.addAssociated(name);
		}
		else
		{
			final String prefix = name + "|";
			pobject.addAssociated(name);

			// SALIST: aBonusList contains all possible selections in form: <displayed info>|<special ability>
			for ( String bString : aBonusList )
			{
				if (bString.startsWith(prefix))
				{
					pobject.addBonusList(bString.substring(bString.indexOf('|') + 1));

					break;
				}
			}
		}
	}

}
