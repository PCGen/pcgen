/*
 * SimpleFeatChoiceManager.java
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

import pcgen.core.*;
import pcgen.core.prereq.PrereqHandler;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * Handle the logic necessary to choose a Feat.
 *
 * @author   Andrew Wilson <nuance@sourceforge.net>
 * @version  $Revision$
 */

public class SimpleFeatChoiceManager extends AbstractSimpleChoiceManager<Ability>
{
	/**
	 * Creates a new SimpleFeatChoiceManager object.
	 *
	 * @param  aPObject
	 * @param  theChoices
	 * @param  aPC
	 */
	public SimpleFeatChoiceManager(PObject aPObject, String theChoices, PlayerCharacter aPC)
	{
		super(aPObject, theChoices, aPC);
	}

	/**
	 * Construct the choices available from this ChoiceManager in availableList.
	 * Any Feats that are eligible to be added to availableList that the PC
	 * already has will also be added to selectedList.
	 * @param  aPc
	 * @param  availableList
	 * @param  selectedList
	 */
	public void getChoices(
		final PlayerCharacter aPc,
		final List<Ability>            availableList,
		final List<Ability>            selectedList)
	{
		if (pobject.getAssociatedCount() != 0)
		{
			List<String> abilityKeys = new ArrayList<String>();
			pobject.addAssociatedTo( abilityKeys );
			for ( String key : abilityKeys )
			{
				final Ability ability = Globals.getAbilityKeyed("FEAT", key);
				if ( ability != null )
				{
					selectedList.add( ability );
				}
			}
		}

		Iterator<String> it = choices.iterator();

		while (it.hasNext())
		{
			String featName = it.next();

			final Ability anAbility = Globals.getAbilityKeyed("FEAT", featName);

			if (
				(anAbility != null) &&
				PrereqHandler.passesAll(anAbility.getPreReqList(), aPc, anAbility))
			{
				availableList.add(anAbility);

				if (aPc.hasRealFeat(anAbility) &&
					!selectedList.contains(anAbility))
				{
					selectedList.add(anAbility);
				}
			}
		}
	}

	/**
	 * Apply the choices made to the PObject this choiceManager is associated with
	 *
	 * @param aPC
	 * @param selected
	 */
	public void applyChoices(
			PlayerCharacter  aPC,
			List<Ability>             selected)
	{
		Iterator<Ability> i = selected.iterator();
		while (i.hasNext())
		{
			Ability ability = i.next();
			final String tempString = ability.getKeyName();
			AbilityUtilities.modFeat(aPC, null, tempString, true, false);
			pobject.addAssociated(tempString);
		}
	}

	/**
	 * what type of chooser does this handle
	 *
	 * @return type of chooser
	 */
	public String typeHandled() {
		return chooserHandled;
	}
}
