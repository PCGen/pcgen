/*
 * SpellLevelChoiceManager.java
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
 * Last Editor: $Author$
 * Last Edited: $Date$
 * Copyright 2005 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Deal with choosing a spelllevel
 *
 * @author   Andrew Wilson <nuance@sourceforge.net>
 * @version  $Revision$
 */
public class SpellLevelChoiceManager extends AbstractChoiceManager
{
	/**
	 * Creates a new SpellLevelChoiceManager object.
	 *
	 * @param  aPObject
	 * @param  theChoices
	 * @param  aPC
	 */
	public SpellLevelChoiceManager(
	    PObject         aPObject,
	    String          theChoices,
	    PlayerCharacter aPC)
	{
		super(aPObject, theChoices, aPC);
	}

	/**
	 * Get the list of spells
	 * @param  aPc            DOCUMENT ME!
	 * @param  availableList  DOCUMENT ME!
	 * @param  selectedList   DOCUMENT ME!
	 */
	public void getChoices(
	    PlayerCharacter aPc,
	    List            availableList,
	    List            selectedList)
	{
		uniqueList.clear();
		ChooserUtilities.buildSpellTypeChoices(
		    availableList,
		    uniqueList,
		    aPc,
		    Collections.enumeration(choices));
	}

	public void applyChoices(
			final PlayerCharacter  aPC,
			final List             selected,
			List                   selectedBonusList)
	{
		Iterator it = selected.iterator();
		while (it.hasNext())
		{
			final String chooseString = (String) it.next();

			if (selectedBonusList.isEmpty())
			{
				continue;
			}

			Iterator e = selectedBonusList.iterator();
			while (e.hasNext())
			{
				final String bonusString = (String) e.next();
				pobject.applyBonus(bonusString, chooseString, aPC);
			}
		}

	}
}
