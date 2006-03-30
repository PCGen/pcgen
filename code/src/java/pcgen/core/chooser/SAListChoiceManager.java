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

import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.io.PCGIOHandler;

import java.util.Iterator;
import java.util.List;

/**
 * This is the chooser that deals with choosing from a list of SAs.
 */
public class SAListChoiceManager extends AbstractComplexChoiceManager {

	              List   aBonusList = null;
	private final String stChoices;
	
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
		title = "Special Ability Choice";
		chooserHandled = "SALIST";

		/* reconstruct a suitable choiceString to pass to buildSALIST.  This is
		 * not necessarily the same as the choiceString that was passing because
		 * we may have removed some | separated elements from the front of it in
		 * the constructor of the superclass */
		
		StringBuffer newChoice = new StringBuffer(choiceString.length());
		Iterator choiceIt = choices.iterator();
		while (choiceIt.hasNext()) {
			if (newChoice.length() != 0) {
				newChoice.append('|');
			}
			newChoice.append(choiceIt.next());
		}
		
		stChoices = newChoice.toString();
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
		PCGIOHandler.buildSALIST(stChoices, availableList, aBonusList, aPc);
		pobject.addAssociatedTo(selectedList);
	}

	/**
	 * Hook so we can add behaviour to some of the sub classes but not others.
	 *
	 */
	protected void cleanUpAssociated(
			final PlayerCharacter aPc, int size)
	{
		// remove previous selections from special abilities
		// aBonusList contains all possible selections in form: <displayed info>|<special ability>
		for (int e = 0; e < pobject.getAssociatedCount(); ++e)
		{
			final String aString = pobject.getAssociated(e);
			final String prefix = aString + "|";

			for (int x = 0; x < aBonusList.size(); ++x)
			{
				final String bString = (String) aBonusList.get(x);

				if (bString.startsWith(prefix))
				{
					pobject.removeBonus(bString.substring(bString.indexOf('|') + 1), "", aPc);

					break;
				}
			}
		}

		super.cleanUpAssociated(aPc, size);
	}

	
	/**
	 * Associate a choice with the pobject.
	 * 
	 * @param aPc 
	 * @param name the choice to associate
	 */
	protected void associateChoice(
			final PlayerCharacter aPc, 
			final String          name,
			final String          objPrefix)
	{
		
		if (multiples && !dupsAllowed)
		{
			if (!pobject.containsAssociated(name))
			{
				pobject.addAssociated(name);
			}
		}
		else
		{
			final String prefix = name + "|";
			pobject.addAssociated(objPrefix + name);
			
			// SALIST: aBonusList contains all possible selections in form: <displayed info>|<special ability>
			for (int x = 0; x < aBonusList.size(); ++x)
			{
				final String bString = (String) aBonusList.get(x);
				
				if (bString.startsWith(prefix))
				{
					pobject.addBonusList(bString.substring(bString.indexOf('|') + 1));
					
					break;
				}
			}
		}
	}

}
