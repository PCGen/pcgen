/*
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
 * Current Version: $Revision: 1.5 $
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/10/18 20:23:37 $
 * Copyright 2005 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import pcgen.core.*;
import pcgen.core.prereq.PrereqHandler;
import pcgen.util.chooser.ChooserInterface;

import java.util.Iterator;
import java.util.List;

/**
 * Handle the logic necessary to choose a Feat.
 *
 * @author   Andrew Wilson <nuance@sourceforge.net>
 * @version  $Revision: 1.5 $
 */
public class FeatChoiceManager extends AbstractChoiceManager
{
	/**
	 * Creates a new FeatChoiceManager object.
	 *
	 * @param  aPObject
	 * @param  theChoices
	 * @param  aPC
	 */
	public FeatChoiceManager(PObject aPObject, String theChoices, PlayerCharacter aPC)
	{
		super(aPObject, theChoices, aPC);
	}

	/**
	 * Construct the choices available from this ChoiceManager in availableList.
	 * Any Feats that are eligible to be added to availableList that the PC
	 * already has will also be added to selectedList.
	 *
	 * @param  availableList
	 * @param  selectedList
	 * @param  aPC
	 */
	public void getChoices(
	    final List            availableList,
	    final List            selectedList,
	    final PlayerCharacter aPC)
	{
		if (pobject.getAssociatedCount() != 0)
		{
			pobject.addAssociatedTo(selectedList);
		}

		Iterator it = choices.iterator();

		while (it.hasNext())
		{
			String featName = (String) it.next();

			final Ability anAbility = Globals.getAbilityNamed("FEAT", featName);

			if (
			    (anAbility != null) &&
			    PrereqHandler.passesAll(anAbility.getPreReqList(), aPC, anAbility))
			{
				availableList.add(featName);

				if (aPC.hasRealFeatNamed(featName) &&
				    !selectedList.contains(featName))
				{
					selectedList.add(featName);
				}
			}
		}
	}

	protected void applyChoices(
			PlayerCharacter  aPC,
			ChooserInterface chooser,
			List             selectedBonusList)
	{
		Iterator i = chooser.getSelectedList().iterator();
		while (i.hasNext())
		{
			final String tempString = (String) i.next();
			AbilityUtilities.modFeat(aPC, null, tempString, true, false);
			pobject.addAssociated(tempString);
		}
	}
}
