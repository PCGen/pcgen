/**
 * ArmorTypeChoiceManager.java
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

import java.util.List;

/**
 * This is the chooser that deals with choosing an armor type that the PC
 * is already proficient with.
 */
public class ArmorTypeChoiceManager extends AbstractComplexChoiceManager<String>
{

	/**
	 * Make a new Armor Type chooser.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public ArmorTypeChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		title = "Armor Type Choice";
		chooserHandled = "ARMORTYPE";

		if (choices != null && choices.size() > 0 &&
				choices.get(0).equals(chooserHandled)) {
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
		List<String> armorProfs = aPc.getArmorProfList();
		for ( String profKey : armorProfs )
		{
			if (profKey.startsWith("TYPE"))
			{
				profKey = profKey.substring(5);
			}
			availableList.add(profKey);
		}

		pobject.addAssociatedTo(selectedList);
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
		/* this code was originally added to a class called Utility.java
		 * in August 2002.  As far as I can see there was no mechanism then
		 * which allowed aBonusList to be populated.  There has not been
		 * any such mechanism in the code ever.  I believe that this
		 * ChoiceManager is replacing a chooser which has never worked.
		 * It is currently unused in the data repository.
		 * Andrew Wilson 30 March 2006
		 */
		//		for (Iterator e = aBonusList.iterator(); e.hasNext();)
		//		{
		//			final String bString = (String) e.next();
		//			pobject.addAssociated(prefix + item);
		//			pobject.applyBonus("ARMORPROF=" + bString, item, aPc);
		//		}
	}

}
