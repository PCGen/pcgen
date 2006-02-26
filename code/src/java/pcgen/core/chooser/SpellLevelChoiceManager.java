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
 * Current Version: $Revision: 1.4 $
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/10/18 20:23:37 $
 * Copyright 2005 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.util.chooser.ChooserInterface;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Deal with choosing a spelllevel
 *
 * @author   Andrew Wilson <nuance@sourceforge.net>
 * @version  $Revision: 1.4 $
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
	 *
	 * @param  availableList  DOCUMENT ME!
	 * @param  selectedList   DOCUMENT ME!
	 * @param  aPC            DOCUMENT ME!
	 */
	public void getChoices(
	    List            availableList,
	    List            selectedList,
	    PlayerCharacter aPC)
	{
		uniqueList.clear();
		ChooserUtilities.buildSpellTypeChoices(
		    availableList,
		    uniqueList,
		    aPC,
		    Collections.enumeration(choices));
	}

	protected void applyChoices(
			final PlayerCharacter  aPC,
			final ChooserInterface chooser,
			List                   selectedBonusList)
	{
		Iterator it = chooser.getSelectedList().iterator();
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
