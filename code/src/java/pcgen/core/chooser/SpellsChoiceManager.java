/**
 * SpellsChoiceManager.java
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
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.spell.Spell;

/**
 * This is the chooser that deals with choosing a spell.
 */
public class SpellsChoiceManager extends AbstractComplexChoiceManager<Spell> {

	/**
	 * Make a new spell chooser.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public SpellsChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		title = "Spell choice";
		chooserHandled = "SPELLS";

		if (choices != null && choices.size() > 0 &&
				choices.get(0).equals(chooserHandled)) {
			choices = choices.subList(1, choices.size());
		}
	}

	/**
	 * Parse the Choice string and build a list of available choices.
	 *
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	public void getChoices(
			final PlayerCharacter aPc,
			final List<Spell>            availableList,
			final List<Spell>            selectedList)
	{
		for ( String token : choices )
		{
			String domainName = "";
			String className = "";

			if (token.startsWith("DOMAIN=") || token.startsWith("DOMAIN."))
			{
				domainName = token.substring(7);
			}
			else if (token.startsWith("CLASS=") || token.startsWith("CLASS."))
			{
				className = token.substring(6);
			}

			// 20 level cap XXX
			for (int lvl = 0; lvl < 20; ++lvl)
			{
				final List<Spell> aList = Globals.getSpellsIn(lvl, className, domainName);
				availableList.addAll(aList);
			}
		}

		List<String> associatedChoices = new ArrayList<String>();
		pobject.addAssociatedTo( associatedChoices );
		for ( String choice : associatedChoices )
		{
			Spell spell = Globals.getSpellKeyed( choice );
			selectedList.add( spell );
		}
	}

}
