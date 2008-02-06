/**
 * MiscChoiceManager.java
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
 * Current Version: $Revision$
 * Last Editor:     $Author$
 * Last Edited:     $Date$
 *
 * Copyright 2006 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import java.util.List;

import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

/**
 * This is the chooser that deals with choosing from among a set 
 * of supplied strings.
 */
public class MiscChoiceManager extends AbstractBasicStringChoiceManager {
	
	/**
	 * Make a new Miscellaneous chooser.  This is the chooser that deals
	 * with choosing from among a set of supplied strings.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public MiscChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		/*
		 * This is a hack for 5.x syntax
		 */
		if (!super.typeHandled().equals("MISC"))
		{
			getChoiceList().add(0, super.typeHandled());
		}
	}

	/**
	 * Parse the Choice string and build a list of available choices.
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	@Override
	public void getChoices(
			PlayerCharacter aPc,
			List<String>            availableList,
			List<String>            selectedList)
	{
		for ( String aString : getChoiceList() )
		{
			if (isStackYes() || !availableList.contains(aString))
			{
				availableList.add(aString);
			}
		}
		pobject.addAssociatedTo(selectedList);
		setPreChooserChoices(selectedList.size());
	}

	@Override
	public String typeHandled()
	{
		return "MISC";
	}


}
